package fun.freechat.service.skills;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.service.tools.ToolArguments;
import java.util.LinkedHashSet;
import java.util.Set;

public record CharacterSkillConfiguration(Set<String> enabled) {
    public CharacterSkillConfiguration {
        enabled = Set.copyOf(enabled);
    }

    public static CharacterSkillConfiguration parse(String ext) {
        if (ext == null || ext.isBlank()) {
            return new CharacterSkillConfiguration(Set.of());
        }
        try {
            JsonNode skills = ToolArguments.parse(ext).get("skills");
            if (skills == null || skills.isNull()) {
                return new CharacterSkillConfiguration(Set.of());
            }
            ToolArguments.only(skills, "enabled");
            JsonNode selection = skills.get("enabled");
            if (selection == null || selection.isNull()) {
                return new CharacterSkillConfiguration(Set.of());
            }
            if (!selection.isArray() || selection.size() > 16) {
                throw invalid();
            }
            Set<String> ids = new LinkedHashSet<>();
            for (JsonNode value : selection) {
                if (!value.isTextual() || !value.textValue().matches("[0-9a-f]{32}") || !ids.add(value.textValue())) {
                    throw invalid();
                }
            }
            return new CharacterSkillConfiguration(ids);
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String withoutSkills(String ext) {
        if (ext == null || ext.isBlank()) {
            return ext;
        }
        try {
            ObjectNode root = (ObjectNode) ToolArguments.parse(ext);
            if (root.remove("skills") == null) {
                return ext;
            }
            return ToolArguments.result(root);
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String draftExt(String draft) {
        if (draft == null || draft.isBlank()) {
            return null;
        }
        try {
            JsonNode ext = ToolArguments.parse(draft).get("ext");
            if (ext == null || ext.isNull()) {
                return null;
            }
            if (!ext.isTextual()) {
                throw invalid();
            }
            return ext.textValue();
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String withoutDraftSkills(String draft) {
        if (draft == null || draft.isBlank()) {
            return draft;
        }
        String ext = draftExt(draft);
        if (ext == null) {
            return draft;
        }
        ObjectNode root = (ObjectNode) ToolArguments.parse(draft);
        root.put("ext", withoutSkills(ext));
        return ToolArguments.result(root);
    }

    private static SkillException invalid() {
        return new SkillException(SkillException.Code.SKILL_INVALID_FORMAT);
    }
}
