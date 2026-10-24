package fun.freechat;

import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fun.freechat.mapper.ConnectorInfoMapper;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.ConnectorInfo;
import fun.freechat.model.User;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.connectors.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;

class DatabaseConnectorServiceTest {
    private static final String ID = "a".repeat(32);
    private static final String REVISION = "b".repeat(32);
    private ConnectorInfoMapper infos;
    private UserMapper users;
    private EncryptionService encryption;
    private DatabaseConnectorService service;
    private ConnectorInfo row;

    @BeforeEach
    void setup() {
        infos = mock(ConnectorInfoMapper.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        users = mock(UserMapper.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        encryption = mock(EncryptionService.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        PlatformTransactionManager transactions =
                mock(PlatformTransactionManager.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        when(transactions.getTransaction(any(TransactionDefinition.class))).thenReturn(new SimpleTransactionStatus());
        when(users.selectOne(any(SelectDSLCompleter.class)))
                .thenReturn(Optional.of(
                        new User().withUserId("owner").withEnabled((byte) 1).withLocked((byte) 0)));
        when(infos.insert(any(ConnectorInfo.class))).thenReturn(1);
        when(infos.update(any(UpdateDSLCompleter.class))).thenReturn(1);
        when(infos.delete(any(DeleteDSLCompleter.class))).thenReturn(1);
        when(encryption.encrypt(anyString()))
                .thenAnswer(invocation -> Base64.getEncoder()
                        .encodeToString(invocation.<String>getArgument(0).getBytes(StandardCharsets.UTF_8)));
        when(encryption.decrypt(anyString()))
                .thenAnswer(invocation -> new String(
                        Base64.getDecoder().decode(invocation.<String>getArgument(0)), StandardCharsets.UTF_8));
        service = new DatabaseConnectorService(infos, users, encryption, transactions);
        row = new ConnectorInfo()
                .withConnectorId(ID)
                .withUserId("owner")
                .withRevision(REVISION)
                .withName("name")
                .withTransport("http")
                .withUrl("https://example.com/mcp")
                .withTimeout(60000)
                .withActive((byte) 1)
                .withGmtCreate(LocalDateTime.now())
                .withGmtModified(LocalDateTime.now())
                .withHeaders("{}")
                .withHeaderNames("[]");
        when(infos.selectOne(any(SelectDSLCompleter.class))).thenAnswer(ignored -> Optional.of(row));
    }

    @Test
    void encryptsEveryHeaderAndSummaryReadsNeverDecrypt() {
        var created = service.create(
                "owner", config(Map.of("Authorization", "secret-one", "X-Key", "secret-two", "X-Empty", "")), false);
        ArgumentCaptor<ConnectorInfo> inserted = ArgumentCaptor.forClass(ConnectorInfo.class);
        verify(infos).insert(inserted.capture());
        row = inserted.getValue();
        assertFalse(row.getHeaders().contains("secret-one"));
        assertFalse(row.getHeaders().contains("secret-two"));
        assertTrue(created.connectorId().matches("[0-9a-f]{32}"));
        assertTrue(created.revision().matches("[0-9a-f]{32}"));
        clearInvocations(encryption);
        when(infos.select(any(SelectDSLCompleter.class))).thenReturn(List.of(row));
        assertEquals(created, service.get("owner", created.connectorId()));
        assertEquals(List.of(created), service.list("owner"));
        verifyNoInteractions(encryption);
        var resolved = service.resolve("owner", created.connectorId());
        assertEquals("secret-one", resolved.configuration().headers().get("Authorization"));
        assertEquals("secret-two", resolved.configuration().headers().get("X-Key"));
        assertEquals("", resolved.configuration().headers().get("X-Empty"));
        assertFalse(created.toString().contains("example.com"));
        assertFalse(resolved.toString().contains("secret"));
    }

    @Test
    void importedHeaderPlaceholdersMustBeReplacedBeforeActivation() {
        assertCode(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.create("owner", config(Map.of("Authorization", "")), true));
        service.create("owner", config(Map.of("Authorization", "")), false);
        ArgumentCaptor<ConnectorInfo> inserted = ArgumentCaptor.forClass(ConnectorInfo.class);
        verify(infos).insert(inserted.capture());
        row = inserted.getValue().withConnectorId(ID).withRevision(REVISION);
        assertCode(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.update("owner", ID, REVISION, config(Map.of()), Set.of("Authorization"), true));
        verify(infos, never()).update(any(UpdateDSLCompleter.class));
        var active =
                service.update("owner", ID, REVISION, config(Map.of("Authorization", "replacement")), Set.of(), true);
        assertTrue(active.active());
    }

    @Test
    void exactOwnerChecksRejectForeignAndLegacyCollationAliases() {
        when(infos.select(any(SelectDSLCompleter.class))).thenReturn(List.of(row));
        assertTrue(service.list("Owner").isEmpty());
        assertCode(CONNECTOR_NOT_FOUND, () -> service.get("Owner", ID));
        assertCode(CONNECTOR_NOT_FOUND, () -> service.get("other", ID));
        assertCode(CONNECTOR_ACCESS_DENIED, () -> service.resolve("Owner", ID));
        assertCode(CONNECTOR_ACCESS_DENIED, () -> service.create("Owner", config(Map.of()), true));
        assertCode(
                CONNECTOR_ACCESS_DENIED, () -> service.update("Owner", ID, REVISION, config(Map.of()), Set.of(), true));
        assertCode(CONNECTOR_ACCESS_DENIED, () -> service.delete("Owner", ID, REVISION));
        verifyNoInteractions(encryption);
    }

    @Test
    void enabledUnlockedOwnerRequiredForResolveAndMutations() {
        for (User user : List.of(
                new User().withUserId("owner").withEnabled((byte) 0).withLocked((byte) 0),
                new User().withUserId("owner").withEnabled((byte) 1).withLocked((byte) 1))) {
            when(users.selectOne(any(SelectDSLCompleter.class))).thenReturn(Optional.of(user));
            assertCode(CONNECTOR_ACCESS_DENIED, () -> service.resolve("owner", ID));
            assertCode(CONNECTOR_ACCESS_DENIED, () -> service.create("owner", config(Map.of()), true));
            assertCode(
                    CONNECTOR_ACCESS_DENIED,
                    () -> service.update("owner", ID, REVISION, config(Map.of()), Set.of(), true));
            assertCode(CONNECTOR_ACCESS_DENIED, () -> service.delete("owner", ID, REVISION));
        }
        verify(infos, never()).insert(any(ConnectorInfo.class));
        verify(infos, never()).update(any(UpdateDSLCompleter.class));
        verify(infos, never()).delete(any(DeleteDSLCompleter.class));
    }

    @Test
    void quotaAndRevisionGuardsFailWithoutWriting() {
        when(infos.count(any(CountDSLCompleter.class))).thenReturn(32L);
        assertCode(CONNECTOR_LIMIT_EXCEEDED, () -> service.create("owner", config(Map.of()), true));
        assertCode(
                CONNECTOR_VERSION_CONFLICT,
                () -> service.update("owner", ID, "c".repeat(32), config(Map.of()), Set.of(), true));
        assertCode(CONNECTOR_VERSION_CONFLICT, () -> service.delete("owner", ID, "c".repeat(32)));
        verify(infos, never()).insert(any(ConnectorInfo.class));
        verify(infos, never()).update(any(UpdateDSLCompleter.class));
        verify(infos, never()).delete(any(DeleteDSLCompleter.class));
        when(infos.update(any(UpdateDSLCompleter.class))).thenReturn(0);
        assertCode(
                CONNECTOR_VERSION_CONFLICT,
                () -> service.update("owner", ID, REVISION, config(Map.of()), Set.of(), true));
        when(infos.delete(any(DeleteDSLCompleter.class))).thenReturn(0);
        assertCode(CONNECTOR_VERSION_CONFLICT, () -> service.delete("owner", ID, REVISION));
    }

    @Test
    void updateRetainsEncryptedValuesOnlyOnSameOriginAndRejectsOverlapOrMissingNames() {
        service.create("owner", config(Map.of("Authorization", "secret")), true);
        ArgumentCaptor<ConnectorInfo> inserted = ArgumentCaptor.forClass(ConnectorInfo.class);
        verify(infos).insert(inserted.capture());
        row = inserted.getValue().withConnectorId(ID).withRevision(REVISION);
        var updated = service.update(
                "owner",
                ID,
                REVISION,
                new ConnectorConfiguration(
                        "renamed", "sse", "https://EXAMPLE.com:443/sse", 1000, Map.of("X-Key", "new")),
                Set.of("authorization"),
                false);
        assertEquals(Set.of("Authorization", "X-Key"), updated.headerNames());
        assertFalse(updated.active());
        assertNotEquals(REVISION, updated.revision());
        assertCode(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.update(
                        "owner", ID, REVISION, config(Map.of("authorization", "new")), Set.of("Authorization"), true));
        assertCode(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.update("owner", ID, REVISION, config(Map.of()), Set.of("X-Missing"), true));
        for (String url :
                List.of("https://other.example/mcp", "http://example.com/mcp", "https://example.com:8443/mcp")) {
            assertCode(
                    CONNECTOR_INVALID_CONFIGURATION,
                    () -> service.update(
                            "owner",
                            ID,
                            REVISION,
                            new ConnectorConfiguration("name", "http", url, null, null),
                            Set.of("Authorization"),
                            true));
        }
        assertDoesNotThrow(() -> service.update(
                "owner",
                ID,
                REVISION,
                new ConnectorConfiguration("name", "http", "http://new.example/mcp", null, null),
                Set.of(),
                true));
    }

    @Test
    void validatesTheEntireMergedHeaderBudget() {
        service.create("owner", config(Map.of("X-Old", "a".repeat(8192))), true);
        ArgumentCaptor<ConnectorInfo> inserted = ArgumentCaptor.forClass(ConnectorInfo.class);
        verify(infos).insert(inserted.capture());
        row = inserted.getValue().withConnectorId(ID).withRevision(REVISION);
        assertCode(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.update(
                        "owner", ID, REVISION, config(Map.of("X-New", "b".repeat(8192))), Set.of("X-Old"), true));
        verify(infos, never()).update(any(UpdateDSLCompleter.class));
    }

    @Test
    void legacyCryptoFailureAndStorageFailuresNeverReturnPayloads() {
        when(encryption.encrypt(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        assertCode(
                CONNECTOR_UNAVAILABLE, () -> service.create("owner", config(Map.of("X-Key", "private-token")), true));
        verify(infos, never()).insert(any(ConnectorInfo.class));
        when(infos.selectOne(any(SelectDSLCompleter.class)))
                .thenThrow(new IllegalStateException("https://private.example private-token"));
        assertCode(CONNECTOR_UNAVAILABLE, () -> service.get("owner", ID));
    }

    private static ConnectorConfiguration config(Map<String, String> headers) {
        return new ConnectorConfiguration("name", "http", "https://example.com/mcp", null, headers);
    }

    private static void assertCode(ConnectorException.Code code, org.junit.jupiter.api.function.Executable operation) {
        ConnectorException failure = assertThrows(ConnectorException.class, operation);
        assertEquals(code, failure.code());
        assertEquals(code.name(), failure.getMessage());
        assertNull(failure.getCause());
    }
}
