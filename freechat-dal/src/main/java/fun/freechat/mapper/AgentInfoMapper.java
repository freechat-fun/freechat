package fun.freechat.mapper;

import fun.freechat.model.AgentInfo;
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

import static fun.freechat.mapper.AgentInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface AgentInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(agentId, agentUid, gmtCreate, gmtModified, userId, parentUid, visibility, name, format, version, description, config, example, parameters, ext, draft);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.agentId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<AgentInfo> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AgentInfoResult", value = {
        @Result(column="agent_id", property="agentId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="agent_uid", property="agentUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="parent_uid", property="parentUid", jdbcType=JdbcType.VARCHAR),
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
    List<AgentInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AgentInfoResult")
    Optional<AgentInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long agentId_) {
        return delete(c -> 
            c.where(agentId, isEqualTo(agentId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(AgentInfo row) {
        return MyBatis3Utils.insert(this::insert, row, agentInfo, c ->
            c.map(agentUid).toProperty("agentUid")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(parentUid).toProperty("parentUid")
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
    default int insertSelective(AgentInfo row) {
        return MyBatis3Utils.insert(this::insert, row, agentInfo, c ->
            c.map(agentUid).toPropertyWhenPresent("agentUid", row::getAgentUid)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(parentUid).toPropertyWhenPresent("parentUid", row::getParentUid)
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
    default Optional<AgentInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AgentInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AgentInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<AgentInfo> selectByPrimaryKey(Long agentId_) {
        return selectOne(c ->
            c.where(agentId, isEqualTo(agentId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, agentInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(AgentInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(agentUid).equalTo(row::getAgentUid)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(parentUid).equalTo(row::getParentUid)
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
    static UpdateDSL<UpdateModel> updateSelectiveColumns(AgentInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(agentUid).equalToWhenPresent(row::getAgentUid)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(parentUid).equalToWhenPresent(row::getParentUid)
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
    default int updateByPrimaryKey(AgentInfo row) {
        return update(c ->
            c.set(agentUid).equalTo(row::getAgentUid)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(parentUid).equalTo(row::getParentUid)
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
            .where(agentId, isEqualTo(row::getAgentId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(AgentInfo row) {
        return update(c ->
            c.set(agentUid).equalToWhenPresent(row::getAgentUid)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(parentUid).equalToWhenPresent(row::getParentUid)
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
            .where(agentId, isEqualTo(row::getAgentId))
        );
    }
}