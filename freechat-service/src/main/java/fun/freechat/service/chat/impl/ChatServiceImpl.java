package fun.freechat.service.chat.impl;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.AiServiceTokenStreamParameters;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.LongTermChatMemoryStore;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.util.TraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static dev.langchain4j.data.message.ChatMessageType.SYSTEM;
import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.data.message.ToolExecutionResultMessage.toolExecutionResultMessage;
import static fun.freechat.service.chat.ChatService.asMemoryId;
import static fun.freechat.service.enums.ChatVar.CHARACTER_DESCRIPTION;
import static fun.freechat.service.enums.ChatVar.CHARACTER_GREETING;
import static fun.freechat.service.enums.ChatVar.CHARACTER_NICKNAME;
import static fun.freechat.service.enums.ChatVar.CHAT_CONTEXT;
import static fun.freechat.service.enums.ChatVar.CURRENT_TIME;
import static fun.freechat.service.enums.ChatVar.INPUT;
import static fun.freechat.service.enums.ChatVar.MESSAGE_CONTEXT;
import static fun.freechat.service.enums.ChatVar.RELEVANT_INFORMATION;
import static fun.freechat.service.enums.ChatVar.USER_NICKNAME;
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
    @Autowired
    private RagTaskService ragTaskService;

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
        String chatId = chatContextService.getChatIdByBackend(user.getUserId(), backendId);
        if (StringUtils.isNotBlank(chatId)) {
            return chatId;
        }

        CharacterBackend backend = characterService.getBackend(backendId);
        if (backend == null) {
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

        if (persistentContext == null) {
            return null;
        }

        ChatSession session = chatSessionService.get(persistentContext);
        if (session == null) {
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
    public String getDefaultChatId(User user, String characterUid) {
        CharacterBackend defaultBackend = characterService.getDefaultBackend(characterUid);
        return defaultBackend != null ?
                chatContextService.getChatIdByBackend(user.getUserId(), defaultBackend.getBackendId()) : null;
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'chat:' + #p0 + ',role:' + #p1.type().name() + ',message:' + #p1.text() + ',context:' + #p2")
    public Pair<ChatResponse, Long> send(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (session == null || message == null || !session.acquire()) {
            return null;
        }

        try {
            Object memoryId = asMemoryId(chatId);
            ChatMemory chatMemory = session.getChatMemory(memoryId);
            var messages = handleMessages(message, context, memoryId, session);

            Future<Moderation> moderationFuture = session.getModerationModel() != null ?
                    chatSessionService.triggerModerationIfNeeded(session, messages) : null;

            ChatModel chatModel = session.getChatModel();
            List<ToolSpecification> toolSpecifications = session.getToolSpecifications();

            ChatResponse response = toolSpecifications != null ?
                    chatModel.chat(ChatRequest.builder()
                            .messages(messages)
                            .toolSpecifications(toolSpecifications)
                            .build()) :
                    chatModel.chat(messages);
            TokenUsage tokenUsageAccumulator = response.tokenUsage();

            chatSessionService.verifyModerationIfNeeded(moderationFuture);
            Long messageId = null;

            for (int i = 0; i < MAX_SEQUENTIAL_TOOL_EXECUTIONS; ++i) {
                if (chatMemory instanceof SystemAlwaysOnTopMessageWindowChatMemory myMemory) {
                    messageId = myMemory.addAiMessage(response.aiMessage(), response.tokenUsage());
                } else {
                    chatMemory.add(response.aiMessage());
                }

                session.addMemoryUsage(1L, response.tokenUsage());
                AiMessage aiMessage = response.aiMessage();

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

                response = chatModel.chat(ChatRequest.builder()
                        .messages(chatMemory.messages())
                        .toolSpecifications(toolSpecifications)
                        .build());
                tokenUsageAccumulator = TokenUsage.sum(tokenUsageAccumulator, response.tokenUsage());
            }

            return Pair.of(
                    ChatResponse.builder()
                            .aiMessage(response.aiMessage())
                            .tokenUsage(tokenUsageAccumulator)
                            .finishReason(response.finishReason())
                            .build(),
                    messageId);
        } finally {
            session.release();
        }
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'chat:' + #p0 + ',role:' + #p1.type().name() + ',message:' + #p1.text() + ',context:' + #p2")
    public TokenStream streamSend(String chatId, ChatMessage message, String context) {
        ChatSession session = chatSessionService.get(chatId);
        if (session == null || message == null || !session.acquire()) {
            return null;
        }

        try {
            Object memoryId = asMemoryId(chatId);
            var messages = handleMessages(message, context, memoryId, session);
            return new AiServiceTokenStream(AiServiceTokenStreamParameters.builder()
                    .messages(messages)
                    .toolSpecifications(session.getToolSpecifications())
                    .toolExecutors(session.getToolExecutors())
                    .context(session.getAiServiceContext())
                    .memoryId(memoryId)
                    .build());
        } catch (Exception e) {
            session.release();
            throw e;
        }
    }

    @Override
    public void clearMemory(String chatId) {
        ChatSession session = chatSessionService.get(chatId);
        if (session == null) {
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

    @Override
    public ChatResponse sendAssistant(String chatId, String assistantUid) {
        ChatSession session = chatSessionService.get(chatId);
        if (session == null || assistantUid == null || session.isProcessing()) {
            return null;
        }

        ChatSession assistantSession = createTemporarySession(session, chatId, assistantUid);
        if (assistantSession == null) {
            return null;
        }

        String assistantChatId = assistantChatId(chatId);
        Object assistantMemoryId = asMemoryId(assistantChatId);

        var messages = handleMessages(null, null, assistantChatId, assistantSession);

        Future<Moderation> moderationFuture = assistantSession.getModerationModel() != null ?
                chatSessionService.triggerModerationIfNeeded(assistantSession, messages) : null;

        ChatModel chatModel = assistantSession.getChatModel();
        List<ToolSpecification> toolSpecifications = assistantSession.getToolSpecifications();

        ChatResponse response = toolSpecifications != null ?
                chatModel.chat(ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecifications)
                        .build()) :
                chatModel.chat(messages);
        TokenUsage tokenUsageAccumulator = response.tokenUsage();

        chatSessionService.verifyModerationIfNeeded(moderationFuture);
        ChatMemory chatMemory = assistantSession.getChatMemory(assistantChatId);

        for (int i = 0; i < MAX_SEQUENTIAL_TOOL_EXECUTIONS; ++i) {
            chatMemory.add(response.aiMessage());

            AiMessage aiMessage = response.aiMessage();
            if (!aiMessage.hasToolExecutionRequests()) {
                break;
            }

            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                ToolExecutor toolExecutor = session.getToolExecutors().get(toolExecutionRequest.name());
                String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, assistantMemoryId);
                ToolExecutionResultMessage toolExecutionResultMessage
                        = toolExecutionResultMessage(toolExecutionRequest, toolExecutionResult);
                chatMemory.add(toolExecutionResultMessage);
            }

            response = chatModel.chat(ChatRequest.builder()
                    .messages(chatMemory.messages())
                    .toolSpecifications(toolSpecifications)
                    .build());
            tokenUsageAccumulator = TokenUsage.sum(tokenUsageAccumulator, response.tokenUsage());
        }

        return ChatResponse.builder()
                .aiMessage(response.aiMessage())
                .tokenUsage(tokenUsageAccumulator)
                .finishReason(response.finishReason())
                .build();
    }

    @Override
    public TokenStream streamSendAssistant(String chatId, String assistantUid) {
        ChatSession session = chatSessionService.get(chatId);
        if (session == null || assistantUid == null || session.isProcessing()) {
            return null;
        }

        ChatSession assistantSession = createTemporarySession(session, chatId, assistantUid);
        if (assistantSession == null) {
            return null;
        }

        String assistantChatId = assistantChatId(chatId);
        Object assistantMemoryId = asMemoryId(assistantChatId);
        var messages = handleMessages(null, null, assistantChatId, assistantSession);

        return new AiServiceTokenStream(AiServiceTokenStreamParameters.builder()
                .messages(messages)
                .toolSpecifications(assistantSession.getToolSpecifications())
                .toolExecutors(assistantSession.getToolExecutors())
                .context(assistantSession.getAiServiceContext())
                .memoryId(assistantMemoryId)
                .build());

    }

    private ChatSession createTemporarySession(ChatSession session, String chatId, String assistantUid) {
        Long assistantId = characterService.getLatestIdByUid(assistantUid);
        CharacterInfo assistantSummary = characterService.summary(assistantId);
        CharacterBackend assistantBackend = characterService.getDefaultBackend(assistantUid);
        if (assistantBackend == null) {
            return null;
        }
        String assistantBackendId = assistantBackend.getBackendId();
        Map<String, Object> variables = session.getVariables();
        String assistantChatId = assistantChatId(chatId);
        Object assistantMemoryId = asMemoryId(assistantChatId);
        InMemoryChatMemoryStore assistantMemoryStore = new InMemoryChatMemoryStore();
        assistantMemoryStore.updateMessages(assistantMemoryId,
                reverseMessages(session.getChatMemory(chatId).messages(),
                                (String) variables.get(CHARACTER_GREETING.text())));

        ChatContext assistantChatContext = new ChatContext()
                .withChatId(assistantChatId)
                .withBackendId(assistantBackendId)
                .withAbout((String) variables.get(CHAT_CONTEXT.text()))
                .withCharacterNickname((String) variables.get(USER_NICKNAME.text()))
                .withUserNickname((String) variables.get(CHARACTER_NICKNAME.text()))
                .withUserProfile((String) variables.get(CHARACTER_DESCRIPTION.text()));
        return chatSessionService.createTemporary(assistantChatContext, assistantMemoryStore);
    }

    private static String assistantChatId(String chatId) {
        return chatId + "-assist";
    }

    private List<ChatMessage> handleMessages(
            ChatMessage message, String context, Object memoryId, ChatSession session) {
        ChatMemory chatMemory = session.getChatMemory(memoryId);
        Map<String, Object> variables = session.getVariables();
        variables.put(MESSAGE_CONTEXT.text(), getOrBlank(context));
        variables.put(CURRENT_TIME.text(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String relevantConcatenated = null;
        List<ChatMessage> longTermMemoryMessages = null;
        RetrievalAugmentor knowledgeRetriever = session.getRetriever();
        RetrievalAugmentor longTermMemoryRetriever = session.getLongTermMemoryRetriever();
        if (message != null &&
                message.type() == USER &&
                (knowledgeRetriever != null || longTermMemoryRetriever != null)) {
            List<ChatMessage> messages = chatMemory.messages();
            UserMessage userMessage = (UserMessage) message;

            String traceId = TraceUtils.getTraceId();
            String username = TraceUtils.getTraceAttribute("username");
            String characterUid = chatContextService.getCharacterUid((String) memoryId);
            String characterName = characterService.getNameByUid(characterUid);
            if (knowledgeRetriever != null && ragTaskService.hasAnyTask(characterUid)) {
                long startTime = System.currentTimeMillis();
                Throwable throwable = null;
                try {
                    relevantConcatenated = Optional.of(knowledgeRetriever)
                            .map(retriever -> retriever.augment(
                                    new AugmentationRequest(userMessage, Metadata.from(userMessage, memoryId, messages))))
                            .map(AugmentationResult::contents)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(Content::textSegment)
                            .map(TextSegment::text)
                            .collect(Collectors.joining("\n\n"));
                } catch (Exception ex) {
                    log.warn("Failed to retrieve knowledge from {}!", memoryId, ex);
                    throwable = ex;
                } finally {
                    long endTime = System.currentTimeMillis();
                    TraceUtils.TraceStatus status = throwable == null ?
                            TraceUtils.TraceStatus.SUCCESSFUL : TraceUtils.TraceStatus.FAILED;
                    String traceInfo = new TraceUtils.TraceInfoBuilder()
                            .args(new String[]{characterName, characterUid})
                            .elapseTime(endTime - startTime)
                            .method("ChatServiceImpl::retrieveKnowledge")
                            .status(status)
                            .throwable(throwable)
                            .traceId(traceId)
                            .username(username)
                            .build();
                    TraceUtils.getPerfLogger().trace(traceInfo);
                }
            }

            if (longTermMemoryRetriever != null &&
                    chatMemoryService.roughCount(memoryId) >= ((SystemAlwaysOnTopMessageWindowChatMemory) chatMemory).getMaxMessages()) {
                long startTime = System.currentTimeMillis();
                Throwable throwable = null;
                try {
                    longTermMemoryMessages = longTermChatMemoryStore.getMessages(
                            memoryId, userMessage, messages, longTermMemoryRetriever);
                } catch (Exception ex) {
                    log.warn("Failed to retrieve long-term memory from {}!", memoryId, ex);
                    throwable = ex;
                } finally {
                    long endTime = System.currentTimeMillis();
                    TraceUtils.TraceStatus status = throwable == null ?
                            TraceUtils.TraceStatus.SUCCESSFUL : TraceUtils.TraceStatus.FAILED;
                    String traceInfo = new TraceUtils.TraceInfoBuilder()
                            .args(new String[]{characterName, characterUid})
                            .elapseTime(endTime - startTime)
                            .method("ChatServiceImpl::retrieveLongTermMemory")
                            .status(status)
                            .throwable(throwable)
                            .traceId(traceId)
                            .username(username)
                            .build();
                    TraceUtils.getPerfLogger().trace(traceInfo);
                }
            }
        }

        variables.put(RELEVANT_INFORMATION.text(), getOrBlank(relevantConcatenated));

        ChatPromptContent prompt = session.getPrompt();
        ChatMessage messageToSend = message;
        if (message != null && message.type() == USER && prompt.getMessageToSend() != null) {
            variables.put(INPUT.text().toLowerCase(), toSingleText(message));
            messageToSend = promptService.apply(
                    UserMessage.from(prompt.getMessageToSend().contents()),
                    variables,
                    session.getPromptFormat());
        }

        SystemMessage systemMessage = SystemMessage.from(promptService.apply(
                prompt.getSystem(), variables, session.getPromptFormat()));

        chatMemory.add(systemMessage);
        if (messageToSend != null) {
            chatMemory.add(messageToSend);
        }

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

    private static List<ChatMessage> reverseMessages(List<ChatMessage> messages, String greeting) {
        LinkedList<ChatMessage> reversedMessages = messages.stream()
                .map(message -> switch (message.type()) {
                    case SYSTEM -> message;
                    case AI -> ((AiMessage) message).hasToolExecutionRequests() ? null : UserMessage.from(toSingleText(message));
                    case USER -> AiMessage.from(toSingleText(message));
                    case null, default -> null;
                })
                .filter(Objects::nonNull)
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

        int firstNonSystemMessageIndex = CollectionUtils.isNotEmpty(reversedMessages) &&
                reversedMessages.getFirst().type() == SYSTEM ? 1: 0;

        if (StringUtils.isNotBlank(greeting)) {
            reversedMessages.add(firstNonSystemMessageIndex, UserMessage.from(greeting));
        } else {
            ChatMessage firstNonSystemMessage = firstNonSystemMessageIndex < reversedMessages.size() ?
                    reversedMessages.get(firstNonSystemMessageIndex) : null;
            while (firstNonSystemMessage != null && firstNonSystemMessage.type() != USER) {
                reversedMessages.remove(firstNonSystemMessageIndex);
                firstNonSystemMessage = firstNonSystemMessageIndex < reversedMessages.size() ?
                        reversedMessages.get(firstNonSystemMessageIndex) : null;
            }
        }

        return reversedMessages;
    }
}
