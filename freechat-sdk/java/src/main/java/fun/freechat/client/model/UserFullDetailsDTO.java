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
 * Account detailed information (with password)
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.11.0")
public class UserFullDetailsDTO {
  public static final String SERIALIZED_NAME_REQUEST_ID = "requestId";
  @SerializedName(SERIALIZED_NAME_REQUEST_ID)
  @javax.annotation.Nullable
  private String requestId;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  @javax.annotation.Nullable
  private String username;

  public static final String SERIALIZED_NAME_NICKNAME = "nickname";
  @SerializedName(SERIALIZED_NAME_NICKNAME)
  @javax.annotation.Nullable
  private String nickname;

  public static final String SERIALIZED_NAME_GIVEN_NAME = "givenName";
  @SerializedName(SERIALIZED_NAME_GIVEN_NAME)
  @javax.annotation.Nullable
  private String givenName;

  public static final String SERIALIZED_NAME_MIDDLE_NAME = "middleName";
  @SerializedName(SERIALIZED_NAME_MIDDLE_NAME)
  @javax.annotation.Nullable
  private String middleName;

  public static final String SERIALIZED_NAME_FAMILY_NAME = "familyName";
  @SerializedName(SERIALIZED_NAME_FAMILY_NAME)
  @javax.annotation.Nullable
  private String familyName;

  public static final String SERIALIZED_NAME_PREFERRED_USERNAME = "preferredUsername";
  @SerializedName(SERIALIZED_NAME_PREFERRED_USERNAME)
  @javax.annotation.Nullable
  private String preferredUsername;

  public static final String SERIALIZED_NAME_PROFILE = "profile";
  @SerializedName(SERIALIZED_NAME_PROFILE)
  @javax.annotation.Nullable
  private String profile;

  public static final String SERIALIZED_NAME_PICTURE = "picture";
  @SerializedName(SERIALIZED_NAME_PICTURE)
  @javax.annotation.Nullable
  private String picture;

  public static final String SERIALIZED_NAME_WEBSITE = "website";
  @SerializedName(SERIALIZED_NAME_WEBSITE)
  @javax.annotation.Nullable
  private String website;

  public static final String SERIALIZED_NAME_EMAIL = "email";
  @SerializedName(SERIALIZED_NAME_EMAIL)
  @javax.annotation.Nullable
  private String email;

  public static final String SERIALIZED_NAME_GENDER = "gender";
  @SerializedName(SERIALIZED_NAME_GENDER)
  @javax.annotation.Nullable
  private String gender;

  public static final String SERIALIZED_NAME_BIRTHDATE = "birthdate";
  @SerializedName(SERIALIZED_NAME_BIRTHDATE)
  @javax.annotation.Nullable
  private OffsetDateTime birthdate;

  public static final String SERIALIZED_NAME_ZONEINFO = "zoneinfo";
  @SerializedName(SERIALIZED_NAME_ZONEINFO)
  @javax.annotation.Nullable
  private String zoneinfo;

  public static final String SERIALIZED_NAME_LOCALE = "locale";
  @SerializedName(SERIALIZED_NAME_LOCALE)
  @javax.annotation.Nullable
  private String locale;

  public static final String SERIALIZED_NAME_PHONE_NUMBER = "phoneNumber";
  @SerializedName(SERIALIZED_NAME_PHONE_NUMBER)
  @javax.annotation.Nullable
  private String phoneNumber;

  public static final String SERIALIZED_NAME_UPDATED_AT = "updatedAt";
  @SerializedName(SERIALIZED_NAME_UPDATED_AT)
  @javax.annotation.Nullable
  private OffsetDateTime updatedAt;

  public static final String SERIALIZED_NAME_PLATFORM = "platform";
  @SerializedName(SERIALIZED_NAME_PLATFORM)
  @javax.annotation.Nullable
  private String platform;

  public static final String SERIALIZED_NAME_ENABLED = "enabled";
  @SerializedName(SERIALIZED_NAME_ENABLED)
  @javax.annotation.Nullable
  private Boolean enabled;

