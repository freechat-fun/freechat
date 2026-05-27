package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TgChat implements Serializable {
    private String chatId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String backendId;

    private Long tgChatId;

    private String chatType;

    private String title;

    private String ext;

    private static final long serialVersionUID = 1L;

    public String getChatId() {
        return chatId;
    }

    public TgChat withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public TgChat withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public TgChat withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getBackendId() {
        return backendId;
    }

    public TgChat withBackendId(String backendId) {
        this.setBackendId(backendId);
        return this;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public Long getTgChatId() {
        return tgChatId;
    }

    public TgChat withTgChatId(Long tgChatId) {
        this.setTgChatId(tgChatId);
        return this;
    }

    public void setTgChatId(Long tgChatId) {
        this.tgChatId = tgChatId;
    }

    public String getChatType() {
        return chatType;
    }

    public TgChat withChatType(String chatType) {
        this.setChatType(chatType);
        return this;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getTitle() {
        return title;
    }

    public TgChat withTitle(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExt() {
        return ext;
    }

    public TgChat withExt(String ext) {
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
        TgChat other = (TgChat) that;
        return (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getBackendId() == null ? other.getBackendId() == null : this.getBackendId().equals(other.getBackendId()))
            && (this.getTgChatId() == null ? other.getTgChatId() == null : this.getTgChatId().equals(other.getTgChatId()))
            && (this.getChatType() == null ? other.getChatType() == null : this.getChatType().equals(other.getChatType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getChatId() == null) ? 0 : getChatId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getBackendId() == null) ? 0 : getBackendId().hashCode());
        result = prime * result + ((getTgChatId() == null) ? 0 : getTgChatId().hashCode());
        result = prime * result + ((getChatType() == null) ? 0 : getChatType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
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
        sb.append(", backendId=").append(backendId);
        sb.append(", tgChatId=").append(tgChatId);
        sb.append(", chatType=").append(chatType);
        sb.append(", title=").append(title);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}