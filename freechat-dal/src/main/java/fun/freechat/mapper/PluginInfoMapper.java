package fun.freechat.mapper;

import static fun.freechat.mapper.PluginInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.PluginInfo;
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
public interface PluginInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<PluginInfo>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(pluginId, gmtCreate, gmtModified, userId, visibility, name, manifestFormat, apiFormat, provider, manifestInfo, apiInfo, ext);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="PluginInfoResult", value = {
        @Result(column="plugin_id", property="pluginId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="visibility", property="visibility", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="manifest_format", property="manifestFormat", jdbcType=JdbcType.VARCHAR),
        @Result(column="api_format", property="apiFormat", jdbcType=JdbcType.VARCHAR),
        @Result(column="provider", property="provider", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="manifest_info", property="manifestInfo", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="api_info", property="apiInfo", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<PluginInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PluginInfoResult")
    Optional<PluginInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String pluginId_) {
        return delete(c -> 
            c.where(pluginId, isEqualTo(pluginId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(PluginInfo row) {
        return MyBatis3Utils.insert(this::insert, row, pluginInfo, c ->
            c.map(pluginId).toProperty("pluginId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(manifestFormat).toProperty("manifestFormat")
            .map(apiFormat).toProperty("apiFormat")
            .map(provider).toProperty("provider")
            .map(manifestInfo).toProperty("manifestInfo")
            .map(apiInfo).toProperty("apiInfo")
            .map(ext).toProperty("ext")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<PluginInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, pluginInfo, c ->
            c.map(pluginId).toProperty("pluginId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(userId).toProperty("userId")
            .map(visibility).toProperty("visibility")
            .map(name).toProperty("name")
            .map(manifestFormat).toProperty("manifestFormat")
            .map(apiFormat).toProperty("apiFormat")
            .map(provider).toProperty("provider")
            .map(manifestInfo).toProperty("manifestInfo")
            .map(apiInfo).toProperty("apiInfo")
            .map(ext).toProperty("ext")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(PluginInfo row) {
        return MyBatis3Utils.insert(this::insert, row, pluginInfo, c ->
            c.map(pluginId).toPropertyWhenPresent("pluginId", row::getPluginId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(visibility).toPropertyWhenPresent("visibility", row::getVisibility)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(manifestFormat).toPropertyWhenPresent("manifestFormat", row::getManifestFormat)
            .map(apiFormat).toPropertyWhenPresent("apiFormat", row::getApiFormat)
            .map(provider).toPropertyWhenPresent("provider", row::getProvider)
            .map(manifestInfo).toPropertyWhenPresent("manifestInfo", row::getManifestInfo)
            .map(apiInfo).toPropertyWhenPresent("apiInfo", row::getApiInfo)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PluginInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PluginInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<PluginInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<PluginInfo> selectByPrimaryKey(String pluginId_) {
        return selectOne(c ->
            c.where(pluginId, isEqualTo(pluginId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, pluginInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(PluginInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(pluginId).equalTo(row::getPluginId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(userId).equalTo(row::getUserId)
                .set(visibility).equalTo(row::getVisibility)
                .set(name).equalTo(row::getName)
                .set(manifestFormat).equalTo(row::getManifestFormat)
                .set(apiFormat).equalTo(row::getApiFormat)
                .set(provider).equalTo(row::getProvider)
                .set(manifestInfo).equalTo(row::getManifestInfo)
                .set(apiInfo).equalTo(row::getApiInfo)
                .set(ext).equalTo(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(PluginInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(pluginId).equalToWhenPresent(row::getPluginId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(visibility).equalToWhenPresent(row::getVisibility)
                .set(name).equalToWhenPresent(row::getName)
                .set(manifestFormat).equalToWhenPresent(row::getManifestFormat)
                .set(apiFormat).equalToWhenPresent(row::getApiFormat)
                .set(provider).equalToWhenPresent(row::getProvider)
                .set(manifestInfo).equalToWhenPresent(row::getManifestInfo)
                .set(apiInfo).equalToWhenPresent(row::getApiInfo)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(PluginInfo row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(userId).equalTo(row::getUserId)
            .set(visibility).equalTo(row::getVisibility)
            .set(name).equalTo(row::getName)
            .set(manifestFormat).equalTo(row::getManifestFormat)
            .set(apiFormat).equalTo(row::getApiFormat)
            .set(provider).equalTo(row::getProvider)
            .set(manifestInfo).equalTo(row::getManifestInfo)
            .set(apiInfo).equalTo(row::getApiInfo)
            .set(ext).equalTo(row::getExt)
            .where(pluginId, isEqualTo(row::getPluginId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(PluginInfo row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(visibility).equalToWhenPresent(row::getVisibility)
            .set(name).equalToWhenPresent(row::getName)
            .set(manifestFormat).equalToWhenPresent(row::getManifestFormat)
            .set(apiFormat).equalToWhenPresent(row::getApiFormat)
            .set(provider).equalToWhenPresent(row::getProvider)
            .set(manifestInfo).equalToWhenPresent(row::getManifestInfo)
            .set(apiInfo).equalToWhenPresent(row::getApiInfo)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(pluginId, isEqualTo(row::getPluginId))
        );
    }
}