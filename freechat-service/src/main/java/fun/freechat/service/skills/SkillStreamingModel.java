package fun.freechat.service.skills;

import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatRequestOptions;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import java.util.Set;

public final class SkillStreamingModel implements StreamingChatModel {
    private final StreamingChatModel delegate;
    private final SkillInvocation skills;

    public SkillStreamingModel(StreamingChatModel delegate, SkillInvocation skills) {
        this.delegate = delegate;
        this.skills = skills;
    }

    @Override
    public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
        try {
            delegate.chat(skills.request(request), handler);
        } catch (RuntimeException ignored) {
            handler.onError(new IllegalStateException("Chat skill invocation failed"));
        }
    }

    @Override
    public void chat(ChatRequest request, ChatRequestOptions options, StreamingChatResponseHandler handler) {
        try {
            delegate.chat(skills.request(request), options, handler);
        } catch (RuntimeException ignored) {
            handler.onError(new IllegalStateException("Chat skill invocation failed"));
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
    public ModelProvider provider() {
        return delegate.provider();
    }
}
