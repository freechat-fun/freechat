package fun.freechat.service.chat;

import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract sealed class ChatTask<T> permits ChatTask.Sync, ChatTask.Stream {

    final CompletableFuture<T> future = new CompletableFuture<>();
    final CompletableFuture<Void> settlement = new CompletableFuture<>();

    public CompletableFuture<T> future() {
        return future;
    }

    public CompletableFuture<Void> settled() {
        // Observers must not be able to complete/cancel the queue's own settlement signal.
        return settlement.copy();
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

    public static final class Stream extends ChatTask<TokenStream> implements ChatStreamHandle {
        private static final long STREAM_TIMEOUT_MS = 600_000L;

        private final Supplier<TokenStream> streamBuilder;
        private final CountDownLatch cleanupRequested = new CountDownLatch(1);
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private volatile ChatTaskQueue owner;

        public Stream(Supplier<TokenStream> streamBuilder) {
            this.streamBuilder = streamBuilder;
        }

        @Override
        public CompletableFuture<TokenStream> ready() {
            return future.copy();
        }

        void queuedIn(ChatTaskQueue queue) {
            owner = queue;
        }

        @Override
        public void cancel() {
            ChatTaskQueue queue = owner;
            if (queue == null) {
                requestCancellation();
            } else {
                queue.cancel(this);
            }
        }

        boolean isCancelled() {
            return cancelled.get();
        }

        // Request only: never interrupt builder/abort IO or run active cleanup on the caller.
        void requestCancellation() {
            cancelled.set(true);
            cleanupRequested.countDown();
        }

        @Override
        void execute() {
            TokenStream raw = null;
            QueueAwareTokenStream wrapped = null;
            try {
                if (isCancelled()) {
                    future.completeExceptionally(new ChatQueueRejectedException());
                    return;
                }
                raw = streamBuilder.get();
                if (raw == null) {
                    future.complete(null);
                    return;
                }
                // Ready callbacks can run before cleanup, so cancellation must already bar start().
                wrapped = new QueueAwareTokenStream(raw, cleanupRequested, cancelled::get);
                future.complete(wrapped);
                if (!cleanupRequested.await(STREAM_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    requestCancellation();
                }
            } catch (InterruptedException e) {
                requestCancellation();
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
                    if (wrapped != null) {
                        wrapped.awaitTermination();
                    }
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
