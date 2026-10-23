package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SkillInfo implements Serializable {
    private String skillId;

    private String userId;

    private String revision;

    private String name;

    private String digest;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String description;

    private String manifest;

    private static final long serialVersionUID = 1L;

    public String getSkillId() {
        return skillId;
    }

    public SkillInfo withSkillId(String skillId) {
        this.setSkillId(skillId);
        return this;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUserId() {
        return userId;
    }

    public SkillInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRevision() {
        return revision;
    }

    public SkillInfo withRevision(String revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getName() {
        return name;
    }

    public SkillInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDigest() {
        return digest;
    }

    public SkillInfo withDigest(String digest) {
        this.setDigest(digest);
        return this;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public SkillInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public SkillInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getDescription() {
        return description;
    }

    public SkillInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManifest() {
        return manifest;
    }

    public SkillInfo withManifest(String manifest) {
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
        SkillInfo other = (SkillInfo) that;
        return (this.getSkillId() == null ? other.getSkillId() == null : this.getSkillId().equals(other.getSkillId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getRevision() == null ? other.getRevision() == null : this.getRevision().equals(other.getRevision()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDigest() == null ? other.getDigest() == null : this.getDigest().equals(other.getDigest()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getManifest() == null ? other.getManifest() == null : this.getManifest().equals(other.getManifest()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSkillId() == null) ? 0 : getSkillId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDigest() == null) ? 0 : getDigest().hashCode());
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
        sb.append(", skillId=").append(skillId);
        sb.append(", userId=").append(userId);
        sb.append(", revision=").append(revision);
        sb.append(", name=").append(name);
        sb.append(", digest=").append(digest);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", description=").append(description);
        sb.append(", manifest=").append(manifest);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}