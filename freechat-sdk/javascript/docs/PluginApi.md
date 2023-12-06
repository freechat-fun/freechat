# freechat-sdk.PluginApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchPluginDetails**](PluginApi.md#batchSearchPluginDetails) | **POST** /api/v1/plugin/batch/details/search | Batch Search Plugin Details
[**batchSearchPluginSummary**](PluginApi.md#batchSearchPluginSummary) | **POST** /api/v1/plugin/batch/search | Batch Search Plugin Summaries
[**countPlugins**](PluginApi.md#countPlugins) | **POST** /api/v1/plugin/count | Calculate Number of Plugins
[**createPlugin**](PluginApi.md#createPlugin) | **POST** /api/v1/plugin | Create Plugin
[**createPlugins**](PluginApi.md#createPlugins) | **POST** /api/v1/plugin/batch | Batch Create Plugins
[**deletePlugin**](PluginApi.md#deletePlugin) | **DELETE** /api/v1/plugin/{pluginId} | Delete Plugin
[**deletePlugins**](PluginApi.md#deletePlugins) | **DELETE** /api/v1/plugin/batch | Batch Delete Plugins
[**getPluginDetails**](PluginApi.md#getPluginDetails) | **GET** /api/v1/plugin/details/{pluginId} | Get Plugin Details
[**getPluginSummary**](PluginApi.md#getPluginSummary) | **GET** /api/v1/plugin/summary/{pluginId} | Get Plugin Summary
[**refreshPluginInfo**](PluginApi.md#refreshPluginInfo) | **PUT** /api/v1/plugin/refresh/{pluginId} | Refresh Plugin Information
[**searchPluginDetails**](PluginApi.md#searchPluginDetails) | **POST** /api/v1/plugin/details/search | Search Plugin Details
[**searchPluginSummary**](PluginApi.md#searchPluginSummary) | **POST** /api/v1/plugin/search | Search Plugin Summary
[**updatePlugin**](PluginApi.md#updatePlugin) | **PUT** /api/v1/plugin/{pluginId} | Update Plugin



## batchSearchPluginDetails

> [[PluginDetailsDTO]] batchSearchPluginDetails(pluginQueryDTO)

Batch Search Plugin Details

Batch call shortcut for /api/v1/plugin/details/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"demo"},"orderBy":["modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Test","provider":"freechat.fun","text":"demo","manifestFormat":"dash_scope","apiFormat":"openapi_v3","tags":["test"],"aiModels":["123"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}]; // [PluginQueryDTO] | Query conditions
apiInstance.batchSearchPluginDetails(pluginQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**[PluginQueryDTO]**](PluginQueryDTO.md)| Query conditions | 

### Return type

**[[PluginDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## batchSearchPluginSummary

> [[PluginSummaryDTO]] batchSearchPluginSummary(pluginQueryDTO)

Batch Search Plugin Summaries

Batch call shortcut for /api/v1/plugin/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"demo"},"orderBy":["modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Test","provider":"freechat.fun","text":"demo","manifestFormat":"dash_scope","apiFormat":"openapi_v3","tags":["test"],"aiModels":["123"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}]; // [PluginQueryDTO] | Query conditions
apiInstance.batchSearchPluginSummary(pluginQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**[PluginQueryDTO]**](PluginQueryDTO.md)| Query conditions | 

### Return type

**[[PluginSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## countPlugins

> Number countPlugins(pluginQueryDTO)

Calculate Number of Plugins

Calculate the number of plugins according to the specified query conditions.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginQueryDTO = new freechat-sdk.PluginQueryDTO(); // PluginQueryDTO | Query conditions
apiInstance.countPlugins(pluginQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## createPlugin

> String createPlugin(pluginCreateDTO)

Create Plugin

Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginCreateDTO = {"name":"Test plugin","provider":"freechat.fun NLP Lab","manifestInfo":"http://127.0.0.1:8080/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"http://127.0.0.1:8080/public/test/plugin/demo/.well-known/api-docs.json","tags":["test","demo"]}; // PluginCreateDTO | Information of the plugin to be created
apiInstance.createPlugin(pluginCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | [**PluginCreateDTO**](PluginCreateDTO.md)| Information of the plugin to be created | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## createPlugins

> [String] createPlugins(pluginCreateDTO)

Batch Create Plugins

Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginCreateDTO = [{"name":"First Test Plugin","provider":"freechat.fun NLP Lab","manifestInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json","tags":["test1","demo1"]},{"name":"Second Test Plugin","visibility":"private","manifestInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/ai-plugin.json","apiInfo":"https://freechat.fun/public/test/plugin/demo/.well-known/api-docs.json","tags":["test2","demo2"],"aiModels":["123","456"]}]; // [PluginCreateDTO] | List of plugin information to be created
apiInstance.createPlugins(pluginCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | [**[PluginCreateDTO]**](PluginCreateDTO.md)| List of plugin information to be created | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deletePlugin

> Boolean deletePlugin(pluginId)

Delete Plugin

Delete plugin. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginId = "pluginId_example"; // String | The pluginId to be deleted
apiInstance.deletePlugin(pluginId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **String**| The pluginId to be deleted | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## deletePlugins

> [String] deletePlugins(requestBody)

Batch Delete Plugins

Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let requestBody = ["null"]; // [String] | List of pluginIds to be deleted
apiInstance.deletePlugins(requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**[String]**](String.md)| List of pluginIds to be deleted | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## getPluginDetails

> PluginDetailsDTO getPluginDetails(pluginId)

Get Plugin Details

Get plugin detailed information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginId = "pluginId_example"; // String | PluginId to be obtained
apiInstance.getPluginDetails(pluginId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **String**| PluginId to be obtained | 

### Return type

[**PluginDetailsDTO**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getPluginSummary

> PluginSummaryDTO getPluginSummary(pluginId)

Get Plugin Summary

Get plugin summary information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginId = "pluginId_example"; // String | PluginId to be obtained
apiInstance.getPluginSummary(pluginId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **String**| PluginId to be obtained | 

### Return type

[**PluginSummaryDTO**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## refreshPluginInfo

> refreshPluginInfo(pluginId)

Refresh Plugin Information

For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginId = "pluginId_example"; // String | The pluginId to be fetched
apiInstance.refreshPluginInfo(pluginId).then(() => {
  console.log('API called successfully.');
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **String**| The pluginId to be fetched | 

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


## searchPluginDetails

> [PluginDetailsDTO] searchPluginDetails(pluginQueryDTO)

Search Plugin Details

Same as /api/v1/plugin/search, but returns detailed information of the plugin.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","provider":"freechat.fun","tags":["test2"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}; // PluginQueryDTO | Query conditions
apiInstance.searchPluginDetails(pluginQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

### Return type

[**[PluginDetailsDTO]**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## searchPluginSummary

> [PluginSummaryDTO] searchPluginSummary(pluginQueryDTO)

Search Plugin Summary

Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","provider":"freechat.fun","text":"demo","tags":["test2"]},"orderBy":["modifyTime asc"],"pageNum":0,"pageSize":1}; // PluginQueryDTO | Query conditions
apiInstance.searchPluginSummary(pluginQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md)| Query conditions | 

### Return type

[**[PluginSummaryDTO]**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updatePlugin

> Boolean updatePlugin(pluginId, pluginUpdateDTO)

Update Plugin

Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.PluginApi();
let pluginId = "pluginId_example"; // String | The pluginId to be updated
let pluginUpdateDTO = {"name":"Second Test Plugin (New)","visibility":"public","tags":["test2","demo2","business"]}; // PluginUpdateDTO | The plugin information to be updated
apiInstance.updatePlugin(pluginId, pluginUpdateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **String**| The pluginId to be updated | 
 **pluginUpdateDTO** | [**PluginUpdateDTO**](PluginUpdateDTO.md)| The plugin information to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

