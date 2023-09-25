package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class ApiToken implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String type;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date issuedAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String token;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String policy;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ApiToken withId(Long id) {
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
    public ApiToken withGmtCreate(Date gmtCreate) {
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
    public ApiToken withGmtModified(Date gmtModified) {
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
    public ApiToken withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getType() {
        return type;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ApiToken withType(String type) {
        this.setType(type);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getIssuedAt() {
        return issuedAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ApiToken withIssuedAt(Date issuedAt) {
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
    public ApiToken withExpiresAt(Date expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getToken() {
        return token;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ApiToken withToken(String token) {
        this.setToken(token);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPolicy() {
        return policy;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ApiToken withPolicy(String policy) {
        this.setPolicy(policy);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPolicy(String policy) {
        this.policy = policy == null ? null : policy.trim();
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
        ApiToken other = (ApiToken) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
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
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
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
        sb.append(", type=").append(type);
        sb.append(", issuedAt=").append(issuedAt);
        sb.append(", expiresAt=").append(expiresAt);
        sb.append(", token=").append(token);
        sb.append(", policy=").append(policy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}