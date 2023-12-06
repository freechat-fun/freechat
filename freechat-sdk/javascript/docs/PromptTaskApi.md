# freechat-sdk.PromptTaskApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createPromptTask**](PromptTaskApi.md#createPromptTask) | **POST** /api/v1/prompt/task | Add Prompt Task
[**deletePromptTask**](PromptTaskApi.md#deletePromptTask) | **DELETE** /api/v1/prompt/task/{promptTaskId} | Delete Prompt Task
[**getPromptTask**](PromptTaskApi.md#getPromptTask) | **GET** /api/v1/prompt/task/{promptTaskId} | Get Prompt Task
[**updatePromptTask**](PromptTaskApi.md#updatePromptTask) | **PUT** /api/v1/prompt/task/{promptTaskId} | Update Prompt Task



## createPromptTask

> String createPromptTask(promptTaskDTO)

Add Prompt Task

Add a prompt task.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptTaskApi();
let promptTaskDTO = new freechat-sdk.PromptTaskDTO(); // PromptTaskDTO | The prompt task to be added
apiInstance.createPromptTask(promptTaskDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task to be added | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## deletePromptTask

> Boolean deletePromptTask(promptTaskId)

Delete Prompt Task

Delete a prompt task.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptTaskApi();
let promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be deleted
apiInstance.deletePromptTask(promptTaskId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **String**| The promptTaskId to be deleted | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getPromptTask

> PromptTaskDetailsDTO getPromptTask(promptTaskId)

Get Prompt Task

Get the prompt task details.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptTaskApi();
let promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be queried
apiInstance.getPromptTask(promptTaskId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **String**| The promptTaskId to be queried | 

### Return type

[**PromptTaskDetailsDTO**](PromptTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updatePromptTask

> Boolean updatePromptTask(promptTaskId, promptTaskDTO)

Update Prompt Task

Update a prompt task.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptTaskApi();
let promptTaskId = "promptTaskId_example"; // String | The promptTaskId to be updated
let promptTaskDTO = new freechat-sdk.PromptTaskDTO(); // PromptTaskDTO | The prompt task info to be updated
apiInstance.updatePromptTask(promptTaskId, promptTaskDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **String**| The promptTaskId to be updated | 
 **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md)| The prompt task info to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

