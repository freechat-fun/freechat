# coding: utf-8

"""
    FreeChat OpenAPI Definition

    # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports characters replies with **mixed text and image information**. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](https://freechat.fun/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](https://freechat.fun/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](https://freechat.fun/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](https://freechat.fun/img/snapshot_m1.jpg) ![Chat Snapshot 2](https://freechat.fun/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](https://freechat.fun/img/snapshot_m3.jpg) ![Chat Snapshot 4](https://freechat.fun/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 

    The version of the OpenAPI document: 2.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.api.character_api import CharacterApi


class TestCharacterApi(unittest.TestCase):
    """CharacterApi unit test stubs"""

    def setUp(self) -> None:
        self.api = CharacterApi()

    def tearDown(self) -> None:
        pass

    def test_add_character_backend(self) -> None:
        """Test case for add_character_backend

        Add Character Backend
        """
        pass

    def test_batch_search_character_details(self) -> None:
        """Test case for batch_search_character_details

        Batch Search Character Details
        """
        pass

    def test_batch_search_character_summary(self) -> None:
        """Test case for batch_search_character_summary

        Batch Search Character Summaries
        """
        pass

    def test_clone_character(self) -> None:
        """Test case for clone_character

        Clone Character
        """
        pass

    def test_count_characters(self) -> None:
        """Test case for count_characters

        Calculate Number of Characters
        """
        pass

    def test_count_public_characters(self) -> None:
        """Test case for count_public_characters

        Calculate Number of Public Characters
        """
        pass

    def test_create_character(self) -> None:
        """Test case for create_character

        Create Character
        """
        pass

    def test_delete_character(self) -> None:
        """Test case for delete_character

        Delete Character
        """
        pass

    def test_delete_character_by_name(self) -> None:
        """Test case for delete_character_by_name

        Delete Character by Name
        """
        pass

    def test_delete_character_by_uid(self) -> None:
        """Test case for delete_character_by_uid

        Delete Character by Uid
        """
        pass

    def test_delete_character_document(self) -> None:
        """Test case for delete_character_document

        Delete Character Document
        """
        pass

    def test_delete_character_picture(self) -> None:
        """Test case for delete_character_picture

        Delete Character Picture
        """
        pass

    def test_delete_character_video(self) -> None:
        """Test case for delete_character_video

        Delete Character Video
        """
        pass

    def test_delete_character_voice(self) -> None:
        """Test case for delete_character_voice

        Delete Character Voice
        """
        pass

    def test_exists_character_name(self) -> None:
        """Test case for exists_character_name

        Check If Character Name Exists
        """
        pass

    def test_export_character(self) -> None:
        """Test case for export_character

        Export Character Configuration
        """
        pass

    def test_get_character_details(self) -> None:
        """Test case for get_character_details

        Get Character Details
        """
        pass

    def test_get_character_latest_id_by_name(self) -> None:
        """Test case for get_character_latest_id_by_name

        Get Latest Character Id by Name
        """
        pass

    def test_get_character_summary(self) -> None:
        """Test case for get_character_summary

        Get Character Summary
        """
        pass

    def test_get_default_character_backend(self) -> None:
        """Test case for get_default_character_backend

        Get Default Character Backend
        """
        pass

    def test_import_character(self) -> None:
        """Test case for import_character

        Import Character Configuration
        """
        pass

    def test_list_character_backend_ids(self) -> None:
        """Test case for list_character_backend_ids

        List Character Backend ids
        """
        pass

    def test_list_character_backends(self) -> None:
        """Test case for list_character_backends

        List Character Backends
        """
        pass

    def test_list_character_documents(self) -> None:
        """Test case for list_character_documents

        List Character Documents
        """
        pass

    def test_list_character_pictures(self) -> None:
        """Test case for list_character_pictures

        List Character Pictures
        """
        pass

    def test_list_character_versions_by_name(self) -> None:
        """Test case for list_character_versions_by_name

        List Versions by Character Name
        """
        pass

    def test_list_character_videos(self) -> None:
        """Test case for list_character_videos

        List Character Videos
        """
        pass

    def test_list_character_voices(self) -> None:
        """Test case for list_character_voices

        List Character Voices
        """
        pass

    def test_new_character_name(self) -> None:
        """Test case for new_character_name

        Create New Character Name
        """
        pass

    def test_publish_character(self) -> None:
        """Test case for publish_character

        Publish Character
        """
        pass

    def test_publish_character1(self) -> None:
        """Test case for publish_character1

        Publish Character
        """
        pass

    def test_remove_character_backend(self) -> None:
        """Test case for remove_character_backend

        Remove Character Backend
        """
        pass

    def test_search_character_details(self) -> None:
        """Test case for search_character_details

        Search Character Details
        """
        pass

    def test_search_character_summary(self) -> None:
        """Test case for search_character_summary

        Search Character Summary
        """
        pass

    def test_search_public_character_summary(self) -> None:
        """Test case for search_public_character_summary

        Search Public Character Summary
        """
        pass

    def test_set_default_character_backend(self) -> None:
        """Test case for set_default_character_backend

        Set Default Character Backend
        """
        pass

    def test_update_character(self) -> None:
        """Test case for update_character

        Update Character
        """
        pass

    def test_update_character_backend(self) -> None:
        """Test case for update_character_backend

        Update Character Backend
        """
        pass

    def test_upload_character_avatar(self) -> None:
        """Test case for upload_character_avatar

        Upload Character Avatar
        """
        pass

    def test_upload_character_document(self) -> None:
        """Test case for upload_character_document

        Upload Character Document
        """
        pass

    def test_upload_character_picture(self) -> None:
        """Test case for upload_character_picture

        Upload Character Picture
        """
        pass

    def test_upload_character_video(self) -> None:
        """Test case for upload_character_video

        Upload Character Video
        """
        pass

    def test_upload_character_voice(self) -> None:
        """Test case for upload_character_voice

        Upload Character Voice
        """
        pass


if __name__ == '__main__':
    unittest.main()
