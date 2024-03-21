package fun.freechat;

import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCharacterUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestImageUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class ImageIT extends AbstractIntegrationTest {
    private String ownerId;
    private String otherId;
    private String ownerToken;
    private String otherToken;
    private Long characterId;

    @BeforeEach
    public void setUp() {
        Pair<String, String> ownerAndToken = TestAccountUtils.createUserAndToken(
                ImageIT.class.getName() + "-1");
        ownerId = ownerAndToken.getLeft();
        ownerToken = ownerAndToken.getRight();

        Pair<String, String> otherAndToken = TestAccountUtils.createUserAndToken(
                ImageIT.class.getName() + "-2");
        otherId = otherAndToken.getLeft();
        otherToken = otherAndToken.getRight();

        characterId = TestCharacterUtils.createCharacter(ownerId);
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestCharacterUtils.deleteCharacters(ownerId);
        TestAccountUtils.deleteUserAndToken(ownerId);
        TestAccountUtils.deleteUserAndToken(otherId);
    }

    @Test
    public void testCharacterPictures() {
        String url1 = testClient.post().uri("/api/v1/character/picture/" + characterId)
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

        String url2 = testClient.post().uri("/api/v1/character/picture/" + characterId)
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

        testClient.post().uri("/api/v1/character/picture/" + characterId)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(bodyFrom("/freechat_light.png"))
                .exchange()
                .expectStatus().isForbidden();

        List<String> urls = testClient.get().uri("/api/v1/character/pictures/" + characterId)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(urls).hasSize(2).contains(url1, url2);

        testClient.get().uri("/api/v1/character/pictures/" + characterId)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        String key1 = getImageKey(url1);
        String key2 = getImageKey(url2);

        testClient.delete().uri("/api/v1/character/picture/" + key1)
                .header(AUTHORIZATION, "Bearer " + otherToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        Boolean success = testClient.delete().uri("/api/v1/character/picture/" + key1)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));

        success = testClient.delete().uri("/api/v1/character/picture/" + key2)
                .header(AUTHORIZATION, "Bearer " + ownerToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .returnResult()
                .getResponseBody();

        assertTrue(BooleanUtils.isTrue(success));
    }

    private MultiValueMap<String, HttpEntity<?>> bodyFrom(String imagePath) {
        Path path = Path.of(imagePath);
        String filename = path.getFileName().toString();

        final byte[] fileContent = TestImageUtils.imageData(imagePath);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileContent).filename(filename);

        return builder.build();
    }

    private String getImageKey(String url) {
        int lastSlashIndex = url.lastIndexOf('/');

        assertTrue(lastSlashIndex > 0);
        assertTrue(lastSlashIndex < url.length() - 1);

        return url.substring(lastSlashIndex + 1);
    }
}
