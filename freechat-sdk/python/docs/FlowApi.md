# freechat-sdk.FlowApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batch_search_flow_details**](FlowApi.md#batch_search_flow_details) | **POST** /api/v1/flow/batch/details/search | Batch Search Flow Details
[**batch_search_flow_summary**](FlowApi.md#batch_search_flow_summary) | **POST** /api/v1/flow/batch/search | Batch Search Flow Summaries
[**clone_flow**](FlowApi.md#clone_flow) | **POST** /api/v1/flow/clone/{flowId} | Clone Flow
[**clone_flows**](FlowApi.md#clone_flows) | **POST** /api/v1/flow/batch/clone | Batch Clone Flows
[**count_flows**](FlowApi.md#count_flows) | **POST** /api/v1/flow/count | Calculate Number of Flows
[**create_flow**](FlowApi.md#create_flow) | **POST** /api/v1/flow | Create Flow
[**create_flows**](FlowApi.md#create_flows) | **POST** /api/v1/flow/batch | Batch Create Flows
[**delete_flow**](FlowApi.md#delete_flow) | **DELETE** /api/v1/flow/{flowId} | Delete Flow
[**delete_flows**](FlowApi.md#delete_flows) | **DELETE** /api/v1/flow/batch/delete | Batch Delete Flows
[**get_flow_details**](FlowApi.md#get_flow_details) | **GET** /api/v1/flow/details/{flowId} | Get Flow Details
[**get_flow_summary**](FlowApi.md#get_flow_summary) | **GET** /api/v1/flow/summary/{flowId} | Get Flow Summary
[**list_flow_versions_by_name**](FlowApi.md#list_flow_versions_by_name) | **POST** /api/v1/flow/versions/{name} | List Versions by Flow Name
[**publish_flow**](FlowApi.md#publish_flow) | **POST** /api/v1/flow/publish/{flowId}/{visibility} | Publish Flow
[**search_flow_details**](FlowApi.md#search_flow_details) | **POST** /api/v1/flow/details/search | Search Flow Details
[**search_flow_summary**](FlowApi.md#search_flow_summary) | **POST** /api/v1/flow/search | Search Flow Summary
[**update_flow**](FlowApi.md#update_flow) | **PUT** /api/v1/flow/{flowId} | Update Flow


# **batch_search_flow_details**
> List[List[FlowDetailsDTO]] batch_search_flow_details(flow_query_dto)

Batch Search Flow Details

Batch call shortcut for /api/v1/flow/details/search.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_details_dto import FlowDetailsDTO
from freechat-sdk.models.flow_query_dto import FlowQueryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[FlowQueryDTO] | Query conditions

    try:
        # Batch Search Flow Details
        api_response = api_instance.batch_search_flow_details(flow_query_dto)
        print("The response of FlowApi->batch_search_flow_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->batch_search_flow_details: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_query_dto** | [**List[FlowQueryDTO]**](FlowQueryDTO.md)| Query conditions | 

### Return type

**List[List[FlowDetailsDTO]]**

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

# **batch_search_flow_summary**
> List[List[FlowSummaryDTO]] batch_search_flow_summary(flow_query_dto)

Batch Search Flow Summaries

Batch call shortcut for /api/v1/flow/search.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_query_dto import FlowQueryDTO
from freechat-sdk.models.flow_summary_dto import FlowSummaryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[FlowQueryDTO] | Query conditions

    try:
        # Batch Search Flow Summaries
        api_response = api_instance.batch_search_flow_summary(flow_query_dto)
        print("The response of FlowApi->batch_search_flow_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->batch_search_flow_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_query_dto** | [**List[FlowQueryDTO]**](FlowQueryDTO.md)| Query conditions | 

### Return type

**List[List[FlowSummaryDTO]]**

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

# **clone_flow**
> str clone_flow(flow_id)

Clone Flow

Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | The referenced flowId

    try:
        # Clone Flow
        api_response = api_instance.clone_flow(flow_id)
        print("The response of FlowApi->clone_flow:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->clone_flow: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| The referenced flowId | 

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

# **clone_flows**
> List[str] clone_flows(request_body)

Batch Clone Flows

Batch clone multiple flows. Ensure transactionality, return the flowId list after success.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of flow information to be created

    try:
        # Batch Clone Flows
        api_response = api_instance.clone_flows(request_body)
        print("The response of FlowApi->clone_flows:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->clone_flows: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of flow information to be created | 

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

# **count_flows**
> int count_flows(flow_query_dto)

Calculate Number of Flows

Calculate the number of flows according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_query_dto import FlowQueryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_query_dto = freechat-sdk.FlowQueryDTO() # FlowQueryDTO | Query conditions

    try:
        # Calculate Number of Flows
        api_response = api_instance.count_flows(flow_query_dto)
        print("The response of FlowApi->count_flows:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->count_flows: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_query_dto** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

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

# **create_flow**
> str create_flow(flow_create_dto)

Create Flow

Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_create_dto import FlowCreateDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_create_dto = {"name":"A Test Flow","description":"A flow description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test","demo"],"aiModels":["123"]} # FlowCreateDTO | Information of the flow to be created

    try:
        # Create Flow
        api_response = api_instance.create_flow(flow_create_dto)
        print("The response of FlowApi->create_flow:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->create_flow: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_create_dto** | [**FlowCreateDTO**](FlowCreateDTO.md)| Information of the flow to be created | 

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

# **create_flows**
> List[str] create_flows(flow_create_dto)

Batch Create Flows

Batch create multiple flows. Ensure transactionality, return the flowId list after success.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_create_dto import FlowCreateDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_create_dto = [{"name":"First Test Flow","description":"First flow description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test1","demo1"],"aiModels":["123"]},{"name":"Second Test Flow","visibility":"public","description":"Second flow description","format":"langflow","config":"{}","parameters":"{\"robot\": null}","tags":["test2","demo2"],"aiModels":["123","456"]}] # List[FlowCreateDTO] | List of flow information to be created

    try:
        # Batch Create Flows
        api_response = api_instance.create_flows(flow_create_dto)
        print("The response of FlowApi->create_flows:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->create_flows: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_create_dto** | [**List[FlowCreateDTO]**](FlowCreateDTO.md)| List of flow information to be created | 

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

# **delete_flow**
> bool delete_flow(flow_id)

Delete Flow

Delete flow. Return success or failure.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | FlowId to be deleted

    try:
        # Delete Flow
        api_response = api_instance.delete_flow(flow_id)
        print("The response of FlowApi->delete_flow:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->delete_flow: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| FlowId to be deleted | 

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

# **delete_flows**
> List[str] delete_flows(request_body)

Batch Delete Flows

Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of flowId to be deleted

    try:
        # Batch Delete Flows
        api_response = api_instance.delete_flows(request_body)
        print("The response of FlowApi->delete_flows:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->delete_flows: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of flowId to be deleted | 

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

# **get_flow_details**
> FlowDetailsDTO get_flow_details(flow_id)

Get Flow Details

Get flow detailed information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_details_dto import FlowDetailsDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | FlowId to be obtained

    try:
        # Get Flow Details
        api_response = api_instance.get_flow_details(flow_id)
        print("The response of FlowApi->get_flow_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->get_flow_details: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| FlowId to be obtained | 

### Return type

[**FlowDetailsDTO**](FlowDetailsDTO.md)

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

# **get_flow_summary**
> FlowSummaryDTO get_flow_summary(flow_id)

Get Flow Summary

Get flow summary information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_summary_dto import FlowSummaryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | flowId to be obtained

    try:
        # Get Flow Summary
        api_response = api_instance.get_flow_summary(flow_id)
        print("The response of FlowApi->get_flow_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->get_flow_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| flowId to be obtained | 

### Return type

[**FlowSummaryDTO**](FlowSummaryDTO.md)

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

# **list_flow_versions_by_name**
> List[FlowItemForNameDTO] list_flow_versions_by_name(name)

List Versions by Flow Name

List the versions and corresponding flowIds by flow name.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_item_for_name_dto import FlowItemForNameDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    name = 'name_example' # str | Flow name

    try:
        # List Versions by Flow Name
        api_response = api_instance.list_flow_versions_by_name(name)
        print("The response of FlowApi->list_flow_versions_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->list_flow_versions_by_name: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Flow name | 

### Return type

[**List[FlowItemForNameDTO]**](FlowItemForNameDTO.md)

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

# **publish_flow**
> str publish_flow(flow_id, visibility)

Publish Flow

Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | The flowId to be published
    visibility = 'visibility_example' # str | Visibility: public | private | ...

    try:
        # Publish Flow
        api_response = api_instance.publish_flow(flow_id, visibility)
        print("The response of FlowApi->publish_flow:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->publish_flow: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| The flowId to be published | 
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

# **search_flow_details**
> List[FlowDetailsDTO] search_flow_details(flow_query_dto)

Search Flow Details

Same as /api/v1/flow/search, but returns detailed information of the flow.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_details_dto import FlowDetailsDTO
from freechat-sdk.models.flow_query_dto import FlowQueryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_query_dto = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # FlowQueryDTO | Query conditions

    try:
        # Search Flow Details
        api_response = api_instance.search_flow_details(flow_query_dto)
        print("The response of FlowApi->search_flow_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->search_flow_details: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_query_dto** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

### Return type

[**List[FlowDetailsDTO]**](FlowDetailsDTO.md)

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

# **search_flow_summary**
> List[FlowSummaryDTO] search_flow_summary(flow_query_dto)

Search Flow Summary

Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_query_dto import FlowQueryDTO
from freechat-sdk.models.flow_summary_dto import FlowSummaryDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_query_dto = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # FlowQueryDTO | Query conditions

    try:
        # Search Flow Summary
        api_response = api_instance.search_flow_summary(flow_query_dto)
        print("The response of FlowApi->search_flow_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->search_flow_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_query_dto** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

### Return type

[**List[FlowSummaryDTO]**](FlowSummaryDTO.md)

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

# **update_flow**
> bool update_flow(flow_id, flow_update_dto)

Update Flow

Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.flow_update_dto import FlowUpdateDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat-sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat-sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat-sdk.FlowApi(api_client)
    flow_id = 'flow_id_example' # str | FlowId to be updated
    flow_update_dto = {"version":2,"name":"Second Test Flow (New)","visibility":"public","description":"Second flow description (new)","config":"{}","inputs":"{\"robot\": null}","tags":["test2","demo2","robot"],"aiModels":["123","456"]} # FlowUpdateDTO | Flow information to be updated

    try:
        # Update Flow
        api_response = api_instance.update_flow(flow_id, flow_update_dto)
        print("The response of FlowApi->update_flow:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FlowApi->update_flow: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flow_id** | **str**| FlowId to be updated | 
 **flow_update_dto** | [**FlowUpdateDTO**](FlowUpdateDTO.md)| Flow information to be updated | 

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

