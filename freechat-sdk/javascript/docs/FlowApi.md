# freechat-sdk.FlowApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchFlowDetails**](FlowApi.md#batchSearchFlowDetails) | **POST** /api/v1/flow/batch/details/search | Batch Search Flow Details
[**batchSearchFlowSummary**](FlowApi.md#batchSearchFlowSummary) | **POST** /api/v1/flow/batch/search | Batch Search Flow Summaries
[**cloneFlow**](FlowApi.md#cloneFlow) | **POST** /api/v1/flow/clone/{flowId} | Clone Flow
[**cloneFlows**](FlowApi.md#cloneFlows) | **POST** /api/v1/flow/batch/clone | Batch Clone Flows
[**countFlows**](FlowApi.md#countFlows) | **POST** /api/v1/flow/count | Calculate Number of Flows
[**createFlow**](FlowApi.md#createFlow) | **POST** /api/v1/flow | Create Flow
[**createFlows**](FlowApi.md#createFlows) | **POST** /api/v1/flow/batch | Batch Create Flows
[**deleteFlow**](FlowApi.md#deleteFlow) | **DELETE** /api/v1/flow/{flowId} | Delete Flow
[**deleteFlows**](FlowApi.md#deleteFlows) | **DELETE** /api/v1/flow/batch/delete | Batch Delete Flows
[**getFlowDetails**](FlowApi.md#getFlowDetails) | **GET** /api/v1/flow/details/{flowId} | Get Flow Details
[**getFlowSummary**](FlowApi.md#getFlowSummary) | **GET** /api/v1/flow/summary/{flowId} | Get Flow Summary
[**listFlowVersionsByName**](FlowApi.md#listFlowVersionsByName) | **POST** /api/v1/flow/versions/{name} | List Versions by Flow Name
[**publishFlow**](FlowApi.md#publishFlow) | **POST** /api/v1/flow/publish/{flowId}/{visibility} | Publish Flow
[**searchFlowDetails**](FlowApi.md#searchFlowDetails) | **POST** /api/v1/flow/details/search | Search Flow Details
[**searchFlowSummary**](FlowApi.md#searchFlowSummary) | **POST** /api/v1/flow/search | Search Flow Summary
[**updateFlow**](FlowApi.md#updateFlow) | **PUT** /api/v1/flow/{flowId} | Update Flow



## batchSearchFlowDetails

> [[FlowDetailsDTO]] batchSearchFlowDetails(flowQueryDTO)

Batch Search Flow Details

Batch call shortcut for /api/v1/flow/details/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [FlowQueryDTO] | Query conditions
apiInstance.batchSearchFlowDetails(flowQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | [**[FlowQueryDTO]**](FlowQueryDTO.md)| Query conditions | 

### Return type

**[[FlowDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## batchSearchFlowSummary

> [[FlowSummaryDTO]] batchSearchFlowSummary(flowQueryDTO)

Batch Search Flow Summaries

Batch call shortcut for /api/v1/flow/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [FlowQueryDTO] | Query conditions
apiInstance.batchSearchFlowSummary(flowQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | [**[FlowQueryDTO]**](FlowQueryDTO.md)| Query conditions | 

### Return type

**[[FlowSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## cloneFlow

> String cloneFlow(flowId)

Clone Flow

Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | The referenced flowId
apiInstance.cloneFlow(flowId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| The referenced flowId | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## cloneFlows

> [String] cloneFlows(requestBody)

Batch Clone Flows

Batch clone multiple flows. Ensure transactionality, return the flowId list after success.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let requestBody = ["null"]; // [String] | List of flow information to be created
apiInstance.cloneFlows(requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**[String]**](String.md)| List of flow information to be created | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## countFlows

> Number countFlows(flowQueryDTO)

Calculate Number of Flows

Calculate the number of flows according to the specified query conditions.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowQueryDTO = new freechat-sdk.FlowQueryDTO(); // FlowQueryDTO | Query conditions
apiInstance.countFlows(flowQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## createFlow

> String createFlow(flowCreateDTO)

Create Flow

Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowCreateDTO = {"name":"A Test Flow","description":"A flow description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test","demo"],"aiModels":["123"]}; // FlowCreateDTO | Information of the flow to be created
apiInstance.createFlow(flowCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowCreateDTO** | [**FlowCreateDTO**](FlowCreateDTO.md)| Information of the flow to be created | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## createFlows

> [String] createFlows(flowCreateDTO)

Batch Create Flows

Batch create multiple flows. Ensure transactionality, return the flowId list after success.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowCreateDTO = [{"name":"First Test Flow","description":"First flow description","format":"langflow","config":"{}","parameters":"{\"name\": null}","tags":["test1","demo1"],"aiModels":["123"]},{"name":"Second Test Flow","visibility":"public","description":"Second flow description","format":"langflow","config":"{}","parameters":"{\"robot\": null}","tags":["test2","demo2"],"aiModels":["123","456"]}]; // [FlowCreateDTO] | List of flow information to be created
apiInstance.createFlows(flowCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowCreateDTO** | [**[FlowCreateDTO]**](FlowCreateDTO.md)| List of flow information to be created | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deleteFlow

> Boolean deleteFlow(flowId)

Delete Flow

Delete flow. Return success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | FlowId to be deleted
apiInstance.deleteFlow(flowId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| FlowId to be deleted | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## deleteFlows

> [String] deleteFlows(requestBody)

Batch Delete Flows

Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let requestBody = ["null"]; // [String] | List of flowId to be deleted
apiInstance.deleteFlows(requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**[String]**](String.md)| List of flowId to be deleted | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## getFlowDetails

> FlowDetailsDTO getFlowDetails(flowId)

Get Flow Details

Get flow detailed information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | FlowId to be obtained
apiInstance.getFlowDetails(flowId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| FlowId to be obtained | 

### Return type

[**FlowDetailsDTO**](FlowDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getFlowSummary

> FlowSummaryDTO getFlowSummary(flowId)

Get Flow Summary

Get flow summary information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | flowId to be obtained
apiInstance.getFlowSummary(flowId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| flowId to be obtained | 

### Return type

[**FlowSummaryDTO**](FlowSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listFlowVersionsByName

> [FlowItemForNameDTO] listFlowVersionsByName(name)

List Versions by Flow Name

List the versions and corresponding flowIds by flow name.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let name = "name_example"; // String | Flow name
apiInstance.listFlowVersionsByName(name).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Flow name | 

### Return type

[**[FlowItemForNameDTO]**](FlowItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## publishFlow

> String publishFlow(flowId, visibility)

Publish Flow

Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | The flowId to be published
let visibility = "visibility_example"; // String | Visibility: public | private | ...
apiInstance.publishFlow(flowId, visibility).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| The flowId to be published | 
 **visibility** | **String**| Visibility: public | private | ... | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## searchFlowDetails

> [FlowDetailsDTO] searchFlowDetails(flowQueryDTO)

Search Flow Details

Same as /api/v1/flow/search, but returns detailed information of the flow.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowQueryDTO = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // FlowQueryDTO | Query conditions
apiInstance.searchFlowDetails(flowQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

### Return type

[**[FlowDetailsDTO]**](FlowDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## searchFlowSummary

> [FlowSummaryDTO] searchFlowSummary(flowQueryDTO)

Search Flow Summary

Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowQueryDTO = {"where":{"format":"langflow","visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // FlowQueryDTO | Query conditions
apiInstance.searchFlowSummary(flowQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | [**FlowQueryDTO**](FlowQueryDTO.md)| Query conditions | 

### Return type

[**[FlowSummaryDTO]**](FlowSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateFlow

> Boolean updateFlow(flowId, flowUpdateDTO)

Update Flow

Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.FlowApi();
let flowId = "flowId_example"; // String | FlowId to be updated
let flowUpdateDTO = {"version":2,"name":"Second Test Flow (New)","visibility":"public","description":"Second flow description (new)","config":"{}","inputs":"{\"robot\": null}","tags":["test2","demo2","robot"],"aiModels":["123","456"]}; // FlowUpdateDTO | Flow information to be updated
apiInstance.updateFlow(flowId, flowUpdateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | **String**| FlowId to be updated | 
 **flowUpdateDTO** | [**FlowUpdateDTO**](FlowUpdateDTO.md)| Flow information to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

