package fun.freechat;

import fun.freechat.api.dto.*;
import fun.freechat.service.enums.*;
import fun.freechat.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.apiKeyFor;
import static fun.freechat.util.TestAiApiKeyUtils.keyNameFor;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static fun.freechat.util.TestCommonUtils.parametersFor;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatIT extends AbstractIntegrationTest{
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
            
            [[[Some information you may need to know about this conversation]]]
            {{{CHAT_CONTEXT}}}
            {{{MESSAGE_CONTEXT}}}
            {{{RELEVANT_INFORMATION}}}
            """;

    private String developerId;
    private String developerApiKey;
    private String userId;
    private String userApiKey;
    private Long promptId;
    private String promptTaskId;
    private Long characterId;
    private String backendId;
    private String chatId;

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
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestChatUtils.deleteChats(userId);
        TestCharacterUtils.deleteCharacters(developerId);
        TestPromptUtils.deletePrompts(developerId);
        TestCommonUtils.waitAWhile();
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
    private void testCreatePrompt() {
        ChatPromptContentDTO prompt = new ChatPromptContentDTO();
        prompt.setSystem(SYSTEM_PROMPT);

        PromptCreateDTO dto = new PromptCreateDTO();
        dto.setName("test_character_prompt");
        dto.setFormat(PromptFormat.MUSTACHE.text());
        dto.setLang("en");
        dto.setVisibility(Visibility.PUBLIC.text());
        dto.setChatTemplate(prompt);

        promptId = testClient.post().uri("/api/v1/prompt")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(promptId);

    }

    private void testCreatePromptTask() {
        PromptRefDTO promptRef = new PromptRefDTO();
        promptRef.setPromptId(promptId);

        PromptTaskDTO dto = new PromptTaskDTO();
        dto.setApiKeyName(apiKeyName());
        dto.setModelId(modelId());
        dto.setParams(parametersFor(modelId()));
        dto.setPromptRef(promptRef);

        promptTaskId = testClient.post().uri("/api/v1/prompt/task")
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

    private void testCreateCharacter() {
        CharacterCreateDTO dto = new CharacterCreateDTO();
        dto.setName(CHARACTER_NICKNAME + "-bot");
        dto.setGender(CHARACTER_GENDER);
        dto.setProfile(CHARACTER_PROFILE);
        dto.setChatStyle(CHARACTER_CHAT_STYLE);
        dto.setChatExample(CHARACTER_CHAT_EXAMPLE);
        dto.setGreeting(CHARACTER_GREETING);
        dto.setVisibility(Visibility.PRIVATE.text());

        characterId = testClient.post().uri("/api/v1/character")
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(characterId);
    }

    private void testPublishCharacter() {
        characterId = testClient.post().uri("/api/v1/character/publish/" + characterId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(characterId);
    }

    private void testCreateBackend() {
        CharacterBackendDTO dto = new CharacterBackendDTO();
        dto.setChatPromptTaskId(promptTaskId);
        dto.setIsDefault(true);
        dto.setMessageWindowSize(5);
        dto.setLongTermMemoryWindowSize(0);
        dto.setInitQuota(2L);
        dto.setQuotaType(QuotaType.MESSAGES.text());

        backendId = testClient.post().uri("/api/v1/character/backend/" + characterId)
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

    private void testUpdateBackend() {
        CharacterBackendDTO dto = new CharacterBackendDTO();
        dto.setLongTermMemoryWindowSize(2);

        testClient.put().uri("/api/v1/character/backend/" + backendId)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    private void testCreateChatFailed() {
        ChatCreateDTO dto = new ChatCreateDTO();
        dto.setBackendId(backendId);
        dto.setCharacterNickname(CHARACTER_NICKNAME);
        dto.setUserNickname(USER_NICKNAME);
        dto.setUserProfile(USER_PROFILE);
        dto.setCharacterId(characterId);

        testClient.post().uri("/api/v1/chat")
                .accept(MediaType.TEXT_PLAIN)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void testCreateChat() {
        ChatCreateDTO dto = new ChatCreateDTO();
        dto.setBackendId(backendId);
        dto.setCharacterNickname(CHARACTER_NICKNAME);
        dto.setUserNickname(USER_NICKNAME);
        dto.setUserProfile(USER_PROFILE);
        dto.setCharacterId(characterId);

        chatId = testClient.post().uri("/api/v1/chat")
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

    private void testSendMessage() {
        ChatContentDTO content = ChatContentDTO.fromText("My wife's name is Lily! Did you married? If you had a wife, what's her name?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        LlmResultDTO result = testClient.post().uri("/api/v1/chat/send/" + chatId)
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

        result = testClient.post().uri("/api/v1/chat/send/" + chatId)
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

    private void testMemoryUsage() {
        MemoryUsageDTO usage = testClient.get().uri("/api/v1/chat/memory/usage/" + chatId)
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

    private void testSendMessageFailedByQuota() {
        ChatContentDTO content = ChatContentDTO.fromText("Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        testClient.post().uri("/api/v1/chat/send/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        testClient.post().uri("/api/v1/chat/send/stream/" + chatId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    private void testResendMessageWithSpecializedApiKey() {
        ChatUpdateDTO updateDto = new ChatUpdateDTO();
        updateDto.setApiKeyValue(apiKey());

        testClient.put().uri("/api/v1/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(Boolean.TRUE);

        ChatContentDTO content = ChatContentDTO.fromText("Sorry, I forgot your name. what's it?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        LlmResultDTO result = testClient.post().uri("/api/v1/chat/send/" + chatId)
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

    private void testSendMessageWithoutLongTermMemory() {
        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        LlmResultDTO result = testClient.post().uri("/api/v1/chat/send/" + chatId)
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

    private void testStreamSendMessageWithoutLongTermMemory() throws ExecutionException, InterruptedException, TimeoutException {
        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v1/chat/send/stream/" + chatId)
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

    private void testStreamSendMessageWithLongTermMemory() throws Exception {

        ChatContentDTO content = ChatContentDTO.fromText("Do you remember my wife's name?");

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        System.out.println(USER_NICKNAME + ": " + content.getContent());
        testClient.post().uri("/api/v1/chat/send/stream/" + chatId)
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

    private void testListMessagesFailed() {
        testClient.get().uri("/api/v1/chat/messages/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void testListMessages() {
        List<ChatMessageRecordDTO> messages = testClient.get().uri("/api/v1/chat/messages/" + chatId)
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
                .map(message ->
                    "[" + message.getRole().toUpperCase() + "]: " + message.getContents().getFirst().getContent())
                .forEach(System.out::println);
    }

    private void testRollbackMessages() {
        testClient.post().uri("/api/v1/chat/messages/rollback/" + chatId + "/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .hasSize(2);
    }

    private void testDeleteChatFailed() {
        testClient.delete().uri("/api/v1/chat/" + chatId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + developerApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }

    private void testDeleteChat() {
        Boolean succeed = testClient.delete().uri("/api/v1/chat/" + chatId)
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


    private void testDeleteBackend() {
        Boolean succeed = testClient.delete().uri("/api/v1/character/backend/" + backendId)
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

    private void testDeleteCharacter() {
        Boolean succeed = testClient.delete().uri("/api/v1/character/" + characterId)
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

    private void testDeletePromptTask() {
        Boolean succeed = testClient.delete().uri("/api/v1/prompt/task/" + promptTaskId)
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

    private void testDeletePrompt() {
        Boolean succeed = testClient.delete().uri("/api/v1/prompt/" + promptId)
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
    public void testAll() throws Exception {
        testCreatePrompt();
        TestCommonUtils.waitAWhile();

        testCreatePromptTask();
        TestCommonUtils.waitAWhile();

        testCreateCharacter();
        TestCommonUtils.waitAWhile();

        testCreateBackend();
        TestCommonUtils.waitAWhile();

        testCreateChatFailed();
        TestCommonUtils.waitAWhile();

        testPublishCharacter();
        TestCommonUtils.waitAWhile();

        testCreateChat();
        TestCommonUtils.waitAWhile();

        testSendMessage();
        TestCommonUtils.waitAWhile();

        testMemoryUsage();
        TestCommonUtils.waitAWhile();

        testSendMessageFailedByQuota();
        TestCommonUtils.waitAWhile();

        testResendMessageWithSpecializedApiKey();
        TestCommonUtils.waitAWhile();

        testSendMessageWithoutLongTermMemory();
        TestCommonUtils.waitAWhile();

        testRollbackMessages();
        TestCommonUtils.waitAWhile();

        testStreamSendMessageWithoutLongTermMemory();
        TestCommonUtils.waitAWhile();

        testRollbackMessages();
        TestCommonUtils.waitAWhile();

        testUpdateBackend();
        TestCommonUtils.waitAWhile();

        testStreamSendMessageWithLongTermMemory();
        TestCommonUtils.waitAWhile();

        testListMessagesFailed();
        TestCommonUtils.waitAWhile();

        testListMessages();
        TestCommonUtils.waitAWhile();

        testDeleteChatFailed();
        TestCommonUtils.waitAWhile();

        testDeleteChat();
        TestCommonUtils.waitAWhile();

        testDeleteBackend();
        TestCommonUtils.waitAWhile();

        testDeleteCharacter();
        TestCommonUtils.waitAWhile();

        testDeletePromptTask();
        TestCommonUtils.waitAWhile();

        testDeletePrompt();
        TestCommonUtils.waitAWhile();
    }
}
