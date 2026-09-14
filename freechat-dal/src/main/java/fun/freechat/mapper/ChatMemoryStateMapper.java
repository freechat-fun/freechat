package fun.freechat.mapper;

import static fun.freechat.mapper.ChatMemoryStateDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ChatMemoryState;
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
public interface ChatMemoryStateMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<ChatMemoryState>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(chatId, userId, characterUid, storeType, generation, version, status, fingerprint, lastActivity, latestFinalizedId, episode, overflowThroughId, idleThroughId, summaryId, profileId, profileRevalidationPending, turnToken, turnLeaseUntil, turnDeadline, turnRevision, dueAt, retryAt, retryAttempts, claimToken, claimLeaseUntil, claimDeadline, reconciledThroughId, gmtCreate, gmtModified);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ChatMemoryStateResult", value = {
        @Result(column="chat_id", property="chatId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="character_uid", property="characterUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="store_type", property="storeType", jdbcType=JdbcType.VARCHAR),
        @Result(column="generation", property="generation", jdbcType=JdbcType.BIGINT),
        @Result(column="version", property="version", jdbcType=JdbcType.BIGINT),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="fingerprint", property="fingerprint", jdbcType=JdbcType.VARCHAR),
        @Result(column="last_activity", property="lastActivity", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="latest_finalized_id", property="latestFinalizedId", jdbcType=JdbcType.BIGINT),
        @Result(column="episode", property="episode", jdbcType=JdbcType.BIGINT),
        @Result(column="overflow_through_id", property="overflowThroughId", jdbcType=JdbcType.BIGINT),
        @Result(column="idle_through_id", property="idleThroughId", jdbcType=JdbcType.BIGINT),
        @Result(column="summary_id", property="summaryId", jdbcType=JdbcType.VARCHAR),
        @Result(column="profile_id", property="profileId", jdbcType=JdbcType.VARCHAR),
        @Result(column="profile_revalidation_pending", property="profileRevalidationPending", jdbcType=JdbcType.TINYINT),
        @Result(column="turn_token", property="turnToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="turn_lease_until", property="turnLeaseUntil", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="turn_deadline", property="turnDeadline", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="turn_revision", property="turnRevision", jdbcType=JdbcType.BIGINT),
        @Result(column="due_at", property="dueAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="retry_at", property="retryAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="retry_attempts", property="retryAttempts", jdbcType=JdbcType.INTEGER),
        @Result(column="claim_token", property="claimToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="claim_lease_until", property="claimLeaseUntil", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="claim_deadline", property="claimDeadline", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="reconciled_through_id", property="reconciledThroughId", jdbcType=JdbcType.BIGINT),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP)
    })
    List<ChatMemoryState> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ChatMemoryStateResult")
    Optional<ChatMemoryState> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, chatMemoryState, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, chatMemoryState, completer);
    }

    default int deleteByPrimaryKey(String chatId_) {
        return delete(c -> 
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    default int insert(ChatMemoryState row) {
        return MyBatis3Utils.insert(this::insert, row, chatMemoryState, c ->
            c.withMappedColumn(chatId)
            .withMappedColumn(userId)
            .withMappedColumn(characterUid)
            .withMappedColumn(storeType)
            .withMappedColumn(generation)
            .withMappedColumn(version)
            .withMappedColumn(status)
            .withMappedColumn(fingerprint)
            .withMappedColumn(lastActivity)
            .withMappedColumn(latestFinalizedId)
            .withMappedColumn(episode)
            .withMappedColumn(overflowThroughId)
            .withMappedColumn(idleThroughId)
            .withMappedColumn(summaryId)
            .withMappedColumn(profileId)
            .withMappedColumn(profileRevalidationPending)
            .withMappedColumn(turnToken)
            .withMappedColumn(turnLeaseUntil)
            .withMappedColumn(turnDeadline)
            .withMappedColumn(turnRevision)
            .withMappedColumn(dueAt)
            .withMappedColumn(retryAt)
            .withMappedColumn(retryAttempts)
            .withMappedColumn(claimToken)
            .withMappedColumn(claimLeaseUntil)
            .withMappedColumn(claimDeadline)
            .withMappedColumn(reconciledThroughId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
        );
    }

    default int insertMultiple(Collection<ChatMemoryState> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, chatMemoryState, c ->
            c.withMappedColumn(chatId)
            .withMappedColumn(userId)
            .withMappedColumn(characterUid)
            .withMappedColumn(storeType)
            .withMappedColumn(generation)
            .withMappedColumn(version)
            .withMappedColumn(status)
            .withMappedColumn(fingerprint)
            .withMappedColumn(lastActivity)
            .withMappedColumn(latestFinalizedId)
            .withMappedColumn(episode)
            .withMappedColumn(overflowThroughId)
            .withMappedColumn(idleThroughId)
            .withMappedColumn(summaryId)
            .withMappedColumn(profileId)
            .withMappedColumn(profileRevalidationPending)
            .withMappedColumn(turnToken)
            .withMappedColumn(turnLeaseUntil)
            .withMappedColumn(turnDeadline)
            .withMappedColumn(turnRevision)
            .withMappedColumn(dueAt)
            .withMappedColumn(retryAt)
            .withMappedColumn(retryAttempts)
            .withMappedColumn(claimToken)
            .withMappedColumn(claimLeaseUntil)
            .withMappedColumn(claimDeadline)
            .withMappedColumn(reconciledThroughId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
        );
    }

    default int insertSelective(ChatMemoryState row) {
        return MyBatis3Utils.insert(this::insert, row, chatMemoryState, c ->
            c.withMappedColumnWhenPresent(chatId, row::getChatId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(characterUid, row::getCharacterUid)
            .withMappedColumnWhenPresent(storeType, row::getStoreType)
            .withMappedColumnWhenPresent(generation, row::getGeneration)
            .withMappedColumnWhenPresent(version, row::getVersion)
            .withMappedColumnWhenPresent(status, row::getStatus)
            .withMappedColumnWhenPresent(fingerprint, row::getFingerprint)
            .withMappedColumnWhenPresent(lastActivity, row::getLastActivity)
            .withMappedColumnWhenPresent(latestFinalizedId, row::getLatestFinalizedId)
            .withMappedColumnWhenPresent(episode, row::getEpisode)
            .withMappedColumnWhenPresent(overflowThroughId, row::getOverflowThroughId)
            .withMappedColumnWhenPresent(idleThroughId, row::getIdleThroughId)
            .withMappedColumnWhenPresent(summaryId, row::getSummaryId)
            .withMappedColumnWhenPresent(profileId, row::getProfileId)
            .withMappedColumnWhenPresent(profileRevalidationPending, row::getProfileRevalidationPending)
            .withMappedColumnWhenPresent(turnToken, row::getTurnToken)
            .withMappedColumnWhenPresent(turnLeaseUntil, row::getTurnLeaseUntil)
            .withMappedColumnWhenPresent(turnDeadline, row::getTurnDeadline)
            .withMappedColumnWhenPresent(turnRevision, row::getTurnRevision)
            .withMappedColumnWhenPresent(dueAt, row::getDueAt)
            .withMappedColumnWhenPresent(retryAt, row::getRetryAt)
            .withMappedColumnWhenPresent(retryAttempts, row::getRetryAttempts)
            .withMappedColumnWhenPresent(claimToken, row::getClaimToken)
            .withMappedColumnWhenPresent(claimLeaseUntil, row::getClaimLeaseUntil)
            .withMappedColumnWhenPresent(claimDeadline, row::getClaimDeadline)
            .withMappedColumnWhenPresent(reconciledThroughId, row::getReconciledThroughId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
        );
    }

    default Optional<ChatMemoryState> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, chatMemoryState, completer);
    }

    default List<ChatMemoryState> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, chatMemoryState, completer);
    }

    default List<ChatMemoryState> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, chatMemoryState, completer);
    }

    default Optional<ChatMemoryState> selectByPrimaryKey(String chatId_) {
        return selectOne(c ->
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, chatMemoryState, completer);
    }

    static UpdateDSL updateAllColumns(ChatMemoryState row, UpdateDSL dsl) {
        return dsl.set(chatId).equalTo(row::getChatId)
                .set(userId).equalTo(row::getUserId)
                .set(characterUid).equalTo(row::getCharacterUid)
                .set(storeType).equalTo(row::getStoreType)
                .set(generation).equalTo(row::getGeneration)
                .set(version).equalTo(row::getVersion)
                .set(status).equalTo(row::getStatus)
                .set(fingerprint).equalTo(row::getFingerprint)
                .set(lastActivity).equalTo(row::getLastActivity)
                .set(latestFinalizedId).equalTo(row::getLatestFinalizedId)
                .set(episode).equalTo(row::getEpisode)
                .set(overflowThroughId).equalTo(row::getOverflowThroughId)
                .set(idleThroughId).equalTo(row::getIdleThroughId)
                .set(summaryId).equalTo(row::getSummaryId)
                .set(profileId).equalTo(row::getProfileId)
                .set(profileRevalidationPending).equalTo(row::getProfileRevalidationPending)
                .set(turnToken).equalTo(row::getTurnToken)
                .set(turnLeaseUntil).equalTo(row::getTurnLeaseUntil)
                .set(turnDeadline).equalTo(row::getTurnDeadline)
                .set(turnRevision).equalTo(row::getTurnRevision)
                .set(dueAt).equalTo(row::getDueAt)
                .set(retryAt).equalTo(row::getRetryAt)
                .set(retryAttempts).equalTo(row::getRetryAttempts)
                .set(claimToken).equalTo(row::getClaimToken)
                .set(claimLeaseUntil).equalTo(row::getClaimLeaseUntil)
                .set(claimDeadline).equalTo(row::getClaimDeadline)
                .set(reconciledThroughId).equalTo(row::getReconciledThroughId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified);
    }

    static UpdateDSL updateSelectiveColumns(ChatMemoryState row, UpdateDSL dsl) {
        return dsl.set(chatId).equalToWhenPresent(row::getChatId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(characterUid).equalToWhenPresent(row::getCharacterUid)
                .set(storeType).equalToWhenPresent(row::getStoreType)
                .set(generation).equalToWhenPresent(row::getGeneration)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(fingerprint).equalToWhenPresent(row::getFingerprint)
                .set(lastActivity).equalToWhenPresent(row::getLastActivity)
                .set(latestFinalizedId).equalToWhenPresent(row::getLatestFinalizedId)
                .set(episode).equalToWhenPresent(row::getEpisode)
                .set(overflowThroughId).equalToWhenPresent(row::getOverflowThroughId)
                .set(idleThroughId).equalToWhenPresent(row::getIdleThroughId)
                .set(summaryId).equalToWhenPresent(row::getSummaryId)
                .set(profileId).equalToWhenPresent(row::getProfileId)
                .set(profileRevalidationPending).equalToWhenPresent(row::getProfileRevalidationPending)
                .set(turnToken).equalToWhenPresent(row::getTurnToken)
                .set(turnLeaseUntil).equalToWhenPresent(row::getTurnLeaseUntil)
                .set(turnDeadline).equalToWhenPresent(row::getTurnDeadline)
                .set(turnRevision).equalToWhenPresent(row::getTurnRevision)
                .set(dueAt).equalToWhenPresent(row::getDueAt)
                .set(retryAt).equalToWhenPresent(row::getRetryAt)
                .set(retryAttempts).equalToWhenPresent(row::getRetryAttempts)
                .set(claimToken).equalToWhenPresent(row::getClaimToken)
                .set(claimLeaseUntil).equalToWhenPresent(row::getClaimLeaseUntil)
                .set(claimDeadline).equalToWhenPresent(row::getClaimDeadline)
                .set(reconciledThroughId).equalToWhenPresent(row::getReconciledThroughId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified);
    }

    default int updateByPrimaryKey(ChatMemoryState row) {
        return update(c ->
            c.set(userId).equalTo(row::getUserId)
            .set(characterUid).equalTo(row::getCharacterUid)
            .set(storeType).equalTo(row::getStoreType)
            .set(generation).equalTo(row::getGeneration)
            .set(version).equalTo(row::getVersion)
            .set(status).equalTo(row::getStatus)
            .set(fingerprint).equalTo(row::getFingerprint)
            .set(lastActivity).equalTo(row::getLastActivity)
            .set(latestFinalizedId).equalTo(row::getLatestFinalizedId)
            .set(episode).equalTo(row::getEpisode)
            .set(overflowThroughId).equalTo(row::getOverflowThroughId)
            .set(idleThroughId).equalTo(row::getIdleThroughId)
            .set(summaryId).equalTo(row::getSummaryId)
            .set(profileId).equalTo(row::getProfileId)
            .set(profileRevalidationPending).equalTo(row::getProfileRevalidationPending)
            .set(turnToken).equalTo(row::getTurnToken)
            .set(turnLeaseUntil).equalTo(row::getTurnLeaseUntil)
            .set(turnDeadline).equalTo(row::getTurnDeadline)
            .set(turnRevision).equalTo(row::getTurnRevision)
            .set(dueAt).equalTo(row::getDueAt)
            .set(retryAt).equalTo(row::getRetryAt)
            .set(retryAttempts).equalTo(row::getRetryAttempts)
            .set(claimToken).equalTo(row::getClaimToken)
            .set(claimLeaseUntil).equalTo(row::getClaimLeaseUntil)
            .set(claimDeadline).equalTo(row::getClaimDeadline)
            .set(reconciledThroughId).equalTo(row::getReconciledThroughId)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }

    default int updateByPrimaryKeySelective(ChatMemoryState row) {
        return update(c ->
            c.set(userId).equalToWhenPresent(row::getUserId)
            .set(characterUid).equalToWhenPresent(row::getCharacterUid)
            .set(storeType).equalToWhenPresent(row::getStoreType)
            .set(generation).equalToWhenPresent(row::getGeneration)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(fingerprint).equalToWhenPresent(row::getFingerprint)
            .set(lastActivity).equalToWhenPresent(row::getLastActivity)
            .set(latestFinalizedId).equalToWhenPresent(row::getLatestFinalizedId)
            .set(episode).equalToWhenPresent(row::getEpisode)
            .set(overflowThroughId).equalToWhenPresent(row::getOverflowThroughId)
            .set(idleThroughId).equalToWhenPresent(row::getIdleThroughId)
            .set(summaryId).equalToWhenPresent(row::getSummaryId)
            .set(profileId).equalToWhenPresent(row::getProfileId)
            .set(profileRevalidationPending).equalToWhenPresent(row::getProfileRevalidationPending)
            .set(turnToken).equalToWhenPresent(row::getTurnToken)
            .set(turnLeaseUntil).equalToWhenPresent(row::getTurnLeaseUntil)
            .set(turnDeadline).equalToWhenPresent(row::getTurnDeadline)
            .set(turnRevision).equalToWhenPresent(row::getTurnRevision)
            .set(dueAt).equalToWhenPresent(row::getDueAt)
            .set(retryAt).equalToWhenPresent(row::getRetryAt)
            .set(retryAttempts).equalToWhenPresent(row::getRetryAttempts)
            .set(claimToken).equalToWhenPresent(row::getClaimToken)
            .set(claimLeaseUntil).equalToWhenPresent(row::getClaimLeaseUntil)
            .set(claimDeadline).equalToWhenPresent(row::getClaimDeadline)
            .set(reconciledThroughId).equalToWhenPresent(row::getReconciledThroughId)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }
}