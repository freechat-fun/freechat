# freechat-sdk.PromptApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**applyStringPromptRef**](PromptApi.md#applyStringPromptRef) | **POST** /api/v1/prompt/apply/ref | Apply Parameters to Prompt Record
[**applyStringPromptTemplate**](PromptApi.md#applyStringPromptTemplate) | **POST** /api/v1/prompt/apply/string | Apply Parameters to String Prompt Template
[**batchSearchPromptDetails**](PromptApi.md#batchSearchPromptDetails) | **POST** /api/v1/prompt/batch/details/search | Batch Search Prompt Details
[**batchSearchPromptSummary**](PromptApi.md#batchSearchPromptSummary) | **POST** /api/v1/prompt/batch/search | Batch Search Prompt Summaries
[**clonePrompt**](PromptApi.md#clonePrompt) | **POST** /api/v1/prompt/clone/{promptId} | Clone Prompt
[**clonePrompts**](PromptApi.md#clonePrompts) | **POST** /api/v1/prompt/batch/clone | Batch Clone Prompts
[**countPrompts**](PromptApi.md#countPrompts) | **POST** /api/v1/prompt/count | Calculate Number of Prompts
[**createPrompt**](PromptApi.md#createPrompt) | **POST** /api/v1/prompt | Create Prompt
[**createPrompts**](PromptApi.md#createPrompts) | **POST** /api/v1/prompt/batch | Batch Create Prompts
[**deletePrompt**](PromptApi.md#deletePrompt) | **DELETE** /api/v1/prompt/{promptId} | Delete Prompt
[**deletePrompts**](PromptApi.md#deletePrompts) | **DELETE** /api/v1/prompt/batch | Batch Delete Prompts
[**getPromptDetails**](PromptApi.md#getPromptDetails) | **GET** /api/v1/prompt/details/{promptId} | Get Prompt Details
[**getPromptSummary**](PromptApi.md#getPromptSummary) | **GET** /api/v1/prompt/summary/{promptId} | Get Prompt Summary
[**listPromptVersionsByName**](PromptApi.md#listPromptVersionsByName) | **POST** /api/v1/prompt/versions/{name} | List Versions by Prompt Name
[**publishPrompt**](PromptApi.md#publishPrompt) | **POST** /api/v1/prompt/publish/{promptId}/{visibility} | Publish Prompt
[**searchPromptDetails**](PromptApi.md#searchPromptDetails) | **POST** /api/v1/prompt/details/search | Search Prompt Details
[**searchPromptSummary**](PromptApi.md#searchPromptSummary) | **POST** /api/v1/prompt/search | Search Prompt Summary
[**sendPrompt**](PromptApi.md#sendPrompt) | **POST** /api/v1/prompt/send | Send Prompt
[**streamSendPrompt**](PromptApi.md#streamSendPrompt) | **POST** /api/v1/prompt/send/stream | Send Prompt by Streaming Back
[**updatePrompt**](PromptApi.md#updatePrompt) | **PUT** /api/v1/prompt/{promptId} | Update Prompt



## applyStringPromptRef

> String applyStringPromptRef(promptRefDTO)

Apply Parameters to Prompt Record

Apply parameters to prompt record.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptRefDTO = new freechat-sdk.PromptRefDTO(); // PromptRefDTO | Prompt record
apiInstance.applyStringPromptRef(promptRefDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptRefDTO** | [**PromptRefDTO**](PromptRefDTO.md)| Prompt record | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## applyStringPromptTemplate

> String applyStringPromptTemplate(promptTemplateDTO)

Apply Parameters to String Prompt Template

Apply parameters to string type prompt template.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptTemplateDTO = new freechat-sdk.PromptTemplateDTO(); // PromptTemplateDTO | String type prompt template
apiInstance.applyStringPromptTemplate(promptTemplateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTemplateDTO** | [**PromptTemplateDTO**](PromptTemplateDTO.md)| String type prompt template | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## batchSearchPromptDetails

> [[PromptDetailsDTO]] batchSearchPromptDetails(promptQueryDTO)

Batch Search Prompt Details

Batch call shortcut for /api/v1/prompt/details/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [PromptQueryDTO] | Query conditions
apiInstance.batchSearchPromptDetails(promptQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**[PromptQueryDTO]**](PromptQueryDTO.md)| Query conditions | 

### Return type

**[[PromptDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## batchSearchPromptSummary

> [[PromptSummaryDTO]] batchSearchPromptSummary(promptQueryDTO)

Batch Search Prompt Summaries

Batch call shortcut for /api/v1/prompt/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [PromptQueryDTO] | Query conditions
apiInstance.batchSearchPromptSummary(promptQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**[PromptQueryDTO]**](PromptQueryDTO.md)| Query conditions | 

### Return type

**[[PromptSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## clonePrompt

> String clonePrompt(promptId)

Clone Prompt

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | The referenced promptId
apiInstance.clonePrompt(promptId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| The referenced promptId | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## clonePrompts

> [String] clonePrompts(requestBody)

Batch Clone Prompts

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let requestBody = ["null"]; // [String] | List of prompt information to be created
apiInstance.clonePrompts(requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**[String]**](String.md)| List of prompt information to be created | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## countPrompts

> Number countPrompts(promptQueryDTO)

Calculate Number of Prompts

Calculate the number of prompts according to the specified query conditions.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptQueryDTO = new freechat-sdk.PromptQueryDTO(); // PromptQueryDTO | Query conditions
apiInstance.countPrompts(promptQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## createPrompt

> String createPrompt(promptCreateDTO)

Create Prompt

Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptCreateDTO = {"name":"A Test Prompt","description":"A prompt description","template":"Hello world. I'm {name}","inputs":"{\"name\": null}","tags":["test","demo"],"aiModels":["123"]}; // PromptCreateDTO | Information of the prompt to be created
apiInstance.createPrompt(promptCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | [**PromptCreateDTO**](PromptCreateDTO.md)| Information of the prompt to be created | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## createPrompts

> [String] createPrompts(promptCreateDTO)

Batch Create Prompts

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptCreateDTO = [{"name":"First Test Prompt","description":"First prompt description","template":"Hello world. I'm {name}","inputs":"{\"name\": null}","tags":["test1","demo1"],"aiModels":["123"]},{"name":"Second Test Prompt","visibility":"public","description":"Second prompt description","template":"I wanna call you {robot}","inputs":"{\"robot\": null}","tags":["test2","demo2"],"aiModels":["123","456"]}]; // [PromptCreateDTO] | List of prompt information to be created
apiInstance.createPrompts(promptCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | [**[PromptCreateDTO]**](PromptCreateDTO.md)| List of prompt information to be created | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deletePrompt

> Boolean deletePrompt(promptId)

Delete Prompt

Delete prompt. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | The promptId to be deleted
apiInstance.deletePrompt(promptId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| The promptId to be deleted | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## deletePrompts

> [String] deletePrompts(requestBody)

Batch Delete Prompts

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let requestBody = ["null"]; // [String] | List of promptIds to be deleted
apiInstance.deletePrompts(requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**[String]**](String.md)| List of promptIds to be deleted | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## getPromptDetails

> PromptDetailsDTO getPromptDetails(promptId)

Get Prompt Details

Get prompt detailed information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | PromptId to be obtained
apiInstance.getPromptDetails(promptId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| PromptId to be obtained | 

### Return type

[**PromptDetailsDTO**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getPromptSummary

> PromptSummaryDTO getPromptSummary(promptId)

Get Prompt Summary

Get prompt summary information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | PromptId to be obtained
apiInstance.getPromptSummary(promptId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| PromptId to be obtained | 

### Return type

[**PromptSummaryDTO**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPromptVersionsByName

> [PromptItemForNameDTO] listPromptVersionsByName(name)

List Versions by Prompt Name

List the versions and corresponding promptIds by prompt name.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let name = "name_example"; // String | Prompt name
apiInstance.listPromptVersionsByName(name).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Prompt name | 

### Return type

[**[PromptItemForNameDTO]**](PromptItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## publishPrompt

> String publishPrompt(promptId, visibility)

Publish Prompt

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | The promptId to be published
let visibility = "visibility_example"; // String | Visibility: public | private | ...
apiInstance.publishPrompt(promptId, visibility).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| The promptId to be published | 
 **visibility** | **String**| Visibility: public | private | ... | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## searchPromptDetails

> [PromptDetailsDTO] searchPromptDetails(promptQueryDTO)

Search Prompt Details

Same as /api/v1/prompt/search, but returns detailed information of the prompt.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // PromptQueryDTO | Query conditions
apiInstance.searchPromptDetails(promptQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

[**[PromptDetailsDTO]**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## searchPromptSummary

> [PromptSummaryDTO] searchPromptSummary(promptQueryDTO)

Search Prompt Summary

Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"],"aiModels":["123"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // PromptQueryDTO | Query conditions
apiInstance.searchPromptSummary(promptQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md)| Query conditions | 

### Return type

[**[PromptSummaryDTO]**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## sendPrompt

> LlmResultDTO sendPrompt(promptAiParamDTO)

Send Prompt

Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptAiParamDTO = {"prompt":"say 'hello'","params":{"apiKey":"","modelId":"[open_ai]gpt-3.5-turbo"}}; // PromptAiParamDTO | Call parameters
apiInstance.sendPrompt(promptAiParamDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | 

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## streamSendPrompt

> SseEmitter streamSendPrompt(promptAiParamDTO)

Send Prompt by Streaming Back

Refer to /api/v1/prompt/send, stream back chunks of the response.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptAiParamDTO = {"prompt":"say 'hello'","params":{"apiKey":"","modelId":"[open_ai]gpt-3.5-turbo"}}; // PromptAiParamDTO | Call parameters
apiInstance.streamSendPrompt(promptAiParamDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md)| Call parameters | 

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/event-stream


## updatePrompt

> Boolean updatePrompt(promptId, promptUpdateDTO)

Update Prompt

Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PromptApi();
let promptId = "promptId_example"; // String | The promptId to be updated
let promptUpdateDTO = {"version":2,"name":"Second Test Prompt (New)","visibility":"public","description":"Second prompt description (new)","template":"I wanna call you {robot}","inputs":"{\"robot\": null}","tags":["test2","demo2","robot"],"aiModels":["123","456"]}; // PromptUpdateDTO | The prompt information to be updated
apiInstance.updatePrompt(promptId, promptUpdateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **String**| The promptId to be updated | 
 **promptUpdateDTO** | [**PromptUpdateDTO**](PromptUpdateDTO.md)| The prompt information to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

