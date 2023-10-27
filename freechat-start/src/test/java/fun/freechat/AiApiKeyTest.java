package fun.freechat;

import fun.freechat.api.dto.AiApiKeyCreateDTO;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestAiApiKeyUtils;
import fun.freechat.util.TestCommonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@SuppressWarnings("unused")
public class AiApiKeyTest {
    @Value("${auth.aiApiKey.limits:#{null}}")
    private Integer maxCount;

    @Autowired
    private WebTestClient testClient;

    private String userId;

    private String apiToken;

    private List<AiApiKeyCreateDTO> apiKeys;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken("31");
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        apiKeys = new ArrayList<>(maxCount + 1);
        for (int i = 0; i < maxCount + 1; ++i) {
            AiApiKeyCreateDTO apiKey = new AiApiKeyCreateDTO();
            apiKey.setName("test_api_key_" + i);
            apiKey.setProvider(ModelProvider.DASH_SCOPE.text());
            apiKey.setToken("FakeToken" + i);
            apiKey.setEnabled(i % 2 == 0);
            apiKeys.add(apiKey);
        }
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestAiApiKeyUtils.cleanAiApiKeys(userId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    public void testAll() {
        List<Long> ids = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; ++i) {
            testClient.post().uri("/api/v1/ai/apikey")
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .bodyValue(apiKeys.get(i))
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Long.class).value(id -> {
                        assertThat(id).isGreaterThan(0L);
                        ids.add(id);
                    });
        }

        testClient.post().uri("/api/v1/ai/apikey")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(apiKeys.get(maxCount))
                .exchange()
                .expectStatus().is4xxClientError();

        testClient.get().uri("/api/v1/ai/apikeys/dash_scope")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(3);

        for (int i = 0; i < maxCount; ++i) {
            testClient.get().uri("/api/v1/ai/apikey/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                        .jsonPath("$.name").isEqualTo("test_api_key_" + i)
                        .jsonPath("$.token").value(token -> assertThat(token.toString()).contains("*"));
        }

        testClient.get().uri("/api/v1/ai/apikey/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isForbidden();

        for (int i = 0; i < maxCount; ++i) {
            testClient.put().uri("/api/v1/ai/apikey/disable/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }

        for (int i = 0; i < maxCount; ++i) {
            testClient.put().uri("/api/v1/ai/apikey/enable/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }

        for (int i = 0; i < maxCount; ++i) {
            testClient.delete().uri("/api/v1/ai/apikey/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }
    }
}
