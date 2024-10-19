package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCharacterUtils;
import fun.freechat.util.TestCommonUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.util.List;

import static fun.freechat.api.util.FileUtils.getKeyFromUrl;
import static fun.freechat.util.TestResourceUtils.bodyFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CharacterIT extends AbstractIntegrationTest {
    private String ownerId;
    private String otherId;
    private String ownerToken;
    private String otherToken;
    private String characterUid;

    @BeforeEach
    public void setUp() {
        Pair<String, String> ownerAndToken = TestAccountUtils.createUserAndToken(
                CharacterIT.class.getName() + "-1");
        ownerId = ownerAndToken.getLeft();
        ownerToken = ownerAndToken.getRight();

        Pair<String, String> otherAndToken = TestAccountUtils.createUserAndToken(
                CharacterIT.class.getName() + "-2");
        otherId = otherAndToken.getLeft();
        otherToken = otherAndToken.getRight();

        Long characterId = TestCharacterUtils.createCharacter(ownerId);
        TestCommonUtils.waitAWhile();
        characterUid = TestCharacterUtils.idToUid(characterId);
    }

    @AfterEach
    public void cleanUp() {
        TestCharacterUtils.deleteCharacters(ownerId);
        TestAccountUtils.deleteUserAndToken(ownerId);
        TestAccountUtils.deleteUserAndToken(otherId);
    }

    @Test
    public void testCharacterPictures() {
        String url1 = testClient.post().uri("/api/v2/character/picture/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_light.png"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(url1));

        String url2 = testClient.post().uri("/api/v2/character/picture/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_dark.png"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(url2));

        testClient.post().uri("/api/v2/character/picture/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_light.png"))
                .exchange()
                .expectStatus().isForbidden();

        List<String> urls = testClient.get().uri("/api/v2/character/pictures/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(urls).hasSize(2).contains(url1, url2);

        testClient.get().uri("/api/v2/character/pictures/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        String key1 = getKeyFromUrl(url1);
        String key2 = getKeyFromUrl(url2);

        testClient.delete().uri("/api/v2/character/picture/" + key1)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        Boolean success = testClient.delete().uri("/api/v2/character/picture/" + key1)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));

        success = testClient.delete().uri("/api/v2/character/picture/" + key2)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));
    }

    @Test
    public void testCharacterDocuments() {
        String url1 = testClient.post().uri("/api/v2/character/document/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/miles-of-smiles-terms-of-use.txt"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(url1));

        String url2 = testClient.post().uri("/api/v2/character/document/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/story-about-happy-carrot.txt"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertTrue(StringUtils.isNotBlank(url2));

        testClient.post().uri("/api/v2/character/document/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/miles-of-smiles-terms-of-use.txt"))
                .exchange()
                .expectStatus().isForbidden();

        List<String> urls = testClient.get().uri("/api/v2/character/documents/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(urls).hasSize(2).contains(url1, url2);

        testClient.get().uri("/api/v2/character/documents/" + characterUid)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        String key1 = getKeyFromUrl(url1);
        String key2 = getKeyFromUrl(url2);

        testClient.delete().uri("/api/v2/character/document/" + key1)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        Boolean success = testClient.delete().uri("/api/v2/character/document/" + key1)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));

        success = testClient.delete().uri("/api/v2/character/document/" + key2)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));
    }
}
