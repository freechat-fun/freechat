package fun.freechat;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TtsIT extends AbstractIntegrationTest {
    @Test
    void should_list_predefined_speakers() {
        testClient.get().uri("/api/v2/public/tts/speakers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .value(speakers -> assertThat(speakers).hasSize(55));
    }

    @Test
    void should_play_predefined_speaker_sample() throws IOException {
        byte[] audioData = testClient.get().uri("/api/v2/public/tts/play/sample/Claribel Dervla")
                .accept(MediaType.valueOf("audio/wav"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.valueOf("audio/wav"))
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

        assertThat(audioData).isNotNull();
        try (InputStream in = new ByteArrayInputStream(audioData)) {
            String mimeType = URLConnection.guessContentTypeFromStream(in);
            assertThat(mimeType).isEqualTo("audio/x-wav");
        }
    }
}
