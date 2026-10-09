package fun.freechat.channels.telegram;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.OkHttpClient;

public final class TelegramHttpClient {
    private TelegramHttpClient() {}

    public static OkHttpClient create(Duration timeout) {
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("Telegram HTTP timeout must be positive");
        }
        return new OkHttpClient.Builder()
                .callTimeout(timeout)
                .readTimeout(timeout)
                .connectTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .retryOnConnectionFailure(false)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .tag(Attempt.class, new Attempt())
                        .build()))
                .addNetworkInterceptor(chain -> {
                    Attempt attempt = chain.request().tag(Attempt.class);
                    // OkHttp's 503 Retry-After: 0 follow-up ignores retryOnConnectionFailure.
                    if (attempt == null || !attempt.started.compareAndSet(false, true)) {
                        throw new IOException("Telegram HTTP follow-up blocked");
                    }
                    return chain.proceed(chain.request());
                })
                .build();
    }

    /** Closes only an exclusively owned client, after its producers have stopped. */
    public static void closeOwned(OkHttpClient http) {
        boolean interrupted = Thread.interrupted();
        CountDownLatch idle = new CountDownLatch(1);
        http.dispatcher().setIdleCallback(idle::countDown);
        http.dispatcher().cancelAll();
        var executor = http.dispatcher().executorService();
        executor.shutdown();
        try {
            while (http.dispatcher().runningCallsCount() != 0) {
                try {
                    idle.await();
                } catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
            while (!executor.isTerminated()) {
                try {
                    executor.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
        } finally {
            http.connectionPool().evictAll();
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class Attempt {
        final AtomicBoolean started = new AtomicBoolean();
    }
}
