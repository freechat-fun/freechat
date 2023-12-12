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
 * Request data for creating new flow information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FlowCreateDTO {
  public static final String SERIALIZED_NAME_PARENT_ID = "parentId";
  @SerializedName(SERIALIZED_NAME_PARENT_ID)
  private String parentId;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_FORMAT = "format";
  @SerializedName(SERIALIZED_NAME_FORMAT)
  private String format;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  private String description;

  public static final String SERIALIZED_NAME_CONFIG = "config";
  @SerializedName(SERIALIZED_NAME_CONFIG)
  private String config;

  public static final String SERIALIZED_NAME_EXAMPLE = "example";
  @SerializedName(SERIALIZED_NAME_EXAMPLE)
  private String example;

  public static final String SERIALIZED_NAME_PARAMETERS = "parameters";
  @SerializedName(SERIALIZED_NAME_PARAMETERS)
  private String parameters;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_DRAFT = "draft";
  @SerializedName(SERIALIZED_NAME_DRAFT)
  private String draft;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<String> aiModels;

  public FlowCreateDTO() {
  }

  public FlowCreateDTO parentId(String parentId) {
    
    this.parentId = parentId;
    return this;
  }

   /**
   * Referenced flow
   * @return parentId
  **/
  @javax.annotation.Nullable
  public String getParentId() {
    return parentId;
  }


  public void setParentId(String parentId) {
    this.parentId = parentId;
  }


  public FlowCreateDTO visibility(String visibility) {
    
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


  public FlowCreateDTO format(String format) {
    
    this.format = format;
    return this;
  }

   /**
   * Flow format, currently supported: langflow
   * @return format
  **/
  @javax.annotation.Nullable
  public String getFormat() {
    return format;
  }


  public void setFormat(String format) {
    this.format = format;
  }


  public FlowCreateDTO name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Flow name
   * @return name
  **/
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public FlowCreateDTO description(String description) {
    
    this.description = description;
    return this;
  }

   /**
   * Flow description
   * @return description
  **/
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public FlowCreateDTO config(String config) {
    
    this.config = config;
    return this;
  }

   /**
   * Flow configuration
   * @return config
  **/
  @javax.annotation.Nullable
  public String getConfig() {
    return config;
  }


  public void setConfig(String config) {
    this.config = config;
  }


  public FlowCreateDTO example(String example) {
    
    this.example = example;
    return this;
  }

   /**
   * Flow example
   * @return example
  **/
  @javax.annotation.Nullable
  public String getExample() {
    return example;
  }


  public void setExample(String example) {
    this.example = example;
  }


  public FlowCreateDTO parameters(String parameters) {
    
    this.parameters = parameters;
    return this;
  }

   /**
   * Flow parameters, JSON format
   * @return parameters
  **/
  @javax.annotation.Nullable
  public String getParameters() {
    return parameters;
  }


  public void setParameters(String parameters) {
    this.parameters = parameters;
  }


  public FlowCreateDTO ext(String ext) {
    
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


  public FlowCreateDTO draft(String draft) {
    
    this.draft = draft;
    return this;
  }

   /**
   * Draft content
   * @return draft
  **/
  @javax.annotation.Nullable
  public String getDraft() {
    return draft;
  }


  public void setDraft(String draft) {
    this.draft = draft;
  }


  public FlowCreateDTO tags(List<String> tags) {
    
    this.tags = tags;
    return this;
  }

  public FlowCreateDTO addTagsItem(String tagsItem) {
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


  public FlowCreateDTO aiModels(List<String> aiModels) {
    
    this.aiModels = aiModels;
    return this;
  }

  public FlowCreateDTO addAiModelsItem(String aiModelsItem) {
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
   * @return the FlowCreateDTO instance itself
   */
  public FlowCreateDTO putAdditionalProperty(String key, Object value) {
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
    FlowCreateDTO flowCreateDTO = (FlowCreateDTO) o;
    return Objects.equals(this.parentId, flowCreateDTO.parentId) &&
        Objects.equals(this.visibility, flowCreateDTO.visibility) &&
        Objects.equals(this.format, flowCreateDTO.format) &&
        Objects.equals(this.name, flowCreateDTO.name) &&
        Objects.equals(this.description, flowCreateDTO.description) &&
        Objects.equals(this.config, flowCreateDTO.config) &&
        Objects.equals(this.example, flowCreateDTO.example) &&
        Objects.equals(this.parameters, flowCreateDTO.parameters) &&
        Objects.equals(this.ext, flowCreateDTO.ext) &&
        Objects.equals(this.draft, flowCreateDTO.draft) &&
        Objects.equals(this.tags, flowCreateDTO.tags) &&
        Objects.equals(this.aiModels, flowCreateDTO.aiModels)&&
        Objects.equals(this.additionalProperties, flowCreateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentId, visibility, format, name, description, config, example, parameters, ext, draft, tags, aiModels, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FlowCreateDTO {\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("    example: ").append(toIndentedString(example)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    draft: ").append(toIndentedString(draft)).append("\n");
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
    openapiFields.add("parentId");
    openapiFields.add("visibility");
    openapiFields.add("format");
    openapiFields.add("name");
    openapiFields.add("description");
    openapiFields.add("config");
    openapiFields.add("example");
    openapiFields.add("parameters");
    openapiFields.add("ext");
    openapiFields.add("draft");
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
  * @throws IOException if the JSON Element is invalid with respect to FlowCreateDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!FlowCreateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in FlowCreateDTO is not found in the empty JSON string", FlowCreateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : FlowCreateDTO.openapiRequiredFields) {
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
      if ((jsonObj.get("format") != null && !jsonObj.get("format").isJsonNull()) && !jsonObj.get("format").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `format` to be a primitive type in the JSON string but got `%s`", jsonObj.get("format").toString()));
      }
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if ((jsonObj.get("config") != null && !jsonObj.get("config").isJsonNull()) && !jsonObj.get("config").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `config` to be a primitive type in the JSON string but got `%s`", jsonObj.get("config").toString()));
      }
      if ((jsonObj.get("example") != null && !jsonObj.get("example").isJsonNull()) && !jsonObj.get("example").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `example` to be a primitive type in the JSON string but got `%s`", jsonObj.get("example").toString()));
      }
      if ((jsonObj.get("parameters") != null && !jsonObj.get("parameters").isJsonNull()) && !jsonObj.get("parameters").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `parameters` to be a primitive type in the JSON string but got `%s`", jsonObj.get("parameters").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      if ((jsonObj.get("draft") != null && !jsonObj.get("draft").isJsonNull()) && !jsonObj.get("draft").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `draft` to be a primitive type in the JSON string but got `%s`", jsonObj.get("draft").toString()));
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
       if (!FlowCreateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'FlowCreateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<FlowCreateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(FlowCreateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<FlowCreateDTO>() {
           @Override
           public void write(JsonWriter out, FlowCreateDTO value) throws IOException {
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
           public FlowCreateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             FlowCreateDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of FlowCreateDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of FlowCreateDTO
  * @throws IOException if the JSON string is invalid with respect to FlowCreateDTO
  */
  public static FlowCreateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, FlowCreateDTO.class);
  }

 /**
  * Convert an instance of FlowCreateDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

