package fun.freechat.mapper;

import static fun.freechat.mapper.PluginInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.PluginInfo;
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
public interface PluginInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(pluginId, pluginUid, gmtCreate, gmtModified, userId, visibility, name, manifestFormat, apiFormat, provider, manifestInfo, apiInfo, ext);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.pluginId", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<PluginInfo> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="PluginInfoResult", value = {
        @Result(column="plugin_id", property="pluginId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="plugin_uid", property="pluginUid", jdbcType=JdbcType.VARCHAR),
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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("PluginInfoResult")
    Optional<PluginInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, pluginInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, pluginInfo, completer);
    }

    default int deleteByPrimaryKey(Long pluginId_) {
        return delete(c -> 
            c.where(pluginId, isEqualTo(pluginId_))
        );
    }

    default int insert(PluginInfo row) {
        return MyBatis3Utils.insert(this::insert, row, pluginInfo, c ->
            c.withMappedColumn(pluginUid)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(userId)
            .withMappedColumn(visibility)
            .withMappedColumn(name)
            .withMappedColumn(manifestFormat)
            .withMappedColumn(apiFormat)
            .withMappedColumn(provider)
            .withMappedColumn(manifestInfo)
            .withMappedColumn(apiInfo)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(PluginInfo row) {
        return MyBatis3Utils.insert(this::insert, row, pluginInfo, c ->
            c.withMappedColumnWhenPresent(pluginUid, row::getPluginUid)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(visibility, row::getVisibility)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(manifestFormat, row::getManifestFormat)
            .withMappedColumnWhenPresent(apiFormat, row::getApiFormat)
            .withMappedColumnWhenPresent(provider, row::getProvider)
            .withMappedColumnWhenPresent(manifestInfo, row::getManifestInfo)
            .withMappedColumnWhenPresent(apiInfo, row::getApiInfo)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<PluginInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, pluginInfo, completer);
    }

    default List<PluginInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, pluginInfo, completer);
    }

    default List<PluginInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, pluginInfo, completer);
    }

    default Optional<PluginInfo> selectByPrimaryKey(Long pluginId_) {
        return selectOne(c ->
            c.where(pluginId, isEqualTo(pluginId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, pluginInfo, completer);
    }

    static UpdateDSL updateAllColumns(PluginInfo row, UpdateDSL dsl) {
        return dsl.set(pluginId).equalTo(row::getPluginId)
                .set(pluginUid).equalTo(row::getPluginUid)
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

    static UpdateDSL updateSelectiveColumns(PluginInfo row, UpdateDSL dsl) {
        return dsl.set(pluginId).equalToWhenPresent(row::getPluginId)
                .set(pluginUid).equalToWhenPresent(row::getPluginUid)
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

    default int updateByPrimaryKey(PluginInfo row) {
        return update(c ->
            c.set(pluginUid).equalTo(row::getPluginUid)
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
            .set(ext).equalTo(row::getExt)
            .where(pluginId, isEqualTo(row::getPluginId))
        );
    }

    default int updateByPrimaryKeySelective(PluginInfo row) {
        return update(c ->
            c.set(pluginUid).equalToWhenPresent(row::getPluginUid)
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
            .set(ext).equalToWhenPresent(row::getExt)
            .where(pluginId, isEqualTo(row::getPluginId))
        );
    }
}