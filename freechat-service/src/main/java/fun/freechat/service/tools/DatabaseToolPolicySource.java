package fun.freechat.service.tools;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;

import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.mapper.CharacterInfoDynamicSqlSupport;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.service.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DatabaseToolPolicySource implements ToolPolicySource {
    private final ChatContextMapper contexts;
    private final CharacterBackendMapper backends;
    private final CharacterInfoMapper characters;

    @Override
    public Policy load(String chatId) {
        try {
            var context = contexts.selectByPrimaryKey(chatId).orElseThrow();
            var backend = backends.selectByPrimaryKey(context.getBackendId()).orElseThrow();
            var character = characters
                    .selectOne(query -> query.where(
                                    CharacterInfoDynamicSqlSupport.characterUid, isEqualTo(backend.getCharacterUid()))
                            .and(CharacterInfoDynamicSqlSupport.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                            .orderBy(
                                    CharacterInfoDynamicSqlSupport.version.descending(),
                                    CharacterInfoDynamicSqlSupport.characterId.descending())
                            .limit(1))
                    .orElseThrow();
            var enabled = new java.util.HashSet<>(
                    CharacterToolConfiguration.parse(character.getExt()).enabled());
            if (!fun.freechat.service.skills.CharacterSkillConfiguration.parse(character.getExt())
                    .enabled()
                    .isEmpty()) {
                enabled.retainAll(fun.freechat.service.skills.SkillTools.SAFE_TOOLS);
            }
            return new Policy(
                    context.getUserId(),
                    character.getCharacterUid(),
                    backend.getBackendId(),
                    enabled,
                    backend.getLongTermMemoryWindowSize() != null && backend.getLongTermMemoryWindowSize() > 0,
                    Byte.valueOf((byte) 1).equals(backend.getEnableAlbumTool()));
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Tool authorization unavailable");
        }
    }
}
