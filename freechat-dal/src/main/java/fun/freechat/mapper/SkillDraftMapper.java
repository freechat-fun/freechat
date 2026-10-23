package fun.freechat.mapper;

import static fun.freechat.mapper.SkillDraftDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.SkillDraft;
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
public interface SkillDraftMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<SkillDraft>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(draftId, skillId, userId, revision, expectedRevision, name, digest, status, expiresAt, gcAfter, gmtCreate, gmtModified, description, manifest);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="SkillDraftResult", value = {
        @Result(column="draft_id", property="draftId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="skill_id", property="skillId", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="revision", property="revision", jdbcType=JdbcType.VARCHAR),
        @Result(column="expected_revision", property="expectedRevision", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="digest", property="digest", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.VARCHAR),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gc_after", property="gcAfter", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="description", property="description", jdbcType=JdbcType.LONGVARCHAR),
        @Result(column="manifest", property="manifest", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<SkillDraft> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("SkillDraftResult")
    Optional<SkillDraft> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, skillDraft, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, skillDraft, completer);
    }

    default int deleteByPrimaryKey(String draftId_) {
        return delete(c -> 
            c.where(draftId, isEqualTo(draftId_))
        );
    }

    default int insert(SkillDraft row) {
        return MyBatis3Utils.insert(this::insert, row, skillDraft, c ->
            c.withMappedColumn(draftId)
            .withMappedColumn(skillId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(expectedRevision)
            .withMappedColumn(name)
            .withMappedColumn(digest)
            .withMappedColumn(status)
            .withMappedColumn(expiresAt)
            .withMappedColumn(gcAfter)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(description)
            .withMappedColumn(manifest)
        );
    }

    default int insertMultiple(Collection<SkillDraft> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, skillDraft, c ->
            c.withMappedColumn(draftId)
            .withMappedColumn(skillId)
            .withMappedColumn(userId)
            .withMappedColumn(revision)
            .withMappedColumn(expectedRevision)
            .withMappedColumn(name)
            .withMappedColumn(digest)
            .withMappedColumn(status)
            .withMappedColumn(expiresAt)
            .withMappedColumn(gcAfter)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(description)
            .withMappedColumn(manifest)
        );
    }

    default int insertSelective(SkillDraft row) {
        return MyBatis3Utils.insert(this::insert, row, skillDraft, c ->
            c.withMappedColumnWhenPresent(draftId, row::getDraftId)
            .withMappedColumnWhenPresent(skillId, row::getSkillId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(revision, row::getRevision)
            .withMappedColumnWhenPresent(expectedRevision, row::getExpectedRevision)
            .withMappedColumnWhenPresent(name, row::getName)
            .withMappedColumnWhenPresent(digest, row::getDigest)
            .withMappedColumnWhenPresent(status, row::getStatus)
            .withMappedColumnWhenPresent(expiresAt, row::getExpiresAt)
            .withMappedColumnWhenPresent(gcAfter, row::getGcAfter)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(description, row::getDescription)
            .withMappedColumnWhenPresent(manifest, row::getManifest)
        );
    }

    default Optional<SkillDraft> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, skillDraft, completer);
    }

    default List<SkillDraft> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, skillDraft, completer);
    }

    default List<SkillDraft> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, skillDraft, completer);
    }

    default Optional<SkillDraft> selectByPrimaryKey(String draftId_) {
        return selectOne(c ->
            c.where(draftId, isEqualTo(draftId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, skillDraft, completer);
    }

    static UpdateDSL updateAllColumns(SkillDraft row, UpdateDSL dsl) {
        return dsl.set(draftId).equalTo(row::getDraftId)
                .set(skillId).equalTo(row::getSkillId)
                .set(userId).equalTo(row::getUserId)
                .set(revision).equalTo(row::getRevision)
                .set(expectedRevision).equalTo(row::getExpectedRevision)
                .set(name).equalTo(row::getName)
                .set(digest).equalTo(row::getDigest)
                .set(status).equalTo(row::getStatus)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(gcAfter).equalTo(row::getGcAfter)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(description).equalTo(row::getDescription)
                .set(manifest).equalTo(row::getManifest);
    }

    static UpdateDSL updateSelectiveColumns(SkillDraft row, UpdateDSL dsl) {
        return dsl.set(draftId).equalToWhenPresent(row::getDraftId)
                .set(skillId).equalToWhenPresent(row::getSkillId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(revision).equalToWhenPresent(row::getRevision)
                .set(expectedRevision).equalToWhenPresent(row::getExpectedRevision)
                .set(name).equalToWhenPresent(row::getName)
                .set(digest).equalToWhenPresent(row::getDigest)
                .set(status).equalToWhenPresent(row::getStatus)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(gcAfter).equalToWhenPresent(row::getGcAfter)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(manifest).equalToWhenPresent(row::getManifest);
    }

    default int updateByPrimaryKey(SkillDraft row) {
        return update(c ->
            c.set(skillId).equalTo(row::getSkillId)
            .set(userId).equalTo(row::getUserId)
            .set(revision).equalTo(row::getRevision)
            .set(expectedRevision).equalTo(row::getExpectedRevision)
            .set(name).equalTo(row::getName)
            .set(digest).equalTo(row::getDigest)
            .set(status).equalTo(row::getStatus)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .set(gcAfter).equalTo(row::getGcAfter)
            .set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(description).equalTo(row::getDescription)
            .set(manifest).equalTo(row::getManifest)
            .where(draftId, isEqualTo(row::getDraftId))
        );
    }

    default int updateByPrimaryKeySelective(SkillDraft row) {
        return update(c ->
            c.set(skillId).equalToWhenPresent(row::getSkillId)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(revision).equalToWhenPresent(row::getRevision)
            .set(expectedRevision).equalToWhenPresent(row::getExpectedRevision)
            .set(name).equalToWhenPresent(row::getName)
            .set(digest).equalToWhenPresent(row::getDigest)
            .set(status).equalToWhenPresent(row::getStatus)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .set(gcAfter).equalToWhenPresent(row::getGcAfter)
            .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(manifest).equalToWhenPresent(row::getManifest)
            .where(draftId, isEqualTo(row::getDraftId))
        );
    }
}