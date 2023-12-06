# freechat-sdk.AIServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addAiApiKey**](AIServiceApi.md#addAiApiKey) | **POST** /api/v1/ai/apikey | Add Model Provider Credential
[**deleteAiApiKey**](AIServiceApi.md#deleteAiApiKey) | **DELETE** /api/v1/ai/apikey/{id} | Delete Credential of Model Provider
[**disableAiApiKey**](AIServiceApi.md#disableAiApiKey) | **PUT** /api/v1/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enableAiApiKey**](AIServiceApi.md#enableAiApiKey) | **PUT** /api/v1/ai/apikey/enable/{id} | Enable Model Provider Credential
[**getAiApiKey**](AIServiceApi.md#getAiApiKey) | **GET** /api/v1/ai/apikey/{id} | Get credential of Model Provider
[**getAiModelInfo**](AIServiceApi.md#getAiModelInfo) | **GET** /api/v1/ai/model/{modelId} | Get Model Information
[**listAiApiKeys**](AIServiceApi.md#listAiApiKeys) | **GET** /api/v1/ai/apikeys/{provider} | List Credentials of Model Provider
[**listAiModelInfo**](AIServiceApi.md#listAiModelInfo) | **GET** /api/v1/ai/models/{pageSize} | List Models
[**listAiModelInfo1**](AIServiceApi.md#listAiModelInfo1) | **GET** /api/v1/ai/models | List Models
[**listAiModelInfo2**](AIServiceApi.md#listAiModelInfo2) | **GET** /api/v1/ai/models/{pageSize}/{pageNum} | List Models



## addAiApiKey

> Number addAiApiKey(aiApiKeyCreateDTO)

Add Model Provider Credential

Add a credential for the model service.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let aiApiKeyCreateDTO = new freechat-sdk.AiApiKeyCreateDTO(); // AiApiKeyCreateDTO | Model call credential information
apiInstance.addAiApiKey(aiApiKeyCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aiApiKeyCreateDTO** | [**AiApiKeyCreateDTO**](AiApiKeyCreateDTO.md)| Model call credential information | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deleteAiApiKey

> Boolean deleteAiApiKey(id)

Delete Credential of Model Provider

Delete the credential information of the model provider.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let id = 789; // Number | Credential identifier
apiInstance.deleteAiApiKey(id).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**| Credential identifier | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## disableAiApiKey

> Boolean disableAiApiKey(id)

Disable Model Provider Credential

Disable the credential information of the model provider.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let id = 789; // Number | Credential identifier
apiInstance.disableAiApiKey(id).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**| Credential identifier | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## enableAiApiKey

> Boolean enableAiApiKey(id)

Enable Model Provider Credential

Enable the credential information of the model provider.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let id = 789; // Number | Credential identifier
apiInstance.enableAiApiKey(id).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**| Credential identifier | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAiApiKey

> AiApiKeyInfoDTO getAiApiKey(id)

Get credential of Model Provider

Get the credential information of the model provider.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let id = 789; // Number | Credential identifier
apiInstance.getAiApiKey(id).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**| Credential identifier | 

### Return type

[**AiApiKeyInfoDTO**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAiModelInfo

> AiModelInfoDTO getAiModelInfo(modelId)

Get Model Information

Return specific model information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let modelId = "modelId_example"; // String | Model identifier
apiInstance.getAiModelInfo(modelId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **modelId** | **String**| Model identifier | 

### Return type

[**AiModelInfoDTO**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAiApiKeys

> [AiApiKeyInfoDTO] listAiApiKeys(provider)

List Credentials of Model Provider

List all credential information of the model provider.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let provider = "provider_example"; // String | Model provider
apiInstance.listAiApiKeys(provider).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **provider** | **String**| Model provider | 

### Return type

[**[AiApiKeyInfoDTO]**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAiModelInfo

> [AiModelInfoDTO] listAiModelInfo(pageSize)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let pageSize = 789; // Number | Maximum quantity
apiInstance.listAiModelInfo(pageSize).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **Number**| Maximum quantity | 

### Return type

[**[AiModelInfoDTO]**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAiModelInfo1

> [AiModelInfoDTO] listAiModelInfo1()

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
apiInstance.listAiModelInfo1().then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters

This endpoint does not need any parameter.

### Return type

[**[AiModelInfoDTO]**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAiModelInfo2

> [AiModelInfoDTO] listAiModelInfo2(pageSize, pageNum)

List Models

Return model information by page, return the pageNum page, up to pageSize model information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AIServiceApi();
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
apiInstance.listAiModelInfo2(pageSize, pageNum).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 

### Return type

[**[AiModelInfoDTO]**](AiModelInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

