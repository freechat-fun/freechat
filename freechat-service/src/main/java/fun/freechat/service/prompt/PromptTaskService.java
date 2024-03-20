package fun.freechat.service.prompt;

import fun.freechat.model.PromptTask;

public interface PromptTaskService {
    boolean create(PromptTask task);
    boolean update(PromptTask task);
    boolean delete(String taskId);
    boolean deleteByPromptUid(String promptUid);
    PromptTask get(String taskId);
    String getOwner(String taskId);
}
