package fun.freechat.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import dev.langchain4j.model.TokenCountEstimator;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.LongTermChatMemoryStore;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.impl.LongTermChatMemoryStoreImpl;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBoundsFactory;
import fun.freechat.service.chat.memory.MemoryCheckpoints;
import fun.freechat.service.chat.memory.MemoryConsolidator;
import fun.freechat.service.chat.memory.MemoryHistoryReconciler;
import fun.freechat.service.chat.memory.MemoryIdleWorker;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryModelResolver;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublisher;
import fun.freechat.service.chat.memory.MemoryScheduler;
import fun.freechat.service.chat.memory.MemorySourceReader;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryVectorRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import fun.freechat.service.rag.impl.InMemoryEmbeddingStoreServiceImpl;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.MockMakers;
import org.redisson.api.RBucket;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.ExecutorOptions;
import org.redisson.client.codec.Codec;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

/** Real Spring graph/lifecycle, with interface doubles at the SQL, Redis and provider boundaries. */
@Timeout(15)
class LongTermMemoryRuntimeConfigTest {
    private static final String BACKEND_FAILURE =
            "Long-term memory requires the durable Milvus exact-read embedding backend";
    private static final String SECRET = "private-provider-url-credential-and-memory-payload";

    private final RedissonClient redisson = doubleOf(RedissonClient.class);
    private final RScheduledExecutorService distributed = doubleOf(RScheduledExecutorService.class);
    private final ExactEmbeddingStoreService durable = mock(
            ExactEmbeddingStoreService.class,
            withSettings().mockMaker(MockMakers.SUBCLASS).extraInterfaces(MemoryEmbeddingCleanupService.class));
    private final EmbeddingModelService embeddings = doubleOf(EmbeddingModelService.class);

