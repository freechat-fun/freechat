/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.15
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
import fun.freechat.client.model.CharacterQueryWhere;
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
 * Character query request
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CharacterQueryDTO {
  public static final String SERIALIZED_NAME_WHERE = "where";
  @SerializedName(SERIALIZED_NAME_WHERE)
  private CharacterQueryWhere where;

  public static final String SERIALIZED_NAME_ORDER_BY = "orderBy";
  @SerializedName(SERIALIZED_NAME_ORDER_BY)
  private List<String> orderBy;

  public static final String SERIALIZED_NAME_PAGE_NUM = "pageNum";
  @SerializedName(SERIALIZED_NAME_PAGE_NUM)
  private Long pageNum;

  public static final String SERIALIZED_NAME_PAGE_SIZE = "pageSize";
  @SerializedName(SERIALIZED_NAME_PAGE_SIZE)
  private Long pageSize;

  public CharacterQueryDTO() {
  }

  public CharacterQueryDTO where(CharacterQueryWhere where) {
    this.where = where;
    return this;
  }

   /**
   * Get where
   * @return where
  **/
  @javax.annotation.Nullable
  public CharacterQueryWhere getWhere() {
    return where;
  }

  public void setWhere(CharacterQueryWhere where) {
    this.where = where;
  }


  public CharacterQueryDTO orderBy(List<String> orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  public CharacterQueryDTO addOrderByItem(String orderByItem) {
    if (this.orderBy == null) {
      this.orderBy = new ArrayList<>();
    }
    this.orderBy.add(orderByItem);
    return this;
  }

   /**
   * Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order) 
   * @return orderBy
  **/
  @javax.annotation.Nullable
  public List<String> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<String> orderBy) {
    this.orderBy = orderBy;
  }


  public CharacterQueryDTO pageNum(Long pageNum) {
    this.pageNum = pageNum;
    return this;
  }

   /**
   * Page number, default is 0
   * @return pageNum
  **/
  @javax.annotation.Nullable
  public Long getPageNum() {
    return pageNum;
  }

  public void setPageNum(Long pageNum) {
    this.pageNum = pageNum;
  }


  public CharacterQueryDTO pageSize(Long pageSize) {
    this.pageSize = pageSize;
    return this;
  }

   /**
   * Number of items per page, 1～50, default is 10
   * @return pageSize
  **/
  @javax.annotation.Nullable
  public Long getPageSize() {
    return pageSize;
  }

  public void setPageSize(Long pageSize) {
    this.pageSize = pageSize;
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
   * @return the CharacterQueryDTO instance itself
   */
  public CharacterQueryDTO putAdditionalProperty(String key, Object value) {
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
    CharacterQueryDTO characterQueryDTO = (CharacterQueryDTO) o;
    return Objects.equals(this.where, characterQueryDTO.where) &&
        Objects.equals(this.orderBy, characterQueryDTO.orderBy) &&
        Objects.equals(this.pageNum, characterQueryDTO.pageNum) &&
        Objects.equals(this.pageSize, characterQueryDTO.pageSize)&&
        Objects.equals(this.additionalProperties, characterQueryDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(where, orderBy, pageNum, pageSize, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterQueryDTO {\n");
    sb.append("    where: ").append(toIndentedString(where)).append("\n");
    sb.append("    orderBy: ").append(toIndentedString(orderBy)).append("\n");
    sb.append("    pageNum: ").append(toIndentedString(pageNum)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
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
    openapiFields.add("where");
    openapiFields.add("orderBy");
    openapiFields.add("pageNum");
    openapiFields.add("pageSize");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to CharacterQueryDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterQueryDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterQueryDTO is not found in the empty JSON string", CharacterQueryDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      // validate the optional field `where`
      if (jsonObj.get("where") != null && !jsonObj.get("where").isJsonNull()) {
        CharacterQueryWhere.validateJsonElement(jsonObj.get("where"));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("orderBy") != null && !jsonObj.get("orderBy").isJsonNull() && !jsonObj.get("orderBy").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `orderBy` to be an array in the JSON string but got `%s`", jsonObj.get("orderBy").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterQueryDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterQueryDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterQueryDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterQueryDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterQueryDTO>() {
           @Override
           public void write(JsonWriter out, CharacterQueryDTO value) throws IOException {
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
           public CharacterQueryDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterQueryDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of CharacterQueryDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of CharacterQueryDTO
  * @throws IOException if the JSON string is invalid with respect to CharacterQueryDTO
  */
  public static CharacterQueryDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterQueryDTO.class);
  }

 /**
  * Convert an instance of CharacterQueryDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

