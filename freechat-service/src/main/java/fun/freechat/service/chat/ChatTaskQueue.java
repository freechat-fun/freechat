package fun.freechat.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatTaskQueue {

    private static final long POLL_TIMEOUT_SECONDS = 5L;

    private final String chatId;
    private final LinkedBlockingQueue<ChatTask<?>> taskQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean draining = new AtomicBoolean(false);
    private final Object lifecycleMonitor = new Object();
    private volatile ActiveTask activeTask;
    private final AtomicLong idleSince = new AtomicLong(System.currentTimeMillis());
    private final Lock coordination;

    private record ActiveTask(ChatTask<?> task, Thread worker, CountDownLatch done) {}

    public ChatTaskQueue(String chatId, Lock coordination) {
        this.chatId = chatId;
        this.coordination = coordination;
    }

    public <T> ChatTask<T> submit(ChatTask<T> task) {
        if (task instanceof ChatTask.Stream stream) {
            stream.queuedIn(this);
        }
        if (draining.get() || isCancelled(task)) {
            reject(task);
            return task;
        }
        taskQueue.add(task);
        if ((draining.get() || isCancelled(task)) && taskQueue.remove(task)) {
            reject(task);
        }
        return task;
    }

    void cancel(ChatTask.Stream task) {
        task.requestCancellation();
        if (taskQueue.remove(task)) {
            reject(task);
        }
        // Dequeued/active tasks belong to the worker, even while it is waiting for coordination.
    }

    private static boolean isCancelled(ChatTask<?> task) {
        return task instanceof ChatTask.Stream stream && stream.isCancelled();
    }

    private static void reject(ChatTask<?> task) {
        task.future.completeExceptionally(new ChatQueueRejectedException());
        task.settlement.complete(null);
    }

    public void startWorker(ExecutorService executor) {
        executor.submit(this::workerLoop);
    }

    public void drain(long timeoutMs) {
        ActiveTask active;
        synchronized (lifecycleMonitor) {
            draining.set(true);
            active = activeTask;
        }

        if (active != null) {
            boolean finished = false;
            try {
                // Session reset can drain from inside the task itself. Never await our own completion.
                if (active.worker() != Thread.currentThread()) {
                    finished = active.done().await(Math.max(0, timeoutMs), TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (!finished && active.task() instanceof ChatTask.Stream stream) {
                    // Bounded request only: the worker retains coordination until close/abort finishes.
                    stream.cancel();
                }
            }
        }

        List<ChatTask<?>> remaining = new ArrayList<>();
        taskQueue.drainTo(remaining);
        for (ChatTask<?> task : remaining) {
            reject(task);
        }
    }

    public boolean isIdle() {
        return activeTask == null && taskQueue.isEmpty();
    }

    public long getIdleSinceMs() {
        return idleSince.get();
    }

    private void workerLoop() {
        Thread.currentThread().setName("chat-queue-" + chatId);
        while (!Thread.currentThread().isInterrupted() && !draining.get()) {
            ChatTask<?> task;
            try {
                task = taskQueue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (task == null) {
                idleSince.set(System.currentTimeMillis());
                continue;
            }
            ActiveTask active;
            synchronized (lifecycleMonitor) {
                active = draining.get() || isCancelled(task)
                        ? null
                        : new ActiveTask(task, Thread.currentThread(), new CountDownLatch(1));
                if (active != null) {
                    activeTask = active;
                }
            }
            if (active == null) {
                reject(task);
                continue;
            }
            Throwable coordinationFailure = null;
            try {
                coordination.lockInterruptibly();
                try {
                    if (draining.get() || isCancelled(task)) {
                        task.future.completeExceptionally(new ChatQueueRejectedException());
                    } else {
                        task.execute();
                    }
                } finally {
                    // Stream cleanup restores interruption; Redisson's synchronous unlock needs it cleared too.
                    boolean interrupted = Thread.interrupted();
                    try {
                        coordination.unlock();
                    } finally {
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                task.future.completeExceptionally(new ChatQueueRejectedException());
            } catch (Throwable ignored) {
                coordinationFailure = new IllegalStateException("Chat coordination failed");
                task.future.completeExceptionally(coordinationFailure);
            } finally {
                synchronized (lifecycleMonitor) {
                    activeTask = null;
                }
                idleSince.set(System.currentTimeMillis());
                active.done().countDown();
                // Neither readiness nor a terminal model callback proves cleanup/unlock has finished.
                if (coordinationFailure == null) {
                    task.settlement.complete(null);
                } else {
                    task.settlement.completeExceptionally(coordinationFailure);
                }
            }
        }

        draining.set(true);
        List<ChatTask<?>> remaining = new ArrayList<>();
        taskQueue.drainTo(remaining);
        for (ChatTask<?> task : remaining) {
            reject(task);
        }
    }
}
