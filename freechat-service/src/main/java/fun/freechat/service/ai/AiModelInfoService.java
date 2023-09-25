package fun.freechat.service.ai;

import fun.freechat.model.AiModelInfo;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.ModelType;

import java.util.List;

public interface AiModelInfoService {
    String createOrUpdate(String name, String description, ModelProvider provider, ModelType type);
    boolean delete(String modelId);
    AiModelInfo get(String modelId);
    List<AiModelInfo> list(int limit, int offset);
}
