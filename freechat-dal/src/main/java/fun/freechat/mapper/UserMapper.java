package fun.freechat.mapper;

import static fun.freechat.mapper.UserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import fun.freechat.model.User;
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
public interface UserMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<User>, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(userId, gmtCreate, gmtModified, username, password, nickname, givenName, middleName, familyName, preferredUsername, profile, picture, website, email, emailVerified, gender, birthdate, zoneinfo, locale, phoneNumber, phoneNumberVerified, updatedAt, platform, enabled, locked, expiresAt, passwordExpiresAt, address);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UserResult", value = {
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="gmt_create", property="gmtCreate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmt_modified", property="gmtModified", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
        @Result(column="nickname", property="nickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="given_name", property="givenName", jdbcType=JdbcType.VARCHAR),
        @Result(column="middle_name", property="middleName", jdbcType=JdbcType.VARCHAR),
        @Result(column="family_name", property="familyName", jdbcType=JdbcType.VARCHAR),
        @Result(column="preferred_username", property="preferredUsername", jdbcType=JdbcType.VARCHAR),
        @Result(column="profile", property="profile", jdbcType=JdbcType.VARCHAR),
        @Result(column="picture", property="picture", jdbcType=JdbcType.VARCHAR),
        @Result(column="website", property="website", jdbcType=JdbcType.VARCHAR),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="email_verified", property="emailVerified", jdbcType=JdbcType.TINYINT),
        @Result(column="gender", property="gender", jdbcType=JdbcType.VARCHAR),
        @Result(column="birthdate", property="birthdate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="zoneinfo", property="zoneinfo", jdbcType=JdbcType.VARCHAR),
        @Result(column="locale", property="locale", jdbcType=JdbcType.VARCHAR),
        @Result(column="phone_number", property="phoneNumber", jdbcType=JdbcType.VARCHAR),
        @Result(column="phone_number_verified", property="phoneNumberVerified", jdbcType=JdbcType.TINYINT),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="platform", property="platform", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="locked", property="locked", jdbcType=JdbcType.TINYINT),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="password_expires_at", property="passwordExpiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="address", property="address", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<User> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UserResult")
    Optional<User> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, user, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, user, completer);
    }

    default int deleteByPrimaryKey(String userId_) {
        return delete(c -> 
            c.where(userId, isEqualTo(userId_))
        );
    }

    default int insert(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.withMappedColumn(userId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(username)
            .withMappedColumn(password)
            .withMappedColumn(nickname)
            .withMappedColumn(givenName)
            .withMappedColumn(middleName)
            .withMappedColumn(familyName)
            .withMappedColumn(preferredUsername)
            .withMappedColumn(profile)
            .withMappedColumn(picture)
            .withMappedColumn(website)
            .withMappedColumn(email)
            .withMappedColumn(emailVerified)
            .withMappedColumn(gender)
            .withMappedColumn(birthdate)
            .withMappedColumn(zoneinfo)
            .withMappedColumn(locale)
            .withMappedColumn(phoneNumber)
            .withMappedColumn(phoneNumberVerified)
            .withMappedColumn(updatedAt)
            .withMappedColumn(platform)
            .withMappedColumn(enabled)
            .withMappedColumn(locked)
            .withMappedColumn(expiresAt)
            .withMappedColumn(passwordExpiresAt)
            .withMappedColumn(address)
        );
    }

    default int insertMultiple(Collection<User> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, user, c ->
            c.withMappedColumn(userId)
            .withMappedColumn(gmtCreate)
            .withMappedColumn(gmtModified)
            .withMappedColumn(username)
            .withMappedColumn(password)
            .withMappedColumn(nickname)
            .withMappedColumn(givenName)
            .withMappedColumn(middleName)
            .withMappedColumn(familyName)
            .withMappedColumn(preferredUsername)
            .withMappedColumn(profile)
            .withMappedColumn(picture)
            .withMappedColumn(website)
            .withMappedColumn(email)
            .withMappedColumn(emailVerified)
            .withMappedColumn(gender)
            .withMappedColumn(birthdate)
            .withMappedColumn(zoneinfo)
            .withMappedColumn(locale)
            .withMappedColumn(phoneNumber)
            .withMappedColumn(phoneNumberVerified)
            .withMappedColumn(updatedAt)
            .withMappedColumn(platform)
            .withMappedColumn(enabled)
            .withMappedColumn(locked)
            .withMappedColumn(expiresAt)
            .withMappedColumn(passwordExpiresAt)
            .withMappedColumn(address)
        );
    }

    default int insertSelective(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(gmtCreate, row::getGmtCreate)
            .withMappedColumnWhenPresent(gmtModified, row::getGmtModified)
            .withMappedColumnWhenPresent(username, row::getUsername)
            .withMappedColumnWhenPresent(password, row::getPassword)
            .withMappedColumnWhenPresent(nickname, row::getNickname)
            .withMappedColumnWhenPresent(givenName, row::getGivenName)
            .withMappedColumnWhenPresent(middleName, row::getMiddleName)
            .withMappedColumnWhenPresent(familyName, row::getFamilyName)
            .withMappedColumnWhenPresent(preferredUsername, row::getPreferredUsername)
            .withMappedColumnWhenPresent(profile, row::getProfile)
            .withMappedColumnWhenPresent(picture, row::getPicture)
            .withMappedColumnWhenPresent(website, row::getWebsite)
            .withMappedColumnWhenPresent(email, row::getEmail)
            .withMappedColumnWhenPresent(emailVerified, row::getEmailVerified)
            .withMappedColumnWhenPresent(gender, row::getGender)
            .withMappedColumnWhenPresent(birthdate, row::getBirthdate)
            .withMappedColumnWhenPresent(zoneinfo, row::getZoneinfo)
            .withMappedColumnWhenPresent(locale, row::getLocale)
            .withMappedColumnWhenPresent(phoneNumber, row::getPhoneNumber)
            .withMappedColumnWhenPresent(phoneNumberVerified, row::getPhoneNumberVerified)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
            .withMappedColumnWhenPresent(platform, row::getPlatform)
            .withMappedColumnWhenPresent(enabled, row::getEnabled)
            .withMappedColumnWhenPresent(locked, row::getLocked)
            .withMappedColumnWhenPresent(expiresAt, row::getExpiresAt)
            .withMappedColumnWhenPresent(passwordExpiresAt, row::getPasswordExpiresAt)
            .withMappedColumnWhenPresent(address, row::getAddress)
        );
    }

    default Optional<User> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, user, completer);
    }

    default List<User> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, user, completer);
    }

    default List<User> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, user, completer);
    }

    default Optional<User> selectByPrimaryKey(String userId_) {
        return selectOne(c ->
            c.where(userId, isEqualTo(userId_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, user, completer);
    }

    static UpdateDSL updateAllColumns(User row, UpdateDSL dsl) {
        return dsl.set(userId).equalTo(row::getUserId)
                .set(gmtCreate).equalTo(row::getGmtCreate)
                .set(gmtModified).equalTo(row::getGmtModified)
                .set(username).equalTo(row::getUsername)
                .set(password).equalTo(row::getPassword)
                .set(nickname).equalTo(row::getNickname)
                .set(givenName).equalTo(row::getGivenName)
                .set(middleName).equalTo(row::getMiddleName)
                .set(familyName).equalTo(row::getFamilyName)
                .set(preferredUsername).equalTo(row::getPreferredUsername)
                .set(profile).equalTo(row::getProfile)
                .set(picture).equalTo(row::getPicture)
                .set(website).equalTo(row::getWebsite)
                .set(email).equalTo(row::getEmail)
                .set(emailVerified).equalTo(row::getEmailVerified)
                .set(gender).equalTo(row::getGender)
                .set(birthdate).equalTo(row::getBirthdate)
                .set(zoneinfo).equalTo(row::getZoneinfo)
                .set(locale).equalTo(row::getLocale)
                .set(phoneNumber).equalTo(row::getPhoneNumber)
                .set(phoneNumberVerified).equalTo(row::getPhoneNumberVerified)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .set(platform).equalTo(row::getPlatform)
                .set(enabled).equalTo(row::getEnabled)
                .set(locked).equalTo(row::getLocked)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(passwordExpiresAt).equalTo(row::getPasswordExpiresAt)
                .set(address).equalTo(row::getAddress);
    }

    static UpdateDSL updateSelectiveColumns(User row, UpdateDSL dsl) {
        return dsl.set(userId).equalToWhenPresent(row::getUserId)
                .set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
                .set(gmtModified).equalToWhenPresent(row::getGmtModified)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(password).equalToWhenPresent(row::getPassword)
                .set(nickname).equalToWhenPresent(row::getNickname)
                .set(givenName).equalToWhenPresent(row::getGivenName)
                .set(middleName).equalToWhenPresent(row::getMiddleName)
                .set(familyName).equalToWhenPresent(row::getFamilyName)
                .set(preferredUsername).equalToWhenPresent(row::getPreferredUsername)
                .set(profile).equalToWhenPresent(row::getProfile)
                .set(picture).equalToWhenPresent(row::getPicture)
                .set(website).equalToWhenPresent(row::getWebsite)
                .set(email).equalToWhenPresent(row::getEmail)
                .set(emailVerified).equalToWhenPresent(row::getEmailVerified)
                .set(gender).equalToWhenPresent(row::getGender)
                .set(birthdate).equalToWhenPresent(row::getBirthdate)
                .set(zoneinfo).equalToWhenPresent(row::getZoneinfo)
                .set(locale).equalToWhenPresent(row::getLocale)
                .set(phoneNumber).equalToWhenPresent(row::getPhoneNumber)
                .set(phoneNumberVerified).equalToWhenPresent(row::getPhoneNumberVerified)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
                .set(platform).equalToWhenPresent(row::getPlatform)
                .set(enabled).equalToWhenPresent(row::getEnabled)
                .set(locked).equalToWhenPresent(row::getLocked)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(passwordExpiresAt).equalToWhenPresent(row::getPasswordExpiresAt)
                .set(address).equalToWhenPresent(row::getAddress);
    }

    default int updateByPrimaryKey(User row) {
        return update(c ->
            c.set(gmtCreate).equalTo(row::getGmtCreate)
            .set(gmtModified).equalTo(row::getGmtModified)
            .set(username).equalTo(row::getUsername)
            .set(password).equalTo(row::getPassword)
            .set(nickname).equalTo(row::getNickname)
            .set(givenName).equalTo(row::getGivenName)
            .set(middleName).equalTo(row::getMiddleName)
            .set(familyName).equalTo(row::getFamilyName)
            .set(preferredUsername).equalTo(row::getPreferredUsername)
            .set(profile).equalTo(row::getProfile)
            .set(picture).equalTo(row::getPicture)
            .set(website).equalTo(row::getWebsite)
            .set(email).equalTo(row::getEmail)
            .set(emailVerified).equalTo(row::getEmailVerified)
            .set(gender).equalTo(row::getGender)
            .set(birthdate).equalTo(row::getBirthdate)
            .set(zoneinfo).equalTo(row::getZoneinfo)
            .set(locale).equalTo(row::getLocale)
            .set(phoneNumber).equalTo(row::getPhoneNumber)
            .set(phoneNumberVerified).equalTo(row::getPhoneNumberVerified)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .set(platform).equalTo(row::getPlatform)
            .set(enabled).equalTo(row::getEnabled)
            .set(locked).equalTo(row::getLocked)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .set(passwordExpiresAt).equalTo(row::getPasswordExpiresAt)
            .set(address).equalTo(row::getAddress)
            .where(userId, isEqualTo(row::getUserId))
        );
    }

    default int updateByPrimaryKeySelective(User row) {
        return update(c ->
            c.set(gmtCreate).equalToWhenPresent(row::getGmtCreate)
            .set(gmtModified).equalToWhenPresent(row::getGmtModified)
            .set(username).equalToWhenPresent(row::getUsername)
            .set(password).equalToWhenPresent(row::getPassword)
            .set(nickname).equalToWhenPresent(row::getNickname)
            .set(givenName).equalToWhenPresent(row::getGivenName)
            .set(middleName).equalToWhenPresent(row::getMiddleName)
            .set(familyName).equalToWhenPresent(row::getFamilyName)
            .set(preferredUsername).equalToWhenPresent(row::getPreferredUsername)
            .set(profile).equalToWhenPresent(row::getProfile)
            .set(picture).equalToWhenPresent(row::getPicture)
            .set(website).equalToWhenPresent(row::getWebsite)
            .set(email).equalToWhenPresent(row::getEmail)
            .set(emailVerified).equalToWhenPresent(row::getEmailVerified)
            .set(gender).equalToWhenPresent(row::getGender)
            .set(birthdate).equalToWhenPresent(row::getBirthdate)
            .set(zoneinfo).equalToWhenPresent(row::getZoneinfo)
            .set(locale).equalToWhenPresent(row::getLocale)
            .set(phoneNumber).equalToWhenPresent(row::getPhoneNumber)
            .set(phoneNumberVerified).equalToWhenPresent(row::getPhoneNumberVerified)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .set(platform).equalToWhenPresent(row::getPlatform)
            .set(enabled).equalToWhenPresent(row::getEnabled)
            .set(locked).equalToWhenPresent(row::getLocked)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .set(passwordExpiresAt).equalToWhenPresent(row::getPasswordExpiresAt)
            .set(address).equalToWhenPresent(row::getAddress)
            .where(userId, isEqualTo(row::getUserId))
        );
    }
}