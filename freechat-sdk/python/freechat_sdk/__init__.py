# coding: utf-8

# flake8: noqa

"""
    FreeChat OpenAPI Definition

    # FreeChat: Create Friends for Yourself with AI  English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)  ## Introduction Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.  It also serves as a prompt engineering platform.  It is recommended to run [Ollama](https://ollama.com/) + FreeChat locally to test your models. See the instructions below for [running locally](#running-locally)  ## Features - Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**. - Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions. - Extensively employs distributed technologies and caching to support **high concurrency** access. - Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**. - **Offers a comprehensive range of Open APIs**, with [more than 180 methods](https://freechat.fun/w/docs) and provides java/python/typescript SDKs. These methods enable easy construction of systems for end-users. - Supports setting **RAG** (Retrieval Augmented Generation) for characters. - Supports **long-term memory, preset memory** for characters. - Supports characters evoking **proactive chat**. - Supports **automatic image generation** for characters and replies with mixed text and image messages. - Supports setting **quota limits** for characters. - Supports characters **importing and exporting**. - Supports **character-to-character chats**. - Supports **character voices**. - Supports individual **debugging and sharing prompts**.  ## Snapshots ### On PC #### Home Page ![Home Page Snapshot](https://freechat.fun/img/snapshot_w1.jpg) #### Development View ![Development View Snapshot](https://freechat.fun/img/snapshot_w2.jpg) #### Chat View ![Chat View Snapshot](https://freechat.fun/img/snapshot_w3.jpg)  ### On Mobile ![Chat Snapshot 1](https://freechat.fun/img/snapshot_m1.jpg) ![Chat Snapshot 2](https://freechat.fun/img/snapshot_m2.jpg)<br /> ![Chat Snapshot 3](https://freechat.fun/img/snapshot_m3.jpg) ![Chat Snapshot 4](https://freechat.fun/img/snapshot_m4.jpg)  ## Character Design ```mermaid flowchart TD     A(Character) --> B(Profile)     A --> C(Knowledge/RAG)     A --> D(Album)     A --> E(Backend-1)     A --> F(Backend-n...)     E --> G(Message Window)     E --> H(Long Term Memory Settings)     E --> I(Quota Limit)     E --> J(Chat/Greeting Prompt Tasks)     E --> P(Album/TTS Tools)     E --> L(Moderation Settings)     J --> M(Model & Parameters)     J --> N(API Keys)     J --> O(Prompt Reference)     O --> R(Template)     O --> S(Variables)     O --> T(Version)     O --> U(...)     style L stroke-dasharray: 5, 5 ```  After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.  ## How to Play ### Online Website You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!  ### Running in a Kubernetes Cluster FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:  1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`. 2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed. 3. Switch to the `scripts/` directory. 4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster. 5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`. 6. Run `install.sh` script to install FreeChat and its dependencies. 7. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application. 8. Run `restart.sh` to restart the service. 9. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources. 10. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.  As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.  If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`: ```yaml mysql:   enabled: false   url: <your mysql url>   auth:     rootPassword: <your mysql root password>     username: <your mysql username>     password: <your mysql password for the username>  redis:   enabled: false   url: <your redis url>   auth:     password: <your redis password>   milvus:   enabled: false   url: <your milvus url>   milvus:     auth:       token: <your milvus api-key> ```  With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.  If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application: ```yaml prometheus:   enabled: true grafana:   enabled: true ```  ### Running Locally You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).  Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!  ### Running in an IDE To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup VM options: ```shell -Dspring.config.location=classpath:/application.yml \\ -DAPP_HOME=local-data/freechat \\ -Dspring.profiles.active=local ```  ### Use SDK #### Java - **Dependency** ```xml <dependency>   <groupId>fun.freechat</groupId>   <artifactId>freechat-sdk</artifactId>   <version>${freechat-sdk.version}</version> </dependency> ```  - **Example** ```java import fun.freechat.client.ApiClient; import fun.freechat.client.ApiException; import fun.freechat.client.Configuration; import fun.freechat.client.api.AccountApi; import fun.freechat.client.auth.ApiKeyAuth; import fun.freechat.client.model.UserDetailsDTO;  public class AccountClientExample {     public static void main(String[] args) {         ApiClient defaultClient = Configuration.getDefaultApiClient();         defaultClient.setBasePath(\"https://freechat.fun\");                  // Configure HTTP bearer authorization: bearerAuth         HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");         bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");          AccountApi apiInstance = new AccountApi(defaultClient);         try {             UserDetailsDTO result = apiInstance.getUserDetails();             System.out.println(result);         } catch (ApiException e) {             e.printStackTrace();         }     } } ```  #### Python - **Installation** ```shell pip install freechat-sdk ```  - **Example** ```python import freechat_sdk from freechat_sdk.rest import ApiException from pprint import pprint  # Defining the host is optional and defaults to https://freechat.fun # See configuration.py for a list of all supported configuration parameters. configuration = freechat_sdk.Configuration(     host = \"https://freechat.fun\" )  # Configure Bearer authorization: bearerAuth configuration = freechat_sdk.Configuration(     access_token = os.environ[\"FREECHAT_TOKEN\"] )  # Enter a context with an instance of the API client with freechat_sdk.ApiClient(configuration) as api_client:     # Create an instance of the API class     api_instance = freechat_sdk.AccountApi(api_client)      try:         details = api_instance.get_user_details()         pprint(details)     except ApiException as e:         print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e) ```  #### TypeScript - **Installation** ```shell npm install freechat-sdk --save ```  - **Example**  Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)  ## System Dependencies | | Projects | ---- | ---- | Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/) | LLM Framework | [LangChain4j](https://docs.langchain4j.dev/) | Model Providers | [Ollama](https://ollama.com/), [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/) | Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/) | Monitoring & Alerting | [Kube State Metrics](https://kubernetes.io/docs/concepts/cluster-administration/kube-state-metrics/), [Prometheus](https://prometheus.io/), [Promtail](https://grafana.com/docs/loki/latest/send-data/promtail/), [Loki](https://grafana.com/oss/loki/), [Grafana](https://grafana.com/) | OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)  ## Collaboration ### Application Integration The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, the online FreeChat service is completely free and there are no current plans for charging.  ### Model Integration FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future. 

    The version of the OpenAPI document: 2.7.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


__version__ = "2.7.0"

# Define package exports
__all__ = [
    "AIServiceApi",
    "AccountApi",
    "AccountManagerForAdminApi",
    "AgentApi",
    "AppConfigForAdminApi",
    "AppMetaForAdminApi",
    "CharacterApi",
    "ChatApi",
    "EncryptionManagerForAdminApi",
    "InteractiveStatisticsApi",
    "OrganizationApi",
    "PluginApi",
    "PromptApi",
    "PromptTaskApi",
    "RagApi",
    "TTSServiceApi",
    "TagManagerForBizAdminApi",
    "ApiResponse",
    "ApiClient",
    "Configuration",
    "OpenApiException",
    "ApiTypeError",
    "ApiValueError",
    "ApiKeyError",
    "ApiAttributeError",
    "ApiException",
    "AgentCreateDTO",
    "AgentDetailsDTO",
    "AgentItemForNameDTO",
    "AgentQueryDTO",
    "AgentQueryWhere",
    "AgentSummaryDTO",
    "AgentSummaryStatsDTO",
    "AgentUpdateDTO",
    "AiApiKeyCreateDTO",
    "AiApiKeyInfoDTO",
    "AiModelInfoDTO",
    "ApiTokenInfoDTO",
    "AppMetaDTO",
    "CharacterBackendDTO",
    "CharacterBackendDetailsDTO",
    "CharacterCreateDTO",
    "CharacterDetailsDTO",
    "CharacterItemForNameDTO",
    "CharacterQueryDTO",
    "CharacterQueryWhere",
    "CharacterSummaryDTO",
    "CharacterSummaryStatsDTO",
    "CharacterUpdateDTO",
    "ChatContentDTO",
    "ChatContextDTO",
    "ChatCreateDTO",
    "ChatMessageDTO",
    "ChatMessageRecordDTO",
    "ChatPromptContentDTO",
    "ChatSessionDTO",
    "ChatToolCallDTO",
    "ChatUpdateDTO",
    "HotTagDTO",
    "InteractiveStatsDTO",
    "LlmResultDTO",
    "MemoryUsageDTO",
    "PluginCreateDTO",
    "PluginDetailsDTO",
    "PluginQueryDTO",
    "PluginQueryWhere",
    "PluginSummaryDTO",
    "PluginSummaryStatsDTO",
    "PluginUpdateDTO",
    "PromptAiParamDTO",
    "PromptCreateDTO",
    "PromptDetailsDTO",
    "PromptItemForNameDTO",
    "PromptQueryDTO",
    "PromptQueryWhere",
    "PromptRefDTO",
    "PromptSummaryDTO",
    "PromptSummaryStatsDTO",
    "PromptTaskDTO",
    "PromptTaskDetailsDTO",
    "PromptTemplateDTO",
    "PromptUpdateDTO",
    "RagTaskDTO",
    "RagTaskDetailsDTO",
    "SseEmitter",
    "TokenUsageDTO",
    "UserBasicInfoDTO",
    "UserDetailsDTO",
    "UserFullDetailsDTO",
]

# import apis into sdk package
from freechat_sdk.api.ai_service_api import AIServiceApi as AIServiceApi
from freechat_sdk.api.account_api import AccountApi as AccountApi
from freechat_sdk.api.account_manager_for_admin_api import AccountManagerForAdminApi as AccountManagerForAdminApi
from freechat_sdk.api.agent_api import AgentApi as AgentApi
from freechat_sdk.api.app_config_for_admin_api import AppConfigForAdminApi as AppConfigForAdminApi
from freechat_sdk.api.app_meta_for_admin_api import AppMetaForAdminApi as AppMetaForAdminApi
from freechat_sdk.api.character_api import CharacterApi as CharacterApi
from freechat_sdk.api.chat_api import ChatApi as ChatApi
from freechat_sdk.api.encryption_manager_for_admin_api import EncryptionManagerForAdminApi as EncryptionManagerForAdminApi
from freechat_sdk.api.interactive_statistics_api import InteractiveStatisticsApi as InteractiveStatisticsApi
from freechat_sdk.api.organization_api import OrganizationApi as OrganizationApi
from freechat_sdk.api.plugin_api import PluginApi as PluginApi
from freechat_sdk.api.prompt_api import PromptApi as PromptApi
from freechat_sdk.api.prompt_task_api import PromptTaskApi as PromptTaskApi
from freechat_sdk.api.rag_api import RagApi as RagApi
from freechat_sdk.api.tts_service_api import TTSServiceApi as TTSServiceApi
from freechat_sdk.api.tag_manager_for_biz_admin_api import TagManagerForBizAdminApi as TagManagerForBizAdminApi

# import ApiClient
from freechat_sdk.api_response import ApiResponse as ApiResponse
from freechat_sdk.api_client import ApiClient as ApiClient
from freechat_sdk.configuration import Configuration as Configuration
from freechat_sdk.exceptions import OpenApiException as OpenApiException
from freechat_sdk.exceptions import ApiTypeError as ApiTypeError
from freechat_sdk.exceptions import ApiValueError as ApiValueError
from freechat_sdk.exceptions import ApiKeyError as ApiKeyError
from freechat_sdk.exceptions import ApiAttributeError as ApiAttributeError
from freechat_sdk.exceptions import ApiException as ApiException

# import models into sdk package
from freechat_sdk.models.agent_create_dto import AgentCreateDTO as AgentCreateDTO
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO as AgentDetailsDTO
from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO as AgentItemForNameDTO
from freechat_sdk.models.agent_query_dto import AgentQueryDTO as AgentQueryDTO
from freechat_sdk.models.agent_query_where import AgentQueryWhere as AgentQueryWhere
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO as AgentSummaryDTO
from freechat_sdk.models.agent_summary_stats_dto import AgentSummaryStatsDTO as AgentSummaryStatsDTO
from freechat_sdk.models.agent_update_dto import AgentUpdateDTO as AgentUpdateDTO
from freechat_sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO as AiApiKeyCreateDTO
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO as AiApiKeyInfoDTO
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO as AiModelInfoDTO
from freechat_sdk.models.api_token_info_dto import ApiTokenInfoDTO as ApiTokenInfoDTO
from freechat_sdk.models.app_meta_dto import AppMetaDTO as AppMetaDTO
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO as CharacterBackendDTO
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO as CharacterBackendDetailsDTO
from freechat_sdk.models.character_create_dto import CharacterCreateDTO as CharacterCreateDTO
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO as CharacterDetailsDTO
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO as CharacterItemForNameDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO as CharacterQueryDTO
from freechat_sdk.models.character_query_where import CharacterQueryWhere as CharacterQueryWhere
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO as CharacterSummaryDTO
from freechat_sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO as CharacterSummaryStatsDTO
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO as CharacterUpdateDTO
from freechat_sdk.models.chat_content_dto import ChatContentDTO as ChatContentDTO
from freechat_sdk.models.chat_context_dto import ChatContextDTO as ChatContextDTO
from freechat_sdk.models.chat_create_dto import ChatCreateDTO as ChatCreateDTO
from freechat_sdk.models.chat_message_dto import ChatMessageDTO as ChatMessageDTO
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO as ChatMessageRecordDTO
from freechat_sdk.models.chat_prompt_content_dto import ChatPromptContentDTO as ChatPromptContentDTO
from freechat_sdk.models.chat_session_dto import ChatSessionDTO as ChatSessionDTO
from freechat_sdk.models.chat_tool_call_dto import ChatToolCallDTO as ChatToolCallDTO
from freechat_sdk.models.chat_update_dto import ChatUpdateDTO as ChatUpdateDTO
from freechat_sdk.models.hot_tag_dto import HotTagDTO as HotTagDTO
from freechat_sdk.models.interactive_stats_dto import InteractiveStatsDTO as InteractiveStatsDTO
from freechat_sdk.models.llm_result_dto import LlmResultDTO as LlmResultDTO
from freechat_sdk.models.memory_usage_dto import MemoryUsageDTO as MemoryUsageDTO
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO as PluginCreateDTO
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO as PluginDetailsDTO
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO as PluginQueryDTO
from freechat_sdk.models.plugin_query_where import PluginQueryWhere as PluginQueryWhere
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO as PluginSummaryDTO
from freechat_sdk.models.plugin_summary_stats_dto import PluginSummaryStatsDTO as PluginSummaryStatsDTO
from freechat_sdk.models.plugin_update_dto import PluginUpdateDTO as PluginUpdateDTO
from freechat_sdk.models.prompt_ai_param_dto import PromptAiParamDTO as PromptAiParamDTO
from freechat_sdk.models.prompt_create_dto import PromptCreateDTO as PromptCreateDTO
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO as PromptDetailsDTO
from freechat_sdk.models.prompt_item_for_name_dto import PromptItemForNameDTO as PromptItemForNameDTO
from freechat_sdk.models.prompt_query_dto import PromptQueryDTO as PromptQueryDTO
from freechat_sdk.models.prompt_query_where import PromptQueryWhere as PromptQueryWhere
from freechat_sdk.models.prompt_ref_dto import PromptRefDTO as PromptRefDTO
from freechat_sdk.models.prompt_summary_dto import PromptSummaryDTO as PromptSummaryDTO
from freechat_sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO as PromptSummaryStatsDTO
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO as PromptTaskDTO
from freechat_sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO as PromptTaskDetailsDTO
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO as PromptTemplateDTO
from freechat_sdk.models.prompt_update_dto import PromptUpdateDTO as PromptUpdateDTO
from freechat_sdk.models.rag_task_dto import RagTaskDTO as RagTaskDTO
from freechat_sdk.models.rag_task_details_dto import RagTaskDetailsDTO as RagTaskDetailsDTO
from freechat_sdk.models.sse_emitter import SseEmitter as SseEmitter
from freechat_sdk.models.token_usage_dto import TokenUsageDTO as TokenUsageDTO
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO as UserBasicInfoDTO
from freechat_sdk.models.user_details_dto import UserDetailsDTO as UserDetailsDTO
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO as UserFullDetailsDTO

