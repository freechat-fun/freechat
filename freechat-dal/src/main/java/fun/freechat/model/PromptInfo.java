package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PromptInfo implements Serializable {
    private Long promptId;

    private String promptUid;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String userId;

    private String parentUid;

    private String visibility;

    private String name;

    private String type;

    private String format;

    private String lang;

    private Integer version;

    private String description;

    private String template;

    private String example;

    private String inputs;

    private String ext;

    private String draft;

    private static final long serialVersionUID = 1L;

    public Long getPromptId() {
        return promptId;
    }

    public PromptInfo withPromptId(Long promptId) {
        this.setPromptId(promptId);
        return this;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public String getPromptUid() {
        return promptUid;
    }

    public PromptInfo withPromptUid(String promptUid) {
        this.setPromptUid(promptUid);
        return this;
    }

    public void setPromptUid(String promptUid) {
        this.promptUid = promptUid;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public PromptInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public PromptInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public PromptInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public PromptInfo withParentUid(String parentUid) {
        this.setParentUid(parentUid);
        return this;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getVisibility() {
        return visibility;
    }

    public PromptInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public PromptInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public PromptInfo withType(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public PromptInfo withFormat(String format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLang() {
        return lang;
    }

    public PromptInfo withLang(String lang) {
        this.setLang(lang);
        return this;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getVersion() {
        return version;
    }

    public PromptInfo withVersion(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public PromptInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public PromptInfo withTemplate(String template) {
        this.setTemplate(template);
        return this;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getExample() {
        return example;
    }

    public PromptInfo withExample(String example) {
        this.setExample(example);
        return this;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getInputs() {
        return inputs;
    }

    public PromptInfo withInputs(String inputs) {
        this.setInputs(inputs);
        return this;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getExt() {
        return ext;
    }

    public PromptInfo withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDraft() {
        return draft;
    }

    public PromptInfo withDraft(String draft) {
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
        PromptInfo other = (PromptInfo) that;
        return (this.getPromptId() == null ? other.getPromptId() == null : this.getPromptId().equals(other.getPromptId()))
            && (this.getPromptUid() == null ? other.getPromptUid() == null : this.getPromptUid().equals(other.getPromptUid()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getParentUid() == null ? other.getParentUid() == null : this.getParentUid().equals(other.getParentUid()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getFormat() == null ? other.getFormat() == null : this.getFormat().equals(other.getFormat()))
            && (this.getLang() == null ? other.getLang() == null : this.getLang().equals(other.getLang()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getTemplate() == null ? other.getTemplate() == null : this.getTemplate().equals(other.getTemplate()))
            && (this.getExample() == null ? other.getExample() == null : this.getExample().equals(other.getExample()))
            && (this.getInputs() == null ? other.getInputs() == null : this.getInputs().equals(other.getInputs()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()))
            && (this.getDraft() == null ? other.getDraft() == null : this.getDraft().equals(other.getDraft()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPromptId() == null) ? 0 : getPromptId().hashCode());
        result = prime * result + ((getPromptUid() == null) ? 0 : getPromptUid().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getParentUid() == null) ? 0 : getParentUid().hashCode());
        result = prime * result + ((getVisibility() == null) ? 0 : getVisibility().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getFormat() == null) ? 0 : getFormat().hashCode());
        result = prime * result + ((getLang() == null) ? 0 : getLang().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getTemplate() == null) ? 0 : getTemplate().hashCode());
        result = prime * result + ((getExample() == null) ? 0 : getExample().hashCode());
        result = prime * result + ((getInputs() == null) ? 0 : getInputs().hashCode());
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
        sb.append(", promptId=").append(promptId);
        sb.append(", promptUid=").append(promptUid);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", userId=").append(userId);
        sb.append(", parentUid=").append(parentUid);
        sb.append(", visibility=").append(visibility);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", format=").append(format);
        sb.append(", lang=").append(lang);
        sb.append(", version=").append(version);
        sb.append(", description=").append(description);
        sb.append(", template=").append(template);
        sb.append(", example=").append(example);
        sb.append(", inputs=").append(inputs);
        sb.append(", ext=").append(ext);
        sb.append(", draft=").append(draft);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}