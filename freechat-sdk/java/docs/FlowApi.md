# FlowApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**batchSearchFlowDetails**](FlowApi.md#batchSearchFlowDetails) | **POST** /api/v1/flow/batch/details/search | Batch Search Flow Details |
| [**batchSearchFlowSummary**](FlowApi.md#batchSearchFlowSummary) | **POST** /api/v1/flow/batch/search | Batch Search Flow Summaries |
| [**cloneFlow**](FlowApi.md#cloneFlow) | **POST** /api/v1/flow/clone/{flowId} | Clone Flow |
| [**cloneFlows**](FlowApi.md#cloneFlows) | **POST** /api/v1/flow/batch/clone | Batch Clone Flows |
| [**countFlows**](FlowApi.md#countFlows) | **POST** /api/v1/flow/count | Calculate Number of Flows |
| [**createFlow**](FlowApi.md#createFlow) | **POST** /api/v1/flow | Create Flow |
| [**createFlows**](FlowApi.md#createFlows) | **POST** /api/v1/flow/batch | Batch Create Flows |
| [**deleteFlow**](FlowApi.md#deleteFlow) | **DELETE** /api/v1/flow/{flowId} | Delete Flow |
| [**deleteFlows**](FlowApi.md#deleteFlows) | **DELETE** /api/v1/flow/batch/delete | Batch Delete Flows |
| [**getFlowDetails**](FlowApi.md#getFlowDetails) | **GET** /api/v1/flow/details/{flowId} | Get Flow Details |
| [**getFlowSummary**](FlowApi.md#getFlowSummary) | **GET** /api/v1/flow/summary/{flowId} | Get Flow Summary |
| [**listFlowVersionsByName**](FlowApi.md#listFlowVersionsByName) | **POST** /api/v1/flow/versions/{name} | List Versions by Flow Name |
| [**publishFlow**](FlowApi.md#publishFlow) | **POST** /api/v1/flow/publish/{flowId}/{visibility} | Publish Flow |
| [**searchFlowDetails**](FlowApi.md#searchFlowDetails) | **POST** /api/v1/flow/details/search | Search Flow Details |
| [**searchFlowSummary**](FlowApi.md#searchFlowSummary) | **POST** /api/v1/flow/search | Search Flow Summary |
| [**updateFlow**](FlowApi.md#updateFlow) | **PUT** /api/v1/flow/{flowId} | Update Flow |


<a id="batchSearchFlowDetails"></a>
# **batchSearchFlowDetails**
> List&lt;List&lt;FlowDetailsDTO&gt;&gt; batchSearchFlowDetails(flowQueryDTO)

Batch Search Flow Details

Batch call shortcut for /api/v1/flow/details/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    List<FlowQueryDTO> flowQueryDTO = Arrays.asList(); // List<FlowQueryDTO> | Query conditions
    try {
      List<List<FlowDetailsDTO>> result = apiInstance.batchSearchFlowDetails(flowQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#batchSearchFlowDetails");
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
| **flowQueryDTO** | [**List&lt;FlowQueryDTO&gt;**](FlowQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;FlowDetailsDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="batchSearchFlowSummary"></a>
# **batchSearchFlowSummary**
> List&lt;List&lt;FlowSummaryDTO&gt;&gt; batchSearchFlowSummary(flowQueryDTO)

Batch Search Flow Summaries

Batch call shortcut for /api/v1/flow/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    List<FlowQueryDTO> flowQueryDTO = Arrays.asList(); // List<FlowQueryDTO> | Query conditions
    try {
      List<List<FlowSummaryDTO>> result = apiInstance.batchSearchFlowSummary(flowQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#batchSearchFlowSummary");
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
| **flowQueryDTO** | [**List&lt;FlowQueryDTO&gt;**](FlowQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;FlowSummaryDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="cloneFlow"></a>
# **cloneFlow**
> String cloneFlow(flowId)

Clone Flow

Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | The referenced flowId
    try {
      String result = apiInstance.cloneFlow(flowId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#cloneFlow");
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
| **flowId** | **String**| The referenced flowId | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="cloneFlows"></a>
# **cloneFlows**
> List&lt;String&gt; cloneFlows(requestBody)

Batch Clone Flows

Batch clone multiple flows. Ensure transactionality, return the flowId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    List<String> requestBody = Arrays.asList(); // List<String> | List of flow information to be created
    try {
      List<String> result = apiInstance.cloneFlows(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#cloneFlows");
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
| **requestBody** | [**List&lt;String&gt;**](String.md)| List of flow information to be created | |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="countFlows"></a>
# **countFlows**
> Long countFlows(flowQueryDTO)

Calculate Number of Flows

Calculate the number of flows according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    FlowQueryDTO flowQueryDTO = new FlowQueryDTO(); // FlowQueryDTO | Query conditions
    try {
      Long result = apiInstance.countFlows(flowQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#countFlows");
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
| **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | |

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

<a id="createFlow"></a>
# **createFlow**
> String createFlow(flowCreateDTO)

Create Flow

Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    FlowCreateDTO flowCreateDTO = new FlowCreateDTO(); // FlowCreateDTO | Information of the flow to be created
    try {
      String result = apiInstance.createFlow(flowCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#createFlow");
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
| **flowCreateDTO** | [**FlowCreateDTO**](FlowCreateDTO.md)| Information of the flow to be created | |

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

<a id="createFlows"></a>
# **createFlows**
> List&lt;String&gt; createFlows(flowCreateDTO)

Batch Create Flows

Batch create multiple flows. Ensure transactionality, return the flowId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    List<FlowCreateDTO> flowCreateDTO = Arrays.asList(); // List<FlowCreateDTO> | List of flow information to be created
    try {
      List<String> result = apiInstance.createFlows(flowCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#createFlows");
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
| **flowCreateDTO** | [**List&lt;FlowCreateDTO&gt;**](FlowCreateDTO.md)| List of flow information to be created | |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteFlow"></a>
# **deleteFlow**
> Boolean deleteFlow(flowId)

Delete Flow

Delete flow. Return success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | FlowId to be deleted
    try {
      Boolean result = apiInstance.deleteFlow(flowId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#deleteFlow");
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
| **flowId** | **String**| FlowId to be deleted | |

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

<a id="deleteFlows"></a>
# **deleteFlows**
> List&lt;String&gt; deleteFlows(requestBody)

Batch Delete Flows

Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    List<String> requestBody = Arrays.asList(); // List<String> | List of flowId to be deleted
    try {
      List<String> result = apiInstance.deleteFlows(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#deleteFlows");
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
| **requestBody** | [**List&lt;String&gt;**](String.md)| List of flowId to be deleted | |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getFlowDetails"></a>
# **getFlowDetails**
> FlowDetailsDTO getFlowDetails(flowId)

Get Flow Details

Get flow detailed information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | FlowId to be obtained
    try {
      FlowDetailsDTO result = apiInstance.getFlowDetails(flowId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#getFlowDetails");
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
| **flowId** | **String**| FlowId to be obtained | |

### Return type

[**FlowDetailsDTO**](FlowDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getFlowSummary"></a>
# **getFlowSummary**
> FlowSummaryDTO getFlowSummary(flowId)

Get Flow Summary

Get flow summary information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | flowId to be obtained
    try {
      FlowSummaryDTO result = apiInstance.getFlowSummary(flowId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#getFlowSummary");
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
| **flowId** | **String**| flowId to be obtained | |

### Return type

[**FlowSummaryDTO**](FlowSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listFlowVersionsByName"></a>
# **listFlowVersionsByName**
> List&lt;FlowItemForNameDTO&gt; listFlowVersionsByName(name)

List Versions by Flow Name

List the versions and corresponding flowIds by flow name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String name = "name_example"; // String | Flow name
    try {
      List<FlowItemForNameDTO> result = apiInstance.listFlowVersionsByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#listFlowVersionsByName");
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
| **name** | **String**| Flow name | |

### Return type

[**List&lt;FlowItemForNameDTO&gt;**](FlowItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="publishFlow"></a>
# **publishFlow**
> String publishFlow(flowId, visibility)

Publish Flow

Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | The flowId to be published
    String visibility = "visibility_example"; // String | Visibility: public | private | ...
    try {
      String result = apiInstance.publishFlow(flowId, visibility);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#publishFlow");
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
| **flowId** | **String**| The flowId to be published | |
| **visibility** | **String**| Visibility: public | private | ... | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchFlowDetails"></a>
# **searchFlowDetails**
> List&lt;FlowDetailsDTO&gt; searchFlowDetails(flowQueryDTO)

Search Flow Details

Same as /api/v1/flow/search, but returns detailed information of the flow.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    FlowQueryDTO flowQueryDTO = new FlowQueryDTO(); // FlowQueryDTO | Query conditions
    try {
      List<FlowDetailsDTO> result = apiInstance.searchFlowDetails(flowQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#searchFlowDetails");
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
| **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;FlowDetailsDTO&gt;**](FlowDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchFlowSummary"></a>
# **searchFlowSummary**
> List&lt;FlowSummaryDTO&gt; searchFlowSummary(flowQueryDTO)

Search Flow Summary

Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    FlowQueryDTO flowQueryDTO = new FlowQueryDTO(); // FlowQueryDTO | Query conditions
    try {
      List<FlowSummaryDTO> result = apiInstance.searchFlowSummary(flowQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#searchFlowSummary");
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
| **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;FlowSummaryDTO&gt;**](FlowSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateFlow"></a>
# **updateFlow**
> Boolean updateFlow(flowId, flowUpdateDTO)

Update Flow

Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.FlowApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    FlowApi apiInstance = new FlowApi(defaultClient);
    String flowId = "flowId_example"; // String | FlowId to be updated
    FlowUpdateDTO flowUpdateDTO = new FlowUpdateDTO(); // FlowUpdateDTO | Flow information to be updated
    try {
      Boolean result = apiInstance.updateFlow(flowId, flowUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FlowApi#updateFlow");
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
| **flowId** | **String**| FlowId to be updated | |
| **flowUpdateDTO** | [**FlowUpdateDTO**](FlowUpdateDTO.md)| Flow information to be updated | |

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

