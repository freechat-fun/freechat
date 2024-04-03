package fun.freechat;

import fun.freechat.api.dto.*;
import fun.freechat.service.enums.GenderType;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.Visibility;
import fun.freechat.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static fun.freechat.util.TestChatUtils.modelParams;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class ChatIT extends AbstractIntegrationTest{
    private static final String MODEL_ID = "[open_ai]gpt-4";
    private static final String API_KEY_NAME = "test_api_key_open_ai";
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

        TestAiApiKeyUtils.addAiApiKey(developerId, API_KEY_NAME,
                ModelProvider.OPEN_AI, TestAiApiKeyUtils.apiKeyOfOpenAI(), true);
    }

    private void createUser() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(ChatIT.class.getName());
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
        dto.setApiKeyName(API_KEY_NAME);
        dto.setModelId(MODEL_ID);
        dto.setParams(modelParams(MODEL_ID));
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
        dto.setMessageWindowSize(50);

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
        ChatContentDTO content = ChatContentDTO.fromText("Did you married? If you had a wife, what's her name?");

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

    private void testStreamSendMessage() throws Exception {
        ChatContentDTO content = ChatContentDTO.fromText("OK. Nice to meet you. Bye!");

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
                    if (Objects.isNull(text)) {
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

        testStreamSendMessage();
        TestCommonUtils.waitAWhile();

        testListMessagesFailed();
        TestCommonUtils.waitAWhile();

        testListMessages();
        TestCommonUtils.waitAWhile();

        testRollbackMessages();
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
