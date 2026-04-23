package fun.freechat.mapper;

import static fun.freechat.mapper.AgentInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.AgentInfo;
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
public interface AgentInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(agentId, agentUid, gmtCreate, gmtModified, userId, parentUid, visibility, name, format, version, description, config, example, parameters, ext, draft);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.agentId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<AgentInfo> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AgentInfoResult")
    Optional<AgentInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, agentInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, agentInfo, completer);
    }

    default int deleteByPrimaryKey(Long agentId_) {
        return delete(c -> 
            c.where(agentId, isEqualTo(agentId_))
        );
    }

    default int insert(AgentInfo row) {
        return MyBatis3Utils.insert(this::insert, row, agentInfo, c ->
            c.withMappedColumn(agentUid)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(parentUid)
            .withMappedColumn(visibility)
            .withMappedColumn(name)
            .withMappedColumn(format)
            .withMappedColumn(version)
            .withMappedColumn(description)
            .withMappedColumn(config)
            .withMappedColumn(example)
            .withMappedColumn(parameters)
            .withMappedColumn(ext)
            .withMappedColumn(draft)
        );
    }

    default int insertSelective(AgentInfo row) {
        return MyBatis3Utils.insert(this::insert, row, agentInfo, c ->
            c.withMappedColumnWhenPresent(agentUid, row::getAgentUid)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(parentUid, row::getParentUid)
            .withMappedColumnWhenPresent(visibility, row::getVisibility)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(format, row::getFormat)
            .withMappedColumnWhenPresent(version, row::getVersion)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(config, row::getConfig)
            .withMappedColumnWhenPresent(example, row::getExample)
            .withMappedColumnWhenPresent(parameters, row::getParameters)
            .withMappedColumnWhenPresent(ext, row::getExt)
            .withMappedColumnWhenPresent(draft, row::getDraft)
        );
    }

    default Optional<AgentInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, agentInfo, completer);
    }

    default List<AgentInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, agentInfo, completer);
    }

    default List<AgentInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, agentInfo, completer);
    }

    default Optional<AgentInfo> selectByPrimaryKey(Long agentId_) {
        return selectOne(c ->
            c.where(agentId, isEqualTo(agentId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, agentInfo, completer);
    }

    static UpdateDSL updateAllColumns(AgentInfo row, UpdateDSL dsl) {
        return dsl.set(agentId).equalTo(row::getAgentId)
                .set(agentUid).equalTo(row::getAgentUid)
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

    static UpdateDSL updateSelectiveColumns(AgentInfo row, UpdateDSL dsl) {
        return dsl.set(agentId).equalToWhenPresent(row::getAgentId)
                .set(agentUid).equalToWhenPresent(row::getAgentUid)
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