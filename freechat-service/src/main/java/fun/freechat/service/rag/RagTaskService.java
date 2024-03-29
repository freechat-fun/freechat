package fun.freechat.service.rag;

import fun.freechat.model.PromptTask;
import fun.freechat.model.RagTask;

import java.util.List;

public interface RagTaskService {
    boolean create(RagTask task);
    boolean update(RagTask task);
    boolean delete(RagTask taskId);
    List<RagTask> list(String characterUid);
    boolean deleteByCharacterUid(String characterUid);
    PromptTask get(String taskId);
    String getOwner(String taskId);
    void start(String taskId);
}
