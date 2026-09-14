package fun.freechat.mapper;

import static fun.freechat.mapper.ChatMemoryCommitDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ChatMemoryCommit;
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
public interface ChatMemoryCommitMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<ChatMemoryCommit>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(attemptId, chatId, generation, operation, sourceStartId, sourceEndId, expectedCursor, expectedHead, leaseToken, status, fingerprint, schemaVersion, modelId, errorCategory, leaseUntil, gcAfter, gmtCreate, gmtModified, manifest, progress, tokenUsage);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ChatMemoryCommitResult", value = {
        @Result(column="attempt_id", property="attemptId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="chat_id", property="chatId", jdbcType=JdbcType.VARCHAR),
        @Result(column="generation", property="generation", jdbcType=JdbcType.BIGINT),
        @Result(column="operation", property="operation", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_start_id", property="sourceStartId", jdbcType=JdbcType.BIGINT),
        @Result(column="source_end_id", property="sourceEndId", jdbcType=JdbcType.BIGINT),
        @Result(column="expected_cursor", property="expectedCursor", jdbcType=JdbcType.BIGINT),
        @Result(column="expected_head", property="expectedHead", jdbcType=JdbcType.VARCHAR),
        @Result(column="lease_token", property="leaseToken", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="fingerprint", property="fingerprint", jdbcType=JdbcType.VARCHAR),
        @Result(column="schema_version", property="schemaVersion", jdbcType=JdbcType.INTEGER),
        @Result(column="model_id", property="modelId", jdbcType=JdbcType.VARCHAR),
        @Result(column="error_category", property="errorCategory", jdbcType=JdbcType.VARCHAR),
        @Result(column="lease_until", property="leaseUntil", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gc_after", property="gcAfter", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="manifest", property="manifest", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="progress", property="progress", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="token_usage", property="tokenUsage", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<ChatMemoryCommit> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ChatMemoryCommitResult")
    Optional<ChatMemoryCommit> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, chatMemoryCommit, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, chatMemoryCommit, completer);
    }

    default int deleteByPrimaryKey(String attemptId_) {
        return delete(c -> 
            c.where(attemptId, isEqualTo(attemptId_))
        );
    }

    default int insert(ChatMemoryCommit row) {
        return MyBatis3Utils.insert(this::insert, row, chatMemoryCommit, c ->
            c.withMappedColumn(attemptId)
            .withMappedColumn(chatId)
            .withMappedColumn(generation)
            .withMappedColumn(operation)
            .withMappedColumn(sourceStartId)
            .withMappedColumn(sourceEndId)
            .withMappedColumn(expectedCursor)
            .withMappedColumn(expectedHead)
            .withMappedColumn(leaseToken)
            .withMappedColumn(status)
            .withMappedColumn(fingerprint)
            .withMappedColumn(schemaVersion)
            .withMappedColumn(modelId)
            .withMappedColumn(errorCategory)
            .withMappedColumn(leaseUntil)
            .withMappedColumn(gcAfter)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(manifest)
            .withMappedColumn(progress)
            .withMappedColumn(tokenUsage)
        );
    }

    default int insertMultiple(Collection<ChatMemoryCommit> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, chatMemoryCommit, c ->
            c.withMappedColumn(attemptId)
            .withMappedColumn(chatId)
            .withMappedColumn(generation)
            .withMappedColumn(operation)
            .withMappedColumn(sourceStartId)
            .withMappedColumn(sourceEndId)
            .withMappedColumn(expectedCursor)
            .withMappedColumn(expectedHead)
            .withMappedColumn(leaseToken)
            .withMappedColumn(status)
            .withMappedColumn(fingerprint)
            .withMappedColumn(schemaVersion)
            .withMappedColumn(modelId)
            .withMappedColumn(errorCategory)
            .withMappedColumn(leaseUntil)
            .withMappedColumn(gcAfter)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(manifest)
            .withMappedColumn(progress)
            .withMappedColumn(tokenUsage)
        );
    }

    default int insertSelective(ChatMemoryCommit row) {
        return MyBatis3Utils.insert(this::insert, row, chatMemoryCommit, c ->
            c.withMappedColumnWhenPresent(attemptId, row::getAttemptId)
            .withMappedColumnWhenPresent(chatId, row::getChatId)
            .withMappedColumnWhenPresent(generation, row::getGeneration)
            .withMappedColumnWhenPresent(operation, row::getOperation)
            .withMappedColumnWhenPresent(sourceStartId, row::getSourceStartId)
            .withMappedColumnWhenPresent(sourceEndId, row::getSourceEndId)
            .withMappedColumnWhenPresent(expectedCursor, row::getExpectedCursor)
            .withMappedColumnWhenPresent(expectedHead, row::getExpectedHead)
            .withMappedColumnWhenPresent(leaseToken, row::getLeaseToken)
            .withMappedColumnWhenPresent(status, row::getStatus)
            .withMappedColumnWhenPresent(fingerprint, row::getFingerprint)
            .withMappedColumnWhenPresent(schemaVersion, row::getSchemaVersion)
            .withMappedColumnWhenPresent(modelId, row::getModelId)
            .withMappedColumnWhenPresent(errorCategory, row::getErrorCategory)
            .withMappedColumnWhenPresent(leaseUntil, row::getLeaseUntil)
            .withMappedColumnWhenPresent(gcAfter, row::getGcAfter)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(manifest, row::getManifest)
            .withMappedColumnWhenPresent(progress, row::getProgress)
            .withMappedColumnWhenPresent(tokenUsage, row::getTokenUsage)
        );
    }

    default Optional<ChatMemoryCommit> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, chatMemoryCommit, completer);
    }

    default List<ChatMemoryCommit> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, chatMemoryCommit, completer);
    }

    default List<ChatMemoryCommit> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, chatMemoryCommit, completer);
    }

    default Optional<ChatMemoryCommit> selectByPrimaryKey(String attemptId_) {
        return selectOne(c ->
            c.where(attemptId, isEqualTo(attemptId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, chatMemoryCommit, completer);
    }

    static UpdateDSL updateAllColumns(ChatMemoryCommit row, UpdateDSL dsl) {
        return dsl.set(attemptId).equalTo(row::getAttemptId)
                .set(chatId).equalTo(row::getChatId)
                .set(generation).equalTo(row::getGeneration)
                .set(operation).equalTo(row::getOperation)
                .set(sourceStartId).equalTo(row::getSourceStartId)
                .set(sourceEndId).equalTo(row::getSourceEndId)
                .set(expectedCursor).equalTo(row::getExpectedCursor)
                .set(expectedHead).equalTo(row::getExpectedHead)
                .set(leaseToken).equalTo(row::getLeaseToken)
                .set(status).equalTo(row::getStatus)
                .set(fingerprint).equalTo(row::getFingerprint)
                .set(schemaVersion).equalTo(row::getSchemaVersion)
                .set(modelId).equalTo(row::getModelId)
                .set(errorCategory).equalTo(row::getErrorCategory)
                .set(leaseUntil).equalTo(row::getLeaseUntil)
                .set(gcAfter).equalTo(row::getGcAfter)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(manifest).equalTo(row::getManifest)
                .set(progress).equalTo(row::getProgress)
                .set(tokenUsage).equalTo(row::getTokenUsage);
    }

    static UpdateDSL updateSelectiveColumns(ChatMemoryCommit row, UpdateDSL dsl) {
        return dsl.set(attemptId).equalToWhenPresent(row::getAttemptId)
                .set(chatId).equalToWhenPresent(row::getChatId)
                .set(generation).equalToWhenPresent(row::getGeneration)
                .set(operation).equalToWhenPresent(row::getOperation)
                .set(sourceStartId).equalToWhenPresent(row::getSourceStartId)
                .set(sourceEndId).equalToWhenPresent(row::getSourceEndId)
                .set(expectedCursor).equalToWhenPresent(row::getExpectedCursor)
                .set(expectedHead).equalToWhenPresent(row::getExpectedHead)
                .set(leaseToken).equalToWhenPresent(row::getLeaseToken)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(fingerprint).equalToWhenPresent(row::getFingerprint)
                .set(schemaVersion).equalToWhenPresent(row::getSchemaVersion)
                .set(modelId).equalToWhenPresent(row::getModelId)
                .set(errorCategory).equalToWhenPresent(row::getErrorCategory)
                .set(leaseUntil).equalToWhenPresent(row::getLeaseUntil)
                .set(gcAfter).equalToWhenPresent(row::getGcAfter)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(manifest).equalToWhenPresent(row::getManifest)
                .set(progress).equalToWhenPresent(row::getProgress)
                .set(tokenUsage).equalToWhenPresent(row::getTokenUsage);
    }

    default int updateByPrimaryKey(ChatMemoryCommit row) {
        return update(c ->
            c.set(chatId).equalTo(row::getChatId)
            .set(generation).equalTo(row::getGeneration)
            .set(operation).equalTo(row::getOperation)
            .set(sourceStartId).equalTo(row::getSourceStartId)
            .set(sourceEndId).equalTo(row::getSourceEndId)
            .set(expectedCursor).equalTo(row::getExpectedCursor)
            .set(expectedHead).equalTo(row::getExpectedHead)
            .set(leaseToken).equalTo(row::getLeaseToken)
            .set(status).equalTo(row::getStatus)
            .set(fingerprint).equalTo(row::getFingerprint)
            .set(schemaVersion).equalTo(row::getSchemaVersion)
            .set(modelId).equalTo(row::getModelId)
            .set(errorCategory).equalTo(row::getErrorCategory)
            .set(leaseUntil).equalTo(row::getLeaseUntil)
            .set(gcAfter).equalTo(row::getGcAfter)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(manifest).equalTo(row::getManifest)
            .set(progress).equalTo(row::getProgress)
            .set(tokenUsage).equalTo(row::getTokenUsage)
            .where(attemptId, isEqualTo(row::getAttemptId))
        );
    }

    default int updateByPrimaryKeySelective(ChatMemoryCommit row) {
        return update(c ->
            c.set(chatId).equalToWhenPresent(row::getChatId)
            .set(generation).equalToWhenPresent(row::getGeneration)
            .set(operation).equalToWhenPresent(row::getOperation)
            .set(sourceStartId).equalToWhenPresent(row::getSourceStartId)
            .set(sourceEndId).equalToWhenPresent(row::getSourceEndId)
            .set(expectedCursor).equalToWhenPresent(row::getExpectedCursor)
            .set(expectedHead).equalToWhenPresent(row::getExpectedHead)
            .set(leaseToken).equalToWhenPresent(row::getLeaseToken)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(fingerprint).equalToWhenPresent(row::getFingerprint)
            .set(schemaVersion).equalToWhenPresent(row::getSchemaVersion)
            .set(modelId).equalToWhenPresent(row::getModelId)
            .set(errorCategory).equalToWhenPresent(row::getErrorCategory)
            .set(leaseUntil).equalToWhenPresent(row::getLeaseUntil)
            .set(gcAfter).equalToWhenPresent(row::getGcAfter)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(manifest).equalToWhenPresent(row::getManifest)
            .set(progress).equalToWhenPresent(row::getProgress)
            .set(tokenUsage).equalToWhenPresent(row::getTokenUsage)
            .where(attemptId, isEqualTo(row::getAttemptId))
        );
    }
}