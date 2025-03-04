package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Disabled
class OrganizationApiSubordinatesIT extends AbstractIntegrationTest {
    private String userId;
    private String apiToken;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(OrganizationApiSubordinatesIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        TestOrgUtils.addSubordinates(userId);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestOrgUtils.cleanTestTree();
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    void should_get_solid_subordinates() {
        testClient.get().uri("/api/v2/org/subordinates")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(3);
    }

    @Test
    void should_get_all_subordinates() {
        testClient.get().uri("/api/v2/org/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    void should_get_subordinate_all_subordinates() {
        testClient.get().uri("/api/v2/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    void should_update_subordinate_subordinates() {
        testClient.get().uri("/api/v2/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);

        testClient.put().uri("/api/v2/org/manage/41/subordinates")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue("[\"40\"]")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        testClient.get().uri("/api/v2/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(2);

    }
}
