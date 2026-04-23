package fun.freechat.mapper;

import static fun.freechat.mapper.InteractiveStatsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.InteractiveStats;
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
public interface InteractiveStatsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, referType, referId, viewCount, referCount, recommendCount, scoreCount, score);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<InteractiveStats> insertStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="InteractiveStatsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="refer_type", property="referType", jdbcType=JdbcType.VARCHAR),
        @Result(column="refer_id", property="referId", jdbcType=JdbcType.VARCHAR),
        @Result(column="view_count", property="viewCount", jdbcType=JdbcType.BIGINT),
        @Result(column="refer_count", property="referCount", jdbcType=JdbcType.BIGINT),
        @Result(column="recommend_count", property="recommendCount", jdbcType=JdbcType.BIGINT),
        @Result(column="score_count", property="scoreCount", jdbcType=JdbcType.BIGINT),
        @Result(column="score", property="score", jdbcType=JdbcType.BIGINT)
    })
    List<InteractiveStats> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("InteractiveStatsResult")
    Optional<InteractiveStats> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, interactiveStats, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, interactiveStats, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(InteractiveStats row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStats, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(referType)
            .withMappedColumn(referId)
            .withMappedColumn(viewCount)
            .withMappedColumn(referCount)
            .withMappedColumn(recommendCount)
            .withMappedColumn(scoreCount)
            .withMappedColumn(score)
        );
    }

    default int insertSelective(InteractiveStats row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStats, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(referType, row::getReferType)
            .withMappedColumnWhenPresent(referId, row::getReferId)
            .withMappedColumnWhenPresent(viewCount, row::getViewCount)
            .withMappedColumnWhenPresent(referCount, row::getReferCount)
            .withMappedColumnWhenPresent(recommendCount, row::getRecommendCount)
            .withMappedColumnWhenPresent(scoreCount, row::getScoreCount)
            .withMappedColumnWhenPresent(score, row::getScore)
        );
    }

    default Optional<InteractiveStats> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, interactiveStats, completer);
    }

    default List<InteractiveStats> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, interactiveStats, completer);
    }

    default List<InteractiveStats> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, interactiveStats, completer);
    }

    default Optional<InteractiveStats> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, interactiveStats, completer);
    }

    static UpdateDSL updateAllColumns(InteractiveStats row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(referType).equalTo(row::getReferType)
                .set(referId).equalTo(row::getReferId)
                .set(viewCount).equalTo(row::getViewCount)
                .set(referCount).equalTo(row::getReferCount)
                .set(recommendCount).equalTo(row::getRecommendCount)
                .set(scoreCount).equalTo(row::getScoreCount)
                .set(score).equalTo(row::getScore);
    }

    static UpdateDSL updateSelectiveColumns(InteractiveStats row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(referType).equalToWhenPresent(row::getReferType)
                .set(referId).equalToWhenPresent(row::getReferId)
                .set(viewCount).equalToWhenPresent(row::getViewCount)
                .set(referCount).equalToWhenPresent(row::getReferCount)
                .set(recommendCount).equalToWhenPresent(row::getRecommendCount)
                .set(scoreCount).equalToWhenPresent(row::getScoreCount)
                .set(score).equalToWhenPresent(row::getScore);
    }

    default int updateByPrimaryKey(InteractiveStats row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(referType).equalTo(row::getReferType)
            .set(referId).equalTo(row::getReferId)
            .set(viewCount).equalTo(row::getViewCount)
            .set(referCount).equalTo(row::getReferCount)
            .set(recommendCount).equalTo(row::getRecommendCount)
            .set(scoreCount).equalTo(row::getScoreCount)
            .set(score).equalTo(row::getScore)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(InteractiveStats row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(referType).equalToWhenPresent(row::getReferType)
            .set(referId).equalToWhenPresent(row::getReferId)
            .set(viewCount).equalToWhenPresent(row::getViewCount)
            .set(referCount).equalToWhenPresent(row::getReferCount)
            .set(recommendCount).equalToWhenPresent(row::getRecommendCount)
            .set(scoreCount).equalToWhenPresent(row::getScoreCount)
            .set(score).equalToWhenPresent(row::getScore)
            .where(id, isEqualTo(row::getId))
        );
    }
}