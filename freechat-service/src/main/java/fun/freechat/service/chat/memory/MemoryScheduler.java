package fun.freechat.service.chat.memory;

import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.WorkerOptions;
import org.redisson.api.options.ExecutorOptions;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.Kryo5Codec;
import org.redisson.executor.SpringTasksInjector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

/** Distributed dispatch only. SQL owns eligibility, claim fencing and all business retries. */
@Slf4j
public final class MemoryScheduler implements SmartLifecycle {
    public static final String DISPATCH_EXECUTOR = "freechat:memory:dispatch:v1";
    public static final String WORK_EXECUTOR = "freechat:memory:extraction:v1";
    public static final String MAINTENANCE_EXECUTOR = "freechat:memory:maintenance:v1";
    public static final String DISPATCH_TASK = "memory-dispatch-v1";
    public static final String MAINTENANCE_TASK = "memory-maintenance-v1";
    private static final String CURSOR_KEY = DISPATCH_EXECUTOR + ":cursor";
    private static final long STOP_MILLIS = 5_000;

    private final RedissonClient redisson;
    private final BeanFactory beans;
    private final MemoryWorkRepository work;
    private final LongTermMemoryProperties properties;
    private final RScheduledExecutorService dispatcher;
    private final RScheduledExecutorService extraction;
    private final RScheduledExecutorService maintenance;
    private final RBucket<String> cursor;
    private final RBucket<String> registration;
    private final Duration dispatchTimeout;
    private final Duration queueTtl;
    private final long watchdogMillis;
    private ScheduledThreadPoolExecutor watchdog;
    private ThreadPoolExecutor dispatchPool;
    private ThreadPoolExecutor extractionPool;
    private ThreadPoolExecutor maintenancePool;
    private boolean registered;
    private volatile boolean running;

    public MemoryScheduler(
            RedissonClient redisson,
            BeanFactory beans,
            MemoryWorkRepository work,
            LongTermMemoryProperties properties) {
        this.redisson = Objects.requireNonNull(redisson);
        this.beans = Objects.requireNonNull(beans);
        this.work = Objects.requireNonNull(work);
        this.properties = Objects.requireNonNull(properties);
        properties.afterPropertiesSet();
        dispatchTimeout = Duration.ofMillis(
                Math.max(1_000, Math.min(30_000, properties.getScanInterval().toMillis())));
        watchdogMillis = dispatchTimeout.toMillis();
        queueTtl = minimum(properties.getExtractionClaimLease(), properties.getJobMaxDuration());
        // Plain, instantiable codecs: Redisson can reconstruct these with a task ClassLoader.
        // Never inherit the application's anonymous/global codec or serialize Spring services.
        dispatcher = redisson.getExecutorService(ExecutorOptions.name(DISPATCH_EXECUTOR)
                .codec(new Kryo5Codec())
                .taskRetryInterval(dispatchTimeout.multipliedBy(2)));
        extraction = redisson.getExecutorService(ExecutorOptions.name(WORK_EXECUTOR)
                .codec(new Kryo5Codec())
                .taskRetryInterval(properties.getJobMaxDuration().plus(properties.getLeaseRenewInterval())));
        maintenance = redisson.getExecutorService(ExecutorOptions.name(MAINTENANCE_EXECUTOR)
                .codec(new Kryo5Codec())
                .taskRetryInterval(properties.getJobMaxDuration().plus(properties.getLeaseRenewInterval())));
        cursor = redisson.getBucket(CURSOR_KEY, StringCodec.INSTANCE);
        registration = redisson.getBucket(DISPATCH_EXECUTOR + ":node:" + UUID.randomUUID(), StringCodec.INSTANCE);
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        dispatchPool = pool(1, "memory-dispatch-");
        extractionPool = pool(properties.getWorkersPerNode(), "memory-extraction-");
        maintenancePool = pool(1, "memory-maintenance-");
        watchdog = new ScheduledThreadPoolExecutor(
                1,
                Thread.ofPlatform()
                        .daemon()
                        .name("memory-scheduler-watchdog-", 0)
                        .factory());
        watchdog.setRemoveOnCancelPolicy(true);
        running = true;
        // The sole local timer repairs Redisson lifecycle metadata. It never scans SQL or extracts.
        watchdog.scheduleWithFixedDelay(this::ensureRegistration, 0, watchdogMillis, TimeUnit.MILLISECONDS);
    }

    private synchronized void ensureRegistration() {
        if (!running) {
            return;
        }
        try {
            // A node-specific expiring marker detects Redis data loss without counting all workers
            // (Redisson's cluster-wide worker count can block for ten minutes).
            if (!registered || !registration.isExists()) {
                deregister();
                registered = false;
                extraction.registerWorkers(
                        options(properties.getWorkersPerNode(), extractionPool, properties.getJobMaxDuration()));
                dispatcher.registerWorkers(options(1, dispatchPool, dispatchTimeout));
                maintenance.registerWorkers(options(1, maintenancePool, properties.getJobMaxDuration()));
                registered = true;
            }
            registration.set("1", Duration.ofMillis(watchdogMillis * 4));
            if (!dispatcher.hasTask(DISPATCH_TASK)) {
                // The stable ID is atomically deduplicated by Redisson, including startup races.
                dispatcher.scheduleWithFixedDelay(
                        DISPATCH_TASK, new DispatchTask(), Duration.ZERO, properties.getScanInterval());
            }
            if (!maintenance.hasTask(MAINTENANCE_TASK)) {
                maintenance.scheduleWithFixedDelay(
                        MAINTENANCE_TASK, new MaintenanceTask(), Duration.ZERO, properties.getScanInterval());
            }
        } catch (RuntimeException ignored) {
            log.warn("Memory scheduler registration deferred");
        }
    }

