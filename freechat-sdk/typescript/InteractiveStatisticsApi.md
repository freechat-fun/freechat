# .InteractiveStatisticsApi

All URIs are relative to *https://freechat.fun*

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


# **addStatistic**
> number addStatistic()

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiAddStatisticRequest = {
  // string | Resource type: prompt | flow | plugin
  infoType: "infoType_example",
  // string | Unique resource identifier
  infoId: "infoId_example",
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Delta in statistical value
  delta: 1,
};

apiInstance.addStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Resource type: prompt | flow | plugin | defaults to undefined
 **infoId** | [**string**] | Unique resource identifier | defaults to undefined
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **delta** | [**number**] | Delta in statistical value | defaults to undefined


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getScore**
> number getScore()

Get the current user\'s score for the corresponding resource.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiGetScoreRequest = {
  // string | Resource type: prompt | flow | plugin
  infoType: "infoType_example",
  // string | Unique resource identifier
  infoId: "infoId_example",
};

apiInstance.getScore(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Resource type: prompt | flow | plugin | defaults to undefined
 **infoId** | [**string**] | Unique resource identifier | defaults to undefined


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getStatistic**
> number getStatistic()

Get the statistics of the corresponding metrics of the corresponding resources.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiGetStatisticRequest = {
  // string | Resource type: prompt | flow | plugin
  infoType: "infoType_example",
  // string | Unique resource identifier
  infoId: "infoId_example",
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
};

apiInstance.getStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Resource type: prompt | flow | plugin | defaults to undefined
 **infoId** | [**string**] | Unique resource identifier | defaults to undefined
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getStatistics**
> InteractiveStatsDTO getStatistics()

Get all statistics of the corresponding resources.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiGetStatisticsRequest = {
  // string | Resource type: prompt | flow | plugin
  infoType: "infoType_example",
  // string | Unique resource identifier
  infoId: "infoId_example",
};

apiInstance.getStatistics(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Resource type: prompt | flow | plugin | defaults to undefined
 **infoId** | [**string**] | Unique resource identifier | defaults to undefined


### Return type

**InteractiveStatsDTO**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **increaseStatistic**
> number increaseStatistic()

Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiIncreaseStatisticRequest = {
  // string | Resource type: prompt | flow | plugin
  infoType: "infoType_example",
  // string | Unique resource identifier
  infoId: "infoId_example",
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
};

apiInstance.increaseStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Resource type: prompt | flow | plugin | defaults to undefined
 **infoId** | [**string**] | Unique resource identifier | defaults to undefined
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listCharactersByStatistic**
> Array<CharacterSummaryStatsDTO> listCharactersByStatistic()

List characters based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListCharactersByStatisticRequest = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listCharactersByStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<CharacterSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listCharactersByStatistic1**
> Array<CharacterSummaryStatsDTO> listCharactersByStatistic1()

List characters based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListCharactersByStatistic1Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 0,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listCharactersByStatistic1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<CharacterSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listCharactersByStatistic2**
> Array<CharacterSummaryStatsDTO> listCharactersByStatistic2()

List characters based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListCharactersByStatistic2Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listCharactersByStatistic2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<CharacterSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listFlowsByStatistic**
> Array<FlowSummaryStatsDTO> listFlowsByStatistic()

List flows based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListFlowsByStatisticRequest = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 0,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listFlowsByStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<FlowSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listFlowsByStatistic1**
> Array<FlowSummaryStatsDTO> listFlowsByStatistic1()

List flows based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListFlowsByStatistic1Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listFlowsByStatistic1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<FlowSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listFlowsByStatistic2**
> Array<FlowSummaryStatsDTO> listFlowsByStatistic2()

List flows based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListFlowsByStatistic2Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listFlowsByStatistic2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<FlowSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPluginsByStatistic**
> Array<PluginSummaryStatsDTO> listPluginsByStatistic()

List plugins based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPluginsByStatisticRequest = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 0,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPluginsByStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PluginSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPluginsByStatistic1**
> Array<PluginSummaryStatsDTO> listPluginsByStatistic1()

List plugins based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPluginsByStatistic1Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPluginsByStatistic1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PluginSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPluginsByStatistic2**
> Array<PluginSummaryStatsDTO> listPluginsByStatistic2()

List plugins based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPluginsByStatistic2Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPluginsByStatistic2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PluginSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPromptsByStatistic**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPromptsByStatisticRequest = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPromptsByStatistic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PromptSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPromptsByStatistic1**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic1()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPromptsByStatistic1Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 0,
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPromptsByStatistic1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PromptSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listPromptsByStatistic2**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic2()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .InteractiveStatisticsApi(configuration);

let body:.InteractiveStatisticsApiListPromptsByStatistic2Request = {
  // string | Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
  // string | Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

apiInstance.listPromptsByStatistic2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<PromptSummaryStatsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


