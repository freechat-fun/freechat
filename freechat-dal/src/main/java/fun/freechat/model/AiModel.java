package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class AiModel implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String modelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String referType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String referId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiModel withId(Long id) {
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
    public AiModel withGmtCreate(Date gmtCreate) {
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
    public AiModel withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getModelId() {
        return modelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiModel withModelId(String modelId) {
        this.setModelId(modelId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getReferType() {
        return referType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiModel withReferType(String referType) {
        this.setReferType(referType);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setReferType(String referType) {
        this.referType = referType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getReferId() {
        return referId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AiModel withReferId(String referId) {
        this.setReferId(referId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setReferId(String referId) {
        this.referId = referId;
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
        AiModel other = (AiModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getModelId() == null ? other.getModelId() == null : this.getModelId().equals(other.getModelId()))
            && (this.getReferType() == null ? other.getReferType() == null : this.getReferType().equals(other.getReferType()))
            && (this.getReferId() == null ? other.getReferId() == null : this.getReferId().equals(other.getReferId()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
        result = prime * result + ((getReferType() == null) ? 0 : getReferType().hashCode());
        result = prime * result + ((getReferId() == null) ? 0 : getReferId().hashCode());
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
        sb.append(", modelId=").append(modelId);
        sb.append(", referType=").append(referType);
        sb.append(", referId=").append(referId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}