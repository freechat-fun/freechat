package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptInfo;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating new prompt information")
@Data
public class PromptCreateDTO {
    @Schema(description = "Referenced prompt")
    private String parentUid;
    @Schema(description = "Visibility: private (default), public, public_org, hidden")
    private String visibility;
    @Schema(description = "Prompt name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Prompt description")
    private String description;
    @Schema(description = "Prompt text template content, choose one from template and chatTemplate field, priority: template > chatTemplate")
    private String template;
    @Schema(description = "Prompt chat template content")
    private ChatPromptContentDTO chatTemplate;
    @Schema(description = "Prompt format: mustache (default) | f_string")
    private String format;
    @Schema(description = "Prompt language: en (default) | zh_CN | ...")
    private String lang;
    @Schema(description = "Prompt example")
    private String example;
    @Schema(description = "Prompt parameters, JSON format")
    private String inputs;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Draft content")
    private String draft;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set, empty means no model limit")
    private List<String> aiModels;

    public Triple<PromptInfo, List<String>, List<String>> toPromptInfo() {
        PromptInfo promptInfo = CommonUtils.convert(this, PromptInfo.class);
        promptInfo.setPromptId(null);
        promptInfo.setVersion(0);
        promptInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        promptInfo.setVisibility(Visibility.of(getVisibility()).text());
        promptInfo.setFormat(PromptFormat.of(getFormat()).text());
        if (StringUtils.isBlank(getTemplate()) && Objects.nonNull(getChatTemplate())) {
            try {
                promptInfo.setTemplate(
                        InfoUtils.defaultMapper().writeValueAsString(
                                getChatTemplate().toChatPromptContent()));
                promptInfo.setType(PromptType.CHAT.text());
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            promptInfo.setType(PromptType.STRING.text());
        }
        return Triple.of(promptInfo, getTags(), getAiModels());
    }

    public static PromptCreateDTO from(Triple<PromptInfo, List<String>, List<String>> promptInfoTriple) {
        if (Objects.isNull(promptInfoTriple) || Objects.isNull(promptInfoTriple.getLeft())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prompt info should not be null.");
        }
        PromptInfo promptInfo = promptInfoTriple.getLeft();
        PromptCreateDTO dto =CommonUtils.convert(promptInfo, PromptCreateDTO.class);
        if (PromptType.CHAT == PromptType.of(promptInfo.getType())) {
            try {
                ChatPromptContent promptTemplate = InfoUtils.defaultMapper().readValue(
                        promptInfo.getTemplate(), ChatPromptContent.class);
                dto.setChatTemplate(ChatPromptContentDTO.from(promptTemplate));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid template: " + promptInfo.getTemplate());
            }
        }
        dto.setTags(promptInfoTriple.getMiddle());
        dto.setAiModels(promptInfoTriple.getRight());

        return dto;
    }
}
