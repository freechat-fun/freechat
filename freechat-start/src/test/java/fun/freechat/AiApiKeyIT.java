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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AiApiKeyIT extends AbstractIntegrationTest {
    @Value("${auth.aiApiKey.limits:#{null}}")
    private Integer maxCount;
    private String userId;
    private String apiToken;
    private LinkedList<AiApiKeyCreateDTO> apiKeys;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(AiApiKeyIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        apiKeys = new LinkedList<>();
        for (int i = 0; i < maxCount * 2; ++i) {
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
    public void should_pass_all_tests() {
        List<Long> ids = new ArrayList<>(maxCount);
        for (int i = 0; i < apiKeys.size() - 1; ++i) {
            testClient.post().uri("/api/v2/ai/apikey")
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

        testClient.post().uri("/api/v2/ai/apikey")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue(apiKeys.getLast())
                .exchange()
                .expectStatus().is4xxClientError();

        testClient.get().uri("/api/v2/ai/apikeys/dash_scope")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(3);

        for (int i = 0; i < maxCount; ++i) {
            testClient.get().uri("/api/v2/ai/apikey/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                        .jsonPath("$.name").isEqualTo("test_api_key_" + i)
                        .jsonPath("$.token").value(token -> assertThat(token.toString()).contains("*"));
        }

        testClient.get().uri("/api/v2/ai/apikey/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isForbidden();

        for (int i = 0; i < maxCount; ++i) {
            testClient.put().uri("/api/v2/ai/apikey/disable/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }

        for (int i = 0; i < maxCount; ++i) {
            testClient.put().uri("/api/v2/ai/apikey/enable/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }

        for (int i = 0; i < maxCount; ++i) {
            testClient.delete().uri("/api/v2/ai/apikey/" + ids.get(i))
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + apiToken)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Boolean.class).isEqualTo(true);
        }
    }
}
