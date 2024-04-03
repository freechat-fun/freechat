package fun.freechat.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class RagConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}
