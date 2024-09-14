package fun.freechat.model;

import jakarta.annotation.Generated;

import java.io.Serializable;
import java.util.Date;

public class AgentInfo implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long agentId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String agentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String parentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String format;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String config;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String example;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String parameters;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getAgentId() {
        return agentId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withAgentId(Long agentId) {
        this.setAgentId(agentId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getAgentUid() {
        return agentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withAgentUid(String agentUid) {
        this.setAgentUid(agentUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAgentUid(String agentUid) {
        this.agentUid = agentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withGmtCreate(Date gmtCreate) {
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
    public AgentInfo withGmtModified(Date gmtModified) {
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
    public AgentInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getParentUid() {
        return parentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withParentUid(String parentUid) {
        this.setParentUid(parentUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getVisibility() {
        return visibility;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getName() {
        return name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withName(String name) {
        this.setName(name);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setName(String name) {
        this.name = name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getFormat() {
        return format;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withFormat(String format) {
        this.setFormat(format);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setFormat(String format) {
        this.format = format;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getVersion() {
        return version;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withVersion(Integer version) {
        this.setVersion(version);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getDescription() {
        return description;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getConfig() {
        return config;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withConfig(String config) {
        this.setConfig(config);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setConfig(String config) {
        this.config = config;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExample() {
        return example;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withExample(String example) {
        this.setExample(example);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExample(String example) {
        this.example = example;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getParameters() {
        return parameters;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withParameters(String parameters) {
        this.setParameters(parameters);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExt() {
        return ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExt(String ext) {
        this.ext = ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getDraft() {
        return draft;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public AgentInfo withDraft(String draft) {
        this.setDraft(draft);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDraft(String draft) {
        this.draft = draft;
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
        AgentInfo other = (AgentInfo) that;
        return (this.getAgentId() == null ? other.getAgentId() == null : this.getAgentId().equals(other.getAgentId()))
            && (this.getAgentUid() == null ? other.getAgentUid() == null : this.getAgentUid().equals(other.getAgentUid()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getParentUid() == null ? other.getParentUid() == null : this.getParentUid().equals(other.getParentUid()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getFormat() == null ? other.getFormat() == null : this.getFormat().equals(other.getFormat()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAgentId() == null) ? 0 : getAgentId().hashCode());
        result = prime * result + ((getAgentUid() == null) ? 0 : getAgentUid().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getParentUid() == null) ? 0 : getParentUid().hashCode());
        result = prime * result + ((getVisibility() == null) ? 0 : getVisibility().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getFormat() == null) ? 0 : getFormat().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", agentId=").append(agentId);
        sb.append(", agentUid=").append(agentUid);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", userId=").append(userId);
        sb.append(", parentUid=").append(parentUid);
        sb.append(", visibility=").append(visibility);
        sb.append(", name=").append(name);
        sb.append(", format=").append(format);
        sb.append(", version=").append(version);
        sb.append(", description=").append(description);
        sb.append(", config=").append(config);
        sb.append(", example=").append(example);
        sb.append(", parameters=").append(parameters);
        sb.append(", ext=").append(ext);
        sb.append(", draft=").append(draft);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}