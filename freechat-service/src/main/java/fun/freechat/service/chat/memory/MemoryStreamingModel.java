package fun.freechat.service.chat.memory;

import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatRequestOptions;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.CompleteToolCall;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.PartialThinkingContext;
import dev.langchain4j.model.chat.response.PartialToolCall;
import dev.langchain4j.model.chat.response.PartialToolCallContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.response.StreamingHandle;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Preflight and accounting around the pinned framework loop, not a replacement tool loop. */
public final class MemoryStreamingModel implements StreamingChatModel {
    private final StreamingChatModel delegate;
    private final MemoryInvocation invocation;
    private final MemoryTurnLifetime lifetime;
    private final Set<StreamingHandle> handles = Collections.newSetFromMap(new IdentityHashMap<>());

    public MemoryStreamingModel(StreamingChatModel delegate, MemoryInvocation invocation, MemoryTurnLifetime lifetime) {
        this.delegate = delegate;
        this.invocation = invocation;
        this.lifetime = lifetime;
    }

    @Override
    public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
        dispatch(request, handler, call -> delegate.chat(call.request, call));
    }

    @Override
    public void chat(ChatRequest request, ChatRequestOptions options, StreamingChatResponseHandler handler) {
        dispatch(request, handler, call -> delegate.chat(call.request, options, call));
    }

    private void dispatch(ChatRequest request, StreamingChatResponseHandler handler, Consumer<Call> send) {
        Call call = new Call(handler);
        try {
            lifetime.check();
            call.request = request.toBuilder().messages(invocation.messages()).build();
            lifetime.check();
            send.accept(call);
        } catch (Throwable ignored) {
            call.onError(MemoryTurnLifetime.unavailable());
        }
    }

    private void capture(StreamingHandle handle) {
        if (handle == null) {
            return;
        }
        boolean overflow;
        synchronized (handles) {
            if (handles.contains(handle)) {
                return;
            }
            overflow = handles.size() >= lifetime.cancellationLimit();
            if (!overflow) {
                handles.add(handle);
            }
        }
        if (overflow) {
            lifetime.close();
        }
        lifetime.onCancel(handle::cancel);
    }

    private final class Call implements StreamingChatResponseHandler {
        private final String id = UUID.randomUUID().toString();
        private final StreamingChatResponseHandler handler;
        private final AtomicBoolean terminal = new AtomicBoolean();
        private final AtomicBoolean errorDelivered = new AtomicBoolean();
        private ChatRequest request;

        private Call(StreamingChatResponseHandler handler) {
            this.handler = handler;
        }

        private void forward(Runnable action) {
            if (terminal.get()) {
                return;
            }
            try {
                lifetime.check();
                if (!terminal.get()) {
                    action.run();
                }
            } catch (Throwable ignored) {
                fail();
            }
        }

        private void contextual(java.util.function.Supplier<StreamingHandle> handle, Runnable action) {
            try {
                capture(handle.get());
                forward(action);
            } catch (Throwable ignored) {
                fail();
            }
        }

        @Override
        public void onPartialResponse(String response) {
            forward(() -> handler.onPartialResponse(response));
        }

        @Override
        public void onPartialResponse(PartialResponse response, PartialResponseContext context) {
            contextual(() -> context.streamingHandle(), () -> handler.onPartialResponse(response, context));
        }

        @Override
        public void onPartialThinking(PartialThinking thinking) {
            forward(() -> handler.onPartialThinking(thinking));
        }

        @Override
        public void onPartialThinking(PartialThinking thinking, PartialThinkingContext context) {
            contextual(() -> context.streamingHandle(), () -> handler.onPartialThinking(thinking, context));
        }

        @Override
        public void onPartialToolCall(PartialToolCall toolCall) {
            forward(() -> handler.onPartialToolCall(toolCall));
        }

        @Override
        public void onPartialToolCall(PartialToolCall toolCall, PartialToolCallContext context) {
            contextual(() -> context.streamingHandle(), () -> handler.onPartialToolCall(toolCall, context));
        }

        @Override
        public void onCompleteToolCall(CompleteToolCall toolCall) {
            forward(() -> handler.onCompleteToolCall(toolCall));
        }

        @Override
        public void onUnmappedRawEvent(Object event) {
            forward(() -> handler.onUnmappedRawEvent(event));
        }

        @Override
        public void onCompleteResponse(ChatResponse response) {
            try {
                // Also call this for duplicate/cancelled responses: SQL deduplicates by this call ID,
                // and missing usage remains unknown. It must precede the framework's memory.add(ai).
                boolean accepted = invocation.response(id, response);
                if (!accepted || !terminal.compareAndSet(false, true)) {
                    return;
                }
                lifetime.check();
                handler.onCompleteResponse(response);
            } catch (Throwable ignored) {
                fail();
            }
        }

        @Override
        public void onError(Throwable error) {
            if (terminal.compareAndSet(false, true)) {
                fail();
            }
        }

        private void fail() {
            terminal.set(true);
            lifetime.close();
            if (errorDelivered.compareAndSet(false, true)) {
                MemoryTurnLifetime.safely(() -> handler.onError(MemoryTurnLifetime.unavailable()));
            }
        }
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
    public List<ChatModelListener> listeners() {
        return delegate.listeners();
    }

    @Override
    public ModelProvider provider() {
        return delegate.provider();
    }
}
