package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class OrganizationApiOwnersIT extends AbstractIntegrationTest {
    private String userId;

    private String apiToken;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(OrganizationApiOwnersIT.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        TestOrgUtils.addOwners(userId);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestOrgUtils.cleanTestTree();
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    public void testGetSolidOwners() {
        testClient.get().uri("/api/v1/org/owners")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(4);
    }

    @Test
    public void testGetAllOwners() {
        testClient.get().uri("/api/v1/org/manage/41/owners")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    public void testGetSubordinateSolidOwners() {
        testClient.get().uri("/api/v1/org/manage/41/owners")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    public void testUpdateSubordinateOwners() {
        testClient.get().uri("/api/v1/org/manage/40/owners")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);

        testClient.put().uri("/api/v1/org/manage/40/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue("[\"" + userId + "\", \"41\"]")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        testClient.get().uri("/api/v1/org/manage/40/owners")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(6);

    }
}
