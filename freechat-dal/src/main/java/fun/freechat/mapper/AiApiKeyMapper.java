package fun.freechat.mapper;

import fun.freechat.model.AiApiKey;
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

import static fun.freechat.mapper.AiApiKeyDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface AiApiKeyMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, gmtUsed, name, provider, enabled, userId, token);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<AiApiKey> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AiApiKeyResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_used", property="gmtUsed", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="provider", property="provider", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="token", property="token", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<AiApiKey> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AiApiKeyResult")
    Optional<AiApiKey> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(AiApiKey row) {
        return MyBatis3Utils.insert(this::insert, row, aiApiKey, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(gmtUsed).toProperty("gmtUsed")
            .map(name).toProperty("name")
            .map(provider).toProperty("provider")
            .map(enabled).toProperty("enabled")
            .map(userId).toProperty("userId")
            .map(token).toProperty("token")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(AiApiKey row) {
        return MyBatis3Utils.insert(this::insert, row, aiApiKey, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(gmtUsed).toPropertyWhenPresent("gmtUsed", row::getGmtUsed)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(provider).toPropertyWhenPresent("provider", row::getProvider)
            .map(enabled).toPropertyWhenPresent("enabled", row::getEnabled)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(token).toPropertyWhenPresent("token", row::getToken)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<AiApiKey> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AiApiKey> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AiApiKey> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<AiApiKey> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, aiApiKey, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(AiApiKey row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtUsed).equalTo(row::getGmtUsed)
                .set(name).equalTo(row::getName)
                .set(provider).equalTo(row::getProvider)
                .set(enabled).equalTo(row::getEnabled)
                .set(userId).equalTo(row::getUserId)
                .set(token).equalTo(row::getToken);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(AiApiKey row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtUsed).equalToWhenPresent(row::getGmtUsed)
                .set(name).equalToWhenPresent(row::getName)
                .set(provider).equalToWhenPresent(row::getProvider)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(token).equalToWhenPresent(row::getToken);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(AiApiKey row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(gmtUsed).equalTo(row::getGmtUsed)
            .set(name).equalTo(row::getName)
            .set(provider).equalTo(row::getProvider)
            .set(enabled).equalTo(row::getEnabled)
            .set(userId).equalTo(row::getUserId)
            .set(token).equalTo(row::getToken)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(AiApiKey row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(gmtUsed).equalToWhenPresent(row::getGmtUsed)
            .set(name).equalToWhenPresent(row::getName)
            .set(provider).equalToWhenPresent(row::getProvider)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(token).equalToWhenPresent(row::getToken)
            .where(id, isEqualTo(row::getId))
        );
    }
}