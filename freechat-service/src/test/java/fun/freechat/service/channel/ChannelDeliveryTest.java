package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.*;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(15)
class ChannelDeliveryTest {
    private static final ChannelAddress ADDRESS = new ChannelAddress("example", "account", "conversation");
    private static final ChannelText TEXT = ChannelText.plain("final answer");

    @Test
    void textOnlyTransportHasNoOptionalCapabilitiesAndExplicitUnsupportedOperationsFailClearly() throws Exception {
        ChannelTransport textOnly = (address, text) -> new ChannelReceipt("text-id");
        try (Fixture fixture = new Fixture(textOnly, 3, Duration.ofSeconds(2))) {
            assertTrue(fixture.delivery.supports(ChannelTransport.class));
            assertFalse(fixture.delivery.supports(ChannelMediaTransport.class));
            assertFalse(fixture.delivery.supports(ChannelMessageEditor.class));
            assertFalse(fixture.delivery.supports(ChannelStatusTransport.class));
            assertFailure(ChannelFailure.Kind.UNSUPPORTED, fixture.delivery.editText("id", TEXT));
            assertFailure(ChannelFailure.Kind.UNSUPPORTED, fixture.delivery.sendTyping(() -> true));
            assertFailure(
                    ChannelFailure.Kind.UNSUPPORTED,
                    fixture.delivery.sendMedia(
                            ChannelMedia.reference(ChannelMedia.Kind.IMAGE, "provider-file", "caption")));
            assertEquals(0, fixture.scheduler.pendingCount());
            assertEquals("text-id", get(fixture.delivery.sendText(TEXT)).messageId());
        }
    }

