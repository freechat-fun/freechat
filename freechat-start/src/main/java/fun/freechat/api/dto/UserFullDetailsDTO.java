package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Schema(description = "Account detailed information (including password)")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserFullDetailsDTO extends UserDetailsDTO{
    private String password;

    public static UserFullDetailsDTO from(User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        UserFullDetailsDTO dto = CommonUtils.convert(user, UserFullDetailsDTO.class);
        dto.setLocked(user.getLocked() != (byte)0);
        dto.setEnabled(user.getEnabled() != (byte)0);
        return dto;
    }
}
