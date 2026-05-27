package fun.freechat.channels.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@ConditionalOnProperty(name = "channels.telegram.enabled", havingValue = "true")
public class TelegramChannelConfig {

    @Bean
    public TelegramClient telegramClient(@Value("${channels.telegram.bot-token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }
}
