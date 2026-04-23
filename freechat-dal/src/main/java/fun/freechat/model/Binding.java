package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Binding implements Serializable {
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String userId;

    private String platform;

    private String sub;

    private String iss;

    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;

    private String aud;

    private String refreshToken;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public Binding withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public Binding withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public Binding withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public Binding withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public Binding withPlatform(String platform) {
        this.setPlatform(platform);
        return this;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSub() {
        return sub;
    }

    public Binding withSub(String sub) {
        this.setSub(sub);
        return this;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getIss() {
        return iss;
    }

    public Binding withIss(String iss) {
        this.setIss(iss);
        return this;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public Binding withIssuedAt(LocalDateTime issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Binding withExpiresAt(LocalDateTime expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getAud() {
        return aud;
    }

    public Binding withAud(String aud) {
        this.setAud(aud);
        return this;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Binding withRefreshToken(String refreshToken) {
        this.setRefreshToken(refreshToken);
        return this;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
        Binding other = (Binding) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getPlatform() == null ? other.getPlatform() == null : this.getPlatform().equals(other.getPlatform()))
            && (this.getSub() == null ? other.getSub() == null : this.getSub().equals(other.getSub()))
            && (this.getIss() == null ? other.getIss() == null : this.getIss().equals(other.getIss()))
            && (this.getIssuedAt() == null ? other.getIssuedAt() == null : this.getIssuedAt().equals(other.getIssuedAt()))
            && (this.getExpiresAt() == null ? other.getExpiresAt() == null : this.getExpiresAt().equals(other.getExpiresAt()))
            && (this.getAud() == null ? other.getAud() == null : this.getAud().equals(other.getAud()))
            && (this.getRefreshToken() == null ? other.getRefreshToken() == null : this.getRefreshToken().equals(other.getRefreshToken()));
    }

    @Override
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
        result = prime * result + ((getAud() == null) ? 0 : getAud().hashCode());
        result = prime * result + ((getRefreshToken() == null) ? 0 : getRefreshToken().hashCode());
        return result;
    }

    @Override
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