/*
 * FreeChat OpenAPI Definition
 * # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports setting **quota limits** for characters. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   deployment:     enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   deployment:     enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   deployment:     enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   deployment:     enabled: true grafana:   deployment:     enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI GPT and Alibaba Qwen series models. However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 
 *
 * The version of the OpenAPI document: 1.3.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.AgentCreateDTO;
import fun.freechat.client.model.AgentDetailsDTO;
import fun.freechat.client.model.AgentItemForNameDTO;
import fun.freechat.client.model.AgentQueryDTO;
import fun.freechat.client.model.AgentSummaryDTO;
import fun.freechat.client.model.AgentUpdateDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AgentApi
 */
@Disabled
public class AgentApiTest {

    private final AgentApi api = new AgentApi();

    /**
     * Batch Search Agent Details
     *
     * Batch call shortcut for /api/v1/agent/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchAgentDetailsTest() throws ApiException {
        List<AgentQueryDTO> agentQueryDTO = null;
        List<List<AgentDetailsDTO>> response = api.batchSearchAgentDetails(agentQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Agent Summaries
     *
     * Batch call shortcut for /api/v1/agent/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchAgentSummaryTest() throws ApiException {
        List<AgentQueryDTO> agentQueryDTO = null;
        List<List<AgentSummaryDTO>> response = api.batchSearchAgentSummary(agentQueryDTO);
        // TODO: test validations
    }

    /**
     * Clone Agent
     *
     * Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void cloneAgentTest() throws ApiException {
        Long agentId = null;
        Long response = api.cloneAgent(agentId);
        // TODO: test validations
    }

    /**
     * Batch Clone Agents
     *
     * Batch clone multiple agents. Ensure transactionality, return the agentId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void cloneAgentsTest() throws ApiException {
        List<Long> requestBody = null;
        List<Long> response = api.cloneAgents(requestBody);
        // TODO: test validations
    }

    /**
     * Calculate Number of Agents
     *
     * Calculate the number of agents according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countAgentsTest() throws ApiException {
        AgentQueryDTO agentQueryDTO = null;
        Long response = api.countAgents(agentQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Agent
     *
     * Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createAgentTest() throws ApiException {
        AgentCreateDTO agentCreateDTO = null;
        Long response = api.createAgent(agentCreateDTO);
        // TODO: test validations
    }

    /**
     * Batch Create Agents
     *
     * Batch create multiple agents. Ensure transactionality, return the agentId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createAgentsTest() throws ApiException {
        List<AgentCreateDTO> agentCreateDTO = null;
        List<Long> response = api.createAgents(agentCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Agent
     *
     * Delete agent. Return success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteAgentTest() throws ApiException {
        Long agentId = null;
        Boolean response = api.deleteAgent(agentId);
        // TODO: test validations
    }

    /**
     * Batch Delete Agents
     *
     * Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteAgentsTest() throws ApiException {
        List<Long> requestBody = null;
        List<Long> response = api.deleteAgents(requestBody);
        // TODO: test validations
    }

    /**
     * Get Agent Details
     *
     * Get agent detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAgentDetailsTest() throws ApiException {
        Long agentId = null;
        AgentDetailsDTO response = api.getAgentDetails(agentId);
        // TODO: test validations
    }

    /**
     * Get Agent Summary
     *
     * Get agent summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAgentSummaryTest() throws ApiException {
        Long agentId = null;
        AgentSummaryDTO response = api.getAgentSummary(agentId);
        // TODO: test validations
    }

    /**
     * List Versions by Agent Name
     *
     * List the versions and corresponding agentIds by agent name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAgentVersionsByNameTest() throws ApiException {
        String name = null;
        List<AgentItemForNameDTO> response = api.listAgentVersionsByName(name);
        // TODO: test validations
    }

    /**
     * Publish Agent
     *
     * Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishAgentTest() throws ApiException {
        Long agentId = null;
        String visibility = null;
        Long response = api.publishAgent(agentId, visibility);
        // TODO: test validations
    }

    /**
     * Search Agent Details
     *
     * Same as /api/v1/agent/search, but returns detailed information of the agent.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchAgentDetailsTest() throws ApiException {
        AgentQueryDTO agentQueryDTO = null;
        List<AgentDetailsDTO> response = api.searchAgentDetails(agentQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Agent Summary
     *
     * Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchAgentSummaryTest() throws ApiException {
        AgentQueryDTO agentQueryDTO = null;
        List<AgentSummaryDTO> response = api.searchAgentSummary(agentQueryDTO);
        // TODO: test validations
    }

    /**
     * Update Agent
     *
     * Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateAgentTest() throws ApiException {
        Long agentId = null;
        AgentUpdateDTO agentUpdateDTO = null;
        Boolean response = api.updateAgent(agentId, agentUpdateDTO);
        // TODO: test validations
    }

}
