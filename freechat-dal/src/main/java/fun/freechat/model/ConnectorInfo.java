package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ConnectorInfo implements Serializable {
    private String connectorId;

    private String userId;

    private String revision;

    private String name;

    private String transport;

    private String url;

    private Integer timeout;

    private Byte active;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String headers;

    private String headerNames;

    private static final long serialVersionUID = 1L;

    public String getConnectorId() {
        return connectorId;
    }

    public ConnectorInfo withConnectorId(String connectorId) {
        this.setConnectorId(connectorId);
        return this;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getUserId() {
        return userId;
    }

    public ConnectorInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRevision() {
        return revision;
    }

    public ConnectorInfo withRevision(String revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getName() {
        return name;
    }

    public ConnectorInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransport() {
        return transport;
    }

    public ConnectorInfo withTransport(String transport) {
        this.setTransport(transport);
        return this;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getUrl() {
        return url;
    }

    public ConnectorInfo withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public ConnectorInfo withTimeout(Integer timeout) {
        this.setTimeout(timeout);
        return this;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Byte getActive() {
        return active;
    }

    public ConnectorInfo withActive(Byte active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Byte active) {
        this.active = active;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public ConnectorInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public ConnectorInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getHeaders() {
        return headers;
    }

    public ConnectorInfo withHeaders(String headers) {
        this.setHeaders(headers);
        return this;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getHeaderNames() {
        return headerNames;
    }

    public ConnectorInfo withHeaderNames(String headerNames) {
        this.setHeaderNames(headerNames);
        return this;
    }

    public void setHeaderNames(String headerNames) {
        this.headerNames = headerNames;
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
        ConnectorInfo other = (ConnectorInfo) that;
        return (this.getConnectorId() == null ? other.getConnectorId() == null : this.getConnectorId().equals(other.getConnectorId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getRevision() == null ? other.getRevision() == null : this.getRevision().equals(other.getRevision()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getTransport() == null ? other.getTransport() == null : this.getTransport().equals(other.getTransport()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getTimeout() == null ? other.getTimeout() == null : this.getTimeout().equals(other.getTimeout()))
            && (this.getActive() == null ? other.getActive() == null : this.getActive().equals(other.getActive()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getHeaders() == null ? other.getHeaders() == null : this.getHeaders().equals(other.getHeaders()))
            && (this.getHeaderNames() == null ? other.getHeaderNames() == null : this.getHeaderNames().equals(other.getHeaderNames()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getConnectorId() == null) ? 0 : getConnectorId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getTransport() == null) ? 0 : getTransport().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getTimeout() == null) ? 0 : getTimeout().hashCode());
        result = prime * result + ((getActive() == null) ? 0 : getActive().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getHeaders() == null) ? 0 : getHeaders().hashCode());
        result = prime * result + ((getHeaderNames() == null) ? 0 : getHeaderNames().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", connectorId=").append(connectorId);
        sb.append(", userId=").append(userId);
        sb.append(", revision=").append(revision);
        sb.append(", name=").append(name);
        sb.append(", transport=").append(transport);
        sb.append(", url=").append(url);
        sb.append(", timeout=").append(timeout);
        sb.append(", active=").append(active);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", headers=").append(headers);
        sb.append(", headerNames=").append(headerNames);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}