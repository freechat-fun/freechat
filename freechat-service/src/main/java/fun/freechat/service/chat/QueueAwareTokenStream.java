package fun.freechat.service.chat;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.PartialThinkingContext;
import dev.langchain4j.model.chat.response.PartialToolCall;
import dev.langchain4j.model.chat.response.PartialToolCallContext;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.BeforeToolExecution;
import dev.langchain4j.service.tool.ToolExecution;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class QueueAwareTokenStream implements TokenStream, AutoCloseable {
    private final TokenStream delegate;
    private final CountDownLatch cleanupRequested;
    private final BooleanSupplier cancelled;
    private final CompletableFuture<Void> delegateDone = new CompletableFuture<>();
    private final CompletableFuture<Void> startDone = new CompletableFuture<>();
    private final CompletableFuture<Void> closeDone = new CompletableFuture<>();
    private final Object monitor = new Object();
    private boolean started;
    private boolean closing;
    private Thread startThread;
    private Thread closeThread;
    private Consumer<ChatResponse> userOnComplete;
    private Consumer<Throwable> userOnError;
    private ChatResponse response;
    private Throwable error;
    private boolean terminal;
    private boolean delivered;
    private boolean closed;
    private Thread callbackThread;
    private CompletableFuture<Void> callbackDone = CompletableFuture.completedFuture(null);

    QueueAwareTokenStream(TokenStream delegate, CountDownLatch cleanupRequested) {
        this(delegate, cleanupRequested, () -> false);
    }

    QueueAwareTokenStream(TokenStream delegate, CountDownLatch cleanupRequested, BooleanSupplier cancelled) {
        this.delegate = delegate;
        this.cleanupRequested = cleanupRequested;
        this.cancelled = cancelled;
        // An admitted turn can expire before the caller registers callbacks or starts streaming.
        delegate.onCompleteResponse(value -> terminate(value, null));
        delegate.onError(failure -> terminate(null, failure));
    }

    @Override
    public TokenStream onPartialResponse(Consumer<String> handler) {
        delegate.onPartialResponse(handler);
        return this;
    }

    @Override
    public TokenStream onPartialResponseWithContext(BiConsumer<PartialResponse, PartialResponseContext> handler) {
        delegate.onPartialResponseWithContext(handler);
        return this;
    }

    @Override
    public TokenStream onPartialThinking(Consumer<PartialThinking> handler) {
        delegate.onPartialThinking(handler);
        return this;
    }

    @Override
    public TokenStream onPartialThinkingWithContext(BiConsumer<PartialThinking, PartialThinkingContext> handler) {
        delegate.onPartialThinkingWithContext(handler);
        return this;
    }

    @Override
    public TokenStream onPartialToolCall(Consumer<PartialToolCall> handler) {
        delegate.onPartialToolCall(handler);
        return this;
    }

    @Override
    public TokenStream onPartialToolCallWithContext(BiConsumer<PartialToolCall, PartialToolCallContext> handler) {
        delegate.onPartialToolCallWithContext(handler);
        return this;
    }

    @Override
    public TokenStream onUnmappedRawEvent(Consumer<Object> handler) {
        delegate.onUnmappedRawEvent(handler);
        return this;
    }

    @Override
    public TokenStream onRetrieved(Consumer<List<Content>> handler) {
        delegate.onRetrieved(handler);
        return this;
    }

    @Override
    public TokenStream onIntermediateResponse(Consumer<ChatResponse> handler) {
        delegate.onIntermediateResponse(handler);
        return this;
    }

    @Override
    public TokenStream beforeToolExecution(Consumer<BeforeToolExecution> handler) {
        delegate.beforeToolExecution(handler);
        return this;
    }

    @Override
    public TokenStream onToolExecuted(Consumer<ToolExecution> handler) {
        delegate.onToolExecuted(handler);
        return this;
    }

    @Override
    public TokenStream onCompleteResponse(Consumer<ChatResponse> handler) {
        synchronized (monitor) {
            userOnComplete = handler;
        }
        deliver();
        return this;
    }

    @Override
    public TokenStream onError(Consumer<Throwable> handler) {
        synchronized (monitor) {
            userOnError = handler;
        }
        deliver();
        return this;
    }

    @Override
    public TokenStream ignoreErrors() {
        return onError(ignored -> {});
    }

    @Override
    public void start() {
        synchronized (monitor) {
            if (terminal || closed || cancelled.getAsBoolean() || started) {
                throw new IllegalStateException("Chat stream is no longer available");
            }
            started = true;
            startThread = Thread.currentThread();
        }
        try {
            try {
                delegate.start();
            } finally {
                synchronized (monitor) {
                    startThread = null;
                }
                startDone.complete(null);
            }
        } catch (RuntimeException | Error failure) {
            // A throwing start can already have dispatched provider work.
            close();
            throw failure;
        }
    }

    private void terminate(ChatResponse value, Throwable failure) {
        synchronized (monitor) {
            if (terminal) {
                return;
            }
            terminal = true;
            response = value;
            error = failure;
        }
        try {
            deliver();
        } finally {
            delegateDone.complete(null);
            cleanupRequested.countDown();
        }
    }

    private void deliver() {
        Runnable callback;
        synchronized (monitor) {
            if (!terminal || delivered) {
                return;
            }
            if (error == null) {
                if (userOnComplete == null) {
                    return;
                }
                ChatResponse value = response;
                Consumer<ChatResponse> handler = userOnComplete;
                callback = () -> handler.accept(value);
            } else {
                if (userOnError == null) {
                    return;
                }
                Throwable failure = error;
                Consumer<Throwable> handler = userOnError;
                callback = () -> handler.accept(failure);
            }
            delivered = true;
            response = null;
            error = null;
            callbackThread = Thread.currentThread();
            callbackDone = new CompletableFuture<>();
        }
        try {
            callback.run();
        } finally {
            synchronized (monitor) {
                callbackThread = null;
            }
            callbackDone.complete(null);
        }
    }

    void awaitTermination() {
        // Cancellation wakes the owner, but only delegate termination permits coordination release.
        delegateDone.join();
        awaitCallback();
    }

    private void awaitCallback() {
        CompletableFuture<Void> done;
        synchronized (monitor) {
            if (callbackThread == Thread.currentThread()) {
                return;
            }
            done = callbackDone;
        }
        done.join();
    }

    public Long finalMessageId() {
        return delegate instanceof fun.freechat.service.chat.memory.MemoryTokenStream memory
                ? memory.finalMessageId()
                : null;
    }

    @Override
    public void close() {
        boolean waitForStart;
        synchronized (monitor) {
            closed = true;
            cleanupRequested.countDown();
            if (callbackThread == Thread.currentThread()
                    || startThread == Thread.currentThread()
                    || closeThread == Thread.currentThread()) {
                return;
            }
            waitForStart = started;
        }
        boolean interrupted = Thread.interrupted();
        try {
            if (waitForStart) {
                startDone.join();
            }
            interrupted |= Thread.interrupted();
            closeDelegate();
        } finally {
            awaitCallback();
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void closeDelegate() {
        boolean ownsClose;
        synchronized (monitor) {
            ownsClose = !closing;
            if (ownsClose) {
                closing = true;
                closeThread = Thread.currentThread();
            }
        }
        if (!ownsClose) {
            closeDone.join();
            return;
        }
        try {
            boolean stopped;
            synchronized (monitor) {
                stopped = !started;
            }
            if (delegate instanceof AutoCloseable closeable) {
                closeable.close();
                stopped = true;
            }
            if (stopped) {
                terminate(null, new IllegalStateException("Chat stream was cancelled"));
            }
        } catch (Exception ignored) {
            throw new IllegalStateException("Chat stream cancellation failed");
        } finally {
            synchronized (monitor) {
                closeThread = null;
            }
            closeDone.complete(null);
        }
    }
}
