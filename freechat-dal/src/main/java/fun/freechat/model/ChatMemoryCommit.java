package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMemoryCommit implements Serializable {
    private String attemptId;

    private String chatId;

    private Long generation;

    private String operation;

    private Long sourceStartId;

    private Long sourceEndId;

    private Long expectedCursor;

    private String expectedHead;

    private String leaseToken;

    private String status;

    private String fingerprint;

    private Integer schemaVersion;

    private String modelId;

    private String errorCategory;

    private LocalDateTime leaseUntil;

    private LocalDateTime gcAfter;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String manifest;

    private String progress;

    private String tokenUsage;

    private static final long serialVersionUID = 1L;

    public String getAttemptId() {
        return attemptId;
    }

    public ChatMemoryCommit withAttemptId(String attemptId) {
        this.setAttemptId(attemptId);
        return this;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getChatId() {
        return chatId;
    }

    public ChatMemoryCommit withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getGeneration() {
        return generation;
    }

    public ChatMemoryCommit withGeneration(Long generation) {
        this.setGeneration(generation);
        return this;
    }

    public void setGeneration(Long generation) {
        this.generation = generation;
    }

    public String getOperation() {
        return operation;
    }

    public ChatMemoryCommit withOperation(String operation) {
        this.setOperation(operation);
        return this;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getSourceStartId() {
        return sourceStartId;
    }

    public ChatMemoryCommit withSourceStartId(Long sourceStartId) {
        this.setSourceStartId(sourceStartId);
        return this;
    }

    public void setSourceStartId(Long sourceStartId) {
        this.sourceStartId = sourceStartId;
    }

    public Long getSourceEndId() {
        return sourceEndId;
    }

    public ChatMemoryCommit withSourceEndId(Long sourceEndId) {
        this.setSourceEndId(sourceEndId);
        return this;
    }

    public void setSourceEndId(Long sourceEndId) {
        this.sourceEndId = sourceEndId;
    }

    public Long getExpectedCursor() {
        return expectedCursor;
    }

    public ChatMemoryCommit withExpectedCursor(Long expectedCursor) {
        this.setExpectedCursor(expectedCursor);
        return this;
    }

    public void setExpectedCursor(Long expectedCursor) {
        this.expectedCursor = expectedCursor;
    }

    public String getExpectedHead() {
        return expectedHead;
    }

    public ChatMemoryCommit withExpectedHead(String expectedHead) {
        this.setExpectedHead(expectedHead);
        return this;
    }

    public void setExpectedHead(String expectedHead) {
        this.expectedHead = expectedHead;
    }

    public String getLeaseToken() {
        return leaseToken;
    }

    public ChatMemoryCommit withLeaseToken(String leaseToken) {
        this.setLeaseToken(leaseToken);
        return this;
    }

    public void setLeaseToken(String leaseToken) {
        this.leaseToken = leaseToken;
    }

    public String getStatus() {
        return status;
    }

    public ChatMemoryCommit withStatus(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public ChatMemoryCommit withFingerprint(String fingerprint) {
        this.setFingerprint(fingerprint);
        return this;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public ChatMemoryCommit withSchemaVersion(Integer schemaVersion) {
        this.setSchemaVersion(schemaVersion);
        return this;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getModelId() {
        return modelId;
    }

    public ChatMemoryCommit withModelId(String modelId) {
        this.setModelId(modelId);
        return this;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getErrorCategory() {
        return errorCategory;
    }

    public ChatMemoryCommit withErrorCategory(String errorCategory) {
        this.setErrorCategory(errorCategory);
        return this;
    }

    public void setErrorCategory(String errorCategory) {
        this.errorCategory = errorCategory;
    }

    public LocalDateTime getLeaseUntil() {
        return leaseUntil;
    }

    public ChatMemoryCommit withLeaseUntil(LocalDateTime leaseUntil) {
        this.setLeaseUntil(leaseUntil);
        return this;
    }

    public void setLeaseUntil(LocalDateTime leaseUntil) {
        this.leaseUntil = leaseUntil;
    }

    public LocalDateTime getGcAfter() {
        return gcAfter;
    }

    public ChatMemoryCommit withGcAfter(LocalDateTime gcAfter) {
        this.setGcAfter(gcAfter);
        return this;
    }

    public void setGcAfter(LocalDateTime gcAfter) {
        this.gcAfter = gcAfter;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public ChatMemoryCommit withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public ChatMemoryCommit withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getManifest() {
        return manifest;
    }

    public ChatMemoryCommit withManifest(String manifest) {
        this.setManifest(manifest);
        return this;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    public String getProgress() {
        return progress;
    }

    public ChatMemoryCommit withProgress(String progress) {
        this.setProgress(progress);
        return this;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTokenUsage() {
        return tokenUsage;
    }

    public ChatMemoryCommit withTokenUsage(String tokenUsage) {
        this.setTokenUsage(tokenUsage);
        return this;
    }

    public void setTokenUsage(String tokenUsage) {
        this.tokenUsage = tokenUsage;
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
        ChatMemoryCommit other = (ChatMemoryCommit) that;
        return (this.getAttemptId() == null ? other.getAttemptId() == null : this.getAttemptId().equals(other.getAttemptId()))
            && (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getGeneration() == null ? other.getGeneration() == null : this.getGeneration().equals(other.getGeneration()))
            && (this.getOperation() == null ? other.getOperation() == null : this.getOperation().equals(other.getOperation()))
            && (this.getSourceStartId() == null ? other.getSourceStartId() == null : this.getSourceStartId().equals(other.getSourceStartId()))
            && (this.getSourceEndId() == null ? other.getSourceEndId() == null : this.getSourceEndId().equals(other.getSourceEndId()))
            && (this.getExpectedCursor() == null ? other.getExpectedCursor() == null : this.getExpectedCursor().equals(other.getExpectedCursor()))
            && (this.getExpectedHead() == null ? other.getExpectedHead() == null : this.getExpectedHead().equals(other.getExpectedHead()))
            && (this.getLeaseToken() == null ? other.getLeaseToken() == null : this.getLeaseToken().equals(other.getLeaseToken()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getFingerprint() == null ? other.getFingerprint() == null : this.getFingerprint().equals(other.getFingerprint()))
            && (this.getSchemaVersion() == null ? other.getSchemaVersion() == null : this.getSchemaVersion().equals(other.getSchemaVersion()))
            && (this.getModelId() == null ? other.getModelId() == null : this.getModelId().equals(other.getModelId()))
            && (this.getErrorCategory() == null ? other.getErrorCategory() == null : this.getErrorCategory().equals(other.getErrorCategory()))
            && (this.getLeaseUntil() == null ? other.getLeaseUntil() == null : this.getLeaseUntil().equals(other.getLeaseUntil()))
            && (this.getGcAfter() == null ? other.getGcAfter() == null : this.getGcAfter().equals(other.getGcAfter()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getManifest() == null ? other.getManifest() == null : this.getManifest().equals(other.getManifest()))
            && (this.getProgress() == null ? other.getProgress() == null : this.getProgress().equals(other.getProgress()))
            && (this.getTokenUsage() == null ? other.getTokenUsage() == null : this.getTokenUsage().equals(other.getTokenUsage()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAttemptId() == null) ? 0 : getAttemptId().hashCode());
        result = prime * result + ((getChatId() == null) ? 0 : getChatId().hashCode());
        result = prime * result + ((getGeneration() == null) ? 0 : getGeneration().hashCode());
        result = prime * result + ((getOperation() == null) ? 0 : getOperation().hashCode());
        result = prime * result + ((getSourceStartId() == null) ? 0 : getSourceStartId().hashCode());
        result = prime * result + ((getSourceEndId() == null) ? 0 : getSourceEndId().hashCode());
        result = prime * result + ((getExpectedCursor() == null) ? 0 : getExpectedCursor().hashCode());
        result = prime * result + ((getExpectedHead() == null) ? 0 : getExpectedHead().hashCode());
        result = prime * result + ((getLeaseToken() == null) ? 0 : getLeaseToken().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFingerprint() == null) ? 0 : getFingerprint().hashCode());
        result = prime * result + ((getSchemaVersion() == null) ? 0 : getSchemaVersion().hashCode());
        result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
        result = prime * result + ((getErrorCategory() == null) ? 0 : getErrorCategory().hashCode());
        result = prime * result + ((getLeaseUntil() == null) ? 0 : getLeaseUntil().hashCode());
        result = prime * result + ((getGcAfter() == null) ? 0 : getGcAfter().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getManifest() == null) ? 0 : getManifest().hashCode());
        result = prime * result + ((getProgress() == null) ? 0 : getProgress().hashCode());
        result = prime * result + ((getTokenUsage() == null) ? 0 : getTokenUsage().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", attemptId=").append(attemptId);
        sb.append(", chatId=").append(chatId);
        sb.append(", generation=").append(generation);
        sb.append(", operation=").append(operation);
        sb.append(", sourceStartId=").append(sourceStartId);
        sb.append(", sourceEndId=").append(sourceEndId);
        sb.append(", expectedCursor=").append(expectedCursor);
        sb.append(", expectedHead=").append(expectedHead);
        sb.append(", leaseToken=").append(leaseToken);
        sb.append(", status=").append(status);
        sb.append(", fingerprint=").append(fingerprint);
        sb.append(", schemaVersion=").append(schemaVersion);
        sb.append(", modelId=").append(modelId);
        sb.append(", errorCategory=").append(errorCategory);
        sb.append(", leaseUntil=").append(leaseUntil);
        sb.append(", gcAfter=").append(gcAfter);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", manifest=").append(manifest);
        sb.append(", progress=").append(progress);
        sb.append(", tokenUsage=").append(tokenUsage);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}