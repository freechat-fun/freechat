# PromptApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**applyPromptRef**](PromptApi.md#applyPromptRef) | **POST** /api/v2/prompt/apply/ref | Apply Parameters to Prompt Record |
| [**applyPromptTemplate**](PromptApi.md#applyPromptTemplate) | **POST** /api/v2/prompt/apply/template | Apply Parameters to Prompt Template |
| [**batchSearchPromptDetails**](PromptApi.md#batchSearchPromptDetails) | **POST** /api/v2/prompt/batch/details/search | Batch Search Prompt Details |
| [**batchSearchPromptSummary**](PromptApi.md#batchSearchPromptSummary) | **POST** /api/v2/prompt/batch/search | Batch Search Prompt Summaries |
| [**clonePrompt**](PromptApi.md#clonePrompt) | **POST** /api/v2/prompt/clone/{promptId} | Clone Prompt |
| [**clonePrompts**](PromptApi.md#clonePrompts) | **POST** /api/v2/prompt/batch/clone | Batch Clone Prompts |
| [**countPrompts**](PromptApi.md#countPrompts) | **POST** /api/v2/prompt/count | Calculate Number of Prompts |
| [**countPublicPrompts**](PromptApi.md#countPublicPrompts) | **POST** /api/v2/public/prompt/count | Calculate Number of Public Prompts |
| [**createPrompt**](PromptApi.md#createPrompt) | **POST** /api/v2/prompt | Create Prompt |
| [**createPrompts**](PromptApi.md#createPrompts) | **POST** /api/v2/prompt/batch | Batch Create Prompts |
| [**deletePrompt**](PromptApi.md#deletePrompt) | **DELETE** /api/v2/prompt/{promptId} | Delete Prompt |
| [**deletePromptByName**](PromptApi.md#deletePromptByName) | **DELETE** /api/v2/prompt/name/{name} | Delete Prompt by Name |
| [**deletePrompts**](PromptApi.md#deletePrompts) | **DELETE** /api/v2/prompt/batch | Batch Delete Prompts |
| [**existsPromptName**](PromptApi.md#existsPromptName) | **GET** /api/v2/prompt/exists/name/{name} | Check If Prompt Name Exists |
| [**getPromptDetails**](PromptApi.md#getPromptDetails) | **GET** /api/v2/prompt/details/{promptId} | Get Prompt Details |
| [**getPromptSummary**](PromptApi.md#getPromptSummary) | **GET** /api/v2/prompt/summary/{promptId} | Get Prompt Summary |
| [**listPromptVersionsByName**](PromptApi.md#listPromptVersionsByName) | **POST** /api/v2/prompt/versions/{name} | List Versions by Prompt Name |
| [**newPromptName**](PromptApi.md#newPromptName) | **GET** /api/v2/prompt/create/name/{desired} | Create New Prompt Name |
| [**publishPrompt**](PromptApi.md#publishPrompt) | **POST** /api/v2/prompt/publish/{promptId}/{visibility} | Publish Prompt |
| [**searchPromptDetails**](PromptApi.md#searchPromptDetails) | **POST** /api/v2/prompt/details/search | Search Prompt Details |
| [**searchPromptSummary**](PromptApi.md#searchPromptSummary) | **POST** /api/v2/prompt/search | Search Prompt Summary |
| [**searchPublicPromptSummary**](PromptApi.md#searchPublicPromptSummary) | **POST** /api/v2/public/prompt/search | Search Public Prompt Summary |
| [**sendPrompt**](PromptApi.md#sendPrompt) | **POST** /api/v2/prompt/send | Send Prompt |
| [**streamSendPrompt**](PromptApi.md#streamSendPrompt) | **POST** /api/v2/prompt/send/stream | Send Prompt by Streaming Back |
| [**updatePrompt**](PromptApi.md#updatePrompt) | **PUT** /api/v2/prompt/{promptId} | Update Prompt |


<a id="applyPromptRef"></a>
# **applyPromptRef**
> String applyPromptRef(promptRefDTO)

Apply Parameters to Prompt Record

Apply parameters to prompt record.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptRefDTO promptRefDTO = new PromptRefDTO(); // PromptRefDTO | Prompt record
    try {
      String result = apiInstance.applyPromptRef(promptRefDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#applyPromptRef");
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
| **promptRefDTO** | [**PromptRefDTO**](PromptRefDTO.md)| Prompt record | |

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

<a id="applyPromptTemplate"></a>
# **applyPromptTemplate**
> String applyPromptTemplate(promptTemplateDTO)

Apply Parameters to Prompt Template

Apply parameters to prompt template.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptTemplateDTO promptTemplateDTO = new PromptTemplateDTO(); // PromptTemplateDTO | String type prompt template
    try {
      String result = apiInstance.applyPromptTemplate(promptTemplateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#applyPromptTemplate");
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
| **promptTemplateDTO** | [**PromptTemplateDTO**](PromptTemplateDTO.md)| String type prompt template | |

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

<a id="batchSearchPromptDetails"></a>
# **batchSearchPromptDetails**
> List&lt;List&lt;PromptDetailsDTO&gt;&gt; batchSearchPromptDetails(promptQueryDTO)

Batch Search Prompt Details

Batch call shortcut for /api/v2/prompt/details/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    List<PromptQueryDTO> promptQueryDTO = Arrays.asList(); // List<PromptQueryDTO> | Query conditions
    try {
      List<List<PromptDetailsDTO>> result = apiInstance.batchSearchPromptDetails(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#batchSearchPromptDetails");
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
| **promptQueryDTO** | [**List&lt;PromptQueryDTO&gt;**](PromptQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;PromptDetailsDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="batchSearchPromptSummary"></a>
# **batchSearchPromptSummary**
> List&lt;List&lt;PromptSummaryDTO&gt;&gt; batchSearchPromptSummary(promptQueryDTO)

Batch Search Prompt Summaries

Batch call shortcut for /api/v2/prompt/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    List<PromptQueryDTO> promptQueryDTO = Arrays.asList(); // List<PromptQueryDTO> | Query conditions
    try {
      List<List<PromptSummaryDTO>> result = apiInstance.batchSearchPromptSummary(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#batchSearchPromptSummary");
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
| **promptQueryDTO** | [**List&lt;PromptQueryDTO&gt;**](PromptQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;PromptSummaryDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="clonePrompt"></a>
# **clonePrompt**
> Long clonePrompt(promptId)

Clone Prompt

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | The referenced promptId
    try {
      Long result = apiInstance.clonePrompt(promptId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#clonePrompt");
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
| **promptId** | **Long**| The referenced promptId | |

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

<a id="clonePrompts"></a>
# **clonePrompts**
> List&lt;Long&gt; clonePrompts(requestBody)

Batch Clone Prompts

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    List<Long> requestBody = Arrays.asList(); // List<Long> | List of prompt information to be created
    try {
      List<Long> result = apiInstance.clonePrompts(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#clonePrompts");
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
| **requestBody** | [**List&lt;Long&gt;**](Long.md)| List of prompt information to be created | |

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

<a id="countPrompts"></a>
# **countPrompts**
> Long countPrompts(promptQueryDTO)

Calculate Number of Prompts

Calculate the number of prompts according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptQueryDTO promptQueryDTO = new PromptQueryDTO(); // PromptQueryDTO | Query conditions
    try {
      Long result = apiInstance.countPrompts(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#countPrompts");
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
| **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | |

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

<a id="countPublicPrompts"></a>
# **countPublicPrompts**
> Long countPublicPrompts(promptQueryDTO)

Calculate Number of Public Prompts

Calculate the number of prompts according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptQueryDTO promptQueryDTO = new PromptQueryDTO(); // PromptQueryDTO | Query conditions
    try {
      Long result = apiInstance.countPublicPrompts(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#countPublicPrompts");
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
| **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | |

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

<a id="createPrompt"></a>
# **createPrompt**
> Long createPrompt(promptCreateDTO)

Create Prompt

Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptCreateDTO promptCreateDTO = new PromptCreateDTO(); // PromptCreateDTO | Information of the prompt to be created
    try {
      Long result = apiInstance.createPrompt(promptCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#createPrompt");
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
| **promptCreateDTO** | [**PromptCreateDTO**](PromptCreateDTO.md)| Information of the prompt to be created | |

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

<a id="createPrompts"></a>
# **createPrompts**
> List&lt;Long&gt; createPrompts(promptCreateDTO)

Batch Create Prompts

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    List<PromptCreateDTO> promptCreateDTO = Arrays.asList(); // List<PromptCreateDTO> | List of prompt information to be created
    try {
      List<Long> result = apiInstance.createPrompts(promptCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#createPrompts");
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
| **promptCreateDTO** | [**List&lt;PromptCreateDTO&gt;**](PromptCreateDTO.md)| List of prompt information to be created | |

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

<a id="deletePrompt"></a>
# **deletePrompt**
> Boolean deletePrompt(promptId)

Delete Prompt

Delete prompt. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | The promptId to be deleted
    try {
      Boolean result = apiInstance.deletePrompt(promptId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#deletePrompt");
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
| **promptId** | **Long**| The promptId to be deleted | |

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

<a id="deletePromptByName"></a>
# **deletePromptByName**
> List&lt;Long&gt; deletePromptByName(name)

Delete Prompt by Name

Delete prompt by name. return the list of successfully deleted promptIds.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    String name = "name_example"; // String | The prompt name to be deleted
    try {
      List<Long> result = apiInstance.deletePromptByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#deletePromptByName");
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
| **name** | **String**| The prompt name to be deleted | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deletePrompts"></a>
# **deletePrompts**
> List&lt;Long&gt; deletePrompts(requestBody)

Batch Delete Prompts

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    List<Long> requestBody = Arrays.asList(); // List<Long> | List of promptIds to be deleted
    try {
      List<Long> result = apiInstance.deletePrompts(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#deletePrompts");
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
| **requestBody** | [**List&lt;Long&gt;**](Long.md)| List of promptIds to be deleted | |

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

<a id="existsPromptName"></a>
# **existsPromptName**
> Boolean existsPromptName(name)

Check If Prompt Name Exists

Check if the prompt name already exists.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    String name = "name_example"; // String | Name
    try {
      Boolean result = apiInstance.existsPromptName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#existsPromptName");
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
| **name** | **String**| Name | |

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

<a id="getPromptDetails"></a>
# **getPromptDetails**
> PromptDetailsDTO getPromptDetails(promptId)

Get Prompt Details

Get prompt detailed information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | PromptId to be obtained
    try {
      PromptDetailsDTO result = apiInstance.getPromptDetails(promptId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#getPromptDetails");
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
| **promptId** | **Long**| PromptId to be obtained | |

### Return type

[**PromptDetailsDTO**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getPromptSummary"></a>
# **getPromptSummary**
> PromptSummaryDTO getPromptSummary(promptId)

Get Prompt Summary

Get prompt summary information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | PromptId to be obtained
    try {
      PromptSummaryDTO result = apiInstance.getPromptSummary(promptId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#getPromptSummary");
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
| **promptId** | **Long**| PromptId to be obtained | |

### Return type

[**PromptSummaryDTO**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listPromptVersionsByName"></a>
# **listPromptVersionsByName**
> List&lt;PromptItemForNameDTO&gt; listPromptVersionsByName(name)

List Versions by Prompt Name

List the versions and corresponding promptIds by prompt name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    String name = "name_example"; // String | Prompt name
    try {
      List<PromptItemForNameDTO> result = apiInstance.listPromptVersionsByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#listPromptVersionsByName");
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
| **name** | **String**| Prompt name | |

### Return type

[**List&lt;PromptItemForNameDTO&gt;**](PromptItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="newPromptName"></a>
# **newPromptName**
> String newPromptName(desired)

Create New Prompt Name

Create a new prompt name starting with a desired name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    String desired = "desired_example"; // String | Desired name
    try {
      String result = apiInstance.newPromptName(desired);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#newPromptName");
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
| **desired** | **String**| Desired name | |

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

<a id="publishPrompt"></a>
# **publishPrompt**
> Long publishPrompt(promptId, visibility)

Publish Prompt

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | The promptId to be published
    String visibility = "visibility_example"; // String | Visibility: public | private | ...
    try {
      Long result = apiInstance.publishPrompt(promptId, visibility);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#publishPrompt");
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
| **promptId** | **Long**| The promptId to be published | |
| **visibility** | **String**| Visibility: public | private | ... | |

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

<a id="searchPromptDetails"></a>
# **searchPromptDetails**
> List&lt;PromptDetailsDTO&gt; searchPromptDetails(promptQueryDTO)

Search Prompt Details

Same as /api/v2/prompt/search, but returns detailed information of the prompt.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptQueryDTO promptQueryDTO = new PromptQueryDTO(); // PromptQueryDTO | Query conditions
    try {
      List<PromptDetailsDTO> result = apiInstance.searchPromptDetails(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#searchPromptDetails");
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
| **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;PromptDetailsDTO&gt;**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchPromptSummary"></a>
# **searchPromptSummary**
> List&lt;PromptSummaryDTO&gt; searchPromptSummary(promptQueryDTO)

Search Prompt Summary

Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptQueryDTO promptQueryDTO = new PromptQueryDTO(); // PromptQueryDTO | Query conditions
    try {
      List<PromptSummaryDTO> result = apiInstance.searchPromptSummary(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#searchPromptSummary");
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
| **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;PromptSummaryDTO&gt;**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchPublicPromptSummary"></a>
# **searchPublicPromptSummary**
> List&lt;PromptSummaryDTO&gt; searchPublicPromptSummary(promptQueryDTO)

Search Public Prompt Summary

Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptQueryDTO promptQueryDTO = new PromptQueryDTO(); // PromptQueryDTO | Query conditions
    try {
      List<PromptSummaryDTO> result = apiInstance.searchPublicPromptSummary(promptQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#searchPublicPromptSummary");
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
| **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;PromptSummaryDTO&gt;**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="sendPrompt"></a>
# **sendPrompt**
> LlmResultDTO sendPrompt(promptAiParamDTO)

Send Prompt

Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptAiParamDTO promptAiParamDTO = new PromptAiParamDTO(); // PromptAiParamDTO | Call parameters
    try {
      LlmResultDTO result = apiInstance.sendPrompt(promptAiParamDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#sendPrompt");
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
| **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | |

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="streamSendPrompt"></a>
# **streamSendPrompt**
> SseEmitter streamSendPrompt(promptAiParamDTO)

Send Prompt by Streaming Back

Refer to /api/v2/prompt/send, stream back chunks of the response.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    PromptAiParamDTO promptAiParamDTO = new PromptAiParamDTO(); // PromptAiParamDTO | Call parameters
    try {
      SseEmitter result = apiInstance.streamSendPrompt(promptAiParamDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#streamSendPrompt");
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
| **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | |

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updatePrompt"></a>
# **updatePrompt**
> Boolean updatePrompt(promptId, promptUpdateDTO)

Update Prompt

Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.PromptApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PromptApi apiInstance = new PromptApi(defaultClient);
    Long promptId = 56L; // Long | The promptId to be updated
    PromptUpdateDTO promptUpdateDTO = new PromptUpdateDTO(); // PromptUpdateDTO | The prompt information to be updated
    try {
      Boolean result = apiInstance.updatePrompt(promptId, promptUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PromptApi#updatePrompt");
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
| **promptId** | **Long**| The promptId to be updated | |
| **promptUpdateDTO** | [**PromptUpdateDTO**](PromptUpdateDTO.md)| The prompt information to be updated | |

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

