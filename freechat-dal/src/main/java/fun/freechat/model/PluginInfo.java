package fun.freechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PluginInfo implements Serializable {
    private Long pluginId;

    private String pluginUid;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String userId;

    private String visibility;

    private String name;

    private String manifestFormat;

    private String apiFormat;

    private String provider;

    private String manifestInfo;

    private String apiInfo;

    private String ext;

    private static final long serialVersionUID = 1L;

    public Long getPluginId() {
        return pluginId;
    }

    public PluginInfo withPluginId(Long pluginId) {
        this.setPluginId(pluginId);
        return this;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginUid() {
        return pluginUid;
    }

    public PluginInfo withPluginUid(String pluginUid) {
        this.setPluginUid(pluginUid);
        return this;
    }

    public void setPluginUid(String pluginUid) {
        this.pluginUid = pluginUid;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public PluginInfo withGmtCreate(LocalDateTime gmtCreate) {
        this.setGmtCreate(gmtCreate);
        return this;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public PluginInfo withGmtModified(LocalDateTime gmtModified) {
        this.setGmtModified(gmtModified);
        return this;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public PluginInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVisibility() {
        return visibility;
    }

    public PluginInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public PluginInfo withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManifestFormat() {
        return manifestFormat;
    }

    public PluginInfo withManifestFormat(String manifestFormat) {
        this.setManifestFormat(manifestFormat);
        return this;
    }

    public void setManifestFormat(String manifestFormat) {
        this.manifestFormat = manifestFormat;
    }

    public String getApiFormat() {
        return apiFormat;
    }

    public PluginInfo withApiFormat(String apiFormat) {
        this.setApiFormat(apiFormat);
        return this;
    }

    public void setApiFormat(String apiFormat) {
        this.apiFormat = apiFormat;
    }

    public String getProvider() {
        return provider;
    }

    public PluginInfo withProvider(String provider) {
        this.setProvider(provider);
        return this;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getManifestInfo() {
        return manifestInfo;
    }

    public PluginInfo withManifestInfo(String manifestInfo) {
        this.setManifestInfo(manifestInfo);
        return this;
    }

    public void setManifestInfo(String manifestInfo) {
        this.manifestInfo = manifestInfo;
    }

    public String getApiInfo() {
        return apiInfo;
    }

    public PluginInfo withApiInfo(String apiInfo) {
        this.setApiInfo(apiInfo);
        return this;
    }

    public void setApiInfo(String apiInfo) {
        this.apiInfo = apiInfo;
    }

    public String getExt() {
        return ext;
    }

    public PluginInfo withExt(String ext) {
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
        PluginInfo other = (PluginInfo) that;
        return (this.getPluginId() == null ? other.getPluginId() == null : this.getPluginId().equals(other.getPluginId()))
            && (this.getPluginUid() == null ? other.getPluginUid() == null : this.getPluginUid().equals(other.getPluginUid()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getManifestFormat() == null ? other.getManifestFormat() == null : this.getManifestFormat().equals(other.getManifestFormat()))
            && (this.getApiFormat() == null ? other.getApiFormat() == null : this.getApiFormat().equals(other.getApiFormat()))
            && (this.getProvider() == null ? other.getProvider() == null : this.getProvider().equals(other.getProvider()))
            && (this.getManifestInfo() == null ? other.getManifestInfo() == null : this.getManifestInfo().equals(other.getManifestInfo()))
            && (this.getApiInfo() == null ? other.getApiInfo() == null : this.getApiInfo().equals(other.getApiInfo()))
            && (this.getExt() == null ? other.getExt() == null : this.getExt().equals(other.getExt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPluginId() == null) ? 0 : getPluginId().hashCode());
        result = prime * result + ((getPluginUid() == null) ? 0 : getPluginUid().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getVisibility() == null) ? 0 : getVisibility().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getManifestFormat() == null) ? 0 : getManifestFormat().hashCode());
        result = prime * result + ((getApiFormat() == null) ? 0 : getApiFormat().hashCode());
        result = prime * result + ((getProvider() == null) ? 0 : getProvider().hashCode());
        result = prime * result + ((getManifestInfo() == null) ? 0 : getManifestInfo().hashCode());
        result = prime * result + ((getApiInfo() == null) ? 0 : getApiInfo().hashCode());
        result = prime * result + ((getExt() == null) ? 0 : getExt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", pluginId=").append(pluginId);
        sb.append(", pluginUid=").append(pluginUid);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", userId=").append(userId);
        sb.append(", visibility=").append(visibility);
        sb.append(", name=").append(name);
        sb.append(", manifestFormat=").append(manifestFormat);
        sb.append(", apiFormat=").append(apiFormat);
        sb.append(", provider=").append(provider);
        sb.append(", manifestInfo=").append(manifestInfo);
        sb.append(", apiInfo=").append(apiInfo);
        sb.append(", ext=").append(ext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}