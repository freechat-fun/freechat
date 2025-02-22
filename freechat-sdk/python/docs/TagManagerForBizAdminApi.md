# freechat_sdk.TagManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_tag**](TagManagerForBizAdminApi.md#create_tag) | **POST** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Create Tag
[**delete_tag**](TagManagerForBizAdminApi.md#delete_tag) | **DELETE** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Delete Tag


# **create_tag**
> bool create_tag(refer_type, refer_id, tag)

Create Tag

Create a tag, tags created by the administrator cannot be deleted by ordinary users.

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
    api_instance = freechat_sdk.TagManagerForBizAdminApi(api_client)
    refer_type = 'refer_type_example' # str | Tag type (prompt, agent, plugin...)
    refer_id = 'refer_id_example' # str | Resource identifier of the tag
    tag = 'tag_example' # str | Tag content

    try:
        # Create Tag
        api_response = api_instance.create_tag(refer_type, refer_id, tag)
        print("The response of TagManagerForBizAdminApi->create_tag:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TagManagerForBizAdminApi->create_tag: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **refer_type** | **str**| Tag type (prompt, agent, plugin...) | 
 **refer_id** | **str**| Resource identifier of the tag | 
 **tag** | **str**| Tag content | 

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

# **delete_tag**
> bool delete_tag(refer_type, refer_id, tag)

Delete Tag

Delete a tag, any tag created by anyone can be deleted.

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
    api_instance = freechat_sdk.TagManagerForBizAdminApi(api_client)
    refer_type = 'refer_type_example' # str | Tag type (prompt, agent, plugin...)
    refer_id = 'refer_id_example' # str | Resource identifier of the tag
    tag = 'tag_example' # str | Tag content

    try:
        # Delete Tag
        api_response = api_instance.delete_tag(refer_type, refer_id, tag)
        print("The response of TagManagerForBizAdminApi->delete_tag:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TagManagerForBizAdminApi->delete_tag: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **refer_type** | **str**| Tag type (prompt, agent, plugin...) | 
 **refer_id** | **str**| Resource identifier of the tag | 
 **tag** | **str**| Tag content | 

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

