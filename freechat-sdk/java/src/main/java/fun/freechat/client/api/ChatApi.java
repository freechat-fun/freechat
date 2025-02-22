/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports characters replies with **mixed text and image information**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 2.3.0
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


import fun.freechat.client.model.ChatCreateDTO;
import fun.freechat.client.model.ChatMessageDTO;
import fun.freechat.client.model.ChatMessageRecordDTO;
import fun.freechat.client.model.ChatSessionDTO;
import fun.freechat.client.model.ChatUpdateDTO;
import fun.freechat.client.model.LlmResultDTO;
import fun.freechat.client.model.MemoryUsageDTO;
import fun.freechat.client.model.SseEmitter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public ChatApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ChatApi(ApiClient apiClient) {
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
     * Build call for clearMemory
     * @param chatId Chat session identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call clearMemoryCall(String chatId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/memory/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call clearMemoryValidateBeforeCall(String chatId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling clearMemory(Async)");
        }

        return clearMemoryCall(chatId, _callback);

    }

    /**
     * Clear Memory
     * Clear memory of the chat session.
     * @param chatId Chat session identifier (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> clearMemory(String chatId) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = clearMemoryWithHttpInfo(chatId);
        return localVarResp.getData();
    }

    /**
     * Clear Memory
     * Clear memory of the chat session.
     * @param chatId Chat session identifier (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> clearMemoryWithHttpInfo(String chatId) throws ApiException {
        okhttp3.Call localVarCall = clearMemoryValidateBeforeCall(chatId, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Clear Memory (asynchronously)
     * Clear memory of the chat session.
     * @param chatId Chat session identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call clearMemoryAsync(String chatId, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = clearMemoryValidateBeforeCall(chatId, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for deleteChat
     * @param chatId Chat session identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deleteChatCall(String chatId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call deleteChatValidateBeforeCall(String chatId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling deleteChat(Async)");
        }

        return deleteChatCall(chatId, _callback);

    }

    /**
     * Delete Chat Session
     * Delete the chat session.
     * @param chatId Chat session identifier (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean deleteChat(String chatId) throws ApiException {
        ApiResponse<Boolean> localVarResp = deleteChatWithHttpInfo(chatId);
        return localVarResp.getData();
    }

    /**
     * Delete Chat Session
     * Delete the chat session.
     * @param chatId Chat session identifier (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> deleteChatWithHttpInfo(String chatId) throws ApiException {
        okhttp3.Call localVarCall = deleteChatValidateBeforeCall(chatId, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Delete Chat Session (asynchronously)
     * Delete the chat session.
     * @param chatId Chat session identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deleteChatAsync(String chatId, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = deleteChatValidateBeforeCall(chatId, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getDefaultChatId
     * @param characterUid Character uid (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getDefaultChatIdCall(String characterUid, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/{characterUid}"
            .replace("{" + "characterUid" + "}", localVarApiClient.escapeString(characterUid.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "text/plain"
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
    private okhttp3.Call getDefaultChatIdValidateBeforeCall(String characterUid, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'characterUid' is set
        if (characterUid == null) {
            throw new ApiException("Missing the required parameter 'characterUid' when calling getDefaultChatId(Async)");
        }

        return getDefaultChatIdCall(characterUid, _callback);

    }

    /**
     * Get Default Chat
     * Get default chat id of current user and the character.
     * @param characterUid Character uid (required)
     * @return String
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public String getDefaultChatId(String characterUid) throws ApiException {
        ApiResponse<String> localVarResp = getDefaultChatIdWithHttpInfo(characterUid);
        return localVarResp.getData();
    }

    /**
     * Get Default Chat
     * Get default chat id of current user and the character.
     * @param characterUid Character uid (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<String> getDefaultChatIdWithHttpInfo(String characterUid) throws ApiException {
        okhttp3.Call localVarCall = getDefaultChatIdValidateBeforeCall(characterUid, null);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Default Chat (asynchronously)
     * Get default chat id of current user and the character.
     * @param characterUid Character uid (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getDefaultChatIdAsync(String characterUid, final ApiCallback<String> _callback) throws ApiException {

        okhttp3.Call localVarCall = getDefaultChatIdValidateBeforeCall(characterUid, _callback);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getMemoryUsage
     * @param chatId Chat session identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getMemoryUsageCall(String chatId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/memory/usage/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call getMemoryUsageValidateBeforeCall(String chatId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling getMemoryUsage(Async)");
        }

        return getMemoryUsageCall(chatId, _callback);

    }

    /**
     * Get Memory Usage
     * Get memory usage of a chat.
     * @param chatId Chat session identifier (required)
     * @return MemoryUsageDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public MemoryUsageDTO getMemoryUsage(String chatId) throws ApiException {
        ApiResponse<MemoryUsageDTO> localVarResp = getMemoryUsageWithHttpInfo(chatId);
        return localVarResp.getData();
    }

    /**
     * Get Memory Usage
     * Get memory usage of a chat.
     * @param chatId Chat session identifier (required)
     * @return ApiResponse&lt;MemoryUsageDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<MemoryUsageDTO> getMemoryUsageWithHttpInfo(String chatId) throws ApiException {
        okhttp3.Call localVarCall = getMemoryUsageValidateBeforeCall(chatId, null);
        Type localVarReturnType = new TypeToken<MemoryUsageDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Memory Usage (asynchronously)
     * Get memory usage of a chat.
     * @param chatId Chat session identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getMemoryUsageAsync(String chatId, final ApiCallback<MemoryUsageDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getMemoryUsageValidateBeforeCall(chatId, _callback);
        Type localVarReturnType = new TypeToken<MemoryUsageDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listChats
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listChatsCall(final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat";

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
    private okhttp3.Call listChatsValidateBeforeCall(final ApiCallback _callback) throws ApiException {
        return listChatsCall(_callback);

    }

    /**
     * List Chats
     * List chats of current user.
     * @return List&lt;ChatSessionDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatSessionDTO> listChats() throws ApiException {
        ApiResponse<List<ChatSessionDTO>> localVarResp = listChatsWithHttpInfo();
        return localVarResp.getData();
    }

    /**
     * List Chats
     * List chats of current user.
     * @return ApiResponse&lt;List&lt;ChatSessionDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatSessionDTO>> listChatsWithHttpInfo() throws ApiException {
        okhttp3.Call localVarCall = listChatsValidateBeforeCall(null);
        Type localVarReturnType = new TypeToken<List<ChatSessionDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chats (asynchronously)
     * List chats of current user.
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listChatsAsync(final ApiCallback<List<ChatSessionDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listChatsValidateBeforeCall(_callback);
        Type localVarReturnType = new TypeToken<List<ChatSessionDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listDebugMessages
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessagesCall(String chatId, Integer limit, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/debug/{chatId}/{limit}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "limit" + "}", localVarApiClient.escapeString(limit.toString()));

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
    private okhttp3.Call listDebugMessagesValidateBeforeCall(String chatId, Integer limit, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listDebugMessages(Async)");
        }

        // verify the required parameter 'limit' is set
        if (limit == null) {
            throw new ApiException("Missing the required parameter 'limit' when calling listDebugMessages(Async)");
        }

        return listDebugMessagesCall(chatId, limit, _callback);

    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listDebugMessages(String chatId, Integer limit) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listDebugMessagesWithHttpInfo(chatId, limit);
        return localVarResp.getData();
    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listDebugMessagesWithHttpInfo(String chatId, Integer limit) throws ApiException {
        okhttp3.Call localVarCall = listDebugMessagesValidateBeforeCall(chatId, limit, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Debug Messages (asynchronously)
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessagesAsync(String chatId, Integer limit, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listDebugMessagesValidateBeforeCall(chatId, limit, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listDebugMessages1
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessages1Call(String chatId, Integer limit, Integer offset, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/debug/{chatId}/{limit}/{offset}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "limit" + "}", localVarApiClient.escapeString(limit.toString()))
            .replace("{" + "offset" + "}", localVarApiClient.escapeString(offset.toString()));

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
    private okhttp3.Call listDebugMessages1ValidateBeforeCall(String chatId, Integer limit, Integer offset, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listDebugMessages1(Async)");
        }

        // verify the required parameter 'limit' is set
        if (limit == null) {
            throw new ApiException("Missing the required parameter 'limit' when calling listDebugMessages1(Async)");
        }

        // verify the required parameter 'offset' is set
        if (offset == null) {
            throw new ApiException("Missing the required parameter 'offset' when calling listDebugMessages1(Async)");
        }

        return listDebugMessages1Call(chatId, limit, offset, _callback);

    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listDebugMessages1(String chatId, Integer limit, Integer offset) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listDebugMessages1WithHttpInfo(chatId, limit, offset);
        return localVarResp.getData();
    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listDebugMessages1WithHttpInfo(String chatId, Integer limit, Integer offset) throws ApiException {
        okhttp3.Call localVarCall = listDebugMessages1ValidateBeforeCall(chatId, limit, offset, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Debug Messages (asynchronously)
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessages1Async(String chatId, Integer limit, Integer offset, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listDebugMessages1ValidateBeforeCall(chatId, limit, offset, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listDebugMessages2
     * @param chatId Chat session identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessages2Call(String chatId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/debug/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call listDebugMessages2ValidateBeforeCall(String chatId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listDebugMessages2(Async)");
        }

        return listDebugMessages2Call(chatId, _callback);

    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listDebugMessages2(String chatId) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listDebugMessages2WithHttpInfo(chatId);
        return localVarResp.getData();
    }

    /**
     * List Chat Debug Messages
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listDebugMessages2WithHttpInfo(String chatId) throws ApiException {
        okhttp3.Call localVarCall = listDebugMessages2ValidateBeforeCall(chatId, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Debug Messages (asynchronously)
     * List debug messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listDebugMessages2Async(String chatId, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listDebugMessages2ValidateBeforeCall(chatId, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listMessages
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessagesCall(String chatId, Integer limit, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/{chatId}/{limit}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "limit" + "}", localVarApiClient.escapeString(limit.toString()));

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
    private okhttp3.Call listMessagesValidateBeforeCall(String chatId, Integer limit, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listMessages(Async)");
        }

        // verify the required parameter 'limit' is set
        if (limit == null) {
            throw new ApiException("Missing the required parameter 'limit' when calling listMessages(Async)");
        }

        return listMessagesCall(chatId, limit, _callback);

    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listMessages(String chatId, Integer limit) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listMessagesWithHttpInfo(chatId, limit);
        return localVarResp.getData();
    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listMessagesWithHttpInfo(String chatId, Integer limit) throws ApiException {
        okhttp3.Call localVarCall = listMessagesValidateBeforeCall(chatId, limit, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Messages (asynchronously)
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessagesAsync(String chatId, Integer limit, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listMessagesValidateBeforeCall(chatId, limit, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listMessages1
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessages1Call(String chatId, Integer limit, Integer offset, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/{chatId}/{limit}/{offset}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "limit" + "}", localVarApiClient.escapeString(limit.toString()))
            .replace("{" + "offset" + "}", localVarApiClient.escapeString(offset.toString()));

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
    private okhttp3.Call listMessages1ValidateBeforeCall(String chatId, Integer limit, Integer offset, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listMessages1(Async)");
        }

        // verify the required parameter 'limit' is set
        if (limit == null) {
            throw new ApiException("Missing the required parameter 'limit' when calling listMessages1(Async)");
        }

        // verify the required parameter 'offset' is set
        if (offset == null) {
            throw new ApiException("Missing the required parameter 'offset' when calling listMessages1(Async)");
        }

        return listMessages1Call(chatId, limit, offset, _callback);

    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listMessages1(String chatId, Integer limit, Integer offset) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listMessages1WithHttpInfo(chatId, limit, offset);
        return localVarResp.getData();
    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listMessages1WithHttpInfo(String chatId, Integer limit, Integer offset) throws ApiException {
        okhttp3.Call localVarCall = listMessages1ValidateBeforeCall(chatId, limit, offset, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Messages (asynchronously)
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param limit Messages limit (required)
     * @param offset Messages offset (from new to old) (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessages1Async(String chatId, Integer limit, Integer offset, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listMessages1ValidateBeforeCall(chatId, limit, offset, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for listMessages2
     * @param chatId Chat session identifier (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessages2Call(String chatId, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call listMessages2ValidateBeforeCall(String chatId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling listMessages2(Async)");
        }

        return listMessages2Call(chatId, _callback);

    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @return List&lt;ChatMessageRecordDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<ChatMessageRecordDTO> listMessages2(String chatId) throws ApiException {
        ApiResponse<List<ChatMessageRecordDTO>> localVarResp = listMessages2WithHttpInfo(chatId);
        return localVarResp.getData();
    }

    /**
     * List Chat Messages
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @return ApiResponse&lt;List&lt;ChatMessageRecordDTO&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<ChatMessageRecordDTO>> listMessages2WithHttpInfo(String chatId) throws ApiException {
        okhttp3.Call localVarCall = listMessages2ValidateBeforeCall(chatId, null);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * List Chat Messages (asynchronously)
     * List messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call listMessages2Async(String chatId, final ApiCallback<List<ChatMessageRecordDTO>> _callback) throws ApiException {

        okhttp3.Call localVarCall = listMessages2ValidateBeforeCall(chatId, _callback);
        Type localVarReturnType = new TypeToken<List<ChatMessageRecordDTO>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for rollbackMessages
     * @param chatId Chat session identifier (required)
     * @param count Message count to be rolled back (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call rollbackMessagesCall(String chatId, Integer count, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/messages/rollback/{chatId}/{count}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "count" + "}", localVarApiClient.escapeString(count.toString()));

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
    private okhttp3.Call rollbackMessagesValidateBeforeCall(String chatId, Integer count, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling rollbackMessages(Async)");
        }

        // verify the required parameter 'count' is set
        if (count == null) {
            throw new ApiException("Missing the required parameter 'count' when calling rollbackMessages(Async)");
        }

        return rollbackMessagesCall(chatId, count, _callback);

    }

    /**
     * Rollback Chat Messages
     * Rollback messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param count Message count to be rolled back (required)
     * @return List&lt;Long&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public List<Long> rollbackMessages(String chatId, Integer count) throws ApiException {
        ApiResponse<List<Long>> localVarResp = rollbackMessagesWithHttpInfo(chatId, count);
        return localVarResp.getData();
    }

    /**
     * Rollback Chat Messages
     * Rollback messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param count Message count to be rolled back (required)
     * @return ApiResponse&lt;List&lt;Long&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<List<Long>> rollbackMessagesWithHttpInfo(String chatId, Integer count) throws ApiException {
        okhttp3.Call localVarCall = rollbackMessagesValidateBeforeCall(chatId, count, null);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Rollback Chat Messages (asynchronously)
     * Rollback messages of a chat.
     * @param chatId Chat session identifier (required)
     * @param count Message count to be rolled back (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call rollbackMessagesAsync(String chatId, Integer count, final ApiCallback<List<Long>> _callback) throws ApiException {

        okhttp3.Call localVarCall = rollbackMessagesValidateBeforeCall(chatId, count, _callback);
        Type localVarReturnType = new TypeToken<List<Long>>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for sendAssistant
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call sendAssistantCall(String chatId, String assistantUid, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/send/assistant/{chatId}/{assistantUid}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "assistantUid" + "}", localVarApiClient.escapeString(assistantUid.toString()));

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
    private okhttp3.Call sendAssistantValidateBeforeCall(String chatId, String assistantUid, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling sendAssistant(Async)");
        }

        // verify the required parameter 'assistantUid' is set
        if (assistantUid == null) {
            throw new ApiException("Missing the required parameter 'assistantUid' when calling sendAssistant(Async)");
        }

        return sendAssistantCall(chatId, assistantUid, _callback);

    }

    /**
     * Send Assistant for Chat Message
     * Send a message to assistant for a new chat message.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @return LlmResultDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public LlmResultDTO sendAssistant(String chatId, String assistantUid) throws ApiException {
        ApiResponse<LlmResultDTO> localVarResp = sendAssistantWithHttpInfo(chatId, assistantUid);
        return localVarResp.getData();
    }

    /**
     * Send Assistant for Chat Message
     * Send a message to assistant for a new chat message.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @return ApiResponse&lt;LlmResultDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<LlmResultDTO> sendAssistantWithHttpInfo(String chatId, String assistantUid) throws ApiException {
        okhttp3.Call localVarCall = sendAssistantValidateBeforeCall(chatId, assistantUid, null);
        Type localVarReturnType = new TypeToken<LlmResultDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Send Assistant for Chat Message (asynchronously)
     * Send a message to assistant for a new chat message.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call sendAssistantAsync(String chatId, String assistantUid, final ApiCallback<LlmResultDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = sendAssistantValidateBeforeCall(chatId, assistantUid, _callback);
        Type localVarReturnType = new TypeToken<LlmResultDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for sendMessage
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call sendMessageCall(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = chatMessageDTO;

        // create path and map variables
        String localVarPath = "/api/v2/chat/send/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call sendMessageValidateBeforeCall(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling sendMessage(Async)");
        }

        // verify the required parameter 'chatMessageDTO' is set
        if (chatMessageDTO == null) {
            throw new ApiException("Missing the required parameter 'chatMessageDTO' when calling sendMessage(Async)");
        }

        return sendMessageCall(chatId, chatMessageDTO, _callback);

    }

    /**
     * Send Chat Message
     * Send a chat message to character.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @return LlmResultDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public LlmResultDTO sendMessage(String chatId, ChatMessageDTO chatMessageDTO) throws ApiException {
        ApiResponse<LlmResultDTO> localVarResp = sendMessageWithHttpInfo(chatId, chatMessageDTO);
        return localVarResp.getData();
    }

    /**
     * Send Chat Message
     * Send a chat message to character.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @return ApiResponse&lt;LlmResultDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<LlmResultDTO> sendMessageWithHttpInfo(String chatId, ChatMessageDTO chatMessageDTO) throws ApiException {
        okhttp3.Call localVarCall = sendMessageValidateBeforeCall(chatId, chatMessageDTO, null);
        Type localVarReturnType = new TypeToken<LlmResultDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Send Chat Message (asynchronously)
     * Send a chat message to character.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call sendMessageAsync(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback<LlmResultDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = sendMessageValidateBeforeCall(chatId, chatMessageDTO, _callback);
        Type localVarReturnType = new TypeToken<LlmResultDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for startChat
     * @param chatCreateDTO Parameters for starting a chat session (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call startChatCall(ChatCreateDTO chatCreateDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = chatCreateDTO;

        // create path and map variables
        String localVarPath = "/api/v2/chat";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "text/plain"
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
    private okhttp3.Call startChatValidateBeforeCall(ChatCreateDTO chatCreateDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatCreateDTO' is set
        if (chatCreateDTO == null) {
            throw new ApiException("Missing the required parameter 'chatCreateDTO' when calling startChat(Async)");
        }

        return startChatCall(chatCreateDTO, _callback);

    }

    /**
     * Start Chat Session
     * Start a chat session.
     * @param chatCreateDTO Parameters for starting a chat session (required)
     * @return String
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public String startChat(ChatCreateDTO chatCreateDTO) throws ApiException {
        ApiResponse<String> localVarResp = startChatWithHttpInfo(chatCreateDTO);
        return localVarResp.getData();
    }

    /**
     * Start Chat Session
     * Start a chat session.
     * @param chatCreateDTO Parameters for starting a chat session (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<String> startChatWithHttpInfo(ChatCreateDTO chatCreateDTO) throws ApiException {
        okhttp3.Call localVarCall = startChatValidateBeforeCall(chatCreateDTO, null);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Start Chat Session (asynchronously)
     * Start a chat session.
     * @param chatCreateDTO Parameters for starting a chat session (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call startChatAsync(ChatCreateDTO chatCreateDTO, final ApiCallback<String> _callback) throws ApiException {

        okhttp3.Call localVarCall = startChatValidateBeforeCall(chatCreateDTO, _callback);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for streamSendAssistant
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call streamSendAssistantCall(String chatId, String assistantUid, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v2/chat/send/stream/assistant/{chatId}/{assistantUid}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()))
            .replace("{" + "assistantUid" + "}", localVarApiClient.escapeString(assistantUid.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "text/event-stream"
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
    private okhttp3.Call streamSendAssistantValidateBeforeCall(String chatId, String assistantUid, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling streamSendAssistant(Async)");
        }

        // verify the required parameter 'assistantUid' is set
        if (assistantUid == null) {
            throw new ApiException("Missing the required parameter 'assistantUid' when calling streamSendAssistant(Async)");
        }

        return streamSendAssistantCall(chatId, assistantUid, _callback);

    }

    /**
     * Send Assistant for Chat Message by Streaming Back
     * Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @return SseEmitter
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public SseEmitter streamSendAssistant(String chatId, String assistantUid) throws ApiException {
        ApiResponse<SseEmitter> localVarResp = streamSendAssistantWithHttpInfo(chatId, assistantUid);
        return localVarResp.getData();
    }

    /**
     * Send Assistant for Chat Message by Streaming Back
     * Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @return ApiResponse&lt;SseEmitter&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<SseEmitter> streamSendAssistantWithHttpInfo(String chatId, String assistantUid) throws ApiException {
        okhttp3.Call localVarCall = streamSendAssistantValidateBeforeCall(chatId, assistantUid, null);
        Type localVarReturnType = new TypeToken<SseEmitter>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Send Assistant for Chat Message by Streaming Back (asynchronously)
     * Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param assistantUid Assistant uid (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call streamSendAssistantAsync(String chatId, String assistantUid, final ApiCallback<SseEmitter> _callback) throws ApiException {

        okhttp3.Call localVarCall = streamSendAssistantValidateBeforeCall(chatId, assistantUid, _callback);
        Type localVarReturnType = new TypeToken<SseEmitter>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for streamSendMessage
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call streamSendMessageCall(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = chatMessageDTO;

        // create path and map variables
        String localVarPath = "/api/v2/chat/send/stream/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "text/event-stream"
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
    private okhttp3.Call streamSendMessageValidateBeforeCall(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling streamSendMessage(Async)");
        }

        // verify the required parameter 'chatMessageDTO' is set
        if (chatMessageDTO == null) {
            throw new ApiException("Missing the required parameter 'chatMessageDTO' when calling streamSendMessage(Async)");
        }

        return streamSendMessageCall(chatId, chatMessageDTO, _callback);

    }

    /**
     * Send Chat Message by Streaming Back
     * Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @return SseEmitter
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public SseEmitter streamSendMessage(String chatId, ChatMessageDTO chatMessageDTO) throws ApiException {
        ApiResponse<SseEmitter> localVarResp = streamSendMessageWithHttpInfo(chatId, chatMessageDTO);
        return localVarResp.getData();
    }

    /**
     * Send Chat Message by Streaming Back
     * Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @return ApiResponse&lt;SseEmitter&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<SseEmitter> streamSendMessageWithHttpInfo(String chatId, ChatMessageDTO chatMessageDTO) throws ApiException {
        okhttp3.Call localVarCall = streamSendMessageValidateBeforeCall(chatId, chatMessageDTO, null);
        Type localVarReturnType = new TypeToken<SseEmitter>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Send Chat Message by Streaming Back (asynchronously)
     * Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.
     * @param chatId Chat session identifier (required)
     * @param chatMessageDTO Chat message (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call streamSendMessageAsync(String chatId, ChatMessageDTO chatMessageDTO, final ApiCallback<SseEmitter> _callback) throws ApiException {

        okhttp3.Call localVarCall = streamSendMessageValidateBeforeCall(chatId, chatMessageDTO, _callback);
        Type localVarReturnType = new TypeToken<SseEmitter>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for updateChat
     * @param chatId Chat session identifier (required)
     * @param chatUpdateDTO The chat session information to be updated (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updateChatCall(String chatId, ChatUpdateDTO chatUpdateDTO, final ApiCallback _callback) throws ApiException {
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

        Object localVarPostBody = chatUpdateDTO;

        // create path and map variables
        String localVarPath = "/api/v2/chat/{chatId}"
            .replace("{" + "chatId" + "}", localVarApiClient.escapeString(chatId.toString()));

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
    private okhttp3.Call updateChatValidateBeforeCall(String chatId, ChatUpdateDTO chatUpdateDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'chatId' is set
        if (chatId == null) {
            throw new ApiException("Missing the required parameter 'chatId' when calling updateChat(Async)");
        }

        // verify the required parameter 'chatUpdateDTO' is set
        if (chatUpdateDTO == null) {
            throw new ApiException("Missing the required parameter 'chatUpdateDTO' when calling updateChat(Async)");
        }

        return updateChatCall(chatId, chatUpdateDTO, _callback);

    }

    /**
     * Update Chat Session
     * Update the chat session.
     * @param chatId Chat session identifier (required)
     * @param chatUpdateDTO The chat session information to be updated (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean updateChat(String chatId, ChatUpdateDTO chatUpdateDTO) throws ApiException {
        ApiResponse<Boolean> localVarResp = updateChatWithHttpInfo(chatId, chatUpdateDTO);
        return localVarResp.getData();
    }

    /**
     * Update Chat Session
     * Update the chat session.
     * @param chatId Chat session identifier (required)
     * @param chatUpdateDTO The chat session information to be updated (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> updateChatWithHttpInfo(String chatId, ChatUpdateDTO chatUpdateDTO) throws ApiException {
        okhttp3.Call localVarCall = updateChatValidateBeforeCall(chatId, chatUpdateDTO, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Update Chat Session (asynchronously)
     * Update the chat session.
     * @param chatId Chat session identifier (required)
     * @param chatUpdateDTO The chat session information to be updated (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table border="1">
       <caption>Response Details</caption>
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updateChatAsync(String chatId, ChatUpdateDTO chatUpdateDTO, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = updateChatValidateBeforeCall(chatId, chatUpdateDTO, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
