package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class ChatContext implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtRead;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String chatType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userNickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String backendId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String characterNickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userProfile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String about;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getChatId() {
        return chatId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setChatId(String chatId) {
        this.chatId = chatId == null ? null : chatId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withGmtCreate(Date gmtCreate) {
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
    public ChatContext withGmtModified(Date gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtRead() {
        return gmtRead;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withGmtRead(Date gmtRead) {
        this.setGmtRead(gmtRead);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setGmtRead(Date gmtRead) {
        this.gmtRead = gmtRead;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getChatType() {
        return chatType;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withChatType(String chatType) {
        this.setChatType(chatType);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setChatType(String chatType) {
        this.chatType = chatType == null ? null : chatType.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserId() {
        return userId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserNickname() {
        return userNickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withUserNickname(String userNickname) {
        this.setUserNickname(userNickname);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname == null ? null : userNickname.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getBackendId() {
        return backendId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withBackendId(String backendId) {
        this.setBackendId(backendId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setBackendId(String backendId) {
        this.backendId = backendId == null ? null : backendId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getCharacterNickname() {
        return characterNickname;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withCharacterNickname(String characterNickname) {
        this.setCharacterNickname(characterNickname);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setCharacterNickname(String characterNickname) {
        this.characterNickname = characterNickname == null ? null : characterNickname.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUserProfile() {
        return userProfile;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withUserProfile(String userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile == null ? null : userProfile.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getAbout() {
        return about;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withAbout(String about) {
        this.setAbout(about);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setAbout(String about) {
        this.about = about == null ? null : about.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExt() {
        return ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public ChatContext withExt(String ext) {
        this.setExt(ext);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setExt(String ext) {
        this.ext = ext == null ? null : ext.trim();
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
        ChatContext other = (ChatContext) that;
        return (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getGmtRead() == null ? other.getGmtRead() == null : this.getGmtRead().equals(other.getGmtRead()))
            && (this.getChatType() == null ? other.getChatType() == null : this.getChatType().equals(other.getChatType()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserNickname() == null ? other.getUserNickname() == null : this.getUserNickname().equals(other.getUserNickname()))
            && (this.getBackendId() == null ? other.getBackendId() == null : this.getBackendId().equals(other.getBackendId()))
            && (this.getCharacterNickname() == null ? other.getCharacterNickname() == null : this.getCharacterNickname().equals(other.getCharacterNickname()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        sb.append(", userProfile=").append(userProfile);
        sb.append(", about=").append(about);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}