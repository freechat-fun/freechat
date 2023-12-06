# AppMetaForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**expose**](AppMetaForAdminApi.md#expose) | **GET** /api/v1/admin/app/expose | Expose DTO definitions |
| [**getAppMeta**](AppMetaForAdminApi.md#getAppMeta) | **GET** /api/v1/admin/app/meta | Get Application Information |


<a id="expose"></a>
# **expose**
> String expose(openAiParam, qwenParam, aiForPromptResult)

Expose DTO definitions

This method does nothing.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AppMetaForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppMetaForAdminApi apiInstance = new AppMetaForAdminApi(defaultClient);
    OpenAiParamDTO openAiParam = new OpenAiParamDTO(); // OpenAiParamDTO | 
    QwenParamDTO qwenParam = new QwenParamDTO(); // QwenParamDTO | 
    LlmResultDTO aiForPromptResult = new LlmResultDTO(); // LlmResultDTO | 
    try {
      String result = apiInstance.expose(openAiParam, qwenParam, aiForPromptResult);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppMetaForAdminApi#expose");
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
| **openAiParam** | [**OpenAiParamDTO**](.md)|  | |
| **qwenParam** | [**QwenParamDTO**](.md)|  | |
| **aiForPromptResult** | [**LlmResultDTO**](.md)|  | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getAppMeta"></a>
# **getAppMeta**
> AppMetaDTO getAppMeta()

Get Application Information

Get application information to accurately locate the corresponding project version.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AppMetaForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppMetaForAdminApi apiInstance = new AppMetaForAdminApi(defaultClient);
    try {
      AppMetaDTO result = apiInstance.getAppMeta();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppMetaForAdminApi#getAppMeta");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
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
| **200** | OK |  -  |

