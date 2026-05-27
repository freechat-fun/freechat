package fun.freechat.mapper;

import static fun.freechat.mapper.CharacterInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.CharacterInfo;
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
public interface CharacterInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(characterId, characterUid, gmtCreate, gmtModified, userId, parentUid, visibility, name, nickname, avatar, picture, video, gender, lang, version, priority, description, profile, greeting, chatStyle, chatExample, defaultScene, ext, draft);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.characterId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<CharacterInfo> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="CharacterInfoResult", value = {
        @Result(column="character_id", property="characterId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="character_uid", property="characterUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="parent_uid", property="parentUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="visibility", property="visibility", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="nickname", property="nickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="avatar", property="avatar", jdbcType=JdbcType.VARCHAR),
        @Result(column="picture", property="picture", jdbcType=JdbcType.VARCHAR),
        @Result(column="video", property="video", jdbcType=JdbcType.VARCHAR),
        @Result(column="gender", property="gender", jdbcType=JdbcType.VARCHAR),
        @Result(column="lang", property="lang", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="priority", property="priority", jdbcType=JdbcType.INTEGER),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="profile", property="profile", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="greeting", property="greeting", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="chat_style", property="chatStyle", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="chat_example", property="chatExample", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="default_scene", property="defaultScene", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<CharacterInfo> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("CharacterInfoResult")
    Optional<CharacterInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, characterInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, characterInfo, completer);
    }

    default int deleteByPrimaryKey(Long characterId_) {
        return delete(c -> 
            c.where(characterId, isEqualTo(characterId_))
        );
    }

    default int insert(CharacterInfo row) {
        return MyBatis3Utils.insert(this::insert, row, characterInfo, c ->
            c.withMappedColumn(characterUid)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(parentUid)
            .withMappedColumn(visibility)
            .withMappedColumn(name)
            .withMappedColumn(nickname)
            .withMappedColumn(avatar)
            .withMappedColumn(picture)
            .withMappedColumn(video)
            .withMappedColumn(gender)
            .withMappedColumn(lang)
            .withMappedColumn(version)
            .withMappedColumn(priority)
            .withMappedColumn(description)
            .withMappedColumn(profile)
            .withMappedColumn(greeting)
            .withMappedColumn(chatStyle)
            .withMappedColumn(chatExample)
            .withMappedColumn(defaultScene)
            .withMappedColumn(ext)
            .withMappedColumn(draft)
        );
    }

    default int insertSelective(CharacterInfo row) {
        return MyBatis3Utils.insert(this::insert, row, characterInfo, c ->
            c.withMappedColumnWhenPresent(characterUid, row::getCharacterUid)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(parentUid, row::getParentUid)
            .withMappedColumnWhenPresent(visibility, row::getVisibility)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(nickname, row::getNickname)
            .withMappedColumnWhenPresent(avatar, row::getAvatar)
            .withMappedColumnWhenPresent(picture, row::getPicture)
            .withMappedColumnWhenPresent(video, row::getVideo)
            .withMappedColumnWhenPresent(gender, row::getGender)
            .withMappedColumnWhenPresent(lang, row::getLang)
            .withMappedColumnWhenPresent(version, row::getVersion)
            .withMappedColumnWhenPresent(priority, row::getPriority)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(profile, row::getProfile)
            .withMappedColumnWhenPresent(greeting, row::getGreeting)
            .withMappedColumnWhenPresent(chatStyle, row::getChatStyle)
            .withMappedColumnWhenPresent(chatExample, row::getChatExample)
            .withMappedColumnWhenPresent(defaultScene, row::getDefaultScene)
            .withMappedColumnWhenPresent(ext, row::getExt)
            .withMappedColumnWhenPresent(draft, row::getDraft)
        );
    }

    default Optional<CharacterInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, characterInfo, completer);
    }

    default List<CharacterInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, characterInfo, completer);
    }

    default List<CharacterInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, characterInfo, completer);
    }

    default Optional<CharacterInfo> selectByPrimaryKey(Long characterId_) {
        return selectOne(c ->
            c.where(characterId, isEqualTo(characterId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, characterInfo, completer);
    }

    static UpdateDSL updateAllColumns(CharacterInfo row, UpdateDSL dsl) {
        return dsl.set(characterId).equalTo(row::getCharacterId)
                .set(characterUid).equalTo(row::getCharacterUid)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(parentUid).equalTo(row::getParentUid)
                .set(visibility).equalTo(row::getVisibility)
                .set(name).equalTo(row::getName)
                .set(nickname).equalTo(row::getNickname)
                .set(avatar).equalTo(row::getAvatar)
                .set(picture).equalTo(row::getPicture)
                .set(video).equalTo(row::getVideo)
                .set(gender).equalTo(row::getGender)
                .set(lang).equalTo(row::getLang)
                .set(version).equalTo(row::getVersion)
                .set(priority).equalTo(row::getPriority)
                .set(description).equalTo(row::getDescription)
                .set(profile).equalTo(row::getProfile)
                .set(greeting).equalTo(row::getGreeting)
                .set(chatStyle).equalTo(row::getChatStyle)
                .set(chatExample).equalTo(row::getChatExample)
                .set(defaultScene).equalTo(row::getDefaultScene)
                .set(ext).equalTo(row::getExt)
                .set(draft).equalTo(row::getDraft);
    }

    static UpdateDSL updateSelectiveColumns(CharacterInfo row, UpdateDSL dsl) {
        return dsl.set(characterId).equalToWhenPresent(row::getCharacterId)
                .set(characterUid).equalToWhenPresent(row::getCharacterUid)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(parentUid).equalToWhenPresent(row::getParentUid)
                .set(visibility).equalToWhenPresent(row::getVisibility)
                .set(name).equalToWhenPresent(row::getName)
                .set(nickname).equalToWhenPresent(row::getNickname)
                .set(avatar).equalToWhenPresent(row::getAvatar)
                .set(picture).equalToWhenPresent(row::getPicture)
                .set(video).equalToWhenPresent(row::getVideo)
                .set(gender).equalToWhenPresent(row::getGender)
                .set(lang).equalToWhenPresent(row::getLang)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(priority).equalToWhenPresent(row::getPriority)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(profile).equalToWhenPresent(row::getProfile)
                .set(greeting).equalToWhenPresent(row::getGreeting)
                .set(chatStyle).equalToWhenPresent(row::getChatStyle)
                .set(chatExample).equalToWhenPresent(row::getChatExample)
                .set(defaultScene).equalToWhenPresent(row::getDefaultScene)
                .set(ext).equalToWhenPresent(row::getExt)
                .set(draft).equalToWhenPresent(row::getDraft);
    }

    default int updateByPrimaryKey(CharacterInfo row) {
        return update(c ->
            c.set(characterUid).equalTo(row::getCharacterUid)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(parentUid).equalTo(row::getParentUid)
            .set(visibility).equalTo(row::getVisibility)
            .set(name).equalTo(row::getName)
            .set(nickname).equalTo(row::getNickname)
            .set(avatar).equalTo(row::getAvatar)
            .set(picture).equalTo(row::getPicture)
            .set(video).equalTo(row::getVideo)
            .set(gender).equalTo(row::getGender)
            .set(lang).equalTo(row::getLang)
            .set(version).equalTo(row::getVersion)
            .set(priority).equalTo(row::getPriority)
            .set(description).equalTo(row::getDescription)
            .set(profile).equalTo(row::getProfile)
            .set(greeting).equalTo(row::getGreeting)
            .set(chatStyle).equalTo(row::getChatStyle)
            .set(chatExample).equalTo(row::getChatExample)
            .set(defaultScene).equalTo(row::getDefaultScene)
            .set(ext).equalTo(row::getExt)
            .set(draft).equalTo(row::getDraft)
            .where(characterId, isEqualTo(row::getCharacterId))
        );
    }

    default int updateByPrimaryKeySelective(CharacterInfo row) {
        return update(c ->
            c.set(characterUid).equalToWhenPresent(row::getCharacterUid)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(parentUid).equalToWhenPresent(row::getParentUid)
            .set(visibility).equalToWhenPresent(row::getVisibility)
            .set(name).equalToWhenPresent(row::getName)
            .set(nickname).equalToWhenPresent(row::getNickname)
            .set(avatar).equalToWhenPresent(row::getAvatar)
            .set(picture).equalToWhenPresent(row::getPicture)
            .set(video).equalToWhenPresent(row::getVideo)
            .set(gender).equalToWhenPresent(row::getGender)
            .set(lang).equalToWhenPresent(row::getLang)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(priority).equalToWhenPresent(row::getPriority)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(profile).equalToWhenPresent(row::getProfile)
            .set(greeting).equalToWhenPresent(row::getGreeting)
            .set(chatStyle).equalToWhenPresent(row::getChatStyle)
            .set(chatExample).equalToWhenPresent(row::getChatExample)
            .set(defaultScene).equalToWhenPresent(row::getDefaultScene)
            .set(ext).equalToWhenPresent(row::getExt)
            .set(draft).equalToWhenPresent(row::getDraft)
            .where(characterId, isEqualTo(row::getCharacterId))
        );
    }
}