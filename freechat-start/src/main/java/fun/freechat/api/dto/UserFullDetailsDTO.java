package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Account detailed information (including password)")
@Data
public class UserFullDetailsDTO extends UserDetailsDTO{
    private String password;
}
