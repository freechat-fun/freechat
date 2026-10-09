package fun.freechat.channels.telegram.command;

import fun.freechat.channels.spi.ChannelTurnContext;
import java.util.concurrent.CompletionStage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommandHandler {

    String name();

    CompletionStage<Void> execute(String backendId, Update update, ChannelTurnContext turn);
}
