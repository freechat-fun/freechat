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
 * Character summary content, including interactive statistical information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterSummaryStatsDTO {
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

  public static final String SERIALIZED_NAME_VIEW_COUNT = "viewCount";
  @SerializedName(SERIALIZED_NAME_VIEW_COUNT)
  private Long viewCount;

  public static final String SERIALIZED_NAME_REFER_COUNT = "referCount";
  @SerializedName(SERIALIZED_NAME_REFER_COUNT)
  private Long referCount;

  public static final String SERIALIZED_NAME_RECOMMEND_COUNT = "recommendCount";
  @SerializedName(SERIALIZED_NAME_RECOMMEND_COUNT)
  private Long recommendCount;

  public static final String SERIALIZED_NAME_SCORE_COUNT = "scoreCount";
  @SerializedName(SERIALIZED_NAME_SCORE_COUNT)
  private Long scoreCount;

  public static final String SERIALIZED_NAME_SCORE = "score";
  @SerializedName(SERIALIZED_NAME_SCORE)
  private Long score;

  public CharacterSummaryStatsDTO() {
  }

  public CharacterSummaryStatsDTO requestId(String requestId) {
    
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


  public CharacterSummaryStatsDTO characterId(String characterId) {
    
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


  public CharacterSummaryStatsDTO gmtCreate(OffsetDateTime gmtCreate) {
    
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


  public CharacterSummaryStatsDTO gmtModified(OffsetDateTime gmtModified) {
    
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


  public CharacterSummaryStatsDTO visibility(String visibility) {
    
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


  public CharacterSummaryStatsDTO version(Integer version) {
    
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


  public CharacterSummaryStatsDTO name(String name) {
    
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


  public CharacterSummaryStatsDTO description(String description) {
    
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


  public CharacterSummaryStatsDTO avatar(String avatar) {
    
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


  public CharacterSummaryStatsDTO picture(String picture) {
    
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


  public CharacterSummaryStatsDTO lang(String lang) {
    
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


  public CharacterSummaryStatsDTO username(String username) {
    
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


  public CharacterSummaryStatsDTO tags(List<String> tags) {
    
    this.tags = tags;
    return this;
  }

  public CharacterSummaryStatsDTO addTagsItem(String tagsItem) {
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


  public CharacterSummaryStatsDTO viewCount(Long viewCount) {
    
    this.viewCount = viewCount;
    return this;
  }

   /**
   * View count
   * @return viewCount
  **/
  @javax.annotation.Nullable
  public Long getViewCount() {
    return viewCount;
  }


  public void setViewCount(Long viewCount) {
    this.viewCount = viewCount;
  }


  public CharacterSummaryStatsDTO referCount(Long referCount) {
    
    this.referCount = referCount;
    return this;
  }

   /**
   * Reference count
   * @return referCount
  **/
  @javax.annotation.Nullable
  public Long getReferCount() {
    return referCount;
  }


  public void setReferCount(Long referCount) {
    this.referCount = referCount;
  }


  public CharacterSummaryStatsDTO recommendCount(Long recommendCount) {
    
    this.recommendCount = recommendCount;
    return this;
  }

   /**
   * Recommendation count
   * @return recommendCount
  **/
  @javax.annotation.Nullable
  public Long getRecommendCount() {
    return recommendCount;
  }


  public void setRecommendCount(Long recommendCount) {
    this.recommendCount = recommendCount;
  }


  public CharacterSummaryStatsDTO scoreCount(Long scoreCount) {
    
    this.scoreCount = scoreCount;
    return this;
  }

   /**
   * Score count
   * @return scoreCount
  **/
  @javax.annotation.Nullable
  public Long getScoreCount() {
    return scoreCount;
  }


  public void setScoreCount(Long scoreCount) {
    this.scoreCount = scoreCount;
  }


  public CharacterSummaryStatsDTO score(Long score) {
    
    this.score = score;
    return this;
  }

   /**
   * Average score
   * @return score
  **/
  @javax.annotation.Nullable
  public Long getScore() {
    return score;
  }


  public void setScore(Long score) {
    this.score = score;
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
   * @return the CharacterSummaryStatsDTO instance itself
   */
  public CharacterSummaryStatsDTO putAdditionalProperty(String key, Object value) {
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
    CharacterSummaryStatsDTO characterSummaryStatsDTO = (CharacterSummaryStatsDTO) o;
    return Objects.equals(this.requestId, characterSummaryStatsDTO.requestId) &&
        Objects.equals(this.characterId, characterSummaryStatsDTO.characterId) &&
        Objects.equals(this.gmtCreate, characterSummaryStatsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, characterSummaryStatsDTO.gmtModified) &&
        Objects.equals(this.visibility, characterSummaryStatsDTO.visibility) &&
        Objects.equals(this.version, characterSummaryStatsDTO.version) &&
        Objects.equals(this.name, characterSummaryStatsDTO.name) &&
        Objects.equals(this.description, characterSummaryStatsDTO.description) &&
        Objects.equals(this.avatar, characterSummaryStatsDTO.avatar) &&
        Objects.equals(this.picture, characterSummaryStatsDTO.picture) &&
        Objects.equals(this.lang, characterSummaryStatsDTO.lang) &&
        Objects.equals(this.username, characterSummaryStatsDTO.username) &&
        Objects.equals(this.tags, characterSummaryStatsDTO.tags) &&
        Objects.equals(this.viewCount, characterSummaryStatsDTO.viewCount) &&
        Objects.equals(this.referCount, characterSummaryStatsDTO.referCount) &&
        Objects.equals(this.recommendCount, characterSummaryStatsDTO.recommendCount) &&
        Objects.equals(this.scoreCount, characterSummaryStatsDTO.scoreCount) &&
        Objects.equals(this.score, characterSummaryStatsDTO.score)&&
        Objects.equals(this.additionalProperties, characterSummaryStatsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, characterId, gmtCreate, gmtModified, visibility, version, name, description, avatar, picture, lang, username, tags, viewCount, referCount, recommendCount, scoreCount, score, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterSummaryStatsDTO {\n");
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
    sb.append("    viewCount: ").append(toIndentedString(viewCount)).append("\n");
    sb.append("    referCount: ").append(toIndentedString(referCount)).append("\n");
    sb.append("    recommendCount: ").append(toIndentedString(recommendCount)).append("\n");
    sb.append("    scoreCount: ").append(toIndentedString(scoreCount)).append("\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
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
    openapiFields.add("viewCount");
    openapiFields.add("referCount");
    openapiFields.add("recommendCount");
    openapiFields.add("scoreCount");
    openapiFields.add("score");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterSummaryStatsDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterSummaryStatsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterSummaryStatsDTO is not found in the empty JSON string", CharacterSummaryStatsDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : CharacterSummaryStatsDTO.openapiRequiredFields) {
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
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterSummaryStatsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterSummaryStatsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterSummaryStatsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterSummaryStatsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterSummaryStatsDTO>() {
           @Override
           public void write(JsonWriter out, CharacterSummaryStatsDTO value) throws IOException {
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
           public CharacterSummaryStatsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterSummaryStatsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterSummaryStatsDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterSummaryStatsDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterSummaryStatsDTO
  */
  public static CharacterSummaryStatsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterSummaryStatsDTO.class);
  }

 /**
  * Convert an instance of CharacterSummaryStatsDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

