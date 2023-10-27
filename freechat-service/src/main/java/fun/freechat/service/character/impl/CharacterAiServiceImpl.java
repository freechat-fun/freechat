package fun.freechat.service.character.impl;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static dev.langchain4j.data.message.ToolExecutionResultMessage.toolExecutionResultMessage;
import static fun.freechat.service.enums.ChatVar.*;
import static java.util.stream.Collectors.joining;

@Service
@Slf4j
@SuppressWarnings("unused")
public class CharacterAiServiceImpl implements CharacterAiService {
    private static final int MAX_TOOL_EXECUTION_TIMES = 30;

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
                        String backendId,
                        String ext) {
        ChatContext context = new ChatContext()
                .withUserId(user.getUserId())
                .withUserNickname(userNickname)
                .withUserProfile(userProfile)
                .withCharacterNickname(characterNickname)
                .withBackendId(backendId)
                .withExt(ext);
        String chatId = chatContextService.create(context);
        if (StringUtils.isBlank(chatId)) {
            return null;
        }
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session)) {
            chatContextService.delete(chatId);
            return null;
        }
        return chatId;
    }

    @Override
    public boolean delete(String chatId) {
        chatSessionService.reset(chatId);
        return chatContextService.delete(chatId);
    }

    @Override
    public Response<ChatMessage> send(String chatId, String text) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session) || StringUtils.isBlank(text)) {
            return null;
        }

        Map<String, Object> variables = session.getVariables();
        variables.put(INPUT.text(), text);

        if (Objects.nonNull(session.getRetriever())) {
            List<TextSegment> relevant = session.getRetriever().findRelevant(text);

            if (CollectionUtils.isNotEmpty(relevant)) {
                String relevantConcatenated = relevant.stream()
                        .map(TextSegment::text)
                        .collect(joining("\n\n"));
                log.debug("Retrieved relevant information:\n" + relevantConcatenated + "\n");
                variables.put(RELEVANT_INFORMATION.text(), relevantConcatenated);
            }
        }

        try {

            ChatPromptContent prompt = session.getPrompt();

            ChatMessage systemMessage = ChatMessage.from(
                    PromptRole.SYSTEM,
                    promptService.apply(prompt.getSystem(), variables, PromptFormat.of(prompt.getFormat())));

            ChatMemoryStore chatMemoryStore = session.getChatMemoryStore();
            if (chatMemoryStore instanceof ChatMemoryService chatMemoryService) {
                chatMemoryService.updateSystemMessageIfNecessary(chatId, systemMessage);
            }

            ChatMessage userMessage = Optional.ofNullable(prompt.getMessagesToSend())
                    .map(userPrompt ->
                            promptService.apply(userPrompt, variables, PromptFormat.of(prompt.getFormat())))
                    .orElse(ChatMessage.from(PromptRole.USER, text));
            userMessage.setName((String) variables.get(USER_NICKNAME.text()));

            ChatMemory chatMemory = session.getChatMemory();
            chatMemory.add(PromptUtils.convertChatMessage(userMessage));

            var messages = chatMemory.messages();

            Future<Moderation> moderationFuture = Objects.nonNull(session.getModerationModel()) ?
                    chatSessionService.triggerModerationIfNeeded(session, messages) : null;

            ChatLanguageModel chatModel = session.getChatModel();
            List<ToolSpecification> toolSpecifications = session.getToolSpecifications();

            Response<AiMessage> response = Objects.nonNull(toolSpecifications) ?
                    chatModel.generate(messages, toolSpecifications) :
                    chatModel.generate(messages);

            chatSessionService.verifyModerationIfNeeded(session, moderationFuture);

            TokenUsage tokenUsage = new TokenUsage();
            ToolExecutionRequest toolExecutionRequest;
            for (int i = 0; i < MAX_TOOL_EXECUTION_TIMES; ++i) { // TODO limit number of cycles
                chatMemory.add(response.content());
                tokenUsage.add(response.tokenUsage());

                toolExecutionRequest = response.content().toolExecutionRequest();
                if (Objects.isNull(toolExecutionRequest)) {
                    break;
                }

                ToolExecutor toolExecutor = session.getToolExecutors().get(toolExecutionRequest.name());
                String toolExecutionResult = toolExecutor.execute(toolExecutionRequest);
                ToolExecutionResultMessage toolExecutionResultMessage
                        = toolExecutionResultMessage(toolExecutionRequest.name(), toolExecutionResult);

                chatMemory.add(toolExecutionResultMessage);
                response = chatModel.generate(chatMemory.messages(), toolSpecifications);
            }

            Object aiName = variables.get(CHARACTER_NICKNAME.text());
            return Response.from(
                    PromptUtils.convertL4jMessage(response.content()).withName((String) aiName),
                    tokenUsage,
                    response.finishReason());
        } finally {
            variables.put(INPUT.text(), "");
            variables.put(RELEVANT_INFORMATION.text(), "");

        }
    }

    @Override
    public TokenStream streamSend(String chatId, String text) {
        return null;
    }
}
