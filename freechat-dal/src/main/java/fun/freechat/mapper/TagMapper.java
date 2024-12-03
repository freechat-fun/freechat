package fun.freechat.mapper;

import static fun.freechat.mapper.TagDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.Tag;
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
public interface TagMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(id, gmtCreate, gmtModified, content, referType, referId, userId);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="row.id", before=false, resultType=Long.class)
    int insert(InsertStatementProvider<Tag> insertStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TagResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="content", property="content", jdbcType=JdbcType.VARCHAR),
        @Result(column="refer_type", property="referType", jdbcType=JdbcType.VARCHAR),
        @Result(column="refer_id", property="referId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR)
    })
    List<Tag> selectMany(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TagResult")
    Optional<Tag> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(Tag row) {
        return MyBatis3Utils.insert(this::insert, row, tag, c ->
            c.map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(content).toProperty("content")
            .map(referType).toProperty("referType")
            .map(referId).toProperty("referId")
            .map(userId).toProperty("userId")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(Tag row) {
        return MyBatis3Utils.insert(this::insert, row, tag, c ->
            c.map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(content).toPropertyWhenPresent("content", row::getContent)
            .map(referType).toPropertyWhenPresent("referType", row::getReferType)
            .map(referId).toPropertyWhenPresent("referId", row::getReferId)
            .map(userId).toPropertyWhenPresent("userId", row::getUserId)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<Tag> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<Tag> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<Tag> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<Tag> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, tag, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(Tag row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(content).equalTo(row::getContent)
                .set(referType).equalTo(row::getReferType)
                .set(referId).equalTo(row::getReferId)
                .set(userId).equalTo(row::getUserId);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Tag row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(content).equalToWhenPresent(row::getContent)
                .set(referType).equalToWhenPresent(row::getReferType)
                .set(referId).equalToWhenPresent(row::getReferId)
                .set(userId).equalToWhenPresent(row::getUserId);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKey(Tag row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(content).equalTo(row::getContent)
            .set(referType).equalTo(row::getReferType)
            .set(referId).equalTo(row::getReferId)
            .set(userId).equalTo(row::getUserId)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int updateByPrimaryKeySelective(Tag row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(content).equalToWhenPresent(row::getContent)
            .set(referType).equalToWhenPresent(row::getReferType)
            .set(referId).equalToWhenPresent(row::getReferId)
            .set(userId).equalToWhenPresent(row::getUserId)
            .where(id, isEqualTo(row::getId))
        );
    }
}