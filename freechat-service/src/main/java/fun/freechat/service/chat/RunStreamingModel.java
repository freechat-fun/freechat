package fun.freechat.service.chat;

import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatRequestOptions;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.*;
import java.util.Set;

/** Observes actual conversational calls, inside memory and skill preparation wrappers. */
final class RunStreamingModel implements StreamingChatModel {
    private final StreamingChatModel delegate;
    private final ChatRun run;

    RunStreamingModel(StreamingChatModel delegate, ChatRun run) {
        this.delegate = delegate;
        this.run = run;
    }

    @Override
    public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
        run.waitingModel();
        delegate.chat(request, observe(handler));
    }

    @Override
    public void chat(ChatRequest request, ChatRequestOptions options, StreamingChatResponseHandler handler) {
        run.waitingModel();
        delegate.chat(request, options, observe(handler));
    }

    private StreamingChatResponseHandler observe(StreamingChatResponseHandler handler) {
        return new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String response) {
                handler.onPartialResponse(response);
            }

            @Override
            public void onPartialResponse(PartialResponse response, PartialResponseContext context) {
                handler.onPartialResponse(response, context);
            }

            @Override
            public void onPartialThinking(PartialThinking thinking) {
                handler.onPartialThinking(thinking);
            }

            @Override
            public void onPartialThinking(PartialThinking thinking, PartialThinkingContext context) {
                handler.onPartialThinking(thinking, context);
            }

            @Override
            public void onPartialToolCall(PartialToolCall toolCall) {
                handler.onPartialToolCall(toolCall);
            }

            @Override
            public void onPartialToolCall(PartialToolCall toolCall, PartialToolCallContext context) {
                handler.onPartialToolCall(toolCall, context);
            }

            @Override
            public void onCompleteToolCall(CompleteToolCall toolCall) {
                handler.onCompleteToolCall(toolCall);
            }

            @Override
            public void onUnmappedRawEvent(Object event) {
                handler.onUnmappedRawEvent(event);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                if (!response.aiMessage().hasToolExecutionRequests()) run.finalizing();
                handler.onCompleteResponse(response);
            }

            @Override
            public void onError(Throwable error) {
                handler.onError(error);
            }
        };
    }

    @Override
    public ChatRequestParameters defaultRequestParameters() {
        return delegate.defaultRequestParameters();
    }

    @Override
    public Set<Capability> supportedCapabilities() {
        return delegate.supportedCapabilities();
    }

    @Override
    public java.util.List<dev.langchain4j.model.chat.listener.ChatModelListener> listeners() {
        return delegate.listeners();
    }

    @Override
    public ModelProvider provider() {
        return delegate.provider();
    }
}
