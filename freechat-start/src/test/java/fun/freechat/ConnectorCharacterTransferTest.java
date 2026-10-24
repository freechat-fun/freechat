package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.api.CharacterApi;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.api.dto.conf.CharacterConfigurationDTO;
import fun.freechat.api.dto.conf.ConnectorConfigurationDTO;
import fun.freechat.api.util.CharacterUtils;
import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.connectors.*;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolRegistry;
import fun.freechat.util.TarUtils;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

class ConnectorCharacterTransferTest {
    private static final String SOURCE = "a".repeat(32);
    private static final String DRAFT_SOURCE = "b".repeat(32);
    private static final String FOREIGN = "f".repeat(32);
    private static final String SECRET = "PRIVATE_CONNECTOR_HEADER_SENTINEL";
    private final CharacterService characters = fake(CharacterService.class);
    private final ConnectorService connectors = fake(ConnectorService.class);
    private final List<ConnectorService.Summary> imported = new ArrayList<>();
    private final List<ConnectorConfiguration> importedConfigurations = new ArrayList<>();
    private final CharacterApi api = new CharacterApi();
    private final CharacterInfo source = new CharacterInfo()
            .withCharacterId(1L)
            .withUserId("creator")
            .withName("Persona")
            .withVisibility("public")
            .withExt(ext(SOURCE))
            .withDraft(draft(ext(DRAFT_SOURCE)));
    private SecurityContext previousSecurity;
    private Object previousCharacterService;

    @BeforeEach
    void setup() {
        previousSecurity = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        authenticate("creator", "ROLE_USER");
        previousCharacterService = ReflectionTestUtils.getField(CharacterUtils.class, "characterService");
        ReflectionTestUtils.setField(CharacterUtils.class, "characterService", characters);
        ReflectionTestUtils.setField(api, "characterService", characters);
        ReflectionTestUtils.setField(api, "connectorService", connectors);
        ReflectionTestUtils.setField(
                api, "connectorPolicy", new ConnectorCharacterPolicy(connectors, fake(CharacterInfoMapper.class)));
        ReflectionTestUtils.setField(api, "toolRegistry", new ToolRegistry(List.of()));
        ReflectionTestUtils.setField(api, "interactiveStatsService", fake(InteractiveStatsService.class));
        when(characters.details(eq(1L), any(User.class))).thenReturn(Triple.of(source, List.of(), List.of()));
        when(characters.create(any())).thenAnswer(invocation -> {
            Pair<CharacterInfo, List<String>> pair = invocation.getArgument(0);
            pair.getLeft().setCharacterId(2L);
            pair.getLeft().setCharacterUid("new-character");
            return true;
        });
        when(connectors.list("creator")).thenReturn(List.of(summary(SOURCE), summary(DRAFT_SOURCE)));
        when(connectors.list("importer")).thenAnswer(ignored -> List.copyOf(imported));
        when(connectors.create(eq("importer"), any(ConnectorConfiguration.class), eq(false)))
                .thenAnswer(invocation -> {
                    ConnectorConfiguration configuration = invocation.getArgument(1);
                    importedConfigurations.add(configuration);
                    String id = "%032x".formatted(imported.size() + 100);
                    var created = new ConnectorService.Summary(
                            id,
                            "fresh-revision-" + id,
                            configuration.name(),
                            configuration.transport(),
                            configuration.url(),
                            configuration.timeout(),
                            false,
                            configuration.headers().keySet(),
                            Instant.EPOCH);
                    imported.add(created);
                    return created;
                });
    }

    @AfterEach
    void restore() {
        ReflectionTestUtils.setField(CharacterUtils.class, "characterService", previousCharacterService);
        SecurityContextHolder.setContext(previousSecurity);
    }

    @Test
    void exportDefaultsToExclusionAndNeverLoadsRegistry(@TempDir Path root) throws Exception {
        var method = CharacterApi.class.getMethod(
                "exportCharacter", Long.class, boolean.class, jakarta.servlet.http.HttpServletResponse.class);
        assertEquals(
                "false",
                method.getParameters()[1].getAnnotation(RequestParam.class).defaultValue());
        JsonNode archive = export(false, root);
        assertFalse(archive.has("connectors"));
        assertCleared(
                archive.path("character").path("ext").asText(),
                archive.path("character").path("draft").asText());
        assertEquals(
                Set.of(SOURCE),
                CharacterConnectorConfiguration.parse(source.getExt()).enabled());
        verifyNoInteractions(connectors);
    }

