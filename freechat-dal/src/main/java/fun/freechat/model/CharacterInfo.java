package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CharacterInfo implements Serializable {
    private Long characterId;

    private String characterUid;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String userId;

    private String parentUid;

    private String visibility;

    private String name;

    private String nickname;

    private String avatar;

    private String picture;

    private String video;

    private String gender;

    private String lang;

    private Integer version;

    private Integer priority;

    private Long tgBotId;

    private String description;

    private String profile;

    private String greeting;

    private String chatStyle;

    private String chatExample;

    private String defaultScene;

    private String ext;

    private String draft;

    private static final long serialVersionUID = 1L;

    public Long getCharacterId() {
        return characterId;
    }

    public CharacterInfo withCharacterId(Long characterId) {
        this.setCharacterId(characterId);
        return this;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getCharacterUid() {
        return characterUid;
    }

    public CharacterInfo withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public CharacterInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public CharacterInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public CharacterInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public CharacterInfo withParentUid(String parentUid) {
        this.setParentUid(parentUid);
        return this;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getVisibility() {
        return visibility;
    }

    public CharacterInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public CharacterInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public CharacterInfo withNickname(String nickname) {
        this.setNickname(nickname);
        return this;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public CharacterInfo withAvatar(String avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPicture() {
        return picture;
    }

    public CharacterInfo withPicture(String picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getVideo() {
        return video;
    }

    public CharacterInfo withVideo(String video) {
        this.setVideo(video);
        return this;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getGender() {
        return gender;
    }

    public CharacterInfo withGender(String gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLang() {
        return lang;
    }

    public CharacterInfo withLang(String lang) {
        this.setLang(lang);
        return this;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getVersion() {
        return version;
    }

    public CharacterInfo withVersion(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public CharacterInfo withPriority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getTgBotId() {
        return tgBotId;
    }

    public CharacterInfo withTgBotId(Long tgBotId) {
        this.setTgBotId(tgBotId);
        return this;
    }

    public void setTgBotId(Long tgBotId) {
        this.tgBotId = tgBotId;
    }

    public String getDescription() {
        return description;
    }

    public CharacterInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile() {
        return profile;
    }

    public CharacterInfo withProfile(String profile) {
        this.setProfile(profile);
        return this;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getGreeting() {
        return greeting;
    }

    public CharacterInfo withGreeting(String greeting) {
        this.setGreeting(greeting);
        return this;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getChatStyle() {
        return chatStyle;
    }

    public CharacterInfo withChatStyle(String chatStyle) {
        this.setChatStyle(chatStyle);
        return this;
    }

    public void setChatStyle(String chatStyle) {
        this.chatStyle = chatStyle;
    }

    public String getChatExample() {
        return chatExample;
    }

    public CharacterInfo withChatExample(String chatExample) {
        this.setChatExample(chatExample);
        return this;
    }

    public void setChatExample(String chatExample) {
        this.chatExample = chatExample;
    }

    public String getDefaultScene() {
        return defaultScene;
    }

    public CharacterInfo withDefaultScene(String defaultScene) {
        this.setDefaultScene(defaultScene);
        return this;
    }

    public void setDefaultScene(String defaultScene) {
        this.defaultScene = defaultScene;
    }

    public String getExt() {
        return ext;
    }

    public CharacterInfo withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDraft() {
        return draft;
    }

    public CharacterInfo withDraft(String draft) {
        this.setDraft(draft);
        return this;
    }

    public void setDraft(String draft) {
        this.draft = draft;
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
        CharacterInfo other = (CharacterInfo) that;
        return (this.getCharacterId() == null ? other.getCharacterId() == null : this.getCharacterId().equals(other.getCharacterId()))
            && (this.getCharacterUid() == null ? other.getCharacterUid() == null : this.getCharacterUid().equals(other.getCharacterUid()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getParentUid() == null ? other.getParentUid() == null : this.getParentUid().equals(other.getParentUid()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getPicture() == null ? other.getPicture() == null : this.getPicture().equals(other.getPicture()))
            && (this.getVideo() == null ? other.getVideo() == null : this.getVideo().equals(other.getVideo()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getLang() == null ? other.getLang() == null : this.getLang().equals(other.getLang()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getTgBotId() == null ? other.getTgBotId() == null : this.getTgBotId().equals(other.getTgBotId()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getProfile() == null ? other.getProfile() == null : this.getProfile().equals(other.getProfile()))
            && (this.getGreeting() == null ? other.getGreeting() == null : this.getGreeting().equals(other.getGreeting()))
            && (this.getChatStyle() == null ? other.getChatStyle() == null : this.getChatStyle().equals(other.getChatStyle()))
            && (this.getChatExample() == null ? other.getChatExample() == null : this.getChatExample().equals(other.getChatExample()))
            && (this.getDefaultScene() == null ? other.getDefaultScene() == null : this.getDefaultScene().equals(other.getDefaultScene()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()))
            && (this.getDraft() == null ? other.getDraft() == null : this.getDraft().equals(other.getDraft()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCharacterId() == null) ? 0 : getCharacterId().hashCode());
        result = prime * result + ((getCharacterUid() == null) ? 0 : getCharacterUid().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getParentUid() == null) ? 0 : getParentUid().hashCode());
        result = prime * result + ((getVisibility() == null) ? 0 : getVisibility().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getPicture() == null) ? 0 : getPicture().hashCode());
        result = prime * result + ((getVideo() == null) ? 0 : getVideo().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getLang() == null) ? 0 : getLang().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getTgBotId() == null) ? 0 : getTgBotId().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getProfile() == null) ? 0 : getProfile().hashCode());
        result = prime * result + ((getGreeting() == null) ? 0 : getGreeting().hashCode());
        result = prime * result + ((getChatStyle() == null) ? 0 : getChatStyle().hashCode());
        result = prime * result + ((getChatExample() == null) ? 0 : getChatExample().hashCode());
        result = prime * result + ((getDefaultScene() == null) ? 0 : getDefaultScene().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        result = prime * result + ((getDraft() == null) ? 0 : getDraft().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", characterId=").append(characterId);
        sb.append(", characterUid=").append(characterUid);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", userId=").append(userId);
        sb.append(", parentUid=").append(parentUid);
        sb.append(", visibility=").append(visibility);
        sb.append(", name=").append(name);
        sb.append(", nickname=").append(nickname);
        sb.append(", avatar=").append(avatar);
        sb.append(", picture=").append(picture);
        sb.append(", video=").append(video);
        sb.append(", gender=").append(gender);
        sb.append(", lang=").append(lang);
        sb.append(", version=").append(version);
        sb.append(", priority=").append(priority);
        sb.append(", tgBotId=").append(tgBotId);
        sb.append(", description=").append(description);
        sb.append(", profile=").append(profile);
        sb.append(", greeting=").append(greeting);
        sb.append(", chatStyle=").append(chatStyle);
        sb.append(", chatExample=").append(chatExample);
        sb.append(", defaultScene=").append(defaultScene);
        sb.append(", ext=").append(ext);
        sb.append(", draft=").append(draft);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}