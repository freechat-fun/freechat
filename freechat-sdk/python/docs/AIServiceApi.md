# freechat-sdk.AIServiceApi

All URIs are relative to *https://freechat.fun*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_ai_api_key**](AIServiceApi.md#add_ai_api_key) | **POST** /api/v1/ai/apikey | Add Model Provider Credential
[**delete_ai_api_key**](AIServiceApi.md#delete_ai_api_key) | **DELETE** /api/v1/ai/apikey/{id} | Delete Credential of Model Provider
[**disable_ai_api_key**](AIServiceApi.md#disable_ai_api_key) | **PUT** /api/v1/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enable_ai_api_key**](AIServiceApi.md#enable_ai_api_key) | **PUT** /api/v1/ai/apikey/enable/{id} | Enable Model Provider Credential
[**get_ai_api_key**](AIServiceApi.md#get_ai_api_key) | **GET** /api/v1/ai/apikey/{id} | Get credential of Model Provider
[**get_ai_model_info**](AIServiceApi.md#get_ai_model_info) | **GET** /api/v1/ai/model/{modelId} | Get Model Information
[**list_ai_api_keys**](AIServiceApi.md#list_ai_api_keys) | **GET** /api/v1/ai/apikeys/{provider} | List Credentials of Model Provider
[**list_ai_model_info**](AIServiceApi.md#list_ai_model_info) | **GET** /api/v1/ai/models/{pageSize} | List Models
[**list_ai_model_info1**](AIServiceApi.md#list_ai_model_info1) | **GET** /api/v1/ai/models | List Models
[**list_ai_model_info2**](AIServiceApi.md#list_ai_model_info2) | **GET** /api/v1/ai/models/{pageSize}/{pageNum} | List Models


# **add_ai_api_key**
> int add_ai_api_key(ai_api_key_create_dto)

Add Model Provider Credential

Add a credential for the model service.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
    ai_api_key_create_dto = freechat-sdk.AiApiKeyCreateDTO() # AiApiKeyCreateDTO | Model call credential information

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
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
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
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
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
import time
import os
import freechat-sdk
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
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
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
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

# **get_ai_model_info**
> AiModelInfoDTO get_ai_model_info(model_id)

Get Model Information

Return specific model information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
    model_id = 'model_id_example' # str | Model identifier

    try:
        # Get Model Information
        api_response = api_instance.get_ai_model_info(model_id)
        print("The response of AIServiceApi->get_ai_model_info:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->get_ai_model_info: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **model_id** | **str**| Model identifier | 

### Return type

[**AiModelInfoDTO**](AiModelInfoDTO.md)

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
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
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

# **list_ai_model_info**
> List[AiModelInfoDTO] list_ai_model_info(page_size)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
    page_size = 56 # int | Maximum quantity

    try:
        # List Models
        api_response = api_instance.list_ai_model_info(page_size)
        print("The response of AIServiceApi->list_ai_model_info:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->list_ai_model_info: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page_size** | **int**| Maximum quantity | 

### Return type

[**List[AiModelInfoDTO]**](AiModelInfoDTO.md)

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

# **list_ai_model_info1**
> List[AiModelInfoDTO] list_ai_model_info1()

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)

    try:
        # List Models
        api_response = api_instance.list_ai_model_info1()
        print("The response of AIServiceApi->list_ai_model_info1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->list_ai_model_info1: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**List[AiModelInfoDTO]**](AiModelInfoDTO.md)

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

# **list_ai_model_info2**
> List[AiModelInfoDTO] list_ai_model_info2(page_size, page_num)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.ai_model_info_dto import AiModelInfoDTO
from freechat-sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://freechat.fun
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat-sdk.Configuration(
    host = "https://freechat.fun"
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
    api_instance = freechat-sdk.AIServiceApi(api_client)
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number

    try:
        # List Models
        api_response = api_instance.list_ai_model_info2(page_size, page_num)
        print("The response of AIServiceApi->list_ai_model_info2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIServiceApi->list_ai_model_info2: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 

### Return type

[**List[AiModelInfoDTO]**](AiModelInfoDTO.md)

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

