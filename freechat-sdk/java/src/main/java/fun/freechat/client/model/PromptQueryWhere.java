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
 * Query condition
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PromptQueryWhere {
  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_TAGS_OP = "tagsOp";
  @SerializedName(SERIALIZED_NAME_TAGS_OP)
  private String tagsOp;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<String> aiModels;

  public static final String SERIALIZED_NAME_AI_MODELS_OP = "aiModelsOp";
  @SerializedName(SERIALIZED_NAME_AI_MODELS_OP)
  private String aiModelsOp;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  private String type;

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  private String lang;

  public static final String SERIALIZED_NAME_TEXT = "text";
  @SerializedName(SERIALIZED_NAME_TEXT)
  private String text;

  public PromptQueryWhere() {
  }

  public PromptQueryWhere visibility(String visibility) {
    this.visibility = visibility;
    return this;
  }

   /**
   * Visibility: public, public_org (search this organization), private (default)
   * @return visibility
  **/
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public PromptQueryWhere username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Effective when searching public, public_org prompts, if not specified, search all users
   * @return username
  **/
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public PromptQueryWhere tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PromptQueryWhere addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

   /**
   * Tags
   * @return tags
  **/
  @javax.annotation.Nullable
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }


  public PromptQueryWhere tagsOp(String tagsOp) {
    this.tagsOp = tagsOp;
    return this;
  }

   /**
   * Relationship between tags: and | or (default)
   * @return tagsOp
  **/
  @javax.annotation.Nullable
  public String getTagsOp() {
    return tagsOp;
  }

  public void setTagsOp(String tagsOp) {
    this.tagsOp = tagsOp;
  }


  public PromptQueryWhere aiModels(List<String> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PromptQueryWhere addAiModelsItem(String aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

   /**
   * Model set
   * @return aiModels
  **/
  @javax.annotation.Nullable
  public List<String> getAiModels() {
    return aiModels;
  }

  public void setAiModels(List<String> aiModels) {
    this.aiModels = aiModels;
  }


  public PromptQueryWhere aiModelsOp(String aiModelsOp) {
    this.aiModelsOp = aiModelsOp;
    return this;
  }

   /**
   * Relationship between model sets: and | or (default)
   * @return aiModelsOp
  **/
  @javax.annotation.Nullable
  public String getAiModelsOp() {
    return aiModelsOp;
  }

  public void setAiModelsOp(String aiModelsOp) {
    this.aiModelsOp = aiModelsOp;
  }


  public PromptQueryWhere name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Name, left match
   * @return name
  **/
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public PromptQueryWhere type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Type, exact match: string (default) | chat
   * @return type
  **/
  @javax.annotation.Nullable
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public PromptQueryWhere lang(String lang) {
    this.lang = lang;
    return this;
  }

   /**
   * Language, exact match
   * @return lang
  **/
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }


  public PromptQueryWhere text(String text) {
    this.text = text;
    return this;
  }

   /**
   * Name, description, template, example, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time.
   * @return text
  **/
  @javax.annotation.Nullable
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
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
   * @return the PromptQueryWhere instance itself
   */
  public PromptQueryWhere putAdditionalProperty(String key, Object value) {
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
    PromptQueryWhere promptQueryWhere = (PromptQueryWhere) o;
    return Objects.equals(this.visibility, promptQueryWhere.visibility) &&
        Objects.equals(this.username, promptQueryWhere.username) &&
        Objects.equals(this.tags, promptQueryWhere.tags) &&
        Objects.equals(this.tagsOp, promptQueryWhere.tagsOp) &&
        Objects.equals(this.aiModels, promptQueryWhere.aiModels) &&
        Objects.equals(this.aiModelsOp, promptQueryWhere.aiModelsOp) &&
        Objects.equals(this.name, promptQueryWhere.name) &&
        Objects.equals(this.type, promptQueryWhere.type) &&
        Objects.equals(this.lang, promptQueryWhere.lang) &&
        Objects.equals(this.text, promptQueryWhere.text)&&
        Objects.equals(this.additionalProperties, promptQueryWhere.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(visibility, username, tags, tagsOp, aiModels, aiModelsOp, name, type, lang, text, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptQueryWhere {\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    tagsOp: ").append(toIndentedString(tagsOp)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
    sb.append("    aiModelsOp: ").append(toIndentedString(aiModelsOp)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
    openapiFields.add("username");
    openapiFields.add("tags");
    openapiFields.add("tagsOp");
    openapiFields.add("aiModels");
    openapiFields.add("aiModelsOp");
    openapiFields.add("name");
    openapiFields.add("type");
    openapiFields.add("lang");
    openapiFields.add("text");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to PromptQueryWhere
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PromptQueryWhere.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PromptQueryWhere is not found in the empty JSON string", PromptQueryWhere.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
      if ((jsonObj.get("tagsOp") != null && !jsonObj.get("tagsOp").isJsonNull()) && !jsonObj.get("tagsOp").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `tagsOp` to be a primitive type in the JSON string but got `%s`", jsonObj.get("tagsOp").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("aiModels") != null && !jsonObj.get("aiModels").isJsonNull() && !jsonObj.get("aiModels").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `aiModels` to be an array in the JSON string but got `%s`", jsonObj.get("aiModels").toString()));
      }
      if ((jsonObj.get("aiModelsOp") != null && !jsonObj.get("aiModelsOp").isJsonNull()) && !jsonObj.get("aiModelsOp").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `aiModelsOp` to be a primitive type in the JSON string but got `%s`", jsonObj.get("aiModelsOp").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("type") != null && !jsonObj.get("type").isJsonNull()) && !jsonObj.get("type").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `type` to be a primitive type in the JSON string but got `%s`", jsonObj.get("type").toString()));
      }
      if ((jsonObj.get("lang") != null && !jsonObj.get("lang").isJsonNull()) && !jsonObj.get("lang").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `lang` to be a primitive type in the JSON string but got `%s`", jsonObj.get("lang").toString()));
      }
      if ((jsonObj.get("text") != null && !jsonObj.get("text").isJsonNull()) && !jsonObj.get("text").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `text` to be a primitive type in the JSON string but got `%s`", jsonObj.get("text").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PromptQueryWhere.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PromptQueryWhere' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PromptQueryWhere> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PromptQueryWhere.class));

       return (TypeAdapter<T>) new TypeAdapter<PromptQueryWhere>() {
           @Override
           public void write(JsonWriter out, PromptQueryWhere value) throws IOException {
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
           public PromptQueryWhere read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PromptQueryWhere instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of PromptQueryWhere given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of PromptQueryWhere
  * @throws IOException if the JSON string is invalid with respect to PromptQueryWhere
  */
  public static PromptQueryWhere fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PromptQueryWhere.class);
  }

 /**
  * Convert an instance of PromptQueryWhere to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

