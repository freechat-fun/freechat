package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.FlowInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating flow information")
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowUpdateDTO extends FlowCreateDTO {
    public Triple<FlowInfo, List<String>, List<String>> toFlowInfo(String flowId) {
        FlowInfo flowInfo = CommonUtils.convert(this, FlowInfo.class);
        flowInfo.setFlowId(flowId);
        flowInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        return Triple.of(flowInfo, getTags(), getAiModels());
    }
}
