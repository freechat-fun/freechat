package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class PromptTask implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String taskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtExecuted;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String promptUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String modelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String apiKeyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String cron;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String status;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String variables;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String apiKeyValue;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String params;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getTaskId() {
        return taskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withTaskId(String taskId) {
        this.setTaskId(taskId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withGmtCreate(Date gmtCreate) {
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
    public PromptTask withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtExecuted() {
        return gmtExecuted;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withGmtExecuted(Date gmtExecuted) {
        this.setGmtExecuted(gmtExecuted);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtExecuted(Date gmtExecuted) {
        this.gmtExecuted = gmtExecuted;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPromptUid() {
        return promptUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withPromptUid(String promptUid) {
        this.setPromptUid(promptUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPromptUid(String promptUid) {
        this.promptUid = promptUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getDraft() {
        return draft;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withDraft(Byte draft) {
        this.setDraft(draft);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDraft(Byte draft) {
        this.draft = draft;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getModelId() {
        return modelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withModelId(String modelId) {
        this.setModelId(modelId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getApiKeyName() {
        return apiKeyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withApiKeyName(String apiKeyName) {
        this.setApiKeyName(apiKeyName);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getCron() {
        return cron;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withCron(String cron) {
        this.setCron(cron);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCron(String cron) {
        this.cron = cron;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getStatus() {
        return status;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getVariables() {
        return variables;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withVariables(String variables) {
        this.setVariables(variables);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVariables(String variables) {
        this.variables = variables;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getApiKeyValue() {
        return apiKeyValue;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withApiKeyValue(String apiKeyValue) {
        this.setApiKeyValue(apiKeyValue);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getParams() {
        return params;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PromptTask withParams(String params) {
        this.setParams(params);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setParams(String params) {
        this.params = params;
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
        PromptTask other = (PromptTask) that;
        return (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtExecuted() == null ? other.getGmtExecuted() == null : this.getGmtExecuted().equals(other.getGmtExecuted()))
            && (this.getPromptUid() == null ? other.getPromptUid() == null : this.getPromptUid().equals(other.getPromptUid()))
            && (this.getDraft() == null ? other.getDraft() == null : this.getDraft().equals(other.getDraft()))
            && (this.getModelId() == null ? other.getModelId() == null : this.getModelId().equals(other.getModelId()))
            && (this.getApiKeyName() == null ? other.getApiKeyName() == null : this.getApiKeyName().equals(other.getApiKeyName()))
            && (this.getCron() == null ? other.getCron() == null : this.getCron().equals(other.getCron()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getGmtExecuted() == null) ? 0 : getGmtExecuted().hashCode());
        result = prime * result + ((getPromptUid() == null) ? 0 : getPromptUid().hashCode());
        result = prime * result + ((getDraft() == null) ? 0 : getDraft().hashCode());
        result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
        result = prime * result + ((getApiKeyName() == null) ? 0 : getApiKeyName().hashCode());
        result = prime * result + ((getCron() == null) ? 0 : getCron().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", taskId=").append(taskId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", gmtExecuted=").append(gmtExecuted);
        sb.append(", promptUid=").append(promptUid);
        sb.append(", draft=").append(draft);
        sb.append(", modelId=").append(modelId);
        sb.append(", apiKeyName=").append(apiKeyName);
        sb.append(", cron=").append(cron);
        sb.append(", status=").append(status);
        sb.append(", variables=").append(variables);
        sb.append(", apiKeyValue=").append(apiKeyValue);
        sb.append(", params=").append(params);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}