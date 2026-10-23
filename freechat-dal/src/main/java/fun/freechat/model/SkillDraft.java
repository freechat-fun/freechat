package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SkillDraft implements Serializable {
    private String draftId;

    private String skillId;

    private String userId;

    private String revision;

    private String expectedRevision;

    private String name;

    private String digest;

    private String status;

    private LocalDateTime expiresAt;

    private LocalDateTime gcAfter;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String description;

    private String manifest;

    private static final long serialVersionUID = 1L;

    public String getDraftId() {
        return draftId;
    }

    public SkillDraft withDraftId(String draftId) {
        this.setDraftId(draftId);
        return this;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public String getSkillId() {
        return skillId;
    }

    public SkillDraft withSkillId(String skillId) {
        this.setSkillId(skillId);
        return this;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUserId() {
        return userId;
    }

    public SkillDraft withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRevision() {
        return revision;
    }

    public SkillDraft withRevision(String revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getExpectedRevision() {
        return expectedRevision;
    }

    public SkillDraft withExpectedRevision(String expectedRevision) {
        this.setExpectedRevision(expectedRevision);
        return this;
    }

    public void setExpectedRevision(String expectedRevision) {
        this.expectedRevision = expectedRevision;
    }

    public String getName() {
        return name;
    }

    public SkillDraft withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDigest() {
        return digest;
    }

    public SkillDraft withDigest(String digest) {
        this.setDigest(digest);
        return this;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getStatus() {
        return status;
    }

    public SkillDraft withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public SkillDraft withExpiresAt(LocalDateTime expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getGcAfter() {
        return gcAfter;
    }

    public SkillDraft withGcAfter(LocalDateTime gcAfter) {
        this.setGcAfter(gcAfter);
        return this;
    }

    public void setGcAfter(LocalDateTime gcAfter) {
        this.gcAfter = gcAfter;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public SkillDraft withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public SkillDraft withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getDescription() {
        return description;
    }

    public SkillDraft withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManifest() {
        return manifest;
    }

    public SkillDraft withManifest(String manifest) {
        this.setManifest(manifest);
        return this;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
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
        SkillDraft other = (SkillDraft) that;
        return (this.getDraftId() == null ? other.getDraftId() == null : this.getDraftId().equals(other.getDraftId()))
            && (this.getSkillId() == null ? other.getSkillId() == null : this.getSkillId().equals(other.getSkillId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getRevision() == null ? other.getRevision() == null : this.getRevision().equals(other.getRevision()))
            && (this.getExpectedRevision() == null ? other.getExpectedRevision() == null : this.getExpectedRevision().equals(other.getExpectedRevision()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDigest() == null ? other.getDigest() == null : this.getDigest().equals(other.getDigest()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getExpiresAt() == null ? other.getExpiresAt() == null : this.getExpiresAt().equals(other.getExpiresAt()))
            && (this.getGcAfter() == null ? other.getGcAfter() == null : this.getGcAfter().equals(other.getGcAfter()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getManifest() == null ? other.getManifest() == null : this.getManifest().equals(other.getManifest()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDraftId() == null) ? 0 : getDraftId().hashCode());
        result = prime * result + ((getSkillId() == null) ? 0 : getSkillId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
        result = prime * result + ((getExpectedRevision() == null) ? 0 : getExpectedRevision().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDigest() == null) ? 0 : getDigest().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getExpiresAt() == null) ? 0 : getExpiresAt().hashCode());
        result = prime * result + ((getGcAfter() == null) ? 0 : getGcAfter().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getManifest() == null) ? 0 : getManifest().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", draftId=").append(draftId);
        sb.append(", skillId=").append(skillId);
        sb.append(", userId=").append(userId);
        sb.append(", revision=").append(revision);
        sb.append(", expectedRevision=").append(expectedRevision);
        sb.append(", name=").append(name);
        sb.append(", digest=").append(digest);
        sb.append(", status=").append(status);
        sb.append(", expiresAt=").append(expiresAt);
        sb.append(", gcAfter=").append(gcAfter);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", description=").append(description);
        sb.append(", manifest=").append(manifest);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}