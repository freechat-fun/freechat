package fun.freechat.mapper;

import fun.freechat.model.RagTask;
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

import static fun.freechat.mapper.RagTaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface RagTaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, gmtStart, gmtEnd, characterUid, sourceType, maxSegmentSize, maxOverlapSize, status, source, ext);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<RagTask> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="RagTaskResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_start", property="gmtStart", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_end", property="gmtEnd", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="character_uid", property="characterUid", jdbcType=JdbcType.VARCHAR),
        @Result(column="source_type", property="sourceType", jdbcType=JdbcType.VARCHAR),
        @Result(column="max_segment_size", property="maxSegmentSize", jdbcType=JdbcType.INTEGER),
        @Result(column="max_overlap_size", property="maxOverlapSize", jdbcType=JdbcType.INTEGER),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="source", property="source", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="ext", property="ext", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<RagTask> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("RagTaskResult")
    Optional<RagTask> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(RagTask row) {
        return MyBatis3Utils.insert(this::insert, row, ragTask, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(gmtStart).toProperty("gmtStart")
            .map(gmtEnd).toProperty("gmtEnd")
            .map(characterUid).toProperty("characterUid")
            .map(sourceType).toProperty("sourceType")
            .map(maxSegmentSize).toProperty("maxSegmentSize")
            .map(maxOverlapSize).toProperty("maxOverlapSize")
            .map(status).toProperty("status")
            .map(source).toProperty("source")
            .map(ext).toProperty("ext")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(RagTask row) {
        return MyBatis3Utils.insert(this::insert, row, ragTask, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(gmtStart).toPropertyWhenPresent("gmtStart", row::getGmtStart)
            .map(gmtEnd).toPropertyWhenPresent("gmtEnd", row::getGmtEnd)
            .map(characterUid).toPropertyWhenPresent("characterUid", row::getCharacterUid)
            .map(sourceType).toPropertyWhenPresent("sourceType", row::getSourceType)
            .map(maxSegmentSize).toPropertyWhenPresent("maxSegmentSize", row::getMaxSegmentSize)
            .map(maxOverlapSize).toPropertyWhenPresent("maxOverlapSize", row::getMaxOverlapSize)
            .map(status).toPropertyWhenPresent("status", row::getStatus)
            .map(source).toPropertyWhenPresent("source", row::getSource)
            .map(ext).toPropertyWhenPresent("ext", row::getExt)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<RagTask> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<RagTask> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<RagTask> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<RagTask> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, ragTask, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(RagTask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(gmtStart).equalTo(row::getGmtStart)
                .set(gmtEnd).equalTo(row::getGmtEnd)
                .set(characterUid).equalTo(row::getCharacterUid)
                .set(sourceType).equalTo(row::getSourceType)
                .set(maxSegmentSize).equalTo(row::getMaxSegmentSize)
                .set(maxOverlapSize).equalTo(row::getMaxOverlapSize)
                .set(status).equalTo(row::getStatus)
                .set(source).equalTo(row::getSource)
                .set(ext).equalTo(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(RagTask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(gmtStart).equalToWhenPresent(row::getGmtStart)
                .set(gmtEnd).equalToWhenPresent(row::getGmtEnd)
                .set(characterUid).equalToWhenPresent(row::getCharacterUid)
                .set(sourceType).equalToWhenPresent(row::getSourceType)
                .set(maxSegmentSize).equalToWhenPresent(row::getMaxSegmentSize)
                .set(maxOverlapSize).equalToWhenPresent(row::getMaxOverlapSize)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(source).equalToWhenPresent(row::getSource)
                .set(ext).equalToWhenPresent(row::getExt);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(RagTask row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(gmtStart).equalTo(row::getGmtStart)
            .set(gmtEnd).equalTo(row::getGmtEnd)
            .set(characterUid).equalTo(row::getCharacterUid)
            .set(sourceType).equalTo(row::getSourceType)
            .set(maxSegmentSize).equalTo(row::getMaxSegmentSize)
            .set(maxOverlapSize).equalTo(row::getMaxOverlapSize)
            .set(status).equalTo(row::getStatus)
            .set(source).equalTo(row::getSource)
            .set(ext).equalTo(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(RagTask row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(gmtStart).equalToWhenPresent(row::getGmtStart)
            .set(gmtEnd).equalToWhenPresent(row::getGmtEnd)
            .set(characterUid).equalToWhenPresent(row::getCharacterUid)
            .set(sourceType).equalToWhenPresent(row::getSourceType)
            .set(maxSegmentSize).equalToWhenPresent(row::getMaxSegmentSize)
            .set(maxOverlapSize).equalToWhenPresent(row::getMaxOverlapSize)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(source).equalToWhenPresent(row::getSource)
            .set(ext).equalToWhenPresent(row::getExt)
            .where(id, isEqualTo(row::getId))
        );
    }
}