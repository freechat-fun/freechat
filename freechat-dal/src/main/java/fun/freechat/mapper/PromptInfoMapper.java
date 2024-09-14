package fun.freechat.mapper;

import fun.freechat.model.PromptInfo;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

import java.util.List;
import java.util.Optional;

import static fun.freechat.mapper.PromptInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface PromptInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(promptId, promptUid, gmtCreate, gmtModified, userId, parentUid, visibility, name, type, format, lang, version, description, template, example, inputs, ext, draft);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.promptId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<PromptInfo> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="PromptInfoResult", value = {
        @Result(column="prompt_id", property="promptId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="prompt_uid", property="promptUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="parent_uid", property="parentUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="visibility", property="visibility", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="lang", property="lang", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="template", property="template", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="example", property="example", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="inputs", property="inputs", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PromptInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PromptInfoResult")
    Optional<PromptInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long promptId_) {
        return delete(c -> 
            c.where(promptId, isEqualTo(promptId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(PromptInfo row) {
        return MyBatis3Utils.insert(this::insert, row, promptInfo, c ->
            c.map(promptUid).toProperty("promptUid")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentUid).toProperty("parentUid")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(type).toProperty("type")
            .map(format).toProperty("format")
            .map(lang).toProperty("lang")
            .map(version).toProperty("version")
            .map(description).toProperty("description")
            .map(template).toProperty("template")
            .map(example).toProperty("example")
            .map(inputs).toProperty("inputs")
            .map(ext).toProperty("ext")
            .map(draft).toProperty("draft")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(PromptInfo row) {
        return MyBatis3Utils.insert(this::insert, row, promptInfo, c ->
            c.map(promptUid).toPropertyWhenPresent("promptUid", row::getPromptUid)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(parentUid).toPropertyWhenPresent("parentUid", row::getParentUid)
            .map(visibility).toPropertyWhenPresent("visibility", row::getVisibility)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(type).toPropertyWhenPresent("type", row::getType)
            .map(format).toPropertyWhenPresent("format", row::getFormat)
            .map(lang).toPropertyWhenPresent("lang", row::getLang)
            .map(version).toPropertyWhenPresent("version", row::getVersion)
            .map(description).toPropertyWhenPresent("description", row::getDescription)
            .map(template).toPropertyWhenPresent("template", row::getTemplate)
            .map(example).toPropertyWhenPresent("example", row::getExample)
            .map(inputs).toPropertyWhenPresent("inputs", row::getInputs)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
            .map(draft).toPropertyWhenPresent("draft", row::getDraft)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PromptInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PromptInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PromptInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PromptInfo> selectByPrimaryKey(Long promptId_) {
        return selectOne(c ->
            c.where(promptId, isEqualTo(promptId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, promptInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(PromptInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(promptUid).equalTo(row::getPromptUid)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(parentUid).equalTo(row::getParentUid)
                .set(visibility).equalTo(row::getVisibility)
                .set(name).equalTo(row::getName)
                .set(type).equalTo(row::getType)
                .set(format).equalTo(row::getFormat)
                .set(lang).equalTo(row::getLang)
                .set(version).equalTo(row::getVersion)
                .set(description).equalTo(row::getDescription)
                .set(template).equalTo(row::getTemplate)
                .set(example).equalTo(row::getExample)
                .set(inputs).equalTo(row::getInputs)
                .set(ext).equalTo(row::getExt)
                .set(draft).equalTo(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(PromptInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(promptUid).equalToWhenPresent(row::getPromptUid)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(parentUid).equalToWhenPresent(row::getParentUid)
                .set(visibility).equalToWhenPresent(row::getVisibility)
                .set(name).equalToWhenPresent(row::getName)
                .set(type).equalToWhenPresent(row::getType)
                .set(format).equalToWhenPresent(row::getFormat)
                .set(lang).equalToWhenPresent(row::getLang)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(template).equalToWhenPresent(row::getTemplate)
                .set(example).equalToWhenPresent(row::getExample)
                .set(inputs).equalToWhenPresent(row::getInputs)
                .set(ext).equalToWhenPresent(row::getExt)
                .set(draft).equalToWhenPresent(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(PromptInfo row) {
        return update(c ->
            c.set(promptUid).equalTo(row::getPromptUid)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(parentUid).equalTo(row::getParentUid)
            .set(visibility).equalTo(row::getVisibility)
            .set(name).equalTo(row::getName)
            .set(type).equalTo(row::getType)
            .set(format).equalTo(row::getFormat)
            .set(lang).equalTo(row::getLang)
            .set(version).equalTo(row::getVersion)
            .set(description).equalTo(row::getDescription)
            .set(template).equalTo(row::getTemplate)
            .set(example).equalTo(row::getExample)
            .set(inputs).equalTo(row::getInputs)
            .set(ext).equalTo(row::getExt)
            .set(draft).equalTo(row::getDraft)
            .where(promptId, isEqualTo(row::getPromptId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(PromptInfo row) {
        return update(c ->
            c.set(promptUid).equalToWhenPresent(row::getPromptUid)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(parentUid).equalToWhenPresent(row::getParentUid)
            .set(visibility).equalToWhenPresent(row::getVisibility)
            .set(name).equalToWhenPresent(row::getName)
            .set(type).equalToWhenPresent(row::getType)
            .set(format).equalToWhenPresent(row::getFormat)
            .set(lang).equalToWhenPresent(row::getLang)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(template).equalToWhenPresent(row::getTemplate)
            .set(example).equalToWhenPresent(row::getExample)
            .set(inputs).equalToWhenPresent(row::getInputs)
            .set(ext).equalToWhenPresent(row::getExt)
            .set(draft).equalToWhenPresent(row::getDraft)
            .where(promptId, isEqualTo(row::getPromptId))
        );
    }
}