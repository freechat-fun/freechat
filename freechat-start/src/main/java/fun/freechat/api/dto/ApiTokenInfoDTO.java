package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.service.account.MaskedApiToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Objects;

@Schema(description = "API token information")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiTokenInfoDTO extends TraceableDTO {
    @Schema(description = "Token identifier")
    private Long id;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Token type")
    private String type;
    @Schema(description = "Token issuance time")
    private Date issuedAt;
    @Schema(description = "Token expiration time")
    private Date expiresAt;
    @Schema(description = "Token content")
    private String token;
    @Schema(description = "Token policy")
    private String policy;
    @Schema(description = "Token owner")
    private String username;

    public static ApiTokenInfoDTO from(MaskedApiToken apiToken) {
        if (apiToken == null) {
            return null;
        }
        ApiTokenInfoDTO dto = CommonUtils.convert(apiToken, ApiTokenInfoDTO.class);
        dto.setUsername(AccountUtils.userIdToName(apiToken.getUserId()));
        return dto;
    }
}
