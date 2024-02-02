package fun.freechat.mapper;

import static fun.freechat.mapper.CharacterBackendDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.CharacterBackend;
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
public interface CharacterBackendMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<CharacterBackend>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(backendId, gmtCreate, gmtModified, characterId, isDefault, chatPromptTaskId, greetingPromptTaskId, moderationModelId, moderationApiKeyName, forwardToUser, messageWindowSize, moderationParams);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="CharacterBackendResult", value = {
        @Result(column="backend_id", property="backendId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="character_id", property="characterId", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_default", property="isDefault", jdbcType=JdbcType.TINYINT),
        @Result(column="chat_prompt_task_id", property="chatPromptTaskId", jdbcType=JdbcType.VARCHAR),
        @Result(column="greeting_prompt_task_id", property="greetingPromptTaskId", jdbcType=JdbcType.VARCHAR),
        @Result(column="moderation_model_id", property="moderationModelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="moderation_api_key_name", property="moderationApiKeyName", jdbcType=JdbcType.VARCHAR),
        @Result(column="forward_to_user", property="forwardToUser", jdbcType=JdbcType.TINYINT),
        @Result(column="message_window_size", property="messageWindowSize", jdbcType=JdbcType.INTEGER),
        @Result(column="moderation_params", property="moderationParams", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<CharacterBackend> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("CharacterBackendResult")
    Optional<CharacterBackend> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String backendId_) {
        return delete(c -> 
            c.where(backendId, isEqualTo(backendId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(CharacterBackend row) {
        return MyBatis3Utils.insert(this::insert, row, characterBackend, c ->
            c.map(backendId).toProperty("backendId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(characterId).toProperty("characterId")
            .map(isDefault).toProperty("isDefault")
            .map(chatPromptTaskId).toProperty("chatPromptTaskId")
            .map(greetingPromptTaskId).toProperty("greetingPromptTaskId")
            .map(moderationModelId).toProperty("moderationModelId")
            .map(moderationApiKeyName).toProperty("moderationApiKeyName")
            .map(forwardToUser).toProperty("forwardToUser")
            .map(messageWindowSize).toProperty("messageWindowSize")
            .map(moderationParams).toProperty("moderationParams")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<CharacterBackend> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, characterBackend, c ->
            c.map(backendId).toProperty("backendId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(characterId).toProperty("characterId")
            .map(isDefault).toProperty("isDefault")
            .map(chatPromptTaskId).toProperty("chatPromptTaskId")
            .map(greetingPromptTaskId).toProperty("greetingPromptTaskId")
            .map(moderationModelId).toProperty("moderationModelId")
            .map(moderationApiKeyName).toProperty("moderationApiKeyName")
            .map(forwardToUser).toProperty("forwardToUser")
            .map(messageWindowSize).toProperty("messageWindowSize")
            .map(moderationParams).toProperty("moderationParams")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(CharacterBackend row) {
        return MyBatis3Utils.insert(this::insert, row, characterBackend, c ->
            c.map(backendId).toPropertyWhenPresent("backendId", row::getBackendId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(characterId).toPropertyWhenPresent("characterId", row::getCharacterId)
            .map(isDefault).toPropertyWhenPresent("isDefault", row::getIsDefault)
            .map(chatPromptTaskId).toPropertyWhenPresent("chatPromptTaskId", row::getChatPromptTaskId)
            .map(greetingPromptTaskId).toPropertyWhenPresent("greetingPromptTaskId", row::getGreetingPromptTaskId)
            .map(moderationModelId).toPropertyWhenPresent("moderationModelId", row::getModerationModelId)
            .map(moderationApiKeyName).toPropertyWhenPresent("moderationApiKeyName", row::getModerationApiKeyName)
            .map(forwardToUser).toPropertyWhenPresent("forwardToUser", row::getForwardToUser)
            .map(messageWindowSize).toPropertyWhenPresent("messageWindowSize", row::getMessageWindowSize)
            .map(moderationParams).toPropertyWhenPresent("moderationParams", row::getModerationParams)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CharacterBackend> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CharacterBackend> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CharacterBackend> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CharacterBackend> selectByPrimaryKey(String backendId_) {
        return selectOne(c ->
            c.where(backendId, isEqualTo(backendId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, characterBackend, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(CharacterBackend row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(backendId).equalTo(row::getBackendId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(characterId).equalTo(row::getCharacterId)
                .set(isDefault).equalTo(row::getIsDefault)
                .set(chatPromptTaskId).equalTo(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalTo(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalTo(row::getModerationModelId)
                .set(moderationApiKeyName).equalTo(row::getModerationApiKeyName)
                .set(forwardToUser).equalTo(row::getForwardToUser)
                .set(messageWindowSize).equalTo(row::getMessageWindowSize)
                .set(moderationParams).equalTo(row::getModerationParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(CharacterBackend row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(backendId).equalToWhenPresent(row::getBackendId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(characterId).equalToWhenPresent(row::getCharacterId)
                .set(isDefault).equalToWhenPresent(row::getIsDefault)
                .set(chatPromptTaskId).equalToWhenPresent(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalToWhenPresent(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalToWhenPresent(row::getModerationModelId)
                .set(moderationApiKeyName).equalToWhenPresent(row::getModerationApiKeyName)
                .set(forwardToUser).equalToWhenPresent(row::getForwardToUser)
                .set(messageWindowSize).equalToWhenPresent(row::getMessageWindowSize)
                .set(moderationParams).equalToWhenPresent(row::getModerationParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(CharacterBackend row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(characterId).equalTo(row::getCharacterId)
            .set(isDefault).equalTo(row::getIsDefault)
            .set(chatPromptTaskId).equalTo(row::getChatPromptTaskId)
            .set(greetingPromptTaskId).equalTo(row::getGreetingPromptTaskId)
            .set(moderationModelId).equalTo(row::getModerationModelId)
            .set(moderationApiKeyName).equalTo(row::getModerationApiKeyName)
            .set(forwardToUser).equalTo(row::getForwardToUser)
            .set(messageWindowSize).equalTo(row::getMessageWindowSize)
            .set(moderationParams).equalTo(row::getModerationParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(CharacterBackend row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(characterId).equalToWhenPresent(row::getCharacterId)
            .set(isDefault).equalToWhenPresent(row::getIsDefault)
            .set(chatPromptTaskId).equalToWhenPresent(row::getChatPromptTaskId)
            .set(greetingPromptTaskId).equalToWhenPresent(row::getGreetingPromptTaskId)
            .set(moderationModelId).equalToWhenPresent(row::getModerationModelId)
            .set(moderationApiKeyName).equalToWhenPresent(row::getModerationApiKeyName)
            .set(forwardToUser).equalToWhenPresent(row::getForwardToUser)
            .set(messageWindowSize).equalToWhenPresent(row::getMessageWindowSize)
            .set(moderationParams).equalToWhenPresent(row::getModerationParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }
}