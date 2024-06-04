# coding: utf-8

# flake8: noqa

"""
    FreeChat OpenAPI Definition

    # FreeChat: Create Some Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports setting **quota limits** for characters. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat Prompt Task)     E --> K(Greeting Prompt Task)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Refence)     J --> P(Tool Specifications)     O --> Q(Template)     O --> R(Variables)     O --> S(Version)     O --> T(...)     style K stroke-dasharray: 5, 5     style L stroke-dasharray: 5, 5     style P stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.      > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.     >      > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*  7. Run `install.sh` script to install FreeChat and its dependencies. 8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 9. Run `restart.sh` to restart the service. 10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml bitnami:   mysql:     enabled: false   redis:     enabled: false   milvus:     enabled: false  mysql:   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   url: <your redis url>   auth:     password: <your redis password>   milvus:   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml bitnami:   prometheus:     enabled: true   grafana:     enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters: ```shell -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [OpenAI](https://platform.openai.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI and DashScope is also expected to be supported soon). However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 

    The version of the OpenAPI document: 1.2.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


__version__ = "1.2.0"

# import apis into sdk package
from freechat_sdk.api.ai_service_api import AIServiceApi
from freechat_sdk.api.account_api import AccountApi
from freechat_sdk.api.account_manager_for_admin_api import AccountManagerForAdminApi
from freechat_sdk.api.agent_api import AgentApi
from freechat_sdk.api.app_config_for_admin_api import AppConfigForAdminApi
from freechat_sdk.api.app_meta_for_admin_api import AppMetaForAdminApi
from freechat_sdk.api.character_api import CharacterApi
from freechat_sdk.api.chat_api import ChatApi
from freechat_sdk.api.encryption_manager_for_admin_api import EncryptionManagerForAdminApi
from freechat_sdk.api.interactive_statistics_api import InteractiveStatisticsApi
from freechat_sdk.api.organization_api import OrganizationApi
from freechat_sdk.api.plugin_api import PluginApi
from freechat_sdk.api.prompt_api import PromptApi
from freechat_sdk.api.prompt_task_api import PromptTaskApi
from freechat_sdk.api.rag_api import RagApi

# import ApiClient
from freechat_sdk.api_response import ApiResponse
from freechat_sdk.api_client import ApiClient
from freechat_sdk.configuration import Configuration
from freechat_sdk.exceptions import OpenApiException
from freechat_sdk.exceptions import ApiTypeError
from freechat_sdk.exceptions import ApiValueError
from freechat_sdk.exceptions import ApiKeyError
from freechat_sdk.exceptions import ApiAttributeError
from freechat_sdk.exceptions import ApiException

# import models into sdk package
from freechat_sdk.models.agent_create_dto import AgentCreateDTO
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO
from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
from freechat_sdk.models.agent_query_where import AgentQueryWhere
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO
from freechat_sdk.models.agent_summary_stats_dto import AgentSummaryStatsDTO
from freechat_sdk.models.agent_update_dto import AgentUpdateDTO
from freechat_sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat_sdk.models.api_token_info_dto import ApiTokenInfoDTO
from freechat_sdk.models.app_config_create_dto import AppConfigCreateDTO
from freechat_sdk.models.app_config_info_dto import AppConfigInfoDTO
from freechat_sdk.models.app_meta_dto import AppMetaDTO
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO
from freechat_sdk.models.character_create_dto import CharacterCreateDTO
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_query_where import CharacterQueryWhere
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO
from freechat_sdk.models.chat_content_dto import ChatContentDTO
from freechat_sdk.models.chat_context_dto import ChatContextDTO
from freechat_sdk.models.chat_create_dto import ChatCreateDTO
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO
from freechat_sdk.models.chat_prompt_content_dto import ChatPromptContentDTO
from freechat_sdk.models.chat_session_dto import ChatSessionDTO
from freechat_sdk.models.chat_tool_call_dto import ChatToolCallDTO
from freechat_sdk.models.chat_update_dto import ChatUpdateDTO
from freechat_sdk.models.hot_tag_dto import HotTagDTO
from freechat_sdk.models.interactive_stats_dto import InteractiveStatsDTO
from freechat_sdk.models.llm_result_dto import LlmResultDTO
from freechat_sdk.models.memory_usage_dto import MemoryUsageDTO
from freechat_sdk.models.open_ai_param_dto import OpenAiParamDTO
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
from freechat_sdk.models.plugin_query_where import PluginQueryWhere
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO
from freechat_sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO
from freechat_sdk.models.plugin_update_dto import PluginUpdateDTO
from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO
from freechat_sdk.models.prompt_create_dto import PromptCreateDTO
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO
from freechat_sdk.models.prompt_item_for_name_dto import PromptItemForNameDTO
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO
from freechat_sdk.models.prompt_query_where import PromptQueryWhere
from freechat_sdk.models.prompt_ref_dto import PromptRefDTO
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO
from freechat_sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO
from freechat_sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO
from freechat_sdk.models.prompt_update_dto import PromptUpdateDTO
from freechat_sdk.models.qwen_param_dto import QwenParamDTO
from freechat_sdk.models.rag_task_dto import RagTaskDTO
from freechat_sdk.models.rag_task_details_dto import RagTaskDetailsDTO
from freechat_sdk.models.sse_emitter import SseEmitter
from freechat_sdk.models.token_usage_dto import TokenUsageDTO
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO
from freechat_sdk.models.user_details_dto import UserDetailsDTO
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO
