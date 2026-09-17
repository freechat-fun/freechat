package fun.freechat.service.util;

import fun.freechat.model.ChatContext;

public final class ChannelUtils {
    private static final String TELEGRAM_USER_PREFIX = "tg-";

    private ChannelUtils() {}

    public static String telegramUserId(long chatId) {
        return TELEGRAM_USER_PREFIX + chatId;
    }

    public static boolean isValidChannelUser(ChatContext context) {
        // A prefix alone cannot establish ownership of a channel chat.
        return context.getTgChatId() != null
                && telegramUserId(context.getTgChatId()).equals(context.getUserId());
    }
}
