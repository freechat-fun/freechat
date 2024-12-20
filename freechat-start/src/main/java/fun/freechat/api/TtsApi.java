package fun.freechat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "TTS Service")
@RequestMapping("/api/v2/public/tts")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class TtsApi {
    @Operation(
            operationId = "listTtsSpeakerIndexes",
            summary = "List TTS Speaker Idxs",
            description = "Return TTS speaker indexes."
    )
    @GetMapping("/speakers")
    public List<String> list() {
        try {
            URL speakersUrl = Objects.requireNonNull(
                    this.getClass().getResource("/xtts_v2_speaker_idxs.json"));
            return InfoUtils.defaultMapper().readValue(speakersUrl, new TypeReference<>() {});
        } catch (IOException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No speakers for TTS", e);
        }
    }
}
