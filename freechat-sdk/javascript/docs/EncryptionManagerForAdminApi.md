# freechat-sdk.EncryptionManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**encryptText**](EncryptionManagerForAdminApi.md#encryptText) | **GET** /api/v1/admin/encryption/encrypt/{text} | Encrypt Text



## encryptText

> String encryptText(text)

Encrypt Text

Encrypt a piece of text with the built-in key.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.EncryptionManagerForAdminApi();
let text = "text_example"; // String | Text to be encrypted
apiInstance.encryptText(text).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **text** | **String**| Text to be encrypted | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain

