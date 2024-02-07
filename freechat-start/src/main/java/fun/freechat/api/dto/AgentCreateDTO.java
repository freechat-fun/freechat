package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AgentInfo;
import fun.freechat.service.enums.AgentFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating new agent information")
@Data
public class AgentCreateDTO {
    @Schema(description = "Referenced agent")
    private String parentId;
    @Schema(description = "Visibility: private (default), public, public_org, hidden")
    private String visibility;
    @Schema(description = "Agent format, currently supported: langflow")
    private String format;
    @Schema(description = "Agent name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Agent description")
    private String description;
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
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set, empty means no model limit")
    private List<String> aiModels;

    public Triple<AgentInfo, List<String>, List<String>> toAgentInfo() {
        AgentInfo agentInfo = CommonUtils.convert(this, AgentInfo.class);
        agentInfo.setAgentId(null);
        agentInfo.setVersion(0);
        agentInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        if (StringUtils.isBlank(agentInfo.getFormat())) {
            agentInfo.setFormat(AgentFormat.LANGFLOW.text());
        }
        return Triple.of(agentInfo, getTags(), getAiModels());
    }
}
