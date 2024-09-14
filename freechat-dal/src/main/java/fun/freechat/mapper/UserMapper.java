package fun.freechat.mapper;

import fun.freechat.model.User;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static fun.freechat.mapper.UserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface UserMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<User>, CommonUpdateMapper {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    BasicColumn[] selectList = BasicColumn.columnList(userId, gmtCreate, gmtModified, username, password, nickname, givenName, middleName, familyName, preferredUsername, profile, picture, website, email, emailVerified, gender, birthdate, zoneinfo, locale, phoneNumber, phoneNumberVerified, updatedAt, platform, enabled, locked, expiresAt, passwordExpiresAt, address);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UserResult")
    Optional<User> selectOne(SelectStatementProvider selectStatement);

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int deleteByPrimaryKey(String userId_) {
        return delete(c -> 
            c.where(userId, isEqualTo(userId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insert(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.map(userId).toProperty("userId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
            .map(nickname).toProperty("nickname")
            .map(givenName).toProperty("givenName")
            .map(middleName).toProperty("middleName")
            .map(familyName).toProperty("familyName")
            .map(preferredUsername).toProperty("preferredUsername")
            .map(profile).toProperty("profile")
            .map(picture).toProperty("picture")
            .map(website).toProperty("website")
            .map(email).toProperty("email")
            .map(emailVerified).toProperty("emailVerified")
            .map(gender).toProperty("gender")
            .map(birthdate).toProperty("birthdate")
            .map(zoneinfo).toProperty("zoneinfo")
            .map(locale).toProperty("locale")
            .map(phoneNumber).toProperty("phoneNumber")
            .map(phoneNumberVerified).toProperty("phoneNumberVerified")
            .map(updatedAt).toProperty("updatedAt")
            .map(platform).toProperty("platform")
            .map(enabled).toProperty("enabled")
            .map(locked).toProperty("locked")
            .map(expiresAt).toProperty("expiresAt")
            .map(passwordExpiresAt).toProperty("passwordExpiresAt")
            .map(address).toProperty("address")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertMultiple(Collection<User> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, user, c ->
            c.map(userId).toProperty("userId")
            .map(gmtCreate).toProperty("gmtCreate")
            .map(gmtModified).toProperty("gmtModified")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
            .map(nickname).toProperty("nickname")
            .map(givenName).toProperty("givenName")
            .map(middleName).toProperty("middleName")
            .map(familyName).toProperty("familyName")
            .map(preferredUsername).toProperty("preferredUsername")
            .map(profile).toProperty("profile")
            .map(picture).toProperty("picture")
            .map(website).toProperty("website")
            .map(email).toProperty("email")
            .map(emailVerified).toProperty("emailVerified")
            .map(gender).toProperty("gender")
            .map(birthdate).toProperty("birthdate")
            .map(zoneinfo).toProperty("zoneinfo")
            .map(locale).toProperty("locale")
            .map(phoneNumber).toProperty("phoneNumber")
            .map(phoneNumberVerified).toProperty("phoneNumberVerified")
            .map(updatedAt).toProperty("updatedAt")
            .map(platform).toProperty("platform")
            .map(enabled).toProperty("enabled")
            .map(locked).toProperty("locked")
            .map(expiresAt).toProperty("expiresAt")
            .map(passwordExpiresAt).toProperty("passwordExpiresAt")
            .map(address).toProperty("address")
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int insertSelective(User row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(gmtCreate).toPropertyWhenPresent("gmtCreate", row::getGmtCreate)
            .map(gmtModified).toPropertyWhenPresent("gmtModified", row::getGmtModified)
            .map(username).toPropertyWhenPresent("username", row::getUsername)
            .map(password).toPropertyWhenPresent("password", row::getPassword)
            .map(nickname).toPropertyWhenPresent("nickname", row::getNickname)
            .map(givenName).toPropertyWhenPresent("givenName", row::getGivenName)
            .map(middleName).toPropertyWhenPresent("middleName", row::getMiddleName)
            .map(familyName).toPropertyWhenPresent("familyName", row::getFamilyName)
            .map(preferredUsername).toPropertyWhenPresent("preferredUsername", row::getPreferredUsername)
            .map(profile).toPropertyWhenPresent("profile", row::getProfile)
            .map(picture).toPropertyWhenPresent("picture", row::getPicture)
            .map(website).toPropertyWhenPresent("website", row::getWebsite)
            .map(email).toPropertyWhenPresent("email", row::getEmail)
            .map(emailVerified).toPropertyWhenPresent("emailVerified", row::getEmailVerified)
            .map(gender).toPropertyWhenPresent("gender", row::getGender)
            .map(birthdate).toPropertyWhenPresent("birthdate", row::getBirthdate)
            .map(zoneinfo).toPropertyWhenPresent("zoneinfo", row::getZoneinfo)
            .map(locale).toPropertyWhenPresent("locale", row::getLocale)
            .map(phoneNumber).toPropertyWhenPresent("phoneNumber", row::getPhoneNumber)
            .map(phoneNumberVerified).toPropertyWhenPresent("phoneNumberVerified", row::getPhoneNumberVerified)
            .map(updatedAt).toPropertyWhenPresent("updatedAt", row::getUpdatedAt)
            .map(platform).toPropertyWhenPresent("platform", row::getPlatform)
            .map(enabled).toPropertyWhenPresent("enabled", row::getEnabled)
            .map(locked).toPropertyWhenPresent("locked", row::getLocked)
            .map(expiresAt).toPropertyWhenPresent("expiresAt", row::getExpiresAt)
            .map(passwordExpiresAt).toPropertyWhenPresent("passwordExpiresAt", row::getPasswordExpiresAt)
            .map(address).toPropertyWhenPresent("address", row::getAddress)
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<User> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<User> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default List<User> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default Optional<User> selectByPrimaryKey(String userId_) {
        return selectOne(c ->
            c.where(userId, isEqualTo(userId_))
        );
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, user, completer);
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateAllColumns(User row, UpdateDSL<UpdateModel> dsl) {
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(User row, UpdateDSL<UpdateModel> dsl) {
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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