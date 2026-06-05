package fun.freechat.service.chat;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.BeforeToolExecution;
import dev.langchain4j.service.tool.ToolExecution;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Decorator around a {@link TokenStream} that counts down a {@link CountDownLatch} when the stream
 * terminates (via {@code onCompleteResponse} or {@code onError}). This allows the per-session queue
 * worker to block until the stream finishes, keeping the queue slot occupied during streaming.
 *
 * <p>All builder methods except {@code onCompleteResponse}, {@code onError}, and {@code start}
 * delegate directly to the wrapped stream.
 */
final class QueueAwareTokenStream implements TokenStream {

    private final TokenStream delegate;
    private final CountDownLatch completionLatch;
    private Consumer<ChatResponse> userOnComplete;
    private Consumer<Throwable> userOnError;

    QueueAwareTokenStream(TokenStream delegate, CountDownLatch completionLatch) {
        this.delegate = delegate;
        this.completionLatch = completionLatch;
    }

    @Override
    public TokenStream onPartialResponse(Consumer<String> partialResponseHandler) {
        delegate.onPartialResponse(partialResponseHandler);
        return this;
    }

    @Override
    public TokenStream onPartialThinking(Consumer<PartialThinking> partialThinkingHandler) {
        delegate.onPartialThinking(partialThinkingHandler);
        return this;
    }

    @Override
    public TokenStream onRetrieved(Consumer<List<Content>> contentHandler) {
        delegate.onRetrieved(contentHandler);
        return this;
    }

    @Override
    public TokenStream onIntermediateResponse(Consumer<ChatResponse> intermediateResponseHandler) {
        delegate.onIntermediateResponse(intermediateResponseHandler);
        return this;
    }

    @Override
    public TokenStream beforeToolExecution(Consumer<BeforeToolExecution> beforeToolExecutionHandler) {
        delegate.beforeToolExecution(beforeToolExecutionHandler);
        return this;
    }

    @Override
    public TokenStream onToolExecuted(Consumer<ToolExecution> toolExecuteHandler) {
        delegate.onToolExecuted(toolExecuteHandler);
        return this;
    }

    @Override
    public TokenStream onCompleteResponse(Consumer<ChatResponse> completeResponseHandler) {
        this.userOnComplete = completeResponseHandler;
        return this;
    }

    @Override
    public TokenStream onError(Consumer<Throwable> errorHandler) {
        this.userOnError = errorHandler;
        return this;
    }

    @Override
    public TokenStream ignoreErrors() {
        delegate.ignoreErrors();
        return this;
    }

    @Override
    public void start() {
        delegate.onCompleteResponse(response -> {
            try {
                if (userOnComplete != null) {
                    userOnComplete.accept(response);
                }
            } finally {
                completionLatch.countDown();
            }
        });
        delegate.onError(error -> {
            try {
                if (userOnError != null) {
                    userOnError.accept(error);
                }
            } finally {
                completionLatch.countDown();
            }
        });
        delegate.start();
    }
}
