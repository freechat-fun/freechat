package fun.freechat.mapper;

import static fun.freechat.mapper.ConnectorInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ConnectorInfo;
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
public interface ConnectorInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<ConnectorInfo>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(connectorId, userId, revision, name, transport, url, timeout, active, gmtCreate, gmtModified, headers, headerNames);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ConnectorInfoResult", value = {
        @Result(column="connector_id", property="connectorId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="revision", property="revision", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="transport", property="transport", jdbcType=JdbcType.VARCHAR),
        @Result(column="url", property="url", jdbcType=JdbcType.VARCHAR),
        @Result(column="timeout", property="timeout", jdbcType=JdbcType.INTEGER),
        @Result(column="active", property="active", jdbcType=JdbcType.TINYINT),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="headers", property="headers", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="header_names", property="headerNames", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<ConnectorInfo> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ConnectorInfoResult")
    Optional<ConnectorInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, connectorInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, connectorInfo, completer);
    }

    default int deleteByPrimaryKey(String connectorId_) {
        return delete(c -> 
            c.where(connectorId, isEqualTo(connectorId_))
        );
    }

    default int insert(ConnectorInfo row) {
        return MyBatis3Utils.insert(this::insert, row, connectorInfo, c ->
            c.withMappedColumn(connectorId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(name)
            .withMappedColumn(transport)
            .withMappedColumn(url)
            .withMappedColumn(timeout)
            .withMappedColumn(active)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(headers)
            .withMappedColumn(headerNames)
        );
    }

    default int insertMultiple(Collection<ConnectorInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, connectorInfo, c ->
            c.withMappedColumn(connectorId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(name)
            .withMappedColumn(transport)
            .withMappedColumn(url)
            .withMappedColumn(timeout)
            .withMappedColumn(active)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(headers)
            .withMappedColumn(headerNames)
        );
    }

    default int insertSelective(ConnectorInfo row) {
        return MyBatis3Utils.insert(this::insert, row, connectorInfo, c ->
            c.withMappedColumnWhenPresent(connectorId, row::getConnectorId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(revision, row::getRevision)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(transport, row::getTransport)
            .withMappedColumnWhenPresent(url, row::getUrl)
            .withMappedColumnWhenPresent(timeout, row::getTimeout)
            .withMappedColumnWhenPresent(active, row::getActive)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(headers, row::getHeaders)
            .withMappedColumnWhenPresent(headerNames, row::getHeaderNames)
        );
    }

    default Optional<ConnectorInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, connectorInfo, completer);
    }

    default List<ConnectorInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, connectorInfo, completer);
    }

    default List<ConnectorInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, connectorInfo, completer);
    }

    default Optional<ConnectorInfo> selectByPrimaryKey(String connectorId_) {
        return selectOne(c ->
            c.where(connectorId, isEqualTo(connectorId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, connectorInfo, completer);
    }

    static UpdateDSL updateAllColumns(ConnectorInfo row, UpdateDSL dsl) {
        return dsl.set(connectorId).equalTo(row::getConnectorId)
                .set(userId).equalTo(row::getUserId)
                .set(revision).equalTo(row::getRevision)
                .set(name).equalTo(row::getName)
                .set(transport).equalTo(row::getTransport)
                .set(url).equalTo(row::getUrl)
                .set(timeout).equalTo(row::getTimeout)
                .set(active).equalTo(row::getActive)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(headers).equalTo(row::getHeaders)
                .set(headerNames).equalTo(row::getHeaderNames);
    }

    static UpdateDSL updateSelectiveColumns(ConnectorInfo row, UpdateDSL dsl) {
        return dsl.set(connectorId).equalToWhenPresent(row::getConnectorId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(revision).equalToWhenPresent(row::getRevision)
                .set(name).equalToWhenPresent(row::getName)
                .set(transport).equalToWhenPresent(row::getTransport)
                .set(url).equalToWhenPresent(row::getUrl)
                .set(timeout).equalToWhenPresent(row::getTimeout)
                .set(active).equalToWhenPresent(row::getActive)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(headers).equalToWhenPresent(row::getHeaders)
                .set(headerNames).equalToWhenPresent(row::getHeaderNames);
    }

    default int updateByPrimaryKey(ConnectorInfo row) {
        return update(c ->
            c.set(userId).equalTo(row::getUserId)
            .set(revision).equalTo(row::getRevision)
            .set(name).equalTo(row::getName)
            .set(transport).equalTo(row::getTransport)
            .set(url).equalTo(row::getUrl)
            .set(timeout).equalTo(row::getTimeout)
            .set(active).equalTo(row::getActive)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(headers).equalTo(row::getHeaders)
            .set(headerNames).equalTo(row::getHeaderNames)
            .where(connectorId, isEqualTo(row::getConnectorId))
        );
    }

    default int updateByPrimaryKeySelective(ConnectorInfo row) {
        return update(c ->
            c.set(userId).equalToWhenPresent(row::getUserId)
            .set(revision).equalToWhenPresent(row::getRevision)
            .set(name).equalToWhenPresent(row::getName)
            .set(transport).equalToWhenPresent(row::getTransport)
            .set(url).equalToWhenPresent(row::getUrl)
            .set(timeout).equalToWhenPresent(row::getTimeout)
            .set(active).equalToWhenPresent(row::getActive)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(headers).equalToWhenPresent(row::getHeaders)
            .set(headerNames).equalToWhenPresent(row::getHeaderNames)
            .where(connectorId, isEqualTo(row::getConnectorId))
        );
    }
}