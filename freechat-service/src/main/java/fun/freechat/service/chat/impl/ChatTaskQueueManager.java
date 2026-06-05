package fun.freechat.service.chat.impl;

import fun.freechat.service.chat.ChatTaskQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatTaskQueueManager implements SmartLifecycle {

    private static final long SHUTDOWN_TIMEOUT_MS = 10_000L;
    private static final long IDLE_TTL_MS = 3_600_000L;

    private final ConcurrentHashMap<String, ChatTaskQueue> queues = new ConcurrentHashMap<>();
    private final ExecutorService workerExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile boolean running = false;

    public ChatTaskQueue getOrCreateQueue(String chatId) {
        return queues.computeIfAbsent(chatId, id -> {
            ChatTaskQueue queue = new ChatTaskQueue(id);
            queue.startWorker(workerExecutor);
            return queue;
        });
    }

    public void drainAndRemove(String chatId, long timeoutMs) {
        ChatTaskQueue queue = queues.remove(chatId);
        if (queue != null) {
            queue.drain(timeoutMs);
        }
    }

    @Scheduled(fixedDelay = 300_000)
    public void cleanupIdleQueues() {
        long now = System.currentTimeMillis();
        queues.entrySet().removeIf(entry -> {
            ChatTaskQueue queue = entry.getValue();
            if (queue.isIdle() && (now - queue.getIdleSinceMs()) > IDLE_TTL_MS) {
                queue.drain(0);
                log.debug("Removed idle chat queue for chatId={}", entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        long deadline = System.currentTimeMillis() + SHUTDOWN_TIMEOUT_MS;
        List<CompletableFuture<Void>> drainFutures = new ArrayList<>();
        for (Map.Entry<String, ChatTaskQueue> entry : queues.entrySet()) {
            long remaining = Math.max(0, deadline - System.currentTimeMillis());
            drainFutures.add(CompletableFuture.runAsync(() -> entry.getValue().drain(remaining)));
        }
        try {
            long remaining = Math.max(0, deadline - System.currentTimeMillis());
            CompletableFuture.allOf(drainFutures.toArray(new CompletableFuture[0]))
                    .get(remaining, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.warn("Timed out waiting for chat queues to drain during shutdown");
        } catch (Exception e) {
            log.warn("Error during chat queue shutdown", e);
        }
        workerExecutor.shutdownNow();
        queues.clear();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 100;
    }
}
