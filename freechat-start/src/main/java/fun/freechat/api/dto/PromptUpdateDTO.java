package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptInfo;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating prompt information")
@Data
@EqualsAndHashCode(callSuper = true)
public class PromptUpdateDTO extends PromptCreateDTO {
    public Triple<PromptInfo, List<String>, List<String>> toPromptInfo(Long promptId) {
        PromptInfo promptInfo = CommonUtils.convert(this, PromptInfo.class);
        promptInfo.setPromptId(promptId);
        promptInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        if (StringUtils.isBlank(getTemplate()) && getChatTemplate() != null) {
            try {
                promptInfo.setTemplate(
                        InfoUtils.defaultMapper().writeValueAsString(
                                getChatTemplate().toChatPromptContent()));
                promptInfo.setType(PromptType.CHAT.text());
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        return Triple.of(promptInfo, getTags(), getAiModels());
    }
}
