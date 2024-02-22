package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.ChatContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Chat context")
@Data
public class ChatContextDTO extends TraceableDTO {
    @Schema(description = "Chat identifier")
    private String chatId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Read time")
    private Date gmtRead;
    @Schema(description = "User nickname for this session")
    private String userNickname;
    @Schema(description = "User profile for this session")
    private String userProfile;
    @Schema(description = "Character nickname for this session")
    private String characterNickname;
    @Schema(description = "Anything about this session")
    private String about;
    @Schema(description = "Character backend for this session", requiredMode = REQUIRED)
    private String backendId;
    @Schema(description = "Extra info for this session")
    private String ext;

    public static ChatContextDTO from(ChatContext chatContext) {
        if (Objects.isNull(chatContext)) {
            return null;
        }

        return CommonUtils.convert(chatContext, ChatContextDTO.class);
    }
}
