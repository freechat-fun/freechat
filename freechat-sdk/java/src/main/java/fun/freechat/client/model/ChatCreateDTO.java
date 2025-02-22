/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports characters replies with **mixed text and image information**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.3.0
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
import java.util.Arrays;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Request data for starting a chat session
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.11.0")
public class ChatCreateDTO {
  public static final String SERIALIZED_NAME_USER_NICKNAME = "userNickname";
  @SerializedName(SERIALIZED_NAME_USER_NICKNAME)
  @javax.annotation.Nullable
  private String userNickname;

  public static final String SERIALIZED_NAME_USER_PROFILE = "userProfile";
  @SerializedName(SERIALIZED_NAME_USER_PROFILE)
  @javax.annotation.Nullable
  private String userProfile;

  public static final String SERIALIZED_NAME_CHARACTER_NICKNAME = "characterNickname";
  @SerializedName(SERIALIZED_NAME_CHARACTER_NICKNAME)
  @javax.annotation.Nullable
  private String characterNickname;

  public static final String SERIALIZED_NAME_ABOUT = "about";
  @SerializedName(SERIALIZED_NAME_ABOUT)
  @javax.annotation.Nullable
  private String about;

  public static final String SERIALIZED_NAME_CHARACTER_UID = "characterUid";
  @SerializedName(SERIALIZED_NAME_CHARACTER_UID)
  @javax.annotation.Nonnull
  private String characterUid;

  public static final String SERIALIZED_NAME_BACKEND_ID = "backendId";
  @SerializedName(SERIALIZED_NAME_BACKEND_ID)
  @javax.annotation.Nullable
  private String backendId;

  public static final String SERIALIZED_NAME_API_KEY_NAME = "apiKeyName";
  @SerializedName(SERIALIZED_NAME_API_KEY_NAME)
  @javax.annotation.Nullable
  private String apiKeyName;

  public static final String SERIALIZED_NAME_API_KEY_VALUE = "apiKeyValue";
  @SerializedName(SERIALIZED_NAME_API_KEY_VALUE)
  @javax.annotation.Nullable
  private String apiKeyValue;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  @javax.annotation.Nullable
  private String ext;

  public ChatCreateDTO() {
  }

  public ChatCreateDTO userNickname(@javax.annotation.Nullable String userNickname) {
    this.userNickname = userNickname;
    return this;
  }

  /**
   * User nickname for this session
   * @return userNickname
   */
  @javax.annotation.Nullable
  public String getUserNickname() {
    return userNickname;
  }

  public void setUserNickname(@javax.annotation.Nullable String userNickname) {
    this.userNickname = userNickname;
  }


  public ChatCreateDTO userProfile(@javax.annotation.Nullable String userProfile) {
    this.userProfile = userProfile;
    return this;
  }

  /**
   * User profile for this session
   * @return userProfile
   */
  @javax.annotation.Nullable
  public String getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(@javax.annotation.Nullable String userProfile) {
    this.userProfile = userProfile;
  }


  public ChatCreateDTO characterNickname(@javax.annotation.Nullable String characterNickname) {
    this.characterNickname = characterNickname;
    return this;
  }

  /**
   * Character nickname for this session
   * @return characterNickname
   */
  @javax.annotation.Nullable
  public String getCharacterNickname() {
    return characterNickname;
  }

  public void setCharacterNickname(@javax.annotation.Nullable String characterNickname) {
    this.characterNickname = characterNickname;
  }


  public ChatCreateDTO about(@javax.annotation.Nullable String about) {
    this.about = about;
    return this;
  }

  /**
   * Anything about this session
   * @return about
   */
  @javax.annotation.Nullable
  public String getAbout() {
    return about;
  }

  public void setAbout(@javax.annotation.Nullable String about) {
    this.about = about;
  }


  public ChatCreateDTO characterUid(@javax.annotation.Nonnull String characterUid) {
    this.characterUid = characterUid;
    return this;
  }

  /**
   * Character uid for this session
   * @return characterUid
   */
  @javax.annotation.Nonnull
  public String getCharacterUid() {
    return characterUid;
  }

  public void setCharacterUid(@javax.annotation.Nonnull String characterUid) {
    this.characterUid = characterUid;
  }


  public ChatCreateDTO backendId(@javax.annotation.Nullable String backendId) {
    this.backendId = backendId;
    return this;
  }

  /**
   * Character backend for this session
   * @return backendId
   */
  @javax.annotation.Nullable
  public String getBackendId() {
    return backendId;
  }

  public void setBackendId(@javax.annotation.Nullable String backendId) {
    this.backendId = backendId;
  }


  public ChatCreateDTO apiKeyName(@javax.annotation.Nullable String apiKeyName) {
    this.apiKeyName = apiKeyName;
    return this;
  }

  /**
   * API-KEY name, priority: apiKeyName &gt; apiKeyValue
   * @return apiKeyName
   */
  @javax.annotation.Nullable
  public String getApiKeyName() {
    return apiKeyName;
  }

  public void setApiKeyName(@javax.annotation.Nullable String apiKeyName) {
    this.apiKeyName = apiKeyName;
  }


