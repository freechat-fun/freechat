package fun.freechat.service.character.impl;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.TokenStream;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.character.*;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptRole;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.util.PromptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static dev.langchain4j.data.message.ToolExecutionResultMessage.toolExecutionResultMessage;
import static fun.freechat.service.enums.ChatVar.*;

@Service
@Slf4j
@SuppressWarnings("unused")
public class CharacterAiServiceImpl implements CharacterAiService {
    private static final int MAX_SEQUENTIAL_TOOL_EXECUTIONS = 10;

    @Autowired
    private ChatContextService chatContextService;

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private PromptService promptService;

    @Autowired
    private CharacterService characterService;

    @Override
    public String start(User user,
                        String userNickname,
                        String userProfile,
                        String characterNickname,
                        String about,
                        String backendId,
                        String ext) {
        ChatContext context = new ChatContext()
                .withUserId(user.getUserId())
                .withUserNickname(userNickname)
                .withUserProfile(userProfile)
                .withCharacterNickname(characterNickname)
                .withAbout(about)
                .withBackendId(backendId)
                .withExt(ext);
        ChatContext persistentContext = chatContextService.create(context);
        if (Objects.isNull(persistentContext)) {
            return null;
        }
        ChatSession session = chatSessionService.get(persistentContext);
        if (Objects.isNull(session)) {
            chatContextService.delete(persistentContext.getChatId());
            return null;
        }
        return persistentContext.getChatId();
    }

    @Override
    public boolean delete(String chatId) {
        chatSessionService.reset(chatId);
        return chatContextService.delete(chatId);
    }

    @Override
    public Response<ChatMessage> send(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session) || Objects.isNull(message)) {
            return null;
        }

        Object memoryId = asMemoryId(chatId);
        ChatMemory chatMemory = session.getChatMemory(memoryId);

        var messages = handleMessages(message, context, memoryId, session, chatMemory);

        Future<Moderation> moderationFuture = Objects.nonNull(session.getModerationModel()) ?
                chatSessionService.triggerModerationIfNeeded(session, messages) : null;

        ChatLanguageModel chatModel = session.getChatModel();
        List<ToolSpecification> toolSpecifications = session.getToolSpecifications();

        Response<dev.langchain4j.data.message.AiMessage> response = Objects.nonNull(toolSpecifications) ?
                chatModel.generate(messages, toolSpecifications) :
                chatModel.generate(messages);
        TokenUsage tokenUsageAccumulator = response.tokenUsage();

        chatSessionService.verifyModerationIfNeeded(session, moderationFuture);

        for (int i = 0; i < MAX_SEQUENTIAL_TOOL_EXECUTIONS; ++i) {
            chatMemory.add(response.content());

            AiMessage aiMessage = response.content();

            if (!aiMessage.hasToolExecutionRequests()) {
                break;
            }

            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                ToolExecutor toolExecutor = session.getToolExecutors().get(toolExecutionRequest.name());
                String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, memoryId);
                dev.langchain4j.data.message.ToolExecutionResultMessage toolExecutionResultMessage
                        = toolExecutionResultMessage(toolExecutionRequest, toolExecutionResult);
                chatMemory.add(toolExecutionResultMessage);
            }

            response = chatModel.generate(chatMemory.messages(), toolSpecifications);
            tokenUsageAccumulator = tokenUsageAccumulator.add(response.tokenUsage());
        }

        Object aiName = session.getVariables().get(CHARACTER_NICKNAME.text());
        return Response.from(
                PromptUtils.convertL4jMessage(response.content()).withName((String) aiName),
                tokenUsageAccumulator,
                response.finishReason());
    }

    @Override
    public TokenStream streamSend(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session) || Objects.isNull(message)) {
            return null;
        }

        Object memoryId = asMemoryId(chatId);
        ChatMemory chatMemory = session.getChatMemory(memoryId);

        var messages = handleMessages(message, context, memoryId, session, chatMemory);
        return new AiServiceTokenStream(messages, session.getAiServiceContext(), memoryId);
    }

    private List<dev.langchain4j.data.message.ChatMessage> handleMessages(
            ChatMessage message, String context, Object memoryId,
            ChatSession session, ChatMemory chatMemory) {
        Map<String, Object> variables = session.getVariables();
        variables.put(INPUT.text().toLowerCase(), message.getContentText());
        variables.put(MESSAGE_CONTEXT.text(), getOrBlank(context));
        variables.put(CURRENT_TIME.text(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        dev.langchain4j.data.message.ChatMessage l4jMessage = PromptUtils.convertChatMessage(message);
        String relevantConcatenated = null;
        if (l4jMessage.type() == dev.langchain4j.data.message.ChatMessageType.USER &&
                Objects.nonNull(session.getRetrieverAugmentor())) {
            Metadata metadata = Metadata.from((UserMessage) l4jMessage, memoryId, chatMemory.messages());
            dev.langchain4j.data.message.UserMessage relevantMessage =
                    session.getRetrieverAugmentor().augment(UserMessage.from(""), metadata);
            relevantConcatenated = PromptUtils.convertL4jMessage(relevantMessage).getContentText();
        }
        variables.put(RELEVANT_INFORMATION.text(), getOrBlank(relevantConcatenated));

        ChatPromptContent prompt = session.getPrompt();
        ChatMessage systemMessage = ChatMessage.from(
                PromptRole.SYSTEM,
                promptService.apply(prompt.getSystem(), variables, PromptFormat.of(prompt.getFormat())));

        ChatMessage userMessage = Optional.ofNullable(prompt.getMessageToSend())
                .map(userPrompt ->
                        promptService.apply(userPrompt, variables, PromptFormat.of(prompt.getFormat())))
                .orElse(message);
        userMessage.setName((String) variables.get(USER_NICKNAME.text()));

        chatMemory.add(PromptUtils.convertChatMessage(systemMessage));
        chatMemory.add(PromptUtils.convertChatMessage(userMessage));

        return chatMemory.messages();
    }

    private String getOrBlank(String content) {
        return StringUtils.isBlank(content) ? "" : content;
    }
}
