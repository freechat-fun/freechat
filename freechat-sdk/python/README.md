# freechat-sdk
# FreeChat: Create Some Friends for Yourself with AI

English | [中文版](https://github.com/freechat-fun/freechat/blob/main/README.zh-CN.md)

## Introduction
Welcome! FreeChat aims to build a cloud-native, robust, and quickly commercializable enterprise-level AI virtual character platform.

It also serves as a prompt engineering platform.

## Features
- Primarily uses Java and emphasizes **security, robustness, scalability, traceability, and maintainability**.
- Boasts **account systems and permission management**, supporting OAuth2 authentication. Introduces the \"organization\" concept and related permission constraint functions.
- Extensively employs distributed technologies and caching to support **high concurrency** access.
- Provides flexible character customization options, supports direct intervention in prompts, and supports **configuring multiple backends for each character**.
- **Offers a comprehensive range of Open APIs**, with more than 180 interfaces and provides java/python/typescript SDKs. These interfaces enable easy construction of systems for end-users.
- Supports setting **RAG** (Retrieval Augmented Generation) for characters.
- Supports **long-term memory, preset memory** for characters.
- Supports characters evoking **proactive chat**.
- Supports setting **quota limits** for characters.
- Supports individual **debugging and sharing prompts**.

## Snapshots
### On PC
#### Home Page
![Home Page Snapshot](/img/snapshot_w1.jpg)
#### Development View
![Development View Snapshot](/img/snapshot_w2.jpg)
#### Chat View
![Chat View Snapshot](/img/snapshot_w3.jpg)

### On Mobile
![Chat Snapshot 1](/img/snapshot_m1.jpg) ![Chat Snapshot 2](/img/snapshot_m2.jpg)<br />
![Chat Snapshot 3](/img/snapshot_m3.jpg) ![Chat Snapshot 4](/img/snapshot_m4.jpg)

## Character Design
```mermaid
flowchart TD
    A(Character) --> B(Profile)
    A --> C(Knowledge/RAG)
    A --> D(Album)
    A --> E(Backend-1)
    A --> F(Backend-n...)
    E --> G(Message Window)
    E --> H(Long Term Memory Settings)
    E --> I(Quota Limit)
    E --> J(Chat Prompt Task)
    E --> K(Greeting Prompt Task)
    E --> L(Moderation Settings)
    J --> M(Model & Parameters)
    J --> N(API Keys)
    J --> O(Prompt Refence)
    J --> P(Tool Specifications)
    O --> Q(Template)
    O --> R(Variables)
    O --> S(Version)
    O --> T(...)
    style K stroke-dasharray: 5, 5
    style L stroke-dasharray: 5, 5
    style P stroke-dasharray: 5, 5
```

After setting up an unified persona and knowledge for a character, different backends can be configured. For example, different model may be adopted for different users based on cost considerations.

## How to Play
### Online Website
You can visit [freechat.fun](https://freechat.fun) to experience FreeChat. Share your designed AI character!

### Running in a Kubernetes Cluster
FreeChat is dedicated to the principles of cloud-native design. If you have a Kubernetes cluster, you can deploy FreeChat to your environment by following these steps:

1. Put the Kubernetes configuration file in the `configs/helm/` directory, named `kube-private.conf`.
2. Place the Helm configuration file in the same directory, named `values-private.yaml`. Make sure to reference the default `values.yaml` and customize the variables as needed.
3. Switch to the `scripts/` directory.
4. If needed, run `install-in.sh` to deploy `ingress-nginx` on the Kubernetes cluster.
5. If needed, run `install-cm.sh` to deploy `cert-manager` on the Kubernetes cluster, which automatically issues certificates for domains specified in `ingress.hosts`.
6. Run `install-pvc.sh` to install PersistentVolumeClaim resources.

    > By default, FreeChat operates files by accessing the \"local file system.\" You may want to use high-availability distributed storage in the cloud. As a cloud-native-designed system, we recommend interfacing through Kubernetes CSI to avoid individually adapting storage products for each cloud platform. Most cloud service providers offer cloud storage drivers for Kubernetes, with a series of predefined StorageClass resources. Please choose the appropriate configuration according to your actual needs and set it in Helm's `global.storageClass` option.
    > 
    > *In the future, FreeChat may be refactored to use MinIO's APIs directly, as it is now installed in the Kubernetes cluster as a dependency (serving Milvus).*

7. Run `install.sh` script to install FreeChat and its dependencies.
8. FreeChat aims to provide Open API services. If you like the interactive experience of [freechat.fun](https://freechat.fun), run `install-web.sh` to deploy the front-end application.
9. Run `restart.sh` to restart the service.
10. If you modified any Helm configuration files, use `upgrade.sh` to update the corresponding Kubernetes resources.
11. To remove specific resources, run the `uninstall*.sh` script corresponding to the resource you want to uninstall.

As a cloud-native application, the services FreeChat relies on are obtained and deployed to your cluster through the helm repository.

If you prefer cloud services with SLA (Service Level Agreement) guarantees, simply make the relevant settings in `configs/helm/values-private.yaml`:
```yaml
mysql:
  deployment:
    enabled: false
  url: <your mysql url>
  auth:
    rootPassword: <your mysql root password>
    username: <your mysql username>
    password: <your mysql password for the username>

redis:
  deployment:
    enabled: false
  url: <your redis url>
  auth:
    password: <your redis password>


milvus:
  deployment:
    enabled: false
  url: <your milvus url>
  milvus:
    auth:
      token: <your milvus api-key>
```

With this, FreeChat will not automatically install these services, but rather use the configuration information to connect directly.

If your Kubernetes cluster does not have a standalone monitoring system, you can enable the following switch. This will install Prometheus and Grafana services in the same namespace, dedicated to monitoring the status of the services under the FreeChat application:
```yaml
prometheus:
  deployment:
    enabled: true
grafana:
  deployment:
    enabled: true
```

### Running Locally
You can also run FreeChat locally. Currently supported on MacOS and Linux (although only tested on MacOS). You need to install the Docker toolset and have a network that can access [Docker Hub](https://hub.docker.com/).

Once ready, enter the `scripts/` directory and run `local-run.sh`, which will download and run the necessary docker containers. After a successful startup, you can access `http://localhost` via a browser to see the locally running freechat.fun. The built-in administrator username and password are \"admin:freechat\". Use `local-run.sh --help` to view the supported options of the script. Good luck!

### Running in an IDE
To run FreeChat in an IDE, you need to start all dependent services first but do not need to run the container for the FreeChat application itself. You can execute the `scripts/local-deps.sh` script to start services like `MySQL`, `Redis`, `Milvus`, etc., locally. Once done, open and debug `freechat-start/src/main/java/fun/freechat/Application.java`。Make sure you have set the following startup parameters:
```shell
-Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \\
-DAPP_HOME=local-data/freechat \\
-Dspring.profiles.active=local
```

### Use SDK
#### Java
- **Dependency**
```xml
<dependency>
  <groupId>fun.freechat</groupId>
  <artifactId>freechat-sdk</artifactId>
  <version>${freechat-sdk.version}</version>
</dependency>
```

- **Example**
```java
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.api.AccountApi;
import fun.freechat.client.auth.ApiKeyAuth;
import fun.freechat.client.model.UserDetailsDTO;

public class AccountClientExample {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(\"https://freechat.fun\");
        
        // Configure HTTP bearer authorization: bearerAuth
        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication(\"bearerAuth\");
        bearerAuth.setBearerToken(\"FREECHAT_TOKEN\");

        AccountApi apiInstance = new AccountApi(defaultClient);
        try {
            UserDetailsDTO result = apiInstance.getUserDetails();
            System.out.println(result);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
```

#### Python
- **Installation**
```shell
pip install freechat-sdk
```

- **Example**
```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = \"https://freechat.fun\"
)

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ[\"FREECHAT_TOKEN\"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.AccountApi(api_client)

    try:
        details = api_instance.get_user_details()
        pprint(details)
    except ApiException as e:
        print(\"Exception when calling AccountClient->get_user_details: %s\\n\" % e)
```

#### TypeScript
- **Installation**
```shell
npm install freechat-sdk --save
```

- **Example**

Refer to [FreeChatApiContext.tsx](https://github.com/freechat-fun/freechat/blob/main/freechat-web/src/contexts/FreeChatApiProvider.tsx)

## System Dependencies
| | Projects
| ---- | ----
| Application Framework | [Spring Boot](https://spring.io/projects/spring-boot/)
| LLM Framework | [LangChain4j](https://docs.langchain4j.dev/)
| Model Providers | [OpenAI](https://platform.openai.com/), [Azure OpenAI](https://oai.azure.com/), [DashScope(Alibaba)](https://dashscope.aliyun.com/)
| Database Systems | [MySQL](https://www.mysql.com/), [Redis](https://redis.io/), [Milvus](https://milvus.io/)
| Monitoring & Alerting | [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/)
| OpenAPI Tools | [Springdoc-openapi](https://springdoc.org/), [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator), [OpenAPI Explorer](https://github.com/Authress-Engineering/openapi-explorer)

## Collaboration
### Application Integration
The FreeChat system is entirely oriented towards Open APIs. The site [freechat.fun](https://freechat.fun) is developed using its TypeScript SDK and hardly depends on private interfaces. You can use these online interfaces to develop your own applications or sites, making them fit your preferences. Currently, FreeChat is completely free with no paid plans (after all, users use their own API Key to call LLM services).

### Model Integration
FreeChat aims to explore AI virtual character technology with anthropomorphic characteristics. So far, it supports model services from OpenAI GPT and Alibaba Qwen series models. However, we are more interested in supporting models that are under research and can endow AI with more personality traits. If you are researching this area and hope FreeChat supports your model, please contact us. We look forward to AI technology helping people create their own \"soul mates\" in the future.


This Python package is automatically generated by the [OpenAPI Generator](https://openapi-generator.tech) project:

- API version: 1.3.0
- Package version: 1.3.0
- Generator version: 7.7.0
- Build package: org.openapitools.codegen.languages.PythonClientCodegen

## Requirements.

Python 3.7+

## Installation & Usage
### pip install

If the python package is hosted on a repository, you can install directly using:

```sh
pip install git+https://github.com/freechat-fun/freechat.git
```
(you may need to run `pip` with root permission: `sudo pip install git+https://github.com/freechat-fun/freechat.git`)

Then import the package:
```python
import freechat_sdk
```

### Setuptools

Install via [Setuptools](http://pypi.python.org/pypi/setuptools).

```sh
python setup.py install --user
```
(or `sudo python setup.py install` to install the package for all users)

Then import the package:
```python
import freechat_sdk
```

### Tests

Execute `pytest` to run the tests.

## Getting Started

Please follow the [installation procedure](#installation--usage) and then run the following:

```python

import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)


# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.AIServiceApi(api_client)
    ai_api_key_create_dto = freechat_sdk.AiApiKeyCreateDTO() # AiApiKeyCreateDTO | Model call credential information

    try:
        # Add Model Provider Credential
        api_response = api_instance.add_ai_api_key(ai_api_key_create_dto)
        print("The response of AIServiceApi->add_ai_api_key:\n")
        pprint(api_response)
    except ApiException as e:
        print("Exception when calling AIServiceApi->add_ai_api_key: %s\n" % e)

```

## Documentation for API Endpoints

All URIs are relative to *http://127.0.0.1:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AIServiceApi* | [**add_ai_api_key**](docs/AIServiceApi.md#add_ai_api_key) | **POST** /api/v1/ai/apikey | Add Model Provider Credential
*AIServiceApi* | [**delete_ai_api_key**](docs/AIServiceApi.md#delete_ai_api_key) | **DELETE** /api/v1/ai/apikey/{id} | Delete Credential of Model Provider
*AIServiceApi* | [**disable_ai_api_key**](docs/AIServiceApi.md#disable_ai_api_key) | **PUT** /api/v1/ai/apikey/disable/{id} | Disable Model Provider Credential
*AIServiceApi* | [**enable_ai_api_key**](docs/AIServiceApi.md#enable_ai_api_key) | **PUT** /api/v1/ai/apikey/enable/{id} | Enable Model Provider Credential
*AIServiceApi* | [**get_ai_api_key**](docs/AIServiceApi.md#get_ai_api_key) | **GET** /api/v1/ai/apikey/{id} | Get credential of Model Provider
*AIServiceApi* | [**get_ai_model_info**](docs/AIServiceApi.md#get_ai_model_info) | **GET** /api/v1/ai/model/{modelId} | Get Model Information
*AIServiceApi* | [**list_ai_api_keys**](docs/AIServiceApi.md#list_ai_api_keys) | **GET** /api/v1/ai/apikeys/{provider} | List Credentials of Model Provider
*AIServiceApi* | [**list_ai_model_info**](docs/AIServiceApi.md#list_ai_model_info) | **GET** /api/v1/ai/models/{pageSize} | List Models
*AIServiceApi* | [**list_ai_model_info1**](docs/AIServiceApi.md#list_ai_model_info1) | **GET** /api/v1/ai/models | List Models
*AIServiceApi* | [**list_ai_model_info2**](docs/AIServiceApi.md#list_ai_model_info2) | **GET** /api/v1/ai/models/{pageSize}/{pageNum} | List Models
*AccountApi* | [**create_token**](docs/AccountApi.md#create_token) | **POST** /api/v1/account/token | Create API Token
*AccountApi* | [**create_token1**](docs/AccountApi.md#create_token1) | **POST** /api/v1/account/token/{duration} | Create API Token
*AccountApi* | [**delete_token**](docs/AccountApi.md#delete_token) | **DELETE** /api/v1/account/token/{token} | Delete API Token
*AccountApi* | [**delete_token_by_id**](docs/AccountApi.md#delete_token_by_id) | **DELETE** /api/v1/account/token/id/{id} | Delete API Token by Id
*AccountApi* | [**disable_token**](docs/AccountApi.md#disable_token) | **PUT** /api/v1/account/token/{token} | Disable API Token
*AccountApi* | [**disable_token_by_id**](docs/AccountApi.md#disable_token_by_id) | **PUT** /api/v1/account/token/id/{id} | Disable API Token by Id
*AccountApi* | [**get_token_by_id**](docs/AccountApi.md#get_token_by_id) | **GET** /api/v1/account/token/id/{id} | Get API Token by Id
*AccountApi* | [**get_user_basic**](docs/AccountApi.md#get_user_basic) | **GET** /api/v1/account/basic | Get User Basic Information
*AccountApi* | [**get_user_basic1**](docs/AccountApi.md#get_user_basic1) | **GET** /api/v1/account/basic/{username} | Get User Basic Information
*AccountApi* | [**get_user_details**](docs/AccountApi.md#get_user_details) | **GET** /api/v1/account/details | Get User Details
*AccountApi* | [**list_tokens**](docs/AccountApi.md#list_tokens) | **GET** /api/v1/account/tokens | List API Tokens
*AccountApi* | [**update_user_info**](docs/AccountApi.md#update_user_info) | **PUT** /api/v1/account/details | Update User Details
*AccountApi* | [**upload_user_picture**](docs/AccountApi.md#upload_user_picture) | **POST** /api/v1/account/picture | Upload User Picture
*AccountManagerForAdminApi* | [**create_token_for_user**](docs/AccountManagerForAdminApi.md#create_token_for_user) | **POST** /api/v1/admin/token/{username}/{duration} | Create API Token for User.
*AccountManagerForAdminApi* | [**create_user**](docs/AccountManagerForAdminApi.md#create_user) | **POST** /api/v1/admin/user | Create User
*AccountManagerForAdminApi* | [**delete_token_for_user**](docs/AccountManagerForAdminApi.md#delete_token_for_user) | **DELETE** /api/v1/admin/token/{token} | Delete API Token
*AccountManagerForAdminApi* | [**delete_user**](docs/AccountManagerForAdminApi.md#delete_user) | **DELETE** /api/v1/admin/user/{username} | Delete User
*AccountManagerForAdminApi* | [**disable_token_for_user**](docs/AccountManagerForAdminApi.md#disable_token_for_user) | **PUT** /api/v1/admin/token/{token} | Disable API Token
*AccountManagerForAdminApi* | [**get_details_of_user**](docs/AccountManagerForAdminApi.md#get_details_of_user) | **GET** /api/v1/admin/user/{username} | Get User Details
*AccountManagerForAdminApi* | [**get_user_by_token**](docs/AccountManagerForAdminApi.md#get_user_by_token) | **GET** /api/v1/admin/tokenBy/{token} | Get User by API Token
*AccountManagerForAdminApi* | [**list_authorities_of_user**](docs/AccountManagerForAdminApi.md#list_authorities_of_user) | **GET** /api/v1/admin/authority/{username} | List User Permissions
*AccountManagerForAdminApi* | [**list_tokens_of_user**](docs/AccountManagerForAdminApi.md#list_tokens_of_user) | **GET** /api/v1/admin/token/{username} | Get API Token of User
*AccountManagerForAdminApi* | [**list_users**](docs/AccountManagerForAdminApi.md#list_users) | **GET** /api/v1/admin/users/{pageSize}/{pageNum} | List User Information
*AccountManagerForAdminApi* | [**list_users1**](docs/AccountManagerForAdminApi.md#list_users1) | **GET** /api/v1/admin/users/{pageSize} | List User Information
*AccountManagerForAdminApi* | [**list_users2**](docs/AccountManagerForAdminApi.md#list_users2) | **GET** /api/v1/admin/users | List User Information
*AccountManagerForAdminApi* | [**update_authorities_of_user**](docs/AccountManagerForAdminApi.md#update_authorities_of_user) | **PUT** /api/v1/admin/authority/{username} | Update User Permissions
*AccountManagerForAdminApi* | [**update_user**](docs/AccountManagerForAdminApi.md#update_user) | **PUT** /api/v1/admin/user | Update User
*AgentApi* | [**batch_search_agent_details**](docs/AgentApi.md#batch_search_agent_details) | **POST** /api/v1/agent/batch/details/search | Batch Search Agent Details
*AgentApi* | [**batch_search_agent_summary**](docs/AgentApi.md#batch_search_agent_summary) | **POST** /api/v1/agent/batch/search | Batch Search Agent Summaries
*AgentApi* | [**clone_agent**](docs/AgentApi.md#clone_agent) | **POST** /api/v1/agent/clone/{agentId} | Clone Agent
*AgentApi* | [**clone_agents**](docs/AgentApi.md#clone_agents) | **POST** /api/v1/agent/batch/clone | Batch Clone Agents
*AgentApi* | [**count_agents**](docs/AgentApi.md#count_agents) | **POST** /api/v1/agent/count | Calculate Number of Agents
*AgentApi* | [**create_agent**](docs/AgentApi.md#create_agent) | **POST** /api/v1/agent | Create Agent
*AgentApi* | [**create_agents**](docs/AgentApi.md#create_agents) | **POST** /api/v1/agent/batch | Batch Create Agents
*AgentApi* | [**delete_agent**](docs/AgentApi.md#delete_agent) | **DELETE** /api/v1/agent/{agentId} | Delete Agent
*AgentApi* | [**delete_agents**](docs/AgentApi.md#delete_agents) | **DELETE** /api/v1/agent/batch/delete | Batch Delete Agents
*AgentApi* | [**get_agent_details**](docs/AgentApi.md#get_agent_details) | **GET** /api/v1/agent/details/{agentId} | Get Agent Details
*AgentApi* | [**get_agent_summary**](docs/AgentApi.md#get_agent_summary) | **GET** /api/v1/agent/summary/{agentId} | Get Agent Summary
*AgentApi* | [**list_agent_versions_by_name**](docs/AgentApi.md#list_agent_versions_by_name) | **POST** /api/v1/agent/versions/{name} | List Versions by Agent Name
*AgentApi* | [**publish_agent**](docs/AgentApi.md#publish_agent) | **POST** /api/v1/agent/publish/{agentId}/{visibility} | Publish Agent
*AgentApi* | [**search_agent_details**](docs/AgentApi.md#search_agent_details) | **POST** /api/v1/agent/details/search | Search Agent Details
*AgentApi* | [**search_agent_summary**](docs/AgentApi.md#search_agent_summary) | **POST** /api/v1/agent/search | Search Agent Summary
*AgentApi* | [**update_agent**](docs/AgentApi.md#update_agent) | **PUT** /api/v1/agent/{agentId} | Update Agent
*AppConfigForAdminApi* | [**get_app_configs**](docs/AppConfigForAdminApi.md#get_app_configs) | **GET** /api/v1/admin/app/configs | Get Configurations
*AppMetaForAdminApi* | [**expose**](docs/AppMetaForAdminApi.md#expose) | **GET** /api/v1/admin/app/expose | Expose DTO definitions
*AppMetaForAdminApi* | [**get_app_meta**](docs/AppMetaForAdminApi.md#get_app_meta) | **GET** /api/v1/admin/app/meta | Get Application Information
*CharacterApi* | [**add_character_backend**](docs/CharacterApi.md#add_character_backend) | **POST** /api/v1/character/backend/{characterId} | Add Character Backend
*CharacterApi* | [**batch_search_character_details**](docs/CharacterApi.md#batch_search_character_details) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details
*CharacterApi* | [**batch_search_character_summary**](docs/CharacterApi.md#batch_search_character_summary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries
*CharacterApi* | [**clone_character**](docs/CharacterApi.md#clone_character) | **POST** /api/v1/character/clone/{characterId} | Clone Character
*CharacterApi* | [**count_characters**](docs/CharacterApi.md#count_characters) | **POST** /api/v1/character/count | Calculate Number of Characters
*CharacterApi* | [**create_character**](docs/CharacterApi.md#create_character) | **POST** /api/v1/character | Create Character
*CharacterApi* | [**delete_character**](docs/CharacterApi.md#delete_character) | **DELETE** /api/v1/character/{characterId} | Delete Character
*CharacterApi* | [**delete_character_by_name**](docs/CharacterApi.md#delete_character_by_name) | **DELETE** /api/v1/character/name/{name} | Delete Character by Name
*CharacterApi* | [**delete_character_document**](docs/CharacterApi.md#delete_character_document) | **DELETE** /api/v1/character/document/{key} | Delete Character Document
*CharacterApi* | [**delete_character_picture**](docs/CharacterApi.md#delete_character_picture) | **DELETE** /api/v1/character/picture/{key} | Delete Character Picture
*CharacterApi* | [**exists_character_name**](docs/CharacterApi.md#exists_character_name) | **GET** /api/v1/character/exists/name/{name} | Check If Character Name Exists
*CharacterApi* | [**export_character**](docs/CharacterApi.md#export_character) | **GET** /api/v1/character/export/{characterId} | Export Character Configuration
*CharacterApi* | [**get_character_details**](docs/CharacterApi.md#get_character_details) | **GET** /api/v1/character/details/{characterId} | Get Character Details
*CharacterApi* | [**get_character_latest_id_by_name**](docs/CharacterApi.md#get_character_latest_id_by_name) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
*CharacterApi* | [**get_character_summary**](docs/CharacterApi.md#get_character_summary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
*CharacterApi* | [**get_default_character_backend**](docs/CharacterApi.md#get_default_character_backend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend
*CharacterApi* | [**import_character**](docs/CharacterApi.md#import_character) | **POST** /api/v1/character/import | Import Character Configuration
*CharacterApi* | [**list_character_backend_ids**](docs/CharacterApi.md#list_character_backend_ids) | **GET** /api/v1/character/backend/ids/{characterId} | List Character Backend ids
*CharacterApi* | [**list_character_backends**](docs/CharacterApi.md#list_character_backends) | **GET** /api/v1/character/backends/{characterId} | List Character Backends
*CharacterApi* | [**list_character_documents**](docs/CharacterApi.md#list_character_documents) | **GET** /api/v1/character/documents/{characterId} | List Character Documents
*CharacterApi* | [**list_character_pictures**](docs/CharacterApi.md#list_character_pictures) | **GET** /api/v1/character/pictures/{characterId} | List Character Pictures
*CharacterApi* | [**list_character_versions_by_name**](docs/CharacterApi.md#list_character_versions_by_name) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
*CharacterApi* | [**new_character_name**](docs/CharacterApi.md#new_character_name) | **GET** /api/v1/character/create/name/{desired} | Create New Character Name
*CharacterApi* | [**publish_character**](docs/CharacterApi.md#publish_character) | **POST** /api/v1/character/publish/{characterId} | Publish Character
*CharacterApi* | [**publish_character1**](docs/CharacterApi.md#publish_character1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character
*CharacterApi* | [**remove_character_backend**](docs/CharacterApi.md#remove_character_backend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend
*CharacterApi* | [**search_character_details**](docs/CharacterApi.md#search_character_details) | **POST** /api/v1/character/details/search | Search Character Details
*CharacterApi* | [**search_character_summary**](docs/CharacterApi.md#search_character_summary) | **POST** /api/v1/character/search | Search Character Summary
*CharacterApi* | [**set_default_character_backend**](docs/CharacterApi.md#set_default_character_backend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend
*CharacterApi* | [**update_character**](docs/CharacterApi.md#update_character) | **PUT** /api/v1/character/{characterId} | Update Character
*CharacterApi* | [**update_character_backend**](docs/CharacterApi.md#update_character_backend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend
*CharacterApi* | [**upload_character_avatar**](docs/CharacterApi.md#upload_character_avatar) | **POST** /api/v1/character/avatar/{characterId} | Upload Character Avatar
*CharacterApi* | [**upload_character_document**](docs/CharacterApi.md#upload_character_document) | **POST** /api/v1/character/document/{characterId} | Upload Character Document
*CharacterApi* | [**upload_character_picture**](docs/CharacterApi.md#upload_character_picture) | **POST** /api/v1/character/picture/{characterId} | Upload Character Picture
*ChatApi* | [**clear_memory**](docs/ChatApi.md#clear_memory) | **DELETE** /api/v1/chat/memory/{chatId} | Clear Memory
*ChatApi* | [**delete_chat**](docs/ChatApi.md#delete_chat) | **DELETE** /api/v1/chat/{chatId} | Delete Chat Session
*ChatApi* | [**get_default_chat_id**](docs/ChatApi.md#get_default_chat_id) | **GET** /api/v1/chat/{characterId} | Get Default Chat
*ChatApi* | [**get_memory_usage**](docs/ChatApi.md#get_memory_usage) | **GET** /api/v1/chat/memory/usage/{chatId} | Get Memory Usage
*ChatApi* | [**list_chats**](docs/ChatApi.md#list_chats) | **GET** /api/v1/chat | List Chats
*ChatApi* | [**list_messages**](docs/ChatApi.md#list_messages) | **GET** /api/v1/chat/messages/{chatId} | List Chat Messages
*ChatApi* | [**list_messages1**](docs/ChatApi.md#list_messages1) | **GET** /api/v1/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
*ChatApi* | [**list_messages2**](docs/ChatApi.md#list_messages2) | **GET** /api/v1/chat/messages/{chatId}/{limit} | List Chat Messages
*ChatApi* | [**rollback_messages**](docs/ChatApi.md#rollback_messages) | **POST** /api/v1/chat/messages/rollback/{chatId}/{count} | Rollback Chat Messages
*ChatApi* | [**send_message**](docs/ChatApi.md#send_message) | **POST** /api/v1/chat/send/{chatId} | Send Chat Message
*ChatApi* | [**start_chat**](docs/ChatApi.md#start_chat) | **POST** /api/v1/chat | Start Chat Session
*ChatApi* | [**stream_send_message**](docs/ChatApi.md#stream_send_message) | **POST** /api/v1/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
*ChatApi* | [**update_chat**](docs/ChatApi.md#update_chat) | **PUT** /api/v1/chat/{chatId} | Update Chat Session
*EncryptionManagerForAdminApi* | [**encrypt_text**](docs/EncryptionManagerForAdminApi.md#encrypt_text) | **GET** /api/v1/admin/encryption/encrypt/{text} | Encrypt Text
*InteractiveStatisticsApi* | [**add_statistic**](docs/InteractiveStatisticsApi.md#add_statistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics
*InteractiveStatisticsApi* | [**get_score**](docs/InteractiveStatisticsApi.md#get_score) | **GET** /api/v1/score/{infoType}/{infoId} | Get Score for Resource
*InteractiveStatisticsApi* | [**get_statistic**](docs/InteractiveStatisticsApi.md#get_statistic) | **GET** /api/v1/stats/{infoType}/{infoId}/{statsType} | Get Statistics
*InteractiveStatisticsApi* | [**get_statistics**](docs/InteractiveStatisticsApi.md#get_statistics) | **GET** /api/v1/stats/{infoType}/{infoId} | Get All Statistics
*InteractiveStatisticsApi* | [**increase_statistic**](docs/InteractiveStatisticsApi.md#increase_statistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType} | Increase Statistics
*InteractiveStatisticsApi* | [**list_agents_by_statistic**](docs/InteractiveStatisticsApi.md#list_agents_by_statistic) | **GET** /api/v1/stats/agents/by/{statsType} | List Agents by Statistics
*InteractiveStatisticsApi* | [**list_agents_by_statistic1**](docs/InteractiveStatisticsApi.md#list_agents_by_statistic1) | **GET** /api/v1/stats/agents/by/{statsType}/{pageSize} | List Agents by Statistics
*InteractiveStatisticsApi* | [**list_agents_by_statistic2**](docs/InteractiveStatisticsApi.md#list_agents_by_statistic2) | **GET** /api/v1/stats/agents/by/{statsType}/{pageSize}/{pageNum} | List Agents by Statistics
*InteractiveStatisticsApi* | [**list_characters_by_statistic**](docs/InteractiveStatisticsApi.md#list_characters_by_statistic) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics
*InteractiveStatisticsApi* | [**list_characters_by_statistic1**](docs/InteractiveStatisticsApi.md#list_characters_by_statistic1) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics
*InteractiveStatisticsApi* | [**list_characters_by_statistic2**](docs/InteractiveStatisticsApi.md#list_characters_by_statistic2) | **GET** /api/v1/stats/characters/by/{statsType} | List Characters by Statistics
*InteractiveStatisticsApi* | [**list_hot_tags**](docs/InteractiveStatisticsApi.md#list_hot_tags) | **GET** /api/v1/tags/hot/{infoType}/{pageSize} | Hot Tags
*InteractiveStatisticsApi* | [**list_plugins_by_statistic**](docs/InteractiveStatisticsApi.md#list_plugins_by_statistic) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**list_plugins_by_statistic1**](docs/InteractiveStatisticsApi.md#list_plugins_by_statistic1) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**list_plugins_by_statistic2**](docs/InteractiveStatisticsApi.md#list_plugins_by_statistic2) | **GET** /api/v1/stats/plugins/by/{statsType} | List Plugins by Statistics
*InteractiveStatisticsApi* | [**list_prompts_by_statistic**](docs/InteractiveStatisticsApi.md#list_prompts_by_statistic) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics
*InteractiveStatisticsApi* | [**list_prompts_by_statistic1**](docs/InteractiveStatisticsApi.md#list_prompts_by_statistic1) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics
*InteractiveStatisticsApi* | [**list_prompts_by_statistic2**](docs/InteractiveStatisticsApi.md#list_prompts_by_statistic2) | **GET** /api/v1/stats/prompts/by/{statsType} | List Prompts by Statistics
*OrganizationApi* | [**get_owners**](docs/OrganizationApi.md#get_owners) | **GET** /api/v1/org/owners | Get My Superior Relationship
*OrganizationApi* | [**get_owners_dot**](docs/OrganizationApi.md#get_owners_dot) | **GET** /api/v1/org/owners/dot | Get DOT of Superior Relationship
*OrganizationApi* | [**get_subordinate_owners**](docs/OrganizationApi.md#get_subordinate_owners) | **GET** /api/v1/org/manage/{username}/owners | Get Superior Relationship
*OrganizationApi* | [**get_subordinate_subordinates**](docs/OrganizationApi.md#get_subordinate_subordinates) | **GET** /api/v1/org/manage/{username}/subordinates | Get Subordinate Relationship
*OrganizationApi* | [**get_subordinates**](docs/OrganizationApi.md#get_subordinates) | **GET** /api/v1/org/subordinates | Get My Subordinate Relationship
*OrganizationApi* | [**get_subordinates_dot**](docs/OrganizationApi.md#get_subordinates_dot) | **GET** /api/v1/org/subordinates/dot | Get DOT of Subordinate Relationship
*OrganizationApi* | [**list_subordinate_authorities**](docs/OrganizationApi.md#list_subordinate_authorities) | **GET** /api/v1/org/authority/{username} | List Subordinate Permissions
*OrganizationApi* | [**remove_subordinate_subordinates_tree**](docs/OrganizationApi.md#remove_subordinate_subordinates_tree) | **DELETE** /api/v1/org/manage/{username}/subordinates | Clear Subordinate Relationship
*OrganizationApi* | [**update_subordinate_authorities**](docs/OrganizationApi.md#update_subordinate_authorities) | **PUT** /api/v1/org/authority/{username} | Update Subordinate Permissions
*OrganizationApi* | [**update_subordinate_owners**](docs/OrganizationApi.md#update_subordinate_owners) | **PUT** /api/v1/org/manage/{username}/owners | Update Superior Relationship
*OrganizationApi* | [**update_subordinate_subordinates**](docs/OrganizationApi.md#update_subordinate_subordinates) | **PUT** /api/v1/org/manage/{username}/subordinates | Update Subordinate Relationship
*PluginApi* | [**batch_search_plugin_details**](docs/PluginApi.md#batch_search_plugin_details) | **POST** /api/v1/plugin/batch/details/search | Batch Search Plugin Details
*PluginApi* | [**batch_search_plugin_summary**](docs/PluginApi.md#batch_search_plugin_summary) | **POST** /api/v1/plugin/batch/search | Batch Search Plugin Summaries
*PluginApi* | [**count_plugins**](docs/PluginApi.md#count_plugins) | **POST** /api/v1/plugin/count | Calculate Number of Plugins
*PluginApi* | [**create_plugin**](docs/PluginApi.md#create_plugin) | **POST** /api/v1/plugin | Create Plugin
*PluginApi* | [**create_plugins**](docs/PluginApi.md#create_plugins) | **POST** /api/v1/plugin/batch | Batch Create Plugins
*PluginApi* | [**delete_plugin**](docs/PluginApi.md#delete_plugin) | **DELETE** /api/v1/plugin/{pluginId} | Delete Plugin
*PluginApi* | [**delete_plugins**](docs/PluginApi.md#delete_plugins) | **DELETE** /api/v1/plugin/batch | Batch Delete Plugins
*PluginApi* | [**get_plugin_details**](docs/PluginApi.md#get_plugin_details) | **GET** /api/v1/plugin/details/{pluginId} | Get Plugin Details
*PluginApi* | [**get_plugin_summary**](docs/PluginApi.md#get_plugin_summary) | **GET** /api/v1/plugin/summary/{pluginId} | Get Plugin Summary
*PluginApi* | [**refresh_plugin_info**](docs/PluginApi.md#refresh_plugin_info) | **PUT** /api/v1/plugin/refresh/{pluginId} | Refresh Plugin Information
*PluginApi* | [**search_plugin_details**](docs/PluginApi.md#search_plugin_details) | **POST** /api/v1/plugin/details/search | Search Plugin Details
*PluginApi* | [**search_plugin_summary**](docs/PluginApi.md#search_plugin_summary) | **POST** /api/v1/plugin/search | Search Plugin Summary
*PluginApi* | [**update_plugin**](docs/PluginApi.md#update_plugin) | **PUT** /api/v1/plugin/{pluginId} | Update Plugin
*PromptApi* | [**apply_prompt_ref**](docs/PromptApi.md#apply_prompt_ref) | **POST** /api/v1/prompt/apply/ref | Apply Parameters to Prompt Record
*PromptApi* | [**apply_prompt_template**](docs/PromptApi.md#apply_prompt_template) | **POST** /api/v1/prompt/apply/template | Apply Parameters to Prompt Template
*PromptApi* | [**batch_search_prompt_details**](docs/PromptApi.md#batch_search_prompt_details) | **POST** /api/v1/prompt/batch/details/search | Batch Search Prompt Details
*PromptApi* | [**batch_search_prompt_summary**](docs/PromptApi.md#batch_search_prompt_summary) | **POST** /api/v1/prompt/batch/search | Batch Search Prompt Summaries
*PromptApi* | [**clone_prompt**](docs/PromptApi.md#clone_prompt) | **POST** /api/v1/prompt/clone/{promptId} | Clone Prompt
*PromptApi* | [**clone_prompts**](docs/PromptApi.md#clone_prompts) | **POST** /api/v1/prompt/batch/clone | Batch Clone Prompts
*PromptApi* | [**count_prompts**](docs/PromptApi.md#count_prompts) | **POST** /api/v1/prompt/count | Calculate Number of Prompts
*PromptApi* | [**create_prompt**](docs/PromptApi.md#create_prompt) | **POST** /api/v1/prompt | Create Prompt
*PromptApi* | [**create_prompts**](docs/PromptApi.md#create_prompts) | **POST** /api/v1/prompt/batch | Batch Create Prompts
*PromptApi* | [**delete_prompt**](docs/PromptApi.md#delete_prompt) | **DELETE** /api/v1/prompt/{promptId} | Delete Prompt
*PromptApi* | [**delete_prompt_by_name**](docs/PromptApi.md#delete_prompt_by_name) | **DELETE** /api/v1/prompt/name/{name} | Delete Prompt by Name
*PromptApi* | [**delete_prompts**](docs/PromptApi.md#delete_prompts) | **DELETE** /api/v1/prompt/batch | Batch Delete Prompts
*PromptApi* | [**exists_prompt_name**](docs/PromptApi.md#exists_prompt_name) | **GET** /api/v1/prompt/exists/name/{name} | Check If Prompt Name Exists
*PromptApi* | [**get_prompt_details**](docs/PromptApi.md#get_prompt_details) | **GET** /api/v1/prompt/details/{promptId} | Get Prompt Details
*PromptApi* | [**get_prompt_summary**](docs/PromptApi.md#get_prompt_summary) | **GET** /api/v1/prompt/summary/{promptId} | Get Prompt Summary
*PromptApi* | [**list_prompt_versions_by_name**](docs/PromptApi.md#list_prompt_versions_by_name) | **POST** /api/v1/prompt/versions/{name} | List Versions by Prompt Name
*PromptApi* | [**new_prompt_name**](docs/PromptApi.md#new_prompt_name) | **GET** /api/v1/prompt/create/name/{desired} | Create New Prompt Name
*PromptApi* | [**publish_prompt**](docs/PromptApi.md#publish_prompt) | **POST** /api/v1/prompt/publish/{promptId}/{visibility} | Publish Prompt
*PromptApi* | [**search_prompt_details**](docs/PromptApi.md#search_prompt_details) | **POST** /api/v1/prompt/details/search | Search Prompt Details
*PromptApi* | [**search_prompt_summary**](docs/PromptApi.md#search_prompt_summary) | **POST** /api/v1/prompt/search | Search Prompt Summary
*PromptApi* | [**send_prompt**](docs/PromptApi.md#send_prompt) | **POST** /api/v1/prompt/send | Send Prompt
*PromptApi* | [**stream_send_prompt**](docs/PromptApi.md#stream_send_prompt) | **POST** /api/v1/prompt/send/stream | Send Prompt by Streaming Back
*PromptApi* | [**update_prompt**](docs/PromptApi.md#update_prompt) | **PUT** /api/v1/prompt/{promptId} | Update Prompt
*PromptTaskApi* | [**create_prompt_task**](docs/PromptTaskApi.md#create_prompt_task) | **POST** /api/v1/prompt/task | Create Prompt Task
*PromptTaskApi* | [**delete_prompt_task**](docs/PromptTaskApi.md#delete_prompt_task) | **DELETE** /api/v1/prompt/task/{promptTaskId} | Delete Prompt Task
*PromptTaskApi* | [**get_prompt_task**](docs/PromptTaskApi.md#get_prompt_task) | **GET** /api/v1/prompt/task/{promptTaskId} | Get Prompt Task
*PromptTaskApi* | [**update_prompt_task**](docs/PromptTaskApi.md#update_prompt_task) | **PUT** /api/v1/prompt/task/{promptTaskId} | Update Prompt Task
*RagApi* | [**cancel_rag_task**](docs/RagApi.md#cancel_rag_task) | **POST** /api/v1/rag/task/cancel/{taskId} | Cancel RAG Task
*RagApi* | [**create_rag_task**](docs/RagApi.md#create_rag_task) | **POST** /api/v1/rag/task/{characterId} | Create RAG Task
*RagApi* | [**delete_rag_task**](docs/RagApi.md#delete_rag_task) | **DELETE** /api/v1/rag/task/{taskId} | Delete RAG Task
*RagApi* | [**get_rag_task**](docs/RagApi.md#get_rag_task) | **GET** /api/v1/rag/task/{taskId} | Get RAG Task
*RagApi* | [**get_rag_task_status**](docs/RagApi.md#get_rag_task_status) | **GET** /api/v1/rag/task/status/{taskId} | Get RAG Task Status
*RagApi* | [**list_rag_tasks**](docs/RagApi.md#list_rag_tasks) | **GET** /api/v1/rag/tasks/{characterId} | List RAG Tasks
*RagApi* | [**start_rag_task**](docs/RagApi.md#start_rag_task) | **POST** /api/v1/rag/task/start/{taskId} | Start RAG Task
*RagApi* | [**update_rag_task**](docs/RagApi.md#update_rag_task) | **PUT** /api/v1/rag/task/{taskId} | Update RAG Task


## Documentation For Models

 - [AgentCreateDTO](docs/AgentCreateDTO.md)
 - [AgentDetailsDTO](docs/AgentDetailsDTO.md)
 - [AgentItemForNameDTO](docs/AgentItemForNameDTO.md)
 - [AgentQueryDTO](docs/AgentQueryDTO.md)
 - [AgentQueryWhere](docs/AgentQueryWhere.md)
 - [AgentSummaryDTO](docs/AgentSummaryDTO.md)
 - [AgentSummaryStatsDTO](docs/AgentSummaryStatsDTO.md)
 - [AgentUpdateDTO](docs/AgentUpdateDTO.md)
 - [AiApiKeyCreateDTO](docs/AiApiKeyCreateDTO.md)
 - [AiApiKeyInfoDTO](docs/AiApiKeyInfoDTO.md)
 - [AiModelInfoDTO](docs/AiModelInfoDTO.md)
 - [ApiTokenInfoDTO](docs/ApiTokenInfoDTO.md)
 - [AppConfigInfoDTO](docs/AppConfigInfoDTO.md)
 - [AppMetaDTO](docs/AppMetaDTO.md)
 - [CharacterBackendDTO](docs/CharacterBackendDTO.md)
 - [CharacterBackendDetailsDTO](docs/CharacterBackendDetailsDTO.md)
 - [CharacterCreateDTO](docs/CharacterCreateDTO.md)
 - [CharacterDetailsDTO](docs/CharacterDetailsDTO.md)
 - [CharacterItemForNameDTO](docs/CharacterItemForNameDTO.md)
 - [CharacterQueryDTO](docs/CharacterQueryDTO.md)
 - [CharacterQueryWhere](docs/CharacterQueryWhere.md)
 - [CharacterSummaryDTO](docs/CharacterSummaryDTO.md)
 - [CharacterSummaryStatsDTO](docs/CharacterSummaryStatsDTO.md)
 - [CharacterUpdateDTO](docs/CharacterUpdateDTO.md)
 - [ChatContentDTO](docs/ChatContentDTO.md)
 - [ChatContextDTO](docs/ChatContextDTO.md)
 - [ChatCreateDTO](docs/ChatCreateDTO.md)
 - [ChatMessageDTO](docs/ChatMessageDTO.md)
 - [ChatMessageRecordDTO](docs/ChatMessageRecordDTO.md)
 - [ChatPromptContentDTO](docs/ChatPromptContentDTO.md)
 - [ChatSessionDTO](docs/ChatSessionDTO.md)
 - [ChatToolCallDTO](docs/ChatToolCallDTO.md)
 - [ChatUpdateDTO](docs/ChatUpdateDTO.md)
 - [HotTagDTO](docs/HotTagDTO.md)
 - [InteractiveStatsDTO](docs/InteractiveStatsDTO.md)
 - [LlmResultDTO](docs/LlmResultDTO.md)
 - [MemoryUsageDTO](docs/MemoryUsageDTO.md)
 - [OpenAiParamDTO](docs/OpenAiParamDTO.md)
 - [PluginCreateDTO](docs/PluginCreateDTO.md)
 - [PluginDetailsDTO](docs/PluginDetailsDTO.md)
 - [PluginQueryDTO](docs/PluginQueryDTO.md)
 - [PluginQueryWhere](docs/PluginQueryWhere.md)
 - [PluginSummaryDTO](docs/PluginSummaryDTO.md)
 - [PluginSummaryStatsDTO](docs/PluginSummaryStatsDTO.md)
 - [PluginUpdateDTO](docs/PluginUpdateDTO.md)
 - [PromptAiParamDTO](docs/PromptAiParamDTO.md)
 - [PromptCreateDTO](docs/PromptCreateDTO.md)
 - [PromptDetailsDTO](docs/PromptDetailsDTO.md)
 - [PromptItemForNameDTO](docs/PromptItemForNameDTO.md)
 - [PromptQueryDTO](docs/PromptQueryDTO.md)
 - [PromptQueryWhere](docs/PromptQueryWhere.md)
 - [PromptRefDTO](docs/PromptRefDTO.md)
 - [PromptSummaryDTO](docs/PromptSummaryDTO.md)
 - [PromptSummaryStatsDTO](docs/PromptSummaryStatsDTO.md)
 - [PromptTaskDTO](docs/PromptTaskDTO.md)
 - [PromptTaskDetailsDTO](docs/PromptTaskDetailsDTO.md)
 - [PromptTemplateDTO](docs/PromptTemplateDTO.md)
 - [PromptUpdateDTO](docs/PromptUpdateDTO.md)
 - [QwenParamDTO](docs/QwenParamDTO.md)
 - [RagTaskDTO](docs/RagTaskDTO.md)
 - [RagTaskDetailsDTO](docs/RagTaskDetailsDTO.md)
 - [SseEmitter](docs/SseEmitter.md)
 - [TokenUsageDTO](docs/TokenUsageDTO.md)
 - [UserBasicInfoDTO](docs/UserBasicInfoDTO.md)
 - [UserDetailsDTO](docs/UserDetailsDTO.md)
 - [UserFullDetailsDTO](docs/UserFullDetailsDTO.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="bearerAuth"></a>
### bearerAuth

- **Type**: Bearer authentication


## Author




