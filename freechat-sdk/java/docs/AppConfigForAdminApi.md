# AppConfigForAdminApi

All URIs are relative to *https://freechat.fun*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getAppConfig**](AppConfigForAdminApi.md#getAppConfig) | **GET** /api/v1/admin/app/config/{name} | Get Configuration |
| [**getAppConfigByVersion**](AppConfigForAdminApi.md#getAppConfigByVersion) | **GET** /api/v1/admin/app/config/{name}/{version} | Get Specified Version of Configuration |
| [**listAppConfigNames**](AppConfigForAdminApi.md#listAppConfigNames) | **POST** /api/v1/admin/app/configs | List Configuration Names |
| [**publishAppConfig**](AppConfigForAdminApi.md#publishAppConfig) | **POST** /api/v1/admin/app/config | Publish Configuration |


<a id="getAppConfig"></a>
# **getAppConfig**
> AppConfigInfoDTO getAppConfig(name)

Get Configuration

Get the latest configuration information of the application by name.

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
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppConfigForAdminApi apiInstance = new AppConfigForAdminApi(defaultClient);
    String name = "name_example"; // String | Configuration name
    try {
      AppConfigInfoDTO result = apiInstance.getAppConfig(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppConfigForAdminApi#getAppConfig");
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
| **name** | **String**| Configuration name | |

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

<a id="getAppConfigByVersion"></a>
# **getAppConfigByVersion**
> AppConfigInfoDTO getAppConfigByVersion(name, version)

Get Specified Version of Configuration

Get the configuration information of the application by name and version.

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
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppConfigForAdminApi apiInstance = new AppConfigForAdminApi(defaultClient);
    String name = "name_example"; // String | Configuration name
    Integer version = 56; // Integer | Configuration version
    try {
      AppConfigInfoDTO result = apiInstance.getAppConfigByVersion(name, version);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppConfigForAdminApi#getAppConfigByVersion");
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
| **name** | **String**| Configuration name | |
| **version** | **Integer**| Configuration version | |

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

<a id="listAppConfigNames"></a>
# **listAppConfigNames**
> List&lt;String&gt; listAppConfigNames()

List Configuration Names

List all application configuration names.

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
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppConfigForAdminApi apiInstance = new AppConfigForAdminApi(defaultClient);
    try {
      List<String> result = apiInstance.listAppConfigNames();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppConfigForAdminApi#listAppConfigNames");
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

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="publishAppConfig"></a>
# **publishAppConfig**
> Integer publishAppConfig(appConfigCreateDTO)

Publish Configuration

Publish application configuration, return configuration version.

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
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AppConfigForAdminApi apiInstance = new AppConfigForAdminApi(defaultClient);
    AppConfigCreateDTO appConfigCreateDTO = new AppConfigCreateDTO(); // AppConfigCreateDTO | Configuration information
    try {
      Integer result = apiInstance.publishAppConfig(appConfigCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AppConfigForAdminApi#publishAppConfig");
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
| **appConfigCreateDTO** | [**AppConfigCreateDTO**](AppConfigCreateDTO.md)| Configuration information | |

### Return type

**Integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

