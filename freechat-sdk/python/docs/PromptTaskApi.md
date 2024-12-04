# freechat_sdk.PromptTaskApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_prompt_task**](PromptTaskApi.md#create_prompt_task) | **POST** /api/v2/prompt/task | Create Prompt Task
[**delete_prompt_task**](PromptTaskApi.md#delete_prompt_task) | **DELETE** /api/v2/prompt/task/{promptTaskId} | Delete Prompt Task
[**get_prompt_task**](PromptTaskApi.md#get_prompt_task) | **GET** /api/v2/prompt/task/{promptTaskId} | Get Prompt Task
[**update_prompt_task**](PromptTaskApi.md#update_prompt_task) | **PUT** /api/v2/prompt/task/{promptTaskId} | Update Prompt Task


# **create_prompt_task**
> str create_prompt_task(prompt_task_dto)

Create Prompt Task

Create a prompt task.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO
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
    api_instance = freechat_sdk.PromptTaskApi(api_client)
    prompt_task_dto = freechat_sdk.PromptTaskDTO() # PromptTaskDTO | The prompt task to be added

    try:
        # Create Prompt Task
        api_response = api_instance.create_prompt_task(prompt_task_dto)
        print("The response of PromptTaskApi->create_prompt_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptTaskApi->create_prompt_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_task_dto** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task to be added | 

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

# **delete_prompt_task**
> bool delete_prompt_task(prompt_task_id)

Delete Prompt Task

Delete a prompt task.

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
    api_instance = freechat_sdk.PromptTaskApi(api_client)
    prompt_task_id = 'prompt_task_id_example' # str | The promptTaskId to be deleted

    try:
        # Delete Prompt Task
        api_response = api_instance.delete_prompt_task(prompt_task_id)
        print("The response of PromptTaskApi->delete_prompt_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptTaskApi->delete_prompt_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_task_id** | **str**| The promptTaskId to be deleted | 

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

# **get_prompt_task**
> PromptTaskDetailsDTO get_prompt_task(prompt_task_id)

Get Prompt Task

Get the prompt task details.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO
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
    api_instance = freechat_sdk.PromptTaskApi(api_client)
    prompt_task_id = 'prompt_task_id_example' # str | The promptTaskId to be queried

    try:
        # Get Prompt Task
        api_response = api_instance.get_prompt_task(prompt_task_id)
        print("The response of PromptTaskApi->get_prompt_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptTaskApi->get_prompt_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_task_id** | **str**| The promptTaskId to be queried | 

### Return type

[**PromptTaskDetailsDTO**](PromptTaskDetailsDTO.md)

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

# **update_prompt_task**
> bool update_prompt_task(prompt_task_id, prompt_task_dto)

Update Prompt Task

Update a prompt task.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO
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
    api_instance = freechat_sdk.PromptTaskApi(api_client)
    prompt_task_id = 'prompt_task_id_example' # str | The promptTaskId to be updated
    prompt_task_dto = freechat_sdk.PromptTaskDTO() # PromptTaskDTO | The prompt task info to be updated

    try:
        # Update Prompt Task
        api_response = api_instance.update_prompt_task(prompt_task_id, prompt_task_dto)
        print("The response of PromptTaskApi->update_prompt_task:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PromptTaskApi->update_prompt_task: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt_task_id** | **str**| The promptTaskId to be updated | 
 **prompt_task_dto** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task info to be updated | 

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

