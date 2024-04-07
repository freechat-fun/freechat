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
import fun.freechat.client.model.ChatContentDTO;
import fun.freechat.client.model.ChatToolCallDTO;
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
 * Chat message
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ChatMessageDTO {
  public static final String SERIALIZED_NAME_ROLE = "role";
  @SerializedName(SERIALIZED_NAME_ROLE)
  private String role;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_CONTENTS = "contents";
  @SerializedName(SERIALIZED_NAME_CONTENTS)
  private List<ChatContentDTO> contents;

  public static final String SERIALIZED_NAME_TOOL_CALLS = "toolCalls";
  @SerializedName(SERIALIZED_NAME_TOOL_CALLS)
  private List<ChatToolCallDTO> toolCalls;

  public static final String SERIALIZED_NAME_CONTEXT = "context";
  @SerializedName(SERIALIZED_NAME_CONTEXT)
  private String context;

  public static final String SERIALIZED_NAME_CONTENT_TEXT = "contentText";
  @SerializedName(SERIALIZED_NAME_CONTENT_TEXT)
  private String contentText;

  public ChatMessageDTO() {
  }

  public ChatMessageDTO role(String role) {
    this.role = role;
    return this;
  }

   /**
   * Chat role: system | assistant | user | tool_call | tool_result
   * @return role
  **/
  @javax.annotation.Nullable
  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }


  public ChatMessageDTO name(String name) {
    this.name = name;
    return this;
  }

   /**
   * user: Name of the user role; tool_call: Name of the called tool
   * @return name
  **/
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public ChatMessageDTO contents(List<ChatContentDTO> contents) {
    this.contents = contents;
    return this;
  }

  public ChatMessageDTO addContentsItem(ChatContentDTO contentsItem) {
    if (this.contents == null) {
      this.contents = new ArrayList<>();
    }
    this.contents.add(contentsItem);
    return this;
  }

   /**
   * default: Dialogue content; tool_result: tool call result, serialized as json
   * @return contents
  **/
  @javax.annotation.Nullable
  public List<ChatContentDTO> getContents() {
    return contents;
  }

  public void setContents(List<ChatContentDTO> contents) {
    this.contents = contents;
  }


  public ChatMessageDTO toolCalls(List<ChatToolCallDTO> toolCalls) {
    this.toolCalls = toolCalls;
    return this;
  }

  public ChatMessageDTO addToolCallsItem(ChatToolCallDTO toolCallsItem) {
    if (this.toolCalls == null) {
      this.toolCalls = new ArrayList<>();
    }
    this.toolCalls.add(toolCallsItem);
    return this;
  }

   /**
   * Tool calls information during the conversation
   * @return toolCalls
  **/
  @javax.annotation.Nullable
  public List<ChatToolCallDTO> getToolCalls() {
    return toolCalls;
  }

  public void setToolCalls(List<ChatToolCallDTO> toolCalls) {
    this.toolCalls = toolCalls;
  }


  public ChatMessageDTO context(String context) {
    this.context = context;
    return this;
  }

   /**
   * Contextual information in this round of conversation (the external RAG result can be passed in through this parameter)
   * @return context
  **/
  @javax.annotation.Nullable
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }


  public ChatMessageDTO contentText(String contentText) {
    this.contentText = contentText;
    return this;
  }

   /**
   * Get contentText
   * @return contentText
  **/
  @javax.annotation.Nullable
  public String getContentText() {
    return contentText;
  }

  public void setContentText(String contentText) {
    this.contentText = contentText;
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
   * @return the ChatMessageDTO instance itself
   */
  public ChatMessageDTO putAdditionalProperty(String key, Object value) {
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
    ChatMessageDTO chatMessageDTO = (ChatMessageDTO) o;
    return Objects.equals(this.role, chatMessageDTO.role) &&
        Objects.equals(this.name, chatMessageDTO.name) &&
        Objects.equals(this.contents, chatMessageDTO.contents) &&
        Objects.equals(this.toolCalls, chatMessageDTO.toolCalls) &&
        Objects.equals(this.context, chatMessageDTO.context) &&
        Objects.equals(this.contentText, chatMessageDTO.contentText)&&
        Objects.equals(this.additionalProperties, chatMessageDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, name, contents, toolCalls, context, contentText, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatMessageDTO {\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    contents: ").append(toIndentedString(contents)).append("\n");
    sb.append("    toolCalls: ").append(toIndentedString(toolCalls)).append("\n");
    sb.append("    context: ").append(toIndentedString(context)).append("\n");
    sb.append("    contentText: ").append(toIndentedString(contentText)).append("\n");
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
    openapiFields.add("role");
    openapiFields.add("name");
    openapiFields.add("contents");
    openapiFields.add("toolCalls");
    openapiFields.add("context");
    openapiFields.add("contentText");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to ChatMessageDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ChatMessageDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ChatMessageDTO is not found in the empty JSON string", ChatMessageDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("role") != null && !jsonObj.get("role").isJsonNull()) && !jsonObj.get("role").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `role` to be a primitive type in the JSON string but got `%s`", jsonObj.get("role").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if (jsonObj.get("contents") != null && !jsonObj.get("contents").isJsonNull()) {
        JsonArray jsonArraycontents = jsonObj.getAsJsonArray("contents");
        if (jsonArraycontents != null) {
          // ensure the json data is an array
          if (!jsonObj.get("contents").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `contents` to be an array in the JSON string but got `%s`", jsonObj.get("contents").toString()));
          }

          // validate the optional field `contents` (array)
          for (int i = 0; i < jsonArraycontents.size(); i++) {
            ChatContentDTO.validateJsonElement(jsonArraycontents.get(i));
          };
        }
      }
      if (jsonObj.get("toolCalls") != null && !jsonObj.get("toolCalls").isJsonNull()) {
        JsonArray jsonArraytoolCalls = jsonObj.getAsJsonArray("toolCalls");
        if (jsonArraytoolCalls != null) {
          // ensure the json data is an array
          if (!jsonObj.get("toolCalls").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `toolCalls` to be an array in the JSON string but got `%s`", jsonObj.get("toolCalls").toString()));
          }

          // validate the optional field `toolCalls` (array)
          for (int i = 0; i < jsonArraytoolCalls.size(); i++) {
            ChatToolCallDTO.validateJsonElement(jsonArraytoolCalls.get(i));
          };
        }
      }
      if ((jsonObj.get("context") != null && !jsonObj.get("context").isJsonNull()) && !jsonObj.get("context").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `context` to be a primitive type in the JSON string but got `%s`", jsonObj.get("context").toString()));
      }
      if ((jsonObj.get("contentText") != null && !jsonObj.get("contentText").isJsonNull()) && !jsonObj.get("contentText").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `contentText` to be a primitive type in the JSON string but got `%s`", jsonObj.get("contentText").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ChatMessageDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ChatMessageDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ChatMessageDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ChatMessageDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ChatMessageDTO>() {
           @Override
           public void write(JsonWriter out, ChatMessageDTO value) throws IOException {
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
           public ChatMessageDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ChatMessageDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of ChatMessageDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ChatMessageDTO
  * @throws IOException if the JSON string is invalid with respect to ChatMessageDTO
  */
  public static ChatMessageDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ChatMessageDTO.class);
  }

 /**
  * Convert an instance of ChatMessageDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

