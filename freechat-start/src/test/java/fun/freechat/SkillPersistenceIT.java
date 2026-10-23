package fun.freechat;

import static fun.freechat.service.skills.SkillException.Code.*;
import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.freechat.mapper.SkillCoordinationMapper;
import fun.freechat.mapper.SkillDraftMapper;
import fun.freechat.mapper.SkillInfoMapper;
import fun.freechat.mapper.UserMapper;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.skills.DatabaseSkillService;
import fun.freechat.service.skills.SkillBundleValidator;
import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillReviewer;
import fun.freechat.service.skills.SkillService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real SQL/user-row locks plus a shared nonlocal FileStore; no application, LLM, or user database. */
@Testcontainers
@Timeout(120)
class SkillPersistenceIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static SkillInfoMapper infos;
    private static SkillDraftMapper drafts;
    private static UserMapper users;
    private static SkillCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;
    private RemoteFiles files;
    private DatabaseSkillService service;
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
        Configuration config = new Configuration();
        config.setLogImpl(NoLoggingImpl.class);
        config.addMapper(SkillInfoMapper.class);
        config.addMapper(SkillDraftMapper.class);
        config.addMapper(UserMapper.class);
        config.addMapper(SkillCoordinationMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(config);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        infos = sessions.getMapper(SkillInfoMapper.class);
        drafts = sessions.getMapper(SkillDraftMapper.class);
        users = sessions.getMapper(UserMapper.class);
        coordination = sessions.getMapper(SkillCoordinationMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
    }

    @AfterAll
    static void closeSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void setup() {
        sql.update("DELETE FROM skill_info");
        sql.update("DELETE FROM skill_draft");
        alice = seedUser();
        bob = seedUser();
        files = new RemoteFiles();
        service = replica(bundle -> assertNoTransaction());
    }

    @Test
    void ownerScopedLookupsAndMutationsNeverTouchAnotherOwnersFiles() {
        var draft = service.prepare(alice, null, null, input("original"));
        assertCode(
                SKILL_NOT_FOUND,
                () -> service.confirm(bob, draft.draftId(), draft.bundle().digest()));
        var installed = service.confirm(alice, draft.draftId(), draft.bundle().digest());
        int reads = files.reads.get();
        assertTrue(service.list(bob).isEmpty());
        assertEquals(1, service.list(alice).size());
        assertCode(SKILL_NOT_FOUND, () -> service.get(bob, installed.skillId()));
        assertCode(
                SKILL_NOT_FOUND, () -> service.prepare(bob, installed.skillId(), installed.revision(), input("other")));
        assertCode(SKILL_NOT_FOUND, () -> service.delete(bob, installed.skillId(), installed.revision()));
        assertCode(SKILL_NOT_FOUND, () -> service.get(alice, "../SKILL.md"));
        assertEquals(reads, files.reads.get());
        assertEquals(installed, replica(bundle -> {}).get(alice, installed.skillId()));
        assertTrue(files.objects.keySet().stream()
                .allMatch(path -> path.matches(
                        "private/skills/[0-9a-f]{64}/[0-9a-f]{32}/[0-9a-f]{32}/(?:SKILL.md|references/guide.txt)")));
    }

    @Test
    void confirmationIsDigestPinnedSingleUseAndReplicaIndependent() throws Exception {
        var draft = service.prepare(alice, null, null, input("original"));
        assertCode(SKILL_VERSION_CONFLICT, () -> service.confirm(alice, draft.draftId(), "0".repeat(64)));
        var results = race(
                2,
                () -> replica(bundle -> {})
                        .confirm(alice, draft.draftId(), draft.bundle().digest()));
        assertEquals(
                1,
                results.stream()
                        .filter(SkillService.Installed.class::isInstance)
                        .count());
        assertEquals(1, codes(results, SKILL_VERSION_CONFLICT));
        assertEquals(1, count("SELECT COUNT(*) FROM skill_info WHERE user_id = ?", alice));
        assertEquals(
                "published",
                sql.queryForObject("SELECT status FROM skill_draft WHERE draft_id = ?", String.class, draft.draftId()));
    }

    @Test
    void staleUpdatesAndDeletesPreserveWinnerAndNeverResurrectDeletion() throws Exception {
        var first = install("original");
        var a = service.prepare(alice, first.skillId(), first.revision(), input("first-edit"));
        var b = replica(bundle -> {}).prepare(alice, first.skillId(), first.revision(), input("second-edit"));
        var tasks = List.<Callable<Object>>of(
                () -> service.confirm(alice, a.draftId(), a.bundle().digest()),
                () -> replica(bundle -> {})
                        .confirm(alice, b.draftId(), b.bundle().digest()));
        var results = race(tasks);
        assertEquals(
                1,
                results.stream()
                        .filter(SkillService.Installed.class::isInstance)
                        .count());
        assertEquals(1, codes(results, SKILL_VERSION_CONFLICT));
        var winner = service.get(alice, first.skillId());
        assertNotEquals(first.revision(), winner.revision());
        assertCode(SKILL_VERSION_CONFLICT, () -> service.delete(alice, first.skillId(), first.revision()));
        assertCode(
                SKILL_VERSION_CONFLICT, () -> service.update(alice, first.skillId(), first.revision(), input("stale")));
        var pending = service.prepare(alice, first.skillId(), winner.revision(), input("pending"));
        service.delete(alice, first.skillId(), winner.revision());
        assertCode(
                SKILL_NOT_FOUND,
                () -> service.confirm(alice, pending.draftId(), pending.bundle().digest()));
        assertCode(SKILL_NOT_FOUND, () -> service.get(alice, first.skillId()));
        assertEquals(0, count("SELECT COUNT(*) FROM skill_info WHERE user_id = ?", alice));
    }

    @Test
    void concurrentStaleDeleteAndPublicationHaveOnlyOneWinner() throws Exception {
        var first = install("original");
        var draft = service.prepare(alice, first.skillId(), first.revision(), input("edit"));
        var results = race(List.of(
                () -> service.confirm(alice, draft.draftId(), draft.bundle().digest()), () -> {
                    replica(bundle -> {}).delete(alice, first.skillId(), first.revision());
                    return "deleted";
                }));
        assertEquals(
                1,
                results.stream()
                        .filter(value -> !(value instanceof SkillException.Code))
                        .count());
        assertEquals(1, codes(results, SKILL_VERSION_CONFLICT) + codes(results, SKILL_NOT_FOUND));
    }

    @Test
    void userRowLockEnforcesInstalledAndPendingQuotaAcrossReplicas() throws Exception {
        var example = install("seed");
        for (int i = 0; i < 62; i++) {
            sql.update("""
                    INSERT INTO skill_info (skill_id, user_id, revision, name, description, digest, manifest, gmt_create, gmt_modified)
                    SELECT ?, user_id, revision, name, description, digest, manifest, gmt_create, gmt_modified
                    FROM skill_info WHERE skill_id = ?
                    """, uuid(), example.skillId());
        }
        var results = race(4, () -> replica(bundle -> {}).prepare(alice, null, null, input("last-slot")));
        assertEquals(
                1, results.stream().filter(SkillService.Draft.class::isInstance).count());
        assertEquals(3, codes(results, SKILL_LIMIT_EXCEEDED));
        var draft = (SkillService.Draft) results.stream()
                .filter(SkillService.Draft.class::isInstance)
                .findFirst()
                .orElseThrow();
        service.confirm(alice, draft.draftId(), draft.bundle().digest());
        assertEquals(64, service.list(alice).size());
        assertCode(SKILL_LIMIT_EXCEEDED, () -> service.prepare(alice, null, null, input("overflow")));
        var replacement = service.update(alice, example.skillId(), example.revision(), input("replacement"));
        assertEquals("replacement", replacement.bundle().name());
        assertEquals(64, service.list(alice).size());
        assertNotNull(service.prepare(bob, null, null, input("independent")));
    }

    @Test
    void rejectedReviewsStillConsumeConcurrentRateLimitAndWindowExpires() throws Exception {
        AtomicInteger reviews = new AtomicInteger();
        SkillReviewer reject = bundle -> {
            assertNoTransaction();
            reviews.incrementAndGet();
            throw new SkillException(SKILL_UNSAFE_INSTRUCTIONS);
        };
        var results = race(12, () -> replica(reject).prepare(alice, null, null, input("review-me")));
        assertEquals(8, reviews.get());
        assertEquals(8, codes(results, SKILL_UNSAFE_INSTRUCTIONS));
        assertEquals(4, codes(results, SKILL_LIMIT_EXCEEDED));
        assertEquals(8, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected'", alice));
        assertEquals(0, count("SELECT COUNT(*) FROM skill_info WHERE user_id = ?", alice));
        assertNotNull(service.prepare(bob, null, null, input("independent")));
        sql.update(
                "UPDATE skill_draft SET gmt_create = UTC_TIMESTAMP(6) - INTERVAL 31 MINUTE WHERE user_id = ?", alice);
        assertNotNull(service.prepare(alice, null, null, input("new-window")));
    }

    @Test
    void fileAndReviewFailuresLeavePriorPublicationAndSanitizedErrors() {
        var first = install("original");
        files.failWrites = true;
        assertCode(
                SKILL_STORAGE_UNAVAILABLE,
                () -> service.update(alice, first.skillId(), first.revision(), input("io-error")));
        files.failWrites = false;
        assertEquals(first, service.get(alice, first.skillId()));
        var badReviewer = replica(bundle -> {
            throw new IllegalStateException("private provider URL and body");
        });
        assertCode(
                SKILL_REVIEW_UNAVAILABLE,
                () -> badReviewer.update(alice, first.skillId(), first.revision(), input("review-error")));
        assertEquals(first, service.get(alice, first.skillId()));
        assertEquals(2, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected'", alice));
    }

    @Test
    void storedTamperingCannotPublishAndGetAlwaysVerifiesAuthoritativeRevision() {
        var first = install("original");
        var next = service.prepare(alice, first.skillId(), first.revision(), input("tamper-me"));
        String path = files.objects.keySet().stream()
                .filter(key -> key.contains("/" + next.draftId() + "/") && key.endsWith("SKILL.md"))
                .findFirst()
                .orElseThrow();
        files.objects.get(path)[0] ^= 1;
        assertCode(
                SKILL_STORAGE_UNAVAILABLE,
                () -> replica(bundle -> {})
                        .confirm(alice, next.draftId(), next.bundle().digest()));
        assertEquals(first, service.get(alice, first.skillId()));
        var updated =
                replica(bundle -> {}).update(alice, first.skillId(), first.revision(), input("new-authoritative"));
        assertEquals(updated, service.get(alice, first.skillId()));
        String published = files.objects.keySet().stream()
                .filter(key -> key.contains("/" + updated.revision() + "/") && key.endsWith("SKILL.md"))
                .findFirst()
                .orElseThrow();
        files.objects.remove(published);
        assertCode(SKILL_STORAGE_UNAVAILABLE, () -> service.get(alice, first.skillId()));
    }

    @Test
    void expiredDraftsAndRetiredVersionsAreCollectedWithoutTouchingPublishedOrUnrelatedFiles() {
        var first = install("original");
        var latest = service.update(alice, first.skillId(), first.revision(), input("latest"));
        var abandoned = service.prepare(alice, null, null, input("abandoned"));
        sql.update(
                "UPDATE skill_draft SET expires_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE draft_id = ?",
                abandoned.draftId());
        assertCode(
                SKILL_DRAFT_EXPIRED,
                () -> service.confirm(
                        alice, abandoned.draftId(), abandoned.bundle().digest()));
        files.objects.put("private/unrelated.txt", new byte[] {7});
        sql.update(
                "UPDATE skill_draft SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE user_id = ? AND status <> 'published'",
                alice);
        replica(bundle -> {}).sweepExpired();
        assertEquals(latest, service.get(alice, latest.skillId()));
        assertTrue(files.objects.containsKey("private/unrelated.txt"));
        assertFalse(files.objects.keySet().stream()
                .anyMatch(path ->
                        path.contains("/" + first.revision() + "/") || path.contains("/" + abandoned.draftId() + "/")));
        assertEquals(2, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'cleaned'", alice));
        // Simulate the bounded delayed-write grace period without sleeping.
        sql.update(
                "UPDATE skill_draft SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE user_id = ? AND status = 'cleaned'",
                alice);
        service.sweepExpired();
        assertEquals(1, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ?", alice));
    }

    @Test
    void additiveMigrationCanRepeatWithoutChangingExistingRows() {
        var installed = install("preserved");
        var migration = new org.springframework.jdbc.datasource.init.ResourceDatabasePopulator(
                new org.springframework.core.io.ClassPathResource("sql/upgrade-prompt-only-skills.sql"));
        migration.execute(dataSource);
        migration.execute(dataSource);
        assertEquals(installed, service.get(alice, installed.skillId()));
        assertEquals(1, count("SELECT COUNT(*) FROM user WHERE user_id = ?", alice));
    }

    @Test
    void cleanupFailuresRemainDurableAndSweepBatchIsBounded() {
        var draft = service.prepare(alice, null, null, input("cleanup"));
        sql.update(
                "UPDATE skill_draft SET expires_at = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE draft_id = ?",
                draft.draftId());
        files.failDeletes = true;
        service.sweepExpired();
        assertEquals(
                "collecting",
                sql.queryForObject("SELECT status FROM skill_draft WHERE draft_id = ?", String.class, draft.draftId()));
        assertFalse(files.objects.isEmpty());
        files.failDeletes = false;
        sql.update(
                "UPDATE skill_draft SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE draft_id = ?",
                draft.draftId());
        service.sweepExpired();
        assertTrue(files.objects.isEmpty());
        for (int i = 0; i < 25; i++) {
            String id = uuid();
            sql.update("""
                    INSERT INTO skill_draft (draft_id, skill_id, user_id, revision, name, description, digest, manifest,
                        status, expires_at, gc_after, gmt_create, gmt_modified)
                    SELECT ?, skill_id, user_id, ?, name, description, digest, manifest, 'rejected',
                        UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, UTC_TIMESTAMP(6) - INTERVAL 1 SECOND, gmt_create, gmt_modified
                    FROM skill_draft WHERE draft_id = ?
                    """, id, id, draft.draftId());
        }
        service.sweepExpired();
        assertEquals(21, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'cleaned'", alice));
        assertEquals(5, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected'", alice));
    }

    @Test
    void traversalManifestCannotMakeCollectorDeleteUnrelatedObject() {
        var draft = service.prepare(alice, null, null, input("malformed-manifest"));
        String manifest = sql.queryForObject(
                "SELECT manifest FROM skill_draft WHERE draft_id = ?", String.class, draft.draftId());
        sql.update(
                "UPDATE skill_draft SET manifest = ?, expires_at = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE draft_id = ?",
                manifest.replace("references/guide.txt", "../../../unrelated.txt"),
                draft.draftId());
        files.objects.put("private/unrelated.txt", new byte[] {9});
        int before = files.objects.size();
        service.sweepExpired();
        assertEquals(before, files.objects.size());
        assertEquals(
                "collecting",
                sql.queryForObject("SELECT status FROM skill_draft WHERE draft_id = ?", String.class, draft.draftId()));
    }

    private DatabaseSkillService replica(SkillReviewer reviewer) {
        return new DatabaseSkillService(
                infos, drafts, users, coordination, files, new SkillBundleValidator(), reviewer, transactions);
    }

    private SkillService.Installed install(String name) {
        var draft = service.prepare(alice, null, null, input(name));
        return service.confirm(alice, draft.draftId(), draft.bundle().digest());
    }

    private static Map<String, String> input(String name) {
        return Map.of(
                "SKILL.md",
                "---\nname: " + name + "\ndescription: Clear friendly writing\n---\nPrefer short paragraphs.\n",
                "references/guide.txt",
                "Use everyday words. 中文 café\n");
    }

    private static String seedUser() {
        String id = uuid();
        sql.update(
                "INSERT INTO user (user_id, username, password, gmt_create, gmt_modified) VALUES (?, ?, 'unused', UTC_TIMESTAMP(), UTC_TIMESTAMP())",
                id,
                id);
        return id;
    }

    private static long count(String statement, Object... args) {
        return sql.queryForObject(statement, Long.class, args);
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static List<Object> race(int count, Callable<Object> operation) throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(operation);
        }
        return race(tasks);
    }

    private static List<Object> race(List<Callable<Object>> tasks) throws Exception {
        CountDownLatch ready = new CountDownLatch(tasks.size());
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = tasks.stream()
                    .map(task -> executor.submit(() -> {
                        ready.countDown();
                        assertTrue(start.await(20, TimeUnit.SECONDS));
                        try {
                            return task.call();
                        } catch (SkillException failure) {
                            return failure.code();
                        }
                    }))
                    .toList();
            assertTrue(ready.await(20, TimeUnit.SECONDS));
            start.countDown();
            List<Object> results = new ArrayList<>();
            for (var future : futures) {
                results.add(future.get(30, TimeUnit.SECONDS));
            }
            return results;
        }
    }

    private static long codes(List<Object> results, SkillException.Code code) {
        return results.stream().filter(code::equals).count();
    }

    private static void assertCode(SkillException.Code code, Executable work) {
        var failure = assertThrows(SkillException.class, work);
        assertEquals(code, failure.code());
        assertEquals(code.name(), failure.getMessage());
        assertNull(failure.getCause());
    }

    private static void assertNoTransaction() {
        assertFalse(
                TransactionSynchronizationManager.isActualTransactionActive(),
                "No SQL lock may span file/reviewer I/O");
    }

    private static final class RemoteFiles implements FileStore {
        final Map<String, byte[]> objects = new ConcurrentHashMap<>();
        final AtomicInteger reads = new AtomicInteger();
        volatile boolean failWrites;
        volatile boolean failDeletes;

        @Override
        public Path toPath(String path) {
            throw new AssertionError("not a local backend");
        }

        @Override
        public void close() {}

        @Override
        public long size(String path) {
            throw new AssertionError("untrusted hint");
        }

        @Override
        public List<String> list(String path, String regex, boolean recursive) {
            throw new AssertionError("no listing");
        }

        @Override
        public void createDirectories(String path) {
            assertNoTransaction();
        }

        @Override
        public long write(String path, InputStream stream, Long length, Instant modified) {
            throw new AssertionError("unexpected stream write");
        }

        @Override
        public long write(String path, byte[] bytes, Instant modified) throws IOException {
            assertNoTransaction();
            if (failWrites) {
                throw new IOException("private backend diagnostics");
            }
            if (objects.putIfAbsent(path, bytes.clone()) != null) {
                throw new AssertionError("immutable revision overwritten");
            }
            return bytes.length;
        }

        @Override
        public OutputStream newOutputStream(String path) {
            throw new AssertionError("unexpected output stream");
        }

        @Override
        public InputStream newInputStream(String path) throws IOException {
            assertNoTransaction();
            reads.incrementAndGet();
            byte[] bytes = objects.get(path);
            if (bytes == null) {
                throw new IOException("private missing object");
            }
            return new ByteArrayInputStream(bytes.clone());
        }

        @Override
        public boolean exists(String path) {
            assertNoTransaction();
            return objects.containsKey(path);
        }

        @Override
        public void delete(String path) throws IOException {
            assertNoTransaction();
            if (failDeletes) {
                throw new IOException("private delete failure");
            }
            objects.remove(path);
        }

        @Override
        public void move(String source, String target) {
            throw new AssertionError("move is not atomic");
        }

        @Override
        public long getLastModifiedTime(String path) {
            throw new AssertionError("not needed");
        }

        @Override
        public void setLastModifiedTime(String path, long modified) {
            throw new AssertionError("not needed");
        }
    }
}
