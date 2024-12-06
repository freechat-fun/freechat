package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class ShortLink implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String token;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String path;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ShortLink withId(Long id) {
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
    public ShortLink withGmtCreate(Date gmtCreate) {
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
    public ShortLink withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getToken() {
        return token;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ShortLink withToken(String token) {
        this.setToken(token);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setToken(String token) {
        this.token = token;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPath() {
        return path;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ShortLink withPath(String path) {
        this.setPath(path);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPath(String path) {
        this.path = path;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getExpiresAt() {
        return expiresAt;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ShortLink withExpiresAt(Date expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
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
        ShortLink other = (ShortLink) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getToken() == null ? other.getToken() == null : this.getToken().equals(other.getToken()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
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
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
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
        sb.append(", token=").append(token);
        sb.append(", path=").append(path);
        sb.append(", expiresAt=").append(expiresAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}