package fun.freechat.service.prompt.impl;

import fun.freechat.mapper.PromptTaskDynamicSqlSupport;
import fun.freechat.mapper.PromptTaskMapper;
import fun.freechat.model.PromptTask;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
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

    @Override
    public boolean create(PromptTask task) {
        if (StringUtils.isBlank(task.getPromptId())) {
            return false;
        }
        Date now = new Date();
        int rows = promptTaskMapper.insertSelective(task
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
        int rows = promptTaskMapper.updateByPrimaryKey(task.withGmtModified(new Date()));
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(String taskId) {
        int rows = promptTaskMapper.deleteByPrimaryKey(taskId);
        return rows > 0;
    }

    @Override
    public boolean deleteByPromptId(String promptId) {
        int rows = promptTaskMapper.delete(c ->
                c.where(PromptTaskDynamicSqlSupport.promptId, isEqualTo(promptId)));
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public PromptTask get(String taskId) {
        return promptTaskMapper.selectByPrimaryKey(taskId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwner(String taskId) {
        PromptTask task = get(taskId);
        if (Objects.isNull(task)) {
            return null;
        }
        String promptId = task.getPromptId();
        return promptService.getOwner(promptId);
    }
}
