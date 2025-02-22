# freechat_sdk.AIManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_or_update_ai_model_info**](AIManagerForBizAdminApi.md#create_or_update_ai_model_info) | **PUT** /api/v2/biz/admin/ai/model | Create or Update Model Information
[**delete_ai_model_info**](AIManagerForBizAdminApi.md#delete_ai_model_info) | **DELETE** /api/v2/biz/admin/ai/model/{modelId} | Delete Model Information


# **create_or_update_ai_model_info**
> str create_or_update_ai_model_info(ai_model_info_update_dto)

Create or Update Model Information

Create or update model information. If no modelId is passed or the modelId does not exist in the database, create a new one (keep the same modelId); otherwise update. Return modelId if successful.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.ai_model_info_update_dto import AiModelInfoUpdateDTO
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
    api_instance = freechat_sdk.AIManagerForBizAdminApi(api_client)
    ai_model_info_update_dto = freechat_sdk.AiModelInfoUpdateDTO() # AiModelInfoUpdateDTO | Model information

    try:
        # Create or Update Model Information
        api_response = api_instance.create_or_update_ai_model_info(ai_model_info_update_dto)
        print("The response of AIManagerForBizAdminApi->create_or_update_ai_model_info:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIManagerForBizAdminApi->create_or_update_ai_model_info: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ai_model_info_update_dto** | [**AiModelInfoUpdateDTO**](AiModelInfoUpdateDTO.md)| Model information | 

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

# **delete_ai_model_info**
> bool delete_ai_model_info(model_id)

Delete Model Information

Delete model information based on modelId.

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
    api_instance = freechat_sdk.AIManagerForBizAdminApi(api_client)
    model_id = 'model_id_example' # str | Model identifier

    try:
        # Delete Model Information
        api_response = api_instance.delete_ai_model_info(model_id)
        print("The response of AIManagerForBizAdminApi->delete_ai_model_info:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AIManagerForBizAdminApi->delete_ai_model_info: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **model_id** | **str**| Model identifier | 

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

