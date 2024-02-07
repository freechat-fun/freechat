# freechat_sdk.AgentApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batch_search_agent_details**](AgentApi.md#batch_search_agent_details) | **POST** /api/v1/agent/batch/details/search | Batch Search Agent Details
[**batch_search_agent_summary**](AgentApi.md#batch_search_agent_summary) | **POST** /api/v1/agent/batch/search | Batch Search Agent Summaries
[**clone_agent**](AgentApi.md#clone_agent) | **POST** /api/v1/agent/clone/{agentId} | Clone Agent
[**clone_agents**](AgentApi.md#clone_agents) | **POST** /api/v1/agent/batch/clone | Batch Clone Agents
[**count_agents**](AgentApi.md#count_agents) | **POST** /api/v1/agent/count | Calculate Number of Agents
[**create_agent**](AgentApi.md#create_agent) | **POST** /api/v1/agent | Create Agent
[**create_agents**](AgentApi.md#create_agents) | **POST** /api/v1/agent/batch | Batch Create Agents
[**delete_agent**](AgentApi.md#delete_agent) | **DELETE** /api/v1/agent/{agentId} | Delete Agent
[**delete_agents**](AgentApi.md#delete_agents) | **DELETE** /api/v1/agent/batch/delete | Batch Delete Agents
[**get_agent_details**](AgentApi.md#get_agent_details) | **GET** /api/v1/agent/details/{agentId} | Get Agent Details
[**get_agent_summary**](AgentApi.md#get_agent_summary) | **GET** /api/v1/agent/summary/{agentId} | Get Agent Summary
[**list_agent_versions_by_name**](AgentApi.md#list_agent_versions_by_name) | **POST** /api/v1/agent/versions/{name} | List Versions by Agent Name
[**publish_agent**](AgentApi.md#publish_agent) | **POST** /api/v1/agent/publish/{agentId}/{visibility} | Publish Agent
[**search_agent_details**](AgentApi.md#search_agent_details) | **POST** /api/v1/agent/details/search | Search Agent Details
[**search_agent_summary**](AgentApi.md#search_agent_summary) | **POST** /api/v1/agent/search | Search Agent Summary
[**update_agent**](AgentApi.md#update_agent) | **PUT** /api/v1/agent/{agentId} | Update Agent


# **batch_search_agent_details**
> List[List[AgentDetailsDTO]] batch_search_agent_details(agent_query_dto)

Batch Search Agent Details

Batch call shortcut for /api/v1/agent/details/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[AgentQueryDTO] | Query conditions

    try:
        # Batch Search Agent Details
        api_response = api_instance.batch_search_agent_details(agent_query_dto)
        print("The response of AgentApi->batch_search_agent_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->batch_search_agent_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_query_dto** | [**List[AgentQueryDTO]**](AgentQueryDTO.md)| Query conditions | 

### Return type

**List[List[AgentDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **batch_search_agent_summary**
> List[List[AgentSummaryDTO]] batch_search_agent_summary(agent_query_dto)

Batch Search Agent Summaries

Batch call shortcut for /api/v1/agent/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[AgentQueryDTO] | Query conditions

    try:
        # Batch Search Agent Summaries
        api_response = api_instance.batch_search_agent_summary(agent_query_dto)
        print("The response of AgentApi->batch_search_agent_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->batch_search_agent_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_query_dto** | [**List[AgentQueryDTO]**](AgentQueryDTO.md)| Query conditions | 

### Return type

**List[List[AgentSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **clone_agent**
> str clone_agent(agent_id)

Clone Agent

Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | The referenced agentId

    try:
        # Clone Agent
        api_response = api_instance.clone_agent(agent_id)
        print("The response of AgentApi->clone_agent:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->clone_agent: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| The referenced agentId | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **clone_agents**
> List[str] clone_agents(request_body)

Batch Clone Agents

Batch clone multiple agents. Ensure transactionality, return the agentId list after success.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
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
    api_instance = freechat_sdk.AgentApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of agent information to be created

    try:
        # Batch Clone Agents
        api_response = api_instance.clone_agents(request_body)
        print("The response of AgentApi->clone_agents:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->clone_agents: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of agent information to be created | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **count_agents**
> int count_agents(agent_query_dto)

Calculate Number of Agents

Calculate the number of agents according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_query_dto = freechat_sdk.AgentQueryDTO() # AgentQueryDTO | Query conditions

    try:
        # Calculate Number of Agents
        api_response = api_instance.count_agents(agent_query_dto)
        print("The response of AgentApi->count_agents:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->count_agents: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_query_dto** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_agent**
> str create_agent(agent_create_dto)

Create Agent

Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_create_dto import AgentCreateDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_create_dto = {"name":"A Test Agent","description":"A agent description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test","demo"],"aiModels":["123"]} # AgentCreateDTO | Information of the agent to be created

    try:
        # Create Agent
        api_response = api_instance.create_agent(agent_create_dto)
        print("The response of AgentApi->create_agent:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->create_agent: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_create_dto** | [**AgentCreateDTO**](AgentCreateDTO.md)| Information of the agent to be created | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_agents**
> List[str] create_agents(agent_create_dto)

Batch Create Agents

Batch create multiple agents. Ensure transactionality, return the agentId list after success.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_create_dto import AgentCreateDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_create_dto = [{"name":"First Test Agent","description":"First agent description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test1","demo1"],"aiModels":["123"]},{"name":"Second Test Agent","visibility":"public","description":"Second agent description","format":"langflow","config":"{}","parameters":"{\"robot\": null}","tags":["test2","demo2"],"aiModels":["123","456"]}] # List[AgentCreateDTO] | List of agent information to be created

    try:
        # Batch Create Agents
        api_response = api_instance.create_agents(agent_create_dto)
        print("The response of AgentApi->create_agents:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->create_agents: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_create_dto** | [**List[AgentCreateDTO]**](AgentCreateDTO.md)| List of agent information to be created | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_agent**
> bool delete_agent(agent_id)

Delete Agent

Delete agent. Return success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | AgentId to be deleted

    try:
        # Delete Agent
        api_response = api_instance.delete_agent(agent_id)
        print("The response of AgentApi->delete_agent:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->delete_agent: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| AgentId to be deleted | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_agents**
> List[str] delete_agents(request_body)

Batch Delete Agents

Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
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
    api_instance = freechat_sdk.AgentApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of agentId to be deleted

    try:
        # Batch Delete Agents
        api_response = api_instance.delete_agents(request_body)
        print("The response of AgentApi->delete_agents:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->delete_agents: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of agentId to be deleted | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_agent_details**
> AgentDetailsDTO get_agent_details(agent_id)

Get Agent Details

Get agent detailed information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | AgentId to be obtained

    try:
        # Get Agent Details
        api_response = api_instance.get_agent_details(agent_id)
        print("The response of AgentApi->get_agent_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->get_agent_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| AgentId to be obtained | 

### Return type

[**AgentDetailsDTO**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_agent_summary**
> AgentSummaryDTO get_agent_summary(agent_id)

Get Agent Summary

Get agent summary information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | agentId to be obtained

    try:
        # Get Agent Summary
        api_response = api_instance.get_agent_summary(agent_id)
        print("The response of AgentApi->get_agent_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->get_agent_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| agentId to be obtained | 

### Return type

[**AgentSummaryDTO**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_agent_versions_by_name**
> List[AgentItemForNameDTO] list_agent_versions_by_name(name)

List Versions by Agent Name

List the versions and corresponding agentIds by agent name.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    name = 'name_example' # str | Agent name

    try:
        # List Versions by Agent Name
        api_response = api_instance.list_agent_versions_by_name(name)
        print("The response of AgentApi->list_agent_versions_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->list_agent_versions_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Agent name | 

### Return type

[**List[AgentItemForNameDTO]**](AgentItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **publish_agent**
> str publish_agent(agent_id, visibility)

Publish Agent

Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | The agentId to be published
    visibility = 'visibility_example' # str | Visibility: public | private | ...

    try:
        # Publish Agent
        api_response = api_instance.publish_agent(agent_id, visibility)
        print("The response of AgentApi->publish_agent:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->publish_agent: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| The agentId to be published | 
 **visibility** | **str**| Visibility: public | private | ... | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_agent_details**
> List[AgentDetailsDTO] search_agent_details(agent_query_dto)

Search Agent Details

Same as /api/v1/agent/search, but returns detailed information of the agent.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_query_dto = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # AgentQueryDTO | Query conditions

    try:
        # Search Agent Details
        api_response = api_instance.search_agent_details(agent_query_dto)
        print("The response of AgentApi->search_agent_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->search_agent_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_query_dto** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | 

### Return type

[**List[AgentDetailsDTO]**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_agent_summary**
> List[AgentSummaryDTO] search_agent_summary(agent_query_dto)

Search Agent Summary

Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_query_dto import AgentQueryDTO
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_query_dto = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # AgentQueryDTO | Query conditions

    try:
        # Search Agent Summary
        api_response = api_instance.search_agent_summary(agent_query_dto)
        print("The response of AgentApi->search_agent_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->search_agent_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_query_dto** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | 

### Return type

[**List[AgentSummaryDTO]**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_agent**
> bool update_agent(agent_id, agent_update_dto)

Update Agent

Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.agent_update_dto import AgentUpdateDTO
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
    api_instance = freechat_sdk.AgentApi(api_client)
    agent_id = 'agent_id_example' # str | AgentId to be updated
    agent_update_dto = {"version":2,"name":"Second Test Agent (New)","visibility":"public","description":"Second agent description (new)","config":"{}","inputs":"{\"robot\": null}","tags":["test2","demo2","robot"],"aiModels":["123","456"]} # AgentUpdateDTO | Agent information to be updated

    try:
        # Update Agent
        api_response = api_instance.update_agent(agent_id, agent_update_dto)
        print("The response of AgentApi->update_agent:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AgentApi->update_agent: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agent_id** | **str**| AgentId to be updated | 
 **agent_update_dto** | [**AgentUpdateDTO**](AgentUpdateDTO.md)| Agent information to be updated | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

