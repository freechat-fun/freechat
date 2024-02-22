# freechat_sdk.PluginApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batch_search_plugin_details**](PluginApi.md#batch_search_plugin_details) | **POST** /api/v1/plugin/batch/details/search | Batch Search Plugin Details
[**batch_search_plugin_summary**](PluginApi.md#batch_search_plugin_summary) | **POST** /api/v1/plugin/batch/search | Batch Search Plugin Summaries
[**count_plugins**](PluginApi.md#count_plugins) | **POST** /api/v1/plugin/count | Calculate Number of Plugins
[**create_plugin**](PluginApi.md#create_plugin) | **POST** /api/v1/plugin | Create Plugin
[**create_plugins**](PluginApi.md#create_plugins) | **POST** /api/v1/plugin/batch | Batch Create Plugins
[**delete_plugin**](PluginApi.md#delete_plugin) | **DELETE** /api/v1/plugin/{pluginId} | Delete Plugin
[**delete_plugins**](PluginApi.md#delete_plugins) | **DELETE** /api/v1/plugin/batch | Batch Delete Plugins
[**get_plugin_details**](PluginApi.md#get_plugin_details) | **GET** /api/v1/plugin/details/{pluginId} | Get Plugin Details
[**get_plugin_summary**](PluginApi.md#get_plugin_summary) | **GET** /api/v1/plugin/summary/{pluginId} | Get Plugin Summary
[**refresh_plugin_info**](PluginApi.md#refresh_plugin_info) | **PUT** /api/v1/plugin/refresh/{pluginId} | Refresh Plugin Information
[**search_plugin_details**](PluginApi.md#search_plugin_details) | **POST** /api/v1/plugin/details/search | Search Plugin Details
[**search_plugin_summary**](PluginApi.md#search_plugin_summary) | **POST** /api/v1/plugin/search | Search Plugin Summary
[**update_plugin**](PluginApi.md#update_plugin) | **PUT** /api/v1/plugin/{pluginId} | Update Plugin


# **batch_search_plugin_details**
> List[List[PluginDetailsDTO]] batch_search_plugin_details(plugin_query_dto)

Batch Search Plugin Details

Batch call shortcut for /api/v1/plugin/details/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_query_dto = [{"where":{"visibility":"public","username":"amin","text":"demo"},"orderBy":["modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Test","provider":"freechat.fun","text":"demo","manifestFormat":"dash_scope","apiFormat":"openapi_v3","tags":["test"],"aiModels":["123"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}] # List[PluginQueryDTO] | Query conditions

    try:
        # Batch Search Plugin Details
        api_response = api_instance.batch_search_plugin_details(plugin_query_dto)
        print("The response of PluginApi->batch_search_plugin_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->batch_search_plugin_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_query_dto** | [**List[PluginQueryDTO]**](PluginQueryDTO.md)| Query conditions | 

### Return type

**List[List[PluginDetailsDTO]]**

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

# **batch_search_plugin_summary**
> List[List[PluginSummaryDTO]] batch_search_plugin_summary(plugin_query_dto)

Batch Search Plugin Summaries

Batch call shortcut for /api/v1/plugin/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_query_dto = [{"where":{"visibility":"public","username":"amin","text":"demo"},"orderBy":["modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Test","provider":"freechat.fun","text":"demo","manifestFormat":"dash_scope","apiFormat":"openapi_v3","tags":["test"],"aiModels":["123"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}] # List[PluginQueryDTO] | Query conditions

    try:
        # Batch Search Plugin Summaries
        api_response = api_instance.batch_search_plugin_summary(plugin_query_dto)
        print("The response of PluginApi->batch_search_plugin_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->batch_search_plugin_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_query_dto** | [**List[PluginQueryDTO]**](PluginQueryDTO.md)| Query conditions | 

### Return type

**List[List[PluginSummaryDTO]]**

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

# **count_plugins**
> int count_plugins(plugin_query_dto)

Calculate Number of Plugins

Calculate the number of plugins according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_query_dto = freechat_sdk.PluginQueryDTO() # PluginQueryDTO | Query conditions

    try:
        # Calculate Number of Plugins
        api_response = api_instance.count_plugins(plugin_query_dto)
        print("The response of PluginApi->count_plugins:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->count_plugins: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_query_dto** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

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

# **create_plugin**
> str create_plugin(plugin_create_dto)

Create Plugin

Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_create_dto = {"name":"Test plugin","provider":"freechat.fun NLP Lab","manifestInfo":"http://127.0.0.1:8080/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"http://127.0.0.1:8080/public/test/plugin/demo/.well-known/api-docs.json","tags":["test","demo"]} # PluginCreateDTO | Information of the plugin to be created

    try:
        # Create Plugin
        api_response = api_instance.create_plugin(plugin_create_dto)
        print("The response of PluginApi->create_plugin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->create_plugin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_create_dto** | [**PluginCreateDTO**](PluginCreateDTO.md)| Information of the plugin to be created | 

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

# **create_plugins**
> List[str] create_plugins(plugin_create_dto)

Batch Create Plugins

Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_create_dto = [{"name":"First Test Plugin","provider":"freechat.fun NLP Lab","manifestInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json","tags":["test1","demo1"]},{"name":"Second Test Plugin","visibility":"private","manifestInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json","tags":["test2","demo2"],"aiModels":["123","456"]}] # List[PluginCreateDTO] | List of plugin information to be created

    try:
        # Batch Create Plugins
        api_response = api_instance.create_plugins(plugin_create_dto)
        print("The response of PluginApi->create_plugins:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->create_plugins: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_create_dto** | [**List[PluginCreateDTO]**](PluginCreateDTO.md)| List of plugin information to be created | 

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

# **delete_plugin**
> bool delete_plugin(plugin_id)

Delete Plugin

Delete plugin. Returns success or failure.

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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_id = 'plugin_id_example' # str | The pluginId to be deleted

    try:
        # Delete Plugin
        api_response = api_instance.delete_plugin(plugin_id)
        print("The response of PluginApi->delete_plugin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->delete_plugin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_id** | **str**| The pluginId to be deleted | 

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

# **delete_plugins**
> List[str] delete_plugins(request_body)

Batch Delete Plugins

Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.

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
    api_instance = freechat_sdk.PluginApi(api_client)
    request_body = ['request_body_example'] # List[str] | List of pluginIds to be deleted

    try:
        # Batch Delete Plugins
        api_response = api_instance.delete_plugins(request_body)
        print("The response of PluginApi->delete_plugins:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->delete_plugins: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**List[str]**](str.md)| List of pluginIds to be deleted | 

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

# **get_plugin_details**
> PluginDetailsDTO get_plugin_details(plugin_id)

Get Plugin Details

Get plugin detailed information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_id = 'plugin_id_example' # str | PluginId to be obtained

    try:
        # Get Plugin Details
        api_response = api_instance.get_plugin_details(plugin_id)
        print("The response of PluginApi->get_plugin_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->get_plugin_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_id** | **str**| PluginId to be obtained | 

### Return type

[**PluginDetailsDTO**](PluginDetailsDTO.md)

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

# **get_plugin_summary**
> PluginSummaryDTO get_plugin_summary(plugin_id)

Get Plugin Summary

Get plugin summary information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_id = 'plugin_id_example' # str | PluginId to be obtained

    try:
        # Get Plugin Summary
        api_response = api_instance.get_plugin_summary(plugin_id)
        print("The response of PluginApi->get_plugin_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->get_plugin_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_id** | **str**| PluginId to be obtained | 

### Return type

[**PluginSummaryDTO**](PluginSummaryDTO.md)

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

# **refresh_plugin_info**
> refresh_plugin_info(plugin_id)

Refresh Plugin Information

For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.

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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_id = 'plugin_id_example' # str | The pluginId to be fetched

    try:
        # Refresh Plugin Information
        api_instance.refresh_plugin_info(plugin_id)
    except Exception as e:
        print("Exception when calling PluginApi->refresh_plugin_info: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_id** | **str**| The pluginId to be fetched | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_plugin_details**
> List[PluginDetailsDTO] search_plugin_details(plugin_query_dto)

Search Plugin Details

Same as /api/v1/plugin/search, but returns detailed information of the plugin.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","provider":"freechat.fun","tags":["test2"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1} # PluginQueryDTO | Query conditions

    try:
        # Search Plugin Details
        api_response = api_instance.search_plugin_details(plugin_query_dto)
        print("The response of PluginApi->search_plugin_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->search_plugin_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_query_dto** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

### Return type

[**List[PluginDetailsDTO]**](PluginDetailsDTO.md)

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

# **search_plugin_summary**
> List[PluginSummaryDTO] search_plugin_summary(plugin_query_dto)

Search Plugin Summary

Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","provider":"freechat.fun","text":"demo","tags":["test2"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1} # PluginQueryDTO | Query conditions

    try:
        # Search Plugin Summary
        api_response = api_instance.search_plugin_summary(plugin_query_dto)
        print("The response of PluginApi->search_plugin_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->search_plugin_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_query_dto** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

### Return type

[**List[PluginSummaryDTO]**](PluginSummaryDTO.md)

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

# **update_plugin**
> bool update_plugin(plugin_id, plugin_update_dto)

Update Plugin

Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.plugin_update_dto import PluginUpdateDTO
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
    api_instance = freechat_sdk.PluginApi(api_client)
    plugin_id = 'plugin_id_example' # str | The pluginId to be updated
    plugin_update_dto = {"name":"Second Test Plugin (New)","visibility":"public","tags":["test2","demo2","business"]} # PluginUpdateDTO | The plugin information to be updated

    try:
        # Update Plugin
        api_response = api_instance.update_plugin(plugin_id, plugin_update_dto)
        print("The response of PluginApi->update_plugin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PluginApi->update_plugin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **plugin_id** | **str**| The pluginId to be updated | 
 **plugin_update_dto** | [**PluginUpdateDTO**](PluginUpdateDTO.md)| The plugin information to be updated | 

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

