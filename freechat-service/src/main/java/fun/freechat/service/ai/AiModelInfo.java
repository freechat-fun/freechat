package fun.freechat.service.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiModelInfo implements Serializable {
    private String modelId;
    private Date gmtCreate;
    private Date gmtModified;
    private String name;
    private String description;
    private String provider;
    private String type;
    @Serial
    private static final long serialVersionUID = 1L;
}