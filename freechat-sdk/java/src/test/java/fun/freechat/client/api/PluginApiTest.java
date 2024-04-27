/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory** for characters. - Supports setting **quota limits** for characters.  ## System Snapshots  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up a unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, execute `install-in.sh` to deploy `ingress-nginx` to the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Execute the `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Execute `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml bitnami:   mysql:     enabled: false   redis:     enabled: false   milvus:     enabled: false  mysql:   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   url: <your redis url>   auth:     password: <your redis password>   milvus:   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### 使用 SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [DashScope](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI and DashScope ([HuggingFace](https://huggingface.co/) is also expected to be supported soon). However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future.
 *
 * The version of the OpenAPI document: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.PluginCreateDTO;
import fun.freechat.client.model.PluginDetailsDTO;
import fun.freechat.client.model.PluginQueryDTO;
import fun.freechat.client.model.PluginSummaryDTO;
import fun.freechat.client.model.PluginUpdateDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PluginApi
 */
@Disabled
public class PluginApiTest {

    private final PluginApi api = new PluginApi();

    /**
     * Batch Search Plugin Details
     *
     * Batch call shortcut for /api/v1/plugin/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPluginDetailsTest() throws ApiException {
        List<PluginQueryDTO> pluginQueryDTO = null;
        List<List<PluginDetailsDTO>> response = api.batchSearchPluginDetails(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Plugin Summaries
     *
     * Batch call shortcut for /api/v1/plugin/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPluginSummaryTest() throws ApiException {
        List<PluginQueryDTO> pluginQueryDTO = null;
        List<List<PluginSummaryDTO>> response = api.batchSearchPluginSummary(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Calculate Number of Plugins
     *
     * Calculate the number of plugins according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPluginsTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        Long response = api.countPlugins(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Plugin
     *
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPluginTest() throws ApiException {
        PluginCreateDTO pluginCreateDTO = null;
        Long response = api.createPlugin(pluginCreateDTO);
        // TODO: test validations
    }

    /**
     * Batch Create Plugins
     *
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPluginsTest() throws ApiException {
        List<PluginCreateDTO> pluginCreateDTO = null;
        List<Long> response = api.createPlugins(pluginCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Plugin
     *
     * Delete plugin. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePluginTest() throws ApiException {
        Long pluginId = null;
        Boolean response = api.deletePlugin(pluginId);
        // TODO: test validations
    }

    /**
     * Batch Delete Plugins
     *
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePluginsTest() throws ApiException {
        List<Long> requestBody = null;
        List<Long> response = api.deletePlugins(requestBody);
        // TODO: test validations
    }

    /**
     * Get Plugin Details
     *
     * Get plugin detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPluginDetailsTest() throws ApiException {
        Long pluginId = null;
        PluginDetailsDTO response = api.getPluginDetails(pluginId);
        // TODO: test validations
    }

    /**
     * Get Plugin Summary
     *
     * Get plugin summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPluginSummaryTest() throws ApiException {
        Long pluginId = null;
        PluginSummaryDTO response = api.getPluginSummary(pluginId);
        // TODO: test validations
    }

    /**
     * Refresh Plugin Information
     *
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void refreshPluginInfoTest() throws ApiException {
        Long pluginId = null;
        api.refreshPluginInfo(pluginId);
        // TODO: test validations
    }

    /**
     * Search Plugin Details
     *
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPluginDetailsTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        List<PluginDetailsDTO> response = api.searchPluginDetails(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Plugin Summary
     *
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPluginSummaryTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        List<PluginSummaryDTO> response = api.searchPluginSummary(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Update Plugin
     *
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updatePluginTest() throws ApiException {
        Long pluginId = null;
        PluginUpdateDTO pluginUpdateDTO = null;
        Boolean response = api.updatePlugin(pluginId, pluginUpdateDTO);
        // TODO: test validations
    }

}
