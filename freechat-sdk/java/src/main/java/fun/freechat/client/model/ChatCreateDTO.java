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
 * Request data for starting a chat session
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ChatCreateDTO {
  public static final String SERIALIZED_NAME_USER_NICKNAME = "userNickname";
  @SerializedName(SERIALIZED_NAME_USER_NICKNAME)
  private String userNickname;

  public static final String SERIALIZED_NAME_USER_PROFILE = "userProfile";
  @SerializedName(SERIALIZED_NAME_USER_PROFILE)
  private String userProfile;

  public static final String SERIALIZED_NAME_CHARACTER_NICKNAME = "characterNickname";
  @SerializedName(SERIALIZED_NAME_CHARACTER_NICKNAME)
  private String characterNickname;

  public static final String SERIALIZED_NAME_ABOUT = "about";
  @SerializedName(SERIALIZED_NAME_ABOUT)
  private String about;

  public static final String SERIALIZED_NAME_CHARACTER_ID = "characterId";
  @SerializedName(SERIALIZED_NAME_CHARACTER_ID)
  private Long characterId;

  public static final String SERIALIZED_NAME_BACKEND_ID = "backendId";
  @SerializedName(SERIALIZED_NAME_BACKEND_ID)
  private String backendId;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public ChatCreateDTO() {
  }

  public ChatCreateDTO userNickname(String userNickname) {
    this.userNickname = userNickname;
    return this;
  }

   /**
   * User nickname for this session
   * @return userNickname
  **/
  @javax.annotation.Nullable
  public String getUserNickname() {
    return userNickname;
  }

  public void setUserNickname(String userNickname) {
    this.userNickname = userNickname;
  }


  public ChatCreateDTO userProfile(String userProfile) {
    this.userProfile = userProfile;
    return this;
  }

   /**
   * User profile for this session
   * @return userProfile
  **/
  @javax.annotation.Nullable
  public String getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(String userProfile) {
    this.userProfile = userProfile;
  }


  public ChatCreateDTO characterNickname(String characterNickname) {
    this.characterNickname = characterNickname;
    return this;
  }

   /**
   * Character nickname for this session
   * @return characterNickname
  **/
  @javax.annotation.Nullable
  public String getCharacterNickname() {
    return characterNickname;
  }

  public void setCharacterNickname(String characterNickname) {
    this.characterNickname = characterNickname;
  }


  public ChatCreateDTO about(String about) {
    this.about = about;
    return this;
  }

   /**
   * Anything about this session
   * @return about
  **/
  @javax.annotation.Nullable
  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }


  public ChatCreateDTO characterId(Long characterId) {
    this.characterId = characterId;
    return this;
  }

   /**
   * Character id for this session
   * @return characterId
  **/
  @javax.annotation.Nonnull
  public Long getCharacterId() {
    return characterId;
  }

  public void setCharacterId(Long characterId) {
    this.characterId = characterId;
  }


  public ChatCreateDTO backendId(String backendId) {
    this.backendId = backendId;
    return this;
  }

   /**
   * Character backend for this session
   * @return backendId
  **/
  @javax.annotation.Nullable
  public String getBackendId() {
    return backendId;
  }

  public void setBackendId(String backendId) {
    this.backendId = backendId;
  }


  public ChatCreateDTO ext(String ext) {
    this.ext = ext;
    return this;
  }

   /**
   * Extra info for this session
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
   * @return the ChatCreateDTO instance itself
   */
  public ChatCreateDTO putAdditionalProperty(String key, Object value) {
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
    ChatCreateDTO chatCreateDTO = (ChatCreateDTO) o;
    return Objects.equals(this.userNickname, chatCreateDTO.userNickname) &&
        Objects.equals(this.userProfile, chatCreateDTO.userProfile) &&
        Objects.equals(this.characterNickname, chatCreateDTO.characterNickname) &&
        Objects.equals(this.about, chatCreateDTO.about) &&
        Objects.equals(this.characterId, chatCreateDTO.characterId) &&
        Objects.equals(this.backendId, chatCreateDTO.backendId) &&
        Objects.equals(this.ext, chatCreateDTO.ext)&&
        Objects.equals(this.additionalProperties, chatCreateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userNickname, userProfile, characterNickname, about, characterId, backendId, ext, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatCreateDTO {\n");
    sb.append("    userNickname: ").append(toIndentedString(userNickname)).append("\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    characterNickname: ").append(toIndentedString(characterNickname)).append("\n");
    sb.append("    about: ").append(toIndentedString(about)).append("\n");
    sb.append("    characterId: ").append(toIndentedString(characterId)).append("\n");
    sb.append("    backendId: ").append(toIndentedString(backendId)).append("\n");
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
    openapiFields.add("userNickname");
    openapiFields.add("userProfile");
    openapiFields.add("characterNickname");
    openapiFields.add("about");
    openapiFields.add("characterId");
    openapiFields.add("backendId");
    openapiFields.add("ext");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("characterId");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to ChatCreateDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ChatCreateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ChatCreateDTO is not found in the empty JSON string", ChatCreateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : ChatCreateDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("userNickname") != null && !jsonObj.get("userNickname").isJsonNull()) && !jsonObj.get("userNickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `userNickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("userNickname").toString()));
      }
      if ((jsonObj.get("userProfile") != null && !jsonObj.get("userProfile").isJsonNull()) && !jsonObj.get("userProfile").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `userProfile` to be a primitive type in the JSON string but got `%s`", jsonObj.get("userProfile").toString()));
      }
      if ((jsonObj.get("characterNickname") != null && !jsonObj.get("characterNickname").isJsonNull()) && !jsonObj.get("characterNickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterNickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterNickname").toString()));
      }
      if ((jsonObj.get("about") != null && !jsonObj.get("about").isJsonNull()) && !jsonObj.get("about").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `about` to be a primitive type in the JSON string but got `%s`", jsonObj.get("about").toString()));
      }
      if ((jsonObj.get("backendId") != null && !jsonObj.get("backendId").isJsonNull()) && !jsonObj.get("backendId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `backendId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("backendId").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ChatCreateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ChatCreateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ChatCreateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ChatCreateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ChatCreateDTO>() {
           @Override
           public void write(JsonWriter out, ChatCreateDTO value) throws IOException {
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
           public ChatCreateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ChatCreateDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of ChatCreateDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ChatCreateDTO
  * @throws IOException if the JSON string is invalid with respect to ChatCreateDTO
  */
  public static ChatCreateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ChatCreateDTO.class);
  }

 /**
  * Convert an instance of ChatCreateDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

