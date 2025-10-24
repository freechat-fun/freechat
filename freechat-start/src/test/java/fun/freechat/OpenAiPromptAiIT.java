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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.apiKeyFor;
import static fun.freechat.util.TestAiApiKeyUtils.keyNameFor;
import static fun.freechat.util.TestCommonUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiPromptAiIT extends AbstractIntegrationTest {
    private static final String PROMPT = "say 'hello'";
    private static final String PROMPT_DRAFT = "{\"template\":\"say 'goodbye'\",\"type\":\"string\"}";
    private static final String PROMPT_TEMPLATE_FSTRING = "say '{greeting}'";
    private static final String PROMPT_TEMPLATE_MUSTACHE = "say '{{greeting}}'";

    private String userId;
    private String apiToken;
    private Long promptId;

    protected ModelProvider modelProvider() {
        return OPEN_AI;
    }

    private String modelId() {
        ModelProvider provider = modelProvider();
        return "[" + provider.text() + "]" + defaultModelFor(provider);
    }

    private String embeddingModelId() {
        ModelProvider provider = modelProvider();
        return "[" + provider.text() + "]" + defaultEmbeddingModelFor(provider);
    }

    private String apiKey() {
        return apiKeyFor(modelProvider());
    }

    private String apiKeyName() {
        return keyNameFor(modelProvider());
    }

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(OpenAiPromptAiIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        promptId = TestPromptUtils.createPrompt(userId, PROMPT, PROMPT_DRAFT);
        ModelProvider provider = modelProvider();

        TestAiApiKeyUtils.addAiApiKey(userId, keyNameFor(provider), provider, apiKeyFor(provider), true);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompt(userId, promptId);
        TestAiApiKeyUtils.cleanAiApiKeys(userId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    private PromptAiParamDTO createRequest(String modelId) {
        return PromptAiParamDTO.builder()
                .params(parametersFor(modelId))
                .build();
    }

    private ChatMessageDTO userMessage(String content) {
        return ChatMessageDTO.builder()
                .role("user")
                .name("Rose")
                .contents(List.of(ChatContentDTO.from(TEXT, content)))
                .build();
    }

    private ChatMessageDTO aiMessage(String content) {
        return ChatMessageDTO.builder()
                .role("assistant")
                .contents(List.of(ChatContentDTO.from(TEXT, content)))
                .build();
    }

    @Test
    void should_send_prompt_with_api_key() {
        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v2/prompt/send")
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

    @Test
    void should_send_prompt_with_api_key_name() {
        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKeyName", apiKeyName());
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v2/prompt/send")
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

    @ParameterizedTest
    @MethodSource
    void should_send_prompt_template(String template, String format, Map<String, Object> variables) {
        PromptTemplateDTO promptTemplate = PromptTemplateDTO.builder()
                .template(template)
                .format(format)
                .variables(variables)
                .build();

        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPromptTemplate(promptTemplate);

        testClient.post().uri("/api/v2/prompt/send")
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

    static Stream<Arguments> should_send_prompt_template() {
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("greeting", "goodbye");
        return Stream.of(
                Arguments.of(PROMPT_TEMPLATE_FSTRING, PromptFormat.F_STRING.text(), variables),
                Arguments.of(PROMPT_TEMPLATE_MUSTACHE, PromptFormat.MUSTACHE.text(), variables)
        );
    }

    @Test
    void should_send_prompt_reference() {
        PromptRefDTO promptRef = PromptRefDTO.builder().promptId(promptId).draft(false).build();

        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPromptRef(promptRef);

        testClient.post().uri("/api/v2/prompt/send")
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

        testClient.post().uri("/api/v2/prompt/send")
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

    @Test
    void should_send_prompt_and_get_embeddings() {
        PromptAiParamDTO aiRequest = createRequest(embeddingModelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v2/prompt/send")
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

    @Test
    void should_failed_to_send_prompt() {
        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKeyName", "No Key");
        aiRequest.setPrompt(PROMPT);

        testClient.post().uri("/api/v2/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isForbidden();

        PromptRefDTO promptRef = PromptRefDTO.builder().promptId(1000L).build();
        aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPromptRef(promptRef);

        testClient.post().uri("/api/v2/prompt/send")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(aiRequest)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void should_send_chat_prompt() {
        String system = "Your name is {name}. You are {age} years old.";

        List<ChatMessageDTO> messages = new LinkedList<>();
        messages.add(userMessage("Hello. What's your name?"));
        messages.add(aiMessage("Hi! My name is {name}"));

        ChatMessageDTO messageToBeSent = userMessage("Nice to meet you {name}! How old are you?");

        ChatPromptContentDTO promptContent = ChatPromptContentDTO.builder()
                .system(system)
                .messageToSend(messageToBeSent)
                .messages(messages)
                .build();

        Map<String, Object> variables = new HashMap<>(2);
        variables.put("name", "Jack");
        variables.put("age", "18");

        PromptTemplateDTO promptTemplate = PromptTemplateDTO.builder()
                .format(PromptFormat.F_STRING.text())
                .variables(variables)
                .chatTemplate(promptContent)
                .build();

        PromptAiParamDTO aiRequest = createRequest(modelId());
        aiRequest.getParams().put("apiKey", apiKey());
        aiRequest.setPromptTemplate(promptTemplate);

        testClient.post().uri("/api/v2/prompt/send")
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
}
