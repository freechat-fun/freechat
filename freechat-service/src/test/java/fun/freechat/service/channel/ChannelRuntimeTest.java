package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Timeout(15)
class ChannelRuntimeTest {
    private final List<ChannelRuntime> runtimes = new ArrayList<>();
    private final List<CompletableFuture<Void>> gates = new CopyOnWriteArrayList<>();

    @AfterEach
    void cleanup() {
        gates.forEach(gate -> gate.complete(null));
        runtimes.forEach(runtime -> {
            if (runtime.isRunning()) {
                runtime.stop();
            }
        });
    }

    @Test
    void springDiscoversPlainTextPluginAndRunsACompleteConversationWithoutProviderDependencies() throws Exception {
        FakePlugin plugin;
        ChannelInstance<String> instance;
        try (var spring = new AnnotationConfigApplicationContext(ExampleConfiguration.class)) {
            plugin = spring.getBean(FakePlugin.class);
            instance = get(plugin.opened);
            ChannelRegistry registry = spring.getBean(ChannelRegistry.class);
            ChannelRuntime runtime = spring.getBean(ChannelRuntime.class);
            assertSame(plugin, registry.find("example").orElseThrow());
            assertTrue(registry.find("missing").isEmpty());
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> registry.plugins().clear());
            assertTrue(runtime.isRunning());
            assertFalse(instance.outbound("chat").supports(ChannelMediaTransport.class));
            assertFalse(instance.outbound("chat").supports(ChannelMessageEditor.class));
            assertFalse(instance.outbound("chat").supports(ChannelStatusTransport.class));
            assertFalse(plugin instanceof ChannelInvitationProvider);
            get(instance.receive("chat", "provider-event", "hello"));
            assertEquals(List.of("hello"), plugin.messages);
            assertTrue(plugin.receiveThread.get().startsWith("channel-example-receive-"));
            assertTrue(plugin.sendThread.get().startsWith("channel-example-send-"));
            assertEquals(0, runtime.pendingReceives("example"));
            assertEquals(0, runtime.pendingSends("example"));
        }
        assertFalse(instance.isActive());
        assertTrue(plugin.stops.get() > 0, "Receiving must be stopped before disposing the plugin");
        assertEquals(1, plugin.closes.get());
        assertFailure(ChannelFailure.Kind.CANCELLED, instance.receive("chat", null, "late"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Uppercase", "1number", "has space", "has_underscore", "a/b"})
    void invalidPluginIdsFailBeforeAnyStartup(String id) {
        FakePlugin plugin = new FakePlugin(id);
        assertThrows(IllegalArgumentException.class, () -> new ChannelRegistry(List.of(plugin)));
        assertEquals(0, plugin.starts.get());
    }

    @Test
    void duplicateAndTooLongPluginIdsFailBeforeStartup() {
        FakePlugin first = new FakePlugin("duplicate");
        FakePlugin second = new FakePlugin("duplicate");
        assertThrows(IllegalArgumentException.class, () -> new ChannelRegistry(List.of(first, second)));
        assertThrows(
                IllegalArgumentException.class, () -> new ChannelRegistry(List.of(new FakePlugin("a".repeat(65)))));
        assertEquals(0, first.starts.get());
        assertEquals(0, second.starts.get());
        assertTrue(new ChannelRegistry(List.of(new FakePlugin("a".repeat(64))))
                .find("a".repeat(64))
                .isPresent());
    }

    @Test
    void receiveLaneIncludesServiceSettlementFinalOutboundAndHistoryCompletion() throws Exception {
        FakePlugin plugin = new FakePlugin("turns");
        var serviceSettled = gate();
        var providerRelease = gate();
        var historyDone = gate();
        var started = new CompletableFuture<Void>();
        var providerEntered = new CompletableFuture<Void>();
        var historyEntered = new CompletableFuture<Void>();
        List<String> events = new CopyOnWriteArrayList<>();
        plugin.transport = (address, text) -> {
            events.add("send");
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("accepted");
        };
        plugin.handler = (envelope, turn) -> {
            events.add("handle:" + envelope.payload());
            if (envelope.payload().equals("first")) {
                started.complete(null);
                return serviceSettled
                        .thenCompose(ignored -> turn.outbound().sendText(ChannelText.plain("final")))
                        .thenCompose(receipt -> {
                            events.add("history:" + receipt.messageId());
                            historyEntered.complete(null);
                            return historyDone;
                        });
            }
            return CompletableFuture.completedFuture(null);
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var first = instance.receive("same", "1", "first");
        get(started);
        var second = instance.receive("same", "2", "second");
        assertEquals(2, runtime.pendingReceives("turns"));
        assertEquals(List.of("handle:first"), events);
        serviceSettled.complete(null);
        get(providerEntered);
        assertFalse(first.isDone());
        assertFalse(second.isDone());
        providerRelease.complete(null);
        get(historyEntered);
        assertFalse(first.isDone(), "Final delivery alone is not full-turn completion");
        assertFalse(second.isDone(), "History still owns the receive position");
        historyDone.complete(null);
        get(first);
        get(second);
        assertEquals(List.of("handle:first", "send", "history:accepted", "handle:second"), events);
        assertEquals(0, runtime.pendingReceives("turns"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ownerCancellationKeepsDependentHandlerAndReceiptRecordingAheadOfNextTurn(boolean replaceAccount)
            throws Exception {
        FakePlugin plugin = new FakePlugin("receipt-cancel");
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var historyEntered = new CompletableFuture<Void>();
        var historyRelease = gate();
        var outbound = new CompletableFuture<CompletableFuture<ChannelReceipt>>();
        List<String> events = new CopyOnWriteArrayList<>();
        plugin.transport = (address, text) -> {
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("accepted-after-cancel");
        };
        plugin.handler = (envelope, turn) -> {
            events.add(envelope.payload());
            if (envelope.payload().equals("next")) {
                return CompletableFuture.completedFuture(null);
            }
            var send = turn.outbound().sendText(ChannelText.plain("reply"));
            outbound.complete(send);
            return send.thenCompose(receipt -> {
                events.add("history:" + receipt.messageId());
                historyEntered.complete(null);
                return historyRelease;
            });
        };
        ChannelRuntime runtime = launch(plugin);
        var old = get(plugin.opened);
        var first = old.receive("chat", null, "first");
        get(providerEntered);
        var send = get(outbound);
        ChannelInstance<String> current = old;
        if (replaceAccount) {
            old.close();
            current = plugin.context.openInstance("account");
        } else {
            assertTrue(first.cancel(false));
        }
        var next = current.receive("chat", null, "next");
        if (replaceAccount) {
            old.close();
        }
        assertTrue(first.isCancelled());
        assertFalse(send.isDone(), "Owner cancellation must preserve the provider receipt observation");
        assertThrows(java.util.concurrent.TimeoutException.class, () -> next.get(100, TimeUnit.MILLISECONDS));
        assertEquals(2, runtime.pendingReceives(plugin.id()));
        assertEquals(1, runtime.pendingSends(plugin.id()));
        assertEquals(List.of("first"), events);
        providerRelease.complete(null);
        get(historyEntered);
        assertEquals("accepted-after-cancel", get(send).messageId());
        assertFalse(next.isDone(), "Recording still owns the receive position after physical delivery");
        assertEquals(2, runtime.pendingReceives(plugin.id()));
        historyRelease.complete(null);
        get(next);
        assertEquals(List.of("first", "history:accepted-after-cancel", "next"), events);
        assertEquals(0, runtime.pendingReceives(plugin.id()));
    }

    @Test
    void cancellingPublicOutboundCannotReleaseTheTurnWhoseDependentHandlerHasCompleted() throws Exception {
        FakePlugin plugin = new FakePlugin("observation-cancel");
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var outbound = new CompletableFuture<CompletableFuture<ChannelReceipt>>();
        var handlerDone = new CompletableFuture<Void>();
        AtomicInteger handled = new AtomicInteger();
        plugin.transport = (address, text) -> {
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("physically-accepted");
        };
        plugin.handler = (envelope, turn) -> {
            if (handled.incrementAndGet() > 1) {
                return CompletableFuture.completedFuture(null);
            }
            var send = turn.outbound().sendText(ChannelText.plain("reply"));
            outbound.complete(send);
            return send.thenApply(receipt -> (Void) null).whenComplete((ignored, error) -> handlerDone.complete(null));
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var first = instance.receive("chat", null, "first");
        get(providerEntered);
        var send = get(outbound);
        var next = instance.receive("chat", null, "next");
        assertTrue(send.cancel(false));
        get(handlerDone);
        assertTrue(send.isCancelled(), "Direct public cancellation retains normal CompletableFuture semantics");
        assertFalse(first.isDone());
        assertThrows(java.util.concurrent.TimeoutException.class, () -> next.get(100, TimeUnit.MILLISECONDS));
        assertEquals(2, runtime.pendingReceives(plugin.id()));
        assertEquals(1, handled.get());
        providerRelease.complete(null);
        assertThrows(ExecutionException.class, () -> get(first));
        get(next);
        assertEquals(2, handled.get());
        assertEquals(0, runtime.pendingReceives(plugin.id()));
    }

    @Test
    void cancelledQueuedChildNeverInvokesProviderButActiveChildStillPinsCompletedHandler() throws Exception {
        FakePlugin plugin = new FakePlugin("queued-child");
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var sends = new CompletableFuture<List<CompletableFuture<ChannelReceipt>>>();
        plugin.transport = (address, text) -> {
            plugin.messages.add(text.text());
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("accepted");
        };
        plugin.handler = (envelope, turn) -> {
            if (envelope.payload().equals("next")) {
                return CompletableFuture.completedFuture(null);
            }
            var active = turn.outbound().sendText(ChannelText.plain("active"));
            get(providerEntered);
            var queued = turn.outbound().sendText(ChannelText.plain("queued"));
            sends.complete(List.of(active, queued));
            return queued.thenApply(receipt -> null);
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var first = instance.receive("chat", null, "first");
        var children = get(sends);
        var next = instance.receive("chat", null, "next");
        assertTrue(first.cancel(false));
        assertFailure(ChannelFailure.Kind.CANCELLED, children.get(1));
        assertFalse(children.getFirst().isDone());
        assertThrows(java.util.concurrent.TimeoutException.class, () -> next.get(100, TimeUnit.MILLISECONDS));
        assertEquals(2, runtime.pendingReceives(plugin.id()));
        assertEquals(1, runtime.pendingSends(plugin.id()));
        providerRelease.complete(null);
        assertEquals("accepted", get(children.getFirst()).messageId());
        get(next);
        assertEquals(List.of("active"), plugin.messages);
    }

    @Test
    void shutdownRetainsAcceptedReceiptUntilLatePhysicalSendAndHandlerSettle() throws Exception {
        FakePlugin plugin = new FakePlugin("late-receipt");
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var outbound = new CompletableFuture<CompletableFuture<ChannelReceipt>>();
        var receiver = new CompletableFuture<Thread>();
        plugin.transport = (address, text) -> {
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("accepted-during-shutdown");
        };
        plugin.handler = (envelope, turn) -> {
            receiver.complete(Thread.currentThread());
            var send = turn.outbound().sendText(ChannelText.plain("reply"));
            outbound.complete(send);
            return send.thenAccept(receipt -> plugin.messages.add(receipt.messageId()));
        };
        try (var timer = new ScheduledThreadPoolExecutor(1)) {
            var context = new ChannelPluginRuntime<>(plugin, timer);
            try {
                plugin.start(context);
                var first = get(plugin.opened).receive("chat", null, "first");
                get(providerEntered);
                var send = get(outbound);
                context.close(Duration.ofMillis(20));
                assertTrue(first.isCancelled());
                assertFalse(send.isDone());
                assertEquals(1, context.pendingReceives());
                assertEquals(1, context.pendingSends());
                assertTrue(plugin.messages.isEmpty());
                providerRelease.complete(null);
                assertEquals("accepted-during-shutdown", get(send).messageId());
                assertTrue(get(receiver).join(Duration.ofSeconds(3)));
                assertEquals(List.of("accepted-during-shutdown"), plugin.messages);
                assertEquals(0, context.pendingReceives());
                assertEquals(0, context.pendingSends());
            } finally {
                providerRelease.complete(null);
                context.close(Duration.ofSeconds(2));
            }
        }
    }

    @Test
    void cancellationOfDependentRetryingHandlerWakesDelayWithoutAnotherAttempt() throws Exception {
        FakePlugin plugin = new FakePlugin("retry-cancel");
        plugin.policy = new ChannelPolicy(
                1, 1, 2, 4, 2, 4, 2, Duration.ofMinutes(2), Duration.ofMinutes(1), 3, Duration.ofMillis(100));
        var retryScheduled = new CompletableFuture<ScheduledFuture<?>>();
        var outbound = new CompletableFuture<CompletableFuture<ChannelReceipt>>();
        AtomicInteger attempts = new AtomicInteger();
        plugin.transport = (address, text) -> {
            attempts.incrementAndGet();
            throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(30));
        };
        plugin.handler = (envelope, turn) -> {
            if (envelope.payload().equals("next")) {
                return CompletableFuture.completedFuture(null);
            }
            var send = turn.outbound().sendText(ChannelText.plain("reply"));
            outbound.complete(send);
            return send.thenApply(receipt -> null);
        };
        try (var timer = new ScheduledThreadPoolExecutor(1) {
            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
                var future = super.schedule(command, delay, unit);
                if (unit.toSeconds(delay) == 30) {
                    retryScheduled.complete(future);
                }
                return future;
            }
        }) {
            var context = new ChannelPluginRuntime<>(plugin, timer);
            try {
                plugin.start(context);
                var instance = get(plugin.opened);
                var first = instance.receive("chat", null, "first");
                var retry = get(retryScheduled);
                var send = get(outbound);
                var next = instance.receive("chat", null, "next");
                assertTrue(first.cancel(false));
                assertFailure(ChannelFailure.Kind.CANCELLED, send);
                get(next);
                assertTrue(retry.isCancelled());
                assertEquals(1, attempts.get());
                assertEquals(0, context.pendingReceives());
                assertEquals(0, context.pendingSends());
            } finally {
                context.close(Duration.ofSeconds(2));
            }
        }
    }

    @Test
    void handlerClosingRejectsLateAdmissionsButPreviouslyAdmittedSendCanFinishItsRetry() throws Exception {
        FakePlugin plugin = new FakePlugin("closing-handler");
        plugin.policy = new ChannelPolicy(
                1, 1, 2, 4, 2, 4, 2, Duration.ofMinutes(2), Duration.ofMinutes(1), 3, Duration.ofMillis(100));
        var handlerClosed = new CompletableFuture<Void>();
        var handler = new CompletableFuture<Void>() {
            @Override
            public CompletableFuture<Void> whenComplete(
                    java.util.function.BiConsumer<? super Void, ? super Throwable> action) {
                return super.whenComplete((value, error) -> {
                    action.accept(value, error);
                    handlerClosed.complete(null);
                });
            }
        };
        gates.add(handler);
        var turnEntered = new CompletableFuture<ChannelTurnContext>();
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var retryWakeup = new CompletableFuture<Callable<?>>();
        var outbound = new CompletableFuture<CompletableFuture<ChannelReceipt>>();
        AtomicInteger attempts = new AtomicInteger();
        plugin.transport = (address, text) -> {
            if (attempts.incrementAndGet() == 1) {
                providerEntered.complete(null);
                awaitGate(providerRelease);
                throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(30));
            }
            return new ChannelReceipt("accepted-on-retry");
        };
        plugin.handler = (envelope, turn) -> {
            turnEntered.complete(turn);
            outbound.complete(turn.outbound().sendText(ChannelText.plain("admitted")));
            return handler;
        };
        try (var timer = new ScheduledThreadPoolExecutor(1) {
            @Override
            public <V> ScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
                var future = super.schedule(command, delay, unit);
                if (unit.toSeconds(delay) == 30) {
                    retryWakeup.complete(command);
                }
                return future;
            }
        }) {
            var context = new ChannelPluginRuntime<>(plugin, timer);
            try {
                plugin.start(context);
                var first = get(plugin.opened).receive("chat", null, "first");
                get(providerEntered);
                handler.complete(null);
                get(handlerClosed);
                assertFailure(
                        ChannelFailure.Kind.CANCELLED,
                        get(turnEntered).outbound().sendText(ChannelText.plain("too-late")));
                assertFalse(first.isDone(), "An unobserved child still owns physical receive capacity");
                assertEquals(1, context.pendingReceives());
                providerRelease.complete(null);
                get(retryWakeup).call();
                assertEquals("accepted-on-retry", get(get(outbound)).messageId());
                get(first);
                assertEquals(2, attempts.get());
                assertEquals(0, context.pendingReceives());
            } finally {
                handler.complete(null);
                providerRelease.complete(null);
                context.close(Duration.ofSeconds(2));
            }
        }
    }

    @Test
    void historyFailureAfterConfirmedDeliveryDoesNotResendOrRepeatTheTurn() throws Exception {
        FakePlugin plugin = new FakePlugin("history");
        AtomicInteger handled = new AtomicInteger();
        AtomicInteger recorded = new AtomicInteger();
        plugin.handler = (envelope, turn) -> {
            handled.incrementAndGet();
            return turn.outbound()
                    .sendText(ChannelText.plain(envelope.payload()))
                    .thenAccept(receipt -> {
                        recorded.incrementAndGet();
                        if (envelope.payload().equals("first")) {
                            throw new IllegalStateException("history storage unavailable");
                        }
                    });
        };
        launch(plugin);
        var instance = get(plugin.opened);
        assertFailure(ChannelFailure.Kind.FAILED, instance.receive("chat", null, "first"));
        get(instance.receive("chat", null, "second"));
        assertEquals(List.of("first", "second"), plugin.messages);
        assertEquals(2, handled.get());
        assertEquals(2, recorded.get());
    }

    @Test
    void blockedPluginDoesNotConsumeAnotherPluginsReceiveOrSendCapacity() throws Exception {
        FakePlugin slow = new FakePlugin("slow");
        FakePlugin fast = new FakePlugin("fast");
        var providerRelease = gate();
        var providerEntered = new CompletableFuture<Void>();
        slow.transport = (address, text) -> {
            providerEntered.complete(null);
            awaitGate(providerRelease);
            return new ChannelReceipt("slow");
        };
        ChannelRuntime runtime = launch(slow, fast);
        var slowInstance = get(slow.opened);
        var fastInstance = get(fast.opened);
        var active = slowInstance.receive("chat", null, "first");
        get(providerEntered);
        var queued = slowInstance.receive("chat", null, "second");
        assertFailure(ChannelFailure.Kind.REJECTED, slowInstance.receive("chat", null, "overflow"));
        get(fastInstance.receive("chat", null, "independent"));
        assertEquals(List.of("independent"), fast.messages);
        assertFalse(active.isDone());
        assertFalse(queued.isDone());
        assertEquals(2, runtime.pendingReceives("slow"));
        assertEquals(1, runtime.pendingSends("slow"));
        providerRelease.complete(null);
        get(active);
        get(queued);
    }

    @Test
    void saturatedReceiveWorkersCanWaitForRepliesUsingIndependentSendCapacity() throws Exception {
        FakePlugin plugin = new FakePlugin("capacity");
        plugin.policy = policy(2, 2, 1, 2, Duration.ofSeconds(5));
        var release = gate();
        CountDownLatch entered = new CountDownLatch(2);
        plugin.handler = (envelope, turn) -> {
            entered.countDown();
            get(release);
            get(turn.outbound().sendText(ChannelText.plain(envelope.payload())));
            return CompletableFuture.completedFuture(null);
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var first = instance.receive("one", null, "one");
        var second = instance.receive("two", null, "two");
        await(entered);
        assertEquals(2, runtime.pendingReceives("capacity"));
        assertFailure(ChannelFailure.Kind.REJECTED, instance.receive("three", null, "overflow"));
        release.complete(null);
        get(first);
        get(second);
        assertEquals(2, plugin.messages.size());
        assertEquals(0, runtime.pendingReceives("capacity"));
        assertTrue(plugin.sendThread.get().startsWith("channel-capacity-send-"));
    }

    @Test
    void accountReplacementCancelsOldGenerationButWaitsForActualCleanup() throws Exception {
        FakePlugin plugin = new FakePlugin("generations");
        var cleanup = gate();
        var entered = new CompletableFuture<ChannelTurnContext>();
        CountDownLatch cancellation = new CountDownLatch(1);
        List<String> handled = new CopyOnWriteArrayList<>();
        plugin.handler = (envelope, turn) -> {
            handled.add(envelope.payload());
            if (envelope.payload().equals("active")) {
                turn.onCancel(cancellation::countDown);
                entered.complete(turn);
                return cleanup;
            }
            return CompletableFuture.completedFuture(null);
        };
        ChannelRuntime runtime = launch(plugin);
        var old = get(plugin.opened);
        var active = old.receive("chat", null, "active");
        var turn = get(entered);
        var staleQueued = old.receive("chat", null, "stale");
        old.close();
        await(cancellation);
        assertTrue(active.isCancelled());
        assertTrue(staleQueued.isCancelled());
        assertTrue(turn.isCancelled());
        assertFailure(ChannelFailure.Kind.CANCELLED, old.receive("chat", null, "late"));
        assertFailure(ChannelFailure.Kind.CANCELLED, old.outbound("chat").sendText(ChannelText.plain("late")));
        var replacement = plugin.context.openInstance("account");
        var next = replacement.receive("chat", null, "new");
        old.close();
        assertTrue(replacement.isActive());
        assertFalse(next.isDone());
        assertEquals(2, runtime.pendingReceives("generations"));
        cleanup.complete(null);
        get(next);
        assertEquals(List.of("active", "new"), handled);
        assertEquals(0, runtime.pendingReceives("generations"));
    }

    @Test
    void deadlineRequestsCancellationOnceAndDoesNotReleaseCapacityBeforeCleanup() throws Exception {
        FakePlugin plugin = new FakePlugin("deadline");
        plugin.policy = policy(1, 1, 1, 2, Duration.ofMillis(250));
        var cleanup = gate();
        var entered = new CompletableFuture<ChannelTurnContext>();
        CountDownLatch cancelled = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<String> cancellationThread = new AtomicReference<>();
        plugin.handler = (envelope, turn) -> {
            turn.onCancel(() -> {
                calls.incrementAndGet();
                cancellationThread.set(Thread.currentThread().getName());
                cancelled.countDown();
            });
            entered.complete(turn);
            return cleanup;
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        Instant before = Instant.now();
        var result = instance.receive("chat", null, "active");
        var turn = get(entered);
        assertFalse(turn.deadline().isBefore(before));
        assertFalse(turn.deadline().isAfter(Instant.now().plusSeconds(1)));
        await(cancelled);
        assertThrows(java.util.concurrent.CancellationException.class, () -> get(result));
        assertTrue(turn.isCancelled());
        assertEquals(1, calls.get());
        assertTrue(cancellationThread.get().startsWith("channel-deadline-cancel-"));
        assertEquals(1, runtime.pendingReceives("deadline"));
        assertFailure(ChannelFailure.Kind.REJECTED, instance.receive("other", null, "too-early"));
        CountDownLatch lateHook = new CountDownLatch(1);
        turn.onCancel(lateHook::countDown);
        await(lateHook);
        instance.close();
        assertEquals(1, calls.get());
        cleanup.complete(null);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failingHandlerReleasesLaneAndStopsTurnTimers(boolean asynchronous) throws Exception {
        FakePlugin plugin = new FakePlugin("failure");
        var stage = gate();
        var entered = new CompletableFuture<Void>();
        AtomicReference<ScheduledFuture<?>> timer = new AtomicReference<>();
        AtomicInteger handled = new AtomicInteger();
        plugin.handler = (envelope, turn) -> {
            if (handled.incrementAndGet() == 1) {
                timer.set(turn.schedule(() -> fail("Finished turn timer executed"), Duration.ofHours(1)));
                entered.complete(null);
                if (!asynchronous) {
                    throw new IllegalArgumentException("provider details");
                }
                return stage;
            }
            return CompletableFuture.completedFuture(null);
        };
        launch(plugin);
        var instance = get(plugin.opened);
        var failed = instance.receive("chat", null, "first");
        get(entered);
        var next = instance.receive("chat", null, "second");
        stage.completeExceptionally(new IllegalStateException("provider details"));
        assertFailure(ChannelFailure.Kind.FAILED, failed);
        get(next);
        assertTrue(timer.get().isCancelled());
        assertEquals(2, handled.get());
    }

    @Test
    void timerCallbacksAndBuiltInTypingOnlyEnqueueRawSdkWorkOnSendThreads() throws Exception {
        FakePlugin plugin = new FakePlugin("timers");
        var transport = new StatusTransport();
        plugin.transport = transport;
        var finish = gate();
        var scheduledSend = new CompletableFuture<Void>();
        var heartbeatSent = new CompletableFuture<Void>();
        AtomicReference<String> timerThread = new AtomicReference<>();
        var entered = new CompletableFuture<ChannelTurnContext>();
        plugin.handler = (envelope, turn) -> {
            turn.typing(Duration.ofMillis(20));
            turn.schedule(
                    () -> {
                        timerThread.set(Thread.currentThread().getName());
                        turn.outbound()
                                .sendText(ChannelText.plain("timer reply"))
                                .whenComplete((receipt, error) -> {
                                    if (error == null) {
                                        scheduledSend.complete(null);
                                    } else {
                                        scheduledSend.completeExceptionally(error);
                                    }
                                });
                    },
                    Duration.ZERO);
            entered.complete(turn);
            return finish;
        };
        transport.afterTyping = () -> {
            if (transport.typingCalls.get() >= 2) {
                heartbeatSent.complete(null);
            }
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var result = instance.receive("chat", null, "hello");
        get(entered);
        get(scheduledSend);
        get(heartbeatSent);
        finish.complete(null);
        get(result);
        get(instance.outbound("chat").sendText(ChannelText.plain("send-lane barrier")));
        assertTrue(timerThread.get().startsWith("channel-timer-"));
        assertTrue(transport.threads.stream().allMatch(name -> name.startsWith("channel-timers-send-")));
        assertEquals(0, runtime.pendingSends("timers"));
    }

    @Test
    void queuedBuiltInTypingExpiresWhenTurnCompletesBeforeSendSlotIsAvailable() throws Exception {
        FakePlugin plugin = new FakePlugin("expired");
        var transport = new StatusTransport();
        plugin.transport = transport;
        var providerEntered = new CompletableFuture<Void>();
        var providerRelease = gate();
        var turnEntered = new CompletableFuture<Void>();
        var finish = gate();
        transport.beforeText = () -> {
            providerEntered.complete(null);
            awaitGate(providerRelease);
        };
        plugin.handler = (envelope, turn) -> {
            turn.typing(Duration.ofHours(1));
            turnEntered.complete(null);
            return finish;
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var blocker = instance.outbound("chat").sendText(ChannelText.plain("blocking"));
        get(providerEntered);
        var result = instance.receive("chat", null, "hello");
        get(turnEntered);
        assertEquals(2, runtime.pendingSends("expired"));
        finish.complete(null);
        get(result);
        providerRelease.complete(null);
        get(blocker);
        get(instance.outbound("chat").sendText(ChannelText.plain("barrier")));
        assertEquals(0, transport.typingCalls.get());
        assertEquals(0, runtime.pendingSends("expired"));
    }

    @Test
    void instanceLimitAndDuplicateInstanceCheckRecoverAfterClosingAnAccount() throws Exception {
        FakePlugin plugin = new FakePlugin("instances");
        launch(plugin);
        var first = get(plugin.opened);
        assertThrows(IllegalStateException.class, () -> plugin.context.openInstance("account"));
        var second = plugin.context.openInstance("second");
        ChannelFailure failure = assertThrows(ChannelFailure.class, () -> plugin.context.openInstance("third"));
        assertEquals(ChannelFailure.Kind.REJECTED, failure.kind());
        first.close();
        var replacement = plugin.context.openInstance("account");
        first.close();
        assertTrue(replacement.isActive());
        assertTrue(second.isActive());
        get(replacement.receive("chat", null, "replacement"));
        assertEquals(List.of("replacement"), plugin.messages);
    }

    @Test
    void startupIsAsynchronousAndBlockedActivationDoesNotHoldUpAnotherPlugin() throws Exception {
        var startEntered = new CompletableFuture<Void>();
        var startRelease = gate();
        FakePlugin slow = new FakePlugin("slow-start") {
            @Override
            public void start(ChannelRuntimeContext<String> context) {
                startEntered.complete(null);
                awaitGate(startRelease);
                super.start(context);
            }
        };
        FakePlugin healthy = new FakePlugin("healthy-start");
        ChannelRuntime runtime = new ChannelRuntime(new ChannelRegistry(List.of(slow, healthy)));
        runtimes.add(runtime);
        var starting = CompletableFuture.runAsync(runtime::start);
        get(startEntered);
        starting.get(1, TimeUnit.SECONDS);
        get(get(healthy.opened).receive("chat", null, "healthy"));
        assertFalse(slow.opened.isDone());
        assertEquals(List.of("healthy"), healthy.messages);
        startRelease.complete(null);
        get(slow.opened);
    }

    @Test
    void asynchronousStartupFailureAndReconcileFailureAreRetriedWithoutBlockingHealthyPlugin() throws Exception {
        var recovering = new RecoveringPlugin();
        var healthy = new FakePlugin("healthy");
        ChannelRuntime runtime = launch(recovering, healthy);
        // Joining the short-lived lifecycle worker also observes refreshPending cleanup;
        // a callback inside plugin.start alone would race the host's completion callback.
        assertTrue(get(recovering.startWorkers).join(Duration.ofSeconds(3)));
        get(get(healthy.opened).receive("chat", null, "healthy"));
        runtime.reconcile();
        get(recovering.opened);
        assertTrue(get(recovering.startWorkers).join(Duration.ofSeconds(3)));
        runtime.backendChanged("started");
        assertEquals("started", get(recovering.backendEvents));
        runtime.reconcile();
        assertTrue(get(recovering.reconcileWorkers).join(Duration.ofSeconds(3)));
        runtime.backendChanged("failed-reconcile-barrier");
        assertEquals("failed-reconcile-barrier", get(recovering.backendEvents));
        runtime.reconcile();
        get(recovering.recovered);
        assertEquals(2, recovering.starts.get());
        assertEquals(2, recovering.reconciles.get());
    }

    @Test
    void shutdownCancelsTurnsAndTimersRejectsLateWorkAndClosesPlugin() throws Exception {
        FakePlugin plugin = new FakePlugin("shutdown");
        var cleanup = gate();
        var entered = new CompletableFuture<Void>();
        CountDownLatch cancelled = new CountDownLatch(1);
        AtomicReference<ScheduledFuture<?>> timer = new AtomicReference<>();
        plugin.handler = (envelope, turn) -> {
            turn.onCancel(cancelled::countDown);
            timer.set(turn.schedule(() -> fail("Shutdown timer ran"), Duration.ofHours(1)));
            entered.complete(null);
            return cleanup;
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        var active = instance.receive("chat", null, "active");
        get(entered);
        var queued = instance.receive("chat", null, "queued");
        var stopped = CompletableFuture.runAsync(runtime::stop);
        await(cancelled);
        assertThrows(java.util.concurrent.CancellationException.class, () -> get(queued));
        cleanup.complete(null);
        get(stopped);
        assertTrue(active.isCancelled());
        assertTrue(queued.isCancelled());
        assertTrue(timer.get().isCancelled());
        assertFalse(runtime.isRunning());
        assertFalse(instance.isActive());
        assertEquals(0, runtime.pendingReceives("shutdown"));
        assertEquals(0, runtime.pendingSends("shutdown"));
        assertTrue(plugin.stops.get() > 0, "Receiving must be stopped before disposing the plugin");
        assertEquals(1, plugin.closes.get());
        assertFailure(ChannelFailure.Kind.CANCELLED, instance.receive("chat", null, "late"));
        assertEquals(
                ChannelFailure.Kind.REJECTED,
                assertThrows(ChannelFailure.class, () -> plugin.context.openInstance("late"))
                        .kind());
    }

    @Test
    void lateHandlerSettlementAfterShutdownStillTerminatesCancellationWorkers() throws Exception {
        FakePlugin plugin = new FakePlugin("late-cleanup");
        var cleanup = gate();
        var receiveWorker = new CompletableFuture<Thread>();
        var cancellationWorker = new CompletableFuture<Thread>();
        plugin.handler = (envelope, turn) -> {
            turn.onCancel(() -> cancellationWorker.complete(Thread.currentThread()));
            receiveWorker.complete(Thread.currentThread());
            return cleanup;
        };
        try (var timer = new ScheduledThreadPoolExecutor(1)) {
            var context = new ChannelPluginRuntime<>(plugin, timer);
            try {
                plugin.start(context);
                var active = get(plugin.opened).receive("chat", null, "active");
                Thread receiver = get(receiveWorker);
                var stopped = CompletableFuture.runAsync(() -> context.close(Duration.ofMillis(50)));
                Thread canceller = get(cancellationWorker);
                get(stopped);
                assertTrue(active.isCancelled());
                assertEquals(1, context.pendingReceives());
                cleanup.complete(null);
                assertTrue(receiver.join(Duration.ofSeconds(3)));
                assertEquals(0, context.pendingReceives());
                assertTrue(
                        canceller.join(Duration.ofSeconds(1)),
                        "Cancellation executor must terminate when late physical cleanup finally settles");
            } finally {
                cleanup.complete(null);
                // Re-close only for test hygiene after the assertion: this is not a second
                // application shutdown, and must not be necessary to release owned workers.
                context.close(Duration.ofSeconds(2));
            }
        }
    }

    @Test
    void failureToStopReceivingMustNotSkipPluginResourceCleanup() throws Exception {
        FakePlugin plugin = new FakePlugin("failed-stop") {
            @Override
            public void stopReceiving() {
                super.stopReceiving();
                throw new IllegalStateException("polling shutdown failure");
            }
        };
        ChannelRuntime runtime = launch(plugin);
        var instance = get(plugin.opened);
        get(CompletableFuture.runAsync(runtime::stop));
        assertFalse(instance.isActive());
        assertTrue(plugin.stops.get() > 0);
        assertEquals(1, plugin.closes.get(), "A failed polling stop must not leak plugin-owned clients");
    }

    @Test
    void blockingPluginResourceCloseCannotMakeShutdownUnbounded() throws Exception {
        var closeEntered = new CompletableFuture<Void>();
        var closeFinished = new CompletableFuture<Void>();
        var closeRelease = gate();
        FakePlugin plugin = new FakePlugin("slow-close") {
            @Override
            public void close() {
                closeEntered.complete(null);
                awaitGate(closeRelease);
                super.close();
                closeFinished.complete(null);
            }
        };
        ChannelRuntime runtime = launch(plugin);
        get(plugin.opened);
        var stopped = CompletableFuture.runAsync(runtime::stop);
        get(closeEntered);
        try {
            stopped.get(1, TimeUnit.SECONDS);
        } finally {
            closeRelease.complete(null);
            get(stopped);
            get(closeFinished);
        }
    }

    private ChannelRuntime launch(FakePlugin... plugins) {
        ChannelRuntime runtime = new ChannelRuntime(new ChannelRegistry(List.of(plugins)));
        runtimes.add(runtime);
        runtime.start();
        return runtime;
    }

    private CompletableFuture<Void> gate() {
        var gate = new CompletableFuture<Void>();
        gates.add(gate);
        return gate;
    }

    private static ChannelPolicy policy(
            int concurrency, int capacity, int laneCapacity, int maxInstances, Duration timeout) {
        return new ChannelPolicy(
                concurrency,
                1,
                capacity,
                4,
                laneCapacity,
                4,
                maxInstances,
                timeout,
                Duration.ofSeconds(2),
                3,
                Duration.ofMillis(100));
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timed out waiting for controlled runtime operation");
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(3, TimeUnit.SECONDS);
    }

    private static <T> T get(BlockingQueue<T> queue) throws InterruptedException {
        T value = queue.poll(3, TimeUnit.SECONDS);
        assertNotNull(value, "Timed out waiting for lifecycle callback");
        return value;
    }

    private static void awaitGate(CompletableFuture<Void> gate) {
        try {
            get(gate);
        } catch (Exception error) {
            throw new AssertionError("Controlled plugin gate did not open", error);
        }
    }

    private static ChannelFailure assertFailure(ChannelFailure.Kind kind, Future<?> future) {
        ExecutionException error = assertThrows(ExecutionException.class, () -> future.get(3, TimeUnit.SECONDS));
        ChannelFailure failure = assertInstanceOf(ChannelFailure.class, error.getCause());
        assertEquals(kind, failure.kind());
        return failure;
    }

    @Configuration(proxyBeanMethods = false)
    static class ExampleConfiguration {
        @Bean(destroyMethod = "")
        FakePlugin examplePlugin() {
            return new FakePlugin("example");
        }

        @Bean
        ChannelRegistry channelRegistry(List<ChannelPlugin<?>> plugins) {
            return new ChannelRegistry(plugins);
        }

        @Bean
        ChannelRuntime channelRuntime(ChannelRegistry registry) {
            return new ChannelRuntime(registry);
        }
    }

    static class FakePlugin implements ChannelPlugin<String> {
        final String id;
        final CompletableFuture<ChannelInstance<String>> opened = new CompletableFuture<>();
        final AtomicInteger starts = new AtomicInteger();
        final AtomicInteger stops = new AtomicInteger();
        final AtomicInteger closes = new AtomicInteger();
        final List<String> messages = new CopyOnWriteArrayList<>();
        final AtomicReference<String> receiveThread = new AtomicReference<>();
        final AtomicReference<String> sendThread = new AtomicReference<>();
        volatile ChannelRuntimeContext<String> context;
        ChannelPolicy policy = ChannelRuntimeTest.policy(1, 2, 2, 2, Duration.ofSeconds(5));
        ChannelTransport transport;
        ChannelInboundHandler<String> handler;

        FakePlugin(String id) {
            this.id = id;
            transport = (address, text) -> {
                sendThread.set(Thread.currentThread().getName());
                messages.add(text.text());
                return new ChannelReceipt("receipt");
            };
            handler = (envelope, turn) -> {
                receiveThread.set(Thread.currentThread().getName());
                try (var ignored = turn.typing(Duration.ofSeconds(1))) {
                    return turn.outbound()
                            .sendText(ChannelText.plain(envelope.payload()))
                            .thenApply(receipt -> null);
                }
            };
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public ChannelTransport transport() {
            return transport;
        }

        @Override
        public ChannelInboundHandler<String> inboundHandler() {
            return handler;
        }

        @Override
        public ChannelPolicy policy() {
            return policy;
        }

        @Override
        public void start(ChannelRuntimeContext<String> runtime) {
            starts.incrementAndGet();
            context = runtime;
            opened.complete(runtime.openInstance("account"));
        }

        @Override
        public void stopReceiving() {
            stops.incrementAndGet();
        }

        @Override
        public void close() {
            closes.incrementAndGet();
        }
    }

    private static final class RecoveringPlugin extends FakePlugin implements ChannelBackendListener {
        final BlockingQueue<Thread> startWorkers = new LinkedBlockingQueue<>();
        final BlockingQueue<Thread> reconcileWorkers = new LinkedBlockingQueue<>();
        final CompletableFuture<Void> recovered = new CompletableFuture<>();
        final BlockingQueue<String> backendEvents = new LinkedBlockingQueue<>();
        final AtomicInteger reconciles = new AtomicInteger();

        RecoveringPlugin() {
            super("recovering");
        }

        @Override
        public void start(ChannelRuntimeContext<String> runtime) {
            startWorkers.add(Thread.currentThread());
            if (starts.get() == 0) {
                starts.incrementAndGet();
                throw new IllegalStateException("transient startup failure");
            }
            super.start(runtime);
        }

        @Override
        public void backendChanged(String backendId) {
            backendEvents.add(backendId);
        }

        @Override
        public void reconcile() {
            reconcileWorkers.add(Thread.currentThread());
            if (reconciles.incrementAndGet() == 1) {
                throw new IllegalStateException("transient reconcile failure");
            }
            recovered.complete(null);
        }
    }

    private static final class StatusTransport implements ChannelTransport, ChannelStatusTransport {
        final AtomicInteger typingCalls = new AtomicInteger();
        final List<String> threads = new CopyOnWriteArrayList<>();
        Runnable beforeText = () -> {};
        Runnable afterTyping = () -> {};

        @Override
        public ChannelReceipt sendText(ChannelAddress address, ChannelText text) {
            threads.add(Thread.currentThread().getName());
            beforeText.run();
            return new ChannelReceipt("text");
        }

        @Override
        public void sendTyping(ChannelAddress address) {
            threads.add(Thread.currentThread().getName());
            typingCalls.incrementAndGet();
            afterTyping.run();
        }
    }
}
