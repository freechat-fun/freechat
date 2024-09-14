package fun.freechat.model;

import jakarta.annotation.Generated;

import java.io.Serializable;
import java.util.Date;

public class RagTask implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtStart;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtEnd;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String characterUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String sourceType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer maxSegmentSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer maxOverlapSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String status;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String source;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withId(Long id) {
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
    public RagTask withGmtCreate(Date gmtCreate) {
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
    public RagTask withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtStart() {
        return gmtStart;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withGmtStart(Date gmtStart) {
        this.setGmtStart(gmtStart);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtStart(Date gmtStart) {
        this.gmtStart = gmtStart;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtEnd() {
        return gmtEnd;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withGmtEnd(Date gmtEnd) {
        this.setGmtEnd(gmtEnd);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtEnd(Date gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getCharacterUid() {
        return characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getSourceType() {
        return sourceType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withSourceType(String sourceType) {
        this.setSourceType(sourceType);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getMaxSegmentSize() {
        return maxSegmentSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withMaxSegmentSize(Integer maxSegmentSize) {
        this.setMaxSegmentSize(maxSegmentSize);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setMaxSegmentSize(Integer maxSegmentSize) {
        this.maxSegmentSize = maxSegmentSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getMaxOverlapSize() {
        return maxOverlapSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withMaxOverlapSize(Integer maxOverlapSize) {
        this.setMaxOverlapSize(maxOverlapSize);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setMaxOverlapSize(Integer maxOverlapSize) {
        this.maxOverlapSize = maxOverlapSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getStatus() {
        return status;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getSource() {
        return source;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withSource(String source) {
        this.setSource(source);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setSource(String source) {
        this.source = source;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExt() {
        return ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public RagTask withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExt(String ext) {
        this.ext = ext;
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
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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