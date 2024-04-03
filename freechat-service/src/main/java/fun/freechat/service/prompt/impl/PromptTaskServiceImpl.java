package fun.freechat.service.prompt.impl;

import fun.freechat.mapper.PromptTaskDynamicSqlSupport;
import fun.freechat.mapper.PromptTaskMapper;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.PromptTask;
import fun.freechat.model.User;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.util.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class PromptTaskServiceImpl implements PromptTaskService {
    @Autowired
    private PromptTaskMapper promptTaskMapper;
    @Autowired
    private PromptService promptService;
    @Autowired
    private EncryptionService encryptionService;

    @Override
    public boolean create(PromptTask task) {
        if (StringUtils.isBlank(task.getPromptUid())) {
            return false;
        }

        Date now = new Date();
        int rows = promptTaskMapper.insertSelective(task
                .withApiKeyValue(encryptionService.encrypt(task.getApiKeyValue()))
                .withGmtCreate(now)
                .withGmtModified(now)
                .withGmtStart(null)
                .withGmtEnd(null)
                .withTaskId(IdUtils.newId()));
        if (rows <= 0) {
            task.setTaskId(null);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PromptTask task) {
        int rows = promptTaskMapper.updateByPrimaryKeySelective(task
                .withApiKeyValue(encryptionService.encrypt(task.getApiKeyValue()))
                .withGmtModified(new Date()));
        return rows > 0;
    }

    @Override
    public boolean delete(String taskId) {
        int rows = promptTaskMapper.deleteByPrimaryKey(taskId);
        return rows > 0;
    }

    @Override
    public void deleteByPromptUid(String promptUid) {
        promptTaskMapper.delete(c ->
                c.where(PromptTaskDynamicSqlSupport.promptUid, isEqualTo(promptUid)));
    }

    @Override
    public void deleteByUser(User user) {
        promptService.search(
                PromptService.queryBuilder()
                        .where(PromptService.Query.whereBuilder().build())
                        .build(), user)
                .stream()
                .map(Triple::getLeft)
                .map(PromptInfo::getPromptUid)
                .collect(Collectors.toSet())
                .forEach(this::deleteByPromptUid);
    }

    @Override
    public PromptTask get(String taskId) {
        return promptTaskMapper.selectByPrimaryKey(taskId)
                .map(task -> task.withApiKeyValue(encryptionService.decrypt(task.getApiKeyValue())))
                .orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwner(String taskId) {
        return promptTaskMapper.selectByPrimaryKey(taskId)
                .map(PromptTask::getPromptUid)
                .map(promptService::getOwnerByUid)
                .orElse(null);
    }
}
