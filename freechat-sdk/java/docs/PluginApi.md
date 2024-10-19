# PluginApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**batchSearchPluginDetails**](PluginApi.md#batchSearchPluginDetails) | **POST** /api/v2/plugin/batch/details/search | Batch Search Plugin Details |
| [**batchSearchPluginSummary**](PluginApi.md#batchSearchPluginSummary) | **POST** /api/v2/plugin/batch/search | Batch Search Plugin Summaries |
| [**countPlugins**](PluginApi.md#countPlugins) | **POST** /api/v2/plugin/count | Calculate Number of Plugins |
| [**createPlugin**](PluginApi.md#createPlugin) | **POST** /api/v2/plugin | Create Plugin |
| [**createPlugins**](PluginApi.md#createPlugins) | **POST** /api/v2/plugin/batch | Batch Create Plugins |
| [**deletePlugin**](PluginApi.md#deletePlugin) | **DELETE** /api/v2/plugin/{pluginId} | Delete Plugin |
| [**deletePlugins**](PluginApi.md#deletePlugins) | **DELETE** /api/v2/plugin/batch | Batch Delete Plugins |
| [**getPluginDetails**](PluginApi.md#getPluginDetails) | **GET** /api/v2/plugin/details/{pluginId} | Get Plugin Details |
| [**getPluginSummary**](PluginApi.md#getPluginSummary) | **GET** /api/v2/plugin/summary/{pluginId} | Get Plugin Summary |
| [**refreshPluginInfo**](PluginApi.md#refreshPluginInfo) | **PUT** /api/v2/plugin/refresh/{pluginId} | Refresh Plugin Information |
| [**searchPluginDetails**](PluginApi.md#searchPluginDetails) | **POST** /api/v2/plugin/details/search | Search Plugin Details |
| [**searchPluginSummary**](PluginApi.md#searchPluginSummary) | **POST** /api/v2/plugin/search | Search Plugin Summary |
| [**updatePlugin**](PluginApi.md#updatePlugin) | **PUT** /api/v2/plugin/{pluginId} | Update Plugin |


<a id="batchSearchPluginDetails"></a>
# **batchSearchPluginDetails**
> List&lt;List&lt;PluginDetailsDTO&gt;&gt; batchSearchPluginDetails(pluginQueryDTO)

Batch Search Plugin Details

Batch call shortcut for /api/v2/plugin/details/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    List<PluginQueryDTO> pluginQueryDTO = Arrays.asList(); // List<PluginQueryDTO> | Query conditions
    try {
      List<List<PluginDetailsDTO>> result = apiInstance.batchSearchPluginDetails(pluginQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#batchSearchPluginDetails");
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
| **pluginQueryDTO** | [**List&lt;PluginQueryDTO&gt;**](PluginQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;PluginDetailsDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="batchSearchPluginSummary"></a>
# **batchSearchPluginSummary**
> List&lt;List&lt;PluginSummaryDTO&gt;&gt; batchSearchPluginSummary(pluginQueryDTO)

Batch Search Plugin Summaries

Batch call shortcut for /api/v2/plugin/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    List<PluginQueryDTO> pluginQueryDTO = Arrays.asList(); // List<PluginQueryDTO> | Query conditions
    try {
      List<List<PluginSummaryDTO>> result = apiInstance.batchSearchPluginSummary(pluginQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#batchSearchPluginSummary");
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
| **pluginQueryDTO** | [**List&lt;PluginQueryDTO&gt;**](PluginQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;PluginSummaryDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="countPlugins"></a>
# **countPlugins**
> Long countPlugins(pluginQueryDTO)

Calculate Number of Plugins

Calculate the number of plugins according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    PluginQueryDTO pluginQueryDTO = new PluginQueryDTO(); // PluginQueryDTO | Query conditions
    try {
      Long result = apiInstance.countPlugins(pluginQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#countPlugins");
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
| **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createPlugin"></a>
# **createPlugin**
> Long createPlugin(pluginCreateDTO)

Create Plugin

Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    PluginCreateDTO pluginCreateDTO = new PluginCreateDTO(); // PluginCreateDTO | Information of the plugin to be created
    try {
      Long result = apiInstance.createPlugin(pluginCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#createPlugin");
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
| **pluginCreateDTO** | [**PluginCreateDTO**](PluginCreateDTO.md)| Information of the plugin to be created | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createPlugins"></a>
# **createPlugins**
> List&lt;Long&gt; createPlugins(pluginCreateDTO)

Batch Create Plugins

Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    List<PluginCreateDTO> pluginCreateDTO = Arrays.asList(); // List<PluginCreateDTO> | List of plugin information to be created
    try {
      List<Long> result = apiInstance.createPlugins(pluginCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#createPlugins");
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
| **pluginCreateDTO** | [**List&lt;PluginCreateDTO&gt;**](PluginCreateDTO.md)| List of plugin information to be created | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deletePlugin"></a>
# **deletePlugin**
> Boolean deletePlugin(pluginId)

Delete Plugin

Delete plugin. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    Long pluginId = 56L; // Long | The pluginId to be deleted
    try {
      Boolean result = apiInstance.deletePlugin(pluginId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#deletePlugin");
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
| **pluginId** | **Long**| The pluginId to be deleted | |

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

<a id="deletePlugins"></a>
# **deletePlugins**
> List&lt;Long&gt; deletePlugins(requestBody)

Batch Delete Plugins

Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    List<Long> requestBody = Arrays.asList(); // List<Long> | List of pluginIds to be deleted
    try {
      List<Long> result = apiInstance.deletePlugins(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#deletePlugins");
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
| **requestBody** | [**List&lt;Long&gt;**](Long.md)| List of pluginIds to be deleted | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getPluginDetails"></a>
# **getPluginDetails**
> PluginDetailsDTO getPluginDetails(pluginId)

Get Plugin Details

Get plugin detailed information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    Long pluginId = 56L; // Long | PluginId to be obtained
    try {
      PluginDetailsDTO result = apiInstance.getPluginDetails(pluginId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#getPluginDetails");
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
| **pluginId** | **Long**| PluginId to be obtained | |

### Return type

[**PluginDetailsDTO**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getPluginSummary"></a>
# **getPluginSummary**
> PluginSummaryDTO getPluginSummary(pluginId)

Get Plugin Summary

Get plugin summary information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    Long pluginId = 56L; // Long | PluginId to be obtained
    try {
      PluginSummaryDTO result = apiInstance.getPluginSummary(pluginId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#getPluginSummary");
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
| **pluginId** | **Long**| PluginId to be obtained | |

### Return type

[**PluginSummaryDTO**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="refreshPluginInfo"></a>
# **refreshPluginInfo**
> refreshPluginInfo(pluginId)

Refresh Plugin Information

For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    Long pluginId = 56L; // Long | The pluginId to be fetched
    try {
      apiInstance.refreshPluginInfo(pluginId);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#refreshPluginInfo");
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
| **pluginId** | **Long**| The pluginId to be fetched | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchPluginDetails"></a>
# **searchPluginDetails**
> List&lt;PluginDetailsDTO&gt; searchPluginDetails(pluginQueryDTO)

Search Plugin Details

Same as /api/v2/plugin/search, but returns detailed information of the plugin.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    PluginQueryDTO pluginQueryDTO = new PluginQueryDTO(); // PluginQueryDTO | Query conditions
    try {
      List<PluginDetailsDTO> result = apiInstance.searchPluginDetails(pluginQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#searchPluginDetails");
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
| **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;PluginDetailsDTO&gt;**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchPluginSummary"></a>
# **searchPluginSummary**
> List&lt;PluginSummaryDTO&gt; searchPluginSummary(pluginQueryDTO)

Search Plugin Summary

Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    PluginQueryDTO pluginQueryDTO = new PluginQueryDTO(); // PluginQueryDTO | Query conditions
    try {
      List<PluginSummaryDTO> result = apiInstance.searchPluginSummary(pluginQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#searchPluginSummary");
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
| **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;PluginSummaryDTO&gt;**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updatePlugin"></a>
# **updatePlugin**
> Boolean updatePlugin(pluginId, pluginUpdateDTO)

Update Plugin

Update plugin, refer to /api/v2/plugin/create, required field: pluginId. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PluginApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PluginApi apiInstance = new PluginApi(defaultClient);
    Long pluginId = 56L; // Long | The pluginId to be updated
    PluginUpdateDTO pluginUpdateDTO = new PluginUpdateDTO(); // PluginUpdateDTO | The plugin information to be updated
    try {
      Boolean result = apiInstance.updatePlugin(pluginId, pluginUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PluginApi#updatePlugin");
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
| **pluginId** | **Long**| The pluginId to be updated | |
| **pluginUpdateDTO** | [**PluginUpdateDTO**](PluginUpdateDTO.md)| The plugin information to be updated | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

