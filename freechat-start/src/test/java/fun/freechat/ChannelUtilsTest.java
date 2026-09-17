package fun.freechat;

import static fun.freechat.service.util.ChannelUtils.isValidChannelUser;
import static fun.freechat.service.util.ChannelUtils.telegramUserId;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.model.ChatContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ChannelUtilsTest {
    @ParameterizedTest
    @CsvSource({
        "12345,tg-12345",
        "-1001234567890,tg--1001234567890",
        "9223372036854775807,tg-9223372036854775807",
        "-9223372036854775808,tg--9223372036854775808"
    })
    void telegramUserIdPreservesPersistedFormat(long chatId, String expected) {
        assertEquals(expected, telegramUserId(chatId));
    }

    @ParameterizedTest
    @CsvSource({
        "12345,tg-12345,private",
        "-12345,tg--12345,group",
        "-1001234567890,tg--1001234567890,supergroup",
        "-1001234567890,tg--1001234567890,channel"
    })
    void exactTelegramChatIdentityIsValidRegardlessOfSender(long chatId, String userId, String chatType) {
        ChatContext context = new ChatContext()
                .withUserId(userId)
                .withTgChatId(chatId)
                .withTgUserId(987654321L)
                .withChatType(chatType);

        assertTrue(isValidChannelUser(context));
        context.setTgUserId(null);
        assertTrue(isValidChannelUser(context));
    }

    @ParameterizedTest
    @CsvSource(
            nullValues = "NULL",
            value = {
                "NULL,website-user",
                "NULL,tg-12345",
                "NULL,tg-null",
                "NULL,NULL",
                "12345,NULL",
                "12345,''",
                "12345,' '",
                "12345,tg-",
                "12345,tg-54321",
                "12345,tg--12345",
                "-12345,tg-12345",
                "12345,tg-12345-suffix",
                "12345,tg-012345",
                "12345,TG-12345",
                "12345,'tg-12345 '",
                "12345,unknown-12345",
                "12345,website-user",
                "12345,tg-987654321"
            })
    void missingMismatchedOrUnknownChannelIdentityIsRejected(Long chatId, String userId) {
        ChatContext context = new ChatContext()
                .withUserId(userId)
                .withTgChatId(chatId)
                .withTgUserId(987654321L)
                .withChatType("private");

        assertFalse(isValidChannelUser(context));
    }
}
