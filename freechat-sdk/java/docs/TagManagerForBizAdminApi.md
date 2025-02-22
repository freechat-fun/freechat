# TagManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createTag**](TagManagerForBizAdminApi.md#createTag) | **POST** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Create Tag |
| [**deleteTag**](TagManagerForBizAdminApi.md#deleteTag) | **DELETE** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Delete Tag |


<a id="createTag"></a>
# **createTag**
> Boolean createTag(referType, referId, tag)

Create Tag

Create a tag, tags created by the administrator cannot be deleted by ordinary users.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TagManagerForBizAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TagManagerForBizAdminApi apiInstance = new TagManagerForBizAdminApi(defaultClient);
    String referType = "referType_example"; // String | Tag type (prompt, agent, plugin...)
    String referId = "referId_example"; // String | Resource identifier of the tag
    String tag = "tag_example"; // String | Tag content
    try {
      Boolean result = apiInstance.createTag(referType, referId, tag);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TagManagerForBizAdminApi#createTag");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **referType** | **String**| Tag type (prompt, agent, plugin...) | |
| **referId** | **String**| Resource identifier of the tag | |
| **tag** | **String**| Tag content | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteTag"></a>
# **deleteTag**
> Boolean deleteTag(referType, referId, tag)

Delete Tag

Delete a tag, any tag created by anyone can be deleted.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TagManagerForBizAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TagManagerForBizAdminApi apiInstance = new TagManagerForBizAdminApi(defaultClient);
    String referType = "referType_example"; // String | Tag type (prompt, agent, plugin...)
    String referId = "referId_example"; // String | Resource identifier of the tag
    String tag = "tag_example"; // String | Tag content
    try {
      Boolean result = apiInstance.deleteTag(referType, referId, tag);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TagManagerForBizAdminApi#deleteTag");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **referType** | **String**| Tag type (prompt, agent, plugin...) | |
| **referId** | **String**| Resource identifier of the tag | |
| **tag** | **String**| Tag content | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

