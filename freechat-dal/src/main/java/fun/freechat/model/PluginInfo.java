package fun.freechat.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.util.Date;

public class PluginInfo implements Serializable {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Long pluginId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String pluginUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Date gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String manifestFormat;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String apiFormat;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String provider;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String manifestInfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String apiInfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private static final long serialVersionUID = 1L;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Long getPluginId() {
        return pluginId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withPluginId(Long pluginId) {
        this.setPluginId(pluginId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPluginUid() {
        return pluginUid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withPluginUid(String pluginUid) {
        this.setPluginUid(pluginUid);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPluginUid(String pluginUid) {
        this.pluginUid = pluginUid == null ? null : pluginUid.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Date getGmtCreate() {
        return gmtCreate;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withGmtCreate(Date gmtCreate) {
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
    public PluginInfo withGmtModified(Date gmtModified) {
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
    public PluginInfo withUserId(String userId) {
        this.setUserId(userId);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getVisibility() {
        return visibility;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withVisibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setVisibility(String visibility) {
        this.visibility = visibility == null ? null : visibility.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getName() {
        return name;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withName(String name) {
        this.setName(name);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getManifestFormat() {
        return manifestFormat;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withManifestFormat(String manifestFormat) {
        this.setManifestFormat(manifestFormat);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setManifestFormat(String manifestFormat) {
        this.manifestFormat = manifestFormat == null ? null : manifestFormat.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getApiFormat() {
        return apiFormat;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withApiFormat(String apiFormat) {
        this.setApiFormat(apiFormat);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setApiFormat(String apiFormat) {
        this.apiFormat = apiFormat == null ? null : apiFormat.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getProvider() {
        return provider;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withProvider(String provider) {
        this.setProvider(provider);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setProvider(String provider) {
        this.provider = provider == null ? null : provider.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getManifestInfo() {
        return manifestInfo;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withManifestInfo(String manifestInfo) {
        this.setManifestInfo(manifestInfo);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setManifestInfo(String manifestInfo) {
        this.manifestInfo = manifestInfo == null ? null : manifestInfo.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getApiInfo() {
        return apiInfo;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withApiInfo(String apiInfo) {
        this.setApiInfo(apiInfo);
        return this;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setApiInfo(String apiInfo) {
        this.apiInfo = apiInfo == null ? null : apiInfo.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getExt() {
        return ext;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public PluginInfo withExt(String ext) {
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
        PluginInfo other = (PluginInfo) that;
        return (this.getPluginId() == null ? other.getPluginId() == null : this.getPluginId().equals(other.getPluginId()))
            && (this.getPluginUid() == null ? other.getPluginUid() == null : this.getPluginUid().equals(other.getPluginUid()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getManifestFormat() == null ? other.getManifestFormat() == null : this.getManifestFormat().equals(other.getManifestFormat()))
            && (this.getApiFormat() == null ? other.getApiFormat() == null : this.getApiFormat().equals(other.getApiFormat()));
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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
        return result;
    }

    @Override
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
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