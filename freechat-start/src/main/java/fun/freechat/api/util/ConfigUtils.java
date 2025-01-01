package fun.freechat.api.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class ConfigUtils {
    // keys
    public static final String AVATAR_MAX_SIZE_KEY = "avatar.maxSize";
    public static final String AVATAR_MAX_COUNT_KEY = "avatar.maxCount";
    public static final String DOCUMENT_MAX_SIZE_KEY = "document.maxSize";
    public static final String DOCUMENT_MAX_COUNT_KEY = "document.maxCount";
    public static final String PICTURE_MAX_SIZE_KEY = "picture.maxSize";
    public static final String PICTURE_MAX_COUNT_KEY = "picture.maxCount";
    public static final String VOICE_MAX_SIZE_KEY = "voice.maxSize";
    public static final String VOICE_MAX_COUNT_KEY = "voice.maxCount";
    public static final String WEB_VERSION_KEY = "web.version";

    // default values
    public static final long DEFAULT_AVATAR_MAX_SIZE = 1024L * 1024L;
    public static final int DEFAULT_AVATAR_MAX_COUNT = 10;
    public static final long DEFAULT_DOCUMENT_MAX_SIZE = 10L * 1024L * 1024L;
    public static final int DEFAULT_DOCUMENT_MAX_COUNT = 5;
    public static final long DEFAULT_PICTURE_MAX_SIZE = 2L * 1024L * 1024L;
    public static final int DEFAULT_PICTURE_MAX_COUNT = 10;
    public static final long DEFAULT_VOICE_MAX_SIZE = 1024L * 1024L;
    public static final int DEFAULT_VOICE_MAX_COUNT = 1;

    public static long getOrDefault(Properties properties, String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    public static int getOrDefault(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}
