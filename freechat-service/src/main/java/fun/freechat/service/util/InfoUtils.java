package fun.freechat.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.output.TokenUsage;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class InfoUtils {
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(NON_NULL);

    private static final int VISIBLE_SIZE = 4;

    public static ObjectMapper defaultMapper() {
        return DEFAULT_MAPPER;
    }

    public static List<String> trimListElements(List<String> originList) {
        return Optional.ofNullable(originList)
                .orElse(Collections.emptyList())
                .stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    public static String desensitize(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        int length = text.length();
        if (length > VISIBLE_SIZE * 2) {
            String prefix = text.substring(0, VISIBLE_SIZE);
            String suffix = text.substring(length - VISIBLE_SIZE, length);
            String stars = StringUtils.repeat('*', Math.min(VISIBLE_SIZE, length - VISIBLE_SIZE * 2));
            return (prefix + stars + suffix);
        } else {
            return StringUtils.repeat('*', length);
        }
    }

    public static String serialize(TokenUsage tokenUsage) {
        return String.format("[%d,%d,%d]",
                tokenUsage.inputTokenCount(),
                tokenUsage.outputTokenCount(),
                tokenUsage.totalTokenCount());
    }
}
