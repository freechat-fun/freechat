package fun.freechat.mapper;

import static fun.freechat.mapper.BindingDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.Binding;
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
public interface BindingMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, userId, platform, sub, iss, issuedAt, expiresAt, aud, refreshToken);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<Binding> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("BindingResult")
    Optional<Binding> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, binding, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, binding, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Binding row) {
        return MyBatis3Utils.insert(this::insert, row, binding, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(platform)
            .withMappedColumn(sub)
            .withMappedColumn(iss)
            .withMappedColumn(issuedAt)
            .withMappedColumn(expiresAt)
            .withMappedColumn(aud)
            .withMappedColumn(refreshToken)
        );
    }

    default int insertSelective(Binding row) {
        return MyBatis3Utils.insert(this::insert, row, binding, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(platform, row::getPlatform)
            .withMappedColumnWhenPresent(sub, row::getSub)
            .withMappedColumnWhenPresent(iss, row::getIss)
            .withMappedColumnWhenPresent(issuedAt, row::getIssuedAt)
            .withMappedColumnWhenPresent(expiresAt, row::getExpiresAt)
            .withMappedColumnWhenPresent(aud, row::getAud)
            .withMappedColumnWhenPresent(refreshToken, row::getRefreshToken)
        );
    }

    default Optional<Binding> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, binding, completer);
    }

    default List<Binding> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, binding, completer);
    }

    default List<Binding> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, binding, completer);
    }

    default Optional<Binding> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, binding, completer);
    }

    static UpdateDSL updateAllColumns(Binding row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
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

    static UpdateDSL updateSelectiveColumns(Binding row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
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