package fun.freechat.service.util;

import fun.freechat.service.common.ConfigService;
import fun.freechat.service.enums.ConfigFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class ConfigUtils {
    public static Properties of(String config, ConfigFormat format) {
        Properties properties = new Properties();

        if (StringUtils.isBlank(config)) {
            return properties;
        }
        try {
            switch (format) {
                case KV -> properties.load(new StringReader(config));
                case JSON, YAML -> throw new NotImplementedException();
            }
        } catch (IOException e) {
            log.warn("Failed to parse {}", config, e);
        }

        return properties;
    }

    public static Properties of(Triple<String, ConfigFormat, Integer> configTriple) {
        if (Objects.isNull(configTriple)) {
            return new Properties();
        }
        return of(configTriple.getLeft(), configTriple.getMiddle());
    }

    public static Properties getProperties(ConfigService configService, String configName) {
        return of(configService.load(configName));
    }

    public static long getLongOrDefault(Properties properties, String key, long defaultValue) {
        String valueStr = properties.getProperty(key);
        long value = defaultValue;
        if (StringUtils.isNotBlank(valueStr)) {
            try {
                value = Long.parseLong(valueStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return value;
    }

    public static int getIntOrDefault(Properties properties, String key, int defaultValue) {
        String valueStr = properties.getProperty(key);
        int value = defaultValue;
        if (StringUtils.isNotBlank(valueStr)) {
            try {
                value = Integer.parseInt(valueStr);
            } catch (NumberFormatException ignored) {
            }
        }
        return value;
    }
}
