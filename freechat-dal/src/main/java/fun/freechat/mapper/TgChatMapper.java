package fun.freechat.mapper;

import static fun.freechat.mapper.TgChatDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.TgChat;
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
public interface TgChatMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<TgChat>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(chatId, gmtCreate, gmtModified, backendId, tgChatId, chatType, title, ext);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TgChatResult", value = {
        @Result(column="chat_id", property="chatId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="backend_id", property="backendId", jdbcType=JdbcType.VARCHAR),
        @Result(column="tg_chat_id", property="tgChatId", jdbcType=JdbcType.BIGINT),
        @Result(column="chat_type", property="chatType", jdbcType=JdbcType.VARCHAR),
        @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<TgChat> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TgChatResult")
    Optional<TgChat> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, tgChat, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, tgChat, completer);
    }

    default int deleteByPrimaryKey(String chatId_) {
        return delete(c -> 
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    default int insert(TgChat row) {
        return MyBatis3Utils.insert(this::insert, row, tgChat, c ->
            c.withMappedColumn(chatId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(backendId)
            .withMappedColumn(tgChatId)
            .withMappedColumn(chatType)
            .withMappedColumn(title)
            .withMappedColumn(ext)
        );
    }

    default int insertMultiple(Collection<TgChat> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, tgChat, c ->
            c.withMappedColumn(chatId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(backendId)
            .withMappedColumn(tgChatId)
            .withMappedColumn(chatType)
            .withMappedColumn(title)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(TgChat row) {
        return MyBatis3Utils.insert(this::insert, row, tgChat, c ->
            c.withMappedColumnWhenPresent(chatId, row::getChatId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(backendId, row::getBackendId)
            .withMappedColumnWhenPresent(tgChatId, row::getTgChatId)
            .withMappedColumnWhenPresent(chatType, row::getChatType)
            .withMappedColumnWhenPresent(title, row::getTitle)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<TgChat> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, tgChat, completer);
    }

    default List<TgChat> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, tgChat, completer);
    }

    default List<TgChat> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, tgChat, completer);
    }

    default Optional<TgChat> selectByPrimaryKey(String chatId_) {
        return selectOne(c ->
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, tgChat, completer);
    }

    static UpdateDSL updateAllColumns(TgChat row, UpdateDSL dsl) {
        return dsl.set(chatId).equalTo(row::getChatId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(backendId).equalTo(row::getBackendId)
                .set(tgChatId).equalTo(row::getTgChatId)
                .set(chatType).equalTo(row::getChatType)
                .set(title).equalTo(row::getTitle)
                .set(ext).equalTo(row::getExt);
    }

    static UpdateDSL updateSelectiveColumns(TgChat row, UpdateDSL dsl) {
        return dsl.set(chatId).equalToWhenPresent(row::getChatId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(backendId).equalToWhenPresent(row::getBackendId)
                .set(tgChatId).equalToWhenPresent(row::getTgChatId)
                .set(chatType).equalToWhenPresent(row::getChatType)
                .set(title).equalToWhenPresent(row::getTitle)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    default int updateByPrimaryKey(TgChat row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(backendId).equalTo(row::getBackendId)
            .set(tgChatId).equalTo(row::getTgChatId)
            .set(chatType).equalTo(row::getChatType)
            .set(title).equalTo(row::getTitle)
            .set(ext).equalTo(row::getExt)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }

    default int updateByPrimaryKeySelective(TgChat row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(backendId).equalToWhenPresent(row::getBackendId)
            .set(tgChatId).equalToWhenPresent(row::getTgChatId)
            .set(chatType).equalToWhenPresent(row::getChatType)
            .set(title).equalToWhenPresent(row::getTitle)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }
}