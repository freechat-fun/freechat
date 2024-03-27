package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class InteractiveStats implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String referType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String referId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long viewCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long referCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long recommendCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long scoreCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long score;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withId(Long id) {
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
    public InteractiveStats withGmtCreate(Date gmtCreate) {
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
    public InteractiveStats withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getReferType() {
        return referType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withReferType(String referType) {
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
    public InteractiveStats withReferId(String referId) {
        this.setReferId(referId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setReferId(String referId) {
        this.referId = referId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getViewCount() {
        return viewCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withViewCount(Long viewCount) {
        this.setViewCount(viewCount);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getReferCount() {
        return referCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withReferCount(Long referCount) {
        this.setReferCount(referCount);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setReferCount(Long referCount) {
        this.referCount = referCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getRecommendCount() {
        return recommendCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withRecommendCount(Long recommendCount) {
        this.setRecommendCount(recommendCount);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setRecommendCount(Long recommendCount) {
        this.recommendCount = recommendCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getScoreCount() {
        return scoreCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withScoreCount(Long scoreCount) {
        this.setScoreCount(scoreCount);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setScoreCount(Long scoreCount) {
        this.scoreCount = scoreCount;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getScore() {
        return score;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public InteractiveStats withScore(Long score) {
        this.setScore(score);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setScore(Long score) {
        this.score = score;
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
        InteractiveStats other = (InteractiveStats) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getReferType() == null ? other.getReferType() == null : this.getReferType().equals(other.getReferType()))
            && (this.getReferId() == null ? other.getReferId() == null : this.getReferId().equals(other.getReferId()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getReferCount() == null ? other.getReferCount() == null : this.getReferCount().equals(other.getReferCount()))
            && (this.getRecommendCount() == null ? other.getRecommendCount() == null : this.getRecommendCount().equals(other.getRecommendCount()))
            && (this.getScoreCount() == null ? other.getScoreCount() == null : this.getScoreCount().equals(other.getScoreCount()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getReferType() == null) ? 0 : getReferType().hashCode());
        result = prime * result + ((getReferId() == null) ? 0 : getReferId().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getReferCount() == null) ? 0 : getReferCount().hashCode());
        result = prime * result + ((getRecommendCount() == null) ? 0 : getRecommendCount().hashCode());
        result = prime * result + ((getScoreCount() == null) ? 0 : getScoreCount().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
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
        sb.append(", referType=").append(referType);
        sb.append(", referId=").append(referId);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", referCount=").append(referCount);
        sb.append(", recommendCount=").append(recommendCount);
        sb.append(", scoreCount=").append(scoreCount);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}