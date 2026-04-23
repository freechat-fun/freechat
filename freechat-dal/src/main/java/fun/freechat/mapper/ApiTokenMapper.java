package fun.freechat.mapper;

import static fun.freechat.mapper.ApiTokenDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ApiToken;
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
public interface ApiTokenMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, userId, token, type, issuedAt, expiresAt, policy);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<ApiToken> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ApiTokenResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="token", property="token", jdbcType=JdbcType.VARCHAR),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="issued_at", property="issuedAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="policy", property="policy", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<ApiToken> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ApiTokenResult")
    Optional<ApiToken> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, apiToken, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, apiToken, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(ApiToken row) {
        return MyBatis3Utils.insert(this::insert, row, apiToken, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(token)
            .withMappedColumn(type)
            .withMappedColumn(issuedAt)
            .withMappedColumn(expiresAt)
            .withMappedColumn(policy)
        );
    }

    default int insertSelective(ApiToken row) {
        return MyBatis3Utils.insert(this::insert, row, apiToken, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(token, row::getToken)
            .withMappedColumnWhenPresent(type, row::getType)
            .withMappedColumnWhenPresent(issuedAt, row::getIssuedAt)
            .withMappedColumnWhenPresent(expiresAt, row::getExpiresAt)
            .withMappedColumnWhenPresent(policy, row::getPolicy)
        );
    }

    default Optional<ApiToken> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, apiToken, completer);
    }

    default List<ApiToken> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, apiToken, completer);
    }

    default List<ApiToken> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, apiToken, completer);
    }

    default Optional<ApiToken> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, apiToken, completer);
    }

    static UpdateDSL updateAllColumns(ApiToken row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(token).equalTo(row::getToken)
                .set(type).equalTo(row::getType)
                .set(issuedAt).equalTo(row::getIssuedAt)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(policy).equalTo(row::getPolicy);
    }

    static UpdateDSL updateSelectiveColumns(ApiToken row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(token).equalToWhenPresent(row::getToken)
                .set(type).equalToWhenPresent(row::getType)
                .set(issuedAt).equalToWhenPresent(row::getIssuedAt)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(policy).equalToWhenPresent(row::getPolicy);
    }

    default int updateByPrimaryKey(ApiToken row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(token).equalTo(row::getToken)
            .set(type).equalTo(row::getType)
            .set(issuedAt).equalTo(row::getIssuedAt)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .set(policy).equalTo(row::getPolicy)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(ApiToken row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(token).equalToWhenPresent(row::getToken)
            .set(type).equalToWhenPresent(row::getType)
            .set(issuedAt).equalToWhenPresent(row::getIssuedAt)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .set(policy).equalToWhenPresent(row::getPolicy)
            .where(id, isEqualTo(row::getId))
        );
    }
}