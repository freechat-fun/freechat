package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestPromptUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@SuppressWarnings("unused")
public class InteractiveStatsApiTest {
    @Autowired
    private WebTestClient testClient;

    private String userId;

    private String apiToken;

    private String promptId;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken("31");
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        promptId = TestPromptUtils.createPrompt(userId, "Hello unit-test", null);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompt(userId, promptId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    public void testAll() {
        testClient.post().uri("/api/v1/stats/prompt/"+ promptId + "/score/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(5L);

        testClient.post().uri("/api/v1/stats/prompt/"+ promptId + "/refer_count")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(1L);

        testClient.post().uri("/api/v1/stats/prompt/"+ promptId + "/score/3")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v1/stats/prompt/"+ promptId + "/score")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v1/score/prompt/" + promptId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class).isEqualTo(3L);

        testClient.get().uri("/api/v1/stats/prompt/"+ promptId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.referType").isEqualTo("prompt")
                    .jsonPath("$.referId").isEqualTo(promptId)
                    .jsonPath("$.referCount").isEqualTo(1L)
                    .jsonPath("$.scoreCount").isEqualTo(1L)
                    .jsonPath("$.score").isEqualTo(3L);

        String jsonPathPrefix = "$[?(@.promptId=='" + promptId + "')]";

        testClient.get().uri("/api/v1//stats/prompts/by/score/10/0")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath(jsonPathPrefix + ".name").isEqualTo("Unit-test Prompt for " + userId)
                    .jsonPath(jsonPathPrefix + ".referCount").isEqualTo(1)
                    .jsonPath(jsonPathPrefix + ".scoreCount").isEqualTo(1)
                    .jsonPath(jsonPathPrefix + ".score").isEqualTo(3);

    }
}
