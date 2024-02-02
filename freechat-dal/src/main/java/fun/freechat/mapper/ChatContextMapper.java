package fun.freechat.mapper;

import static fun.freechat.mapper.ChatContextDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ChatContext;
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
public interface ChatContextMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<ChatContext>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(chatId, gmtCreate, gmtModified, userId, userNickname, characterNickname, backendId, userProfile, about, ext);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ChatContextResult", value = {
        @Result(column="chat_id", property="chatId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_nickname", property="userNickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="character_nickname", property="characterNickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="backend_id", property="backendId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_profile", property="userProfile", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="about", property="about", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<ChatContext> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ChatContextResult")
    Optional<ChatContext> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String chatId_) {
        return delete(c -> 
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(ChatContext row) {
        return MyBatis3Utils.insert(this::insert, row, chatContext, c ->
            c.map(chatId).toProperty("chatId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(userNickname).toProperty("userNickname")
            .map(characterNickname).toProperty("characterNickname")
            .map(backendId).toProperty("backendId")
            .map(userProfile).toProperty("userProfile")
            .map(about).toProperty("about")
            .map(ext).toProperty("ext")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<ChatContext> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, chatContext, c ->
            c.map(chatId).toProperty("chatId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(userNickname).toProperty("userNickname")
            .map(characterNickname).toProperty("characterNickname")
            .map(backendId).toProperty("backendId")
            .map(userProfile).toProperty("userProfile")
            .map(about).toProperty("about")
            .map(ext).toProperty("ext")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(ChatContext row) {
        return MyBatis3Utils.insert(this::insert, row, chatContext, c ->
            c.map(chatId).toPropertyWhenPresent("chatId", row::getChatId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(userNickname).toPropertyWhenPresent("userNickname", row::getUserNickname)
            .map(characterNickname).toPropertyWhenPresent("characterNickname", row::getCharacterNickname)
            .map(backendId).toPropertyWhenPresent("backendId", row::getBackendId)
            .map(userProfile).toPropertyWhenPresent("userProfile", row::getUserProfile)
            .map(about).toPropertyWhenPresent("about", row::getAbout)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ChatContext> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ChatContext> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ChatContext> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ChatContext> selectByPrimaryKey(String chatId_) {
        return selectOne(c ->
            c.where(chatId, isEqualTo(chatId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, chatContext, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(ChatContext row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(chatId).equalTo(row::getChatId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(userNickname).equalTo(row::getUserNickname)
                .set(characterNickname).equalTo(row::getCharacterNickname)
                .set(backendId).equalTo(row::getBackendId)
                .set(userProfile).equalTo(row::getUserProfile)
                .set(about).equalTo(row::getAbout)
                .set(ext).equalTo(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(ChatContext row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(chatId).equalToWhenPresent(row::getChatId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(userNickname).equalToWhenPresent(row::getUserNickname)
                .set(characterNickname).equalToWhenPresent(row::getCharacterNickname)
                .set(backendId).equalToWhenPresent(row::getBackendId)
                .set(userProfile).equalToWhenPresent(row::getUserProfile)
                .set(about).equalToWhenPresent(row::getAbout)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(ChatContext row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(userNickname).equalTo(row::getUserNickname)
            .set(characterNickname).equalTo(row::getCharacterNickname)
            .set(backendId).equalTo(row::getBackendId)
            .set(userProfile).equalTo(row::getUserProfile)
            .set(about).equalTo(row::getAbout)
            .set(ext).equalTo(row::getExt)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(ChatContext row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(userNickname).equalToWhenPresent(row::getUserNickname)
            .set(characterNickname).equalToWhenPresent(row::getCharacterNickname)
            .set(backendId).equalToWhenPresent(row::getBackendId)
            .set(userProfile).equalToWhenPresent(row::getUserProfile)
            .set(about).equalToWhenPresent(row::getAbout)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(chatId, isEqualTo(row::getChatId))
        );
    }
}