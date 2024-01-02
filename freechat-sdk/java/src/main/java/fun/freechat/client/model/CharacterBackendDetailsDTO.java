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
 * Character backend detailed information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterBackendDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_BACKEND_ID = "backendId";
  @SerializedName(SERIALIZED_NAME_BACKEND_ID)
  private String backendId;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_CHARACTER_ID = "characterId";
  @SerializedName(SERIALIZED_NAME_CHARACTER_ID)
  private String characterId;

  public static final String SERIALIZED_NAME_IS_DEFAULT = "isDefault";
  @SerializedName(SERIALIZED_NAME_IS_DEFAULT)
  private Boolean isDefault;

  public static final String SERIALIZED_NAME_GREETING_PROMPT_TASK_ID = "greetingPromptTaskId";
  @SerializedName(SERIALIZED_NAME_GREETING_PROMPT_TASK_ID)
  private String greetingPromptTaskId;

  public static final String SERIALIZED_NAME_EXPERIENCE_PROMPT_TASK_ID = "experiencePromptTaskId";
  @SerializedName(SERIALIZED_NAME_EXPERIENCE_PROMPT_TASK_ID)
  private String experiencePromptTaskId;

  public static final String SERIALIZED_NAME_MESSAGE_WINDOW_SIZE = "messageWindowSize";
  @SerializedName(SERIALIZED_NAME_MESSAGE_WINDOW_SIZE)
  private Integer messageWindowSize;

  public static final String SERIALIZED_NAME_FORWARD_TO_USER = "forwardToUser";
  @SerializedName(SERIALIZED_NAME_FORWARD_TO_USER)
  private Boolean forwardToUser;

  public CharacterBackendDetailsDTO() {
  }

  public CharacterBackendDetailsDTO requestId(String requestId) {
    
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


  public CharacterBackendDetailsDTO backendId(String backendId) {
    
    this.backendId = backendId;
    return this;
  }

   /**
   * Character backend identifier
   * @return backendId
  **/
  @javax.annotation.Nullable
  public String getBackendId() {
    return backendId;
  }


  public void setBackendId(String backendId) {
    this.backendId = backendId;
  }


  public CharacterBackendDetailsDTO gmtCreate(OffsetDateTime gmtCreate) {
    
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


  public CharacterBackendDetailsDTO gmtModified(OffsetDateTime gmtModified) {
    
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


  public CharacterBackendDetailsDTO characterId(String characterId) {
    
    this.characterId = characterId;
    return this;
  }

   /**
   * Character identifier
   * @return characterId
  **/
  @javax.annotation.Nullable
  public String getCharacterId() {
    return characterId;
  }


  public void setCharacterId(String characterId) {
    this.characterId = characterId;
  }


  public CharacterBackendDetailsDTO isDefault(Boolean isDefault) {
    
    this.isDefault = isDefault;
    return this;
  }

   /**
   * Whether it is the default backend
   * @return isDefault
  **/
  @javax.annotation.Nullable
  public Boolean isIsDefault() {
    return isDefault;
  }


  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }


  public CharacterBackendDetailsDTO greetingPromptTaskId(String greetingPromptTaskId) {
    
    this.greetingPromptTaskId = greetingPromptTaskId;
    return this;
  }

   /**
   * Prompt task identifier for greeting
   * @return greetingPromptTaskId
  **/
  @javax.annotation.Nullable
  public String getGreetingPromptTaskId() {
    return greetingPromptTaskId;
  }


  public void setGreetingPromptTaskId(String greetingPromptTaskId) {
    this.greetingPromptTaskId = greetingPromptTaskId;
  }


  public CharacterBackendDetailsDTO experiencePromptTaskId(String experiencePromptTaskId) {
    
    this.experiencePromptTaskId = experiencePromptTaskId;
    return this;
  }

   /**
   * Prompt task identifier for experience
   * @return experiencePromptTaskId
  **/
  @javax.annotation.Nullable
  public String getExperiencePromptTaskId() {
    return experiencePromptTaskId;
  }


  public void setExperiencePromptTaskId(String experiencePromptTaskId) {
    this.experiencePromptTaskId = experiencePromptTaskId;
  }


  public CharacterBackendDetailsDTO messageWindowSize(Integer messageWindowSize) {
    
    this.messageWindowSize = messageWindowSize;
    return this;
  }

   /**
   * Max messages in the character&#39;s memory
   * @return messageWindowSize
  **/
  @javax.annotation.Nullable
  public Integer getMessageWindowSize() {
    return messageWindowSize;
  }


  public void setMessageWindowSize(Integer messageWindowSize) {
    this.messageWindowSize = messageWindowSize;
  }


  public CharacterBackendDetailsDTO forwardToUser(Boolean forwardToUser) {
    
    this.forwardToUser = forwardToUser;
    return this;
  }

   /**
   * Whether to forward messages to the character owner
   * @return forwardToUser
  **/
  @javax.annotation.Nullable
  public Boolean isForwardToUser() {
    return forwardToUser;
  }


  public void setForwardToUser(Boolean forwardToUser) {
    this.forwardToUser = forwardToUser;
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
   * @return the CharacterBackendDetailsDTO instance itself
   */
  public CharacterBackendDetailsDTO putAdditionalProperty(String key, Object value) {
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
    CharacterBackendDetailsDTO characterBackendDetailsDTO = (CharacterBackendDetailsDTO) o;
    return Objects.equals(this.requestId, characterBackendDetailsDTO.requestId) &&
        Objects.equals(this.backendId, characterBackendDetailsDTO.backendId) &&
        Objects.equals(this.gmtCreate, characterBackendDetailsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, characterBackendDetailsDTO.gmtModified) &&
        Objects.equals(this.characterId, characterBackendDetailsDTO.characterId) &&
        Objects.equals(this.isDefault, characterBackendDetailsDTO.isDefault) &&
        Objects.equals(this.greetingPromptTaskId, characterBackendDetailsDTO.greetingPromptTaskId) &&
        Objects.equals(this.experiencePromptTaskId, characterBackendDetailsDTO.experiencePromptTaskId) &&
        Objects.equals(this.messageWindowSize, characterBackendDetailsDTO.messageWindowSize) &&
        Objects.equals(this.forwardToUser, characterBackendDetailsDTO.forwardToUser)&&
        Objects.equals(this.additionalProperties, characterBackendDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, backendId, gmtCreate, gmtModified, characterId, isDefault, greetingPromptTaskId, experiencePromptTaskId, messageWindowSize, forwardToUser, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterBackendDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    backendId: ").append(toIndentedString(backendId)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    characterId: ").append(toIndentedString(characterId)).append("\n");
    sb.append("    isDefault: ").append(toIndentedString(isDefault)).append("\n");
    sb.append("    greetingPromptTaskId: ").append(toIndentedString(greetingPromptTaskId)).append("\n");
    sb.append("    experiencePromptTaskId: ").append(toIndentedString(experiencePromptTaskId)).append("\n");
    sb.append("    messageWindowSize: ").append(toIndentedString(messageWindowSize)).append("\n");
    sb.append("    forwardToUser: ").append(toIndentedString(forwardToUser)).append("\n");
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
    openapiFields.add("backendId");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("characterId");
    openapiFields.add("isDefault");
    openapiFields.add("greetingPromptTaskId");
    openapiFields.add("experiencePromptTaskId");
    openapiFields.add("messageWindowSize");
    openapiFields.add("forwardToUser");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterBackendDetailsDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterBackendDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterBackendDetailsDTO is not found in the empty JSON string", CharacterBackendDetailsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("backendId") != null && !jsonObj.get("backendId").isJsonNull()) && !jsonObj.get("backendId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `backendId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("backendId").toString()));
      }
      if ((jsonObj.get("characterId") != null && !jsonObj.get("characterId").isJsonNull()) && !jsonObj.get("characterId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterId").toString()));
      }
      if ((jsonObj.get("greetingPromptTaskId") != null && !jsonObj.get("greetingPromptTaskId").isJsonNull()) && !jsonObj.get("greetingPromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greetingPromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greetingPromptTaskId").toString()));
      }
      if ((jsonObj.get("experiencePromptTaskId") != null && !jsonObj.get("experiencePromptTaskId").isJsonNull()) && !jsonObj.get("experiencePromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `experiencePromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("experiencePromptTaskId").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterBackendDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterBackendDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterBackendDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterBackendDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterBackendDetailsDTO>() {
           @Override
           public void write(JsonWriter out, CharacterBackendDetailsDTO value) throws IOException {
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
           public CharacterBackendDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterBackendDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterBackendDetailsDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterBackendDetailsDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterBackendDetailsDTO
  */
  public static CharacterBackendDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterBackendDetailsDTO.class);
  }

 /**
  * Convert an instance of CharacterBackendDetailsDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

