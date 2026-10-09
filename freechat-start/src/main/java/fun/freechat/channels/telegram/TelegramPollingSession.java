package fun.freechat.channels.telegram;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.DefaultGetUpdatesGenerator;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
public final class TelegramPollingSession implements AutoCloseable {
    @FunctionalInterface
    public interface Factory {
        AutoCloseable start(String token, Consumer<List<Update>> updates) throws Exception;
    }

    private final OkHttpClient http;
    private final TelegramClient client;
    private final Consumer<List<Update>> updates;
    private final Thread worker;
    private volatile boolean stopping;

    private TelegramPollingSession(OkHttpClient http, TelegramUrl url, String token, Consumer<List<Update>> updates) {
        this.http = http;
        this.client = new OkHttpTelegramClient(http, token, url);
        this.updates = Objects.requireNonNull(updates);
        worker = Thread.ofVirtual().name("telegram-polling-worker").unstarted(this::poll);
    }

    public static TelegramPollingSession start(TelegramUrl url, String token, Consumer<List<Update>> updates) {
        return start(url, token, updates, TelegramHttpClient.create(Duration.ofSeconds(60)));
    }

    /** Takes exclusive ownership of the supplied finite-timeout HTTP client, including on failure. */
    public static TelegramPollingSession start(
            TelegramUrl url, String token, Consumer<List<Update>> updates, OkHttpClient ownedHttp) {
        TelegramPollingSession session = null;
        boolean started = false;
        try {
            if (ownedHttp.callTimeoutMillis() <= 0 || ownedHttp.readTimeoutMillis() <= 0) {
                throw new IllegalArgumentException("Telegram polling requires finite HTTP timeouts");
            }
            session = new TelegramPollingSession(ownedHttp, url, token, updates);
            if (!Boolean.TRUE.equals(session.client.execute(new DeleteWebhook()))) {
                throw new IllegalStateException("Telegram webhook deletion failed");
            }
            session.worker.start();
            started = true;
            return session;
        } catch (Exception ignored) {
            // Provider exceptions include tokens and response bodies.
            throw new IllegalStateException("Telegram polling registration failed");
        } finally {
            if (!started) {
                if (session != null) {
                    session.close();
                } else {
                    TelegramHttpClient.closeOwned(ownedHttp);
                }
            }
        }
    }

    private void poll() {
        var generator = new DefaultGetUpdatesGenerator();
        int lastUpdateId = 0;
        while (!stopping) {
            try {
                List<Update> received = client.execute(generator.apply(Math.min(lastUpdateId, Integer.MAX_VALUE - 1)));
                if (stopping) {
                    return;
                }
                int previous = lastUpdateId;
                List<Update> fresh = received.stream()
                        .filter(update ->
                                update != null && update.getUpdateId() != null && update.getUpdateId() > previous)
                        .toList();
                for (Update update : fresh) {
                    lastUpdateId = Math.max(lastUpdateId, update.getUpdateId());
                }
                if (!fresh.isEmpty() && !stopping) {
                    updates.accept(fresh);
                }
            } catch (Exception ignored) {
                if (!stopping) {
                    log.warn("Telegram polling request failed; retry deferred");
                    LockSupport.parkNanos(this, Duration.ofSeconds(1).toNanos());
                }
            }
        }
    }

    @Override
    public synchronized void close() {
        stopping = true;
        http.dispatcher().cancelAll();
        worker.interrupt();
        boolean interrupted = Thread.interrupted();
        while (worker.isAlive()) {
            try {
                worker.join();
            } catch (InterruptedException ignored) {
                interrupted = true;
            }
        }
        // Future cancellation is not proof that the SDK's asynchronous HTTP callback has returned.
        TelegramHttpClient.closeOwned(http);
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
