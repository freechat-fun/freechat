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
 * Character summary content, including interactive statistical information
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.11.0")
public class CharacterSummaryStatsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  @javax.annotation.Nullable
  private String requestId;

  public static final String SERIALIZED_NAME_CHARACTER_ID = "characterId";
  @SerializedName(SERIALIZED_NAME_CHARACTER_ID)
  @javax.annotation.Nullable
  private Long characterId;

  public static final String SERIALIZED_NAME_CHARACTER_UID = "characterUid";
  @SerializedName(SERIALIZED_NAME_CHARACTER_UID)
  @javax.annotation.Nullable
  private String characterUid;

  public static final String SERIALIZED_NAME_GMT_CREATE = "gmtCreate";
  @SerializedName(SERIALIZED_NAME_GMT_CREATE)
  @javax.annotation.Nullable
  private OffsetDateTime gmtCreate;

  public static final String SERIALIZED_NAME_GMT_MODIFIED = "gmtModified";
  @SerializedName(SERIALIZED_NAME_GMT_MODIFIED)
  @javax.annotation.Nullable
  private OffsetDateTime gmtModified;

  public static final String SERIALIZED_NAME_PARENT_UID = "parentUid";
  @SerializedName(SERIALIZED_NAME_PARENT_UID)
  @javax.annotation.Nullable
  private String parentUid;

  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  @javax.annotation.Nullable
  private String visibility;

  public static final String SERIALIZED_NAME_VERSION = "version";
  @SerializedName(SERIALIZED_NAME_VERSION)
  @javax.annotation.Nullable
  private Integer version;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nullable
  private String name;

  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  @javax.annotation.Nullable
  private String description;

  public static final String SERIALIZED_NAME_NICKNAME = "nickname";
  @SerializedName(SERIALIZED_NAME_NICKNAME)
  @javax.annotation.Nullable
  private String nickname;

  public static final String SERIALIZED_NAME_AVATAR = "avatar";
  @SerializedName(SERIALIZED_NAME_AVATAR)
  @javax.annotation.Nullable
  private String avatar;

  public static final String SERIALIZED_NAME_PICTURE = "picture";
  @SerializedName(SERIALIZED_NAME_PICTURE)
  @javax.annotation.Nullable
  private String picture;

  public static final String SERIALIZED_NAME_VIDEO = "video";
  @SerializedName(SERIALIZED_NAME_VIDEO)
  @javax.annotation.Nullable
  private String video;

  public static final String SERIALIZED_NAME_GENDER = "gender";
  @SerializedName(SERIALIZED_NAME_GENDER)
  @javax.annotation.Nullable
  private String gender;

  public static final String SERIALIZED_NAME_LANG = "lang";
  @SerializedName(SERIALIZED_NAME_LANG)
  @javax.annotation.Nullable
  private String lang;

  public static final String SERIALIZED_NAME_GREETING = "greeting";
  @SerializedName(SERIALIZED_NAME_GREETING)
  @javax.annotation.Nullable
  private String greeting;

  public static final String SERIALIZED_NAME_DEFAULT_SCENE = "defaultScene";
  @SerializedName(SERIALIZED_NAME_DEFAULT_SCENE)
  @javax.annotation.Nullable
  private String defaultScene;

  public static final String SERIALIZED_NAME_PRIORITY = "priority";
  @SerializedName(SERIALIZED_NAME_PRIORITY)
  @javax.annotation.Nullable
  private Integer priority;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  @javax.annotation.Nullable
  private String username;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  @javax.annotation.Nullable
  private List<String> tags = new ArrayList<>();

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

  public CharacterSummaryStatsDTO() {
  }

  public CharacterSummaryStatsDTO requestId(@javax.annotation.Nullable String requestId) {
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


  public CharacterSummaryStatsDTO characterId(@javax.annotation.Nullable Long characterId) {
    this.characterId = characterId;
    return this;
  }

  /**
   * Character identifier, will change after publish
   * @return characterId
   */
  @javax.annotation.Nullable
  public Long getCharacterId() {
    return characterId;
  }

  public void setCharacterId(@javax.annotation.Nullable Long characterId) {
    this.characterId = characterId;
  }


  public CharacterSummaryStatsDTO characterUid(@javax.annotation.Nullable String characterUid) {
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


  public CharacterSummaryStatsDTO gmtCreate(@javax.annotation.Nullable OffsetDateTime gmtCreate) {
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


  public CharacterSummaryStatsDTO gmtModified(@javax.annotation.Nullable OffsetDateTime gmtModified) {
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


  public CharacterSummaryStatsDTO parentUid(@javax.annotation.Nullable String parentUid) {
    this.parentUid = parentUid;
    return this;
  }

  /**
   * Referenced character
   * @return parentUid
   */
  @javax.annotation.Nullable
  public String getParentUid() {
    return parentUid;
  }

  public void setParentUid(@javax.annotation.Nullable String parentUid) {
    this.parentUid = parentUid;
  }


  public CharacterSummaryStatsDTO visibility(@javax.annotation.Nullable String visibility) {
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


  public CharacterSummaryStatsDTO version(@javax.annotation.Nullable Integer version) {
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

  public void setVersion(@javax.annotation.Nullable Integer version) {
    this.version = version;
  }


  public CharacterSummaryStatsDTO name(@javax.annotation.Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Character name
   * @return name
   */
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(@javax.annotation.Nullable String name) {
    this.name = name;
  }


  public CharacterSummaryStatsDTO description(@javax.annotation.Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Character description
   * @return description
   */
  @javax.annotation.Nullable
  public String getDescription() {
    return description;
  }

  public void setDescription(@javax.annotation.Nullable String description) {
    this.description = description;
  }


  public CharacterSummaryStatsDTO nickname(@javax.annotation.Nullable String nickname) {
    this.nickname = nickname;
    return this;
  }

  /**
   * Character nickname
   * @return nickname
   */
  @javax.annotation.Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@javax.annotation.Nullable String nickname) {
    this.nickname = nickname;
  }


  public CharacterSummaryStatsDTO avatar(@javax.annotation.Nullable String avatar) {
    this.avatar = avatar;
    return this;
  }

  /**
   * Character avatar url
   * @return avatar
   */
  @javax.annotation.Nullable
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(@javax.annotation.Nullable String avatar) {
    this.avatar = avatar;
  }


  public CharacterSummaryStatsDTO picture(@javax.annotation.Nullable String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Character picture url
   * @return picture
   */
  @javax.annotation.Nullable
  public String getPicture() {
    return picture;
  }

  public void setPicture(@javax.annotation.Nullable String picture) {
    this.picture = picture;
  }


  public CharacterSummaryStatsDTO video(@javax.annotation.Nullable String video) {
    this.video = video;
    return this;
  }

  /**
   * Character video url
   * @return video
   */
  @javax.annotation.Nullable
  public String getVideo() {
    return video;
  }

  public void setVideo(@javax.annotation.Nullable String video) {
    this.video = video;
  }


  public CharacterSummaryStatsDTO gender(@javax.annotation.Nullable String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Character gender: male | female | other
   * @return gender
   */
  @javax.annotation.Nullable
  public String getGender() {
    return gender;
  }

  public void setGender(@javax.annotation.Nullable String gender) {
    this.gender = gender;
  }


  public CharacterSummaryStatsDTO lang(@javax.annotation.Nullable String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Character language: en (default) | zh | ...
   * @return lang
   */
  @javax.annotation.Nullable
  public String getLang() {
    return lang;
  }

  public void setLang(@javax.annotation.Nullable String lang) {
    this.lang = lang;
  }


  public CharacterSummaryStatsDTO greeting(@javax.annotation.Nullable String greeting) {
    this.greeting = greeting;
    return this;
  }

  /**
   * Character greeting
   * @return greeting
   */
  @javax.annotation.Nullable
  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(@javax.annotation.Nullable String greeting) {
    this.greeting = greeting;
  }


  public CharacterSummaryStatsDTO defaultScene(@javax.annotation.Nullable String defaultScene) {
    this.defaultScene = defaultScene;
    return this;
  }

  /**
   * Default scene, which will be set as the default conversation background information when creating a new chat
   * @return defaultScene
   */
  @javax.annotation.Nullable
  public String getDefaultScene() {
    return defaultScene;
  }

  public void setDefaultScene(@javax.annotation.Nullable String defaultScene) {
    this.defaultScene = defaultScene;
  }


  public CharacterSummaryStatsDTO priority(@javax.annotation.Nullable Integer priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Character priority
   * @return priority
   */
  @javax.annotation.Nullable
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(@javax.annotation.Nullable Integer priority) {
    this.priority = priority;
  }


  public CharacterSummaryStatsDTO username(@javax.annotation.Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Character owner
   * @return username
   */
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(@javax.annotation.Nullable String username) {
    this.username = username;
  }


  public CharacterSummaryStatsDTO tags(@javax.annotation.Nullable List<String> tags) {
    this.tags = tags;
    return this;
  }

  public CharacterSummaryStatsDTO addTagsItem(String tagsItem) {
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


  public CharacterSummaryStatsDTO viewCount(@javax.annotation.Nullable Long viewCount) {
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


  public CharacterSummaryStatsDTO referCount(@javax.annotation.Nullable Long referCount) {
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


  public CharacterSummaryStatsDTO recommendCount(@javax.annotation.Nullable Long recommendCount) {
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


  public CharacterSummaryStatsDTO scoreCount(@javax.annotation.Nullable Long scoreCount) {
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


  public CharacterSummaryStatsDTO score(@javax.annotation.Nullable Long score) {
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
   * @return the CharacterSummaryStatsDTO instance itself
   */
  public CharacterSummaryStatsDTO putAdditionalProperty(String key, Object value) {
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
    CharacterSummaryStatsDTO characterSummaryStatsDTO = (CharacterSummaryStatsDTO) o;
    return Objects.equals(this.requestId, characterSummaryStatsDTO.requestId) &&
        Objects.equals(this.characterId, characterSummaryStatsDTO.characterId) &&
        Objects.equals(this.characterUid, characterSummaryStatsDTO.characterUid) &&
        Objects.equals(this.gmtCreate, characterSummaryStatsDTO.gmtCreate) &&
        Objects.equals(this.gmtModified, characterSummaryStatsDTO.gmtModified) &&
        Objects.equals(this.parentUid, characterSummaryStatsDTO.parentUid) &&
        Objects.equals(this.visibility, characterSummaryStatsDTO.visibility) &&
        Objects.equals(this.version, characterSummaryStatsDTO.version) &&
        Objects.equals(this.name, characterSummaryStatsDTO.name) &&
        Objects.equals(this.description, characterSummaryStatsDTO.description) &&
        Objects.equals(this.nickname, characterSummaryStatsDTO.nickname) &&
        Objects.equals(this.avatar, characterSummaryStatsDTO.avatar) &&
        Objects.equals(this.picture, characterSummaryStatsDTO.picture) &&
        Objects.equals(this.video, characterSummaryStatsDTO.video) &&
        Objects.equals(this.gender, characterSummaryStatsDTO.gender) &&
        Objects.equals(this.lang, characterSummaryStatsDTO.lang) &&
        Objects.equals(this.greeting, characterSummaryStatsDTO.greeting) &&
        Objects.equals(this.defaultScene, characterSummaryStatsDTO.defaultScene) &&
        Objects.equals(this.priority, characterSummaryStatsDTO.priority) &&
        Objects.equals(this.username, characterSummaryStatsDTO.username) &&
        Objects.equals(this.tags, characterSummaryStatsDTO.tags) &&
        Objects.equals(this.viewCount, characterSummaryStatsDTO.viewCount) &&
        Objects.equals(this.referCount, characterSummaryStatsDTO.referCount) &&
        Objects.equals(this.recommendCount, characterSummaryStatsDTO.recommendCount) &&
        Objects.equals(this.scoreCount, characterSummaryStatsDTO.scoreCount) &&
        Objects.equals(this.score, characterSummaryStatsDTO.score)&&
        Objects.equals(this.additionalProperties, characterSummaryStatsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, characterId, characterUid, gmtCreate, gmtModified, parentUid, visibility, version, name, description, nickname, avatar, picture, video, gender, lang, greeting, defaultScene, priority, username, tags, viewCount, referCount, recommendCount, scoreCount, score, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CharacterSummaryStatsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    characterId: ").append(toIndentedString(characterId)).append("\n");
    sb.append("    characterUid: ").append(toIndentedString(characterUid)).append("\n");
    sb.append("    gmtCreate: ").append(toIndentedString(gmtCreate)).append("\n");
    sb.append("    gmtModified: ").append(toIndentedString(gmtModified)).append("\n");
    sb.append("    parentUid: ").append(toIndentedString(parentUid)).append("\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    avatar: ").append(toIndentedString(avatar)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    video: ").append(toIndentedString(video)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
    sb.append("    greeting: ").append(toIndentedString(greeting)).append("\n");
    sb.append("    defaultScene: ").append(toIndentedString(defaultScene)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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
    openapiFields.add("characterId");
    openapiFields.add("characterUid");
    openapiFields.add("gmtCreate");
    openapiFields.add("gmtModified");
    openapiFields.add("parentUid");
    openapiFields.add("visibility");
    openapiFields.add("version");
    openapiFields.add("name");
    openapiFields.add("description");
    openapiFields.add("nickname");
    openapiFields.add("avatar");
    openapiFields.add("picture");
    openapiFields.add("video");
    openapiFields.add("gender");
    openapiFields.add("lang");
    openapiFields.add("greeting");
    openapiFields.add("defaultScene");
    openapiFields.add("priority");
    openapiFields.add("username");
    openapiFields.add("tags");
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
   * @throws IOException if the JSON Element is invalid with respect to CharacterSummaryStatsDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!CharacterSummaryStatsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in CharacterSummaryStatsDTO is not found in the empty JSON string", CharacterSummaryStatsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("characterUid") != null && !jsonObj.get("characterUid").isJsonNull()) && !jsonObj.get("characterUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `characterUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("characterUid").toString()));
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
      if ((jsonObj.get("description") != null && !jsonObj.get("description").isJsonNull()) && !jsonObj.get("description").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `description` to be a primitive type in the JSON string but got `%s`", jsonObj.get("description").toString()));
      }
      if ((jsonObj.get("nickname") != null && !jsonObj.get("nickname").isJsonNull()) && !jsonObj.get("nickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `nickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("nickname").toString()));
      }
      if ((jsonObj.get("avatar") != null && !jsonObj.get("avatar").isJsonNull()) && !jsonObj.get("avatar").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `avatar` to be a primitive type in the JSON string but got `%s`", jsonObj.get("avatar").toString()));
      }
      if ((jsonObj.get("picture") != null && !jsonObj.get("picture").isJsonNull()) && !jsonObj.get("picture").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `picture` to be a primitive type in the JSON string but got `%s`", jsonObj.get("picture").toString()));
      }
      if ((jsonObj.get("video") != null && !jsonObj.get("video").isJsonNull()) && !jsonObj.get("video").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `video` to be a primitive type in the JSON string but got `%s`", jsonObj.get("video").toString()));
      }
      if ((jsonObj.get("gender") != null && !jsonObj.get("gender").isJsonNull()) && !jsonObj.get("gender").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `gender` to be a primitive type in the JSON string but got `%s`", jsonObj.get("gender").toString()));
      }
      if ((jsonObj.get("lang") != null && !jsonObj.get("lang").isJsonNull()) && !jsonObj.get("lang").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `lang` to be a primitive type in the JSON string but got `%s`", jsonObj.get("lang").toString()));
      }
      if ((jsonObj.get("greeting") != null && !jsonObj.get("greeting").isJsonNull()) && !jsonObj.get("greeting").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `greeting` to be a primitive type in the JSON string but got `%s`", jsonObj.get("greeting").toString()));
      }
      if ((jsonObj.get("defaultScene") != null && !jsonObj.get("defaultScene").isJsonNull()) && !jsonObj.get("defaultScene").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `defaultScene` to be a primitive type in the JSON string but got `%s`", jsonObj.get("defaultScene").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!CharacterSummaryStatsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'CharacterSummaryStatsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<CharacterSummaryStatsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(CharacterSummaryStatsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<CharacterSummaryStatsDTO>() {
           @Override
           public void write(JsonWriter out, CharacterSummaryStatsDTO value) throws IOException {
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
           public CharacterSummaryStatsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             CharacterSummaryStatsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of CharacterSummaryStatsDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of CharacterSummaryStatsDTO
   * @throws IOException if the JSON string is invalid with respect to CharacterSummaryStatsDTO
   */
  public static CharacterSummaryStatsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, CharacterSummaryStatsDTO.class);
  }

  /**
   * Convert an instance of CharacterSummaryStatsDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

