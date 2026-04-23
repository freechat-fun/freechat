package fun.freechat.mapper;

import static fun.freechat.mapper.AiApiKeyDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.AiApiKey;
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
public interface AiApiKeyMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, gmtUsed, name, provider, enabled, userId, token);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<AiApiKey> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AiApiKeyResult")
    Optional<AiApiKey> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, aiApiKey, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, aiApiKey, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(AiApiKey row) {
        return MyBatis3Utils.insert(this::insert, row, aiApiKey, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(gmtUsed)
            .withMappedColumn(name)
            .withMappedColumn(provider)
            .withMappedColumn(enabled)
            .withMappedColumn(userId)
            .withMappedColumn(token)
        );
    }

    default int insertSelective(AiApiKey row) {
        return MyBatis3Utils.insert(this::insert, row, aiApiKey, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(gmtUsed, row::getGmtUsed)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(provider, row::getProvider)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(token, row::getToken)
        );
    }

    default Optional<AiApiKey> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, aiApiKey, completer);
    }

    default List<AiApiKey> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, aiApiKey, completer);
    }

    default List<AiApiKey> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, aiApiKey, completer);
    }

    default Optional<AiApiKey> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, aiApiKey, completer);
    }

    static UpdateDSL updateAllColumns(AiApiKey row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtUsed).equalTo(row::getGmtUsed)
                .set(name).equalTo(row::getName)
                .set(provider).equalTo(row::getProvider)
                .set(enabled).equalTo(row::getEnabled)
                .set(userId).equalTo(row::getUserId)
                .set(token).equalTo(row::getToken);
    }

    static UpdateDSL updateSelectiveColumns(AiApiKey row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtUsed).equalToWhenPresent(row::getGmtUsed)
                .set(name).equalToWhenPresent(row::getName)
                .set(provider).equalToWhenPresent(row::getProvider)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(token).equalToWhenPresent(row::getToken);
    }

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