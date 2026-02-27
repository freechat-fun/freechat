# EncryptionManagerForAdminApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**encryptText**](EncryptionManagerForAdminApi.md#encryptText) | **GET** /api/v2/admin/encryption/encrypt/{text} | Encrypt Text



## encryptText

Encrypt Text

Encrypt a piece of text with the built-in key.

### Example

```bash
freechat encryptText text=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **text** | **string** | Text to be encrypted | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

