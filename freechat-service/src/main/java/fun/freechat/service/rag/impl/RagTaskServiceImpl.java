package fun.freechat.service.rag.impl;

import fun.freechat.mapper.RagTaskDynamicSqlSupport;
import fun.freechat.mapper.RagTaskMapper;
import fun.freechat.model.RagTask;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.TaskStatus;
import fun.freechat.service.rag.RagTaskExt;
import fun.freechat.service.rag.RagTaskRunner;
import fun.freechat.service.rag.RagTaskService;
import fun.freechat.service.rag.RagTaskStartedEvent;
import fun.freechat.service.util.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import static fun.freechat.service.enums.TaskStatus.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

@Service
@Slf4j
@SuppressWarnings("unused")
public class RagTaskServiceImpl implements RagTaskService {
    private static final String CACHE_KEY_PREFIX = "RagTaskService_";

    @Autowired
    private RagTaskMapper ragTaskMapper;
    @Autowired
    private RagTaskRunner ragTaskRunner;
    @Autowired
    private CharacterService characterService;

    @Override
    public boolean create(RagTask task) {
        Date now = new Date();
        int rows = ragTaskMapper.insertSelective(task
                .withGmtCreate(now)
                .withGmtModified(now)
                .withGmtStart(null)
                .withGmtEnd(null)
                .withStatus(PENDING.text()));

        return rows > 0;
    }

    @Override
    public boolean update(RagTask task) {
        int rows = ragTaskMapper.updateByPrimaryKeySelective(task
                .withGmtModified(new Date()));
        return rows > 0;
    }

    @Override
    public boolean delete(Long taskId) {
        int rows = ragTaskMapper.deleteByPrimaryKey(taskId);
        return rows > 0;
    }

    @Override
    public List<RagTask> list(String characterUid) {
        return ragTaskMapper.select(c ->
                c.where(RagTaskDynamicSqlSupport.characterUid, isEqualTo(characterUid)));
    }

    @Override
    @MiddlePeriodCache
    public boolean hasAnyTask(String characterUid) {
        return !ragTaskMapper.select(c ->
                c.where(RagTaskDynamicSqlSupport.characterUid, isEqualTo(characterUid))
                        .limit(1))
                .isEmpty();
    }

    @Override
    public boolean deleteByCharacterUid(String characterUid) {
        int rows = ragTaskMapper.delete(c ->
                c.where(RagTaskDynamicSqlSupport.characterUid, isEqualTo(characterUid)));
        return rows > 0;
    }

    @Override
    public RagTask get(Long taskId) {
        return ragTaskMapper.selectByPrimaryKey(taskId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwner(Long taskId) {
        return ragTaskMapper.selectByPrimaryKey(taskId)
                .map(RagTask::getCharacterUid)
                .map(characterService::getOwnerByUid)
                .orElse(null);
    }

    @Override
    public String getCharacterUid(Long taskId) {
        return ragTaskMapper.selectByPrimaryKey(taskId)
                .map(RagTask::getCharacterUid)
                .orElse(null);
    }

    @Override
    public TaskStatus getStatus(Long taskId) {
        var statement = select(RagTaskDynamicSqlSupport.status)
                .from(RagTaskDynamicSqlSupport.ragTask)
                .where(RagTaskDynamicSqlSupport.id, isEqualTo(taskId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return ragTaskMapper.selectOne(statement)
                .map(RagTask::getStatus)
                .map(TaskStatus::of)
                .orElse(TaskStatus.UNKNOWN);
    }

    @Override
    public boolean setStatus(Long taskId, TaskStatus status, RagTaskExt ext) {
        RagTask task = ragTaskMapper.selectByPrimaryKey(taskId).orElse(null);

        if (task == null) {
            return false;
        }

        return setStatus(task, status, ext);
    }

    private boolean setStatus(RagTask task, TaskStatus status, RagTaskExt ext) {
        if (ext != null) {
            task.setExt(ext.toString());
        }

        TaskStatus preStatus = TaskStatus.of(task.getStatus());
        Date now = new Date();
        int rows = 0;

        switch (status) {
            case PENDING:
                if (preStatus != RUNNING) {
                    rows = ragTaskMapper.updateByPrimaryKey(task
                            .withStatus(status.text())
                            .withGmtModified(now)
                            .withGmtStart(null)
                            .withGmtEnd(null));
                }
                break;
            case FAILED, SUCCEEDED:
                if (preStatus == PENDING || preStatus == RUNNING) {
                    rows = ragTaskMapper.updateByPrimaryKey(task
                            .withStatus(status.text())
                            .withGmtModified(now)
                            .withGmtEnd(now));
                }
                break;
            case RUNNING:
                if (preStatus == PENDING) {
                    rows = ragTaskMapper.updateByPrimaryKey(task
                            .withStatus(status.text())
                            .withGmtModified(now)
                            .withGmtStart(now)
                            .withGmtEnd(null));
                }
                break;
            case UNKNOWN:
                rows = ragTaskMapper.updateByPrimaryKey(task
                        .withStatus(status.text())
                        .withGmtModified(now));
                break;
            case CANCELED:
                if (preStatus == PENDING || preStatus == RUNNING || preStatus == UNKNOWN) {
                    rows = ragTaskMapper.updateByPrimaryKey(task
                            .withStatus(status.text())
                            .withGmtModified(now)
                            .withGmtEnd(now));
                }
                break;
        }

        return rows > 0;
    }

    @Override
    public boolean start(Long taskId) {
        RagTask task = ragTaskMapper.selectByPrimaryKey(taskId).orElse(null);

        if (task == null) {
            return false;
        }

        TaskStatus currentStatus = TaskStatus.of(task.getStatus());

        if (currentStatus == RUNNING) {
            return true;
        }

        setStatus(task, PENDING, null);
        Cache cache = CacheUtils.inProcessLongPeriodCache();

        CompletableFuture<Void> future = ragTaskRunner.start(task).whenComplete((result, throwable) -> {
            if (throwable == null) {
                setStatus(task, SUCCEEDED, null);
            } else {
                TaskStatus status = throwable instanceof CancellationException ? CANCELED : FAILED;
                if (status == FAILED) {
                    log.error("RagTask {} failed with error.", taskId, throwable);
                }
                setStatus(task, status, new RagTaskExt(throwable.getMessage(), throwable));
            }
            if (cache != null) {
                cache.evict(CACHE_KEY_PREFIX + task.getId());
            }
        });

        if (future == null) {
            return false;
        }

        if (cache != null) {
            cache.put(CACHE_KEY_PREFIX + task.getId(), future);
        }

        return true;
    }

    @Override
    public boolean cancel(long taskId) {
        Cache cache = CacheUtils.inProcessLongPeriodCache();
        if (cache == null) {
            return false;
        }

        CompletableFuture<Void> future = cache.get(CACHE_KEY_PREFIX +taskId, CompletableFuture.class);
        if (future == null) {
            return false;
        }

        return future.cancel(true);
    }

    @EventListener
    public void handleTaskStarted(RagTaskStartedEvent event) {
        RagTask task = event.task();
        if (task == null) {
            return;
        }

        setStatus(task, RUNNING, null);
    }
}
