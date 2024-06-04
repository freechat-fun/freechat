package fun.freechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.dto.*;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.SourceType;
import fun.freechat.service.enums.TaskStatus;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.util.*;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static fun.freechat.service.enums.ModelProvider.OPEN_AI;
import static fun.freechat.util.TestAiApiKeyUtils.apiKeyFor;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static fun.freechat.util.TestResourceUtils.bodyFrom;
import static fun.freechat.util.TestResourceUtils.getResourceKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiRagIT extends AbstractIntegrationTest {
    private String userId;
    private String userToken;
    private Long characterId;
    private Long taskId;
    private boolean isFile;
    private String url;
    private Long promptId;

    private static ModelProvider modelProvider() {
        return OPEN_AI;
    }

    private static String modelId() {
        ModelProvider provider = modelProvider();
        return "[" + provider.text() + "]" + defaultModelFor(provider);
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        Pair<String, String> ownerAndToken = TestAccountUtils.createUserAndToken(OpenAiRagIT.class.getName());
        userId = ownerAndToken.getLeft();
        userToken = ownerAndToken.getRight();

        ChatPromptContent promptContent = new ChatPromptContent();
        promptContent.setSystem(TestChatUtils.DEFAULT_SYSTEM_PROMPT);
        promptId = TestPromptUtils.createChatPrompt(userId, promptContent);

        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompts(userId);
        TestCharacterUtils.deleteCharacters(userId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    private void setUpCharacterForLang(String lang) {
        characterId = TestCharacterUtils.createCharacter(userId, lang);
    }

    private void testUploadDocument(String doc) {
        if (doc.startsWith("/public/test/")) {
            url = getLocalUrl(doc);
            isFile = false;
            return;
        }

        url = testClient.post().uri("/api/v1/character/document/" + characterId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/" + doc))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(url));
        isFile = true;
    }

    private void testDeleteDocument() {
        if (!isFile) {
            return;
        }

        Boolean success = testClient.delete().uri("/api/v1/character/document/" + getResourceKey(url))
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));
    }

    private void testCreateTask() {
        RagTaskDTO fileRequest = new RagTaskDTO();
        if (isFile) {
            fileRequest.setSource(getResourceKey(url));
            fileRequest.setSourceType(SourceType.FILE.text());
        } else {
            fileRequest.setSource(url);
            fileRequest.setSourceType(SourceType.URL.text());
        }

        taskId = testClient.post().uri("/api/v1/rag/task/" + characterId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(fileRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        assertThat(taskId).isPositive();
    }

    private void testDeleteTask() {
        Boolean result;
        result = testClient.delete().uri("/api/v1/rag/task/" + taskId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(result));
    }

    private void testQueryTask() {
        RagTaskDetailsDTO task1 = testClient.get().uri("/api/v1/rag/task/" + taskId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RagTaskDetailsDTO.class)
                .returnResult()
                .getResponseBody();

        System.out.println(task1);
        assertNotNull(task1);

        List<RagTaskDetailsDTO> tasks;
        tasks = testClient.get().uri("/api/v1/rag/task/" + characterId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RagTaskDetailsDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(tasks).hasSize(1);
    }

    private void testRunTask() throws InterruptedException {
        Boolean result;
        TaskStatus latestStatus = TaskStatus.PENDING;

        result = testClient.post().uri("/api/v1/rag/task/start/" + taskId)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(result));

        for (int i = 0; i < 30; ++i) {
            String status = testClient.get().uri("/api/v1/rag/task/status/" + taskId)
                    .header(AUTHORIZATION, "Bearer " + userToken)
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();

            System.out.printf("[%d]: Task[%d] %s%n", (i + 1), taskId, status);
            latestStatus = TaskStatus.of(status);
            if (latestStatus == TaskStatus.SUCCEEDED || latestStatus == TaskStatus.FAILED || latestStatus == TaskStatus.CANCELED) {
                break;
            }
            Thread.sleep(1000);
        }

        assertSame(TaskStatus.SUCCEEDED, latestStatus);
    }

    private void testChat(String modelId, String question, List<String> expected) throws JsonProcessingException {
        String promptTaskId = TestPromptUtils.createChatPromptTask(promptId, modelId, apiKeyFor(modelId));
        String backend1 = TestCharacterUtils.createCharacterBackend(characterId, promptTaskId);
        String chatId1 = TestChatUtils.createChat(userId, backend1);

        ChatContentDTO content = ChatContentDTO.fromText(question);

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContents(List.of(content));
        dto.setRole("user");

        LlmResultDTO result = testClient.post().uri("/api/v1/chat/send/" + chatId1)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + userToken)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LlmResultDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertNotNull(result.getMessage());
        String answer = result.getMessage().getContents().getFirst().getContent();
        System.out.println("Question: " + content.getContent());
        System.out.println("Answer[" + characterId + "]: " + answer + " (" + result.getTokenUsage() + ")");
        assertThat(answer).containsAnyOf(expected.toArray(new String[0]));
    }

    @ParameterizedTest
    @MethodSource
    public void testAll(String modelId, String doc, String question, String lang, List<String> expected) throws InterruptedException, JsonProcessingException {
        setUpCharacterForLang(lang);

        testUploadDocument(doc);
        testCreateTask();
        testQueryTask();
        testRunTask();

        testChat(modelId, question, expected);

        testDeleteTask();
        testDeleteDocument();
    }

    public static Stream<Arguments> testAll() {
        return Stream.of(
                Arguments.of(modelId(),
                        "miles-of-smiles-terms-of-use.txt",
                        "How many days before the rental can I cancel my booking?",
                        "en",
                        List.of("17", "61")),

                Arguments.of(modelId(),
                        "story-about-happy-carrot.txt",
                        "Who is Charlie?",
                        "en",
                        List.of("carrot")),

                Arguments.of(modelId(),
                        "/public/test/info/request?doc=Once%20upon%20a%20time%20in%20the%20garden%20of%20FlowerField%20there%20bloomed%20a%20gentle%20sunflower%20named%20Lily",
                        "Who is Lily?",
                        "en",
                        List.of("sunflower")),

                Arguments.of(modelId(),
                        "快乐胡萝卜的故事.txt",
                        "查理是谁？",
                        "zh",
                        List.of(""))
        );
    }
}
