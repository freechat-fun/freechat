package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private String userId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String username;

    private String password;

    private String nickname;

    private String givenName;

    private String middleName;

    private String familyName;

    private String preferredUsername;

    private String profile;

    private String picture;

    private String website;

    private String email;

    private Byte emailVerified;

    private String gender;

    private LocalDateTime birthdate;

    private String zoneinfo;

    private String locale;

    private String phoneNumber;

    private Byte phoneNumberVerified;

    private LocalDateTime updatedAt;

    private String platform;

    private Byte enabled;

    private Byte locked;

    private LocalDateTime expiresAt;

    private LocalDateTime passwordExpiresAt;

    private String address;

    private static final long serialVersionUID = 1L;

    public String getUserId() {
        return userId;
    }

    public User withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public User withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public User withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUsername() {
        return username;
    }

    public User withUsername(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public User withPassword(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public User withNickname(String nickname) {
        this.setNickname(nickname);
        return this;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGivenName() {
        return givenName;
    }

    public User withGivenName(String givenName) {
        this.setGivenName(givenName);
        return this;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public User withMiddleName(String middleName) {
        this.setMiddleName(middleName);
        return this;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public User withFamilyName(String familyName) {
        this.setFamilyName(familyName);
        return this;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public User withPreferredUsername(String preferredUsername) {
        this.setPreferredUsername(preferredUsername);
        return this;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getProfile() {
        return profile;
    }

    public User withProfile(String profile) {
        this.setProfile(profile);
        return this;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPicture() {
        return picture;
    }

    public User withPicture(String picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWebsite() {
        return website;
    }

    public User withWebsite(String website) {
        this.setWebsite(website);
        return this;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public User withEmail(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Byte getEmailVerified() {
        return emailVerified;
    }

    public User withEmailVerified(Byte emailVerified) {
        this.setEmailVerified(emailVerified);
        return this;
    }

    public void setEmailVerified(Byte emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getGender() {
        return gender;
    }

    public User withGender(String gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getBirthdate() {
        return birthdate;
    }

    public User withBirthdate(LocalDateTime birthdate) {
        this.setBirthdate(birthdate);
        return this;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public User withZoneinfo(String zoneinfo) {
        this.setZoneinfo(zoneinfo);
        return this;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getLocale() {
        return locale;
    }

    public User withLocale(String locale) {
        this.setLocale(locale);
        return this;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User withPhoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Byte getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public User withPhoneNumberVerified(Byte phoneNumberVerified) {
        this.setPhoneNumberVerified(phoneNumberVerified);
        return this;
    }

    public void setPhoneNumberVerified(Byte phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User withUpdatedAt(LocalDateTime updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPlatform() {
        return platform;
    }

    public User withPlatform(String platform) {
        this.setPlatform(platform);
        return this;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public User withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public Byte getLocked() {
        return locked;
    }

    public User withLocked(Byte locked) {
        this.setLocked(locked);
        return this;
    }

    public void setLocked(Byte locked) {
        this.locked = locked;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public User withExpiresAt(LocalDateTime expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getPasswordExpiresAt() {
        return passwordExpiresAt;
    }

    public User withPasswordExpiresAt(LocalDateTime passwordExpiresAt) {
        this.setPasswordExpiresAt(passwordExpiresAt);
        return this;
    }

    public void setPasswordExpiresAt(LocalDateTime passwordExpiresAt) {
        this.passwordExpiresAt = passwordExpiresAt;
    }

    public String getAddress() {
        return address;
    }

    public User withAddress(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
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