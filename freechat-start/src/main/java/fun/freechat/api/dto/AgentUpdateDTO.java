package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AgentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating agent information")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgentUpdateDTO extends AgentCreateDTO {
    public Triple<AgentInfo, List<String>, List<String>> toAgentInfo(Long agentId) {
        AgentInfo agentInfo = CommonUtils.convert(this, AgentInfo.class);
        agentInfo.setAgentId(agentId);
        agentInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        return Triple.of(agentInfo, getTags(), getAiModels());
    }
}
