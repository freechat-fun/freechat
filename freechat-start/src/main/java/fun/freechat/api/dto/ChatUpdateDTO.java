package fun.freechat.api.dto;

import fun.freechat.model.ChatContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "Request data for updating a chat session")
@Data
public class ChatUpdateDTO extends ChatCreateDTO {
    @Schema(description = "Read time")
    private Date gmtRead;

    public ChatContext toChatContext(String chatId) {
        return new ChatContext()
                .withChatId(chatId)
                .withExt(getExt())
                .withAbout(getAbout())
                .withBackendId(getBackendId())
                .withCharacterNickname(getCharacterNickname())
                .withUserNickname(getUserNickname())
                .withUserProfile(getUserProfile())
                .withGmtRead(getGmtRead());
    }
}
