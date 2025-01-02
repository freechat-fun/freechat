package fun.freechat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.chat.TtsService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.TraceUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;

@RestController
@Tag(name = "TTS Service")
@RequestMapping("/api/v2")
@Validated
@Slf4j
@SuppressWarnings("unused")
public class TtsApi {
    private static final String CACHE_HOME = "audio/cache";
    private static final String SAMPLE_TEXT = "Hello, I am %s. Nice to meet you!";
    private static final int BUFFER_SIZE = 8192;

    @Autowired
    private ChatMemoryService chatMemoryService;
    @Autowired
    private TtsService ttsService;
    @Autowired
    private Tika tika;

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
            operationId = "playSample",
            summary = "Play Sample Audio",
            description = "Play TTS sample audio of the builtin/custom speaker."
    )
    @GetMapping(value = "/public/tts/play/sample/{speakerType}/{speaker}",
            produces = {"audio/mpeg", "audio/aac", "audio/mp4", "audio/wav", "audio/octet-stream"})
    public ResponseEntity<StreamingResponseBody> playSample(
            @Parameter(description = "The speaker type") @PathVariable("speakerType") @Pattern(regexp = "idx|wav") String speakerType,
            @Parameter(description = "The speaker") @PathVariable("speaker") @NotBlank String speaker) {
        ChatMessageRecord messageRecord = ChatMessageRecord.builder()
                .id(0L)
                .message(AiMessage.from(SAMPLE_TEXT.formatted(speaker)))
                .speakerType(TtsSpeakerType.of(speakerType))
                .speaker(speaker)
                .build();

        return doSpeak(messageRecord, "en");
    }

    @Operation(
            operationId = "speakMessage",
            summary = "Speak Message",
            description = "Read out the message."
    )
    @GetMapping(value = "/tts/speak/{messageId}",
            produces = {"audio/mpeg", "audio/aac", "audio/mp4", "audio/wav", "audio/octet-stream"})
    @PreAuthorize("hasPermission(#p0, 'ttsSpeakDefaultOp')")
    public ResponseEntity<StreamingResponseBody> speakMessage(
            @Parameter(description = "The message id") @PathVariable("messageId") @Positive Long messageId) {
        ChatMessageRecord messageRecord = chatMemoryService.get(messageId);
        if (messageRecord == null ||
                messageRecord.getMessage() == null ||
                messageRecord.getMessage().type() != ChatMessageType.AI) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find message record by " + messageId);
        }

        return doSpeak(messageRecord, chatMemoryService.getLang(messageRecord.getChatId()));
    }

    private ResponseEntity<StreamingResponseBody> doSpeak(ChatMessageRecord messageRecord, String lang) {
        FileStore fileStore = StoreUtils.defaultFileStore();
        boolean cacheable = true;
        if (!fileStore.exists(CACHE_HOME)) {
            try {
                fileStore.createDirectories(CACHE_HOME);
            } catch (IOException e) {
                log.warn("Failed to create cache directory: {}", CACHE_HOME, e);
                cacheable = false;
            }
        }

        if (cacheable) {
            String cachePath = getCachePath(messageRecord);
            if (fileStore.exists(cachePath)) {
                try {
                    InputStream voiceStream = fileStore.newInputStream(cachePath);
                    return doSpeakByCachedVoice(
                            messageRecord.getSpeaker(),
                            messageRecord.getSpeakerType(),
                            ((AiMessage) messageRecord.getMessage()).text(),
                            lang,
                            voiceStream);
                } catch (IOException e) {
                    log.warn("Failed to read cached wav file: {}", cachePath, e);
                    fileStore.tryDelete(cachePath);
                }
            }

            return doSpeakByTtsService(
                    messageRecord.getSpeaker(),
                    messageRecord.getSpeakerType(),
                    ((AiMessage) messageRecord.getMessage()).text(),
                    lang,
                    cachePath,
                    fileStore);
        }

        // speak without caching
        return doSpeakByTtsService(
                messageRecord.getSpeaker(),
                messageRecord.getSpeakerType(),
                ((AiMessage) messageRecord.getMessage()).text(),
                lang,
                null,
                fileStore);
    }

    private ResponseEntity<StreamingResponseBody> doSpeakByCachedVoice(
            String speaker, TtsSpeakerType speakerType, String text, String lang, InputStream voiceStream) {
        String traceId = TraceUtils.getTraceId();
        User currentUser = AccountUtils.currentUserOrNull();
        String username = currentUser != null ? currentUser.getUsername() : null;
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        StreamingResponseBody streamingResponseBody = outputStream -> {
            pipeTransfer(
                    voiceStream,
                    outputStream,
                    null,
                    speaker,
                    speakerType,
                    text,
                    lang,
                    traceId,
                    username,
                    startTime);

            try {
                voiceStream.close();
            } catch (Exception ignored) {
                // ignored
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(ttsService.mimeType()))
                .body(streamingResponseBody);
    }

    private ResponseEntity<StreamingResponseBody> doSpeakByTtsService(
            String speaker, TtsSpeakerType speakerType, String text, String lang,
            String cacheFile, FileStore fileStore) {
        String traceId = TraceUtils.getTraceId();
        User currentUser = AccountUtils.currentUserOrNull();
        String username = currentUser != null ? currentUser.getUsername() : null;
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        if (speakerType == TtsSpeakerType.WAV && !ttsService.existsVoice(speaker)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Speaker " + speaker + " not found!");
        }


        OutputStream cacheFileStream = null;
        if (cacheFile != null) {
            try {
                cacheFileStream = fileStore.newOutputStream(cacheFile);
            } catch (IOException e) {
                log.warn("Failed to write to {}", cacheFile);
            }
        }
        final OutputStream extraOutputStream = cacheFileStream;
        AtomicBoolean failed = new AtomicBoolean(false);

        StreamingResponseBody streamingResponseBody = outputStream -> {
            ttsService.speak(speaker, speakerType, text, lang)
                    .thenAccept(voiceStream -> pipeTransfer(
                            voiceStream,
                            outputStream,
                            extraOutputStream,
                            speaker,
                            speakerType,
                            text,
                            lang,
                            traceId,
                            username,
                            startTime))
                    .exceptionally(ex -> {
                        failed.set(true);
                        log.error("Failed to transfer tts data", ex);
                        return null;
                    })
                    .join();

            if (extraOutputStream != null) {
                try {
                    extraOutputStream.close();
                } catch (Exception ignored) {
                    // ignored
                }
            }

            if (failed.get() || !ttsService.mimeType().equals(tika.detect(cacheFile))) {
                fileStore.tryDelete(cacheFile);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(ttsService.mimeType()))
                .body(streamingResponseBody);
    }

    private void pipeTransfer(InputStream voiceStream,
                              OutputStream outputStream,
                              OutputStream extraOutputStream,
                              String speaker,
                              TtsSpeakerType speakerType,
                              String text,
                              String lang,
                              String traceId,
                              String username,
                              AtomicLong startTime) {
        TraceUtils.putTraceAttribute("traceId", traceId);
        TraceUtils.putTraceAttribute("username", username);

        String traceInfo = new TraceUtils.TraceInfoBuilder()
                .args(new String[]{speaker, speakerType.text(), text, lang})
                .elapseTime(System.currentTimeMillis() - startTime.get())
                .method("TtsApi::firstPackageReceived")
                .status(TraceUtils.TraceStatus.SUCCESSFUL)
                .traceId(traceId)
                .username(username)
                .build();
        TraceUtils.getPerfLogger().trace(traceInfo);

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        try {
            while ((bytesRead = voiceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                // send data immediately
                outputStream.flush();

                if (extraOutputStream != null) {
                    extraOutputStream.write(buffer, 0, bytesRead);
                }
            }

            if (extraOutputStream != null) {
                extraOutputStream.flush();
            }
        } catch (IOException e) {
            traceInfo = new TraceUtils.TraceInfoBuilder()
                    .args(new String[]{speaker, speakerType.text(), text, lang})
                    .elapseTime(System.currentTimeMillis() - startTime.get())
                    .method("TtsApi::lastPackageReceived")
                    .status(TraceUtils.TraceStatus.FAILED)
                    .traceId(traceId)
                    .username(username)
                    .build();
            TraceUtils.getPerfLogger().trace(traceInfo);
            throw new IllegalStateException("TTS server error.", e);
        }

        traceInfo = new TraceUtils.TraceInfoBuilder()
                .args(new String[]{speaker, speakerType.text(), text, lang})
                .elapseTime(System.currentTimeMillis() - startTime.get())
                .method("TtsApi::lastPackageReceived")
                .status(TraceUtils.TraceStatus.SUCCESSFUL)
                .traceId(traceId)
                .username(username)
                .build();
        TraceUtils.getPerfLogger().trace(traceInfo);
    }

    private String getCachePath(ChatMessageRecord messageRecord) {
        return "%s/%s-%s-%d.%s".formatted(
                CACHE_HOME,
                messageRecord.getSpeakerType().text(),
                messageRecord.getSpeaker().replaceAll(" ", ""),
                messageRecord.getId(),
                ttsService.audioFormat());
    }
}
