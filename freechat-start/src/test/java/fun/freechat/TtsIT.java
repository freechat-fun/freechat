package fun.freechat;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

class TtsIT extends AbstractIntegrationTest {
    @Test
    void should_list_builtin_speakers() {
        testClient.get().uri("/api/v2/public/tts/builtin/speakers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<String>>() {})
                .value(speakers -> assertThat(speakers).hasSize(55));
    }

    @Test
    void should_play_builtin_speaker_sample() throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient.get().uri("/api/v2/public/tts/play/sample/idx/Claribel Dervla")
                    .accept(MediaType.valueOf("audio/wav"))
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.valueOf("audio/wav"))
                    .returnResult(byte[].class)
                    .getResponseBody()
                    .doOnComplete(() -> futureAnswer.complete(audioDataStream.toByteArray()))
                    .subscribe(event -> {
                        try {
                            audioDataStream.write(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        byte[] audioData = futureAnswer.get();
        assertThat(audioData).isNotNull();
        try (InputStream in = new ByteArrayInputStream(audioData)) {
            String mimeType = URLConnection.guessContentTypeFromStream(in);
            assertThat(mimeType).isEqualTo("audio/x-wav");
        }
    }
}
