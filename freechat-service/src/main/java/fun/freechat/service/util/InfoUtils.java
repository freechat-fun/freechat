package fun.freechat.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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
}