    @Test
    void ownerCanExportOnlyRedactedReferencedDescriptors(@TempDir Path root) throws Exception {
        when(connectors.resolve("creator", SOURCE))
                .thenReturn(new ConnectorService.Resolved(
                        SOURCE,
                        "creator",
                        "live-revision",
                        new ConnectorConfiguration(
                                "Source", "http", "https://example.com/mcp", 60_000, Map.of("Authorization", SECRET)),
                        true));
        JsonNode archive = export(true, root);
        assertEquals(2, archive.path("connectors").size());
        for (JsonNode descriptor : archive.path("connectors")) {
            Set<String> fields = new HashSet<>();
            descriptor.fieldNames().forEachRemaining(fields::add);
            assertEquals(Set.of("reference", "name", "transport", "url", "timeout", "requiredHeaderNames"), fields);
            assertEquals(
                    "Authorization",
                    descriptor.path("requiredHeaderNames").get(0).asText());
        }
        String json = archive.toString();
        assertFalse(json.contains(SECRET));
        assertFalse(json.contains("creator"));
        assertFalse(json.contains("revision"));
        assertFalse(json.contains("active"));
        assertFalse(json.contains("\"headers\""));
        assertEquals(
                Set.of(SOURCE),
                CharacterConnectorConfiguration.parse(
                                archive.path("character").path("ext").asText())
                        .enabled());
        assertEquals(2, CharacterConfigurationDTO.fromJson(json).getConnectors().size());
        verify(connectors).list("creator");
        verify(connectors, never()).resolve(anyString(), anyString());
        verify(connectors, never()).get(anyString(), anyString());
    }

