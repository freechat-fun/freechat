package fun.freechat.service.connectors;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;

import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.CharacterInfoDynamicSqlSupport;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.UserMapper;
import fun.freechat.service.skills.CharacterSkillConfiguration;
import fun.freechat.service.skills.SkillCharacterAccess;
import fun.freechat.service.tools.CharacterToolConfiguration;
import fun.freechat.service.tools.ToolContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DatabaseConnectorPolicySource implements ConnectorPolicySource {
    private final ChatContextMapper contexts;
    private final CharacterBackendMapper backends;
    private final CharacterInfoMapper characters;
    private final SkillCharacterAccess access;
    private final UserMapper users;

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
            if (!access.permits(scope.userId(), character)) throw unavailable();
            requireActiveUser(scope.userId());
            requireActiveUser(character.getUserId());
            return new Policy(
                    character.getUserId(),
                    CharacterConnectorConfiguration.parse(character.getExt()).enabled(),
                    CharacterSkillConfiguration.parse(character.getExt())
                                    .enabled()
                                    .isEmpty()
                            && !CharacterToolConfiguration.parse(character.getExt())
                                    .enabled()
                                    .isEmpty());
        } catch (RuntimeException ignored) {
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
