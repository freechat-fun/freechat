package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class AiApiKey implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtUsed;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String provider;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String token;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withId(Long id) {
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
    public AiApiKey withGmtCreate(Date gmtCreate) {
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
    public AiApiKey withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtUsed() {
        return gmtUsed;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withGmtUsed(Date gmtUsed) {
        this.setGmtUsed(gmtUsed);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtUsed(Date gmtUsed) {
        this.gmtUsed = gmtUsed;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getName() {
        return name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withName(String name) {
        this.setName(name);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getProvider() {
        return provider;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withProvider(String provider) {
        this.setProvider(provider);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setProvider(String provider) {
        this.provider = provider == null ? null : provider.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEnabled() {
        return enabled;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserId() {
        return userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getToken() {
        return token;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiApiKey withToken(String token) {
        this.setToken(token);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
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
        AiApiKey other = (AiApiKey) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtUsed() == null ? other.getGmtUsed() == null : this.getGmtUsed().equals(other.getGmtUsed()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getProvider() == null ? other.getProvider() == null : this.getProvider().equals(other.getProvider()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getToken() == null ? other.getToken() == null : this.getToken().equals(other.getToken()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getGmtUsed() == null) ? 0 : getGmtUsed().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getProvider() == null) ? 0 : getProvider().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
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
        sb.append(", gmtUsed=").append(gmtUsed);
        sb.append(", name=").append(name);
        sb.append(", provider=").append(provider);
        sb.append(", enabled=").append(enabled);
        sb.append(", userId=").append(userId);
        sb.append(", token=").append(token);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}