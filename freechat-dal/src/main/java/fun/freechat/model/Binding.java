package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class Binding implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String platform;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String sub;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String iss;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date issuedAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String aud;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String refreshToken;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withId(Long id) {
        this.setId(id);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withGmtCreate(Date gmtCreate) {
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
    public Binding withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserId() {
        return userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPlatform() {
        return platform;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withPlatform(String platform) {
        this.setPlatform(platform);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPlatform(String platform) {
        this.platform = platform == null ? null : platform.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getSub() {
        return sub;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withSub(String sub) {
        this.setSub(sub);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setSub(String sub) {
        this.sub = sub == null ? null : sub.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getIss() {
        return iss;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withIss(String iss) {
        this.setIss(iss);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setIss(String iss) {
        this.iss = iss == null ? null : iss.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getIssuedAt() {
        return issuedAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withIssuedAt(Date issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getExpiresAt() {
        return expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withExpiresAt(Date expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getAud() {
        return aud;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withAud(String aud) {
        this.setAud(aud);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAud(String aud) {
        this.aud = aud == null ? null : aud.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getRefreshToken() {
        return refreshToken;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Binding withRefreshToken(String refreshToken) {
        this.setRefreshToken(refreshToken);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken == null ? null : refreshToken.trim();
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
        Binding other = (Binding) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getPlatform() == null ? other.getPlatform() == null : this.getPlatform().equals(other.getPlatform()))
            && (this.getSub() == null ? other.getSub() == null : this.getSub().equals(other.getSub()))
            && (this.getIss() == null ? other.getIss() == null : this.getIss().equals(other.getIss()))
            && (this.getIssuedAt() == null ? other.getIssuedAt() == null : this.getIssuedAt().equals(other.getIssuedAt()))
            && (this.getExpiresAt() == null ? other.getExpiresAt() == null : this.getExpiresAt().equals(other.getExpiresAt()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getPlatform() == null) ? 0 : getPlatform().hashCode());
        result = prime * result + ((getSub() == null) ? 0 : getSub().hashCode());
        result = prime * result + ((getIss() == null) ? 0 : getIss().hashCode());
        result = prime * result + ((getIssuedAt() == null) ? 0 : getIssuedAt().hashCode());
        result = prime * result + ((getExpiresAt() == null) ? 0 : getExpiresAt().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", userId=").append(userId);
        sb.append(", platform=").append(platform);
        sb.append(", sub=").append(sub);
        sb.append(", iss=").append(iss);
        sb.append(", issuedAt=").append(issuedAt);
        sb.append(", expiresAt=").append(expiresAt);
        sb.append(", aud=").append(aud);
        sb.append(", refreshToken=").append(refreshToken);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}