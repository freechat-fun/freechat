package fun.freechat.service.skills;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import fun.freechat.mapper.OrgRelationshipDynamicSqlSupport;
import fun.freechat.mapper.OrgRelationshipMapper;
import fun.freechat.model.CharacterInfo;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkillCharacterAccess {
    private final OrgRelationshipMapper relationships;

    public boolean permits(String viewer, CharacterInfo character) {
        if (viewer.equals(character.getUserId())) return true;
        if ("public".equals(character.getVisibility())) return true;
        return "public_org".equals(character.getVisibility())
                && (connected(character.getUserId(), viewer, true) || connected(character.getUserId(), viewer, false));
    }

    private boolean connected(String start, String target, boolean owners) {
        Set<String> seen = new HashSet<>();
        Set<String> frontier = Set.of(start);
        for (int depth = 0; depth < 16 && !frontier.isEmpty(); depth++) {
            seen.addAll(frontier);
            Set<String> current = frontier;
            var rows = relationships.select(query -> query.where(
                            owners ? OrgRelationshipDynamicSqlSupport.userId : OrgRelationshipDynamicSqlSupport.ownerId,
                            isIn(current))
                    .and(OrgRelationshipDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                    .and(OrgRelationshipDynamicSqlSupport.isVirtual, isEqualTo((byte) 0))
                    .limit(257));
            if (rows.size() > 256) return false;
            Set<String> next = new HashSet<>();
            for (var row : rows) {
                String id = owners ? row.getOwnerId() : row.getUserId();
                if (target.equals(id)) return true;
                if (!seen.contains(id)) next.add(id);
            }
            if (seen.size() + next.size() > 256) return false;
            frontier = next;
        }
        return false;
    }
}