  public ChatCreateDTO apiKeyValue(@javax.annotation.Nullable String apiKeyValue) {
    this.apiKeyValue = apiKeyValue;
    return this;
  }

  /**
   * API-KEY value
   * @return apiKeyValue
   */
  @javax.annotation.Nullable
  public String getApiKeyValue() {
    return apiKeyValue;
  }

  public void setApiKeyValue(@javax.annotation.Nullable String apiKeyValue) {
    this.apiKeyValue = apiKeyValue;
  }


  public ChatCreateDTO ext(@javax.annotation.Nullable String ext) {
    this.ext = ext;
    return this;
  }

  /**
   * Extra info for this session
   * @return ext
   */
  @javax.annotation.Nullable
  public String getExt() {
    return ext;
  }

  public void setExt(@javax.annotation.Nullable String ext) {
    this.ext = ext;
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
   * @return the ChatCreateDTO instance itself
   */
  public ChatCreateDTO putAdditionalProperty(String key, Object value) {
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
    ChatCreateDTO chatCreateDTO = (ChatCreateDTO) o;
    return Objects.equals(this.userNickname, chatCreateDTO.userNickname) &&
        Objects.equals(this.userProfile, chatCreateDTO.userProfile) &&
        Objects.equals(this.characterNickname, chatCreateDTO.characterNickname) &&
        Objects.equals(this.about, chatCreateDTO.about) &&
        Objects.equals(this.characterUid, chatCreateDTO.characterUid) &&
        Objects.equals(this.backendId, chatCreateDTO.backendId) &&
        Objects.equals(this.apiKeyName, chatCreateDTO.apiKeyName) &&
        Objects.equals(this.apiKeyValue, chatCreateDTO.apiKeyValue) &&
        Objects.equals(this.ext, chatCreateDTO.ext)&&
        Objects.equals(this.additionalProperties, chatCreateDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userNickname, userProfile, characterNickname, about, characterUid, backendId, apiKeyName, apiKeyValue, ext, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatCreateDTO {\n");
    sb.append("    userNickname: ").append(toIndentedString(userNickname)).append("\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    characterNickname: ").append(toIndentedString(characterNickname)).append("\n");
    sb.append("    about: ").append(toIndentedString(about)).append("\n");
    sb.append("    characterUid: ").append(toIndentedString(characterUid)).append("\n");
    sb.append("    backendId: ").append(toIndentedString(backendId)).append("\n");
    sb.append("    apiKeyName: ").append(toIndentedString(apiKeyName)).append("\n");
    sb.append("    apiKeyValue: ").append(toIndentedString(apiKeyValue)).append("\n");
    sb.append("    ext: ").append(toIndentedString(ext)).append("\n");
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
    openapiFields.add("userNickname");
    openapiFields.add("userProfile");
    openapiFields.add("characterNickname");
    openapiFields.add("about");
    openapiFields.add("characterUid");
    openapiFields.add("backendId");
    openapiFields.add("apiKeyName");
    openapiFields.add("apiKeyValue");
    openapiFields.add("ext");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("characterUid");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ChatCreateDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ChatCreateDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ChatCreateDTO is not found in the empty JSON string", ChatCreateDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : ChatCreateDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("userNickname") != null && !jsonObj.get("userNickname").isJsonNull()) && !jsonObj.get("userNickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `userNickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("userNickname").toString()));
      }
      if ((jsonObj.get("userProfile") != null && !jsonObj.get("userProfile").isJsonNull()) && !jsonObj.get("userProfile").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `userProfile` to be a primitive type in the JSON string but got `%s`", jsonObj.get("userProfile").toString()));
      }
      if ((jsonObj.get("characterNickname") != null && !jsonObj.get("characterNickname").isJsonNull()) && !jsonObj.get("characterNickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterNickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterNickname").toString()));
      }
      if ((jsonObj.get("about") != null && !jsonObj.get("about").isJsonNull()) && !jsonObj.get("about").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `about` to be a primitive type in the JSON string but got `%s`", jsonObj.get("about").toString()));
      }
      if (!jsonObj.get("characterUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterUid").toString()));
      }
      if ((jsonObj.get("backendId") != null && !jsonObj.get("backendId").isJsonNull()) && !jsonObj.get("backendId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `backendId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("backendId").toString()));
      }
      if ((jsonObj.get("apiKeyName") != null && !jsonObj.get("apiKeyName").isJsonNull()) && !jsonObj.get("apiKeyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKeyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKeyName").toString()));
      }
      if ((jsonObj.get("apiKeyValue") != null && !jsonObj.get("apiKeyValue").isJsonNull()) && !jsonObj.get("apiKeyValue").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKeyValue` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKeyValue").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ChatCreateDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ChatCreateDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ChatCreateDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ChatCreateDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ChatCreateDTO>() {
           @Override
           public void write(JsonWriter out, ChatCreateDTO value) throws IOException {
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
           public ChatCreateDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ChatCreateDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of ChatCreateDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ChatCreateDTO
   * @throws IOException if the JSON string is invalid with respect to ChatCreateDTO
   */
  public static ChatCreateDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ChatCreateDTO.class);
  }

  /**
   * Convert an instance of ChatCreateDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

