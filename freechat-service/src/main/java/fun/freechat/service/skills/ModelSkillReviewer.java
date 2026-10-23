package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_REVIEW_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSAFE_INSTRUCTIONS;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.output.FinishReason;
import fun.freechat.service.ai.AiModelFactory;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.service.util.InfoUtils;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Dedicated management-only review; no chat context, tool service, user credentials or model-selected paths. */
@Component
public final class ModelSkillReviewer implements SkillReviewer, DisposableBean {
    public static final String BUILTIN_CLASSIFICATION = "MANAGEMENT";
    static final String SYSTEM_RESOURCE = "/skills/install-skill/SKILL.md";
    static final String SNAPSHOT_START = "BEGIN_UNTRUSTED_SKILL_SNAPSHOT_JSON\n";
    static final String SNAPSHOT_END = "\nEND_UNTRUSTED_SKILL_SNAPSHOT_JSON";
    // A conservative byte budget, not a heuristic character/token estimate. Operators must provide a
    // >=32K-token model. Oversize snapshots are rejected, never truncated; Ollama truncation is disabled.
    static final int CONTEXT_TOKENS = 32 * 1024;
    static final int MAX_INPUT_BYTES = CONTEXT_TOKENS - 1024;
    static final int MAX_RESPONSE_BYTES = 256;
    static final int MAX_OUTPUT_TOKENS = 64;
    static final int MAX_CONCURRENT_REVIEWS = 4;
    private static final Duration DEADLINE = Duration.ofSeconds(15);
    private static final ObjectMapper JSON = JsonMapper.builder(JsonFactory.builder()
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(3)
                            .maxStringLength(MAX_RESPONSE_BYTES)
                            .maxNumberLength(8)
                            .build())
                    .build())
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    private final ChatModel model;
    private final String instructions;
    private final Duration deadline;
    private final ThreadPoolExecutor executor;

    @Autowired
    public ModelSkillReviewer(
            @Value("${skills.review.model-id:}") String modelId,
            @Value("${skills.review.api-key:}") String apiKey,
            @Value("${skills.review.base-url:}") String baseUrl) {
        this(serverModel(modelId, apiKey, baseUrl), DEADLINE);
    }

    public ModelSkillReviewer(ChatModel model) {
        this(model, DEADLINE);
    }

    ModelSkillReviewer(ChatModel model, Duration deadline) {
        if (deadline == null || deadline.isNegative() || deadline.isZero() || deadline.compareTo(DEADLINE) > 0) {
            throw new IllegalArgumentException("Invalid review deadline");
        }
        this.model = model;
        this.deadline = deadline;
        this.instructions = loadInstructions();
        this.executor = new ThreadPoolExecutor(
                0,
                MAX_CONCURRENT_REVIEWS,
                30,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                runnable -> {
                    Thread thread = new Thread(runnable, "skill-review");
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void review(SkillBundle bundle) {
        long started = System.nanoTime();
        if (model == null || instructions == null || Thread.currentThread().isInterrupted()) {
            throw unavailable();
        }
        ChatRequest request = request(bundle);
        Future<ChatResponse> pending = null;
        try {
            long remaining = deadline.toNanos() - (System.nanoTime() - started);
            if (remaining <= 0) {
                throw unavailable();
            }
            // No queue and no per-call detached thread: even a provider ignoring interrupts can occupy
            // at most four workers. Cancellation does not free its slot until the actual call returns.
            pending = executor.submit(() -> model.chat(request));
            ChatResponse response = pending.get(remaining, TimeUnit.NANOSECONDS);
            if (System.nanoTime() - started >= deadline.toNanos()) {
                throw unavailable();
            }
            if (!decision(response)) {
                throw new SkillException(SKILL_UNSAFE_INSTRUCTIONS);
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw unavailable();
        } catch (ExecutionException | TimeoutException | RejectedExecutionException ignored) {
            // Never expose provider prose, exception causes, endpoints, credentials or bundle contents.
            throw unavailable();
        } finally {
            if (pending != null && !pending.isDone()) {
                pending.cancel(true);
            }
        }
    }

    private ChatRequest request(SkillBundle bundle) {
        if (bundle == null) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        SkillBundle checked = new SkillBundleValidator().validate(bundle.files());
        if (!checked.name().equals(bundle.name())
                || !checked.description().equals(bundle.description())
                || !checked.digest().equals(bundle.digest())) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", checked.name());
        snapshot.put("description", checked.description());
        snapshot.put("tags", List.of("user", "prompt-only"));
        snapshot.put("digest", checked.digest());
        snapshot.put("files", checked.files());
        try {
            String userMessage = SNAPSHOT_START + JSON.writeValueAsString(snapshot) + SNAPSHOT_END;
            int bytes = instructions.getBytes(StandardCharsets.UTF_8).length
                    + userMessage.getBytes(StandardCharsets.UTF_8).length;
            if (bytes > MAX_INPUT_BYTES) {
                throw new SkillException(SkillException.Code.SKILL_LIMIT_EXCEEDED);
            }
            var request =
                    ChatRequest.builder().messages(SystemMessage.from(instructions), UserMessage.from(userMessage));
            if (model instanceof OpenAiChatModel openai
                    && openai.defaultRequestParameters().maxCompletionTokens() != null) {
                // Never send both max_tokens and max_completion_tokens to OpenAI.
                request.parameters(OpenAiChatRequestParameters.builder()
                        .maxCompletionTokens(MAX_OUTPUT_TOKENS)
                        .temperature(0.0)
                        .toolSpecifications(List.of())
                        .build());
            } else {
                request.maxOutputTokens(MAX_OUTPUT_TOKENS).temperature(0.0).toolSpecifications(List.of());
            }
            return request.build();
        } catch (SkillException failure) {
            throw failure;
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    private static boolean decision(ChatResponse response) {
        try {
            if (response == null
                    || response.aiMessage() == null
                    || response.finishReason() != FinishReason.STOP
                    || response.aiMessage().hasToolExecutionRequests()
                    || !response.aiMessage().images().isEmpty()
                    || response.aiMessage().thinking() != null
                            && !response.aiMessage().thinking().isBlank()) {
                throw unavailable();
            }
            String text = response.aiMessage().text();
            SkillBundleValidator.textBytes(text, MAX_RESPONSE_BYTES);
            JsonNode result = JSON.readTree(text);
            if (result == null
                    || !result.isObject()
                    || result.size() != 1
                    || !result.has("allowed")
                    || !result.get("allowed").isBoolean()) {
                throw unavailable();
            }
            return result.get("allowed").booleanValue();
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    private static String loadInstructions() {
        // Exact classpath resource only. It is not registered as a character skill or a tool.
        try (InputStream stream = ModelSkillReviewer.class.getResourceAsStream(SYSTEM_RESOURCE)) {
            if (stream == null) {
                return null;
            }
            byte[] bytes = stream.readNBytes(8193);
            if (bytes.length == 0 || bytes.length > 8192) {
                return null;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static ChatModel serverModel(String modelId, String apiKey, String baseUrl) {
        // Nothing here resolves a character, conversation, database model entry or user API key.
        if (modelId == null || modelId.isBlank() || baseUrl == null || baseUrl.isBlank()) {
            return null;
        }
        try {
            URI endpoint = URI.create(baseUrl);
            if (!("https".equals(endpoint.getScheme()) || "http".equals(endpoint.getScheme()))
                    || endpoint.getHost() == null
                    || endpoint.getRawUserInfo() != null
                    || endpoint.getRawQuery() != null
                    || endpoint.getRawFragment() != null) {
                return null;
            }
            AiModelInfo info = InfoUtils.toAiModelInfo(modelId);
            if (!"text2chat".equals(info.getType())) {
                return null;
            }
            Map<String, Object> parameters = Map.of(
                    "baseUrl",
                    baseUrl,
                    "temperature",
                    0.0,
                    "seed",
                    0,
                    "maxTokens",
                    MAX_OUTPUT_TOKENS,
                    "numPredict",
                    MAX_OUTPUT_TOKENS,
                    "numCtx",
                    CONTEXT_TOKENS,
                    "responseFormat",
                    "json_object",
                    "format",
                    "json",
                    "think",
                    false);
            if ("ollama".equals(info.getProvider())) {
                var defaults = OllamaChatRequestParameters.builder()
                        .overrideWith(AiModelFactory.createOllamaChatModel(info.getName(), parameters, DEADLINE)
                                .defaultRequestParameters())
                        .truncate(false)
                        .build();
                // Factory reuse is restricted to explicit server-owned parameters. Hardened clients are
                // rebuilt because the shared factory has no retry control; do not change shared defaults.
                return OllamaChatModel.builder()
                        .baseUrl(baseUrl)
                        .defaultRequestParameters(defaults)
                        .timeout(DEADLINE)
                        .maxRetries(0)
                        .logRequests(false)
                        .logResponses(false)
                        .build();
            }
            if (("open_ai".equals(info.getProvider()) || "dash_scope".equals(info.getProvider()))
                    && apiKey != null
                    && !apiKey.isBlank()) {
                var defaults = AiModelFactory.createOpenAiChatModel(apiKey, info.getName(), parameters, DEADLINE)
                        .defaultRequestParameters();
                if ("dash_scope".equals(info.getProvider())) {
                    defaults = OpenAiChatRequestParameters.builder()
                            .overrideWith(defaults)
                            .maxCompletionTokens(null)
                            .maxOutputTokens(MAX_OUTPUT_TOKENS)
                            .build();
                }
                // Qwen uses its OpenAI-compatible endpoint, not the native SDK's detached timeout worker.
                // Configure e.g. https://dashscope.aliyuncs.com/compatible-mode/v1 explicitly.
                var builder = OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .defaultRequestParameters(defaults)
                        .timeout(DEADLINE)
                        .maxRetries(0)
                        .logRequests(false)
                        .logResponses(false);
                if ("dash_scope".equals(info.getProvider())) {
                    builder.customParameters(Map.of("enable_search", false, "enable_thinking", false));
                }
                return builder.build();
            }
        } catch (RuntimeException ignored) {
            // Invalid or absent server configuration disables review; never log secrets from builders.
        }
        return null;
    }

    private static SkillException unavailable() {
        return new SkillException(SKILL_REVIEW_UNAVAILABLE);
    }

    @Override
    public void destroy() {
        executor.shutdownNow();
    }
}
