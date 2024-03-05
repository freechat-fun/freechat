package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptInfo;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Prompt detailed content")
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class PromptDetailsDTO extends PromptSummaryDTO {
    @Schema(description = "Prompt text template content")
    private String template;
    @Schema(description = "Prompt chat template content")
    private ChatPromptContentDTO chatTemplate;
    @Schema(description = "Prompt example")
    private String example;
    @Schema(description = "Prompt inputs, JSON format")
    private String inputs;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Draft content")
    private String draft;

    public static PromptDetailsDTO from(
            Triple<PromptInfo, List<String>, List<String>> promptInfoTriple) {
        if (Objects.isNull(promptInfoTriple)) {
            return null;
        }
        PromptInfo info = promptInfoTriple.getLeft();
        PromptDetailsDTO dto =
                CommonUtils.convert(info, PromptDetailsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(promptInfoTriple.getLeft().getUserId()));
        if (PromptType.of(info.getType()) == PromptType.CHAT) {
            try {
                ChatPromptContentDTO chatPrompt = ChatPromptContentDTO.from(
                        InfoUtils.defaultMapper().readValue(info.getTemplate(), ChatPromptContent.class));
                dto.setChatTemplate(chatPrompt);
                dto.setTemplate(null);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse chat prompt: {}", info.getTemplate(), e);
                dto.setType(PromptType.STRING.text());
            }
        }
        dto.setTags(promptInfoTriple.getMiddle());
        dto.setAiModels(promptInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
