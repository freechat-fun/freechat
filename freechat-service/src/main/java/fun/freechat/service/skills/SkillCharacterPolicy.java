package fun.freechat.service.skills;

import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.model.CharacterInfo;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkillCharacterPolicy {
    private final SkillService skills;
    private final CharacterInfoMapper characters;

    public void validate(String ownerId, String ext, String draft) {
        Set<String> enabled =
                new java.util.HashSet<>(CharacterSkillConfiguration.parse(ext).enabled());
        enabled.addAll(CharacterSkillConfiguration.parse(CharacterSkillConfiguration.draftExt(draft))
                .enabled());
        if (enabled.isEmpty()) {
            return;
        }
        Set<String> owned =
                skills.list(ownerId).stream().map(SkillService.Summary::skillId).collect(Collectors.toSet());
        if (!owned.containsAll(enabled)) {
            throw new SkillException(SkillException.Code.SKILL_ACCESS_DENIED);
        }
    }

    public String validateUpdate(Long characterId, String ext, String draft) {
        CharacterInfo character = characters
                .selectByPrimaryKey(characterId)
                .orElseThrow(() -> new SkillException(SkillException.Code.SKILL_NOT_FOUND));
        validate(character.getUserId(), ext, draft);
        return character.getUserId();
    }

    public void validatePublication(Long characterId) {
        CharacterInfo character = characters
                .selectByPrimaryKey(characterId)
                .orElseThrow(() -> new SkillException(SkillException.Code.SKILL_NOT_FOUND));
        String draftExt = CharacterSkillConfiguration.draftExt(character.getDraft());
        validate(character.getUserId(), draftExt == null ? character.getExt() : draftExt, null);
    }
}
