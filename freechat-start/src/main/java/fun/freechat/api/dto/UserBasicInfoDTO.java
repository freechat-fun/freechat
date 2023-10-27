package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "User summary information")
@Data
public class UserBasicInfoDTO extends TraceableDTO{
    @Schema(description = "Username")
    private String username;
    @Schema(description = "Nickname")
    private String nickname;
    @Schema(description = "Avatar")
    private String picture;

    public static UserBasicInfoDTO from(User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        return CommonUtils.convert(user, UserBasicInfoDTO.class);
    }
}
