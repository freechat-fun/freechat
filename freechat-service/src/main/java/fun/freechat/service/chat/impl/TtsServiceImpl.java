package fun.freechat.service.chat.impl;

import fun.freechat.service.chat.TtsService;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.util.HttpUtils;
import fun.freechat.util.TraceUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;

@Service
@Slf4j
@SuppressWarnings("unused")
public class TtsServiceImpl implements TtsService {
    @Value("${tts.baseUrl}")
    private String baseUri;
    @Value("${tts.timeout}")
    private Long timeout;
    private String ttsApi;

    @PostConstruct
    public void init() {
        baseUri = mayRemoveTrailingSlash(baseUri);
        ttsApi = baseUri + "/api/tts";
    }

    @Override
    public CompletableFuture<InputStream> speak(String speaker, TtsSpeakerType speakerType, String text, String lang) {
        ensureNotBlank(speaker, "speaker");
        ensureNotBlank(text, "text");
        ensureNotBlank(lang, "lang");

        Map<String, String> headers = createHeaderMap(speaker, speakerType, toLangId(lang));
        String body = createBody(text);

        return HttpUtils.asyncPost(ttsApi, headers, body)
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        log.error("Failed to fetch data from TTS server: {}", HttpUtils.toCurl(ttsApi, headers, body));
                        throw new IllegalStateException("Failed to fetch data from TTS server. HTTP code: " + response.statusCode());
                    }
                })
                .orTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    private static String mayRemoveTrailingSlash(String url) {
        if (StringUtils.isNotEmpty(url) && !url.endsWith("/")) {
            return url;
        } else {
            return url.substring(0, url.length() - 1);
        }
    }

    private Map<String, String> createHeaderMap(String speaker, TtsSpeakerType speakerType, String langId) {
        Map<String, String> headerMap = HashMap.newHashMap(9);
        headerMap.put("Request-Id", TraceUtils.getTraceId());
        headerMap.put("Accept", "audio/wav, text/plain, */*");
        headerMap.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8,en-US;q=0.7,ru;q=0.6");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Origin", baseUri);
        headerMap.put("Referer", baseUri);
        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
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
