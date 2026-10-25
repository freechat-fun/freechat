package fun.freechat.service.connectors;

import static fun.freechat.service.util.ChannelUtils.isValidChannelUser;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;

import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.CharacterInfoDynamicSqlSupport;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.ChatContext;
import fun.freechat.service.chat.TgChatService;
import fun.freechat.service.skills.SkillCharacterAccess;
import fun.freechat.service.tools.CharacterToolConfiguration;
import fun.freechat.service.tools.ToolContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DatabaseConnectorPolicySource implements ConnectorPolicySource {
    private final ChatContextMapper contexts;
    private final CharacterBackendMapper backends;
    private final CharacterInfoMapper characters;
    private final SkillCharacterAccess access;
    private final UserMapper users;
    private final TgChatService telegramChats;

    @Override
    public Policy load(ToolContext scope) {
        try {
            var context = contexts.selectByPrimaryKey(scope.chatId()).orElseThrow();
            if (!scope.userId().equals(context.getUserId())
                    || !scope.backendId().equals(context.getBackendId())) {
                throw unavailable();
            }
            var backend = backends.selectByPrimaryKey(scope.backendId()).orElseThrow();
            if (!scope.characterUid().equals(backend.getCharacterUid())) throw unavailable();
            var character = characters
                    .selectOne(query -> query.where(
                                    CharacterInfoDynamicSqlSupport.characterUid, isEqualTo(scope.characterUid()))
                            .and(CharacterInfoDynamicSqlSupport.visibility, isNotEqualTo("hidden"))
                            .orderBy(
                                    CharacterInfoDynamicSqlSupport.version.descending(),
                                    CharacterInfoDynamicSqlSupport.characterId.descending())
                            .limit(1))
                    .orElseThrow();
            boolean telegram = context.getTgChatId() != null;
            if (telegram) {
                if (!isValidChannelUser(context)) throw unavailable();
            } else {
                if (!access.permits(scope.userId(), character)) throw unavailable();
                requireActiveUser(scope.userId());
            }
            requireActiveUser(character.getUserId());
            var enabled =
                    CharacterConnectorConfiguration.parse(character.getExt()).enabled();
            if (enabled.isEmpty()
                    || CharacterToolConfiguration.parse(character.getExt())
                            .enabled()
                            .isEmpty()) {
                return new Policy(character.getUserId(), enabled, false);
            }
            if (telegram) requireTelegramChat(context, backend);
            return new Policy(character.getUserId(), enabled, true);
        } catch (RuntimeException ignored) {
            throw unavailable();
        }
    }

    private void requireTelegramChat(ChatContext context, CharacterBackend backend) {
        if (StringUtils.isBlank(backend.getTgBotToken())) throw unavailable();
        // The owner-configured bot grants access through its persisted Telegram chat binding.
        var chat = telegramChats
                .findByBackendAndChat(context.getBackendId(), context.getTgChatId())
                .orElseThrow(DatabaseConnectorPolicySource::unavailable);
        if (!context.getBackendId().equals(chat.getBackendId())
                || !context.getTgChatId().equals(chat.getTgChatId())) {
            throw unavailable();
        }
    }

    private void requireActiveUser(String userId) {
        var user = users.selectByPrimaryKey(userId).orElseThrow(DatabaseConnectorPolicySource::unavailable);
        if (!userId.equals(user.getUserId())
                || !Byte.valueOf((byte) 1).equals(user.getEnabled())
                || !Byte.valueOf((byte) 0).equals(user.getLocked())) throw unavailable();
    }

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Chat connector authorization unavailable");
    }
}
