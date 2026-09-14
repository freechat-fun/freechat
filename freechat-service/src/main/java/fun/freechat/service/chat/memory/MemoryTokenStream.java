package fun.freechat.service.chat.memory;

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

/** Exactly-once user termination after SQL fencing, including failure before start/registration. */
public final class MemoryTokenStream implements TokenStream, AutoCloseable {
    private final TokenStream delegate;
    private final MemoryInvocation invocation;
    private final MemoryTurnLifetime lifetime;
    private final AtomicBoolean started = new AtomicBoolean();
    private final Object monitor = new Object();
    private volatile boolean terminal;
    private boolean failed;
    private boolean delivered;
    private boolean ignoreErrors;
    private ChatResponse completedResponse;
    private Consumer<ChatResponse> completeHandler;
    private Consumer<Throwable> errorHandler;

    public MemoryTokenStream(TokenStream delegate, MemoryInvocation invocation, MemoryTurnLifetime lifetime) {
        this.delegate = delegate;
        this.invocation = invocation;
        this.lifetime = lifetime;
        try {
            // Pinned AiServiceTokenStream permits exactly one error registration. Keep these bridges
            // installed even when a later wrapper changes its handlers or elects to ignore errors.
            delegate.onCompleteResponse(this::complete);
            delegate.onError(ignored -> fail());
            lifetime.onFailure(ignored -> failed());
        } catch (Throwable ignored) {
            lifetime.close();
            throw MemoryTurnLifetime.unavailable();
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
    public TokenStream onUnmappedRawEvent(Consumer<Object> handler) {
        return configure(() -> delegate.onUnmappedRawEvent(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onToolExecuted(Consumer<ToolExecution> handler) {
        return configure(() -> delegate.onToolExecuted(value -> forward(() -> handler.accept(value))));
    }

    @Override
    public TokenStream onCompleteResponse(Consumer<ChatResponse> handler) {
        synchronized (monitor) {
            completeHandler = handler;
        }
        deliver();
        return this;
    }

    @Override
    public TokenStream onError(Consumer<Throwable> handler) {
        synchronized (monitor) {
            errorHandler = handler;
            ignoreErrors = false;
        }
        deliver();
        return this;
    }

    @Override
    public TokenStream ignoreErrors() {
        synchronized (monitor) {
            ignoreErrors = true;
            errorHandler = null;
        }
        deliver();
        return this;
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true) || terminal) {
            throw MemoryTurnLifetime.unavailable();
        }
        try {
            lifetime.check();
            if (terminal) {
                throw MemoryTurnLifetime.unavailable();
            }
            delegate.start();
        } catch (Throwable ignored) {
            fail();
            throw MemoryTurnLifetime.unavailable();
        }
    }

    private TokenStream configure(Runnable configuration) {
        try {
            configuration.run();
            return this;
        } catch (Throwable ignored) {
            fail();
            throw MemoryTurnLifetime.unavailable();
        }
    }

    private void forward(Runnable callback) {
        try {
            if (terminal) {
                throw MemoryTurnLifetime.unavailable();
            }
            lifetime.check();
            callback.run();
        } catch (Throwable ignored) {
            fail();
            // Halt framework tool/retrieval processing, without passing the user's exception to its logger.
            throw MemoryTurnLifetime.unavailable();
        }
    }

    private void complete(ChatResponse response) {
        if (terminal) {
            return;
        }
        if (!invocation.completed()) {
            fail();
            return;
        }
        lifetime.finish();
        synchronized (monitor) {
            if (terminal) {
                return;
            }
            completedResponse = response;
            terminal = true;
        }
        // LangChain4j's final response contains cumulative usage; never account it again here.
        deliver();
    }

    private void fail() {
        lifetime.close();
        failed();
    }

    private void failed() {
        synchronized (monitor) {
            if (terminal) {
                return;
            }
            failed = true;
            terminal = true;
        }
        deliver();
    }

    private void deliver() {
        Runnable callback;
        synchronized (monitor) {
            if (!terminal || delivered) {
                return;
            }
            if (failed) {
                if (ignoreErrors) {
                    delivered = true;
                    return;
                }
                if (errorHandler == null) {
                    return;
                }
                Consumer<Throwable> handler = errorHandler;
                callback = () -> handler.accept(MemoryTurnLifetime.unavailable());
            } else {
                if (completeHandler == null) {
                    return;
                }
                Consumer<ChatResponse> handler = completeHandler;
                ChatResponse response = completedResponse;
                callback = () -> handler.accept(response);
            }
            delivered = true;
            completedResponse = null;
        }
        // Queue wrappers run their own finally blocks; a user's payload exception never escapes.
        MemoryTurnLifetime.safely(callback);
    }

    public Long finalMessageId() {
        return invocation.finalMessageId();
    }

    @Override
    public void close() {
        lifetime.close();
        if (!invocation.completed()) {
            failed();
        }
    }
}
