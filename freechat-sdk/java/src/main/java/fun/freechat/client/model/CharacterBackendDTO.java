/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.6.0
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
 * Character backend information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterBackendDTO {
  public static final String SERIALIZED_NAME_IS_DEFAULT = "isDefault";
  @SerializedName(SERIALIZED_NAME_IS_DEFAULT)
  private Boolean isDefault;

  public static final String SERIALIZED_NAME_CHAT_PROMPT_TASK_ID = "chatPromptTaskId";
  @SerializedName(SERIALIZED_NAME_CHAT_PROMPT_TASK_ID)
  private String chatPromptTaskId;

  public static final String SERIALIZED_NAME_GREETING_PROMPT_TASK_ID = "greetingPromptTaskId";
  @SerializedName(SERIALIZED_NAME_GREETING_PROMPT_TASK_ID)
  private String greetingPromptTaskId;

  public static final String SERIALIZED_NAME_MODERATION_MODEL_ID = "moderationModelId";
  @SerializedName(SERIALIZED_NAME_MODERATION_MODEL_ID)
  private String moderationModelId;

  public static final String SERIALIZED_NAME_MODERATION_API_KEY_NAME = "moderationApiKeyName";
  @SerializedName(SERIALIZED_NAME_MODERATION_API_KEY_NAME)
  private String moderationApiKeyName;

  public static final String SERIALIZED_NAME_MODERATION_PARAMS = "moderationParams";
  @SerializedName(SERIALIZED_NAME_MODERATION_PARAMS)
  private String moderationParams;

  public static final String SERIALIZED_NAME_MESSAGE_WINDOW_SIZE = "messageWindowSize";
  @SerializedName(SERIALIZED_NAME_MESSAGE_WINDOW_SIZE)
  private Integer messageWindowSize;

  public static final String SERIALIZED_NAME_FORWARD_TO_USER = "forwardToUser";
  @SerializedName(SERIALIZED_NAME_FORWARD_TO_USER)
  private Boolean forwardToUser;

  public CharacterBackendDTO() {
  }

  public CharacterBackendDTO isDefault(Boolean isDefault) {
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


  public CharacterBackendDTO chatPromptTaskId(String chatPromptTaskId) {
    this.chatPromptTaskId = chatPromptTaskId;
    return this;
  }

   /**
   * Prompt task identifier for chat
   * @return chatPromptTaskId
  **/
  @javax.annotation.Nullable
  public String getChatPromptTaskId() {
    return chatPromptTaskId;
  }

  public void setChatPromptTaskId(String chatPromptTaskId) {
    this.chatPromptTaskId = chatPromptTaskId;
  }


  public CharacterBackendDTO greetingPromptTaskId(String greetingPromptTaskId) {
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


  public CharacterBackendDTO moderationModelId(String moderationModelId) {
    this.moderationModelId = moderationModelId;
    return this;
  }

   /**
   * Moderation model for the character&#39;s response
   * @return moderationModelId
  **/
  @javax.annotation.Nullable
  public String getModerationModelId() {
    return moderationModelId;
  }

  public void setModerationModelId(String moderationModelId) {
    this.moderationModelId = moderationModelId;
  }


  public CharacterBackendDTO moderationApiKeyName(String moderationApiKeyName) {
    this.moderationApiKeyName = moderationApiKeyName;
    return this;
  }

   /**
   * Api key name for moderation model
   * @return moderationApiKeyName
  **/
  @javax.annotation.Nullable
  public String getModerationApiKeyName() {
    return moderationApiKeyName;
  }

  public void setModerationApiKeyName(String moderationApiKeyName) {
    this.moderationApiKeyName = moderationApiKeyName;
  }


  public CharacterBackendDTO moderationParams(String moderationParams) {
    this.moderationParams = moderationParams;
    return this;
  }

   /**
   * Parameters for moderation model
   * @return moderationParams
  **/
  @javax.annotation.Nullable
  public String getModerationParams() {
    return moderationParams;
  }

  public void setModerationParams(String moderationParams) {
    this.moderationParams = moderationParams;
  }


  public CharacterBackendDTO messageWindowSize(Integer messageWindowSize) {
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


  public CharacterBackendDTO forwardToUser(Boolean forwardToUser) {
    this.forwardToUser = forwardToUser;
    return this;
  }

   /**
   * Weather to forward messages to the character owner
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
   * @return the CharacterBackendDTO instance itself
   */
  public CharacterBackendDTO putAdditionalProperty(String key, Object value) {
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
    CharacterBackendDTO characterBackendDTO = (CharacterBackendDTO) o;
    return Objects.equals(this.isDefault, characterBackendDTO.isDefault) &&
        Objects.equals(this.chatPromptTaskId, characterBackendDTO.chatPromptTaskId) &&
        Objects.equals(this.greetingPromptTaskId, characterBackendDTO.greetingPromptTaskId) &&
        Objects.equals(this.moderationModelId, characterBackendDTO.moderationModelId) &&
        Objects.equals(this.moderationApiKeyName, characterBackendDTO.moderationApiKeyName) &&
        Objects.equals(this.moderationParams, characterBackendDTO.moderationParams) &&
        Objects.equals(this.messageWindowSize, characterBackendDTO.messageWindowSize) &&
        Objects.equals(this.forwardToUser, characterBackendDTO.forwardToUser)&&
        Objects.equals(this.additionalProperties, characterBackendDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDefault, chatPromptTaskId, greetingPromptTaskId, moderationModelId, moderationApiKeyName, moderationParams, messageWindowSize, forwardToUser, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterBackendDTO {\n");
    sb.append("    isDefault: ").append(toIndentedString(isDefault)).append("\n");
    sb.append("    chatPromptTaskId: ").append(toIndentedString(chatPromptTaskId)).append("\n");
    sb.append("    greetingPromptTaskId: ").append(toIndentedString(greetingPromptTaskId)).append("\n");
    sb.append("    moderationModelId: ").append(toIndentedString(moderationModelId)).append("\n");
    sb.append("    moderationApiKeyName: ").append(toIndentedString(moderationApiKeyName)).append("\n");
    sb.append("    moderationParams: ").append(toIndentedString(moderationParams)).append("\n");
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
    openapiFields.add("isDefault");
    openapiFields.add("chatPromptTaskId");
    openapiFields.add("greetingPromptTaskId");
    openapiFields.add("moderationModelId");
    openapiFields.add("moderationApiKeyName");
    openapiFields.add("moderationParams");
    openapiFields.add("messageWindowSize");
    openapiFields.add("forwardToUser");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterBackendDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterBackendDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterBackendDTO is not found in the empty JSON string", CharacterBackendDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("chatPromptTaskId") != null && !jsonObj.get("chatPromptTaskId").isJsonNull()) && !jsonObj.get("chatPromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatPromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatPromptTaskId").toString()));
      }
      if ((jsonObj.get("greetingPromptTaskId") != null && !jsonObj.get("greetingPromptTaskId").isJsonNull()) && !jsonObj.get("greetingPromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greetingPromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greetingPromptTaskId").toString()));
      }
      if ((jsonObj.get("moderationModelId") != null && !jsonObj.get("moderationModelId").isJsonNull()) && !jsonObj.get("moderationModelId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationModelId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationModelId").toString()));
      }
      if ((jsonObj.get("moderationApiKeyName") != null && !jsonObj.get("moderationApiKeyName").isJsonNull()) && !jsonObj.get("moderationApiKeyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationApiKeyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationApiKeyName").toString()));
      }
      if ((jsonObj.get("moderationParams") != null && !jsonObj.get("moderationParams").isJsonNull()) && !jsonObj.get("moderationParams").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationParams` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationParams").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterBackendDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterBackendDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterBackendDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterBackendDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterBackendDTO>() {
           @Override
           public void write(JsonWriter out, CharacterBackendDTO value) throws IOException {
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
           public CharacterBackendDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterBackendDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterBackendDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterBackendDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterBackendDTO
  */
  public static CharacterBackendDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterBackendDTO.class);
  }

 /**
  * Convert an instance of CharacterBackendDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

