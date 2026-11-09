package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.api.ChatApi;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatRunEvent;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.MemoryUsage;
import fun.freechat.service.chat.memory.MemoryBounds.PreparationDeferredException;
import fun.freechat.service.enums.QuotaType;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.StandardServletAsyncWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

class ChatApiMemoryTest {
    private static final String PRIVATE = "FABRICATED_PRIVATE_API_PAYLOAD";
    private static final ChatMessageDTO INPUT = ChatMessageDTO.from(UserMessage.from("hello"), null);
    private final ChatApi api = new ChatApi();
    private final ChatService chats = testMock(ChatService.class);
    private final ChatMemoryService memories = testMock(ChatMemoryService.class);
    private final ChatContextService contexts = testMock(ChatContextService.class);
    private final ChatSessionService sessions = testMock(ChatSessionService.class);
    private final CharacterService characters = testMock(CharacterService.class);
    private final ChatSession legacy = testMock(ChatSession.class);
    private final ChatContext context =
            new ChatContext().withChatId("chat").withQuotaType("tokens").withQuota(100L);
    private final ChatRunHandle handle = testMock(ChatRunHandle.class);
    private final CompletableFuture<Void> delivered = new CompletableFuture<>();
    private final CompletableFuture<Void> settled = new CompletableFuture<>();
    private Consumer<ChatRunEvent> observer;
    private long seq;
    private SecurityContext previousSecurity;
    private Map<String, String> previousMdc;

