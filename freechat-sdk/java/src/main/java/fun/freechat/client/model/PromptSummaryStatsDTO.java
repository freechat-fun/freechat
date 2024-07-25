/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   deployment:     enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   deployment:     enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   deployment:     enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   deployment:     enabled: true grafana:   deployment:     enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI GPT and Alibaba Qwen series models. However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 1.3.0
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Prompt template summary content, including interactive statistical information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.7.0")
public class PromptSummaryStatsDTO {
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
  private List<String> tags = new ArrayList<>();

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  private List<AiModelInfoDTO> aiModels = new ArrayList<>();

  public static final String SERIALIZED_NAME_VIEW_COUNT = "viewCount";
  @SerializedName(SERIALIZED_NAME_VIEW_COUNT)
  private Long viewCount;

  public static final String SERIALIZED_NAME_REFER_COUNT = "referCount";
  @SerializedName(SERIALIZED_NAME_REFER_COUNT)
  private Long referCount;

  public static final String SERIALIZED_NAME_RECOMMEND_COUNT = "recommendCount";
  @SerializedName(SERIALIZED_NAME_RECOMMEND_COUNT)
  private Long recommendCount;

  public static final String SERIALIZED_NAME_SCORE_COUNT = "scoreCount";
  @SerializedName(SERIALIZED_NAME_SCORE_COUNT)
  private Long scoreCount;

  public static final String SERIALIZED_NAME_SCORE = "score";
  @SerializedName(SERIALIZED_NAME_SCORE)
  private Long score;

  public PromptSummaryStatsDTO() {
  }

  public PromptSummaryStatsDTO requestId(String requestId) {
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

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }


  public PromptSummaryStatsDTO promptId(Long promptId) {
    this.promptId = promptId;
    return this;
  }

  /**
   * Prompt identifier, will change after publish
   * @return promptId
   */
  @javax.annotation.Nullable
  public Long getPromptId() {
    return promptId;
  }

  public void setPromptId(Long promptId) {
    this.promptId = promptId;
  }


  public PromptSummaryStatsDTO promptUid(String promptUid) {
    this.promptUid = promptUid;
    return this;
  }

  /**
   * Prompt immutable identifier
   * @return promptUid
   */
  @javax.annotation.Nullable
  public String getPromptUid() {
    return promptUid;
  }

  public void setPromptUid(String promptUid) {
    this.promptUid = promptUid;
  }


