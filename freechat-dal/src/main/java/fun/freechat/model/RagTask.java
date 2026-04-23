package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RagTask implements Serializable {
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtStart;

    private LocalDateTime gmtEnd;

    private String characterUid;

    private String sourceType;

    private Integer maxSegmentSize;

    private Integer maxOverlapSize;

    private String status;

    private String source;

    private String ext;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public RagTask withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public RagTask withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public RagTask withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public LocalDateTime getGmtStart() {
        return gmtStart;
    }

    public RagTask withGmtStart(LocalDateTime gmtStart) {
        this.setGmtStart(gmtStart);
        return this;
    }

    public void setGmtStart(LocalDateTime gmtStart) {
        this.gmtStart = gmtStart;
    }

    public LocalDateTime getGmtEnd() {
        return gmtEnd;
    }

    public RagTask withGmtEnd(LocalDateTime gmtEnd) {
        this.setGmtEnd(gmtEnd);
        return this;
    }

    public void setGmtEnd(LocalDateTime gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public String getCharacterUid() {
        return characterUid;
    }

    public RagTask withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    public String getSourceType() {
        return sourceType;
    }

    public RagTask withSourceType(String sourceType) {
        this.setSourceType(sourceType);
        return this;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getMaxSegmentSize() {
        return maxSegmentSize;
    }

    public RagTask withMaxSegmentSize(Integer maxSegmentSize) {
        this.setMaxSegmentSize(maxSegmentSize);
        return this;
    }

    public void setMaxSegmentSize(Integer maxSegmentSize) {
        this.maxSegmentSize = maxSegmentSize;
    }

    public Integer getMaxOverlapSize() {
        return maxOverlapSize;
    }

    public RagTask withMaxOverlapSize(Integer maxOverlapSize) {
        this.setMaxOverlapSize(maxOverlapSize);
        return this;
    }

    public void setMaxOverlapSize(Integer maxOverlapSize) {
        this.maxOverlapSize = maxOverlapSize;
    }

    public String getStatus() {
        return status;
    }

    public RagTask withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public RagTask withSource(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExt() {
        return ext;
    }

    public RagTask withExt(String ext) {
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
        RagTask other = (RagTask) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtStart() == null ? other.getGmtStart() == null : this.getGmtStart().equals(other.getGmtStart()))
            && (this.getGmtEnd() == null ? other.getGmtEnd() == null : this.getGmtEnd().equals(other.getGmtEnd()))
            && (this.getCharacterUid() == null ? other.getCharacterUid() == null : this.getCharacterUid().equals(other.getCharacterUid()))
            && (this.getSourceType() == null ? other.getSourceType() == null : this.getSourceType().equals(other.getSourceType()))
            && (this.getMaxSegmentSize() == null ? other.getMaxSegmentSize() == null : this.getMaxSegmentSize().equals(other.getMaxSegmentSize()))
            && (this.getMaxOverlapSize() == null ? other.getMaxOverlapSize() == null : this.getMaxOverlapSize().equals(other.getMaxOverlapSize()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSource() == null ? other.getSource() == null : this.getSource().equals(other.getSource()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getGmtStart() == null) ? 0 : getGmtStart().hashCode());
        result = prime * result + ((getGmtEnd() == null) ? 0 : getGmtEnd().hashCode());
        result = prime * result + ((getCharacterUid() == null) ? 0 : getCharacterUid().hashCode());
        result = prime * result + ((getSourceType() == null) ? 0 : getSourceType().hashCode());
        result = prime * result + ((getMaxSegmentSize() == null) ? 0 : getMaxSegmentSize().hashCode());
        result = prime * result + ((getMaxOverlapSize() == null) ? 0 : getMaxOverlapSize().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", gmtStart=").append(gmtStart);
        sb.append(", gmtEnd=").append(gmtEnd);
        sb.append(", characterUid=").append(characterUid);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", maxSegmentSize=").append(maxSegmentSize);
        sb.append(", maxOverlapSize=").append(maxOverlapSize);
        sb.append(", status=").append(status);
        sb.append(", source=").append(source);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}