package fun.freechat.service.connectors;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.tools.ToolArguments;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CharacterConnectorConfigurationTest {
    private static final String ID = "a".repeat(32);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                " ",
                "{}",
                "{\"connectors\":null}",
                "{\"connectors\":{}}",
                "{\"connectors\":{\"enabled\":null}}",
                "{\"connectors\":{\"enabled\":[]}}"
            })
    void absentOrEmptySelectionEnablesNothing(String ext) {
        assertEquals(Set.of(), CharacterConnectorConfiguration.parse(ext).enabled());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "[]",
                "{\"connectors\":[]}",
                "{\"connectors\":{\"enabled\":\"bad\"}}",
                "{\"connectors\":{\"enabled\":[1]}}",
                "{\"connectors\":{\"enabled\":[\"../foreign\"]}}",
                "{\"connectors\":{\"enabled\":[],\"ownerId\":\"foreign\"}}",
                "{\"connectors\":{},\"connectors\":{}}"
            })
    void rejectsMalformedAndPrivilegeBearingSelections(String ext) {
        assertEquals(
                ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION,
                assertThrows(ConnectorException.class, () -> CharacterConnectorConfiguration.parse(ext))
                        .code());
    }

    @Test
    void allowsOnlyEightUniqueLowercaseOpaqueIds() {
        List<String> eight =
                IntStream.range(0, 8).mapToObj(i -> "%032x".formatted(i)).toList();
        assertEquals(
                Set.copyOf(eight),
                CharacterConnectorConfiguration.parse(selection(eight)).enabled());
        for (List<String> invalid : List.of(
                List.of(ID, ID),
                List.of(ID.toUpperCase()),
                IntStream.range(0, 9).mapToObj(i -> "%032x".formatted(i)).toList())) {
            assertThrows(ConnectorException.class, () -> CharacterConnectorConfiguration.parse(selection(invalid)));
        }
    }

    @Test
    void removesAndRemapsOnlyConnectorFieldsIncludingDraft() {
        String ext = ToolArguments.result(Map.of(
                "connectors",
                Map.of("enabled", List.of(ID)),
                "skills",
                Map.of("enabled", List.of("b".repeat(32))),
                "custom",
                Map.of("keep", 42)));
        String clean = CharacterConnectorConfiguration.withoutConnectors(ext);
        assertFalse(ToolArguments.parse(clean).has("connectors"));
        assertTrue(ToolArguments.parse(clean).has("skills"));
        assertEquals(42, ToolArguments.parse(clean).path("custom").path("keep").asInt());
        String draft = ToolArguments.result(Map.of("ext", ext, "profile", "persona"));
        var stripped = ToolArguments.parse(CharacterConnectorConfiguration.withoutDraftConnectors(draft));
        assertEquals("persona", stripped.path("profile").asText());
        assertEquals(clean, CharacterConnectorConfiguration.draftExt(stripped.toString()));
        assertEquals(
                Set.of("c".repeat(32)),
                CharacterConnectorConfiguration.parse(
                                CharacterConnectorConfiguration.withEnabled(ext, Set.of("c".repeat(32))))
                        .enabled());
        assertEquals(clean, CharacterConnectorConfiguration.withEnabled(ext, Set.of()));
        assertEquals(
                Set.of(ID),
                CharacterConnectorConfiguration.parse(CharacterConnectorConfiguration.withEnabled(null, Set.of(ID)))
                        .enabled());
        assertNull(CharacterConnectorConfiguration.withEnabled(null, Set.of()));
        assertThrows(ConnectorException.class, () -> CharacterConnectorConfiguration.draftExt("{\"ext\":{}}"));
        assertThrows(ConnectorException.class, () -> CharacterConnectorConfiguration.withEnabled(ext, Set.of("bad")));
    }

    @Test
    void strippingDoesNotTrustOrValidateForeignConnectorPayloads() {
        assertEquals(
                "{\"custom\":42}",
                CharacterConnectorConfiguration.withoutConnectors(
                        "{\"custom\":42,\"connectors\":{\"headers\":{\"Authorization\":\"untrusted\"}}}"));
    }

    private static String selection(List<String> ids) {
        return ToolArguments.result(Map.of("connectors", Map.of("enabled", ids)));
    }
}
