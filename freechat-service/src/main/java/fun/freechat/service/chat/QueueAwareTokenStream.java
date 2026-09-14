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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class QueueAwareTokenStream implements TokenStream, AutoCloseable {
    private final TokenStream delegate;
    private final CountDownLatch completionLatch;
    private final AtomicBoolean started = new AtomicBoolean();
    private final Object monitor = new Object();
    private Consumer<ChatResponse> userOnComplete;
    private Consumer<Throwable> userOnError;
    private ChatResponse response;
    private Throwable error;
    private boolean terminal;
    private boolean delivered;

    QueueAwareTokenStream(TokenStream delegate, CountDownLatch completionLatch) {
        this.delegate = delegate;
        this.completionLatch = completionLatch;
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
            if (terminal || !started.compareAndSet(false, true)) {
                throw new IllegalStateException("Chat stream is no longer available");
            }
        }
        try {
            delegate.start();
        } catch (RuntimeException | Error failure) {
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
            completionLatch.countDown();
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
        }
        callback.run();
    }

    public Long finalMessageId() {
        return delegate instanceof fun.freechat.service.chat.memory.MemoryTokenStream memory
                ? memory.finalMessageId()
                : null;
    }

    @Override
    public void close() {
        try {
            if (delegate instanceof AutoCloseable closeable) {
                closeable.close();
            }
        } catch (Exception ignored) {
            throw new IllegalStateException("Chat stream cancellation failed");
        } finally {
            terminate(null, new IllegalStateException("Chat stream was cancelled"));
        }
    }
}
