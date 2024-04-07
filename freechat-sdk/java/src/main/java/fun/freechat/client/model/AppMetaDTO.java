/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.7.0
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
import java.util.Arrays;

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
 * Application metadata
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AppMetaDTO {
  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_VERSION = "version";
  @SerializedName(SERIALIZED_NAME_VERSION)
  private String version;

  public static final String SERIALIZED_NAME_BUILD_TIMESTAMP = "buildTimestamp";
  @SerializedName(SERIALIZED_NAME_BUILD_TIMESTAMP)
  private String buildTimestamp;

  public static final String SERIALIZED_NAME_BUILD_NUMBER = "buildNumber";
  @SerializedName(SERIALIZED_NAME_BUILD_NUMBER)
  private String buildNumber;

  public static final String SERIALIZED_NAME_COMMIT_URL = "commitUrl";
  @SerializedName(SERIALIZED_NAME_COMMIT_URL)
  private String commitUrl;

  public static final String SERIALIZED_NAME_RELEASE_NOTE_URL = "releaseNoteUrl";
  @SerializedName(SERIALIZED_NAME_RELEASE_NOTE_URL)
  private String releaseNoteUrl;

  public static final String SERIALIZED_NAME_RUNNING_ENV = "runningEnv";
  @SerializedName(SERIALIZED_NAME_RUNNING_ENV)
  private String runningEnv;

  public AppMetaDTO() {
  }

  public AppMetaDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Application name
   * @return name
  **/
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public AppMetaDTO version(String version) {
    this.version = version;
    return this;
  }

   /**
   * Application version
   * @return version
  **/
  @javax.annotation.Nullable
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }


  public AppMetaDTO buildTimestamp(String buildTimestamp) {
    this.buildTimestamp = buildTimestamp;
    return this;
  }

   /**
   * Build time
   * @return buildTimestamp
  **/
  @javax.annotation.Nullable
  public String getBuildTimestamp() {
    return buildTimestamp;
  }

  public void setBuildTimestamp(String buildTimestamp) {
    this.buildTimestamp = buildTimestamp;
  }


  public AppMetaDTO buildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
    return this;
  }

   /**
   * Build number
   * @return buildNumber
  **/
  @javax.annotation.Nullable
  public String getBuildNumber() {
    return buildNumber;
  }

  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }


  public AppMetaDTO commitUrl(String commitUrl) {
    this.commitUrl = commitUrl;
    return this;
  }

   /**
   * Commit URL
   * @return commitUrl
  **/
  @javax.annotation.Nullable
  public String getCommitUrl() {
    return commitUrl;
  }

  public void setCommitUrl(String commitUrl) {
    this.commitUrl = commitUrl;
  }


  public AppMetaDTO releaseNoteUrl(String releaseNoteUrl) {
    this.releaseNoteUrl = releaseNoteUrl;
    return this;
  }

   /**
   * Release notes
   * @return releaseNoteUrl
  **/
  @javax.annotation.Nullable
  public String getReleaseNoteUrl() {
    return releaseNoteUrl;
  }

  public void setReleaseNoteUrl(String releaseNoteUrl) {
    this.releaseNoteUrl = releaseNoteUrl;
  }


  public AppMetaDTO runningEnv(String runningEnv) {
    this.runningEnv = runningEnv;
    return this;
  }

   /**
   * Running environment
   * @return runningEnv
  **/
  @javax.annotation.Nullable
  public String getRunningEnv() {
    return runningEnv;
  }

  public void setRunningEnv(String runningEnv) {
    this.runningEnv = runningEnv;
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
   * @return the AppMetaDTO instance itself
   */
  public AppMetaDTO putAdditionalProperty(String key, Object value) {
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
    AppMetaDTO appMetaDTO = (AppMetaDTO) o;
    return Objects.equals(this.name, appMetaDTO.name) &&
        Objects.equals(this.version, appMetaDTO.version) &&
        Objects.equals(this.buildTimestamp, appMetaDTO.buildTimestamp) &&
        Objects.equals(this.buildNumber, appMetaDTO.buildNumber) &&
        Objects.equals(this.commitUrl, appMetaDTO.commitUrl) &&
        Objects.equals(this.releaseNoteUrl, appMetaDTO.releaseNoteUrl) &&
        Objects.equals(this.runningEnv, appMetaDTO.runningEnv)&&
        Objects.equals(this.additionalProperties, appMetaDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version, buildTimestamp, buildNumber, commitUrl, releaseNoteUrl, runningEnv, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppMetaDTO {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    buildTimestamp: ").append(toIndentedString(buildTimestamp)).append("\n");
    sb.append("    buildNumber: ").append(toIndentedString(buildNumber)).append("\n");
    sb.append("    commitUrl: ").append(toIndentedString(commitUrl)).append("\n");
    sb.append("    releaseNoteUrl: ").append(toIndentedString(releaseNoteUrl)).append("\n");
    sb.append("    runningEnv: ").append(toIndentedString(runningEnv)).append("\n");
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
    openapiFields.add("name");
    openapiFields.add("version");
    openapiFields.add("buildTimestamp");
    openapiFields.add("buildNumber");
    openapiFields.add("commitUrl");
    openapiFields.add("releaseNoteUrl");
    openapiFields.add("runningEnv");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to AppMetaDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!AppMetaDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in AppMetaDTO is not found in the empty JSON string", AppMetaDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("version") != null && !jsonObj.get("version").isJsonNull()) && !jsonObj.get("version").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `version` to be a primitive type in the JSON string but got `%s`", jsonObj.get("version").toString()));
      }
      if ((jsonObj.get("buildTimestamp") != null && !jsonObj.get("buildTimestamp").isJsonNull()) && !jsonObj.get("buildTimestamp").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `buildTimestamp` to be a primitive type in the JSON string but got `%s`", jsonObj.get("buildTimestamp").toString()));
      }
      if ((jsonObj.get("buildNumber") != null && !jsonObj.get("buildNumber").isJsonNull()) && !jsonObj.get("buildNumber").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `buildNumber` to be a primitive type in the JSON string but got `%s`", jsonObj.get("buildNumber").toString()));
      }
      if ((jsonObj.get("commitUrl") != null && !jsonObj.get("commitUrl").isJsonNull()) && !jsonObj.get("commitUrl").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `commitUrl` to be a primitive type in the JSON string but got `%s`", jsonObj.get("commitUrl").toString()));
      }
      if ((jsonObj.get("releaseNoteUrl") != null && !jsonObj.get("releaseNoteUrl").isJsonNull()) && !jsonObj.get("releaseNoteUrl").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `releaseNoteUrl` to be a primitive type in the JSON string but got `%s`", jsonObj.get("releaseNoteUrl").toString()));
      }
      if ((jsonObj.get("runningEnv") != null && !jsonObj.get("runningEnv").isJsonNull()) && !jsonObj.get("runningEnv").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `runningEnv` to be a primitive type in the JSON string but got `%s`", jsonObj.get("runningEnv").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!AppMetaDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'AppMetaDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<AppMetaDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(AppMetaDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<AppMetaDTO>() {
           @Override
           public void write(JsonWriter out, AppMetaDTO value) throws IOException {
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
                   JsonElement jsonElement = gson.toJsonTree(entry.getValue());
                   if (jsonElement.isJsonArray()) {
                     obj.add(entry.getKey(), jsonElement.getAsJsonArray());
                   } else {
                     obj.add(entry.getKey(), jsonElement.getAsJsonObject());
                   }
                 }
               }
             }
             elementAdapter.write(out, obj);
           }

           @Override
           public AppMetaDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             AppMetaDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of AppMetaDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of AppMetaDTO
  * @throws IOException if the JSON string is invalid with respect to AppMetaDTO
  */
  public static AppMetaDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, AppMetaDTO.class);
  }

 /**
  * Convert an instance of AppMetaDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

