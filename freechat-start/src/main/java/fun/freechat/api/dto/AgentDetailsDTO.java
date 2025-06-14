package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AgentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Schema(description = "Agent detailed content")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgentDetailsDTO extends AgentSummaryDTO {
    @Schema(description = "Agent configuration")
    private String config;
    @Schema(description = "Agent example")
    private String example;
    @Schema(description = "Agent parameters, JSON format")
    private String parameters;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Draft content")
    private String draft;

    public static AgentDetailsDTO from(
            Triple<AgentInfo, List<String>, List<String>> agentInfoTriple) {
        if (agentInfoTriple == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find agent!");
        }
        AgentDetailsDTO dto =
                CommonUtils.convert(agentInfoTriple.getLeft(), AgentDetailsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(agentInfoTriple.getLeft().getUserId()));
        dto.setTags(agentInfoTriple.getMiddle());
        dto.setAiModels(agentInfoTriple.getRight()
                .stream()
                .map(AiModelInfoDTO::from)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
