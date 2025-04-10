# EncryptionManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**encryptText**](EncryptionManagerForAdminApi.md#encryptText) | **GET** /api/v2/admin/encryption/encrypt/{text} | Encrypt Text |


<a id="encryptText"></a>
# **encryptText**
> String encryptText(text)

Encrypt Text

Encrypt a piece of text with the built-in key.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.EncryptionManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    EncryptionManagerForAdminApi apiInstance = new EncryptionManagerForAdminApi(defaultClient);
    String text = "text_example"; // String | Text to be encrypted
    try {
      String result = apiInstance.encryptText(text);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling EncryptionManagerForAdminApi#encryptText");
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
| **text** | **String**| Text to be encrypted | |

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