  public PromptSummaryStatsDTO gmtCreate(OffsetDateTime gmtCreate) {
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

  public void setGmtCreate(OffsetDateTime gmtCreate) {
    this.gmtCreate = gmtCreate;
  }


  public PromptSummaryStatsDTO gmtModified(OffsetDateTime gmtModified) {
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

  public void setGmtModified(OffsetDateTime gmtModified) {
    this.gmtModified = gmtModified;
  }


  public PromptSummaryStatsDTO parentUid(String parentUid) {
    this.parentUid = parentUid;
    return this;
  }

  /**
   * Referenced prompt
   * @return parentUid
   */
  @javax.annotation.Nullable
  public String getParentUid() {
    return parentUid;
  }

  public void setParentUid(String parentUid) {
    this.parentUid = parentUid;
  }


  public PromptSummaryStatsDTO visibility(String visibility) {
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

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }


  public PromptSummaryStatsDTO version(Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Version
   * @return version
   */
  @javax.annotation.Nullable
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }


  public PromptSummaryStatsDTO name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Prompt name
   * @return name
   */
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public PromptSummaryStatsDTO type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Prompt type: string | chat
   * @return type
   */
  @javax.annotation.Nullable
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public PromptSummaryStatsDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Prompt description (50 characters, the excess part is represented by ellipsis)
   * @return description
   */
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public PromptSummaryStatsDTO format(String format) {
    this.format = format;
    return this;
  }

  /**
   * Prompt format: mustache (default) | f_string
   * @return format
   */
  @javax.annotation.Nullable
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }


  public PromptSummaryStatsDTO lang(String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Prompt language: en (default) | zh | ...
   * @return lang
   */
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }


  public PromptSummaryStatsDTO username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Prompt owner
   * @return username
   */
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public PromptSummaryStatsDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PromptSummaryStatsDTO addTagsItem(String tagsItem) {
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

  public void setTags(List<String> tags) {
    this.tags = tags;
  }


  public PromptSummaryStatsDTO aiModels(List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PromptSummaryStatsDTO addAiModelsItem(AiModelInfoDTO aiModelsItem) {
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

  public void setAiModels(List<AiModelInfoDTO> aiModels) {
    this.aiModels = aiModels;
  }


  public PromptSummaryStatsDTO viewCount(Long viewCount) {
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

  public void setViewCount(Long viewCount) {
    this.viewCount = viewCount;
  }


  public PromptSummaryStatsDTO referCount(Long referCount) {
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

  public void setReferCount(Long referCount) {
    this.referCount = referCount;
  }


  public PromptSummaryStatsDTO recommendCount(Long recommendCount) {
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

  public void setRecommendCount(Long recommendCount) {
    this.recommendCount = recommendCount;
  }


  public PromptSummaryStatsDTO scoreCount(Long scoreCount) {
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

  public void setScoreCount(Long scoreCount) {
    this.scoreCount = scoreCount;
  }


  public PromptSummaryStatsDTO score(Long score) {
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

  public void setScore(Long score) {
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
   * @return the PromptSummaryStatsDTO instance itself
   */
  public PromptSummaryStatsDTO putAdditionalProperty(String key, Object value) {
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
    PromptSummaryStatsDTO promptSummaryStatsDTO = (PromptSummaryStatsDTO) o;
    return Objects.equals(this.requestId, promptSummaryStatsDTO.requestId) &&
        Objects.equals(this.promptId, promptSummaryStatsDTO.promptId) &&
        Objects.equals(this.promptUid, promptSummaryStatsDTO.promptUid) &&
        Objects.equals(this.gmtCreate, promptSummaryStatsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, promptSummaryStatsDTO.gmtModified) &&
        Objects.equals(this.parentUid, promptSummaryStatsDTO.parentUid) &&
        Objects.equals(this.visibility, promptSummaryStatsDTO.visibility) &&
        Objects.equals(this.version, promptSummaryStatsDTO.version) &&
        Objects.equals(this.name, promptSummaryStatsDTO.name) &&
        Objects.equals(this.type, promptSummaryStatsDTO.type) &&
        Objects.equals(this.description, promptSummaryStatsDTO.description) &&
        Objects.equals(this.format, promptSummaryStatsDTO.format) &&
        Objects.equals(this.lang, promptSummaryStatsDTO.lang) &&
        Objects.equals(this.username, promptSummaryStatsDTO.username) &&
        Objects.equals(this.tags, promptSummaryStatsDTO.tags) &&
        Objects.equals(this.aiModels, promptSummaryStatsDTO.aiModels) &&
        Objects.equals(this.viewCount, promptSummaryStatsDTO.viewCount) &&
        Objects.equals(this.referCount, promptSummaryStatsDTO.referCount) &&
        Objects.equals(this.recommendCount, promptSummaryStatsDTO.recommendCount) &&
        Objects.equals(this.scoreCount, promptSummaryStatsDTO.scoreCount) &&
        Objects.equals(this.score, promptSummaryStatsDTO.score)&&
        Objects.equals(this.additionalProperties, promptSummaryStatsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, promptId, promptUid, gmtCreate, gmtModified, parentUid, visibility, version, name, type, description, format, lang, username, tags, aiModels, viewCount, referCount, recommendCount, scoreCount, score, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromptSummaryStatsDTO {\n");
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
   * @throws IOException if the JSON Element is invalid with respect to PromptSummaryStatsDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PromptSummaryStatsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PromptSummaryStatsDTO is not found in the empty JSON string", PromptSummaryStatsDTO.openapiRequiredFields.toString()));
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
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PromptSummaryStatsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PromptSummaryStatsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PromptSummaryStatsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PromptSummaryStatsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<PromptSummaryStatsDTO>() {
           @Override
           public void write(JsonWriter out, PromptSummaryStatsDTO value) throws IOException {
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
           public PromptSummaryStatsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PromptSummaryStatsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of PromptSummaryStatsDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of PromptSummaryStatsDTO
   * @throws IOException if the JSON string is invalid with respect to PromptSummaryStatsDTO
   */
  public static PromptSummaryStatsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PromptSummaryStatsDTO.class);
  }

  /**
   * Convert an instance of PromptSummaryStatsDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

