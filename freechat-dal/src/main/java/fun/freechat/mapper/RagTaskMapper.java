package fun.freechat.mapper;

import static fun.freechat.mapper.RagTaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.RagTask;
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
public interface RagTaskMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, gmtStart, gmtEnd, characterUid, sourceType, maxSegmentSize, maxOverlapSize, status, source, ext);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<RagTask> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("RagTaskResult")
    Optional<RagTask> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, ragTask, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, ragTask, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(RagTask row) {
        return MyBatis3Utils.insert(this::insert, row, ragTask, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(gmtStart)
            .withMappedColumn(gmtEnd)
            .withMappedColumn(characterUid)
            .withMappedColumn(sourceType)
            .withMappedColumn(maxSegmentSize)
            .withMappedColumn(maxOverlapSize)
            .withMappedColumn(status)
            .withMappedColumn(source)
            .withMappedColumn(ext)
        );
    }

    default int insertSelective(RagTask row) {
        return MyBatis3Utils.insert(this::insert, row, ragTask, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(gmtStart, row::getGmtStart)
            .withMappedColumnWhenPresent(gmtEnd, row::getGmtEnd)
            .withMappedColumnWhenPresent(characterUid, row::getCharacterUid)
            .withMappedColumnWhenPresent(sourceType, row::getSourceType)
            .withMappedColumnWhenPresent(maxSegmentSize, row::getMaxSegmentSize)
            .withMappedColumnWhenPresent(maxOverlapSize, row::getMaxOverlapSize)
            .withMappedColumnWhenPresent(status, row::getStatus)
            .withMappedColumnWhenPresent(source, row::getSource)
            .withMappedColumnWhenPresent(ext, row::getExt)
        );
    }

    default Optional<RagTask> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, ragTask, completer);
    }

    default List<RagTask> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, ragTask, completer);
    }

    default List<RagTask> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, ragTask, completer);
    }

    default Optional<RagTask> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, ragTask, completer);
    }

    static UpdateDSL updateAllColumns(RagTask row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
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

    static UpdateDSL updateSelectiveColumns(RagTask row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
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