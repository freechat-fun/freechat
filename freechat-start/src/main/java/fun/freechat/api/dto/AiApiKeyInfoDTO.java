package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.service.ai.MaskedAiApiKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Model credential information")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiApiKeyInfoDTO extends TraceableDTO {
    @Schema(description = "Credential identifier")
    private Long id;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Last use time")
    private Date gmtUsed;
    @Schema(description = "Credential name")
    private String name;
    @Schema(description = "Model provider: hugging_face | open_ai | azure_open_ai | local_ai | in_process | dash_scope | unknown")
    private String provider;
    @Schema(description = "Credential content")
    private String token;
    @Schema(description = "Whether to enable")
    private Boolean enabled;
    @Schema(description = "Credential owner")
    private String username;

    public static AiApiKeyInfoDTO from(MaskedAiApiKey apiKey) {
        if (Objects.isNull(apiKey)) {
            return null;
        }
        AiApiKeyInfoDTO dto = CommonUtils.convert(apiKey, AiApiKeyInfoDTO.class);
        dto.setEnabled(apiKey.getEnabled() != (byte)0);
        dto.setUsername(AccountUtils.userIdToName(apiKey.getUserId()));
        return dto;
    }
}
