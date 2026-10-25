package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.TgChatService;
import fun.freechat.service.connectors.*;
import fun.freechat.service.skills.SkillCharacterAccess;
import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

class ConnectorRuntimePolicyTest {
    private static final String ID = "a".repeat(32);
    private final ChatContextMapper contexts = fake(ChatContextMapper.class);
    private final CharacterBackendMapper backends = fake(CharacterBackendMapper.class);
    private final CharacterInfoMapper characters = fake(CharacterInfoMapper.class);
    private final OrgRelationshipMapper relationships = fake(OrgRelationshipMapper.class);
    private final UserMapper users = fake(UserMapper.class);
    private final TgChatService telegramChats = fake(TgChatService.class);
    private final ConnectorService connectors = fake(ConnectorService.class);
    private final ConnectorClientFactory clients = fake(ConnectorClientFactory.class);
    private final ConnectorClientFactory.Connection connection = fake(ConnectorClientFactory.Connection.class);
    private final ChatContext context =
            new ChatContext().withChatId("chat").withUserId("viewer").withBackendId("backend");
    private final CharacterBackend backend =
            new CharacterBackend().withBackendId("backend").withCharacterUid("character");
    private final CharacterInfo character = new CharacterInfo()
            .withUserId("creator")
            .withCharacterUid("character")
            .withVisibility("public")
            .withExt("{\"connectors\":{\"enabled\":[\"" + ID + "\"]}}");
    private final DatabaseConnectorPolicySource policies = new DatabaseConnectorPolicySource(
            contexts, backends, characters, new SkillCharacterAccess(relationships), users, telegramChats);
    private final ConnectorRuntimeBinder binder = new ConnectorRuntimeBinder(policies, connectors, clients);

    @BeforeEach
    void setup() {
        doReturn(Optional.of(
                        new User().withUserId("viewer").withEnabled((byte) 1).withLocked((byte) 0)))
                .when(users)
                .selectByPrimaryKey("viewer");
        doReturn(Optional.of(
                        new User().withUserId("creator").withEnabled((byte) 1).withLocked((byte) 0)))
                .when(users)
                .selectByPrimaryKey("creator");
        doReturn(Optional.of(context)).when(contexts).selectByPrimaryKey("chat");
        doReturn(Optional.of(backend)).when(backends).selectByPrimaryKey("backend");
        doReturn(Optional.of(character)).when(characters).selectOne(any(SelectStatementProvider.class));
    }

    @Test
    void sharedRoleUsesCreatorAndNeverTheVisitorAsConnectorOwner() {
        var policy = policies.load(scope("viewer", "character", "backend"));
        assertEquals("creator", policy.ownerId());
        assertEquals(Set.of(ID), policy.enabled());
        assertTrue(policy.permitted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "character", "backend"})
    void forgedInvocationScopeIsDenied(String changed) {
        assertThrows(
                IllegalStateException.class,
                () -> policies.load(scope(
                        changed.equals("user") ? "attacker" : "viewer",
                        changed.equals("character") ? "other" : "character",
                        changed.equals("backend") ? "other" : "backend")));
    }

