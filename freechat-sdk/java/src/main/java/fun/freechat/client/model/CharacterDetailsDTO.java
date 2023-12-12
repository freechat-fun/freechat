/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.0
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
import fun.freechat.client.model.CharacterBackendDetailsDTO;
import fun.freechat.client.model.CharacterInfoDraftDTO;
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
 * Character detailed content
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_CHARACTER_ID = "characterId";
  @SerializedName(SERIALIZED_NAME_CHARACTER_ID)
  private String characterId;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_VERSION = "version";
  @SerializedName(SERIALIZED_NAME_VERSION)
  private Integer version;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  private String description;

  public static final String SERIALIZED_NAME_AVATAR = "avatar";
  @SerializedName(SERIALIZED_NAME_AVATAR)
  private String avatar;

  public static final String SERIALIZED_NAME_PICTURE = "picture";
  @SerializedName(SERIALIZED_NAME_PICTURE)
  private String picture;

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  private String lang;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_PROFILE = "profile";
  @SerializedName(SERIALIZED_NAME_PROFILE)
  private String profile;

  public static final String SERIALIZED_NAME_GREETING = "greeting";
  @SerializedName(SERIALIZED_NAME_GREETING)
  private String greeting;

  public static final String SERIALIZED_NAME_CHAT_STYLE = "chatStyle";
  @SerializedName(SERIALIZED_NAME_CHAT_STYLE)
  private String chatStyle;

  public static final String SERIALIZED_NAME_CHAT_EXAMPLE = "chatExample";
  @SerializedName(SERIALIZED_NAME_CHAT_EXAMPLE)
  private String chatExample;

  public static final String SERIALIZED_NAME_EXPERIENCE = "experience";
  @SerializedName(SERIALIZED_NAME_EXPERIENCE)
  private String experience;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_DRAFT = "draft";
  @SerializedName(SERIALIZED_NAME_DRAFT)
  private CharacterInfoDraftDTO draft;

  public static final String SERIALIZED_NAME_BACKENDS = "backends";
  @SerializedName(SERIALIZED_NAME_BACKENDS)
  private List<CharacterBackendDetailsDTO> backends;

  public CharacterDetailsDTO() {
  }

  public CharacterDetailsDTO requestId(String requestId) {
    
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


  public CharacterDetailsDTO characterId(String characterId) {
    
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


  public CharacterDetailsDTO gmtCreate(OffsetDateTime gmtCreate) {
    
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


  public CharacterDetailsDTO gmtModified(OffsetDateTime gmtModified) {
    
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


  public CharacterDetailsDTO visibility(String visibility) {
    
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


  public CharacterDetailsDTO version(Integer version) {
    
    this.version = version;
    return this;
  }

   /**
   * Version
   * @return version
  **/
  @javax.annotation.Nullable
  public Integer getVersion() {
    return version;
  }


  public void setVersion(Integer version) {
    this.version = version;
  }


  public CharacterDetailsDTO name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Character name
   * @return name
  **/
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public CharacterDetailsDTO description(String description) {
    
    this.description = description;
    return this;
  }

   /**
   * Character description
   * @return description
  **/
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public CharacterDetailsDTO avatar(String avatar) {
    
    this.avatar = avatar;
    return this;
  }

   /**
   * Character avatar url
   * @return avatar
  **/
  @javax.annotation.Nullable
  public String getAvatar() {
    return avatar;
  }


  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }


  public CharacterDetailsDTO picture(String picture) {
    
    this.picture = picture;
    return this;
  }

   /**
   * Character picture url
   * @return picture
  **/
  @javax.annotation.Nullable
  public String getPicture() {
    return picture;
  }


  public void setPicture(String picture) {
    this.picture = picture;
  }


  public CharacterDetailsDTO lang(String lang) {
    
    this.lang = lang;
    return this;
  }

   /**
   * Character language: English | Chinese (Simplified) | ...
   * @return lang
  **/
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }


  public void setLang(String lang) {
    this.lang = lang;
  }


  public CharacterDetailsDTO username(String username) {
    
    this.username = username;
    return this;
  }

   /**
   * Character owner
   * @return username
  **/
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public CharacterDetailsDTO tags(List<String> tags) {
    
    this.tags = tags;
    return this;
  }

  public CharacterDetailsDTO addTagsItem(String tagsItem) {
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


  public CharacterDetailsDTO profile(String profile) {
    
    this.profile = profile;
    return this;
  }

   /**
   * Character profile
   * @return profile
  **/
  @javax.annotation.Nullable
  public String getProfile() {
    return profile;
  }


  public void setProfile(String profile) {
    this.profile = profile;
  }


  public CharacterDetailsDTO greeting(String greeting) {
    
    this.greeting = greeting;
    return this;
  }

   /**
   * Character greeting
   * @return greeting
  **/
  @javax.annotation.Nullable
  public String getGreeting() {
    return greeting;
  }


  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }


  public CharacterDetailsDTO chatStyle(String chatStyle) {
    
    this.chatStyle = chatStyle;
    return this;
  }

   /**
   * Character chat-style
   * @return chatStyle
  **/
  @javax.annotation.Nullable
  public String getChatStyle() {
    return chatStyle;
  }


  public void setChatStyle(String chatStyle) {
    this.chatStyle = chatStyle;
  }


  public CharacterDetailsDTO chatExample(String chatExample) {
    
    this.chatExample = chatExample;
    return this;
  }

   /**
   * Character chat-example
   * @return chatExample
  **/
  @javax.annotation.Nullable
  public String getChatExample() {
    return chatExample;
  }


  public void setChatExample(String chatExample) {
    this.chatExample = chatExample;
  }


  public CharacterDetailsDTO experience(String experience) {
    
    this.experience = experience;
    return this;
  }

   /**
   * Character experience
   * @return experience
  **/
  @javax.annotation.Nullable
  public String getExperience() {
    return experience;
  }


  public void setExperience(String experience) {
    this.experience = experience;
  }


  public CharacterDetailsDTO ext(String ext) {
    
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


  public CharacterDetailsDTO draft(CharacterInfoDraftDTO draft) {
    
    this.draft = draft;
    return this;
  }

   /**
   * Get draft
   * @return draft
  **/
  @javax.annotation.Nullable
  public CharacterInfoDraftDTO getDraft() {
    return draft;
  }


  public void setDraft(CharacterInfoDraftDTO draft) {
    this.draft = draft;
  }


  public CharacterDetailsDTO backends(List<CharacterBackendDetailsDTO> backends) {
    
    this.backends = backends;
    return this;
  }

  public CharacterDetailsDTO addBackendsItem(CharacterBackendDetailsDTO backendsItem) {
    if (this.backends == null) {
      this.backends = new ArrayList<>();
    }
    this.backends.add(backendsItem);
    return this;
  }

   /**
   * Character backends information
   * @return backends
  **/
  @javax.annotation.Nullable
  public List<CharacterBackendDetailsDTO> getBackends() {
    return backends;
  }


  public void setBackends(List<CharacterBackendDetailsDTO> backends) {
    this.backends = backends;
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
   * @return the CharacterDetailsDTO instance itself
   */
  public CharacterDetailsDTO putAdditionalProperty(String key, Object value) {
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
    CharacterDetailsDTO characterDetailsDTO = (CharacterDetailsDTO) o;
    return Objects.equals(this.requestId, characterDetailsDTO.requestId) &&
        Objects.equals(this.characterId, characterDetailsDTO.characterId) &&
        Objects.equals(this.gmtCreate, characterDetailsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, characterDetailsDTO.gmtModified) &&
        Objects.equals(this.visibility, characterDetailsDTO.visibility) &&
        Objects.equals(this.version, characterDetailsDTO.version) &&
        Objects.equals(this.name, characterDetailsDTO.name) &&
        Objects.equals(this.description, characterDetailsDTO.description) &&
        Objects.equals(this.avatar, characterDetailsDTO.avatar) &&
        Objects.equals(this.picture, characterDetailsDTO.picture) &&
        Objects.equals(this.lang, characterDetailsDTO.lang) &&
        Objects.equals(this.username, characterDetailsDTO.username) &&
        Objects.equals(this.tags, characterDetailsDTO.tags) &&
        Objects.equals(this.profile, characterDetailsDTO.profile) &&
        Objects.equals(this.greeting, characterDetailsDTO.greeting) &&
        Objects.equals(this.chatStyle, characterDetailsDTO.chatStyle) &&
        Objects.equals(this.chatExample, characterDetailsDTO.chatExample) &&
        Objects.equals(this.experience, characterDetailsDTO.experience) &&
        Objects.equals(this.ext, characterDetailsDTO.ext) &&
        Objects.equals(this.draft, characterDetailsDTO.draft) &&
        Objects.equals(this.backends, characterDetailsDTO.backends)&&
        Objects.equals(this.additionalProperties, characterDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, characterId, gmtCreate, gmtModified, visibility, version, name, description, avatar, picture, lang, username, tags, profile, greeting, chatStyle, chatExample, experience, ext, draft, backends, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    characterId: ").append(toIndentedString(characterId)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatar: ").append(toIndentedString(avatar)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    greeting: ").append(toIndentedString(greeting)).append("\n");
    sb.append("    chatStyle: ").append(toIndentedString(chatStyle)).append("\n");
    sb.append("    chatExample: ").append(toIndentedString(chatExample)).append("\n");
    sb.append("    experience: ").append(toIndentedString(experience)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    draft: ").append(toIndentedString(draft)).append("\n");
    sb.append("    backends: ").append(toIndentedString(backends)).append("\n");
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
    openapiFields.add("characterId");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("visibility");
    openapiFields.add("version");
    openapiFields.add("name");
    openapiFields.add("description");
    openapiFields.add("avatar");
    openapiFields.add("picture");
    openapiFields.add("lang");
    openapiFields.add("username");
    openapiFields.add("tags");
    openapiFields.add("profile");
    openapiFields.add("greeting");
    openapiFields.add("chatStyle");
    openapiFields.add("chatExample");
    openapiFields.add("experience");
    openapiFields.add("ext");
    openapiFields.add("draft");
    openapiFields.add("backends");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterDetailsDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterDetailsDTO is not found in the empty JSON string", CharacterDetailsDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : CharacterDetailsDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("characterId") != null && !jsonObj.get("characterId").isJsonNull()) && !jsonObj.get("characterId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterId").toString()));
      }
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if ((jsonObj.get("avatar") != null && !jsonObj.get("avatar").isJsonNull()) && !jsonObj.get("avatar").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `avatar` to be a primitive type in the JSON string but got `%s`", jsonObj.get("avatar").toString()));
      }
      if ((jsonObj.get("picture") != null && !jsonObj.get("picture").isJsonNull()) && !jsonObj.get("picture").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `picture` to be a primitive type in the JSON string but got `%s`", jsonObj.get("picture").toString()));
      }
      if ((jsonObj.get("lang") != null && !jsonObj.get("lang").isJsonNull()) && !jsonObj.get("lang").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `lang` to be a primitive type in the JSON string but got `%s`", jsonObj.get("lang").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
      if ((jsonObj.get("profile") != null && !jsonObj.get("profile").isJsonNull()) && !jsonObj.get("profile").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `profile` to be a primitive type in the JSON string but got `%s`", jsonObj.get("profile").toString()));
      }
      if ((jsonObj.get("greeting") != null && !jsonObj.get("greeting").isJsonNull()) && !jsonObj.get("greeting").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greeting` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greeting").toString()));
      }
      if ((jsonObj.get("chatStyle") != null && !jsonObj.get("chatStyle").isJsonNull()) && !jsonObj.get("chatStyle").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatStyle` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatStyle").toString()));
      }
      if ((jsonObj.get("chatExample") != null && !jsonObj.get("chatExample").isJsonNull()) && !jsonObj.get("chatExample").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatExample` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatExample").toString()));
      }
      if ((jsonObj.get("experience") != null && !jsonObj.get("experience").isJsonNull()) && !jsonObj.get("experience").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `experience` to be a primitive type in the JSON string but got `%s`", jsonObj.get("experience").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      // validate the optional field `draft`
      if (jsonObj.get("draft") != null && !jsonObj.get("draft").isJsonNull()) {
        CharacterInfoDraftDTO.validateJsonElement(jsonObj.get("draft"));
      }
      if (jsonObj.get("backends") != null && !jsonObj.get("backends").isJsonNull()) {
        JsonArray jsonArraybackends = jsonObj.getAsJsonArray("backends");
        if (jsonArraybackends != null) {
          // ensure the json data is an array
          if (!jsonObj.get("backends").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `backends` to be an array in the JSON string but got `%s`", jsonObj.get("backends").toString()));
          }

          // validate the optional field `backends` (array)
          for (int i = 0; i < jsonArraybackends.size(); i++) {
            CharacterBackendDetailsDTO.validateJsonElement(jsonArraybackends.get(i));
          };
        }
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterDetailsDTO>() {
           @Override
           public void write(JsonWriter out, CharacterDetailsDTO value) throws IOException {
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
           public CharacterDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterDetailsDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterDetailsDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterDetailsDTO
  */
  public static CharacterDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterDetailsDTO.class);
  }

 /**
  * Convert an instance of CharacterDetailsDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

