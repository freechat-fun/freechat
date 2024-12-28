package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class CharacterBackend implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String backendId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String characterUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte isDefault;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String greetingPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String moderationModelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String moderationApiKeyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte forwardToUser;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer messageWindowSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer longTermMemoryWindowSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer proactiveChatWaitingTime;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long initQuota;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String quotaType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte enableAlbumTool;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte enableTts;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ttsSpeakerIdx;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ttsSpeakerWav;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ttsSpeakerType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String moderationParams;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getBackendId() {
        return backendId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withBackendId(String backendId) {
        this.setBackendId(backendId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withGmtCreate(Date gmtCreate) {
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
    public CharacterBackend withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getCharacterUid() {
        return characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getIsDefault() {
        return isDefault;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withIsDefault(Byte isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getChatPromptTaskId() {
        return chatPromptTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withChatPromptTaskId(String chatPromptTaskId) {
        this.setChatPromptTaskId(chatPromptTaskId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setChatPromptTaskId(String chatPromptTaskId) {
        this.chatPromptTaskId = chatPromptTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getGreetingPromptTaskId() {
        return greetingPromptTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withGreetingPromptTaskId(String greetingPromptTaskId) {
        this.setGreetingPromptTaskId(greetingPromptTaskId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGreetingPromptTaskId(String greetingPromptTaskId) {
        this.greetingPromptTaskId = greetingPromptTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getModerationModelId() {
        return moderationModelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withModerationModelId(String moderationModelId) {
        this.setModerationModelId(moderationModelId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setModerationModelId(String moderationModelId) {
        this.moderationModelId = moderationModelId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getModerationApiKeyName() {
        return moderationApiKeyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withModerationApiKeyName(String moderationApiKeyName) {
        this.setModerationApiKeyName(moderationApiKeyName);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setModerationApiKeyName(String moderationApiKeyName) {
        this.moderationApiKeyName = moderationApiKeyName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getForwardToUser() {
        return forwardToUser;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withForwardToUser(Byte forwardToUser) {
        this.setForwardToUser(forwardToUser);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setForwardToUser(Byte forwardToUser) {
        this.forwardToUser = forwardToUser;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getMessageWindowSize() {
        return messageWindowSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withMessageWindowSize(Integer messageWindowSize) {
        this.setMessageWindowSize(messageWindowSize);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setMessageWindowSize(Integer messageWindowSize) {
        this.messageWindowSize = messageWindowSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getLongTermMemoryWindowSize() {
        return longTermMemoryWindowSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withLongTermMemoryWindowSize(Integer longTermMemoryWindowSize) {
        this.setLongTermMemoryWindowSize(longTermMemoryWindowSize);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setLongTermMemoryWindowSize(Integer longTermMemoryWindowSize) {
        this.longTermMemoryWindowSize = longTermMemoryWindowSize;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getProactiveChatWaitingTime() {
        return proactiveChatWaitingTime;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withProactiveChatWaitingTime(Integer proactiveChatWaitingTime) {
        this.setProactiveChatWaitingTime(proactiveChatWaitingTime);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setProactiveChatWaitingTime(Integer proactiveChatWaitingTime) {
        this.proactiveChatWaitingTime = proactiveChatWaitingTime;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getInitQuota() {
        return initQuota;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withInitQuota(Long initQuota) {
        this.setInitQuota(initQuota);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setInitQuota(Long initQuota) {
        this.initQuota = initQuota;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getQuotaType() {
        return quotaType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withQuotaType(String quotaType) {
        this.setQuotaType(quotaType);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEnableAlbumTool() {
        return enableAlbumTool;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withEnableAlbumTool(Byte enableAlbumTool) {
        this.setEnableAlbumTool(enableAlbumTool);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEnableAlbumTool(Byte enableAlbumTool) {
        this.enableAlbumTool = enableAlbumTool;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Byte getEnableTts() {
        return enableTts;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withEnableTts(Byte enableTts) {
        this.setEnableTts(enableTts);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setEnableTts(Byte enableTts) {
        this.enableTts = enableTts;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getTtsSpeakerIdx() {
        return ttsSpeakerIdx;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withTtsSpeakerIdx(String ttsSpeakerIdx) {
        this.setTtsSpeakerIdx(ttsSpeakerIdx);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setTtsSpeakerIdx(String ttsSpeakerIdx) {
        this.ttsSpeakerIdx = ttsSpeakerIdx;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getTtsSpeakerWav() {
        return ttsSpeakerWav;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withTtsSpeakerWav(String ttsSpeakerWav) {
        this.setTtsSpeakerWav(ttsSpeakerWav);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setTtsSpeakerWav(String ttsSpeakerWav) {
        this.ttsSpeakerWav = ttsSpeakerWav;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getTtsSpeakerType() {
        return ttsSpeakerType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withTtsSpeakerType(String ttsSpeakerType) {
        this.setTtsSpeakerType(ttsSpeakerType);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setTtsSpeakerType(String ttsSpeakerType) {
        this.ttsSpeakerType = ttsSpeakerType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getModerationParams() {
        return moderationParams;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withModerationParams(String moderationParams) {
        this.setModerationParams(moderationParams);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setModerationParams(String moderationParams) {
        this.moderationParams = moderationParams;
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
            && (this.getModerationParams() == null ? other.getModerationParams() == null : this.getModerationParams().equals(other.getModerationParams()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        result = prime * result + ((getModerationParams() == null) ? 0 : getModerationParams().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        sb.append(", moderationParams=").append(moderationParams);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}