package fun.freechat.mapper;

import static fun.freechat.mapper.TgUserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.TgUser;
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
public interface TgUserMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, gmtActive, botId, tgUserId, username, firstName, lastName, lang, isBot, isPremium, phoneNumber, enabled, blocked, ext);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<TgUser> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TgUserResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_active", property="gmtActive", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="bot_id", property="botId", jdbcType=JdbcType.VARCHAR),
        @Result(column="tg_user_id", property="tgUserId", jdbcType=JdbcType.BIGINT),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="first_name", property="firstName", jdbcType=JdbcType.VARCHAR),
        @Result(column="last_name", property="lastName", jdbcType=JdbcType.VARCHAR),
        @Result(column="lang", property="lang", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_bot", property="isBot", jdbcType=JdbcType.TINYINT),
        @Result(column="is_premium", property="isPremium", jdbcType=JdbcType.TINYINT),
        @Result(column="phone_number", property="phoneNumber", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="blocked", property="blocked", jdbcType=JdbcType.TINYINT),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<TgUser> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TgUserResult")
    Optional<TgUser> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, tgUser, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, tgUser, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(TgUser row) {
        return MyBatis3Utils.insert(this::insert, row, tgUser, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(gmtActive)
            .withMappedColumn(botId)
            .withMappedColumn(tgUserId)
            .withMappedColumn(username)
            .withMappedColumn(firstName)
            .withMappedColumn(lastName)
            .withMappedColumn(lang)
            .withMappedColumn(isBot)
            .withMappedColumn(isPremium)
            .withMappedColumn(phoneNumber)
            .withMappedColumn(enabled)
            .withMappedColumn(blocked)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(TgUser row) {
        return MyBatis3Utils.insert(this::insert, row, tgUser, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(gmtActive, row::getGmtActive)
            .withMappedColumnWhenPresent(botId, row::getBotId)
            .withMappedColumnWhenPresent(tgUserId, row::getTgUserId)
            .withMappedColumnWhenPresent(username, row::getUsername)
            .withMappedColumnWhenPresent(firstName, row::getFirstName)
            .withMappedColumnWhenPresent(lastName, row::getLastName)
            .withMappedColumnWhenPresent(lang, row::getLang)
            .withMappedColumnWhenPresent(isBot, row::getIsBot)
            .withMappedColumnWhenPresent(isPremium, row::getIsPremium)
            .withMappedColumnWhenPresent(phoneNumber, row::getPhoneNumber)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
            .withMappedColumnWhenPresent(blocked, row::getBlocked)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<TgUser> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, tgUser, completer);
    }

    default List<TgUser> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, tgUser, completer);
    }

    default List<TgUser> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, tgUser, completer);
    }

    default Optional<TgUser> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, tgUser, completer);
    }

    static UpdateDSL updateAllColumns(TgUser row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtActive).equalTo(row::getGmtActive)
                .set(botId).equalTo(row::getBotId)
                .set(tgUserId).equalTo(row::getTgUserId)
                .set(username).equalTo(row::getUsername)
                .set(firstName).equalTo(row::getFirstName)
                .set(lastName).equalTo(row::getLastName)
                .set(lang).equalTo(row::getLang)
                .set(isBot).equalTo(row::getIsBot)
                .set(isPremium).equalTo(row::getIsPremium)
                .set(phoneNumber).equalTo(row::getPhoneNumber)
                .set(enabled).equalTo(row::getEnabled)
                .set(blocked).equalTo(row::getBlocked)
                .set(ext).equalTo(row::getExt);
    }

    static UpdateDSL updateSelectiveColumns(TgUser row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtActive).equalToWhenPresent(row::getGmtActive)
                .set(botId).equalToWhenPresent(row::getBotId)
                .set(tgUserId).equalToWhenPresent(row::getTgUserId)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(firstName).equalToWhenPresent(row::getFirstName)
                .set(lastName).equalToWhenPresent(row::getLastName)
                .set(lang).equalToWhenPresent(row::getLang)
                .set(isBot).equalToWhenPresent(row::getIsBot)
                .set(isPremium).equalToWhenPresent(row::getIsPremium)
                .set(phoneNumber).equalToWhenPresent(row::getPhoneNumber)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(blocked).equalToWhenPresent(row::getBlocked)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    default int updateByPrimaryKey(TgUser row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(gmtActive).equalTo(row::getGmtActive)
            .set(botId).equalTo(row::getBotId)
            .set(tgUserId).equalTo(row::getTgUserId)
            .set(username).equalTo(row::getUsername)
            .set(firstName).equalTo(row::getFirstName)
            .set(lastName).equalTo(row::getLastName)
            .set(lang).equalTo(row::getLang)
            .set(isBot).equalTo(row::getIsBot)
            .set(isPremium).equalTo(row::getIsPremium)
            .set(phoneNumber).equalTo(row::getPhoneNumber)
            .set(enabled).equalTo(row::getEnabled)
            .set(blocked).equalTo(row::getBlocked)
            .set(ext).equalTo(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(TgUser row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(gmtActive).equalToWhenPresent(row::getGmtActive)
            .set(botId).equalToWhenPresent(row::getBotId)
            .set(tgUserId).equalToWhenPresent(row::getTgUserId)
            .set(username).equalToWhenPresent(row::getUsername)
            .set(firstName).equalToWhenPresent(row::getFirstName)
            .set(lastName).equalToWhenPresent(row::getLastName)
            .set(lang).equalToWhenPresent(row::getLang)
            .set(isBot).equalToWhenPresent(row::getIsBot)
            .set(isPremium).equalToWhenPresent(row::getIsPremium)
            .set(phoneNumber).equalToWhenPresent(row::getPhoneNumber)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .set(blocked).equalToWhenPresent(row::getBlocked)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }
}