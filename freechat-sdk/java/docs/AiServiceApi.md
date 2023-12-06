# AiServiceApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addAiApiKey**](AiServiceApi.md#addAiApiKey) | **POST** /api/v1/ai/apikey | Add Model Provider Credential |
| [**deleteAiApiKey**](AiServiceApi.md#deleteAiApiKey) | **DELETE** /api/v1/ai/apikey/{id} | Delete Credential of Model Provider |
| [**disableAiApiKey**](AiServiceApi.md#disableAiApiKey) | **PUT** /api/v1/ai/apikey/disable/{id} | Disable Model Provider Credential |
| [**enableAiApiKey**](AiServiceApi.md#enableAiApiKey) | **PUT** /api/v1/ai/apikey/enable/{id} | Enable Model Provider Credential |
| [**getAiApiKey**](AiServiceApi.md#getAiApiKey) | **GET** /api/v1/ai/apikey/{id} | Get credential of Model Provider |
| [**getAiModelInfo**](AiServiceApi.md#getAiModelInfo) | **GET** /api/v1/ai/model/{modelId} | Get Model Information |
| [**listAiApiKeys**](AiServiceApi.md#listAiApiKeys) | **GET** /api/v1/ai/apikeys/{provider} | List Credentials of Model Provider |
| [**listAiModelInfo**](AiServiceApi.md#listAiModelInfo) | **GET** /api/v1/ai/models/{pageSize} | List Models |
| [**listAiModelInfo1**](AiServiceApi.md#listAiModelInfo1) | **GET** /api/v1/ai/models | List Models |
| [**listAiModelInfo2**](AiServiceApi.md#listAiModelInfo2) | **GET** /api/v1/ai/models/{pageSize}/{pageNum} | List Models |


<a id="addAiApiKey"></a>
# **addAiApiKey**
> Long addAiApiKey(aiApiKeyCreateDTO)

Add Model Provider Credential

Add a credential for the model service.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    AiApiKeyCreateDTO aiApiKeyCreateDTO = new AiApiKeyCreateDTO(); // AiApiKeyCreateDTO | Model call credential information
    try {
      Long result = apiInstance.addAiApiKey(aiApiKeyCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#addAiApiKey");
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
| **aiApiKeyCreateDTO** | [**AiApiKeyCreateDTO**](AiApiKeyCreateDTO.md)| Model call credential information | |

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

<a id="deleteAiApiKey"></a>
# **deleteAiApiKey**
> Boolean deleteAiApiKey(id)

Delete Credential of Model Provider

Delete the credential information of the model provider.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long id = 56L; // Long | Credential identifier
    try {
      Boolean result = apiInstance.deleteAiApiKey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#deleteAiApiKey");
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
| **id** | **Long**| Credential identifier | |

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

<a id="disableAiApiKey"></a>
# **disableAiApiKey**
> Boolean disableAiApiKey(id)

Disable Model Provider Credential

Disable the credential information of the model provider.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long id = 56L; // Long | Credential identifier
    try {
      Boolean result = apiInstance.disableAiApiKey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#disableAiApiKey");
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
| **id** | **Long**| Credential identifier | |

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

<a id="enableAiApiKey"></a>
# **enableAiApiKey**
> Boolean enableAiApiKey(id)

Enable Model Provider Credential

Enable the credential information of the model provider.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long id = 56L; // Long | Credential identifier
    try {
      Boolean result = apiInstance.enableAiApiKey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#enableAiApiKey");
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
| **id** | **Long**| Credential identifier | |

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

<a id="getAiApiKey"></a>
# **getAiApiKey**
> AiApiKeyInfoDTO getAiApiKey(id)

Get credential of Model Provider

Get the credential information of the model provider.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long id = 56L; // Long | Credential identifier
    try {
      AiApiKeyInfoDTO result = apiInstance.getAiApiKey(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#getAiApiKey");
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
| **id** | **Long**| Credential identifier | |

### Return type

[**AiApiKeyInfoDTO**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getAiModelInfo"></a>
# **getAiModelInfo**
> AiModelInfoDTO getAiModelInfo(modelId)

Get Model Information

Return specific model information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    String modelId = "modelId_example"; // String | Model identifier
    try {
      AiModelInfoDTO result = apiInstance.getAiModelInfo(modelId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#getAiModelInfo");
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
| **modelId** | **String**| Model identifier | |

### Return type

[**AiModelInfoDTO**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAiApiKeys"></a>
# **listAiApiKeys**
> List&lt;AiApiKeyInfoDTO&gt; listAiApiKeys(provider)

List Credentials of Model Provider

List all credential information of the model provider.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    String provider = "provider_example"; // String | Model provider
    try {
      List<AiApiKeyInfoDTO> result = apiInstance.listAiApiKeys(provider);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#listAiApiKeys");
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
| **provider** | **String**| Model provider | |

### Return type

[**List&lt;AiApiKeyInfoDTO&gt;**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAiModelInfo"></a>
# **listAiModelInfo**
> List&lt;AiModelInfoDTO&gt; listAiModelInfo(pageSize)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long pageSize = 56L; // Long | Maximum quantity
    try {
      List<AiModelInfoDTO> result = apiInstance.listAiModelInfo(pageSize);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#listAiModelInfo");
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
| **pageSize** | **Long**| Maximum quantity | |

### Return type

[**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAiModelInfo1"></a>
# **listAiModelInfo1**
> List&lt;AiModelInfoDTO&gt; listAiModelInfo1()

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    try {
      List<AiModelInfoDTO> result = apiInstance.listAiModelInfo1();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#listAiModelInfo1");
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

[**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAiModelInfo2"></a>
# **listAiModelInfo2**
> List&lt;AiModelInfoDTO&gt; listAiModelInfo2(pageSize, pageNum)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AiServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AiServiceApi apiInstance = new AiServiceApi(defaultClient);
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    try {
      List<AiModelInfoDTO> result = apiInstance.listAiModelInfo2(pageSize, pageNum);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AiServiceApi#listAiModelInfo2");
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
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |

### Return type

[**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