    @Test
    void wiresOneRuntimeGraphAndStartsOnlyThroughSmartLifecycle() {
        AtomicReference<Boolean> runningDuringBeanInitialization = new AtomicReference<>();
        AtomicReference<MemoryScheduler> scheduler = new AtomicReference<>();
        ScheduledExecutorService unrelatedRenewals = doubleOf(ScheduledExecutorService.class);
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .withBean(
                        "unrelatedRenewals",
                        ScheduledExecutorService.class,
                        () -> unrelatedRenewals,
                        definition -> definition.setDestroyMethodName(""))
                .withInitializer(context -> context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
                    @Override
                    public Object postProcessAfterInitialization(Object bean, String name) {
                        if (bean instanceof MemoryScheduler memoryScheduler) {
                            runningDuringBeanInitialization.set(memoryScheduler.isRunning());
                        }
                        return bean;
                    }
                }))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    for (Class<?> type : List.of(
                            LongTermMemoryProperties.class,
                            MemoryBoundsFactory.class,
                            MemoryVectorRepository.class,
                            MemoryTurnRepository.class,
                            SystemPromptSnapshotStore.class,
                            MemoryPublicationRepository.class,
                            MemorySourceReader.class,
                            MemoryPublisher.class,
                            MemoryCheckpoints.class,
                            MemoryConsolidator.class,
                            MemoryLifecycleRepository.class,
                            MemoryHistoryReconciler.class,
                            MemoryWorkRepository.class,
                            MemoryIdleWorker.class,
                            MemoryScheduler.class,
                            MemoryModelResolver.class,
                            fun.freechat.service.chat.memory.MemoryMaintenance.class,
                            fun.freechat.service.chat.memory.MemoryGarbageCollector.class,
                            LongTermChatMemoryStore.class)) {
                        assertThat(context.getBeansOfType(type))
                                .as(type.getSimpleName())
                                .hasSize(1);
                    }
                    assertThat(context.getBean("memoryBoundsFactory")).isInstanceOf(MemoryBoundsFactory.class);
                    assertThat(context.getBean("memoryModelResolver")).isInstanceOf(MemoryModelResolver.class);
                    assertThat(context).doesNotHaveBean("localFileStore");
                    assertThat(ReflectionTestUtils.getField(context.getBean(SystemPromptSnapshotStore.class), "files"))
                            .isSameAs(context.getBean("snapshotBackend"));
                    assertThat(ReflectionTestUtils.getField(context.getBean(MemoryVectorRepository.class), "stores"))
                            .isSameAs(durable);
                    assertThat(ReflectionTestUtils.getField(context.getBean(MemoryVectorRepository.class), "models"))
                            .isSameAs(embeddings);
                    assertThat(ReflectionTestUtils.getField(context.getBean(LongTermChatMemoryStore.class), "renewals"))
                            .isSameAs(context.getBean("memoryTurnRenewals"));
                    scheduler.set(context.getBean(MemoryScheduler.class));
                    assertThat(runningDuringBeanInitialization.get()).isFalse();
                    assertThat(scheduler.get().isRunning()).isTrue();
                    assertThat(ReflectionTestUtils.getField(scheduler.get(), "redisson"))
                            .isSameAs(redisson);
                    assertThat(ReflectionTestUtils.getField(scheduler.get(), "beans"))
                            .isSameAs(context.getSourceApplicationContext().getBeanFactory());
                    // Startup registers workers; it must not scan history, resolve providers or embed anything.
                    verifyNoInteractions(
                            durable,
                            embeddings,
                            context.getBean(ChatMemoryCoordinationMapper.class),
                            context.getBean(ChatMemoryStateMapper.class),
                            context.getBean(ChatHistoryMapper.class),
                            context.getBean(ChatMemoryCommitMapper.class),
                            context.getBean(ChatContextMapper.class),
                            context.getBean(PlatformTransactionManager.class),
                            context.getBean(AiApiKeyService.class));
                });
        assertThat(scheduler.get().isRunning()).isFalse();
        verifyNoInteractions(unrelatedRenewals);
        assertSharedResourcesRemainOpen();
    }

    @Test
    void usesPrimarySnapshotBackendWhenLocalFileStoreAlsoExists() {
        FileStore primary = doubleOf(FileStore.class);
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .withBean("localFileStore", FileStore.class, () -> doubleOf(FileStore.class))
                .withBean("remoteFileStore", FileStore.class, () -> primary, definition -> definition.setPrimary(true))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(ReflectionTestUtils.getField(context.getBean(SystemPromptSnapshotStore.class), "files"))
                            .isSameAs(primary);
                    verifyNoInteractions(primary, context.getBean("localFileStore", FileStore.class));
                });
    }

    @Test
    void contextCloseDestroysOnlyLocalRenewalsAndDiscardsDelayedWork() throws Exception {
        List<ScheduledThreadPoolExecutor> local = new ArrayList<>();
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .withPropertyValues("chat.memory.long-term.workers-per-node=3")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    ScheduledThreadPoolExecutor turn =
                            context.getBean("memoryTurnRenewals", ScheduledThreadPoolExecutor.class);
                    ScheduledThreadPoolExecutor claim = (ScheduledThreadPoolExecutor)
                            ReflectionTestUtils.getField(context.getBean(MemoryIdleWorker.class), "renewals");
                    local.add(turn);
                    local.add(claim);
                    assertThat(turn.getCorePoolSize()).isEqualTo(3);
                    assertThat(turn.getRemoveOnCancelPolicy()).isTrue();
                    assertThat(turn.getExecuteExistingDelayedTasksAfterShutdownPolicy())
                            .isFalse();
                    assertThat(turn.getContinueExistingPeriodicTasksAfterShutdownPolicy())
                            .isFalse();
                    Thread thread = turn.submit(Thread::currentThread).get(2, TimeUnit.SECONDS);
                    assertThat(thread.isDaemon()).isTrue();
                    assertThat(thread.getName()).startsWith("memory-turn-renewal-");
                    var cancelled = turn.schedule(() -> {}, 1, TimeUnit.DAYS);
                    cancelled.cancel(false);
                    assertThat(turn.getQueue()).isEmpty();
                    turn.schedule(
                            () -> {
                                throw new AssertionError("Delayed renewal ran after shutdown");
                            },
                            1,
                            TimeUnit.DAYS);
                });
        for (ScheduledThreadPoolExecutor executor : local) {
            assertThat(executor.isShutdown()).isTrue();
            assertThat(executor.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
            assertThat(executor.getQueue()).isEmpty();
        }
        assertSharedResourcesRemainOpen();
    }

    @Test
    void reusesConfiguredTokenEstimatorThroughExistingBeanFactory() {
        TokenCountEstimator estimator = doubleOf(TokenCountEstimator.class);
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .withBean("runtimeTokenEstimator", TokenCountEstimator.class, () -> estimator)
                .withPropertyValues("chat.memory.long-term.token-estimator-bean=runtimeTokenEstimator")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var bounds = context.getBean(MemoryBoundsFactory.class).forLanguage("en");
                    assertThat(ReflectionTestUtils.getField(bounds, "estimator"))
                            .isSameAs(estimator);
                    verifyNoInteractions(embeddings, estimator);
                });
    }

    @Test
    void requiresDurableExactBackendRatherThanFallingBackToFileBackedMemory() {
        runtime()
                .withInitializer(context -> context.getBeanFactory()
                        .registerSingleton("inMemoryEmbeddingStoreService", new InMemoryEmbeddingStoreServiceImpl<>()))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), BACKEND_FAILURE);
                });
        verifyNoInteractions(embeddings, redisson);
    }

    @Test
    void namedBackendMustImplementExactReads() {
        runtime()
                .withBean(
                        "milvusEmbeddingStoreService",
                        EmbeddingStoreService.class,
                        () -> doubleOf(EmbeddingStoreService.class))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), BACKEND_FAILURE);
                });
    }

    @Test
    void exactBackendMustAlsoSupportScopedCleanup() {
        ExactEmbeddingStoreService readOnly = doubleOf(ExactEmbeddingStoreService.class);
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> readOnly)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), "Long-term memory requires scoped Milvus cleanup");
                });
        verifyNoInteractions(readOnly);
    }

    @Test
    void doesNotSubstituteAnUnqualifiedExactStoreForTheDurableBackend() {
        runtime()
                .withBean("anotherExactStore", ExactEmbeddingStoreService.class, () -> durable)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), BACKEND_FAILURE);
                });
        verifyNoInteractions(durable);
    }

    @Test
    void backendCreationFailuresDoNotExposeProviderDetails() {
        runtime()
                .withBean(
                        "milvusEmbeddingStoreService",
                        ExactEmbeddingStoreService.class,
                        () -> {
                            IllegalStateException failure =
                                    new IllegalStateException(SECRET, new RuntimeException(SECRET));
                            failure.addSuppressed(new RuntimeException(SECRET));
                            throw failure;
                        },
                        definition -> definition.setLazyInit(true))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), BACKEND_FAILURE);
                });
    }

    @Test
    void schedulerCreationFailuresAreSanitized() {
        ApplicationContextRunner runner = runtime();
        when(redisson.getExecutorService(any(ExecutorOptions.class))).thenThrow(new IllegalStateException(SECRET));
        runner.withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertSanitized(context.getStartupFailure(), "Memory scheduler initialization failed");
                });
        assertSharedResourcesRemainOpen();
    }

    @Test
    void failedRefreshAlsoClosesAlreadyCreatedLocalTimers() {
        List<ScheduledExecutorService> local = new ArrayList<>();
        runtime()
                .withBean("milvusEmbeddingStoreService", ExactEmbeddingStoreService.class, () -> durable)
                .withUserConfiguration(FailedRefresh.class)
                .withInitializer(context -> context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
                    @Override
                    public Object postProcessAfterInitialization(Object bean, String name) {
                        if (bean instanceof MemoryIdleWorker) {
                            local.add((ScheduledExecutorService) ReflectionTestUtils.getField(bean, "renewals"));
                        } else if ("memoryTurnRenewals".equals(name)) {
                            local.add((ScheduledExecutorService) bean);
                        }
                        return bean;
                    }
                }))
                .run(context -> assertThat(context).hasFailed());
        assertThat(local)
                .hasSize(2)
                .allSatisfy(executor -> assertThat(executor.isShutdown()).isTrue());
        assertSharedResourcesRemainOpen();
    }

    private ApplicationContextRunner runtime() {
        RBucket<?> bucket = doubleOf(RBucket.class);
        when(redisson.getExecutorService(any(ExecutorOptions.class))).thenReturn(distributed);
        when(redisson.getBucket(anyString(), any(Codec.class))).thenAnswer(ignored -> bucket);
        when(distributed.hasTask(MemoryScheduler.DISPATCH_TASK)).thenReturn(true);
        return new ApplicationContextRunner()
                .withInitializer(context -> {
                    context.getEnvironment()
                            .getPropertySources()
                            .remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
                    context.getEnvironment()
                            .getPropertySources()
                            .remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
                })
                .withUserConfiguration(Components.class, LongTermMemoryConfig.class, LongTermMemoryRuntimeConfig.class)
                .withBean(RedissonClient.class, () -> redisson, definition -> definition.setDestroyMethodName(""))
                .withBean(EmbeddingModelService.class, () -> embeddings)
                .withBean("snapshotBackend", FileStore.class, () -> doubleOf(FileStore.class))
                .withBean(ChatMemoryCoordinationMapper.class, () -> doubleOf(ChatMemoryCoordinationMapper.class))
                .withBean(ChatMemoryStateMapper.class, () -> doubleOf(ChatMemoryStateMapper.class))
                .withBean(ChatMemoryCommitMapper.class, () -> doubleOf(ChatMemoryCommitMapper.class))
                .withBean(ChatHistoryMapper.class, () -> doubleOf(ChatHistoryMapper.class))
                .withBean(ChatContextMapper.class, () -> doubleOf(ChatContextMapper.class))
                .withBean(PlatformTransactionManager.class, () -> doubleOf(PlatformTransactionManager.class))
                .withBean(ChatContextService.class, () -> doubleOf(ChatContextService.class))
                .withBean(CharacterService.class, () -> doubleOf(CharacterService.class))
                .withBean(SysUserService.class, () -> doubleOf(SysUserService.class))
                .withBean(PromptTaskService.class, () -> doubleOf(PromptTaskService.class))
                .withBean(PromptService.class, () -> doubleOf(PromptService.class))
                .withBean(AiApiKeyService.class, () -> doubleOf(AiApiKeyService.class));
    }

    private void assertSharedResourcesRemainOpen() {
        assertThat(mockingDetails(distributed).getInvocations())
                .extracting(invocation -> invocation.getMethod().getName())
                .doesNotContain("shutdown", "shutdownNow", "close", "delete", "cancelTask");
        assertThat(mockingDetails(redisson).getInvocations())
                .extracting(invocation -> invocation.getMethod().getName())
                .doesNotContain("shutdown", "close");
    }

    private static void assertSanitized(Throwable failure, String message) {
        assertThat(failure).hasRootCauseInstanceOf(IllegalStateException.class).hasRootCauseMessage(message);
        StringWriter trace = new StringWriter();
        failure.printStackTrace(new PrintWriter(trace));
        assertThat(trace.toString()).doesNotContain(SECRET);
    }

    private static <T> T doubleOf(Class<T> type) {
        // Explicit interface subclass doubles do not require final-class instrumentation on Java 26.
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    @ComponentScan(basePackageClasses = MemoryBoundsFactory.class)
    @Import({LongTermChatMemoryStoreImpl.class, SystemPromptSnapshotStore.class})
    static class Components {}

    @Configuration(proxyBeanMethods = false)
    static class FailedRefresh {
        @Bean
        @DependsOn({"memoryIdleWorker", "memoryTurnRenewals"})
        Object failAfterLocalTimersExist() {
            throw new IllegalStateException("Deliberate late refresh failure");
        }
    }
}
