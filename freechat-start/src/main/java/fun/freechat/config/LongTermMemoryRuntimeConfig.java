package fun.freechat.config;

import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBoundsFactory;
import fun.freechat.service.chat.memory.MemoryCheckpoints;
import fun.freechat.service.chat.memory.MemoryConsolidator;
import fun.freechat.service.chat.memory.MemoryGarbageCollector;
import fun.freechat.service.chat.memory.MemoryHistoryReconciler;
import fun.freechat.service.chat.memory.MemoryIdleWorker;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryMaintenance;
import fun.freechat.service.chat.memory.MemoryModelResolver;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublisher;
import fun.freechat.service.chat.memory.MemoryScheduler;
import fun.freechat.service.chat.memory.MemorySourceReader;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryVectorRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/** Runtime graph only; property binding remains usable without SQL, Redis or model services. */
@Configuration(proxyBeanMethods = false)
public class LongTermMemoryRuntimeConfig {
    // MemoryBoundsFactory and MemoryModelResolver are already registered by component scanning.

    @Bean
    public MemoryVectorRepository memoryVectorRepository(
            @Qualifier("milvusEmbeddingStoreService") ObjectProvider<ExactEmbeddingStoreService> stores,
            EmbeddingModelService models,
            MemoryBoundsFactory boundsFactory,
            LongTermMemoryProperties properties) {
        try {
            // Pin memory to the existing durable, strongly consistent exact-read backend. The
            // file-backed/in-memory RAG service must never become a memory fallback, even if primary.
            return new MemoryVectorRepository(stores.getObject(), models, boundsFactory, properties);
        } catch (RuntimeException ignored) {
            // Bean/driver initialization causes can contain credentials, URLs or record payloads.
            throw new IllegalStateException(
                    "Long-term memory requires the durable Milvus exact-read embedding backend");
        }
    }

    @Bean
    public MemoryTurnRepository memoryTurnRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties,
            SystemPromptSnapshotStore snapshots) {
        return new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
    }

    @Bean
    public MemoryPublicationRepository memoryPublicationRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatMemoryCommitMapper commits,
            ChatHistoryMapper histories,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        return new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
    }

    @Bean
    public MemorySourceReader memorySourceReader(MemoryTurnRepository turns) {
        return new MemorySourceReader(turns);
    }

    @Bean
    public MemoryPublisher memoryPublisher(MemoryPublicationRepository publications, MemoryVectorRepository vectors) {
        return new MemoryPublisher(publications, vectors);
    }

    @Bean
    public MemoryCheckpoints memoryCheckpoints(
            MemoryPublicationRepository publications, MemoryVectorRepository vectors) {
        return new MemoryCheckpoints(publications, vectors);
    }

    @Bean
    public MemoryConsolidator memoryConsolidator(
            MemorySourceReader sources,
            MemoryPublicationRepository publications,
            MemoryPublisher publisher,
            MemoryCheckpoints checkpoints,
            MemoryBoundsFactory boundsFactory,
            LongTermMemoryProperties properties) {
        return new MemoryConsolidator(sources, publications, publisher, checkpoints, boundsFactory, properties);
    }

    @Bean
    public MemoryLifecycleRepository memoryLifecycleRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            ChatContextMapper contexts,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        return new MemoryLifecycleRepository(coordination, states, histories, contexts, transactions, properties);
    }

    @Bean
    public MemoryHistoryReconciler memoryHistoryReconciler(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            ChatMemoryCommitMapper commits,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        return new MemoryHistoryReconciler(coordination, states, histories, commits, transactions, properties);
    }

    @Bean
    public MemoryWorkRepository memoryWorkRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        return new MemoryWorkRepository(coordination, states, transactions, properties);
    }

    @Bean(destroyMethod = "close")
    public MemoryIdleWorker memoryIdleWorker(
            MemoryWorkRepository work,
            MemorySourceReader sources,
            MemoryPublicationRepository publications,
            MemoryModelResolver models,
            MemoryConsolidator consolidator,
            LongTermMemoryProperties properties) {
        // close() owns only this worker's local claim-renewal timer, never a Redisson executor.
        return new MemoryIdleWorker(work, sources, publications, models, consolidator, properties);
    }

    @Bean
    public MemoryGarbageCollector memoryGarbageCollector(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryCommitMapper commits,
            @Qualifier("milvusEmbeddingStoreService") ObjectProvider<ExactEmbeddingStoreService> stores,
            LongTermMemoryProperties properties,
            PlatformTransactionManager transactions) {
        try {
            if (!(stores.getObject() instanceof MemoryEmbeddingCleanupService cleanup)) {
                throw new IllegalStateException();
            }
            return new MemoryGarbageCollector(coordination, commits, cleanup, properties, transactions);
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Long-term memory requires scoped Milvus cleanup");
        }
    }

    @Bean
    public MemoryMaintenance memoryMaintenance(
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
        return new MemoryMaintenance(
                contexts, states, coordination, models, lifecycle, turns, reconciler, garbage, redisson, properties);
    }

    @Bean
    public MemoryScheduler memoryScheduler(
            RedissonClient redisson,
            BeanFactory beans,
            MemoryWorkRepository work,
            LongTermMemoryProperties properties) {
        // Spring SmartLifecycle starts/stops local workers after/before the runtime graph's lifetime.
        // Do not register the shared Redisson executor handles as destroyable Spring beans.
        try {
            return new MemoryScheduler(redisson, beans, work, properties);
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Memory scheduler initialization failed");
        }
    }

    @Bean(name = "memoryTurnRenewals", destroyMethod = "shutdownNow")
    public ScheduledExecutorService memoryTurnRenewals(LongTermMemoryProperties properties) {
        ScheduledThreadPoolExecutor renewals = new ScheduledThreadPoolExecutor(
                properties.getWorkersPerNode(),
                Thread.ofPlatform().daemon().name("memory-turn-renewal-", 0).factory());
        renewals.setRemoveOnCancelPolicy(true);
        renewals.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        renewals.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        return renewals;
    }
}
