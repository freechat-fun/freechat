# PromptTaskApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createPromptTask**](PromptTaskApi.md#createPromptTask) | **POST** /api/v2/prompt/task | Create Prompt Task |
| [**deletePromptTask**](PromptTaskApi.md#deletePromptTask) | **DELETE** /api/v2/prompt/task/{promptTaskId} | Delete Prompt Task |
| [**getPromptTask**](PromptTaskApi.md#getPromptTask) | **GET** /api/v2/prompt/task/{promptTaskId} | Get Prompt Task |
| [**updatePromptTask**](PromptTaskApi.md#updatePromptTask) | **PUT** /api/v2/prompt/task/{promptTaskId} | Update Prompt Task |


<a id="createPromptTask"></a>
# **createPromptTask**
> String createPromptTask(promptTaskDTO)

Create Prompt Task

Create a prompt task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptTaskApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptTaskApi apiInstance = new PromptTaskApi(defaultClient);
    PromptTaskDTO promptTaskDTO = new PromptTaskDTO(); // PromptTaskDTO | The prompt task to be added
    try {
      String result = apiInstance.createPromptTask(promptTaskDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptTaskApi#createPromptTask");
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
| **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task to be added | |

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

<a id="deletePromptTask"></a>
# **deletePromptTask**
> Boolean deletePromptTask(promptTaskId)

Delete Prompt Task

Delete a prompt task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptTaskApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptTaskApi apiInstance = new PromptTaskApi(defaultClient);
    String promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be deleted
    try {
      Boolean result = apiInstance.deletePromptTask(promptTaskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptTaskApi#deletePromptTask");
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
| **promptTaskId** | **String**| The promptTaskId to be deleted | |

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

<a id="getPromptTask"></a>
# **getPromptTask**
> PromptTaskDetailsDTO getPromptTask(promptTaskId)

Get Prompt Task

Get the prompt task details.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptTaskApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptTaskApi apiInstance = new PromptTaskApi(defaultClient);
    String promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be queried
    try {
      PromptTaskDetailsDTO result = apiInstance.getPromptTask(promptTaskId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptTaskApi#getPromptTask");
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
| **promptTaskId** | **String**| The promptTaskId to be queried | |

### Return type

[**PromptTaskDetailsDTO**](PromptTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updatePromptTask"></a>
# **updatePromptTask**
> Boolean updatePromptTask(promptTaskId, promptTaskDTO)

Update Prompt Task

Update a prompt task.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptTaskApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptTaskApi apiInstance = new PromptTaskApi(defaultClient);
    String promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be updated
    PromptTaskDTO promptTaskDTO = new PromptTaskDTO(); // PromptTaskDTO | The prompt task info to be updated
    try {
      Boolean result = apiInstance.updatePromptTask(promptTaskId, promptTaskDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptTaskApi#updatePromptTask");
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
| **promptTaskId** | **String**| The promptTaskId to be updated | |
| **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task info to be updated | |

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

