package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TgChat implements Serializable {
    private String chatId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtRead;

    private String botId;

    private Long tgChatId;

    private Long tgUserId;

    private String chatType;

    private String title;

    private Long lastMessageId;

    private Long messageCount;

    private Byte enabled;

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

    public LocalDateTime getGmtRead() {
        return gmtRead;
    }

    public TgChat withGmtRead(LocalDateTime gmtRead) {
        this.setGmtRead(gmtRead);
        return this;
    }

    public void setGmtRead(LocalDateTime gmtRead) {
        this.gmtRead = gmtRead;
    }

    public String getBotId() {
        return botId;
    }

    public TgChat withBotId(String botId) {
        this.setBotId(botId);
        return this;
    }

    public void setBotId(String botId) {
        this.botId = botId;
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

    public Long getTgUserId() {
        return tgUserId;
    }

    public TgChat withTgUserId(Long tgUserId) {
        this.setTgUserId(tgUserId);
        return this;
    }

    public void setTgUserId(Long tgUserId) {
        this.tgUserId = tgUserId;
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

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public TgChat withLastMessageId(Long lastMessageId) {
        this.setLastMessageId(lastMessageId);
        return this;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public TgChat withMessageCount(Long messageCount) {
        this.setMessageCount(messageCount);
        return this;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public TgChat withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
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
            && (this.getGmtRead() == null ? other.getGmtRead() == null : this.getGmtRead().equals(other.getGmtRead()))
            && (this.getBotId() == null ? other.getBotId() == null : this.getBotId().equals(other.getBotId()))
            && (this.getTgChatId() == null ? other.getTgChatId() == null : this.getTgChatId().equals(other.getTgChatId()))
            && (this.getTgUserId() == null ? other.getTgUserId() == null : this.getTgUserId().equals(other.getTgUserId()))
            && (this.getChatType() == null ? other.getChatType() == null : this.getChatType().equals(other.getChatType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getLastMessageId() == null ? other.getLastMessageId() == null : this.getLastMessageId().equals(other.getLastMessageId()))
            && (this.getMessageCount() == null ? other.getMessageCount() == null : this.getMessageCount().equals(other.getMessageCount()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
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
        result = prime * result + ((getBotId() == null) ? 0 : getBotId().hashCode());
        result = prime * result + ((getTgChatId() == null) ? 0 : getTgChatId().hashCode());
        result = prime * result + ((getTgUserId() == null) ? 0 : getTgUserId().hashCode());
        result = prime * result + ((getChatType() == null) ? 0 : getChatType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getLastMessageId() == null) ? 0 : getLastMessageId().hashCode());
        result = prime * result + ((getMessageCount() == null) ? 0 : getMessageCount().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
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
        sb.append(", botId=").append(botId);
        sb.append(", tgChatId=").append(tgChatId);
        sb.append(", tgUserId=").append(tgUserId);
        sb.append(", chatType=").append(chatType);
        sb.append(", title=").append(title);
        sb.append(", lastMessageId=").append(lastMessageId);
        sb.append(", messageCount=").append(messageCount);
        sb.append(", enabled=").append(enabled);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}