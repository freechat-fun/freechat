# AiManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createOrUpdateAiModelInfo**](AiManagerForBizAdminApi.md#createOrUpdateAiModelInfo) | **PUT** /api/v2/biz/admin/ai/model | Create or Update Model Information |
| [**deleteAiModelInfo**](AiManagerForBizAdminApi.md#deleteAiModelInfo) | **DELETE** /api/v2/biz/admin/ai/model/{modelId} | Delete Model Information |


<a id="createOrUpdateAiModelInfo"></a>
# **createOrUpdateAiModelInfo**
> String createOrUpdateAiModelInfo(aiModelInfoUpdateDTO)

Create or Update Model Information

Create or update model information. If no modelId is passed or the modelId does not exist in the database, create a new one (keep the same modelId); otherwise update. Return modelId if successful.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiManagerForBizAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiManagerForBizAdminApi apiInstance = new AiManagerForBizAdminApi(defaultClient);
    AiModelInfoUpdateDTO aiModelInfoUpdateDTO = new AiModelInfoUpdateDTO(); // AiModelInfoUpdateDTO | Model information
    try {
      String result = apiInstance.createOrUpdateAiModelInfo(aiModelInfoUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiManagerForBizAdminApi#createOrUpdateAiModelInfo");
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
| **aiModelInfoUpdateDTO** | [**AiModelInfoUpdateDTO**](AiModelInfoUpdateDTO.md)| Model information | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteAiModelInfo"></a>
# **deleteAiModelInfo**
> Boolean deleteAiModelInfo(modelId)

Delete Model Information

Delete model information based on modelId.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiManagerForBizAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiManagerForBizAdminApi apiInstance = new AiManagerForBizAdminApi(defaultClient);
    String modelId = "modelId_example"; // String | Model identifier
    try {
      Boolean result = apiInstance.deleteAiModelInfo(modelId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiManagerForBizAdminApi#deleteAiModelInfo");
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
| **modelId** | **String**| Model identifier | |

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

