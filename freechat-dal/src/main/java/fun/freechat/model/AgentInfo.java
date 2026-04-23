package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AgentInfo implements Serializable {
    private Long agentId;

    private String agentUid;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String userId;

    private String parentUid;

    private String visibility;

    private String name;

    private String format;

    private Integer version;

    private String description;

    private String config;

    private String example;

    private String parameters;

    private String ext;

    private String draft;

    private static final long serialVersionUID = 1L;

    public Long getAgentId() {
        return agentId;
    }

    public AgentInfo withAgentId(Long agentId) {
        this.setAgentId(agentId);
        return this;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentUid() {
        return agentUid;
    }

    public AgentInfo withAgentUid(String agentUid) {
        this.setAgentUid(agentUid);
        return this;
    }

    public void setAgentUid(String agentUid) {
        this.agentUid = agentUid;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public AgentInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public AgentInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public AgentInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public AgentInfo withParentUid(String parentUid) {
        this.setParentUid(parentUid);
        return this;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getVisibility() {
        return visibility;
    }

    public AgentInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public AgentInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public AgentInfo withFormat(String format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getVersion() {
        return version;
    }

    public AgentInfo withVersion(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public AgentInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfig() {
        return config;
    }

    public AgentInfo withConfig(String config) {
        this.setConfig(config);
        return this;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getExample() {
        return example;
    }

    public AgentInfo withExample(String example) {
        this.setExample(example);
        return this;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getParameters() {
        return parameters;
    }

    public AgentInfo withParameters(String parameters) {
        this.setParameters(parameters);
        return this;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getExt() {
        return ext;
    }

    public AgentInfo withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDraft() {
        return draft;
    }

    public AgentInfo withDraft(String draft) {
        this.setDraft(draft);
        return this;
    }

    public void setDraft(String draft) {
        this.draft = draft;
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
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getConfig() == null ? other.getConfig() == null : this.getConfig().equals(other.getConfig()))
            && (this.getExample() == null ? other.getExample() == null : this.getExample().equals(other.getExample()))
            && (this.getParameters() == null ? other.getParameters() == null : this.getParameters().equals(other.getParameters()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()))
            && (this.getDraft() == null ? other.getDraft() == null : this.getDraft().equals(other.getDraft()));
    }

    @Override
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
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getConfig() == null) ? 0 : getConfig().hashCode());
        result = prime * result + ((getExample() == null) ? 0 : getExample().hashCode());
        result = prime * result + ((getParameters() == null) ? 0 : getParameters().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        result = prime * result + ((getDraft() == null) ? 0 : getDraft().hashCode());
        return result;
    }

    @Override
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