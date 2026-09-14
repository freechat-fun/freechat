package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.mapper.ChatContextDynamicSqlSupport;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateDynamicSqlSupport;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

@Slf4j
public final class MemoryMaintenance {
    private final ChatContextMapper contexts;
    private final ChatMemoryStateMapper states;
    private final ChatMemoryCoordinationMapper coordination;
    private final MemoryModelResolver models;
    private final MemoryLifecycleRepository lifecycle;
    private final MemoryTurnRepository turns;
    private final MemoryHistoryReconciler reconciler;
    private final MemoryGarbageCollector garbage;
    private final RedissonClient redisson;
    private final LongTermMemoryProperties properties;

    public MemoryMaintenance(
            ChatContextMapper contexts,
            ChatMemoryStateMapper states,
            ChatMemoryCoordinationMapper coordination,
            MemoryModelResolver models,
            MemoryLifecycleRepository lifecycle,
            MemoryTurnRepository turns,
            MemoryHistoryReconciler reconciler,
            MemoryGarbageCollector garbage,
            RedissonClient redisson,
            LongTermMemoryProperties properties) {
        this.contexts = Objects.requireNonNull(contexts);
        this.states = Objects.requireNonNull(states);
        this.coordination = Objects.requireNonNull(coordination);
        this.models = Objects.requireNonNull(models);
        this.lifecycle = Objects.requireNonNull(lifecycle);
        this.turns = Objects.requireNonNull(turns);
        this.reconciler = Objects.requireNonNull(reconciler);
        this.garbage = Objects.requireNonNull(garbage);
        this.redisson = Objects.requireNonNull(redisson);
        this.properties = Objects.requireNonNull(properties);
    }

    public void scan() {
        page("contexts", this::contextPage, this::recover);
        page("states", this::statePage, this::recover);
        page("attempts", after -> garbage.candidates(after, properties.getDispatchBatchSize()), garbage::collect);
    }

    public void recover(String chatId) {
        if (chatId == null
                || chatId.isBlank()
                || chatId.length() > 32
                || chatId.endsWith("-assist")
                || chatId.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid memory recovery identifier");
        }
        var lock = redisson.getLock("freechat:chat:coordination:v1:" + chatId);
        if (!lock.tryLock()) {
            return;
        }
        try {
            ChatMemoryState previous = states.selectByPrimaryKey(chatId).orElse(null);
            if (previous != null && "deleted".equals(previous.getStatus())) {
                return;
            }
            LocalDateTime now = coordination.databaseNow();
            if (previous != null
                    && previous.getTurnToken() != null
                    && previous.getTurnLeaseUntil() != null
                    && previous.getTurnDeadline() != null
                    && now.isBefore(previous.getTurnLeaseUntil())
                    && now.isBefore(previous.getTurnDeadline())) {
                return;
            }
            if (previous != null && "active".equals(previous.getStatus())) {
                turns.recoverExpired(MemoryScopeLedger.scope(previous));
            }
            ChatContext context = contexts.selectByPrimaryKey(chatId).orElse(null);
            if (context == null) {
                if (previous != null) {
                    lifecycle.delete(chatId);
                }
                return;
            }
            if (!"u2c".equals(context.getChatType())) {
                lifecycle.disable(chatId);
                return;
            }
            var configured = models.configuration(chatId);
            if (configured.isEmpty()) {
                lifecycle.disable(chatId);
                return;
            }
            var configuration = configured.orElseThrow();
            ChatContext current = contexts.selectByPrimaryKey(chatId).orElseThrow(MemoryMaintenance::failed);
            if (!configuration.userId().equals(current.getUserId())
                    || !Objects.equals(context.getBackendId(), current.getBackendId())
                    || !Objects.equals(context.getGmtModified(), current.getGmtModified())) {
                throw failed();
            }
            ChatMemoryState active = lifecycle.activate(
                    new MemoryScope(
                            chatId,
                            configuration.userId(),
                            configuration.characterUid(),
                            1,
                            EmbeddingStoreType.longTermMemoryTypeForLang(configuration.language())),
                    configuration.fingerprint());
            MemoryScope scope = MemoryScopeLedger.scope(active);
            turns.recoverExpired(scope);
            reconciler.reconcile(scope, configuration.fingerprint(), configuration.examples());
        } catch (RuntimeException ignored) {
            throw failed();
        } finally {
            boolean interrupted = Thread.interrupted();
            try {
                lock.unlock();
            } catch (RuntimeException ignored) {
                log.warn("Memory recovery coordination deferred");
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private List<String> contextPage(String after) {
        return contexts
                .selectMany(select(ChatContextDynamicSqlSupport.chatId)
                        .from(ChatContextDynamicSqlSupport.chatContext)
                        .where(ChatContextDynamicSqlSupport.chatId, isGreaterThanWhenPresent(after))
                        .and(ChatContextDynamicSqlSupport.chatType, isEqualTo("u2c"))
                        .orderBy(ChatContextDynamicSqlSupport.chatId)
                        .limit(properties.getDispatchBatchSize())
                        .build()
                        .render(RenderingStrategies.MYBATIS3))
                .stream()
                .map(ChatContext::getChatId)
                .toList();
    }

    private List<String> statePage(String after) {
        return states
                .selectMany(select(ChatMemoryStateDynamicSqlSupport.chatId)
                        .from(ChatMemoryStateDynamicSqlSupport.chatMemoryState)
                        .where(ChatMemoryStateDynamicSqlSupport.chatId, isGreaterThan(after == null ? "" : after))
                        .orderBy(ChatMemoryStateDynamicSqlSupport.chatId)
                        .limit(properties.getDispatchBatchSize())
                        .build()
                        .render(RenderingStrategies.MYBATIS3))
                .stream()
                .map(ChatMemoryState::getChatId)
                .toList();
    }

    private void page(String category, Function<String, List<String>> fetch, Consumer<String> visit) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            RBucket<String> cursor =
                    redisson.getBucket("freechat:memory:maintenance:v1:cursor:" + category, StringCodec.INSTANCE);
            String after = cursor.get();
            List<String> ids = fetch.apply(after == null || after.isEmpty() ? null : after);
            if (ids.isEmpty()) {
                cursor.compareAndSet(after, "");
                return;
            }
            long deadline = System.nanoTime()
                    + Math.min(
                            properties.getJobMaxDuration().toNanos(),
                            java.util.concurrent.TimeUnit.SECONDS.toNanos(30));
            for (String id : ids) {
                if (Thread.currentThread().isInterrupted() || System.nanoTime() >= deadline) {
                    return;
                }
                try {
                    visit.accept(id);
                } catch (RuntimeException ignored) {
                    log.warn("Memory maintenance row deferred");
                }
                // Failed or busy rows are revisited next sweep; they cannot starve later IDs.
                if (!cursor.compareAndSet(after, id)) {
                    return;
                }
                after = id;
            }
        } catch (RuntimeException ignored) {
            log.warn("Memory maintenance scan deferred");
        }
    }

    private static IllegalStateException failed() {
        return new IllegalStateException("Memory recovery deferred");
    }
}
