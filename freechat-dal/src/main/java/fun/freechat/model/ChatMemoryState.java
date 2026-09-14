package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMemoryState implements Serializable {
    private String chatId;

    private String userId;

    private String characterUid;

    private String storeType;

    private Long generation;

    private Long version;

    private String status;

    private String fingerprint;

    private LocalDateTime lastActivity;

    private Long latestFinalizedId;

    private Long episode;

    private Long overflowThroughId;

    private Long idleThroughId;

    private String summaryId;

    private String profileId;

    private Byte profileRevalidationPending;

    private String turnToken;

    private LocalDateTime turnLeaseUntil;

    private LocalDateTime turnDeadline;

    private Long turnRevision;

    private LocalDateTime dueAt;

    private LocalDateTime retryAt;

    private Integer retryAttempts;

    private String claimToken;

    private LocalDateTime claimLeaseUntil;

    private LocalDateTime claimDeadline;

    private Long reconciledThroughId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private static final long serialVersionUID = 1L;

    public String getChatId() {
        return chatId;
    }

    public ChatMemoryState withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public ChatMemoryState withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCharacterUid() {
        return characterUid;
    }

    public ChatMemoryState withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    public String getStoreType() {
        return storeType;
    }

    public ChatMemoryState withStoreType(String storeType) {
        this.setStoreType(storeType);
        return this;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public Long getGeneration() {
        return generation;
    }

    public ChatMemoryState withGeneration(Long generation) {
        this.setGeneration(generation);
        return this;
    }

    public void setGeneration(Long generation) {
        this.generation = generation;
    }

    public Long getVersion() {
        return version;
    }

    public ChatMemoryState withVersion(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public ChatMemoryState withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public ChatMemoryState withFingerprint(String fingerprint) {
        this.setFingerprint(fingerprint);
        return this;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public ChatMemoryState withLastActivity(LocalDateTime lastActivity) {
        this.setLastActivity(lastActivity);
        return this;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Long getLatestFinalizedId() {
        return latestFinalizedId;
    }

    public ChatMemoryState withLatestFinalizedId(Long latestFinalizedId) {
        this.setLatestFinalizedId(latestFinalizedId);
        return this;
    }

    public void setLatestFinalizedId(Long latestFinalizedId) {
        this.latestFinalizedId = latestFinalizedId;
    }

    public Long getEpisode() {
        return episode;
    }

    public ChatMemoryState withEpisode(Long episode) {
        this.setEpisode(episode);
        return this;
    }

    public void setEpisode(Long episode) {
        this.episode = episode;
    }

    public Long getOverflowThroughId() {
        return overflowThroughId;
    }

    public ChatMemoryState withOverflowThroughId(Long overflowThroughId) {
        this.setOverflowThroughId(overflowThroughId);
        return this;
    }

    public void setOverflowThroughId(Long overflowThroughId) {
        this.overflowThroughId = overflowThroughId;
    }

    public Long getIdleThroughId() {
        return idleThroughId;
    }

    public ChatMemoryState withIdleThroughId(Long idleThroughId) {
        this.setIdleThroughId(idleThroughId);
        return this;
    }

    public void setIdleThroughId(Long idleThroughId) {
        this.idleThroughId = idleThroughId;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public ChatMemoryState withSummaryId(String summaryId) {
        this.setSummaryId(summaryId);
        return this;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
    }

    public String getProfileId() {
        return profileId;
    }

    public ChatMemoryState withProfileId(String profileId) {
        this.setProfileId(profileId);
        return this;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Byte getProfileRevalidationPending() {
        return profileRevalidationPending;
    }

    public ChatMemoryState withProfileRevalidationPending(Byte profileRevalidationPending) {
        this.setProfileRevalidationPending(profileRevalidationPending);
        return this;
    }

    public void setProfileRevalidationPending(Byte profileRevalidationPending) {
        this.profileRevalidationPending = profileRevalidationPending;
    }

    public String getTurnToken() {
        return turnToken;
    }

    public ChatMemoryState withTurnToken(String turnToken) {
        this.setTurnToken(turnToken);
        return this;
    }

    public void setTurnToken(String turnToken) {
        this.turnToken = turnToken;
    }

    public LocalDateTime getTurnLeaseUntil() {
        return turnLeaseUntil;
    }

    public ChatMemoryState withTurnLeaseUntil(LocalDateTime turnLeaseUntil) {
        this.setTurnLeaseUntil(turnLeaseUntil);
        return this;
    }

    public void setTurnLeaseUntil(LocalDateTime turnLeaseUntil) {
        this.turnLeaseUntil = turnLeaseUntil;
    }

    public LocalDateTime getTurnDeadline() {
        return turnDeadline;
    }

    public ChatMemoryState withTurnDeadline(LocalDateTime turnDeadline) {
        this.setTurnDeadline(turnDeadline);
        return this;
    }

    public void setTurnDeadline(LocalDateTime turnDeadline) {
        this.turnDeadline = turnDeadline;
    }

    public Long getTurnRevision() {
        return turnRevision;
    }

    public ChatMemoryState withTurnRevision(Long turnRevision) {
        this.setTurnRevision(turnRevision);
        return this;
    }

    public void setTurnRevision(Long turnRevision) {
        this.turnRevision = turnRevision;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public ChatMemoryState withDueAt(LocalDateTime dueAt) {
        this.setDueAt(dueAt);
        return this;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getRetryAt() {
        return retryAt;
    }

    public ChatMemoryState withRetryAt(LocalDateTime retryAt) {
        this.setRetryAt(retryAt);
        return this;
    }

    public void setRetryAt(LocalDateTime retryAt) {
        this.retryAt = retryAt;
    }

    public Integer getRetryAttempts() {
        return retryAttempts;
    }

    public ChatMemoryState withRetryAttempts(Integer retryAttempts) {
        this.setRetryAttempts(retryAttempts);
        return this;
    }

    public void setRetryAttempts(Integer retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public String getClaimToken() {
        return claimToken;
    }

    public ChatMemoryState withClaimToken(String claimToken) {
        this.setClaimToken(claimToken);
        return this;
    }

    public void setClaimToken(String claimToken) {
        this.claimToken = claimToken;
    }

    public LocalDateTime getClaimLeaseUntil() {
        return claimLeaseUntil;
    }

    public ChatMemoryState withClaimLeaseUntil(LocalDateTime claimLeaseUntil) {
        this.setClaimLeaseUntil(claimLeaseUntil);
        return this;
    }

    public void setClaimLeaseUntil(LocalDateTime claimLeaseUntil) {
        this.claimLeaseUntil = claimLeaseUntil;
    }

    public LocalDateTime getClaimDeadline() {
        return claimDeadline;
    }

    public ChatMemoryState withClaimDeadline(LocalDateTime claimDeadline) {
        this.setClaimDeadline(claimDeadline);
        return this;
    }

    public void setClaimDeadline(LocalDateTime claimDeadline) {
        this.claimDeadline = claimDeadline;
    }

    public Long getReconciledThroughId() {
        return reconciledThroughId;
    }

    public ChatMemoryState withReconciledThroughId(Long reconciledThroughId) {
        this.setReconciledThroughId(reconciledThroughId);
        return this;
    }

    public void setReconciledThroughId(Long reconciledThroughId) {
        this.reconciledThroughId = reconciledThroughId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public ChatMemoryState withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public ChatMemoryState withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
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
        ChatMemoryState other = (ChatMemoryState) that;
        return (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCharacterUid() == null ? other.getCharacterUid() == null : this.getCharacterUid().equals(other.getCharacterUid()))
            && (this.getStoreType() == null ? other.getStoreType() == null : this.getStoreType().equals(other.getStoreType()))
            && (this.getGeneration() == null ? other.getGeneration() == null : this.getGeneration().equals(other.getGeneration()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getFingerprint() == null ? other.getFingerprint() == null : this.getFingerprint().equals(other.getFingerprint()))
            && (this.getLastActivity() == null ? other.getLastActivity() == null : this.getLastActivity().equals(other.getLastActivity()))
            && (this.getLatestFinalizedId() == null ? other.getLatestFinalizedId() == null : this.getLatestFinalizedId().equals(other.getLatestFinalizedId()))
            && (this.getEpisode() == null ? other.getEpisode() == null : this.getEpisode().equals(other.getEpisode()))
            && (this.getOverflowThroughId() == null ? other.getOverflowThroughId() == null : this.getOverflowThroughId().equals(other.getOverflowThroughId()))
            && (this.getIdleThroughId() == null ? other.getIdleThroughId() == null : this.getIdleThroughId().equals(other.getIdleThroughId()))
            && (this.getSummaryId() == null ? other.getSummaryId() == null : this.getSummaryId().equals(other.getSummaryId()))
            && (this.getProfileId() == null ? other.getProfileId() == null : this.getProfileId().equals(other.getProfileId()))
            && (this.getProfileRevalidationPending() == null ? other.getProfileRevalidationPending() == null : this.getProfileRevalidationPending().equals(other.getProfileRevalidationPending()))
            && (this.getTurnToken() == null ? other.getTurnToken() == null : this.getTurnToken().equals(other.getTurnToken()))
            && (this.getTurnLeaseUntil() == null ? other.getTurnLeaseUntil() == null : this.getTurnLeaseUntil().equals(other.getTurnLeaseUntil()))
            && (this.getTurnDeadline() == null ? other.getTurnDeadline() == null : this.getTurnDeadline().equals(other.getTurnDeadline()))
            && (this.getTurnRevision() == null ? other.getTurnRevision() == null : this.getTurnRevision().equals(other.getTurnRevision()))
            && (this.getDueAt() == null ? other.getDueAt() == null : this.getDueAt().equals(other.getDueAt()))
            && (this.getRetryAt() == null ? other.getRetryAt() == null : this.getRetryAt().equals(other.getRetryAt()))
            && (this.getRetryAttempts() == null ? other.getRetryAttempts() == null : this.getRetryAttempts().equals(other.getRetryAttempts()))
            && (this.getClaimToken() == null ? other.getClaimToken() == null : this.getClaimToken().equals(other.getClaimToken()))
            && (this.getClaimLeaseUntil() == null ? other.getClaimLeaseUntil() == null : this.getClaimLeaseUntil().equals(other.getClaimLeaseUntil()))
            && (this.getClaimDeadline() == null ? other.getClaimDeadline() == null : this.getClaimDeadline().equals(other.getClaimDeadline()))
            && (this.getReconciledThroughId() == null ? other.getReconciledThroughId() == null : this.getReconciledThroughId().equals(other.getReconciledThroughId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getChatId() == null) ? 0 : getChatId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCharacterUid() == null) ? 0 : getCharacterUid().hashCode());
        result = prime * result + ((getStoreType() == null) ? 0 : getStoreType().hashCode());
        result = prime * result + ((getGeneration() == null) ? 0 : getGeneration().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFingerprint() == null) ? 0 : getFingerprint().hashCode());
        result = prime * result + ((getLastActivity() == null) ? 0 : getLastActivity().hashCode());
        result = prime * result + ((getLatestFinalizedId() == null) ? 0 : getLatestFinalizedId().hashCode());
        result = prime * result + ((getEpisode() == null) ? 0 : getEpisode().hashCode());
        result = prime * result + ((getOverflowThroughId() == null) ? 0 : getOverflowThroughId().hashCode());
        result = prime * result + ((getIdleThroughId() == null) ? 0 : getIdleThroughId().hashCode());
        result = prime * result + ((getSummaryId() == null) ? 0 : getSummaryId().hashCode());
        result = prime * result + ((getProfileId() == null) ? 0 : getProfileId().hashCode());
        result = prime * result + ((getProfileRevalidationPending() == null) ? 0 : getProfileRevalidationPending().hashCode());
        result = prime * result + ((getTurnToken() == null) ? 0 : getTurnToken().hashCode());
        result = prime * result + ((getTurnLeaseUntil() == null) ? 0 : getTurnLeaseUntil().hashCode());
        result = prime * result + ((getTurnDeadline() == null) ? 0 : getTurnDeadline().hashCode());
        result = prime * result + ((getTurnRevision() == null) ? 0 : getTurnRevision().hashCode());
        result = prime * result + ((getDueAt() == null) ? 0 : getDueAt().hashCode());
        result = prime * result + ((getRetryAt() == null) ? 0 : getRetryAt().hashCode());
        result = prime * result + ((getRetryAttempts() == null) ? 0 : getRetryAttempts().hashCode());
        result = prime * result + ((getClaimToken() == null) ? 0 : getClaimToken().hashCode());
        result = prime * result + ((getClaimLeaseUntil() == null) ? 0 : getClaimLeaseUntil().hashCode());
        result = prime * result + ((getClaimDeadline() == null) ? 0 : getClaimDeadline().hashCode());
        result = prime * result + ((getReconciledThroughId() == null) ? 0 : getReconciledThroughId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", chatId=").append(chatId);
        sb.append(", userId=").append(userId);
        sb.append(", characterUid=").append(characterUid);
        sb.append(", storeType=").append(storeType);
        sb.append(", generation=").append(generation);
        sb.append(", version=").append(version);
        sb.append(", status=").append(status);
        sb.append(", fingerprint=").append(fingerprint);
        sb.append(", lastActivity=").append(lastActivity);
        sb.append(", latestFinalizedId=").append(latestFinalizedId);
        sb.append(", episode=").append(episode);
        sb.append(", overflowThroughId=").append(overflowThroughId);
        sb.append(", idleThroughId=").append(idleThroughId);
        sb.append(", summaryId=").append(summaryId);
        sb.append(", profileId=").append(profileId);
        sb.append(", profileRevalidationPending=").append(profileRevalidationPending);
        sb.append(", turnToken=").append(turnToken);
        sb.append(", turnLeaseUntil=").append(turnLeaseUntil);
        sb.append(", turnDeadline=").append(turnDeadline);
        sb.append(", turnRevision=").append(turnRevision);
        sb.append(", dueAt=").append(dueAt);
        sb.append(", retryAt=").append(retryAt);
        sb.append(", retryAttempts=").append(retryAttempts);
        sb.append(", claimToken=").append(claimToken);
        sb.append(", claimLeaseUntil=").append(claimLeaseUntil);
        sb.append(", claimDeadline=").append(claimDeadline);
        sb.append(", reconciledThroughId=").append(reconciledThroughId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}