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


import fun.freechat.client.model.PluginCreateDTO;
import fun.freechat.client.model.PluginDetailsDTO;
import fun.freechat.client.model.PluginQueryDTO;
import fun.freechat.client.model.PluginSummaryDTO;
import fun.freechat.client.model.PluginUpdateDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public PluginApi() {
        this(Configuration.getDefaultApiClient());
    }

    public PluginApi(ApiClient apiClient) {
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
     * Build call for batchSearchPluginDetails
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call batchSearchPluginDetailsCall(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginQueryDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/batch/details/search";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call batchSearchPluginDetailsValidateBeforeCall(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginQueryDTO' is set
        if (pluginQueryDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginQueryDTO' when calling batchSearchPluginDetails(Async)");
        }

        return batchSearchPluginDetailsCall(pluginQueryDTO, _callback);

    }

    /**
     * Batch Search Plugin Details
     * Batch call shortcut for /api/v1/plugin/details/search.
     * @param pluginQueryDTO Query conditions (required)
     * @return List&lt;List&lt;PluginDetailsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<List<PluginDetailsDTO>> batchSearchPluginDetails(List<PluginQueryDTO> pluginQueryDTO) throws ApiException {
        ApiResponse<List<List<PluginDetailsDTO>>> localVarResp = batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO);
        return localVarResp.getData();
    }

    /**
     * Batch Search Plugin Details
     * Batch call shortcut for /api/v1/plugin/details/search.
     * @param pluginQueryDTO Query conditions (required)
     * @return ApiResponse&lt;List&lt;List&lt;PluginDetailsDTO&gt;&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<List<PluginDetailsDTO>>> batchSearchPluginDetailsWithHttpInfo(List<PluginQueryDTO> pluginQueryDTO) throws ApiException {
        okhttp3.Call localVarCall = batchSearchPluginDetailsValidateBeforeCall(pluginQueryDTO, null);
        Type localVarReturnType = new TypeToken<List<List<PluginDetailsDTO>>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Batch Search Plugin Details (asynchronously)
     * Batch call shortcut for /api/v1/plugin/details/search.
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call batchSearchPluginDetailsAsync(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback<List<List<PluginDetailsDTO>>> _callback) throws ApiException {

        okhttp3.Call localVarCall = batchSearchPluginDetailsValidateBeforeCall(pluginQueryDTO, _callback);
        Type localVarReturnType = new TypeToken<List<List<PluginDetailsDTO>>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for batchSearchPluginSummary
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call batchSearchPluginSummaryCall(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginQueryDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/batch/search";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call batchSearchPluginSummaryValidateBeforeCall(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginQueryDTO' is set
        if (pluginQueryDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginQueryDTO' when calling batchSearchPluginSummary(Async)");
        }

        return batchSearchPluginSummaryCall(pluginQueryDTO, _callback);

    }

    /**
     * Batch Search Plugin Summaries
     * Batch call shortcut for /api/v1/plugin/search.
     * @param pluginQueryDTO Query conditions (required)
     * @return List&lt;List&lt;PluginSummaryDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<List<PluginSummaryDTO>> batchSearchPluginSummary(List<PluginQueryDTO> pluginQueryDTO) throws ApiException {
        ApiResponse<List<List<PluginSummaryDTO>>> localVarResp = batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO);
        return localVarResp.getData();
    }

    /**
     * Batch Search Plugin Summaries
     * Batch call shortcut for /api/v1/plugin/search.
     * @param pluginQueryDTO Query conditions (required)
     * @return ApiResponse&lt;List&lt;List&lt;PluginSummaryDTO&gt;&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<List<PluginSummaryDTO>>> batchSearchPluginSummaryWithHttpInfo(List<PluginQueryDTO> pluginQueryDTO) throws ApiException {
        okhttp3.Call localVarCall = batchSearchPluginSummaryValidateBeforeCall(pluginQueryDTO, null);
        Type localVarReturnType = new TypeToken<List<List<PluginSummaryDTO>>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Batch Search Plugin Summaries (asynchronously)
     * Batch call shortcut for /api/v1/plugin/search.
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call batchSearchPluginSummaryAsync(List<PluginQueryDTO> pluginQueryDTO, final ApiCallback<List<List<PluginSummaryDTO>>> _callback) throws ApiException {

        okhttp3.Call localVarCall = batchSearchPluginSummaryValidateBeforeCall(pluginQueryDTO, _callback);
        Type localVarReturnType = new TypeToken<List<List<PluginSummaryDTO>>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for countPlugins
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call countPluginsCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginQueryDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/count";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call countPluginsValidateBeforeCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginQueryDTO' is set
        if (pluginQueryDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginQueryDTO' when calling countPlugins(Async)");
        }

        return countPluginsCall(pluginQueryDTO, _callback);

    }

    /**
     * Calculate Number of Plugins
     * Calculate the number of plugins according to the specified query conditions.
     * @param pluginQueryDTO Query conditions (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long countPlugins(PluginQueryDTO pluginQueryDTO) throws ApiException {
        ApiResponse<Long> localVarResp = countPluginsWithHttpInfo(pluginQueryDTO);
        return localVarResp.getData();
    }

    /**
     * Calculate Number of Plugins
     * Calculate the number of plugins according to the specified query conditions.
     * @param pluginQueryDTO Query conditions (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> countPluginsWithHttpInfo(PluginQueryDTO pluginQueryDTO) throws ApiException {
        okhttp3.Call localVarCall = countPluginsValidateBeforeCall(pluginQueryDTO, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Calculate Number of Plugins (asynchronously)
     * Calculate the number of plugins according to the specified query conditions.
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call countPluginsAsync(PluginQueryDTO pluginQueryDTO, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = countPluginsValidateBeforeCall(pluginQueryDTO, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for createPlugin
     * @param pluginCreateDTO Information of the plugin to be created (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPluginCall(PluginCreateDTO pluginCreateDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginCreateDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call createPluginValidateBeforeCall(PluginCreateDTO pluginCreateDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginCreateDTO' is set
        if (pluginCreateDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginCreateDTO' when calling createPlugin(Async)");
        }

        return createPluginCall(pluginCreateDTO, _callback);

    }

    /**
     * Create Plugin
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * @param pluginCreateDTO Information of the plugin to be created (required)
     * @return Long
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Long createPlugin(PluginCreateDTO pluginCreateDTO) throws ApiException {
        ApiResponse<Long> localVarResp = createPluginWithHttpInfo(pluginCreateDTO);
        return localVarResp.getData();
    }

    /**
     * Create Plugin
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * @param pluginCreateDTO Information of the plugin to be created (required)
     * @return ApiResponse&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Long> createPluginWithHttpInfo(PluginCreateDTO pluginCreateDTO) throws ApiException {
        okhttp3.Call localVarCall = createPluginValidateBeforeCall(pluginCreateDTO, null);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Create Plugin (asynchronously)
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * @param pluginCreateDTO Information of the plugin to be created (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPluginAsync(PluginCreateDTO pluginCreateDTO, final ApiCallback<Long> _callback) throws ApiException {

        okhttp3.Call localVarCall = createPluginValidateBeforeCall(pluginCreateDTO, _callback);
        Type localVarReturnType = new TypeToken<Long>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for createPlugins
     * @param pluginCreateDTO List of plugin information to be created (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPluginsCall(List<PluginCreateDTO> pluginCreateDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginCreateDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/batch";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call createPluginsValidateBeforeCall(List<PluginCreateDTO> pluginCreateDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginCreateDTO' is set
        if (pluginCreateDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginCreateDTO' when calling createPlugins(Async)");
        }

        return createPluginsCall(pluginCreateDTO, _callback);

    }

    /**
     * Batch Create Plugins
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * @param pluginCreateDTO List of plugin information to be created (required)
     * @return List&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<Long> createPlugins(List<PluginCreateDTO> pluginCreateDTO) throws ApiException {
        ApiResponse<List<Long>> localVarResp = createPluginsWithHttpInfo(pluginCreateDTO);
        return localVarResp.getData();
    }

    /**
     * Batch Create Plugins
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * @param pluginCreateDTO List of plugin information to be created (required)
     * @return ApiResponse&lt;List&lt;Long&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<Long>> createPluginsWithHttpInfo(List<PluginCreateDTO> pluginCreateDTO) throws ApiException {
        okhttp3.Call localVarCall = createPluginsValidateBeforeCall(pluginCreateDTO, null);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Batch Create Plugins (asynchronously)
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * @param pluginCreateDTO List of plugin information to be created (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPluginsAsync(List<PluginCreateDTO> pluginCreateDTO, final ApiCallback<List<Long>> _callback) throws ApiException {

        okhttp3.Call localVarCall = createPluginsValidateBeforeCall(pluginCreateDTO, _callback);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for deletePlugin
     * @param pluginId The pluginId to be deleted (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePluginCall(Long pluginId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/plugin/{pluginId}"
            .replace("{" + "pluginId" + "}", localVarApiClient.escapeString(pluginId.toString()));

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
        return localVarApiClient.buildCall(basePath, localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call deletePluginValidateBeforeCall(Long pluginId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginId' is set
        if (pluginId == null) {
            throw new ApiException("Missing the required parameter 'pluginId' when calling deletePlugin(Async)");
        }

        return deletePluginCall(pluginId, _callback);

    }

    /**
     * Delete Plugin
     * Delete plugin. Returns success or failure.
     * @param pluginId The pluginId to be deleted (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean deletePlugin(Long pluginId) throws ApiException {
        ApiResponse<Boolean> localVarResp = deletePluginWithHttpInfo(pluginId);
        return localVarResp.getData();
    }

    /**
     * Delete Plugin
     * Delete plugin. Returns success or failure.
     * @param pluginId The pluginId to be deleted (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> deletePluginWithHttpInfo(Long pluginId) throws ApiException {
        okhttp3.Call localVarCall = deletePluginValidateBeforeCall(pluginId, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Delete Plugin (asynchronously)
     * Delete plugin. Returns success or failure.
     * @param pluginId The pluginId to be deleted (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePluginAsync(Long pluginId, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = deletePluginValidateBeforeCall(pluginId, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for deletePlugins
     * @param requestBody List of pluginIds to be deleted (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePluginsCall(List<Long> requestBody, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = requestBody;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/batch";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call deletePluginsValidateBeforeCall(List<Long> requestBody, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'requestBody' is set
        if (requestBody == null) {
            throw new ApiException("Missing the required parameter 'requestBody' when calling deletePlugins(Async)");
        }

        return deletePluginsCall(requestBody, _callback);

    }

    /**
     * Batch Delete Plugins
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * @param requestBody List of pluginIds to be deleted (required)
     * @return List&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<Long> deletePlugins(List<Long> requestBody) throws ApiException {
        ApiResponse<List<Long>> localVarResp = deletePluginsWithHttpInfo(requestBody);
        return localVarResp.getData();
    }

    /**
     * Batch Delete Plugins
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * @param requestBody List of pluginIds to be deleted (required)
     * @return ApiResponse&lt;List&lt;Long&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<Long>> deletePluginsWithHttpInfo(List<Long> requestBody) throws ApiException {
        okhttp3.Call localVarCall = deletePluginsValidateBeforeCall(requestBody, null);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Batch Delete Plugins (asynchronously)
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * @param requestBody List of pluginIds to be deleted (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePluginsAsync(List<Long> requestBody, final ApiCallback<List<Long>> _callback) throws ApiException {

        okhttp3.Call localVarCall = deletePluginsValidateBeforeCall(requestBody, _callback);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getPluginDetails
     * @param pluginId PluginId to be obtained (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPluginDetailsCall(Long pluginId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/plugin/details/{pluginId}"
            .replace("{" + "pluginId" + "}", localVarApiClient.escapeString(pluginId.toString()));

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
    private okhttp3.Call getPluginDetailsValidateBeforeCall(Long pluginId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginId' is set
        if (pluginId == null) {
            throw new ApiException("Missing the required parameter 'pluginId' when calling getPluginDetails(Async)");
        }

        return getPluginDetailsCall(pluginId, _callback);

    }

    /**
     * Get Plugin Details
     * Get plugin detailed information.
     * @param pluginId PluginId to be obtained (required)
     * @return PluginDetailsDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public PluginDetailsDTO getPluginDetails(Long pluginId) throws ApiException {
        ApiResponse<PluginDetailsDTO> localVarResp = getPluginDetailsWithHttpInfo(pluginId);
        return localVarResp.getData();
    }

    /**
     * Get Plugin Details
     * Get plugin detailed information.
     * @param pluginId PluginId to be obtained (required)
     * @return ApiResponse&lt;PluginDetailsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<PluginDetailsDTO> getPluginDetailsWithHttpInfo(Long pluginId) throws ApiException {
        okhttp3.Call localVarCall = getPluginDetailsValidateBeforeCall(pluginId, null);
        Type localVarReturnType = new TypeToken<PluginDetailsDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Plugin Details (asynchronously)
     * Get plugin detailed information.
     * @param pluginId PluginId to be obtained (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPluginDetailsAsync(Long pluginId, final ApiCallback<PluginDetailsDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getPluginDetailsValidateBeforeCall(pluginId, _callback);
        Type localVarReturnType = new TypeToken<PluginDetailsDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getPluginSummary
     * @param pluginId PluginId to be obtained (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPluginSummaryCall(Long pluginId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/plugin/summary/{pluginId}"
            .replace("{" + "pluginId" + "}", localVarApiClient.escapeString(pluginId.toString()));

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
    private okhttp3.Call getPluginSummaryValidateBeforeCall(Long pluginId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginId' is set
        if (pluginId == null) {
            throw new ApiException("Missing the required parameter 'pluginId' when calling getPluginSummary(Async)");
        }

        return getPluginSummaryCall(pluginId, _callback);

    }

    /**
     * Get Plugin Summary
     * Get plugin summary information.
     * @param pluginId PluginId to be obtained (required)
     * @return PluginSummaryDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public PluginSummaryDTO getPluginSummary(Long pluginId) throws ApiException {
        ApiResponse<PluginSummaryDTO> localVarResp = getPluginSummaryWithHttpInfo(pluginId);
        return localVarResp.getData();
    }

    /**
     * Get Plugin Summary
     * Get plugin summary information.
     * @param pluginId PluginId to be obtained (required)
     * @return ApiResponse&lt;PluginSummaryDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<PluginSummaryDTO> getPluginSummaryWithHttpInfo(Long pluginId) throws ApiException {
        okhttp3.Call localVarCall = getPluginSummaryValidateBeforeCall(pluginId, null);
        Type localVarReturnType = new TypeToken<PluginSummaryDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Plugin Summary (asynchronously)
     * Get plugin summary information.
     * @param pluginId PluginId to be obtained (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPluginSummaryAsync(Long pluginId, final ApiCallback<PluginSummaryDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getPluginSummaryValidateBeforeCall(pluginId, _callback);
        Type localVarReturnType = new TypeToken<PluginSummaryDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for refreshPluginInfo
     * @param pluginId The pluginId to be fetched (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call refreshPluginInfoCall(Long pluginId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/plugin/refresh/{pluginId}"
            .replace("{" + "pluginId" + "}", localVarApiClient.escapeString(pluginId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
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
        return localVarApiClient.buildCall(basePath, localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call refreshPluginInfoValidateBeforeCall(Long pluginId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginId' is set
        if (pluginId == null) {
            throw new ApiException("Missing the required parameter 'pluginId' when calling refreshPluginInfo(Async)");
        }

        return refreshPluginInfoCall(pluginId, _callback);

    }

    /**
     * Refresh Plugin Information
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * @param pluginId The pluginId to be fetched (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public void refreshPluginInfo(Long pluginId) throws ApiException {
        refreshPluginInfoWithHttpInfo(pluginId);
    }

    /**
     * Refresh Plugin Information
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * @param pluginId The pluginId to be fetched (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Void> refreshPluginInfoWithHttpInfo(Long pluginId) throws ApiException {
        okhttp3.Call localVarCall = refreshPluginInfoValidateBeforeCall(pluginId, null);
        return localVarApiClient.execute(localVarCall);
    }

    /**
     * Refresh Plugin Information (asynchronously)
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * @param pluginId The pluginId to be fetched (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call refreshPluginInfoAsync(Long pluginId, final ApiCallback<Void> _callback) throws ApiException {

        okhttp3.Call localVarCall = refreshPluginInfoValidateBeforeCall(pluginId, _callback);
        localVarApiClient.executeAsync(localVarCall, _callback);
        return localVarCall;
    }
    /**
     * Build call for searchPluginDetails
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call searchPluginDetailsCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginQueryDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/details/search";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call searchPluginDetailsValidateBeforeCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginQueryDTO' is set
        if (pluginQueryDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginQueryDTO' when calling searchPluginDetails(Async)");
        }

        return searchPluginDetailsCall(pluginQueryDTO, _callback);

    }

    /**
     * Search Plugin Details
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * @param pluginQueryDTO Query conditions (required)
     * @return List&lt;PluginDetailsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PluginDetailsDTO> searchPluginDetails(PluginQueryDTO pluginQueryDTO) throws ApiException {
        ApiResponse<List<PluginDetailsDTO>> localVarResp = searchPluginDetailsWithHttpInfo(pluginQueryDTO);
        return localVarResp.getData();
    }

    /**
     * Search Plugin Details
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * @param pluginQueryDTO Query conditions (required)
     * @return ApiResponse&lt;List&lt;PluginDetailsDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PluginDetailsDTO>> searchPluginDetailsWithHttpInfo(PluginQueryDTO pluginQueryDTO) throws ApiException {
        okhttp3.Call localVarCall = searchPluginDetailsValidateBeforeCall(pluginQueryDTO, null);
        Type localVarReturnType = new TypeToken<List<PluginDetailsDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Search Plugin Details (asynchronously)
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call searchPluginDetailsAsync(PluginQueryDTO pluginQueryDTO, final ApiCallback<List<PluginDetailsDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = searchPluginDetailsValidateBeforeCall(pluginQueryDTO, _callback);
        Type localVarReturnType = new TypeToken<List<PluginDetailsDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for searchPluginSummary
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call searchPluginSummaryCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginQueryDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/search";

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call searchPluginSummaryValidateBeforeCall(PluginQueryDTO pluginQueryDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginQueryDTO' is set
        if (pluginQueryDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginQueryDTO' when calling searchPluginSummary(Async)");
        }

        return searchPluginSummaryCall(pluginQueryDTO, _callback);

    }

    /**
     * Search Plugin Summary
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * @param pluginQueryDTO Query conditions (required)
     * @return List&lt;PluginSummaryDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<PluginSummaryDTO> searchPluginSummary(PluginQueryDTO pluginQueryDTO) throws ApiException {
        ApiResponse<List<PluginSummaryDTO>> localVarResp = searchPluginSummaryWithHttpInfo(pluginQueryDTO);
        return localVarResp.getData();
    }

    /**
     * Search Plugin Summary
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * @param pluginQueryDTO Query conditions (required)
     * @return ApiResponse&lt;List&lt;PluginSummaryDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<PluginSummaryDTO>> searchPluginSummaryWithHttpInfo(PluginQueryDTO pluginQueryDTO) throws ApiException {
        okhttp3.Call localVarCall = searchPluginSummaryValidateBeforeCall(pluginQueryDTO, null);
        Type localVarReturnType = new TypeToken<List<PluginSummaryDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Search Plugin Summary (asynchronously)
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * @param pluginQueryDTO Query conditions (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call searchPluginSummaryAsync(PluginQueryDTO pluginQueryDTO, final ApiCallback<List<PluginSummaryDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = searchPluginSummaryValidateBeforeCall(pluginQueryDTO, _callback);
        Type localVarReturnType = new TypeToken<List<PluginSummaryDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for updatePlugin
     * @param pluginId The pluginId to be updated (required)
     * @param pluginUpdateDTO The plugin information to be updated (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updatePluginCall(Long pluginId, PluginUpdateDTO pluginUpdateDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = pluginUpdateDTO;

        // create path and map variables
        String localVarPath = "/api/v1/plugin/{pluginId}"
            .replace("{" + "pluginId" + "}", localVarApiClient.escapeString(pluginId.toString()));

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
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call updatePluginValidateBeforeCall(Long pluginId, PluginUpdateDTO pluginUpdateDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'pluginId' is set
        if (pluginId == null) {
            throw new ApiException("Missing the required parameter 'pluginId' when calling updatePlugin(Async)");
        }

        // verify the required parameter 'pluginUpdateDTO' is set
        if (pluginUpdateDTO == null) {
            throw new ApiException("Missing the required parameter 'pluginUpdateDTO' when calling updatePlugin(Async)");
        }

        return updatePluginCall(pluginId, pluginUpdateDTO, _callback);

    }

    /**
     * Update Plugin
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * @param pluginId The pluginId to be updated (required)
     * @param pluginUpdateDTO The plugin information to be updated (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean updatePlugin(Long pluginId, PluginUpdateDTO pluginUpdateDTO) throws ApiException {
        ApiResponse<Boolean> localVarResp = updatePluginWithHttpInfo(pluginId, pluginUpdateDTO);
        return localVarResp.getData();
    }

    /**
     * Update Plugin
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * @param pluginId The pluginId to be updated (required)
     * @param pluginUpdateDTO The plugin information to be updated (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> updatePluginWithHttpInfo(Long pluginId, PluginUpdateDTO pluginUpdateDTO) throws ApiException {
        okhttp3.Call localVarCall = updatePluginValidateBeforeCall(pluginId, pluginUpdateDTO, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Update Plugin (asynchronously)
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * @param pluginId The pluginId to be updated (required)
     * @param pluginUpdateDTO The plugin information to be updated (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updatePluginAsync(Long pluginId, PluginUpdateDTO pluginUpdateDTO, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = updatePluginValidateBeforeCall(pluginId, pluginUpdateDTO, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
