package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum StatsType {
    VIEW_COUNT("viewCount"),
    REFER_COUNT("referCount"),
    RECOMMEND_COUNT("recommendCount"),
    SCORE("score"),
    UNKNOWN("unknown")
    ;

    private static final List<String> fields =
            Arrays.stream(StatsType.values()).map(StatsType::fieldName).toList();
    private final String field;

    StatsType(String field) {
        this.field = field;
    }

    public static StatsType of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return StatsType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }

    public String fieldName() {
        return field;
    }

    public static List<String> fieldNames() {
        return fields;
    }
}