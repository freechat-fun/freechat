package fun.freechat.mapper;

import static fun.freechat.mapper.SkillInfoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.SkillInfo;
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
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSL;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface SkillInfoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<SkillInfo>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(skillId, userId, revision, name, digest, gmtCreate, gmtModified, description, manifest);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="SkillInfoResult", value = {
        @Result(column="skill_id", property="skillId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="revision", property="revision", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="digest", property="digest", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="manifest", property="manifest", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<SkillInfo> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("SkillInfoResult")
    Optional<SkillInfo> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, skillInfo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, skillInfo, completer);
    }

    default int deleteByPrimaryKey(String skillId_) {
        return delete(c -> 
            c.where(skillId, isEqualTo(skillId_))
        );
    }

    default int insert(SkillInfo row) {
        return MyBatis3Utils.insert(this::insert, row, skillInfo, c ->
            c.withMappedColumn(skillId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(name)
            .withMappedColumn(digest)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(description)
            .withMappedColumn(manifest)
        );
    }

    default int insertMultiple(Collection<SkillInfo> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, skillInfo, c ->
            c.withMappedColumn(skillId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(name)
            .withMappedColumn(digest)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(description)
            .withMappedColumn(manifest)
        );
    }

    default int insertSelective(SkillInfo row) {
        return MyBatis3Utils.insert(this::insert, row, skillInfo, c ->
            c.withMappedColumnWhenPresent(skillId, row::getSkillId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(revision, row::getRevision)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(digest, row::getDigest)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(manifest, row::getManifest)
        );
    }

    default Optional<SkillInfo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, skillInfo, completer);
    }

    default List<SkillInfo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, skillInfo, completer);
    }

    default List<SkillInfo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, skillInfo, completer);
    }

    default Optional<SkillInfo> selectByPrimaryKey(String skillId_) {
        return selectOne(c ->
            c.where(skillId, isEqualTo(skillId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, skillInfo, completer);
    }

    static UpdateDSL updateAllColumns(SkillInfo row, UpdateDSL dsl) {
        return dsl.set(skillId).equalTo(row::getSkillId)
                .set(userId).equalTo(row::getUserId)
                .set(revision).equalTo(row::getRevision)
                .set(name).equalTo(row::getName)
                .set(digest).equalTo(row::getDigest)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(description).equalTo(row::getDescription)
                .set(manifest).equalTo(row::getManifest);
    }

    static UpdateDSL updateSelectiveColumns(SkillInfo row, UpdateDSL dsl) {
        return dsl.set(skillId).equalToWhenPresent(row::getSkillId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(revision).equalToWhenPresent(row::getRevision)
                .set(name).equalToWhenPresent(row::getName)
                .set(digest).equalToWhenPresent(row::getDigest)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(manifest).equalToWhenPresent(row::getManifest);
    }

    default int updateByPrimaryKey(SkillInfo row) {
        return update(c ->
            c.set(userId).equalTo(row::getUserId)
            .set(revision).equalTo(row::getRevision)
            .set(name).equalTo(row::getName)
            .set(digest).equalTo(row::getDigest)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(description).equalTo(row::getDescription)
            .set(manifest).equalTo(row::getManifest)
            .where(skillId, isEqualTo(row::getSkillId))
        );
    }

    default int updateByPrimaryKeySelective(SkillInfo row) {
        return update(c ->
            c.set(userId).equalToWhenPresent(row::getUserId)
            .set(revision).equalToWhenPresent(row::getRevision)
            .set(name).equalToWhenPresent(row::getName)
            .set(digest).equalToWhenPresent(row::getDigest)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(manifest).equalToWhenPresent(row::getManifest)
            .where(skillId, isEqualTo(row::getSkillId))
        );
    }
}