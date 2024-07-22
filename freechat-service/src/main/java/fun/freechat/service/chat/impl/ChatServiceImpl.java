package fun.freechat.service.chat.impl;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.TokenStream;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;

import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.data.message.ToolExecutionResultMessage.toolExecutionResultMessage;
import static fun.freechat.service.enums.ChatVar.*;
import static fun.freechat.service.util.PromptUtils.toSingleText;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatServiceImpl implements ChatService {
    private static final int MAX_SEQUENTIAL_TOOL_EXECUTIONS = 10;

    @Autowired
    private ChatContextService chatContextService;
    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private ChatMemoryService chatMemoryService;
    @Autowired
    private LongTermChatMemoryStore longTermChatMemoryStore;
    @Autowired
    private PromptService promptService;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private OrgService orgService;

    @Override
    public String start(User user,
                        String userNickname,
                        String userProfile,
                        String characterNickname,
                        String about,
                        String backendId,
                        String apiKeyName,
                        String apiKeyValue,
                        String ext) {
        String chatId = chatContextService.getIdByBackend(user.getUserId(), backendId);
        if (StringUtils.isNotBlank(chatId)) {
            return chatId;
        }

        CharacterBackend backend = characterService.getBackend(backendId);
        if (Objects.isNull(backend)) {
            return null;
        }

        if (StringUtils.isBlank(about)) {
            about = Optional.ofNullable(backend.getCharacterUid())
                    .map(characterService::getLatestIdByUid)
                    .map(characterService::summary)
                    .map(CharacterInfo::getDefaultScene)
                    .orElse(null);
        }

        ChatContext context = new ChatContext()
                .withApiKeyName(apiKeyName)
                .withApiKeyValue(apiKeyValue)
                .withUserId(user.getUserId())
                .withUserNickname(userNickname)
                .withUserProfile(userProfile)
                .withCharacterNickname(characterNickname)
                .withAbout(about)
                .withBackendId(backendId)
                .withQuota(backend.getInitQuota())
                .withQuotaType(backend.getQuotaType())
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
    public List<Triple<ChatContext, CharacterInfo, ChatMessageRecord>> list(User user) {
        String userId = user.getUserId();
        return chatContextService.list(userId)
                .stream()
                .map(chatContext -> Optional.ofNullable(chatContext.getBackendId())
                            .filter(StringUtils::isNotBlank)
                            .map(characterService::getBackendCharacterUid)
                            .filter(StringUtils::isNotBlank)
                            .map(characterService::getLatestIdByUid)
                            .map(characterService::summary)
                            .map(summary -> {
                                String chatId = chatContext.getChatId();
                                ChatMessageRecord latestMessage = chatMemoryService.getLatestChatMessage(chatId);
                                return Triple.of(chatContext, summary, latestMessage);
                            })
                            .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public String getDefaultChatId(User user, Long characterId) {
        String characterUid = characterService.getUid(characterId);
        CharacterBackend defaultBackend = characterService.getDefaultBackend(characterUid);
        return Objects.nonNull(defaultBackend) ?
                chatContextService.getIdByBackend(user.getUserId(), defaultBackend.getBackendId()) : null;
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'chat:' + #p0 + ',role:' + #p1.type().name() + ',message:' + #p1.text() + ',context:' + #p2")
    public Response<AiMessage> send(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session) || Objects.isNull(message) || !session.acquire()) {
            return null;
        }

        try {
            Object memoryId = asMemoryId(chatId);
            ChatMemory chatMemory = session.getChatMemory(memoryId);

            var messages = handleMessages(message, context, memoryId, session, chatMemory);

            Future<Moderation> moderationFuture = Objects.nonNull(session.getModerationModel()) ?
                    chatSessionService.triggerModerationIfNeeded(session, messages) : null;

            ChatLanguageModel chatModel = session.getChatModel();
            List<ToolSpecification> toolSpecifications = session.getToolSpecifications();

            Response<AiMessage> response = Objects.nonNull(toolSpecifications) ?
                    chatModel.generate(messages, toolSpecifications) :
                    chatModel.generate(messages);
            TokenUsage tokenUsageAccumulator = response.tokenUsage();

            chatSessionService.verifyModerationIfNeeded(session, moderationFuture);

            for (int i = 0; i < MAX_SEQUENTIAL_TOOL_EXECUTIONS; ++i) {
                if (chatMemory instanceof SystemAlwaysOnTopMessageWindowChatMemory myMemory) {
                    myMemory.addAiMessage(response.content(), response.tokenUsage());
                } else {
                    chatMemory.add(response.content());
                }

                session.addMemoryUsage(1L, response.tokenUsage());
                AiMessage aiMessage = response.content();

                if (!aiMessage.hasToolExecutionRequests()) {
                    break;
                }

                for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                    ToolExecutor toolExecutor = session.getToolExecutors().get(toolExecutionRequest.name());
                    String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, memoryId);
                    ToolExecutionResultMessage toolExecutionResultMessage
                            = toolExecutionResultMessage(toolExecutionRequest, toolExecutionResult);
                    chatMemory.add(toolExecutionResultMessage);
                }

                response = chatModel.generate(chatMemory.messages(), toolSpecifications);
                tokenUsageAccumulator = tokenUsageAccumulator.add(response.tokenUsage());
            }

            session.getProcessing().set(false);
            return Response.from(response.content(), tokenUsageAccumulator, response.finishReason());
        } finally {
            session.release();
        }
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'chat:' + #p0 + ',role:' + #p1.type().name() + ',message:' + #p1.text() + ',context:' + #p2")
    public TokenStream streamSend(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session) || Objects.isNull(message) || !session.acquire()) {
            return null;
        }

        session.getProcessing().set(true);

        try {
            Object memoryId = asMemoryId(chatId);
            ChatMemory chatMemory = session.getChatMemory(memoryId);

            var messages = handleMessages(message, context, memoryId, session, chatMemory);
            return new AiServiceTokenStream(messages, session.getAiServiceContext(), memoryId);
        } catch (Throwable e) {
            session.getProcessing().set(false);
            throw e;
        }
    }

    @Override
    public void clearMemory(String chatId) {
        ChatSession session = chatSessionService.get(chatId);
        if (Objects.isNull(session)) {
            return;
        }
        ChatPromptContent prompt = session.getPrompt();
        Map<String, Object> variables = session.getVariables();
        ChatMemory chatMemory = session.getChatMemory(chatId);
        chatMemory.clear();

        chatMemory.add(SystemMessage.from(promptService.apply(
                prompt.getSystem(), variables, session.getPromptFormat())));

        if (CollectionUtils.isNotEmpty(prompt.getMessages())) {
            prompt.getMessages().forEach(chatMemory::add);
        }
    }

    private List<ChatMessage> handleMessages(
            ChatMessage message, String context, Object memoryId,
            ChatSession session, ChatMemory chatMemory) {
        Map<String, Object> variables = session.getVariables();
        variables.put(INPUT.text().toLowerCase(), toSingleText(message));
        variables.put(MESSAGE_CONTEXT.text(), getOrBlank(context));
        variables.put(CURRENT_TIME.text(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String relevantConcatenated = null;
        List<ChatMessage> longTermMemoryMessages = null;
        RetrievalAugmentor knowledgeRetriever = session.getRetriever();
        RetrievalAugmentor longTermMemoryRetriever = session.getLongTermMemoryRetriever();
        if (message.type() == USER &&
                (Objects.nonNull(knowledgeRetriever) || Objects.nonNull(longTermMemoryRetriever))) {
            List<ChatMessage> messages = chatMemory.messages();
            UserMessage userMessage = (UserMessage) message;

            if (Objects.nonNull(knowledgeRetriever)) {
                Metadata metadata = Metadata.from(userMessage, memoryId, messages);
                try {
                    UserMessage relevantMessage =
                            session.getRetriever().augment(userMessage, metadata);
                    if (relevantMessage != userMessage) {
                        relevantConcatenated = toSingleText(relevantMessage);
                    }
                } catch (Throwable ex) {
                    log.warn("Failed to retrieve knowledge from {}!", memoryId, ex);
                }
            }

            if (Objects.nonNull(longTermMemoryRetriever)) {
                try {
                    longTermMemoryMessages = longTermChatMemoryStore.getMessages(
                            memoryId, userMessage, messages, longTermMemoryRetriever);
                } catch (Throwable ex) {
                    log.warn("Failed to retrieve long-term memory from {}!", memoryId, ex);
                }
            }
        }
        variables.put(RELEVANT_INFORMATION.text(), getOrBlank(relevantConcatenated));

        ChatPromptContent prompt = session.getPrompt();
        SystemMessage systemMessage = SystemMessage.from(promptService.apply(
                prompt.getSystem(), variables, session.getPromptFormat()));

        ChatMessage messageToSend = message;
        if (message.type() == USER) {
            if (Objects.nonNull(prompt.getMessageToSend())) {
                messageToSend = promptService.apply(
                        UserMessage.from(prompt.getMessageToSend().contents()),
                        variables,
                        session.getPromptFormat());
            } else {
                messageToSend = UserMessage.from(((UserMessage) message).contents());
            }
        }

        chatMemory.add(systemMessage);
        chatMemory.add(messageToSend);

        return mergeMessages(chatMemory.messages(), longTermMemoryMessages);
    }

    private static String getOrBlank(String content) {
        return StringUtils.isBlank(content) ? "" : content;
    }

    private static List<ChatMessage> mergeMessages(List<ChatMessage> messages,
                                                   List<ChatMessage> longTermMemoryMessages) {
        if (CollectionUtils.isEmpty(longTermMemoryMessages) || CollectionUtils.isEmpty(messages)) {
            return messages;
        }

        List<ChatMessage> allMessages = new LinkedList<>();
        // add the system message at first
        allMessages.add(messages.getFirst());
        // add the long term memory messages
        allMessages.addAll(longTermMemoryMessages);
        // add the rest messages
        allMessages.addAll(messages.subList(1, messages.size()));

        return allMessages;
    }
}
