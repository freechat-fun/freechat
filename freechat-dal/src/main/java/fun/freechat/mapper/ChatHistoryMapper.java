package fun.freechat.mapper;

import static fun.freechat.mapper.ChatHistoryDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ChatHistory;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSL;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface ChatHistoryMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, memoryId, gmtCreate, gmtModified, enabled, tgMessageId, turnId, recordKind, messageOrigin, systemMessageRef, episode, message, ext, sourceMessage);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<ChatHistory> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ChatHistoryResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="memory_id", property="memoryId", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="tg_message_id", property="tgMessageId", jdbcType=JdbcType.BIGINT),
        @Result(column="turn_id", property="turnId", jdbcType=JdbcType.VARCHAR),
        @Result(column="record_kind", property="recordKind", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_origin", property="messageOrigin", jdbcType=JdbcType.VARCHAR),
        @Result(column="system_message_ref", property="systemMessageRef", jdbcType=JdbcType.VARCHAR),
        @Result(column="episode", property="episode", jdbcType=JdbcType.BIGINT),
        @Result(column="message", property="message", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="source_message", property="sourceMessage", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<ChatHistory> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ChatHistoryResult")
    Optional<ChatHistory> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, chatHistory, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, chatHistory, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(ChatHistory row) {
        return MyBatis3Utils.insert(this::insert, row, chatHistory, c ->
            c.withMappedColumn(memoryId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(enabled)
            .withMappedColumn(tgMessageId)
            .withMappedColumn(turnId)
            .withMappedColumn(recordKind)
            .withMappedColumn(messageOrigin)
            .withMappedColumn(systemMessageRef)
            .withMappedColumn(episode)
            .withMappedColumn(message)
            .withMappedColumn(ext)
            .withMappedColumn(sourceMessage)
        );
    }

    default int insertSelective(ChatHistory row) {
        return MyBatis3Utils.insert(this::insert, row, chatHistory, c ->
            c.withMappedColumnWhenPresent(memoryId, row::getMemoryId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
            .withMappedColumnWhenPresent(tgMessageId, row::getTgMessageId)
            .withMappedColumnWhenPresent(turnId, row::getTurnId)
            .withMappedColumnWhenPresent(recordKind, row::getRecordKind)
            .withMappedColumnWhenPresent(messageOrigin, row::getMessageOrigin)
            .withMappedColumnWhenPresent(systemMessageRef, row::getSystemMessageRef)
            .withMappedColumnWhenPresent(episode, row::getEpisode)
            .withMappedColumnWhenPresent(message, row::getMessage)
            .withMappedColumnWhenPresent(ext, row::getExt)
            .withMappedColumnWhenPresent(sourceMessage, row::getSourceMessage)
        );
    }

    default Optional<ChatHistory> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, chatHistory, completer);
    }

    default List<ChatHistory> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, chatHistory, completer);
    }

    default List<ChatHistory> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, chatHistory, completer);
    }

    default Optional<ChatHistory> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, chatHistory, completer);
    }

    static UpdateDSL updateAllColumns(ChatHistory row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(memoryId).equalTo(row::getMemoryId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(enabled).equalTo(row::getEnabled)
                .set(tgMessageId).equalTo(row::getTgMessageId)
                .set(turnId).equalTo(row::getTurnId)
                .set(recordKind).equalTo(row::getRecordKind)
                .set(messageOrigin).equalTo(row::getMessageOrigin)
                .set(systemMessageRef).equalTo(row::getSystemMessageRef)
                .set(episode).equalTo(row::getEpisode)
                .set(message).equalTo(row::getMessage)
                .set(ext).equalTo(row::getExt)
                .set(sourceMessage).equalTo(row::getSourceMessage);
    }

    static UpdateDSL updateSelectiveColumns(ChatHistory row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(memoryId).equalToWhenPresent(row::getMemoryId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(tgMessageId).equalToWhenPresent(row::getTgMessageId)
                .set(turnId).equalToWhenPresent(row::getTurnId)
                .set(recordKind).equalToWhenPresent(row::getRecordKind)
                .set(messageOrigin).equalToWhenPresent(row::getMessageOrigin)
                .set(systemMessageRef).equalToWhenPresent(row::getSystemMessageRef)
                .set(episode).equalToWhenPresent(row::getEpisode)
                .set(message).equalToWhenPresent(row::getMessage)
                .set(ext).equalToWhenPresent(row::getExt)
                .set(sourceMessage).equalToWhenPresent(row::getSourceMessage);
    }

    default int updateByPrimaryKey(ChatHistory row) {
        return update(c ->
            c.set(memoryId).equalTo(row::getMemoryId)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(enabled).equalTo(row::getEnabled)
            .set(tgMessageId).equalTo(row::getTgMessageId)
            .set(turnId).equalTo(row::getTurnId)
            .set(recordKind).equalTo(row::getRecordKind)
            .set(messageOrigin).equalTo(row::getMessageOrigin)
            .set(systemMessageRef).equalTo(row::getSystemMessageRef)
            .set(episode).equalTo(row::getEpisode)
            .set(message).equalTo(row::getMessage)
            .set(ext).equalTo(row::getExt)
            .set(sourceMessage).equalTo(row::getSourceMessage)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(ChatHistory row) {
        return update(c ->
            c.set(memoryId).equalToWhenPresent(row::getMemoryId)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .set(tgMessageId).equalToWhenPresent(row::getTgMessageId)
            .set(turnId).equalToWhenPresent(row::getTurnId)
            .set(recordKind).equalToWhenPresent(row::getRecordKind)
            .set(messageOrigin).equalToWhenPresent(row::getMessageOrigin)
            .set(systemMessageRef).equalToWhenPresent(row::getSystemMessageRef)
            .set(episode).equalToWhenPresent(row::getEpisode)
            .set(message).equalToWhenPresent(row::getMessage)
            .set(ext).equalToWhenPresent(row::getExt)
            .set(sourceMessage).equalToWhenPresent(row::getSourceMessage)
            .where(id, isEqualTo(row::getId))
        );
    }
}