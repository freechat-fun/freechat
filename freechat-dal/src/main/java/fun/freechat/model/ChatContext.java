package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatContext implements Serializable {
    private String chatId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtRead;

    private String chatType;

    private String userId;

    private String userNickname;

    private String backendId;

    private String characterNickname;

    private String apiKeyName;

    private Long quota;

    private String quotaType;

    private String userProfile;

    private String about;

    private String apiKeyValue;

    private String ext;

    private static final long serialVersionUID = 1L;

    public String getChatId() {
        return chatId;
    }

    public ChatContext withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public ChatContext withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public ChatContext withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public LocalDateTime getGmtRead() {
        return gmtRead;
    }

    public ChatContext withGmtRead(LocalDateTime gmtRead) {
        this.setGmtRead(gmtRead);
        return this;
    }

    public void setGmtRead(LocalDateTime gmtRead) {
        this.gmtRead = gmtRead;
    }

    public String getChatType() {
        return chatType;
    }

    public ChatContext withChatType(String chatType) {
        this.setChatType(chatType);
        return this;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getUserId() {
        return userId;
    }

    public ChatContext withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public ChatContext withUserNickname(String userNickname) {
        this.setUserNickname(userNickname);
        return this;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getBackendId() {
        return backendId;
    }

    public ChatContext withBackendId(String backendId) {
        this.setBackendId(backendId);
        return this;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public String getCharacterNickname() {
        return characterNickname;
    }

    public ChatContext withCharacterNickname(String characterNickname) {
        this.setCharacterNickname(characterNickname);
        return this;
    }

    public void setCharacterNickname(String characterNickname) {
        this.characterNickname = characterNickname;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public ChatContext withApiKeyName(String apiKeyName) {
        this.setApiKeyName(apiKeyName);
        return this;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    public Long getQuota() {
        return quota;
    }

    public ChatContext withQuota(Long quota) {
        this.setQuota(quota);
        return this;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public ChatContext withQuotaType(String quotaType) {
        this.setQuotaType(quotaType);
        return this;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public ChatContext withUserProfile(String userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getAbout() {
        return about;
    }

    public ChatContext withAbout(String about) {
        this.setAbout(about);
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public ChatContext withApiKeyValue(String apiKeyValue) {
        this.setApiKeyValue(apiKeyValue);
        return this;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String getExt() {
        return ext;
    }

    public ChatContext withExt(String ext) {
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
        ChatContext other = (ChatContext) that;
        return (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtRead() == null ? other.getGmtRead() == null : this.getGmtRead().equals(other.getGmtRead()))
            && (this.getChatType() == null ? other.getChatType() == null : this.getChatType().equals(other.getChatType()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserNickname() == null ? other.getUserNickname() == null : this.getUserNickname().equals(other.getUserNickname()))
            && (this.getBackendId() == null ? other.getBackendId() == null : this.getBackendId().equals(other.getBackendId()))
            && (this.getCharacterNickname() == null ? other.getCharacterNickname() == null : this.getCharacterNickname().equals(other.getCharacterNickname()))
            && (this.getApiKeyName() == null ? other.getApiKeyName() == null : this.getApiKeyName().equals(other.getApiKeyName()))
            && (this.getQuota() == null ? other.getQuota() == null : this.getQuota().equals(other.getQuota()))
            && (this.getQuotaType() == null ? other.getQuotaType() == null : this.getQuotaType().equals(other.getQuotaType()))
            && (this.getUserProfile() == null ? other.getUserProfile() == null : this.getUserProfile().equals(other.getUserProfile()))
            && (this.getAbout() == null ? other.getAbout() == null : this.getAbout().equals(other.getAbout()))
            && (this.getApiKeyValue() == null ? other.getApiKeyValue() == null : this.getApiKeyValue().equals(other.getApiKeyValue()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getChatId() == null) ? 0 : getChatId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getGmtRead() == null) ? 0 : getGmtRead().hashCode());
        result = prime * result + ((getChatType() == null) ? 0 : getChatType().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserNickname() == null) ? 0 : getUserNickname().hashCode());
        result = prime * result + ((getBackendId() == null) ? 0 : getBackendId().hashCode());
        result = prime * result + ((getCharacterNickname() == null) ? 0 : getCharacterNickname().hashCode());
        result = prime * result + ((getApiKeyName() == null) ? 0 : getApiKeyName().hashCode());
        result = prime * result + ((getQuota() == null) ? 0 : getQuota().hashCode());
        result = prime * result + ((getQuotaType() == null) ? 0 : getQuotaType().hashCode());
        result = prime * result + ((getUserProfile() == null) ? 0 : getUserProfile().hashCode());
        result = prime * result + ((getAbout() == null) ? 0 : getAbout().hashCode());
        result = prime * result + ((getApiKeyValue() == null) ? 0 : getApiKeyValue().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", chatId=").append(chatId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", gmtRead=").append(gmtRead);
        sb.append(", chatType=").append(chatType);
        sb.append(", userId=").append(userId);
        sb.append(", userNickname=").append(userNickname);
        sb.append(", backendId=").append(backendId);
        sb.append(", characterNickname=").append(characterNickname);
        sb.append(", apiKeyName=").append(apiKeyName);
        sb.append(", quota=").append(quota);
        sb.append(", quotaType=").append(quotaType);
        sb.append(", userProfile=").append(userProfile);
        sb.append(", about=").append(about);
        sb.append(", apiKeyValue=").append(apiKeyValue);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}