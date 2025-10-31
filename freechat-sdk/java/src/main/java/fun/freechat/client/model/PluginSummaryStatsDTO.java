/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports characters replies with **mixed text and image information**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](https://freechat.fun/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](https://freechat.fun/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](https://freechat.fun/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](https://freechat.fun/img/snapshot_m1.jpg) ![Chat Snapshot 2](https://freechat.fun/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](https://freechat.fun/img/snapshot_m3.jpg) ![Chat Snapshot 4](https://freechat.fun/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.6.0
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Plugin template summary content, including interactive statistical information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.11.0")
public class PluginSummaryStatsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  @javax.annotation.Nullable
  private String requestId;

  public static final String SERIALIZED_NAME_PLUGIN_ID = "pluginId";
  @SerializedName(SERIALIZED_NAME_PLUGIN_ID)
  @javax.annotation.Nullable
  private Long pluginId;

  public static final String SERIALIZED_NAME_PLUGIN_UID = "pluginUid";
  @SerializedName(SERIALIZED_NAME_PLUGIN_UID)
  @javax.annotation.Nullable
  private String pluginUid;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  @javax.annotation.Nullable
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  @javax.annotation.Nullable
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  @javax.annotation.Nullable
  private String visibility;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nullable
  private String name;

  public static final String SERIALIZED_NAME_PROVIDER = "provider";
  @SerializedName(SERIALIZED_NAME_PROVIDER)
  @javax.annotation.Nullable
  private String provider;

  public static final String SERIALIZED_NAME_MANIFEST_FORMAT = "manifestFormat";
  @SerializedName(SERIALIZED_NAME_MANIFEST_FORMAT)
  @javax.annotation.Nullable
  private String manifestFormat;

  public static final String SERIALIZED_NAME_API_FORMAT = "apiFormat";
  @SerializedName(SERIALIZED_NAME_API_FORMAT)
  @javax.annotation.Nullable
  private String apiFormat;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  @javax.annotation.Nullable
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  @javax.annotation.Nullable
  private List<String> tags = new ArrayList<>();

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  @javax.annotation.Nullable
  private List<AiModelInfoDTO> aiModels = new ArrayList<>();

  public static final String SERIALIZED_NAME_VIEW_COUNT = "viewCount";
  @SerializedName(SERIALIZED_NAME_VIEW_COUNT)
  @javax.annotation.Nullable
  private Long viewCount;

  public static final String SERIALIZED_NAME_REFER_COUNT = "referCount";
  @SerializedName(SERIALIZED_NAME_REFER_COUNT)
  @javax.annotation.Nullable
  private Long referCount;

  public static final String SERIALIZED_NAME_RECOMMEND_COUNT = "recommendCount";
  @SerializedName(SERIALIZED_NAME_RECOMMEND_COUNT)
  @javax.annotation.Nullable
  private Long recommendCount;

  public static final String SERIALIZED_NAME_SCORE_COUNT = "scoreCount";
  @SerializedName(SERIALIZED_NAME_SCORE_COUNT)
  @javax.annotation.Nullable
  private Long scoreCount;

  public static final String SERIALIZED_NAME_SCORE = "score";
  @SerializedName(SERIALIZED_NAME_SCORE)
  @javax.annotation.Nullable
  private Long score;

  public PluginSummaryStatsDTO() {
  }

  public PluginSummaryStatsDTO requestId(@javax.annotation.Nullable String requestId) {
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


  public PluginSummaryStatsDTO pluginId(@javax.annotation.Nullable Long pluginId) {
    this.pluginId = pluginId;
    return this;
  }

  /**
   * Plugin identifier
   * @return pluginId
   */
  @javax.annotation.Nullable
  public Long getPluginId() {
    return pluginId;
  }

  public void setPluginId(@javax.annotation.Nullable Long pluginId) {
    this.pluginId = pluginId;
  }


  public PluginSummaryStatsDTO pluginUid(@javax.annotation.Nullable String pluginUid) {
    this.pluginUid = pluginUid;
    return this;
  }

  /**
   * Plugin immutable identifier
   * @return pluginUid
   */
  @javax.annotation.Nullable
  public String getPluginUid() {
    return pluginUid;
  }

  public void setPluginUid(@javax.annotation.Nullable String pluginUid) {
    this.pluginUid = pluginUid;
  }


  public PluginSummaryStatsDTO gmtCreate(@javax.annotation.Nullable OffsetDateTime gmtCreate) {
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


  public PluginSummaryStatsDTO gmtModified(@javax.annotation.Nullable OffsetDateTime gmtModified) {
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


  public PluginSummaryStatsDTO visibility(@javax.annotation.Nullable String visibility) {
    this.visibility = visibility;
    return this;
  }

  /**
   * Visibility: private, public, public_org, hidden
   * @return visibility
   */
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(@javax.annotation.Nullable String visibility) {
    this.visibility = visibility;
  }


  public PluginSummaryStatsDTO name(@javax.annotation.Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Plugin name
   * @return name
   */
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(@javax.annotation.Nullable String name) {
    this.name = name;
  }


  public PluginSummaryStatsDTO provider(@javax.annotation.Nullable String provider) {
    this.provider = provider;
    return this;
  }

  /**
   * Information of the provider
   * @return provider
   */
  @javax.annotation.Nullable
  public String getProvider() {
    return provider;
  }

  public void setProvider(@javax.annotation.Nullable String provider) {
    this.provider = provider;
  }


  public PluginSummaryStatsDTO manifestFormat(@javax.annotation.Nullable String manifestFormat) {
    this.manifestFormat = manifestFormat;
    return this;
  }

  /**
   * Manifest format, currently supported: dash_scope, open_ai
   * @return manifestFormat
   */
  @javax.annotation.Nullable
  public String getManifestFormat() {
    return manifestFormat;
  }

  public void setManifestFormat(@javax.annotation.Nullable String manifestFormat) {
    this.manifestFormat = manifestFormat;
  }


  public PluginSummaryStatsDTO apiFormat(@javax.annotation.Nullable String apiFormat) {
    this.apiFormat = apiFormat;
    return this;
  }

  /**
   * API description format, currently supported: openapi_v3
   * @return apiFormat
   */
  @javax.annotation.Nullable
  public String getApiFormat() {
    return apiFormat;
  }

  public void setApiFormat(@javax.annotation.Nullable String apiFormat) {
    this.apiFormat = apiFormat;
  }


  public PluginSummaryStatsDTO username(@javax.annotation.Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Plugin owner
   * @return username
   */
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(@javax.annotation.Nullable String username) {
    this.username = username;
  }


  public PluginSummaryStatsDTO tags(@javax.annotation.Nullable List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PluginSummaryStatsDTO addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Tag set
   * @return tags
   */
  @javax.annotation.Nullable
  public List<String> getTags() {
    return tags;
  }

  public void setTags(@javax.annotation.Nullable List<String> tags) {
    this.tags = tags;
  }


  public PluginSummaryStatsDTO aiModels(@javax.annotation.Nullable List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PluginSummaryStatsDTO addAiModelsItem(AiModelInfoDTO aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

  /**
   * Supported model set
   * @return aiModels
   */
  @javax.annotation.Nullable
  public List<AiModelInfoDTO> getAiModels() {
    return aiModels;
  }

  public void setAiModels(@javax.annotation.Nullable List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
  }


  public PluginSummaryStatsDTO viewCount(@javax.annotation.Nullable Long viewCount) {
    this.viewCount = viewCount;
    return this;
  }

  /**
   * View count
   * @return viewCount
   */
  @javax.annotation.Nullable
  public Long getViewCount() {
    return viewCount;
  }

  public void setViewCount(@javax.annotation.Nullable Long viewCount) {
    this.viewCount = viewCount;
  }


  public PluginSummaryStatsDTO referCount(@javax.annotation.Nullable Long referCount) {
    this.referCount = referCount;
    return this;
  }

  /**
   * Reference count
   * @return referCount
   */
  @javax.annotation.Nullable
  public Long getReferCount() {
    return referCount;
  }

  public void setReferCount(@javax.annotation.Nullable Long referCount) {
    this.referCount = referCount;
  }


  public PluginSummaryStatsDTO recommendCount(@javax.annotation.Nullable Long recommendCount) {
    this.recommendCount = recommendCount;
    return this;
  }

  /**
   * Recommendation count
   * @return recommendCount
   */
  @javax.annotation.Nullable
  public Long getRecommendCount() {
    return recommendCount;
  }

  public void setRecommendCount(@javax.annotation.Nullable Long recommendCount) {
    this.recommendCount = recommendCount;
  }


  public PluginSummaryStatsDTO scoreCount(@javax.annotation.Nullable Long scoreCount) {
    this.scoreCount = scoreCount;
    return this;
  }

  /**
   * Score count
   * @return scoreCount
   */
  @javax.annotation.Nullable
  public Long getScoreCount() {
    return scoreCount;
  }

  public void setScoreCount(@javax.annotation.Nullable Long scoreCount) {
    this.scoreCount = scoreCount;
  }


  public PluginSummaryStatsDTO score(@javax.annotation.Nullable Long score) {
    this.score = score;
    return this;
  }

  /**
   * Average score
   * @return score
   */
  @javax.annotation.Nullable
  public Long getScore() {
    return score;
  }

  public void setScore(@javax.annotation.Nullable Long score) {
    this.score = score;
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
   * @return the PluginSummaryStatsDTO instance itself
   */
  public PluginSummaryStatsDTO putAdditionalProperty(String key, Object value) {
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
    PluginSummaryStatsDTO pluginSummaryStatsDTO = (PluginSummaryStatsDTO) o;
    return Objects.equals(this.requestId, pluginSummaryStatsDTO.requestId) &&
        Objects.equals(this.pluginId, pluginSummaryStatsDTO.pluginId) &&
        Objects.equals(this.pluginUid, pluginSummaryStatsDTO.pluginUid) &&
        Objects.equals(this.gmtCreate, pluginSummaryStatsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, pluginSummaryStatsDTO.gmtModified) &&
        Objects.equals(this.visibility, pluginSummaryStatsDTO.visibility) &&
        Objects.equals(this.name, pluginSummaryStatsDTO.name) &&
        Objects.equals(this.provider, pluginSummaryStatsDTO.provider) &&
        Objects.equals(this.manifestFormat, pluginSummaryStatsDTO.manifestFormat) &&
        Objects.equals(this.apiFormat, pluginSummaryStatsDTO.apiFormat) &&
        Objects.equals(this.username, pluginSummaryStatsDTO.username) &&
        Objects.equals(this.tags, pluginSummaryStatsDTO.tags) &&
        Objects.equals(this.aiModels, pluginSummaryStatsDTO.aiModels) &&
        Objects.equals(this.viewCount, pluginSummaryStatsDTO.viewCount) &&
        Objects.equals(this.referCount, pluginSummaryStatsDTO.referCount) &&
        Objects.equals(this.recommendCount, pluginSummaryStatsDTO.recommendCount) &&
        Objects.equals(this.scoreCount, pluginSummaryStatsDTO.scoreCount) &&
        Objects.equals(this.score, pluginSummaryStatsDTO.score)&&
        Objects.equals(this.additionalProperties, pluginSummaryStatsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, pluginId, pluginUid, gmtCreate, gmtModified, visibility, name, provider, manifestFormat, apiFormat, username, tags, aiModels, viewCount, referCount, recommendCount, scoreCount, score, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginSummaryStatsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    pluginId: ").append(toIndentedString(pluginId)).append("\n");
    sb.append("    pluginUid: ").append(toIndentedString(pluginUid)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    manifestFormat: ").append(toIndentedString(manifestFormat)).append("\n");
    sb.append("    apiFormat: ").append(toIndentedString(apiFormat)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
    sb.append("    viewCount: ").append(toIndentedString(viewCount)).append("\n");
    sb.append("    referCount: ").append(toIndentedString(referCount)).append("\n");
    sb.append("    recommendCount: ").append(toIndentedString(recommendCount)).append("\n");
    sb.append("    scoreCount: ").append(toIndentedString(scoreCount)).append("\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
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
    openapiFields.add("pluginId");
    openapiFields.add("pluginUid");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("visibility");
    openapiFields.add("name");
    openapiFields.add("provider");
    openapiFields.add("manifestFormat");
    openapiFields.add("apiFormat");
    openapiFields.add("username");
    openapiFields.add("tags");
    openapiFields.add("aiModels");
    openapiFields.add("viewCount");
    openapiFields.add("referCount");
    openapiFields.add("recommendCount");
    openapiFields.add("scoreCount");
    openapiFields.add("score");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to PluginSummaryStatsDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PluginSummaryStatsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PluginSummaryStatsDTO is not found in the empty JSON string", PluginSummaryStatsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("pluginUid") != null && !jsonObj.get("pluginUid").isJsonNull()) && !jsonObj.get("pluginUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `pluginUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("pluginUid").toString()));
      }
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("provider") != null && !jsonObj.get("provider").isJsonNull()) && !jsonObj.get("provider").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `provider` to be a primitive type in the JSON string but got `%s`", jsonObj.get("provider").toString()));
      }
      if ((jsonObj.get("manifestFormat") != null && !jsonObj.get("manifestFormat").isJsonNull()) && !jsonObj.get("manifestFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestFormat").toString()));
      }
      if ((jsonObj.get("apiFormat") != null && !jsonObj.get("apiFormat").isJsonNull()) && !jsonObj.get("apiFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiFormat").toString()));
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
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PluginSummaryStatsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PluginSummaryStatsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PluginSummaryStatsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PluginSummaryStatsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<PluginSummaryStatsDTO>() {
           @Override
           public void write(JsonWriter out, PluginSummaryStatsDTO value) throws IOException {
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
           public PluginSummaryStatsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PluginSummaryStatsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of PluginSummaryStatsDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of PluginSummaryStatsDTO
   * @throws IOException if the JSON string is invalid with respect to PluginSummaryStatsDTO
   */
  public static PluginSummaryStatsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PluginSummaryStatsDTO.class);
  }

  /**
   * Convert an instance of PluginSummaryStatsDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

