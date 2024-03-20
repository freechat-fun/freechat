package fun.freechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.api.dto.*;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestAiApiKeyUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestPromptUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "DASHSCOPE_API_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class PromptAiIT extends AbstractIntegrationTest {
    private static final String PROMPT = "say 'hello'";
    private static final String PROMPT_DRAFT = "{\"template\":\"say 'goodbye'\",\"type\":\"string\"}";
    private static final String PROMPT_TEMPLATE_FSTRING = "say '{greeting}'";
    private static final String PROMPT_TEMPLATE_MUSTACHE = "say '{{greeting}}'";

    private String userId;

    private String apiToken;

    private Long promptId;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(PromptAiIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        promptId = TestPromptUtils.createPrompt(userId, PROMPT, PROMPT_DRAFT);

        TestAiApiKeyUtils.addAiApiKey(userId, "test_api_key_dash_scope",
                ModelProvider.DASH_SCOPE, TestAiApiKeyUtils.apiKeyOfDashScope(), true);
        TestAiApiKeyUtils.addAiApiKey(userId, "test_api_key_open_ai",
                ModelProvider.OPEN_AI, TestAiApiKeyUtils.apiKeyOfOpenAI(), true);

        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompt(userId, promptId);
        TestAiApiKeyUtils.cleanAiApiKeys(userId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    private PromptAiParamDTO createRequest(String modelId) {
        PromptAiParamDTO aiForPrompt = new PromptAiParamDTO();
        Map<String, Object> param = new HashMap<>();
        if (modelId.startsWith("[dash_scope]")) {
            param.put("topP", 0.8d);
            param.put("seed", new Random().nextInt(0, Integer.MAX_VALUE));
        } else if (modelId.startsWith("[open_ai]")) {
            param.put("baseUrl", "https://api.openai-proxy.com/v1");
            param.put("maxTokens", 100);
            param.put("temperature", 0.7d);
        }
        param.put("modelId", modelId);
        aiForPrompt.setParams(param);
        return aiForPrompt;
    }

    private ChatMessageDTO userMessage(String content) {
        ChatMessageDTO message = new ChatMessageDTO();
        message.setRole("user");
        message.setName("Rose");
        message.setContents(List.of(ChatContentDTO.fromText(content)));
        return message;
    }

    private ChatMessageDTO aiMessage(String content) {
        ChatMessageDTO message = new ChatMessageDTO();
        message.setRole("assistant");
        message.setContents(List.of(ChatContentDTO.fromText(content)));
        return message;
    }

    @ParameterizedTest
    @MethodSource
    public void testPromptWithApiKey(String modelId, String apiKey) {
        PromptAiParamDTO aiRequest = createRequest(modelId);
        aiRequest.getParams().put("apiKey", apiKey);
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("hello"));
    }

    static Stream<Arguments> testPromptWithApiKey() {
        return Stream.of(
                Arguments.of("[dash_scope]qwen-vl-max", TestAiApiKeyUtils.apiKeyOfDashScope()),
                Arguments.of("[open_ai]gpt-3.5-turbo-instruct", TestAiApiKeyUtils.apiKeyOfOpenAI()),
                Arguments.of("[open_ai]gpt-3.5-turbo", TestAiApiKeyUtils.apiKeyOfOpenAI())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testPromptWithApiKeyName(String modelId, String apiKeyName) {
        PromptAiParamDTO aiRequest = createRequest(modelId);
        aiRequest.getParams().put("apiKeyName", apiKeyName);
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("hello"));
    }

    static Stream<Arguments> testPromptWithApiKeyName() {
        return Stream.of(
                Arguments.of("[dash_scope]qwen-vl-max", "test_api_key_dash_scope"),
                Arguments.of("[open_ai]gpt-3.5-turbo", "test_api_key_open_ai")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testPromptTemplate(String template, String format, Map<String, Object> variables) {
        PromptTemplateDTO promptTemplate = new PromptTemplateDTO();
        promptTemplate.setTemplate(template);
        promptTemplate.setFormat(format);
        promptTemplate.setVariables(variables);

        PromptAiParamDTO aiRequest = createRequest("[dash_scope]qwen-vl-max");
        aiRequest.getParams().put("apiKey", TestAiApiKeyUtils.apiKeyOfDashScope());
        aiRequest.setPromptTemplate(promptTemplate);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("goodbye"));
    }

    static Stream<Arguments> testPromptTemplate() {
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("greeting", "goodbye");
        return Stream.of(
                Arguments.of(PROMPT_TEMPLATE_FSTRING, PromptFormat.F_STRING.text(), variables),
                Arguments.of(PROMPT_TEMPLATE_MUSTACHE, PromptFormat.MUSTACHE.text(), variables)
        );
    }

    @Test
    public void testPromptRef() {
        PromptRefDTO promptRef = new PromptRefDTO();
        promptRef.setPromptId(promptId);
        promptRef.setDraft(false);

        PromptAiParamDTO aiRequest = createRequest("[dash_scope]qwen-vl-max");
        aiRequest.getParams().put("apiKey", TestAiApiKeyUtils.apiKeyOfDashScope());
        aiRequest.setPromptRef(promptRef);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("hello"));

        promptRef.setDraft(true);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("goodbye"));
    }

    @ParameterizedTest
    @MethodSource
    public void testEmbedding(String modelId, String apiKey) {
        PromptAiParamDTO aiRequest = createRequest(modelId);
        aiRequest.getParams().put("apiKey", apiKey);
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text -> {
                    try {
                        List<Float> embeddings = new ObjectMapper().readValue(text.toString(), new TypeReference<>() {});
                        assertThat(embeddings).hasSizeGreaterThan(0);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    static Stream<Arguments> testEmbedding() {
        return Stream.of(
                Arguments.of("[open_ai]text-embedding-ada-002", TestAiApiKeyUtils.apiKeyOfOpenAI()),
                Arguments.of("[dash_scope]text-embedding-v1", TestAiApiKeyUtils.apiKeyOfDashScope())
        );
    }

    @Test
    public void testPromptOpForbidden() {
        PromptAiParamDTO aiRequest = createRequest("[dash_scope]qwen-vl-max");
        aiRequest.getParams().put("apiKeyName", "No Key");
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isForbidden();

        PromptRefDTO promptRef = new PromptRefDTO();
        promptRef.setPromptId(1000L);
        aiRequest = createRequest("[dash_scope]qwen-vl-max");
        aiRequest.getParams().put("apiKey", TestAiApiKeyUtils.apiKeyOfDashScope());
        aiRequest.setPromptRef(promptRef);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource
    public void testChatPrompt(String modelId, String apiKey) {
        String system = "Your name is {name}. You are {age} years old.";

        List<ChatMessageDTO> messages = new LinkedList<>();
        messages.add(userMessage("Hello. What's your name?"));
        messages.add(aiMessage("Hi! My name is {name}"));

        ChatMessageDTO messageToBeSent = userMessage("Nice to meet you {name}! How old are you?");

        ChatPromptContentDTO promptContent = new ChatPromptContentDTO();
        promptContent.setSystem(system);
        promptContent.setMessageToSend(messageToBeSent);
        promptContent.setMessages(messages);

        Map<String, Object> variables = new HashMap<>(2);
        variables.put("name", "Jack");
        variables.put("age", "18");

        PromptTemplateDTO promptTemplate = new PromptTemplateDTO();
        promptTemplate.setFormat(PromptFormat.F_STRING.text());
        promptTemplate.setVariables(variables);
        promptTemplate.setChatTemplate(promptContent);

        PromptAiParamDTO aiRequest = createRequest(modelId);
        aiRequest.getParams().put("apiKey", apiKey);
        aiRequest.setPromptTemplate(promptTemplate);

        testClient.post().uri("/api/v1/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.text").value(text ->
                        assertThat(text.toString()).containsIgnoringCase("18"));
    }

    static Stream<Arguments> testChatPrompt() {
        return Stream.of(
                Arguments.of("[dash_scope]qwen-vl-max", TestAiApiKeyUtils.apiKeyOfDashScope()),
                Arguments.of("[open_ai]gpt-3.5-turbo", TestAiApiKeyUtils.apiKeyOfOpenAI())
        );
    }
}
