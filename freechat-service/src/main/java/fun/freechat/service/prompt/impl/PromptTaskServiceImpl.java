package fun.freechat.service.prompt.impl;

import fun.freechat.mapper.PromptTaskDynamicSqlSupport;
import fun.freechat.mapper.PromptTaskMapper;
import fun.freechat.model.PromptTask;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.util.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class PromptTaskServiceImpl implements PromptTaskService {
    final static String CACHE_KEY_PREFIX = "PromptTaskService_task_";
    final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

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
                .withGmtExecuted(null)
                .withTaskId(IdUtils.newId()));
        if (rows <= 0) {
            task.setTaskId(null);
            return false;
        }
        return true;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.taskId")
    public boolean update(PromptTask task) {
        int rows = promptTaskMapper.updateByPrimaryKeySelective(task
                .withApiKeyValue(encryptionService.encrypt(task.getApiKeyValue()))
                .withGmtModified(new Date()));
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(String taskId) {
        int rows = promptTaskMapper.deleteByPrimaryKey(taskId);
        return rows > 0;
    }

    @Override
    public boolean deleteByPromptUid(String promptUid) {
        int rows = promptTaskMapper.delete(c ->
                c.where(PromptTaskDynamicSqlSupport.promptUid, isEqualTo(promptUid)));
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public PromptTask get(String taskId) {
        return promptTaskMapper.selectByPrimaryKey(taskId)
                .map(task -> task.withApiKeyValue(encryptionService.decrypt(task.getApiKeyValue())))
                .orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwner(String taskId) {
        PromptTask task = get(taskId);
        if (Objects.isNull(task)) {
            return null;
        }
        String promptUid = task.getPromptUid();
        return promptService.getOwnerByUid(promptUid);
    }
}
