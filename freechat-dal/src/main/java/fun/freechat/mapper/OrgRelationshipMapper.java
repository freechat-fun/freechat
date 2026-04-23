package fun.freechat.mapper;

import static fun.freechat.mapper.OrgRelationshipDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.OrgRelationship;
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
public interface OrgRelationshipMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, userId, ownerId, isVirtual, enabled);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<OrgRelationship> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="OrgRelationshipResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="owner_id", property="ownerId", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_virtual", property="isVirtual", jdbcType=JdbcType.TINYINT),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT)
    })
    List<OrgRelationship> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("OrgRelationshipResult")
    Optional<OrgRelationship> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, orgRelationship, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, orgRelationship, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(OrgRelationship row) {
        return MyBatis3Utils.insert(this::insert, row, orgRelationship, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(ownerId)
            .withMappedColumn(isVirtual)
            .withMappedColumn(enabled)
        );
    }

    default int insertSelective(OrgRelationship row) {
        return MyBatis3Utils.insert(this::insert, row, orgRelationship, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(ownerId, row::getOwnerId)
            .withMappedColumnWhenPresent(isVirtual, row::getIsVirtual)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
        );
    }

    default Optional<OrgRelationship> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, orgRelationship, completer);
    }

    default List<OrgRelationship> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, orgRelationship, completer);
    }

    default List<OrgRelationship> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, orgRelationship, completer);
    }

    default Optional<OrgRelationship> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, orgRelationship, completer);
    }

    static UpdateDSL updateAllColumns(OrgRelationship row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(ownerId).equalTo(row::getOwnerId)
                .set(isVirtual).equalTo(row::getIsVirtual)
                .set(enabled).equalTo(row::getEnabled);
    }

    static UpdateDSL updateSelectiveColumns(OrgRelationship row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(ownerId).equalToWhenPresent(row::getOwnerId)
                .set(isVirtual).equalToWhenPresent(row::getIsVirtual)
                .set(enabled).equalToWhenPresent(row::getEnabled);
    }

    default int updateByPrimaryKey(OrgRelationship row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(ownerId).equalTo(row::getOwnerId)
            .set(isVirtual).equalTo(row::getIsVirtual)
            .set(enabled).equalTo(row::getEnabled)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(OrgRelationship row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(ownerId).equalToWhenPresent(row::getOwnerId)
            .set(isVirtual).equalToWhenPresent(row::getIsVirtual)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .where(id, isEqualTo(row::getId))
        );
    }
}