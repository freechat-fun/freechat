package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import fun.freechat.api.ChatApi;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.MemoryUsage;
import fun.freechat.service.enums.QuotaType;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

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
    private final TokenStream stream = mock(
            TokenStream.class,
            withSettings()
                    .mockMaker(MockMakers.SUBCLASS)
                    .extraInterfaces(AutoCloseable.class)
                    .defaultAnswer(RETURNS_SELF));
    private Consumer<String> partial;
    private Consumer<PartialThinking> thinking;
    private Consumer<ChatResponse> complete;
    private Consumer<Throwable> error;
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
        when(chats.streamSend(eq("chat"), any(), isNull())).thenReturn(stream);
        when(chats.streamSendAssistant("chat", "assistant")).thenReturn(stream);
        doAnswer(call -> {
                    partial = call.getArgument(0);
                    return stream;
                })
                .when(stream)
                .onPartialResponse(any());
        doAnswer(call -> {
                    thinking = call.getArgument(0);
                    return stream;
                })
                .when(stream)
                .onPartialThinking(any());
        doAnswer(call -> {
                    complete = call.getArgument(0);
                    return stream;
                })
                .when(stream)
                .onCompleteResponse(any());
        doAnswer(call -> {
                    error = call.getArgument(0);
                    return stream;
                })
                .when(stream)
                .onError(any());
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
        assertQuotaRejected(() -> api.streamSend("chat", INPUT, new MockHttpServletResponse()));
        assertQuotaRejected(() -> api.sendAssistant("chat", "assistant"));
        assertQuotaRejected(() -> api.streamSendAssistant("chat", "assistant", new MockHttpServletResponse()));
        verify(memories, times(4)).usage("chat");
        verifyNoInteractions(chats, sessions);
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

    static Stream<Arguments> setupFailures() {
        return Stream.of(false, true)
                .flatMap(assistant -> Stream.of("identity", "register", "start", "header", "close")
                        .map(phase -> Arguments.of(assistant, phase)));
    }

    @ParameterizedTest
    @MethodSource("setupFailures")
    void admittedStreamsCloseWhenSetupFailsAndOriginalErrorDoesNotEscape(boolean assistant, String phase)
            throws Exception {
        HttpServletResponse response = testMock(HttpServletResponse.class);
        switch (phase) {
            case "identity" -> when(characters.getNameByUid(anyString())).thenThrow(new IllegalStateException(PRIVATE));
            case "register" ->
                doThrow(new IllegalStateException(PRIVATE)).when(stream).onPartialResponse(any());
            case "start", "close" ->
                doThrow(new IllegalStateException(PRIVATE)).when(stream).start();
            case "header" ->
                doThrow(new IllegalStateException(PRIVATE)).when(response).addHeader(anyString(), anyString());
            default -> fail("Unknown fixture phase");
        }
        if (phase.equals("close")) {
            doThrow(new IOException(PRIVATE)).when((AutoCloseable) stream).close();
        }
        ResponseStatusException failure = assertThrows(ResponseStatusException.class, () -> {
            if (assistant) {
                api.streamSendAssistant("chat", "assistant", response);
            } else {
                api.streamSend("chat", INPUT, response);
            }
        });
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, failure.getStatusCode());
        assertEquals("Chat stream initialization failed", failure.getReason());
        assertNull(failure.getCause());
        verify((AutoCloseable) stream).close();
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
        verify((AutoCloseable) stream, atLeastOnce()).close();
        verifyNoInteractions(sessions);
        verify(memories).usage("chat");
        verifyNoMoreInteractions(memories);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void latePartialDeliveryAfterServletCompletionClosesStream(boolean useThinking) throws Exception {
        MvcResult result = startHttp(false);
        MockAsyncContext async = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (var listener : async.getListeners()) {
            listener.onComplete(new AsyncEvent(async));
        }
        clearInvocations(stream);
        if (useThinking) {
            thinking.accept(new PartialThinking("late"));
        } else {
            partial.accept("late");
        }
        verify((AutoCloseable) stream).close();
    }

    @Test
    void legacyCompletionStillPersistsUsageAndReturnsItsStoredId() throws Exception {
        when(memories.updateChatMessageTokenUsage(
                        "chat", response().aiMessage(), response().tokenUsage()))
                .thenReturn(88L);
        MvcResult result = startHttp(false);
        partial.accept("answer");
        complete.accept(response());
        verify(legacy).addMemoryUsage(1L, response().tokenUsage());
        verify(memories)
                .updateChatMessageTokenUsage(
                        "chat", response().aiMessage(), response().tokenUsage());
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains("\"messageId\":88"));
        assertTrue(body.contains("\"finishReason\":\"stop\""));
        assertEquals("no", result.getResponse().getHeader("X-Accel-Buffering"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void providerErrorsExposeOnlyFixedServletFailure(boolean assistant) throws Exception {
        MvcResult result = startHttp(assistant);
        error.accept(new IllegalStateException(PRIVATE));
        Object terminal = result.getAsyncResult(1000);
        assertInstanceOf(IllegalStateException.class, terminal);
        assertEquals("Chat stream failed", ((Throwable) terminal).getMessage());
        assertNull(((Throwable) terminal).getCause());
        assertFalse(result.getResponse().getContentAsString().contains(PRIVATE));
    }

    private MvcResult startHttp(boolean assistant) throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(api).build();
        var request = assistant
                ? get("/api/v2/chat/send/stream/assistant/chat/assistant")
                : post("/api/v2/chat/send/stream/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"user\",\"contents\":[{\"type\":\"text\",\"content\":\"hello\"}]}");
        return mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();
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
