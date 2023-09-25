package fun.freechat.mapper;

import static fun.freechat.mapper.NonRealTimeConfigDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.NonRealTimeConfig;
import jakarta.annotation.Generated;
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
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface NonRealTimeConfigMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<NonRealTimeConfig>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(name, version, gmtCreate, gmtModified, format, userId, content);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="NonRealTimeConfigResult", value = {
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="content", property="content", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<NonRealTimeConfig> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("NonRealTimeConfigResult")
    Optional<NonRealTimeConfig> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String name_, Integer version_) {
        return delete(c -> 
            c.where(name, isEqualTo(name_))
            .and(version, isEqualTo(version_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(NonRealTimeConfig row) {
        return MyBatis3Utils.insert(this::insert, row, nonRealTimeConfig, c ->
            c.map(name).toProperty("name")
            .map(version).toProperty("version")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(format).toProperty("format")
            .map(userId).toProperty("userId")
            .map(content).toProperty("content")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<NonRealTimeConfig> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, nonRealTimeConfig, c ->
            c.map(name).toProperty("name")
            .map(version).toProperty("version")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(format).toProperty("format")
            .map(userId).toProperty("userId")
            .map(content).toProperty("content")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(NonRealTimeConfig row) {
        return MyBatis3Utils.insert(this::insert, row, nonRealTimeConfig, c ->
            c.map(name).toPropertyWhenPresent("name", row::getName)
            .map(version).toPropertyWhenPresent("version", row::getVersion)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(format).toPropertyWhenPresent("format", row::getFormat)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(content).toPropertyWhenPresent("content", row::getContent)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<NonRealTimeConfig> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<NonRealTimeConfig> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<NonRealTimeConfig> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<NonRealTimeConfig> selectByPrimaryKey(String name_, Integer version_) {
        return selectOne(c ->
            c.where(name, isEqualTo(name_))
            .and(version, isEqualTo(version_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, nonRealTimeConfig, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(NonRealTimeConfig row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalTo(row::getName)
                .set(version).equalTo(row::getVersion)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(format).equalTo(row::getFormat)
                .set(userId).equalTo(row::getUserId)
                .set(content).equalTo(row::getContent);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(NonRealTimeConfig row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(name).equalToWhenPresent(row::getName)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(format).equalToWhenPresent(row::getFormat)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(content).equalToWhenPresent(row::getContent);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(NonRealTimeConfig row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(format).equalTo(row::getFormat)
            .set(userId).equalTo(row::getUserId)
            .set(content).equalTo(row::getContent)
            .where(name, isEqualTo(row::getName))
            .and(version, isEqualTo(row::getVersion))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(NonRealTimeConfig row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(format).equalToWhenPresent(row::getFormat)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(content).equalToWhenPresent(row::getContent)
            .where(name, isEqualTo(row::getName))
            .and(version, isEqualTo(row::getVersion))
        );
    }
}