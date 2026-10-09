package fun.freechat.channels.telegram.command;

import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@Slf4j
public class HelpCommand implements TelegramCommandHandler {

    private static final String HELP_TEXT = "Available commands:\n"
            + "/start - Start chatting with me\n"
            + "/reset - Clear the current conversation memory\n"
            + "/help  - Show this help";

    @Override
    public String name() {
        return "help";
    }

    @Override
    public CompletionStage<Void> execute(String backendId, Update update, ChannelTurnContext turn) {
        if (!update.hasMessage()) {
            return CompletableFuture.completedFuture(null);
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        if (chat == null) {
            return CompletableFuture.completedFuture(null);
        }
        return turn.outbound()
                .sendText(ChannelText.plain(HELP_TEXT))
                .<Void>thenApply(receipt -> null)
                .whenComplete((ignored, failure) -> {
                    if (failure != null) {
                        log.warn("/help reply failed for chat {}", chat.getId());
                    }
                });
    }
}
