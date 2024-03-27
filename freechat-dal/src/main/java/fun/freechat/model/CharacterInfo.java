package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class CharacterInfo implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long characterId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String characterUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String parentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String nickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String avatar;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String picture;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String gender;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String lang;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer priority;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String profile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String greeting;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatStyle;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatExample;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getCharacterId() {
        return characterId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withCharacterId(Long characterId) {
        this.setCharacterId(characterId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getCharacterUid() {
        return characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withCharacterUid(String characterUid) {
        this.setCharacterUid(characterUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterUid(String characterUid) {
        this.characterUid = characterUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withGmtCreate(Date gmtCreate) {
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
    public CharacterInfo withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserId() {
        return userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getParentUid() {
        return parentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withParentUid(String parentUid) {
        this.setParentUid(parentUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getVisibility() {
        return visibility;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getName() {
        return name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withName(String name) {
        this.setName(name);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setName(String name) {
        this.name = name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getNickname() {
        return nickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withNickname(String nickname) {
        this.setNickname(nickname);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getAvatar() {
        return avatar;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withAvatar(String avatar) {
        this.setAvatar(avatar);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPicture() {
        return picture;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withPicture(String picture) {
        this.setPicture(picture);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getGender() {
        return gender;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withGender(String gender) {
        this.setGender(gender);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getLang() {
        return lang;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withLang(String lang) {
        this.setLang(lang);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setLang(String lang) {
        this.lang = lang;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getVersion() {
        return version;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withVersion(Integer version) {
        this.setVersion(version);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getPriority() {
        return priority;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withPriority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getDescription() {
        return description;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getProfile() {
        return profile;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withProfile(String profile) {
        this.setProfile(profile);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getGreeting() {
        return greeting;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withGreeting(String greeting) {
        this.setGreeting(greeting);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getChatStyle() {
        return chatStyle;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withChatStyle(String chatStyle) {
        this.setChatStyle(chatStyle);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setChatStyle(String chatStyle) {
        this.chatStyle = chatStyle;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getChatExample() {
        return chatExample;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withChatExample(String chatExample) {
        this.setChatExample(chatExample);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setChatExample(String chatExample) {
        this.chatExample = chatExample;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExt() {
        return ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExt(String ext) {
        this.ext = ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getDraft() {
        return draft;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public CharacterInfo withDraft(String draft) {
        this.setDraft(draft);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setDraft(String draft) {
        this.draft = draft;
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
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getLang() == null ? other.getLang() == null : this.getLang().equals(other.getLang()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getLang() == null) ? 0 : getLang().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        sb.append(", gender=").append(gender);
        sb.append(", lang=").append(lang);
        sb.append(", version=").append(version);
        sb.append(", priority=").append(priority);
        sb.append(", description=").append(description);
        sb.append(", profile=").append(profile);
        sb.append(", greeting=").append(greeting);
        sb.append(", chatStyle=").append(chatStyle);
        sb.append(", chatExample=").append(chatExample);
        sb.append(", ext=").append(ext);
        sb.append(", draft=").append(draft);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}