/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports setting **quota limits** for characters. - Supports individual **debugging and sharing prompts**.  ## System Snapshots  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml bitnami:   mysql:     enabled: false   redis:     enabled: false   milvus:     enabled: false  mysql:   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   url: <your redis url>   auth:     password: <your redis password>   milvus:   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml bitnami:   prometheus:     enabled: true   grafana:     enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [DashScope](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI and DashScope ([HuggingFace](https://huggingface.co/) is also expected to be supported soon). However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future.
 *
 * The version of the OpenAPI document: 1.1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiCallback;
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.ApiResponse;
import fun.freechat.client.Configuration;
import fun.freechat.client.Pair;
import fun.freechat.client.ProgressRequestBody;
import fun.freechat.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import fun.freechat.client.model.AgentSummaryStatsDTO;
import fun.freechat.client.model.CharacterSummaryStatsDTO;
import fun.freechat.client.model.HotTagDTO;
import fun.freechat.client.model.InteractiveStatsDTO;
import fun.freechat.client.model.PluginSummaryStatsDTO;
import fun.freechat.client.model.PromptSummaryStatsDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractiveStatisticsApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public InteractiveStatisticsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public InteractiveStatisticsApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public int getHostIndex() {
        return localHostIndex;
    }

    public void setHostIndex(int hostIndex) {
        this.localHostIndex = hostIndex;
    }

    public String getCustomBaseUrl() {
        return localCustomBaseUrl;
    }

    public void setCustomBaseUrl(String customBaseUrl) {
        this.localCustomBaseUrl = customBaseUrl;
    }

    /**
     * Build call for addStatistic
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param delta Delta in statistical value (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call addStatisticCall(String infoType, String infoId, String statsType, Long delta, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/{infoType}/{infoId}/{statsType}/{delta}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "infoId" + "}", localVarApiClient.escapeString(infoId.toString()))
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "delta" + "}", localVarApiClient.escapeString(delta.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call addStatisticValidateBeforeCall(String infoType, String infoId, String statsType, Long delta, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling addStatistic(Async)");
        }

        // verify the required parameter 'infoId' is set
        if (infoId == null) {
            throw new ApiException("Missing the required parameter 'infoId' when calling addStatistic(Async)");
        }

        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling addStatistic(Async)");
        }

        // verify the required parameter 'delta' is set
        if (delta == null) {
            throw new ApiException("Missing the required parameter 'delta' when calling addStatistic(Async)");
        }

        return addStatisticCall(infoType, infoId, statsType, delta, _callback);

    }

    /**
     * Add Statistics
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param delta Delta in statistical value (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long addStatistic(String infoType, String infoId, String statsType, Long delta) throws ApiException {
        ApiResponse<Long> localVarResp = addStatisticWithHttpInfo(infoType, infoId, statsType, delta);
        return localVarResp.getData();
    }

    /**
     * Add Statistics
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param delta Delta in statistical value (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> addStatisticWithHttpInfo(String infoType, String infoId, String statsType, Long delta) throws ApiException {
        okhttp3.Call localVarCall = addStatisticValidateBeforeCall(infoType, infoId, statsType, delta, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Add Statistics (asynchronously)
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param delta Delta in statistical value (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call addStatisticAsync(String infoType, String infoId, String statsType, Long delta, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = addStatisticValidateBeforeCall(infoType, infoId, statsType, delta, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getScore
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getScoreCall(String infoType, String infoId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/score/{infoType}/{infoId}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "infoId" + "}", localVarApiClient.escapeString(infoId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call getScoreValidateBeforeCall(String infoType, String infoId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling getScore(Async)");
        }

        // verify the required parameter 'infoId' is set
        if (infoId == null) {
            throw new ApiException("Missing the required parameter 'infoId' when calling getScore(Async)");
        }

        return getScoreCall(infoType, infoId, _callback);

    }

    /**
     * Get Score for Resource
     * Get the current user&#39;s score for the corresponding resource.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long getScore(String infoType, String infoId) throws ApiException {
        ApiResponse<Long> localVarResp = getScoreWithHttpInfo(infoType, infoId);
        return localVarResp.getData();
    }

    /**
     * Get Score for Resource
     * Get the current user&#39;s score for the corresponding resource.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> getScoreWithHttpInfo(String infoType, String infoId) throws ApiException {
        okhttp3.Call localVarCall = getScoreValidateBeforeCall(infoType, infoId, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Score for Resource (asynchronously)
     * Get the current user&#39;s score for the corresponding resource.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getScoreAsync(String infoType, String infoId, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = getScoreValidateBeforeCall(infoType, infoId, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getStatistic
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getStatisticCall(String infoType, String infoId, String statsType, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/{infoType}/{infoId}/{statsType}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "infoId" + "}", localVarApiClient.escapeString(infoId.toString()))
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call getStatisticValidateBeforeCall(String infoType, String infoId, String statsType, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling getStatistic(Async)");
        }

        // verify the required parameter 'infoId' is set
        if (infoId == null) {
            throw new ApiException("Missing the required parameter 'infoId' when calling getStatistic(Async)");
        }

        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling getStatistic(Async)");
        }

        return getStatisticCall(infoType, infoId, statsType, _callback);

    }

    /**
     * Get Statistics
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long getStatistic(String infoType, String infoId, String statsType) throws ApiException {
        ApiResponse<Long> localVarResp = getStatisticWithHttpInfo(infoType, infoId, statsType);
        return localVarResp.getData();
    }

    /**
     * Get Statistics
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> getStatisticWithHttpInfo(String infoType, String infoId, String statsType) throws ApiException {
        okhttp3.Call localVarCall = getStatisticValidateBeforeCall(infoType, infoId, statsType, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Statistics (asynchronously)
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getStatisticAsync(String infoType, String infoId, String statsType, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = getStatisticValidateBeforeCall(infoType, infoId, statsType, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getStatistics
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getStatisticsCall(String infoType, String infoId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/{infoType}/{infoId}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "infoId" + "}", localVarApiClient.escapeString(infoId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call getStatisticsValidateBeforeCall(String infoType, String infoId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling getStatistics(Async)");
        }

        // verify the required parameter 'infoId' is set
        if (infoId == null) {
            throw new ApiException("Missing the required parameter 'infoId' when calling getStatistics(Async)");
        }

        return getStatisticsCall(infoType, infoId, _callback);

    }

    /**
     * Get All Statistics
     * Get all statistics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @return InteractiveStatsDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public InteractiveStatsDTO getStatistics(String infoType, String infoId) throws ApiException {
        ApiResponse<InteractiveStatsDTO> localVarResp = getStatisticsWithHttpInfo(infoType, infoId);
        return localVarResp.getData();
    }

    /**
     * Get All Statistics
     * Get all statistics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @return ApiResponse&lt;InteractiveStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<InteractiveStatsDTO> getStatisticsWithHttpInfo(String infoType, String infoId) throws ApiException {
        okhttp3.Call localVarCall = getStatisticsValidateBeforeCall(infoType, infoId, null);
        Type localVarReturnType = new TypeToken<InteractiveStatsDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get All Statistics (asynchronously)
     * Get all statistics of the corresponding resources.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getStatisticsAsync(String infoType, String infoId, final ApiCallback<InteractiveStatsDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getStatisticsValidateBeforeCall(infoType, infoId, _callback);
        Type localVarReturnType = new TypeToken<InteractiveStatsDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for increaseStatistic
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call increaseStatisticCall(String infoType, String infoId, String statsType, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/{infoType}/{infoId}/{statsType}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "infoId" + "}", localVarApiClient.escapeString(infoId.toString()))
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call increaseStatisticValidateBeforeCall(String infoType, String infoId, String statsType, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling increaseStatistic(Async)");
        }

        // verify the required parameter 'infoId' is set
        if (infoId == null) {
            throw new ApiException("Missing the required parameter 'infoId' when calling increaseStatistic(Async)");
        }

        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling increaseStatistic(Async)");
        }

        return increaseStatisticCall(infoType, infoId, statsType, _callback);

    }

    /**
     * Increase Statistics
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long increaseStatistic(String infoType, String infoId, String statsType) throws ApiException {
        ApiResponse<Long> localVarResp = increaseStatisticWithHttpInfo(infoType, infoId, statsType);
        return localVarResp.getData();
    }

    /**
     * Increase Statistics
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> increaseStatisticWithHttpInfo(String infoType, String infoId, String statsType) throws ApiException {
        okhttp3.Call localVarCall = increaseStatisticValidateBeforeCall(infoType, infoId, statsType, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Increase Statistics (asynchronously)
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param infoId Unique resource identifier (required)
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call increaseStatisticAsync(String infoType, String infoId, String statsType, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = increaseStatisticValidateBeforeCall(infoType, infoId, statsType, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listAgentsByStatistic
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatisticCall(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/agents/by/{statsType}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listAgentsByStatisticValidateBeforeCall(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listAgentsByStatistic(Async)");
        }

        return listAgentsByStatisticCall(statsType, asc, _callback);

    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;AgentSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<AgentSummaryStatsDTO> listAgentsByStatistic(String statsType, String asc) throws ApiException {
        ApiResponse<List<AgentSummaryStatsDTO>> localVarResp = listAgentsByStatisticWithHttpInfo(statsType, asc);
        return localVarResp.getData();
    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;AgentSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<AgentSummaryStatsDTO>> listAgentsByStatisticWithHttpInfo(String statsType, String asc) throws ApiException {
        okhttp3.Call localVarCall = listAgentsByStatisticValidateBeforeCall(statsType, asc, null);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Agents by Statistics (asynchronously)
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatisticAsync(String statsType, String asc, final ApiCallback<List<AgentSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listAgentsByStatisticValidateBeforeCall(statsType, asc, _callback);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listAgentsByStatistic1
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatistic1Call(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/agents/by/{statsType}/{pageSize}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listAgentsByStatistic1ValidateBeforeCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listAgentsByStatistic1(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listAgentsByStatistic1(Async)");
        }

        return listAgentsByStatistic1Call(statsType, pageSize, asc, _callback);

    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;AgentSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<AgentSummaryStatsDTO> listAgentsByStatistic1(String statsType, Long pageSize, String asc) throws ApiException {
        ApiResponse<List<AgentSummaryStatsDTO>> localVarResp = listAgentsByStatistic1WithHttpInfo(statsType, pageSize, asc);
        return localVarResp.getData();
    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;AgentSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<AgentSummaryStatsDTO>> listAgentsByStatistic1WithHttpInfo(String statsType, Long pageSize, String asc) throws ApiException {
        okhttp3.Call localVarCall = listAgentsByStatistic1ValidateBeforeCall(statsType, pageSize, asc, null);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Agents by Statistics (asynchronously)
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatistic1Async(String statsType, Long pageSize, String asc, final ApiCallback<List<AgentSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listAgentsByStatistic1ValidateBeforeCall(statsType, pageSize, asc, _callback);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listAgentsByStatistic2
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatistic2Call(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/agents/by/{statsType}/{pageSize}/{pageNum}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()))
            .replace("{" + "pageNum" + "}", localVarApiClient.escapeString(pageNum.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listAgentsByStatistic2ValidateBeforeCall(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listAgentsByStatistic2(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listAgentsByStatistic2(Async)");
        }

        // verify the required parameter 'pageNum' is set
        if (pageNum == null) {
            throw new ApiException("Missing the required parameter 'pageNum' when calling listAgentsByStatistic2(Async)");
        }

        return listAgentsByStatistic2Call(statsType, pageSize, pageNum, asc, _callback);

    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;AgentSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<AgentSummaryStatsDTO> listAgentsByStatistic2(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        ApiResponse<List<AgentSummaryStatsDTO>> localVarResp = listAgentsByStatistic2WithHttpInfo(statsType, pageSize, pageNum, asc);
        return localVarResp.getData();
    }

    /**
     * List Agents by Statistics
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;AgentSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<AgentSummaryStatsDTO>> listAgentsByStatistic2WithHttpInfo(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        okhttp3.Call localVarCall = listAgentsByStatistic2ValidateBeforeCall(statsType, pageSize, pageNum, asc, null);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Agents by Statistics (asynchronously)
     * List agents based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listAgentsByStatistic2Async(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback<List<AgentSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listAgentsByStatistic2ValidateBeforeCall(statsType, pageSize, pageNum, asc, _callback);
        Type localVarReturnType = new TypeToken<List<AgentSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listCharactersByStatistic
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatisticCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/characters/by/{statsType}/{pageSize}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listCharactersByStatisticValidateBeforeCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listCharactersByStatistic(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listCharactersByStatistic(Async)");
        }

        return listCharactersByStatisticCall(statsType, pageSize, asc, _callback);

    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;CharacterSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<CharacterSummaryStatsDTO> listCharactersByStatistic(String statsType, Long pageSize, String asc) throws ApiException {
        ApiResponse<List<CharacterSummaryStatsDTO>> localVarResp = listCharactersByStatisticWithHttpInfo(statsType, pageSize, asc);
        return localVarResp.getData();
    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;CharacterSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<CharacterSummaryStatsDTO>> listCharactersByStatisticWithHttpInfo(String statsType, Long pageSize, String asc) throws ApiException {
        okhttp3.Call localVarCall = listCharactersByStatisticValidateBeforeCall(statsType, pageSize, asc, null);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Characters by Statistics (asynchronously)
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatisticAsync(String statsType, Long pageSize, String asc, final ApiCallback<List<CharacterSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listCharactersByStatisticValidateBeforeCall(statsType, pageSize, asc, _callback);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listCharactersByStatistic1
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatistic1Call(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()))
            .replace("{" + "pageNum" + "}", localVarApiClient.escapeString(pageNum.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listCharactersByStatistic1ValidateBeforeCall(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listCharactersByStatistic1(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listCharactersByStatistic1(Async)");
        }

        // verify the required parameter 'pageNum' is set
        if (pageNum == null) {
            throw new ApiException("Missing the required parameter 'pageNum' when calling listCharactersByStatistic1(Async)");
        }

        return listCharactersByStatistic1Call(statsType, pageSize, pageNum, asc, _callback);

    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;CharacterSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<CharacterSummaryStatsDTO> listCharactersByStatistic1(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        ApiResponse<List<CharacterSummaryStatsDTO>> localVarResp = listCharactersByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc);
        return localVarResp.getData();
    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;CharacterSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<CharacterSummaryStatsDTO>> listCharactersByStatistic1WithHttpInfo(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        okhttp3.Call localVarCall = listCharactersByStatistic1ValidateBeforeCall(statsType, pageSize, pageNum, asc, null);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Characters by Statistics (asynchronously)
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatistic1Async(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback<List<CharacterSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listCharactersByStatistic1ValidateBeforeCall(statsType, pageSize, pageNum, asc, _callback);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listCharactersByStatistic2
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatistic2Call(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/characters/by/{statsType}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listCharactersByStatistic2ValidateBeforeCall(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listCharactersByStatistic2(Async)");
        }

        return listCharactersByStatistic2Call(statsType, asc, _callback);

    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;CharacterSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<CharacterSummaryStatsDTO> listCharactersByStatistic2(String statsType, String asc) throws ApiException {
        ApiResponse<List<CharacterSummaryStatsDTO>> localVarResp = listCharactersByStatistic2WithHttpInfo(statsType, asc);
        return localVarResp.getData();
    }

    /**
     * List Characters by Statistics
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;CharacterSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<CharacterSummaryStatsDTO>> listCharactersByStatistic2WithHttpInfo(String statsType, String asc) throws ApiException {
        okhttp3.Call localVarCall = listCharactersByStatistic2ValidateBeforeCall(statsType, asc, null);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Characters by Statistics (asynchronously)
     * List characters based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listCharactersByStatistic2Async(String statsType, String asc, final ApiCallback<List<CharacterSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listCharactersByStatistic2ValidateBeforeCall(statsType, asc, _callback);
        Type localVarReturnType = new TypeToken<List<CharacterSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listHotTags
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param pageSize Maximum quantity (required)
     * @param text Key word (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listHotTagsCall(String infoType, Long pageSize, String text, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/tags/hot/{infoType}/{pageSize}"
            .replace("{" + "infoType" + "}", localVarApiClient.escapeString(infoType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (text != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("text", text));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listHotTagsValidateBeforeCall(String infoType, Long pageSize, String text, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'infoType' is set
        if (infoType == null) {
            throw new ApiException("Missing the required parameter 'infoType' when calling listHotTags(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listHotTags(Async)");
        }

        return listHotTagsCall(infoType, pageSize, text, _callback);

    }

    /**
     * Hot Tags
     * Get popular tags for a specified info type.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param pageSize Maximum quantity (required)
     * @param text Key word (optional)
     * @return List&lt;HotTagDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<HotTagDTO> listHotTags(String infoType, Long pageSize, String text) throws ApiException {
        ApiResponse<List<HotTagDTO>> localVarResp = listHotTagsWithHttpInfo(infoType, pageSize, text);
        return localVarResp.getData();
    }

    /**
     * Hot Tags
     * Get popular tags for a specified info type.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param pageSize Maximum quantity (required)
     * @param text Key word (optional)
     * @return ApiResponse&lt;List&lt;HotTagDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<HotTagDTO>> listHotTagsWithHttpInfo(String infoType, Long pageSize, String text) throws ApiException {
        okhttp3.Call localVarCall = listHotTagsValidateBeforeCall(infoType, pageSize, text, null);
        Type localVarReturnType = new TypeToken<List<HotTagDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Hot Tags (asynchronously)
     * Get popular tags for a specified info type.
     * @param infoType Info type: prompt | agent | plugin | character (required)
     * @param pageSize Maximum quantity (required)
     * @param text Key word (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listHotTagsAsync(String infoType, Long pageSize, String text, final ApiCallback<List<HotTagDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listHotTagsValidateBeforeCall(infoType, pageSize, text, _callback);
        Type localVarReturnType = new TypeToken<List<HotTagDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPluginsByStatistic
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatisticCall(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()))
            .replace("{" + "pageNum" + "}", localVarApiClient.escapeString(pageNum.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPluginsByStatisticValidateBeforeCall(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPluginsByStatistic(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listPluginsByStatistic(Async)");
        }

        // verify the required parameter 'pageNum' is set
        if (pageNum == null) {
            throw new ApiException("Missing the required parameter 'pageNum' when calling listPluginsByStatistic(Async)");
        }

        return listPluginsByStatisticCall(statsType, pageSize, pageNum, asc, _callback);

    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PluginSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PluginSummaryStatsDTO> listPluginsByStatistic(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        ApiResponse<List<PluginSummaryStatsDTO>> localVarResp = listPluginsByStatisticWithHttpInfo(statsType, pageSize, pageNum, asc);
        return localVarResp.getData();
    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PluginSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PluginSummaryStatsDTO>> listPluginsByStatisticWithHttpInfo(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPluginsByStatisticValidateBeforeCall(statsType, pageSize, pageNum, asc, null);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Plugins by Statistics (asynchronously)
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatisticAsync(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback<List<PluginSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPluginsByStatisticValidateBeforeCall(statsType, pageSize, pageNum, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPluginsByStatistic1
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatistic1Call(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/plugins/by/{statsType}/{pageSize}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPluginsByStatistic1ValidateBeforeCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPluginsByStatistic1(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listPluginsByStatistic1(Async)");
        }

        return listPluginsByStatistic1Call(statsType, pageSize, asc, _callback);

    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PluginSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PluginSummaryStatsDTO> listPluginsByStatistic1(String statsType, Long pageSize, String asc) throws ApiException {
        ApiResponse<List<PluginSummaryStatsDTO>> localVarResp = listPluginsByStatistic1WithHttpInfo(statsType, pageSize, asc);
        return localVarResp.getData();
    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PluginSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PluginSummaryStatsDTO>> listPluginsByStatistic1WithHttpInfo(String statsType, Long pageSize, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPluginsByStatistic1ValidateBeforeCall(statsType, pageSize, asc, null);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Plugins by Statistics (asynchronously)
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatistic1Async(String statsType, Long pageSize, String asc, final ApiCallback<List<PluginSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPluginsByStatistic1ValidateBeforeCall(statsType, pageSize, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPluginsByStatistic2
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatistic2Call(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/plugins/by/{statsType}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPluginsByStatistic2ValidateBeforeCall(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPluginsByStatistic2(Async)");
        }

        return listPluginsByStatistic2Call(statsType, asc, _callback);

    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PluginSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PluginSummaryStatsDTO> listPluginsByStatistic2(String statsType, String asc) throws ApiException {
        ApiResponse<List<PluginSummaryStatsDTO>> localVarResp = listPluginsByStatistic2WithHttpInfo(statsType, asc);
        return localVarResp.getData();
    }

    /**
     * List Plugins by Statistics
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PluginSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PluginSummaryStatsDTO>> listPluginsByStatistic2WithHttpInfo(String statsType, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPluginsByStatistic2ValidateBeforeCall(statsType, asc, null);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Plugins by Statistics (asynchronously)
     * List plugins based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPluginsByStatistic2Async(String statsType, String asc, final ApiCallback<List<PluginSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPluginsByStatistic2ValidateBeforeCall(statsType, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PluginSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPromptsByStatistic
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatisticCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/prompts/by/{statsType}/{pageSize}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPromptsByStatisticValidateBeforeCall(String statsType, Long pageSize, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPromptsByStatistic(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listPromptsByStatistic(Async)");
        }

        return listPromptsByStatisticCall(statsType, pageSize, asc, _callback);

    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PromptSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PromptSummaryStatsDTO> listPromptsByStatistic(String statsType, Long pageSize, String asc) throws ApiException {
        ApiResponse<List<PromptSummaryStatsDTO>> localVarResp = listPromptsByStatisticWithHttpInfo(statsType, pageSize, asc);
        return localVarResp.getData();
    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PromptSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PromptSummaryStatsDTO>> listPromptsByStatisticWithHttpInfo(String statsType, Long pageSize, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPromptsByStatisticValidateBeforeCall(statsType, pageSize, asc, null);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Prompts by Statistics (asynchronously)
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatisticAsync(String statsType, Long pageSize, String asc, final ApiCallback<List<PromptSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPromptsByStatisticValidateBeforeCall(statsType, pageSize, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPromptsByStatistic1
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatistic1Call(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()))
            .replace("{" + "pageSize" + "}", localVarApiClient.escapeString(pageSize.toString()))
            .replace("{" + "pageNum" + "}", localVarApiClient.escapeString(pageNum.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPromptsByStatistic1ValidateBeforeCall(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPromptsByStatistic1(Async)");
        }

        // verify the required parameter 'pageSize' is set
        if (pageSize == null) {
            throw new ApiException("Missing the required parameter 'pageSize' when calling listPromptsByStatistic1(Async)");
        }

        // verify the required parameter 'pageNum' is set
        if (pageNum == null) {
            throw new ApiException("Missing the required parameter 'pageNum' when calling listPromptsByStatistic1(Async)");
        }

        return listPromptsByStatistic1Call(statsType, pageSize, pageNum, asc, _callback);

    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PromptSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PromptSummaryStatsDTO> listPromptsByStatistic1(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        ApiResponse<List<PromptSummaryStatsDTO>> localVarResp = listPromptsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc);
        return localVarResp.getData();
    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PromptSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PromptSummaryStatsDTO>> listPromptsByStatistic1WithHttpInfo(String statsType, Long pageSize, Long pageNum, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPromptsByStatistic1ValidateBeforeCall(statsType, pageSize, pageNum, asc, null);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Prompts by Statistics (asynchronously)
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param pageSize Maximum quantity (required)
     * @param pageNum Current page number (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatistic1Async(String statsType, Long pageSize, Long pageNum, String asc, final ApiCallback<List<PromptSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPromptsByStatistic1ValidateBeforeCall(statsType, pageSize, pageNum, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listPromptsByStatistic2
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatistic2Call(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/stats/prompts/by/{statsType}"
            .replace("{" + "statsType" + "}", localVarApiClient.escapeString(statsType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (asc != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("asc", asc));
        }

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call listPromptsByStatistic2ValidateBeforeCall(String statsType, String asc, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'statsType' is set
        if (statsType == null) {
            throw new ApiException("Missing the required parameter 'statsType' when calling listPromptsByStatistic2(Async)");
        }

        return listPromptsByStatistic2Call(statsType, asc, _callback);

    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return List&lt;PromptSummaryStatsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PromptSummaryStatsDTO> listPromptsByStatistic2(String statsType, String asc) throws ApiException {
        ApiResponse<List<PromptSummaryStatsDTO>> localVarResp = listPromptsByStatistic2WithHttpInfo(statsType, asc);
        return localVarResp.getData();
    }

    /**
     * List Prompts by Statistics
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @return ApiResponse&lt;List&lt;PromptSummaryStatsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PromptSummaryStatsDTO>> listPromptsByStatistic2WithHttpInfo(String statsType, String asc) throws ApiException {
        okhttp3.Call localVarCall = listPromptsByStatistic2ValidateBeforeCall(statsType, asc, null);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Prompts by Statistics (asynchronously)
     * List prompts based on statistics, including interactive statistical data.
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score (required)
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order (optional)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listPromptsByStatistic2Async(String statsType, String asc, final ApiCallback<List<PromptSummaryStatsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listPromptsByStatistic2ValidateBeforeCall(statsType, asc, _callback);
        Type localVarReturnType = new TypeToken<List<PromptSummaryStatsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
