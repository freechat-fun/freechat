/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.15
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
import java.io.IOException;
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
 * Request data for updating plugin information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PluginUpdateDTO {
  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_MANIFEST_FORMAT = "manifestFormat";
  @SerializedName(SERIALIZED_NAME_MANIFEST_FORMAT)
  private String manifestFormat;

  public static final String SERIALIZED_NAME_MANIFEST_INFO = "manifestInfo";
  @SerializedName(SERIALIZED_NAME_MANIFEST_INFO)
  private String manifestInfo;

  public static final String SERIALIZED_NAME_API_FORMAT = "apiFormat";
  @SerializedName(SERIALIZED_NAME_API_FORMAT)
  private String apiFormat;

  public static final String SERIALIZED_NAME_API_INFO = "apiInfo";
  @SerializedName(SERIALIZED_NAME_API_INFO)
  private String apiInfo;

  public static final String SERIALIZED_NAME_PROVIDER = "provider";
  @SerializedName(SERIALIZED_NAME_PROVIDER)
  private String provider;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<String> aiModels;

  public PluginUpdateDTO() {
  }

  public PluginUpdateDTO visibility(String visibility) {
    this.visibility = visibility;
    return this;
  }

   /**
   * Visibility: private (default), public, public_org, hidden
   * @return visibility
  **/
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public PluginUpdateDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Plugin name
   * @return name
  **/
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public PluginUpdateDTO manifestFormat(String manifestFormat) {
    this.manifestFormat = manifestFormat;
    return this;
  }

   /**
   * Manifest format, currently supported: dash_scope (default), open_ai
   * @return manifestFormat
  **/
  @javax.annotation.Nullable
  public String getManifestFormat() {
    return manifestFormat;
  }

  public void setManifestFormat(String manifestFormat) {
    this.manifestFormat = manifestFormat;
  }


  public PluginUpdateDTO manifestInfo(String manifestInfo) {
    this.manifestInfo = manifestInfo;
    return this;
  }

   /**
   * Manifest content, can be full content or a URL
   * @return manifestInfo
  **/
  @javax.annotation.Nullable
  public String getManifestInfo() {
    return manifestInfo;
  }

  public void setManifestInfo(String manifestInfo) {
    this.manifestInfo = manifestInfo;
  }


  public PluginUpdateDTO apiFormat(String apiFormat) {
    this.apiFormat = apiFormat;
    return this;
  }

   /**
   * API description format, currently supported: openapi_v3 (default)
   * @return apiFormat
  **/
  @javax.annotation.Nullable
  public String getApiFormat() {
    return apiFormat;
  }

  public void setApiFormat(String apiFormat) {
    this.apiFormat = apiFormat;
  }


  public PluginUpdateDTO apiInfo(String apiInfo) {
    this.apiInfo = apiInfo;
    return this;
  }

   /**
   * API description content, can be full content or a URL
   * @return apiInfo
  **/
  @javax.annotation.Nullable
  public String getApiInfo() {
    return apiInfo;
  }

  public void setApiInfo(String apiInfo) {
    this.apiInfo = apiInfo;
  }


  public PluginUpdateDTO provider(String provider) {
    this.provider = provider;
    return this;
  }

   /**
   * Provider information, default is the current user&#39;s username
   * @return provider
  **/
  @javax.annotation.Nullable
  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }


  public PluginUpdateDTO ext(String ext) {
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


  public PluginUpdateDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PluginUpdateDTO addTagsItem(String tagsItem) {
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


  public PluginUpdateDTO aiModels(List<String> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PluginUpdateDTO addAiModelsItem(String aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

   /**
   * Supported model set, empty means no model limit
   * @return aiModels
  **/
  @javax.annotation.Nullable
  public List<String> getAiModels() {
    return aiModels;
  }

  public void setAiModels(List<String> aiModels) {
    this.aiModels = aiModels;
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
   * @return the PluginUpdateDTO instance itself
   */
  public PluginUpdateDTO putAdditionalProperty(String key, Object value) {
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
    PluginUpdateDTO pluginUpdateDTO = (PluginUpdateDTO) o;
    return Objects.equals(this.visibility, pluginUpdateDTO.visibility) &&
        Objects.equals(this.name, pluginUpdateDTO.name) &&
        Objects.equals(this.manifestFormat, pluginUpdateDTO.manifestFormat) &&
        Objects.equals(this.manifestInfo, pluginUpdateDTO.manifestInfo) &&
        Objects.equals(this.apiFormat, pluginUpdateDTO.apiFormat) &&
        Objects.equals(this.apiInfo, pluginUpdateDTO.apiInfo) &&
        Objects.equals(this.provider, pluginUpdateDTO.provider) &&
        Objects.equals(this.ext, pluginUpdateDTO.ext) &&
        Objects.equals(this.tags, pluginUpdateDTO.tags) &&
        Objects.equals(this.aiModels, pluginUpdateDTO.aiModels)&&
        Objects.equals(this.additionalProperties, pluginUpdateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(visibility, name, manifestFormat, manifestInfo, apiFormat, apiInfo, provider, ext, tags, aiModels, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginUpdateDTO {\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    manifestFormat: ").append(toIndentedString(manifestFormat)).append("\n");
    sb.append("    manifestInfo: ").append(toIndentedString(manifestInfo)).append("\n");
    sb.append("    apiFormat: ").append(toIndentedString(apiFormat)).append("\n");
    sb.append("    apiInfo: ").append(toIndentedString(apiInfo)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
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
    openapiFields.add("visibility");
    openapiFields.add("name");
    openapiFields.add("manifestFormat");
    openapiFields.add("manifestInfo");
    openapiFields.add("apiFormat");
    openapiFields.add("apiInfo");
    openapiFields.add("provider");
    openapiFields.add("ext");
    openapiFields.add("tags");
    openapiFields.add("aiModels");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to PluginUpdateDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PluginUpdateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PluginUpdateDTO is not found in the empty JSON string", PluginUpdateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : PluginUpdateDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("manifestFormat") != null && !jsonObj.get("manifestFormat").isJsonNull()) && !jsonObj.get("manifestFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestFormat").toString()));
      }
      if ((jsonObj.get("manifestInfo") != null && !jsonObj.get("manifestInfo").isJsonNull()) && !jsonObj.get("manifestInfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestInfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestInfo").toString()));
      }
      if ((jsonObj.get("apiFormat") != null && !jsonObj.get("apiFormat").isJsonNull()) && !jsonObj.get("apiFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiFormat").toString()));
      }
      if ((jsonObj.get("apiInfo") != null && !jsonObj.get("apiInfo").isJsonNull()) && !jsonObj.get("apiInfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiInfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiInfo").toString()));
      }
      if ((jsonObj.get("provider") != null && !jsonObj.get("provider").isJsonNull()) && !jsonObj.get("provider").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `provider` to be a primitive type in the JSON string but got `%s`", jsonObj.get("provider").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("aiModels") != null && !jsonObj.get("aiModels").isJsonNull() && !jsonObj.get("aiModels").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `aiModels` to be an array in the JSON string but got `%s`", jsonObj.get("aiModels").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PluginUpdateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PluginUpdateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PluginUpdateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PluginUpdateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<PluginUpdateDTO>() {
           @Override
           public void write(JsonWriter out, PluginUpdateDTO value) throws IOException {
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
           public PluginUpdateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PluginUpdateDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of PluginUpdateDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of PluginUpdateDTO
  * @throws IOException if the JSON string is invalid with respect to PluginUpdateDTO
  */
  public static PluginUpdateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PluginUpdateDTO.class);
  }

 /**
  * Convert an instance of PluginUpdateDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

