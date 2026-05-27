package fun.freechat.mapper;

import static fun.freechat.mapper.TgMessageDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.TgMessage;
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
public interface TgMessageMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, chatId, tgMessageId, tgUserId, direction, messageType, content, payload, ext);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<TgMessage> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TgMessageResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="chat_id", property="chatId", jdbcType=JdbcType.VARCHAR),
        @Result(column="tg_message_id", property="tgMessageId", jdbcType=JdbcType.BIGINT),
        @Result(column="tg_user_id", property="tgUserId", jdbcType=JdbcType.BIGINT),
        @Result(column="direction", property="direction", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_type", property="messageType", jdbcType=JdbcType.VARCHAR),
        @Result(column="content", property="content", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="payload", property="payload", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<TgMessage> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TgMessageResult")
    Optional<TgMessage> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, tgMessage, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, tgMessage, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(TgMessage row) {
        return MyBatis3Utils.insert(this::insert, row, tgMessage, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(chatId)
            .withMappedColumn(tgMessageId)
            .withMappedColumn(tgUserId)
            .withMappedColumn(direction)
            .withMappedColumn(messageType)
            .withMappedColumn(content)
            .withMappedColumn(payload)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(TgMessage row) {
        return MyBatis3Utils.insert(this::insert, row, tgMessage, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(chatId, row::getChatId)
            .withMappedColumnWhenPresent(tgMessageId, row::getTgMessageId)
            .withMappedColumnWhenPresent(tgUserId, row::getTgUserId)
            .withMappedColumnWhenPresent(direction, row::getDirection)
            .withMappedColumnWhenPresent(messageType, row::getMessageType)
            .withMappedColumnWhenPresent(content, row::getContent)
            .withMappedColumnWhenPresent(payload, row::getPayload)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<TgMessage> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, tgMessage, completer);
    }

    default List<TgMessage> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, tgMessage, completer);
    }

    default List<TgMessage> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, tgMessage, completer);
    }

    default Optional<TgMessage> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, tgMessage, completer);
    }

    static UpdateDSL updateAllColumns(TgMessage row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(chatId).equalTo(row::getChatId)
                .set(tgMessageId).equalTo(row::getTgMessageId)
                .set(tgUserId).equalTo(row::getTgUserId)
                .set(direction).equalTo(row::getDirection)
                .set(messageType).equalTo(row::getMessageType)
                .set(content).equalTo(row::getContent)
                .set(payload).equalTo(row::getPayload)
                .set(ext).equalTo(row::getExt);
    }

    static UpdateDSL updateSelectiveColumns(TgMessage row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(chatId).equalToWhenPresent(row::getChatId)
                .set(tgMessageId).equalToWhenPresent(row::getTgMessageId)
                .set(tgUserId).equalToWhenPresent(row::getTgUserId)
                .set(direction).equalToWhenPresent(row::getDirection)
                .set(messageType).equalToWhenPresent(row::getMessageType)
                .set(content).equalToWhenPresent(row::getContent)
                .set(payload).equalToWhenPresent(row::getPayload)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    default int updateByPrimaryKey(TgMessage row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(chatId).equalTo(row::getChatId)
            .set(tgMessageId).equalTo(row::getTgMessageId)
            .set(tgUserId).equalTo(row::getTgUserId)
            .set(direction).equalTo(row::getDirection)
            .set(messageType).equalTo(row::getMessageType)
            .set(content).equalTo(row::getContent)
            .set(payload).equalTo(row::getPayload)
            .set(ext).equalTo(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(TgMessage row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(chatId).equalToWhenPresent(row::getChatId)
            .set(tgMessageId).equalToWhenPresent(row::getTgMessageId)
            .set(tgUserId).equalToWhenPresent(row::getTgUserId)
            .set(direction).equalToWhenPresent(row::getDirection)
            .set(messageType).equalToWhenPresent(row::getMessageType)
            .set(content).equalToWhenPresent(row::getContent)
            .set(payload).equalToWhenPresent(row::getPayload)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }
}