package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TgMessage implements Serializable {
    private Long id;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String chatId;

    private String botId;

    private Long tgChatId;

    private Long tgMessageId;

    private Long tgUserId;

    private String direction;

    private String messageType;

    private Long replyToMessageId;

    private Byte enabled;

    private String content;

    private String payload;

    private String ext;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public TgMessage withId(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public TgMessage withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public TgMessage withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getChatId() {
        return chatId;
    }

    public TgMessage withChatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getBotId() {
        return botId;
    }

    public TgMessage withBotId(String botId) {
        this.setBotId(botId);
        return this;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public Long getTgChatId() {
        return tgChatId;
    }

    public TgMessage withTgChatId(Long tgChatId) {
        this.setTgChatId(tgChatId);
        return this;
    }

    public void setTgChatId(Long tgChatId) {
        this.tgChatId = tgChatId;
    }

    public Long getTgMessageId() {
        return tgMessageId;
    }

    public TgMessage withTgMessageId(Long tgMessageId) {
        this.setTgMessageId(tgMessageId);
        return this;
    }

    public void setTgMessageId(Long tgMessageId) {
        this.tgMessageId = tgMessageId;
    }

    public Long getTgUserId() {
        return tgUserId;
    }

    public TgMessage withTgUserId(Long tgUserId) {
        this.setTgUserId(tgUserId);
        return this;
    }

    public void setTgUserId(Long tgUserId) {
        this.tgUserId = tgUserId;
    }

    public String getDirection() {
        return direction;
    }

    public TgMessage withDirection(String direction) {
        this.setDirection(direction);
        return this;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getMessageType() {
        return messageType;
    }

    public TgMessage withMessageType(String messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public TgMessage withReplyToMessageId(Long replyToMessageId) {
        this.setReplyToMessageId(replyToMessageId);
        return this;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public TgMessage withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public String getContent() {
        return content;
    }

    public TgMessage withContent(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPayload() {
        return payload;
    }

    public TgMessage withPayload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getExt() {
        return ext;
    }

    public TgMessage withExt(String ext) {
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
        TgMessage other = (TgMessage) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getChatId() == null ? other.getChatId() == null : this.getChatId().equals(other.getChatId()))
            && (this.getBotId() == null ? other.getBotId() == null : this.getBotId().equals(other.getBotId()))
            && (this.getTgChatId() == null ? other.getTgChatId() == null : this.getTgChatId().equals(other.getTgChatId()))
            && (this.getTgMessageId() == null ? other.getTgMessageId() == null : this.getTgMessageId().equals(other.getTgMessageId()))
            && (this.getTgUserId() == null ? other.getTgUserId() == null : this.getTgUserId().equals(other.getTgUserId()))
            && (this.getDirection() == null ? other.getDirection() == null : this.getDirection().equals(other.getDirection()))
            && (this.getMessageType() == null ? other.getMessageType() == null : this.getMessageType().equals(other.getMessageType()))
            && (this.getReplyToMessageId() == null ? other.getReplyToMessageId() == null : this.getReplyToMessageId().equals(other.getReplyToMessageId()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getPayload() == null ? other.getPayload() == null : this.getPayload().equals(other.getPayload()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getChatId() == null) ? 0 : getChatId().hashCode());
        result = prime * result + ((getBotId() == null) ? 0 : getBotId().hashCode());
        result = prime * result + ((getTgChatId() == null) ? 0 : getTgChatId().hashCode());
        result = prime * result + ((getTgMessageId() == null) ? 0 : getTgMessageId().hashCode());
        result = prime * result + ((getTgUserId() == null) ? 0 : getTgUserId().hashCode());
        result = prime * result + ((getDirection() == null) ? 0 : getDirection().hashCode());
        result = prime * result + ((getMessageType() == null) ? 0 : getMessageType().hashCode());
        result = prime * result + ((getReplyToMessageId() == null) ? 0 : getReplyToMessageId().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getPayload() == null) ? 0 : getPayload().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", chatId=").append(chatId);
        sb.append(", botId=").append(botId);
        sb.append(", tgChatId=").append(tgChatId);
        sb.append(", tgMessageId=").append(tgMessageId);
        sb.append(", tgUserId=").append(tgUserId);
        sb.append(", direction=").append(direction);
        sb.append(", messageType=").append(messageType);
        sb.append(", replyToMessageId=").append(replyToMessageId);
        sb.append(", enabled=").append(enabled);
        sb.append(", content=").append(content);
        sb.append(", payload=").append(payload);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}