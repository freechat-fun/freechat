package fun.freechat.config;

import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LongTermMemoryConfig {
    @Bean
    // Boot requires a canonical kebab-case prefix; relaxed binding accepts longTerm YAML.
    @ConfigurationProperties("chat.memory.long-term")
    public LongTermMemoryProperties longTermMemoryProperties() {
        return new LongTermMemoryProperties();
    }
}