    private WorkerOptions options(int workers, ThreadPoolExecutor pool, Duration timeout) {
        return WorkerOptions.defaults()
                .workers(workers)
                .executorService(pool)
                .tasksInjector(new SpringTasksInjector(beans))
                .taskTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /** Invoked only by the Redisson task, never by the local lifecycle watchdog. */
    public void dispatch() {
        if (!running) {
            return;
        }
        // A non-waiting coordination lock prevents retry overlap from regressing the keyset cursor.
        // Its bounded lease is secondary to SQL claim fencing; extraction never holds this lock.
        var lock = redisson.getLock(DISPATCH_EXECUTOR + ":scan");
        boolean acquired = false;
        try {
            acquired = lock.tryLock(0, dispatchTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!acquired) {
                return;
            }
            String after = cursor.get();
            List<String> candidates = work.due(after, properties.getDispatchBatchSize());
            if (candidates.isEmpty()) {
                cursor.compareAndSet(after, ""); // End of scan: revisit earlier IDs next tick.
                return;
            }
            long until = System.nanoTime() + dispatchTimeout.toNanos();
            for (String chatId : candidates) {
                if (!running || Thread.currentThread().isInterrupted() || System.nanoTime() >= until) {
                    break;
                }
                var selected = work.claim(chatId); // Commit the SQL claim BEFORE contacting the queue.
                if (selected.isPresent()) {
                    Claim claim = selected.orElseThrow();
                    try {
                        // Token, not chat ID: an old/ambiguous task can never execute a successor's claim.
                        extraction.submit(claim.token(), new ExtractionTask(claim), queueTtl);
                    } catch (RuntimeException ignored) {
                        // Submission might have succeeded before the connection failed. Fence that
                        // exact token, never cancel a chat-wide job or charge a business failure.
                        try {
                            work.release(claim);
                        } catch (RuntimeException deferred) {
                            // Durable SQL lease/deadline still makes the work discoverable again.
                            log.warn("Memory enqueue claim release deferred");
                        }
                        log.warn("Memory enqueue deferred");
                    }
                }
                // CAS also prevents a delayed dispatcher from rewinding another node's progress.
                if (!cursor.compareAndSet(after, chatId)) {
                    break;
                }
                after = chatId;
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException ignored) {
            log.warn("Memory dispatch deferred");
        } finally {
            if (acquired) {
                try {
                    lock.unlock();
                } catch (RuntimeException ignored) {
                    log.warn("Memory dispatch coordination expired");
                }
            }
        }
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        watchdog.shutdownNow();
        try {
            deregister();
        } catch (RuntimeException ignored) {
            log.warn("Memory worker deregistration deferred");
        } finally {
            registered = false;
            dispatchPool.shutdownNow();
            extractionPool.shutdownNow();
            maintenancePool.shutdownNow();
            long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(STOP_MILLIS);
            awaitPool(dispatchPool, deadline);
            awaitPool(extractionPool, deadline);
            awaitPool(maintenancePool, deadline);
        }
        // Do NOT shutdown/delete shared executors, cancel the dispatcher, or close injected beans.
        // The node marker expires; other nodes and all durable jobs remain alive.
    }

    private void deregister() {
        try {
            dispatcher.deregisterWorkers();
        } finally {
            try {
                extraction.deregisterWorkers();
            } finally {
                maintenance.deregisterWorkers();
            }
        }
    }

    private static void awaitPool(ThreadPoolExecutor pool, long deadline) {
        try {
            pool.awaitTermination(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static ThreadPoolExecutor pool(int size, String name) {
        return new ThreadPoolExecutor(
                size,
                size,
                0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(size),
                Thread.ofPlatform().daemon().name(name, 0).factory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private static Duration minimum(Duration left, Duration right) {
        return left.compareTo(right) <= 0 ? left : right;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /** Public static tasks have no enclosing instance; only injected, transient local services. */
    @Slf4j
    public static final class DispatchTask implements Runnable {
        @Autowired
        private transient MemoryScheduler scheduler;

        @Override
        public void run() {
            try {
                scheduler.dispatch();
            } catch (Error ignored) {
                throw new Error("Memory dispatcher failed");
            } catch (Throwable ignored) {
                log.warn("Memory dispatcher unavailable");
            }
        }
    }

    @Slf4j
    public static final class MaintenanceTask implements Runnable {
        @Autowired
        private transient MemoryMaintenance maintenance;

        @Override
        public void run() {
            try {
                maintenance.scan();
            } catch (Error ignored) {
                throw new Error("Memory maintenance failed");
            } catch (Throwable ignored) {
                log.warn("Memory maintenance unavailable");
            }
        }
    }

    @Slf4j
    public static final class ExtractionTask implements Callable<Void> {
        private final Claim claim;

        @Autowired
        private transient MemoryIdleWorker worker;

        public ExtractionTask(Claim claim) {
            this.claim = Objects.requireNonNull(claim);
        }

        @Override
        public Void call() {
            try {
                worker.execute(claim);
            } catch (Error ignored) {
                throw new Error("Memory extraction task failed");
            } catch (Throwable ignored) {
                // The worker owns release/fail; stale tasks must not acquire or borrow a new claim.
                // Never let provider/driver causes (including sneaky checked failures) reach Redis.
                log.warn("Memory extraction task rejected");
            }
            return null;
        }
    }
}
