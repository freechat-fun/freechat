package fun.freechat.service.chat.memory;

import static fun.freechat.service.ai.AiModelFactory.createAzureOpenAiChatModel;
import static fun.freechat.service.ai.AiModelFactory.createOllamaChatModel;
import static fun.freechat.service.ai.AiModelFactory.createOpenAiChatModel;
import static fun.freechat.service.ai.AiModelFactory.createQwenChatModel;
import static fun.freechat.service.enums.ChatVar.*;
import static fun.freechat.util.ByteUtils.isTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.model.chat.ChatModel;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.PromptTask;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.util.LangUtils;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MemoryModelResolver {
    private static final int MAX_RECALL_LIMIT = 30;
    private static final ObjectMapper JSON = JsonMapper.builder(JsonFactory.builder()
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .streamReadConstraints(
                            StreamReadConstraints.builder().maxNestingDepth(32).build())
                    .build())
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();
    // Only fields consumed by the ordinary synchronous factories. In particular, never hash a
    // whole PromptTask/ChatContext/credential wrapper or arbitrary credential-bearing params.
    private static final Set<String> MODEL_PARAMETERS = Set.of(
            "baseUrl",
            "temperature",
            "topP",
            "topK",
            "maxTokens",
            "numPredict",
            "numCtx",
            "seed",
            "presencePenalty",
            "frequencyPenalty",
            "repeatPenalty",
            "repetitionPenalty",
            "stop",
            "stops",
            "responseFormat",
            "format",
            "reasoningEffort",
            "user",
            "think",
            "enableSearch",
            "enableThinking");
    private static final Set<String> INTEGER_PARAMETERS = Set.of("topK", "maxTokens", "numPredict", "numCtx", "seed");
    private static final Set<String> DECIMAL_PARAMETERS =
            Set.of("temperature", "topP", "presencePenalty", "frequencyPenalty", "repeatPenalty", "repetitionPenalty");

    private final ChatContextService contexts;
    private final CharacterService characters;
    private final SysUserService users;
    private final PromptTaskService tasks;
    private final PromptService prompts;
    private final AiApiKeyService keys;
    private final LongTermMemoryProperties properties;

    public MemoryModelResolver(
            ChatContextService contexts,
            CharacterService characters,
            SysUserService users,
            PromptTaskService tasks,
            PromptService prompts,
            AiApiKeyService keys,
            LongTermMemoryProperties properties) {
        this.contexts = contexts;
        this.characters = characters;
        this.users = users;
        this.tasks = tasks;
        this.prompts = prompts;
        this.keys = keys;
        this.properties = properties;
    }

    /** The chat ID must come from the trusted invocation/job scope, never model-produced arguments. */
    public Resolved resolve(String chatId) {
        return resolveWithConfiguration(chatId)
                .orElseThrow(MemoryModelResolver::unavailable)
                .resolved();
    }

    /** Reads and renders configuration only: never claims credentials or constructs a model/session. */
    public Optional<Configuration> configuration(String chatId) {
        try {
            return load(chatId).map(Prepared::configuration);
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    /** Resolves the model and examples from the same load, avoiding mixed configuration snapshots. */
    public Optional<Resolution> resolveWithConfiguration(String chatId) {
        try {
            Optional<Prepared> prepared = load(chatId);
            if (prepared.isEmpty()) {
                return Optional.empty();
            }
            Prepared current = prepared.get();
            return Optional.of(new Resolution(construct(current), current.configuration()));
        } catch (Exception ignored) {
            // Parsing, repositories, credential decryption and provider builders can include payloads
            // in their messages/causes (including suppressed close failures). Do not propagate them.
            throw unavailable();
        }
    }

    public String sessionFingerprint(
            ChatContext context,
            CharacterBackend backend,
            String ownerId,
            CharacterInfo character,
            PromptTask task,
            PromptInfo prompt,
            User user) {
        try {
            if (context.getChatId().endsWith("-assist")
                    || backend.getLongTermMemoryWindowSize() == null
                    || backend.getLongTermMemoryWindowSize() <= 0) {
                return null;
            }
            return prepare(context, backend, ownerId, character, task, prompt, user)
                    .configuration()
                    .fingerprint();
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    public Optional<Resolution> resolveForSession(String chatId, String fingerprint) {
        try {
            Optional<Prepared> prepared = load(chatId);
            String current =
                    prepared.map(value -> value.configuration().fingerprint()).orElse(null);
            if (!java.util.Objects.equals(fingerprint, current)) {
                throw unavailable();
            }
            if (prepared.isEmpty()) {
                return Optional.empty();
            }
            Prepared value = prepared.get();
            return Optional.of(new Resolution(construct(value), value.configuration()));
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Memory model resolution unavailable");
    }

    private Optional<Prepared> load(String chatId) throws Exception {
        require(StringUtils.isNotBlank(chatId));
        if (chatId.endsWith("-assist")) {
            return Optional.empty();
        }
        ChatContext context = contexts.get(chatId);
        if (context == null) {
            return Optional.empty();
        }
        require(chatId.equals(context.getChatId()));
        if (StringUtils.isBlank(context.getBackendId())) {
            return Optional.empty();
        }
        CharacterBackend backend = characters.getBackend(context.getBackendId());
        if (backend == null
                || backend.getLongTermMemoryWindowSize() == null
                || backend.getLongTermMemoryWindowSize() <= 0) {
            return Optional.empty();
        }
        String characterUid = backend.getCharacterUid();
        require(StringUtils.isNotBlank(characterUid) && StringUtils.isNotBlank(backend.getChatPromptTaskId()));
        String ownerId = characters.getOwnerByUid(characterUid);
        require(StringUtils.isNotBlank(ownerId));
        User owner = users.loadByUserId(ownerId);
        require(owner != null);
        Long characterId = characters.getLatestIdByUid(characterUid, owner);
        require(characterId != null);
        CharacterInfo character = characters.details(characterId, owner).getLeft();
        PromptTask task = tasks.get(backend.getChatPromptTaskId());
        require(task != null && StringUtils.isNotBlank(task.getPromptUid()));
        Long promptId = prompts.getLatestIdByUid(task.getPromptUid(), owner);
        require(promptId != null);
        PromptInfo prompt = prompts.details(promptId, owner).getLeft();
        User user = users.loadByUserId(context.getUserId());
        return Optional.of(prepare(context, backend, ownerId, character, task, prompt, user));
    }

    private Prepared prepare(
            ChatContext context,
            CharacterBackend backend,
            String ownerId,
            CharacterInfo character,
            PromptTask task,
            PromptInfo prompt,
            User user)
            throws Exception {
        require(StringUtils.isNotBlank(context.getUserId()));
        require(backend.getMessageWindowSize() != null && backend.getMessageWindowSize() > 0);
        require(StringUtils.isNotBlank(ownerId));
        String characterUid = backend.getCharacterUid();
        require(StringUtils.isNotBlank(characterUid) && StringUtils.isNotBlank(backend.getChatPromptTaskId()));
        require(character != null && StringUtils.isNotBlank(character.getLang()));
        require(task != null && StringUtils.isNotBlank(task.getPromptUid()));
        require(prompt != null && user != null);
        properties.afterPropertiesSet();
        AiModelInfo model = InfoUtils.toAiModelInfo(task.getModelId());
        require(model != null && StringUtils.isNotBlank(model.getName()));
        require("text2chat".equalsIgnoreCase(model.getType()) || "text2text".equalsIgnoreCase(model.getType()));
        ModelProvider provider = ModelProvider.of(model.getProvider());
        require(provider == ModelProvider.OPEN_AI
                || provider == ModelProvider.AZURE_OPEN_AI
                || provider == ModelProvider.DASH_SCOPE
                || provider == ModelProvider.OLLAMA);
        Map<String, Object> parameters = parameters(task.getParams(), provider);
        Integer contextLimit = provider == ModelProvider.OLLAMA && parameters.get("numCtx") != null
                ? new BigDecimal(parameters.get("numCtx").toString()).intValueExact()
                : null;
        if (contextLimit != null) {
            require(contextLimit > 0
                    && Math.max((long) properties.getMaxInputTokens(), properties.getExtractionMaxInputTokens())
                                    + properties.getResponseReserveTokens()
                            <= contextLimit);
        }

        String userBaseline = fallback(context.getUserProfile(), user.getProfile());
        Map<String, Object> inputs = object(prompt.getInputs());
        Map<String, Object> taskVariables = object(task.getVariables());
        Map<String, Object> variables = variables(context, character, user, inputs, taskVariables);
        Map<String, Object> selectedTemplate = template(prompt, task);
        List<ChatMessage> examples = examples(selectedTemplate, variables, PromptFormat.of(prompt.getFormat()));
        Map<String, Object> characterDefaults = new TreeMap<>();
        characterDefaults.put("name", text(character.getName()));
        characterDefaults.put("configuredNickname", text(character.getNickname()));
        // Normal chat falls back to name, not CharacterInfo.nickname.
        characterDefaults.put("nickname", fallback(context.getCharacterNickname(), character.getName()));
        characterDefaults.put("description", text(character.getDescription()));
        characterDefaults.put("profile", text(character.getProfile()));
        characterDefaults.put("gender", text(character.getGender()));
        characterDefaults.put("chatStyle", text(character.getChatStyle()));
        characterDefaults.put("chatExample", text(character.getChatExample()));
        characterDefaults.put("greeting", text(character.getGreeting()));
        characterDefaults.put("defaultScene", text(character.getDefaultScene()));
        characterDefaults.put("context", text(context.getAbout()));
        String characterBaseline = JSON.writeValueAsString(characterDefaults);

        Map<String, Object> configuration = new TreeMap<>();
        configuration.put("version", 2);
        configuration.put("backendId", context.getBackendId());
        configuration.put("characterUid", characterUid);
        configuration.put("characterOwnerId", ownerId);
        configuration.put("userId", context.getUserId());
        configuration.put("language", character.getLang());
        configuration.put("modelId", task.getModelId());
        configuration.put("parameters", parameters);
        configuration.put("messageWindowSize", backend.getMessageWindowSize());
        configuration.put("longTermMemoryWindowSize", backend.getLongTermMemoryWindowSize());
        configuration.put("userBaseline", userBaseline);
        configuration.put("userNickname", text((String) variables.get(USER_NICKNAME.text())));
        configuration.put("characterBaseline", characterDefaults);
        configuration.put("promptUid", task.getPromptUid());
        configuration.put("promptFormat", text(prompt.getFormat()));
        configuration.put("promptTemplate", selectedTemplate);
        configuration.put("promptInputs", inputs);
        configuration.put("taskVariables", taskVariables);
        configuration.put("renderedExamples", ChatMessageSerializer.messagesToJson(examples));
        configuration.put("extractionTimeout", properties.getExtractionTimeout().toString());
        configuration.put("extractionMaxInputTokens", properties.getExtractionMaxInputTokens());
        configuration.put("maxInputTokens", properties.getMaxInputTokens());
        configuration.put("responseReserveTokens", properties.getResponseReserveTokens());
        configuration.put("summaryMaxTokens", properties.getSummaryMaxTokens());
        configuration.put("profileMaxTokens", properties.getProfileMaxTokens());
        configuration.put("profileMaxFacts", properties.getProfileMaxFacts());
        configuration.put("tokenEstimatorBean", properties.getTokenEstimatorBean());
        String fingerprint = MemoryDocumentCodec.hash(JSON.writeValueAsString(configuration));
        return new Prepared(
                new Configuration(context.getUserId(), characterUid, character.getLang(), fingerprint, examples),
                context,
                ownerId,
                task,
                model,
                provider,
                parameters,
                userBaseline,
                characterBaseline,
                backend.getMessageWindowSize(),
                Math.min(backend.getLongTermMemoryWindowSize(), MAX_RECALL_LIMIT),
                contextLimit);
    }

    private Resolved construct(Prepared prepared) throws Exception {
        Configuration configuration = prepared.configuration();
        ModelProvider provider = prepared.provider();
        AiModelInfo model = prepared.model();
        // Copy only; never mutate stored task parameters or baseline records. Preserve provider
        // sampling settings instead of guessing which model families accept a fixed temperature.
        Map<String, Object> extraction = new TreeMap<>(prepared.parameters());
        extraction.put("maxTokens", properties.getResponseReserveTokens());
        extraction.put("numPredict", properties.getResponseReserveTokens());
        extraction.put("enableSearch", false);
        extraction.put("enableThinking", false);
        extraction.put("think", false);
        ChatModel chatModel;
        try (CloseableAiApiKey credential = credential(prepared.context(), prepared.ownerId(), prepared.task())) {
            require(credential != null);
            // Ordinary chat also claims/closes the selected wrapper for Ollama, but never reads its token.
            String token = provider == ModelProvider.OLLAMA ? null : credential.token();
            require(provider == ModelProvider.OLLAMA || StringUtils.isNotBlank(token));
            chatModel = switch (provider) {
                case OPEN_AI ->
                    createOpenAiChatModel(token, model.getName(), extraction, properties.getExtractionTimeout());
                case AZURE_OPEN_AI ->
                    createAzureOpenAiChatModel(token, model.getName(), extraction, properties.getExtractionTimeout());
                case DASH_SCOPE ->
                    createQwenChatModel(token, model.getName(), extraction, properties.getExtractionTimeout());
                case OLLAMA -> createOllamaChatModel(model.getName(), extraction, properties.getExtractionTimeout());
                default -> throw new IllegalArgumentException();
            };
        }
        return new Resolved(
                chatModel,
                prepared.task().getModelId(),
                configuration.language(),
                prepared.userBaseline(),
                prepared.characterBaseline(),
                configuration.fingerprint(),
                configuration.characterUid(),
                configuration.userId(),
                prepared.messageWindowSize(),
                prepared.recallLimit(),
                prepared.contextLimit());
    }

    private List<ChatMessage> examples(Map<String, Object> template, Map<String, Object> variables, PromptFormat format)
            throws Exception {
        // The annotated deserializer can normalize serialized null to an empty list. Reject malformed
        // serialized lists first rather than silently treating them as a configuration without examples.
        Object serializedMessages = template.get("messages");
        if (serializedMessages != null) {
            require(serializedMessages instanceof String);
            var messages = JSON.readTree((String) serializedMessages);
            require(messages != null && messages.isArray());
        }
        // Honor ChatPromptContent's serialized-message-list Jackson annotations, not a JSON array cast.
        ChatPromptContent content = JSON.readerFor(ChatPromptContent.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(JSON.writeValueAsString(template));
        require(template.get("messages") == null || content.getMessages() != null);
        if (content.getMessages() == null) {
            return List.of();
        }
        // Applying the whole content would also synthesize a messageToSend; only examples belong here.
        return content.getMessages().stream()
                .map(message -> prompts.apply(message, variables, format))
                .collect(java.util.stream.Collectors.toUnmodifiableList());
    }

    private static Map<String, Object> variables(
            ChatContext context,
            CharacterInfo character,
            User user,
            Map<String, Object> inputs,
            Map<String, Object> taskVariables) {
        Map<String, Object> variables = new HashMap<>();
        inputs.forEach((key, value) -> {
            if (value != null && !(value instanceof String string && StringUtils.isBlank(string))) {
                variables.put(key, value);
            }
        });
        variables.putAll(taskVariables);
        // Match ordinary chat: authoritative values overwrite tasks, including blank/null defaults.
        variables.put(CHARACTER_LANG.text(), LangUtils.codeToLabel(character.getLang()));
        variables.put(
                CHARACTER_NICKNAME.text(),
                StringUtils.defaultIfBlank(context.getCharacterNickname(), character.getName()));
        variables.put(CHARACTER_DESCRIPTION.text(), character.getDescription());
        variables.put(CHARACTER_GENDER.text(), character.getGender());
        variables.put(CHARACTER_CHAT_STYLE.text(), StringUtils.defaultIfBlank(character.getChatStyle(), ""));
        variables.put(CHARACTER_CHAT_EXAMPLE.text(), StringUtils.defaultIfBlank(character.getChatExample(), ""));
        variables.put(CHARACTER_GREETING.text(), StringUtils.defaultIfBlank(character.getGreeting(), ""));
        variables.put(CHARACTER_PROFILE.text(), StringUtils.defaultIfBlank(character.getProfile(), ""));
        variables.put(
                USER_PROFILE.text(),
                StringUtils.defaultIfBlank(
                        StringUtils.defaultIfBlank(context.getUserProfile(), user.getProfile()), ""));
        variables.put(
                USER_NICKNAME.text(),
                StringUtils.defaultIfBlank(
                        context.getUserNickname(),
                        StringUtils.defaultIfBlank(
                                user.getNickname(),
                                StringUtils.defaultIfBlank(user.getPreferredUsername(), user.getUsername()))));
        variables.put(CHAT_CONTEXT.text(), StringUtils.defaultIfBlank(context.getAbout(), ""));
        return variables;
    }

    private CloseableAiApiKey credential(ChatContext context, String ownerId, PromptTask task) {
        String keyOwner = context.getUserId();
        String keyName = context.getApiKeyName();
        String keyValue = context.getApiKeyValue();
        if (StringUtils.isBlank(keyName) && StringUtils.isBlank(keyValue)) {
            keyOwner = ownerId;
            keyName = task.getApiKeyName();
            keyValue = task.getApiKeyValue();
        }
        return StringUtils.isNotBlank(keyName) ? keys.use(keyOwner, keyName) : keys.use(keyValue);
    }

    private static Map<String, Object> parameters(String json, ModelProvider provider) throws Exception {
        Map<String, Object> result = new TreeMap<>();
        for (var entry : object(json).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!MODEL_PARAMETERS.contains(key)) {
                continue;
            }
            if (value != null) {
                if (INTEGER_PARAMETERS.contains(key)) {
                    require(value instanceof Number);
                    BigDecimal number = new BigDecimal(value.toString());
                    if (key.equals("seed") && provider == ModelProvider.AZURE_OPEN_AI) {
                        number.longValueExact();
                    } else {
                        number.intValueExact();
                    }
                } else if (DECIMAL_PARAMETERS.contains(key)) {
                    require(value instanceof Number && Double.isFinite(((Number) value).doubleValue()));
                } else if (key.equals("stop") || key.equals("stops")) {
                    require(value instanceof List<?> list && list.stream().allMatch(String.class::isInstance));
                } else if (key.equals("think") || key.equals("enableSearch") || key.equals("enableThinking")) {
                    require(value instanceof Boolean || value instanceof Number || value instanceof String);
                } else {
                    require(value instanceof String);
                }
            }
            result.put(key, value);
        }
        String baseUrl = (String) result.get("baseUrl");
        if (StringUtils.isNotBlank(baseUrl)) {
            URI uri = URI.create(baseUrl);
            require(uri.getHost() != null
                    && ("http".equalsIgnoreCase(uri.getScheme())
                            || "https".equalsIgnoreCase(uri.getScheme())
                            || provider == ModelProvider.DASH_SCOPE && "wss".equalsIgnoreCase(uri.getScheme())));
        } else {
            require(provider != ModelProvider.AZURE_OPEN_AI && provider != ModelProvider.OLLAMA);
        }
        return result;
    }

    private static Map<String, Object> template(PromptInfo prompt, PromptTask task) throws Exception {
        String template = prompt.getTemplate();
        if (isTrue(task.getDraft()) && StringUtils.isNotBlank(prompt.getDraft())) {
            // Do not use PromptUtils.getDraftTemplate: its malformed-input path logs the draft.
            Map<String, Object> draft = object(prompt.getDraft());
            if (PromptType.of((String) draft.get("type")) == PromptType.CHAT) {
                Object chatTemplate = draft.get("chatTemplate");
                require(chatTemplate instanceof Map<?, ?>);
                template = JSON.writeValueAsString(chatTemplate);
            } else {
                template = (String) draft.get("template");
            }
        }
        require(StringUtils.isNotBlank(template));
        Map<String, Object> result = object(template);
        require(result.get("system") == null || result.get("system") instanceof String);
        return result;
    }

    private static Map<String, Object> object(String json) throws Exception {
        if (StringUtils.isBlank(json)) {
            return Map.of();
        }
        Map<String, Object> result = JSON.readValue(json, new TypeReference<>() {});
        require(result != null);
        return result;
    }

    private static String fallback(String preferred, String alternative) {
        return StringUtils.isNotBlank(preferred) ? preferred : text(alternative);
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    private record Prepared(
            Configuration configuration,
            ChatContext context,
            String ownerId,
            PromptTask task,
            AiModelInfo model,
            ModelProvider provider,
            Map<String, Object> parameters,
            String userBaseline,
            String characterBaseline,
            int messageWindowSize,
            int recallLimit,
            Integer contextLimit) {
        @Override
        public String toString() {
            return "Prepared[redacted]";
        }
    }

    /** Credential/model-free configuration and the exact rendered examples for history reconciliation. */
    public record Configuration(
            String userId, String characterUid, String language, String fingerprint, List<ChatMessage> examples) {
        public Configuration {
            examples = List.copyOf(examples);
        }

        @Override
        public String toString() {
            return "Configuration[redacted]";
        }
    }

    public record Resolution(Resolved resolved, Configuration configuration) {
        @Override
        public String toString() {
            return "Resolution[redacted]";
        }
    }

    /** Baselines are explicit configuration, not learned facts/deltas. Never serialize the model. */
    public record Resolved(
            ChatModel model,
            String modelId,
            String language,
            String userBaseline,
            String characterBaseline,
            String fingerprint,
            String characterUid,
            String userId,
            int messageWindowSize,
            int recallLimit,
            Integer providerContextLimit) {
        @Override
        public String toString() {
            return "Resolved[redacted]";
        }
    }
}
