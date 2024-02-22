# freechat_sdk.EncryptionManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**encrypt_text**](EncryptionManagerForAdminApi.md#encrypt_text) | **GET** /api/v1/admin/encryption/encrypt/{text} | Encrypt Text


# **encrypt_text**
> str encrypt_text(text)

Encrypt Text

Encrypt a piece of text with the built-in key.

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
    api_instance = freechat_sdk.EncryptionManagerForAdminApi(api_client)
    text = 'text_example' # str | Text to be encrypted

    try:
        # Encrypt Text
        api_response = api_instance.encrypt_text(text)
        print("The response of EncryptionManagerForAdminApi->encrypt_text:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling EncryptionManagerForAdminApi->encrypt_text: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **text** | **str**| Text to be encrypted | 

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

