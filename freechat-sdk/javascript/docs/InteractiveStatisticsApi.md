# freechat-sdk.InteractiveStatisticsApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addStatistic**](InteractiveStatisticsApi.md#addStatistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics
[**getScore**](InteractiveStatisticsApi.md#getScore) | **GET** /api/v1/score/{infoType}/{infoId} | Get Score for Resource
[**getStatistic**](InteractiveStatisticsApi.md#getStatistic) | **GET** /api/v1/stats/{infoType}/{infoId}/{statsType} | Get Statistics
[**getStatistics**](InteractiveStatisticsApi.md#getStatistics) | **GET** /api/v1/stats/{infoType}/{infoId} | Get All Statistics
[**increaseStatistic**](InteractiveStatisticsApi.md#increaseStatistic) | **POST** /api/v1/stats/{infoType}/{infoId}/{statsType} | Increase Statistics
[**listCharactersByStatistic**](InteractiveStatisticsApi.md#listCharactersByStatistic) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics
[**listCharactersByStatistic1**](InteractiveStatisticsApi.md#listCharactersByStatistic1) | **GET** /api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics
[**listCharactersByStatistic2**](InteractiveStatisticsApi.md#listCharactersByStatistic2) | **GET** /api/v1/stats/characters/by/{statsType} | List Characters by Statistics
[**listFlowsByStatistic**](InteractiveStatisticsApi.md#listFlowsByStatistic) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize}/{pageNum} | List Flows by Statistics
[**listFlowsByStatistic1**](InteractiveStatisticsApi.md#listFlowsByStatistic1) | **GET** /api/v1/stats/flows/by/{statsType} | List Flows by Statistics
[**listFlowsByStatistic2**](InteractiveStatisticsApi.md#listFlowsByStatistic2) | **GET** /api/v1/stats/flows/by/{statsType}/{pageSize} | List Flows by Statistics
[**listPluginsByStatistic**](InteractiveStatisticsApi.md#listPluginsByStatistic) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics
[**listPluginsByStatistic1**](InteractiveStatisticsApi.md#listPluginsByStatistic1) | **GET** /api/v1/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics
[**listPluginsByStatistic2**](InteractiveStatisticsApi.md#listPluginsByStatistic2) | **GET** /api/v1/stats/plugins/by/{statsType} | List Plugins by Statistics
[**listPromptsByStatistic**](InteractiveStatisticsApi.md#listPromptsByStatistic) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics
[**listPromptsByStatistic1**](InteractiveStatisticsApi.md#listPromptsByStatistic1) | **GET** /api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics
[**listPromptsByStatistic2**](InteractiveStatisticsApi.md#listPromptsByStatistic2) | **GET** /api/v1/stats/prompts/by/{statsType} | List Prompts by Statistics



## addStatistic

> Number addStatistic(infoType, infoId, statsType, delta)

Add Statistics

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
let infoId = "infoId_example"; // String | Unique resource identifier
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let delta = 789; // Number | Delta in statistical value
apiInstance.addStatistic(infoType, infoId, statsType, delta).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **String**| Resource type: prompt | flow | plugin | 
 **infoId** | **String**| Unique resource identifier | 
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **delta** | **Number**| Delta in statistical value | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getScore

> Number getScore(infoType, infoId)

Get Score for Resource

Get the current user&#39;s score for the corresponding resource.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
let infoId = "infoId_example"; // String | Unique resource identifier
apiInstance.getScore(infoType, infoId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **String**| Resource type: prompt | flow | plugin | 
 **infoId** | **String**| Unique resource identifier | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getStatistic

> Number getStatistic(infoType, infoId, statsType)

Get Statistics

Get the statistics of the corresponding metrics of the corresponding resources.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
let infoId = "infoId_example"; // String | Unique resource identifier
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
apiInstance.getStatistic(infoType, infoId, statsType).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **String**| Resource type: prompt | flow | plugin | 
 **infoId** | **String**| Unique resource identifier | 
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getStatistics

> InteractiveStatsDTO getStatistics(infoType, infoId)

Get All Statistics

Get all statistics of the corresponding resources.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
let infoId = "infoId_example"; // String | Unique resource identifier
apiInstance.getStatistics(infoType, infoId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **String**| Resource type: prompt | flow | plugin | 
 **infoId** | **String**| Unique resource identifier | 

### Return type

[**InteractiveStatsDTO**](InteractiveStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## increaseStatistic

> Number increaseStatistic(infoType, infoId, statsType)

Increase Statistics

Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let infoType = "infoType_example"; // String | Resource type: prompt | flow | plugin
let infoId = "infoId_example"; // String | Unique resource identifier
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
apiInstance.increaseStatistic(infoType, infoId, statsType).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **String**| Resource type: prompt | flow | plugin | 
 **infoId** | **String**| Unique resource identifier | 
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCharactersByStatistic

> [CharacterSummaryStatsDTO] listCharactersByStatistic(statsType, pageSize, opts)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listCharactersByStatistic(statsType, pageSize, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCharactersByStatistic1

> [CharacterSummaryStatsDTO] listCharactersByStatistic1(statsType, pageSize, pageNum, opts)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listCharactersByStatistic1(statsType, pageSize, pageNum, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCharactersByStatistic2

> [CharacterSummaryStatsDTO] listCharactersByStatistic2(statsType, opts)

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listCharactersByStatistic2(statsType, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listFlowsByStatistic

> [FlowSummaryStatsDTO] listFlowsByStatistic(statsType, pageSize, pageNum, opts)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listFlowsByStatistic(statsType, pageSize, pageNum, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listFlowsByStatistic1

> [FlowSummaryStatsDTO] listFlowsByStatistic1(statsType, opts)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listFlowsByStatistic1(statsType, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listFlowsByStatistic2

> [FlowSummaryStatsDTO] listFlowsByStatistic2(statsType, pageSize, opts)

List Flows by Statistics

List flows based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listFlowsByStatistic2(statsType, pageSize, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[FlowSummaryStatsDTO]**](FlowSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPluginsByStatistic

> [PluginSummaryStatsDTO] listPluginsByStatistic(statsType, pageSize, pageNum, opts)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPluginsByStatistic(statsType, pageSize, pageNum, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPluginsByStatistic1

> [PluginSummaryStatsDTO] listPluginsByStatistic1(statsType, pageSize, opts)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPluginsByStatistic1(statsType, pageSize, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPluginsByStatistic2

> [PluginSummaryStatsDTO] listPluginsByStatistic2(statsType, opts)

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPluginsByStatistic2(statsType, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPromptsByStatistic

> [PromptSummaryStatsDTO] listPromptsByStatistic(statsType, pageSize, opts)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPromptsByStatistic(statsType, pageSize, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPromptsByStatistic1

> [PromptSummaryStatsDTO] listPromptsByStatistic1(statsType, pageSize, pageNum, opts)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPromptsByStatistic1(statsType, pageSize, pageNum, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listPromptsByStatistic2

> [PromptSummaryStatsDTO] listPromptsByStatistic2(statsType, opts)

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.InteractiveStatisticsApi();
let statsType = "statsType_example"; // String | Statistics type: view_count | refer_count | recommend_count | score
let opts = {
  'asc': "asc_example" // String | Default is descending order, set asc=1 for ascending order
};
apiInstance.listPromptsByStatistic2(statsType, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **String**| Statistics type: view_count | refer_count | recommend_count | score | 
 **asc** | **String**| Default is descending order, set asc&#x3D;1 for ascending order | [optional] 

### Return type

[**[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

