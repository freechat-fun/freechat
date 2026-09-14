package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServiceContext;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.AiServiceTokenStreamParameters;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolServiceContext;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** No providers, containers, files, credentials or substituted tool loop. Uses the pinned AI-service loop. */
class MemoryStreamingTest {
    private static final String PRIVATE = "private-provider-or-user-payload";
    private static final String FINGERPRINT = "a".repeat(64);
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);
    private static final ToolSpecification TOOL =
            ToolSpecification.builder().name("lookup").build();
    private static final ToolExecutionRequest TOOL_CALL = ToolExecutionRequest.builder()
            .id("tool-call")
            .name("lookup")
            .arguments("{}")
            .build();
    private static final UserMessage USER = UserMessage.from("hello");

    @Test
    void pinnedLoopAccountsEachCallBeforeMemoryAndAcceptsFinalSqlBeforeNotification() {
        Fixture f = new Fixture();
        f.properties.setMaxToolRounds(2);
        MemoryTokenStream stream = f.stream();
        List<ChatResponse> finals = new ArrayList<>();
        stream.onCompleteResponse(response -> {
            assertTrue(f.invocation.completed());
            assertEquals("complete", f.events.getLast());
            finals.add(response);
        });
        stream.onError(error -> fail("Unexpected sanitized error: " + error));
        stream.start();
        ChatResponse first = response(AiMessage.from(TOOL_CALL), new TokenUsage(10, 2));
        f.provider.handlers.getFirst().onCompleteResponse(first);
        assertEquals(2, f.provider.requests.size());
        f.provider.handlers.getFirst().onCompleteResponse(first);
        assertEquals(2, f.provider.requests.size());
        f.provider.handlers.get(1).onCompleteResponse(response(AiMessage.from(TOOL_CALL), new TokenUsage(20, 3)));
        assertEquals(3, f.provider.requests.size());
        ChatResponse last = response(AiMessage.from("answer"), new TokenUsage(30, 4));
        f.provider.handlers.get(2).onCompleteResponse(last);
        f.provider.handlers.get(2).onCompleteResponse(last);
        f.provider.handlers.getFirst().onError(new IllegalArgumentException(PRIVATE));
        stream.close();
        f.timers.renew.run(); // A timer already dequeued before finish is harmless.
        assertEquals(1, finals.size());
        assertEquals(new TokenUsage(60, 9), finals.getFirst().tokenUsage());
        assertEquals(3, f.accounting.size());
        assertEquals(
                java.util.Arrays.asList(
                        new TokenUsage(10, 2), null, new TokenUsage(20, 3), null, new TokenUsage(30, 4)),
                f.writtenUsage);
        assertEquals(
                List.of(
                        "usage",
                        "tool-message",
                        "tool-execute",
                        "tool-result",
                        "usage",
                        "tool-message",
                        "tool-execute",
                        "tool-result",
                        "usage",
                        "complete"),
                f.events);
        assertEquals(0, f.aborts.get());
        assertTrue(f.accounting.keySet().stream()
                .allMatch(id -> UUID.fromString(id).toString().equals(id)));
        assertTrue(f.provider.requests.stream()
                .allMatch(request -> request.messages().getFirst() instanceof SystemMessage));
        assertTrue(f.provider.requests.get(2).messages().size()
                > f.provider.requests.getFirst().messages().size());
        verify(f.timers.deadline).cancel(false);
        verify(f.timers.heartbeat).cancel(false);
        verify(f.timers.scheduler, never()).shutdown();
    }

    @Test
    void httpCompletionReturnsCanonicalFinalIdWithoutRecountingRecursiveProviderUsage() throws Exception {
        Fixture f = new Fixture();
        var constructor = fun.freechat.service.chat.QueueAwareTokenStream.class.getDeclaredConstructor(
                TokenStream.class, CountDownLatch.class);
        constructor.setAccessible(true);
        CountDownLatch finished = new CountDownLatch(1);
        var queued = constructor.newInstance(f.stream(), finished);
        var api = new fun.freechat.api.ChatApi();
        var chats = testMock(fun.freechat.service.chat.ChatService.class);
        var contexts = testMock(fun.freechat.service.chat.ChatContextService.class);
        var memories = testMock(fun.freechat.service.chat.ChatMemoryService.class);
        var sessions = testMock(fun.freechat.service.chat.ChatSessionService.class);
        var characters = testMock(fun.freechat.service.character.CharacterService.class);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatService", chats);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatContextService", contexts);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatMemoryService", memories);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "chatSessionService", sessions);
        org.springframework.test.util.ReflectionTestUtils.setField(api, "characterService", characters);
        when(contexts.get("chat")).thenReturn(new fun.freechat.model.ChatContext().withApiKeyName("synthetic"));
        when(contexts.getCharacterUid("chat")).thenReturn("character");
        when(characters.getNameByUid("character")).thenReturn("Character");
        when(chats.streamSend(eq("chat"), any(), isNull())).thenReturn(queued);
        var previous = org.springframework.security.core.context.SecurityContextHolder.getContext();
        var mdc = org.slf4j.MDC.getCopyOfContextMap();
        var security = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        security.setAuthentication(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(
                        new fun.freechat.model.User().withUserId("owner").withUsername("owner"), null, List.of()));
        org.springframework.security.core.context.SecurityContextHolder.setContext(security);
        try {
            var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(api)
                    .build();
            var result = mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(
                                    "/api/v2/chat/send/stream/chat")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{\"role\":\"user\",\"contents\":[{\"type\":\"text\",\"content\":\"hello\"}]}"))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.request()
                            .asyncStarted())
                    .andReturn();
            f.provider
                    .handlers
                    .getFirst()
                    .onCompleteResponse(response(AiMessage.from(TOOL_CALL), new TokenUsage(10, 2)));
            f.provider.handlers.get(1).onPartialResponse("answer");
            ChatResponse finalResponse = response(AiMessage.from("answer"), new TokenUsage(20, 3));
            f.provider.handlers.get(1).onCompleteResponse(finalResponse);
            f.provider.handlers.get(1).onCompleteResponse(finalResponse);
            assertEquals(0L, finished.getCount());
            assertEquals(9L, queued.finalMessageId());
            assertEquals(2, f.accounting.size());
            assertEquals(
                    List.of("usage", "tool-message", "tool-execute", "tool-result", "usage", "complete"), f.events);
            List<String> frames = result.getResponse()
                    .getContentAsString()
                    .lines()
                    .filter(line -> line.startsWith("data:"))
                    .toList();
            assertEquals(2, frames.size());
            var last = fun.freechat.service.util.InfoUtils.defaultMapper()
                    .readValue(frames.getLast().substring(5), fun.freechat.api.dto.LlmResultDTO.class);
            assertEquals(9L, last.getMessage().getMessageId());
            assertEquals("stop", last.getFinishReason());
            assertEquals(fun.freechat.api.dto.TokenUsageDTO.from(new TokenUsage(30, 5)), last.getTokenUsage());
            assertNull(last.getText());
            verifyNoInteractions(memories, sessions);
            assertEquals(0, f.aborts.get());
        } finally {
            queued.close();
            org.springframework.security.core.context.SecurityContextHolder.setContext(previous);
            if (mdc == null) {
                org.slf4j.MDC.clear();
            } else {
                org.slf4j.MDC.setContextMap(mdc);
            }
        }
    }

    @Test
    void excessRoundIsAccountedButRejectedBeforeToolMessagesAndExecution() {
        Fixture f = new Fixture();
        f.properties.setMaxToolRounds(1);
        MemoryTokenStream stream = f.stream();
        AtomicInteger errors = new AtomicInteger();
        stream.onError(error -> {
            assertSafe(error);
            errors.incrementAndGet();
        });
        stream.start();
        ChatResponse tool = response(AiMessage.from(TOOL_CALL), new TokenUsage(8, 3));
        f.provider.handlers.getFirst().onCompleteResponse(tool);
        f.provider.handlers.get(1).onCompleteResponse(tool);
        f.provider.handlers.get(1).onCompleteResponse(tool);
        assertEquals(2, f.accounting.size());
        assertEquals(2, f.provider.requests.size());
        assertEquals(1, f.executions.get());
        assertEquals(2, f.writtenUsage.size());
        assertEquals(1, f.aborts.get());
        assertEquals(1, errors.get());
        assertFalse(f.invocation.completed());
    }

    @Test
    void timeoutFencesBeforeNotificationAndCancelsDeduplicatedContextHandles() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        AtomicInteger partials = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        stream.onPartialResponse(value -> partials.incrementAndGet()).onError(error -> {
            assertEquals(1, f.aborts.get());
            assertSafe(error);
            f.events.add("error");
            errors.incrementAndGet();
        });
        stream.start();
        Handle handle = new Handle(() -> f.events.add("cancel"));
        StreamingChatResponseHandler callback = f.provider.handlers.getFirst();
        for (int i = 0; i < 100; i++) {
            callback.onPartialResponse(new PartialResponse("token"), new PartialResponseContext(handle));
            callback.onPartialThinking(new PartialThinking("thinking"), new PartialThinkingContext(handle));
            callback.onPartialToolCall(partialTool(), new PartialToolCallContext(handle));
        }
        f.timers.timeout.run();
        assertEquals(List.of("abort", "error", "cancel"), f.events);
        assertEquals(1, handle.cancellations.get());
        callback.onPartialResponse("late");
        callback.onUnmappedRawEvent(PRIVATE);
        callback.onError(new IllegalArgumentException(PRIVATE));
        ChatResponse late = response(AiMessage.from("late answer"), null);
        callback.onCompleteResponse(late);
        callback.onCompleteResponse(late);
        assertEquals(1, f.accounting.size());
        assertNull(f.accounting.values().iterator().next().getTokenUsage());
        assertTrue(f.writtenUsage.isEmpty());
        assertEquals(100, partials.get());
        assertEquals(1, errors.get());
        Handle lateHandle = new Handle(() -> {});
        callback.onPartialThinking(new PartialThinking("late"), new PartialThinkingContext(lateHandle));
        assertEquals(1, lateHandle.cancellations.get());
        assertFalse(Thread.currentThread().isInterrupted());
    }

    @Test
    void everyPinnedModelCallbackIsForwardedAndSuppressedAfterTerminal() {
        Fixture f = new Fixture();
        MemoryStreamingModel model = f.model();
        AtomicInteger forwarded = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        StreamingChatResponseHandler sink = new StreamingChatResponseHandler() {
            public void onPartialResponse(String value) {
                forwarded.incrementAndGet();
            }

            public void onPartialResponse(PartialResponse value, PartialResponseContext context) {
                forwarded.incrementAndGet();
            }

            public void onPartialThinking(PartialThinking value) {
                forwarded.incrementAndGet();
            }

            public void onPartialThinking(PartialThinking value, PartialThinkingContext context) {
                forwarded.incrementAndGet();
            }

            public void onPartialToolCall(PartialToolCall value) {
                forwarded.incrementAndGet();
            }

            public void onPartialToolCall(PartialToolCall value, PartialToolCallContext context) {
                forwarded.incrementAndGet();
            }

            public void onCompleteToolCall(CompleteToolCall value) {
                forwarded.incrementAndGet();
            }

            public void onUnmappedRawEvent(Object value) {
                forwarded.incrementAndGet();
            }

            public void onCompleteResponse(ChatResponse value) {
                fail("Unexpected completion");
            }

            public void onError(Throwable value) {
                assertSafe(value);
                errors.incrementAndGet();
            }
        };
        model.chat(request(), sink);
        Handle handle = new Handle(() -> {});
        StreamingChatResponseHandler callback = f.provider.handlers.getFirst();
        allCallbacks(callback, handle);
        assertEquals(8, forwarded.get());
        callback.onError(new AssertionError(PRIVATE));
        allCallbacks(callback, handle);
        assertEquals(8, forwarded.get());
        assertEquals(1, errors.get());
        assertEquals(1, f.aborts.get());
    }

    @Test
    void preflightRebuildsMessagesWithoutDiscardingRequestParametersAndDelegatesMetadata() {
        Fixture f = new Fixture();
        MemoryStreamingModel model = f.model();
        ChatRequest request = request();
        model.chat(request, sink());
        ChatRequest actual = f.provider.requests.getFirst();
        assertEquals(request.parameters(), actual.parameters());
        assertEquals(List.of(TOOL), actual.toolSpecifications());
        assertNotEquals(request.messages(), actual.messages());
        assertEquals(USER, actual.messages().getLast());
        assertTrue(((SystemMessage) actual.messages().getFirst()).text().contains("Session memory JSON"));
        assertSame(f.provider.defaults, model.defaultRequestParameters());
        assertSame(f.provider.listeners, model.listeners());
        assertEquals(f.provider.supportedCapabilities(), model.supportedCapabilities());
        assertEquals(f.provider.provider(), model.provider());
        f.lifetime.close();
        model.chat(request, sink());
        assertEquals(1, f.provider.requests.size());
    }

    @Test
    void renewalFailureBeforeRegistrationIsReplayedOnceAndPreventsDelayedStart() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        f.renewFailure.set(true);
        f.timers.renew.run();
        AtomicInteger errors = new AtomicInteger();
        stream.onError(error -> {
            assertSafe(error);
            assertEquals(1, f.aborts.get());
            errors.incrementAndGet();
        });
        stream.onError(error -> errors.incrementAndGet());
        assertSafe(assertThrows(IllegalStateException.class, stream::start));
        assertEquals(1, errors.get());
        assertTrue(f.provider.requests.isEmpty());
        assertEquals(1, f.aborts.get());
    }

    @Test
    void oneStartOnlyAndSuccessfulCallbackExceptionsCannotLeakOrPreventFinalization() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        CountDownLatch finalized = new CountDownLatch(1);
        AtomicInteger completions = new AtomicInteger();
        stream.onCompleteResponse(response -> {
            try {
                completions.incrementAndGet();
                throw new IllegalArgumentException(PRIVATE);
            } finally {
                finalized.countDown();
            }
        });
        stream.start();
        assertSafe(assertThrows(IllegalStateException.class, stream::start));
        assertEquals(0, f.aborts.get());
        ChatResponse response = response(AiMessage.from("answer"), new TokenUsage(1, 2));
        assertDoesNotThrow(() -> f.provider.handlers.getFirst().onCompleteResponse(response));
        assertEquals(0, finalized.getCount());
        assertEquals(1, completions.get());
        assertSafe(assertThrows(IllegalStateException.class, stream::start));
        assertEquals(1, f.provider.requests.size());
    }

    @Test
    void failedCallbackExceptionsAreSwallowedAndAbortExceptionIsSanitized() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        doThrow(new IllegalArgumentException(PRIVATE)).when(f.turns).abort(any());
        CountDownLatch finalized = new CountDownLatch(1);
        stream.onError(error -> {
            try {
                assertSafe(error);
                throw new AssertionError(PRIVATE);
            } finally {
                finalized.countDown();
            }
        });
        stream.start();
        assertDoesNotThrow(() -> f.provider.handlers.getFirst().onError(new IllegalArgumentException(PRIVATE)));
        assertEquals(0, finalized.getCount());
        assertSafe(assertThrows(IllegalStateException.class, f.invocation::check));
    }

    @Test
    void partialCallbackExceptionStopsTurnWithoutExposingPayload() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        AtomicInteger errors = new AtomicInteger();
        stream.onPartialResponse(value -> {
            throw new IllegalArgumentException(PRIVATE);
        });
        stream.onError(error -> {
            assertSafe(error);
            errors.incrementAndGet();
        });
        stream.start();
        assertDoesNotThrow(() -> f.provider.handlers.getFirst().onPartialResponse("token"));
        f.provider.handlers.getFirst().onCompleteResponse(response(AiMessage.from("late"), new TokenUsage(2, 1)));
        assertEquals(1, f.accounting.size());
        assertEquals(1, f.aborts.get());
        assertEquals(1, errors.get());
        assertFalse(f.invocation.completed());
    }

    @Test
    void ignoreErrorsKeepsMandatoryFrameworkBridgeAndNoDoubleRegistration() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        AtomicInteger errors = new AtomicInteger();
        stream.onError(error -> errors.incrementAndGet()).ignoreErrors();
        assertDoesNotThrow(stream::start);
        f.provider.handlers.getFirst().onError(new IllegalArgumentException(PRIVATE));
        stream.onError(error -> errors.incrementAndGet());
        assertEquals(0, errors.get());
        assertEquals(1, f.aborts.get());
    }

    @Test
    void unpersistedCompletionIsNotSuccessAndTerminalBridgesExistBeforeStart() {
        Fixture f = new Fixture();
        TokenStream delegate = testMock(TokenStream.class);
        AtomicReference<Consumer<ChatResponse>> complete = new AtomicReference<>();
        doAnswer(call -> {
                    complete.set(call.getArgument(0));
                    return delegate;
                })
                .when(delegate)
                .onCompleteResponse(any());
        MemoryTokenStream stream = new MemoryTokenStream(delegate, f.invocation, f.lifetime());
        assertNotNull(complete.get());
        complete.get().accept(response(AiMessage.from("not persisted"), null));
        AtomicInteger errors = new AtomicInteger();
        AtomicInteger completions = new AtomicInteger();
        stream.onCompleteResponse(value -> completions.incrementAndGet()).onError(value -> errors.incrementAndGet());
        assertEquals(0, completions.get());
        assertEquals(1, errors.get());
        assertEquals(1, f.aborts.get());
        assertThrows(IllegalStateException.class, stream::start);
        verify(delegate, never()).start();
    }

    @Test
    void successfulCompletionCanBeRegisteredLateWithoutRepeatingCumulativeAccounting() {
        Fixture f = new Fixture();
        MemoryTokenStream stream = f.stream();
        stream.start();
        ChatResponse response = response(AiMessage.from("answer"), new TokenUsage(3, 2));
        f.provider.handlers.getFirst().onCompleteResponse(response);
        AtomicInteger delivered = new AtomicInteger();
        stream.onCompleteResponse(value -> delivered.incrementAndGet());
        stream.onCompleteResponse(value -> delivered.incrementAndGet());
        assertEquals(1, delivered.get());
        assertEquals(1, f.accounting.size());
        assertEquals(0, f.aborts.get());
    }

    @Test
    void synchronousProviderAndPreflightExceptionsAreSanitizedBeforeFrameworkErrorHandling() {
        Fixture f = new Fixture();
        AtomicReference<Throwable> error = new AtomicReference<>();
        StreamingChatModel provider = new StreamingChatModel() {
            @Override
            public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
                throw new IllegalArgumentException(PRIVATE);
            }
        };
        var model = new MemoryStreamingModel(provider, f.invocation, f.lifetime());
        StreamingChatResponseHandler sink = new StreamingChatResponseHandler() {
            public void onCompleteResponse(ChatResponse response) {
                fail("Unexpected completion");
            }

            public void onError(Throwable problem) {
                error.set(problem);
            }
        };
        assertDoesNotThrow(() -> model.chat(request(), sink));
        assertSafe(error.get());
        assertEquals(1, f.aborts.get());

        Fixture stale = new Fixture();
        MemoryStreamingModel rejected = stale.model();
        stale.valid.set(false);
        error.set(null);
        assertDoesNotThrow(() -> rejected.chat(request(), sink));
        assertSafe(error.get());
        assertTrue(stale.provider.requests.isEmpty());
    }

    @Test
    void allTokenStreamNonterminalMethodsStayFluentAndRejectEventsAfterClose() {
        Fixture f = new Fixture();
        List<Runnable> callbacks = new ArrayList<>();
        AtomicInteger delivered = new AtomicInteger();
        TokenStream raw = mock(
                TokenStream.class, withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(call -> {
                    if (call.getArguments().length == 1) {
                        Object callback = call.getArgument(0);
                        if (callback instanceof Consumer<?> consumer) {
                            callbacks.add(() -> consumer.accept(null));
                        } else if (callback instanceof java.util.function.BiConsumer<?, ?> consumer) {
                            callbacks.add(() -> consumer.accept(null, null));
                        }
                    }
                    return call.getMethod().getReturnType() == TokenStream.class ? call.getMock() : null;
                }));
        MemoryTokenStream stream = new MemoryTokenStream(raw, f.invocation, f.lifetime());
        callbacks.clear(); // The two terminal bridges are tested separately.
        assertSame(stream, stream.onPartialResponse(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onPartialResponseWithContext((value, context) -> delivered.incrementAndGet()));
        assertSame(stream, stream.onPartialThinking(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onPartialThinkingWithContext((value, context) -> delivered.incrementAndGet()));
        assertSame(stream, stream.onPartialToolCall(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onPartialToolCallWithContext((value, context) -> delivered.incrementAndGet()));
        assertSame(stream, stream.onRetrieved(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onIntermediateResponse(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.beforeToolExecution(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onToolExecuted(value -> delivered.incrementAndGet()));
        assertSame(stream, stream.onUnmappedRawEvent(value -> delivered.incrementAndGet()));
        assertEquals(11, callbacks.size());
        callbacks.forEach(Runnable::run);
        assertEquals(11, delivered.get());
        stream.close();
        callbacks.forEach(callback -> assertSafe(assertThrows(IllegalStateException.class, callback::run)));
        assertEquals(11, delivered.get());
    }

    @Test
    void constructorSchedulingFailureAbortsAndDisposesAlreadyScheduledTimer() {
        Fixture f = new Fixture();
        when(f.timers.scheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any()))
                .thenThrow(new RejectedExecutionException(PRIVATE));
        assertSafe(assertThrows(IllegalStateException.class, f::lifetime));
        assertEquals(1, f.aborts.get());
        verify(f.timers.deadline).cancel(false);
        verify(f.timers.scheduler, never()).shutdown();
    }

    @Test
    void finishBeforeSqlCompletionFailsClosed() {
        Fixture f = new Fixture();
        assertSafe(assertThrows(IllegalStateException.class, () -> f.lifetime().finish()));
        assertEquals(1, f.aborts.get());
        assertFalse(f.invocation.completed());
    }

    @Test
    void callbackCanReenterLifetimeFromAnotherThreadWithoutHeartbeatMonitorDeadlock() throws Exception {
        Fixture f = new Fixture();
        MemoryTurnLifetime lifetime = f.lifetime();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        lifetime.onFailure(error -> {
            try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                executor.submit(lifetime::close).get(2, TimeUnit.SECONDS);
            } catch (Throwable problem) {
                failure.set(problem);
            }
        });
        lifetime.close();
        assertNull(failure.get());
        assertEquals(1, f.aborts.get());
    }

    @Test
    void cancellationRegistryIsBoundedAndExcessDistinctHandlesFailClosed() {
        Fixture f = new Fixture();
        f.properties.setMaxToolRounds(1);
        MemoryTokenStream stream = f.stream();
        stream.start();
        Handle a = new Handle(() -> {});
        Handle b = new Handle(() -> {});
        Handle c = new Handle(() -> {});
        StreamingChatResponseHandler callback = f.provider.handlers.getFirst();
        callback.onPartialResponse(new PartialResponse("a"), new PartialResponseContext(a));
        callback.onPartialThinking(new PartialThinking("b"), new PartialThinkingContext(b));
        callback.onPartialToolCall(partialTool(), new PartialToolCallContext(c));
        assertEquals(1, f.aborts.get());
        assertEquals(1, a.cancellations.get());
        assertEquals(1, b.cancellations.get());
        assertEquals(1, c.cancellations.get());
    }

    @Test
    void realSchedulerStartsBeforeTokenStartAndUsesSqlRemainingTimeNotLeaseWallClock() throws Exception {
        Fixture f = new Fixture();
        when(f.turns.remainingMillis(any())).thenReturn(40L);
        f.properties.setLeaseRenewInterval(Duration.ofMillis(5));
        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)) {
            MemoryTurnLifetime lifetime = new MemoryTurnLifetime(f.turns, f.invocation, f.properties, scheduler);
            CountDownLatch timedOut = new CountDownLatch(1);
            AtomicBoolean abortedBeforeNotification = new AtomicBoolean();
            lifetime.onFailure(error -> {
                abortedBeforeNotification.set(f.aborts.get() == 1);
                timedOut.countDown();
            });
            assertTrue(timedOut.await(3, TimeUnit.SECONDS));
            assertTrue(abortedBeforeNotification.get());
            assertFalse(scheduler.isShutdown());
            assertFalse(Thread.currentThread().isInterrupted());
            assertThrows(IllegalStateException.class, lifetime::check);
            verify(f.turns).remainingMillis(f.invocation.lease());
        }
    }

    private static ChatResponse response(AiMessage message, TokenUsage usage) {
        return ChatResponse.builder().aiMessage(message).tokenUsage(usage).build();
    }

    private static ChatRequest request() {
        return ChatRequest.builder()
                .messages(UserMessage.from("stale"))
                .parameters(DefaultChatRequestParameters.builder()
                        .temperature(0.2)
                        .maxOutputTokens(37)
                        .toolSpecifications(TOOL)
                        .build())
                .build();
    }

    private static PartialToolCall partialTool() {
        return PartialToolCall.builder()
                .index(0)
                .id("tool-call")
                .name("lookup")
                .partialArguments("{")
                .build();
    }

    private static void allCallbacks(StreamingChatResponseHandler callback, Handle handle) {
        callback.onPartialResponse("token");
        callback.onPartialResponse(new PartialResponse("token"), new PartialResponseContext(handle));
        callback.onPartialThinking(new PartialThinking("thinking"));
        callback.onPartialThinking(new PartialThinking("thinking"), new PartialThinkingContext(handle));
        callback.onPartialToolCall(partialTool());
        callback.onPartialToolCall(partialTool(), new PartialToolCallContext(handle));
        callback.onCompleteToolCall(new CompleteToolCall(0, TOOL_CALL));
        callback.onUnmappedRawEvent("raw");
    }

    private static StreamingChatResponseHandler sink() {
        return new StreamingChatResponseHandler() {
            public void onCompleteResponse(ChatResponse response) {}

            public void onError(Throwable error) {
                assertSafe(error);
            }
        };
    }

    private static void assertSafe(Throwable error) {
        assertNotNull(error);
        assertFalse(error.toString().contains(PRIVATE));
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static final class Handle implements StreamingHandle {
        private final AtomicInteger cancellations = new AtomicInteger();
        private final Runnable action;

        private Handle(Runnable action) {
            this.action = action;
        }

        public void cancel() {
            cancellations.incrementAndGet();
            action.run();
        }

        public boolean isCancelled() {
            return cancellations.get() > 0;
        }
    }

    private static final class Provider implements StreamingChatModel {
        private final List<ChatRequest> requests = new ArrayList<>();
        private final List<StreamingChatResponseHandler> handlers = new ArrayList<>();
        private final ChatRequestParameters defaults =
                DefaultChatRequestParameters.builder().temperature(0.4).build();
        private final List<ChatModelListener> listeners = List.of(new ChatModelListener() {});

        @Override
        public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
            requests.add(request);
            handlers.add(handler);
        }

        @Override
        public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
            fail("The wrapper must delegate chat, not doChat");
        }

        public ChatRequestParameters defaultRequestParameters() {
            return defaults;
        }

        public List<ChatModelListener> listeners() {
            return listeners;
        }

        public Set<Capability> supportedCapabilities() {
            return Set.of();
        }

        public ModelProvider provider() {
            return ModelProvider.OTHER;
        }
    }

    private static final class Timers {
        private final ScheduledExecutorService scheduler = testMock(ScheduledExecutorService.class);
        private final ScheduledFuture<?> deadline = testMock(ScheduledFuture.class);
        private final ScheduledFuture<?> heartbeat = testMock(ScheduledFuture.class);
        private Runnable timeout;
        private Runnable renew;

        private Timers() {
            doAnswer(call -> {
                        timeout = call.getArgument(0);
                        return deadline;
                    })
                    .when(scheduler)
                    .schedule(any(Runnable.class), anyLong(), any());
            doAnswer(call -> {
                        renew = call.getArgument(0);
                        return heartbeat;
                    })
                    .when(scheduler)
                    .scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any());
        }
    }

    private static <T> T testMock(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    private static final class Fixture {
        private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
        private final MemoryTurnRepository turns = testMock(MemoryTurnRepository.class);
        private final Timers timers = new Timers();
        private final Provider provider = new Provider();
        private final List<String> events = Collections.synchronizedList(new ArrayList<>());
        private final List<TokenUsage> writtenUsage = new ArrayList<>();
        private final Map<String, ChatMemoryCommit> accounting = new LinkedHashMap<>();
        private final AtomicInteger aborts = new AtomicInteger();
        private final AtomicInteger executions = new AtomicInteger();
        private final AtomicBoolean valid = new AtomicBoolean(true);
        private final AtomicBoolean renewFailure = new AtomicBoolean();
        private final MemoryInvocation invocation;
        private MemoryTurnLifetime lifetime;

        private Fixture() {
            MemoryScope scope =
                    new MemoryScope("chat", "owner", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
            var lease =
                    new MemoryTurnRepository.TurnLease(scope, UUID.randomUUID().toString(), NOW.plusDays(100), 1);
            ChatMemoryState state = new ChatMemoryState()
                    .withChatId("chat")
                    .withFingerprint(FINGERPRINT)
                    .withLatestFinalizedId(0L)
                    .withOverflowThroughId(0L);
            when(turns.read("chat")).thenReturn(Optional.of(state));
            when(turns.remainingMillis(any())).thenReturn(60_000L);
            doAnswer(call -> {
                        requireValid();
                        return null;
                    })
                    .when(turns)
                    .check(any());
            when(turns.renew(any())).thenAnswer(call -> {
                requireValid();
                if (renewFailure.get()) {
                    throw new IllegalArgumentException(PRIVATE);
                }
                return lease.deadline();
            });
            doAnswer(call -> {
                        valid.set(false);
                        aborts.incrementAndGet();
                        events.add("abort");
                        return null;
                    })
                    .when(turns)
                    .abort(any());
            when(turns.append(any(), any(), any(), any(), any(), any())).thenAnswer(call -> {
                requireValid();
                ChatMessage message = call.getArgument(1);
                if (!(message instanceof UserMessage)) {
                    events.add(message instanceof AiMessage ? "tool-message" : "tool-result");
                    writtenUsage.add(call.getArgument(5));
                }
                return 2L;
            });
            when(turns.complete(any(), any(), any(), any())).thenAnswer(call -> {
                requireValid();
                valid.set(false);
                events.add("complete");
                writtenUsage.add(call.getArgument(3));
                return 9L;
            });
            ChatMemoryCoordinationMapper coordination = testMock(ChatMemoryCoordinationMapper.class);
            when(coordination.databaseNow()).thenReturn(NOW);
            when(coordination.lock(any())).thenReturn(Optional.of(state));
            ChatMemoryCommitMapper commits = testMock(ChatMemoryCommitMapper.class);
            when(commits.selectByPrimaryKey(any()))
                    .thenAnswer(call -> Optional.ofNullable(accounting.get(call.getArgument(0))));
            when(commits.insertSelective(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                accounting.put(row.getAttemptId(), row);
                events.add("usage");
                return 1;
            });
            PlatformTransactionManager transactions = testMock(PlatformTransactionManager.class);
            when(transactions.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
            var publications = new MemoryPublicationRepository(
                    coordination,
                    testMock(ChatMemoryStateMapper.class),
                    commits,
                    testMock(ChatHistoryMapper.class),
                    transactions,
                    properties);
            TokenCountEstimator estimator = testMock(TokenCountEstimator.class);
            when(estimator.estimateTokenCountInText(anyString())).thenReturn(1);
            MemoryBounds bounds = new MemoryBounds(estimator, properties);
            var resolved = new MemoryModelResolver.Resolved(
                    null, "fake-model", "en", "", "", FINGERPRINT, "character", "owner", 100, 2, null);
            invocation = new MemoryInvocation(
                    lease,
                    resolved,
                    turns,
                    new MemorySourceReader(turns),
                    publications,
                    new MemoryPublisher(publications, null),
                    null,
                    bounds,
                    properties);
            invocation.add(SystemMessage.from("base"));
            invocation.addInput(USER, USER);
            invocation.tools(List.of(TOOL));
        }

        private void requireValid() {
            if (!valid.get()) {
                throw new IllegalStateException(PRIVATE);
            }
        }

        private MemoryTurnLifetime lifetime() {
            if (lifetime == null) {
                lifetime = new MemoryTurnLifetime(turns, invocation, properties, timers.scheduler);
            }
            return lifetime;
        }

        private MemoryStreamingModel model() {
            return new MemoryStreamingModel(provider, invocation, lifetime());
        }

        private MemoryTokenStream stream() {
            AiServiceContext context = AiServiceContext.create(MemoryStreamingTest.class);
            context.streamingChatModel = model();
            context.initChatMemories(invocation);
            context.toolService.maxToolCallingRoundTrips(properties.getMaxToolRounds());
            InvocationContext call = InvocationContext.builder()
                    .chatMemoryId("chat")
                    .methodName("test")
                    .interfaceName("MemoryStreamingTest")
                    .methodArguments(List.of())
                    .build();
            ToolServiceContext tools = new ToolServiceContext(List.of(TOOL), Map.of("lookup", (request, id) -> {
                invocation.check();
                executions.incrementAndGet();
                events.add("tool-execute");
                return "found";
            }));
            AiServiceTokenStream raw = new AiServiceTokenStream(AiServiceTokenStreamParameters.builder()
                    .messages(invocation.messages())
                    .context(context)
                    .invocationContext(call)
                    .toolServiceContext(tools)
                    .toolArgumentsErrorHandler(context.toolService.argumentsErrorHandler())
                    .toolExecutionErrorHandler(context.toolService.executionErrorHandler())
                    .build());
            return new MemoryTokenStream(raw, invocation, lifetime());
        }
    }
}
