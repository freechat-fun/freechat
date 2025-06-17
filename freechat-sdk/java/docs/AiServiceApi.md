# AiServiceApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addAiApiKey**](AiServiceApi.md#addAiApiKey) | **POST** /api/v2/ai/apikey | Add Model Provider Credential |
| [**deleteAiApiKey**](AiServiceApi.md#deleteAiApiKey) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider |
| [**disableAiApiKey**](AiServiceApi.md#disableAiApiKey) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential |
| [**enableAiApiKey**](AiServiceApi.md#enableAiApiKey) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential |
| [**getAiApiKey**](AiServiceApi.md#getAiApiKey) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider |
| [**listAiApiKeys**](AiServiceApi.md#listAiApiKeys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider |


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

