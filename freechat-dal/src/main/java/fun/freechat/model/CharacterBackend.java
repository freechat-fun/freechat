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
    private String characterId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte isDefault;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String greetingPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String experiencePromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String moderationModelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String moderationApiKeyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Byte forwardToUser;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer messageWindowSize;

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
        this.backendId = backendId == null ? null : backendId.trim();
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
    public String getCharacterId() {
        return characterId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withCharacterId(String characterId) {
        this.setCharacterId(characterId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterId(String characterId) {
        this.characterId = characterId == null ? null : characterId.trim();
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
        this.chatPromptTaskId = chatPromptTaskId == null ? null : chatPromptTaskId.trim();
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
        this.greetingPromptTaskId = greetingPromptTaskId == null ? null : greetingPromptTaskId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExperiencePromptTaskId() {
        return experiencePromptTaskId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterBackend withExperiencePromptTaskId(String experiencePromptTaskId) {
        this.setExperiencePromptTaskId(experiencePromptTaskId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExperiencePromptTaskId(String experiencePromptTaskId) {
        this.experiencePromptTaskId = experiencePromptTaskId == null ? null : experiencePromptTaskId.trim();
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
        this.moderationModelId = moderationModelId == null ? null : moderationModelId.trim();
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
        this.moderationApiKeyName = moderationApiKeyName == null ? null : moderationApiKeyName.trim();
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
        this.moderationParams = moderationParams == null ? null : moderationParams.trim();
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
            && (this.getCharacterId() == null ? other.getCharacterId() == null : this.getCharacterId().equals(other.getCharacterId()))
            && (this.getIsDefault() == null ? other.getIsDefault() == null : this.getIsDefault().equals(other.getIsDefault()))
            && (this.getChatPromptTaskId() == null ? other.getChatPromptTaskId() == null : this.getChatPromptTaskId().equals(other.getChatPromptTaskId()))
            && (this.getGreetingPromptTaskId() == null ? other.getGreetingPromptTaskId() == null : this.getGreetingPromptTaskId().equals(other.getGreetingPromptTaskId()))
            && (this.getExperiencePromptTaskId() == null ? other.getExperiencePromptTaskId() == null : this.getExperiencePromptTaskId().equals(other.getExperiencePromptTaskId()))
            && (this.getModerationModelId() == null ? other.getModerationModelId() == null : this.getModerationModelId().equals(other.getModerationModelId()))
            && (this.getModerationApiKeyName() == null ? other.getModerationApiKeyName() == null : this.getModerationApiKeyName().equals(other.getModerationApiKeyName()))
            && (this.getForwardToUser() == null ? other.getForwardToUser() == null : this.getForwardToUser().equals(other.getForwardToUser()))
            && (this.getMessageWindowSize() == null ? other.getMessageWindowSize() == null : this.getMessageWindowSize().equals(other.getMessageWindowSize()))
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
        result = prime * result + ((getCharacterId() == null) ? 0 : getCharacterId().hashCode());
        result = prime * result + ((getIsDefault() == null) ? 0 : getIsDefault().hashCode());
        result = prime * result + ((getChatPromptTaskId() == null) ? 0 : getChatPromptTaskId().hashCode());
        result = prime * result + ((getGreetingPromptTaskId() == null) ? 0 : getGreetingPromptTaskId().hashCode());
        result = prime * result + ((getExperiencePromptTaskId() == null) ? 0 : getExperiencePromptTaskId().hashCode());
        result = prime * result + ((getModerationModelId() == null) ? 0 : getModerationModelId().hashCode());
        result = prime * result + ((getModerationApiKeyName() == null) ? 0 : getModerationApiKeyName().hashCode());
        result = prime * result + ((getForwardToUser() == null) ? 0 : getForwardToUser().hashCode());
        result = prime * result + ((getMessageWindowSize() == null) ? 0 : getMessageWindowSize().hashCode());
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
        sb.append(", characterId=").append(characterId);
        sb.append(", isDefault=").append(isDefault);
        sb.append(", chatPromptTaskId=").append(chatPromptTaskId);
        sb.append(", greetingPromptTaskId=").append(greetingPromptTaskId);
        sb.append(", experiencePromptTaskId=").append(experiencePromptTaskId);
        sb.append(", moderationModelId=").append(moderationModelId);
        sb.append(", moderationApiKeyName=").append(moderationApiKeyName);
        sb.append(", forwardToUser=").append(forwardToUser);
        sb.append(", messageWindowSize=").append(messageWindowSize);
        sb.append(", moderationParams=").append(moderationParams);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}