package fun.freechat.mapper;

import fun.freechat.model.InteractiveStats;
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

import static fun.freechat.mapper.InteractiveStatsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface InteractiveStatsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, referType, referId, viewCount, referCount, recommendCount, scoreCount, score);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<InteractiveStats> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("InteractiveStatsResult")
    Optional<InteractiveStats> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(InteractiveStats row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStats, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(referType).toProperty("referType")
            .map(referId).toProperty("referId")
            .map(viewCount).toProperty("viewCount")
            .map(referCount).toProperty("referCount")
            .map(recommendCount).toProperty("recommendCount")
            .map(scoreCount).toProperty("scoreCount")
            .map(score).toProperty("score")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(InteractiveStats row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStats, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(referType).toPropertyWhenPresent("referType", row::getReferType)
            .map(referId).toPropertyWhenPresent("referId", row::getReferId)
            .map(viewCount).toPropertyWhenPresent("viewCount", row::getViewCount)
            .map(referCount).toPropertyWhenPresent("referCount", row::getReferCount)
            .map(recommendCount).toPropertyWhenPresent("recommendCount", row::getRecommendCount)
            .map(scoreCount).toPropertyWhenPresent("scoreCount", row::getScoreCount)
            .map(score).toPropertyWhenPresent("score", row::getScore)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<InteractiveStats> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<InteractiveStats> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<InteractiveStats> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<InteractiveStats> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, interactiveStats, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(InteractiveStats row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(referType).equalTo(row::getReferType)
                .set(referId).equalTo(row::getReferId)
                .set(viewCount).equalTo(row::getViewCount)
                .set(referCount).equalTo(row::getReferCount)
                .set(recommendCount).equalTo(row::getRecommendCount)
                .set(scoreCount).equalTo(row::getScoreCount)
                .set(score).equalTo(row::getScore);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(InteractiveStats row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(referType).equalToWhenPresent(row::getReferType)
                .set(referId).equalToWhenPresent(row::getReferId)
                .set(viewCount).equalToWhenPresent(row::getViewCount)
                .set(referCount).equalToWhenPresent(row::getReferCount)
                .set(recommendCount).equalToWhenPresent(row::getRecommendCount)
                .set(scoreCount).equalToWhenPresent(row::getScoreCount)
                .set(score).equalToWhenPresent(row::getScore);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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