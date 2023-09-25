package fun.freechat.mapper;

import static fun.freechat.mapper.InteractiveStatsScoreDetailsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.InteractiveStatsScoreDetails;
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
public interface InteractiveStatsScoreDetailsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, referType, referId, userId, score);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<InteractiveStatsScoreDetails> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="InteractiveStatsScoreDetailsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="refer_type", property="referType", jdbcType=JdbcType.VARCHAR),
        @Result(column="refer_id", property="referId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="score", property="score", jdbcType=JdbcType.BIGINT)
    })
    List<InteractiveStatsScoreDetails> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("InteractiveStatsScoreDetailsResult")
    Optional<InteractiveStatsScoreDetails> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(InteractiveStatsScoreDetails row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStatsScoreDetails, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(referType).toProperty("referType")
            .map(referId).toProperty("referId")
            .map(userId).toProperty("userId")
            .map(score).toProperty("score")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(InteractiveStatsScoreDetails row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStatsScoreDetails, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(referType).toPropertyWhenPresent("referType", row::getReferType)
            .map(referId).toPropertyWhenPresent("referId", row::getReferId)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(score).toPropertyWhenPresent("score", row::getScore)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<InteractiveStatsScoreDetails> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<InteractiveStatsScoreDetails> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<InteractiveStatsScoreDetails> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<InteractiveStatsScoreDetails> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, interactiveStatsScoreDetails, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(InteractiveStatsScoreDetails row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(referType).equalTo(row::getReferType)
                .set(referId).equalTo(row::getReferId)
                .set(userId).equalTo(row::getUserId)
                .set(score).equalTo(row::getScore);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(InteractiveStatsScoreDetails row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(referType).equalToWhenPresent(row::getReferType)
                .set(referId).equalToWhenPresent(row::getReferId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(score).equalToWhenPresent(row::getScore);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(InteractiveStatsScoreDetails row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(referType).equalTo(row::getReferType)
            .set(referId).equalTo(row::getReferId)
            .set(userId).equalTo(row::getUserId)
            .set(score).equalTo(row::getScore)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(InteractiveStatsScoreDetails row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(referType).equalToWhenPresent(row::getReferType)
            .set(referId).equalToWhenPresent(row::getReferId)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(score).equalToWhenPresent(row::getScore)
            .where(id, isEqualTo(row::getId))
        );
    }
}