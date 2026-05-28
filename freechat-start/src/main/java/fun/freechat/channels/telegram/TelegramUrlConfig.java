package fun.freechat.channels.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramUrl;

@Configuration
public class TelegramUrlConfig {

    @Bean
    @ConditionalOnMissingBean
    public TelegramUrl telegramUrl() {
        return TelegramUrl.DEFAULT_URL;
    }
}
