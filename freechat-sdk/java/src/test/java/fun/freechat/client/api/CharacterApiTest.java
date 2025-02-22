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
import fun.freechat.client.model.CharacterBackendDTO;
import fun.freechat.client.model.CharacterBackendDetailsDTO;
import fun.freechat.client.model.CharacterCreateDTO;
import fun.freechat.client.model.CharacterDetailsDTO;
import fun.freechat.client.model.CharacterItemForNameDTO;
import fun.freechat.client.model.CharacterQueryDTO;
import fun.freechat.client.model.CharacterSummaryDTO;
import fun.freechat.client.model.CharacterUpdateDTO;
import java.io.File;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for CharacterApi
 */
@Disabled
public class CharacterApiTest {

    private final CharacterApi api = new CharacterApi();

    /**
     * Add Character Backend
     *
     * Add a backend configuration for a character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void addCharacterBackendTest() throws ApiException {
        String characterUid = null;
        CharacterBackendDTO characterBackendDTO = null;
        String response = api.addCharacterBackend(characterUid, characterBackendDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Character Details
     *
     * Batch call shortcut for /api/v2/character/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchCharacterDetailsTest() throws ApiException {
        List<CharacterQueryDTO> characterQueryDTO = null;
        List<List<CharacterDetailsDTO>> response = api.batchSearchCharacterDetails(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Character Summaries
     *
     * Batch call shortcut for /api/v2/character/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchCharacterSummaryTest() throws ApiException {
        List<CharacterQueryDTO> characterQueryDTO = null;
        List<List<CharacterSummaryDTO>> response = api.batchSearchCharacterSummary(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Clone Character
     *
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void cloneCharacterTest() throws ApiException {
        Long characterId = null;
        Long response = api.cloneCharacter(characterId);
        // TODO: test validations
    }

    /**
     * Calculate Number of Characters
     *
     * Calculate the number of characters according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countCharactersTest() throws ApiException {
        CharacterQueryDTO characterQueryDTO = null;
        Long response = api.countCharacters(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Calculate Number of Public Characters
     *
     * Calculate the number of characters according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPublicCharactersTest() throws ApiException {
        CharacterQueryDTO characterQueryDTO = null;
        Long response = api.countPublicCharacters(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Character
     *
     * Create a character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createCharacterTest() throws ApiException {
        CharacterCreateDTO characterCreateDTO = null;
        Long response = api.createCharacter(characterCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Character
     *
     * Delete character. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterTest() throws ApiException {
        Long characterId = null;
        Boolean response = api.deleteCharacter(characterId);
        // TODO: test validations
    }

    /**
     * Delete Character by Name
     *
     * Delete character by name. return the list of successfully deleted characterIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterByNameTest() throws ApiException {
        String name = null;
        List<Long> response = api.deleteCharacterByName(name);
        // TODO: test validations
    }

    /**
     * Delete Character by Uid
     *
     * Delete character by uid. return the list of successfully deleted characterIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterByUidTest() throws ApiException {
        String characterUid = null;
        List<Long> response = api.deleteCharacterByUid(characterUid);
        // TODO: test validations
    }

    /**
     * Delete Character Document
     *
     * Delete a document of the character by key.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterDocumentTest() throws ApiException {
        String key = null;
        Boolean response = api.deleteCharacterDocument(key);
        // TODO: test validations
    }

    /**
     * Delete Character Picture
     *
     * Delete a picture of the character by key.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterPictureTest() throws ApiException {
        String key = null;
        Boolean response = api.deleteCharacterPicture(key);
        // TODO: test validations
    }

    /**
     * Delete Character Voice
     *
     * Delete a voice of the character by key.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteCharacterVoiceTest() throws ApiException {
        String characterBackendId = null;
        String key = null;
        Boolean response = api.deleteCharacterVoice(characterBackendId, key);
        // TODO: test validations
    }

    /**
     * Check If Character Name Exists
     *
     * Check if the character name already exists.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void existsCharacterNameTest() throws ApiException {
        String name = null;
        Boolean response = api.existsCharacterName(name);
        // TODO: test validations
    }

    /**
     * Export Character Configuration
     *
     * Export character configuration in tar.gz format, including settings, documents and pictures.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void exportCharacterTest() throws ApiException {
        Long characterId = null;
        api.exportCharacter(characterId);
        // TODO: test validations
    }

    /**
     * Get Character Details
     *
     * Get character detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getCharacterDetailsTest() throws ApiException {
        Long characterId = null;
        CharacterDetailsDTO response = api.getCharacterDetails(characterId);
        // TODO: test validations
    }

    /**
     * Get Latest Character Id by Name
     *
     * Get latest characterId by character name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getCharacterLatestIdByNameTest() throws ApiException {
        String name = null;
        Long response = api.getCharacterLatestIdByName(name);
        // TODO: test validations
    }

    /**
     * Get Character Summary
     *
     * Get character summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getCharacterSummaryTest() throws ApiException {
        Long characterId = null;
        CharacterSummaryDTO response = api.getCharacterSummary(characterId);
        // TODO: test validations
    }

    /**
     * Get Default Character Backend
     *
     * Get the default backend configuration.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getDefaultCharacterBackendTest() throws ApiException {
        String characterUid = null;
        CharacterBackendDetailsDTO response = api.getDefaultCharacterBackend(characterUid);
        // TODO: test validations
    }

    /**
     * Import Character Configuration
     *
     * Import character configuration from a tar.gz file.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void importCharacterTest() throws ApiException {
        File _file = null;
        Long response = api.importCharacter(_file);
        // TODO: test validations
    }

    /**
     * List Character Backend ids
     *
     * List character backend identifiers.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterBackendIdsTest() throws ApiException {
        String characterUid = null;
        List<String> response = api.listCharacterBackendIds(characterUid);
        // TODO: test validations
    }

    /**
     * List Character Backends
     *
     * List character backends.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterBackendsTest() throws ApiException {
        String characterUid = null;
        List<CharacterBackendDetailsDTO> response = api.listCharacterBackends(characterUid);
        // TODO: test validations
    }

    /**
     * List Character Documents
     *
     * List documents of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterDocumentsTest() throws ApiException {
        String characterUid = null;
        List<String> response = api.listCharacterDocuments(characterUid);
        // TODO: test validations
    }

    /**
     * List Character Pictures
     *
     * List pictures of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterPicturesTest() throws ApiException {
        String characterUid = null;
        List<String> response = api.listCharacterPictures(characterUid);
        // TODO: test validations
    }

    /**
     * List Versions by Character Name
     *
     * List the versions and corresponding characterIds by character name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterVersionsByNameTest() throws ApiException {
        String name = null;
        List<CharacterItemForNameDTO> response = api.listCharacterVersionsByName(name);
        // TODO: test validations
    }

    /**
     * List Character Voices
     *
     * List voices of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharacterVoicesTest() throws ApiException {
        String characterBackendId = null;
        List<String> response = api.listCharacterVoices(characterBackendId);
        // TODO: test validations
    }

    /**
     * Create New Character Name
     *
     * Create a new character name starting with a desired name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void newCharacterNameTest() throws ApiException {
        String desired = null;
        String response = api.newCharacterName(desired);
        // TODO: test validations
    }

    /**
     * Publish Character
     *
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishCharacterTest() throws ApiException {
        Long characterId = null;
        String visibility = null;
        Long response = api.publishCharacter(characterId, visibility);
        // TODO: test validations
    }

    /**
     * Publish Character
     *
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishCharacter1Test() throws ApiException {
        Long characterId = null;
        Long response = api.publishCharacter1(characterId);
        // TODO: test validations
    }

    /**
     * Remove Character Backend
     *
     * Remove a backend configuration.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void removeCharacterBackendTest() throws ApiException {
        String characterBackendId = null;
        Boolean response = api.removeCharacterBackend(characterBackendId);
        // TODO: test validations
    }

    /**
     * Search Character Details
     *
     * Same as /api/v2/character/search, but returns detailed information of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchCharacterDetailsTest() throws ApiException {
        CharacterQueryDTO characterQueryDTO = null;
        List<CharacterDetailsDTO> response = api.searchCharacterDetails(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Character Summary
     *
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchCharacterSummaryTest() throws ApiException {
        CharacterQueryDTO characterQueryDTO = null;
        List<CharacterSummaryDTO> response = api.searchCharacterSummary(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Public Character Summary
     *
     * Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPublicCharacterSummaryTest() throws ApiException {
        CharacterQueryDTO characterQueryDTO = null;
        List<CharacterSummaryDTO> response = api.searchPublicCharacterSummary(characterQueryDTO);
        // TODO: test validations
    }

    /**
     * Set Default Character Backend
     *
     * Set the default backend configuration.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void setDefaultCharacterBackendTest() throws ApiException {
        String characterBackendId = null;
        Boolean response = api.setDefaultCharacterBackend(characterBackendId);
        // TODO: test validations
    }

    /**
     * Update Character
     *
     * Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateCharacterTest() throws ApiException {
        Long characterId = null;
        CharacterUpdateDTO characterUpdateDTO = null;
        Boolean response = api.updateCharacter(characterId, characterUpdateDTO);
        // TODO: test validations
    }

    /**
     * Update Character Backend
     *
     * Update a backend configuration.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateCharacterBackendTest() throws ApiException {
        String characterBackendId = null;
        CharacterBackendDTO characterBackendDTO = null;
        Boolean response = api.updateCharacterBackend(characterBackendId, characterBackendDTO);
        // TODO: test validations
    }

    /**
     * Upload Character Avatar
     *
     * Upload an avatar of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void uploadCharacterAvatarTest() throws ApiException {
        String characterUid = null;
        File _file = null;
        String response = api.uploadCharacterAvatar(characterUid, _file);
        // TODO: test validations
    }

    /**
     * Upload Character Document
     *
     * Upload a document of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void uploadCharacterDocumentTest() throws ApiException {
        String characterUid = null;
        File _file = null;
        String response = api.uploadCharacterDocument(characterUid, _file);
        // TODO: test validations
    }

    /**
     * Upload Character Picture
     *
     * Upload a picture of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void uploadCharacterPictureTest() throws ApiException {
        String characterUid = null;
        File _file = null;
        String response = api.uploadCharacterPicture(characterUid, _file);
        // TODO: test validations
    }

    /**
     * Upload Character Voice
     *
     * Upload a voice of the character.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void uploadCharacterVoiceTest() throws ApiException {
        String characterBackendId = null;
        File _file = null;
        String response = api.uploadCharacterVoice(characterBackendId, _file);
        // TODO: test validations
    }

}
