# freechat-sdk.AppConfigForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_app_config**](AppConfigForAdminApi.md#get_app_config) | **GET** /api/v1/admin/app/config/{name} | Get Configuration
[**get_app_config_by_version**](AppConfigForAdminApi.md#get_app_config_by_version) | **GET** /api/v1/admin/app/config/{name}/{version} | Get Specified Version of Configuration
[**list_app_config_names**](AppConfigForAdminApi.md#list_app_config_names) | **POST** /api/v1/admin/app/configs | List Configuration Names
[**publish_app_config**](AppConfigForAdminApi.md#publish_app_config) | **POST** /api/v1/admin/app/config | Publish Configuration


# **get_app_config**
> AppConfigInfoDTO get_app_config(name)

Get Configuration

Get the latest configuration information of the application by name.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.app_config_info_dto import AppConfigInfoDTO
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
    api_instance = freechat-sdk.AppConfigForAdminApi(api_client)
    name = 'name_example' # str | Configuration name

    try:
        # Get Configuration
        api_response = api_instance.get_app_config(name)
        print("The response of AppConfigForAdminApi->get_app_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppConfigForAdminApi->get_app_config: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Configuration name | 

### Return type

[**AppConfigInfoDTO**](AppConfigInfoDTO.md)

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

# **get_app_config_by_version**
> AppConfigInfoDTO get_app_config_by_version(name, version)

Get Specified Version of Configuration

Get the configuration information of the application by name and version.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.app_config_info_dto import AppConfigInfoDTO
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
    api_instance = freechat-sdk.AppConfigForAdminApi(api_client)
    name = 'name_example' # str | Configuration name
    version = 56 # int | Configuration version

    try:
        # Get Specified Version of Configuration
        api_response = api_instance.get_app_config_by_version(name, version)
        print("The response of AppConfigForAdminApi->get_app_config_by_version:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppConfigForAdminApi->get_app_config_by_version: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Configuration name | 
 **version** | **int**| Configuration version | 

### Return type

[**AppConfigInfoDTO**](AppConfigInfoDTO.md)

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

# **list_app_config_names**
> List[str] list_app_config_names()

List Configuration Names

List all application configuration names.

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
    api_instance = freechat-sdk.AppConfigForAdminApi(api_client)

    try:
        # List Configuration Names
        api_response = api_instance.list_app_config_names()
        print("The response of AppConfigForAdminApi->list_app_config_names:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppConfigForAdminApi->list_app_config_names: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

**List[str]**

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

# **publish_app_config**
> int publish_app_config(app_config_create_dto)

Publish Configuration

Publish application configuration, return configuration version.

### Example

* Bearer Authentication (bearerAuth):
```python
import time
import os
import freechat-sdk
from freechat-sdk.models.app_config_create_dto import AppConfigCreateDTO
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
    api_instance = freechat-sdk.AppConfigForAdminApi(api_client)
    app_config_create_dto = freechat-sdk.AppConfigCreateDTO() # AppConfigCreateDTO | Configuration information

    try:
        # Publish Configuration
        api_response = api_instance.publish_app_config(app_config_create_dto)
        print("The response of AppConfigForAdminApi->publish_app_config:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppConfigForAdminApi->publish_app_config: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app_config_create_dto** | [**AppConfigCreateDTO**](AppConfigCreateDTO.md)| Configuration information | 

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

