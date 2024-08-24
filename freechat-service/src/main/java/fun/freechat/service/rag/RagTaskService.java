package fun.freechat.service.rag;

import fun.freechat.model.RagTask;
import fun.freechat.service.enums.TaskStatus;

import java.util.List;

public interface RagTaskService {
    boolean create(RagTask task);
    boolean update(RagTask task);
    boolean delete(Long taskId);
    List<RagTask> list(String characterUid);
    boolean hasAnyTask(String characterUid);
    boolean deleteByCharacterUid(String characterUid);
    RagTask get(Long taskId);
    String getOwner(Long taskId);
    String getCharacterUid(Long taskId);
    TaskStatus getStatus(Long taskId);
    boolean setStatus(Long taskId, TaskStatus status, RagTaskExt ext);
    boolean start(Long taskId);
    boolean cancel(long taskId);
}
