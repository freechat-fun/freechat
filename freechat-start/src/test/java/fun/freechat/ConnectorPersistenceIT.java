package fun.freechat;

import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.freechat.mapper.ConnectorInfoMapper;
import fun.freechat.mapper.UserMapper;
import fun.freechat.service.common.impl.EncryptionServiceImpl;
import fun.freechat.service.connectors.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real owner locks, binary identifiers, encrypted storage, and cross-replica revision/limit races. No remote MCP I/O. */
@Testcontainers
@Timeout(120)
class ConnectorPersistenceIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ConnectorInfoMapper infos;
    private static UserMapper users;
    private static DataSourceTransactionManager transactions;
    private static EncryptionServiceImpl encryption;
    private DatabaseConnectorService service;
    private String alice;
    private String bob;

    @BeforeAll
    static void configureSqlOnly() throws Exception {
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(MYSQL.getJdbcUrl());
        pool.setUsername(MYSQL.getUsername());
        pool.setPassword(MYSQL.getPassword());
        pool.setMaximumPoolSize(8);
        pool.setConnectionInitSql("SET time_zone = '+00:00'");
        dataSource = new HikariDataSource(pool);
        sql = new JdbcTemplate(dataSource);
        Configuration configuration = new Configuration();
        configuration.setLogImpl(NoLoggingImpl.class);
        configuration.addMapper(ConnectorInfoMapper.class);
        configuration.addMapper(UserMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        infos = sessions.getMapper(ConnectorInfoMapper.class);
        users = sessions.getMapper(UserMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
        encryption = new EncryptionServiceImpl();
        ReflectionTestUtils.setField(encryption, "aesKey", "0123456789abcdef");
        encryption.init();
    }

    @AfterAll
    static void closeSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void setup() {
        sql.update("DELETE FROM connector_info");
        alice = seedUser();
        bob = seedUser();
        service = replica();
    }

    @Test
    void everyHeaderIsEncryptedAndMetadataSurvivesWithoutDecryption() {
        var created = service.create(
                alice,
                config(
                        "https://unlisted.invalid/mcp",
                        Map.of("Authorization", "Bearer auth-secret", "X-Api-Key", "custom-secret", "X-Empty", "")),
                false);
        String stored = sql.queryForObject(
                "SELECT headers FROM connector_info WHERE connector_id = ?", String.class, created.connectorId());
        assertFalse(stored.contains("auth-secret"));
        assertFalse(stored.contains("custom-secret"));
        assertFalse(stored.contains("connector-header:v1:"));
        assertEquals(Set.of("Authorization", "X-Api-Key", "X-Empty"), created.headerNames());
        assertEquals(created, replica().get(alice, created.connectorId()));
        assertEquals(
                "custom-secret",
                replica()
                        .resolve(alice, created.connectorId())
                        .configuration()
                        .headers()
                        .get("X-Api-Key"));
        sql.update(
                "UPDATE connector_info SET headers = '{\"Authorization\":\"corrupted\"}' WHERE connector_id = ?",
                created.connectorId());
        assertEquals(created, service.get(alice, created.connectorId()));
        assertEquals(List.of(created), service.list(alice));
        code(CONNECTOR_UNAVAILABLE, () -> service.resolve(alice, created.connectorId()));
    }

    @Test
    void ownershipIsExactEvenWithLegacyUserCollationAndForeignIdsStayNotFound() {
        var created = service.create(alice, config("https://example.com/mcp", Map.of("X-Key", "secret")), true);
        assertTrue(service.list(bob).isEmpty());
        code(CONNECTOR_NOT_FOUND, () -> service.get(bob, created.connectorId()));
        code(CONNECTOR_NOT_FOUND, () -> service.resolve(bob, created.connectorId()));
        code(
                CONNECTOR_NOT_FOUND,
                () -> service.update(
                        bob,
                        created.connectorId(),
                        created.revision(),
                        config("https://example.com/mcp", Map.of()),
                        Set.of(),
                        true));
        code(CONNECTOR_NOT_FOUND, () -> service.delete(bob, created.connectorId(), created.revision()));
        String alias = alice.toUpperCase(java.util.Locale.ROOT);
        assertNotEquals(alice, alias);
        assertTrue(service.list(alias).isEmpty());
        code(CONNECTOR_NOT_FOUND, () -> service.get(alias, created.connectorId()));
        code(CONNECTOR_ACCESS_DENIED, () -> service.create(alias, config("https://example.com/mcp", Map.of()), true));
        code(CONNECTOR_ACCESS_DENIED, () -> service.resolve(alias, created.connectorId()));
        sql.update("UPDATE user SET locked = 1 WHERE user_id = ?", alice);
        code(CONNECTOR_ACCESS_DENIED, () -> service.resolve(alice, created.connectorId()));
        code(CONNECTOR_ACCESS_DENIED, () -> service.delete(alice, created.connectorId(), created.revision()));
        sql.update("UPDATE user SET locked = 0, enabled = 0 WHERE user_id = ?", alice);
        code(CONNECTOR_ACCESS_DENIED, () -> service.create(alice, config("https://example.com/mcp", Map.of()), true));
        code(
                CONNECTOR_ACCESS_DENIED,
                () -> service.update(
                        alice,
                        created.connectorId(),
                        created.revision(),
                        config("https://example.com/mcp", Map.of()),
                        Set.of(),
                        true));
    }

    @Test
    void retainingHeadersPreservesCiphertextAndRequiresSameOrigin() {
        var created = service.create(alice, config("https://example.com/mcp", Map.of("Authorization", "secret")), true);
        String cipher = sql.queryForObject(
                "SELECT headers FROM connector_info WHERE connector_id = ?", String.class, created.connectorId());
        code(
                CONNECTOR_INVALID_CONFIGURATION,
                () -> service.update(
                        alice,
                        created.connectorId(),
                        created.revision(),
                        config("https://other.example/mcp", Map.of()),
                        Set.of("Authorization"),
                        true));
        var changed = service.update(
                alice,
                created.connectorId(),
                created.revision(),
                config("https://EXAMPLE.com:443/other", Map.of()),
                Set.of("authorization"),
                false);
        assertNotEquals(created.revision(), changed.revision());
        assertFalse(changed.active());
        assertEquals(
                cipher,
                sql.queryForObject(
                        "SELECT headers FROM connector_info WHERE connector_id = ?",
                        String.class,
                        created.connectorId()));
        assertEquals(
                "secret",
                replica()
                        .resolve(alice, changed.connectorId())
                        .configuration()
                        .headers()
                        .get("Authorization"));
        var cleared = service.update(
                alice,
                changed.connectorId(),
                changed.revision(),
                config("http://other.example/mcp", Map.of()),
                Set.of(),
                true);
        assertTrue(cleared.headerNames().isEmpty());
        assertTrue(service.resolve(alice, cleared.connectorId())
                .configuration()
                .headers()
                .isEmpty());
    }

    @Test
    void optimisticUpdatesAndDeletesHaveOneWinnerAcrossReplicas() throws Exception {
        var created = service.create(alice, config("https://example.com/mcp", Map.of()), true);
        var results = race(
                6,
                () -> replica()
                        .update(
                                alice,
                                created.connectorId(),
                                created.revision(),
                                config("https://example.com/updated", Map.of("X-Key", "new-secret")),
                                Set.of(),
                                true));
        assertEquals(
                1,
                results.stream()
                        .filter(ConnectorService.Summary.class::isInstance)
                        .count());
        assertEquals(
                5, results.stream().filter(CONNECTOR_VERSION_CONFLICT::equals).count());
        var current = service.get(alice, created.connectorId());
        var deletes = race(6, () -> {
            replica().delete(alice, created.connectorId(), current.revision());
            return "deleted";
        });
        assertEquals(1, deletes.stream().filter("deleted"::equals).count());
        assertEquals(5, deletes.stream().filter(CONNECTOR_NOT_FOUND::equals).count());
        assertTrue(service.list(alice).isEmpty());
    }

    @Test
    void userLockSerializesTheQuotaAcrossReplicasAndDeletionReleasesCapacity() throws Exception {
        for (int i = 0; i < 31; i++) {
            service.create(alice, config("https://example.com/mcp", Map.of()), true);
        }
        var results = race(6, () -> replica().create(alice, config("https://example.com/mcp", Map.of()), true));
        assertEquals(
                1,
                results.stream()
                        .filter(ConnectorService.Summary.class::isInstance)
                        .count());
        assertEquals(
                5, results.stream().filter(CONNECTOR_LIMIT_EXCEEDED::equals).count());
        assertEquals(32, service.list(alice).size());
        var removed = service.list(alice).getFirst();
        service.delete(alice, removed.connectorId(), removed.revision());
        assertDoesNotThrow(() -> replica().create(alice, config("https://example.com/mcp", Map.of()), true));
        assertDoesNotThrow(() -> replica().create(bob, config("https://example.com/mcp", Map.of()), true));
    }

    @Test
    void upgradeIsIdempotent() {
        var upgrade = new ResourceDatabasePopulator(new ClassPathResource("sql/upgrade-connectors.sql"));
        upgrade.execute(dataSource);
        upgrade.execute(dataSource);
        assertDoesNotThrow(() -> service.create(alice, config("https://example.com/mcp", Map.of()), true));
    }

    private static DatabaseConnectorService replica() {
        return new DatabaseConnectorService(infos, users, encryption, transactions);
    }

    private static ConnectorConfiguration config(String url, Map<String, String> headers) {
        return new ConnectorConfiguration("Connector display / name", "http", url, null, headers);
    }

    private static String seedUser() {
        String id = "a" + UUID.randomUUID().toString().replace("-", "").substring(1);
        sql.update(
                "INSERT INTO user (user_id,username,password,gmt_create,gmt_modified) VALUES (?,?,?,UTC_TIMESTAMP(),UTC_TIMESTAMP())",
                id,
                id,
                "not-a-password");
        return id;
    }

    private static List<Object> race(int count, Callable<Object> operation) throws Exception {
        CountDownLatch ready = new CountDownLatch(count);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(count)) {
            var futures = new ArrayList<java.util.concurrent.Future<Object>>();
            for (int i = 0; i < count; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    assertTrue(start.await(30, TimeUnit.SECONDS));
                    try {
                        return operation.call();
                    } catch (ConnectorException error) {
                        return error.code();
                    }
                }));
            }
            assertTrue(ready.await(30, TimeUnit.SECONDS));
            start.countDown();
            List<Object> results = new ArrayList<>();
            for (var future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        }
    }

    private static void code(ConnectorException.Code expected, org.junit.jupiter.api.function.Executable operation) {
        ConnectorException error = assertThrows(ConnectorException.class, operation);
        assertEquals(expected, error.code());
        assertEquals(expected.name(), error.getMessage());
        assertNull(error.getCause());
    }
}
