package fun.freechat.mapper;

import static fun.freechat.mapper.CharacterInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.CharacterInfo;
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
public interface CharacterInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<CharacterInfo>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(characterId, gmtCreate, gmtModified, userId, parentId, visibility, name, nickname, avatar, picture, gender, lang, version, priority, description, profile, greeting, chatStyle, chatExample, ext, draft);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="CharacterInfoResult", value = {
        @Result(column="character_id", property="characterId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="parent_id", property="parentId", jdbcType=JdbcType.VARCHAR),
        @Result(column="visibility", property="visibility", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="nickname", property="nickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="avatar", property="avatar", jdbcType=JdbcType.VARCHAR),
        @Result(column="picture", property="picture", jdbcType=JdbcType.VARCHAR),
        @Result(column="gender", property="gender", jdbcType=JdbcType.VARCHAR),
        @Result(column="lang", property="lang", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="priority", property="priority", jdbcType=JdbcType.INTEGER),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="profile", property="profile", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="greeting", property="greeting", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="chat_style", property="chatStyle", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="chat_example", property="chatExample", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<CharacterInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("CharacterInfoResult")
    Optional<CharacterInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String characterId_) {
        return delete(c -> 
            c.where(characterId, isEqualTo(characterId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(CharacterInfo row) {
        return MyBatis3Utils.insert(this::insert, row, characterInfo, c ->
            c.map(characterId).toProperty("characterId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentId).toProperty("parentId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(nickname).toProperty("nickname")
            .map(avatar).toProperty("avatar")
            .map(picture).toProperty("picture")
            .map(gender).toProperty("gender")
            .map(lang).toProperty("lang")
            .map(version).toProperty("version")
            .map(priority).toProperty("priority")
            .map(description).toProperty("description")
            .map(profile).toProperty("profile")
            .map(greeting).toProperty("greeting")
            .map(chatStyle).toProperty("chatStyle")
            .map(chatExample).toProperty("chatExample")
            .map(ext).toProperty("ext")
            .map(draft).toProperty("draft")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<CharacterInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, characterInfo, c ->
            c.map(characterId).toProperty("characterId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentId).toProperty("parentId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(nickname).toProperty("nickname")
            .map(avatar).toProperty("avatar")
            .map(picture).toProperty("picture")
            .map(gender).toProperty("gender")
            .map(lang).toProperty("lang")
            .map(version).toProperty("version")
            .map(priority).toProperty("priority")
            .map(description).toProperty("description")
            .map(profile).toProperty("profile")
            .map(greeting).toProperty("greeting")
            .map(chatStyle).toProperty("chatStyle")
            .map(chatExample).toProperty("chatExample")
            .map(ext).toProperty("ext")
            .map(draft).toProperty("draft")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(CharacterInfo row) {
        return MyBatis3Utils.insert(this::insert, row, characterInfo, c ->
            c.map(characterId).toPropertyWhenPresent("characterId", row::getCharacterId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(parentId).toPropertyWhenPresent("parentId", row::getParentId)
            .map(visibility).toPropertyWhenPresent("visibility", row::getVisibility)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(nickname).toPropertyWhenPresent("nickname", row::getNickname)
            .map(avatar).toPropertyWhenPresent("avatar", row::getAvatar)
            .map(picture).toPropertyWhenPresent("picture", row::getPicture)
            .map(gender).toPropertyWhenPresent("gender", row::getGender)
            .map(lang).toPropertyWhenPresent("lang", row::getLang)
            .map(version).toPropertyWhenPresent("version", row::getVersion)
            .map(priority).toPropertyWhenPresent("priority", row::getPriority)
            .map(description).toPropertyWhenPresent("description", row::getDescription)
            .map(profile).toPropertyWhenPresent("profile", row::getProfile)
            .map(greeting).toPropertyWhenPresent("greeting", row::getGreeting)
            .map(chatStyle).toPropertyWhenPresent("chatStyle", row::getChatStyle)
            .map(chatExample).toPropertyWhenPresent("chatExample", row::getChatExample)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
            .map(draft).toPropertyWhenPresent("draft", row::getDraft)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CharacterInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CharacterInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<CharacterInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<CharacterInfo> selectByPrimaryKey(String characterId_) {
        return selectOne(c ->
            c.where(characterId, isEqualTo(characterId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, characterInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(CharacterInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(characterId).equalTo(row::getCharacterId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(parentId).equalTo(row::getParentId)
                .set(visibility).equalTo(row::getVisibility)
                .set(name).equalTo(row::getName)
                .set(nickname).equalTo(row::getNickname)
                .set(avatar).equalTo(row::getAvatar)
                .set(picture).equalTo(row::getPicture)
                .set(gender).equalTo(row::getGender)
                .set(lang).equalTo(row::getLang)
                .set(version).equalTo(row::getVersion)
                .set(priority).equalTo(row::getPriority)
                .set(description).equalTo(row::getDescription)
                .set(profile).equalTo(row::getProfile)
                .set(greeting).equalTo(row::getGreeting)
                .set(chatStyle).equalTo(row::getChatStyle)
                .set(chatExample).equalTo(row::getChatExample)
                .set(ext).equalTo(row::getExt)
                .set(draft).equalTo(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(CharacterInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(characterId).equalToWhenPresent(row::getCharacterId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(parentId).equalToWhenPresent(row::getParentId)
                .set(visibility).equalToWhenPresent(row::getVisibility)
                .set(name).equalToWhenPresent(row::getName)
                .set(nickname).equalToWhenPresent(row::getNickname)
                .set(avatar).equalToWhenPresent(row::getAvatar)
                .set(picture).equalToWhenPresent(row::getPicture)
                .set(gender).equalToWhenPresent(row::getGender)
                .set(lang).equalToWhenPresent(row::getLang)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(priority).equalToWhenPresent(row::getPriority)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(profile).equalToWhenPresent(row::getProfile)
                .set(greeting).equalToWhenPresent(row::getGreeting)
                .set(chatStyle).equalToWhenPresent(row::getChatStyle)
                .set(chatExample).equalToWhenPresent(row::getChatExample)
                .set(ext).equalToWhenPresent(row::getExt)
                .set(draft).equalToWhenPresent(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(CharacterInfo row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(parentId).equalTo(row::getParentId)
            .set(visibility).equalTo(row::getVisibility)
            .set(name).equalTo(row::getName)
            .set(nickname).equalTo(row::getNickname)
            .set(avatar).equalTo(row::getAvatar)
            .set(picture).equalTo(row::getPicture)
            .set(gender).equalTo(row::getGender)
            .set(lang).equalTo(row::getLang)
            .set(version).equalTo(row::getVersion)
            .set(priority).equalTo(row::getPriority)
            .set(description).equalTo(row::getDescription)
            .set(profile).equalTo(row::getProfile)
            .set(greeting).equalTo(row::getGreeting)
            .set(chatStyle).equalTo(row::getChatStyle)
            .set(chatExample).equalTo(row::getChatExample)
            .set(ext).equalTo(row::getExt)
            .set(draft).equalTo(row::getDraft)
            .where(characterId, isEqualTo(row::getCharacterId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(CharacterInfo row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(parentId).equalToWhenPresent(row::getParentId)
            .set(visibility).equalToWhenPresent(row::getVisibility)
            .set(name).equalToWhenPresent(row::getName)
            .set(nickname).equalToWhenPresent(row::getNickname)
            .set(avatar).equalToWhenPresent(row::getAvatar)
            .set(picture).equalToWhenPresent(row::getPicture)
            .set(gender).equalToWhenPresent(row::getGender)
            .set(lang).equalToWhenPresent(row::getLang)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(priority).equalToWhenPresent(row::getPriority)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(profile).equalToWhenPresent(row::getProfile)
            .set(greeting).equalToWhenPresent(row::getGreeting)
            .set(chatStyle).equalToWhenPresent(row::getChatStyle)
            .set(chatExample).equalToWhenPresent(row::getChatExample)
            .set(ext).equalToWhenPresent(row::getExt)
            .set(draft).equalToWhenPresent(row::getDraft)
            .where(characterId, isEqualTo(row::getCharacterId))
        );
    }
}