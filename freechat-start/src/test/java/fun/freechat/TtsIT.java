package fun.freechat;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class TtsIT extends AbstractIntegrationTest {
    @Test
    public void should_list_inner_speakers() {
        testClient.get().uri("/api/v2/public/tts/speakers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(String.class)
                .hasSize(55);
    }
}
