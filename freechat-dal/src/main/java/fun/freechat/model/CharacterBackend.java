package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CharacterBackend implements Serializable {
    private String backendId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String characterUid;

    private Byte isDefault;

    private String chatPromptTaskId;

    private String greetingPromptTaskId;

    private String moderationModelId;

    private String moderationApiKeyName;

    private String imageModelId;

    private String imageApiKeyName;

    private Byte forwardToUser;

    private Integer messageWindowSize;

    private Integer longTermMemoryWindowSize;

    private Integer proactiveChatWaitingTime;

    private Long initQuota;

    private String quotaType;

    private Byte enableAlbumTool;

    private Byte enableTts;

    private String ttsSpeakerIdx;

    private String ttsSpeakerWav;

    private String ttsSpeakerType;

    private Long tgBotId;

    private String moderationApiKeyValue;

    private String moderationParams;

    private String imageApiKeyValue;

    private String imageParams;

    private static final long serialVersionUID = 1L;

    public String getBackendId() {
        return backendId;
    }

    public CharacterBackend withBackendId(String backendId) {
        this.setBackendId(backendId);
        return this;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public CharacterBackend withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public CharacterBackend withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCharacterUid() {
        return characterUid;
    }

    public CharacterBackend withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    public Byte getIsDefault() {
        return isDefault;
    }

    public CharacterBackend withIsDefault(Byte isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
    }

    public String getChatPromptTaskId() {
        return chatPromptTaskId;
    }

    public CharacterBackend withChatPromptTaskId(String chatPromptTaskId) {
        this.setChatPromptTaskId(chatPromptTaskId);
        return this;
    }

    public void setChatPromptTaskId(String chatPromptTaskId) {
        this.chatPromptTaskId = chatPromptTaskId;
    }

    public String getGreetingPromptTaskId() {
        return greetingPromptTaskId;
    }

    public CharacterBackend withGreetingPromptTaskId(String greetingPromptTaskId) {
        this.setGreetingPromptTaskId(greetingPromptTaskId);
        return this;
    }

    public void setGreetingPromptTaskId(String greetingPromptTaskId) {
        this.greetingPromptTaskId = greetingPromptTaskId;
    }

    public String getModerationModelId() {
        return moderationModelId;
    }

    public CharacterBackend withModerationModelId(String moderationModelId) {
        this.setModerationModelId(moderationModelId);
        return this;
    }

    public void setModerationModelId(String moderationModelId) {
        this.moderationModelId = moderationModelId;
    }

    public String getModerationApiKeyName() {
        return moderationApiKeyName;
    }

    public CharacterBackend withModerationApiKeyName(String moderationApiKeyName) {
        this.setModerationApiKeyName(moderationApiKeyName);
        return this;
    }

    public void setModerationApiKeyName(String moderationApiKeyName) {
        this.moderationApiKeyName = moderationApiKeyName;
    }

    public String getImageModelId() {
        return imageModelId;
    }

    public CharacterBackend withImageModelId(String imageModelId) {
        this.setImageModelId(imageModelId);
        return this;
    }

    public void setImageModelId(String imageModelId) {
        this.imageModelId = imageModelId;
    }

    public String getImageApiKeyName() {
        return imageApiKeyName;
    }

    public CharacterBackend withImageApiKeyName(String imageApiKeyName) {
        this.setImageApiKeyName(imageApiKeyName);
        return this;
    }

    public void setImageApiKeyName(String imageApiKeyName) {
        this.imageApiKeyName = imageApiKeyName;
    }

    public Byte getForwardToUser() {
        return forwardToUser;
    }

    public CharacterBackend withForwardToUser(Byte forwardToUser) {
        this.setForwardToUser(forwardToUser);
        return this;
    }

    public void setForwardToUser(Byte forwardToUser) {
        this.forwardToUser = forwardToUser;
    }

    public Integer getMessageWindowSize() {
        return messageWindowSize;
    }

    public CharacterBackend withMessageWindowSize(Integer messageWindowSize) {
        this.setMessageWindowSize(messageWindowSize);
        return this;
    }

    public void setMessageWindowSize(Integer messageWindowSize) {
        this.messageWindowSize = messageWindowSize;
    }

    public Integer getLongTermMemoryWindowSize() {
        return longTermMemoryWindowSize;
    }

    public CharacterBackend withLongTermMemoryWindowSize(Integer longTermMemoryWindowSize) {
        this.setLongTermMemoryWindowSize(longTermMemoryWindowSize);
        return this;
    }

    public void setLongTermMemoryWindowSize(Integer longTermMemoryWindowSize) {
        this.longTermMemoryWindowSize = longTermMemoryWindowSize;
    }

    public Integer getProactiveChatWaitingTime() {
        return proactiveChatWaitingTime;
    }

    public CharacterBackend withProactiveChatWaitingTime(Integer proactiveChatWaitingTime) {
        this.setProactiveChatWaitingTime(proactiveChatWaitingTime);
        return this;
    }

    public void setProactiveChatWaitingTime(Integer proactiveChatWaitingTime) {
        this.proactiveChatWaitingTime = proactiveChatWaitingTime;
    }

    public Long getInitQuota() {
        return initQuota;
    }

    public CharacterBackend withInitQuota(Long initQuota) {
        this.setInitQuota(initQuota);
        return this;
    }

    public void setInitQuota(Long initQuota) {
        this.initQuota = initQuota;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public CharacterBackend withQuotaType(String quotaType) {
        this.setQuotaType(quotaType);
        return this;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public Byte getEnableAlbumTool() {
        return enableAlbumTool;
    }

    public CharacterBackend withEnableAlbumTool(Byte enableAlbumTool) {
        this.setEnableAlbumTool(enableAlbumTool);
        return this;
    }

    public void setEnableAlbumTool(Byte enableAlbumTool) {
        this.enableAlbumTool = enableAlbumTool;
    }

    public Byte getEnableTts() {
        return enableTts;
    }

    public CharacterBackend withEnableTts(Byte enableTts) {
        this.setEnableTts(enableTts);
        return this;
    }

    public void setEnableTts(Byte enableTts) {
        this.enableTts = enableTts;
    }

    public String getTtsSpeakerIdx() {
        return ttsSpeakerIdx;
    }

    public CharacterBackend withTtsSpeakerIdx(String ttsSpeakerIdx) {
        this.setTtsSpeakerIdx(ttsSpeakerIdx);
        return this;
    }

    public void setTtsSpeakerIdx(String ttsSpeakerIdx) {
        this.ttsSpeakerIdx = ttsSpeakerIdx;
    }

    public String getTtsSpeakerWav() {
        return ttsSpeakerWav;
    }

    public CharacterBackend withTtsSpeakerWav(String ttsSpeakerWav) {
        this.setTtsSpeakerWav(ttsSpeakerWav);
        return this;
    }

    public void setTtsSpeakerWav(String ttsSpeakerWav) {
        this.ttsSpeakerWav = ttsSpeakerWav;
    }

    public String getTtsSpeakerType() {
        return ttsSpeakerType;
    }

    public CharacterBackend withTtsSpeakerType(String ttsSpeakerType) {
        this.setTtsSpeakerType(ttsSpeakerType);
        return this;
    }

    public void setTtsSpeakerType(String ttsSpeakerType) {
        this.ttsSpeakerType = ttsSpeakerType;
    }

    public Long getTgBotId() {
        return tgBotId;
    }

    public CharacterBackend withTgBotId(Long tgBotId) {
        this.setTgBotId(tgBotId);
        return this;
    }

    public void setTgBotId(Long tgBotId) {
        this.tgBotId = tgBotId;
    }

    public String getModerationApiKeyValue() {
        return moderationApiKeyValue;
    }

    public CharacterBackend withModerationApiKeyValue(String moderationApiKeyValue) {
        this.setModerationApiKeyValue(moderationApiKeyValue);
        return this;
    }

    public void setModerationApiKeyValue(String moderationApiKeyValue) {
        this.moderationApiKeyValue = moderationApiKeyValue;
    }

    public String getModerationParams() {
        return moderationParams;
    }

    public CharacterBackend withModerationParams(String moderationParams) {
        this.setModerationParams(moderationParams);
        return this;
    }

    public void setModerationParams(String moderationParams) {
        this.moderationParams = moderationParams;
    }

    public String getImageApiKeyValue() {
        return imageApiKeyValue;
    }

    public CharacterBackend withImageApiKeyValue(String imageApiKeyValue) {
        this.setImageApiKeyValue(imageApiKeyValue);
        return this;
    }

    public void setImageApiKeyValue(String imageApiKeyValue) {
        this.imageApiKeyValue = imageApiKeyValue;
    }

    public String getImageParams() {
        return imageParams;
    }

    public CharacterBackend withImageParams(String imageParams) {
        this.setImageParams(imageParams);
        return this;
    }

    public void setImageParams(String imageParams) {
        this.imageParams = imageParams;
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
        CharacterBackend other = (CharacterBackend) that;
        return (this.getBackendId() == null ? other.getBackendId() == null : this.getBackendId().equals(other.getBackendId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getCharacterUid() == null ? other.getCharacterUid() == null : this.getCharacterUid().equals(other.getCharacterUid()))
            && (this.getIsDefault() == null ? other.getIsDefault() == null : this.getIsDefault().equals(other.getIsDefault()))
            && (this.getChatPromptTaskId() == null ? other.getChatPromptTaskId() == null : this.getChatPromptTaskId().equals(other.getChatPromptTaskId()))
            && (this.getGreetingPromptTaskId() == null ? other.getGreetingPromptTaskId() == null : this.getGreetingPromptTaskId().equals(other.getGreetingPromptTaskId()))
            && (this.getModerationModelId() == null ? other.getModerationModelId() == null : this.getModerationModelId().equals(other.getModerationModelId()))
            && (this.getModerationApiKeyName() == null ? other.getModerationApiKeyName() == null : this.getModerationApiKeyName().equals(other.getModerationApiKeyName()))
            && (this.getImageModelId() == null ? other.getImageModelId() == null : this.getImageModelId().equals(other.getImageModelId()))
            && (this.getImageApiKeyName() == null ? other.getImageApiKeyName() == null : this.getImageApiKeyName().equals(other.getImageApiKeyName()))
            && (this.getForwardToUser() == null ? other.getForwardToUser() == null : this.getForwardToUser().equals(other.getForwardToUser()))
            && (this.getMessageWindowSize() == null ? other.getMessageWindowSize() == null : this.getMessageWindowSize().equals(other.getMessageWindowSize()))
            && (this.getLongTermMemoryWindowSize() == null ? other.getLongTermMemoryWindowSize() == null : this.getLongTermMemoryWindowSize().equals(other.getLongTermMemoryWindowSize()))
            && (this.getProactiveChatWaitingTime() == null ? other.getProactiveChatWaitingTime() == null : this.getProactiveChatWaitingTime().equals(other.getProactiveChatWaitingTime()))
            && (this.getInitQuota() == null ? other.getInitQuota() == null : this.getInitQuota().equals(other.getInitQuota()))
            && (this.getQuotaType() == null ? other.getQuotaType() == null : this.getQuotaType().equals(other.getQuotaType()))
            && (this.getEnableAlbumTool() == null ? other.getEnableAlbumTool() == null : this.getEnableAlbumTool().equals(other.getEnableAlbumTool()))
            && (this.getEnableTts() == null ? other.getEnableTts() == null : this.getEnableTts().equals(other.getEnableTts()))
            && (this.getTtsSpeakerIdx() == null ? other.getTtsSpeakerIdx() == null : this.getTtsSpeakerIdx().equals(other.getTtsSpeakerIdx()))
            && (this.getTtsSpeakerWav() == null ? other.getTtsSpeakerWav() == null : this.getTtsSpeakerWav().equals(other.getTtsSpeakerWav()))
            && (this.getTtsSpeakerType() == null ? other.getTtsSpeakerType() == null : this.getTtsSpeakerType().equals(other.getTtsSpeakerType()))
            && (this.getTgBotId() == null ? other.getTgBotId() == null : this.getTgBotId().equals(other.getTgBotId()))
            && (this.getModerationApiKeyValue() == null ? other.getModerationApiKeyValue() == null : this.getModerationApiKeyValue().equals(other.getModerationApiKeyValue()))
            && (this.getModerationParams() == null ? other.getModerationParams() == null : this.getModerationParams().equals(other.getModerationParams()))
            && (this.getImageApiKeyValue() == null ? other.getImageApiKeyValue() == null : this.getImageApiKeyValue().equals(other.getImageApiKeyValue()))
            && (this.getImageParams() == null ? other.getImageParams() == null : this.getImageParams().equals(other.getImageParams()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBackendId() == null) ? 0 : getBackendId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getCharacterUid() == null) ? 0 : getCharacterUid().hashCode());
        result = prime * result + ((getIsDefault() == null) ? 0 : getIsDefault().hashCode());
        result = prime * result + ((getChatPromptTaskId() == null) ? 0 : getChatPromptTaskId().hashCode());
        result = prime * result + ((getGreetingPromptTaskId() == null) ? 0 : getGreetingPromptTaskId().hashCode());
        result = prime * result + ((getModerationModelId() == null) ? 0 : getModerationModelId().hashCode());
        result = prime * result + ((getModerationApiKeyName() == null) ? 0 : getModerationApiKeyName().hashCode());
        result = prime * result + ((getImageModelId() == null) ? 0 : getImageModelId().hashCode());
        result = prime * result + ((getImageApiKeyName() == null) ? 0 : getImageApiKeyName().hashCode());
        result = prime * result + ((getForwardToUser() == null) ? 0 : getForwardToUser().hashCode());
        result = prime * result + ((getMessageWindowSize() == null) ? 0 : getMessageWindowSize().hashCode());
        result = prime * result + ((getLongTermMemoryWindowSize() == null) ? 0 : getLongTermMemoryWindowSize().hashCode());
        result = prime * result + ((getProactiveChatWaitingTime() == null) ? 0 : getProactiveChatWaitingTime().hashCode());
        result = prime * result + ((getInitQuota() == null) ? 0 : getInitQuota().hashCode());
        result = prime * result + ((getQuotaType() == null) ? 0 : getQuotaType().hashCode());
        result = prime * result + ((getEnableAlbumTool() == null) ? 0 : getEnableAlbumTool().hashCode());
        result = prime * result + ((getEnableTts() == null) ? 0 : getEnableTts().hashCode());
        result = prime * result + ((getTtsSpeakerIdx() == null) ? 0 : getTtsSpeakerIdx().hashCode());
        result = prime * result + ((getTtsSpeakerWav() == null) ? 0 : getTtsSpeakerWav().hashCode());
        result = prime * result + ((getTtsSpeakerType() == null) ? 0 : getTtsSpeakerType().hashCode());
        result = prime * result + ((getTgBotId() == null) ? 0 : getTgBotId().hashCode());
        result = prime * result + ((getModerationApiKeyValue() == null) ? 0 : getModerationApiKeyValue().hashCode());
        result = prime * result + ((getModerationParams() == null) ? 0 : getModerationParams().hashCode());
        result = prime * result + ((getImageApiKeyValue() == null) ? 0 : getImageApiKeyValue().hashCode());
        result = prime * result + ((getImageParams() == null) ? 0 : getImageParams().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", backendId=").append(backendId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", characterUid=").append(characterUid);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", chatPromptTaskId=").append(chatPromptTaskId);
        sb.append(", greetingPromptTaskId=").append(greetingPromptTaskId);
        sb.append(", moderationModelId=").append(moderationModelId);
        sb.append(", moderationApiKeyName=").append(moderationApiKeyName);
        sb.append(", imageModelId=").append(imageModelId);
        sb.append(", imageApiKeyName=").append(imageApiKeyName);
        sb.append(", forwardToUser=").append(forwardToUser);
        sb.append(", messageWindowSize=").append(messageWindowSize);
        sb.append(", longTermMemoryWindowSize=").append(longTermMemoryWindowSize);
        sb.append(", proactiveChatWaitingTime=").append(proactiveChatWaitingTime);
        sb.append(", initQuota=").append(initQuota);
        sb.append(", quotaType=").append(quotaType);
        sb.append(", enableAlbumTool=").append(enableAlbumTool);
        sb.append(", enableTts=").append(enableTts);
        sb.append(", ttsSpeakerIdx=").append(ttsSpeakerIdx);
        sb.append(", ttsSpeakerWav=").append(ttsSpeakerWav);
        sb.append(", ttsSpeakerType=").append(ttsSpeakerType);
        sb.append(", tgBotId=").append(tgBotId);
        sb.append(", moderationApiKeyValue=").append(moderationApiKeyValue);
        sb.append(", moderationParams=").append(moderationParams);
        sb.append(", imageApiKeyValue=").append(imageApiKeyValue);
        sb.append(", imageParams=").append(imageParams);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}