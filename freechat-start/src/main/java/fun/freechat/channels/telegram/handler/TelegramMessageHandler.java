package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.spi.ChannelTurnContext;
import java.util.concurrent.CompletionStage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramMessageHandler {

    CompletionStage<Void> handle(String backendId, Update update, ChannelTurnContext turn);
}
