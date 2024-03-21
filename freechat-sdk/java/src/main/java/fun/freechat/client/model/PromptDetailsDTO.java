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
import fun.freechat.client.model.AiModelInfoDTO;
import fun.freechat.client.model.ChatPromptContentDTO;
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
 * Prompt detailed content
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PromptDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_PROMPT_ID = "promptId";
  @SerializedName(SERIALIZED_NAME_PROMPT_ID)
  private Long promptId;

  public static final String SERIALIZED_NAME_PROMPT_UID = "promptUid";
  @SerializedName(SERIALIZED_NAME_PROMPT_UID)
  private String promptUid;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_PARENT_UID = "parentUid";
  @SerializedName(SERIALIZED_NAME_PARENT_UID)
  private String parentUid;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  private String visibility;

  public static final String SERIALIZED_NAME_VERSION = "version";
  @SerializedName(SERIALIZED_NAME_VERSION)
  private Integer version;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  private String type;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  private String description;

  public static final String SERIALIZED_NAME_FORMAT = "format";
  @SerializedName(SERIALIZED_NAME_FORMAT)
  private String format;

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  private String lang;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  private List<String> tags;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<AiModelInfoDTO> aiModels;

  public static final String SERIALIZED_NAME_TEMPLATE = "template";
  @SerializedName(SERIALIZED_NAME_TEMPLATE)
  private String template;

  public static final String SERIALIZED_NAME_CHAT_TEMPLATE = "chatTemplate";
  @SerializedName(SERIALIZED_NAME_CHAT_TEMPLATE)
  private ChatPromptContentDTO chatTemplate;

  public static final String SERIALIZED_NAME_EXAMPLE = "example";
  @SerializedName(SERIALIZED_NAME_EXAMPLE)
  private String example;

  public static final String SERIALIZED_NAME_INPUTS = "inputs";
  @SerializedName(SERIALIZED_NAME_INPUTS)
  private String inputs;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public static final String SERIALIZED_NAME_DRAFT = "draft";
  @SerializedName(SERIALIZED_NAME_DRAFT)
  private String draft;

  public PromptDetailsDTO() {
  }

  public PromptDetailsDTO requestId(String requestId) {
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


  public PromptDetailsDTO promptId(Long promptId) {
    this.promptId = promptId;
    return this;
  }

   /**
   * Prompt identifier, will change after publish
   * @return promptId
  **/
  @javax.annotation.Nullable
  public Long getPromptId() {
    return promptId;
  }

  public void setPromptId(Long promptId) {
    this.promptId = promptId;
  }


  public PromptDetailsDTO promptUid(String promptUid) {
    this.promptUid = promptUid;
    return this;
  }

   /**
   * Prompt immutable identifier
   * @return promptUid
  **/
  @javax.annotation.Nullable
  public String getPromptUid() {
    return promptUid;
  }

  public void setPromptUid(String promptUid) {
    this.promptUid = promptUid;
  }


  public PromptDetailsDTO gmtCreate(OffsetDateTime gmtCreate) {
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


  public PromptDetailsDTO gmtModified(OffsetDateTime gmtModified) {
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


  public PromptDetailsDTO parentUid(String parentUid) {
    this.parentUid = parentUid;
    return this;
  }

   /**
   * Referenced prompt
   * @return parentUid
  **/
  @javax.annotation.Nullable
  public String getParentUid() {
    return parentUid;
  }

  public void setParentUid(String parentUid) {
    this.parentUid = parentUid;
  }


  public PromptDetailsDTO visibility(String visibility) {
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


  public PromptDetailsDTO version(Integer version) {
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


  public PromptDetailsDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Prompt name
   * @return name
  **/
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public PromptDetailsDTO type(String type) {
    this.type = type;
    return this;
  }

   /**
   * Prompt type: string | chat
   * @return type
  **/
  @javax.annotation.Nullable
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public PromptDetailsDTO description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Prompt description (50 characters, the excess part is represented by ellipsis)
   * @return description
  **/
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public PromptDetailsDTO format(String format) {
    this.format = format;
    return this;
  }

   /**
   * Prompt format: mustache (default) | f_string
   * @return format
  **/
  @javax.annotation.Nullable
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }


  public PromptDetailsDTO lang(String lang) {
    this.lang = lang;
    return this;
  }

   /**
   * Prompt language: en (default) | zh | ...
   * @return lang
  **/
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }


  public PromptDetailsDTO username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Prompt owner
   * @return username
  **/
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public PromptDetailsDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PromptDetailsDTO addTagsItem(String tagsItem) {
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


  public PromptDetailsDTO aiModels(List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PromptDetailsDTO addAiModelsItem(AiModelInfoDTO aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

   /**
   * Supported model set
   * @return aiModels
  **/
  @javax.annotation.Nullable
  public List<AiModelInfoDTO> getAiModels() {
    return aiModels;
  }

  public void setAiModels(List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
  }


  public PromptDetailsDTO template(String template) {
    this.template = template;
    return this;
  }

   /**
   * Prompt text template content
   * @return template
  **/
  @javax.annotation.Nullable
  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }


  public PromptDetailsDTO chatTemplate(ChatPromptContentDTO chatTemplate) {
    this.chatTemplate = chatTemplate;
    return this;
  }

   /**
   * Get chatTemplate
   * @return chatTemplate
  **/
  @javax.annotation.Nullable
  public ChatPromptContentDTO getChatTemplate() {
    return chatTemplate;
  }

  public void setChatTemplate(ChatPromptContentDTO chatTemplate) {
    this.chatTemplate = chatTemplate;
  }


  public PromptDetailsDTO example(String example) {
    this.example = example;
    return this;
  }

   /**
   * Prompt example
   * @return example
  **/
  @javax.annotation.Nullable
  public String getExample() {
    return example;
  }

  public void setExample(String example) {
    this.example = example;
  }


  public PromptDetailsDTO inputs(String inputs) {
    this.inputs = inputs;
    return this;
  }

   /**
   * Prompt inputs, JSON format
   * @return inputs
  **/
  @javax.annotation.Nullable
  public String getInputs() {
    return inputs;
  }

  public void setInputs(String inputs) {
    this.inputs = inputs;
  }


  public PromptDetailsDTO ext(String ext) {
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


  public PromptDetailsDTO draft(String draft) {
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
   * @return the PromptDetailsDTO instance itself
   */
  public PromptDetailsDTO putAdditionalProperty(String key, Object value) {
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
    PromptDetailsDTO promptDetailsDTO = (PromptDetailsDTO) o;
    return Objects.equals(this.requestId, promptDetailsDTO.requestId) &&
        Objects.equals(this.promptId, promptDetailsDTO.promptId) &&
        Objects.equals(this.promptUid, promptDetailsDTO.promptUid) &&
        Objects.equals(this.gmtCreate, promptDetailsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, promptDetailsDTO.gmtModified) &&
        Objects.equals(this.parentUid, promptDetailsDTO.parentUid) &&
        Objects.equals(this.visibility, promptDetailsDTO.visibility) &&
        Objects.equals(this.version, promptDetailsDTO.version) &&
        Objects.equals(this.name, promptDetailsDTO.name) &&
        Objects.equals(this.type, promptDetailsDTO.type) &&
        Objects.equals(this.description, promptDetailsDTO.description) &&
        Objects.equals(this.format, promptDetailsDTO.format) &&
        Objects.equals(this.lang, promptDetailsDTO.lang) &&
        Objects.equals(this.username, promptDetailsDTO.username) &&
        Objects.equals(this.tags, promptDetailsDTO.tags) &&
        Objects.equals(this.aiModels, promptDetailsDTO.aiModels) &&
        Objects.equals(this.template, promptDetailsDTO.template) &&
        Objects.equals(this.chatTemplate, promptDetailsDTO.chatTemplate) &&
        Objects.equals(this.example, promptDetailsDTO.example) &&
        Objects.equals(this.inputs, promptDetailsDTO.inputs) &&
        Objects.equals(this.ext, promptDetailsDTO.ext) &&
        Objects.equals(this.draft, promptDetailsDTO.draft)&&
        Objects.equals(this.additionalProperties, promptDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, promptId, promptUid, gmtCreate, gmtModified, parentUid, visibility, version, name, type, description, format, lang, username, tags, aiModels, template, chatTemplate, example, inputs, ext, draft, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    promptId: ").append(toIndentedString(promptId)).append("\n");
    sb.append("    promptUid: ").append(toIndentedString(promptUid)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    parentUid: ").append(toIndentedString(parentUid)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
    sb.append("    template: ").append(toIndentedString(template)).append("\n");
    sb.append("    chatTemplate: ").append(toIndentedString(chatTemplate)).append("\n");
    sb.append("    example: ").append(toIndentedString(example)).append("\n");
    sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
    sb.append("    draft: ").append(toIndentedString(draft)).append("\n");
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
    openapiFields.add("promptId");
    openapiFields.add("promptUid");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("parentUid");
    openapiFields.add("visibility");
    openapiFields.add("version");
    openapiFields.add("name");
    openapiFields.add("type");
    openapiFields.add("description");
    openapiFields.add("format");
    openapiFields.add("lang");
    openapiFields.add("username");
    openapiFields.add("tags");
    openapiFields.add("aiModels");
    openapiFields.add("template");
    openapiFields.add("chatTemplate");
    openapiFields.add("example");
    openapiFields.add("inputs");
    openapiFields.add("ext");
    openapiFields.add("draft");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to PromptDetailsDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PromptDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PromptDetailsDTO is not found in the empty JSON string", PromptDetailsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("promptUid") != null && !jsonObj.get("promptUid").isJsonNull()) && !jsonObj.get("promptUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `promptUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("promptUid").toString()));
      }
      if ((jsonObj.get("parentUid") != null && !jsonObj.get("parentUid").isJsonNull()) && !jsonObj.get("parentUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `parentUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("parentUid").toString()));
      }
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("type") != null && !jsonObj.get("type").isJsonNull()) && !jsonObj.get("type").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `type` to be a primitive type in the JSON string but got `%s`", jsonObj.get("type").toString()));
      }
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if ((jsonObj.get("format") != null && !jsonObj.get("format").isJsonNull()) && !jsonObj.get("format").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `format` to be a primitive type in the JSON string but got `%s`", jsonObj.get("format").toString()));
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
      if (jsonObj.get("aiModels") != null && !jsonObj.get("aiModels").isJsonNull()) {
        JsonArray jsonArrayaiModels = jsonObj.getAsJsonArray("aiModels");
        if (jsonArrayaiModels != null) {
          // ensure the json data is an array
          if (!jsonObj.get("aiModels").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `aiModels` to be an array in the JSON string but got `%s`", jsonObj.get("aiModels").toString()));
          }

          // validate the optional field `aiModels` (array)
          for (int i = 0; i < jsonArrayaiModels.size(); i++) {
            AiModelInfoDTO.validateJsonElement(jsonArrayaiModels.get(i));
          };
        }
      }
      if ((jsonObj.get("template") != null && !jsonObj.get("template").isJsonNull()) && !jsonObj.get("template").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `template` to be a primitive type in the JSON string but got `%s`", jsonObj.get("template").toString()));
      }
      // validate the optional field `chatTemplate`
      if (jsonObj.get("chatTemplate") != null && !jsonObj.get("chatTemplate").isJsonNull()) {
        ChatPromptContentDTO.validateJsonElement(jsonObj.get("chatTemplate"));
      }
      if ((jsonObj.get("example") != null && !jsonObj.get("example").isJsonNull()) && !jsonObj.get("example").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `example` to be a primitive type in the JSON string but got `%s`", jsonObj.get("example").toString()));
      }
      if ((jsonObj.get("inputs") != null && !jsonObj.get("inputs").isJsonNull()) && !jsonObj.get("inputs").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `inputs` to be a primitive type in the JSON string but got `%s`", jsonObj.get("inputs").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
      if ((jsonObj.get("draft") != null && !jsonObj.get("draft").isJsonNull()) && !jsonObj.get("draft").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `draft` to be a primitive type in the JSON string but got `%s`", jsonObj.get("draft").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PromptDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PromptDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PromptDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PromptDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<PromptDetailsDTO>() {
           @Override
           public void write(JsonWriter out, PromptDetailsDTO value) throws IOException {
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
           public PromptDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PromptDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of PromptDetailsDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of PromptDetailsDTO
  * @throws IOException if the JSON string is invalid with respect to PromptDetailsDTO
  */
  public static PromptDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PromptDetailsDTO.class);
  }

 /**
  * Convert an instance of PromptDetailsDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

