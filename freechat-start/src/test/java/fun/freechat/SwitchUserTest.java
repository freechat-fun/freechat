package fun.freechat;

import fun.freechat.util.AuthorityUtils;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class SwitchUserTest extends AbstractIntegrationTest {
    @Value("${auth.impersonate.headerName}")
    private String impersonateHeaderName;

    private String adminApiToken;

    private String orgApiToken;

    private String usernameOf(String sn) {
        return SwitchUserTest.class.getName() + "-" + sn;
    }

    @BeforeEach
    public void setUp() {
        Pair<String, String> adminAndToken = TestAccountUtils.createUserAndToken(
                usernameOf("11"), Set.of(AuthorityUtils.bizRole()));
        Pair<String, String> orgAndToken = TestAccountUtils.createUserAndToken(
                usernameOf("21"), Set.of(AuthorityUtils.orgRole()));
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(usernameOf("31"));
        adminApiToken = adminAndToken.getRight();
        orgApiToken = orgAndToken.getRight();
        TestOrgUtils.addOwners(userAndToken.getLeft());
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestOrgUtils.cleanTestTree();
        TestAccountUtils.deleteUserAndTokenByUsername(usernameOf("51"));
        TestAccountUtils.deleteUserAndTokenByUsername(usernameOf("31"));
        TestAccountUtils.deleteUserAndTokenByUsername(usernameOf("21"));
        TestAccountUtils.deleteUserAndTokenByUsername(usernameOf("11"));
        TestCommonUtils.waitAWhile();
    }

    @Test
    public void testSwitchUserSuccessfully() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + adminApiToken)
                .header(impersonateHeaderName, usernameOf("31"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.username").isEqualTo(usernameOf("31"));
    }

    @Test
    public void testSwitchUserAutoRegisterSuccessfully() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + adminApiToken)
                .header(impersonateHeaderName, usernameOf("51"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .jsonPath("$.username").isEqualTo(usernameOf("51"));
    }

    @Test
    public void testSwitchUserFailure() {
        testClient.get().uri("/api/v1/account/details")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + orgApiToken)
                .header(impersonateHeaderName, usernameOf("31"))
                .exchange()
                .expectStatus().isForbidden();
    }
}
