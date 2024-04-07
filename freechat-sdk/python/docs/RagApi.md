# freechat_sdk.RagApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancel_rag_task**](RagApi.md#cancel_rag_task) | **POST** /api/v1/rag/task/cancel/{taskId} | Cancel RAG Task
[**create_rag_task**](RagApi.md#create_rag_task) | **POST** /api/v1/rag/task/{characterId} | Create RAG Task
[**delete_rag_task**](RagApi.md#delete_rag_task) | **DELETE** /api/v1/rag/task/{taskId} | Delete RAG Task
[**get_rag_task**](RagApi.md#get_rag_task) | **GET** /api/v1/rag/task/{taskId} | Get RAG Task
[**get_rag_task_status**](RagApi.md#get_rag_task_status) | **GET** /api/v1/rag/task/status/{taskId} | Get RAG Task Status
[**list_rag_tasks**](RagApi.md#list_rag_tasks) | **GET** /api/v1/rag/tasks/{characterId} | List RAG Tasks
[**start_rag_task**](RagApi.md#start_rag_task) | **POST** /api/v1/rag/task/start/{taskId} | Start RAG Task
[**update_rag_task**](RagApi.md#update_rag_task) | **PUT** /api/v1/rag/task/{taskId} | Update RAG Task


# **cancel_rag_task**
> bool cancel_rag_task(task_id)

Cancel RAG Task

Cancel a RAG task.

### Example

* Bearer Authentication (bearerAuth):

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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be canceled

    try:
        # Cancel RAG Task
        api_response = api_instance.cancel_rag_task(task_id)
        print("The response of RagApi->cancel_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->cancel_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be canceled | 

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

# **create_rag_task**
> int create_rag_task(character_id, rag_task_dto)

Create RAG Task

Create a RAG task.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.rag_task_dto import RagTaskDTO
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
    api_instance = freechat_sdk.RagApi(api_client)
    character_id = 56 # int | The characterId to be added a RAG task
    rag_task_dto = freechat_sdk.RagTaskDTO() # RagTaskDTO | The RAG task to be added

    try:
        # Create RAG Task
        api_response = api_instance.create_rag_task(character_id, rag_task_dto)
        print("The response of RagApi->create_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->create_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be added a RAG task | 
 **rag_task_dto** | [**RagTaskDTO**](RagTaskDTO.md)| The RAG task to be added | 

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

# **delete_rag_task**
> bool delete_rag_task(task_id)

Delete RAG Task

Delete a RAG task.

### Example

* Bearer Authentication (bearerAuth):

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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be deleted

    try:
        # Delete RAG Task
        api_response = api_instance.delete_rag_task(task_id)
        print("The response of RagApi->delete_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->delete_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be deleted | 

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

# **get_rag_task**
> RagTaskDetailsDTO get_rag_task(task_id)

Get RAG Task

Get the RAG task details.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.rag_task_details_dto import RagTaskDetailsDTO
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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be queried

    try:
        # Get RAG Task
        api_response = api_instance.get_rag_task(task_id)
        print("The response of RagApi->get_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->get_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be queried | 

### Return type

[**RagTaskDetailsDTO**](RagTaskDetailsDTO.md)

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

# **get_rag_task_status**
> str get_rag_task_status(task_id)

Get RAG Task Status

Get the RAG task execution status: pending | running | succeeded | failed | canceled.

### Example

* Bearer Authentication (bearerAuth):

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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be queried status

    try:
        # Get RAG Task Status
        api_response = api_instance.get_rag_task_status(task_id)
        print("The response of RagApi->get_rag_task_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->get_rag_task_status: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be queried status | 

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

# **list_rag_tasks**
> List[RagTaskDetailsDTO] list_rag_tasks(character_id)

List RAG Tasks

List the RAG tasks by characterId.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.rag_task_details_dto import RagTaskDetailsDTO
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
    api_instance = freechat_sdk.RagApi(api_client)
    character_id = 56 # int | The characterId to be queried

    try:
        # List RAG Tasks
        api_response = api_instance.list_rag_tasks(character_id)
        print("The response of RagApi->list_rag_tasks:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->list_rag_tasks: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be queried | 

### Return type

[**List[RagTaskDetailsDTO]**](RagTaskDetailsDTO.md)

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

# **start_rag_task**
> bool start_rag_task(task_id)

Start RAG Task

Start a RAG task.

### Example

* Bearer Authentication (bearerAuth):

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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be started

    try:
        # Start RAG Task
        api_response = api_instance.start_rag_task(task_id)
        print("The response of RagApi->start_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->start_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be started | 

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

# **update_rag_task**
> bool update_rag_task(task_id, rag_task_dto)

Update RAG Task

Update a RAG task.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.rag_task_dto import RagTaskDTO
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
    api_instance = freechat_sdk.RagApi(api_client)
    task_id = 56 # int | The taskId to be updated
    rag_task_dto = freechat_sdk.RagTaskDTO() # RagTaskDTO | The prompt task info to be updated

    try:
        # Update RAG Task
        api_response = api_instance.update_rag_task(task_id, rag_task_dto)
        print("The response of RagApi->update_rag_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling RagApi->update_rag_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **task_id** | **int**| The taskId to be updated | 
 **rag_task_dto** | [**RagTaskDTO**](RagTaskDTO.md)| The prompt task info to be updated | 

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

