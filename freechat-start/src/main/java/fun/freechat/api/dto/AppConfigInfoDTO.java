package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;
import java.util.Properties;

@Schema(description = "Configuration information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AppConfigInfoDTO extends TraceableDTO {
    @Schema(description = "Configuration content")
    private Map<Object, Object> properties;

    public static AppConfigInfoDTO from(Properties properties) {
        return AppConfigInfoDTO.builder()
                .properties(properties)
                .build();
    }
}
