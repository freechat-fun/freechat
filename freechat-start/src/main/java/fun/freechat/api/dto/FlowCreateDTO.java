package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.FlowInfo;
import fun.freechat.service.enums.FlowFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating new flow information")
@Data
public class FlowCreateDTO {
    @Schema(description = "Referenced flow")
    private String parentId;
    @Schema(description = "Visibility: private (default), public, public_org, hidden")
    private String visibility;
    @Schema(description = "Flow format, currently supported: langflow")
    private String format;
    @Schema(description = "Flow name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Flow description")
    private String description;
    @Schema(description = "Flow configuration")
    private String config;
    @Schema(description = "Flow example")
    private String example;
    @Schema(description = "Flow parameters, JSON format")
    private String parameters;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Draft content")
    private String draft;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set, empty means no model limit")
    private List<String> aiModels;

    public Triple<FlowInfo, List<String>, List<String>> toFlowInfo() {
        FlowInfo flowInfo = CommonUtils.convert(this, FlowInfo.class);
        flowInfo.setFlowId(null);
        flowInfo.setVersion(0);
        flowInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        if (StringUtils.isBlank(flowInfo.getFormat())) {
            flowInfo.setFormat(FlowFormat.LANGFLOW.text());
        }
        return Triple.of(flowInfo, getTags(), getAiModels());
    }
}