    @Test
    void textEditAndMediaShareOneFifoAndRunOnlyOnSendWorkers() throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("text");
            };
            var text = fixture.delivery.sendText(TEXT);
            get(entered);
            var edit = fixture.delivery.editText("text", TEXT);
            var media =
                    fixture.delivery.sendMedia(ChannelMedia.reference(ChannelMedia.Kind.IMAGE, "file-id", "caption"));
            assertEquals(List.of("text"), transport.operations);
            assertEquals(3, fixture.scheduler.pendingCount());
            release.complete(null);
            get(text);
            get(edit);
            get(media);
            assertEquals(List.of("text", "edit:text", "media:IMAGE"), transport.operations);
            assertTrue(transport.threads.stream().allMatch(name -> name.startsWith("channel-example-send-")));
            assertEquals(List.of(ADDRESS, ADDRESS, ADDRESS), transport.addresses);
        }
    }

    @Test
    void finalRateLimitedEditRetriesWithoutAnotherTokenAndDoesNotLetNextSendOvertake() throws Exception {
        var transport = new RecordingTransport();
        AtomicInteger attempts = new AtomicInteger();
        transport.edit = (id, text) -> {
            if (attempts.incrementAndGet() == 1) {
                throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofMillis(17));
            }
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var finalEdit =
                    fixture.delivery.editText("known-id", new ChannelText("final", ChannelText.Format.MARKDOWN));
            Callback<?> retry = fixture.timer.next();
            var following = fixture.delivery.sendText(TEXT);
            assertEquals(Duration.ofMillis(17).toNanos(), retry.delayNanos);
            assertEquals(1, attempts.get());
            assertFalse(finalEdit.isDone());
            assertFalse(following.isDone());
            assertEquals(1, fixture.scheduler.activeCount());
            retry.run();
            get(finalEdit);
            get(following);
            assertEquals(List.of("edit:known-id", "edit:known-id", "text"), transport.operations);
            assertTrue(
                    transport.threads.stream().allMatch(name -> name.startsWith("channel-example-send-")),
                    "A retry timer must not call the raw transport itself");
        }
    }

    @Test
    void knownNonAcceptanceRetriesAreBoundedByAttemptsAndFollowingWorkStillRuns() throws Exception {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            if (text == TEXT) {
                attempts.incrementAndGet();
                throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofNanos(1));
            }
            return new ChannelReceipt("next");
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var send = fixture.delivery.sendText(TEXT);
            fixture.timer.next().run();
            fixture.timer.next().run();
            assertFailure(ChannelFailure.Kind.RATE_LIMITED, send);
            assertEquals(3, attempts.get());
            assertTrue(fixture.timer.callbacks.isEmpty());
            assertEquals(
                    "next",
                    get(fixture.delivery.sendText(ChannelText.plain("next"))).messageId());
        }
    }

    @Test
    void retryableKnownIdEditUsesBoundedDefaultDelay() throws Exception {
        var transport = new RecordingTransport();
        AtomicInteger attempts = new AtomicInteger();
        transport.edit = (id, text) -> {
            if (attempts.incrementAndGet() == 1) {
                throw new ChannelFailure(ChannelFailure.Kind.RETRYABLE);
            }
        };
        try (Fixture fixture = new Fixture(transport, 2, Duration.ofSeconds(2))) {
            var edit = fixture.delivery.editText("id", TEXT);
            Callback<?> retry = fixture.timer.next();
            assertEquals(Duration.ofMillis(250).toNanos(), retry.delayNanos);
            retry.run();
            get(edit);
            assertEquals(2, attempts.get());
        }
    }

    @Test
    void advertisedDelayBeyondDeadlineIsNotScheduled() {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            attempts.incrementAndGet();
            throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(5));
        };
        try (Fixture fixture = new Fixture(transport, 10, Duration.ofMillis(100))) {
            assertFailure(ChannelFailure.Kind.RATE_LIMITED, fixture.delivery.sendText(TEXT));
            assertEquals(1, attempts.get());
            assertTrue(fixture.timer.callbacks.isEmpty());
        }
    }

    @Test
    void retryWhoseTimerWakesAfterDeadlineMustNotMakeAnotherProviderCall() throws Exception {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            if (attempts.incrementAndGet() == 1) {
                throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofNanos(1));
            }
            return new ChannelReceipt("too-late");
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofMillis(300))) {
            var send = fixture.delivery.sendText(TEXT);
            Callback<?> retry = fixture.timer.next();
            // Exercise real monotonic deadline expiry, while retaining control of the retry callback.
            try (var clock = new ScheduledThreadPoolExecutor(1)) {
                get(clock.schedule(() -> {}, 400, TimeUnit.MILLISECONDS));
            }
            retry.run();
            assertThrows(
                    ExecutionException.class,
                    () -> get(send),
                    "Delivery must fail rather than invoke the provider after its overall deadline");
            assertEquals(1, attempts.get());
        }
    }

    @Test
    void queuedDeliveryDeadlineIncludesTimeWaitingForTheSendSlot() throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofMillis(300))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("accepted");
            };
            var active = fixture.delivery.sendText(TEXT);
            get(entered);
            var queued = fixture.delivery.editText("known-id", TEXT);
            try (var clock = new ScheduledThreadPoolExecutor(1)) {
                get(clock.schedule(() -> {}, 400, TimeUnit.MILLISECONDS));
            }
            release.complete(null);
            get(active);
            assertFailure(ChannelFailure.Kind.FAILED, queued);
            assertEquals(
                    List.of("text"), transport.operations, "Expired queued work must not make a new provider call");
        }
    }

    @Test
    void cancellationWakesRetryDelayAndReleasesLaneWithoutWaitingForTimer() throws Exception {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            if (text == TEXT) {
                attempts.incrementAndGet();
                throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(30));
            }
            return new ChannelReceipt("next");
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofMinutes(1))) {
            var send = fixture.delivery.sendText(TEXT);
            Callback<?> retry = fixture.timer.next();
            var next = fixture.delivery.sendText(ChannelText.plain("next"));
            assertTrue(send.cancel(false));
            assertEquals("next", get(next).messageId());
            assertTrue(retry.isCancelled());
            retry.run();
            assertEquals(1, attempts.get());
            assertEquals(0, fixture.scheduler.pendingCount());
        }
    }

    @ParameterizedTest
    @EnumSource(
            value = ChannelFailure.Kind.class,
            names = {"AMBIGUOUS", "FAILED"})
    void ambiguousAndPermanentSendsNeverRetry(ChannelFailure.Kind kind) {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            attempts.incrementAndGet();
            throw new ChannelFailure(kind);
        };
        try (Fixture fixture = new Fixture(transport, 5, Duration.ofSeconds(2))) {
            assertFailure(kind, fixture.delivery.sendText(TEXT));
            assertEquals(1, attempts.get());
            assertTrue(fixture.timer.callbacks.isEmpty());
        }
    }

    @Test
    void ambiguousMediaSendIsNotDuplicatedAndUnadvertisedKindIsUnsupported() {
        var transport = new RecordingTransport();
        AtomicInteger attempts = new AtomicInteger();
        transport.media = media -> {
            attempts.incrementAndGet();
            throw new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS);
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            assertFailure(
                    ChannelFailure.Kind.UNSUPPORTED,
                    fixture.delivery.sendMedia(ChannelMedia.reference(ChannelMedia.Kind.VOICE, "voice-file", null)));
            assertEquals(0, attempts.get());
            assertFailure(
                    ChannelFailure.Kind.AMBIGUOUS,
                    fixture.delivery.sendMedia(ChannelMedia.reference(ChannelMedia.Kind.IMAGE, "image-file", null)));
            assertEquals(1, attempts.get());
            assertTrue(fixture.timer.callbacks.isEmpty());
        }
    }

    @Test
    void expiredAndCoalescedTypingNeverReachRawTransport() throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("blocked");
            };
            var send = fixture.delivery.sendText(TEXT);
            get(entered);
            AtomicBoolean needed = new AtomicBoolean(true);
            var typing = fixture.delivery.sendTyping(needed::get);
            get(fixture.delivery.sendTyping(needed::get));
            assertEquals(2, fixture.scheduler.pendingCount(), "At most one typing operation may be queued");
            needed.set(false);
            release.complete(null);
            get(send);
            get(typing);
            assertEquals(List.of("text"), transport.operations);
            get(fixture.delivery.sendTyping(() -> true));
            assertEquals(List.of("text", "typing"), transport.operations);
        }
    }

    @Test
    void cancelledInFlightSendRetainsSlotUntilRawProviderCallReturns() throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("accepted");
            };
            var send = fixture.delivery.sendText(TEXT);
            get(entered);
            assertTrue(send.cancel(false));
            var edit = fixture.delivery.editText("known-id", TEXT);
            assertEquals(1, fixture.scheduler.activeCount());
            assertEquals(2, fixture.scheduler.pendingCount());
            assertFalse(edit.isDone());
            release.complete(null);
            get(edit);
            assertEquals(List.of("text", "edit:known-id"), transport.operations);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ownerCancellationAndShutdownPreserveActiveReceiptButCancelQueuedDelivery(boolean shutdown) throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("late-accepted");
            };
            var active = fixture.delivery.sendText(TEXT);
            get(entered);
            var queued = fixture.delivery.editText("id", TEXT);
            fixture.usable.set(false);
            if (shutdown) {
                fixture.scheduler.close(Duration.ZERO);
            } else {
                fixture.scheduler.cancelOwner(fixture);
            }
            assertFalse(active.isDone());
            assertFailure(ChannelFailure.Kind.CANCELLED, queued);
            assertEquals(1, fixture.scheduler.pendingCount());
            release.complete(null);
            assertEquals("late-accepted", get(active).messageId());
            assertEquals(List.of("text"), transport.operations);
        }
    }

    @Test
    void ownerCancellationWakesRetryWithoutCancellingTheResultObservation() throws Exception {
        AtomicInteger attempts = new AtomicInteger();
        ChannelTransport transport = (address, text) -> {
            attempts.incrementAndGet();
            throw new ChannelFailure(ChannelFailure.Kind.RATE_LIMITED, Duration.ofSeconds(30));
        };
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofMinutes(1))) {
            var send = fixture.delivery.sendText(TEXT);
            var retry = fixture.timer.next();
            fixture.scheduler.cancelOwner(fixture);
            assertFailure(ChannelFailure.Kind.CANCELLED, send);
            assertFalse(send.isCancelled());
            assertTrue(retry.isCancelled());
            retry.run();
            assertEquals(1, attempts.get());
            assertEquals(0, fixture.scheduler.pendingCount());
        }
    }

    @Test
    void revokedGenerationRejectsQueuedAndNewSends() throws Exception {
        var transport = new RecordingTransport();
        try (Fixture fixture = new Fixture(transport, 3, Duration.ofSeconds(2))) {
            var entered = new CompletableFuture<Void>();
            var release = fixture.gate();
            transport.text = text -> {
                entered.complete(null);
                awaitGate(release);
                return new ChannelReceipt("accepted");
            };
            var active = fixture.delivery.sendText(TEXT);
            get(entered);
            var stale = fixture.delivery.editText("known-id", TEXT);
            fixture.usable.set(false);
            assertFailure(ChannelFailure.Kind.CANCELLED, fixture.delivery.sendText(TEXT));
            release.complete(null);
            get(active);
            assertFailure(ChannelFailure.Kind.CANCELLED, stale);
            assertEquals(List.of("text"), transport.operations);
        }
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(3, TimeUnit.SECONDS);
    }

    private static void awaitGate(CompletableFuture<Void> gate) {
        try {
            get(gate);
        } catch (Exception error) {
            throw new AssertionError("Controlled provider gate did not open", error);
        }
    }

    private static ChannelFailure assertFailure(ChannelFailure.Kind kind, Future<?> future) {
        ExecutionException error = assertThrows(ExecutionException.class, () -> future.get(3, TimeUnit.SECONDS));
        ChannelFailure failure = assertInstanceOf(ChannelFailure.class, error.getCause());
        assertEquals(kind, failure.kind());
        return failure;
    }

    private static final class Fixture implements AutoCloseable {
        final ChannelTaskScheduler<ChannelAddress> scheduler =
                new ChannelTaskScheduler<>("channel-example-send", 1, 4, 4);
        final ManualTimer timer = new ManualTimer();
        final AtomicBoolean usable = new AtomicBoolean(true);
        final List<CompletableFuture<Void>> gates = new CopyOnWriteArrayList<>();
        final ChannelDelivery delivery;

        Fixture(ChannelTransport transport, int attempts, Duration deadline) {
            var policy = new ChannelPolicy(
                    1, 1, 4, 4, 4, 4, 2, Duration.ofSeconds(5), deadline, attempts, Duration.ofMillis(100));
            delivery = new ChannelDelivery(ADDRESS, this, transport, policy, scheduler, timer, usable::get);
        }

        CompletableFuture<Void> gate() {
            var gate = new CompletableFuture<Void>();
            gates.add(gate);
            return gate;
        }

        @Override
        public void close() {
            usable.set(false);
            gates.forEach(gate -> gate.complete(null));
            scheduler.close(Duration.ofSeconds(2));
            timer.shutdownNow();
        }
    }

    private static final class RecordingTransport
            implements ChannelTransport, ChannelMessageEditor, ChannelMediaTransport, ChannelStatusTransport {
        final List<String> operations = new CopyOnWriteArrayList<>();
        final List<String> threads = new CopyOnWriteArrayList<>();
        final List<ChannelAddress> addresses = new CopyOnWriteArrayList<>();
        Function<ChannelText, ChannelReceipt> text = ignored -> new ChannelReceipt("text");
        BiConsumer<String, ChannelText> edit = (id, value) -> {};
        Function<ChannelMedia, ChannelReceipt> media = ignored -> new ChannelReceipt("media");

        private void record(String operation, ChannelAddress address) {
            addresses.add(address);
            threads.add(Thread.currentThread().getName());
            operations.add(operation);
        }

        @Override
        public ChannelReceipt sendText(ChannelAddress address, ChannelText value) {
            record("text", address);
            return text.apply(value);
        }

        @Override
        public void editText(ChannelAddress address, String messageId, ChannelText value) {
            record("edit:" + messageId, address);
            edit.accept(messageId, value);
        }

        @Override
        public Set<ChannelMedia.Kind> mediaKinds() {
            return Set.of(ChannelMedia.Kind.IMAGE);
        }

        @Override
        public ChannelReceipt sendMedia(ChannelAddress address, ChannelMedia value) {
            record("media:" + value.kind(), address);
            return media.apply(value);
        }

        @Override
        public void sendTyping(ChannelAddress address) {
            record("typing", address);
        }
    }

    private static final class ManualTimer extends ScheduledThreadPoolExecutor {
        final BlockingQueue<Callback<?>> callbacks = new LinkedBlockingQueue<>();

        ManualTimer() {
            super(1);
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return schedule(
                    () -> {
                        command.run();
                        return null;
                    },
                    delay,
                    unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
            Callback<V> callback = new Callback<>(command, unit.toNanos(delay));
            callbacks.add(callback);
            return callback;
        }

        Callback<?> next() throws InterruptedException {
            Callback<?> callback = callbacks.poll(3, TimeUnit.SECONDS);
            assertNotNull(callback, "Runtime did not schedule the expected retry");
            return callback;
        }
    }

    private static final class Callback<V> extends FutureTask<V> implements ScheduledFuture<V> {
        final long delayNanos;

        Callback(Callable<V> command, long delayNanos) {
            super(command);
            this.delayNanos = delayNanos;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayNanos, TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            return Long.compare(delayNanos, other.getDelay(TimeUnit.NANOSECONDS));
        }
    }
}
