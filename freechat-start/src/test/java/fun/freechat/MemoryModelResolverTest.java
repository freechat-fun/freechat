package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import com.alibaba.dashscope.protocol.ApiServiceOption;
import com.azure.ai.openai.implementation.OpenAIClientImpl;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.PromptTask;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.AiModelFactory;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.impl.ChatSessionServiceImpl;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryModelResolver;
import fun.freechat.service.chat.memory.MemoryModelResolver.Resolved;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.prompt.impl.PromptServiceImpl;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.slf4j.LoggerFactory;

class MemoryModelResolverTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final ChatContextService contexts = fake(ChatContextService.class);
    private final CharacterService characters = fake(CharacterService.class);
    private final SysUserService users = fake(SysUserService.class);
    private final PromptTaskService tasks = fake(PromptTaskService.class);
    private final PromptService prompts = fake(PromptService.class);
    private final AiApiKeyService keys = fake(AiApiKeyService.class);
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final MemoryModelResolver resolver =
            new MemoryModelResolver(contexts, characters, users, tasks, prompts, keys, properties);
    private final AtomicInteger requests = new AtomicInteger();
    private HttpServer server;
    private String endpoint;
    private ChatContext context;
    private CharacterBackend backend;
    private CharacterInfo character;
    private PromptTask task;
    private PromptInfo prompt;
    private User owner;
    private User user;
    private RecordingKey key;

    @BeforeEach
    void setup() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        server.start();
        endpoint = "http://127.0.0.1:" + server.getAddress().getPort();
        context = new ChatContext()
                .withChatId("chat")
                .withUserId("user")
                .withBackendId("backend")
                .withAbout("session context");
        backend = new CharacterBackend()
                .withBackendId("backend")
                .withCharacterUid("character")
                .withChatPromptTaskId("task")
                .withMessageWindowSize(10)
                .withLongTermMemoryWindowSize(50);
        character = new CharacterInfo()
                .withCharacterUid("character")
                .withName("character name")
                .withNickname("configured nickname")
                .withDescription("description")
                .withProfile("character profile")
                .withGender("fictional gender")
                .withChatStyle("style")
                .withChatExample("example")
                .withGreeting("greeting")
                .withDefaultScene("scene")
                .withLang("en");
        task = new PromptTask()
                .withTaskId("task")
                .withPromptUid("prompt")
                .withModelId("[open_ai]chat-model|text2chat")
                .withApiKeyValue("task-key")
                .withParams(
                        JSON.writeValueAsString(Map.of("baseUrl", endpoint, "temperature", 0.73, "maxTokens", 9000)))
                .withVariables("{\"nested\":{\"a\":1,\"b\":2}}");
        prompt = new PromptInfo()
                .withPromptUid("prompt")
                .withFormat("mustache")
                .withTemplate("{\"system\":\"system {{USER_PROFILE}}\",\"messages\":\"[]\"}")
                .withInputs("{\"topic\":\"default\"}");
        owner = new User().withUserId("owner");
        user = new User().withUserId("user").withNickname("user name").withProfile("default user profile");
        key = new RecordingKey("test-token");
        when(contexts.get("chat")).thenReturn(context);
        when(characters.getBackend("backend")).thenReturn(backend);
        when(characters.getOwnerByUid("character")).thenReturn("owner");
        when(users.loadByUserId("owner")).thenReturn(owner);
        when(characters.getLatestIdByUid("character", owner)).thenReturn(1L);
        when(characters.details(1L, owner)).thenReturn(Triple.of(character, List.of(), List.of()));
        when(tasks.get("task")).thenReturn(task);
        when(prompts.getLatestIdByUid("prompt", owner)).thenReturn(2L);
        when(prompts.details(2L, owner)).thenReturn(Triple.of(prompt, List.of(), List.of()));
        PromptService renderer = new PromptServiceImpl();
        when(prompts.apply(any(ChatMessage.class), anyMap(), any(PromptFormat.class)))
                .thenAnswer(call ->
                        renderer.apply((ChatMessage) call.getArgument(0), call.getArgument(1), call.getArgument(2)));
        when(users.loadByUserId("user")).thenReturn(user);
        when(keys.use(nullable(String.class))).thenReturn(key);
        when(keys.use(anyString(), anyString())).thenReturn(key);
        properties.setExtractionTimeout(Duration.ofSeconds(37));
        properties.setResponseReserveTokens(321);
    }

    @AfterEach
    void noProviderRequests() {
        server.stop(0);
        assertEquals(0, requests.get(), "Resolution must never call a provider");
    }

    @ParameterizedTest
    @CsvSource({
        "context-name,context-raw,task-name,task-raw,user,context-name",
        "context-name,,task-name,task-raw,user,context-name",
        ",context-raw,task-name,task-raw,,context-raw",
        "' ',context-raw,task-name,task-raw,,context-raw",
        ",,task-name,task-raw,owner,task-name",
        ",,,task-raw,,task-raw"
    })
    void credentialPrecedence(
            String contextName,
            String contextRaw,
            String taskName,
            String taskRaw,
            String selectedOwner,
            String selectedKey)
            throws Exception {
        context.setApiKeyName(contextName);
        context.setApiKeyValue(contextRaw);
        task.setApiKeyName(taskName);
        task.setApiKeyValue(taskRaw);
        Resolved resolved = resolver.resolve("chat");
        assertInstanceOf(OpenAiChatModel.class, resolved.model());
        if (selectedOwner != null) {
            verify(keys).use(selectedOwner, selectedKey);
        } else {
            verify(keys).use(selectedKey);
        }
        verifyNoMoreInteractions(keys);
        assertEquals(1, key.tokenReads);
        assertEquals(1, key.closes);
        Map<?, ?> headers = (Map<?, ?>) field(field(resolved.model(), "client"), "defaultHeaders");
        assertTrue("Bearer test-token".equals(headers.get("Authorization")));
    }

    @Test
    void onlyConfigurationReadsAndCredentialLifecycle() {
        resolver.resolve("chat");
        verify(contexts).get("chat");
        verify(characters).getBackend("backend");
        verify(characters).getOwnerByUid("character");
        verify(characters).getLatestIdByUid("character", owner);
        verify(characters).details(1L, owner);
        verify(users).loadByUserId("owner");
        verify(users).loadByUserId("user");
        verify(tasks).get("task");
        verify(prompts).getLatestIdByUid("prompt", owner);
        verify(prompts).details(2L, owner);
        verify(keys).use("task-key");
        verifyNoMoreInteractions(contexts, characters, users, tasks, prompts, keys);
        // There is deliberately no session, history, embedding/RAG, queue, or streaming dependency.
        for (Field dependency : MemoryModelResolver.class.getDeclaredFields()) {
            assertFalse(dependency
                    .getType()
                    .getSimpleName()
                    .matches(".*(Session|History|MemoryStore|Streaming|Embedding).*"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"open_ai", "azure_open_ai", "dash_scope", "ollama"})
    void resolvesStandaloneProviderWithBoundsAndPreservedSampling(String provider) throws Exception {
        task.setModelId("[" + provider + "]chat-model|text2chat");
        task.setParams(JSON.writeValueAsString(Map.of(
                "baseUrl",
                endpoint,
                "temperature",
                0.73,
                "maxTokens",
                9000,
                "numPredict",
                -1,
                "numCtx",
                16000,
                "enableSearch",
                true,
                "enableThinking",
                true,
                "think",
                true,
                "topP",
                0.91)));
        Resolved resolved = resolver.resolve("chat");
        ChatModel model = resolved.model();
        ChatRequestParameters parameters = model.defaultRequestParameters();
        assertEquals("chat-model", parameters.modelName());
        assertEquals(0.73, parameters.temperature());
        assertEquals(0.91, parameters.topP());
        assertTrue(parameters.toolSpecifications() == null
                || parameters.toolSpecifications().isEmpty());
        assertTrue(model.listeners().isEmpty());
        assertEquals(10, resolved.messageWindowSize());
        assertEquals(30, resolved.recallLimit());
        assertEquals("character", resolved.characterUid());
        assertEquals("user", resolved.userId());
        assertEquals("en", resolved.language());
        assertEquals(task.getModelId(), resolved.modelId());
        switch (provider) {
            case "open_ai" -> {
                OpenAiChatModel openai = assertInstanceOf(OpenAiChatModel.class, model);
                assertEquals(321, openai.defaultRequestParameters().maxCompletionTokens());
                assertJdkClient(model, Duration.ofSeconds(37));
            }
            case "azure_open_ai" -> {
                assertInstanceOf(AzureOpenAiChatModel.class, model);
                assertEquals(321, parameters.maxOutputTokens());
                OpenAIClientImpl client = (OpenAIClientImpl) field(field(model, "client"), "serviceClient");
                assertEquals(endpoint, client.getEndpoint());
                var pipeline = client.getHttpPipeline();
                var netty = (reactor.netty.http.client.HttpClient) field(pipeline.getHttpClient(), "nettyClient");
                // Azure uses channel handlers for response/read deadlines; the directly inspectable
                // connect timeout demonstrates propagation of the factory's Duration.
                assertEquals(
                        Integer.valueOf(37000),
                        netty.configuration().options().get(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS));
                boolean loggingPolicyFound = false;
                for (int i = 0; i < pipeline.getPolicyCount(); i++) {
                    if (pipeline.getPolicy(i) instanceof HttpLoggingPolicy policy) {
                        loggingPolicyFound = true;
                        assertEquals(HttpLogDetailLevel.NONE, field(policy, "httpLogDetailLevel"));
                    }
                }
                assertTrue(loggingPolicyFound);
            }
            case "dash_scope" -> {
                QwenChatModel qwen = assertInstanceOf(QwenChatModel.class, model);
                assertEquals(321, parameters.maxOutputTokens());
                assertFalse(qwen.defaultRequestParameters().enableSearch());
                assertFalse(qwen.defaultRequestParameters().enableThinking());
                assertEquals(Duration.ofSeconds(37), field(qwen, "timeout"));
                ApiServiceOption option = (ApiServiceOption) field(field(qwen, "generation"), "serviceOption");
                assertEquals(endpoint, option.getBaseHttpUrl());
            }
            case "ollama" -> {
                OllamaChatModel ollama = assertInstanceOf(OllamaChatModel.class, model);
                assertFalse(ollama.defaultRequestParameters().think());
                assertEquals(321, parameters.maxOutputTokens());
                assertEquals(16000, resolved.providerContextLimit());
                assertEquals(0, key.tokenReads);
                assertJdkClient(model, Duration.ofSeconds(37));
            }
            default -> fail();
        }
        if (!provider.equals("ollama")) {
            assertNull(resolved.providerContextLimit(), "Output caps are not provider context limits");
        }
        assertEquals(1, key.closes);
    }

    @Test
    void originalFactorySignaturesStillWorkAndUseDefaultTimeout() throws Exception {
        Map<String, Object> params = Map.of("baseUrl", endpoint, "temperature", 0.73, "maxTokens", 999);
        OpenAiChatModel openai = AiModelFactory.createOpenAiChatModel("test-token", "chat-model", params);
        assertJdkClient(openai, Duration.ofSeconds(60));
        assertEquals(999, openai.defaultRequestParameters().maxCompletionTokens());
        assertJdkClient(AiModelFactory.createOllamaChatModel("chat-model", params), Duration.ofSeconds(60));
        assertEquals(
                Duration.ofSeconds(60),
                field(AiModelFactory.createQwenChatModel("test-token", "chat-model", params), "timeout"));
        assertEquals(
                999,
                AiModelFactory.createAzureOpenAiChatModel("test-token", "deployment", params)
                        .defaultRequestParameters()
                        .maxOutputTokens());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "chat-assist"})
    void invalidAndTemporaryIdsFailBeforeAnyRead(String id) {
        unavailable(() -> resolver.resolve(id));
        verifyNoInteractions(contexts, characters, users, tasks, prompts, keys);
    }

    @Test
    void missingContextDoesNotLoadBackendOrClaimKey() {
        unavailable(() -> resolver.resolve("missing"));
        verify(contexts).get("missing");
        verifyNoInteractions(characters, users, tasks, prompts, keys);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void disabledMemoryStopsBeforeDefaultsOrCredentials(Integer window) {
        backend.setLongTermMemoryWindowSize(window);
        unavailable(() -> resolver.resolve("chat"));
        verify(characters).getBackend("backend");
        verifyNoMoreInteractions(characters);
        verifyNoInteractions(users, tasks, prompts, keys);
    }

    @Test
    void missingBackendFailsWithoutCredentials() {
        when(characters.getBackend("backend")).thenReturn(null);
        unavailable(() -> resolver.resolve("chat"));
        verifyNoInteractions(users, tasks, prompts, keys);
    }

    @Test
    void baselinesFallbackAndNeverMutateSourceRecords() throws Exception {
        context.setUserProfile(" ");
        String before = snapshot();
        Resolved resolved = resolver.resolve("chat");
        assertTrue(user.getProfile().equals(resolved.userBaseline()));
        Map<?, ?> defaults = JSON.readValue(resolved.characterBaseline(), Map.class);
        assertTrue(character.getName().equals(defaults.get("nickname")));
        assertTrue(character.getNickname().equals(defaults.get("configuredNickname")));
        assertTrue(character.getDescription().equals(defaults.get("description")));
        assertTrue(character.getProfile().equals(defaults.get("profile")));
        assertTrue(character.getGender().equals(defaults.get("gender")));
        assertTrue(character.getChatStyle().equals(defaults.get("chatStyle")));
        assertTrue(character.getGreeting().equals(defaults.get("greeting")));
        assertTrue(context.getAbout().equals(defaults.get("context")));
        assertTrue(before.equals(snapshot()), "Resolution must not mutate configuration");
        assertEquals("Resolved[redacted]", resolved.toString());
        context.setUserProfile("session override");
        context.setCharacterNickname("session nickname");
        Resolved overridden = resolver.resolve("chat");
        assertTrue(context.getUserProfile().equals(overridden.userBaseline()));
        assertTrue(context.getCharacterNickname()
                .equals(JSON.readTree(overridden.characterBaseline())
                        .get("nickname")
                        .asText()));
        assertNotEquals(resolved.fingerprint(), overridden.fingerprint());
    }

    @Test
    void fingerprintIsCanonicalAndExcludesCredentialsAndBookkeeping() {
        String first = resolver.resolve("chat").fingerprint();
        assertTrue(first.matches("[a-f0-9]{64}"));
        task.setVariables("{ \"nested\": { \"b\": 2, \"a\": 1 } }");
        task.setApiKeyValue("rotated-task-key");
        context.setApiKeyName("new-key-name");
        context.setApiKeyValue("rotated-context-key");
        context.setQuota(1L);
        assertEquals(first, resolver.resolve("chat").fingerprint());
        character.setProfile("changed explicit profile");
        String changedProfile = resolver.resolve("chat").fingerprint();
        assertNotEquals(first, changedProfile);
        prompt.setTemplate("{\"system\":\"new system\"}");
        String changedTemplate = resolver.resolve("chat").fingerprint();
        assertNotEquals(changedProfile, changedTemplate);
        prompt.setInputs("{\"topic\":\"new default\"}");
        String changedInput = resolver.resolve("chat").fingerprint();
        assertNotEquals(changedTemplate, changedInput);
        task.setVariables("{\"nested\":{\"a\":3,\"b\":2}}");
        assertNotEquals(changedInput, resolver.resolve("chat").fingerprint());
    }

    @Test
    void baselineAndExtractionConfigurationChangesInvalidateFingerprint() {
        List<Runnable> changes = List.of(
                () -> context.setAbout("changed context"),
                () -> context.setUserNickname("changed user nickname"),
                () -> character.setLang("zh"),
                () -> character.setGreeting("changed greeting"),
                () -> character.setChatStyle("changed style"),
                () -> backend.setMessageWindowSize(11),
                () -> backend.setLongTermMemoryWindowSize(5),
                () -> task.setParams(task.getParams().replace("0.73", "0.62")),
                () -> properties.setResponseReserveTokens(400),
                () -> properties.setExtractionTimeout(Duration.ofSeconds(40)));
        String previous = resolver.resolve("chat").fingerprint();
        for (Runnable change : changes) {
            change.run();
            String current = resolver.resolve("chat").fingerprint();
            assertNotEquals(previous, current);
            previous = current;
        }
        assertEquals(5, resolver.resolve("chat").recallLimit());
    }

    @Test
    void currentDraftAndOnlyActiveTemplateAreFingerprinted() {
        task.setDraft((byte) 1);
        prompt.setDraft("{\"type\":\"chat\",\"chatTemplate\":{\"system\":\"draft one\"}}");
        String first = resolver.resolve("chat").fingerprint();
        prompt.setTemplate("{\"system\":\"inactive template\"}");
        assertEquals(first, resolver.resolve("chat").fingerprint());
        prompt.setDraft("{\"type\":\"chat\",\"chatTemplate\":{\"system\":\"draft two\"}}");
        assertNotEquals(first, resolver.resolve("chat").fingerprint());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "not-json-private-payload",
                "[]",
                "null",
                "{} {}",
                "{\"temperature\":1,\"temperature\":2}",
                "{\"temperature\":\"private-payload\"}",
                "{\"seed\":0.5}",
                "{\"stop\":[1]}",
                "{\"baseUrl\":\"private-payload\"}",
                "{\"numCtx\":2147483648}"
            })
    void malformedParametersFailSanitizedBeforeCredentialClaim(String params) {
        task.setParams(params);
        unavailable(() -> resolver.resolve("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    void missingCredentialsAndPayloadBearingFailuresAreSanitizedAndClosed() {
        key = new RecordingKey(" ");
        when(keys.use("task-key")).thenReturn(key);
        unavailable(() -> resolver.resolve("chat"));
        assertEquals(1, key.closes);
        when(keys.use("task-key")).thenThrow(new IllegalStateException("private credential payload"));
        unavailable(() -> resolver.resolve("chat"));
    }

    @Test
    void noCredentialDoesNotFallBackToOwnerWhenContextOverrideIsInvalid() {
        context.setApiKeyName("revoked-context-name");
        when(keys.use("user", "revoked-context-name")).thenReturn(new RecordingKey(""));
        unavailable(() -> resolver.resolve("chat"));
        verify(keys).use("user", "revoked-context-name");
        verifyNoMoreInteractions(keys);
    }

    @Test
    void missingRawCredentialFailsButOllamaDoesNotRequireAToken() throws Exception {
        task.setApiKeyValue(null);
        key = new RecordingKey(null);
        when(keys.use((String) null)).thenReturn(key);
        unavailable(() -> resolver.resolve("chat"));
        assertEquals(1, key.closes);
        task.setModelId("[ollama]chat-model");
        task.setParams(JSON.writeValueAsString(Map.of("baseUrl", endpoint)));
        Resolved resolved = resolver.resolve("chat");
        assertInstanceOf(OllamaChatModel.class, resolved.model());
        assertNull(resolved.providerContextLimit());
        assertEquals(1, key.tokenReads);
        assertEquals(2, key.closes);
    }

    @ParameterizedTest
    @ValueSource(strings = {"text2image", "embedding", "moderation"})
    void nonChatModelsAreRejectedBeforeCredentials(String type) {
        task.setModelId("[dash_scope]some-model|" + type);
        unavailable(() -> resolver.resolve("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    void closeFailureIsSanitized() {
        key.failClose = true;
        unavailable(() -> resolver.resolve("chat"));
        assertEquals(1, key.closes);
    }

    @Test
    void invalidPropertiesWindowAndKnownContextBudgetFailBeforeClaim() throws Exception {
        properties.setExtractionTimeout(Duration.ZERO);
        unavailable(() -> resolver.resolve("chat"));
        properties.setExtractionTimeout(Duration.ofSeconds(37));
        backend.setMessageWindowSize(0);
        unavailable(() -> resolver.resolve("chat"));
        backend.setMessageWindowSize(10);
        task.setModelId("[ollama]chat-model");
        task.setParams(JSON.writeValueAsString(Map.of("baseUrl", endpoint, "numCtx", 8192)));
        unavailable(() -> resolver.resolve("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    void invalidDraftAndUnknownProviderFailBeforeClaim() {
        task.setDraft((byte) 1);
        prompt.setDraft("private malformed draft");
        unavailable(() -> resolver.resolve("chat"));
        task.setDraft((byte) 0);
        task.setModelId("[unknown]private-model");
        unavailable(() -> resolver.resolve("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    @Timeout(10)
    void qwenDeadlineInterruptsLocalWorkWithoutMakingProviderRequest() throws Exception {
        QwenChatModel model = AiModelFactory.createQwenChatModel(
                "test-token", "qwen-plus", Map.of("baseUrl", endpoint), Duration.ofMillis(200));
        CountDownLatch interrupted = new CountDownLatch(1);
        CountDownLatch block = new CountDownLatch(1);
        model.setGenerationParamCustomizer(ignored -> {
            try {
                block.await();
            } catch (InterruptedException expected) {
                interrupted.countDown();
                Thread.currentThread().interrupt();
                throw new IllegalStateException("local work cancelled");
            }
        });
        assertTimeout(Duration.ofSeconds(3), () -> {
            IllegalStateException error = assertThrows(IllegalStateException.class, () -> model.chat("test"));
            assertEquals("Chat model request timed out", error.getMessage());
            assertNull(error.getCause());
        });
        assertTrue(interrupted.await(2, TimeUnit.SECONDS));
    }

    @Test
    void configurationOnlyReadsAndRendersMessagesWithoutSideEffects() throws Exception {
        List<ChatMessage> originals = List.of(UserMessage.from("{{topic}}"), AiMessage.from("{{USER_NICKNAME}}"));
        prompt.setTemplate(exampleTemplate(originals));
        String before = snapshot();
        var configuration = resolver.configuration("chat").orElseThrow();
        assertEquals("user", configuration.userId());
        assertEquals("character", configuration.characterUid());
        assertEquals("en", configuration.language());
        assertTrue(configuration.fingerprint().matches("[a-f0-9]{64}"));
        assertEquals(List.of(UserMessage.from("default"), AiMessage.from("user name")), configuration.examples());
        assertEquals("Configuration[redacted]", configuration.toString());
        assertEquals(before, snapshot());
        assertEquals(0, key.tokenReads);
        assertEquals(0, key.closes);
        verifyNoInteractions(keys);
        verify(contexts).get("chat");
        verify(characters).getBackend("backend");
        verify(characters).getOwnerByUid("character");
        verify(characters).getLatestIdByUid("character", owner);
        verify(characters).details(1L, owner);
        verify(users).loadByUserId("owner");
        verify(users).loadByUserId("user");
        verify(tasks).get("task");
        verify(prompts).getLatestIdByUid("prompt", owner);
        verify(prompts).details(2L, owner);
        for (ChatMessage original : originals) {
            verify(prompts).apply(eq(original), anyMap(), eq(PromptFormat.MUSTACHE));
        }
        // This exhausts the complete dependency boundary: no session/history/file access, writes,
        // credential acquisition, or whole-content apply (which synthesizes messageToSend).
        verifyNoMoreInteractions(contexts, characters, users, tasks, prompts, keys);
        for (Field dependency : MemoryModelResolver.class.getDeclaredFields()) {
            assertFalse(dependency
                    .getType()
                    .getSimpleName()
                    .matches(".*(Session|History|MemoryStore|Streaming|Embedding|File|Path|ChatModel).*"));
        }
        for (var component : MemoryModelResolver.Configuration.class.getRecordComponents()) {
            assertTrue(component.getType() == String.class || component.getType() == List.class);
        }
    }

    @Test
    void configurationExamplesAreDefensivelyCopiedAndUnmodifiable() {
        var mutable = new java.util.ArrayList<ChatMessage>(List.of(UserMessage.from("private example")));
        var configuration = new MemoryModelResolver.Configuration("user", "character", "en", "hash", mutable);
        mutable.clear();
        assertEquals(List.of(UserMessage.from("private example")), configuration.examples());
        assertThrows(
                UnsupportedOperationException.class,
                () -> configuration.examples().clear());
        assertThrows(
                NullPointerException.class,
                () -> new MemoryModelResolver.Configuration("user", "character", "en", "hash", null));
    }

    @Test
    void inputTaskAndAuthoritativeVariablePrecedenceMatchesNormalChat() throws Exception {
        prompt.setInputs("""
                {"topic":"input", "inputOnly":"kept", "blank":" ", "nil":null, "number":7,
                 "CHARACTER_LANG":"input-language", "USER_PROFILE":"input-profile"}
                """);
        task.setVariables("""
                {"topic":"task", "taskBlank":" ", "taskNull":null,
                 "CHARACTER_LANG":"task-language", "CHARACTER_NICKNAME":"task-character",
                 "CHARACTER_DESCRIPTION":"task-description", "CHARACTER_GENDER":"task-gender",
                 "CHARACTER_CHAT_STYLE":"task-style", "CHARACTER_CHAT_EXAMPLE":"task-example",
                 "CHARACTER_GREETING":"task-greeting", "CHARACTER_PROFILE":"task-profile",
                 "USER_PROFILE":"task-user-profile", "USER_NICKNAME":"task-user", "CHAT_CONTEXT":"task-context"}
                """);
        context.setCharacterNickname("context character");
        context.setUserNickname("context user");
        context.setUserProfile("context profile");
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("""
                {{topic}}|{{inputOnly}}|{{number}}|{{CHARACTER_LANG}}|{{CHARACTER_NICKNAME}}|{{CHARACTER_DESCRIPTION}}|{{CHARACTER_GENDER}}|{{CHARACTER_CHAT_STYLE}}|{{CHARACTER_CHAT_EXAMPLE}}|{{CHARACTER_GREETING}}|{{CHARACTER_PROFILE}}|{{USER_PROFILE}}|{{USER_NICKNAME}}|{{CHAT_CONTEXT}}"""))));
        assertEquals(
                List.of(
                        UserMessage.from(
                                "task|kept|7|English|context character|description|fictional gender|style|example|greeting|character profile|context profile|context user|session context")),
                resolver.configuration("chat").orElseThrow().examples());
        var variables = renderedVariables();
        assertFalse(variables.containsKey("blank"));
        assertFalse(variables.containsKey("nil"));
        assertEquals(" ", variables.get("taskBlank"));
        assertTrue(variables.containsKey("taskNull"));
        assertNull(variables.get("taskNull"));
        verifyNoInteractions(keys);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void blankOrMissingProfilesAndOptionalCharacterFieldsOverrideTaskValues(String blank) throws Exception {
        context.setUserProfile(blank);
        user.setProfile(blank);
        character.setProfile(blank);
        character.setChatStyle(blank);
        character.setChatExample(blank);
        character.setGreeting(blank);
        character.setDescription(null);
        character.setGender(null);
        context.setAbout(blank);
        task.setVariables("""
                {"USER_PROFILE":"must-not-survive", "CHARACTER_PROFILE":"must-not-survive",
                 "CHARACTER_DESCRIPTION":"must-not-survive", "CHARACTER_GENDER":"must-not-survive"}
                """);
        prompt.setTemplate(
                exampleTemplate(
                        List.of(
                                UserMessage.from(
                                        "[{{USER_PROFILE}}|{{CHARACTER_PROFILE}}|{{CHARACTER_CHAT_STYLE}}|{{CHARACTER_CHAT_EXAMPLE}}|{{CHARACTER_GREETING}}|{{CHAT_CONTEXT}}]"))));
        assertEquals(
                List.of(UserMessage.from("[|||||]")),
                resolver.configuration("chat").orElseThrow().examples());
        var variables = renderedVariables();
        assertTrue(variables.containsKey("CHARACTER_DESCRIPTION"));
        assertNull(variables.get("CHARACTER_DESCRIPTION"));
        assertTrue(variables.containsKey("CHARACTER_GENDER"));
        assertNull(variables.get("CHARACTER_GENDER"));
    }

    @ParameterizedTest
    @CsvSource({
        "session,nickname,preferred,username,session",
        "' ',nickname,preferred,username,nickname",
        ",,preferred,username,preferred",
        ",' ',' ',username,username"
    })
    void nicknameFallbackAndDefaultProfileMatchNormalChat(
            String session, String nickname, String preferred, String username, String expected) throws Exception {
        context.setUserNickname(session);
        context.setCharacterNickname(" ");
        context.setUserProfile(" ");
        user.setNickname(nickname);
        user.setPreferredUsername(preferred);
        user.setUsername(username);
        prompt.setTemplate(exampleTemplate(
                List.of(UserMessage.from("{{USER_NICKNAME}}|{{CHARACTER_NICKNAME}}|{{USER_PROFILE}}"))));
        var configuration = resolver.configuration("chat").orElseThrow();
        assertEquals(
                List.of(UserMessage.from(expected + "|character name|default user profile")), configuration.examples());
        ChatSession ordinary = ordinarySessions(fake(ChatMemoryService.class)).get(context);
        assertNotNull(ordinary);
        assertEquals(expected, ordinary.getVariables().get("USER_NICKNAME"));
        assertEquals("default user profile", ordinary.getVariables().get("USER_PROFILE"));
        assertEquals(configuration.fingerprint(), ordinary.getMemoryFingerprint());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mustache", "f_string"})
    void serializedMessageListAndPromptFormatRenderEveryExampleOnly(String format) throws Exception {
        String variable = format.equals("mustache") ? "{{topic}}" : "{topic}";
        List<ChatMessage> originals = List.of(
                SystemMessage.from("system " + variable),
                UserMessage.from("named-user", "user " + variable),
                AiMessage.from("assistant " + variable));
        prompt.setFormat(format);
        prompt.setTemplate(exampleTemplate(originals));
        assertTrue(JSON.readTree(prompt.getTemplate()).get("messages").isTextual());
        assertEquals(
                List.of(
                        SystemMessage.from("system default"),
                        UserMessage.from("named-user", "user default"),
                        AiMessage.from("assistant default")),
                resolver.configuration("chat").orElseThrow().examples());
        verify(prompts, times(3)).apply(any(ChatMessage.class), anyMap(), eq(PromptFormat.of(format)));
        verify(prompts, never()).apply(any(ChatPromptContent.class), anyMap(), any());
        verify(prompts, never()).apply(anyString(), anyMap(), any());
    }

    @Test
    void draftSelectionControlsBothExamplesAndFingerprint() throws Exception {
        String active = exampleTemplate(List.of(UserMessage.from("active {{topic}}")));
        String draft = exampleTemplate(List.of(AiMessage.from("draft {{topic}}")));
        prompt.setTemplate(active);
        prompt.setDraft(JSON.writeValueAsString(Map.of("type", "chat", "chatTemplate", JSON.readTree(draft))));
        var activeConfiguration = resolver.configuration("chat").orElseThrow();
        assertEquals(List.of(UserMessage.from("active default")), activeConfiguration.examples());
        task.setDraft((byte) 1);
        var draftConfiguration = resolver.configuration("chat").orElseThrow();
        assertEquals(List.of(AiMessage.from("draft default")), draftConfiguration.examples());
        assertNotEquals(activeConfiguration.fingerprint(), draftConfiguration.fingerprint());
        prompt.setTemplate("private invalid inactive template");
        assertEquals(draftConfiguration, resolver.configuration("chat").orElseThrow());
        prompt.setDraft(" ");
        prompt.setTemplate(active);
        assertEquals(activeConfiguration, resolver.configuration("chat").orElseThrow());
    }

    @Test
    void combinedResolutionLoadsOnceAndBindsTheSameConfiguration() throws Exception {
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("{{USER_PROFILE}}"))));
        var resolution = resolver.resolveWithConfiguration("chat").orElseThrow();
        var configuration = resolution.configuration();
        assertEquals(configuration.fingerprint(), resolution.resolved().fingerprint());
        assertEquals(configuration.userId(), resolution.resolved().userId());
        assertEquals(configuration.characterUid(), resolution.resolved().characterUid());
        assertEquals(configuration.language(), resolution.resolved().language());
        assertEquals(List.of(UserMessage.from("default user profile")), configuration.examples());
        assertEquals("Resolution[redacted]", resolution.toString());
        verify(contexts).get("chat");
        verify(tasks).get("task");
        verify(prompts).details(2L, owner);
        assertEquals(configuration, resolver.configuration("chat").orElseThrow());
        assertEquals(1, key.tokenReads);
        assertEquals(1, key.closes);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void configurationIsEmptyForDisabledMemory(Integer window) {
        backend.setLongTermMemoryWindowSize(window);
        assertTrue(resolver.configuration("chat").isEmpty());
        verifyNoInteractions(users, tasks, prompts, keys);
    }

    @ParameterizedTest
    @ValueSource(strings = {"assist", "context", "backend", "backendId"})
    void configurationIsEmptyOnlyForAbsentContextBackendOrAssist(String missing) {
        String id = "chat";
        switch (missing) {
            case "assist" -> id = "chat-assist";
            case "context" -> when(contexts.get("chat")).thenReturn(null);
            case "backend" -> when(characters.getBackend("backend")).thenReturn(null);
            case "backendId" -> context.setBackendId(null);
            default -> fail();
        }
        assertTrue(resolver.configuration(id).isEmpty());
        verifyNoInteractions(users, tasks, prompts, keys);
    }

    @ParameterizedTest
    @ValueSource(longs = {123456789L, -1001234567890L})
    void telegramSessionsWithoutSystemUserShareBackgroundConfiguration(long tgChatId) throws Exception {
        String scope = "tg-" + tgChatId;
        context.setTgChatId(tgChatId);
        context.setTgUserId(987654321L);
        context.setChatType(tgChatId > 0 ? "private" : "group");
        context.setUserId(scope);
        context.setUserNickname("fake Telegram nickname");
        context.setUserProfile("fake Telegram profile");
        owner.setNickname("fake owner nickname");
        owner.setProfile("fake owner profile");
        task.setVariables("{\"USER_NICKNAME\":\"fake task nickname\",\"USER_PROFILE\":\"fake task profile\"}");
        prompt.setTemplate(exampleTemplate(List.of(
                UserMessage.from("{{USER_NICKNAME}}|{{USER_PROFILE}}|{{CHAT_CONTEXT}}"), AiMessage.from("{{topic}}"))));
        String before = snapshot();

        var configuration = resolver.configuration("chat").orElseThrow();
        assertEquals(scope, configuration.userId());
        assertEquals("character", configuration.characterUid());
        assertEquals("en", configuration.language());
        assertEquals(
                List.of(
                        UserMessage.from("fake Telegram nickname|fake Telegram profile|session context"),
                        AiMessage.from("default")),
                configuration.examples());
        assertEquals(
                configuration.fingerprint(),
                resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, null));
        verifyNoInteractions(keys);

        ChatSession session = ordinarySessions(fake(ChatMemoryService.class)).get(context);
        assertNotNull(session);
        assertEquals(configuration.fingerprint(), session.getMemoryFingerprint());
        assertEquals("fake Telegram nickname", session.getVariables().get("USER_NICKNAME"));
        assertEquals("fake Telegram profile", session.getVariables().get("USER_PROFILE"));
        assertEquals("session context", session.getVariables().get("CHAT_CONTEXT"));
        assertInstanceOf(OpenAiChatModel.class, session.getChatModel());
        assertNotNull(session.getStreamingChatModel());
        assertEquals(1, key.closes, "Ordinary sessions must not construct extraction models");

        Resolved background = resolver.resolve("chat");
        assertEquals(scope, background.userId());
        assertEquals("character", background.characterUid());
        assertEquals("fake Telegram profile", background.userBaseline());
        assertEquals(session.getMemoryFingerprint(), background.fingerprint());
        var bound = resolver.resolveForSession("chat", session.getMemoryFingerprint())
                .orElseThrow();
        assertEquals(configuration, bound.configuration());
        assertEquals(scope, bound.resolved().userId());
        assertEquals(background.fingerprint(), bound.resolved().fingerprint());
        assertEquals(3, key.closes);
        verify(users, times(4)).loadByUserId(scope);
        assertEquals(before, snapshot(), "Telegram scope and context must not be replaced by owner or sender data");
    }

    @ParameterizedTest
    @ValueSource(strings = {"nickname", "profile", "about", "scope"})
    void telegramContextChangesInvalidateSessionFingerprint(String change) throws Exception {
        context.setTgChatId(-1001234567890L);
        context.setUserId("tg--1001234567890");
        context.setUserNickname("fake Telegram nickname");
        context.setUserProfile("fake Telegram profile");
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("{{USER_NICKNAME}}|{{USER_PROFILE}}"))));
        ChatSessionServiceImpl sessions = ordinarySessions(fake(ChatMemoryService.class));
        ChatSession session = sessions.get(context);
        assertNotNull(session);
        String original = session.getMemoryFingerprint();
        assertNotNull(original);
        switch (change) {
            case "nickname" -> context.setUserNickname("changed fake nickname");
            case "profile" -> context.setUserProfile("changed fake profile");
            case "about" -> context.setAbout("changed fake context");
            case "scope" -> {
                context.setTgChatId(-1009876543210L);
                context.setUserId("tg--1009876543210");
            }
            default -> fail();
        }
        clearInvocations(keys);
        var changed = resolver.configuration("chat").orElseThrow();
        assertNotEquals(original, changed.fingerprint());
        assertEquals(context.getUserId(), changed.userId());
        unavailable(() -> resolver.resolveForSession("chat", original));
        verifyNoInteractions(keys);
        assertEquals(original, session.getMemoryFingerprint());
        assertEquals("fake Telegram nickname", session.getVariables().get("USER_NICKNAME"));
        assertEquals("fake Telegram profile", session.getVariables().get("USER_PROFILE"));

        ChatSession refreshed = sessions.get(context);
        assertNotNull(refreshed);
        assertEquals(changed.fingerprint(), refreshed.getMemoryFingerprint());
        assertEquals(
                changed,
                resolver.resolveForSession("chat", refreshed.getMemoryFingerprint())
                        .orElseThrow()
                        .configuration());
    }

    @ParameterizedTest
    @CsvSource({
        "50,,",
        "50,'',''",
        "50,' ',' '",
        "50,,fake context profile",
        "50,fake context nickname,",
        "0,,",
        "0,'',''",
        "0,' ',' '",
        "0,,fake context profile",
        "0,fake context nickname,"
    })
    void telegramMissingUserUsesSafeDefaultsWithMemoryEnabledAndDisabled(int window, String nickname, String profile)
            throws Exception {
        context.setTgChatId(-1001234567890L);
        context.setUserId("tg--1001234567890");
        context.setUserNickname(nickname);
        context.setUserProfile(profile);
        owner.setNickname("fake owner nickname must not be used");
        owner.setProfile("fake owner profile must not be used");
        backend.setLongTermMemoryWindowSize(window);
        task.setVariables("{\"USER_NICKNAME\":\"fake task nickname\",\"USER_PROFILE\":\"fake task profile\"}");
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("[{{USER_NICKNAME}}|{{USER_PROFILE}}]"))));
        String expectedNickname = nickname == null || nickname.isBlank() ? null : nickname;
        String expectedProfile = profile == null || profile.isBlank() ? "" : profile;
        String fingerprint = null;
        if (window > 0) {
            var configuration = resolver.configuration("chat").orElseThrow();
            fingerprint = configuration.fingerprint();
            assertEquals("tg--1001234567890", configuration.userId());
            assertEquals(
                    List.of(UserMessage.from(
                            "[" + (expectedNickname == null ? "" : expectedNickname) + "|" + expectedProfile + "]")),
                    configuration.examples());
            Map<String, Object> variables = renderedVariables();
            assertTrue(variables.containsKey("USER_NICKNAME"));
            assertEquals(expectedNickname, variables.get("USER_NICKNAME"));
            assertEquals(expectedProfile, variables.get("USER_PROFILE"));
        } else {
            assertTrue(resolver.configuration("chat").isEmpty());
        }
        assertEquals(
                fingerprint, resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, null));
        verifyNoInteractions(keys);
        ChatMemoryService memories = fake(ChatMemoryService.class);
        List<ChatMessage> stored = new ArrayList<>();
        when(memories.getMessages("chat")).thenAnswer(call -> List.copyOf(stored));
        doAnswer(call -> {
                    stored.clear();
                    stored.addAll(call.getArgument(1));
                    return null;
                })
                .when(memories)
                .updateMessages(eq("chat"), anyList());
        ChatSession session = ordinarySessions(memories).get(context);
        assertNotNull(session);
        assertEquals(fingerprint, session.getMemoryFingerprint());
        assertTrue(session.getVariables().containsKey("USER_NICKNAME"));
        assertEquals(expectedNickname, session.getVariables().get("USER_NICKNAME"));
        assertEquals(expectedProfile, session.getVariables().get("USER_PROFILE"));
        assertEquals(1, key.closes);
        var bound = resolver.resolveForSession("chat", session.getMemoryFingerprint());
        if (window > 0) {
            var resolution = bound.orElseThrow();
            assertEquals(fingerprint, resolution.resolved().fingerprint());
            assertEquals("tg--1001234567890", resolution.resolved().userId());
            assertEquals(expectedProfile, resolution.resolved().userBaseline());
            assertEquals(2, key.closes);
        } else {
            assertTrue(bound.isEmpty());
            assertEquals(1, key.closes);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "user,",
        "user,123",
        "tg-123,",
        "tg-null,",
        "tg-,",
        "tg-,123",
        "tg-124,123",
        "tg-123,-123",
        "tg--123,123",
        "tg-123-sender,123",
        "other-123,123",
        ",123",
        "' ',123"
    })
    void missingUserRequiresExactTrustedTelegramScopeBeforeCredentials(String userId, Long tgChatId) {
        context.setUserId(userId);
        context.setTgChatId(tgChatId);
        context.setUserNickname("fake populated nickname");
        context.setUserProfile("fake populated profile");
        when(users.loadByUserId("user")).thenReturn(null);
        unavailable(() -> resolver.configuration("chat"));
        unavailable(() -> resolver.resolve("chat"));
        unavailable(() -> resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, null));
        ChatMemoryService memories = fake(ChatMemoryService.class);
        ChatSessionServiceImpl sessions = ordinarySessions(memories);
        assertNull(sessions.get(context));
        for (Integer window : new Integer[] {0, null}) {
            backend.setLongTermMemoryWindowSize(window);
            assertNull(sessions.get(context));
        }
        verifyNoInteractions(keys, memories);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ownerId", "owner"})
    void telegramMissingCharacterOwnerStillFailsBeforeCredentials(String missing) {
        context.setTgChatId(123456789L);
        context.setUserId("tg-123456789");
        context.setUserNickname("fake Telegram nickname");
        context.setUserProfile("fake Telegram profile");
        if (missing.equals("ownerId")) {
            when(characters.getOwnerByUid("character")).thenReturn(null);
        } else {
            when(users.loadByUserId("owner")).thenReturn(null);
        }
        unavailable(() -> resolver.configuration("chat"));
        unavailable(() -> resolver.resolve("chat"));
        ChatMemoryService memories = fake(ChatMemoryService.class);
        assertNull(ordinarySessions(memories).get(context));
        verifyNoInteractions(keys, memories);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "userId",
                "contextMismatch",
                "owner",
                "character",
                "task",
                "prompt",
                "user",
                "language",
                "window",
                "parameters",
                "inputs",
                "variables",
                "draft",
                "messages",
                "messageArray",
                "nullMessages"
            })
    void unavailableOrMalformedEnabledConfigurationNeverBecomesEmptyOrLeaksPayload(String failure) {
        switch (failure) {
            case "userId" -> context.setUserId(null);
            case "contextMismatch" -> context.setChatId("another-chat");
            case "owner" -> when(users.loadByUserId("owner")).thenReturn(null);
            case "character" -> when(characters.details(1L, owner)).thenReturn(Triple.of(null, List.of(), List.of()));
            case "task" -> when(tasks.get("task")).thenReturn(null);
            case "prompt" -> when(prompts.details(2L, owner)).thenReturn(Triple.of(null, List.of(), List.of()));
            case "user" -> when(users.loadByUserId("user")).thenReturn(null);
            case "language" -> character.setLang(null);
            case "window" -> backend.setMessageWindowSize(0);
            case "parameters" -> task.setParams("private payload");
            case "inputs" -> prompt.setInputs("private payload");
            case "variables" -> task.setVariables("private payload");
            case "draft" -> {
                task.setDraft((byte) 1);
                prompt.setDraft("private payload");
            }
            case "messages" -> prompt.setTemplate("{\"messages\":\"private payload\"}");
            case "messageArray" -> prompt.setTemplate("{\"messages\":[]}");
            case "nullMessages" -> prompt.setTemplate("{\"messages\":\"null\"}");
            default -> fail();
        }
        unavailable(() -> resolver.configuration("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    void repositoryAndRenderFailuresAreSanitizedRatherThanEmptyExamples() throws Exception {
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("{{topic}}"))));
        doThrow(new IllegalStateException("private render payload"))
                .when(prompts)
                .apply(any(ChatMessage.class), anyMap(), any());
        unavailable(() -> resolver.configuration("chat"));
        doReturn(null).when(prompts).apply(any(ChatMessage.class), anyMap(), any());
        unavailable(() -> resolver.configuration("chat"));
        when(contexts.get("chat")).thenThrow(new IllegalStateException("private repository payload"));
        unavailable(() -> resolver.configuration("chat"));
        verifyNoInteractions(keys);
    }

    @Test
    void sessionFingerprintUsesOnlySuppliedRecordsAndMatchesFreshResolution() throws Exception {
        prompt.setTemplate(exampleTemplate(List.of(UserMessage.from("{{USER_PROFILE}}"))));
        String expected = resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, user);
        verifyNoInteractions(contexts, characters, users, tasks, keys);
        verify(prompts).apply(any(ChatMessage.class), anyMap(), eq(PromptFormat.MUSTACHE));
        verifyNoMoreInteractions(prompts);
        assertEquals(expected, resolver.configuration("chat").orElseThrow().fingerprint());
        assertEquals(
                expected,
                resolver.resolveForSession("chat", expected)
                        .orElseThrow()
                        .resolved()
                        .fingerprint());
        assertEquals(1, key.closes);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"stable", "user", "character", "prompt", "model", "window", "recall", "disabled", "enabled"})
    void ordinarySessionKeepsItsConsumedSnapshotAndRejectsConcurrentReconfiguration(String change) throws Exception {
        if (change.equals("enabled")) {
            backend.setLongTermMemoryWindowSize(0);
        }
        String expected = resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, user);
        ChatMemoryService memories = fake(ChatMemoryService.class);
        when(memories.usage("chat")).thenAnswer(call -> {
            switch (change) {
                case "user" -> user.setProfile("updated private user baseline");
                case "character" -> character.setProfile("updated private character baseline");
                case "prompt" -> prompt.setTemplate("{\"system\":\"updated policy\",\"messages\":\"[]\"}");
                case "model" -> task.setModelId("[open_ai]different-model|text2chat");
                case "window" -> backend.setMessageWindowSize(20);
                case "recall" -> backend.setLongTermMemoryWindowSize(15);
                case "disabled" -> backend.setLongTermMemoryWindowSize(0);
                case "enabled" -> backend.setLongTermMemoryWindowSize(50);
                case "stable" -> {}
                default -> fail();
            }
            return null;
        });
        ChatSession session = ordinarySessions(memories).get(context);
        assertNotNull(session);
        assertEquals(expected, session.getMemoryFingerprint(), "Capture before concurrent configuration changes");
        assertEquals("system {{USER_PROFILE}}", session.getPrompt().getSystem());
        assertEquals("default user profile", session.getVariables().get("USER_PROFILE"));
        assertEquals("character profile", session.getVariables().get("CHARACTER_PROFILE"));
        assertEquals(
                "chat-model", session.getChatModel().defaultRequestParameters().modelName());
        assertEquals(1, key.closes, "Ordinary construction must not create an extraction model");
        verifyNoInteractions(contexts);
        verify(tasks).get("task");
        verify(prompts).details(2L, owner);
        verify(users).loadByUserId("user");
        clearInvocations(keys);
        if (change.equals("stable")) {
            assertEquals(
                    expected,
                    resolver.resolveForSession("chat", expected)
                            .orElseThrow()
                            .resolved()
                            .fingerprint());
            assertEquals(2, key.closes);
        } else {
            unavailable(() -> resolver.resolveForSession("chat", expected));
            verifyNoInteractions(keys);
            assertEquals(1, key.closes);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"disabled", "assist"})
    void disabledAndAssistantSnapshotsDoNotResolveExtractionConfiguration(String mode) {
        if (mode.equals("disabled")) {
            backend.setLongTermMemoryWindowSize(0);
        } else {
            context.setChatId("chat-assist");
        }
        task.setParams("invalid private parameters");
        prompt.setTemplate("invalid private template");
        String fingerprint = resolver.sessionFingerprint(context, backend, "owner", character, task, prompt, user);
        assertNull(fingerprint);
        assertTrue(resolver.resolveForSession(context.getChatId(), fingerprint).isEmpty());
        verifyNoInteractions(keys, users, tasks, prompts);
    }

    @Test
    void missingEnabledFingerprintFailsBeforeCredentialAcquisition() {
        unavailable(() -> resolver.resolveForSession("chat", null));
        verifyNoInteractions(keys);
    }

    @Test
    void malformedSessionConfigurationFailsBeforeOrdinaryProviderConstruction() {
        prompt.setTemplate("invalid private template");
        ChatMemoryService memories = fake(ChatMemoryService.class);
        assertNull(ordinarySessions(memories).get(context));
        verifyNoInteractions(keys, memories);
    }

    @ParameterizedTest
    @ValueSource(strings = {"configuration", "memory-configuration"})
    void ordinarySessionFailureDiagnosticsExposeOnlyStageAndExceptionClass(String stage) {
        String privateMessage = "fake-private-exception-sentinel";
        String privateCause = "fake-private-cause-sentinel";
        String privateSuppressed = "fake-private-suppressed-sentinel";
        String privatePrompt = "fake-private-malformed-prompt-sentinel";
        context.setUserNickname("fake-private-nickname-sentinel");
        context.setUserProfile("fake-private-profile-sentinel");
        context.setAbout("fake-private-context-sentinel");
        context.setApiKeyValue("fake-private-key-sentinel");
        prompt.setTemplate(privatePrompt);
        IllegalArgumentException failure =
                new IllegalArgumentException(privateMessage, new IllegalStateException(privateCause));
        failure.addSuppressed(new IOException(privateSuppressed));
        if (stage.equals("configuration")) {
            when(characters.getBackend("backend")).thenThrow(failure);
        }
        String error = stage.equals("configuration") ? "IllegalArgumentException" : "IllegalStateException";
        List<String> privateValues = List.of(
                privateMessage,
                privateCause,
                privateSuppressed,
                privatePrompt,
                context.getUserNickname(),
                context.getUserProfile(),
                context.getAbout(),
                context.getApiKeyValue());
        ChatMemoryService memories = fake(ChatMemoryService.class);
        ChatSessionServiceImpl sessions = ordinarySessions(memories);
        Logger logger = (Logger) LoggerFactory.getLogger(ChatSessionServiceImpl.class);
        Level previousLevel = logger.getLevel();
        boolean previousAdditive = logger.isAdditive();
        List<Appender<ILoggingEvent>> previousAppenders = new ArrayList<>();
        logger.iteratorForAppenders().forEachRemaining(previousAppenders::add);
        ListAppender<ILoggingEvent> logs = new ListAppender<>();
        logs.setContext(logger.getLoggerContext());
        logs.start();
        try {
            previousAppenders.forEach(logger::detachAppender);
            logger.addAppender(logs);
            logger.setLevel(Level.WARN);
            logger.setAdditive(false);
            assertNull(sessions.get(context));
            verifyNoInteractions(keys, memories);
            assertEquals(1, logs.list.size());
            ILoggingEvent event = logs.list.getFirst();
            assertEquals(Level.WARN, event.getLevel());
            assertEquals("Failed to build chat session of {} (stage={}, error={})", event.getMessage());
            assertEquals(
                    "Failed to build chat session of chat (stage=" + stage + ", error=" + error + ")",
                    event.getFormattedMessage());
            assertArrayEquals(new Object[] {"chat", stage, error}, event.getArgumentArray());
            assertNull(event.getThrowableProxy());
            for (String privateValue : privateValues) {
                assertFalse(event.getFormattedMessage().contains(privateValue));
                for (Object argument : event.getArgumentArray()) {
                    assertInstanceOf(String.class, argument);
                    assertFalse(argument.toString().contains(privateValue));
                }
            }
        } finally {
            logger.detachAppender(logs);
            previousAppenders.forEach(logger::addAppender);
            logger.setLevel(previousLevel);
            logger.setAdditive(previousAdditive);
            logs.stop();
        }
    }

    private ChatSessionServiceImpl ordinarySessions(ChatMemoryService memories) {
        ChatSessionServiceImpl sessions = new ChatSessionServiceImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "characterService", characters);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "userService", users);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "promptTaskService", tasks);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "promptService", prompts);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "aiApiKeyService", keys);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "memoryModels", resolver);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "chatMemoryService", memories);
        EmbeddingModelService embeddings = fake(EmbeddingModelService.class);
        when(embeddings.modelForLang("en")).thenReturn(fake(dev.langchain4j.model.embedding.EmbeddingModel.class));
        EmbeddingStoreService<?> stores = fake(EmbeddingStoreService.class);
        doReturn(fake(dev.langchain4j.store.embedding.EmbeddingStore.class))
                .when(stores)
                .of(anyString(), any());
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "embeddingModelService", embeddings);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "embeddingStoreService", stores);
        org.springframework.test.util.ReflectionTestUtils.setField(
                sessions, "executor", fake(org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor.class));
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "maxResults", 3);
        org.springframework.test.util.ReflectionTestUtils.setField(sessions, "minScore", 0.5);
        when(prompts.apply(anyString(), anyMap(), any(PromptFormat.class))).thenReturn("rendered system");
        return sessions;
    }

    private String exampleTemplate(List<ChatMessage> examples) throws Exception {
        ChatPromptContent content = new ChatPromptContent();
        // Undefined variables would fail if either non-example field were rendered.
        content.setSystem("{{undefinedSystem}} {undefinedSystem}");
        content.setMessageToSend(UserMessage.from("{{undefinedInput}} {undefinedInput}"));
        content.setMessages(examples);
        return JSON.writeValueAsString(content);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Object> renderedVariables() {
        var captured = org.mockito.ArgumentCaptor.forClass(Map.class);
        verify(prompts).apply(any(ChatMessage.class), captured.capture(), any());
        return captured.getValue();
    }

    private void assertJdkClient(ChatModel model, Duration timeout) throws Exception {
        Object client = field(model, "client");
        assertTrue(((String) field(client, "baseUrl")).startsWith(endpoint));
        Object http = field(client, "httpClient");
        // Payload logging would introduce a LoggingHttpClient wrapper instead.
        assertEquals("JdkHttpClient", http.getClass().getSimpleName());
        assertEquals(timeout, field(http, "readTimeout"));
        assertEquals(
                timeout, ((HttpClient) field(http, "delegate")).connectTimeout().orElseThrow());
    }

    private String snapshot() throws Exception {
        return JSON.writeValueAsString(List.of(context, backend, character, task, prompt, owner, user));
    }

    private static void unavailable(Runnable operation) {
        IllegalStateException error = assertThrows(IllegalStateException.class, operation::run);
        assertEquals("Memory model resolution unavailable", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static Object field(Object target, String name) throws Exception {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                // Inspect inherited provider fields without changing production encapsulation.
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    private static final class RecordingKey extends CloseableAiApiKey {
        private int tokenReads;
        private int closes;
        private boolean failClose;

        private RecordingKey(String token) {
            super(token);
        }

        @Override
        public String token() {
            tokenReads++;
            return super.token();
        }

        @Override
        public void close() throws IOException {
            closes++;
            if (failClose) {
                throw new IOException("private close payload");
            }
        }
    }
}
