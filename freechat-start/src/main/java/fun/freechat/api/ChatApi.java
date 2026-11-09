package fun.freechat.api;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.api.dto.ChatCreateDTO;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.api.dto.ChatMessageRecordDTO;
import fun.freechat.api.dto.ChatRunEventDTO;
import fun.freechat.api.dto.ChatSessionDTO;
import fun.freechat.api.dto.ChatUpdateDTO;
import fun.freechat.api.dto.LlmResultDTO;
import fun.freechat.api.dto.MemoryUsageDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.PromptTask;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.chat.ChatRunEvent;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.memory.MemoryBounds.PreparationDeferredException;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.QuotaType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.util.TraceUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Tag(name = "Chat")
@RequestMapping("/api/v2/chat")
@Validated
@Slf4j
@SuppressWarnings("unused")
public class ChatApi {
    private static final long SSE_TIMEOUT = 600 * 1000L;

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

    @ExceptionHandler(PreparationDeferredException.class)
    public ResponseEntity<Map<String, Object>> memoryPreparationDeferred() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.RETRY_AFTER, "1")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(memoryPreparationError());
    }

    private static Map<String, Object> memoryPreparationError() {
        return Map.of(
                "code", 503,
                "errorCode", "CHAT_MEMORY_PREPARING",
                "message", "Conversation memory is still being prepared. Please retry your message.");
    }

    private SseEmitter streamRun(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            String characterUid,
            boolean assistant,
            java.util.function.Function<java.util.function.Consumer<ChatRunEvent>, ChatRunHandle> start) {
        long startTime = System.currentTimeMillis();
        String traceId = TraceUtils.getTraceId();
        String username = AccountUtils.currentUser().getUsername();
        String characterName;
        try {
            characterName = characterService.getNameByUid(characterUid);
            servletResponse.setHeader("X-Accel-Buffering", "no");
            servletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        } catch (RuntimeException failure) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chat stream initialization failed");
        }
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        AtomicReference<ChatRunHandle> handle = new AtomicReference<>();
        AtomicBoolean disconnected = new AtomicBoolean();
        AtomicBoolean first = new AtomicBoolean();
        AtomicBoolean last = new AtomicBoolean();
        AtomicBoolean terminal = new AtomicBoolean();
        String firstMethod = assistant ? "ChatApi::assistantFirstPackageReceived" : "ChatApi::firstPackageReceived";
        String lastMethod = assistant ? "ChatApi::assistantLastPackageReceived" : "ChatApi::lastPackageReceived";
        java.util.function.Consumer<TraceUtils.TraceStatus> lastTrace = status -> {
            if (last.compareAndSet(false, true)) {
                tracePackage(lastMethod, status, startTime, traceId, username, characterName, characterUid);
            }
        };
        Runnable cancel = () -> {
            disconnected.set(true);
            ChatRunHandle current = handle.getAndSet(null);
            if (current != null) current.cancel();
            lastTrace.accept(TraceUtils.TraceStatus.BAD_REQUEST);
        };
        Runnable onCompletion = () -> {
            if (!terminal.get()) cancel.run();
        };
        Runnable deliveryFailed = () -> {
            disconnected.set(true);
            try {
                emitter.completeWithError(new IllegalStateException("Chat response delivery failed"));
            } finally {
                cancel.run();
            }
        };
        emitter.onTimeout(cancel);
        emitter.onError(ignored -> cancel.run());
        emitter.onCompletion(onCompletion);
        // MVC starts DeferredResult processing before flushing earlySendAttempts, but installs
        // emitter callbacks only after that flush. Cover the HTTP lifecycle independently.
        WebAsyncUtils.getAsyncManager(servletRequest)
                .registerDeferredResultInterceptor(emitter, new DeferredResultProcessingInterceptor() {
                    @Override
                    public <T> void postProcess(
                            NativeWebRequest request, DeferredResult<T> result, Object concurrentResult) {
                        // DeferredResult's own handleError short-circuits later handleError interceptors.
                        if (concurrentResult instanceof Throwable) cancel.run();
                    }

                    @Override
                    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> result) {
                        onCompletion.run();
                    }
                });
        ChatRunHandle admitted = start.apply(event -> {
            if (disconnected.get()) throw new IllegalStateException("Chat response delivery failed");
            boolean ending = event.event().equals("run.completed")
                    || event.event().equals("run.failed")
                    || event.event().equals("run.cancelled");
            if ((event.event().equals("message.delta") || event.event().equals("reasoning.delta"))
                    && first.compareAndSet(false, true)) {
                tracePackage(
                        firstMethod,
                        TraceUtils.TraceStatus.SUCCESSFUL,
                        startTime,
                        traceId,
                        username,
                        characterName,
                        characterUid);
            }
            try {
                // Spring's SSE handler flushes each send; queued is available before any model content.
                emitter.send(SseEmitter.event()
                        .name(event.event())
                        .data(ChatRunEventDTO.from(event), MediaType.APPLICATION_JSON));
                if (ending) {
                    terminal.set(true);
                    lastTrace.accept(
                            event.event().equals("run.completed")
                                    ? TraceUtils.TraceStatus.SUCCESSFUL
                                    : TraceUtils.TraceStatus.FAILED);
                    emitter.complete();
                }
            } catch (IOException | RuntimeException failure) {
                deliveryFailed.run();
                // The run projector treats this as delivery failure, not a model failure.
                throw new IllegalStateException("Chat response delivery failed");
            }
        });
        handle.set(admitted);
        if (disconnected.get()) cancel.run();
        admitted.eventsDelivered().whenComplete((ignored, failure) -> {
            if (failure != null) deliveryFailed.run();
            // Success is not an SSE terminal signal; only the explicit terminal event completes it.
        });
        return emitter;
    }

    private static void tracePackage(
            String method,
            TraceUtils.TraceStatus status,
            long startTime,
            String traceId,
            String username,
            String characterName,
            String characterUid) {
        TraceUtils.putTraceAttribute("traceId", traceId);
        TraceUtils.putTraceAttribute("username", username);
        TraceUtils.getPerfLogger()
                .trace(new TraceUtils.TraceInfoBuilder()
                        .args(new String[] {characterName, characterUid})
                        .elapseTime(System.currentTimeMillis() - startTime)
                        .method(method)
                        .status(status)
                        .traceId(traceId)
                        .username(username)
                        .build());
    }

    private void checkQuotaValue(long usage, long limit) {
        if (usage >= limit) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "Usage " + usage + " exceeds limit " + limit);
        }
    }

    private void checkQuota(String chatId) {
        ChatContext context = chatContextService.get(chatId);

        if (StringUtils.isNotBlank(context.getApiKeyName()) || StringUtils.isNotBlank(context.getApiKeyValue())) {
            // use the chatter's own credentials
            return;
        }

        var durableUsage = chatMemoryService.usage(chatId);
        Long limit = Utils.getOrDefault(context.getQuota(), 0L);
        QuotaType quotaType = QuotaType.of(context.getQuotaType());
        if (Objects.requireNonNull(quotaType) == QuotaType.MESSAGES) {
            checkQuotaValue(Utils.getOrDefault(durableUsage.messageUsage(), 0L), limit);
        } else if (quotaType == QuotaType.TOKENS) {
            Integer usage = durableUsage.tokenUsage() == null
                    ? 0
                    : Utils.getOrDefault(durableUsage.tokenUsage().totalTokenCount(), 0);
            checkQuotaValue(usage.longValue(), limit);
        }
    }

    @Operation(operationId = "startChat", summary = "Start Chat Session", description = "Start a chat session.")
    @PostMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.characterUid, 'chatCreateOp')")
    public String start(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Parameters for starting a chat session")
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

        String chatId = chatService.start(
                AccountUtils.currentUser(),
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

    @Operation(operationId = "deleteChat", summary = "Delete Chat Session", description = "Delete the chat session.")
    @DeleteMapping("/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public Boolean delete(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        return chatService.delete(chatId);
    }

    @Operation(operationId = "listChats", summary = "List Chats", description = "List chats of current user.")
    @GetMapping("")
    public List<ChatSessionDTO> list() {
        return chatService.list(AccountUtils.currentUser()).stream()
                .map(chatInfo -> {
                    ChatContext chatContext = chatInfo.getLeft();
                    CharacterInfo characterInfo = chatInfo.getMiddle();
                    String chatOwner = chatContextService.getChatOwner(chatContext.getChatId());
                    String characterOwner = chatContextService.getCharacterOwner(chatContext.getChatId());
                    String currentUserId = AccountUtils.currentUser().getUserId();
                    Boolean isDebugEnabled = currentUserId.equals(chatOwner) && currentUserId.equals(characterOwner);
                    String backendId = chatContext.getBackendId();
                    Optional<CharacterBackend> backend =
                            Optional.ofNullable(chatContext.getBackendId()).map(characterService::getBackend);

                    String provider = backend.map(CharacterBackend::getChatPromptTaskId)
                            .map(promptTaskService::get)
                            .map(PromptTask::getModelId)
                            .map(InfoUtils::toAiModelInfo)
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
                            case PUBLIC_ORG ->
                                orgService.hasRelationship(
                                                AccountUtils.currentUser().getUserId(), characterOwner)
                                        ? "online"
                                        : "offline";
                            case PRIVATE -> BooleanUtils.isTrue(isDebugEnabled) ? "invisible" : "offline";
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
                    return ChatSessionDTO.from(
                            chatInfo,
                            provider,
                            proactiveChatWaitingTime,
                            senderStatus,
                            isDebugEnabled,
                            isCustomizedApiKeyEnabled,
                            backend.map(CharacterBackend::getEnableTts)
                                    .map(b -> b == (byte) 1)
                                    .orElse(false));
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Operation(
            operationId = "getDefaultChatId",
            summary = "Get Default Chat",
            description = "Get default chat id of current user and the character.")
    @GetMapping(value = "/{characterUid}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getDefault(
            @Parameter(description = "Character uid") @PathVariable("characterUid") @NotBlank String characterUid) {
        return chatService.getDefaultChatId(AccountUtils.currentUser(), characterUid);
    }

    @Operation(operationId = "clearMemory", summary = "Clear Memory", description = "Clear memory of the chat session.")
    @DeleteMapping("/memory/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> clearMemory(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        chatService.clearMemory(chatId);
        return messages(chatId, Optional.empty(), Optional.empty());
    }

    @Operation(operationId = "updateChat", summary = "Update Chat Session", description = "Update the chat session.")
    @PutMapping("/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public Boolean update(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "The chat session information to be updated")
                    @RequestBody
                    @NotNull
                    ChatUpdateDTO chatUpdate) {
        return chatContextService.update(chatUpdate.toChatContext(chatId));
    }

    @Operation(
            operationId = "sendMessage",
            summary = "Send Chat Message",
            description = "Send a chat message to character.")
    @PostMapping("/send/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public LlmResultDTO send(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat message") @RequestBody @NotNull
                    ChatMessageDTO chatMessage) {
        checkQuota(chatId);
        Pair<ChatResponse, Long> response =
                chatService.send(chatId, chatMessage.toChatMessage(), chatMessage.getContext());

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to chat by " + chatId);
        }

        return LlmResultDTO.from(response.getLeft(), response.getRight());
    }

    @Operation(
            operationId = "streamSendMessage",
            summary = "Send Chat Message by Streaming Back",
            description = "Streams versioned Hermes-style FreeChat run, message, reasoning and tool events. "
                    + "run.completed, run.failed or run.cancelled terminates the stream. "
                    + "Replaces the previous LlmResultDTO-only SSE contract.")
    @PostMapping(value = "/send/stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public SseEmitter streamSend(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat message") @RequestBody @NotNull
                    ChatMessageDTO chatMessage,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        checkQuota(chatId);
        // Conversion/validation remains an HTTP preflight error, before run admission.
        var message = chatMessage.toChatMessage();
        String characterUid = chatContextService.getCharacterUid(chatId);
        return streamRun(
                servletRequest,
                servletResponse,
                characterUid,
                false,
                observer -> chatService.startRun(chatId, message, chatMessage.getContext(), observer));
    }

    @Operation(operationId = "listMessages", summary = "List Chat Messages", description = "List messages of a chat.")
    @GetMapping(value = {"/messages/{chatId}", "/messages/{chatId}/{limit}", "/messages/{chatId}/{limit}/{offset}"})
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> messages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Messages limit") @PathVariable("limit") Optional<Integer> limit,
            @Parameter(description = "Messages offset (from new to old)") @PathVariable("offset")
                    Optional<Integer> offset) {
        int messagesLimit = limit.orElse(Integer.MAX_VALUE);
        int messagesOffset = Math.max(0, offset.orElse(0));

        var messages = chatMemoryService.listAllChatMessages(chatId).stream()
                .filter(message -> {
                    ChatMessageType type = message.getMessage().type();
                    return type == ChatMessageType.SYSTEM
                            || type == ChatMessageType.USER
                            || (type == ChatMessageType.AI
                                    && !((AiMessage) message.getMessage()).hasToolExecutionRequests());
                })
                .toList();
        if (CollectionUtils.isEmpty(messages) || messages.size() <= messagesOffset) {
            return Collections.emptyList();
        }

        int end = messages.size() - messagesOffset;
        int start = Math.max(0, end - messagesLimit);

        if (start == 0
                && !messages.isEmpty()
                && messages.getFirst().getMessage().type() == ChatMessageType.SYSTEM) {
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

        return messages.subList(start, end).stream()
                .map(r -> ChatMessageRecordDTO.from(r, false))
                .toList();
    }

    @Operation(
            operationId = "listDebugMessages",
            summary = "List Chat Debug Messages",
            description = "List debug messages of a chat.")
    @GetMapping(
            value = {
                "/messages/debug/{chatId}",
                "/messages/debug/{chatId}/{limit}",
                "/messages/debug/{chatId}/{limit}/{offset}"
            })
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageRecordDTO> debugMessages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Messages limit") @PathVariable("limit") Optional<Integer> limit,
            @Parameter(description = "Messages offset (from new to old)") @PathVariable("offset")
                    Optional<Integer> offset) {
        int messagesLimit = limit.orElse(Integer.MAX_VALUE);
        int messagesOffset = Math.max(0, offset.orElse(0));

        var messages = chatMemoryService.listAllChatMessages(chatId).stream()
                .filter(message -> {
                    ChatMessageType type = message.getMessage().type();
                    return type == ChatMessageType.SYSTEM
                            || type == ChatMessageType.USER
                            || (type == ChatMessageType.AI
                                    && !((AiMessage) message.getMessage()).hasToolExecutionRequests());
                })
                .toList();
        if (CollectionUtils.isEmpty(messages) || messages.size() <= messagesOffset) {
            return Collections.emptyList();
        }

        int end = messages.size() - messagesOffset;
        int start = Math.max(0, end - messagesLimit);

        if (start == 0
                && !messages.isEmpty()
                && messages.getFirst().getMessage().type() == ChatMessageType.SYSTEM) {
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

        return messages.subList(start, end).stream()
                .map(r -> ChatMessageRecordDTO.from(r, true))
                .toList();
    }

    @Operation(
            operationId = "rollbackMessages",
            summary = "Rollback Chat Messages",
            description = "Rollback messages of a chat.")
    @PostMapping("/messages/rollback/{chatId}/{count}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<Long> rollbackMessages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Message count to be rolled back") @PathVariable("count") @Positive
                    Integer count) {
        return chatMemoryService.rollback(chatId, count);
    }

    @Operation(
            operationId = "getMemoryUsage",
            summary = "Get Memory Usage",
            description = "Get memory usage of a chat.")
    @GetMapping("/memory/usage/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public MemoryUsageDTO getMemoryUsage(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        return MemoryUsageDTO.from(chatMemoryService.usage(chatId));
    }

    @Operation(
            operationId = "sendAssistant",
            summary = "Send Assistant for Chat Message",
            description = "Send a message to assistant for a new chat message.")
    @GetMapping("/send/assistant/{chatId}/{assistantUid}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'chatAssistantDefaultOp')")
    public LlmResultDTO sendAssistant(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Assistant uid") @PathVariable("assistantUid") @NotBlank String assistantUid) {
        checkQuota(chatId);
        ChatResponse response = chatService.sendAssistant(chatId, assistantUid);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send assistant to " + assistantUid);
        }

        return LlmResultDTO.from(response, null);
    }

    @Operation(
            operationId = "streamSendAssistant",
            summary = "Send Assistant for Chat Message by Streaming Back",
            description = "Streams assistant suggestions using the versioned Hermes-style FreeChat run event contract. "
                    + "run.completed, run.failed or run.cancelled terminates the stream. "
                    + "Replaces the previous LlmResultDTO-only SSE contract.")
    @GetMapping(value = "/send/stream/assistant/{chatId}/{assistantUid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0 + '|' + #p1, 'chatAssistantDefaultOp')")
    public SseEmitter streamSendAssistant(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Assistant uid") @PathVariable("assistantUid") @NotBlank String assistantUid,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        checkQuota(chatId);
        return streamRun(
                servletRequest,
                servletResponse,
                assistantUid,
                true,
                observer -> chatService.startAssistantRun(chatId, assistantUid, observer));
    }
}
