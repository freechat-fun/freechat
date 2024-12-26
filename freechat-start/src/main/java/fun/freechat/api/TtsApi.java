package fun.freechat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import fun.freechat.service.chat.TtsService;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;

@RestController
@Tag(name = "TTS Service")
@RequestMapping("/api/v2")
@Validated
@Slf4j
@SuppressWarnings("unused")
public class TtsApi {
    private static final String SAMPLE_TEXT = "Hello, I am %s. Nice to meet you!";
    private static final int BUFFER_SIZE = 8192;

    @Autowired
    private TtsService ttsService;

    @Operation(
            operationId = "listTtsBuiltinSpeakers",
            summary = "List Builtin Speakers",
            description = "Return builtin TTS speakers."
    )
    @Cacheable(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key ="'f.f.a.TtsApi::list'",
            unless="#result == null")
    @GetMapping("/public/tts/builtin/speakers")
    public List<String> list() {
        try (InputStream inputStream =
                     this.getClass().getResourceAsStream("/xtts_v2_speaker_idxs.json")) {
            return InfoUtils.defaultMapper().readValue(inputStream, new TypeReference<>() {});
        } catch (IOException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No speakers for TTS", e);
        }
    }

    @Operation(
            operationId = "playTtsBuiltinSpeakerSample",
            summary = "Play Sample Audio",
            description = "Play TTS sample audio of builtin speaker."
    )
    @GetMapping(value = "/public/tts/play/sample/{speaker}", produces = "audio/wav")
    public ResponseEntity<StreamingResponseBody> playSample(
            @Parameter(description = "The builtin speaker") @PathVariable("speaker") @NotBlank String speaker) {
        String sampleText = SAMPLE_TEXT.formatted(speaker);

        StreamingResponseBody streamingResponseBody =
                outputStream -> ttsService.playBySpeakerIdx(speaker, sampleText, "en")
                        .thenAccept(voiceStream -> {
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int bytesRead;

                            try {
                                while ((bytesRead = voiceStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                outputStream.flush();
                            } catch (IOException e) {
                                throw new IllegalStateException("TTS server error.");
                            }
                        })
                        .exceptionally(ex -> {
                            log.error("Failed to forward tts voice data.", ex);
                            return null;
                        })
                        .join();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/wav"))
                .body(streamingResponseBody);
    }
}
