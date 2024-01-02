/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.6
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
import java.time.OffsetDateTime;
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
 * API token information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ApiTokenInfoDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  private Long id;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  private String type;

  public static final String SERIALIZED_NAME_ISSUED_AT = "issuedAt";
  @SerializedName(SERIALIZED_NAME_ISSUED_AT)
  private OffsetDateTime issuedAt;

  public static final String SERIALIZED_NAME_EXPIRES_AT = "expiresAt";
  @SerializedName(SERIALIZED_NAME_EXPIRES_AT)
  private OffsetDateTime expiresAt;

  public static final String SERIALIZED_NAME_TOKEN = "token";
  @SerializedName(SERIALIZED_NAME_TOKEN)
  private String token;

  public static final String SERIALIZED_NAME_POLICY = "policy";
  @SerializedName(SERIALIZED_NAME_POLICY)
  private String policy;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  private String username;

  public ApiTokenInfoDTO() {
  }

  public ApiTokenInfoDTO requestId(String requestId) {
    
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


  public ApiTokenInfoDTO id(Long id) {
    
    this.id = id;
    return this;
  }

   /**
   * Token identifier
   * @return id
  **/
  @javax.annotation.Nullable
  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public ApiTokenInfoDTO gmtCreate(OffsetDateTime gmtCreate) {
    
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


  public ApiTokenInfoDTO gmtModified(OffsetDateTime gmtModified) {
    
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


  public ApiTokenInfoDTO type(String type) {
    
    this.type = type;
    return this;
  }

   /**
   * Token type
   * @return type
  **/
  @javax.annotation.Nullable
  public String getType() {
    return type;
  }


  public void setType(String type) {
    this.type = type;
  }


  public ApiTokenInfoDTO issuedAt(OffsetDateTime issuedAt) {
    
    this.issuedAt = issuedAt;
    return this;
  }

   /**
   * Token issuance time
   * @return issuedAt
  **/
  @javax.annotation.Nullable
  public OffsetDateTime getIssuedAt() {
    return issuedAt;
  }


  public void setIssuedAt(OffsetDateTime issuedAt) {
    this.issuedAt = issuedAt;
  }


  public ApiTokenInfoDTO expiresAt(OffsetDateTime expiresAt) {
    
    this.expiresAt = expiresAt;
    return this;
  }

   /**
   * Token expiration time
   * @return expiresAt
  **/
  @javax.annotation.Nullable
  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }


  public void setExpiresAt(OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }


  public ApiTokenInfoDTO token(String token) {
    
    this.token = token;
    return this;
  }

   /**
   * Token content
   * @return token
  **/
  @javax.annotation.Nullable
  public String getToken() {
    return token;
  }


  public void setToken(String token) {
    this.token = token;
  }


  public ApiTokenInfoDTO policy(String policy) {
    
    this.policy = policy;
    return this;
  }

   /**
   * Token policy
   * @return policy
  **/
  @javax.annotation.Nullable
  public String getPolicy() {
    return policy;
  }


  public void setPolicy(String policy) {
    this.policy = policy;
  }


  public ApiTokenInfoDTO username(String username) {
    
    this.username = username;
    return this;
  }

   /**
   * Token owner
   * @return username
  **/
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
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
   * @return the ApiTokenInfoDTO instance itself
   */
  public ApiTokenInfoDTO putAdditionalProperty(String key, Object value) {
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
    ApiTokenInfoDTO apiTokenInfoDTO = (ApiTokenInfoDTO) o;
    return Objects.equals(this.requestId, apiTokenInfoDTO.requestId) &&
        Objects.equals(this.id, apiTokenInfoDTO.id) &&
        Objects.equals(this.gmtCreate, apiTokenInfoDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, apiTokenInfoDTO.gmtModified) &&
        Objects.equals(this.type, apiTokenInfoDTO.type) &&
        Objects.equals(this.issuedAt, apiTokenInfoDTO.issuedAt) &&
        Objects.equals(this.expiresAt, apiTokenInfoDTO.expiresAt) &&
        Objects.equals(this.token, apiTokenInfoDTO.token) &&
        Objects.equals(this.policy, apiTokenInfoDTO.policy) &&
        Objects.equals(this.username, apiTokenInfoDTO.username)&&
        Objects.equals(this.additionalProperties, apiTokenInfoDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, id, gmtCreate, gmtModified, type, issuedAt, expiresAt, token, policy, username, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiTokenInfoDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    issuedAt: ").append(toIndentedString(issuedAt)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    policy: ").append(toIndentedString(policy)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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
    openapiFields.add("id");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("type");
    openapiFields.add("issuedAt");
    openapiFields.add("expiresAt");
    openapiFields.add("token");
    openapiFields.add("policy");
    openapiFields.add("username");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to ApiTokenInfoDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ApiTokenInfoDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ApiTokenInfoDTO is not found in the empty JSON string", ApiTokenInfoDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("type") != null && !jsonObj.get("type").isJsonNull()) && !jsonObj.get("type").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `type` to be a primitive type in the JSON string but got `%s`", jsonObj.get("type").toString()));
      }
      if ((jsonObj.get("token") != null && !jsonObj.get("token").isJsonNull()) && !jsonObj.get("token").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `token` to be a primitive type in the JSON string but got `%s`", jsonObj.get("token").toString()));
      }
      if ((jsonObj.get("policy") != null && !jsonObj.get("policy").isJsonNull()) && !jsonObj.get("policy").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `policy` to be a primitive type in the JSON string but got `%s`", jsonObj.get("policy").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ApiTokenInfoDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ApiTokenInfoDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ApiTokenInfoDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ApiTokenInfoDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ApiTokenInfoDTO>() {
           @Override
           public void write(JsonWriter out, ApiTokenInfoDTO value) throws IOException {
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
           public ApiTokenInfoDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ApiTokenInfoDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of ApiTokenInfoDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ApiTokenInfoDTO
  * @throws IOException if the JSON string is invalid with respect to ApiTokenInfoDTO
  */
  public static ApiTokenInfoDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ApiTokenInfoDTO.class);
  }

 /**
  * Convert an instance of ApiTokenInfoDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

