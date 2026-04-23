package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InteractiveStatsScoreDetails implements Serializable {
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String referType;

    private String referId;

    private String userId;

    private Long score;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public InteractiveStatsScoreDetails withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public InteractiveStatsScoreDetails withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public InteractiveStatsScoreDetails withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getReferType() {
        return referType;
    }

    public InteractiveStatsScoreDetails withReferType(String referType) {
        this.setReferType(referType);
        return this;
    }

    public void setReferType(String referType) {
        this.referType = referType;
    }

    public String getReferId() {
        return referId;
    }

    public InteractiveStatsScoreDetails withReferId(String referId) {
        this.setReferId(referId);
        return this;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public String getUserId() {
        return userId;
    }

    public InteractiveStatsScoreDetails withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getScore() {
        return score;
    }

    public InteractiveStatsScoreDetails withScore(Long score) {
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
        InteractiveStatsScoreDetails other = (InteractiveStatsScoreDetails) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getReferType() == null ? other.getReferType() == null : this.getReferType().equals(other.getReferType()))
            && (this.getReferId() == null ? other.getReferId() == null : this.getReferId().equals(other.getReferId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
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
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}