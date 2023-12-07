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
import fun.freechat.client.model.ChatMessageDTO;
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
 * Prompt chat template content
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ChatPromptContentDTO {
  public static final String SERIALIZED_NAME_SYSTEM = "system";
  @SerializedName(SERIALIZED_NAME_SYSTEM)
  private String system;

  public static final String SERIALIZED_NAME_MESSAGES_TO_SEND = "messagesToSend";
  @SerializedName(SERIALIZED_NAME_MESSAGES_TO_SEND)
  private ChatMessageDTO messagesToSend;

  public static final String SERIALIZED_NAME_MESSAGES = "messages";
  @SerializedName(SERIALIZED_NAME_MESSAGES)
  private List<ChatMessageDTO> messages;

  public ChatPromptContentDTO() {
  }

  public ChatPromptContentDTO system(String system) {
    
    this.system = system;
    return this;
  }

   /**
   * Prompt system template
   * @return system
  **/
  @javax.annotation.Nullable
  public String getSystem() {
    return system;
  }


  public void setSystem(String system) {
    this.system = system;
  }


  public ChatPromptContentDTO messagesToSend(ChatMessageDTO messagesToSend) {
    
    this.messagesToSend = messagesToSend;
    return this;
  }

   /**
   * Get messagesToSend
   * @return messagesToSend
  **/
  @javax.annotation.Nullable
  public ChatMessageDTO getMessagesToSend() {
    return messagesToSend;
  }


  public void setMessagesToSend(ChatMessageDTO messagesToSend) {
    this.messagesToSend = messagesToSend;
  }


  public ChatPromptContentDTO messages(List<ChatMessageDTO> messages) {
    
    this.messages = messages;
    return this;
  }

  public ChatPromptContentDTO addMessagesItem(ChatMessageDTO messagesItem) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    this.messages.add(messagesItem);
    return this;
  }

   /**
   * Pre-set chat messages in the Prompt
   * @return messages
  **/
  @javax.annotation.Nullable
  public List<ChatMessageDTO> getMessages() {
    return messages;
  }


  public void setMessages(List<ChatMessageDTO> messages) {
    this.messages = messages;
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
   * @return the ChatPromptContentDTO instance itself
   */
  public ChatPromptContentDTO putAdditionalProperty(String key, Object value) {
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
    ChatPromptContentDTO chatPromptContentDTO = (ChatPromptContentDTO) o;
    return Objects.equals(this.system, chatPromptContentDTO.system) &&
        Objects.equals(this.messagesToSend, chatPromptContentDTO.messagesToSend) &&
        Objects.equals(this.messages, chatPromptContentDTO.messages)&&
        Objects.equals(this.additionalProperties, chatPromptContentDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(system, messagesToSend, messages, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatPromptContentDTO {\n");
    sb.append("    system: ").append(toIndentedString(system)).append("\n");
    sb.append("    messagesToSend: ").append(toIndentedString(messagesToSend)).append("\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
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
    openapiFields.add("system");
    openapiFields.add("messagesToSend");
    openapiFields.add("messages");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to ChatPromptContentDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ChatPromptContentDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ChatPromptContentDTO is not found in the empty JSON string", ChatPromptContentDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("system") != null && !jsonObj.get("system").isJsonNull()) && !jsonObj.get("system").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `system` to be a primitive type in the JSON string but got `%s`", jsonObj.get("system").toString()));
      }
      // validate the optional field `messagesToSend`
      if (jsonObj.get("messagesToSend") != null && !jsonObj.get("messagesToSend").isJsonNull()) {
        ChatMessageDTO.validateJsonElement(jsonObj.get("messagesToSend"));
      }
      if (jsonObj.get("messages") != null && !jsonObj.get("messages").isJsonNull()) {
        JsonArray jsonArraymessages = jsonObj.getAsJsonArray("messages");
        if (jsonArraymessages != null) {
          // ensure the json data is an array
          if (!jsonObj.get("messages").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `messages` to be an array in the JSON string but got `%s`", jsonObj.get("messages").toString()));
          }

          // validate the optional field `messages` (array)
          for (int i = 0; i < jsonArraymessages.size(); i++) {
            ChatMessageDTO.validateJsonElement(jsonArraymessages.get(i));
          };
        }
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ChatPromptContentDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ChatPromptContentDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ChatPromptContentDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ChatPromptContentDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ChatPromptContentDTO>() {
           @Override
           public void write(JsonWriter out, ChatPromptContentDTO value) throws IOException {
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
           public ChatPromptContentDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ChatPromptContentDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of ChatPromptContentDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ChatPromptContentDTO
  * @throws IOException if the JSON string is invalid with respect to ChatPromptContentDTO
  */
  public static ChatPromptContentDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ChatPromptContentDTO.class);
  }

 /**
  * Convert an instance of ChatPromptContentDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

