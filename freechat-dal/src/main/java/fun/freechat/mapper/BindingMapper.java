package fun.freechat.mapper;

import static fun.freechat.mapper.BindingDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.Binding;
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
public interface BindingMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, userId, platform, sub, iss, issuedAt, expiresAt, aud, refreshToken);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<Binding> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="BindingResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="platform", property="platform", jdbcType=JdbcType.VARCHAR),
        @Result(column="sub", property="sub", jdbcType=JdbcType.VARCHAR),
        @Result(column="iss", property="iss", jdbcType=JdbcType.VARCHAR),
        @Result(column="issued_at", property="issuedAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="aud", property="aud", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="refresh_token", property="refreshToken", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<Binding> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("BindingResult")
    Optional<Binding> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(Binding row) {
        return MyBatis3Utils.insert(this::insert, row, binding, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(platform).toProperty("platform")
            .map(sub).toProperty("sub")
            .map(iss).toProperty("iss")
            .map(issuedAt).toProperty("issuedAt")
            .map(expiresAt).toProperty("expiresAt")
            .map(aud).toProperty("aud")
            .map(refreshToken).toProperty("refreshToken")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(Binding row) {
        return MyBatis3Utils.insert(this::insert, row, binding, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(platform).toPropertyWhenPresent("platform", row::getPlatform)
            .map(sub).toPropertyWhenPresent("sub", row::getSub)
            .map(iss).toPropertyWhenPresent("iss", row::getIss)
            .map(issuedAt).toPropertyWhenPresent("issuedAt", row::getIssuedAt)
            .map(expiresAt).toPropertyWhenPresent("expiresAt", row::getExpiresAt)
            .map(aud).toPropertyWhenPresent("aud", row::getAud)
            .map(refreshToken).toPropertyWhenPresent("refreshToken", row::getRefreshToken)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<Binding> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<Binding> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<Binding> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<Binding> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, binding, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(Binding row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(platform).equalTo(row::getPlatform)
                .set(sub).equalTo(row::getSub)
                .set(iss).equalTo(row::getIss)
                .set(issuedAt).equalTo(row::getIssuedAt)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(aud).equalTo(row::getAud)
                .set(refreshToken).equalTo(row::getRefreshToken);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Binding row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(platform).equalToWhenPresent(row::getPlatform)
                .set(sub).equalToWhenPresent(row::getSub)
                .set(iss).equalToWhenPresent(row::getIss)
                .set(issuedAt).equalToWhenPresent(row::getIssuedAt)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(aud).equalToWhenPresent(row::getAud)
                .set(refreshToken).equalToWhenPresent(row::getRefreshToken);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(Binding row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(platform).equalTo(row::getPlatform)
            .set(sub).equalTo(row::getSub)
            .set(iss).equalTo(row::getIss)
            .set(issuedAt).equalTo(row::getIssuedAt)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .set(aud).equalTo(row::getAud)
            .set(refreshToken).equalTo(row::getRefreshToken)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(Binding row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(platform).equalToWhenPresent(row::getPlatform)
            .set(sub).equalToWhenPresent(row::getSub)
            .set(iss).equalToWhenPresent(row::getIss)
            .set(issuedAt).equalToWhenPresent(row::getIssuedAt)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .set(aud).equalToWhenPresent(row::getAud)
            .set(refreshToken).equalToWhenPresent(row::getRefreshToken)
            .where(id, isEqualTo(row::getId))
        );
    }
}