package fun.freechat.service.chat.impl;

import fun.freechat.service.chat.TtsService;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.HttpUtils;
import fun.freechat.util.TraceUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;

@Service
@Slf4j
@SuppressWarnings("unused")
public class CoquiTtsServiceImpl implements TtsService {
    @Value("${tts.baseUrl}")
    private String baseUri;
    @Value("${tts.format}")
    private String format;
    @Value("${tts.timeout}")
    private Long timeout;
    private String inferenceApi;
    private String uploadVoiceApi;
    private String deleteVoiceApi;
    private String existsVoiceApi;
    private String pingApi;
    private final AtomicBoolean alive = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        baseUri = mayRemoveTrailingSlash(baseUri);
        inferenceApi = baseUri + "/inference/" + format;
        uploadVoiceApi = baseUri + "/speaker/mp3";
        deleteVoiceApi = baseUri + "/speaker/wav";
        existsVoiceApi = baseUri + "/speaker/wav/exists";
        pingApi = baseUri + "/ping";
        ping();
    }

    @Override
    public CompletableFuture<InputStream> speak(String speaker, TtsSpeakerType speakerType, String text, String lang) {
        ensureNotBlank(speaker, "speaker");
        ensureNotBlank(text, "text");
        ensureNotBlank(lang, "lang");
        check();

        Map<String, String> headers = createInferenceHeaders(speaker, speakerType, toLangId(lang));
        String body = createBody(text);

        return HttpUtils.asyncPost(inferenceApi, headers, body)
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return response.body();
                    } else {
                        log.error("Failed to fetch data from TTS server: {}", HttpUtils.toCurl(inferenceApi, headers, body));
                        throw new IllegalStateException("Failed to fetch data from TTS server. HTTP code: " + response.statusCode());
                    }
                })
                .orTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public String uploadVoice(String path) {
        ensureNotBlank(path, "path");
        check();

        Path filePath = StoreUtils.defaultFileStore().toPath(path);
        String response = HttpUtils.upload(uploadVoiceApi, filePath, null, createHeaders());
        if (StringUtils.isBlank(response)) {
            throw new IllegalStateException("Failed to upload voice file.");
        }

        return response;
    }

    @Override
    public void deleteVoice(String name) {
        ensureNotBlank(name, "name");
        check();

        HttpUtils.delete(deleteVoiceApi + "/" + name, createHeaders());
    }

    @Override
    public boolean existsVoice(String name) {
        ensureNotBlank(name, "name");
        check();

        return StringUtils.isNotBlank(HttpUtils.get(existsVoiceApi + "/" + name, createHeaders()));
    }

    @Override
    public boolean isEnabled() {
        return alive.get();
    }

    @Override
    public String mimeType() {
        return switch (format) {
            case "mp3" -> "audio/mpeg";
            case "aac" -> "audio/aac";
            case "m4a" -> "audio/mp3";
            case "wav" -> "audio/wav";
            case null, default -> "audio/octet-stream";
        };
    }

    @Override
    public String audioFormat() {
        return format;
    }

    @Scheduled(fixedDelay = 5000L)
    public void ping() {
        boolean isOnline = HttpUtils.ping(pingApi);
        if (!isOnline) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                // ignored
            }
            // try again
            isOnline = HttpUtils.ping(pingApi);
        }
        alive.set(isOnline);
    }

    private void check() {
        if (!alive.get()) {
            log.warn("TTS server is offline.");
            throw new IllegalStateException("TTS server is offline.");
        }
    }

    private static String mayRemoveTrailingSlash(String url) {
        if (StringUtils.isNotEmpty(url) && !url.endsWith("/")) {
            return url;
        } else {
            return url.substring(0, url.length() - 1);
        }
    }

    private Map<String, String> createHeaders() {
        Map<String, String> headerMap = HashMap.newHashMap(9);
        headerMap.put("Request-Id", TraceUtils.getTraceId());
        headerMap.put("Accept", "audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream, audio/*, text/plain, */*");
        headerMap.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8,en-US;q=0.7,ru;q=0.6");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Origin", baseUri);
        headerMap.put("Referer", baseUri);
        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        return headerMap;
    }

    private Map<String, String> createInferenceHeaders(String speaker, TtsSpeakerType speakerType, String langId) {
        Map<String, String> headerMap = createHeaders();

        headerMap.put("language-id", langId);
        if (speakerType == TtsSpeakerType.IDX) {
            headerMap.put("speaker-id", speaker);
        } else {
            headerMap.put("speaker-wav", speaker);
        }

        return headerMap;
    }

    private static String toLangId(String lang) {
        String lowerCaseLang = lang.toLowerCase();
        return switch (lowerCaseLang) {
            case "zh", "zh_cn" -> "zh-cn";
            case "en-us", "en_us" -> "en";
            default -> lowerCaseLang;
        };
    }

    private static String createBody(String text) {
        return "text=" + text;
    }
}
