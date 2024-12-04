# AppConfigForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAppConfigs**](AppConfigForAdminApi.md#getAppConfigs) | **GET** /api/v2/admin/app/configs | Get Configurations |


<a id="getAppConfigs"></a>
# **getAppConfigs**
> AppConfigInfoDTO getAppConfigs()

Get Configurations

Get all configuration information of the application.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AppConfigForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppConfigForAdminApi apiInstance = new AppConfigForAdminApi(defaultClient);
    try {
      AppConfigInfoDTO result = apiInstance.getAppConfigs();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppConfigForAdminApi#getAppConfigs");
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

[**AppConfigInfoDTO**](AppConfigInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

