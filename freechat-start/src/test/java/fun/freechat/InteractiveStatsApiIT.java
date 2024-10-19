package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestPromptUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class InteractiveStatsApiIT extends AbstractIntegrationTest {
    private String userId;
    private String apiToken;
    private Long promptId;
    private String promptUid;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(InteractiveStatsApiIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        promptId = TestPromptUtils.createPrompt(userId, "Hello integration-test", null);
        promptUid = TestPromptUtils.idToUid(promptId);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompt(userId, promptId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    public void testAll() {
        testClient.post().uri("/api/v2/stats/prompt/"+ promptUid + "/score/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(5L);

        testClient.post().uri("/api/v2/stats/prompt/"+ promptUid + "/refer_count")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(1L);

        testClient.post().uri("/api/v2/stats/prompt/"+ promptUid + "/score/3")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v2/public/stats/prompt/"+ promptUid + "/score")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v2/public/score/prompt/" + promptUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v2/public/stats/prompt/"+ promptUid)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.referType").isEqualTo("prompt")
                    .jsonPath("$.referId").isEqualTo(promptUid)
                    .jsonPath("$.referCount").isEqualTo(1L)
                    .jsonPath("$.scoreCount").isEqualTo(1L)
                    .jsonPath("$.score").isEqualTo(3L);

        String jsonPathPrefix = "$[?(@.promptId=='" + promptId + "')]";

        testClient.get().uri("/api/v2/public/stats/prompts/by/score/10/0")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath(jsonPathPrefix + ".name").isEqualTo("Integration-test Prompt for " + userId)
                    .jsonPath(jsonPathPrefix + ".referCount").isEqualTo(1)
                    .jsonPath(jsonPathPrefix + ".scoreCount").isEqualTo(1)
                    .jsonPath(jsonPathPrefix + ".score").isEqualTo(3);

    }
}
