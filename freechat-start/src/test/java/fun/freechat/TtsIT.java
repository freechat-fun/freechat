package fun.freechat;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TtsIT extends AbstractIntegrationTest {
    @Test
    public void should_list_predefined_speakers() {
        testClient.get().uri("/api/v2/public/tts/speakers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .value(speakers -> assertThat(speakers).hasSize(55));

        // test the caching mechanism
        testClient.get().uri("/api/v2/public/tts/speakers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .value(speakers -> assertThat(speakers).hasSize(55));
    }
}
