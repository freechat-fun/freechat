package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Account detailed information (including password)")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserFullDetailsDTO extends UserDetailsDTO{
    private String password;
}
