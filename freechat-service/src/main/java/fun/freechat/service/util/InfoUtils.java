package fun.freechat.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
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

    public static TokenUsage deserialize(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            List<Integer> usageInfo = defaultMapper().readValue(text, new TypeReference<>() {});
            return switch (usageInfo.size()) {
                case 1 -> new TokenUsage(usageInfo.getFirst());
                case 2 -> new TokenUsage(usageInfo.getFirst(), usageInfo.getLast());
                case 3 -> new TokenUsage(usageInfo.get(0), usageInfo.get(1), usageInfo.get(2));
                default -> null;
            };
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize TokenUsage from {}", text, e);
            return null;
        }
    }

    private static Integer getOrDefault(List<Integer> info, int index, Integer defaultValue) {
        return info.size() > index ? info.get(index) : defaultValue;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String text = "[1,2,3]";
        List<Integer> usage = defaultMapper().readValue(text, new TypeReference<>() {});
        System.out.println(usage);
    }
}
