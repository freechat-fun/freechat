package fun.freechat.mapper;

import static fun.freechat.mapper.TgBotDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.TgBot;
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
public interface TgBotMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<TgBot>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(botId, gmtCreate, gmtModified, tgBotId, username, name, token, webhookUrl, webhookSecret, enabled, description, allowedUpdates, commands, ext);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TgBotResult", value = {
        @Result(column="bot_id", property="botId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="tg_bot_id", property="tgBotId", jdbcType=JdbcType.BIGINT),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="token", property="token", jdbcType=JdbcType.VARCHAR),
        @Result(column="webhook_url", property="webhookUrl", jdbcType=JdbcType.VARCHAR),
        @Result(column="webhook_secret", property="webhookSecret", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="allowed_updates", property="allowedUpdates", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="commands", property="commands", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<TgBot> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TgBotResult")
    Optional<TgBot> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, tgBot, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, tgBot, completer);
    }

    default int deleteByPrimaryKey(String botId_) {
        return delete(c -> 
            c.where(botId, isEqualTo(botId_))
        );
    }

    default int insert(TgBot row) {
        return MyBatis3Utils.insert(this::insert, row, tgBot, c ->
            c.withMappedColumn(botId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(tgBotId)
            .withMappedColumn(username)
            .withMappedColumn(name)
            .withMappedColumn(token)
            .withMappedColumn(webhookUrl)
            .withMappedColumn(webhookSecret)
            .withMappedColumn(enabled)
            .withMappedColumn(description)
            .withMappedColumn(allowedUpdates)
            .withMappedColumn(commands)
            .withMappedColumn(ext)
        );
    }

    default int insertMultiple(Collection<TgBot> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, tgBot, c ->
            c.withMappedColumn(botId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(tgBotId)
            .withMappedColumn(username)
            .withMappedColumn(name)
            .withMappedColumn(token)
            .withMappedColumn(webhookUrl)
            .withMappedColumn(webhookSecret)
            .withMappedColumn(enabled)
            .withMappedColumn(description)
            .withMappedColumn(allowedUpdates)
            .withMappedColumn(commands)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(TgBot row) {
        return MyBatis3Utils.insert(this::insert, row, tgBot, c ->
            c.withMappedColumnWhenPresent(botId, row::getBotId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(tgBotId, row::getTgBotId)
            .withMappedColumnWhenPresent(username, row::getUsername)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(token, row::getToken)
            .withMappedColumnWhenPresent(webhookUrl, row::getWebhookUrl)
            .withMappedColumnWhenPresent(webhookSecret, row::getWebhookSecret)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(allowedUpdates, row::getAllowedUpdates)
            .withMappedColumnWhenPresent(commands, row::getCommands)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<TgBot> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, tgBot, completer);
    }

    default List<TgBot> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, tgBot, completer);
    }

    default List<TgBot> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, tgBot, completer);
    }

    default Optional<TgBot> selectByPrimaryKey(String botId_) {
        return selectOne(c ->
            c.where(botId, isEqualTo(botId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, tgBot, completer);
    }

    static UpdateDSL updateAllColumns(TgBot row, UpdateDSL dsl) {
        return dsl.set(botId).equalTo(row::getBotId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(tgBotId).equalTo(row::getTgBotId)
                .set(username).equalTo(row::getUsername)
                .set(name).equalTo(row::getName)
                .set(token).equalTo(row::getToken)
                .set(webhookUrl).equalTo(row::getWebhookUrl)
                .set(webhookSecret).equalTo(row::getWebhookSecret)
                .set(enabled).equalTo(row::getEnabled)
                .set(description).equalTo(row::getDescription)
                .set(allowedUpdates).equalTo(row::getAllowedUpdates)
                .set(commands).equalTo(row::getCommands)
                .set(ext).equalTo(row::getExt);
    }

    static UpdateDSL updateSelectiveColumns(TgBot row, UpdateDSL dsl) {
        return dsl.set(botId).equalToWhenPresent(row::getBotId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(tgBotId).equalToWhenPresent(row::getTgBotId)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(name).equalToWhenPresent(row::getName)
                .set(token).equalToWhenPresent(row::getToken)
                .set(webhookUrl).equalToWhenPresent(row::getWebhookUrl)
                .set(webhookSecret).equalToWhenPresent(row::getWebhookSecret)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(allowedUpdates).equalToWhenPresent(row::getAllowedUpdates)
                .set(commands).equalToWhenPresent(row::getCommands)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    default int updateByPrimaryKey(TgBot row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(tgBotId).equalTo(row::getTgBotId)
            .set(username).equalTo(row::getUsername)
            .set(name).equalTo(row::getName)
            .set(token).equalTo(row::getToken)
            .set(webhookUrl).equalTo(row::getWebhookUrl)
            .set(webhookSecret).equalTo(row::getWebhookSecret)
            .set(enabled).equalTo(row::getEnabled)
            .set(description).equalTo(row::getDescription)
            .set(allowedUpdates).equalTo(row::getAllowedUpdates)
            .set(commands).equalTo(row::getCommands)
            .set(ext).equalTo(row::getExt)
            .where(botId, isEqualTo(row::getBotId))
        );
    }

    default int updateByPrimaryKeySelective(TgBot row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(tgBotId).equalToWhenPresent(row::getTgBotId)
            .set(username).equalToWhenPresent(row::getUsername)
            .set(name).equalToWhenPresent(row::getName)
            .set(token).equalToWhenPresent(row::getToken)
            .set(webhookUrl).equalToWhenPresent(row::getWebhookUrl)
            .set(webhookSecret).equalToWhenPresent(row::getWebhookSecret)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(allowedUpdates).equalToWhenPresent(row::getAllowedUpdates)
            .set(commands).equalToWhenPresent(row::getCommands)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(botId, isEqualTo(row::getBotId))
        );
    }
}