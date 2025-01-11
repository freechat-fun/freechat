package fun.freechat.service.config.impl;

import fun.freechat.service.config.RuntimeConfig;
import fun.freechat.util.YamlUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component("applicationConfig")
@Slf4j
@SuppressWarnings("unused")
public class ConfigMapApplicationConfig implements RuntimeConfig {
    private static final String PROPERTIES_FILE = "application-runtime.yml";
    private static final String CONFIG_VERSION_KEY = "config.version";

    @Value("${configmap.workdir}")
    private String workdir;
    private final AtomicLong configModifiedTime = new AtomicLong(0L);
    private final AtomicReference<String> configText = new AtomicReference<>("");
    private final AtomicReference<Properties> configProperties =
            new AtomicReference<>(new Properties());
    private final AtomicReference<Map<String, Object>> configMap =
            new AtomicReference<>(new LinkedMap<>());

    @PostConstruct
    public void init() {
        load();
        String version = configProperties.get().getProperty(CONFIG_VERSION_KEY, "0");
        log.info("Config version: {}", version);
    }

    @Override
    public String get() {
        return configText.get();
    }

    @Override
    public Properties getProperties() {
        return configProperties.get();
    }

    @Override
    public Map<String, Object> getMap() {
        return configMap.get();
    }

    @Override
    public <T> T getObject() {
        String text = configText.get();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return YamlUtils.toObject(text);
    }

    @Scheduled(fixedDelay = 5000L)
    public void load() {
        Path configPath = Paths.get(workdir, PROPERTIES_FILE);
        try {
            if (Files.exists(configPath)) {
                long lastModifiedTime = Files.getLastModifiedTime(configPath).toMillis();
                if (lastModifiedTime <= configModifiedTime.get()) {
                    // no changes
                    return;
                }

                String runtimeConf = Files.readString(configPath);
                if (StringUtils.isBlank(runtimeConf)) {
                    log.warn("Fetch a blank string from {}! Ignore it.", configPath);
                    return;
                }
                configModifiedTime.set(lastModifiedTime);
                configText.set(runtimeConf);
                configProperties.set(YamlUtils.toProperties(runtimeConf));
                configMap.set(YamlUtils.toObject(runtimeConf));
            }
        } catch (Exception e) {
            log.error("Failed to load config from {}", configPath, e);
        }
    }
}
