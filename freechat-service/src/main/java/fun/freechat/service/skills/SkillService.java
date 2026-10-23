package fun.freechat.service.skills;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface SkillService {
    record Summary(String skillId, String revision, String name, String description, Instant modified) {}

    record Installed(String skillId, String ownerId, String revision, SkillBundle bundle, Instant modified) {}

    record Draft(String draftId, String skillId, String expectedRevision, SkillBundle bundle, Instant expiresAt) {}

    List<Summary> list(String ownerId);

    Installed get(String ownerId, String skillId);

    Draft prepare(String ownerId, String skillId, String expectedRevision, Map<String, String> files);

    Installed confirm(String ownerId, String draftId, String digest);

    default Installed update(String ownerId, String skillId, String expectedRevision, Map<String, String> files) {
        Draft draft = prepare(ownerId, skillId, expectedRevision, files);
        return confirm(ownerId, draft.draftId(), draft.bundle().digest());
    }

    void delete(String ownerId, String skillId, String expectedRevision);
}
