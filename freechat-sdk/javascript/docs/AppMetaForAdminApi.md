# freechat-sdk.AppMetaForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**expose**](AppMetaForAdminApi.md#expose) | **GET** /api/v1/admin/app/expose | Expose DTO definitions
[**getAppMeta**](AppMetaForAdminApi.md#getAppMeta) | **GET** /api/v1/admin/app/meta | Get Application Information



## expose

> String expose(openAiParam, qwenParam, aiForPromptResult)

Expose DTO definitions

This method does nothing.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppMetaForAdminApi();
let openAiParam = new freechat-sdk.OpenAiParamDTO(); // OpenAiParamDTO | 
let qwenParam = new freechat-sdk.QwenParamDTO(); // QwenParamDTO | 
let aiForPromptResult = new freechat-sdk.LlmResultDTO(); // LlmResultDTO | 
apiInstance.expose(openAiParam, qwenParam, aiForPromptResult).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **openAiParam** | [**OpenAiParamDTO**](.md)|  | 
 **qwenParam** | [**QwenParamDTO**](.md)|  | 
 **aiForPromptResult** | [**LlmResultDTO**](.md)|  | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAppMeta

> AppMetaDTO getAppMeta()

Get Application Information

Get application information to accurately locate the corresponding project version.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppMetaForAdminApi();
apiInstance.getAppMeta().then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters

This endpoint does not need any parameter.

### Return type

[**AppMetaDTO**](AppMetaDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

