package fun.freechat.service.chat;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecutor;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;

public final class ChatExecution {
    private final ChatSession session;
    private final Object memoryId;
    private final ChatMemory memory;
    private ChatResponse pending;
    private Long messageId;

    public ChatExecution(ChatSession session, Object memoryId) {
        this.session = session;
        this.memoryId = memoryId;
        this.memory = session.getChatMemory(memoryId);
    }

    public Pair<ChatResponse, Long> send(LongTermChatMemoryStore.Binding binding, Runnable verifyModeration) {
        ChatModel delegate = session.getChatModel();
        ChatModel model = new ChatModel() {
            private boolean first = true;

            @Override
            public ChatResponse chat(ChatRequest request) {
                if (binding != null) {
                    binding.lifetime().check();
                }
                ChatResponse response = delegate.chat(request);
                if (binding != null
                        && !binding.memory().response(UUID.randomUUID().toString(), response)) {
                    throw new IllegalStateException("Chat memory invocation is no longer valid");
                }
                if (first) {
                    verifyModeration.run();
                    first = false;
                }
                pending = response;
                return response;
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
        };
        Result<AiMessage> result = builder(Synchronous.class)
                .chatModel(model)
                // The synchronous 1.20 loop counts the final non-tool response against this limit too.
                .maxToolCallingRoundTrips(session.getMaxToolRounds() + 1)
                .build()
                .send(memoryId);
        if (binding != null) {
            messageId = binding.memory().finalMessageId();
            binding.lifetime().finish();
        }
        return Pair.of(
                ChatResponse.builder()
                        .aiMessage(result.content())
                        .metadata(result.finalResponse().metadata().toBuilder()
                                .tokenUsage(result.tokenUsage())
                                .build())
                        .build(),
                messageId);
    }

    public TokenStream stream() {
        return builder(Streaming.class)
                .streamingChatModel(session.getStreamingChatModel())
                .build()
                .send(memoryId);
    }

    private <T> AiServices<T> builder(Class<T> type) {
        ChatMemory executionMemory = new ChatMemory() {
            private int toolRounds;

            @Override
            public Object id() {
                return memoryId;
            }

            @Override
            public void add(ChatMessage message) {
                // Input is already persisted; AiServices' required invocation message is execution-only.
                if (message instanceof UserMessage) {
                    return;
                }
                if (message instanceof AiMessage ai
                        && ai.hasToolExecutionRequests()
                        && ++toolRounds > session.getMaxToolRounds()) {
                    throw new IllegalStateException("Chat tool round limit exceeded");
                }
                if (message instanceof AiMessage ai && pending != null) {
                    if (memory instanceof SystemAlwaysOnTopMessageWindowChatMemory window) {
                        messageId = window.addAiMessage(ai, pending.tokenUsage());
                    } else {
                        memory.add(ai);
                    }
                    session.addMemoryUsage(1L, pending.tokenUsage());
                    pending = null;
                } else {
                    memory.add(message);
                }
            }

            @Override
            public List<ChatMessage> messages() {
                return memory.messages();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Execution cannot clear chat history");
            }
        };
        Map<ToolSpecification, ToolExecutor> tools = new LinkedHashMap<>();
        if (session.getToolSpecifications() != null) {
            for (ToolSpecification specification : session.getToolSpecifications()) {
                tools.put(specification, session.getToolExecutors().get(specification.name()));
            }
        }
        return AiServices.builder(type)
                .chatMemoryProvider(ignored -> executionMemory)
                .userMessageProvider(ignored -> "Continue the prepared conversation")
                .chatRequestTransformer(request ->
                        request.toBuilder().messages(memory.messages()).build())
                .tools(tools)
                .maxToolCallingRoundTrips(session.getMaxToolRounds())
                .toolArgumentsErrorHandler(session.getToolArgumentsErrorHandler())
                .toolExecutionErrorHandler(session.getToolExecutionErrorHandler());
    }

    interface Synchronous {
        Result<AiMessage> send(@MemoryId Object memoryId);
    }

    interface Streaming {
        TokenStream send(@MemoryId Object memoryId);
    }
}
