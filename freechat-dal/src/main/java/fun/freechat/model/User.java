package fun.freechat.model;

import jakarta.annotation.Generated;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String username;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String password;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String nickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String givenName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String middleName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String familyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String preferredUsername;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String profile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String picture;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String website;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String email;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte emailVerified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String gender;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date birthdate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String zoneinfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String locale;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String phoneNumber;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte phoneNumberVerified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date updatedAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String platform;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte locked;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date passwordExpiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String address;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserId() {
        return userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withGmtCreate(Date gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtModified() {
        return gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUsername() {
        return username;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withUsername(String username) {
        this.setUsername(username);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPassword() {
        return password;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPassword(String password) {
        this.setPassword(password);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getNickname() {
        return nickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withNickname(String nickname) {
        this.setNickname(nickname);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getGivenName() {
        return givenName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withGivenName(String givenName) {
        this.setGivenName(givenName);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getMiddleName() {
        return middleName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withMiddleName(String middleName) {
        this.setMiddleName(middleName);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getFamilyName() {
        return familyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withFamilyName(String familyName) {
        this.setFamilyName(familyName);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPreferredUsername() {
        return preferredUsername;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPreferredUsername(String preferredUsername) {
        this.setPreferredUsername(preferredUsername);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getProfile() {
        return profile;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withProfile(String profile) {
        this.setProfile(profile);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPicture() {
        return picture;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPicture(String picture) {
        this.setPicture(picture);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getWebsite() {
        return website;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withWebsite(String website) {
        this.setWebsite(website);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setWebsite(String website) {
        this.website = website;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getEmail() {
        return email;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withEmail(String email) {
        this.setEmail(email);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEmailVerified() {
        return emailVerified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withEmailVerified(Byte emailVerified) {
        this.setEmailVerified(emailVerified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEmailVerified(Byte emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getGender() {
        return gender;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withGender(String gender) {
        this.setGender(gender);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getBirthdate() {
        return birthdate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withBirthdate(Date birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getZoneinfo() {
        return zoneinfo;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withZoneinfo(String zoneinfo) {
        this.setZoneinfo(zoneinfo);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getLocale() {
        return locale;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withLocale(String locale) {
        this.setLocale(locale);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPhoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPhoneNumberVerified(Byte phoneNumberVerified) {
        this.setPhoneNumberVerified(phoneNumberVerified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPhoneNumberVerified(Byte phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withUpdatedAt(Date updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPlatform() {
        return platform;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPlatform(String platform) {
        this.setPlatform(platform);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEnabled() {
        return enabled;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getLocked() {
        return locked;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withLocked(Byte locked) {
        this.setLocked(locked);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setLocked(Byte locked) {
        this.locked = locked;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getExpiresAt() {
        return expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withExpiresAt(Date expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getPasswordExpiresAt() {
        return passwordExpiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withPasswordExpiresAt(Date passwordExpiresAt) {
        this.setPasswordExpiresAt(passwordExpiresAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPasswordExpiresAt(Date passwordExpiresAt) {
        this.passwordExpiresAt = passwordExpiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getAddress() {
        return address;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public User withAddress(String address) {
        this.setAddress(address);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getGivenName() == null ? other.getGivenName() == null : this.getGivenName().equals(other.getGivenName()))
            && (this.getMiddleName() == null ? other.getMiddleName() == null : this.getMiddleName().equals(other.getMiddleName()))
            && (this.getFamilyName() == null ? other.getFamilyName() == null : this.getFamilyName().equals(other.getFamilyName()))
            && (this.getPreferredUsername() == null ? other.getPreferredUsername() == null : this.getPreferredUsername().equals(other.getPreferredUsername()))
            && (this.getProfile() == null ? other.getProfile() == null : this.getProfile().equals(other.getProfile()))
            && (this.getPicture() == null ? other.getPicture() == null : this.getPicture().equals(other.getPicture()))
            && (this.getWebsite() == null ? other.getWebsite() == null : this.getWebsite().equals(other.getWebsite()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getEmailVerified() == null ? other.getEmailVerified() == null : this.getEmailVerified().equals(other.getEmailVerified()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getBirthdate() == null ? other.getBirthdate() == null : this.getBirthdate().equals(other.getBirthdate()))
            && (this.getZoneinfo() == null ? other.getZoneinfo() == null : this.getZoneinfo().equals(other.getZoneinfo()))
            && (this.getLocale() == null ? other.getLocale() == null : this.getLocale().equals(other.getLocale()))
            && (this.getPhoneNumber() == null ? other.getPhoneNumber() == null : this.getPhoneNumber().equals(other.getPhoneNumber()))
            && (this.getPhoneNumberVerified() == null ? other.getPhoneNumberVerified() == null : this.getPhoneNumberVerified().equals(other.getPhoneNumberVerified()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()))
            && (this.getPlatform() == null ? other.getPlatform() == null : this.getPlatform().equals(other.getPlatform()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getLocked() == null ? other.getLocked() == null : this.getLocked().equals(other.getLocked()))
            && (this.getExpiresAt() == null ? other.getExpiresAt() == null : this.getExpiresAt().equals(other.getExpiresAt()))
            && (this.getPasswordExpiresAt() == null ? other.getPasswordExpiresAt() == null : this.getPasswordExpiresAt().equals(other.getPasswordExpiresAt()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getGivenName() == null) ? 0 : getGivenName().hashCode());
        result = prime * result + ((getMiddleName() == null) ? 0 : getMiddleName().hashCode());
        result = prime * result + ((getFamilyName() == null) ? 0 : getFamilyName().hashCode());
        result = prime * result + ((getPreferredUsername() == null) ? 0 : getPreferredUsername().hashCode());
        result = prime * result + ((getProfile() == null) ? 0 : getProfile().hashCode());
        result = prime * result + ((getPicture() == null) ? 0 : getPicture().hashCode());
        result = prime * result + ((getWebsite() == null) ? 0 : getWebsite().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getEmailVerified() == null) ? 0 : getEmailVerified().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getBirthdate() == null) ? 0 : getBirthdate().hashCode());
        result = prime * result + ((getZoneinfo() == null) ? 0 : getZoneinfo().hashCode());
        result = prime * result + ((getLocale() == null) ? 0 : getLocale().hashCode());
        result = prime * result + ((getPhoneNumber() == null) ? 0 : getPhoneNumber().hashCode());
        result = prime * result + ((getPhoneNumberVerified() == null) ? 0 : getPhoneNumberVerified().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        result = prime * result + ((getPlatform() == null) ? 0 : getPlatform().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getLocked() == null) ? 0 : getLocked().hashCode());
        result = prime * result + ((getExpiresAt() == null) ? 0 : getExpiresAt().hashCode());
        result = prime * result + ((getPasswordExpiresAt() == null) ? 0 : getPasswordExpiresAt().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", nickname=").append(nickname);
        sb.append(", givenName=").append(givenName);
        sb.append(", middleName=").append(middleName);
        sb.append(", familyName=").append(familyName);
        sb.append(", preferredUsername=").append(preferredUsername);
        sb.append(", profile=").append(profile);
        sb.append(", picture=").append(picture);
        sb.append(", website=").append(website);
        sb.append(", email=").append(email);
        sb.append(", emailVerified=").append(emailVerified);
        sb.append(", gender=").append(gender);
        sb.append(", birthdate=").append(birthdate);
        sb.append(", zoneinfo=").append(zoneinfo);
        sb.append(", locale=").append(locale);
        sb.append(", phoneNumber=").append(phoneNumber);
        sb.append(", phoneNumberVerified=").append(phoneNumberVerified);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", platform=").append(platform);
        sb.append(", enabled=").append(enabled);
        sb.append(", locked=").append(locked);
        sb.append(", expiresAt=").append(expiresAt);
        sb.append(", passwordExpiresAt=").append(passwordExpiresAt);
        sb.append(", address=").append(address);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}