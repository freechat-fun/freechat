package fun.freechat.api.admin;

import fun.freechat.api.dto.tg.TgMessageDTO;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Telegram Manager (for admin)", description = "Inspect Telegram chat bindings and message history.")
@RequestMapping("/api/v2/admin/chat/tg")
@Validated
@SuppressWarnings("unused")
public class TelegramManagerApi {

    @Autowired
    private TgChatBindingService tgChatBindingService;

    @Autowired
    private TgMessageService tgMessageService;

    @Operation(
            operationId = "findTelegramChat",
            summary = "Find Telegram Chat",
            description = "Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.")
    @GetMapping(value = "/{backendId}/{tgChatId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String findTelegramChat(
            @Parameter(description = "Character backend identifier") @PathVariable("backendId") @NotBlank
                    String backendId,
            @Parameter(description = "Telegram chat id") @PathVariable("tgChatId") @NotNull Long tgChatId) {
        String chatId = tgChatBindingService.findChatId(backendId, tgChatId);
        if (StringUtils.isBlank(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No telegram chat bound.");
        }
        return chatId;
    }

    @Operation(
            operationId = "listTelegramMessages",
            summary = "List Telegram Messages",
            description = "List Telegram messages recorded against the given tg_chat.chat_id, newest first.")
    @GetMapping("/messages/{chatId}")
    public List<TgMessageDTO> listTelegramMessages(
            @Parameter(description = "tg_chat.chat_id") @PathVariable("chatId") @NotBlank String chatId,
            @Parameter(description = "Max rows to return (default 100)")
                    @RequestParam(value = "limit", required = false)
                    Integer limit,
            @Parameter(description = "Row offset (default 0)") @RequestParam(value = "offset", required = false)
                    Integer offset) {
        return tgMessageService.listByChat(chatId, limit, offset).stream()
                .map(TgMessageDTO::from)
                .toList();
    }
}
