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
    BasicColumn[] selectList = BasicColumn.columnList(backendId, gmtCreate, gmtModified, characterUid, isDefault, chatPromptTaskId, greetingPromptTaskId, moderationModelId, moderationApiKeyName, forwardToUser, messageWindowSize, longTermMemoryWindowSize, proactiveChatWaitingTime, initQuota, quotaType, enableAlbumTool, enableTts, ttsSpeakerIdx, ttsSpeakerWav, ttsSpeakerType, moderationParams);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="CharacterBackendResult", value = {
        @Result(column="backend_id", property="backendId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="character_uid", property="characterUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_default", property="isDefault", jdbcType=JdbcType.TINYINT),
        @Result(column="chat_prompt_task_id", property="chatPromptTaskId", jdbcType=JdbcType.VARCHAR),
        @Result(column="greeting_prompt_task_id", property="greetingPromptTaskId", jdbcType=JdbcType.VARCHAR),
        @Result(column="moderation_model_id", property="moderationModelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="moderation_api_key_name", property="moderationApiKeyName", jdbcType=JdbcType.VARCHAR),
        @Result(column="forward_to_user", property="forwardToUser", jdbcType=JdbcType.TINYINT),
        @Result(column="message_window_size", property="messageWindowSize", jdbcType=JdbcType.INTEGER),
        @Result(column="long_term_memory_window_size", property="longTermMemoryWindowSize", jdbcType=JdbcType.INTEGER),
        @Result(column="proactive_chat_waiting_time", property="proactiveChatWaitingTime", jdbcType=JdbcType.INTEGER),
        @Result(column="init_quota", property="initQuota", jdbcType=JdbcType.BIGINT),
        @Result(column="quota_type", property="quotaType", jdbcType=JdbcType.VARCHAR),
        @Result(column="enable_album_tool", property="enableAlbumTool", jdbcType=JdbcType.TINYINT),
        @Result(column="enable_tts", property="enableTts", jdbcType=JdbcType.TINYINT),
        @Result(column="tts_speaker_idx", property="ttsSpeakerIdx", jdbcType=JdbcType.VARCHAR),
        @Result(column="tts_speaker_wav", property="ttsSpeakerWav", jdbcType=JdbcType.VARCHAR),
        @Result(column="tts_speaker_type", property="ttsSpeakerType", jdbcType=JdbcType.VARCHAR),
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
            .map(characterUid).toProperty("characterUid")
            .map(isDefault).toProperty("isDefault")
            .map(chatPromptTaskId).toProperty("chatPromptTaskId")
            .map(greetingPromptTaskId).toProperty("greetingPromptTaskId")
            .map(moderationModelId).toProperty("moderationModelId")
            .map(moderationApiKeyName).toProperty("moderationApiKeyName")
            .map(forwardToUser).toProperty("forwardToUser")
            .map(messageWindowSize).toProperty("messageWindowSize")
            .map(longTermMemoryWindowSize).toProperty("longTermMemoryWindowSize")
            .map(proactiveChatWaitingTime).toProperty("proactiveChatWaitingTime")
            .map(initQuota).toProperty("initQuota")
            .map(quotaType).toProperty("quotaType")
            .map(enableAlbumTool).toProperty("enableAlbumTool")
            .map(enableTts).toProperty("enableTts")
            .map(ttsSpeakerIdx).toProperty("ttsSpeakerIdx")
            .map(ttsSpeakerWav).toProperty("ttsSpeakerWav")
            .map(ttsSpeakerType).toProperty("ttsSpeakerType")
            .map(moderationParams).toProperty("moderationParams")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<CharacterBackend> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, characterBackend, c ->
            c.map(backendId).toProperty("backendId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(characterUid).toProperty("characterUid")
            .map(isDefault).toProperty("isDefault")
            .map(chatPromptTaskId).toProperty("chatPromptTaskId")
            .map(greetingPromptTaskId).toProperty("greetingPromptTaskId")
            .map(moderationModelId).toProperty("moderationModelId")
            .map(moderationApiKeyName).toProperty("moderationApiKeyName")
            .map(forwardToUser).toProperty("forwardToUser")
            .map(messageWindowSize).toProperty("messageWindowSize")
            .map(longTermMemoryWindowSize).toProperty("longTermMemoryWindowSize")
            .map(proactiveChatWaitingTime).toProperty("proactiveChatWaitingTime")
            .map(initQuota).toProperty("initQuota")
            .map(quotaType).toProperty("quotaType")
            .map(enableAlbumTool).toProperty("enableAlbumTool")
            .map(enableTts).toProperty("enableTts")
            .map(ttsSpeakerIdx).toProperty("ttsSpeakerIdx")
            .map(ttsSpeakerWav).toProperty("ttsSpeakerWav")
            .map(ttsSpeakerType).toProperty("ttsSpeakerType")
            .map(moderationParams).toProperty("moderationParams")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(CharacterBackend row) {
        return MyBatis3Utils.insert(this::insert, row, characterBackend, c ->
            c.map(backendId).toPropertyWhenPresent("backendId", row::getBackendId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(characterUid).toPropertyWhenPresent("characterUid", row::getCharacterUid)
            .map(isDefault).toPropertyWhenPresent("isDefault", row::getIsDefault)
            .map(chatPromptTaskId).toPropertyWhenPresent("chatPromptTaskId", row::getChatPromptTaskId)
            .map(greetingPromptTaskId).toPropertyWhenPresent("greetingPromptTaskId", row::getGreetingPromptTaskId)
            .map(moderationModelId).toPropertyWhenPresent("moderationModelId", row::getModerationModelId)
            .map(moderationApiKeyName).toPropertyWhenPresent("moderationApiKeyName", row::getModerationApiKeyName)
            .map(forwardToUser).toPropertyWhenPresent("forwardToUser", row::getForwardToUser)
            .map(messageWindowSize).toPropertyWhenPresent("messageWindowSize", row::getMessageWindowSize)
            .map(longTermMemoryWindowSize).toPropertyWhenPresent("longTermMemoryWindowSize", row::getLongTermMemoryWindowSize)
            .map(proactiveChatWaitingTime).toPropertyWhenPresent("proactiveChatWaitingTime", row::getProactiveChatWaitingTime)
            .map(initQuota).toPropertyWhenPresent("initQuota", row::getInitQuota)
            .map(quotaType).toPropertyWhenPresent("quotaType", row::getQuotaType)
            .map(enableAlbumTool).toPropertyWhenPresent("enableAlbumTool", row::getEnableAlbumTool)
            .map(enableTts).toPropertyWhenPresent("enableTts", row::getEnableTts)
            .map(ttsSpeakerIdx).toPropertyWhenPresent("ttsSpeakerIdx", row::getTtsSpeakerIdx)
            .map(ttsSpeakerWav).toPropertyWhenPresent("ttsSpeakerWav", row::getTtsSpeakerWav)
            .map(ttsSpeakerType).toPropertyWhenPresent("ttsSpeakerType", row::getTtsSpeakerType)
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
                .set(characterUid).equalTo(row::getCharacterUid)
                .set(isDefault).equalTo(row::getIsDefault)
                .set(chatPromptTaskId).equalTo(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalTo(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalTo(row::getModerationModelId)
                .set(moderationApiKeyName).equalTo(row::getModerationApiKeyName)
                .set(forwardToUser).equalTo(row::getForwardToUser)
                .set(messageWindowSize).equalTo(row::getMessageWindowSize)
                .set(longTermMemoryWindowSize).equalTo(row::getLongTermMemoryWindowSize)
                .set(proactiveChatWaitingTime).equalTo(row::getProactiveChatWaitingTime)
                .set(initQuota).equalTo(row::getInitQuota)
                .set(quotaType).equalTo(row::getQuotaType)
                .set(enableAlbumTool).equalTo(row::getEnableAlbumTool)
                .set(enableTts).equalTo(row::getEnableTts)
                .set(ttsSpeakerIdx).equalTo(row::getTtsSpeakerIdx)
                .set(ttsSpeakerWav).equalTo(row::getTtsSpeakerWav)
                .set(ttsSpeakerType).equalTo(row::getTtsSpeakerType)
                .set(moderationParams).equalTo(row::getModerationParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(CharacterBackend row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(backendId).equalToWhenPresent(row::getBackendId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(characterUid).equalToWhenPresent(row::getCharacterUid)
                .set(isDefault).equalToWhenPresent(row::getIsDefault)
                .set(chatPromptTaskId).equalToWhenPresent(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalToWhenPresent(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalToWhenPresent(row::getModerationModelId)
                .set(moderationApiKeyName).equalToWhenPresent(row::getModerationApiKeyName)
                .set(forwardToUser).equalToWhenPresent(row::getForwardToUser)
                .set(messageWindowSize).equalToWhenPresent(row::getMessageWindowSize)
                .set(longTermMemoryWindowSize).equalToWhenPresent(row::getLongTermMemoryWindowSize)
                .set(proactiveChatWaitingTime).equalToWhenPresent(row::getProactiveChatWaitingTime)
                .set(initQuota).equalToWhenPresent(row::getInitQuota)
                .set(quotaType).equalToWhenPresent(row::getQuotaType)
                .set(enableAlbumTool).equalToWhenPresent(row::getEnableAlbumTool)
                .set(enableTts).equalToWhenPresent(row::getEnableTts)
                .set(ttsSpeakerIdx).equalToWhenPresent(row::getTtsSpeakerIdx)
                .set(ttsSpeakerWav).equalToWhenPresent(row::getTtsSpeakerWav)
                .set(ttsSpeakerType).equalToWhenPresent(row::getTtsSpeakerType)
                .set(moderationParams).equalToWhenPresent(row::getModerationParams);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(CharacterBackend row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(characterUid).equalTo(row::getCharacterUid)
            .set(isDefault).equalTo(row::getIsDefault)
            .set(chatPromptTaskId).equalTo(row::getChatPromptTaskId)
            .set(greetingPromptTaskId).equalTo(row::getGreetingPromptTaskId)
            .set(moderationModelId).equalTo(row::getModerationModelId)
            .set(moderationApiKeyName).equalTo(row::getModerationApiKeyName)
            .set(forwardToUser).equalTo(row::getForwardToUser)
            .set(messageWindowSize).equalTo(row::getMessageWindowSize)
            .set(longTermMemoryWindowSize).equalTo(row::getLongTermMemoryWindowSize)
            .set(proactiveChatWaitingTime).equalTo(row::getProactiveChatWaitingTime)
            .set(initQuota).equalTo(row::getInitQuota)
            .set(quotaType).equalTo(row::getQuotaType)
            .set(enableAlbumTool).equalTo(row::getEnableAlbumTool)
            .set(enableTts).equalTo(row::getEnableTts)
            .set(ttsSpeakerIdx).equalTo(row::getTtsSpeakerIdx)
            .set(ttsSpeakerWav).equalTo(row::getTtsSpeakerWav)
            .set(ttsSpeakerType).equalTo(row::getTtsSpeakerType)
            .set(moderationParams).equalTo(row::getModerationParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(CharacterBackend row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(characterUid).equalToWhenPresent(row::getCharacterUid)
            .set(isDefault).equalToWhenPresent(row::getIsDefault)
            .set(chatPromptTaskId).equalToWhenPresent(row::getChatPromptTaskId)
            .set(greetingPromptTaskId).equalToWhenPresent(row::getGreetingPromptTaskId)
            .set(moderationModelId).equalToWhenPresent(row::getModerationModelId)
            .set(moderationApiKeyName).equalToWhenPresent(row::getModerationApiKeyName)
            .set(forwardToUser).equalToWhenPresent(row::getForwardToUser)
            .set(messageWindowSize).equalToWhenPresent(row::getMessageWindowSize)
            .set(longTermMemoryWindowSize).equalToWhenPresent(row::getLongTermMemoryWindowSize)
            .set(proactiveChatWaitingTime).equalToWhenPresent(row::getProactiveChatWaitingTime)
            .set(initQuota).equalToWhenPresent(row::getInitQuota)
            .set(quotaType).equalToWhenPresent(row::getQuotaType)
            .set(enableAlbumTool).equalToWhenPresent(row::getEnableAlbumTool)
            .set(enableTts).equalToWhenPresent(row::getEnableTts)
            .set(ttsSpeakerIdx).equalToWhenPresent(row::getTtsSpeakerIdx)
            .set(ttsSpeakerWav).equalToWhenPresent(row::getTtsSpeakerWav)
            .set(ttsSpeakerType).equalToWhenPresent(row::getTtsSpeakerType)
            .set(moderationParams).equalToWhenPresent(row::getModerationParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }
}