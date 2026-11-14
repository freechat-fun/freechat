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
import fun.freechat.service.common.FileStore;
import fun.freechat.service.connectors.*;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolRegistry;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.TarUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

class ConnectorCharacterTransferTest {
    private static final String SOURCE = "a".repeat(32);
    private static final String DRAFT_SOURCE = "b".repeat(32);
    private static final String FOREIGN = "f".repeat(32);
    private static final String SECRET = "PRIVATE_CONNECTOR_HEADER_SENTINEL";
    private final CharacterService characters = fake(CharacterService.class);
    private final ConnectorService connectors = fake(ConnectorService.class);
    private final FileStore store = fake(FileStore.class);
    private final PlatformTransactionManager transactions = fake(PlatformTransactionManager.class);
    private final List<ConnectorService.Summary> imported = new ArrayList<>();
    private final List<ConnectorConfiguration> importedConfigurations = new ArrayList<>();
    private final CharacterApi api = new CharacterApi();
    private final CharacterInfo source = new CharacterInfo()
            .withCharacterId(1L)
            .withVersion(1)
            .withUserId("creator")
            .withName("Persona")
            .withVisibility("public")
            .withExt(ext(SOURCE))
            .withDraft(draft(ext(DRAFT_SOURCE)));
    private SecurityContext previousSecurity;
    private Object previousCharacterService;
    private Object previousFileStore;
    private CharacterInfo createdCharacter;
    private CharacterInfo updatedCharacter;

