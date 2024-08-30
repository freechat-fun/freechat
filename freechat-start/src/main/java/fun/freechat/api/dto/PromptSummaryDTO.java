package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Schema(description = "Prompt summary content")
@Data
@EqualsAndHashCode(callSuper = true)
public class PromptSummaryDTO extends TraceableDTO {
    @Schema(description = "Prompt identifier, will change after publish")
    private Long promptId;
    @Schema(description = "Prompt immutable identifier")
    private String promptUid;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Referenced prompt")
    private String parentUid;
    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;
    @Schema(description = "Version")
    private Integer version;
    @Schema(description = "Prompt name")
    private String name;
    @Schema(description = "Prompt type: string | chat")
    private String type;
    @Schema(description = "Prompt description (50 characters, the excess part is represented by ellipsis)")
    private String description;
    @Schema(description = "Prompt format: mustache (default) | f_string")
    private String format;
    @Schema(description = "Prompt language: en (default) | zh | ...")
    private String lang;
    @Schema(description = "Prompt owner")
    private String username;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set")
    private List<AiModelInfoDTO> aiModels;

    public static PromptSummaryDTO from(Triple<PromptInfo, List<String>, List<String>> promptInfoTriple) {
        if (promptInfoTriple == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find prompt!");
        }
        PromptSummaryDTO dto =
                CommonUtils.convert(promptInfoTriple.getLeft(), PromptSummaryDTO.class);
        dto.setUsername(AccountUtils.userIdToName(promptInfoTriple.getLeft().getUserId()));
        dto.setTags(promptInfoTriple.getMiddle());
        dto.setAiModels(promptInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
