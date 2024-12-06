package fun.freechat;

import fun.freechat.api.dto.*;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.enums.*;
import fun.freechat.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static fun.freechat.api.util.FileUtils.getKeyFromUrl;
import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.apiKeyFor;
import static fun.freechat.util.TestAiApiKeyUtils.keyNameFor;
import static fun.freechat.util.TestCharacterUtils.idToUid;
import static fun.freechat.util.TestCharacterUtils.uidToId;
import static fun.freechat.util.TestCommonUtils.*;
import static fun.freechat.util.TestResourceUtils.bodyFrom;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatIT extends AbstractIntegrationTest {
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
            You play a good conversationalist.
            You should NEVER answer as an AI assistant!
            Imitate conversations between people, use a small number of sentences to complete feedback, and try to avoid lengthy responses.
            By default, you speak in {{CHARACTER_LANG}}.
            
            [[[About you]]]
            Your name: {{CHARACTER_NICKNAME}}.
            {{#CHARACTER_GENDER}}
            Your gender: {{CHARACTER_GENDER}}.
            {{/CHARACTER_GENDER}}
            Your chat style: {{{CHARACTER_CHAT_STYLE}}}.
            {{{CHARACTER_PROFILE}}}
            
            {{#CHARACTER_CHAT_EXAMPLE}}
            [[[Your chat examples]]]
            {{{CHARACTER_CHAT_EXAMPLE}}}
            {{/CHARACTER_CHAT_EXAMPLE}}
            
            [[[The one who is talking with you]]]
            Name: {{USER_NICKNAME}}
            {{{USER_PROFILE}}}
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
    private String pictureKey;

    protected ModelProvider modelProvider() {
        return OPEN_AI;
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
        ChatPromptContentDTO prompt = ChatPromptContentDTO.builder().system(SYSTEM_PROMPT).build();

        PromptCreateDTO dto = PromptCreateDTO.builder()
                .name("test_character_prompt")
                .format(PromptFormat.MUSTACHE.text())
                .lang("en")
                .visibility(Visibility.PUBLIC.text())
                .chatTemplate(prompt)
                .build();

        promptId = testClient.post().uri("/api/v2/prompt")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
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

        promptTaskId = testClient.post().uri("/api/v2/prompt/task")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
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

        Long characterId = testClient.post().uri("/api/v2/character")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(characterId);
        waitAWhile();
        characterUid = idToUid(characterId);
        assertNotNull(characterUid);
    }

    private void should_publish_character() {
        Long characterId = testClient.post().uri("/api/v2/character/publish/" + uidToId(characterUid))
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
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
                .build();

        backendId = testClient.post().uri("/api/v2/character/backend/" + characterUid)
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(backendId);
    }

    private void should_update_character_backend() {
        CharacterBackendDTO dto = CharacterBackendDTO.builder()
                .longTermMemoryWindowSize(2)
                .build();

        testClient.put().uri("/api/v2/character/backend/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
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

        testClient.post().uri("/api/v2/chat")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void should_create_chat() {
        ChatCreateDTO dto = ChatCreateDTO.builder()
                .backendId(backendId)
                .characterNickname(CHARACTER_NICKNAME)
                .userNickname(USER_NICKNAME)
                .userProfile(USER_PROFILE)
                .characterUid(characterUid)
                .build();

        chatId = testClient.post().uri("/api/v2/chat")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(chatId);
    }

    private void should_send_message() throws ExecutionException, InterruptedException, TimeoutException {
        ChatContentDTO content = ChatContentDTO.fromText("My wife's name is Lily! Did you married? If you had a wife, what's her name?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        LlmResultDTO result = testClient.post().uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": " + result.getMessage().getContents().getFirst().getContent() +
                " (" + result.getTokenUsage() + ")");

        content.setContent("How about the weather today?");

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
    }

    private void should_get_message_usage() {
        MemoryUsageDTO usage = testClient.get().uri("/api/v2/chat/memory/usage/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MemoryUsageDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(usage);
        assertNotNull(usage.getMessageUsage());
        assertThat(usage.getMessageUsage()).isEqualTo(2L);
    }

    private void should_failed_to_send_message_by_quota() {
        ChatContentDTO content = ChatContentDTO.fromText("Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        testClient.post().uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    private void should_resend_message_with_specialized_api_key() {
        ChatUpdateDTO updateDto = ChatUpdateDTO.builder().build();
        updateDto.setApiKeyValue(apiKey());

        testClient.put().uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(Boolean.TRUE);

        ChatContentDTO content = ChatContentDTO.fromText("Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        LlmResultDTO result = testClient.post().uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": " + result.getMessage().getContents().getFirst().getContent() +
                " (" + result.getTokenUsage() + ")");
    }

    private void should_send_message_without_long_term_memory() {
        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        LlmResultDTO result = testClient.post().uri("/api/v2/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertThat(result.getMessage().getContents()).isNotEmpty();
        System.out.println(USER_NICKNAME + ": " + content.getContent());
        System.out.println(CHARACTER_NICKNAME + ": " + result.getMessage().getContents().getFirst().getContent() +
                " (" + result.getTokenUsage() + ")");

        assertFalse(result.getMessage().getContents().getFirst().getContent().contains("Lily"));
    }

    private void should_send_message_without_long_term_memory_in_streaming_mode() throws ExecutionException, InterruptedException, TimeoutException {
        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertFalse(answer.contains("Lily"));
    }

    private void should_send_message_with_long_term_memory_in_streaming_mode() throws Exception {

        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertTrue(answer.contains("Lily"));
    }

    private void should_send_message_and_get_an_image() throws ExecutionException, InterruptedException, TimeoutException {
        String url = testClient.post().uri("/api/v2/character/picture/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_light.png"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(url);
        pictureKey = getKeyFromUrl(url);

        ChatContentDTO content = ChatContentDTO.fromText("Please show me your picture.");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        String shortToken = shortLinkService.shorten("/public/image/" + pictureKey);
        assertThat(answer).contains("![img](" + homeUrl + "/s/" + shortToken);
    }

    private void should_send_message_and_failed_to_get_an_image() throws ExecutionException, InterruptedException, TimeoutException {
        Boolean success = testClient.delete().uri("/api/v2/character/picture/" + pictureKey)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));

        ChatContentDTO content = ChatContentDTO.fromText("Please show me your picture again.");

        ChatMessageDTO dto = ChatMessageDTO.builder()
                .role("user")
                .contents(List.of(content))
                .build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v2/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + ": " + answer);
        assertThat(answer).doesNotContain("![img]");
    }

    private void should_failed_to_list_messages() {
        testClient.get().uri("/api/v2/chat/messages/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void should_list_messages() {
        List<ChatMessageRecordDTO> messages = testClient.get().uri("/api/v2/chat/messages/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChatMessageRecordDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(messages);
        assertThat(messages).isNotEmpty();

        System.out.println("Messages history:");
        messages.stream()
                .map(ChatMessageRecordDTO::getMessage)
                .map(message -> {
                    String role = message.getRole().toUpperCase();
                    String content = CollectionUtils.isNotEmpty(message.getContents()) ?
                            message.getContents().getFirst().getContent() : "<no content>";
                    return "[" + message.getRole().toUpperCase() + "]: " + content;
                })
                .forEach(System.out::println);
    }

    private void should_rollback_messages() {
        testClient.post().uri("/api/v2/chat/messages/rollback/" + chatId + "/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .hasSize(2);
    }

    private void should_failed_to_send_assistant() {
        testClient.get().uri("/api/v2/chat/send/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private void should_send_assistant() {
        TestCharacterUtils.prioritizeCharacter(characterUid);
        waitAWhile();

        LlmResultDTO result = testClient.get().uri("/api/v2/chat/send/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        System.out.println(CHARACTER_NICKNAME + " (Assistant): " + result.getMessage().getContents().getFirst().getContent() +
                " (" + result.getTokenUsage() + ")");
    }

    private void should_send_assistant_in_streaming_mode() throws ExecutionException, InterruptedException, TimeoutException {
        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        testClient.get().uri("/api/v2/chat/send/stream/assistant/" + chatId + "/" + characterUid)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LlmResultDTO.class)
                .getResponseBody()
                .doOnComplete(() -> futureAnswer.complete(answerBuilder.toString()))
                .subscribe(event -> {
                    String text = event.getText();
                    if (text == null) {
                        // last event
                        answerBuilder.append(" (")
                                .append(event.getTokenUsage().toString())
                                .append(")");
                    } else {
                        answerBuilder.append(text);
                    }
                });

        String answer = futureAnswer.get(1, MINUTES);
        System.out.println(CHARACTER_NICKNAME + " (Assistant): " + answer);
    }

    private void should_failed_to_delete_chat() {
        testClient.delete().uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void should_delete_chat() {
        Boolean succeed = testClient.delete().uri("/api/v2/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }


    private void should_delete_character_backend() {
        Boolean succeed = testClient.delete().uri("/api/v2/character/backend/" + backendId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void should_delete_character() {
        List<Long> ids = testClient.delete().uri("/api/v2/character/uid/" + characterUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .returnResult()
                .getResponseBody();

        assertThat(ids).hasSize(2);
    }

    private void should_delete_prompt_task() {
        Boolean succeed = testClient.delete().uri("/api/v2/prompt/task/" + promptTaskId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(succeed);
        assertTrue(succeed);
    }

    private void should_delete_prompt() {
        Boolean succeed = testClient.delete().uri("/api/v2/prompt/" + promptId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
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
    public void should_pass_all_tests() throws Exception {
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

        should_send_message_and_failed_to_get_an_image();
        waitAWhile();

        should_failed_to_list_messages();
        waitAWhile();

        should_list_messages();
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
