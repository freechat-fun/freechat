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
import fun.freechat.client.model.CharacterInfoDraftDTO;
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
 * Request data for creating new character information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterCreateDTO {
  public static final String SERIALIZED_NAME_PARENT_ID = "parentId";
  @SerializedName(SERIALIZED_NAME_PARENT_ID)
  private String parentId;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

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

  public static final String SERIALIZED_NAME_GENDER = "gender";
  @SerializedName(SERIALIZED_NAME_GENDER)
  private String gender;

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

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  private String lang;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_DRAFT = "draft";
  @SerializedName(SERIALIZED_NAME_DRAFT)
  private CharacterInfoDraftDTO draft;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public CharacterCreateDTO() {
  }

  public CharacterCreateDTO parentId(String parentId) {
    
    this.parentId = parentId;
    return this;
  }

   /**
   * Referenced character
   * @return parentId
  **/
  @javax.annotation.Nullable
  public String getParentId() {
    return parentId;
  }


  public void setParentId(String parentId) {
    this.parentId = parentId;
  }


  public CharacterCreateDTO visibility(String visibility) {
    
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


  public CharacterCreateDTO name(String name) {
    
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


  public CharacterCreateDTO description(String description) {
    
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


  public CharacterCreateDTO avatar(String avatar) {
    
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


  public CharacterCreateDTO picture(String picture) {
    
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


  public CharacterCreateDTO gender(String gender) {
    
    this.gender = gender;
    return this;
  }

   /**
   * Character gender: male | female | non_human | unknown
   * @return gender
  **/
  @javax.annotation.Nullable
  public String getGender() {
    return gender;
  }


  public void setGender(String gender) {
    this.gender = gender;
  }


  public CharacterCreateDTO profile(String profile) {
    
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


  public CharacterCreateDTO greeting(String greeting) {
    
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


  public CharacterCreateDTO chatStyle(String chatStyle) {
    
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


  public CharacterCreateDTO chatExample(String chatExample) {
    
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


  public CharacterCreateDTO experience(String experience) {
    
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


  public CharacterCreateDTO lang(String lang) {
    
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


  public CharacterCreateDTO ext(String ext) {
    
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


  public CharacterCreateDTO draft(CharacterInfoDraftDTO draft) {
    
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


  public CharacterCreateDTO tags(List<String> tags) {
    
    this.tags = tags;
    return this;
  }

  public CharacterCreateDTO addTagsItem(String tagsItem) {
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
   * @return the CharacterCreateDTO instance itself
   */
  public CharacterCreateDTO putAdditionalProperty(String key, Object value) {
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
    CharacterCreateDTO characterCreateDTO = (CharacterCreateDTO) o;
    return Objects.equals(this.parentId, characterCreateDTO.parentId) &&
        Objects.equals(this.visibility, characterCreateDTO.visibility) &&
        Objects.equals(this.name, characterCreateDTO.name) &&
        Objects.equals(this.description, characterCreateDTO.description) &&
        Objects.equals(this.avatar, characterCreateDTO.avatar) &&
        Objects.equals(this.picture, characterCreateDTO.picture) &&
        Objects.equals(this.gender, characterCreateDTO.gender) &&
        Objects.equals(this.profile, characterCreateDTO.profile) &&
        Objects.equals(this.greeting, characterCreateDTO.greeting) &&
        Objects.equals(this.chatStyle, characterCreateDTO.chatStyle) &&
        Objects.equals(this.chatExample, characterCreateDTO.chatExample) &&
        Objects.equals(this.experience, characterCreateDTO.experience) &&
        Objects.equals(this.lang, characterCreateDTO.lang) &&
        Objects.equals(this.ext, characterCreateDTO.ext) &&
        Objects.equals(this.draft, characterCreateDTO.draft) &&
        Objects.equals(this.tags, characterCreateDTO.tags)&&
        Objects.equals(this.additionalProperties, characterCreateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, visibility, name, description, avatar, picture, gender, profile, greeting, chatStyle, chatExample, experience, lang, ext, draft, tags, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterCreateDTO {\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    avatar: ").append(toIndentedString(avatar)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    greeting: ").append(toIndentedString(greeting)).append("\n");
    sb.append("    chatStyle: ").append(toIndentedString(chatStyle)).append("\n");
    sb.append("    chatExample: ").append(toIndentedString(chatExample)).append("\n");
    sb.append("    experience: ").append(toIndentedString(experience)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    draft: ").append(toIndentedString(draft)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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
    openapiFields.add("parentId");
    openapiFields.add("visibility");
    openapiFields.add("name");
    openapiFields.add("description");
    openapiFields.add("avatar");
    openapiFields.add("picture");
    openapiFields.add("gender");
    openapiFields.add("profile");
    openapiFields.add("greeting");
    openapiFields.add("chatStyle");
    openapiFields.add("chatExample");
    openapiFields.add("experience");
    openapiFields.add("lang");
    openapiFields.add("ext");
    openapiFields.add("draft");
    openapiFields.add("tags");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterCreateDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterCreateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterCreateDTO is not found in the empty JSON string", CharacterCreateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : CharacterCreateDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("parentId") != null && !jsonObj.get("parentId").isJsonNull()) && !jsonObj.get("parentId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `parentId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("parentId").toString()));
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
      if ((jsonObj.get("gender") != null && !jsonObj.get("gender").isJsonNull()) && !jsonObj.get("gender").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `gender` to be a primitive type in the JSON string but got `%s`", jsonObj.get("gender").toString()));
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
      if ((jsonObj.get("lang") != null && !jsonObj.get("lang").isJsonNull()) && !jsonObj.get("lang").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `lang` to be a primitive type in the JSON string but got `%s`", jsonObj.get("lang").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      // validate the optional field `draft`
      if (jsonObj.get("draft") != null && !jsonObj.get("draft").isJsonNull()) {
        CharacterInfoDraftDTO.validateJsonElement(jsonObj.get("draft"));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterCreateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterCreateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterCreateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterCreateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterCreateDTO>() {
           @Override
           public void write(JsonWriter out, CharacterCreateDTO value) throws IOException {
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
           public CharacterCreateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterCreateDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterCreateDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterCreateDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterCreateDTO
  */
  public static CharacterCreateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterCreateDTO.class);
  }

 /**
  * Convert an instance of CharacterCreateDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