    @BeforeEach
    void prepareBoundariesWithoutApplicationOrExternalServices() {
        previousSecurity = SecurityContextHolder.getContext();
        previousMdc = MDC.getCopyOfContextMap();
        SecurityContext authenticated = SecurityContextHolder.createEmptyContext();
        authenticated.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                new User().withUserId("owner").withUsername("owner"), null, List.of()));
        SecurityContextHolder.setContext(authenticated);
        ReflectionTestUtils.setField(api, "chatService", chats);
        ReflectionTestUtils.setField(api, "chatMemoryService", memories);
        ReflectionTestUtils.setField(api, "chatContextService", contexts);
        ReflectionTestUtils.setField(api, "chatSessionService", sessions);
        ReflectionTestUtils.setField(api, "characterService", characters);
        when(contexts.get("chat")).thenReturn(context);
        when(contexts.getCharacterUid("chat")).thenReturn("character");
        when(characters.getNameByUid(anyString())).thenReturn("Character");
        when(memories.usage("chat")).thenReturn(new MemoryUsage(1L, new TokenUsage(2, 3)));
        when(sessions.get("chat")).thenReturn(legacy);
        when(handle.eventsDelivered()).thenReturn(delivered);
        when(handle.settled()).thenReturn(settled);
        when(chats.startRun(eq("chat"), any(), isNull(), any())).thenAnswer(call -> {
            observer = call.getArgument(3);
            emit("run.queued", Map.of("phase", "queued"));
            return handle;
        });
        when(chats.startAssistantRun(eq("chat"), eq("assistant"), any())).thenAnswer(call -> {
            observer = call.getArgument(2);
            emit("run.queued", Map.of("phase", "queued"));
            return handle;
        });
    }

    @AfterEach
    void restoreThreadContext() {
        SecurityContextHolder.setContext(previousSecurity);
        if (previousMdc == null) {
            MDC.clear();
        } else {
            MDC.setContextMap(previousMdc);
        }
    }

    @ParameterizedTest
    @EnumSource(
            value = QuotaType.class,
            names = {"MESSAGES", "TOKENS"})
    void quotasUseFreshDurableAccountingBeforeStartingAnyProvider(QuotaType type) {
        context.setQuotaType(type.text());
        context.setQuota(5L);
        when(memories.usage("chat")).thenReturn(new MemoryUsage(5L, new TokenUsage(2, 3)));
        assertQuotaRejected(() -> api.send("chat", INPUT));
        assertQuotaRejected(
                () -> api.streamSend("chat", INPUT, new MockHttpServletRequest(), new MockHttpServletResponse()));
        assertQuotaRejected(() -> api.sendAssistant("chat", "assistant"));
        assertQuotaRejected(() -> api.streamSendAssistant(
                "chat", "assistant", new MockHttpServletRequest(), new MockHttpServletResponse()));
        verify(memories, times(4)).usage("chat");
        verifyNoInteractions(chats, sessions);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void streamQuotaRejectionRemainsHttpBeforeAdmission(boolean assistant) throws Exception {
        context.setQuota(0L);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(api).build();
        mvc.perform(streamRequest(assistant))
                .andExpect(status().isTooManyRequests())
                .andExpect(request().asyncNotStarted());
        verifyNoInteractions(chats, handle);
    }

    @Test
    void malformedStreamInputRemainsHttpBeforeAdmission() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(api).build();
        mvc.perform(post("/api/v2/chat/send/stream/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(request().asyncNotStarted());
        verifyNoInteractions(chats, handle);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ownCredentialsBypassSharedQuota(boolean named) {
        context.setQuota(0L);
        if (named) {
            context.setApiKeyName("synthetic-key-name");
        } else {
            context.setApiKeyValue("synthetic-key-value");
        }
        when(chats.send(eq("chat"), any(), isNull())).thenReturn(Pair.of(response(), 71L));
        assertEquals(71L, api.send("chat", INPUT).getMessage().getMessageId());
        verifyNoInteractions(memories, sessions);
    }

    @Test
    void synchronousResponseUsesExactFinalIdWithoutEqualityRewrite() {
        when(chats.send(eq("chat"), any(), isNull())).thenReturn(Pair.of(response(), 71L));
        var result = api.send("chat", INPUT);
        assertEquals(71L, result.getMessage().getMessageId());
        assertEquals("answer", result.getText());
        verify(memories).usage("chat");
        verifyNoMoreInteractions(memories);
        verifyNoInteractions(sessions);
    }

    static Stream<Arguments> disconnects() {
        return Stream.of(false, true)
                .flatMap(assistant ->
                        Stream.of("timeout", "error", "complete").map(event -> Arguments.of(assistant, event)));
    }

    @ParameterizedTest
    @MethodSource("disconnects")
    void servletLifecycleCallbacksCloseBothKindsOfStream(boolean assistant, String event) throws Exception {
        MvcResult result = startHttp(assistant);
        MockAsyncContext async = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (var listener : async.getListeners()) {
            AsyncEvent notification =
                    new AsyncEvent(async, result.getRequest(), result.getResponse(), new IOException(PRIVATE));
            switch (event) {
                case "timeout" -> listener.onTimeout(notification);
                case "error" -> listener.onError(notification);
                case "complete" -> listener.onComplete(notification);
                default -> fail("Unknown fixture event");
            }
        }
        verify(handle, atLeastOnce()).cancel();
        verifyNoInteractions(sessions);
        verify(memories).usage("chat");
        verifyNoMoreInteractions(memories);
    }

    @ParameterizedTest
    @MethodSource("disconnects")
    void firstBufferedFlushFailureStillCancelsThroughMvcLifecycle(boolean assistant, String event) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAsyncSupported(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        IOException brokenConnection = new IOException(PRIVATE);
        HttpServletResponse failingResponse = new HttpServletResponseWrapper(response) {
            @Override
            public void flushBuffer() throws IOException {
                throw brokenConnection;
            }
        };
        var webRequest = new StandardServletAsyncWebRequest(request, failingResponse);
        var manager = WebAsyncUtils.getAsyncManager(request);
        manager.setAsyncWebRequest(webRequest);
        var emitter = assistant
                ? api.streamSendAssistant("chat", "assistant", request, failingResponse)
                : api.streamSend("chat", INPUT, request, failingResponse);
        assertFalse(request.isAsyncStarted(), "Only Spring MVC may start async processing");
        assertTrue(response.getContentAsString().isEmpty(), "queued is buffered until MVC initializes the emitter");
        verify(handle, never()).cancel();

        var returnType = new MethodParameter(
                ChatApi.class.getMethod(
                        assistant ? "streamSendAssistant" : "streamSend",
                        String.class,
                        assistant ? String.class : ChatMessageDTO.class,
                        HttpServletRequest.class,
                        HttpServletResponse.class),
                -1);
        var handler = new ResponseBodyEmitterReturnValueHandler(List.of(new JacksonJsonHttpMessageConverter()));
        IOException failure = assertThrows(
                IOException.class,
                () -> handler.handleReturnValue(emitter, returnType, new ModelAndViewContainer(), webRequest));
        assertSame(brokenConnection, failure.getCause());
        assertTrue(response.getContentAsString().contains("event:run.queued"));
        assertTrue(request.isAsyncStarted());
        Object emitterHandler = ReflectionTestUtils.getField(emitter, "handler");
        var deferred = (DeferredResult<?>) ReflectionTestUtils.getField(emitterHandler, "deferredResult");
        assertNotNull(deferred);
        // Actual Spring initialize failed before installing any emitter lifecycle callbacks.
        assertNull(ReflectionTestUtils.getField(deferred, "errorCallback"));
        assertNull(ReflectionTestUtils.getField(deferred, "completionCallback"));
        assertNull(ReflectionTestUtils.getField(deferred, "timeoutCallback"));

        MockAsyncContext async = (MockAsyncContext) request.getAsyncContext();
        assertFalse(async.getListeners().isEmpty());
        // Simulate the container notification after the real first flush IOException, not emitter.onError.
        if (event.equals("complete")) {
            async.complete();
        } else {
            for (var listener : async.getListeners()) {
                AsyncEvent notification = new AsyncEvent(async, request, response, failure);
                if (event.equals("error")) listener.onError(notification);
                else listener.onTimeout(notification);
            }
            assertTrue(manager.hasConcurrentResult(), "MVC must dispatch an error instead of leaving SSE hanging");
            assertInstanceOf(Throwable.class, manager.getConcurrentResult());
        }
        verify(handle, atLeastOnce()).cancel();
    }

    static Stream<Arguments> deliveryFailureTimings() {
        return Stream.of(false, true)
                .flatMap(assistant -> Stream.of(false, true).map(early -> Arguments.of(assistant, early)));
    }

    @ParameterizedTest
    @MethodSource("deliveryFailureTimings")
    void eventDeliveryFailureUnhooksSseEvenWithoutObserverException(boolean assistant, boolean early) throws Exception {
        if (early) delivered.completeExceptionally(new IOException(PRIVATE));
        MvcResult result = startHttp(assistant);
        if (!early) {
            settled.complete(null);
            assertFalse(WebAsyncUtils.getAsyncManager(result.getRequest()).hasConcurrentResult());
            delivered.completeExceptionally(new IOException(PRIVATE));
        }
        var error = assertInstanceOf(IllegalStateException.class, result.getAsyncResult(1000));
        assertEquals("Chat response delivery failed", error.getMessage());
        assertNull(error.getCause(), "Do not expose private provider/delivery details");
        String body = result.getResponse().getContentAsString();
        assertFalse(body.contains(PRIVATE));
        assertFalse(body.contains("run.completed"));
        verify(handle).cancel();
        ((MockAsyncContext) result.getRequest().getAsyncContext()).complete();
        verify(handle, times(1)).cancel();
        int bodyLength = body.length();
        assertThrows(IllegalStateException.class, () -> emit("message.delta", Map.of("delta", "too late")));
        assertEquals(bodyLength, result.getResponse().getContentAsString().length());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void successfulFuturesNeverReplaceTheQueuedTerminalEvent(boolean assistant) throws Exception {
        MvcResult result = startHttp(assistant);
        var manager = WebAsyncUtils.getAsyncManager(result.getRequest());
        settled.complete(null);
        assertFalse(manager.hasConcurrentResult(), "settled is not event delivery acknowledgement");
        emit("message.delta", Map.of("delta", "answer"));
        delivered.complete(null);
        assertFalse(manager.hasConcurrentResult(), "Only a terminal event may successfully complete SSE");
        emit("run.completed", Map.of("response", response(), "messageId", 88L, "completed", true, "partial", false));
        assertTrue(result.getResponse().getContentAsString().contains("event:run.completed"));
        assertNull(result.getAsyncResult(1000));
        ((MockAsyncContext) result.getRequest().getAsyncContext()).complete();
        verify(handle, never()).cancel();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void immediatelySendsNamedFlatEventsWithoutInventingAMessage(boolean assistant) throws Exception {
        MvcResult result = startHttp(assistant);
        String queued = result.getResponse().getContentAsString();
        assertTrue(queued.contains("event:run.queued"));
        assertTrue(queued.contains("\"run_id\":\"run\""));
        assertTrue(queued.contains("\"session_id\":\"chat\""));
        assertTrue(queued.contains("\"seq\":1"));
        assertTrue(queued.contains("\"v\":1"));
        assertTrue(queued.contains("\"timestamp\":"));
        assertFalse(queued.contains("message.delta"));
        assertFalse(queued.contains("\"message\""));
        assertEquals("no", result.getResponse().getHeader("X-Accel-Buffering"));
        assertEquals("no-store", result.getResponse().getHeader("Cache-Control"));
        emit("run.started", Map.of("phase", "preparing"));
        emit("run.status", Map.of("phase", "thinking"));
        emit("reasoning.delta", Map.of("delta", "consider"));
        emit("run.status", Map.of("phase", "responding"));
        emit("message.delta", Map.of("delta", "answer"));
        emit("run.completed", Map.of("response", response(), "messageId", 88L, "completed", true, "partial", false));
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("event:reasoning.delta"));
        assertTrue(body.contains("event:message.delta"));
        assertTrue(body.contains("\"delta\":\"answer\""));
        assertTrue(body.contains("event:run.completed"));
        assertTrue(body.contains("\"result\":"));
        assertTrue(body.contains("\"messageId\":88"));
        assertTrue(body.contains("\"finishReason\":\"stop\""));
        assertTrue(body.contains("\"tokenUsage\""));
        assertFalse(body.contains("\"response\""));
        assertNull(result.getAsyncResult(1000));
        verifyNoInteractions(sessions);
        verify(memories).usage("chat");
        verifyNoMoreInteractions(memories);
        verify(chats, never()).streamSend(anyString(), any(), any());
        verify(chats, never()).streamSendAssistant(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void acceptedPreparationFailureIsTerminalSseNotHttpError(boolean assistant) throws Exception {
        MvcResult result = startHttp(assistant);
        emit("run.started", Map.of("phase", "preparing"));
        emit(
                "run.failed",
                Map.of(
                        "errorCode",
                        "CHAT_MEMORY_PREPARING",
                        "message",
                        "Please retry your message.",
                        "retryAfter",
                        1,
                        "completed",
                        false,
                        "partial",
                        false));
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("event:run.failed"));
        assertTrue(body.contains("\"error\":{\"code\":\"CHAT_MEMORY_PREPARING\""));
        assertTrue(body.contains("\"retry_after\":1"));
        assertFalse(body.contains("errorCode"));
        assertFalse(body.contains(PRIVATE));
        assertNull(result.getAsyncResult(1000));
    }

    @Test
    void cancellationIsExplicitAndNeverLooksLikeSuccess() throws Exception {
        MvcResult result = startHttp(false);
        emit("message.delta", Map.of("delta", "partial answer"));
        emit("run.status", Map.of("phase", "cancelling"));
        emit("run.cancelled", Map.of("completed", false, "partial", true, "interrupted", true));
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("event:run.cancelled"));
        assertTrue(body.contains("\"interrupted\":true"));
        assertFalse(body.contains("run.completed"));
        assertNull(result.getAsyncResult(1000));
    }

    @Test
    void synchronousMemoryPreparationDeferralStillReturnsRetryableHttpError() throws Exception {
        doThrow(new PreparationDeferredException(PRIVATE)).when(chats).send(eq("chat"), any(), isNull());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(api).build();
        MvcResult result = mvc.perform(post("/api/v2/chat/send/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"user\",\"contents\":[{\"type\":\"text\",\"content\":\"hello\"}]}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(request().asyncNotStarted())
                .andExpect(header().string("Retry-After", "1"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.errorCode").value("CHAT_MEMORY_PREPARING"))
                .andReturn();
        assertFalse(result.getResponse().getContentAsString().contains(PRIVATE));
    }

    private void emit(String name, Map<String, Object> data) {
        observer.accept(
                new ChatRunEvent(name, "run", "chat", ++seq, Instant.ofEpochSecond(1700000000, 125000000), data));
    }

    private MvcResult startHttp(boolean assistant) throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(api).build();
        return mvc.perform(streamRequest(assistant))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();
    }

    private static MockHttpServletRequestBuilder streamRequest(boolean assistant) {
        return assistant
                ? get("/api/v2/chat/send/stream/assistant/chat/assistant")
                : post("/api/v2/chat/send/stream/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"user\",\"contents\":[{\"type\":\"text\",\"content\":\"hello\"}]}");
    }

    private static void assertQuotaRejected(org.junit.jupiter.api.function.Executable action) {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, action);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, error.getStatusCode());
    }

    private static ChatResponse response() {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from("answer"))
                .tokenUsage(new TokenUsage(2, 3))
                .build();
    }

    private static <T> T testMock(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
