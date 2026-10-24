package fun.freechat.service.connectors;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ConnectorTokenStream implements TokenStream, AutoCloseable {
    private final TokenStream delegate;
    private final ConnectorInvocation connectors;
    private final AtomicBoolean started = new AtomicBoolean();
    private final Object monitor = new Object();
    private volatile boolean terminal;
    private boolean delivered;
    private ChatResponse response;
    private Consumer<ChatResponse> onComplete;
    private Consumer<Throwable> onError;

    public ConnectorTokenStream(TokenStream delegate, ConnectorInvocation connectors) {
        this.delegate = delegate;
        this.connectors = connectors;
        try {
            delegate.onCompleteResponse(value -> {
                try {
                    connectors.check();
                    terminate(value);
                } catch (RuntimeException ignored) {
                    terminate(null);
                }
            });
            delegate.onError(ignored -> terminate(null));
        } catch (RuntimeException | Error failure) {
            connectors.close();
            throw unavailable();
        }
    }

    @Override
    public TokenStream onPartialResponse(Consumer<String> handler) {
        return configure(() -> delegate.onPartialResponse(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onPartialResponseWithContext(BiConsumer<PartialResponse, PartialResponseContext> handler) {
        return configure(() -> delegate.onPartialResponseWithContext(
                (value, context) -> forward(() -> handler.accept(value, context))));
    }

    @Override
    public TokenStream onPartialThinking(Consumer<PartialThinking> handler) {
        return configure(() -> delegate.onPartialThinking(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onPartialThinkingWithContext(BiConsumer<PartialThinking, PartialThinkingContext> handler) {
        return configure(() -> delegate.onPartialThinkingWithContext(
                (value, context) -> forward(() -> handler.accept(value, context))));
    }

    @Override
    public TokenStream onPartialToolCall(Consumer<PartialToolCall> handler) {
        return configure(() -> delegate.onPartialToolCall(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onPartialToolCallWithContext(BiConsumer<PartialToolCall, PartialToolCallContext> handler) {
        return configure(() -> delegate.onPartialToolCallWithContext(
                (value, context) -> forward(() -> handler.accept(value, context))));
    }

    @Override
    public TokenStream onRetrieved(Consumer<List<Content>> handler) {
        return configure(() -> delegate.onRetrieved(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onIntermediateResponse(Consumer<ChatResponse> handler) {
        return configure(() -> delegate.onIntermediateResponse(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream beforeToolExecution(Consumer<BeforeToolExecution> handler) {
        return configure(() -> delegate.beforeToolExecution(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onToolExecuted(Consumer<ToolExecution> handler) {
        return configure(() -> delegate.onToolExecuted(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onUnmappedRawEvent(Consumer<Object> handler) {
        return configure(() -> delegate.onUnmappedRawEvent(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onCompleteResponse(Consumer<ChatResponse> handler) {
        synchronized (monitor) {
            onComplete = handler;
        }
        deliver();
        return this;
    }

    @Override
    public TokenStream onError(Consumer<Throwable> handler) {
        synchronized (monitor) {
            onError = handler;
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
        if (!started.compareAndSet(false, true) || terminal) throw unavailable();
        try {
            connectors.check();
            delegate.start();
        } catch (RuntimeException | Error ignored) {
            terminate(null);
            throw unavailable();
        }
    }

    private TokenStream configure(Runnable configuration) {
        try {
            configuration.run();
            return this;
        } catch (RuntimeException | Error ignored) {
            terminate(null);
            throw unavailable();
        }
    }

    private void forward(Runnable callback) {
        try {
            if (terminal) throw unavailable();
            callback.run();
        } catch (RuntimeException | Error ignored) {
            terminate(null);
            throw unavailable();
        }
    }

    private void terminate(ChatResponse result) {
        synchronized (monitor) {
            if (terminal) return;
            response = result;
            terminal = true;
        }
        connectors.close();
        deliver();
    }

    private void deliver() {
        Runnable callback;
        synchronized (monitor) {
            if (!terminal || delivered) return;
            if (response == null) {
                if (onError == null) return;
                var handler = onError;
                callback = () -> handler.accept(unavailable());
            } else {
                if (onComplete == null) return;
                var handler = onComplete;
                var result = response;
                callback = () -> handler.accept(result);
            }
            delivered = true;
            response = null;
        }
        callback.run();
    }

    @Override
    public void close() {
        terminate(null);
    }

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Chat connector stream unavailable");
    }
}
