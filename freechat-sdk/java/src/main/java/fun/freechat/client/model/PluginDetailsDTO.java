/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.1.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fun.freechat.client.model.AiModelInfoDTO;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Plugin detailed content
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PluginDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_PLUGIN_ID = "pluginId";
  @SerializedName(SERIALIZED_NAME_PLUGIN_ID)
  private String pluginId;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_PROVIDER = "provider";
  @SerializedName(SERIALIZED_NAME_PROVIDER)
  private String provider;

  public static final String SERIALIZED_NAME_MANIFEST_FORMAT = "manifestFormat";
  @SerializedName(SERIALIZED_NAME_MANIFEST_FORMAT)
  private String manifestFormat;

  public static final String SERIALIZED_NAME_API_FORMAT = "apiFormat";
  @SerializedName(SERIALIZED_NAME_API_FORMAT)
  private String apiFormat;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<AiModelInfoDTO> aiModels;

  public static final String SERIALIZED_NAME_MANIFEST_INFO = "manifestInfo";
  @SerializedName(SERIALIZED_NAME_MANIFEST_INFO)
  private String manifestInfo;

  public static final String SERIALIZED_NAME_API_INFO = "apiInfo";
  @SerializedName(SERIALIZED_NAME_API_INFO)
  private String apiInfo;

  public static final String SERIALIZED_NAME_TOOL_SPEC_FORMAT = "toolSpecFormat";
  @SerializedName(SERIALIZED_NAME_TOOL_SPEC_FORMAT)
  private String toolSpecFormat;

  public static final String SERIALIZED_NAME_TOOL_SPEC_INFO = "toolSpecInfo";
  @SerializedName(SERIALIZED_NAME_TOOL_SPEC_INFO)
  private String toolSpecInfo;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public PluginDetailsDTO() {
  }

  public PluginDetailsDTO requestId(String requestId) {
    
    this.requestId = requestId;
    return this;
  }

   /**
   * Request identifier
   * @return requestId
  **/
  @javax.annotation.Nullable
  public String getRequestId() {
    return requestId;
  }


  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }


  public PluginDetailsDTO pluginId(String pluginId) {
    
    this.pluginId = pluginId;
    return this;
  }

   /**
   * Plugin identifier
   * @return pluginId
  **/
  @javax.annotation.Nullable
  public String getPluginId() {
    return pluginId;
  }


  public void setPluginId(String pluginId) {
    this.pluginId = pluginId;
  }


  public PluginDetailsDTO gmtCreate(OffsetDateTime gmtCreate) {
    
    this.gmtCreate = gmtCreate;
    return this;
  }

   /**
   * Creation time
   * @return gmtCreate
  **/
  @javax.annotation.Nullable
  public OffsetDateTime getGmtCreate() {
    return gmtCreate;
  }


  public void setGmtCreate(OffsetDateTime gmtCreate) {
    this.gmtCreate = gmtCreate;
  }


  public PluginDetailsDTO gmtModified(OffsetDateTime gmtModified) {
    
    this.gmtModified = gmtModified;
    return this;
  }

   /**
   * Modification time
   * @return gmtModified
  **/
  @javax.annotation.Nullable
  public OffsetDateTime getGmtModified() {
    return gmtModified;
  }


  public void setGmtModified(OffsetDateTime gmtModified) {
    this.gmtModified = gmtModified;
  }


  public PluginDetailsDTO visibility(String visibility) {
    
    this.visibility = visibility;
    return this;
  }

   /**
   * Visibility: private, public, public_org, hidden
   * @return visibility
  **/
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }


  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public PluginDetailsDTO name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Plugin name
   * @return name
  **/
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public PluginDetailsDTO provider(String provider) {
    
    this.provider = provider;
    return this;
  }

   /**
   * Information of the provider
   * @return provider
  **/
  @javax.annotation.Nullable
  public String getProvider() {
    return provider;
  }


  public void setProvider(String provider) {
    this.provider = provider;
  }


  public PluginDetailsDTO manifestFormat(String manifestFormat) {
    
    this.manifestFormat = manifestFormat;
    return this;
  }

   /**
   * Manifest format, currently supported: dash_scope, open_ai
   * @return manifestFormat
  **/
  @javax.annotation.Nullable
  public String getManifestFormat() {
    return manifestFormat;
  }


  public void setManifestFormat(String manifestFormat) {
    this.manifestFormat = manifestFormat;
  }


  public PluginDetailsDTO apiFormat(String apiFormat) {
    
    this.apiFormat = apiFormat;
    return this;
  }

   /**
   * API description format, currently supported: openapi_v3
   * @return apiFormat
  **/
  @javax.annotation.Nullable
  public String getApiFormat() {
    return apiFormat;
  }


  public void setApiFormat(String apiFormat) {
    this.apiFormat = apiFormat;
  }


  public PluginDetailsDTO username(String username) {
    
    this.username = username;
    return this;
  }

   /**
   * Plugin owner
   * @return username
  **/
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public PluginDetailsDTO tags(List<String> tags) {
    
    this.tags = tags;
    return this;
  }

  public PluginDetailsDTO addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

   /**
   * Tag set
   * @return tags
  **/
  @javax.annotation.Nullable
  public List<String> getTags() {
    return tags;
  }


  public void setTags(List<String> tags) {
    this.tags = tags;
  }


  public PluginDetailsDTO aiModels(List<AiModelInfoDTO> aiModels) {
    
    this.aiModels = aiModels;
    return this;
  }

  public PluginDetailsDTO addAiModelsItem(AiModelInfoDTO aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

   /**
   * Supported model set
   * @return aiModels
  **/
  @javax.annotation.Nullable
  public List<AiModelInfoDTO> getAiModels() {
    return aiModels;
  }


  public void setAiModels(List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
  }


  public PluginDetailsDTO manifestInfo(String manifestInfo) {
    
    this.manifestInfo = manifestInfo;
    return this;
  }

   /**
   * Manifest content, different formats may have differences
   * @return manifestInfo
  **/
  @javax.annotation.Nullable
  public String getManifestInfo() {
    return manifestInfo;
  }


  public void setManifestInfo(String manifestInfo) {
    this.manifestInfo = manifestInfo;
  }


  public PluginDetailsDTO apiInfo(String apiInfo) {
    
    this.apiInfo = apiInfo;
    return this;
  }

   /**
   * API description content, different formats may have content differences
   * @return apiInfo
  **/
  @javax.annotation.Nullable
  public String getApiInfo() {
    return apiInfo;
  }


  public void setApiInfo(String apiInfo) {
    this.apiInfo = apiInfo;
  }


  public PluginDetailsDTO toolSpecFormat(String toolSpecFormat) {
    
    this.toolSpecFormat = toolSpecFormat;
    return this;
  }

   /**
   * Tool specification format, currently supported: open_ai
   * @return toolSpecFormat
  **/
  @javax.annotation.Nullable
  public String getToolSpecFormat() {
    return toolSpecFormat;
  }


  public void setToolSpecFormat(String toolSpecFormat) {
    this.toolSpecFormat = toolSpecFormat;
  }


  public PluginDetailsDTO toolSpecInfo(String toolSpecInfo) {
    
    this.toolSpecInfo = toolSpecInfo;
    return this;
  }

   /**
   * Tool specification content
   * @return toolSpecInfo
  **/
  @javax.annotation.Nullable
  public String getToolSpecInfo() {
    return toolSpecInfo;
  }


  public void setToolSpecInfo(String toolSpecInfo) {
    this.toolSpecInfo = toolSpecInfo;
  }


  public PluginDetailsDTO ext(String ext) {
    
    this.ext = ext;
    return this;
  }

   /**
   * Additional information, JSON format
   * @return ext
  **/
  @javax.annotation.Nullable
  public String getExt() {
    return ext;
  }


  public void setExt(String ext) {
    this.ext = ext;
  }

  /**
   * A container for additional, undeclared properties.
   * This is a holder for any undeclared properties as specified with
   * the 'additionalProperties' keyword in the OAS document.
   */
  private Map<String, Object> additionalProperties;

  /**
   * Set the additional (undeclared) property with the specified name and value.
   * If the property does not already exist, create it otherwise replace it.
   *
   * @param key name of the property
   * @param value value of the property
   * @return the PluginDetailsDTO instance itself
   */
  public PluginDetailsDTO putAdditionalProperty(String key, Object value) {
    if (this.additionalProperties == null) {
        this.additionalProperties = new HashMap<String, Object>();
    }
    this.additionalProperties.put(key, value);
    return this;
  }

  /**
   * Return the additional (undeclared) property.
   *
   * @return a map of objects
   */
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Return the additional (undeclared) property with the specified name.
   *
   * @param key name of the property
   * @return an object
   */
  public Object getAdditionalProperty(String key) {
    if (this.additionalProperties == null) {
        return null;
    }
    return this.additionalProperties.get(key);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PluginDetailsDTO pluginDetailsDTO = (PluginDetailsDTO) o;
    return Objects.equals(this.requestId, pluginDetailsDTO.requestId) &&
        Objects.equals(this.pluginId, pluginDetailsDTO.pluginId) &&
        Objects.equals(this.gmtCreate, pluginDetailsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, pluginDetailsDTO.gmtModified) &&
        Objects.equals(this.visibility, pluginDetailsDTO.visibility) &&
        Objects.equals(this.name, pluginDetailsDTO.name) &&
        Objects.equals(this.provider, pluginDetailsDTO.provider) &&
        Objects.equals(this.manifestFormat, pluginDetailsDTO.manifestFormat) &&
        Objects.equals(this.apiFormat, pluginDetailsDTO.apiFormat) &&
        Objects.equals(this.username, pluginDetailsDTO.username) &&
        Objects.equals(this.tags, pluginDetailsDTO.tags) &&
        Objects.equals(this.aiModels, pluginDetailsDTO.aiModels) &&
        Objects.equals(this.manifestInfo, pluginDetailsDTO.manifestInfo) &&
        Objects.equals(this.apiInfo, pluginDetailsDTO.apiInfo) &&
        Objects.equals(this.toolSpecFormat, pluginDetailsDTO.toolSpecFormat) &&
        Objects.equals(this.toolSpecInfo, pluginDetailsDTO.toolSpecInfo) &&
        Objects.equals(this.ext, pluginDetailsDTO.ext)&&
        Objects.equals(this.additionalProperties, pluginDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, pluginId, gmtCreate, gmtModified, visibility, name, provider, manifestFormat, apiFormat, username, tags, aiModels, manifestInfo, apiInfo, toolSpecFormat, toolSpecInfo, ext, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    pluginId: ").append(toIndentedString(pluginId)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    manifestFormat: ").append(toIndentedString(manifestFormat)).append("\n");
    sb.append("    apiFormat: ").append(toIndentedString(apiFormat)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
    sb.append("    manifestInfo: ").append(toIndentedString(manifestInfo)).append("\n");
    sb.append("    apiInfo: ").append(toIndentedString(apiInfo)).append("\n");
    sb.append("    toolSpecFormat: ").append(toIndentedString(toolSpecFormat)).append("\n");
    sb.append("    toolSpecInfo: ").append(toIndentedString(toolSpecInfo)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("requestId");
    openapiFields.add("pluginId");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("visibility");
    openapiFields.add("name");
    openapiFields.add("provider");
    openapiFields.add("manifestFormat");
    openapiFields.add("apiFormat");
    openapiFields.add("username");
    openapiFields.add("tags");
    openapiFields.add("aiModels");
    openapiFields.add("manifestInfo");
    openapiFields.add("apiInfo");
    openapiFields.add("toolSpecFormat");
    openapiFields.add("toolSpecInfo");
    openapiFields.add("ext");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to PluginDetailsDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PluginDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PluginDetailsDTO is not found in the empty JSON string", PluginDetailsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("pluginId") != null && !jsonObj.get("pluginId").isJsonNull()) && !jsonObj.get("pluginId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `pluginId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("pluginId").toString()));
      }
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("provider") != null && !jsonObj.get("provider").isJsonNull()) && !jsonObj.get("provider").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `provider` to be a primitive type in the JSON string but got `%s`", jsonObj.get("provider").toString()));
      }
      if ((jsonObj.get("manifestFormat") != null && !jsonObj.get("manifestFormat").isJsonNull()) && !jsonObj.get("manifestFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestFormat").toString()));
      }
      if ((jsonObj.get("apiFormat") != null && !jsonObj.get("apiFormat").isJsonNull()) && !jsonObj.get("apiFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiFormat").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
      if (jsonObj.get("aiModels") != null && !jsonObj.get("aiModels").isJsonNull()) {
        JsonArray jsonArrayaiModels = jsonObj.getAsJsonArray("aiModels");
        if (jsonArrayaiModels != null) {
          // ensure the json data is an array
          if (!jsonObj.get("aiModels").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `aiModels` to be an array in the JSON string but got `%s`", jsonObj.get("aiModels").toString()));
          }

          // validate the optional field `aiModels` (array)
          for (int i = 0; i < jsonArrayaiModels.size(); i++) {
            AiModelInfoDTO.validateJsonElement(jsonArrayaiModels.get(i));
          };
        }
      }
      if ((jsonObj.get("manifestInfo") != null && !jsonObj.get("manifestInfo").isJsonNull()) && !jsonObj.get("manifestInfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestInfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestInfo").toString()));
      }
      if ((jsonObj.get("apiInfo") != null && !jsonObj.get("apiInfo").isJsonNull()) && !jsonObj.get("apiInfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiInfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiInfo").toString()));
      }
      if ((jsonObj.get("toolSpecFormat") != null && !jsonObj.get("toolSpecFormat").isJsonNull()) && !jsonObj.get("toolSpecFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `toolSpecFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("toolSpecFormat").toString()));
      }
      if ((jsonObj.get("toolSpecInfo") != null && !jsonObj.get("toolSpecInfo").isJsonNull()) && !jsonObj.get("toolSpecInfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `toolSpecInfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("toolSpecInfo").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PluginDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PluginDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PluginDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PluginDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<PluginDetailsDTO>() {
           @Override
           public void write(JsonWriter out, PluginDetailsDTO value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             obj.remove("additionalProperties");
             // serialize additional properties
             if (value.getAdditionalProperties() != null) {
               for (Map.Entry<String, Object> entry : value.getAdditionalProperties().entrySet()) {
                 if (entry.getValue() instanceof String)
                   obj.addProperty(entry.getKey(), (String) entry.getValue());
                 else if (entry.getValue() instanceof Number)
                   obj.addProperty(entry.getKey(), (Number) entry.getValue());
                 else if (entry.getValue() instanceof Boolean)
                   obj.addProperty(entry.getKey(), (Boolean) entry.getValue());
                 else if (entry.getValue() instanceof Character)
                   obj.addProperty(entry.getKey(), (Character) entry.getValue());
                 else {
                   obj.add(entry.getKey(), gson.toJsonTree(entry.getValue()).getAsJsonObject());
                 }
               }
             }
             elementAdapter.write(out, obj);
           }

           @Override
           public PluginDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PluginDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
             for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
               if (!openapiFields.contains(entry.getKey())) {
                 if (entry.getValue().isJsonPrimitive()) { // primitive type
                   if (entry.getValue().getAsJsonPrimitive().isString())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsString());
                   else if (entry.getValue().getAsJsonPrimitive().isNumber())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsNumber());
                   else if (entry.getValue().getAsJsonPrimitive().isBoolean())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsBoolean());
                   else
                     throw new IllegalArgumentException(String.format("The field `%s` has unknown primitive type. Value: %s", entry.getKey(), entry.getValue().toString()));
                 } else if (entry.getValue().isJsonArray()) {
                     instance.putAdditionalProperty(entry.getKey(), gson.fromJson(entry.getValue(), List.class));
                 } else { // JSON object
                     instance.putAdditionalProperty(entry.getKey(), gson.fromJson(entry.getValue(), HashMap.class));
                 }
               }
             }
             return instance;
           }

       }.nullSafe();
    }
  }

 /**
  * Create an instance of PluginDetailsDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of PluginDetailsDTO
  * @throws IOException if the JSON string is invalid with respect to PluginDetailsDTO
  */
  public static PluginDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PluginDetailsDTO.class);
  }

 /**
  * Convert an instance of PluginDetailsDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

