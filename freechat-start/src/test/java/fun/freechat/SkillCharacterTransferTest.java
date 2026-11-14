package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.util.CharacterTransferUtils;
import fun.freechat.service.skills.CharacterSkillConfiguration;
import fun.freechat.service.tools.ToolArguments;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SkillCharacterTransferTest {
    @Test
    void optionalMediaDirectoriesCanBeAbsentInTextOnlyCharacterArchives(@TempDir Path root) {
        CharacterConfigurationDTO configuration = CharacterConfigurationDTO.builder()
                .character(
                        CharacterCreateDTO.builder().name("Text-only character").build())
                .backends(List.of())
                .resourceUrls(Map.of())
                .ragTasks(List.of())
                .build();

        assertDoesNotThrow(() -> CharacterTransferUtils.validate(configuration, root));
    }

    @Test
    void configurationRoundTripNeverRetainsTransferredSkillIdsInFormalOrDraftExtensions() {
        String ext = ToolArguments.result(
                Map.of("skills", Map.of("enabled", List.of("a".repeat(32))), "custom", Map.of("keep", 42)));
        CharacterCreateDTO character = new CharacterCreateDTO();
        character.setExt(CharacterSkillConfiguration.withoutSkills(ext));
        character.setDraft(CharacterSkillConfiguration.withoutDraftSkills(
                ToolArguments.result(Map.of("ext", ext, "profile", "persona"))));
        CharacterConfigurationDTO result = CharacterConfigurationDTO.fromJson(CharacterConfigurationDTO.builder()
                .character(character)
                .backends(List.of())
                .build()
                .toJson());
        assertFalse(ToolArguments.parse(result.getCharacter().getExt()).has("skills"));
        var draft = ToolArguments.parse(result.getCharacter().getDraft());
        assertEquals("persona", draft.path("profile").asText());
        assertFalse(ToolArguments.parse(draft.path("ext").asText()).has("skills"));
        assertEquals(
                42,
                ToolArguments.parse(result.getCharacter().getExt())
                        .path("custom")
                        .path("keep")
                        .asInt());
    }
}
