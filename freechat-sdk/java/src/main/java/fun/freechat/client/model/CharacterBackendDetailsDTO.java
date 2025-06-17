/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports characters replies with **mixed text and image information**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](https://freechat.fun/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](https://freechat.fun/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](https://freechat.fun/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](https://freechat.fun/img/snapshot_m1.jpg) ![Chat Snapshot 2](https://freechat.fun/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](https://freechat.fun/img/snapshot_m3.jpg) ![Chat Snapshot 4](https://freechat.fun/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.5.0
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Character backend detailed information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.11.0")
public class CharacterBackendDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  @javax.annotation.Nullable
  private String requestId;

  public static final String SERIALIZED_NAME_BACKEND_ID = "backendId";
  @SerializedName(SERIALIZED_NAME_BACKEND_ID)
  @javax.annotation.Nullable
  private String backendId;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  @javax.annotation.Nullable
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  @javax.annotation.Nullable
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_CHARACTER_UID = "characterUid";
  @SerializedName(SERIALIZED_NAME_CHARACTER_UID)
  @javax.annotation.Nullable
  private String characterUid;

  public static final String SERIALIZED_NAME_IS_DEFAULT = "isDefault";
  @SerializedName(SERIALIZED_NAME_IS_DEFAULT)
  @javax.annotation.Nullable
  private Boolean isDefault;

  public static final String SERIALIZED_NAME_CHAT_PROMPT_TASK_ID = "chatPromptTaskId";
  @SerializedName(SERIALIZED_NAME_CHAT_PROMPT_TASK_ID)
  @javax.annotation.Nullable
  private String chatPromptTaskId;

  public static final String SERIALIZED_NAME_GREETING_PROMPT_TASK_ID = "greetingPromptTaskId";
  @SerializedName(SERIALIZED_NAME_GREETING_PROMPT_TASK_ID)
  @javax.annotation.Nullable
  private String greetingPromptTaskId;

  public static final String SERIALIZED_NAME_MODERATION_MODEL_ID = "moderationModelId";
  @SerializedName(SERIALIZED_NAME_MODERATION_MODEL_ID)
  @javax.annotation.Nullable
  private String moderationModelId;

  public static final String SERIALIZED_NAME_MODERATION_API_KEY_NAME = "moderationApiKeyName";
  @SerializedName(SERIALIZED_NAME_MODERATION_API_KEY_NAME)
  @javax.annotation.Nullable
  private String moderationApiKeyName;

  public static final String SERIALIZED_NAME_MODERATION_PARAMS = "moderationParams";
  @SerializedName(SERIALIZED_NAME_MODERATION_PARAMS)
  @javax.annotation.Nullable
  private String moderationParams;

  public static final String SERIALIZED_NAME_MESSAGE_WINDOW_SIZE = "messageWindowSize";
  @SerializedName(SERIALIZED_NAME_MESSAGE_WINDOW_SIZE)
  @javax.annotation.Nullable
  private Integer messageWindowSize;

  public static final String SERIALIZED_NAME_LONG_TERM_MEMORY_WINDOW_SIZE = "longTermMemoryWindowSize";
  @SerializedName(SERIALIZED_NAME_LONG_TERM_MEMORY_WINDOW_SIZE)
  @javax.annotation.Nullable
  private Integer longTermMemoryWindowSize;

  public static final String SERIALIZED_NAME_PROACTIVE_CHAT_WAITING_TIME = "proactiveChatWaitingTime";
  @SerializedName(SERIALIZED_NAME_PROACTIVE_CHAT_WAITING_TIME)
  @javax.annotation.Nullable
  private Integer proactiveChatWaitingTime;

  public static final String SERIALIZED_NAME_INIT_QUOTA = "initQuota";
  @SerializedName(SERIALIZED_NAME_INIT_QUOTA)
  @javax.annotation.Nullable
  private Long initQuota;

  public static final String SERIALIZED_NAME_QUOTA_TYPE = "quotaType";
  @SerializedName(SERIALIZED_NAME_QUOTA_TYPE)
  @javax.annotation.Nullable
  private String quotaType;

  public static final String SERIALIZED_NAME_ENABLE_ALBUM_TOOL = "enableAlbumTool";
  @SerializedName(SERIALIZED_NAME_ENABLE_ALBUM_TOOL)
  @javax.annotation.Nullable
  private Boolean enableAlbumTool;

  public static final String SERIALIZED_NAME_ENABLE_TTS = "enableTts";
  @SerializedName(SERIALIZED_NAME_ENABLE_TTS)
  @javax.annotation.Nullable
  private Boolean enableTts;

  public static final String SERIALIZED_NAME_TTS_SPEAKER_IDX = "ttsSpeakerIdx";
  @SerializedName(SERIALIZED_NAME_TTS_SPEAKER_IDX)
  @javax.annotation.Nullable
  private String ttsSpeakerIdx;

  public static final String SERIALIZED_NAME_TTS_SPEAKER_WAV = "ttsSpeakerWav";
  @SerializedName(SERIALIZED_NAME_TTS_SPEAKER_WAV)
  @javax.annotation.Nullable
  private String ttsSpeakerWav;

  public static final String SERIALIZED_NAME_TTS_SPEAKER_TYPE = "ttsSpeakerType";
  @SerializedName(SERIALIZED_NAME_TTS_SPEAKER_TYPE)
  @javax.annotation.Nullable
  private String ttsSpeakerType;

  public CharacterBackendDetailsDTO() {
  }

  public CharacterBackendDetailsDTO requestId(@javax.annotation.Nullable String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Request identifier
   * @return requestId
   */
  @javax.annotation.Nullable
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(@javax.annotation.Nullable String requestId) {
    this.requestId = requestId;
  }


  public CharacterBackendDetailsDTO backendId(@javax.annotation.Nullable String backendId) {
    this.backendId = backendId;
    return this;
  }

  /**
   * Character backend identifier
   * @return backendId
   */
  @javax.annotation.Nullable
  public String getBackendId() {
    return backendId;
  }

  public void setBackendId(@javax.annotation.Nullable String backendId) {
    this.backendId = backendId;
  }


  public CharacterBackendDetailsDTO gmtCreate(@javax.annotation.Nullable OffsetDateTime gmtCreate) {
    this.gmtCreate = gmtCreate;
    return this;
  }

  /**
   * Creation time
   * @return gmtCreate
   */
  @javax.annotation.Nullable
  public OffsetDateTime getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(@javax.annotation.Nullable OffsetDateTime gmtCreate) {
    this.gmtCreate = gmtCreate;
  }


  public CharacterBackendDetailsDTO gmtModified(@javax.annotation.Nullable OffsetDateTime gmtModified) {
    this.gmtModified = gmtModified;
    return this;
  }

  /**
   * Modification time
   * @return gmtModified
   */
  @javax.annotation.Nullable
  public OffsetDateTime getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(@javax.annotation.Nullable OffsetDateTime gmtModified) {
    this.gmtModified = gmtModified;
  }


  public CharacterBackendDetailsDTO characterUid(@javax.annotation.Nullable String characterUid) {
    this.characterUid = characterUid;
    return this;
  }

  /**
   * Character immutable identifier
   * @return characterUid
   */
  @javax.annotation.Nullable
  public String getCharacterUid() {
    return characterUid;
  }

  public void setCharacterUid(@javax.annotation.Nullable String characterUid) {
    this.characterUid = characterUid;
  }


  public CharacterBackendDetailsDTO isDefault(@javax.annotation.Nullable Boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  /**
   * Whether it is the default backend
   * @return isDefault
   */
  @javax.annotation.Nullable
  public Boolean isIsDefault() {
    return isDefault;
  }

  public void setIsDefault(@javax.annotation.Nullable Boolean isDefault) {
    this.isDefault = isDefault;
  }


  public CharacterBackendDetailsDTO chatPromptTaskId(@javax.annotation.Nullable String chatPromptTaskId) {
    this.chatPromptTaskId = chatPromptTaskId;
    return this;
  }

  /**
   * Prompt task identifier for chat
   * @return chatPromptTaskId
   */
  @javax.annotation.Nullable
  public String getChatPromptTaskId() {
    return chatPromptTaskId;
  }

  public void setChatPromptTaskId(@javax.annotation.Nullable String chatPromptTaskId) {
    this.chatPromptTaskId = chatPromptTaskId;
  }


  public CharacterBackendDetailsDTO greetingPromptTaskId(@javax.annotation.Nullable String greetingPromptTaskId) {
    this.greetingPromptTaskId = greetingPromptTaskId;
    return this;
  }

  /**
   * Prompt task identifier for greeting
   * @return greetingPromptTaskId
   */
  @javax.annotation.Nullable
  public String getGreetingPromptTaskId() {
    return greetingPromptTaskId;
  }

  public void setGreetingPromptTaskId(@javax.annotation.Nullable String greetingPromptTaskId) {
    this.greetingPromptTaskId = greetingPromptTaskId;
  }


  public CharacterBackendDetailsDTO moderationModelId(@javax.annotation.Nullable String moderationModelId) {
    this.moderationModelId = moderationModelId;
    return this;
  }

  /**
   * Moderation model for the character&#39;s response
   * @return moderationModelId
   */
  @javax.annotation.Nullable
  public String getModerationModelId() {
    return moderationModelId;
  }

  public void setModerationModelId(@javax.annotation.Nullable String moderationModelId) {
    this.moderationModelId = moderationModelId;
  }


  public CharacterBackendDetailsDTO moderationApiKeyName(@javax.annotation.Nullable String moderationApiKeyName) {
    this.moderationApiKeyName = moderationApiKeyName;
    return this;
  }

  /**
   * Api key name for moderation model
   * @return moderationApiKeyName
   */
  @javax.annotation.Nullable
  public String getModerationApiKeyName() {
    return moderationApiKeyName;
  }

  public void setModerationApiKeyName(@javax.annotation.Nullable String moderationApiKeyName) {
    this.moderationApiKeyName = moderationApiKeyName;
  }


  public CharacterBackendDetailsDTO moderationParams(@javax.annotation.Nullable String moderationParams) {
    this.moderationParams = moderationParams;
    return this;
  }

  /**
   * Parameters for moderation model
   * @return moderationParams
   */
  @javax.annotation.Nullable
  public String getModerationParams() {
    return moderationParams;
  }

  public void setModerationParams(@javax.annotation.Nullable String moderationParams) {
    this.moderationParams = moderationParams;
  }


  public CharacterBackendDetailsDTO messageWindowSize(@javax.annotation.Nullable Integer messageWindowSize) {
    this.messageWindowSize = messageWindowSize;
    return this;
  }

  /**
   * Max messages in the character&#39;s memory
   * @return messageWindowSize
   */
  @javax.annotation.Nullable
  public Integer getMessageWindowSize() {
    return messageWindowSize;
  }

  public void setMessageWindowSize(@javax.annotation.Nullable Integer messageWindowSize) {
    this.messageWindowSize = messageWindowSize;
  }


  public CharacterBackendDetailsDTO longTermMemoryWindowSize(@javax.annotation.Nullable Integer longTermMemoryWindowSize) {
    this.longTermMemoryWindowSize = longTermMemoryWindowSize;
    return this;
  }

  /**
   * Max rounds (a round includes a user message and a character reply) in the character&#39;s long term memory
   * @return longTermMemoryWindowSize
   */
  @javax.annotation.Nullable
  public Integer getLongTermMemoryWindowSize() {
    return longTermMemoryWindowSize;
  }

  public void setLongTermMemoryWindowSize(@javax.annotation.Nullable Integer longTermMemoryWindowSize) {
    this.longTermMemoryWindowSize = longTermMemoryWindowSize;
  }


  public CharacterBackendDetailsDTO proactiveChatWaitingTime(@javax.annotation.Nullable Integer proactiveChatWaitingTime) {
    this.proactiveChatWaitingTime = proactiveChatWaitingTime;
    return this;
  }

  /**
   * Minutes to wait for a proactive chat
   * @return proactiveChatWaitingTime
   */
  @javax.annotation.Nullable
  public Integer getProactiveChatWaitingTime() {
    return proactiveChatWaitingTime;
  }

  public void setProactiveChatWaitingTime(@javax.annotation.Nullable Integer proactiveChatWaitingTime) {
    this.proactiveChatWaitingTime = proactiveChatWaitingTime;
  }


  public CharacterBackendDetailsDTO initQuota(@javax.annotation.Nullable Long initQuota) {
    this.initQuota = initQuota;
    return this;
  }

  /**
   * Initial quota when opening a chat
   * @return initQuota
   */
  @javax.annotation.Nullable
  public Long getInitQuota() {
    return initQuota;
  }

  public void setInitQuota(@javax.annotation.Nullable Long initQuota) {
    this.initQuota = initQuota;
  }


  public CharacterBackendDetailsDTO quotaType(@javax.annotation.Nullable String quotaType) {
    this.quotaType = quotaType;
    return this;
  }

  /**
   * Quota type: messages | tokens | none (not limited)
   * @return quotaType
   */
  @javax.annotation.Nullable
  public String getQuotaType() {
    return quotaType;
  }

  public void setQuotaType(@javax.annotation.Nullable String quotaType) {
    this.quotaType = quotaType;
  }


  public CharacterBackendDetailsDTO enableAlbumTool(@javax.annotation.Nullable Boolean enableAlbumTool) {
    this.enableAlbumTool = enableAlbumTool;
    return this;
  }

  /**
   * Enable character album image retrieval tool
   * @return enableAlbumTool
   */
  @javax.annotation.Nullable
  public Boolean isEnableAlbumTool() {
    return enableAlbumTool;
  }

  public void setEnableAlbumTool(@javax.annotation.Nullable Boolean enableAlbumTool) {
    this.enableAlbumTool = enableAlbumTool;
  }


  public CharacterBackendDetailsDTO enableTts(@javax.annotation.Nullable Boolean enableTts) {
    this.enableTts = enableTts;
    return this;
  }

  /**
   * Enable character tts feature
   * @return enableTts
   */
  @javax.annotation.Nullable
  public Boolean isEnableTts() {
    return enableTts;
  }

  public void setEnableTts(@javax.annotation.Nullable Boolean enableTts) {
    this.enableTts = enableTts;
  }


  public CharacterBackendDetailsDTO ttsSpeakerIdx(@javax.annotation.Nullable String ttsSpeakerIdx) {
    this.ttsSpeakerIdx = ttsSpeakerIdx;
    return this;
  }

  /**
   * Character&#39;s speaker idx for tts
   * @return ttsSpeakerIdx
   */
  @javax.annotation.Nullable
  public String getTtsSpeakerIdx() {
    return ttsSpeakerIdx;
  }

  public void setTtsSpeakerIdx(@javax.annotation.Nullable String ttsSpeakerIdx) {
    this.ttsSpeakerIdx = ttsSpeakerIdx;
  }


  public CharacterBackendDetailsDTO ttsSpeakerWav(@javax.annotation.Nullable String ttsSpeakerWav) {
    this.ttsSpeakerWav = ttsSpeakerWav;
    return this;
  }

  /**
   * Character&#39;s speaker sample for tts
   * @return ttsSpeakerWav
   */
  @javax.annotation.Nullable
  public String getTtsSpeakerWav() {
    return ttsSpeakerWav;
  }

  public void setTtsSpeakerWav(@javax.annotation.Nullable String ttsSpeakerWav) {
    this.ttsSpeakerWav = ttsSpeakerWav;
  }


  public CharacterBackendDetailsDTO ttsSpeakerType(@javax.annotation.Nullable String ttsSpeakerType) {
    this.ttsSpeakerType = ttsSpeakerType;
    return this;
  }

  /**
   * Character&#39;s speaker type for tts: idx | wav
   * @return ttsSpeakerType
   */
  @javax.annotation.Nullable
  public String getTtsSpeakerType() {
    return ttsSpeakerType;
  }

  public void setTtsSpeakerType(@javax.annotation.Nullable String ttsSpeakerType) {
    this.ttsSpeakerType = ttsSpeakerType;
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
   * @return the CharacterBackendDetailsDTO instance itself
   */
  public CharacterBackendDetailsDTO putAdditionalProperty(String key, Object value) {
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
    CharacterBackendDetailsDTO characterBackendDetailsDTO = (CharacterBackendDetailsDTO) o;
    return Objects.equals(this.requestId, characterBackendDetailsDTO.requestId) &&
        Objects.equals(this.backendId, characterBackendDetailsDTO.backendId) &&
        Objects.equals(this.gmtCreate, characterBackendDetailsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, characterBackendDetailsDTO.gmtModified) &&
        Objects.equals(this.characterUid, characterBackendDetailsDTO.characterUid) &&
        Objects.equals(this.isDefault, characterBackendDetailsDTO.isDefault) &&
        Objects.equals(this.chatPromptTaskId, characterBackendDetailsDTO.chatPromptTaskId) &&
        Objects.equals(this.greetingPromptTaskId, characterBackendDetailsDTO.greetingPromptTaskId) &&
        Objects.equals(this.moderationModelId, characterBackendDetailsDTO.moderationModelId) &&
        Objects.equals(this.moderationApiKeyName, characterBackendDetailsDTO.moderationApiKeyName) &&
        Objects.equals(this.moderationParams, characterBackendDetailsDTO.moderationParams) &&
        Objects.equals(this.messageWindowSize, characterBackendDetailsDTO.messageWindowSize) &&
        Objects.equals(this.longTermMemoryWindowSize, characterBackendDetailsDTO.longTermMemoryWindowSize) &&
        Objects.equals(this.proactiveChatWaitingTime, characterBackendDetailsDTO.proactiveChatWaitingTime) &&
        Objects.equals(this.initQuota, characterBackendDetailsDTO.initQuota) &&
        Objects.equals(this.quotaType, characterBackendDetailsDTO.quotaType) &&
        Objects.equals(this.enableAlbumTool, characterBackendDetailsDTO.enableAlbumTool) &&
        Objects.equals(this.enableTts, characterBackendDetailsDTO.enableTts) &&
        Objects.equals(this.ttsSpeakerIdx, characterBackendDetailsDTO.ttsSpeakerIdx) &&
        Objects.equals(this.ttsSpeakerWav, characterBackendDetailsDTO.ttsSpeakerWav) &&
        Objects.equals(this.ttsSpeakerType, characterBackendDetailsDTO.ttsSpeakerType)&&
        Objects.equals(this.additionalProperties, characterBackendDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, backendId, gmtCreate, gmtModified, characterUid, isDefault, chatPromptTaskId, greetingPromptTaskId, moderationModelId, moderationApiKeyName, moderationParams, messageWindowSize, longTermMemoryWindowSize, proactiveChatWaitingTime, initQuota, quotaType, enableAlbumTool, enableTts, ttsSpeakerIdx, ttsSpeakerWav, ttsSpeakerType, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterBackendDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    backendId: ").append(toIndentedString(backendId)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    characterUid: ").append(toIndentedString(characterUid)).append("\n");
    sb.append("    isDefault: ").append(toIndentedString(isDefault)).append("\n");
    sb.append("    chatPromptTaskId: ").append(toIndentedString(chatPromptTaskId)).append("\n");
    sb.append("    greetingPromptTaskId: ").append(toIndentedString(greetingPromptTaskId)).append("\n");
    sb.append("    moderationModelId: ").append(toIndentedString(moderationModelId)).append("\n");
    sb.append("    moderationApiKeyName: ").append(toIndentedString(moderationApiKeyName)).append("\n");
    sb.append("    moderationParams: ").append(toIndentedString(moderationParams)).append("\n");
    sb.append("    messageWindowSize: ").append(toIndentedString(messageWindowSize)).append("\n");
    sb.append("    longTermMemoryWindowSize: ").append(toIndentedString(longTermMemoryWindowSize)).append("\n");
    sb.append("    proactiveChatWaitingTime: ").append(toIndentedString(proactiveChatWaitingTime)).append("\n");
    sb.append("    initQuota: ").append(toIndentedString(initQuota)).append("\n");
    sb.append("    quotaType: ").append(toIndentedString(quotaType)).append("\n");
    sb.append("    enableAlbumTool: ").append(toIndentedString(enableAlbumTool)).append("\n");
    sb.append("    enableTts: ").append(toIndentedString(enableTts)).append("\n");
    sb.append("    ttsSpeakerIdx: ").append(toIndentedString(ttsSpeakerIdx)).append("\n");
    sb.append("    ttsSpeakerWav: ").append(toIndentedString(ttsSpeakerWav)).append("\n");
    sb.append("    ttsSpeakerType: ").append(toIndentedString(ttsSpeakerType)).append("\n");
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
    openapiFields.add("backendId");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("characterUid");
    openapiFields.add("isDefault");
    openapiFields.add("chatPromptTaskId");
    openapiFields.add("greetingPromptTaskId");
    openapiFields.add("moderationModelId");
    openapiFields.add("moderationApiKeyName");
    openapiFields.add("moderationParams");
    openapiFields.add("messageWindowSize");
    openapiFields.add("longTermMemoryWindowSize");
    openapiFields.add("proactiveChatWaitingTime");
    openapiFields.add("initQuota");
    openapiFields.add("quotaType");
    openapiFields.add("enableAlbumTool");
    openapiFields.add("enableTts");
    openapiFields.add("ttsSpeakerIdx");
    openapiFields.add("ttsSpeakerWav");
    openapiFields.add("ttsSpeakerType");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to CharacterBackendDetailsDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterBackendDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterBackendDetailsDTO is not found in the empty JSON string", CharacterBackendDetailsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("backendId") != null && !jsonObj.get("backendId").isJsonNull()) && !jsonObj.get("backendId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `backendId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("backendId").toString()));
      }
      if ((jsonObj.get("characterUid") != null && !jsonObj.get("characterUid").isJsonNull()) && !jsonObj.get("characterUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterUid").toString()));
      }
      if ((jsonObj.get("chatPromptTaskId") != null && !jsonObj.get("chatPromptTaskId").isJsonNull()) && !jsonObj.get("chatPromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `chatPromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("chatPromptTaskId").toString()));
      }
      if ((jsonObj.get("greetingPromptTaskId") != null && !jsonObj.get("greetingPromptTaskId").isJsonNull()) && !jsonObj.get("greetingPromptTaskId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greetingPromptTaskId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greetingPromptTaskId").toString()));
      }
      if ((jsonObj.get("moderationModelId") != null && !jsonObj.get("moderationModelId").isJsonNull()) && !jsonObj.get("moderationModelId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationModelId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationModelId").toString()));
      }
      if ((jsonObj.get("moderationApiKeyName") != null && !jsonObj.get("moderationApiKeyName").isJsonNull()) && !jsonObj.get("moderationApiKeyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationApiKeyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationApiKeyName").toString()));
      }
      if ((jsonObj.get("moderationParams") != null && !jsonObj.get("moderationParams").isJsonNull()) && !jsonObj.get("moderationParams").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `moderationParams` to be a primitive type in the JSON string but got `%s`", jsonObj.get("moderationParams").toString()));
      }
      if ((jsonObj.get("quotaType") != null && !jsonObj.get("quotaType").isJsonNull()) && !jsonObj.get("quotaType").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `quotaType` to be a primitive type in the JSON string but got `%s`", jsonObj.get("quotaType").toString()));
      }
      if ((jsonObj.get("ttsSpeakerIdx") != null && !jsonObj.get("ttsSpeakerIdx").isJsonNull()) && !jsonObj.get("ttsSpeakerIdx").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ttsSpeakerIdx` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ttsSpeakerIdx").toString()));
      }
      if ((jsonObj.get("ttsSpeakerWav") != null && !jsonObj.get("ttsSpeakerWav").isJsonNull()) && !jsonObj.get("ttsSpeakerWav").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ttsSpeakerWav` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ttsSpeakerWav").toString()));
      }
      if ((jsonObj.get("ttsSpeakerType") != null && !jsonObj.get("ttsSpeakerType").isJsonNull()) && !jsonObj.get("ttsSpeakerType").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ttsSpeakerType` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ttsSpeakerType").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterBackendDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterBackendDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterBackendDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterBackendDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterBackendDetailsDTO>() {
           @Override
           public void write(JsonWriter out, CharacterBackendDetailsDTO value) throws IOException {
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
           public CharacterBackendDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterBackendDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of CharacterBackendDetailsDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of CharacterBackendDetailsDTO
   * @throws IOException if the JSON string is invalid with respect to CharacterBackendDetailsDTO
   */
  public static CharacterBackendDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterBackendDetailsDTO.class);
  }

  /**
   * Convert an instance of CharacterBackendDetailsDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

