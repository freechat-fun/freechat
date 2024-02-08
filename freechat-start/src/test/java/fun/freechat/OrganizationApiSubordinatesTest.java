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

public class OrganizationApiSubordinatesTest extends AbstractIntegrationTest {
    private String userId;

    private String apiToken;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(OrganizationApiSubordinatesTest.class.getName());
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
    public void testGetSolidSubordinates() {
        testClient.get().uri("/api/v1/org/subordinates")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(3);
    }

    @Test
    public void testGetAllSubordinates() {
        testClient.get().uri("/api/v1/org/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    public void testGetSubordinateAllSubordinates() {
        testClient.get().uri("/api/v1/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);
    }

    @Test
    public void testUpdateSubordinateSubordinates() {
        testClient.get().uri("/api/v1/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(5);

        testClient.put().uri("/api/v1/org/manage/41/subordinates")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .bodyValue("[\"40\"]")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        testClient.get().uri("/api/v1/org/manage/41/subordinates?all=1")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.length()").isEqualTo(2);

    }
}
