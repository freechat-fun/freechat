package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.api.CharacterApi;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.RagTask;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.connectors.ConnectorService;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.TarUtils;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CharacterExportApiTest {
    private static final String OWNER = "synthetic-owner";
    private static final String UID = "synthetic-character";
    private static final String PROFILE = "PRIVATE_CHARACTER_CONFIGURATION_SENTINEL";
    private static final String SECRET = "PRIVATE_API_KEY_SENTINEL";
    private static final String MISSING_SOURCE =
            "private/" + OWNER + "/character/document/" + UID + "/PRIVATE_SOURCE_SENTINEL.txt";
    private static final ObjectMapper JSON = new ObjectMapper();

    private final CharacterApi api = new CharacterApi();
    private final CharacterService characters = fake(CharacterService.class);
    private final RagTaskService rags = fake(RagTaskService.class);
    private final ConnectorService connectors = fake(ConnectorService.class);
    private final FileStore store = fake(FileStore.class);
    private final User owner = new User().withUserId(OWNER);
    private SecurityContext previousSecurity;
    private Object previousFileStore;
    private MockMvc mvc;

    @TempDir
    Path root;

    @BeforeEach
    void setup() throws Exception {
        previousSecurity = SecurityContextHolder.getContext();
        previousFileStore = ReflectionTestUtils.getField(StoreUtils.class, "localFileStore");
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                        owner, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        ReflectionTestUtils.setField(StoreUtils.class, "localFileStore", store);
        ReflectionTestUtils.setField(api, "characterService", characters);
        ReflectionTestUtils.setField(api, "ragTaskService", rags);
        ReflectionTestUtils.setField(api, "connectorService", connectors);
        CharacterInfo source = new CharacterInfo()
                .withCharacterId(1L)
                .withCharacterUid(UID)
                .withUserId(OWNER)
                .withVersion(1)
                .withName("Synthetic export character")
                .withVisibility("private")
                .withProfile(PROFILE)
                .withExt("{}")
                .withDraft("{\"apiKeyValue\":\"" + SECRET + "\"}");
        when(characters.details(eq(1L), same(owner))).thenReturn(Triple.of(source, List.of(), List.of()));
        when(store.exists(anyString())).thenReturn(false);
        when(rags.list(UID))
                .thenReturn(List.of(new RagTask().withSourceType("file").withSource(MISSING_SOURCE)));
        // Keep the real MVC routing, converters and controller-local exception handling.
        mvc = MockMvcBuilders.standaloneSetup(api).build();
    }

    @AfterEach
    void restore() {
        try {
            ReflectionTestUtils.setField(StoreUtils.class, "localFileStore", previousFileStore);
        } finally {
            SecurityContextHolder.setContext(previousSecurity);
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "false")
    void missingDocumentsRequireConfirmationByDefaultAndWhenForceIsFalse(String force) throws Exception {
        assertMissingDocuments(exportRequest(force));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "false")
    void gzipAcceptStillReceivesTheExactJsonConflict(String force) throws Exception {
        assertMissingDocuments(exportRequest(force).accept("application/gzip"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void forceExportsRealGzipWithoutMissingRagTasksAndPreservesConnectorOption(boolean includeConnectors)
            throws Exception {
        var request = exportRequest("true").accept("application/gzip");
        if (includeConnectors) request.param("includeConnectors", "true");
        JsonNode archive = assertArchive(request);
        assertEmptyRagTasks(archive);
        if (includeConnectors) {
            assertTrue(archive.path("connectors").isArray());
            assertTrue(archive.path("connectors").isEmpty());
        } else {
            assertFalse(archive.has("connectors"));
        }
        assertFalse(archive.toString().contains(MISSING_SOURCE));
        verify(rags).list(UID);
        verifyNoInteractions(connectors);
    }

    @Test
    void noDocumentsAndNoRagTasksDoNotRequireForce() throws Exception {
        when(rags.list(UID)).thenReturn(List.of());
        assertEmptyRagTasks(assertArchive(exportRequest(null).accept("application/gzip")));
        verify(rags).list(UID);
        verifyNoInteractions(connectors);
    }

    @Test
    void invalidForceIsRejectedBeforeExportRuns() throws Exception {
        mvc.perform(exportRequest("not-a-boolean")
                        .param("includeConnectors", "true")
                        .accept("application/gzip"))
                .andExpect(handler().methodName("exportCharacter"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_DISPOSITION));
        verifyNoInteractions(characters, rags, connectors, store);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void forceCannotBypassAuthentication(boolean includeConnectors) throws Exception {
        SecurityContextHolder.clearContext();
        mvc.perform(exportRequest("true")
                        .param("includeConnectors", Boolean.toString(includeConnectors))
                        .accept("application/gzip"))
                .andExpect(handler().methodName("exportCharacter"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_DISPOSITION));
        verifyNoInteractions(characters, rags, connectors, store);
    }

    private void assertMissingDocuments(MockHttpServletRequestBuilder request) throws Exception {
        var response = mvc.perform(request)
                .andExpect(handler().methodName("exportCharacter"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_DISPOSITION))
                .andReturn()
                .getResponse();
        String body = response.getContentAsString(StandardCharsets.UTF_8);
        assertEquals(JSON.readTree("""
                        {
                          "code": 409,
                          "errorCode": "CHARACTER_EXPORT_DOCUMENTS_MISSING",
                          "message": "Some knowledge source documents are missing."
                        }
                        """), JSON.readTree(body));
        for (String forbidden : List.of(PROFILE, SECRET, MISSING_SOURCE)) {
            assertFalse(body.contains(forbidden));
        }
        verify(characters).details(1L, owner);
        verify(rags).list(UID);
        verifyNoInteractions(connectors);
    }

    private JsonNode assertArchive(MockHttpServletRequestBuilder request) throws Exception {
        var response = mvc.perform(request)
                .andExpect(handler().methodName("exportCharacter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/gzip"))
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andReturn()
                .getResponse();
        Path extracted = Files.createTempDirectory(root, "export-");
        TarUtils.extractGzip(new ByteArrayInputStream(response.getContentAsByteArray()), extracted);
        assertTrue(Files.readString(extracted.resolve("manifest.yml")).contains("apiVersion: v3"));
        JsonNode archive = JSON.readTree(Files.readString(extracted.resolve("character.json")));
        assertEquals(PROFILE, archive.path("character").path("profile").asText());
        assertTrue(archive.path("backends").isArray());
        assertTrue(archive.path("backends").isEmpty());
        assertFalse(Files.exists(extracted.resolve("documents")));
        return archive;
    }

    private static void assertEmptyRagTasks(JsonNode archive) {
        assertTrue(archive.path("ragTasks").isArray());
        assertTrue(archive.path("ragTasks").isEmpty());
    }

    private static MockHttpServletRequestBuilder exportRequest(String force) {
        var request = get("/api/v2/character/export/{characterId}", 1L);
        if (force != null) request.param("force", force);
        return request;
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
