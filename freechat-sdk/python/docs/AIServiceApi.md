# freechat_sdk.AIServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_ai_api_key**](AIServiceApi.md#add_ai_api_key) | **POST** /api/v2/ai/apikey | Add Model Provider Credential
[**delete_ai_api_key**](AIServiceApi.md#delete_ai_api_key) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider
[**disable_ai_api_key**](AIServiceApi.md#disable_ai_api_key) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enable_ai_api_key**](AIServiceApi.md#enable_ai_api_key) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential
[**get_ai_api_key**](AIServiceApi.md#get_ai_api_key) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider
[**list_ai_api_keys**](AIServiceApi.md#list_ai_api_keys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider


# **add_ai_api_key**
> int add_ai_api_key(ai_api_key_create_dto)

Add Model Provider Credential

Add a credential for the model service.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO
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
    except Exception as e:
        print("Exception when calling AIServiceApi->add_ai_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ai_api_key_create_dto** | [**AiApiKeyCreateDTO**](AiApiKeyCreateDTO.md)| Model call credential information | 

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

# **delete_ai_api_key**
> bool delete_ai_api_key(id)

Delete Credential of Model Provider

Delete the credential information of the model provider.

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
    api_instance = freechat_sdk.AIServiceApi(api_client)
    id = 56 # int | Credential identifier

    try:
        # Delete Credential of Model Provider
        api_response = api_instance.delete_ai_api_key(id)
        print("The response of AIServiceApi->delete_ai_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->delete_ai_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**| Credential identifier | 

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

# **disable_ai_api_key**
> bool disable_ai_api_key(id)

Disable Model Provider Credential

Disable the credential information of the model provider.

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
    api_instance = freechat_sdk.AIServiceApi(api_client)
    id = 56 # int | Credential identifier

    try:
        # Disable Model Provider Credential
        api_response = api_instance.disable_ai_api_key(id)
        print("The response of AIServiceApi->disable_ai_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->disable_ai_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**| Credential identifier | 

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

# **enable_ai_api_key**
> bool enable_ai_api_key(id)

Enable Model Provider Credential

Enable the credential information of the model provider.

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
    api_instance = freechat_sdk.AIServiceApi(api_client)
    id = 56 # int | Credential identifier

    try:
        # Enable Model Provider Credential
        api_response = api_instance.enable_ai_api_key(id)
        print("The response of AIServiceApi->enable_ai_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->enable_ai_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**| Credential identifier | 

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

# **get_ai_api_key**
> AiApiKeyInfoDTO get_ai_api_key(id)

Get credential of Model Provider

Get the credential information of the model provider.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
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
    id = 56 # int | Credential identifier

    try:
        # Get credential of Model Provider
        api_response = api_instance.get_ai_api_key(id)
        print("The response of AIServiceApi->get_ai_api_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->get_ai_api_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**| Credential identifier | 

### Return type

[**AiApiKeyInfoDTO**](AiApiKeyInfoDTO.md)

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

# **list_ai_api_keys**
> List[AiApiKeyInfoDTO] list_ai_api_keys(provider)

List Credentials of Model Provider

List all credential information of the model provider.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
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
    provider = 'provider_example' # str | Model provider

    try:
        # List Credentials of Model Provider
        api_response = api_instance.list_ai_api_keys(provider)
        print("The response of AIServiceApi->list_ai_api_keys:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->list_ai_api_keys: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **provider** | **str**| Model provider | 

### Return type

[**List[AiApiKeyInfoDTO]**](AiApiKeyInfoDTO.md)

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

