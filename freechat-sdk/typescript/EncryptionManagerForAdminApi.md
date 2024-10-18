# .EncryptionManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**encryptText**](EncryptionManagerForAdminApi.md#encryptText) | **GET** /api/v1/admin/encryption/encrypt/{text} | Encrypt Text


# **encryptText**
> string encryptText()

Encrypt a piece of text with the built-in key.

### Example


```typescript
import { createConfiguration, EncryptionManagerForAdminApi } from '';
import type { EncryptionManagerForAdminApiEncryptTextRequest } from '';

const configuration = createConfiguration();
const apiInstance = new EncryptionManagerForAdminApi(configuration);

const request: EncryptionManagerForAdminApiEncryptTextRequest = {
    // Text to be encrypted
  text: "text_example",
};

const data = await apiInstance.encryptText(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **text** | [**string**] | Text to be encrypted | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


