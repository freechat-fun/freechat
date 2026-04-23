package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InteractiveStats implements Serializable {
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String referType;

    private String referId;

    private Long viewCount;

    private Long referCount;

    private Long recommendCount;

    private Long scoreCount;

    private Long score;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public InteractiveStats withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public InteractiveStats withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public InteractiveStats withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getReferType() {
        return referType;
    }

    public InteractiveStats withReferType(String referType) {
        this.setReferType(referType);
        return this;
    }

    public void setReferType(String referType) {
        this.referType = referType;
    }

    public String getReferId() {
        return referId;
    }

    public InteractiveStats withReferId(String referId) {
        this.setReferId(referId);
        return this;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public InteractiveStats withViewCount(Long viewCount) {
        this.setViewCount(viewCount);
        return this;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getReferCount() {
        return referCount;
    }

    public InteractiveStats withReferCount(Long referCount) {
        this.setReferCount(referCount);
        return this;
    }

    public void setReferCount(Long referCount) {
        this.referCount = referCount;
    }

    public Long getRecommendCount() {
        return recommendCount;
    }

    public InteractiveStats withRecommendCount(Long recommendCount) {
        this.setRecommendCount(recommendCount);
        return this;
    }

    public void setRecommendCount(Long recommendCount) {
        this.recommendCount = recommendCount;
    }

    public Long getScoreCount() {
        return scoreCount;
    }

    public InteractiveStats withScoreCount(Long scoreCount) {
        this.setScoreCount(scoreCount);
        return this;
    }

    public void setScoreCount(Long scoreCount) {
        this.scoreCount = scoreCount;
    }

    public Long getScore() {
        return score;
    }

    public InteractiveStats withScore(Long score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Long score) {
        this.score = score;
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