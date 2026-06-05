package fun.freechat.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatTaskQueue {

    private static final long POLL_TIMEOUT_SECONDS = 5L;

    private final String chatId;
    private final LinkedBlockingQueue<ChatTask<?>> taskQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean draining = new AtomicBoolean(false);
    private final AtomicReference<CountDownLatch> activeLatch = new AtomicReference<>();
    private final AtomicLong idleSince = new AtomicLong(System.currentTimeMillis());

    public ChatTaskQueue(String chatId) {
        this.chatId = chatId;
    }

    public <T> ChatTask<T> submit(ChatTask<T> task) {
        if (draining.get()) {
            task.future.completeExceptionally(new ChatQueueRejectedException());
            return task;
        }
        taskQueue.add(task);
        return task;
    }

    public void startWorker(ExecutorService executor) {
        executor.submit(this::workerLoop);
    }

    public void drain(long timeoutMs) {
        draining.set(true);

        CountDownLatch latch = activeLatch.get();
        if (latch != null) {
            try {
                latch.await(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<ChatTask<?>> remaining = new ArrayList<>();
        taskQueue.drainTo(remaining);
        for (ChatTask<?> task : remaining) {
            task.future.completeExceptionally(new ChatQueueRejectedException());
        }
    }

    public boolean isIdle() {
        return activeLatch.get() == null && taskQueue.isEmpty();
    }

    public long getIdleSinceMs() {
        return idleSince.get();
    }

    private void workerLoop() {
        Thread.currentThread().setName("chat-queue-" + chatId);
        while (!Thread.currentThread().isInterrupted()) {
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
            if (draining.get()) {
                task.future.completeExceptionally(new ChatQueueRejectedException());
                continue;
            }
            CountDownLatch latch = new CountDownLatch(1);
            activeLatch.set(latch);
            try {
                task.execute();
            } catch (Throwable ex) {
                if (!task.future.isDone()) {
                    task.future.completeExceptionally(ex);
                }
            } finally {
                activeLatch.set(null);
                latch.countDown();
                idleSince.set(System.currentTimeMillis());
            }
        }

        List<ChatTask<?>> remaining = new ArrayList<>();
        taskQueue.drainTo(remaining);
        for (ChatTask<?> task : remaining) {
            task.future.completeExceptionally(new ChatQueueRejectedException());
        }
    }
}
