package fun.freechat.service.chat.impl;

import fun.freechat.model.ChatContext;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.TelegramChatBindingService;
import fun.freechat.service.chat.TgChatService;
import fun.freechat.service.chat.TgUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramChatBindingServiceImpl implements TelegramChatBindingService {

    private static final String TG_USER_PREFIX = "tg-";

    private final TgUserService tgUserService;
    private final TgChatService tgChatService;
    private final ChatContextService chatContextService;

    @Override
    public String getOrCreate(
            String backendId,
            Long tgChatId,
            String chatType,
            String title,
            Long tgUserId,
            String username,
            String firstName,
            String lastName) {
        if (tgUserId != null) {
            tgUserService.getOrCreate(backendId, tgUserId, username, firstName, lastName);
        }
        tgChatService.getOrCreate(backendId, tgChatId, chatType, title);

        String fcUserId = TG_USER_PREFIX + tgChatId;
        String existing = chatContextService.getChatIdByBackend(fcUserId, backendId);
        if (StringUtils.isNotBlank(existing)) {
            return existing;
        }

        ChatContext context = new ChatContext()
                .withUserId(fcUserId)
                .withBackendId(backendId)
                .withChatType(chatType)
                .withTgUserId(tgUserId)
                .withTgChatId(tgChatId);
        ChatContext created = chatContextService.create(context);
        return created != null ? created.getChatId() : null;
    }

    @Override
    public String findChatId(String backendId, Long tgChatId) {
        String fcUserId = TG_USER_PREFIX + tgChatId;
        return chatContextService.getChatIdByBackend(fcUserId, backendId);
    }
}
