package fun.freechat.mapper;

import static fun.freechat.mapper.FlowInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.FlowInfo;
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
public interface FlowInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<FlowInfo>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(flowId, gmtCreate, gmtModified, userId, parentId, visibility, name, format, version, description, config, example, parameters, ext, draft);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="FlowInfoResult", value = {
        @Result(column="flow_id", property="flowId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="parent_id", property="parentId", jdbcType=JdbcType.VARCHAR),
        @Result(column="visibility", property="visibility", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="config", property="config", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="example", property="example", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="parameters", property="parameters", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="draft", property="draft", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<FlowInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("FlowInfoResult")
    Optional<FlowInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String flowId_) {
        return delete(c -> 
            c.where(flowId, isEqualTo(flowId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(FlowInfo row) {
        return MyBatis3Utils.insert(this::insert, row, flowInfo, c ->
            c.map(flowId).toProperty("flowId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentId).toProperty("parentId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(format).toProperty("format")
            .map(version).toProperty("version")
            .map(description).toProperty("description")
            .map(config).toProperty("config")
            .map(example).toProperty("example")
            .map(parameters).toProperty("parameters")
            .map(ext).toProperty("ext")
            .map(draft).toProperty("draft")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<FlowInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, flowInfo, c ->
            c.map(flowId).toProperty("flowId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentId).toProperty("parentId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(format).toProperty("format")
            .map(version).toProperty("version")
            .map(description).toProperty("description")
            .map(config).toProperty("config")
            .map(example).toProperty("example")
            .map(parameters).toProperty("parameters")
            .map(ext).toProperty("ext")
            .map(draft).toProperty("draft")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(FlowInfo row) {
        return MyBatis3Utils.insert(this::insert, row, flowInfo, c ->
            c.map(flowId).toPropertyWhenPresent("flowId", row::getFlowId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(parentId).toPropertyWhenPresent("parentId", row::getParentId)
            .map(visibility).toPropertyWhenPresent("visibility", row::getVisibility)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(format).toPropertyWhenPresent("format", row::getFormat)
            .map(version).toPropertyWhenPresent("version", row::getVersion)
            .map(description).toPropertyWhenPresent("description", row::getDescription)
            .map(config).toPropertyWhenPresent("config", row::getConfig)
            .map(example).toPropertyWhenPresent("example", row::getExample)
            .map(parameters).toPropertyWhenPresent("parameters", row::getParameters)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
            .map(draft).toPropertyWhenPresent("draft", row::getDraft)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<FlowInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<FlowInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<FlowInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<FlowInfo> selectByPrimaryKey(String flowId_) {
        return selectOne(c ->
            c.where(flowId, isEqualTo(flowId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, flowInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(FlowInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(flowId).equalTo(row::getFlowId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(parentId).equalTo(row::getParentId)
                .set(visibility).equalTo(row::getVisibility)
                .set(name).equalTo(row::getName)
                .set(format).equalTo(row::getFormat)
                .set(version).equalTo(row::getVersion)
                .set(description).equalTo(row::getDescription)
                .set(config).equalTo(row::getConfig)
                .set(example).equalTo(row::getExample)
                .set(parameters).equalTo(row::getParameters)
                .set(ext).equalTo(row::getExt)
                .set(draft).equalTo(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(FlowInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(flowId).equalToWhenPresent(row::getFlowId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(parentId).equalToWhenPresent(row::getParentId)
                .set(visibility).equalToWhenPresent(row::getVisibility)
                .set(name).equalToWhenPresent(row::getName)
                .set(format).equalToWhenPresent(row::getFormat)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(config).equalToWhenPresent(row::getConfig)
                .set(example).equalToWhenPresent(row::getExample)
                .set(parameters).equalToWhenPresent(row::getParameters)
                .set(ext).equalToWhenPresent(row::getExt)
                .set(draft).equalToWhenPresent(row::getDraft);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(FlowInfo row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(parentId).equalTo(row::getParentId)
            .set(visibility).equalTo(row::getVisibility)
            .set(name).equalTo(row::getName)
            .set(format).equalTo(row::getFormat)
            .set(version).equalTo(row::getVersion)
            .set(description).equalTo(row::getDescription)
            .set(config).equalTo(row::getConfig)
            .set(example).equalTo(row::getExample)
            .set(parameters).equalTo(row::getParameters)
            .set(ext).equalTo(row::getExt)
            .set(draft).equalTo(row::getDraft)
            .where(flowId, isEqualTo(row::getFlowId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(FlowInfo row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(parentId).equalToWhenPresent(row::getParentId)
            .set(visibility).equalToWhenPresent(row::getVisibility)
            .set(name).equalToWhenPresent(row::getName)
            .set(format).equalToWhenPresent(row::getFormat)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(config).equalToWhenPresent(row::getConfig)
            .set(example).equalToWhenPresent(row::getExample)
            .set(parameters).equalToWhenPresent(row::getParameters)
            .set(ext).equalToWhenPresent(row::getExt)
            .set(draft).equalToWhenPresent(row::getDraft)
            .where(flowId, isEqualTo(row::getFlowId))
        );
    }
}