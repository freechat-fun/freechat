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
 * OpenAI series model parameters
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class OpenAiParamDTO {
  public static final String SERIALIZED_NAME_API_KEY = "apiKey";
  @SerializedName(SERIALIZED_NAME_API_KEY)
  private String apiKey;

  public static final String SERIALIZED_NAME_API_KEY_NAME = "apiKeyName";
  @SerializedName(SERIALIZED_NAME_API_KEY_NAME)
  private String apiKeyName;

  public static final String SERIALIZED_NAME_MODEL_ID = "modelId";
  @SerializedName(SERIALIZED_NAME_MODEL_ID)
  private String modelId;

  public static final String SERIALIZED_NAME_BASE_URL = "baseUrl";
  @SerializedName(SERIALIZED_NAME_BASE_URL)
  private String baseUrl;

  public static final String SERIALIZED_NAME_TEMPERATURE = "temperature";
  @SerializedName(SERIALIZED_NAME_TEMPERATURE)
  private Double temperature;

  public static final String SERIALIZED_NAME_TOP_P = "topP";
  @SerializedName(SERIALIZED_NAME_TOP_P)
  private Double topP;

  public static final String SERIALIZED_NAME_MAX_TOKENS = "maxTokens";
  @SerializedName(SERIALIZED_NAME_MAX_TOKENS)
  private Integer maxTokens;

  public static final String SERIALIZED_NAME_PRESENCE_PENALTY = "presencePenalty";
  @SerializedName(SERIALIZED_NAME_PRESENCE_PENALTY)
  private Double presencePenalty;

  public static final String SERIALIZED_NAME_FREQUENCY_PENALTY = "frequencyPenalty";
  @SerializedName(SERIALIZED_NAME_FREQUENCY_PENALTY)
  private Double frequencyPenalty;

  public static final String SERIALIZED_NAME_SEED = "seed";
  @SerializedName(SERIALIZED_NAME_SEED)
  private Integer seed;

  public static final String SERIALIZED_NAME_STOP = "stop";
  @SerializedName(SERIALIZED_NAME_STOP)
  private List<String> stop;

  public OpenAiParamDTO() {
  }

  public OpenAiParamDTO apiKey(String apiKey) {
    
    this.apiKey = apiKey;
    return this;
  }

   /**
   * API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.
   * @return apiKey
  **/
  @javax.annotation.Nullable
  public String getApiKey() {
    return apiKey;
  }


  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }


  public OpenAiParamDTO apiKeyName(String apiKeyName) {
    
    this.apiKeyName = apiKeyName;
    return this;
  }

   /**
   * API-KEY name
   * @return apiKeyName
  **/
  @javax.annotation.Nullable
  public String getApiKeyName() {
    return apiKeyName;
  }


  public void setApiKeyName(String apiKeyName) {
    this.apiKeyName = apiKeyName;
  }


  public OpenAiParamDTO modelId(String modelId) {
    
    this.modelId = modelId;
    return this;
  }

   /**
   * Model identifier
   * @return modelId
  **/
  @javax.annotation.Nonnull
  public String getModelId() {
    return modelId;
  }


  public void setModelId(String modelId) {
    this.modelId = modelId;
  }


  public OpenAiParamDTO baseUrl(String baseUrl) {
    
    this.baseUrl = baseUrl;
    return this;
  }

   /**
   * OpenAI service provider address, default: https://api.openai.com/v1
   * @return baseUrl
  **/
  @javax.annotation.Nullable
  public String getBaseUrl() {
    return baseUrl;
  }


  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }


  public OpenAiParamDTO temperature(Double temperature) {
    
    this.temperature = temperature;
    return this;
  }

   /**
   * Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.
   * @return temperature
  **/
  @javax.annotation.Nullable
  public Double getTemperature() {
    return temperature;
  }


  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }


  public OpenAiParamDTO topP(Double topP) {
    
    this.topP = topP;
    return this;
  }

   /**
   * Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.
   * @return topP
  **/
  @javax.annotation.Nullable
  public Double getTopP() {
    return topP;
  }


  public void setTopP(Double topP) {
    this.topP = topP;
  }


  public OpenAiParamDTO maxTokens(Integer maxTokens) {
    
    this.maxTokens = maxTokens;
    return this;
  }

   /**
   * The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model&#39;s context length.
   * @return maxTokens
  **/
  @javax.annotation.Nullable
  public Integer getMaxTokens() {
    return maxTokens;
  }


  public void setMaxTokens(Integer maxTokens) {
    this.maxTokens = maxTokens;
  }


  public OpenAiParamDTO presencePenalty(Double presencePenalty) {
    
    this.presencePenalty = presencePenalty;
    return this;
  }

   /**
   * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model&#39;s likelihood to talk about new topics.
   * @return presencePenalty
  **/
  @javax.annotation.Nullable
  public Double getPresencePenalty() {
    return presencePenalty;
  }


  public void setPresencePenalty(Double presencePenalty) {
    this.presencePenalty = presencePenalty;
  }


  public OpenAiParamDTO frequencyPenalty(Double frequencyPenalty) {
    
    this.frequencyPenalty = frequencyPenalty;
    return this;
  }

   /**
   * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model&#39;s likelihood to repeat the same line verbatim.
   * @return frequencyPenalty
  **/
  @javax.annotation.Nullable
  public Double getFrequencyPenalty() {
    return frequencyPenalty;
  }


  public void setFrequencyPenalty(Double frequencyPenalty) {
    this.frequencyPenalty = frequencyPenalty;
  }


  public OpenAiParamDTO seed(Integer seed) {
    
    this.seed = seed;
    return this;
  }

   /**
   * If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result.
   * @return seed
  **/
  @javax.annotation.Nullable
  public Integer getSeed() {
    return seed;
  }


  public void setSeed(Integer seed) {
    this.seed = seed;
  }


  public OpenAiParamDTO stop(List<String> stop) {
    
    this.stop = stop;
    return this;
  }

  public OpenAiParamDTO addStopItem(String stopItem) {
    if (this.stop == null) {
      this.stop = new ArrayList<>();
    }
    this.stop.add(stopItem);
    return this;
  }

   /**
   * A collection of stop words that controls the API from generating more tokens.
   * @return stop
  **/
  @javax.annotation.Nullable
  public List<String> getStop() {
    return stop;
  }


  public void setStop(List<String> stop) {
    this.stop = stop;
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
   * @return the OpenAiParamDTO instance itself
   */
  public OpenAiParamDTO putAdditionalProperty(String key, Object value) {
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
    OpenAiParamDTO openAiParamDTO = (OpenAiParamDTO) o;
    return Objects.equals(this.apiKey, openAiParamDTO.apiKey) &&
        Objects.equals(this.apiKeyName, openAiParamDTO.apiKeyName) &&
        Objects.equals(this.modelId, openAiParamDTO.modelId) &&
        Objects.equals(this.baseUrl, openAiParamDTO.baseUrl) &&
        Objects.equals(this.temperature, openAiParamDTO.temperature) &&
        Objects.equals(this.topP, openAiParamDTO.topP) &&
        Objects.equals(this.maxTokens, openAiParamDTO.maxTokens) &&
        Objects.equals(this.presencePenalty, openAiParamDTO.presencePenalty) &&
        Objects.equals(this.frequencyPenalty, openAiParamDTO.frequencyPenalty) &&
        Objects.equals(this.seed, openAiParamDTO.seed) &&
        Objects.equals(this.stop, openAiParamDTO.stop)&&
        Objects.equals(this.additionalProperties, openAiParamDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKey, apiKeyName, modelId, baseUrl, temperature, topP, maxTokens, presencePenalty, frequencyPenalty, seed, stop, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenAiParamDTO {\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
    sb.append("    apiKeyName: ").append(toIndentedString(apiKeyName)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    baseUrl: ").append(toIndentedString(baseUrl)).append("\n");
    sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
    sb.append("    topP: ").append(toIndentedString(topP)).append("\n");
    sb.append("    maxTokens: ").append(toIndentedString(maxTokens)).append("\n");
    sb.append("    presencePenalty: ").append(toIndentedString(presencePenalty)).append("\n");
    sb.append("    frequencyPenalty: ").append(toIndentedString(frequencyPenalty)).append("\n");
    sb.append("    seed: ").append(toIndentedString(seed)).append("\n");
    sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
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
    openapiFields.add("apiKey");
    openapiFields.add("apiKeyName");
    openapiFields.add("modelId");
    openapiFields.add("baseUrl");
    openapiFields.add("temperature");
    openapiFields.add("topP");
    openapiFields.add("maxTokens");
    openapiFields.add("presencePenalty");
    openapiFields.add("frequencyPenalty");
    openapiFields.add("seed");
    openapiFields.add("stop");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("modelId");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to OpenAiParamDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!OpenAiParamDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in OpenAiParamDTO is not found in the empty JSON string", OpenAiParamDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : OpenAiParamDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("apiKey") != null && !jsonObj.get("apiKey").isJsonNull()) && !jsonObj.get("apiKey").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKey` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKey").toString()));
      }
      if ((jsonObj.get("apiKeyName") != null && !jsonObj.get("apiKeyName").isJsonNull()) && !jsonObj.get("apiKeyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKeyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKeyName").toString()));
      }
      if (!jsonObj.get("modelId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `modelId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("modelId").toString()));
      }
      if ((jsonObj.get("baseUrl") != null && !jsonObj.get("baseUrl").isJsonNull()) && !jsonObj.get("baseUrl").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `baseUrl` to be a primitive type in the JSON string but got `%s`", jsonObj.get("baseUrl").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("stop") != null && !jsonObj.get("stop").isJsonNull() && !jsonObj.get("stop").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `stop` to be an array in the JSON string but got `%s`", jsonObj.get("stop").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!OpenAiParamDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'OpenAiParamDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<OpenAiParamDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(OpenAiParamDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<OpenAiParamDTO>() {
           @Override
           public void write(JsonWriter out, OpenAiParamDTO value) throws IOException {
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
           public OpenAiParamDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             OpenAiParamDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of OpenAiParamDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of OpenAiParamDTO
  * @throws IOException if the JSON string is invalid with respect to OpenAiParamDTO
  */
  public static OpenAiParamDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, OpenAiParamDTO.class);
  }

 /**
  * Convert an instance of OpenAiParamDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

