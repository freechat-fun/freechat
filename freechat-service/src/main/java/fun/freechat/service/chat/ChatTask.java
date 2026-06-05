package fun.freechat.service.chat;

import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract sealed class ChatTask<T> permits ChatTask.Sync, ChatTask.Stream {

    final CompletableFuture<T> future = new CompletableFuture<>();

    public CompletableFuture<T> future() {
        return future;
    }

    abstract void execute();

    public static final class Sync<T> extends ChatTask<T> {
        private final Supplier<T> work;

        public Sync(Supplier<T> work) {
            this.work = work;
        }

        @Override
        void execute() {
            try {
                future.complete(work.get());
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }
    }

    public static final class Stream extends ChatTask<TokenStream> {
        private static final long STREAM_TIMEOUT_MS = 600_000L;

        private final Supplier<TokenStream> streamBuilder;

        public Stream(Supplier<TokenStream> streamBuilder) {
            this.streamBuilder = streamBuilder;
        }

        @Override
        void execute() {
            CountDownLatch streamDone = new CountDownLatch(1);
            try {
                TokenStream raw = streamBuilder.get();
                if (raw == null) {
                    future.complete(null);
                    return;
                }
                QueueAwareTokenStream wrapped = new QueueAwareTokenStream(raw, streamDone);
                future.complete(wrapped);
                streamDone.await(STREAM_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!future.isDone()) {
                    future.completeExceptionally(e);
                }
            } catch (Throwable t) {
                if (!future.isDone()) {
                    future.completeExceptionally(t);
                }
            }
        }
    }
}
