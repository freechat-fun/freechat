# InteractiveStatisticsApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addStatistic**](InteractiveStatisticsApi.md#addStatistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics |
| [**getScore**](InteractiveStatisticsApi.md#getScore) | **GET** /api/v1/score/{infoType}/{infoId} | Get Score for Resource |
| [**getStatistic**](InteractiveStatisticsApi.md#getStatistic) | **GET** /api/v1/stats/{infoType}/{infoId}/{statsType} | Get Statistics |
| [**getStatistics**](InteractiveStatisticsApi.md#getStatistics) | **GET** /api/v1/stats/{infoType}/{infoId} | Get All Statistics |
| [**increaseStatistic**](InteractiveStatisticsApi.md#increaseStatistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType} | Increase Statistics |
| [**listCharactersByStatistic**](InteractiveStatisticsApi.md#listCharactersByStatistic) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics |
| [**listCharactersByStatistic1**](InteractiveStatisticsApi.md#listCharactersByStatistic1) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics |
| [**listCharactersByStatistic2**](InteractiveStatisticsApi.md#listCharactersByStatistic2) | **GET** /api/v1/stats/characters/by/{statsType} | List Characters by Statistics |
| [**listFlowsByStatistic**](InteractiveStatisticsApi.md#listFlowsByStatistic) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize}/{pageNum} | List Flows by Statistics |
| [**listFlowsByStatistic1**](InteractiveStatisticsApi.md#listFlowsByStatistic1) | **GET** /api/v1/stats/flows/by/{statsType} | List Flows by Statistics |
| [**listFlowsByStatistic2**](InteractiveStatisticsApi.md#listFlowsByStatistic2) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize} | List Flows by Statistics |
| [**listPluginsByStatistic**](InteractiveStatisticsApi.md#listPluginsByStatistic) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics |
| [**listPluginsByStatistic1**](InteractiveStatisticsApi.md#listPluginsByStatistic1) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics |
| [**listPluginsByStatistic2**](InteractiveStatisticsApi.md#listPluginsByStatistic2) | **GET** /api/v1/stats/plugins/by/{statsType} | List Plugins by Statistics |
| [**listPromptsByStatistic**](InteractiveStatisticsApi.md#listPromptsByStatistic) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics |
| [**listPromptsByStatistic1**](InteractiveStatisticsApi.md#listPromptsByStatistic1) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics |
| [**listPromptsByStatistic2**](InteractiveStatisticsApi.md#listPromptsByStatistic2) | **GET** /api/v1/stats/prompts/by/{statsType} | List Prompts by Statistics |


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
    String infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
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
| **infoType** | **String**| Resource type: prompt | flow | plugin | |
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
    String infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
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
| **infoType** | **String**| Resource type: prompt | flow | plugin | |
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
    String infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
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
| **infoType** | **String**| Resource type: prompt | flow | plugin | |
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
    String infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
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
| **infoType** | **String**| Resource type: prompt | flow | plugin | |
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
    String infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
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
| **infoType** | **String**| Resource type: prompt | flow | plugin | |
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

<a id="listCharactersByStatistic"></a>
# **listCharactersByStatistic**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic(statsType, pageSize, asc)

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
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic(statsType, pageSize, asc);
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

<a id="listCharactersByStatistic1"></a>
# **listCharactersByStatistic1**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic1(statsType, pageSize, pageNum, asc)

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
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic1(statsType, pageSize, pageNum, asc);
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

<a id="listCharactersByStatistic2"></a>
# **listCharactersByStatistic2**
> List&lt;CharacterSummaryStatsDTO&gt; listCharactersByStatistic2(statsType, asc)

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
      List<CharacterSummaryStatsDTO> result = apiInstance.listCharactersByStatistic2(statsType, asc);
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

<a id="listFlowsByStatistic"></a>
# **listFlowsByStatistic**
> List&lt;FlowSummaryStatsDTO&gt; listFlowsByStatistic(statsType, pageSize, pageNum, asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

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
      List<FlowSummaryStatsDTO> result = apiInstance.listFlowsByStatistic(statsType, pageSize, pageNum, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listFlowsByStatistic");
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

[**List&lt;FlowSummaryStatsDTO&gt;**](FlowSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listFlowsByStatistic1"></a>
# **listFlowsByStatistic1**
> List&lt;FlowSummaryStatsDTO&gt; listFlowsByStatistic1(statsType, asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

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
      List<FlowSummaryStatsDTO> result = apiInstance.listFlowsByStatistic1(statsType, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listFlowsByStatistic1");
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

[**List&lt;FlowSummaryStatsDTO&gt;**](FlowSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listFlowsByStatistic2"></a>
# **listFlowsByStatistic2**
> List&lt;FlowSummaryStatsDTO&gt; listFlowsByStatistic2(statsType, pageSize, asc)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

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
      List<FlowSummaryStatsDTO> result = apiInstance.listFlowsByStatistic2(statsType, pageSize, asc);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling InteractiveStatisticsApi#listFlowsByStatistic2");
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

[**List&lt;FlowSummaryStatsDTO&gt;**](FlowSummaryStatsDTO.md)

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
> List&lt;PluginSummaryStatsDTO&gt; listPluginsByStatistic1(statsType, pageSize, asc)

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
      List<PluginSummaryStatsDTO> result = apiInstance.listPluginsByStatistic1(statsType, pageSize, asc);
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

<a id="listPluginsByStatistic2"></a>
# **listPluginsByStatistic2**
> List&lt;PluginSummaryStatsDTO&gt; listPluginsByStatistic2(statsType, asc)

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
      List<PluginSummaryStatsDTO> result = apiInstance.listPluginsByStatistic2(statsType, asc);
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
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic(statsType, pageSize, asc)

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
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic(statsType, pageSize, asc);
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

<a id="listPromptsByStatistic1"></a>
# **listPromptsByStatistic1**
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic1(statsType, pageSize, pageNum, asc)

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
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic1(statsType, pageSize, pageNum, asc);
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

<a id="listPromptsByStatistic2"></a>
# **listPromptsByStatistic2**
> List&lt;PromptSummaryStatsDTO&gt; listPromptsByStatistic2(statsType, asc)

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
      List<PromptSummaryStatsDTO> result = apiInstance.listPromptsByStatistic2(statsType, asc);
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

