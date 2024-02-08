package fun.freechat;

import fun.freechat.api.dto.HotTagDTO;
import fun.freechat.util.TestAccountUtils;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestPromptUtils;
import fun.freechat.util.TestTagUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class TagTest extends AbstractIntegrationTest {
    private String userId;

    private String apiToken;

    private String promptId1;

    private String promptId2;

    @BeforeEach
    public void setUp() {
        Pair<String, String> userAndToken = TestAccountUtils.createUserAndToken(TagTest.class.getName());
        userId = userAndToken.getLeft();
        apiToken = userAndToken.getRight();
        promptId1 = TestPromptUtils.createPrompt(userId, "Hello unit-test 1", null);
        promptId2 = TestPromptUtils.createPrompt(userId, "Hello unit-test 2", null);
        TestTagUtils.addTag(userId, promptId1, "test-tag-1");
        TestTagUtils.addTag(userId, promptId1, "test-tag-2");
        TestTagUtils.addTag(userId, promptId2, "test-tag-1");
        TestCommonUtils.waitAWhile();
    }

    @AfterEach
    public void cleanUp() {
        TestPromptUtils.deletePrompt(userId, promptId1);
        TestPromptUtils.deletePrompt(userId, promptId2);
        TestTagUtils.cleanTags(userId);
        TestAccountUtils.deleteUserAndToken(userId);
    }

    @Test
    public void testHotTags() {
        testClient.get().uri("/api/v1/tags/hot/prompt/10")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + apiToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(HotTagDTO.class)
                .value(hotTags -> assertTrue(hotTags.stream().anyMatch(
                        tag -> tag.getContent().equals("test-tag-1") && tag.getCount() == 2L)));
    }
}
