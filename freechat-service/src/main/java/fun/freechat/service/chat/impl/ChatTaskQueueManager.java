package fun.freechat.service.chat.impl;

import fun.freechat.service.chat.ChatTaskQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Per-chatId FIFO queue registry. Serializes all chat operations (send, streamSend,
 * sendAssistant, streamSendAssistant) for a given chatId so that concurrent requests
 * queue up instead of being rejected.
 *
 * <p>Implements {@link SmartLifecycle} (phase MAX_VALUE-100) instead of annotation @PreDestroy
 * to drain active queues <em>before</em> the HTTP server stops — ensuring in-flight
 * streaming responses can finish rather than being cut mid-stream.
 */
@Component
@Slf4j
public class ChatTaskQueueManager implements SmartLifecycle {

    private static final long SHUTDOWN_TIMEOUT_MS = 10_000L;
    // Queues idle longer than this are eligible for cleanup
    private static final long IDLE_TTL_MS = 3_600_000L;

    private final ConcurrentHashMap<String, ChatTaskQueue> queues = new ConcurrentHashMap<>();
    // Virtual threads: each worker blocks on poll() and stream-completion latches,
    // which is exactly the pattern virtual threads optimize for.
    private final ExecutorService workerExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile boolean running = false;

    public ChatTaskQueue getOrCreateQueue(String chatId) {
        return queues.computeIfAbsent(chatId, id -> {
            ChatTaskQueue queue = new ChatTaskQueue(id);
            queue.startWorker(workerExecutor);
            return queue;
        });
    }

    /**
     * Drains the queue for a chatId (waits for the active task, rejects remaining)
     * then removes it from the registry. Called during session invalidation/reset
     * to ensure the in-flight task completes before the session is evicted.
     */
    public void drainAndRemove(String chatId, long timeoutMs) {
        ChatTaskQueue queue = queues.remove(chatId);
        if (queue != null) {
            queue.drain(timeoutMs);
        }
    }

    // Prevents memory leaks from abandoned chats whose sessions were evicted from
    // Caffeine but whose queues linger. Queue lifetime >= session lifetime by design.
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
        // Stop before the web server (Integer.MAX_VALUE) so queues drain
        // while HTTP connections are still alive.
        return Integer.MAX_VALUE - 100;
    }
}
