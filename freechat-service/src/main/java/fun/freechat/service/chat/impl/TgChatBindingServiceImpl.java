package fun.freechat.service.chat.impl;

import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TgChatBindingServiceImpl implements TgChatBindingService {

    private static final String TG_USER_PREFIX = "tg-";

    private final TgUserService tgUserService;
    private final TgChatService tgChatService;
    private final ChatContextService chatContextService;
    private final ChatSessionService chatSessionService;
    private final CharacterService characterService;

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

        CharacterBackend backend = characterService.getBackend(backendId);
        if (backend == null) {
            return null;
        }

        String about = Optional.ofNullable(backend.getCharacterUid())
                .map(characterService::getLatestIdByUid)
                .map(characterService::summary)
                .map(CharacterInfo::getDefaultScene)
                .orElse(null);

        String nickname = buildNickname(firstName, lastName, username);
        ChatContext context = new ChatContext()
                .withUserId(fcUserId)
                .withUserNickname(nickname)
                .withUserProfile("Telegram user" + (nickname == null ? "" : " " + nickname))
                .withBackendId(backendId)
                .withChatType(chatType)
                .withTgUserId(tgUserId)
                .withTgChatId(tgChatId)
                .withAbout(about)
                .withQuota(backend.getInitQuota())
                .withQuotaType(backend.getQuotaType());

        ChatContext persistent = chatContextService.create(context);
        if (persistent == null) {
            return null;
        }

        ChatSession session = chatSessionService.get(persistent);
        if (session == null) {
            chatContextService.delete(persistent.getChatId());
            return null;
        }
        return persistent.getChatId();
    }

    @Override
    public String findChatId(String backendId, Long tgChatId) {
        String fcUserId = TG_USER_PREFIX + tgChatId;
        return chatContextService.getChatIdByBackend(fcUserId, backendId);
    }

    private static String buildNickname(String firstName, String lastName, String username) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(firstName)) {
            sb.append(firstName.trim());
        }
        if (StringUtils.isNotBlank(lastName)) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(lastName.trim());
        }
        if (sb.isEmpty() && StringUtils.isNotBlank(username)) {
            sb.append(username.trim());
        }
        return sb.isEmpty() ? null : sb.toString();
    }
}
