package fun.freechat.service.common.impl;

import fun.freechat.service.common.ConfigService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static fun.freechat.service.util.CacheUtils.IN_PROCESS_SHORT_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.SHORT_PERIOD_CACHE_NAME;


@Service
@Slf4j
@SuppressWarnings("unused")
public class ConfigMapConfigServiceImpl implements ConfigService {
    private static final String PROPERTIES_FILE = "application-runtime.properties";
    private static final String CONFIG_VERSION_KEY = "config.version";

    @Value("${configmap.workdir}")
    private String workdir;
    private Path configPath;

    @PostConstruct
    private void init() {
        configPath = Paths.get(workdir, PROPERTIES_FILE);
        Properties properties = load();
        if (properties.isEmpty()) {
            return;
        }
        String version = properties.getProperty(CONFIG_VERSION_KEY, "0");
        log.info("Found config from {}", configPath.toString());
        log.info("Config version: {}", version);
    }

    @Override
    @Cacheable(cacheNames = SHORT_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_SHORT_CACHE_MANAGER,
            keyGenerator="fullNameKeyGenerator")
    public Properties load() {
        Properties properties = new Properties();
        try {
            if (Files.exists(configPath)) {
                properties.load(new FileReader(configPath.toFile()));
            }
        } catch (Exception e) {
            log.error("Failed to load config from {}", configPath.toString(), e);
        }
        return properties;
    }
}
