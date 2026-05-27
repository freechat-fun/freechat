package fun.freechat.mapper;

import static fun.freechat.mapper.CharacterBackendDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.CharacterBackend;
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
public interface CharacterBackendMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<CharacterBackend>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(backendId, gmtCreate, gmtModified, characterUid, isDefault, chatPromptTaskId, greetingPromptTaskId, moderationModelId, moderationApiKeyName, imageModelId, imageApiKeyName, forwardToUser, messageWindowSize, longTermMemoryWindowSize, proactiveChatWaitingTime, initQuota, quotaType, enableAlbumTool, enableTts, ttsSpeakerIdx, ttsSpeakerWav, ttsSpeakerType, tgBotId, tgBotToken, moderationApiKeyValue, moderationParams, imageApiKeyValue, imageParams);

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
        @Result(column="image_model_id", property="imageModelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="image_api_key_name", property="imageApiKeyName", jdbcType=JdbcType.VARCHAR),
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
        @Result(column="tg_bot_id", property="tgBotId", jdbcType=JdbcType.BIGINT),
        @Result(column="tg_bot_token", property="tgBotToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="moderation_api_key_value", property="moderationApiKeyValue", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="moderation_params", property="moderationParams", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="image_api_key_value", property="imageApiKeyValue", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="image_params", property="imageParams", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<CharacterBackend> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("CharacterBackendResult")
    Optional<CharacterBackend> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, characterBackend, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, characterBackend, completer);
    }

    default int deleteByPrimaryKey(String backendId_) {
        return delete(c -> 
            c.where(backendId, isEqualTo(backendId_))
        );
    }

    default int insert(CharacterBackend row) {
        return MyBatis3Utils.insert(this::insert, row, characterBackend, c ->
            c.withMappedColumn(backendId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(characterUid)
            .withMappedColumn(isDefault)
            .withMappedColumn(chatPromptTaskId)
            .withMappedColumn(greetingPromptTaskId)
            .withMappedColumn(moderationModelId)
            .withMappedColumn(moderationApiKeyName)
            .withMappedColumn(imageModelId)
            .withMappedColumn(imageApiKeyName)
            .withMappedColumn(forwardToUser)
            .withMappedColumn(messageWindowSize)
            .withMappedColumn(longTermMemoryWindowSize)
            .withMappedColumn(proactiveChatWaitingTime)
            .withMappedColumn(initQuota)
            .withMappedColumn(quotaType)
            .withMappedColumn(enableAlbumTool)
            .withMappedColumn(enableTts)
            .withMappedColumn(ttsSpeakerIdx)
            .withMappedColumn(ttsSpeakerWav)
            .withMappedColumn(ttsSpeakerType)
            .withMappedColumn(tgBotId)
            .withMappedColumn(tgBotToken)
            .withMappedColumn(moderationApiKeyValue)
            .withMappedColumn(moderationParams)
            .withMappedColumn(imageApiKeyValue)
            .withMappedColumn(imageParams)
        );
    }

    default int insertMultiple(Collection<CharacterBackend> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, characterBackend, c ->
            c.withMappedColumn(backendId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(characterUid)
            .withMappedColumn(isDefault)
            .withMappedColumn(chatPromptTaskId)
            .withMappedColumn(greetingPromptTaskId)
            .withMappedColumn(moderationModelId)
            .withMappedColumn(moderationApiKeyName)
            .withMappedColumn(imageModelId)
            .withMappedColumn(imageApiKeyName)
            .withMappedColumn(forwardToUser)
            .withMappedColumn(messageWindowSize)
            .withMappedColumn(longTermMemoryWindowSize)
            .withMappedColumn(proactiveChatWaitingTime)
            .withMappedColumn(initQuota)
            .withMappedColumn(quotaType)
            .withMappedColumn(enableAlbumTool)
            .withMappedColumn(enableTts)
            .withMappedColumn(ttsSpeakerIdx)
            .withMappedColumn(ttsSpeakerWav)
            .withMappedColumn(ttsSpeakerType)
            .withMappedColumn(tgBotId)
            .withMappedColumn(tgBotToken)
            .withMappedColumn(moderationApiKeyValue)
            .withMappedColumn(moderationParams)
            .withMappedColumn(imageApiKeyValue)
            .withMappedColumn(imageParams)
        );
    }

    default int insertSelective(CharacterBackend row) {
        return MyBatis3Utils.insert(this::insert, row, characterBackend, c ->
            c.withMappedColumnWhenPresent(backendId, row::getBackendId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(characterUid, row::getCharacterUid)
            .withMappedColumnWhenPresent(isDefault, row::getIsDefault)
            .withMappedColumnWhenPresent(chatPromptTaskId, row::getChatPromptTaskId)
            .withMappedColumnWhenPresent(greetingPromptTaskId, row::getGreetingPromptTaskId)
            .withMappedColumnWhenPresent(moderationModelId, row::getModerationModelId)
            .withMappedColumnWhenPresent(moderationApiKeyName, row::getModerationApiKeyName)
            .withMappedColumnWhenPresent(imageModelId, row::getImageModelId)
            .withMappedColumnWhenPresent(imageApiKeyName, row::getImageApiKeyName)
            .withMappedColumnWhenPresent(forwardToUser, row::getForwardToUser)
            .withMappedColumnWhenPresent(messageWindowSize, row::getMessageWindowSize)
            .withMappedColumnWhenPresent(longTermMemoryWindowSize, row::getLongTermMemoryWindowSize)
            .withMappedColumnWhenPresent(proactiveChatWaitingTime, row::getProactiveChatWaitingTime)
            .withMappedColumnWhenPresent(initQuota, row::getInitQuota)
            .withMappedColumnWhenPresent(quotaType, row::getQuotaType)
            .withMappedColumnWhenPresent(enableAlbumTool, row::getEnableAlbumTool)
            .withMappedColumnWhenPresent(enableTts, row::getEnableTts)
            .withMappedColumnWhenPresent(ttsSpeakerIdx, row::getTtsSpeakerIdx)
            .withMappedColumnWhenPresent(ttsSpeakerWav, row::getTtsSpeakerWav)
            .withMappedColumnWhenPresent(ttsSpeakerType, row::getTtsSpeakerType)
            .withMappedColumnWhenPresent(tgBotId, row::getTgBotId)
            .withMappedColumnWhenPresent(tgBotToken, row::getTgBotToken)
            .withMappedColumnWhenPresent(moderationApiKeyValue, row::getModerationApiKeyValue)
            .withMappedColumnWhenPresent(moderationParams, row::getModerationParams)
            .withMappedColumnWhenPresent(imageApiKeyValue, row::getImageApiKeyValue)
            .withMappedColumnWhenPresent(imageParams, row::getImageParams)
        );
    }

    default Optional<CharacterBackend> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, characterBackend, completer);
    }

    default List<CharacterBackend> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, characterBackend, completer);
    }

    default List<CharacterBackend> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, characterBackend, completer);
    }

    default Optional<CharacterBackend> selectByPrimaryKey(String backendId_) {
        return selectOne(c ->
            c.where(backendId, isEqualTo(backendId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, characterBackend, completer);
    }

    static UpdateDSL updateAllColumns(CharacterBackend row, UpdateDSL dsl) {
        return dsl.set(backendId).equalTo(row::getBackendId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(characterUid).equalTo(row::getCharacterUid)
                .set(isDefault).equalTo(row::getIsDefault)
                .set(chatPromptTaskId).equalTo(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalTo(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalTo(row::getModerationModelId)
                .set(moderationApiKeyName).equalTo(row::getModerationApiKeyName)
                .set(imageModelId).equalTo(row::getImageModelId)
                .set(imageApiKeyName).equalTo(row::getImageApiKeyName)
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
                .set(tgBotId).equalTo(row::getTgBotId)
                .set(tgBotToken).equalTo(row::getTgBotToken)
                .set(moderationApiKeyValue).equalTo(row::getModerationApiKeyValue)
                .set(moderationParams).equalTo(row::getModerationParams)
                .set(imageApiKeyValue).equalTo(row::getImageApiKeyValue)
                .set(imageParams).equalTo(row::getImageParams);
    }

    static UpdateDSL updateSelectiveColumns(CharacterBackend row, UpdateDSL dsl) {
        return dsl.set(backendId).equalToWhenPresent(row::getBackendId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(characterUid).equalToWhenPresent(row::getCharacterUid)
                .set(isDefault).equalToWhenPresent(row::getIsDefault)
                .set(chatPromptTaskId).equalToWhenPresent(row::getChatPromptTaskId)
                .set(greetingPromptTaskId).equalToWhenPresent(row::getGreetingPromptTaskId)
                .set(moderationModelId).equalToWhenPresent(row::getModerationModelId)
                .set(moderationApiKeyName).equalToWhenPresent(row::getModerationApiKeyName)
                .set(imageModelId).equalToWhenPresent(row::getImageModelId)
                .set(imageApiKeyName).equalToWhenPresent(row::getImageApiKeyName)
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
                .set(tgBotId).equalToWhenPresent(row::getTgBotId)
                .set(tgBotToken).equalToWhenPresent(row::getTgBotToken)
                .set(moderationApiKeyValue).equalToWhenPresent(row::getModerationApiKeyValue)
                .set(moderationParams).equalToWhenPresent(row::getModerationParams)
                .set(imageApiKeyValue).equalToWhenPresent(row::getImageApiKeyValue)
                .set(imageParams).equalToWhenPresent(row::getImageParams);
    }

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
            .set(imageModelId).equalTo(row::getImageModelId)
            .set(imageApiKeyName).equalTo(row::getImageApiKeyName)
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
            .set(tgBotId).equalTo(row::getTgBotId)
            .set(tgBotToken).equalTo(row::getTgBotToken)
            .set(moderationApiKeyValue).equalTo(row::getModerationApiKeyValue)
            .set(moderationParams).equalTo(row::getModerationParams)
            .set(imageApiKeyValue).equalTo(row::getImageApiKeyValue)
            .set(imageParams).equalTo(row::getImageParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }

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
            .set(imageModelId).equalToWhenPresent(row::getImageModelId)
            .set(imageApiKeyName).equalToWhenPresent(row::getImageApiKeyName)
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
            .set(tgBotId).equalToWhenPresent(row::getTgBotId)
            .set(tgBotToken).equalToWhenPresent(row::getTgBotToken)
            .set(moderationApiKeyValue).equalToWhenPresent(row::getModerationApiKeyValue)
            .set(moderationParams).equalToWhenPresent(row::getModerationParams)
            .set(imageApiKeyValue).equalToWhenPresent(row::getImageApiKeyValue)
            .set(imageParams).equalToWhenPresent(row::getImageParams)
            .where(backendId, isEqualTo(row::getBackendId))
        );
    }
}