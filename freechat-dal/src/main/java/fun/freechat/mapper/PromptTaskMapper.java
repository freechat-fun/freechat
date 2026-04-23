package fun.freechat.mapper;

import static fun.freechat.mapper.PromptTaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.PromptTask;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSL;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface PromptTaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<PromptTask>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(taskId, gmtCreate, gmtModified, gmtStart, gmtEnd, promptUid, draft, modelId, apiKeyName, cron, status, variables, apiKeyValue, params, ext);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="PromptTaskResult", value = {
        @Result(column="task_id", property="taskId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_start", property="gmtStart", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_end", property="gmtEnd", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="prompt_uid", property="promptUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.TINYINT),
        @Result(column="model_id", property="modelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="api_key_name", property="apiKeyName", jdbcType=JdbcType.VARCHAR),
        @Result(column="cron", property="cron", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="variables", property="variables", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="api_key_value", property="apiKeyValue", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="params", property="params", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PromptTask> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PromptTaskResult")
    Optional<PromptTask> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, promptTask, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, promptTask, completer);
    }

    default int deleteByPrimaryKey(String taskId_) {
        return delete(c -> 
            c.where(taskId, isEqualTo(taskId_))
        );
    }

    default int insert(PromptTask row) {
        return MyBatis3Utils.insert(this::insert, row, promptTask, c ->
            c.withMappedColumn(taskId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(gmtStart)
            .withMappedColumn(gmtEnd)
            .withMappedColumn(promptUid)
            .withMappedColumn(draft)
            .withMappedColumn(modelId)
            .withMappedColumn(apiKeyName)
            .withMappedColumn(cron)
            .withMappedColumn(status)
            .withMappedColumn(variables)
            .withMappedColumn(apiKeyValue)
            .withMappedColumn(params)
            .withMappedColumn(ext)
        );
    }

    default int insertMultiple(Collection<PromptTask> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, promptTask, c ->
            c.withMappedColumn(taskId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(gmtStart)
            .withMappedColumn(gmtEnd)
            .withMappedColumn(promptUid)
            .withMappedColumn(draft)
            .withMappedColumn(modelId)
            .withMappedColumn(apiKeyName)
            .withMappedColumn(cron)
            .withMappedColumn(status)
            .withMappedColumn(variables)
            .withMappedColumn(apiKeyValue)
            .withMappedColumn(params)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(PromptTask row) {
        return MyBatis3Utils.insert(this::insert, row, promptTask, c ->
            c.withMappedColumnWhenPresent(taskId, row::getTaskId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(gmtStart, row::getGmtStart)
            .withMappedColumnWhenPresent(gmtEnd, row::getGmtEnd)
            .withMappedColumnWhenPresent(promptUid, row::getPromptUid)
            .withMappedColumnWhenPresent(draft, row::getDraft)
            .withMappedColumnWhenPresent(modelId, row::getModelId)
            .withMappedColumnWhenPresent(apiKeyName, row::getApiKeyName)
            .withMappedColumnWhenPresent(cron, row::getCron)
            .withMappedColumnWhenPresent(status, row::getStatus)
            .withMappedColumnWhenPresent(variables, row::getVariables)
            .withMappedColumnWhenPresent(apiKeyValue, row::getApiKeyValue)
            .withMappedColumnWhenPresent(params, row::getParams)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<PromptTask> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, promptTask, completer);
    }

    default List<PromptTask> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, promptTask, completer);
    }

    default List<PromptTask> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, promptTask, completer);
    }

    default Optional<PromptTask> selectByPrimaryKey(String taskId_) {
        return selectOne(c ->
            c.where(taskId, isEqualTo(taskId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, promptTask, completer);
    }

    static UpdateDSL updateAllColumns(PromptTask row, UpdateDSL dsl) {
        return dsl.set(taskId).equalTo(row::getTaskId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtStart).equalTo(row::getGmtStart)
                .set(gmtEnd).equalTo(row::getGmtEnd)
                .set(promptUid).equalTo(row::getPromptUid)
                .set(draft).equalTo(row::getDraft)
                .set(modelId).equalTo(row::getModelId)
                .set(apiKeyName).equalTo(row::getApiKeyName)
                .set(cron).equalTo(row::getCron)
                .set(status).equalTo(row::getStatus)
                .set(variables).equalTo(row::getVariables)
                .set(apiKeyValue).equalTo(row::getApiKeyValue)
                .set(params).equalTo(row::getParams)
                .set(ext).equalTo(row::getExt);
    }

    static UpdateDSL updateSelectiveColumns(PromptTask row, UpdateDSL dsl) {
        return dsl.set(taskId).equalToWhenPresent(row::getTaskId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtStart).equalToWhenPresent(row::getGmtStart)
                .set(gmtEnd).equalToWhenPresent(row::getGmtEnd)
                .set(promptUid).equalToWhenPresent(row::getPromptUid)
                .set(draft).equalToWhenPresent(row::getDraft)
                .set(modelId).equalToWhenPresent(row::getModelId)
                .set(apiKeyName).equalToWhenPresent(row::getApiKeyName)
                .set(cron).equalToWhenPresent(row::getCron)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(variables).equalToWhenPresent(row::getVariables)
                .set(apiKeyValue).equalToWhenPresent(row::getApiKeyValue)
                .set(params).equalToWhenPresent(row::getParams)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    default int updateByPrimaryKey(PromptTask row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(gmtStart).equalTo(row::getGmtStart)
            .set(gmtEnd).equalTo(row::getGmtEnd)
            .set(promptUid).equalTo(row::getPromptUid)
            .set(draft).equalTo(row::getDraft)
            .set(modelId).equalTo(row::getModelId)
            .set(apiKeyName).equalTo(row::getApiKeyName)
            .set(cron).equalTo(row::getCron)
            .set(status).equalTo(row::getStatus)
            .set(variables).equalTo(row::getVariables)
            .set(apiKeyValue).equalTo(row::getApiKeyValue)
            .set(params).equalTo(row::getParams)
            .set(ext).equalTo(row::getExt)
            .where(taskId, isEqualTo(row::getTaskId))
        );
    }

    default int updateByPrimaryKeySelective(PromptTask row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(gmtStart).equalToWhenPresent(row::getGmtStart)
            .set(gmtEnd).equalToWhenPresent(row::getGmtEnd)
            .set(promptUid).equalToWhenPresent(row::getPromptUid)
            .set(draft).equalToWhenPresent(row::getDraft)
            .set(modelId).equalToWhenPresent(row::getModelId)
            .set(apiKeyName).equalToWhenPresent(row::getApiKeyName)
            .set(cron).equalToWhenPresent(row::getCron)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(variables).equalToWhenPresent(row::getVariables)
            .set(apiKeyValue).equalToWhenPresent(row::getApiKeyValue)
            .set(params).equalToWhenPresent(row::getParams)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(taskId, isEqualTo(row::getTaskId))
        );
    }
}