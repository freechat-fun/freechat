# freechat_sdk.AppConfigForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_app_configs**](AppConfigForAdminApi.md#get_app_configs) | **GET** /api/v2/admin/app/configs | Get Configurations


# **get_app_configs**
> AppConfigInfoDTO get_app_configs()

Get Configurations

Get all configuration information of the application.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.app_config_info_dto import AppConfigInfoDTO
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
    api_instance = freechat_sdk.AppConfigForAdminApi(api_client)

    try:
        # Get Configurations
        api_response = api_instance.get_app_configs()
        print("The response of AppConfigForAdminApi->get_app_configs:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AppConfigForAdminApi->get_app_configs: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

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

