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

import fun.freechat.client.ApiException;
import fun.freechat.client.model.LlmResultDTO;
import fun.freechat.client.model.PromptAiParamDTO;
import fun.freechat.client.model.PromptCreateDTO;
import fun.freechat.client.model.PromptDetailsDTO;
import fun.freechat.client.model.PromptItemForNameDTO;
import fun.freechat.client.model.PromptQueryDTO;
import fun.freechat.client.model.PromptRefDTO;
import fun.freechat.client.model.PromptSummaryDTO;
import fun.freechat.client.model.PromptTemplateDTO;
import fun.freechat.client.model.PromptUpdateDTO;
import fun.freechat.client.model.SseEmitter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PromptApi
 */
@Disabled
public class PromptApiTest {

    private final PromptApi api = new PromptApi();

    /**
     * Apply Parameters to Prompt Record
     *
     * Apply parameters to prompt record.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void applyPromptRefTest() throws ApiException {
        PromptRefDTO promptRefDTO = null;
        String response = api.applyPromptRef(promptRefDTO);
        // TODO: test validations
    }

    /**
     * Apply Parameters to Prompt Template
     *
     * Apply parameters to prompt template.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void applyPromptTemplateTest() throws ApiException {
        PromptTemplateDTO promptTemplateDTO = null;
        String response = api.applyPromptTemplate(promptTemplateDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Prompt Details
     *
     * Batch call shortcut for /api/v2/prompt/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPromptDetailsTest() throws ApiException {
        List<PromptQueryDTO> promptQueryDTO = null;
        List<List<PromptDetailsDTO>> response = api.batchSearchPromptDetails(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Prompt Summaries
     *
     * Batch call shortcut for /api/v2/prompt/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPromptSummaryTest() throws ApiException {
        List<PromptQueryDTO> promptQueryDTO = null;
        List<List<PromptSummaryDTO>> response = api.batchSearchPromptSummary(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Clone Prompt
     *
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void clonePromptTest() throws ApiException {
        Long promptId = null;
        Long response = api.clonePrompt(promptId);
        // TODO: test validations
    }

    /**
     * Batch Clone Prompts
     *
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void clonePromptsTest() throws ApiException {
        List<Long> requestBody = null;
        List<Long> response = api.clonePrompts(requestBody);
        // TODO: test validations
    }

    /**
     * Calculate Number of Prompts
     *
     * Calculate the number of prompts according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPromptsTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        Long response = api.countPrompts(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Calculate Number of Public Prompts
     *
     * Calculate the number of prompts according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPublicPromptsTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        Long response = api.countPublicPrompts(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Prompt
     *
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPromptTest() throws ApiException {
        PromptCreateDTO promptCreateDTO = null;
        Long response = api.createPrompt(promptCreateDTO);
        // TODO: test validations
    }

    /**
     * Batch Create Prompts
     *
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPromptsTest() throws ApiException {
        List<PromptCreateDTO> promptCreateDTO = null;
        List<Long> response = api.createPrompts(promptCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Prompt
     *
     * Delete prompt. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptTest() throws ApiException {
        Long promptId = null;
        Boolean response = api.deletePrompt(promptId);
        // TODO: test validations
    }

    /**
     * Delete Prompt by Name
     *
     * Delete prompt by name. return the list of successfully deleted promptIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptByNameTest() throws ApiException {
        String name = null;
        List<Long> response = api.deletePromptByName(name);
        // TODO: test validations
    }

    /**
     * Batch Delete Prompts
     *
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptsTest() throws ApiException {
        List<Long> requestBody = null;
        List<Long> response = api.deletePrompts(requestBody);
        // TODO: test validations
    }

    /**
     * Check If Prompt Name Exists
     *
     * Check if the prompt name already exists.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void existsPromptNameTest() throws ApiException {
        String name = null;
        Boolean response = api.existsPromptName(name);
        // TODO: test validations
    }

    /**
     * Get Prompt Details
     *
     * Get prompt detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPromptDetailsTest() throws ApiException {
        Long promptId = null;
        PromptDetailsDTO response = api.getPromptDetails(promptId);
        // TODO: test validations
    }

    /**
     * Get Prompt Summary
     *
     * Get prompt summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPromptSummaryTest() throws ApiException {
        Long promptId = null;
        PromptSummaryDTO response = api.getPromptSummary(promptId);
        // TODO: test validations
    }

    /**
     * List Versions by Prompt Name
     *
     * List the versions and corresponding promptIds by prompt name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPromptVersionsByNameTest() throws ApiException {
        String name = null;
        List<PromptItemForNameDTO> response = api.listPromptVersionsByName(name);
        // TODO: test validations
    }

    /**
     * Create New Prompt Name
     *
     * Create a new prompt name starting with a desired name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void newPromptNameTest() throws ApiException {
        String desired = null;
        String response = api.newPromptName(desired);
        // TODO: test validations
    }

    /**
     * Publish Prompt
     *
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishPromptTest() throws ApiException {
        Long promptId = null;
        String visibility = null;
        Long response = api.publishPrompt(promptId, visibility);
        // TODO: test validations
    }

    /**
     * Search Prompt Details
     *
     * Same as /api/v2/prompt/search, but returns detailed information of the prompt.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPromptDetailsTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        List<PromptDetailsDTO> response = api.searchPromptDetails(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Prompt Summary
     *
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPromptSummaryTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        List<PromptSummaryDTO> response = api.searchPromptSummary(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Public Prompt Summary
     *
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPublicPromptSummaryTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        List<PromptSummaryDTO> response = api.searchPublicPromptSummary(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Send Prompt
     *
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void sendPromptTest() throws ApiException {
        PromptAiParamDTO promptAiParamDTO = null;
        LlmResultDTO response = api.sendPrompt(promptAiParamDTO);
        // TODO: test validations
    }

    /**
     * Send Prompt by Streaming Back
     *
     * Refer to /api/v2/prompt/send, stream back chunks of the response.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void streamSendPromptTest() throws ApiException {
        PromptAiParamDTO promptAiParamDTO = null;
        SseEmitter response = api.streamSendPrompt(promptAiParamDTO);
        // TODO: test validations
    }

    /**
     * Update Prompt
     *
     * Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updatePromptTest() throws ApiException {
        Long promptId = null;
        PromptUpdateDTO promptUpdateDTO = null;
        Boolean response = api.updatePrompt(promptId, promptUpdateDTO);
        // TODO: test validations
    }

}