  public static final String SERIALIZED_NAME_LOCKED = "locked";
  @SerializedName(SERIALIZED_NAME_LOCKED)
  @javax.annotation.Nullable
  private Boolean locked;

  public static final String SERIALIZED_NAME_EXPIRES_AT = "expiresAt";
  @SerializedName(SERIALIZED_NAME_EXPIRES_AT)
  @javax.annotation.Nullable
  private OffsetDateTime expiresAt;

  public static final String SERIALIZED_NAME_PASSWORD_EXPIRES_AT = "passwordExpiresAt";
  @SerializedName(SERIALIZED_NAME_PASSWORD_EXPIRES_AT)
  @javax.annotation.Nullable
  private OffsetDateTime passwordExpiresAt;

  public static final String SERIALIZED_NAME_ADDRESS = "address";
  @SerializedName(SERIALIZED_NAME_ADDRESS)
  @javax.annotation.Nullable
  private String address;

  public static final String SERIALIZED_NAME_PASSWORD = "password";
  @SerializedName(SERIALIZED_NAME_PASSWORD)
  @javax.annotation.Nullable
  private String password;

  public UserFullDetailsDTO() {
  }

  public UserFullDetailsDTO requestId(@javax.annotation.Nullable String requestId) {
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


  public UserFullDetailsDTO username(@javax.annotation.Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
   */
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(@javax.annotation.Nullable String username) {
    this.username = username;
  }


  public UserFullDetailsDTO nickname(@javax.annotation.Nullable String nickname) {
    this.nickname = nickname;
    return this;
  }

  /**
   * Get nickname
   * @return nickname
   */
  @javax.annotation.Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@javax.annotation.Nullable String nickname) {
    this.nickname = nickname;
  }


  public UserFullDetailsDTO givenName(@javax.annotation.Nullable String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Get givenName
   * @return givenName
   */
  @javax.annotation.Nullable
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(@javax.annotation.Nullable String givenName) {
    this.givenName = givenName;
  }


  public UserFullDetailsDTO middleName(@javax.annotation.Nullable String middleName) {
    this.middleName = middleName;
    return this;
  }

  /**
   * Get middleName
   * @return middleName
   */
  @javax.annotation.Nullable
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(@javax.annotation.Nullable String middleName) {
    this.middleName = middleName;
  }


  public UserFullDetailsDTO familyName(@javax.annotation.Nullable String familyName) {
    this.familyName = familyName;
    return this;
  }

  /**
   * Get familyName
   * @return familyName
   */
  @javax.annotation.Nullable
  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(@javax.annotation.Nullable String familyName) {
    this.familyName = familyName;
  }


  public UserFullDetailsDTO preferredUsername(@javax.annotation.Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  /**
   * Get preferredUsername
   * @return preferredUsername
   */
  @javax.annotation.Nullable
  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(@javax.annotation.Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }


  public UserFullDetailsDTO profile(@javax.annotation.Nullable String profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
   */
  @javax.annotation.Nullable
  public String getProfile() {
    return profile;
  }

  public void setProfile(@javax.annotation.Nullable String profile) {
    this.profile = profile;
  }


  public UserFullDetailsDTO picture(@javax.annotation.Nullable String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Get picture
   * @return picture
   */
  @javax.annotation.Nullable
  public String getPicture() {
    return picture;
  }

  public void setPicture(@javax.annotation.Nullable String picture) {
    this.picture = picture;
  }


  public UserFullDetailsDTO website(@javax.annotation.Nullable String website) {
    this.website = website;
    return this;
  }

  /**
   * Get website
   * @return website
   */
  @javax.annotation.Nullable
  public String getWebsite() {
    return website;
  }

  public void setWebsite(@javax.annotation.Nullable String website) {
    this.website = website;
  }


  public UserFullDetailsDTO email(@javax.annotation.Nullable String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @javax.annotation.Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@javax.annotation.Nullable String email) {
    this.email = email;
  }


  public UserFullDetailsDTO gender(@javax.annotation.Nullable String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
   */
  @javax.annotation.Nullable
  public String getGender() {
    return gender;
  }

  public void setGender(@javax.annotation.Nullable String gender) {
    this.gender = gender;
  }


  public UserFullDetailsDTO birthdate(@javax.annotation.Nullable OffsetDateTime birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  /**
   * Get birthdate
   * @return birthdate
   */
  @javax.annotation.Nullable
  public OffsetDateTime getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(@javax.annotation.Nullable OffsetDateTime birthdate) {
    this.birthdate = birthdate;
  }


  public UserFullDetailsDTO zoneinfo(@javax.annotation.Nullable String zoneinfo) {
    this.zoneinfo = zoneinfo;
    return this;
  }

  /**
   * Get zoneinfo
   * @return zoneinfo
   */
  @javax.annotation.Nullable
  public String getZoneinfo() {
    return zoneinfo;
  }

  public void setZoneinfo(@javax.annotation.Nullable String zoneinfo) {
    this.zoneinfo = zoneinfo;
  }


  public UserFullDetailsDTO locale(@javax.annotation.Nullable String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Get locale
   * @return locale
   */
  @javax.annotation.Nullable
  public String getLocale() {
    return locale;
  }

  public void setLocale(@javax.annotation.Nullable String locale) {
    this.locale = locale;
  }


  public UserFullDetailsDTO phoneNumber(@javax.annotation.Nullable String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  @javax.annotation.Nullable
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(@javax.annotation.Nullable String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }


  public UserFullDetailsDTO updatedAt(@javax.annotation.Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @javax.annotation.Nullable
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@javax.annotation.Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }


  public UserFullDetailsDTO platform(@javax.annotation.Nullable String platform) {
    this.platform = platform;
    return this;
  }

  /**
   * Get platform
   * @return platform
   */
  @javax.annotation.Nullable
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@javax.annotation.Nullable String platform) {
    this.platform = platform;
  }


  public UserFullDetailsDTO enabled(@javax.annotation.Nullable Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
   */
  @javax.annotation.Nullable
  public Boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(@javax.annotation.Nullable Boolean enabled) {
    this.enabled = enabled;
  }


  public UserFullDetailsDTO locked(@javax.annotation.Nullable Boolean locked) {
    this.locked = locked;
    return this;
  }

  /**
   * Get locked
   * @return locked
   */
  @javax.annotation.Nullable
  public Boolean isLocked() {
    return locked;
  }

  public void setLocked(@javax.annotation.Nullable Boolean locked) {
    this.locked = locked;
  }


  public UserFullDetailsDTO expiresAt(@javax.annotation.Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
    return this;
  }

  /**
   * Get expiresAt
   * @return expiresAt
   */
  @javax.annotation.Nullable
  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(@javax.annotation.Nullable OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }


  public UserFullDetailsDTO passwordExpiresAt(@javax.annotation.Nullable OffsetDateTime passwordExpiresAt) {
    this.passwordExpiresAt = passwordExpiresAt;
    return this;
  }

  /**
   * Get passwordExpiresAt
   * @return passwordExpiresAt
   */
  @javax.annotation.Nullable
  public OffsetDateTime getPasswordExpiresAt() {
    return passwordExpiresAt;
  }

  public void setPasswordExpiresAt(@javax.annotation.Nullable OffsetDateTime passwordExpiresAt) {
    this.passwordExpiresAt = passwordExpiresAt;
  }


  public UserFullDetailsDTO address(@javax.annotation.Nullable String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @javax.annotation.Nullable
  public String getAddress() {
    return address;
  }

  public void setAddress(@javax.annotation.Nullable String address) {
    this.address = address;
  }


  public UserFullDetailsDTO password(@javax.annotation.Nullable String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
   */
  @javax.annotation.Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@javax.annotation.Nullable String password) {
    this.password = password;
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
   * @return the UserFullDetailsDTO instance itself
   */
  public UserFullDetailsDTO putAdditionalProperty(String key, Object value) {
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
    UserFullDetailsDTO userFullDetailsDTO = (UserFullDetailsDTO) o;
    return Objects.equals(this.requestId, userFullDetailsDTO.requestId) &&
        Objects.equals(this.username, userFullDetailsDTO.username) &&
        Objects.equals(this.nickname, userFullDetailsDTO.nickname) &&
        Objects.equals(this.givenName, userFullDetailsDTO.givenName) &&
        Objects.equals(this.middleName, userFullDetailsDTO.middleName) &&
        Objects.equals(this.familyName, userFullDetailsDTO.familyName) &&
        Objects.equals(this.preferredUsername, userFullDetailsDTO.preferredUsername) &&
        Objects.equals(this.profile, userFullDetailsDTO.profile) &&
        Objects.equals(this.picture, userFullDetailsDTO.picture) &&
        Objects.equals(this.website, userFullDetailsDTO.website) &&
        Objects.equals(this.email, userFullDetailsDTO.email) &&
        Objects.equals(this.gender, userFullDetailsDTO.gender) &&
        Objects.equals(this.birthdate, userFullDetailsDTO.birthdate) &&
        Objects.equals(this.zoneinfo, userFullDetailsDTO.zoneinfo) &&
        Objects.equals(this.locale, userFullDetailsDTO.locale) &&
        Objects.equals(this.phoneNumber, userFullDetailsDTO.phoneNumber) &&
        Objects.equals(this.updatedAt, userFullDetailsDTO.updatedAt) &&
        Objects.equals(this.platform, userFullDetailsDTO.platform) &&
        Objects.equals(this.enabled, userFullDetailsDTO.enabled) &&
        Objects.equals(this.locked, userFullDetailsDTO.locked) &&
        Objects.equals(this.expiresAt, userFullDetailsDTO.expiresAt) &&
        Objects.equals(this.passwordExpiresAt, userFullDetailsDTO.passwordExpiresAt) &&
        Objects.equals(this.address, userFullDetailsDTO.address) &&
        Objects.equals(this.password, userFullDetailsDTO.password)&&
        Objects.equals(this.additionalProperties, userFullDetailsDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, username, nickname, givenName, middleName, familyName, preferredUsername, profile, picture, website, email, gender, birthdate, zoneinfo, locale, phoneNumber, updatedAt, platform, enabled, locked, expiresAt, passwordExpiresAt, address, password, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserFullDetailsDTO {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    preferredUsername: ").append(toIndentedString(preferredUsername)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    birthdate: ").append(toIndentedString(birthdate)).append("\n");
    sb.append("    zoneinfo: ").append(toIndentedString(zoneinfo)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    locked: ").append(toIndentedString(locked)).append("\n");
    sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
    sb.append("    passwordExpiresAt: ").append(toIndentedString(passwordExpiresAt)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
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
    openapiFields.add("username");
    openapiFields.add("nickname");
    openapiFields.add("givenName");
    openapiFields.add("middleName");
    openapiFields.add("familyName");
    openapiFields.add("preferredUsername");
    openapiFields.add("profile");
    openapiFields.add("picture");
    openapiFields.add("website");
    openapiFields.add("email");
    openapiFields.add("gender");
    openapiFields.add("birthdate");
    openapiFields.add("zoneinfo");
    openapiFields.add("locale");
    openapiFields.add("phoneNumber");
    openapiFields.add("updatedAt");
    openapiFields.add("platform");
    openapiFields.add("enabled");
    openapiFields.add("locked");
    openapiFields.add("expiresAt");
    openapiFields.add("passwordExpiresAt");
    openapiFields.add("address");
    openapiFields.add("password");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to UserFullDetailsDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!UserFullDetailsDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in UserFullDetailsDTO is not found in the empty JSON string", UserFullDetailsDTO.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("requestId") != null && !jsonObj.get("requestId").isJsonNull()) && !jsonObj.get("requestId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `requestId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("requestId").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      if ((jsonObj.get("nickname") != null && !jsonObj.get("nickname").isJsonNull()) && !jsonObj.get("nickname").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `nickname` to be a primitive type in the JSON string but got `%s`", jsonObj.get("nickname").toString()));
      }
      if ((jsonObj.get("givenName") != null && !jsonObj.get("givenName").isJsonNull()) && !jsonObj.get("givenName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `givenName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("givenName").toString()));
      }
      if ((jsonObj.get("middleName") != null && !jsonObj.get("middleName").isJsonNull()) && !jsonObj.get("middleName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `middleName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("middleName").toString()));
      }
      if ((jsonObj.get("familyName") != null && !jsonObj.get("familyName").isJsonNull()) && !jsonObj.get("familyName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `familyName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("familyName").toString()));
      }
      if ((jsonObj.get("preferredUsername") != null && !jsonObj.get("preferredUsername").isJsonNull()) && !jsonObj.get("preferredUsername").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `preferredUsername` to be a primitive type in the JSON string but got `%s`", jsonObj.get("preferredUsername").toString()));
      }
      if ((jsonObj.get("profile") != null && !jsonObj.get("profile").isJsonNull()) && !jsonObj.get("profile").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `profile` to be a primitive type in the JSON string but got `%s`", jsonObj.get("profile").toString()));
      }
      if ((jsonObj.get("picture") != null && !jsonObj.get("picture").isJsonNull()) && !jsonObj.get("picture").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `picture` to be a primitive type in the JSON string but got `%s`", jsonObj.get("picture").toString()));
      }
      if ((jsonObj.get("website") != null && !jsonObj.get("website").isJsonNull()) && !jsonObj.get("website").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `website` to be a primitive type in the JSON string but got `%s`", jsonObj.get("website").toString()));
      }
      if ((jsonObj.get("email") != null && !jsonObj.get("email").isJsonNull()) && !jsonObj.get("email").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `email` to be a primitive type in the JSON string but got `%s`", jsonObj.get("email").toString()));
      }
      if ((jsonObj.get("gender") != null && !jsonObj.get("gender").isJsonNull()) && !jsonObj.get("gender").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `gender` to be a primitive type in the JSON string but got `%s`", jsonObj.get("gender").toString()));
      }
      if ((jsonObj.get("zoneinfo") != null && !jsonObj.get("zoneinfo").isJsonNull()) && !jsonObj.get("zoneinfo").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `zoneinfo` to be a primitive type in the JSON string but got `%s`", jsonObj.get("zoneinfo").toString()));
      }
      if ((jsonObj.get("locale") != null && !jsonObj.get("locale").isJsonNull()) && !jsonObj.get("locale").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `locale` to be a primitive type in the JSON string but got `%s`", jsonObj.get("locale").toString()));
      }
      if ((jsonObj.get("phoneNumber") != null && !jsonObj.get("phoneNumber").isJsonNull()) && !jsonObj.get("phoneNumber").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `phoneNumber` to be a primitive type in the JSON string but got `%s`", jsonObj.get("phoneNumber").toString()));
      }
      if ((jsonObj.get("platform") != null && !jsonObj.get("platform").isJsonNull()) && !jsonObj.get("platform").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `platform` to be a primitive type in the JSON string but got `%s`", jsonObj.get("platform").toString()));
      }
      if ((jsonObj.get("address") != null && !jsonObj.get("address").isJsonNull()) && !jsonObj.get("address").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `address` to be a primitive type in the JSON string but got `%s`", jsonObj.get("address").toString()));
      }
      if ((jsonObj.get("password") != null && !jsonObj.get("password").isJsonNull()) && !jsonObj.get("password").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `password` to be a primitive type in the JSON string but got `%s`", jsonObj.get("password").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!UserFullDetailsDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'UserFullDetailsDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<UserFullDetailsDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(UserFullDetailsDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<UserFullDetailsDTO>() {
           @Override
           public void write(JsonWriter out, UserFullDetailsDTO value) throws IOException {
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
           public UserFullDetailsDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             UserFullDetailsDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of UserFullDetailsDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of UserFullDetailsDTO
   * @throws IOException if the JSON string is invalid with respect to UserFullDetailsDTO
   */
  public static UserFullDetailsDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, UserFullDetailsDTO.class);
  }

  /**
   * Convert an instance of UserFullDetailsDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

