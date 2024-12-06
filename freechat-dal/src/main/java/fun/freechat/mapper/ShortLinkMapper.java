package fun.freechat.mapper;

import static fun.freechat.mapper.ShortLinkDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.ShortLink;
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
public interface ShortLinkMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, token, path, expiresAt);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<ShortLink> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ShortLinkResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="token", property="token", jdbcType=JdbcType.VARCHAR),
        @Result(column="path", property="path", jdbcType=JdbcType.VARCHAR),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<ShortLink> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ShortLinkResult")
    Optional<ShortLink> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(ShortLink row) {
        return MyBatis3Utils.insert(this::insert, row, shortLink, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(token).toProperty("token")
            .map(path).toProperty("path")
            .map(expiresAt).toProperty("expiresAt")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(ShortLink row) {
        return MyBatis3Utils.insert(this::insert, row, shortLink, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(token).toPropertyWhenPresent("token", row::getToken)
            .map(path).toPropertyWhenPresent("path", row::getPath)
            .map(expiresAt).toPropertyWhenPresent("expiresAt", row::getExpiresAt)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ShortLink> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ShortLink> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<ShortLink> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<ShortLink> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, shortLink, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(ShortLink row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(token).equalTo(row::getToken)
                .set(path).equalTo(row::getPath)
                .set(expiresAt).equalTo(row::getExpiresAt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(ShortLink row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(token).equalToWhenPresent(row::getToken)
                .set(path).equalToWhenPresent(row::getPath)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(ShortLink row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(token).equalTo(row::getToken)
            .set(path).equalTo(row::getPath)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(ShortLink row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(token).equalToWhenPresent(row::getToken)
            .set(path).equalToWhenPresent(row::getPath)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}