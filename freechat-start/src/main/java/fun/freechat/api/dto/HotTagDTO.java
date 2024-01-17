package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.HotTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Hot tag")
@Data
public class HotTagDTO {
    @Schema(description = "Tag content")
    private String content;
    @Schema(description = "Tag count")
    private Long count;

    public static HotTagDTO fromHotTag(HotTag hotTag) {
        if (Objects.isNull(hotTag)) {
            return null;
        }
        return CommonUtils.convert(hotTag, HotTagDTO.class);
    }
}
