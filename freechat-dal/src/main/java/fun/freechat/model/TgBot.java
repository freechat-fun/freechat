package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TgBot implements Serializable {
    private String botId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long tgBotId;

    private String username;

    private String name;

    private String token;

    private String webhookUrl;

    private String webhookSecret;

    private Byte enabled;

    private String description;

    private String allowedUpdates;

    private String commands;

    private String ext;

    private static final long serialVersionUID = 1L;

    public String getBotId() {
        return botId;
    }

    public TgBot withBotId(String botId) {
        this.setBotId(botId);
        return this;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public TgBot withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public TgBot withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getTgBotId() {
        return tgBotId;
    }

    public TgBot withTgBotId(Long tgBotId) {
        this.setTgBotId(tgBotId);
        return this;
    }

    public void setTgBotId(Long tgBotId) {
        this.tgBotId = tgBotId;
    }

    public String getUsername() {
        return username;
    }

    public TgBot withUsername(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public TgBot withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public TgBot withToken(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public TgBot withWebhookUrl(String webhookUrl) {
        this.setWebhookUrl(webhookUrl);
        return this;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public TgBot withWebhookSecret(String webhookSecret) {
        this.setWebhookSecret(webhookSecret);
        return this;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public TgBot withEnabled(Byte enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public TgBot withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllowedUpdates() {
        return allowedUpdates;
    }

    public TgBot withAllowedUpdates(String allowedUpdates) {
        this.setAllowedUpdates(allowedUpdates);
        return this;
    }

    public void setAllowedUpdates(String allowedUpdates) {
        this.allowedUpdates = allowedUpdates;
    }

    public String getCommands() {
        return commands;
    }

    public TgBot withCommands(String commands) {
        this.setCommands(commands);
        return this;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getExt() {
        return ext;
    }

    public TgBot withExt(String ext) {
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
        TgBot other = (TgBot) that;
        return (this.getBotId() == null ? other.getBotId() == null : this.getBotId().equals(other.getBotId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getTgBotId() == null ? other.getTgBotId() == null : this.getTgBotId().equals(other.getTgBotId()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getToken() == null ? other.getToken() == null : this.getToken().equals(other.getToken()))
            && (this.getWebhookUrl() == null ? other.getWebhookUrl() == null : this.getWebhookUrl().equals(other.getWebhookUrl()))
            && (this.getWebhookSecret() == null ? other.getWebhookSecret() == null : this.getWebhookSecret().equals(other.getWebhookSecret()))
            && (this.getEnabled() == null ? other.getEnabled() == null : this.getEnabled().equals(other.getEnabled()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getAllowedUpdates() == null ? other.getAllowedUpdates() == null : this.getAllowedUpdates().equals(other.getAllowedUpdates()))
            && (this.getCommands() == null ? other.getCommands() == null : this.getCommands().equals(other.getCommands()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBotId() == null) ? 0 : getBotId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getTgBotId() == null) ? 0 : getTgBotId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
        result = prime * result + ((getWebhookUrl() == null) ? 0 : getWebhookUrl().hashCode());
        result = prime * result + ((getWebhookSecret() == null) ? 0 : getWebhookSecret().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getAllowedUpdates() == null) ? 0 : getAllowedUpdates().hashCode());
        result = prime * result + ((getCommands() == null) ? 0 : getCommands().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", botId=").append(botId);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", tgBotId=").append(tgBotId);
        sb.append(", username=").append(username);
        sb.append(", name=").append(name);
        sb.append(", token=").append(token);
        sb.append(", webhookUrl=").append(webhookUrl);
        sb.append(", webhookSecret=").append(webhookSecret);
        sb.append(", enabled=").append(enabled);
        sb.append(", description=").append(description);
        sb.append(", allowedUpdates=").append(allowedUpdates);
        sb.append(", commands=").append(commands);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}