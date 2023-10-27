package fun.freechat;

import fun.freechat.util.AuthorityUtils;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
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

import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@SuppressWarnings("unused")
public class SwitchUserTest {
    @Value("${auth.impersonate.headerName}")
    private String impersonateHeaderName;

    @Autowired
    private WebTestClient testClient;

    private String adminApiToken;

    private String orgApiToken;

    @BeforeEach
    public void setUp() {
        Pair<String, String> adminAndToken = TestAccountUtils.createUserAndToken(
                "11", Set.of(AuthorityUtils.bizRole()));
        Pair<String, String> orgAndToken = TestAccountUtils.createUserAndToken(
                "21", Set.of(AuthorityUtils.orgRole()));
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken("31");
        adminApiToken = adminAndToken.getRight();
        orgApiToken = orgAndToken.getRight();
        TestOrgUtils.addOwners(userAndToken.getLeft());
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestOrgUtils.cleanTestTree();
        TestAccountUtils.deleteUserAndTokenByUsername("51");
        TestAccountUtils.deleteUserAndTokenByUsername("31");
        TestAccountUtils.deleteUserAndTokenByUsername("21");
        TestAccountUtils.deleteUserAndTokenByUsername("11");
    }

    @Test
    public void testSwitchUserSuccessfully() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + adminApiToken)
                .header(impersonateHeaderName, "31")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.username").isEqualTo("31");
    }

    @Test
    public void testSwitchUserAutoRegisterSuccessfully() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + adminApiToken)
                .header(impersonateHeaderName, "51")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.username").isEqualTo("51");
    }

    @Test
    public void testSwitchUserFailure() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + orgApiToken)
                .header(impersonateHeaderName, "31")
                .exchange()
                .expectStatus().isForbidden();
    }
}
