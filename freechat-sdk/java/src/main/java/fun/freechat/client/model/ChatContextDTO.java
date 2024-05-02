/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory** for characters. - Supports setting **quota limits** for characters.  ## System Snapshots  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up a unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml bitnami:   mysql:     enabled: false   redis:     enabled: false   milvus:     enabled: false  mysql:   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   url: <your redis url>   auth:     password: <your redis password>   milvus:   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### 使用 SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [DashScope](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI and DashScope ([HuggingFace](https://huggingface.co/) is also expected to be supported soon). However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future.
 *
 * The version of the OpenAPI document: 1.0.2
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Chat context
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.5.0")
public class ChatContextDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  private String requestId;

  public static final String SERIALIZED_NAME_CHAT_ID = "chatId";
  @SerializedName(SERIALIZED_NAME_CHAT_ID)
  private String chatId;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_GMT_READ = "gmtRead";
  @SerializedName(SERIALIZED_NAME_GMT_READ)
  private OffsetDateTime gmtRead;

  public static final String SERIALIZED_NAME_USER_NICKNAME = "userNickname";
  @SerializedName(SERIALIZED_NAME_USER_NICKNAME)
  private String userNickname;

  public static final String SERIALIZED_NAME_USER_PROFILE = "userProfile";
  @SerializedName(SERIALIZED_NAME_USER_PROFILE)
  private String userProfile;

  public static final String SERIALIZED_NAME_CHARACTER_NICKNAME = "characterNickname";
  @SerializedName(SERIALIZED_NAME_CHARACTER_NICKNAME)
  private String characterNickname;

  public static final String SERIALIZED_NAME_ABOUT = "about";
  @SerializedName(SERIALIZED_NAME_ABOUT)
  private String about;

  public static final String SERIALIZED_NAME_BACKEND_ID = "backendId";
  @SerializedName(SERIALIZED_NAME_BACKEND_ID)
  private String backendId;

  public static final String SERIALIZED_NAME_API_KEY_NAME = "apiKeyName";
  @SerializedName(SERIALIZED_NAME_API_KEY_NAME)
  private String apiKeyName;

  public static final String SERIALIZED_NAME_API_KEY_VALUE = "apiKeyValue";
  @SerializedName(SERIALIZED_NAME_API_KEY_VALUE)
  private String apiKeyValue;

  public static final String SERIALIZED_NAME_QUOTA = "quota";
  @SerializedName(SERIALIZED_NAME_QUOTA)
  private Long quota;

  public static final String SERIALIZED_NAME_QUOTA_TYPE = "quotaType";
  @SerializedName(SERIALIZED_NAME_QUOTA_TYPE)
  private String quotaType;

  public static final String SERIALIZED_NAME_EXT = "ext";
  @SerializedName(SERIALIZED_NAME_EXT)
  private String ext;

  public ChatContextDTO() {
  }

  public ChatContextDTO requestId(String requestId) {
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


  public ChatContextDTO chatId(String chatId) {
    this.chatId = chatId;
    return this;
  }

   /**
   * Chat identifier
   * @return chatId
  **/
  @javax.annotation.Nullable
  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }


  public ChatContextDTO gmtCreate(OffsetDateTime gmtCreate) {
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


  public ChatContextDTO gmtModified(OffsetDateTime gmtModified) {
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


  public ChatContextDTO gmtRead(OffsetDateTime gmtRead) {
    this.gmtRead = gmtRead;
    return this;
  }

   /**
   * Read time
   * @return gmtRead
  **/
  @javax.annotation.Nullable
  public OffsetDateTime getGmtRead() {
    return gmtRead;
  }

  public void setGmtRead(OffsetDateTime gmtRead) {
    this.gmtRead = gmtRead;
  }


  public ChatContextDTO userNickname(String userNickname) {
    this.userNickname = userNickname;
    return this;
  }

   /**
   * User nickname for this session
   * @return userNickname
  **/
  @javax.annotation.Nullable
  public String getUserNickname() {
    return userNickname;
  }

  public void setUserNickname(String userNickname) {
    this.userNickname = userNickname;
  }


  public ChatContextDTO userProfile(String userProfile) {
    this.userProfile = userProfile;
    return this;
  }

   /**
   * User profile for this session
   * @return userProfile
  **/
  @javax.annotation.Nullable
  public String getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(String userProfile) {
    this.userProfile = userProfile;
  }


  public ChatContextDTO characterNickname(String characterNickname) {
    this.characterNickname = characterNickname;
    return this;
  }

   /**
   * Character nickname for this session
   * @return characterNickname
  **/
  @javax.annotation.Nullable
  public String getCharacterNickname() {
    return characterNickname;
  }

  public void setCharacterNickname(String characterNickname) {
    this.characterNickname = characterNickname;
  }


  public ChatContextDTO about(String about) {
    this.about = about;
    return this;
  }

   /**
   * Anything about this session
   * @return about
  **/
  @javax.annotation.Nullable
  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }


  public ChatContextDTO backendId(String backendId) {
    this.backendId = backendId;
    return this;
  }

   /**
   * Character backend for this session
   * @return backendId
  **/
  @javax.annotation.Nonnull
  public String getBackendId() {
    return backendId;
  }

  public void setBackendId(String backendId) {
    this.backendId = backendId;
  }


  public ChatContextDTO apiKeyName(String apiKeyName) {
    this.apiKeyName = apiKeyName;
    return this;
  }

   /**
   * API-KEY name, priority: apiKeyName &gt; apiKeyValue
   * @return apiKeyName
  **/
  @javax.annotation.Nullable
  public String getApiKeyName() {
    return apiKeyName;
  }

  public void setApiKeyName(String apiKeyName) {
    this.apiKeyName = apiKeyName;
  }


  public ChatContextDTO apiKeyValue(String apiKeyValue) {
    this.apiKeyValue = apiKeyValue;
    return this;
  }

   /**
   * API-KEY value
   * @return apiKeyValue
  **/
  @javax.annotation.Nullable
  public String getApiKeyValue() {
    return apiKeyValue;
  }

  public void setApiKeyValue(String apiKeyValue) {
    this.apiKeyValue = apiKeyValue;
  }


  public ChatContextDTO quota(Long quota) {
    this.quota = quota;
    return this;
  }

   /**
   * Quota of this chat
   * @return quota
  **/
  @javax.annotation.Nullable
  public Long getQuota() {
    return quota;
  }

  public void setQuota(Long quota) {
    this.quota = quota;
  }


  public ChatContextDTO quotaType(String quotaType) {
    this.quotaType = quotaType;
    return this;
  }

   /**
   * Quota type: messages | tokens | none (not limited)
   * @return quotaType
  **/
  @javax.annotation.Nullable
  public String getQuotaType() {
    return quotaType;
  }

  public void setQuotaType(String quotaType) {
    this.quotaType = quotaType;
  }


  public ChatContextDTO ext(String ext) {
    this.ext = ext;
    return this;
  }

   /**
   * Extra info for this session
   * @return ext
  **/
  @javax.annotation.Nullable
  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
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
   * @return the ChatContextDTO instance itself
   */
  public ChatContextDTO putAdditionalProperty(String key, Object value) {
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
    ChatContextDTO chatContextDTO = (ChatContextDTO) o;
    return Objects.equals(this.requestId, chatContextDTO.requestId) &&
        Objects.equals(this.chatId, chatContextDTO.chatId) &&
        Objects.equals(this.gmtCreate, chatContextDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, chatContextDTO.gmtModified) &&
        Objects.equals(this.gmtRead, chatContextDTO.gmtRead) &&
        Objects.equals(this.userNickname, chatContextDTO.userNickname) &&
        Objects.equals(this.userProfile, chatContextDTO.userProfile) &&
        Objects.equals(this.characterNickname, chatContextDTO.characterNickname) &&
        Objects.equals(this.about, chatContextDTO.about) &&
        Objects.equals(this.backendId, chatContextDTO.backendId) &&
        Objects.equals(this.apiKeyName, chatContextDTO.apiKeyName) &&
        Objects.equals(this.apiKeyValue, chatContextDTO.apiKeyValue) &&
        Objects.equals(this.quota, chatContextDTO.quota) &&
        Objects.equals(this.quotaType, chatContextDTO.quotaType) &&
        Objects.equals(this.ext, chatContextDTO.ext)&&
        Objects.equals(this.additionalProperties, chatContextDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, chatId, gmtCreate, gmtModified, gmtRead, userNickname, userProfile, characterNickname, about, backendId, apiKeyName, apiKeyValue, quota, quotaType, ext, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatContextDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    chatId: ").append(toIndentedString(chatId)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    gmtRead: ").append(toIndentedString(gmtRead)).append("\n");
    sb.append("    userNickname: ").append(toIndentedString(userNickname)).append("\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    characterNickname: ").append(toIndentedString(characterNickname)).append("\n");
    sb.append("    about: ").append(toIndentedString(about)).append("\n");
    sb.append("    backendId: ").append(toIndentedString(backendId)).append("\n");
    sb.append("    apiKeyName: ").append(toIndentedString(apiKeyName)).append("\n");
    sb.append("    apiKeyValue: ").append(toIndentedString(apiKeyValue)).append("\n");
    sb.append("    quota: ").append(toIndentedString(quota)).append("\n");
    sb.append("    quotaType: ").append(toIndentedString(quotaType)).append("\n");
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
    openapiFields.add("requestId");
    openapiFields.add("chatId");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("gmtRead");
    openapiFields.add("userNickname");
    openapiFields.add("userProfile");
    openapiFields.add("characterNickname");
    openapiFields.add("about");
    openapiFields.add("backendId");
    openapiFields.add("apiKeyName");
    openapiFields.add("apiKeyValue");
    openapiFields.add("quota");
    openapiFields.add("quotaType");
    openapiFields.add("ext");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("backendId");
  }

 /**
  * Validates the JSON Element and throws an exception if issues found
  *
  * @param jsonElement JSON Element
  * @throws IOException if the JSON Element is invalid with respect to ChatContextDTO
  */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ChatContextDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ChatContextDTO is not found in the empty JSON string", ChatContextDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : ChatContextDTO.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("chatId") != null && !jsonObj.get("chatId").isJsonNull()) && !jsonObj.get("chatId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatId").toString()));
      }
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
      if (!jsonObj.get("backendId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `backendId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("backendId").toString()));
      }
      if ((jsonObj.get("apiKeyName") != null && !jsonObj.get("apiKeyName").isJsonNull()) && !jsonObj.get("apiKeyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKeyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKeyName").toString()));
      }
      if ((jsonObj.get("apiKeyValue") != null && !jsonObj.get("apiKeyValue").isJsonNull()) && !jsonObj.get("apiKeyValue").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiKeyValue` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiKeyValue").toString()));
      }
      if ((jsonObj.get("quotaType") != null && !jsonObj.get("quotaType").isJsonNull()) && !jsonObj.get("quotaType").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `quotaType` to be a primitive type in the JSON string but got `%s`", jsonObj.get("quotaType").toString()));
      }
      if ((jsonObj.get("ext") != null && !jsonObj.get("ext").isJsonNull()) && !jsonObj.get("ext").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ext` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ext").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ChatContextDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ChatContextDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ChatContextDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ChatContextDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<ChatContextDTO>() {
           @Override
           public void write(JsonWriter out, ChatContextDTO value) throws IOException {
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
           public ChatContextDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             ChatContextDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
  * Create an instance of ChatContextDTO given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ChatContextDTO
  * @throws IOException if the JSON string is invalid with respect to ChatContextDTO
  */
  public static ChatContextDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ChatContextDTO.class);
  }

 /**
  * Convert an instance of ChatContextDTO to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

