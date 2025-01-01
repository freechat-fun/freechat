package fun.freechat;

import fun.freechat.util.HttpUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

class TtsIT extends AbstractIntegrationTest {
    private static final String SPEAKER_WAV_NAME = "sample-16k-for-ttsIT.wav";
    @Value("${tts.baseUrl}")
    private String baseUri;
    private String customVoiceApi;
    private static Path speakerWav;

    @BeforeEach
    public void setUp() {
        customVoiceApi = baseUri + "/speaker/wav";
    }

    @BeforeAll
    public static void setUpForAll() throws IOException {
        Path tempDir = Files.createTempDirectory("voice");
        speakerWav = tempDir.resolve(SPEAKER_WAV_NAME);
        try (InputStream inputStream =
                     AbstractIntegrationTest.class.getResourceAsStream("/sample-16k.wav")) {
            Files.copy(Objects.requireNonNull(inputStream), speakerWav);
        }
    }

    @AfterAll
    public static void cleanUpForAll() {
        try {
            Files.delete(speakerWav);
        } catch (IOException ignored) {
            // ignored
        }
    }

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
    void should_play_sample_by_builtin_speaker() throws IOException, ExecutionException, InterruptedException, TimeoutException {
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

        byte[] audioData = futureAnswer.get(1, MINUTES);
        assertThat(audioData).isNotNull();
        try (InputStream in = new ByteArrayInputStream(audioData)) {
            String mimeType = URLConnection.guessContentTypeFromStream(in);
            assertThat(mimeType).isEqualTo("audio/x-wav");
        }
    }

    @Test
    void should_play_sample_by_custom_voice() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        testClient.get().uri("/api/v2/public/tts/play/sample/wav/" + SPEAKER_WAV_NAME)
                .accept(MediaType.valueOf("audio/wav"))
                .exchange()
                .expectStatus().isNotFound();

        assertThat(HttpUtils.upload(customVoiceApi, speakerWav, null)).isNotBlank();

        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient.get().uri("/api/v2/public/tts/play/sample/wav/" + SPEAKER_WAV_NAME)
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

        byte[] audioData = futureAnswer.get(1, MINUTES);
        assertThat(audioData).isNotNull();
        try (InputStream in = new ByteArrayInputStream(audioData)) {
            String mimeType = URLConnection.guessContentTypeFromStream(in);
            assertThat(mimeType).isEqualTo("audio/x-wav");
        }

        HttpUtils.delete(customVoiceApi + "/sample-16k.wav");
    }
}
