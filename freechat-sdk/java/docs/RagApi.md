# RagApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**cancelRagTask**](RagApi.md#cancelRagTask) | **POST** /api/v2/rag/task/cancel/{taskId} | Cancel RAG Task |
| [**createRagTask**](RagApi.md#createRagTask) | **POST** /api/v2/rag/task/{characterUid} | Create RAG Task |
| [**deleteRagTask**](RagApi.md#deleteRagTask) | **DELETE** /api/v2/rag/task/{taskId} | Delete RAG Task |
| [**getRagTask**](RagApi.md#getRagTask) | **GET** /api/v2/rag/task/{taskId} | Get RAG Task |
| [**getRagTaskStatus**](RagApi.md#getRagTaskStatus) | **GET** /api/v2/rag/task/status/{taskId} | Get RAG Task Status |
| [**listRagTasks**](RagApi.md#listRagTasks) | **GET** /api/v2/rag/tasks/{characterUid} | List RAG Tasks |
| [**startRagTask**](RagApi.md#startRagTask) | **POST** /api/v2/rag/task/start/{taskId} | Start RAG Task |
| [**updateRagTask**](RagApi.md#updateRagTask) | **PUT** /api/v2/rag/task/{taskId} | Update RAG Task |


<a id="cancelRagTask"></a>
# **cancelRagTask**
> Boolean cancelRagTask(taskId)

Cancel RAG Task

Cancel a RAG task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be canceled
    try {
      Boolean result = apiInstance.cancelRagTask(taskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#cancelRagTask");
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
| **taskId** | **Long**| The taskId to be canceled | |

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

<a id="createRagTask"></a>
# **createRagTask**
> Long createRagTask(characterUid, ragTaskDTO)

Create RAG Task

Create a RAG task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    String characterUid = "characterUid_example"; // String | The characterUid to be added a RAG task
    RagTaskDTO ragTaskDTO = new RagTaskDTO(); // RagTaskDTO | The RAG task to be added
    try {
      Long result = apiInstance.createRagTask(characterUid, ragTaskDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#createRagTask");
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
| **characterUid** | **String**| The characterUid to be added a RAG task | |
| **ragTaskDTO** | [**RagTaskDTO**](RagTaskDTO.md)| The RAG task to be added | |

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

<a id="deleteRagTask"></a>
# **deleteRagTask**
> Boolean deleteRagTask(taskId)

Delete RAG Task

Delete a RAG task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be deleted
    try {
      Boolean result = apiInstance.deleteRagTask(taskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#deleteRagTask");
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
| **taskId** | **Long**| The taskId to be deleted | |

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

<a id="getRagTask"></a>
# **getRagTask**
> RagTaskDetailsDTO getRagTask(taskId)

Get RAG Task

Get the RAG task details.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be queried
    try {
      RagTaskDetailsDTO result = apiInstance.getRagTask(taskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#getRagTask");
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
| **taskId** | **Long**| The taskId to be queried | |

### Return type

[**RagTaskDetailsDTO**](RagTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getRagTaskStatus"></a>
# **getRagTaskStatus**
> String getRagTaskStatus(taskId)

Get RAG Task Status

Get the RAG task execution status: pending | running | succeeded | failed | canceled.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be queried status
    try {
      String result = apiInstance.getRagTaskStatus(taskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#getRagTaskStatus");
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
| **taskId** | **Long**| The taskId to be queried status | |

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

<a id="listRagTasks"></a>
# **listRagTasks**
> List&lt;RagTaskDetailsDTO&gt; listRagTasks(characterUid)

List RAG Tasks

List the RAG tasks by characterId.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    String characterUid = "characterUid_example"; // String | The characterUid to be queried
    try {
      List<RagTaskDetailsDTO> result = apiInstance.listRagTasks(characterUid);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#listRagTasks");
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
| **characterUid** | **String**| The characterUid to be queried | |

### Return type

[**List&lt;RagTaskDetailsDTO&gt;**](RagTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="startRagTask"></a>
# **startRagTask**
> Boolean startRagTask(taskId)

Start RAG Task

Start a RAG task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be started
    try {
      Boolean result = apiInstance.startRagTask(taskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#startRagTask");
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
| **taskId** | **Long**| The taskId to be started | |

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

<a id="updateRagTask"></a>
# **updateRagTask**
> Boolean updateRagTask(taskId, ragTaskDTO)

Update RAG Task

Update a RAG task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.RagApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    RagApi apiInstance = new RagApi(defaultClient);
    Long taskId = 56L; // Long | The taskId to be updated
    RagTaskDTO ragTaskDTO = new RagTaskDTO(); // RagTaskDTO | The prompt task info to be updated
    try {
      Boolean result = apiInstance.updateRagTask(taskId, ragTaskDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling RagApi#updateRagTask");
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
| **taskId** | **Long**| The taskId to be updated | |
| **ragTaskDTO** | [**RagTaskDTO**](RagTaskDTO.md)| The prompt task info to be updated | |

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

