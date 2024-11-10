/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.1.0
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
 * Qwen series model parameters
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.9.0")
public class QwenParamDTO {
  public static final String SERIALIZED_NAME_API_KEY = "apiKey";
  @SerializedName(SERIALIZED_NAME_API_KEY)
  private String apiKey;

  public static final String SERIALIZED_NAME_API_KEY_NAME = "apiKeyName";
  @SerializedName(SERIALIZED_NAME_API_KEY_NAME)
  private String apiKeyName;

  public static final String SERIALIZED_NAME_MODEL_ID = "modelId";
  @SerializedName(SERIALIZED_NAME_MODEL_ID)
  private String modelId;

  public static final String SERIALIZED_NAME_TOP_P = "topP";
  @SerializedName(SERIALIZED_NAME_TOP_P)
  private Double topP;

  public static final String SERIALIZED_NAME_TOP_K = "topK";
  @SerializedName(SERIALIZED_NAME_TOP_K)
  private Integer topK;

  public static final String SERIALIZED_NAME_MAX_TOKENS = "maxTokens";
  @SerializedName(SERIALIZED_NAME_MAX_TOKENS)
  private Integer maxTokens;

  public static final String SERIALIZED_NAME_ENABLE_SEARCH = "enableSearch";
  @SerializedName(SERIALIZED_NAME_ENABLE_SEARCH)
  private Boolean enableSearch;

  public static final String SERIALIZED_NAME_SEED = "seed";
  @SerializedName(SERIALIZED_NAME_SEED)
  private Integer seed;

  public static final String SERIALIZED_NAME_REPETITION_PENALTY = "repetitionPenalty";
  @SerializedName(SERIALIZED_NAME_REPETITION_PENALTY)
  private Float repetitionPenalty;

  public static final String SERIALIZED_NAME_TEMPERATURE = "temperature";
  @SerializedName(SERIALIZED_NAME_TEMPERATURE)
  private Float temperature;

  public static final String SERIALIZED_NAME_STOP = "stop";
  @SerializedName(SERIALIZED_NAME_STOP)
  private List<String> stop = new ArrayList<>();

  public QwenParamDTO() {
  }

