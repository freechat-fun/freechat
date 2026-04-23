package fun.freechat.mapper;

import static fun.freechat.mapper.InteractiveStatsScoreDetailsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.InteractiveStatsScoreDetails;
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
public interface InteractiveStatsScoreDetailsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, referType, referId, userId, score);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<InteractiveStatsScoreDetails> insertStatement);

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

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("InteractiveStatsScoreDetailsResult")
    Optional<InteractiveStatsScoreDetails> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, interactiveStatsScoreDetails, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, interactiveStatsScoreDetails, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(InteractiveStatsScoreDetails row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStatsScoreDetails, c ->
            c.withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(referType)
            .withMappedColumn(referId)
            .withMappedColumn(userId)
            .withMappedColumn(score)
        );
    }

    default int insertSelective(InteractiveStatsScoreDetails row) {
        return MyBatis3Utils.insert(this::insert, row, interactiveStatsScoreDetails, c ->
            c.withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(referType, row::getReferType)
            .withMappedColumnWhenPresent(referId, row::getReferId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(score, row::getScore)
        );
    }

    default Optional<InteractiveStatsScoreDetails> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, interactiveStatsScoreDetails, completer);
    }

    default List<InteractiveStatsScoreDetails> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, interactiveStatsScoreDetails, completer);
    }

    default List<InteractiveStatsScoreDetails> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, interactiveStatsScoreDetails, completer);
    }

    default Optional<InteractiveStatsScoreDetails> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, interactiveStatsScoreDetails, completer);
    }

    static UpdateDSL updateAllColumns(InteractiveStatsScoreDetails row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(referType).equalTo(row::getReferType)
                .set(referId).equalTo(row::getReferId)
                .set(userId).equalTo(row::getUserId)
                .set(score).equalTo(row::getScore);
    }

    static UpdateDSL updateSelectiveColumns(InteractiveStatsScoreDetails row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(referType).equalToWhenPresent(row::getReferType)
                .set(referId).equalToWhenPresent(row::getReferId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(score).equalToWhenPresent(row::getScore);
    }

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