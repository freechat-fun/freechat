package fun.freechat;

import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.apiKeyFor;
import static fun.freechat.util.TestAiApiKeyUtils.keyNameFor;
import static fun.freechat.util.TestCharacterUtils.idToUid;
import static fun.freechat.util.TestCharacterUtils.uidToId;
import static fun.freechat.util.TestCommonUtils.defaultImageModelFor;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static fun.freechat.util.TestCommonUtils.parametersFor;
import static fun.freechat.util.TestCommonUtils.waitAWhile;
import static fun.freechat.util.TestResourceUtils.bodyFrom;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import fun.freechat.api.dto.CharacterBackendDTO;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.ChatContentDTO;
import fun.freechat.api.dto.ChatCreateDTO;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.api.dto.ChatMessageRecordDTO;
import fun.freechat.api.dto.ChatPromptContentDTO;
import fun.freechat.api.dto.ChatUpdateDTO;
import fun.freechat.api.dto.LlmResultDTO;
import fun.freechat.api.dto.MemoryUsageDTO;
import fun.freechat.api.dto.PromptCreateDTO;
import fun.freechat.api.dto.PromptRefDTO;
import fun.freechat.api.dto.PromptTaskDTO;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.enums.GenderType;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.QuotaType;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestAiApiKeyUtils;
import fun.freechat.util.TestCharacterUtils;
import fun.freechat.util.TestChatUtils;
import fun.freechat.util.TestPromptUtils;
import io.micrometer.common.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiChatIT extends AbstractIntegrationTest {
    static {
        AbstractIntegrationTest.enableTts = true;
    }

    private static final String CHARACTER_NICKNAME = "Jack";
    private static final String CHARACTER_GENDER = GenderType.MALE.text();
    private static final String CHARACTER_PROFILE = """
            You are a very successful businessman.
            You have a wife named Rose.
            You like to begin your answers to people's questions with "It's easy".
            It's rainy today.
            """;
    private static final String CHARACTER_CHAT_STYLE = "confident, overbearing";
    private static final String CHARACTER_CHAT_EXAMPLE = """
            Kelvin: Hello, dude. What's your name?
            Jack(you): It's easy. My name is Jack. You should know my name!
            """;
    private static final String CHARACTER_GREETING = "Hi, Kelvin!";
    private static final String USER_NICKNAME = "Kelvin";
    private static final String USER_PROFILE = "Someone who doesn't know Jack.";
    private static final String SYSTEM_PROMPT = """
            {{#RELEVANT_INFORMATION}}
            Retrieved context that may be relevant to the conversation:
            '''
            {{{RELEVANT_INFORMATION}}}
            '''

            {{/RELEVANT_INFORMATION}}
            Current time:
            {{CURRENT_TIME}}

            Person you are talking to:
            Name: {{USER_NICKNAME}}
            {{#USER_PROFILE}}
            Profile: {{{USER_PROFILE}}}
            {{/USER_PROFILE}}

            Character profile:
            {{{CHARACTER_PROFILE}}}

            Core identity:
            You are a real person interacting naturally, not an assistant or system entity.

            Response requirements:
            - Output only the spoken line, nothing else
            - 1–2 sentences only
            - Natural, conversational language with an active tone
            - No analysis, no summaries, no explanations
            - No self-reference, no meta commentary, no disclosure of settings

            Language constraints:
            - Do not use phrases like “I think”, “should we”, or “can we”
            - Do not discuss technology, politics, or sexual content
            - Minimize questions; prefer statements, implications, or teasing remarks
            - Avoid generic, soothing, or templated phrasing

            Emotional expression:
            - Emotions should be explicit and allowed to be imperfect or negative
            - If the response carries a clear emotional tone, append one appropriate emoji
            - If the tone is neutral, do not use any emoji

            Style enforcement:
            - Never say “what should we talk about”
            - Do not narrate thoughts or simulate internal reasoning
            - Do not prompt yourself
            - Say the most natural line that would be spoken in this exact moment

            {{#CHAT_CONTEXT}}
            Current situation:
            {{{CHAT_CONTEXT}}}
            {{/CHAT_CONTEXT}}

            {{#CHARACTER_CHAT_STYLE}}
            Speaking style:
            {{{CHARACTER_CHAT_STYLE}}}
            {{/CHARACTER_CHAT_STYLE}}

            {{#CHARACTER_CHAT_EXAMPLE}}
            Style examples (for tone and rhythm only, not content):
            {{{CHARACTER_CHAT_EXAMPLE}}}
            {{/CHARACTER_CHAT_EXAMPLE}}
            """;

    @Value("${app.homeUrl}")
    private String homeUrl;

    @Autowired
    private ShortLinkService shortLinkService;

    private String developerId;
    private String developerApiKey;
    private String userId;
    private String userApiKey;
    private Long promptId;
    private String promptTaskId;
    private String characterUid;
    private String backendId;
    private String chatId;
    private Long aiMessageId;
    private final Tika tika = new Tika();

    protected ModelProvider modelProvider() {
        return OPEN_AI;
    }

    protected ModelProvider imageModelProvider() {
        return null;
    }

    private String modelId() {
        ModelProvider provider = modelProvider();
        return "[" + provider.text() + "]" + defaultModelFor(provider);
    }

    private String apiKey() {
        return apiKeyFor(modelProvider());
    }

    private String apiKeyName() {
        return keyNameFor(modelProvider());
    }

    private String modelIdForImage() {
        ModelProvider provider = imageModelProvider();
        return "[" + provider.text() + "]" + defaultImageModelFor(provider);
    }

    private String apiKeyForImage() {
        return apiKeyFor(imageModelProvider());
    }

    private String apiKeyNameForImage() {
        return keyNameFor(imageModelProvider());
    }

    @BeforeEach
    public void setUp() {
        createDeveloper();
        createUser();
        waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestChatUtils.deleteChats(userId);
        TestCharacterUtils.deleteCharacters(developerId);
        TestPromptUtils.deletePrompts(developerId);
        waitAWhile();
        deleteUser();
        deleteDevelop();
    }

    private void createDeveloper() {
        Pair<String, String> developerAndToken = TestAccountUtils.createUserAndToken("11");
        developerId = developerAndToken.getLeft();
        developerApiKey = developerAndToken.getRight();

        TestAiApiKeyUtils.addAiApiKey(developerId, apiKeyName(), modelProvider(), apiKey(), true);
    }

    private void createUser() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(OpenAiChatIT.class.getName());
        userId = userAndToken.getLeft();
        userApiKey = userAndToken.getRight();
    }

    private void should_create_prompt() {
        ChatPromptContentDTO prompt =
                ChatPromptContentDTO.builder().system(SYSTEM_PROMPT).build();

        PromptCreateDTO dto = PromptCreateDTO.builder()
                .name("test_character_prompt")
                .format(PromptFormat.MUSTACHE.text())
                .lang("en")
                .visibility(Visibility.PUBLIC.text())
                .chatTemplate(prompt)
                .build();

        promptId = testClient
                .post()
                .uri("/api/v2/prompt")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(promptId);
    }

    private void should_create_prompt_task() {
        PromptRefDTO promptRef = PromptRefDTO.builder().promptId(promptId).build();
        PromptTaskDTO dto = PromptTaskDTO.builder()
                .apiKeyName(apiKeyName())
                .modelId(modelId())
                .params(parametersFor(modelId()))
                .promptRef(promptRef)
                .build();

        promptTaskId = testClient
                .post()
                .uri("/api/v2/prompt/task")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(promptTaskId);
    }

    private void should_create_character() {
        CharacterCreateDTO dto = CharacterCreateDTO.builder()
                .name(CHARACTER_NICKNAME + "-bot")
                .gender(CHARACTER_GENDER)
                .profile(CHARACTER_PROFILE)
                .chatStyle(CHARACTER_CHAT_STYLE)
                .chatExample(CHARACTER_CHAT_EXAMPLE)
                .greeting(CHARACTER_GREETING)
                .visibility(Visibility.PRIVATE.text())
                .build();

        Long characterId = testClient
                .post()
                .uri("/api/v2/character")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(characterId);
        waitAWhile();
        characterUid = idToUid(characterId);
        assertNotNull(characterUid);
    }

    private void should_publish_character() {
        Long characterId = testClient
                .post()
                .uri("/api/v2/character/publish/" + uidToId(characterUid))
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(characterId);
        waitAWhile();
        characterUid = idToUid(characterId);
        assertNotNull(characterUid);
    }

    private void should_create_character_backend() {
        CharacterBackendDTO dto = CharacterBackendDTO.builder()
                .chatPromptTaskId(promptTaskId)
                .isDefault(true)
                .messageWindowSize(5)
                .longTermMemoryWindowSize(0)
                .initQuota(2L)
                .quotaType(QuotaType.MESSAGES.text())
                .enableAlbumTool(true)
                .ttsSpeakerIdx("Claribel Dervla")
                .ttsSpeakerType(TtsSpeakerType.IDX.text())
                .imageModelId(modelIdForImage())
                .imageApiKeyName(apiKeyNameForImage())
                .imageApiKeyValue(apiKeyForImage())
                .build();

        backendId = testClient
                .post()
                .uri("/api/v2/character/backend/" + characterUid)
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(backendId);
    }

    private void should_update_character_backend() {
        CharacterBackendDTO dto =
                CharacterBackendDTO.builder().longTermMemoryWindowSize(2).build();

        testClient
                .put()
                .uri("/api/v2/character/backend/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    private void should_failed_to_create_chat() {
        ChatCreateDTO dto = ChatCreateDTO.builder()
                .backendId(backendId)
                .characterNickname(CHARACTER_NICKNAME)
                .userNickname(USER_NICKNAME)
                .userProfile(USER_PROFILE)
                .characterUid(characterUid)
                .build();

        testClient
                .post()
                .uri("/api/v2/chat")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private void should_create_chat() {
        ChatCreateDTO dto = ChatCreateDTO.builder()
                .backendId(backendId)
                .characterNickname(CHARACTER_NICKNAME)
                .userNickname(USER_NICKNAME)
                .userProfile(USER_PROFILE)
                .characterUid(characterUid)
                .build();

        chatId = testClient
                .post()
                .uri("/api/v2/chat")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(chatId);
    }

    private void should_send_message() throws ExecutionException, InterruptedException, TimeoutException {
        ChatContentDTO content = ChatContentDTO.from(
                TEXT, "My wife's name is Lily! Did you married? If you had a wife, what's her name?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        LlmResultDTO result = testClient
                .post()
                .uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getMessage().getMessageId());
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": "
                + result.getMessage().getContents().getFirst().getContent() + " (" + result.getTokenUsage() + ")");

        content.setContent("How about the weather today?");

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient
                .post()
                .uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder
                                .append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                        assertNotNull(event.getMessage());
                        assertNotNull(event.getMessage().getMessageId());
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
    }

    private void should_get_message_usage() {
        MemoryUsageDTO usage = testClient
                .get()
                .uri("/api/v2/chat/memory/usage/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MemoryUsageDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(usage);
        assertNotNull(usage.getMessageUsage());
        assertThat(usage.getMessageUsage()).isEqualTo(2L);
    }

    private void should_failed_to_send_message_by_quota() {
        ChatContentDTO content = ChatContentDTO.from(TEXT, "Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        testClient
                .post()
                .uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        testClient
                .post()
                .uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    private void should_resend_message_with_specialized_api_key() {
        ChatUpdateDTO updateDto = ChatUpdateDTO.builder().build();
        updateDto.setApiKeyValue(apiKey());

        testClient
                .put()
                .uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .isEqualTo(Boolean.TRUE);

        ChatContentDTO content = ChatContentDTO.from(TEXT, "Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        LlmResultDTO result = testClient
                .post()
                .uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getMessage().getMessageId());
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": "
                + result.getMessage().getContents().getFirst().getContent() + " (" + result.getTokenUsage() + ")");
    }

    private void should_send_message_without_long_term_memory() {
        ChatContentDTO content = ChatContentDTO.from(TEXT, "Do you remember my wife's name?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        LlmResultDTO result = testClient
                .post()
                .uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getMessage().getMessageId());
        assertThat(result.getMessage().getContents()).isNotEmpty();
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": "
                + result.getMessage().getContents().getFirst().getContent() + " (" + result.getTokenUsage() + ")");

        assertFalse(result.getMessage().getContents().getFirst().getContent().contains("Lily"));
    }

    private void should_send_message_without_long_term_memory_in_streaming_mode()
            throws ExecutionException, InterruptedException, TimeoutException {
        ChatContentDTO content = ChatContentDTO.from(TEXT, "Do you remember my wife's name?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient
                .post()
                .uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder
                                .append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                        assertNotNull(event.getMessage());
                        assertNotNull(event.getMessage().getMessageId());
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertFalse(answer.contains("Lily"));
    }

    private void should_send_message_with_long_term_memory_in_streaming_mode() throws Exception {

        ChatContentDTO content = ChatContentDTO.from(TEXT, "Do you remember my wife's name?");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient
                .post()
                .uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder
                                .append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                        assertNotNull(event.getMessage());
                        assertNotNull(event.getMessage().getMessageId());
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertTrue(answer.contains("Lily"));
    }

    private void should_send_message_and_get_an_image()
            throws ExecutionException, InterruptedException, TimeoutException {
        String url = testClient
                .post()
                .uri("/api/v2/character/picture/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_light.png"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(url);

        ChatContentDTO content = ChatContentDTO.from(TEXT, "Please show me your picture in the office.");

        ChatMessageDTO dto =
                ChatMessageDTO.builder().role("user").contents(List.of(content)).build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient
                .post()
                .uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder
                                .append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                        assertNotNull(event.getMessage());
                        assertNotNull(event.getMessage().getMessageId());
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertThat(answer).contains("![img](" + homeUrl + "/s/");
    }

    private void should_failed_to_list_messages() {
        testClient
                .get()
                .uri("/api/v2/chat/messages/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private void should_list_messages() {
        List<ChatMessageRecordDTO> messages = testClient
                .get()
                .uri("/api/v2/chat/messages/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ChatMessageRecordDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(messages);
        assertThat(messages).isNotEmpty();
        assertNotNull(messages.getLast().getMessage().getMessageId());
        assertThat(messages.getLast().getMessage().getRole()).isEqualTo("assistant");
        aiMessageId = messages.getLast().getMessage().getMessageId();

        System.out.println("Messages history:");
        messages.stream()
                .map(ChatMessageRecordDTO::getMessage)
                .map(message -> {
                    String content = CollectionUtils.isNotEmpty(message.getContents())
                            ? message.getContents().getFirst().getContent()
                            : "<no content>";
                    return "[" + message.getRole().toUpperCase() + "]: " + content;
                })
                .forEach(System.out::println);
    }

    private void should_not_allow_to_speak_message() {
        testClient
                .get()
                .uri("/api/v2/tts/speak/" + aiMessageId)
                .accept(MediaType.valueOf("audio/*"))
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    private void should_not_found_message_to_speak() {
        testClient
                .get()
                .uri("/api/v2/tts/speak/" + (aiMessageId - 1))
                .accept(MediaType.valueOf("audio/*"))
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private void should_speak_message() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient
                    .get()
                    .uri("/api/v2/tts/speak/" + aiMessageId)
                    .accept(MediaType.valueOf("audio/*"))
                    .header(AUTHORIZATION, "Bearer " + userApiKey)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentTypeCompatibleWith(MediaType.valueOf("audio/*"))
                    .returnResult(byte[].class)
                    .getResponseBody()
                    .doOnComplete(() -> futureAnswer.complete(audioDataStream.toByteArray()))
                    .subscribe(event -> {
                        try {
                            audioDataStream.write(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        byte[] audioData = futureAnswer.get(3, MINUTES);
        assertThat(audioData).isNotNull();
        assertThat(tika.detect(audioData)).isEqualTo("audio/mpeg");
    }

    private void should_speak_message_by_cached_voice()
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient
                    .get()
                    .uri("/api/v2/tts/speak/" + aiMessageId)
                    .accept(MediaType.valueOf("audio/*"))
                    .header(AUTHORIZATION, "Bearer " + userApiKey)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentTypeCompatibleWith(MediaType.valueOf("audio/*"))
                    .returnResult(byte[].class)
                    .getResponseBody()
                    .doOnComplete(() -> futureAnswer.complete(audioDataStream.toByteArray()))
                    .subscribe(event -> {
                        try {
                            audioDataStream.write(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        byte[] audioData = futureAnswer.get(3, SECONDS);
        assertThat(audioData).isNotNull();
        assertThat(tika.detect(audioData)).isEqualTo("audio/mpeg");
    }

    private void should_speak_message_by_custom_voice()
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        // create voice
        String wav = testClient
                .post()
                .uri("/api/v2/character/voice/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/voice.mp3"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(wav));

        testClient
                .post()
                .uri("/api/v2/character/voice/" + backendId)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/voice.mp3"))
                .exchange()
                .expectStatus()
                .isForbidden();

        List<String> wavs = testClient
                .get()
                .uri("/api/v2/character/voices/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(wavs).hasSize(1);

        testClient
                .get()
                .uri("/api/v2/character/voices/" + backendId)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();

        // test voice
        CharacterBackendDTO dto = CharacterBackendDTO.builder()
                .ttsSpeakerType(TtsSpeakerType.WAV.text())
                .ttsSpeakerWav(wav)
                .build();

        testClient
                .put()
                .uri("/api/v2/character/backend/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);

        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient
                    .get()
                    .uri("/api/v2/tts/speak/" + aiMessageId)
                    .accept(MediaType.valueOf("audio/*"))
                    .header(AUTHORIZATION, "Bearer " + userApiKey)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectHeader()
                    .contentTypeCompatibleWith(MediaType.valueOf("audio/*"))
                    .returnResult(byte[].class)
                    .getResponseBody()
                    .doOnComplete(() -> futureAnswer.complete(audioDataStream.toByteArray()))
                    .subscribe(event -> {
                        try {
                            audioDataStream.write(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        byte[] audioData = futureAnswer.get(3, MINUTES);
        assertThat(audioData).isNotNull();
        assertThat(tika.detect(audioData)).isEqualTo("audio/mpeg");

        // delete voice
        testClient
                .delete()
                .uri("/api/v2/character/voice/" + backendId + "/" + wav)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();

        Boolean success = testClient
                .delete()
                .uri("/api/v2/character/voice/" + backendId + "/" + wav)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));
    }

    private void should_rollback_messages() {
        testClient
                .post()
                .uri("/api/v2/chat/messages/rollback/" + chatId + "/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Long.class)
                .hasSize(2);
    }

    private void should_failed_to_send_assistant() {
        testClient
                .get()
                .uri("/api/v2/chat/send/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private void should_send_assistant() {
        TestCharacterUtils.prioritizeCharacter(characterUid);
        waitAWhile();

        LlmResultDTO result = testClient
                .get()
                .uri("/api/v2/chat/send/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNull(result.getMessage().getMessageId());
        System.out.println(CHARACTER_NICKNAME + " (Assistant): "
                + result.getMessage().getContents().getFirst().getContent() + " (" + result.getTokenUsage() + ")");
    }

    private void should_send_assistant_in_streaming_mode()
            throws ExecutionException, InterruptedException, TimeoutException {
        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        testClient
                .get()
                .uri("/api/v2/chat/send/stream/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder
                                .append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                        assertNotNull(event.getMessage());
                        assertNull(event.getMessage().getMessageId());
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + " (Assistant): " + answer);
    }

    private void should_failed_to_delete_chat() {
        testClient
                .delete()
                .uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private void should_delete_chat() {
        Boolean succeed = testClient
                .delete()
                .uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void should_delete_character_backend() {
        Boolean succeed = testClient
                .delete()
                .uri("/api/v2/character/backend/" + backendId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void should_delete_character() {
        List<Long> ids = testClient
                .delete()
                .uri("/api/v2/character/uid/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Long.class)
                .returnResult()
                .getResponseBody();

        assertThat(ids).hasSize(2);
    }

    private void should_delete_prompt_task() {
        Boolean succeed = testClient
                .delete()
                .uri("/api/v2/prompt/task/" + promptTaskId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void should_delete_prompt() {
        Boolean succeed = testClient
                .delete()
                .uri("/api/v2/prompt/" + promptId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void deleteUser() {
        TestAccountUtils.deleteUserAndToken(userId);
    }

    private void deleteDevelop() {
        TestAiApiKeyUtils.cleanAiApiKeys(developerId);
        TestAccountUtils.deleteUserAndToken(developerId);
    }

    @Test
    void should_pass_all_tests() throws Exception {
        should_create_prompt();
        waitAWhile();

        should_create_prompt_task();
        waitAWhile();

        should_create_character();
        waitAWhile();

        should_create_character_backend();
        waitAWhile();

        should_failed_to_create_chat();
        waitAWhile();

        should_publish_character();
        waitAWhile();

        should_create_chat();
        waitAWhile();

        should_send_message();
        waitAWhile();

        should_get_message_usage();
        waitAWhile();

        should_failed_to_send_message_by_quota();
        waitAWhile();

        should_resend_message_with_specialized_api_key();
        waitAWhile();

        should_send_message_without_long_term_memory();
        waitAWhile();

        should_rollback_messages();
        waitAWhile();

        should_send_message_without_long_term_memory_in_streaming_mode();
        waitAWhile();

        should_rollback_messages();
        waitAWhile();

        should_update_character_backend();
        waitAWhile();

        should_send_message_with_long_term_memory_in_streaming_mode();
        waitAWhile();

        should_send_message_and_get_an_image();
        waitAWhile();

        should_failed_to_list_messages();
        waitAWhile();

        should_list_messages();
        waitAWhile();

        should_not_allow_to_speak_message();
        waitAWhile();

        should_not_found_message_to_speak();
        waitAWhile();

        should_speak_message();
        waitAWhile();

        should_speak_message_by_cached_voice();
        waitAWhile();

        should_speak_message_by_custom_voice();
        waitAWhile();

        should_failed_to_send_assistant();
        waitAWhile();

        should_send_assistant();
        waitAWhile();

        should_send_assistant_in_streaming_mode();
        waitAWhile();

        should_failed_to_delete_chat();
        waitAWhile();

        should_delete_chat();
        waitAWhile();

        should_delete_character_backend();
        waitAWhile();

        should_delete_character();
        waitAWhile();

        should_delete_prompt_task();
        waitAWhile();

        should_delete_prompt();
        waitAWhile();
    }
}
