package fun.freechat;

import fun.freechat.service.chat.TtsService;
import fun.freechat.util.HttpUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String VOICE_FILENAME = "voice-for-ttsIT.mp3";
    @Value("${tts.baseUrl}")
    private String baseUri;
    @Autowired
    private TtsService ttsService;
    private String customVoiceApi;
    private final Tika tika = new Tika();
    private static Path speakerFilename;

    @BeforeEach
    public void setUp() {
        customVoiceApi = baseUri + "/speaker/mp3";
    }

    @BeforeAll
    public static void setUpForAll() throws IOException {
        Path tempDir = Files.createTempDirectory("voice");
        speakerFilename = tempDir.resolve(VOICE_FILENAME);
        try (InputStream inputStream =
                     AbstractIntegrationTest.class.getResourceAsStream("/voice.mp3")) {
            Files.copy(Objects.requireNonNull(inputStream), speakerFilename);
        }
    }

    @AfterAll
    public static void cleanUpForAll() {
        try {
            Files.delete(speakerFilename);
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
                    .accept(MediaType.valueOf(ttsService.mimeType()))
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.valueOf(ttsService.mimeType()))
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
        assertThat(tika.detect(audioData)).isEqualTo("audio/mpeg");
    }

    @Test
    void should_play_sample_by_custom_voice() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        testClient.get().uri("/api/v2/public/tts/play/sample/wav/" + VOICE_FILENAME)
                .accept(MediaType.valueOf(ttsService.mimeType()))
                .exchange()
                .expectStatus().isNotFound();

        String voiceKey = HttpUtils.upload(customVoiceApi, speakerFilename, null);
        assertThat(voiceKey).isNotBlank();

        CompletableFuture<byte[]> futureAnswer = new CompletableFuture<>();

        try (ByteArrayOutputStream audioDataStream = new ByteArrayOutputStream()) {
            testClient.get().uri("/api/v2/public/tts/play/sample/wav/" + voiceKey)
                    .accept(MediaType.valueOf(ttsService.mimeType()))
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.valueOf(ttsService.mimeType()))
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
        assertThat(tika.detect(audioData)).isEqualTo("audio/mpeg");

        HttpUtils.delete(customVoiceApi + voiceKey);
    }
}
