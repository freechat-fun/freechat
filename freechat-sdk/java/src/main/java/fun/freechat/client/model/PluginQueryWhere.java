/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.2.0
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.freechat.client.JSON;

/**
 * Query condition
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.10.0")
public class PluginQueryWhere {
  public static final String SERIALIZED_NAME_VISIBILITY = "visibility";
  @SerializedName(SERIALIZED_NAME_VISIBILITY)
  @javax.annotation.Nullable
  private String visibility;

  public static final String SERIALIZED_NAME_USERNAME = "username";
  @SerializedName(SERIALIZED_NAME_USERNAME)
  @javax.annotation.Nullable
  private String username;

  public static final String SERIALIZED_NAME_MANIFEST_FORMAT = "manifestFormat";
  @SerializedName(SERIALIZED_NAME_MANIFEST_FORMAT)
  @javax.annotation.Nullable
  private String manifestFormat;

  public static final String SERIALIZED_NAME_API_FORMAT = "apiFormat";
  @SerializedName(SERIALIZED_NAME_API_FORMAT)
  @javax.annotation.Nullable
  private String apiFormat;

  public static final String SERIALIZED_NAME_TAGS = "tags";
  @SerializedName(SERIALIZED_NAME_TAGS)
  @javax.annotation.Nullable
  private List<String> tags = new ArrayList<>();

  public static final String SERIALIZED_NAME_TAGS_OP = "tagsOp";
  @SerializedName(SERIALIZED_NAME_TAGS_OP)
  @javax.annotation.Nullable
  private String tagsOp;

  public static final String SERIALIZED_NAME_AI_MODELS = "aiModels";
  @SerializedName(SERIALIZED_NAME_AI_MODELS)
  @javax.annotation.Nullable
  private List<String> aiModels = new ArrayList<>();

  public static final String SERIALIZED_NAME_AI_MODELS_OP = "aiModelsOp";
  @SerializedName(SERIALIZED_NAME_AI_MODELS_OP)
  @javax.annotation.Nullable
  private String aiModelsOp;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nullable
  private String name;

  public static final String SERIALIZED_NAME_PROVIDER = "provider";
  @SerializedName(SERIALIZED_NAME_PROVIDER)
  @javax.annotation.Nullable
  private String provider;

  public static final String SERIALIZED_NAME_TEXT = "text";
  @SerializedName(SERIALIZED_NAME_TEXT)
  @javax.annotation.Nullable
  private String text;

  public PluginQueryWhere() {
  }

  public PluginQueryWhere visibility(@javax.annotation.Nullable String visibility) {
    this.visibility = visibility;
    return this;
  }

  /**
   * Visibility: public, public_org (search this organization), private (default)
   * @return visibility
   */
  @javax.annotation.Nullable
  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(@javax.annotation.Nullable String visibility) {
    this.visibility = visibility;
  }


  public PluginQueryWhere username(@javax.annotation.Nullable String username) {
    this.username = username;
    return this;
  }

  /**
   * Effective when searching public, public_org prompts, if not specified, search all users
   * @return username
   */
  @javax.annotation.Nullable
  public String getUsername() {
    return username;
  }

  public void setUsername(@javax.annotation.Nullable String username) {
    this.username = username;
  }


  public PluginQueryWhere manifestFormat(@javax.annotation.Nullable String manifestFormat) {
    this.manifestFormat = manifestFormat;
    return this;
  }

  /**
   * Manifest configuration format, currently supported: dash_scope, open_ai.
   * @return manifestFormat
   */
  @javax.annotation.Nullable
  public String getManifestFormat() {
    return manifestFormat;
  }

  public void setManifestFormat(@javax.annotation.Nullable String manifestFormat) {
    this.manifestFormat = manifestFormat;
  }


  public PluginQueryWhere apiFormat(@javax.annotation.Nullable String apiFormat) {
    this.apiFormat = apiFormat;
    return this;
  }

  /**
   * API description format, currently supported: openapi_v3.
   * @return apiFormat
   */
  @javax.annotation.Nullable
  public String getApiFormat() {
    return apiFormat;
  }

  public void setApiFormat(@javax.annotation.Nullable String apiFormat) {
    this.apiFormat = apiFormat;
  }


  public PluginQueryWhere tags(@javax.annotation.Nullable List<String> tags) {
    this.tags = tags;
    return this;
  }

  public PluginQueryWhere addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Tags
   * @return tags
   */
  @javax.annotation.Nullable
  public List<String> getTags() {
    return tags;
  }

  public void setTags(@javax.annotation.Nullable List<String> tags) {
    this.tags = tags;
  }


  public PluginQueryWhere tagsOp(@javax.annotation.Nullable String tagsOp) {
    this.tagsOp = tagsOp;
    return this;
  }

  /**
   * Relationship between tags: and | or (default)
   * @return tagsOp
   */
  @javax.annotation.Nullable
  public String getTagsOp() {
    return tagsOp;
  }

  public void setTagsOp(@javax.annotation.Nullable String tagsOp) {
    this.tagsOp = tagsOp;
  }


  public PluginQueryWhere aiModels(@javax.annotation.Nullable List<String> aiModels) {
    this.aiModels = aiModels;
    return this;
  }

  public PluginQueryWhere addAiModelsItem(String aiModelsItem) {
    if (this.aiModels == null) {
      this.aiModels = new ArrayList<>();
    }
    this.aiModels.add(aiModelsItem);
    return this;
  }

  /**
   * Model set
   * @return aiModels
   */
  @javax.annotation.Nullable
  public List<String> getAiModels() {
    return aiModels;
  }

  public void setAiModels(@javax.annotation.Nullable List<String> aiModels) {
    this.aiModels = aiModels;
  }


  public PluginQueryWhere aiModelsOp(@javax.annotation.Nullable String aiModelsOp) {
    this.aiModelsOp = aiModelsOp;
    return this;
  }

  /**
   * Relationship between model sets: and | or (default)
   * @return aiModelsOp
   */
  @javax.annotation.Nullable
  public String getAiModelsOp() {
    return aiModelsOp;
  }

  public void setAiModelsOp(@javax.annotation.Nullable String aiModelsOp) {
    this.aiModelsOp = aiModelsOp;
  }


  public PluginQueryWhere name(@javax.annotation.Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Name, left match
   * @return name
   */
  @javax.annotation.Nullable
  public String getName() {
    return name;
  }

  public void setName(@javax.annotation.Nullable String name) {
    this.name = name;
  }


  public PluginQueryWhere provider(@javax.annotation.Nullable String provider) {
    this.provider = provider;
    return this;
  }

  /**
   * Provider, left match
   * @return provider
   */
  @javax.annotation.Nullable
  public String getProvider() {
    return provider;
  }

  public void setProvider(@javax.annotation.Nullable String provider) {
    this.provider = provider;
  }


  public PluginQueryWhere text(@javax.annotation.Nullable String text) {
    this.text = text;
    return this;
  }

  /**
   * Name, provider Information, manifest (real-time pulling is not supported at the moment), fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time.
   * @return text
   */
  @javax.annotation.Nullable
  public String getText() {
    return text;
  }

  public void setText(@javax.annotation.Nullable String text) {
    this.text = text;
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
   * @return the PluginQueryWhere instance itself
   */
  public PluginQueryWhere putAdditionalProperty(String key, Object value) {
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
    PluginQueryWhere pluginQueryWhere = (PluginQueryWhere) o;
    return Objects.equals(this.visibility, pluginQueryWhere.visibility) &&
        Objects.equals(this.username, pluginQueryWhere.username) &&
        Objects.equals(this.manifestFormat, pluginQueryWhere.manifestFormat) &&
        Objects.equals(this.apiFormat, pluginQueryWhere.apiFormat) &&
        Objects.equals(this.tags, pluginQueryWhere.tags) &&
        Objects.equals(this.tagsOp, pluginQueryWhere.tagsOp) &&
        Objects.equals(this.aiModels, pluginQueryWhere.aiModels) &&
        Objects.equals(this.aiModelsOp, pluginQueryWhere.aiModelsOp) &&
        Objects.equals(this.name, pluginQueryWhere.name) &&
        Objects.equals(this.provider, pluginQueryWhere.provider) &&
        Objects.equals(this.text, pluginQueryWhere.text)&&
        Objects.equals(this.additionalProperties, pluginQueryWhere.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(visibility, username, manifestFormat, apiFormat, tags, tagsOp, aiModels, aiModelsOp, name, provider, text, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginQueryWhere {\n");
    sb.append("    visibility: ").append(toIndentedString(visibility)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    manifestFormat: ").append(toIndentedString(manifestFormat)).append("\n");
    sb.append("    apiFormat: ").append(toIndentedString(apiFormat)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    tagsOp: ").append(toIndentedString(tagsOp)).append("\n");
    sb.append("    aiModels: ").append(toIndentedString(aiModels)).append("\n");
    sb.append("    aiModelsOp: ").append(toIndentedString(aiModelsOp)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
    openapiFields.add("visibility");
    openapiFields.add("username");
    openapiFields.add("manifestFormat");
    openapiFields.add("apiFormat");
    openapiFields.add("tags");
    openapiFields.add("tagsOp");
    openapiFields.add("aiModels");
    openapiFields.add("aiModelsOp");
    openapiFields.add("name");
    openapiFields.add("provider");
    openapiFields.add("text");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to PluginQueryWhere
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!PluginQueryWhere.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in PluginQueryWhere is not found in the empty JSON string", PluginQueryWhere.openapiRequiredFields.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("visibility") != null && !jsonObj.get("visibility").isJsonNull()) && !jsonObj.get("visibility").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `visibility` to be a primitive type in the JSON string but got `%s`", jsonObj.get("visibility").toString()));
      }
      if ((jsonObj.get("username") != null && !jsonObj.get("username").isJsonNull()) && !jsonObj.get("username").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `username` to be a primitive type in the JSON string but got `%s`", jsonObj.get("username").toString()));
      }
      if ((jsonObj.get("manifestFormat") != null && !jsonObj.get("manifestFormat").isJsonNull()) && !jsonObj.get("manifestFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `manifestFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("manifestFormat").toString()));
      }
      if ((jsonObj.get("apiFormat") != null && !jsonObj.get("apiFormat").isJsonNull()) && !jsonObj.get("apiFormat").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `apiFormat` to be a primitive type in the JSON string but got `%s`", jsonObj.get("apiFormat").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("tags") != null && !jsonObj.get("tags").isJsonNull() && !jsonObj.get("tags").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `tags` to be an array in the JSON string but got `%s`", jsonObj.get("tags").toString()));
      }
      if ((jsonObj.get("tagsOp") != null && !jsonObj.get("tagsOp").isJsonNull()) && !jsonObj.get("tagsOp").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `tagsOp` to be a primitive type in the JSON string but got `%s`", jsonObj.get("tagsOp").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("aiModels") != null && !jsonObj.get("aiModels").isJsonNull() && !jsonObj.get("aiModels").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `aiModels` to be an array in the JSON string but got `%s`", jsonObj.get("aiModels").toString()));
      }
      if ((jsonObj.get("aiModelsOp") != null && !jsonObj.get("aiModelsOp").isJsonNull()) && !jsonObj.get("aiModelsOp").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `aiModelsOp` to be a primitive type in the JSON string but got `%s`", jsonObj.get("aiModelsOp").toString()));
      }
      if ((jsonObj.get("name") != null && !jsonObj.get("name").isJsonNull()) && !jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if ((jsonObj.get("provider") != null && !jsonObj.get("provider").isJsonNull()) && !jsonObj.get("provider").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `provider` to be a primitive type in the JSON string but got `%s`", jsonObj.get("provider").toString()));
      }
      if ((jsonObj.get("text") != null && !jsonObj.get("text").isJsonNull()) && !jsonObj.get("text").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `text` to be a primitive type in the JSON string but got `%s`", jsonObj.get("text").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!PluginQueryWhere.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'PluginQueryWhere' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<PluginQueryWhere> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(PluginQueryWhere.class));

       return (TypeAdapter<T>) new TypeAdapter<PluginQueryWhere>() {
           @Override
           public void write(JsonWriter out, PluginQueryWhere value) throws IOException {
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
           public PluginQueryWhere read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             PluginQueryWhere instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of PluginQueryWhere given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of PluginQueryWhere
   * @throws IOException if the JSON string is invalid with respect to PluginQueryWhere
   */
  public static PluginQueryWhere fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PluginQueryWhere.class);
  }

  /**
   * Convert an instance of PluginQueryWhere to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