    @Test
    void privateVisibilityAndOrganizationChangesAreReadFresh() {
        character.setVisibility("private");
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        character.setVisibility("public_org");
        doReturn(List.of(new OrgRelationship()
                        .withUserId("creator")
                        .withOwnerId("viewer")
                        .withEnabled((byte) 1)
                        .withIsVirtual((byte) 0)))
                .when(relationships)
                .selectMany(any(SelectStatementProvider.class));
        assertTrue(policies.load(scope("viewer", "character", "backend")).permitted());
        doReturn(List.of()).when(relationships).selectMany(any(SelectStatementProvider.class));
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void explicitToolFreeRolesNeverGrantConnectorsEvenWithSkills(boolean skillsEnabled) {
        character.setExt("{\"connectors\":{\"enabled\":[\"" + ID + "\"]},\"tools\":{\"enabled\":[]}"
                + (skillsEnabled ? ",\"skills\":{\"enabled\":[\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"]}" : "") + "}");
        assertFalse(policies.load(scope("viewer", "character", "backend")).permitted());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void skillsDoNotDisableOwnerConnectorsForWebOrTelegram(boolean channel) {
        if (channel) telegram(12345L);
        character.setExt("{\"connectors\":{\"enabled\":[\"" + ID
                + "\"]},\"skills\":{\"enabled\":[\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"]}}");
        var policy = policies.load(scope(context.getUserId(), "character", "backend"));
        assertTrue(policy.permitted());
        assertEquals("creator", policy.ownerId());
        assertEquals(Set.of(ID), policy.enabled());
    }

    @ParameterizedTest
    @ValueSource(strings = {"viewer", "creator"})
    void disabledOrLockedAccountRevokesConnectorUse(String identity) {
        doReturn(Optional.of(new User().withUserId(identity).withEnabled((byte) 0)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        doReturn(Optional.of(
                        new User().withUserId(identity).withEnabled((byte) 1).withLocked((byte) 1)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @Test
    void noConfiguredConnectorsIsBackwardCompatible() {
        character.setExt(null);
        assertTrue(
                policies.load(scope("viewer", "character", "backend")).enabled().isEmpty());
    }

    @Test
    void connectorlessWebChatStillRechecksVisibilityAndOrganizationAccess() {
        character.setExt(null);
        character.setVisibility("private");
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        character.setVisibility("public_org");
        doReturn(List.of(new OrgRelationship()
                        .withUserId("creator")
                        .withOwnerId("viewer")
                        .withEnabled((byte) 1)
                        .withIsVirtual((byte) 0)))
                .when(relationships)
                .selectMany(any(SelectStatementProvider.class));
        assertTrue(
                policies.load(scope("viewer", "character", "backend")).enabled().isEmpty());
        doReturn(List.of()).when(relationships).selectMany(any(SelectStatementProvider.class));
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"viewer", "creator"})
    void connectorlessWebChatStillRequiresActiveAccounts(String identity) {
        character.setExt(null);
        doReturn(Optional.empty()).when(users).selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        doReturn(Optional.of(
                        new User().withUserId(identity).withEnabled((byte) 0).withLocked((byte) 0)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
        doReturn(Optional.of(
                        new User().withUserId(identity).withEnabled((byte) 1).withLocked((byte) 1)))
                .when(users)
                .selectByPrimaryKey(identity);
        assertThrows(IllegalStateException.class, () -> policies.load(scope("viewer", "character", "backend")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "private", "public_org"})
    void telegramWithoutConnectorsDoesNotRequireSystemAccount(String visibility) {
        character.setVisibility(visibility);
        character.setExt(null);
        context.withUserId("tg-12345").withTgChatId(12345L);

        assertNull(binder.binder("chat", "tg-12345", "character", "backend").bind());
        verify(users).selectByPrimaryKey("creator");
        verify(users, never()).selectByPrimaryKey(context.getUserId());
        verifyNoInteractions(relationships, telegramChats, connectors, clients);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"tools\":{\"enabled\":[]}}",
                "{\"tools\":{\"enabled\":[]},\"skills\":{\"enabled\":[\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\"]}}"
            })
    void toolFreeTelegramRolesNeverLoadConnectorCredentials(String restriction) {
        context.withUserId("tg-12345").withTgChatId(12345L);
        character
                .withVisibility("private")
                .withExt("{\"connectors\":{\"enabled\":[\"" + ID + "\"]}," + restriction.substring(1));

        assertNull(binder.binder("chat", "tg-12345", "character", "backend").bind());
        verify(users).selectByPrimaryKey("creator");
        verify(users, never()).selectByPrimaryKey(context.getUserId());
        verifyNoInteractions(relationships, telegramChats, connectors, clients);
    }

    @ParameterizedTest
    @CsvSource({"12345,public", "12345,private", "12345,public_org", "-1001234567890,public", "-1001234567890,private"})
    void enabledConnectorsUseBotOwnerForVerifiedTelegramBindings(long chatId, String visibility) {
        telegram(chatId);
        character.setVisibility(visibility);

        var policy = policies.load(scope(context.getUserId(), "character", "backend"));
        assertTrue(policy.permitted());
        assertEquals("creator", policy.ownerId());
        assertEquals(Set.of(ID), policy.enabled());
        verify(users).selectByPrimaryKey("creator");
        verify(users, never()).selectByPrimaryKey(context.getUserId());
        verify(telegramChats).findByBackendAndChat("backend", chatId);
        verifyNoInteractions(relationships);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "prefix-only",
                "wrong-id",
                "normal-user",
                "missing-binding",
                "wrong-backend",
                "wrong-chat",
                "removed-token",
                "blank-token"
            })
    void forgedOrRevokedTelegramBindingCannotOpenConnector(String invalid) {
        telegram(12345L);
        switch (invalid) {
            case "prefix-only" -> context.setTgChatId(null);
            case "wrong-id" -> context.setUserId("tg-54321");
            case "normal-user" -> context.setUserId("viewer");
            case "missing-binding" ->
                doReturn(Optional.empty()).when(telegramChats).findByBackendAndChat("backend", 12345L);
            case "wrong-backend" ->
                doReturn(Optional.of(new TgChat().withBackendId("other").withTgChatId(12345L)))
                        .when(telegramChats)
                        .findByBackendAndChat("backend", 12345L);
            case "wrong-chat" ->
                doReturn(Optional.of(new TgChat().withBackendId("backend").withTgChatId(54321L)))
                        .when(telegramChats)
                        .findByBackendAndChat("backend", 12345L);
            case "removed-token" -> backend.setTgBotToken(null);
            case "blank-token" -> backend.setTgBotToken(" ");
            default -> throw new AssertionError(invalid);
        }

        assertThrows(
                IllegalStateException.class,
                () -> binder.binder("chat", context.getUserId(), "character", "backend")
                        .bind());
        verifyNoInteractions(connectors, clients);
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "disabled", "locked", "null-enabled", "null-locked"})
    void telegramCannotUseConnectorsOfUnavailableOwner(String state) {
        telegram(12345L);
        User owner = new User().withUserId("creator").withEnabled((byte) 1).withLocked((byte) 0);
        switch (state) {
            case "disabled" -> owner.setEnabled((byte) 0);
            case "locked" -> owner.setLocked((byte) 1);
            case "null-enabled" -> owner.setEnabled(null);
            case "null-locked" -> owner.setLocked(null);
        }
        doReturn(state.equals("missing") ? Optional.empty() : Optional.of(owner))
                .when(users)
                .selectByPrimaryKey("creator");

        assertThrows(
                IllegalStateException.class,
                () -> binder.binder("chat", context.getUserId(), "character", "backend")
                        .bind());
        verifyNoInteractions(connectors, clients);
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void telegramChatCompletesWithoutConnectorsInOrdinaryAndPreparedInvocations(boolean streaming, boolean prepared)
            throws Exception {
        telegram(-1001234567890L);
        character.withVisibility("private").withExt(null);

        assertEquals("answer", run(session(prepared), streaming).aiMessage().text());
        verify(users).selectByPrimaryKey("creator");
        verify(users, never()).selectByPrimaryKey(context.getUserId());
        verifyNoInteractions(relationships, telegramChats, connectors, clients);
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void telegramChatCallsOnlyOwnerConnectorThroughAiServices(boolean streaming, boolean prepared) throws Exception {
        telegram(12345L);
        character.setVisibility("private");
        configureConnector();

        assertEquals("answer", run(session(prepared), streaming).aiMessage().text());
        verify(connectors).resolve("creator", ID);
        verify(clients).open(eq("creator"), any(), any(), any());
        verify(connection).execute("remote_lookup", "{}");
        verify(connection).close();
        verify(users, never()).selectByPrimaryKey(context.getUserId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"binding", "token", "owner", "selection"})
    void liveTelegramRevocationStopsAlreadyBoundConnector(String revoked) {
        telegram(12345L);
        configureConnector();
        try (var invocation = binder.binder("chat", context.getUserId(), "character", "backend")
                .bind()) {
            var tool = invocation.tools().entrySet().iterator().next();
            switch (revoked) {
                case "binding" ->
                    doReturn(Optional.empty()).when(telegramChats).findByBackendAndChat("backend", 12345L);
                case "token" -> backend.setTgBotToken(null);
                case "owner" ->
                    doReturn(Optional.of(new User()
                                    .withUserId("creator")
                                    .withEnabled((byte) 0)
                                    .withLocked((byte) 0)))
                            .when(users)
                            .selectByPrimaryKey("creator");
                case "selection" -> character.setExt(null);
                default -> throw new AssertionError(revoked);
            }
            assertThrows(
                    IllegalStateException.class,
                    () -> tool.getValue()
                            .execute(
                                    ToolExecutionRequest.builder()
                                            .id("call")
                                            .name(tool.getKey().name())
                                            .arguments("{}")
                                            .build(),
                                    "chat"));
        }
        verify(connection, never()).execute(anyString(), anyString());
        verify(connection).close();
    }

    private void telegram(long chatId) {
        context.withUserId("tg-" + chatId).withTgChatId(chatId);
        backend.setTgBotToken("encrypted-test-bot-token");
        doReturn(Optional.of(new TgChat().withBackendId("backend").withTgChatId(chatId)))
                .when(telegramChats)
                .findByBackendAndChat("backend", chatId);
    }

    private void configureConnector() {
        var configuration =
                new ConnectorConfiguration("Owner connector", "http", "https://mcp.example.com", 60000, Map.of());
        doReturn(List.of(new ConnectorService.Summary(
                        ID,
                        "revision",
                        configuration.name(),
                        configuration.transport(),
                        configuration.url(),
                        configuration.timeout(),
                        true,
                        Set.of(),
                        Instant.EPOCH)))
                .when(connectors)
                .list("creator");
        doReturn(new ConnectorService.Resolved(ID, "creator", "revision", configuration, true))
                .when(connectors)
                .resolve("creator", ID);
        doReturn(connection).when(clients).open(eq("creator"), eq(configuration), any(), any());
        doReturn(List.of(ToolSpecification.builder()
                        .name("remote_lookup")
                        .parameters(JsonObjectSchema.builder().build())
                        .build()))
                .when(connection)
                .listTools();
        doReturn("remote result").when(connection).execute("remote_lookup", "{}");
    }

    private ChatSession session(boolean prepared) {
        var memory = MessageWindowChatMemory.withMaxMessages(20);
        memory.add(SystemMessage.from("system"));
        memory.add(UserMessage.from("question"));
        ChatModel sync = fake(ChatModel.class);
        doAnswer(call -> response(call.getArgument(0))).when(sync).chat(any(ChatRequest.class));
        StreamingChatModel streaming = fake(StreamingChatModel.class);
        doAnswer(call -> {
                    ChatResponse response = response(call.getArgument(0));
                    StreamingChatResponseHandler handler = call.getArgument(1);
                    handler.onCompleteResponse(response);
                    return null;
                })
                .when(streaming)
                .chat(any(ChatRequest.class), any(StreamingChatResponseHandler.class));
        var session = ChatSession.builder()
                .chatMemory(memory)
                .variables(Map.of())
                .chatModel(sync)
                .streamingChatModel(streaming)
                .connectorBinder(binder.binder("chat", context.getUserId(), "character", "backend"))
                .build();
        return prepared ? session.forInvocation(memory, streaming, List.of(), 10, () -> {}) : session;
    }

    private static ChatResponse response(ChatRequest request) {
        boolean result = request.messages().stream().anyMatch(ToolExecutionResultMessage.class::isInstance);
        AiMessage message = request.toolSpecifications() != null
                        && !request.toolSpecifications().isEmpty()
                        && !result
                ? AiMessage.from(ToolExecutionRequest.builder()
                        .id("call")
                        .name(request.toolSpecifications().getFirst().name())
                        .arguments("{}")
                        .build())
                : AiMessage.from("answer");
        return ChatResponse.builder().aiMessage(message).build();
    }

    private static ChatResponse run(ChatSession session, boolean streaming) throws Exception {
        var execution = new ChatExecution(session, "chat");
        if (!streaming) return execution.send(null, () -> {}).getLeft();
        CompletableFuture<ChatResponse> completed = new CompletableFuture<>();
        execution.stream()
                .onCompleteResponse(completed::complete)
                .onError(completed::completeExceptionally)
                .start();
        return completed.get(5, TimeUnit.SECONDS);
    }

    private static ToolContext scope(String user, String character, String backend) {
        return new ToolContext("chat", user, character, backend, Duration.ofSeconds(10));
    }

    private static <T> T fake(Class<T> type) {
        return mock(
                type,
                withSettings()
                        .mockMaker(MockMakers.SUBCLASS)
                        .defaultAnswer(invocation -> invocation.getMethod().isDefault()
                                ? CALLS_REAL_METHODS.answer(invocation)
                                : RETURNS_DEFAULTS.answer(invocation)));
    }
}
