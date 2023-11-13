package fun.freechat.api;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import fun.freechat.api.dto.ChatContentDTO;
import fun.freechat.api.dto.ChatCreateDTO;
import fun.freechat.api.dto.ChatMessageDTO;
import fun.freechat.api.dto.LlmResultDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.character.CharacterAiService;
import fun.freechat.service.character.ChatMemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@Tag(name = "Character")
@RequestMapping("/api/v1/character")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class CharacterAiApi {
    @Autowired
    private CharacterAiService characterAiService;

    @Autowired
    private ChatMemoryService chatMemoryService;

    @Operation(
            operationId = "startChat",
            summary = "Start Chat Session",
            description = "Start a chat session."
    )
    @PostMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.backendId, 'chatCreateOp')")
    public String start(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parameters for starting a chat session")
            @RequestBody
            @NotNull
            ChatCreateDTO chatCreateParams) {
        String chatId = characterAiService.start(AccountUtils.currentUser(),
                chatCreateParams.getUserNickname(),
                chatCreateParams.getUserProfile(),
                chatCreateParams.getCharacterNickname(),
                chatCreateParams.getBackendId(),
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
    @DeleteMapping("/chat/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public Boolean delete(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId) {
        return characterAiService.delete(chatId);
    }

    @Operation(
            operationId = "sendMessage",
            summary = "Send Chat Message",
            description = "Send a chat message to character."
    )
    @PostMapping("/chat/send/{chatId}")
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public LlmResultDTO send(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat content") @RequestBody @NotNull ChatContentDTO chatContent) {
        Response<ChatMessage> response = characterAiService.send(
                chatId, chatContent.getContent(), chatContent.getContext(), chatContent.getAttachment());

        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to chat by " + chatId);
        }

        return LlmResultDTO.from(response);
    }

    @Operation(
            operationId = "streamSendMessage",
            summary = "Send Chat Message by Streaming Back",
            description = "Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response."
    )
    @PostMapping(value = "/chat/send/stream/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public SseEmitter streamSend(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Chat content") @RequestBody @NotNull ChatContentDTO chatContent) {
        SseEmitter sseEmitter = new SseEmitter();
        TokenStream tokenStream = characterAiService.streamSend(
                chatId, chatContent.getContent(), chatContent.getContext(), chatContent.getAttachment());

        if (Objects.isNull(tokenStream)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to chat by " + chatId);
        }

        tokenStream.onNext(partialResult -> {
            try {
                LlmResultDTO result = LlmResultDTO.from(
                        partialResult, null, null, null);
                result.setRequestId(null);
                sseEmitter.send(result);
            } catch (NullPointerException | IOException e) {
                sseEmitter.completeWithError(e);
            }
        }).onComplete(response -> {
            try {
                LlmResultDTO result = LlmResultDTO.from(response);
                Objects.requireNonNull(result).setText(null);
                result.setRequestId(null);
                sseEmitter.send(result);
                sseEmitter.complete();
            } catch (NullPointerException | IOException e) {
                sseEmitter.completeWithError(e);
            }
        }).onError(sseEmitter::completeWithError).start();

        return sseEmitter;
    }

    @Operation(
            operationId = "listMessages",
            summary = "List Chat Messages",
            description = "List messages of a chat."
    )
    @GetMapping(value = {
            "/chat/messages/{chatId}",
            "/chat/messages/{chatId}/{limit}",
            "/chat/messages/{chatId}/{limit}/{offset}"})
    @PreAuthorize("hasPermission(#p0, 'chatDefaultOp')")
    public List<ChatMessageDTO> messages(
            @Parameter(description = "Chat session identifier") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Messages limit") @PathVariable("limit") @Positive Optional<Integer> limit,
            @Parameter(description = "Messages offset (from new to old)") @PathVariable("offset") @PositiveOrZero Optional<Integer> offset) {
        int messagesLimit = limit.orElse(Integer.MAX_VALUE);
        int messagesOffset = offset.orElse(0);

        var messages = chatMemoryService.listChatMessages(chatId);
        if (CollectionUtils.isEmpty(messages) || messages.size() <= messagesOffset) {
            return Collections.emptyList();
        }

        int end = messages.size() - messagesOffset;
        int start = Math.max(0, end - messagesLimit);

        return messages.subList(start, end)
                .stream()
                .map(ChatMessageDTO::from)
                .toList();
    }
}
