package fun.freechat.service.skills;

import static fun.freechat.mapper.SkillDraftDynamicSqlSupport.skillDraft;
import static fun.freechat.mapper.SkillInfoDynamicSqlSupport.skillInfo;
import static fun.freechat.service.skills.SkillException.Code.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import fun.freechat.mapper.SkillCoordinationMapper;
import fun.freechat.mapper.SkillDraftMapper;
import fun.freechat.mapper.SkillInfoMapper;
import fun.freechat.mapper.UserDynamicSqlSupport;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.SkillDraft;
import fun.freechat.model.SkillInfo;
import fun.freechat.service.common.FileStore;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/** SQL is authoritative; immutable shared FileStore revisions are staged before pointer publication. */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DatabaseSkillService implements SkillService {
    private static final int MAX_INSTALLED = 64;
    private static final int MAX_ATTEMPTS = 8;
    private static final int DRAFT_MINUTES = 15;
    private static final int GC_GRACE_MINUTES = 60;
    private static final int GC_BATCH = 20;
    private final SkillInfoMapper skills;
    private final SkillDraftMapper drafts;
    private final UserMapper users;
    private final SkillCoordinationMapper coordination;
    private final SkillRevisionFiles files;
    private final SkillBundleValidator validator;
    private final SkillReviewer reviewer;
    private final TransactionTemplate transactions;

    public DatabaseSkillService(
            SkillInfoMapper skills,
            SkillDraftMapper drafts,
            UserMapper users,
            SkillCoordinationMapper coordination,
            FileStore files,
            SkillBundleValidator validator,
            SkillReviewer reviewer,
            PlatformTransactionManager transactionManager) {
        this.skills = skills;
        this.drafts = drafts;
        this.users = users;
        this.coordination = coordination;
        this.files = new SkillRevisionFiles(files);
        this.validator = validator;
        this.reviewer = reviewer;
        this.transactions = new TransactionTemplate(transactionManager);
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    @Override
    public List<Summary> list(String ownerId) {
        owner(ownerId);
        return transaction(() -> skills
                .select(q -> q.where(skillInfo.userId, isEqualTo(ownerId))
                        .orderBy(skillInfo.gmtModified.descending(), skillInfo.skillId)
                        .limit(MAX_INSTALLED))
                .stream()
                .filter(row -> ownerId.equals(row.getUserId()))
                .map(row -> new Summary(
                        row.getSkillId(),
                        row.getRevision(),
                        row.getName(),
                        row.getDescription(),
                        row.getGmtModified().toInstant(ZoneOffset.UTC)))
                .toList());
    }

    @Override
    public Installed get(String ownerId, String skillId) {
        owner(ownerId);
        identifier(skillId, SKILL_NOT_FOUND);
        // No service cache or transaction spanning I/O. Retry if a publication races the file read.
        for (int retry = 0; retry < 3; retry++) {
            SkillInfo row =
                    transaction(() -> findSkill(ownerId, skillId, false).orElseThrow(DatabaseSkillService::notFound));
            SkillBundle bundle;
            try {
                bundle = load(
                        row.getUserId(),
                        row.getSkillId(),
                        row.getRevision(),
                        row.getManifest(),
                        row.getDigest(),
                        row.getName(),
                        row.getDescription());
            } catch (SkillException failure) {
                boolean changed = transaction(() -> findSkill(ownerId, skillId, false)
                        .map(current -> !current.getRevision().equals(row.getRevision()))
                        .orElse(true));
                if (changed) {
                    continue;
                }
                throw failure;
            }
            boolean current = transaction(() -> findSkill(ownerId, skillId, false)
                    .map(next -> next.getRevision().equals(row.getRevision()))
                    .orElse(false));
            if (current) {
                return installed(row, bundle);
            }
        }
        throw conflict();
    }

    @Override
    public Draft prepare(String ownerId, String skillId, String expectedRevision, Map<String, String> input) {
        owner(ownerId);
        if (skillId != null) {
            identifier(skillId, SKILL_NOT_FOUND);
            identifier(expectedRevision, SKILL_VERSION_CONFLICT);
        } else if (expectedRevision != null) {
            throw conflict();
        }
        // Cheap, bounded path/encoding checks only. Content validation and review happen on staged bytes.
        SkillRevisionFiles.Snapshot snapshot = SkillRevisionFiles.snapshot(input);
        SkillDraft reserved = transaction(() -> reserve(ownerId, skillId, expectedRevision, snapshot.manifest()));
        try {
            files.stage(
                    ownerId,
                    reserved.getSkillId(),
                    reserved.getRevision(),
                    snapshot,
                    () -> transaction(() -> {
                        lockOwner(ownerId);
                        checkStaging(lockDraft(ownerId, reserved.getDraftId()), coordination.databaseNow());
                        return null;
                    }));
            SkillBundle bundle = validator.validate(
                    files.read(ownerId, reserved.getSkillId(), reserved.getRevision(), reserved.getManifest()));
            checkBundle(bundle);
            transaction(() -> {
                lockOwner(ownerId);
                SkillDraft row = lockDraft(ownerId, reserved.getDraftId());
                checkStaging(row, coordination.databaseNow());
                checkExpected(row, true);
                return null;
            });
            try {
                reviewer.review(bundle);
            } catch (SkillException failure) {
                throw failure;
            } catch (RuntimeException ignored) {
                throw new SkillException(SKILL_REVIEW_UNAVAILABLE);
            }
            transaction(() -> {
                lockOwner(ownerId);
                SkillDraft row = lockDraft(ownerId, reserved.getDraftId());
                LocalDateTime now = coordination.databaseNow();
                checkStaging(row, now);
                checkExpected(row, true);
                row.withStatus("ready")
                        .withName(bundle.name())
                        .withDescription(bundle.description())
                        .withDigest(bundle.digest())
                        .withGmtModified(now);
                requireOne(drafts.updateByPrimaryKey(row));
                return null;
            });
            return new Draft(
                    reserved.getDraftId(),
                    reserved.getSkillId(),
                    expectedRevision,
                    bundle,
                    reserved.getExpiresAt().toInstant(ZoneOffset.UTC));
        } catch (RuntimeException failure) {
            reject(reserved);
            if (failure instanceof SkillException skillFailure) {
                throw skillFailure;
            }
            throw SkillRevisionFiles.unavailable();
        }
    }

    @Override
    public Installed confirm(String ownerId, String draftId, String digest) {
        owner(ownerId);
        identifier(draftId, SKILL_NOT_FOUND);
        if (digest == null || !digest.matches("[0-9a-f]{64}")) {
            throw conflict();
        }
        SkillDraft snapshot = transaction(() -> {
            lockOwner(ownerId);
            SkillDraft row = lockDraft(ownerId, draftId);
            checkReady(row, digest, coordination.databaseNow());
            checkExpected(row, true);
            return row;
        });
        SkillBundle bundle = load(
                ownerId,
                snapshot.getSkillId(),
                snapshot.getRevision(),
                snapshot.getManifest(),
                snapshot.getDigest(),
                snapshot.getName(),
                snapshot.getDescription());
        return transaction(() -> {
            lockOwner(ownerId);
            SkillDraft row = lockDraft(ownerId, draftId);
            LocalDateTime now = coordination.databaseNow();
            checkReady(row, digest, now);
            if (!row.getRevision().equals(snapshot.getRevision())
                    || !row.getManifest().equals(snapshot.getManifest())) {
                throw conflict();
            }
            SkillInfo previous = checkExpected(row, true);
            if (previous == null && installedCount(ownerId) >= MAX_INSTALLED) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            SkillInfo next = new SkillInfo()
                    .withSkillId(row.getSkillId())
                    .withUserId(ownerId)
                    .withRevision(row.getRevision())
                    .withName(row.getName())
                    .withDescription(row.getDescription())
                    .withDigest(row.getDigest())
                    .withManifest(row.getManifest())
                    .withGmtCreate(previous == null ? now : previous.getGmtCreate())
                    .withGmtModified(now);
            if (previous == null) {
                requireOne(skills.insert(next));
            } else {
                retire(previous, now);
                requireOne(skills.updateByPrimaryKey(next));
            }
            row.withStatus("published").withGcAfter(null).withGmtModified(now);
            requireOne(drafts.updateByPrimaryKey(row));
            return installed(next, bundle);
        });
    }

    @Override
    public void delete(String ownerId, String skillId, String expectedRevision) {
        owner(ownerId);
        identifier(skillId, SKILL_NOT_FOUND);
        identifier(expectedRevision, SKILL_VERSION_CONFLICT);
        transaction(() -> {
            lockOwner(ownerId);
            SkillInfo row = findSkill(ownerId, skillId, true).orElseThrow(DatabaseSkillService::notFound);
            if (!row.getRevision().equals(expectedRevision)) {
                throw conflict();
            }
            retire(row, coordination.databaseNow());
            requireOne(skills.delete(q -> q.where(skillInfo.skillId, isEqualTo(skillId))
                    .and(skillInfo.userId, isEqualTo(ownerId))
                    .and(skillInfo.revision, isEqualTo(expectedRevision))));
            return null;
        });
    }

    private SkillDraft reserve(String owner, String requestedId, String expected, String manifest) {
        lockOwner(owner);
        LocalDateTime now = coordination.databaseNow();
        if (requestedId != null) {
            SkillInfo existing = findSkill(owner, requestedId, true).orElseThrow(DatabaseSkillService::notFound);
            if (!existing.getRevision().equals(expected)) {
                throw conflict();
            }
        }
        long attempts = drafts.count(q -> q.where(skillDraft.userId, isEqualTo(owner))
                .and(skillDraft.gmtCreate, isGreaterThan(now.minusMinutes(30))));
        if (attempts >= MAX_ATTEMPTS) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        if (requestedId == null) {
            long pending = drafts.count(q -> q.where(skillDraft.userId, isEqualTo(owner))
                    .and(skillDraft.expectedRevision, isNull())
                    .and(skillDraft.status, isIn("staging", "ready"))
                    .and(skillDraft.expiresAt, isGreaterThan(now)));
            if (installedCount(owner) + pending >= MAX_INSTALLED) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
        }
        String id = uuid();
        SkillDraft row = new SkillDraft()
                .withDraftId(id)
                .withRevision(id)
                .withSkillId(requestedId == null ? uuid() : requestedId)
                .withUserId(owner)
                .withExpectedRevision(expected)
                .withManifest(manifest)
                .withStatus("staging")
                .withExpiresAt(now.plusMinutes(DRAFT_MINUTES))
                .withGcAfter(now.plusMinutes(DRAFT_MINUTES + GC_GRACE_MINUTES))
                .withGmtCreate(now)
                .withGmtModified(now);
        requireOne(drafts.insert(row));
        return row;
    }

    private SkillInfo checkExpected(SkillDraft row, boolean locked) {
        Optional<SkillInfo> previous = findSkill(row.getUserId(), row.getSkillId(), locked);
        if (row.getExpectedRevision() == null) {
            if (previous.isPresent()) {
                throw conflict();
            }
            return null;
        }
        SkillInfo info = previous.orElseThrow(DatabaseSkillService::notFound);
        if (!info.getRevision().equals(row.getExpectedRevision())) {
            throw conflict();
        }
        return info;
    }

    private void retire(SkillInfo info, LocalDateTime now) {
        SkillDraft prior = lockDraft(info.getUserId(), info.getRevision());
        if (!info.getSkillId().equals(prior.getSkillId()) || !"published".equals(prior.getStatus())) {
            throw SkillRevisionFiles.unavailable();
        }
        requireOne(drafts.updateByPrimaryKey(prior.withStatus("retired")
                .withGcAfter(now.plusMinutes(GC_GRACE_MINUTES))
                .withGmtModified(now)));
    }

    private void reject(SkillDraft reservation) {
        try {
            transaction(() -> {
                // Missing users may still have server-owned files requiring cleanup.
                users.selectOne(q -> q.where(UserDynamicSqlSupport.userId, isEqualTo(reservation.getUserId()))
                        .forUpdate());
                Optional<SkillDraft> existing = findDraft(reservation.getUserId(), reservation.getDraftId());
                LocalDateTime now = coordination.databaseNow();
                SkillDraft row = existing.orElse(reservation);
                if (!"published".equals(row.getStatus()) && !"retired".equals(row.getStatus())) {
                    row.withStatus("rejected")
                            .withGcAfter(now.plusMinutes(GC_GRACE_MINUTES))
                            .withGmtModified(now);
                    requireOne(existing.isPresent() ? drafts.updateByPrimaryKey(row) : drafts.insert(row));
                }
                return null;
            });
        } catch (SkillException ignored) {
            // The reservation already persisted a complete manifest and an expiry-based GC deadline.
        }
    }

    /** Bounded shared queue; every file deletion is derived from a validated durable manifest. */
    @Scheduled(fixedDelayString = "${skills.cleanup-interval-ms:60000}")
    public void sweepExpired() {
        try {
            List<SkillDraft> candidates = transaction(() -> {
                LocalDateTime now = coordination.databaseNow();
                return drafts.select(q -> q.where(skillDraft.gcAfter, isLessThanOrEqualTo(now))
                        .orderBy(skillDraft.gcAfter, skillDraft.draftId)
                        .limit(GC_BATCH));
            });
            for (SkillDraft candidate : candidates) {
                try {
                    SkillDraft claimed = transaction(() -> claimCleanup(candidate));
                    if (claimed == null) {
                        continue;
                    }
                    files.delete(
                            claimed.getUserId(), claimed.getSkillId(), claimed.getRevision(), claimed.getManifest());
                    transaction(() -> {
                        users.selectOne(q -> q.where(UserDynamicSqlSupport.userId, isEqualTo(claimed.getUserId()))
                                .forUpdate());
                        Optional<SkillDraft> current = findDraft(claimed.getUserId(), claimed.getDraftId());
                        if (current.isEmpty()
                                || !"collecting".equals(current.get().getStatus())
                                || !current.get().getGmtModified().equals(claimed.getGmtModified())) {
                            return null;
                        }
                        LocalDateTime now = coordination.databaseNow();
                        if ("cleaned".equals(candidate.getStatus())
                                && candidate.getGcAfter().isBefore(now)) {
                            requireOne(drafts.deleteByPrimaryKey(claimed.getDraftId()));
                        } else {
                            // Re-sweep once after a day for delayed in-flight writes; retain throttle metadata.
                            requireOne(drafts.updateByPrimaryKey(current.get()
                                    .withStatus("cleaned")
                                    .withGcAfter(now.plusDays(1))
                                    .withGmtModified(now)));
                        }
                        return null;
                    });
                } catch (RuntimeException ignored) {
                    // No payload/provider logging; failed collection stays durable and is retried later.
                }
            }
        } catch (RuntimeException ignored) {
            // Database outage: do not delete any files without a committed cleanup claim.
        }
    }

    private SkillDraft claimCleanup(SkillDraft candidate) {
        users.selectOne(q -> q.where(UserDynamicSqlSupport.userId, isEqualTo(candidate.getUserId()))
                .forUpdate());
        SkillDraft row =
                findDraft(candidate.getUserId(), candidate.getDraftId()).orElse(null);
        LocalDateTime now = coordination.databaseNow();
        if (row == null
                || row.getGcAfter() == null
                || row.getGcAfter().isAfter(now)
                || "published".equals(row.getStatus())) {
            return null;
        }
        Optional<SkillInfo> installed = findSkill(row.getUserId(), row.getSkillId(), true);
        if (installed.map(info -> info.getRevision().equals(row.getRevision())).orElse(false)) {
            return null;
        }
        if (("ready".equals(row.getStatus()) || "staging".equals(row.getStatus()))
                && row.getExpiresAt().isAfter(now)) {
            return null;
        }
        // Lease avoids concurrent collectors; collection cannot transition back into publication.
        row.withStatus("collecting").withGcAfter(now.plusMinutes(5)).withGmtModified(now);
        requireOne(drafts.updateByPrimaryKey(row));
        return row;
    }

    private SkillBundle load(
            String owner,
            String skill,
            String revision,
            String manifest,
            String digest,
            String name,
            String description) {
        try {
            SkillBundle bundle = validator.validate(files.read(owner, skill, revision, manifest));
            checkBundle(bundle);
            if (!Objects.equals(digest, bundle.digest())
                    || !Objects.equals(name, bundle.name())
                    || !Objects.equals(description, bundle.description())) {
                throw SkillRevisionFiles.unavailable();
            }
            return bundle;
        } catch (RuntimeException ignored) {
            throw SkillRevisionFiles.unavailable();
        }
    }

    private void lockOwner(String owner) {
        var user = users.selectOne(q ->
                        q.where(UserDynamicSqlSupport.userId, isEqualTo(owner)).forUpdate())
                .orElseThrow(() -> new SkillException(SKILL_ACCESS_DENIED));
        // The legacy user table uses a case-insensitive collation; never alias owner identities.
        if (!owner.equals(user.getUserId())
                || !Byte.valueOf((byte) 1).equals(user.getEnabled())
                || Byte.valueOf((byte) 1).equals(user.getLocked())) {
            throw new SkillException(SKILL_ACCESS_DENIED);
        }
    }

    private Optional<SkillInfo> findSkill(String owner, String id, boolean locked) {
        return skills.selectOne(q -> {
                    var query = q.where(skillInfo.skillId, isEqualTo(id)).and(skillInfo.userId, isEqualTo(owner));
                    return locked ? query.forUpdate() : query;
                })
                .filter(row -> owner.equals(row.getUserId()));
    }

    private Optional<SkillDraft> findDraft(String owner, String id) {
        return drafts.selectOne(q -> q.where(skillDraft.draftId, isEqualTo(id))
                        .and(skillDraft.userId, isEqualTo(owner))
                        .forUpdate())
                .filter(row -> owner.equals(row.getUserId()));
    }

    private SkillDraft lockDraft(String owner, String id) {
        return findDraft(owner, id).orElseThrow(DatabaseSkillService::notFound);
    }

    private long installedCount(String owner) {
        return skills.count(q -> q.where(skillInfo.userId, isEqualTo(owner)));
    }

    private static void checkReady(SkillDraft row, String digest, LocalDateTime now) {
        if (!row.getExpiresAt().isAfter(now)) {
            throw new SkillException(SKILL_DRAFT_EXPIRED);
        }
        if (!"ready".equals(row.getStatus()) || !digest.equals(row.getDigest())) {
            throw conflict();
        }
    }

    private static void checkStaging(SkillDraft row, LocalDateTime now) {
        if (!row.getExpiresAt().isAfter(now)) {
            throw new SkillException(SKILL_DRAFT_EXPIRED);
        }
        if (!"staging".equals(row.getStatus())) {
            throw conflict();
        }
    }

    private static void checkBundle(SkillBundle bundle) {
        if (bundle == null
                || bundle.name() == null
                || bundle.name().length() > 128
                || bundle.description() == null
                || bundle.description().length() > 4096
                || bundle.digest() == null
                || !bundle.digest().matches("[0-9a-f]{64}")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
    }

    private <T> T transaction(Supplier<T> operation) {
        try {
            return transactions.execute(status -> {
                try {
                    return operation.get();
                } catch (SkillException failure) {
                    throw failure;
                } catch (RuntimeException ignored) {
                    throw SkillRevisionFiles.unavailable();
                }
            });
        } catch (SkillException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            throw SkillRevisionFiles.unavailable();
        }
    }

    private static Installed installed(SkillInfo row, SkillBundle bundle) {
        return new Installed(
                row.getSkillId(),
                row.getUserId(),
                row.getRevision(),
                bundle,
                row.getGmtModified().toInstant(ZoneOffset.UTC));
    }

    private static void owner(String owner) {
        if (owner == null || owner.isBlank() || owner.length() > 32) {
            throw new SkillException(SKILL_ACCESS_DENIED);
        }
    }

    private static void identifier(String id, SkillException.Code code) {
        if (!SkillRevisionFiles.opaqueId(id)) {
            throw new SkillException(code);
        }
    }

    private static void requireOne(int count) {
        if (count != 1) {
            throw conflict();
        }
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static SkillException conflict() {
        return new SkillException(SKILL_VERSION_CONFLICT);
    }

    private static SkillException notFound() {
        return new SkillException(SKILL_NOT_FOUND);
    }
}