  public QwenParamDTO apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  /**
   * API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.
   * @return apiKey
   */
  @javax.annotation.Nullable
  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }


  public QwenParamDTO apiKeyName(String apiKeyName) {
    this.apiKeyName = apiKeyName;
    return this;
  }

  /**
   * API-KEY name
   * @return apiKeyName
   */
  @javax.annotation.Nullable
  public String getApiKeyName() {
    return apiKeyName;
  }

  public void setApiKeyName(String apiKeyName) {
    this.apiKeyName = apiKeyName;
  }


  public QwenParamDTO modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * Model identifier
   * @return modelId
   */
  @javax.annotation.Nonnull
  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }


  public QwenParamDTO topP(Double topP) {
    this.topP = topP;
    return this;
  }

  /**
   * Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.
   * @return topP
   */
  @javax.annotation.Nullable
  public Double getTopP() {
    return topP;
  }

  public void setTopP(Double topP) {
    this.topP = topP;
  }


  public QwenParamDTO topK(Integer topK) {
    this.topK = topK;
    return this;
  }

  /**
   * The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective.
   * @return topK
   */
  @javax.annotation.Nullable
  public Integer getTopK() {
    return topK;
  }

  public void setTopK(Integer topK) {
    this.topK = topK;
  }


  public QwenParamDTO maxTokens(Integer maxTokens) {
    this.maxTokens = maxTokens;
    return this;
  }

  /**
   * The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model&#39;s context length.
   * @return maxTokens
   */
  @javax.annotation.Nullable
  public Integer getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(Integer maxTokens) {
    this.maxTokens = maxTokens;
  }


  public QwenParamDTO enableSearch(Boolean enableSearch) {
    this.enableSearch = enableSearch;
    return this;
  }

  /**
   * Whether to use a search engine for data enhancement.
   * @return enableSearch
   */
  @javax.annotation.Nullable
  public Boolean isEnableSearch() {
    return enableSearch;
  }

  public void setEnableSearch(Boolean enableSearch) {
    this.enableSearch = enableSearch;
  }


  public QwenParamDTO seed(Integer seed) {
    this.seed = seed;
    return this;
  }

  /**
   * The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.
   * @return seed
   */
  @javax.annotation.Nullable
  public Integer getSeed() {
    return seed;
  }

  public void setSeed(Integer seed) {
    this.seed = seed;
  }


  public QwenParamDTO repetitionPenalty(Float repetitionPenalty) {
    this.repetitionPenalty = repetitionPenalty;
    return this;
  }

  /**
   * Used to control the repeatability when generating models. Increasing repetition_penalty can reduce the duplication of model generation. 1.0 means no punishment.
   * @return repetitionPenalty
   */
  @javax.annotation.Nullable
  public Float getRepetitionPenalty() {
    return repetitionPenalty;
  }

  public void setRepetitionPenalty(Float repetitionPenalty) {
    this.repetitionPenalty = repetitionPenalty;
  }


  public QwenParamDTO temperature(Float temperature) {
    this.temperature = temperature;
    return this;
  }

  /**
   * Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.
   * @return temperature
   */
  @javax.annotation.Nullable
  public Float getTemperature() {
    return temperature;
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }


  public QwenParamDTO stop(List<String> stop) {
    this.stop = stop;
    return this;
  }

  public QwenParamDTO addStopItem(String stopItem) {
    if (this.stop == null) {
      this.stop = new ArrayList<>();
    }
    this.stop.add(stopItem);
    return this;
  }

  /**
   * A collection of stop words that controls the API from generating more tokens.
   * @return stop
   */
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
   * @return the QwenParamDTO instance itself
   */
  public QwenParamDTO putAdditionalProperty(String key, Object value) {
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
    QwenParamDTO qwenParamDTO = (QwenParamDTO) o;
    return Objects.equals(this.apiKey, qwenParamDTO.apiKey) &&
        Objects.equals(this.apiKeyName, qwenParamDTO.apiKeyName) &&
        Objects.equals(this.modelId, qwenParamDTO.modelId) &&
        Objects.equals(this.topP, qwenParamDTO.topP) &&
        Objects.equals(this.topK, qwenParamDTO.topK) &&
        Objects.equals(this.maxTokens, qwenParamDTO.maxTokens) &&
        Objects.equals(this.enableSearch, qwenParamDTO.enableSearch) &&
        Objects.equals(this.seed, qwenParamDTO.seed) &&
        Objects.equals(this.repetitionPenalty, qwenParamDTO.repetitionPenalty) &&
        Objects.equals(this.temperature, qwenParamDTO.temperature) &&
        Objects.equals(this.stop, qwenParamDTO.stop)&&
        Objects.equals(this.additionalProperties, qwenParamDTO.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKey, apiKeyName, modelId, topP, topK, maxTokens, enableSearch, seed, repetitionPenalty, temperature, stop, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QwenParamDTO {\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
    sb.append("    apiKeyName: ").append(toIndentedString(apiKeyName)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    topP: ").append(toIndentedString(topP)).append("\n");
    sb.append("    topK: ").append(toIndentedString(topK)).append("\n");
    sb.append("    maxTokens: ").append(toIndentedString(maxTokens)).append("\n");
    sb.append("    enableSearch: ").append(toIndentedString(enableSearch)).append("\n");
    sb.append("    seed: ").append(toIndentedString(seed)).append("\n");
    sb.append("    repetitionPenalty: ").append(toIndentedString(repetitionPenalty)).append("\n");
    sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
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
    openapiFields.add("topP");
    openapiFields.add("topK");
    openapiFields.add("maxTokens");
    openapiFields.add("enableSearch");
    openapiFields.add("seed");
    openapiFields.add("repetitionPenalty");
    openapiFields.add("temperature");
    openapiFields.add("stop");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("modelId");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to QwenParamDTO
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!QwenParamDTO.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in QwenParamDTO is not found in the empty JSON string", QwenParamDTO.openapiRequiredFields.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : QwenParamDTO.openapiRequiredFields) {
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
      // ensure the optional json data is an array if present
      if (jsonObj.get("stop") != null && !jsonObj.get("stop").isJsonNull() && !jsonObj.get("stop").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `stop` to be an array in the JSON string but got `%s`", jsonObj.get("stop").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!QwenParamDTO.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'QwenParamDTO' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<QwenParamDTO> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(QwenParamDTO.class));

       return (TypeAdapter<T>) new TypeAdapter<QwenParamDTO>() {
           @Override
           public void write(JsonWriter out, QwenParamDTO value) throws IOException {
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
           public QwenParamDTO read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             JsonObject jsonObj = jsonElement.getAsJsonObject();
             // store additional fields in the deserialized instance
             QwenParamDTO instance = thisAdapter.fromJsonTree(jsonObj);
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
   * Create an instance of QwenParamDTO given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of QwenParamDTO
   * @throws IOException if the JSON string is invalid with respect to QwenParamDTO
   */
  public static QwenParamDTO fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, QwenParamDTO.class);
  }

  /**
   * Convert an instance of QwenParamDTO to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

