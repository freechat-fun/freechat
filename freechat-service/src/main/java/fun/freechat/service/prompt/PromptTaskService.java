package fun.freechat.service.prompt;

import fun.freechat.model.PromptTask;
import fun.freechat.model.User;

public interface PromptTaskService {
    boolean create(PromptTask task);
    boolean update(PromptTask task);
    boolean delete(String taskId);
    void deleteByPromptUid(String promptUid);
    void deleteByUser(User user);
    PromptTask get(String taskId);
    String getOwner(String taskId);
}
