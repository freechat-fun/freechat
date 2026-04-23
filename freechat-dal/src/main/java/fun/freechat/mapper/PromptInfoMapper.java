package fun.freechat.mapper;

import static fun.freechat.mapper.PromptInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.PromptInfo;
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
public interface PromptInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(promptId, promptUid, gmtCreate, gmtModified, userId, parentUid, visibility, name, type, format, lang, version, description, template, example, inputs, ext, draft);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.promptId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<PromptInfo> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PromptInfoResult")
    Optional<PromptInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, promptInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, promptInfo, completer);
    }

    default int deleteByPrimaryKey(Long promptId_) {
        return delete(c -> 
            c.where(promptId, isEqualTo(promptId_))
        );
    }

    default int insert(PromptInfo row) {
        return MyBatis3Utils.insert(this::insert, row, promptInfo, c ->
            c.withMappedColumn(promptUid)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(parentUid)
            .withMappedColumn(visibility)
            .withMappedColumn(name)
            .withMappedColumn(type)
            .withMappedColumn(format)
            .withMappedColumn(lang)
            .withMappedColumn(version)
            .withMappedColumn(description)
            .withMappedColumn(template)
            .withMappedColumn(example)
            .withMappedColumn(inputs)
            .withMappedColumn(ext)
            .withMappedColumn(draft)
        );
    }

    default int insertSelective(PromptInfo row) {
        return MyBatis3Utils.insert(this::insert, row, promptInfo, c ->
            c.withMappedColumnWhenPresent(promptUid, row::getPromptUid)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(parentUid, row::getParentUid)
            .withMappedColumnWhenPresent(visibility, row::getVisibility)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(type, row::getType)
            .withMappedColumnWhenPresent(format, row::getFormat)
            .withMappedColumnWhenPresent(lang, row::getLang)
            .withMappedColumnWhenPresent(version, row::getVersion)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(template, row::getTemplate)
            .withMappedColumnWhenPresent(example, row::getExample)
            .withMappedColumnWhenPresent(inputs, row::getInputs)
            .withMappedColumnWhenPresent(ext, row::getExt)
            .withMappedColumnWhenPresent(draft, row::getDraft)
        );
    }

    default Optional<PromptInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, promptInfo, completer);
    }

    default List<PromptInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, promptInfo, completer);
    }

    default List<PromptInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, promptInfo, completer);
    }

    default Optional<PromptInfo> selectByPrimaryKey(Long promptId_) {
        return selectOne(c ->
            c.where(promptId, isEqualTo(promptId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, promptInfo, completer);
    }

    static UpdateDSL updateAllColumns(PromptInfo row, UpdateDSL dsl) {
        return dsl.set(promptId).equalTo(row::getPromptId)
                .set(promptUid).equalTo(row::getPromptUid)
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

    static UpdateDSL updateSelectiveColumns(PromptInfo row, UpdateDSL dsl) {
        return dsl.set(promptId).equalToWhenPresent(row::getPromptId)
                .set(promptUid).equalToWhenPresent(row::getPromptUid)
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