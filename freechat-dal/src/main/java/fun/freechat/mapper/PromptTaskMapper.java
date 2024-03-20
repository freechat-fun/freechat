package fun.freechat.mapper;

import static fun.freechat.mapper.PromptTaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.PromptTask;
import jakarta.annotation.Generated;
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
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface PromptTaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<PromptTask>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(taskId, gmtCreate, gmtModified, gmtExecuted, promptUid, draft, modelId, apiKeyName, cron, status, variables, apiKeyValue, params);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="PromptTaskResult", value = {
        @Result(column="task_id", property="taskId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_executed", property="gmtExecuted", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="prompt_uid", property="promptUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.TINYINT),
        @Result(column="model_id", property="modelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="api_key_name", property="apiKeyName", jdbcType=JdbcType.VARCHAR),
        @Result(column="cron", property="cron", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="variables", property="variables", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="api_key_value", property="apiKeyValue", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="params", property="params", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PromptTask> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PromptTaskResult")
    Optional<PromptTask> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String taskId_) {
        return delete(c -> 
            c.where(taskId, isEqualTo(taskId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(PromptTask row) {
        return MyBatis3Utils.insert(this::insert, row, promptTask, c ->
            c.map(taskId).toProperty("taskId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(gmtExecuted).toProperty("gmtExecuted")
            .map(promptUid).toProperty("promptUid")
            .map(draft).toProperty("draft")
            .map(modelId).toProperty("modelId")
            .map(apiKeyName).toProperty("apiKeyName")
            .map(cron).toProperty("cron")
            .map(status).toProperty("status")
            .map(variables).toProperty("variables")
            .map(apiKeyValue).toProperty("apiKeyValue")
            .map(params).toProperty("params")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<PromptTask> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, promptTask, c ->
            c.map(taskId).toProperty("taskId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(gmtExecuted).toProperty("gmtExecuted")
            .map(promptUid).toProperty("promptUid")
            .map(draft).toProperty("draft")
            .map(modelId).toProperty("modelId")
            .map(apiKeyName).toProperty("apiKeyName")
            .map(cron).toProperty("cron")
            .map(status).toProperty("status")
            .map(variables).toProperty("variables")
            .map(apiKeyValue).toProperty("apiKeyValue")
            .map(params).toProperty("params")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(PromptTask row) {
        return MyBatis3Utils.insert(this::insert, row, promptTask, c ->
            c.map(taskId).toPropertyWhenPresent("taskId", row::getTaskId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(gmtExecuted).toPropertyWhenPresent("gmtExecuted", row::getGmtExecuted)
            .map(promptUid).toPropertyWhenPresent("promptUid", row::getPromptUid)
            .map(draft).toPropertyWhenPresent("draft", row::getDraft)
            .map(modelId).toPropertyWhenPresent("modelId", row::getModelId)
            .map(apiKeyName).toPropertyWhenPresent("apiKeyName", row::getApiKeyName)
            .map(cron).toPropertyWhenPresent("cron", row::getCron)
            .map(status).toPropertyWhenPresent("status", row::getStatus)
            .map(variables).toPropertyWhenPresent("variables", row::getVariables)
            .map(apiKeyValue).toPropertyWhenPresent("apiKeyValue", row::getApiKeyValue)
            .map(params).toPropertyWhenPresent("params", row::getParams)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PromptTask> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PromptTask> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PromptTask> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PromptTask> selectByPrimaryKey(String taskId_) {
        return selectOne(c ->
            c.where(taskId, isEqualTo(taskId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, promptTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(PromptTask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(taskId).equalTo(row::getTaskId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtExecuted).equalTo(row::getGmtExecuted)
                .set(promptUid).equalTo(row::getPromptUid)
                .set(draft).equalTo(row::getDraft)
                .set(modelId).equalTo(row::getModelId)
                .set(apiKeyName).equalTo(row::getApiKeyName)
                .set(cron).equalTo(row::getCron)
                .set(status).equalTo(row::getStatus)
                .set(variables).equalTo(row::getVariables)
                .set(apiKeyValue).equalTo(row::getApiKeyValue)
                .set(params).equalTo(row::getParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(PromptTask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(taskId).equalToWhenPresent(row::getTaskId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtExecuted).equalToWhenPresent(row::getGmtExecuted)
                .set(promptUid).equalToWhenPresent(row::getPromptUid)
                .set(draft).equalToWhenPresent(row::getDraft)
                .set(modelId).equalToWhenPresent(row::getModelId)
                .set(apiKeyName).equalToWhenPresent(row::getApiKeyName)
                .set(cron).equalToWhenPresent(row::getCron)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(variables).equalToWhenPresent(row::getVariables)
                .set(apiKeyValue).equalToWhenPresent(row::getApiKeyValue)
                .set(params).equalToWhenPresent(row::getParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(PromptTask row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(gmtExecuted).equalTo(row::getGmtExecuted)
            .set(promptUid).equalTo(row::getPromptUid)
            .set(draft).equalTo(row::getDraft)
            .set(modelId).equalTo(row::getModelId)
            .set(apiKeyName).equalTo(row::getApiKeyName)
            .set(cron).equalTo(row::getCron)
            .set(status).equalTo(row::getStatus)
            .set(variables).equalTo(row::getVariables)
            .set(apiKeyValue).equalTo(row::getApiKeyValue)
            .set(params).equalTo(row::getParams)
            .where(taskId, isEqualTo(row::getTaskId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(PromptTask row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(gmtExecuted).equalToWhenPresent(row::getGmtExecuted)
            .set(promptUid).equalToWhenPresent(row::getPromptUid)
            .set(draft).equalToWhenPresent(row::getDraft)
            .set(modelId).equalToWhenPresent(row::getModelId)
            .set(apiKeyName).equalToWhenPresent(row::getApiKeyName)
            .set(cron).equalToWhenPresent(row::getCron)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(variables).equalToWhenPresent(row::getVariables)
            .set(apiKeyValue).equalToWhenPresent(row::getApiKeyValue)
            .set(params).equalToWhenPresent(row::getParams)
            .where(taskId, isEqualTo(row::getTaskId))
        );
    }
}