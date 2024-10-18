package fun.freechat.api;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.*;
import fun.freechat.service.ai.AiModelInfoService;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.QuotaType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.util.TraceUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@Tag(name = "Chat")
@RequestMapping("/api/v1/chat")
@ResponseBody
@Validated
@Slf4j
@SuppressWarnings("unused")
public class ChatApi {
    @Autowired
    private CharacterService characterService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatMemoryService chatMemoryService;
    @Autowired
    private ChatContextService chatContextService;
    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private PromptTaskService promptTaskService;
    @Autowired
    private AiModelInfoService aiModelInfoService;

    private void checkQuotaValue(long usage, long limit) {
        if (usage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Usage " + usage + " exceeds limit " + limit);
        }
    }

    private void checkQuota(String chatId) {
        ChatContext context = chatContextService.get(chatId);

        if (StringUtils.isNotBlank(context.getApiKeyName()) ||
                StringUtils.isNotBlank(context.getApiKeyValue())) {
            // use the chatter's own credentials
            return;
        }

        ChatSession session = chatSessionService.get(chatId);
        Long limit = Utils.getOrDefault(context.getQuota(), 0L);
        switch (QuotaType.of(context.getQuotaType())) {
            case MESSAGES -> {
                Long usage = Utils.getOrDefault(session.getMemoryUsage().messageUsage(), 0L);
                checkQuotaValue(usage, limit);
            }
            case TOKENS -> {
                Integer usage = Utils.getOrDefault(session.getMemoryUsage().tokenUsage().totalTokenCount(), 0);
                checkQuotaValue(usage.longValue(), limit);
            }
        }
    }

