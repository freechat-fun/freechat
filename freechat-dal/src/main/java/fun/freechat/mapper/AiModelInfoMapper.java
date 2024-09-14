package fun.freechat.mapper;

import fun.freechat.model.AiModelInfo;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.*;
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
import org.mybatis.dynamic.sql.util.mybatis3.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static fun.freechat.mapper.AiModelInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface AiModelInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<AiModelInfo>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(modelId, gmtCreate, gmtModified, name, description, provider, type);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AiModelInfoResult", value = {
        @Result(column="model_id", property="modelId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.VARCHAR),
        @Result(column="provider", property="provider", jdbcType=JdbcType.VARCHAR),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR)
    })
    List<AiModelInfo> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AiModelInfoResult")
    Optional<AiModelInfo> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String modelId_) {
        return delete(c -> 
            c.where(modelId, isEqualTo(modelId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(AiModelInfo row) {
        return MyBatis3Utils.insert(this::insert, row, aiModelInfo, c ->
            c.map(modelId).toProperty("modelId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(name).toProperty("name")
            .map(description).toProperty("description")
            .map(provider).toProperty("provider")
            .map(type).toProperty("type")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<AiModelInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, aiModelInfo, c ->
            c.map(modelId).toProperty("modelId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(name).toProperty("name")
            .map(description).toProperty("description")
            .map(provider).toProperty("provider")
            .map(type).toProperty("type")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(AiModelInfo row) {
        return MyBatis3Utils.insert(this::insert, row, aiModelInfo, c ->
            c.map(modelId).toPropertyWhenPresent("modelId", row::getModelId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(name).toPropertyWhenPresent("name", row::getName)
            .map(description).toPropertyWhenPresent("description", row::getDescription)
            .map(provider).toPropertyWhenPresent("provider", row::getProvider)
            .map(type).toPropertyWhenPresent("type", row::getType)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<AiModelInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AiModelInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<AiModelInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<AiModelInfo> selectByPrimaryKey(String modelId_) {
        return selectOne(c ->
            c.where(modelId, isEqualTo(modelId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, aiModelInfo, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(AiModelInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(modelId).equalTo(row::getModelId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(name).equalTo(row::getName)
                .set(description).equalTo(row::getDescription)
                .set(provider).equalTo(row::getProvider)
                .set(type).equalTo(row::getType);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(AiModelInfo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(modelId).equalToWhenPresent(row::getModelId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(name).equalToWhenPresent(row::getName)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(provider).equalToWhenPresent(row::getProvider)
                .set(type).equalToWhenPresent(row::getType);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(AiModelInfo row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(name).equalTo(row::getName)
            .set(description).equalTo(row::getDescription)
            .set(provider).equalTo(row::getProvider)
            .set(type).equalTo(row::getType)
            .where(modelId, isEqualTo(row::getModelId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(AiModelInfo row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(name).equalToWhenPresent(row::getName)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(provider).equalToWhenPresent(row::getProvider)
            .set(type).equalToWhenPresent(row::getType)
            .where(modelId, isEqualTo(row::getModelId))
        );
    }
}