    @BeforeEach
    void setup() {
        previousSecurity = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        authenticate("creator", "ROLE_USER");
        previousCharacterService = ReflectionTestUtils.getField(CharacterUtils.class, "characterService");
        previousFileStore = ReflectionTestUtils.getField(StoreUtils.class, "localFileStore");
        ReflectionTestUtils.setField(CharacterUtils.class, "characterService", characters);
        ReflectionTestUtils.setField(StoreUtils.class, "localFileStore", store);
        ReflectionTestUtils.setField(api, "transactionManager", transactions);
        when(transactions.getTransaction(any())).thenAnswer(ignored -> new SimpleTransactionStatus());
        ReflectionTestUtils.setField(api, "characterService", characters);
        ReflectionTestUtils.setField(api, "connectorService", connectors);
        ReflectionTestUtils.setField(
                api, "connectorPolicy", new ConnectorCharacterPolicy(connectors, fake(CharacterInfoMapper.class)));
        ReflectionTestUtils.setField(api, "toolRegistry", new ToolRegistry(List.of()));
        ReflectionTestUtils.setField(api, "interactiveStatsService", fake(InteractiveStatsService.class));
        when(characters.details(eq(1L), any(User.class))).thenReturn(Triple.of(source, List.of(), List.of()));
        when(characters.create(any())).thenAnswer(invocation -> {
            Pair<CharacterInfo, List<String>> pair = invocation.getArgument(0);
            createdCharacter = pair.getLeft();
            createdCharacter.setCharacterId(2L);
            createdCharacter.setCharacterUid("new-character");
            return true;
        });
        when(characters.update(any())).thenAnswer(invocation -> {
            Pair<CharacterInfo, List<String>> pair = invocation.getArgument(0);
            updatedCharacter = pair.getLeft();
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
        try {
            ReflectionTestUtils.setField(CharacterUtils.class, "characterService", previousCharacterService);
        } finally {
            try {
                ReflectionTestUtils.setField(StoreUtils.class, "localFileStore", previousFileStore);
            } finally {
                SecurityContextHolder.setContext(previousSecurity);
            }
        }
    }

    @Test
    void oversizedBackendListIsRejectedBeforeWritingAnArchive() {
        when(characters.details(eq(1L), any(User.class)))
                .thenReturn(Triple.of(
                        source,
                        List.of(),
                        Collections.nCopies(
                                fun.freechat.api.util.CharacterTransferUtils.MAX_BACKENDS + 1,
                                new CharacterBackend())));
        var response = new MockHttpServletResponse();
        ResponseStatusException failure = assertThrows(
                ResponseStatusException.class,
                () -> api.exportCharacter(1L, false, false, new MockHttpServletRequest(), response));
        assertEquals(HttpStatus.BAD_REQUEST, failure.getStatusCode());
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    void exportDefaultsToExclusionAndNeverLoadsRegistry(@TempDir Path root) throws Exception {
        var method = CharacterApi.class.getMethod(
                "exportCharacter",
                Long.class,
                boolean.class,
                boolean.class,
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class);
        assertEquals(
                "false",
                method.getParameters()[1].getAnnotation(RequestParam.class).defaultValue());
        assertEquals(
                "false",
                method.getParameters()[2].getAnnotation(RequestParam.class).defaultValue());
        JsonNode archive = export(false, root);
        assertFalse(archive.has("connectors"));
        assertClearedExt(archive.path("character").path("ext").asText());
        assertNull(CharacterConfigurationDTO.fromJson(archive.toString())
                .getCharacter()
                .getDraft());
        assertTrue(archive.path("resourceUrls").isObject());
        assertTrue(archive.path("resourceUrls").isEmpty());
        assertTrue(archive.path("ragTasks").isArray());
        assertTrue(archive.path("ragTasks").isEmpty());
        assertTrue(archive.path("backends").isArray());
        assertTrue(archive.path("backends").isEmpty());
        assertFalse(archive.toString().contains(DRAFT_SOURCE));
        assertEquals(
                Set.of(SOURCE),
                CharacterConnectorConfiguration.parse(source.getExt()).enabled());
        assertEquals(draft(ext(DRAFT_SOURCE)), source.getDraft());
        assertEquals("public", source.getVisibility());
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
        assertEquals(1, archive.path("connectors").size());
        assertEquals(SOURCE, archive.path("connectors").get(0).path("reference").asText());
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
        assertEquals(1, CharacterConfigurationDTO.fromJson(json).getConnectors().size());
        assertNull(CharacterConfigurationDTO.fromJson(json).getCharacter().getDraft());
        assertFalse(json.contains(DRAFT_SOURCE));
        assertEquals(draft(ext(DRAFT_SOURCE)), source.getDraft());
        verify(connectors).list("creator");
        verify(connectors, never()).resolve(anyString(), anyString());
        verify(connectors, never()).get(anyString(), anyString());
    }

    @Test
    void exportIncludesOnlyTheBoundedFormalReferencesAndDiscardsDraft(@TempDir Path root) throws Exception {
        List<String> ids = java.util.stream.IntStream.range(0, 16)
                .mapToObj(i -> "%032x".formatted(i))
                .toList();
        source.setExt(ext(ids.subList(0, 8).toArray(String[]::new)));
        source.setDraft(draft(ext(ids.subList(8, 16).toArray(String[]::new))));
        when(connectors.list("creator"))
                .thenReturn(ids.stream()
                        .map(ConnectorCharacterTransferTest::summary)
                        .toList());
        JsonNode archive = export(true, root);
        Set<String> references = new HashSet<>();
        archive.path("connectors")
                .forEach(descriptor ->
                        references.add(descriptor.path("reference").asText()));
        assertEquals(8, archive.path("connectors").size());
        assertEquals(new HashSet<>(ids.subList(0, 8)), references);
        assertNull(CharacterConfigurationDTO.fromJson(archive.toString())
                .getCharacter()
                .getDraft());
        for (String draftReference : ids.subList(8, 16)) {
            assertFalse(archive.toString().contains(draftReference));
        }
        source.setDraft(draft(source.getExt()));
        JsonNode overlapping = export(true, root);
        assertEquals(8, overlapping.path("connectors").size());
        assertNull(CharacterConfigurationDTO.fromJson(overlapping.toString())
                .getCharacter()
                .getDraft());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void unpublishedCharacterCannotBeExportedBeforeAnyRegistryRead(Integer version) {
        source.setVersion(version);
        for (boolean includeConnectors : List.of(false, true)) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    assertThrows(
                                    ResponseStatusException.class,
                                    () -> api.exportCharacter(
                                            1L, includeConnectors, false, new MockHttpServletRequest(), response))
                            .getStatusCode());
            assertEquals(0, response.getContentAsByteArray().length);
        }
        verifyNoInteractions(connectors, store);
    }

    @Test
    void draftOnlyReferencesDoNotRequireRegistryAccess(@TempDir Path root) throws Exception {
        source.setExt(ext());
        source.setDraft(draft(ext(FOREIGN)));
        JsonNode archive = export(true, root);
        assertTrue(archive.path("connectors").isArray());
        assertTrue(archive.path("connectors").isEmpty());
        assertNull(CharacterConfigurationDTO.fromJson(archive.toString())
                .getCharacter()
                .getDraft());
        assertFalse(archive.toString().contains(FOREIGN));
        verifyNoInteractions(connectors);
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
                                () -> api.exportCharacter(
                                        1L, true, false, new MockHttpServletRequest(), new MockHttpServletResponse()))
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
                                () -> api.exportCharacter(
                                        1L, true, false, new MockHttpServletRequest(), new MockHttpServletResponse()))
                        .getStatusCode());
        verifyNoInteractions(connectors, characters);
    }

    @Test
    void ownerCannotExportForeignReferencesEvenWithAdministratorAuthority() {
        authenticate("creator", "ROLE_ADMIN");
        source.setExt(ext(FOREIGN));
        assertEquals(
                ConnectorException.Code.CONNECTOR_ACCESS_DENIED,
                assertThrows(
                                ConnectorException.class,
                                () -> api.exportCharacter(
                                        1L, true, false, new MockHttpServletRequest(), new MockHttpServletResponse()))
                        .code());
        verify(connectors, never()).resolve(anyString(), anyString());
    }

    @Test
    void includedArchiveImportsFreshPrivateInactiveCopyAndRemapsOnlyFormalSelection(@TempDir Path root)
            throws Exception {
        JsonNode archive = export(true, root);
        authenticate("importer", "ROLE_USER");
        CharacterInfo importedCharacter = importJson(archive.toString());
        assertEquals("importer", importedCharacter.getUserId());
        assertEquals("private", importedCharacter.getVisibility());
        assertEquals(1, imported.size());
        assertTrue(imported.stream().noneMatch(ConnectorService.Summary::active));
        assertTrue(importedConfigurations.stream().allMatch(c -> c.headers().equals(Map.of("Authorization", ""))));
        assertEquals(
                Set.of(imported.getFirst().connectorId()),
                CharacterConnectorConfiguration.parse(importedCharacter.getExt())
                        .enabled());
        assertNull(importedCharacter.getDraft());
        assertFalse(importedCharacter.getExt().contains(SOURCE));
        assertFalse(importedCharacter.getExt().contains(DRAFT_SOURCE));
        assertFalse(importedCharacter.getExt().contains(SECRET));
        assertEquals(
                42,
                ToolArguments.parse(importedCharacter.getExt()).path("custom").asInt());
        verify(connectors).create(eq("importer"), any(ConnectorConfiguration.class), eq(false));
        verify(connectors, never()).create(argThat(owner -> !"importer".equals(owner)), any(), anyBoolean());
        verify(connectors, never()).resolve(anyString(), anyString());
        verify(connectors, never()).get(anyString(), anyString());
        verify(connectors, never()).create(anyString(), any(), eq(true));
        verify(connectors, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    void hostileRawIdsAreStrippedWithoutDescriptorsAndCharacterIsPrivate() throws Exception {
        authenticate("importer", "ROLE_ADMIN");
        CharacterInfo importedCharacter = importJson(archive(ext(SOURCE, FOREIGN), draft(ext(FOREIGN)), null));
        assertClearedExt(importedCharacter.getExt());
        assertNull(importedCharacter.getDraft());
        assertEquals("private", importedCharacter.getVisibility());
        verifyNoInteractions(connectors);
    }

    @Test
    void onlyReferencesDeclaredByDescriptorsAreRemapped() throws Exception {
        authenticate("importer", "ROLE_USER");
        CharacterInfo importedCharacter = importJson(archive(
                ext(SOURCE, FOREIGN), draft(ext(FOREIGN)), List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        assertEquals(1, imported.size());
        assertEquals(
                Set.of(imported.getFirst().connectorId()),
                CharacterConnectorConfiguration.parse(importedCharacter.getExt())
                        .enabled());
        assertNull(importedCharacter.getDraft());
        assertFalse(importedCharacter.getExt().contains(SOURCE));
        assertFalse(importedCharacter.getExt().contains(FOREIGN));
        verify(connectors, never()).get(anyString(), anyString());
        verify(connectors, never()).resolve(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"v2", "v3"})
    void draftOnlyAndUnreferencedDescriptorsAreNotCreated(String version) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode document = (ObjectNode) ToolArguments.parse(archive(
                ext(SOURCE),
                draft(ext(DRAFT_SOURCE)),
                List.of(
                        ConnectorConfigurationDTO.from(summary(SOURCE)),
                        ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)),
                        ConnectorConfigurationDTO.from(summary(FOREIGN)))));
        if ("v2".equals(version)) {
            document.remove(List.of("resourceUrls", "ragTasks"));
        }
        CharacterInfo importedCharacter = importJson(document.toString(), version);
        assertEquals(1, imported.size());
        assertEquals(
                Set.of(imported.getFirst().connectorId()),
                CharacterConnectorConfiguration.parse(importedCharacter.getExt())
                        .enabled());
        assertNull(importedCharacter.getDraft());
        assertFalse(importedCharacter.getExt().contains(SOURCE));
        assertFalse(importedCharacter.getExt().contains(DRAFT_SOURCE));
        assertFalse(importedCharacter.getExt().contains(FOREIGN));
        verify(connectors).create(eq("importer"), argThat(c -> "Source".equals(c.name())), eq(false));
        verify(connectors, never()).create(anyString(), argThat(c -> !"Source".equals(c.name())), anyBoolean());
        verify(connectors, never()).get(anyString(), anyString());
        verify(connectors, never()).resolve(anyString(), anyString());
        verify(connectors, never()).delete(anyString(), anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"v2", "v3"})
    void archivesWithOnlyDraftDescriptorsCreateNoConnectors(String version) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode document = (ObjectNode) ToolArguments.parse(archive(
                ext(), draft(ext(DRAFT_SOURCE)), List.of(ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)))));
        if ("v2".equals(version)) {
            document.remove(List.of("resourceUrls", "ragTasks"));
        }
        CharacterInfo importedCharacter = importJson(document.toString(), version);
        assertClearedExt(importedCharacter.getExt());
        assertNull(importedCharacter.getDraft());
        assertEquals("private", importedCharacter.getVisibility());
        assertTrue(imported.isEmpty());
        verifyNoInteractions(connectors);
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
    void unexpectedDescriptorFieldsAreRejectedRatherThanSilentlyDiscarded(String field) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(0)).put(field, SECRET);
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, () -> importJson(archive.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertFalse(error.getMessage().contains(SECRET));
        assertNull(error.getCause());
        verifyNoInteractions(connectors, transactions, store);
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
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
    void descriptorSyntaxAndHeaderNamesAreValidatedWithoutWrites(String patch) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(0)).setAll((ObjectNode) ToolArguments.parse(patch));
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, () -> importJson(archive.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertFalse(error.getMessage().contains(SECRET));
        assertNull(error.getCause());
        verifyNoInteractions(connectors, transactions, store);
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
    }

    @Test
    void entireDescriptorListIsValidatedBeforeAnyWrite() throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode archive = (ObjectNode) ToolArguments.parse(archive(
                ext(SOURCE),
                null,
                List.of(
                        ConnectorConfigurationDTO.from(summary(SOURCE)),
                        ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)))));
        ((ObjectNode) archive.path("connectors").get(1)).put("url", "https://user:password@example.com/mcp");
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, () -> importJson(archive.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertFalse(error.getMessage().contains(SECRET));
        assertNull(error.getCause());
        verifyNoInteractions(connectors, transactions, store);
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
    }

    @Test
    void characterCreationFailureDeletesOnlyFreshRowsWithTheirExactRevisions() throws Exception {
        authenticate("importer", "ROLE_USER");
        doReturn(false).when(characters).create(any());
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> importJson(archive(
                        ext(SOURCE, DRAFT_SOURCE),
                        draft(ext(FOREIGN)),
                        List.of(
                                ConnectorConfigurationDTO.from(summary(SOURCE)),
                                ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE)),
                                ConnectorConfigurationDTO.from(summary(FOREIGN))))));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
        assertEquals(2, imported.size());
        verify(characters).create(any());
        verify(characters, never()).update(any());
        assertOnlyFreshConnectorsDeleted();
        verify(transactions).rollback(any());
        verify(transactions, never()).commit(any());
    }

    @Test
    void failedSecondConnectorCreationCleansFirstWithoutCreatingCharacter() throws Exception {
        authenticate("importer", "ROLE_USER");
        doThrow(new ConnectorException(ConnectorException.Code.CONNECTOR_LIMIT_EXCEEDED))
                .when(connectors)
                .create(eq("importer"), argThat(c -> "Draft".equals(c.name())), eq(false));
        ConnectorException error = assertThrows(
                ConnectorException.class,
                () -> importJson(archive(
                        ext(SOURCE, DRAFT_SOURCE),
                        draft(ext(FOREIGN)),
                        List.of(
                                ConnectorConfigurationDTO.from(summary(SOURCE)),
                                ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE))))));
        assertEquals(ConnectorException.Code.CONNECTOR_LIMIT_EXCEEDED, error.code());
        assertEquals(1, imported.size());
        verify(connectors, times(2)).create(eq("importer"), any(ConnectorConfiguration.class), eq(false));
        assertOnlyFreshConnectorsDeleted();
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
        verify(transactions).rollback(any());
        verify(transactions, never()).commit(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"backends", "resourceUrls", "ragTasks"})
    void missingRequiredV3FieldsAreRejectedBeforeAnyWrite(String field) throws Exception {
        authenticate("importer", "ROLE_USER");
        ObjectNode document = (ObjectNode) ToolArguments.parse(
                archive(ext(SOURCE), null, List.of(ConnectorConfigurationDTO.from(summary(SOURCE)))));
        document.remove(field);
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, () -> importJson(document.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
        verifyNoInteractions(connectors, transactions, store);
    }

    @Test
    void failureAfterCharacterCreationCleansNewConnectorsThroughTheImportBoundary() throws Exception {
        authenticate("importer", "ROLE_USER");
        doReturn(false).when(characters).update(any());
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> importJson(archive(
                        ext(SOURCE),
                        draft(ext(DRAFT_SOURCE)),
                        List.of(
                                ConnectorConfigurationDTO.from(summary(SOURCE)),
                                ConnectorConfigurationDTO.from(summary(DRAFT_SOURCE))))));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatusCode());
        verify(characters).create(any());
        verify(characters).update(any());
        assertEquals(1, imported.size());
        assertNull(createdCharacter.getDraft());
        assertEquals("private", createdCharacter.getVisibility());
        assertOnlyFreshConnectorsDeleted();
        verify(transactions).rollback(any());
        verify(transactions, never()).commit(any());
    }

    @Test
    void duplicateAndOversizedDescriptorListsAreRejectedBeforeWrites() throws Exception {
        authenticate("importer", "ROLE_USER");
        var descriptor = ConnectorConfigurationDTO.from(summary(SOURCE));
        List<ConnectorConfigurationDTO> oversized = java.util.stream.IntStream.range(0, 17)
                .mapToObj(i -> ConnectorConfigurationDTO.from(summary("%032x".formatted(i))))
                .toList();
        for (List<ConnectorConfigurationDTO> invalid :
                List.of(List.of(descriptor, descriptor), Collections.nCopies(17, descriptor), oversized)) {
            ResponseStatusException error =
                    assertThrows(ResponseStatusException.class, () -> importJson(archive(ext(SOURCE), null, invalid)));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        }
        verifyNoInteractions(connectors, transactions, store);
        verify(characters, never()).create(any());
        verify(characters, never()).update(any());
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
        api.exportCharacter(1L, includeConnectors, false, new MockHttpServletRequest(), response);
        Path directory = Files.createTempDirectory(root, "export-");
        TarUtils.extractGzip(new ByteArrayInputStream(response.getContentAsByteArray()), directory);
        assertEquals("application/gzip", response.getContentType());
        assertTrue(Files.readString(directory.resolve("manifest.yml")).contains("apiVersion: v3"));
        return ToolArguments.parse(Files.readString(directory.resolve("character.json")));
    }

    private CharacterInfo importJson(String json) throws Exception {
        return importJson(json, "v3");
    }

    private CharacterInfo importJson(String json, String version) throws Exception {
        CharacterApi.ManifestInfo info = new CharacterApi.ManifestInfo();
        info.setApiVersion(version);
        String manifestText = ReflectionTestUtils.invokeMethod(info, "toYaml");
        byte[] manifest = Objects.requireNonNull(manifestText).getBytes(StandardCharsets.UTF_8);
        byte[] content = json.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarUtils.compressGzip(
                List.of(
                        new TarUtils.Entry("manifest.yml", () -> new ByteArrayInputStream(manifest), manifest.length),
                        new TarUtils.Entry("character.json", () -> new ByteArrayInputStream(content), content.length)),
                bytes);
        MockMultipartFile file =
                new MockMultipartFile("file", "connector.tar.gz", "application/gzip", bytes.toByteArray());
        Long id = api.importCharacter(new MockHttpServletRequest(), file);
        verify(characters).create(any());
        verify(characters).update(any());
        assertNotNull(createdCharacter);
        assertNotNull(updatedCharacter);
        assertEquals(2L, id);
        for (CharacterInfo character : List.of(createdCharacter, updatedCharacter)) {
            assertEquals(id, character.getCharacterId());
            assertEquals("importer", character.getUserId());
            assertEquals("private", character.getVisibility());
            assertNull(character.getDraft());
        }
        assertEquals(ToolArguments.parse(createdCharacter.getExt()), ToolArguments.parse(updatedCharacter.getExt()));
        verify(transactions).commit(any());
        verify(transactions, never()).rollback(any());
        return updatedCharacter;
    }

    private void assertOnlyFreshConnectorsDeleted() {
        assertFalse(imported.isEmpty());
        for (var created : imported) {
            assertNotEquals(SOURCE, created.connectorId());
            assertNotEquals(DRAFT_SOURCE, created.connectorId());
            assertNotEquals(FOREIGN, created.connectorId());
            verify(connectors).delete("importer", created.connectorId(), created.revision());
        }
        verify(connectors, times(imported.size())).delete(any(), any(), any());
        verify(connectors, never()).delete(any(), eq(SOURCE), any());
        verify(connectors, never()).delete(any(), eq(DRAFT_SOURCE), any());
        verify(connectors, never()).delete(any(), eq(FOREIGN), any());
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
                .resourceUrls(Map.of())
                .ragTasks(List.of())
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

    private static void assertClearedExt(String ext) {
        assertFalse(ToolArguments.parse(ext).has("connectors"));
        assertEquals(42, ToolArguments.parse(ext).path("custom").asInt());
    }

    private static void assertCleared(String ext, String draft) {
        assertClearedExt(ext);
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
