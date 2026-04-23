package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PromptTask implements Serializable {
    private String taskId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtStart;

    private LocalDateTime gmtEnd;

    private String promptUid;

    private Byte draft;

    private String modelId;

    private String apiKeyName;

    private String cron;

    private String status;

    private String variables;

    private String apiKeyValue;

    private String params;

    private String ext;

    private static final long serialVersionUID = 1L;

    public String getTaskId() {
        return taskId;
    }

    public PromptTask withTaskId(String taskId) {
        this.setTaskId(taskId);
        return this;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public PromptTask withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public PromptTask withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public LocalDateTime getGmtStart() {
        return gmtStart;
    }

    public PromptTask withGmtStart(LocalDateTime gmtStart) {
        this.setGmtStart(gmtStart);
        return this;
    }

    public void setGmtStart(LocalDateTime gmtStart) {
        this.gmtStart = gmtStart;
    }

    public LocalDateTime getGmtEnd() {
        return gmtEnd;
    }

    public PromptTask withGmtEnd(LocalDateTime gmtEnd) {
        this.setGmtEnd(gmtEnd);
        return this;
    }

    public void setGmtEnd(LocalDateTime gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public String getPromptUid() {
        return promptUid;
    }

    public PromptTask withPromptUid(String promptUid) {
        this.setPromptUid(promptUid);
        return this;
    }

    public void setPromptUid(String promptUid) {
        this.promptUid = promptUid;
    }

    public Byte getDraft() {
        return draft;
    }

    public PromptTask withDraft(Byte draft) {
        this.setDraft(draft);
        return this;
    }

    public void setDraft(Byte draft) {
        this.draft = draft;
    }

    public String getModelId() {
        return modelId;
    }

    public PromptTask withModelId(String modelId) {
        this.setModelId(modelId);
        return this;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public PromptTask withApiKeyName(String apiKeyName) {
        this.setApiKeyName(apiKeyName);
        return this;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    public String getCron() {
        return cron;
    }

    public PromptTask withCron(String cron) {
        this.setCron(cron);
        return this;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getStatus() {
        return status;
    }

    public PromptTask withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVariables() {
        return variables;
    }

    public PromptTask withVariables(String variables) {
        this.setVariables(variables);
        return this;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public PromptTask withApiKeyValue(String apiKeyValue) {
        this.setApiKeyValue(apiKeyValue);
        return this;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String getParams() {
        return params;
    }

    public PromptTask withParams(String params) {
        this.setParams(params);
        return this;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getExt() {
        return ext;
    }

    public PromptTask withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    public void setExt(String ext) {
        this.ext = ext;
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
        PromptTask other = (PromptTask) that;
        return (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtStart() == null ? other.getGmtStart() == null : this.getGmtStart().equals(other.getGmtStart()))
            && (this.getGmtEnd() == null ? other.getGmtEnd() == null : this.getGmtEnd().equals(other.getGmtEnd()))
            && (this.getPromptUid() == null ? other.getPromptUid() == null : this.getPromptUid().equals(other.getPromptUid()))
            && (this.getDraft() == null ? other.getDraft() == null : this.getDraft().equals(other.getDraft()))
            && (this.getModelId() == null ? other.getModelId() == null : this.getModelId().equals(other.getModelId()))
            && (this.getApiKeyName() == null ? other.getApiKeyName() == null : this.getApiKeyName().equals(other.getApiKeyName()))
            && (this.getCron() == null ? other.getCron() == null : this.getCron().equals(other.getCron()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getVariables() == null ? other.getVariables() == null : this.getVariables().equals(other.getVariables()))
            && (this.getApiKeyValue() == null ? other.getApiKeyValue() == null : this.getApiKeyValue().equals(other.getApiKeyValue()))
            && (this.getParams() == null ? other.getParams() == null : this.getParams().equals(other.getParams()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getGmtStart() == null) ? 0 : getGmtStart().hashCode());
        result = prime * result + ((getGmtEnd() == null) ? 0 : getGmtEnd().hashCode());
        result = prime * result + ((getPromptUid() == null) ? 0 : getPromptUid().hashCode());
        result = prime * result + ((getDraft() == null) ? 0 : getDraft().hashCode());
        result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
        result = prime * result + ((getApiKeyName() == null) ? 0 : getApiKeyName().hashCode());
        result = prime * result + ((getCron() == null) ? 0 : getCron().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getVariables() == null) ? 0 : getVariables().hashCode());
        result = prime * result + ((getApiKeyValue() == null) ? 0 : getApiKeyValue().hashCode());
        result = prime * result + ((getParams() == null) ? 0 : getParams().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", taskId=").append(taskId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", gmtStart=").append(gmtStart);
        sb.append(", gmtEnd=").append(gmtEnd);
        sb.append(", promptUid=").append(promptUid);
        sb.append(", draft=").append(draft);
        sb.append(", modelId=").append(modelId);
        sb.append(", apiKeyName=").append(apiKeyName);
        sb.append(", cron=").append(cron);
        sb.append(", status=").append(status);
        sb.append(", variables=").append(variables);
        sb.append(", apiKeyValue=").append(apiKeyValue);
        sb.append(", params=").append(params);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}