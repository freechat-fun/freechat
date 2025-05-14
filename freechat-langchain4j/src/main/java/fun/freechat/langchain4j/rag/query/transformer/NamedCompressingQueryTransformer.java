package fun.freechat.langchain4j.rag.query.transformer;

import dev.langchain4j.data.message.*;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Slf4j
public class NamedCompressingQueryTransformer extends CompressingQueryTransformer {
    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE_EN = PromptTemplate.from(
            """
                    Read and understand the conversation between the User and the AI. \
                    Then, analyze the new query from the User. \
                    Identify all relevant details, terms, and context from both the conversation and the new query. \
                    Reformulate this query into a clear, concise, and self-contained format suitable for information retrieval.
                    
                    {{#aiName}}
                    The AI name should appear in the final query.
                    AI name: {{aiName}}
                    {{/aiName}}
                    
                    {{#chatMemory}}
                    Conversation:
                    {{{chatMemory}}}
                    {{/chatMemory}}
                    
                    User query: {{query}}
                    
                    It is very important that you provide only reformulated query and nothing else! \
                    Do not prepend a query with anything!"""
    );

    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE_ZH = PromptTemplate.from(
            """
                    阅读并理解用户与AI之间的对话。\
                    然后，分析用户的新查询。从对话和新查询中识别所有相关的细节、术语和上下文。\
                    将这个查询重新表述为一个清晰、简洁、自包含的格式，适合信息检索。
                    
                    {{#aiName}}
                    AI名字应该出现在最终查询中。
                    AI名字：{{aiName}}
                    {{/aiName}}

                    {{#chatMemory}}
                    对话：
                    {{{chatMemory}}}
                    {{/chatMemory}}

                    用户查询：{{query}}

                    非常重要的是，你只提供重新表述过的查询，不要添加其他任何内容！查询前不要加任何东西！"""
    );

    private final String aiName;

    @Builder(builderMethodName = "extraBuilder")
    public NamedCompressingQueryTransformer(ChatModel chatModel, PromptTemplate promptTemplate, String aiName) {
        super(chatModel, Utils.getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE_EN));
        this.aiName = aiName;
    }

    @Override
    public Collection<Query> transform(Query query) {
        List<ChatMessage> chatMemory = Optional.of(query)
                .map(Query::metadata)
                .map(Metadata::chatMemory)
                .orElse(Collections.emptyList());
        if (chatMemory.isEmpty() && StringUtils.isBlank(aiName)) {
            return singletonList(query);
        }

        Prompt prompt = createPrompt(query, format(chatMemory));
        String compressedQueryText = chatModel.chat(prompt.text());
        log.info("Transformed original query '{}' into '{}'", query.text(), compressedQueryText);
        Query compressedQuery = Query.from(compressedQueryText, query.metadata());
        return singletonList(compressedQuery);
    }

    @Override
    protected String format(ChatMessage message) {
        if (message.type() == ChatMessageType.USER) {
            return "User: " + toSingleText(message);
        } else if (message.type() == ChatMessageType.AI) {
            AiMessage aiMessage = (AiMessage) message;
            if (aiMessage.hasToolExecutionRequests()) {
                return null;
            }
            return StringUtils.isBlank(aiName) ?
                    "AI: " + aiMessage.text() :
                    aiName + ": " + aiMessage.text();
        } else {
            return null;
        }
    }

    @Override
    protected Prompt createPrompt(Query query, String chatMemory) {
        Map<String, Object> variables = HashMap.newHashMap(3);
        if (StringUtils.isNotBlank(aiName)) {
            variables.put("aiName", aiName);
        }
        if (StringUtils.isNotBlank(chatMemory)) {
            variables.put("chatMemory", chatMemory);
        }
        variables.put("query", query.text());
        return promptTemplate.apply(variables);
    }

    private static String toSingleText(ChatMessage message) {
        return ((UserMessage) message).contents()
                .stream()
                .filter(TextContent.class::isInstance)
                .map(TextContent.class::cast)
                .map(TextContent::text)
                .collect(Collectors.joining("\n"));
    }
}
