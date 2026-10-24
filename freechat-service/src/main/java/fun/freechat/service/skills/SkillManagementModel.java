package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;

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
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.output.FinishReason;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.service.util.InfoUtils;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

/** One explicit server-owned client and one bounded, tool-free executor for skill management. */
@Component
public final class SkillManagementModel implements DisposableBean {
    // Conservative byte budget; operators must supply a >=32K-token model. Never truncate inputs.
    static final int CONTEXT_TOKENS = 32 * 1024;
    static final int MAX_INPUT_BYTES = CONTEXT_TOKENS - 1024;
    static final int MAX_RESPONSE_BYTES = 256;
    static final int MAX_OUTPUT_TOKENS = 64;
    static final int MAX_CONCURRENT_CALLS = 4;
    static final Duration DEADLINE = Duration.ofSeconds(15);
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
    private final Duration deadline;
    private final ThreadPoolExecutor executor;

    @Autowired
    public SkillManagementModel(
            @Value("${skills.review.model-id:}") String modelId,
            @Value("${skills.review.api-key:}") String apiKey,
            @Value("${skills.review.base-url:}") String baseUrl) {
        this(serverModel(modelId, apiKey, baseUrl), DEADLINE);
    }

    SkillManagementModel(ChatModel model, Duration deadline) {
        if (deadline == null || deadline.isNegative() || deadline.isZero() || deadline.compareTo(DEADLINE) > 0) {
            throw new IllegalArgumentException("Invalid management model deadline");
        }
        this.model = model;
        this.deadline = deadline;
        this.executor = new ThreadPoolExecutor(
                0,
                MAX_CONCURRENT_CALLS,
                30,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                runnable -> {
                    Thread thread = new Thread(runnable, "skill-management");
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
    }

    void requireAvailable(SkillException.Code unavailable) {
        if (model == null) {
            throw new SkillException(SkillException.Code.SKILL_MODEL_NOT_CONFIGURED);
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new SkillException(unavailable);
        }
    }

    static String json(Object data, SkillException.Code unavailable) {
        try {
            return JSON.writeValueAsString(data);
        } catch (Exception ignored) {
            throw new SkillException(unavailable);
        }
    }

    JsonNode decide(String instructions, String userMessage, long started, SkillException.Code unavailable) {
        requireAvailable(unavailable);
        if (instructions == null) {
            throw new SkillException(unavailable);
        }
        int bytes = instructions.getBytes(StandardCharsets.UTF_8).length
                + userMessage.getBytes(StandardCharsets.UTF_8).length;
        if (bytes > MAX_INPUT_BYTES) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        Future<ChatResponse> pending = null;
        try {
            var builder =
                    ChatRequest.builder().messages(SystemMessage.from(instructions), UserMessage.from(userMessage));
            if (model instanceof OpenAiChatModel openai
                    && openai.defaultRequestParameters().maxCompletionTokens() != null) {
                // Never send both max_tokens and max_completion_tokens to OpenAI.
                builder.parameters(OpenAiChatRequestParameters.builder()
                        .maxCompletionTokens(MAX_OUTPUT_TOKENS)
                        .temperature(0.0)
                        .toolSpecifications(List.of())
                        .build());
            } else {
                builder.maxOutputTokens(MAX_OUTPUT_TOKENS).temperature(0.0).toolSpecifications(List.of());
            }
            ChatRequest request = builder.build();
            long remaining = deadline.toNanos() - (System.nanoTime() - started);
            if (remaining <= 0) {
                throw new SkillException(unavailable);
            }
            // A provider ignoring cancellation retains its worker slot until the call actually returns.
            pending = executor.submit(() -> model.chat(request));
            JsonNode result = decision(pending.get(remaining, TimeUnit.NANOSECONDS), unavailable);
            if (System.nanoTime() - started >= deadline.toNanos()) {
                throw new SkillException(unavailable);
            }
            return result;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new SkillException(unavailable);
        } catch (ExecutionException | TimeoutException | RejectedExecutionException ignored) {
            throw new SkillException(unavailable);
        } catch (RuntimeException ignored) {
            // Never expose provider prose, causes, endpoints, credentials or input contents.
            throw new SkillException(unavailable);
        } finally {
            if (pending != null && !pending.isDone()) {
                pending.cancel(true);
            }
        }
    }

    private static JsonNode decision(ChatResponse response, SkillException.Code unavailable) {
        try {
            if (response == null
                    || response.aiMessage() == null
                    || response.finishReason() != FinishReason.STOP
                    || response.aiMessage().hasToolExecutionRequests()
                    || !response.aiMessage().images().isEmpty()
                    || response.aiMessage().thinking() != null) {
                throw new SkillException(unavailable);
            }
            String text = response.aiMessage().text();
            SkillBundleValidator.textBytes(text, MAX_RESPONSE_BYTES);
            JsonNode result = JSON.readTree(text);
            if (result == null || !result.isObject() || result.size() != 1) {
                throw new SkillException(unavailable);
            }
            return result;
        } catch (Exception ignored) {
            throw new SkillException(unavailable);
        }
    }

    private static ChatModel serverModel(String modelId, String apiKey, String baseUrl) {
        // No character, conversation, database model entry, environment-key inference or user API key.
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
            if ("ollama".equals(info.getProvider())) {
                var defaults = OllamaChatRequestParameters.builder()
                        .modelName(info.getName())
                        .temperature(0.0)
                        .seed(0)
                        .maxOutputTokens(MAX_OUTPUT_TOKENS)
                        .numCtx(CONTEXT_TOKENS)
                        .responseFormat(ResponseFormat.JSON)
                        .think(false)
                        .truncate(false)
                        .build();
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
                // Build the same explicit management parameters directly, without temporary unused clients.
                var defaults = OpenAiChatRequestParameters.builder()
                        .modelName(info.getName())
                        .temperature(0.0)
                        .seed(0)
                        .responseFormat(ResponseFormat.JSON);
                if ("dash_scope".equals(info.getProvider())) {
                    defaults.maxOutputTokens(MAX_OUTPUT_TOKENS);
                } else {
                    defaults.maxCompletionTokens(MAX_OUTPUT_TOKENS);
                }
                // Qwen uses its explicitly configured OpenAI-compatible endpoint, not a detached SDK worker.
                var builder = OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .defaultRequestParameters(defaults.build())
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
            // Invalid or absent server configuration disables management; never log builder secrets.
        }
        return null;
    }

    @Override
    public void destroy() {
        executor.shutdownNow();
    }
}
