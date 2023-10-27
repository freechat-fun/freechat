package fun.freechat.api.util;

import fun.freechat.api.dto.AiModelInfoDTO;
import fun.freechat.model.AiModelInfo;
import fun.freechat.service.ai.AiModelInfoService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AiModelUtils implements ApplicationContextAware {
    private static AiModelInfoService aiModelInfoService;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        aiModelInfoService = applicationContext.getBean(AiModelInfoService.class);
    }

    public static AiModelInfoDTO getModelInfoDTO(String modelId) {
        AiModelInfo modelInfo = aiModelInfoService.get(modelId);
        if (Objects.isNull(modelInfo)) {
            return AiModelInfoDTO.from(modelId);
        }
        return AiModelInfoDTO.from(modelInfo);
    }
}