    @Operation(
            operationId = "startChat",
            summary = "Start Chat Session",
            description = "Start a chat session."
    )
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.characterUid, 'chatCreateOp')")
    public String start(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parameters for starting a chat session")
            @RequestBody
            @NotNull
            ChatCreateDTO chatCreateParams) {
        String chatCharacterUid = chatCreateParams.getCharacterUid();
        String backendId = null;
        if (chatCreateParams.getBackendId() == null) {
            CharacterBackend backend = characterService.getDefaultBackend(chatCharacterUid);
            if (backend != null) {
                backendId = backend.getBackendId();
            }
        } else {
            String backendCharacterUid = characterService.getBackendCharacterUid(chatCreateParams.getBackendId());
            if (backendCharacterUid.equals(chatCharacterUid)) {
                backendId = chatCreateParams.getBackendId();
            }
        }

        if (StringUtils.isBlank(backendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No backend found.");
        }

        String chatId = chatService.start(AccountUtils.currentUser(),
                chatCreateParams.getUserNickname(),
                chatCreateParams.getUserProfile(),
                chatCreateParams.getCharacterNickname(),
                chatCreateParams.getAbout(),
                backendId,
                chatCreateParams.getApiKeyName(),
                chatCreateParams.getApiKeyValue(),
                chatCreateParams.getExt());
        if (StringUtils.isBlank(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Failed to start a chat.");
        }
        return chatId;
    }

    @Operation(
            operationId = "deleteChat",
            summary = "Delete Chat Session",
            description = "Delete the chat session."
    )
    @DeleteMapping("/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public Boolean delete(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        return chatService.delete(chatId);
    }

    @Operation(
            operationId = "listChats",
            summary = "List Chats",
            description = "List chats of current user."
    )
    @GetMapping("")
    public List<ChatSessionDTO> list() {
        return chatService.list(AccountUtils.currentUser())
                .stream()
                .map(chatInfo -> {
                    ChatContext chatContext = chatInfo.getLeft();
                    CharacterInfo characterInfo = chatInfo.getMiddle();
                    String chatOwner = chatContextService.getChatOwner(chatContext.getChatId());
                    String characterOwner = chatContextService.getCharacterOwner(chatContext.getChatId());
                    String currentUserId = AccountUtils.currentUser().getUserId();
                    Boolean isDebugEnabled =currentUserId.equals(chatOwner) && currentUserId.equals(characterOwner);
                    String backendId = chatContext.getBackendId();
                    Optional<CharacterBackend> backend = Optional.ofNullable(chatContext.getBackendId())
                            .map(characterService::getBackend);

                    String provider = backend.map(CharacterBackend::getChatPromptTaskId)
                            .map(promptTaskService::get)
                            .map(PromptTask::getModelId)
                            .map(aiModelInfoService::get)
                            .map(AiModelInfo::getProvider)
                            .orElse(null);

                    Integer proactiveChatWaitingTime = backend.map(CharacterBackend::getProactiveChatWaitingTime)
                            .orElse(0);

                    Boolean isCustomizedApiKeyEnabled = Optional.ofNullable(provider)
                            .map(ModelProvider::of)
                            .map(ModelProvider::hasPublicEndpoint)
                            .orElse(Boolean.FALSE);

                    String senderStatus;
                    if (characterInfo == null || StringUtils.isBlank(characterOwner)) {
                        // The character or backend no longer exists.
                        senderStatus = "offline";
                    } else {
                        Visibility visibility = Visibility.of(characterInfo.getVisibility());
                        senderStatus = switch (visibility) {
                            case PUBLIC -> "online";
                            case PUBLIC_ORG -> orgService.hasRelationship(
                                    AccountUtils.currentUser().getUserId(), characterOwner) ? "online" : "offline";
                            case PRIVATE -> isDebugEnabled ? "invisible" : "offline";
                            case null, default -> "offline";
                        };
                    }
                    ChatMessageRecord latestMessage = chatInfo.getRight();
                    if (latestMessage != null && latestMessage.getMessage().type() == ChatMessageType.SYSTEM) {
                        ChatMessageRecord replacedMessage = null;
                        ChatSession session = chatSessionService.get(chatContext.getChatId());
                        if (session != null) {
                            // replace with greeting message
                            String greeting = (String) session.getVariables().get(ChatVar.CHARACTER_GREETING.text());
                            if (StringUtils.isNotBlank(greeting)) {
                                AiMessage greetingMessage = AiMessage.from(greeting);
                                latestMessage.setMessage(greetingMessage);
                                replacedMessage = latestMessage;
                            }
                        }
                        chatInfo = Triple.of(chatInfo.getLeft(), chatInfo.getMiddle(), replacedMessage);
                    }
                    return ChatSessionDTO.from(chatInfo, provider, proactiveChatWaitingTime,
                            senderStatus, isDebugEnabled, isCustomizedApiKeyEnabled);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Operation(
            operationId = "getDefaultChatId",
            summary = "Get Default Chat",
            description = "Get default chat id of current user and the character."
    )
    @GetMapping(value = "/{characterId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getDefault(
            @Parameter(description = "Character identifier") @PathVariable("characterId") @Positive Long characterId) {
        return chatService.getDefaultChatId(AccountUtils.currentUser(), characterId);
    }

    @Operation(
            operationId = "clearMemory",
            summary = "Clear Memory",
            description = "Clear memory of the chat session."
    )
    @DeleteMapping("/memory/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> clearMemory(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        chatService.clearMemory(chatId);
        return messages(chatId, Optional.empty(), Optional.empty());
    }

    @Operation(
            operationId = "updateChat",
            summary = "Update Chat Session",
            description = "Update the chat session."
    )
    @PutMapping("/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public Boolean update(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The chat session information to be updated")
            @RequestBody
            @NotNull
            ChatUpdateDTO chatUpdate) {
        return chatContextService.update(chatUpdate.toChatContext(chatId));
    }

    @Operation(
            operationId = "sendMessage",
            summary = "Send Chat Message",
            description = "Send a chat message to character."
    )
    @PostMapping("/send/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public LlmResultDTO send(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat message") @RequestBody @NotNull ChatMessageDTO chatMessage) {
        checkQuota(chatId);
        Response<AiMessage> response = chatService.send(
                chatId, chatMessage.toChatMessage(), chatMessage.getContext());

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to chat by " + chatId);
        }

        return LlmResultDTO.from(response);
    }

    @Operation(
            operationId = "streamSendMessage",
            summary = "Send Chat Message by Streaming Back",
            description = "Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response."
    )
    @PostMapping(value = "/send/stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public SseEmitter streamSend(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat message") @RequestBody @NotNull ChatMessageDTO chatMessage) {
        checkQuota(chatId);
        SseEmitter sseEmitter = new SseEmitter();
        // avoid instruction reordering
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        TokenStream tokenStream = chatService.streamSend(
                chatId, chatMessage.toChatMessage(), chatMessage.getContext());

        if (tokenStream == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to chat by " + chatId);
        }

        ChatSession session = chatSessionService.get(chatId);

        String traceId = TraceUtils.getTraceId();
        String username = AccountUtils.currentUser().getUsername();
        String characterUid = chatContextService.getCharacterUid(chatId);
        String characterName = characterService.getNameByUid(characterUid);
        AtomicBoolean firstPackageReceived = new AtomicBoolean(false);

        tokenStream.onNext(partialResult -> {
            if (!session.isProcessing()) {
                return;
            }

            if (!firstPackageReceived.get()) {
                TraceUtils.putTraceAttribute("traceId", traceId);
                TraceUtils.putTraceAttribute("username", username);
                long firstPackageReceivedTime = System.currentTimeMillis();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{characterName, characterUid})
                        .elapseTime(firstPackageReceivedTime - startTime.get())
                        .method("ChatApi::firstPackageReceived")
                        .response(partialResult)
                        .status(TraceUtils.TraceStatus.SUCCESSFUL)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
                firstPackageReceived.set(true);
            }
            try {
                LlmResultDTO result = LlmResultDTO.from(
                        partialResult, null, null, null);
                result.setRequestId(null);
                sseEmitter.send(result);
            } catch (IllegalStateException | NullPointerException | IOException e) {
                log.warn("Error when sending message.", e);
                session.release();
                sseEmitter.completeWithError(e);
                long lastPackageReceivedTime = System.currentTimeMillis();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{characterName, characterUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::lastPackageReceived")
                        .response(partialResult)
                        .throwable(e)
                        .status(TraceUtils.TraceStatus.BAD_REQUEST)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            }
        }).onComplete(response -> {
            if (!session.isProcessing()) {
                return;
            }

            long lastPackageReceivedTime = System.currentTimeMillis();
            try {
                session.addMemoryUsage(1L, response.tokenUsage());
                chatMemoryService.updateChatMessageTokenUsage(chatId, response.content(), response.tokenUsage());
                LlmResultDTO result = LlmResultDTO.from(response);
                Objects.requireNonNull(result).setText(null);
                result.setRequestId(null);
                sseEmitter.send(result);
                sseEmitter.complete();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{characterName, characterUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::lastPackageReceived")
                        .status(TraceUtils.TraceStatus.SUCCESSFUL)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            } catch (Exception e) {
                log.warn("Error when sending message.", e);
                sseEmitter.completeWithError(e);
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{characterName, characterUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::lastPackageReceived")
                        .throwable(e)
                        .status(TraceUtils.TraceStatus.BAD_REQUEST)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            } finally {
                session.release();
            }
        }).onError(ex -> {
            if (!session.isProcessing()) {
                return;
            }

            try {
                log.error("SSE exception.", ex);
                sseEmitter.completeWithError(ex);
            } finally {
                session.release();
            }
            long lastPackageReceivedTime = System.currentTimeMillis();
            String traceInfo = new TraceUtils.TraceInfoBuilder()
                    .args(new String[]{characterName, characterUid})
                    .elapseTime(lastPackageReceivedTime - startTime.get())
                    .method("ChatApi::lastPackageReceived")
                    .status(TraceUtils.TraceStatus.FAILED)
                    .traceId(traceId)
                    .username(username)
                    .build();
            TraceUtils.getPerfLogger().trace(traceInfo);
        }).start();

        return sseEmitter;
    }

    @Operation(
            operationId = "listMessages",
            summary = "List Chat Messages",
            description = "List messages of a chat."
    )
    @GetMapping(value = {
            "/messages/{chatId}",
            "/messages/{chatId}/{limit}",
            "/messages/{chatId}/{limit}/{offset}"})
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> messages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Messages limit") @PathVariable("limit") Optional<Integer> limit,
            @Parameter(description = "Messages offset (from new to old)") @PathVariable("offset") Optional<Integer> offset) {
        int messagesLimit = limit.orElse(Integer.MAX_VALUE);
        int messagesOffset = Math.max(0, offset.orElse(0));

        var messages = chatMemoryService.listAllChatMessages(chatId);
        if (CollectionUtils.isEmpty(messages) || messages.size() <= messagesOffset) {
            return Collections.emptyList();
        }

        int end = messages.size() - messagesOffset;
        int start = Math.max(0, end - messagesLimit);

        if (start == 0) {
            LinkedList<ChatMessageRecord> records = new LinkedList<>(messages);
            // remove system message
            ChatMessageRecord firstRecord = records.removeFirst();
            --end;
            ChatSession session = chatSessionService.get(chatId);
            if (session != null) {
                // add greeting message
                String greeting = (String) session.getVariables().get(ChatVar.CHARACTER_GREETING.text());
                if (StringUtils.isNotBlank(greeting)) {
                    AiMessage greetingMessage = AiMessage.from(greeting);
                    firstRecord.setMessage(greetingMessage);
                    records.addFirst(firstRecord);
                    ++end;
                }
            }
            messages = records;
        }

        return messages.subList(start, end)
                .stream()
                .map(r -> ChatMessageRecordDTO.from(r, false))
                .toList();
    }

    @Operation(
            operationId = "listDebugMessages",
            summary = "List Chat Debug Messages",
            description = "List debug messages of a chat."
    )
    @GetMapping(value = {
            "/messages/debug/{chatId}",
            "/messages/debug/{chatId}/{limit}",
            "/messages/debug/{chatId}/{limit}/{offset}"})
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> debugMessages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Messages limit") @PathVariable("limit") Optional<Integer> limit,
            @Parameter(description = "Messages offset (from new to old)") @PathVariable("offset") Optional<Integer> offset) {
        int messagesLimit = limit.orElse(Integer.MAX_VALUE);
        int messagesOffset = Math.max(0, offset.orElse(0));

        var messages = chatMemoryService.listAllChatMessages(chatId);
        if (CollectionUtils.isEmpty(messages) || messages.size() <= messagesOffset) {
            return Collections.emptyList();
        }

        int end = messages.size() - messagesOffset;
        int start = Math.max(0, end - messagesLimit);

        if (start == 0) {
            LinkedList<ChatMessageRecord> records = new LinkedList<>(messages);
            // remove system message
            ChatMessageRecord firstRecord = records.removeFirst();
            --end;
            ChatSession session = chatSessionService.get(chatId);
            if (session != null) {
                // add greeting message
                String greeting = (String) session.getVariables().get(ChatVar.CHARACTER_GREETING.text());
                if (StringUtils.isNotBlank(greeting)) {
                    AiMessage greetingMessage = AiMessage.from(greeting);
                    firstRecord.setMessage(greetingMessage);
                    records.addFirst(firstRecord);
                    ++end;
                }
            }
            messages = records;
        }

        return messages.subList(start, end)
                .stream()
                .map(r -> ChatMessageRecordDTO.from(r, true))
                .toList();
    }

    @Operation(
            operationId = "rollbackMessages",
            summary = "Rollback Chat Messages",
            description = "Rollback messages of a chat."
    )
    @PostMapping("/messages/rollback/{chatId}/{count}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<Long> rollbackMessages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Message count to be rolled back") @PathVariable("count") @Positive Integer count) {
        return chatMemoryService.rollback(chatId, count);
    }

    @Operation(
            operationId = "getMemoryUsage",
            summary = "Get Memory Usage",
            description = "Get memory usage of a chat."
    )
    @GetMapping("/memory/usage/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public MemoryUsageDTO getMemoryUsage(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        return MemoryUsageDTO.from(chatMemoryService.usage(chatId));
    }

    @Operation(
            operationId = "sendAssistant",
            summary = "Send Assistant for Chat Message",
            description = "Send a message to assistant for a new chat message."
    )
    @GetMapping("/send/assistant/{chatId}/{assistantUid}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'chatAssistantDefaultOp')")
    public LlmResultDTO sendAssistant(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Assistant uid") @PathVariable("assistantUid") @NotBlank String assistantUid) {
        checkQuota(chatId);
        Response<AiMessage> response = chatService.sendAssistant(chatId, assistantUid);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send assistant to " + assistantUid);
        }

        return LlmResultDTO.from(response);
    }

    @Operation(
            operationId = "streamSendAssistant",
            summary = "Send Assistant for Chat Message by Streaming Back",
            description = "Refer to /api/v1/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response."
    )
    @GetMapping(value = "/send/stream/assistant/{chatId}/{assistantUid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'chatAssistantDefaultOp')")
    public SseEmitter streamSendAssistant(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Assistant uid") @PathVariable("assistantUid") @NotBlank String assistantUid) {
        checkQuota(chatId);
        SseEmitter sseEmitter = new SseEmitter();
        // avoid instruction reordering
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        TokenStream tokenStream = chatService.streamSendAssistant(chatId, assistantUid);

        if (tokenStream == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send assistant to " + assistantUid);
        }


        String traceId = TraceUtils.getTraceId();
        String username = AccountUtils.currentUser().getUsername();
        String assistantName = characterService.getNameByUid(assistantUid);
        AtomicBoolean firstPackageReceived = new AtomicBoolean(false);

        tokenStream.onNext(partialResult -> {
            if (!firstPackageReceived.get()) {
                TraceUtils.putTraceAttribute("traceId", traceId);
                TraceUtils.putTraceAttribute("username", username);
                long firstPackageReceivedTime = System.currentTimeMillis();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{assistantName, assistantUid})
                        .elapseTime(firstPackageReceivedTime - startTime.get())
                        .method("ChatApi::assistantFirstPackageReceived")
                        .response(partialResult)
                        .status(TraceUtils.TraceStatus.SUCCESSFUL)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
                firstPackageReceived.set(true);
            }
            try {
                LlmResultDTO result = LlmResultDTO.from(
                        partialResult, null, null, null);
                result.setRequestId(null);
                sseEmitter.send(result);
            } catch (IllegalStateException | NullPointerException | IOException e) {
                log.warn("Error when sending message.", e);
                sseEmitter.completeWithError(e);
                long lastPackageReceivedTime = System.currentTimeMillis();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{assistantName, assistantUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::assistantLastPackageReceived")
                        .response(partialResult)
                        .throwable(e)
                        .status(TraceUtils.TraceStatus.BAD_REQUEST)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            }
        }).onComplete(response -> {
            long lastPackageReceivedTime = System.currentTimeMillis();
            try {
                LlmResultDTO result = LlmResultDTO.from(response);
                Objects.requireNonNull(result).setText(null);
                result.setRequestId(null);
                sseEmitter.send(result);
                sseEmitter.complete();
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{assistantName, assistantUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::assistantLastPackageReceived")
                        .status(TraceUtils.TraceStatus.SUCCESSFUL)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            } catch (Exception e) {
                log.warn("Error when sending message.", e);
                sseEmitter.completeWithError(e);
                String traceInfo = new TraceUtils.TraceInfoBuilder()
                        .args(new String[]{assistantName, assistantUid})
                        .elapseTime(lastPackageReceivedTime - startTime.get())
                        .method("ChatApi::assistantLastPackageReceived")
                        .throwable(e)
                        .status(TraceUtils.TraceStatus.BAD_REQUEST)
                        .traceId(traceId)
                        .username(username)
                        .build();
                TraceUtils.getPerfLogger().trace(traceInfo);
            }
        }).onError(ex -> {
            log.error("SSE exception.", ex);
            sseEmitter.completeWithError(ex);
            long lastPackageReceivedTime = System.currentTimeMillis();
            String traceInfo = new TraceUtils.TraceInfoBuilder()
                    .args(new String[]{assistantName, assistantUid})
                    .elapseTime(lastPackageReceivedTime - startTime.get())
                    .method("ChatApi::assistantLastPackageReceived")
                    .status(TraceUtils.TraceStatus.FAILED)
                    .traceId(traceId)
                    .username(username)
                    .build();
            TraceUtils.getPerfLogger().trace(traceInfo);
        }).start();

        return sseEmitter;
    }
}
