# freechat_sdk.AppMetaForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**expose**](AppMetaForAdminApi.md#expose) | **GET** /api/v1/admin/app/expose | Expose DTO definitions
[**get_app_meta**](AppMetaForAdminApi.md#get_app_meta) | **GET** /api/v1/admin/app/meta | Get Application Information


# **expose**
> str expose(open_ai_param, qwen_param, ai_for_prompt_result)

Expose DTO definitions

This method does nothing.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.llm_result_dto import LlmResultDTO
from freechat_sdk.models.open_ai_param_dto import OpenAiParamDTO
from freechat_sdk.models.qwen_param_dto import QwenParamDTO
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
    api_instance = freechat_sdk.AppMetaForAdminApi(api_client)
    open_ai_param = freechat_sdk.OpenAiParamDTO() # OpenAiParamDTO | 
    qwen_param = freechat_sdk.QwenParamDTO() # QwenParamDTO | 
    ai_for_prompt_result = freechat_sdk.LlmResultDTO() # LlmResultDTO | 

    try:
        # Expose DTO definitions
        api_response = api_instance.expose(open_ai_param, qwen_param, ai_for_prompt_result)
        print("The response of AppMetaForAdminApi->expose:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppMetaForAdminApi->expose: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **open_ai_param** | [**OpenAiParamDTO**](.md)|  | 
 **qwen_param** | [**QwenParamDTO**](.md)|  | 
 **ai_for_prompt_result** | [**LlmResultDTO**](.md)|  | 

### Return type

**str**

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

# **get_app_meta**
> AppMetaDTO get_app_meta()

Get Application Information

Get application information to accurately locate the corresponding project version.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.app_meta_dto import AppMetaDTO
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
    api_instance = freechat_sdk.AppMetaForAdminApi(api_client)

    try:
        # Get Application Information
        api_response = api_instance.get_app_meta()
        print("The response of AppMetaForAdminApi->get_app_meta:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppMetaForAdminApi->get_app_meta: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**AppMetaDTO**](AppMetaDTO.md)

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

