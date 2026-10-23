package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.api.CharacterApi;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.skills.CharacterSkillConfiguration;
import fun.freechat.service.tools.ToolArguments;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

class SkillCharacterTransferTest {
    @Test
    void optionalMediaDirectoriesCanBeAbsentInTextOnlyCharacterArchives(@TempDir Path root) {
        CharacterApi api = new CharacterApi();
        CharacterInfo character = new CharacterInfo().withCharacterUid("character");
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(api, "handleAvatar", null, "owner", character, root));
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(api, "handlePictures", "owner", character, root));
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(api, "handleVideos", "owner", character, root));
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(api, "handleDocuments", "owner", character, root));
        assertDoesNotThrow(
                () -> ReflectionTestUtils.invokeMethod(api, "handleVoices", "owner", character, List.of(), root));
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
