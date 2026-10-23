package fun.freechat.service.skills;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.tools.CharacterToolConfiguration;
import fun.freechat.service.tools.ToolArguments;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CharacterSkillConfigurationTest {
    private static final String ID = "a".repeat(32);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                " ",
                "{}",
                "{\"skills\":null}",
                "{\"skills\":{}}",
                "{\"skills\":{\"enabled\":null}}",
                "{\"skills\":{\"enabled\":[]}}"
            })
    void existingCharactersHaveNoSkills(String ext) {
        assertEquals(Set.of(), CharacterSkillConfiguration.parse(ext).enabled());
    }

    @Test
    void selectedSkillsAddOnlyDefaultLoaderToolsAndNeverOverrideExplicitTools() {
        String ext = ToolArguments.result(Map.of("skills", Map.of("enabled", List.of(ID))));
        assertEquals(Set.of(ID), CharacterSkillConfiguration.parse(ext).enabled());
        assertTrue(CharacterToolConfiguration.parse(ext).enabled().containsAll(SkillTools.NAMES));
        var selected = ToolArguments.JSON.createObjectNode();
        selected.setAll((com.fasterxml.jackson.databind.node.ObjectNode) ToolArguments.parse(ext));
        selected.set("tools", ToolArguments.JSON.valueToTree(Map.of("enabled", List.of())));
        assertEquals(
                Set.of(), CharacterToolConfiguration.parse(selected.toString()).enabled());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "[]",
                "{\"skills\":[]}",
                "{\"skills\":{\"enabled\":[\"install-skill\"]}}",
                "{\"skills\":{\"enabled\":[1]}}",
                "{\"skills\":{\"enabled\":[],\"ownerId\":\"other\"}}",
                "{\"skills\":{},\"skills\":{}}"
            })
    void rejectsMalformedOrPrivilegeBearingConfiguration(String ext) {
        assertThrows(SkillException.class, () -> CharacterSkillConfiguration.parse(ext));
    }

    @Test
    void stripsTransferBindingsWithoutChangingOtherExtensionFields() {
        String ext = ToolArguments.result(Map.of(
                "skills",
                Map.of("enabled", List.of(ID)),
                "tools",
                Map.of("enabled", List.of("calculate")),
                "custom",
                Map.of("x", 42)));
        var clean = ToolArguments.parse(CharacterSkillConfiguration.withoutSkills(ext));
        assertFalse(clean.has("skills"));
        assertEquals(42, clean.path("custom").path("x").asInt());
        assertEquals("calculate", clean.path("tools").path("enabled").get(0).asText());
        String draft = ToolArguments.result(Map.of("ext", ext, "profile", "persona"));
        var transferred = ToolArguments.parse(CharacterSkillConfiguration.withoutDraftSkills(draft));
        assertEquals("persona", transferred.path("profile").asText());
        assertFalse(ToolArguments.parse(transferred.path("ext").asText()).has("skills"));
        assertThrows(SkillException.class, () -> CharacterSkillConfiguration.draftExt("{\"ext\":{}}"));
    }
}
