# InteractiveStatisticsApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addStatistic**](InteractiveStatisticsApi.md#addStatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics |
| [**getScore**](InteractiveStatisticsApi.md#getScore) | **GET** /api/v2/public/score/{infoType}/{infoId} | Get Score for Resource |
| [**getStatistic**](InteractiveStatisticsApi.md#getStatistic) | **GET** /api/v2/public/stats/{infoType}/{infoId}/{statsType} | Get Statistics |
| [**getStatistics**](InteractiveStatisticsApi.md#getStatistics) | **GET** /api/v2/public/stats/{infoType}/{infoId} | Get All Statistics |
| [**increaseStatistic**](InteractiveStatisticsApi.md#increaseStatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType} | Increase Statistics |
| [**listAgentsByStatistic**](InteractiveStatisticsApi.md#listAgentsByStatistic) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize} | List Agents by Statistics |
| [**listAgentsByStatistic1**](InteractiveStatisticsApi.md#listAgentsByStatistic1) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize}/{pageNum} | List Agents by Statistics |
| [**listAgentsByStatistic2**](InteractiveStatisticsApi.md#listAgentsByStatistic2) | **GET** /api/v2/public/stats/agents/by/{statsType} | List Agents by Statistics |
| [**listCharactersByStatistic**](InteractiveStatisticsApi.md#listCharactersByStatistic) | **GET** /api/v2/public/stats/characters/by/{statsType} | List Characters by Statistics |
| [**listCharactersByStatistic1**](InteractiveStatisticsApi.md#listCharactersByStatistic1) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics |
| [**listCharactersByStatistic2**](InteractiveStatisticsApi.md#listCharactersByStatistic2) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics |
| [**listHotTags**](InteractiveStatisticsApi.md#listHotTags) | **GET** /api/v2/public/tags/hot/{infoType}/{pageSize} | Hot Tags |
| [**listPluginsByStatistic**](InteractiveStatisticsApi.md#listPluginsByStatistic) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics |
| [**listPluginsByStatistic1**](InteractiveStatisticsApi.md#listPluginsByStatistic1) | **GET** /api/v2/public/stats/plugins/by/{statsType} | List Plugins by Statistics |
| [**listPluginsByStatistic2**](InteractiveStatisticsApi.md#listPluginsByStatistic2) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics |
| [**listPromptsByStatistic**](InteractiveStatisticsApi.md#listPromptsByStatistic) | **GET** /api/v2/public/stats/prompts/by/{statsType} | List Prompts by Statistics |
| [**listPromptsByStatistic1**](InteractiveStatisticsApi.md#listPromptsByStatistic1) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics |
| [**listPromptsByStatistic2**](InteractiveStatisticsApi.md#listPromptsByStatistic2) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics |


<a id="addStatistic"></a>
# **addStatistic**
> Long addStatistic(infoType, infoId, statsType, delta)

Add Statistics

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    String infoId = "infoId_example"; // String | Unique resource identifier
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long delta = 56L; // Long | Delta in statistical value
    try {
      Long result = apiInstance.addStatistic(infoType, infoId, statsType, delta);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#addStatistic");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **infoId** | **String**| Unique resource identifier | |
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **delta** | **Long**| Delta in statistical value | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getScore"></a>
# **getScore**
> Long getScore(infoType, infoId)

Get Score for Resource

Get the current user&#39;s score for the corresponding resource.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    String infoId = "infoId_example"; // String | Unique resource identifier
    try {
      Long result = apiInstance.getScore(infoType, infoId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#getScore");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **infoId** | **String**| Unique resource identifier | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getStatistic"></a>
# **getStatistic**
> Long getStatistic(infoType, infoId, statsType)

Get Statistics

Get the statistics of the corresponding metrics of the corresponding resources.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    String infoId = "infoId_example"; // String | Unique resource identifier
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    try {
      Long result = apiInstance.getStatistic(infoType, infoId, statsType);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#getStatistic");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **infoId** | **String**| Unique resource identifier | |
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getStatistics"></a>
# **getStatistics**
> InteractiveStatsDTO getStatistics(infoType, infoId)

Get All Statistics

Get all statistics of the corresponding resources.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    String infoId = "infoId_example"; // String | Unique resource identifier
    try {
      InteractiveStatsDTO result = apiInstance.getStatistics(infoType, infoId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#getStatistics");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **infoId** | **String**| Unique resource identifier | |

### Return type

[**InteractiveStatsDTO**](InteractiveStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="increaseStatistic"></a>
# **increaseStatistic**
> Long increaseStatistic(infoType, infoId, statsType)

Increase Statistics

Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    String infoId = "infoId_example"; // String | Unique resource identifier
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    try {
      Long result = apiInstance.increaseStatistic(infoType, infoId, statsType);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#increaseStatistic");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **infoId** | **String**| Unique resource identifier | |
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAgentsByStatistic"></a>
# **listAgentsByStatistic**
> List&lt;AgentSummaryStatsDTO&gt; listAgentsByStatistic(statsType, pageSize, asc)

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<AgentSummaryStatsDTO> result = apiInstance.listAgentsByStatistic(statsType, pageSize, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listAgentsByStatistic");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;AgentSummaryStatsDTO&gt;**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAgentsByStatistic1"></a>
# **listAgentsByStatistic1**
> List&lt;AgentSummaryStatsDTO&gt; listAgentsByStatistic1(statsType, pageSize, pageNum, asc)

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<AgentSummaryStatsDTO> result = apiInstance.listAgentsByStatistic1(statsType, pageSize, pageNum, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listAgentsByStatistic1");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;AgentSummaryStatsDTO&gt;**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAgentsByStatistic2"></a>
# **listAgentsByStatistic2**
> List&lt;AgentSummaryStatsDTO&gt; listAgentsByStatistic2(statsType, asc)

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<AgentSummaryStatsDTO> result = apiInstance.listAgentsByStatistic2(statsType, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listAgentsByStatistic2");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;AgentSummaryStatsDTO&gt;**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharactersByStatistic"></a>
# **listCharactersByStatistic**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic(statsType, asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic(statsType, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listCharactersByStatistic");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;CharacterSummaryStatsDTO&gt;**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharactersByStatistic1"></a>
# **listCharactersByStatistic1**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic1(statsType, pageSize, asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic1(statsType, pageSize, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listCharactersByStatistic1");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;CharacterSummaryStatsDTO&gt;**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharactersByStatistic2"></a>
# **listCharactersByStatistic2**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic2(statsType, pageSize, pageNum, asc)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic2(statsType, pageSize, pageNum, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listCharactersByStatistic2");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;CharacterSummaryStatsDTO&gt;**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listHotTags"></a>
# **listHotTags**
> List&lt;HotTagDTO&gt; listHotTags(infoType, pageSize, text)

Hot Tags

Get popular tags for a specified info type.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String infoType = "infoType_example"; // String | Info type: prompt | agent | plugin | character
    Long pageSize = 56L; // Long | Maximum quantity
    String text = "text_example"; // String | Key word
    try {
      List<HotTagDTO> result = apiInstance.listHotTags(infoType, pageSize, text);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listHotTags");
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
| **infoType** | **String**| Info type: prompt | agent | plugin | character | |
| **pageSize** | **Long**| Maximum quantity | |
| **text** | **String**| Key word | [optional] |

### Return type

[**List&lt;HotTagDTO&gt;**](HotTagDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPluginsByStatistic"></a>
# **listPluginsByStatistic**
> List&lt;PluginSummaryStatsDTO&gt; listPluginsByStatistic(statsType, pageSize, pageNum, asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PluginSummaryStatsDTO> result = apiInstance.listPluginsByStatistic(statsType, pageSize, pageNum, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPluginsByStatistic");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PluginSummaryStatsDTO&gt;**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPluginsByStatistic1"></a>
# **listPluginsByStatistic1**
> List&lt;PluginSummaryStatsDTO&gt; listPluginsByStatistic1(statsType, asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PluginSummaryStatsDTO> result = apiInstance.listPluginsByStatistic1(statsType, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPluginsByStatistic1");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PluginSummaryStatsDTO&gt;**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPluginsByStatistic2"></a>
# **listPluginsByStatistic2**
> List&lt;PluginSummaryStatsDTO&gt; listPluginsByStatistic2(statsType, pageSize, asc)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PluginSummaryStatsDTO> result = apiInstance.listPluginsByStatistic2(statsType, pageSize, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPluginsByStatistic2");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PluginSummaryStatsDTO&gt;**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPromptsByStatistic"></a>
# **listPromptsByStatistic**
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic(statsType, asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic(statsType, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPromptsByStatistic");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PromptSummaryStatsDTO&gt;**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPromptsByStatistic1"></a>
# **listPromptsByStatistic1**
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic1(statsType, pageSize, asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic1(statsType, pageSize, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPromptsByStatistic1");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PromptSummaryStatsDTO&gt;**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPromptsByStatistic2"></a>
# **listPromptsByStatistic2**
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic2(statsType, pageSize, pageNum, asc)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.InteractiveStatisticsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    InteractiveStatisticsApi apiInstance = new InteractiveStatisticsApi(defaultClient);
    String statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    String asc = "asc_example"; // String | Default is descending order, set asc=1 for ascending order
    try {
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic2(statsType, pageSize, pageNum, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listPromptsByStatistic2");
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
| **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | |
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |
| **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] |

### Return type

[**List&lt;PromptSummaryStatsDTO&gt;**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

