package fun.freechat.mapper;

import static fun.freechat.mapper.ApiTokenDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ApiToken;
import jakarta.annotation.Generated;
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

@Mapper
public interface ApiTokenMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, userId, token, type, issuedAt, expiresAt, policy);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<ApiToken> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ApiTokenResult")
    Optional<ApiToken> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(ApiToken row) {
        return MyBatis3Utils.insert(this::insert, row, apiToken, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(token).toProperty("token")
            .map(type).toProperty("type")
            .map(issuedAt).toProperty("issuedAt")
            .map(expiresAt).toProperty("expiresAt")
            .map(policy).toProperty("policy")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(ApiToken row) {
        return MyBatis3Utils.insert(this::insert, row, apiToken, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(token).toPropertyWhenPresent("token", row::getToken)
            .map(type).toPropertyWhenPresent("type", row::getType)
            .map(issuedAt).toPropertyWhenPresent("issuedAt", row::getIssuedAt)
            .map(expiresAt).toPropertyWhenPresent("expiresAt", row::getExpiresAt)
            .map(policy).toPropertyWhenPresent("policy", row::getPolicy)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ApiToken> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ApiToken> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ApiToken> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ApiToken> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, apiToken, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(ApiToken row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(token).equalTo(row::getToken)
                .set(type).equalTo(row::getType)
                .set(issuedAt).equalTo(row::getIssuedAt)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(policy).equalTo(row::getPolicy);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(ApiToken row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(token).equalToWhenPresent(row::getToken)
                .set(type).equalToWhenPresent(row::getType)
                .set(issuedAt).equalToWhenPresent(row::getIssuedAt)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(policy).equalToWhenPresent(row::getPolicy);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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