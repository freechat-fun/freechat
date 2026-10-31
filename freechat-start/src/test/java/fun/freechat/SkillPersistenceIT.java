package fun.freechat;

import static fun.freechat.service.skills.SkillException.Code.*;
import static fun.freechat.util.TestCommonUtils.MYSQL_IMAGE;
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
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real SQL/user-row locks plus a shared nonlocal FileStore; no application, LLM, or user database. */
@Testcontainers
@Timeout(120)
class SkillPersistenceIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(MYSQL_IMAGE)
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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void hiddenFilesAreAbsentFromReviewsPreviewsAndInstalledRevisions(boolean sourceImport) {
        var visible = input("visible");
        var submitted = new TreeMap<>(visible);
        submitted.put(".env", "hidden-secret");
        submitted.put("references/.cache/data.bin", "x".repeat(SkillBundleValidator.MAX_TOTAL_BYTES + 1));
        for (int i = 0; i < 20; i++) {
            submitted.put(".hidden" + i + "/install.sh", "bash ignored");
        }
        var reviews = new AtomicInteger();
        service = replica(bundle -> {
            assertNoTransaction();
            assertEquals(visible.keySet(), bundle.files().keySet());
            assertFalse(bundle.files().values().stream().anyMatch(text -> text.contains("hidden-secret")));
            reviews.incrementAndGet();
        });
        var draft = sourceImport
                ? service.prepareSource(alice, () -> submitted)
                : service.prepare(alice, null, null, submitted);
        assertEquals(visible, draft.bundle().files());
        assertEquals(
                new SkillBundleValidator().validate(visible).digest(),
                draft.bundle().digest());
        var installed = service.confirm(alice, draft.draftId(), draft.bundle().digest());
        assertEquals(visible, service.get(alice, installed.skillId()).bundle().files());
        submitted.put("SKILL.md", input("updated").get("SKILL.md"));
        var updated = service.update(alice, installed.skillId(), installed.revision(), submitted);
        assertEquals(submitted.get("SKILL.md"), updated.bundle().files().get("SKILL.md"));
        assertEquals(2, reviews.get());
        assertTrue(files.objects.keySet().stream()
                .allMatch(path -> path.endsWith("/SKILL.md") || path.endsWith("/references/guide.txt")));
        assertEquals(
                visible.keySet(),
                replica(bundle -> {})
                        .get(alice, installed.skillId())
                        .bundle()
                        .files()
                        .keySet());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ordinaryTextNamesAndLooseMarkdownReachReviewAndPersist(boolean sourceImport) {
        var input = new TreeMap<>(Map.of(
                "SKILL.md",
                "# Writing guide\nUse concise prose.\n```unknown\nunfinished",
                "LICENSE",
                "Copyright Example",
                "README.zh-CN.md",
                "说明",
                "config/rclone.conf.example",
                "[local]\ntype = local",
                "references/写作 指南.data",
                "短句。"));
        var reviews = new AtomicInteger();
        service = replica(bundle -> {
            assertNoTransaction();
            assertEquals(input, bundle.files());
            reviews.incrementAndGet();
        });
        var draft =
                sourceImport ? service.prepareSource(alice, () -> input) : service.prepare(alice, null, null, input);
        assertEquals("Writing guide", draft.bundle().name());
        var installed = service.confirm(alice, draft.draftId(), draft.bundle().digest());
        assertEquals(
                input,
                replica(bundle -> {}).get(alice, installed.skillId()).bundle().files());
        input.put("SKILL.md", "---\nthis header is not: [valid YAML\n---\nKeep instructions concise.");
        var updated = service.update(alice, installed.skillId(), installed.revision(), input);
        assertEquals("Imported skill", updated.bundle().name());
        assertEquals(input, service.get(alice, installed.skillId()).bundle().files());
        assertEquals(2, reviews.get());
        input.put("notes.anything", "#!/bin/sh\nrm -rf /tmp/example");
        assertCode(
                SKILL_UNSAFE_INSTRUCTIONS, () -> service.update(alice, updated.skillId(), updated.revision(), input));
        assertEquals(2, reviews.get());
        assertFalse(service.get(alice, updated.skillId()).bundle().files().containsKey("notes.anything"));
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
    void failedSourcesConsumeAttemptsBeforeNinthCallbackAndEmptyManifestsAreCollectible() {
        AtomicInteger callbacks = new AtomicInteger();
        var sourceFailure = new SkillException(SKILL_SOURCE_AMBIGUOUS);
        Supplier<Map<String, String>> source = () -> {
            assertNoTransaction();
            callbacks.incrementAndGet();
            assertEquals(
                    1,
                    count(
                            "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'staging' AND manifest = JSON_OBJECT()",
                            alice));
            throw sourceFailure;
        };
        for (int i = 0; i < 8; i++) {
            assertSame(sourceFailure, assertThrows(SkillException.class, () -> service.prepareSource(alice, source)));
        }
        assertCode(SKILL_LIMIT_EXCEEDED, () -> service.prepareSource(alice, source));
        assertEquals(8, callbacks.get());
        assertEquals(
                8,
                count(
                        "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected' AND manifest = JSON_OBJECT()",
                        alice));
        assertEquals(0, files.writes.get());
        assertEquals(0, files.reads.get());
        assertTrue(service.list(alice).isEmpty());
        files.objects.put("private/unrelated.txt", new byte[] {7});
        files.failDeletes = true;
        sql.update("UPDATE skill_draft SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE user_id = ?", alice);
        service.sweepExpired();
        assertEquals(8, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'cleaned'", alice));
        assertEquals(0, files.exists.get());
        assertEquals(0, files.deletes.get());
        assertCode(SKILL_LIMIT_EXCEEDED, () -> service.prepareSource(alice, source));
        assertEquals(8, callbacks.get());
        sql.update("UPDATE skill_draft SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE user_id = ?", alice);
        service.sweepExpired();
        assertEquals(0, count("SELECT COUNT(*) FROM skill_draft WHERE user_id = ?", alice));
        assertEquals(1, files.objects.size());
        assertTrue(files.objects.containsKey("private/unrelated.txt"));
    }

    @Test
    void inaccessibleOwnersAreRejectedBeforeSourceResolution() {
        sql.update("UPDATE user SET enabled = 0 WHERE user_id = ?", alice);
        sql.update("UPDATE user SET locked = 1 WHERE user_id = ?", bob);
        AtomicInteger callbacks = new AtomicInteger();
        for (String owner : List.of(alice, bob, uuid())) {
            assertCode(
                    SKILL_ACCESS_DENIED,
                    () -> service.prepareSource(owner, () -> {
                        callbacks.incrementAndGet();
                        return input("not-resolved");
                    }));
        }
        assertEquals(0, callbacks.get());
        assertEquals(0, count("SELECT COUNT(*) FROM skill_draft"));
        assertEquals(0, files.writes.get());
    }

    @Test
    void slowSourceSuspendsCallerTransactionAndDoesNotHoldOwnerLock() throws Exception {
        var advice = new TransactionInterceptor();
        advice.setTransactionManager(transactions);
        advice.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        var proxy = new ProxyFactory(service);
        proxy.addAdvice(advice);
        var transactional = (SkillService) proxy.getProxy();
        var resolving = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var pending = executor.submit(() -> new TransactionTemplate(transactions).execute(status -> {
                assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                var draft = transactional.prepareSource(alice, () -> {
                    assertNoTransaction();
                    resolving.countDown();
                    await(release);
                    assertNoTransaction();
                    return input("slow-source");
                });
                assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                return draft;
            }));
            try {
                assertTrue(resolving.await(20, TimeUnit.SECONDS));
                assertEquals(
                        1,
                        count(
                                "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'staging' AND manifest = JSON_OBJECT()",
                                alice));
                assertEquals(0, files.writes.get());
                // A second replica must acquire the same owner lock while the source remains blocked.
                var independent = executor.submit(
                        () -> replica(bundle -> assertNoTransaction()).prepare(alice, null, null, input("same-owner")));
                assertNotNull(independent.get(10, TimeUnit.SECONDS));
                assertFalse(pending.isDone());
            } finally {
                release.countDown();
            }
            var draft = pending.get(20, TimeUnit.SECONDS);
            assertEquals(
                    input("slow-source"),
                    service.confirm(alice, draft.draftId(), draft.bundle().digest())
                            .bundle()
                            .files());
        }
    }

    @Test
    void sourceSnapshotManifestPrecedesWritesAndReviewAndConfirmationUseExactMultipleFiles() {
        Map<String, String> resolved = new TreeMap<>(input("source-bundle"));
        resolved.put("notes.md", "Keep punctuation and line endings.\r\nRésumé 中文\r\n");
        Map<String, String> expected = Map.copyOf(resolved);
        AtomicInteger callbacks = new AtomicInteger();
        AtomicInteger reviews = new AtomicInteger();
        files.beforeWrite = () -> {
            String manifest = sql.queryForObject(
                    "SELECT manifest FROM skill_draft WHERE user_id = ? AND status = 'staging'", String.class, alice);
            assertNotEquals("{}", manifest);
            for (String path : expected.keySet()) {
                assertTrue(manifest.contains("\"" + path + "\""));
            }
            // Mutation after snapshotting must not affect any later staged or reviewed bytes.
            resolved.clear();
        };
        var draft = replica(bundle -> {
                    assertNoTransaction();
                    reviews.incrementAndGet();
                    assertEquals(expected, bundle.files());
                    assertEquals(expected.size(), files.objects.size());
                    expected.forEach((path, content) -> {
                        byte[] stored = files.objects.entrySet().stream()
                                .filter(entry -> entry.getKey().endsWith("/" + path))
                                .findFirst()
                                .orElseThrow()
                                .getValue();
                        assertArrayEquals(content.getBytes(java.nio.charset.StandardCharsets.UTF_8), stored);
                    });
                })
                .prepareSource(alice, () -> {
                    assertNoTransaction();
                    callbacks.incrementAndGet();
                    return resolved;
                });
        assertEquals(1, callbacks.get());
        assertEquals(1, reviews.get());
        assertEquals(expected.size(), files.writes.get());
        assertNull(draft.expectedRevision());
        assertEquals(expected, draft.bundle().files());
        var installed = service.confirm(alice, draft.draftId(), draft.bundle().digest());
        assertEquals(draft.bundle(), installed.bundle());
        assertEquals(installed, replica(bundle -> {}).get(alice, installed.skillId()));
    }

    @Test
    void sourceErrorsAndInvalidSnapshotsAreRejectedWithoutWritesOrChangingPriorSkills() {
        var first = install("original");
        int writes = files.writes.get();
        int reads = files.reads.get();
        var noReview = replica(bundle -> fail("Unresolved sources must never be reviewed"));
        assertCode(
                SKILL_SOURCE_UNAVAILABLE,
                () -> noReview.prepareSource(alice, () -> {
                    throw new IllegalStateException("private source URL and response body");
                }));
        assertCode(
                SKILL_SOURCE_ANALYSIS_UNAVAILABLE,
                () -> noReview.prepareSource(alice, () -> {
                    throw new SkillException(SKILL_SOURCE_ANALYSIS_UNAVAILABLE);
                }));
        assertCode(SKILL_INVALID_FORMAT, () -> noReview.prepareSource(alice, () -> null));
        assertCode(SKILL_INVALID_FORMAT, () -> noReview.prepareSource(alice, () -> Map.of("../SKILL.md", "invalid")));
        assertEquals(writes, files.writes.get());
        assertEquals(reads, files.reads.get());
        assertEquals(
                4,
                count(
                        "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected' AND manifest = JSON_OBJECT()",
                        alice));
        assertEquals(first, service.get(alice, first.skillId()));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"expired", "rejected", "collecting", "missing", "owner-disabled", "owner-locked", "owner-deleted"
            })
    void lateSourceResultsCannotStageOrPublishAfterReservationOrOwnerRevocation(String change) {
        var first = install("original");
        int writes = files.writes.get();
        int reads = files.reads.get();
        SkillException.Code expected =
                switch (change) {
                    case "expired" -> SKILL_DRAFT_EXPIRED;
                    case "missing" -> SKILL_NOT_FOUND;
                    case "owner-disabled", "owner-locked", "owner-deleted" -> SKILL_ACCESS_DENIED;
                    default -> SKILL_VERSION_CONFLICT;
                };
        var noReview = replica(bundle -> fail("Late source results must never be reviewed"));
        assertCode(
                expected,
                () -> noReview.prepareSource(alice, () -> {
                    assertNoTransaction();
                    String draftId = sql.queryForObject(
                            "SELECT draft_id FROM skill_draft WHERE user_id = ? AND status = 'staging'",
                            String.class,
                            alice);
                    switch (change) {
                        case "expired" ->
                            sql.update(
                                    "UPDATE skill_draft SET expires_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE draft_id = ?",
                                    draftId);
                        case "rejected", "collecting" ->
                            sql.update("UPDATE skill_draft SET status = ? WHERE draft_id = ?", change, draftId);
                        case "missing" -> sql.update("DELETE FROM skill_draft WHERE draft_id = ?", draftId);
                        case "owner-disabled" -> sql.update("UPDATE user SET enabled = 0 WHERE user_id = ?", alice);
                        case "owner-locked" -> sql.update("UPDATE user SET locked = 1 WHERE user_id = ?", alice);
                        case "owner-deleted" -> sql.update("DELETE FROM user WHERE user_id = ?", alice);
                        default -> fail("Unknown revocation");
                    }
                    return input("too-late");
                }));
        assertEquals(writes, files.writes.get());
        assertEquals(reads, files.reads.get());
        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected' AND manifest = JSON_OBJECT()",
                        alice));
        assertEquals(
                1,
                count("SELECT COUNT(*) FROM skill_info WHERE user_id = ? AND revision = ?", alice, first.revision()));
        String rejectedId = sql.queryForObject(
                "SELECT draft_id FROM skill_draft WHERE user_id = ? AND status = 'rejected'", String.class, alice);
        assertCode(
                change.equals("expired")
                        ? SKILL_DRAFT_EXPIRED
                        : change.startsWith("owner-") ? SKILL_ACCESS_DENIED : SKILL_VERSION_CONFLICT,
                () -> service.confirm(alice, rejectedId, "0".repeat(64)));
        assertEquals(first, service.get(alice, first.skillId()));
    }

    @Test
    void concurrentSourceFailuresRespectOwnerAttemptQuotaAcrossReplicas() throws Exception {
        AtomicInteger callbacks = new AtomicInteger();
        var results = race(
                12,
                () -> replica(bundle -> fail("Failed sources must never be reviewed"))
                        .prepareSource(alice, () -> {
                            assertNoTransaction();
                            callbacks.incrementAndGet();
                            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
                        }));
        assertEquals(8, callbacks.get());
        assertEquals(8, codes(results, SKILL_SOURCE_UNAVAILABLE));
        assertEquals(4, codes(results, SKILL_LIMIT_EXCEEDED));
        assertEquals(
                8,
                count(
                        "SELECT COUNT(*) FROM skill_draft WHERE user_id = ? AND status = 'rejected' AND manifest = JSON_OBJECT()",
                        alice));
        assertEquals(0, files.writes.get());
        sql.update(
                "UPDATE skill_draft SET gmt_create = UTC_TIMESTAMP(6) - INTERVAL 31 MINUTE WHERE user_id = ?", alice);
        assertNotNull(service.prepareSource(alice, () -> input("new-window")));
    }

    @Test
    void concurrentSourceReservationsConsumeLastInstalledSlotBeforeCallbacks() throws Exception {
        var first = install("seed");
        for (int i = 0; i < 62; i++) {
            sql.update("""
                    INSERT INTO skill_info (skill_id, user_id, revision, name, description, digest, manifest, gmt_create, gmt_modified)
                    SELECT ?, user_id, revision, name, description, digest, manifest, gmt_create, gmt_modified
                    FROM skill_info WHERE skill_id = ?
                    """, uuid(), first.skillId());
        }
        AtomicInteger callbacks = new AtomicInteger();
        Supplier<Map<String, String>> source = () -> {
            assertNoTransaction();
            callbacks.incrementAndGet();
            return input("last-source-slot");
        };
        var results = race(4, () -> replica(bundle -> assertNoTransaction()).prepareSource(alice, source));
        assertEquals(1, callbacks.get());
        assertEquals(3, codes(results, SKILL_LIMIT_EXCEEDED));
        var draft = (SkillService.Draft) results.stream()
                .filter(SkillService.Draft.class::isInstance)
                .findFirst()
                .orElseThrow();
        service.confirm(alice, draft.draftId(), draft.bundle().digest());
        assertEquals(64, service.list(alice).size());
        assertCode(SKILL_LIMIT_EXCEEDED, () -> service.prepareSource(alice, source));
        assertEquals(1, callbacks.get());
        assertNotNull(service.prepareSource(bob, () -> input("other-owner")));
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

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(20, TimeUnit.SECONDS));
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new AssertionError(failure);
        }
    }

    private static void assertNoTransaction() {
        assertFalse(
                TransactionSynchronizationManager.isActualTransactionActive(),
                "No SQL lock may span source/file/reviewer I/O");
    }

    private static final class RemoteFiles implements FileStore {
        final Map<String, byte[]> objects = new ConcurrentHashMap<>();
        final AtomicInteger reads = new AtomicInteger();
        final AtomicInteger writes = new AtomicInteger();
        final AtomicInteger exists = new AtomicInteger();
        final AtomicInteger deletes = new AtomicInteger();
        volatile Runnable beforeWrite = () -> {};
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
            writes.incrementAndGet();
            beforeWrite.run();
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
            exists.incrementAndGet();
            return objects.containsKey(path);
        }

        @Override
        public void delete(String path) throws IOException {
            assertNoTransaction();
            deletes.incrementAndGet();
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
