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
        private final CountDownLatch streamDone = new CountDownLatch(1);

        public Stream(Supplier<TokenStream> streamBuilder) {
            this.streamBuilder = streamBuilder;
        }

        // Request cleanup on the owning worker, including while the builder is still running.
        // Do not interrupt an in-flight memory abort or run cleanup on the draining thread.
        void cancel() {
            streamDone.countDown();
        }

        @Override
        void execute() {
            TokenStream raw = null;
            QueueAwareTokenStream wrapped = null;
            try {
                raw = streamBuilder.get();
                if (raw == null) {
                    future.complete(null);
                    return;
                }
                wrapped = new QueueAwareTokenStream(raw, streamDone);
                future.complete(wrapped);
                streamDone.await(STREAM_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                future.completeExceptionally(new IllegalStateException("Chat stream interrupted"));
            } catch (Throwable t) {
                future.completeExceptionally(new IllegalStateException("Chat stream failed"));
            } finally {
                boolean interrupted = Thread.interrupted();
                try {
                    if (wrapped != null) {
                        wrapped.close();
                    } else if (raw instanceof AutoCloseable closeable) {
                        closeable.close();
                    }
                } catch (Throwable ignored) {
                    future.completeExceptionally(new IllegalStateException("Chat stream cancellation failed"));
                } finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
