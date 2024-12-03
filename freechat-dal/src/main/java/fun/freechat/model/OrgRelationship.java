package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class OrgRelationship implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ownerId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte isVirtual;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public OrgRelationship withId(Long id) {
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
    public OrgRelationship withGmtCreate(Date gmtCreate) {
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
    public OrgRelationship withGmtModified(Date gmtModified) {
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
    public OrgRelationship withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getOwnerId() {
        return ownerId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public OrgRelationship withOwnerId(String ownerId) {
        this.setOwnerId(ownerId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getIsVirtual() {
        return isVirtual;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public OrgRelationship withIsVirtual(Byte isVirtual) {
        this.setIsVirtual(isVirtual);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setIsVirtual(Byte isVirtual) {
        this.isVirtual = isVirtual;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEnabled() {
        return enabled;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public OrgRelationship withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
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
        OrgRelationship other = (OrgRelationship) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getOwnerId() == null ? other.getOwnerId() == null : this.getOwnerId().equals(other.getOwnerId()))
            && (this.getIsVirtual() == null ? other.getIsVirtual() == null : this.getIsVirtual().equals(other.getIsVirtual()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()));
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
        result = prime * result + ((getOwnerId() == null) ? 0 : getOwnerId().hashCode());
        result = prime * result + ((getIsVirtual() == null) ? 0 : getIsVirtual().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
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
        sb.append(", ownerId=").append(ownerId);
        sb.append(", isVirtual=").append(isVirtual);
        sb.append(", enabled=").append(enabled);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}