    @Test
    void exportIncludesTheBoundedUnionOfFormalAndDraftReferences(@TempDir Path root) throws Exception {
        List<String> ids = java.util.stream.IntStream.range(0, 16)
                .mapToObj(i -> "%032x".formatted(i))
                .toList();
        source.setExt(ext(ids.subList(0, 8).toArray(String[]::new)));
        source.setDraft(draft(ext(ids.subList(8, 16).toArray(String[]::new))));
        when(connectors.list("creator"))
                .thenReturn(ids.stream()
                        .map(ConnectorCharacterTransferTest::summary)
                        .toList());
        assertEquals(16, export(true, root).path("connectors").size());
        source.setDraft(draft(source.getExt()));
        assertEquals(8, export(true, root).path("connectors").size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN"})
    void publicViewerAndOtherOwnerAdministratorCannotIncludeDefinitions(String role, @TempDir Path root)
            throws Exception {
        authenticate("viewer", role);
        assertEquals(
                HttpStatus.FORBIDDEN,
                assertThrows(
                                ResponseStatusException.class,
                                () -> api.exportCharacter(1L, true, new MockHttpServletResponse()))
                        .getStatusCode());
        assertFalse(export(false, root).has("connectors"));
        verifyNoInteractions(connectors);
    }

    @Test
    void exportRequiresAuthenticationBeforeAnyRegistryRead() {
        SecurityContextHolder.clearContext();
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                assertThrows(
                                ResponseStatusException.class,
                                () -> api.exportCharacter(1L, true, new MockHttpServletResponse()))
                        .getStatusCode());
        verifyNoInteractions(connectors, characters);
    }

    @Test
    void ownerCannotExportForeignReferencesEvenWithAdministratorAuthority() {
        authenticate("creator", "ROLE_ADMIN");
        source.setDraft(draft(ext(FOREIGN)));
        assertEquals(
                ConnectorException.Code.CONNECTOR_ACCESS_DENIED,
                assertThrows(
                                ConnectorException.class,
                                () -> api.exportCharacter(1L, true, new MockHttpServletResponse()))
                        .code());
        verify(connectors, never()).resolve(anyString(), anyString());
    }

    @Test
    void includedArchiveImportsFreshPrivateInactiveCopiesAndRemapsBothSelections(@TempDir Path root) throws Exception {
        JsonNode archive = export(true, root);
        authenticate("importer", "ROLE_USER");
        CharacterInfo importedCharacter = importJson(archive.toString(), root);
        assertEquals("importer", importedCharacter.getUserId());
        assertEquals("private", importedCharacter.getVisibility());
        assertEquals(2, imported.size());
        assertTrue(imported.stream().noneMatch(ConnectorService.Summary::active));
        assertTrue(importedConfigurations.stream().allMatch(c -> c.headers().equals(Map.of("Authorization", ""))));
        assertEquals(
                Set.of(imported.get(0).connectorId()),
                CharacterConnectorConfiguration.parse(importedCharacter.getExt())
                        .enabled());
        assertEquals(
                Set.of(imported.get(1).connectorId()),
                CharacterConnectorConfiguration.parse(
                                CharacterConnectorConfiguration.draftExt(importedCharacter.getDraft()))
                        .enabled());
        assertFalse(importedCharacter.getExt().contains(SOURCE));
        assertFalse(importedCharacter.getDraft().contains(DRAFT_SOURCE));
        assertEquals(
                42,
                ToolArguments.parse(importedCharacter.getExt()).path("custom").asInt());
        assertEquals(
                "persona",
                ToolArguments.parse(importedCharacter.getDraft())
                        .path("profile")
                        .asText());
        verify(connectors, never()).resolve(anyString(), anyString());
        verify(connectors, never()).get(anyString(), anyString());
        verify(connectors, never()).create(anyString(), any(), eq(true));
        verify(connectors, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    void hostileRawIdsAreStrippedWithoutDescriptors(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_ADMIN");
        CharacterInfo importedCharacter = importJson(archive(ext(SOURCE, FOREIGN), draft(ext(FOREIGN)), null), root);
        assertCleared(importedCharacter.getExt(), importedCharacter.getDraft());
        verifyNoInteractions(connectors);
    }

    @Test
    void onlyReferencesDeclaredByDescriptorsAreRemapped(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        CharacterInfo importedCharacter = importJson(
                archive(
                        ext(SOURCE, FOREIGN),
                        draft(ext(FOREIGN)),
                        List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))),
                root);
        assertEquals(
                Set.of(imported.getFirst().connectorId()),
                CharacterConnectorConfiguration.parse(importedCharacter.getExt())
                        .enabled());
        assertTrue(CharacterConnectorConfiguration.parse(
                        CharacterConnectorConfiguration.draftExt(importedCharacter.getDraft()))
                .enabled()
                .isEmpty());
        assertFalse(importedCharacter.getExt().contains(FOREIGN));
        assertFalse(importedCharacter.getDraft().contains(FOREIGN));
        verify(connectors, never()).get(anyString(), anyString());
        verify(connectors, never()).resolve(anyString(), anyString());
    }

    @Test
    void cloneClearsFormalAndDraftBindingsWithoutCopyingConnectorRows() {
        authenticate("viewer", "ROLE_USER");
        when(characters.getUid(1L)).thenReturn("source-character");
        assertEquals(2L, api.clone(1L));
        assertEquals("viewer", source.getUserId());
        assertEquals("private", source.getVisibility());
        assertEquals("source-character", source.getParentUid());
        assertCleared(source.getExt(), source.getDraft());
        verifyNoInteractions(connectors);
    }

    @ParameterizedTest
    @ValueSource(strings = {"headers", "credentials", "ownerId", "active", "revision", "configuration", "unexpected"})
    void unexpectedDescriptorFieldsAreRejectedRatherThanSilentlyDiscarded(String field, @TempDir Path root)
            throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(0)).put(field, SECRET);
        assertThrows(ResponseStatusException.class, () -> importJson(archive.toString(), root));
        verifyNoInteractions(connectors);
        verify(characters, never()).create(any());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"transport\":\"stdio\"}",
                "{\"transport\":\"HTTP\"}",
                "{\"timeout\":\"60000\"}",
                "{\"timeout\":0}",
                "{\"timeout\":120001}",
                "{\"reference\":\"not-an-id\"}",
                "{\"name\":\"\"}",
                "{\"url\":\"file:///tmp/mcp\"}",
                "{\"url\":\"https://example.com/mcp?token=secret\"}",
                "{\"requiredHeaderNames\":[1]}",
                "{\"requiredHeaderNames\":[\"Authorization\",\"authorization\"]}",
                "{\"requiredHeaderNames\":[\"Authorization\",\"Authorization\"]}",
                "{\"requiredHeaderNames\":[\"Host\"]}"
            })
    void descriptorSyntaxAndHeaderNamesAreValidatedWithoutWrites(String patch, @TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(0)).setAll((ObjectNode) ToolArguments.parse(patch));
        assertThrows(ResponseStatusException.class, () -> importJson(archive.toString(), root));
        verifyNoInteractions(connectors);
        verify(characters, never()).create(any());
    }

    @Test
    void entireDescriptorListIsValidatedBeforeAnyWrite(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(archive(
                ext(SOURCE),
                null,
                List.of(
                        ConnectorConfigurationDTO.from(summary(SOURCE)),
                        ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(1)).put("url", "https://user:password@example.com/mcp");
        assertThrows(ResponseStatusException.class, () -> importJson(archive.toString(), root));
        verifyNoInteractions(connectors);
        verify(characters, never()).create(any());
    }

    @Test
    void characterCreationFailureDeletesOnlyFreshRowsWithTheirExactRevisions(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        doReturn(false).when(characters).create(any());
        assertThrows(
                ResponseStatusException.class,
                () -> importJson(
                        archive(
                                ext(SOURCE),
                                draft(ext(DRAFT_SOURCE)),
                                List.of(
                                        ConnectorConfigurationDTO.from(summary(SOURCE)),
                                        ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)))),
                        root));
        assertEquals(2, imported.size());
        for (var created : imported) {
            verify(connectors).delete("importer", created.connectorId(), created.revision());
        }
        verify(connectors, never()).delete(anyString(), eq(SOURCE), anyString());
        verify(connectors, never()).delete(anyString(), eq(DRAFT_SOURCE), anyString());
    }

    @Test
    void failedSecondConnectorCreationCleansFirstWithoutCreatingCharacter(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        doThrow(new ConnectorException(ConnectorException.Code.CONNECTOR_LIMIT_EXCEEDED))
                .when(connectors)
                .create(eq("importer"), argThat(c -> "Draft".equals(c.name())), eq(false));
        assertThrows(
                ConnectorException.class,
                () -> importJson(
                        archive(
                                ext(SOURCE),
                                draft(ext(DRAFT_SOURCE)),
                                List.of(
                                        ConnectorConfigurationDTO.from(summary(SOURCE)),
                                        ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)))),
                        root));
        assertEquals(1, imported.size());
        verify(connectors)
                .delete(
                        "importer",
                        imported.getFirst().connectorId(),
                        imported.getFirst().revision());
        verify(characters, never()).create(any());
    }

    @Test
    void failureAfterCharacterCreationCleansNewConnectorsThroughTheImportBoundary() throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode document = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        document.remove("backends");
        byte[] json = document.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String manifestText = ReflectionTestUtils.invokeMethod(new CharacterApi.ManifestInfo(), "toYaml");
        byte[] manifest = Objects.requireNonNull(manifestText).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        var bytes = new java.io.ByteArrayOutputStream();
        TarUtils.compressGzip(
                List.of(
                        Triple.of("manifest.yml", (java.io.InputStream) new ByteArrayInputStream(manifest), (long)
                                manifest.length),
                        Triple.of("character.json", (java.io.InputStream) new ByteArrayInputStream(json), (long)
                                json.length)),
                bytes);
        var file = new org.springframework.mock.web.MockMultipartFile(
                "file", "connector.tar.gz", "application/gzip", bytes.toByteArray());
        assertThrows(
                ResponseStatusException.class,
                () -> api.importCharacter(new org.springframework.mock.web.MockHttpServletRequest(), file));
        verify(characters).create(any());
        assertEquals(1, imported.size());
        var created = imported.getFirst();
        verify(connectors).delete("importer", created.connectorId(), created.revision());
        verify(connectors, never()).delete(anyString(), eq(SOURCE), anyString());
    }

    @Test
    void duplicateAndOversizedDescriptorListsAreRejectedBeforeWrites(@TempDir Path root) throws Exception {
        authenticate("importer", "ROLE_USER");
        var descriptor = ConnectorConfigurationDTO.from(summary(SOURCE));
        for (List<ConnectorConfigurationDTO> invalid :
                List.of(List.of(descriptor, descriptor), Collections.nCopies(17, descriptor))) {
            assertThrows(ResponseStatusException.class, () -> importJson(archive(ext(SOURCE), null, invalid), root));
        }
        verifyNoInteractions(connectors);
        verify(characters, never()).create(any());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"headers\":\"PRIVATE_CONNECTOR_HEADER_SENTINEL\"}",
                "{\"character\":{},\"connectors\":[null]}",
                "{\"character\":{},\"connectors\":[],\"connectors\":[]}",
                "{\"character\":{},\"connectors\":{}}",
                "{\"character\":{}} trailing"
            })
    void malformedArchiveErrorsDoNotExposePayload(String json) {
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, () -> CharacterConfigurationDTO.fromJson(json));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertFalse(error.getMessage().contains(SECRET));
        assertNull(error.getCause());
    }

    @Test
    void importAndSerializationLogsNeverIncludeJsonOrJacksonExceptionPayloads() {
        Logger logger = (Logger) LoggerFactory.getLogger(CharacterConfigurationDTO.class);
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        boolean additive = logger.isAdditive();
        logger.setAdditive(false);
        logger.addAppender(events);
        try {
            assertThrows(
                    ResponseStatusException.class,
                    () -> CharacterConfigurationDTO.fromJson("{\"character\":\"" + SECRET));
            CharacterCreateDTO malicious = new CharacterCreateDTO() {
                @Override
                public String getName() {
                    throw new IllegalStateException(SECRET);
                }
            };
            assertThrows(
                    ResponseStatusException.class,
                    () -> CharacterConfigurationDTO.builder()
                            .character(malicious)
                            .build()
                            .toJson());
            assertEquals(2, events.list.size());
            for (ILoggingEvent event : events.list) {
                assertFalse(event.getFormattedMessage().contains(SECRET));
                assertNull(event.getThrowableProxy());
                assertNull(event.getArgumentArray());
            }
        } finally {
            logger.detachAppender(events);
            logger.setAdditive(additive);
            events.stop();
        }
    }

    private JsonNode export(boolean includeConnectors, Path root) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        api.exportCharacter(1L, includeConnectors, response);
        TarUtils.extractGzip(new ByteArrayInputStream(response.getContentAsByteArray()), root);
        return ToolArguments.parse(Files.readString(root.resolve("character.json")));
    }

    private CharacterInfo importJson(String json, Path root) throws Exception {
        Files.writeString(root.resolve("character.json"), json);
        Pair<CharacterInfo, List<CharacterBackend>> importedCharacter =
                ReflectionTestUtils.invokeMethod(api, "handleCharacterJson", root, new LinkedHashMap<String, String>());
        return Objects.requireNonNull(importedCharacter).getLeft();
    }

    private static String archive(String ext, String draft, List<ConnectorConfigurationDTO> descriptors) {
        return CharacterConfigurationDTO.builder()
                .character(CharacterCreateDTO.builder()
                        .name("Persona")
                        .visibility("public")
                        .ext(ext)
                        .draft(draft)
                        .build())
                .backends(List.of())
                .connectors(descriptors)
                .build()
                .toJson();
    }

    private static String ext(String... ids) {
        return ToolArguments.result(Map.of("custom", 42, "connectors", Map.of("enabled", List.of(ids))));
    }

    private static String draft(String ext) {
        return ToolArguments.result(Map.of("profile", "persona", "ext", ext));
    }

    private static void assertCleared(String ext, String draft) {
        assertFalse(ToolArguments.parse(ext).has("connectors"));
        assertEquals(42, ToolArguments.parse(ext).path("custom").asInt());
        assertFalse(ToolArguments.parse(CharacterConnectorConfiguration.draftExt(draft))
                .has("connectors"));
        assertEquals("persona", ToolArguments.parse(draft).path("profile").asText());
    }

    private static ConnectorService.Summary summary(String id) {
        return new ConnectorService.Summary(
                id,
                "source-revision",
                id.equals(SOURCE) ? "Source" : "Draft",
                "http",
                "https://example.com/mcp",
                60_000,
                true,
                Set.of("Authorization"),
                Instant.EPOCH);
    }

    private static void authenticate(String id, String role) {
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        new User().withUserId(id), null, List.of(new SimpleGrantedAuthority(role))));
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
