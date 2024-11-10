# .InteractiveStatisticsApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addStatistic**](InteractiveStatisticsApi.md#addStatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType}/{delta} | Add Statistics
[**getScore**](InteractiveStatisticsApi.md#getScore) | **GET** /api/v2/public/score/{infoType}/{infoId} | Get Score for Resource
[**getStatistic**](InteractiveStatisticsApi.md#getStatistic) | **GET** /api/v2/public/stats/{infoType}/{infoId}/{statsType} | Get Statistics
[**getStatistics**](InteractiveStatisticsApi.md#getStatistics) | **GET** /api/v2/public/stats/{infoType}/{infoId} | Get All Statistics
[**increaseStatistic**](InteractiveStatisticsApi.md#increaseStatistic) | **POST** /api/v2/stats/{infoType}/{infoId}/{statsType} | Increase Statistics
[**listAgentsByStatistic**](InteractiveStatisticsApi.md#listAgentsByStatistic) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize} | List Agents by Statistics
[**listAgentsByStatistic1**](InteractiveStatisticsApi.md#listAgentsByStatistic1) | **GET** /api/v2/public/stats/agents/by/{statsType}/{pageSize}/{pageNum} | List Agents by Statistics
[**listAgentsByStatistic2**](InteractiveStatisticsApi.md#listAgentsByStatistic2) | **GET** /api/v2/public/stats/agents/by/{statsType} | List Agents by Statistics
[**listCharactersByStatistic**](InteractiveStatisticsApi.md#listCharactersByStatistic) | **GET** /api/v2/public/stats/characters/by/{statsType} | List Characters by Statistics
[**listCharactersByStatistic1**](InteractiveStatisticsApi.md#listCharactersByStatistic1) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize} | List Characters by Statistics
[**listCharactersByStatistic2**](InteractiveStatisticsApi.md#listCharactersByStatistic2) | **GET** /api/v2/public/stats/characters/by/{statsType}/{pageSize}/{pageNum} | List Characters by Statistics
[**listHotTags**](InteractiveStatisticsApi.md#listHotTags) | **GET** /api/v2/public/tags/hot/{infoType}/{pageSize} | Hot Tags
[**listPluginsByStatistic**](InteractiveStatisticsApi.md#listPluginsByStatistic) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize}/{pageNum} | List Plugins by Statistics
[**listPluginsByStatistic1**](InteractiveStatisticsApi.md#listPluginsByStatistic1) | **GET** /api/v2/public/stats/plugins/by/{statsType} | List Plugins by Statistics
[**listPluginsByStatistic2**](InteractiveStatisticsApi.md#listPluginsByStatistic2) | **GET** /api/v2/public/stats/plugins/by/{statsType}/{pageSize} | List Plugins by Statistics
[**listPromptsByStatistic**](InteractiveStatisticsApi.md#listPromptsByStatistic) | **GET** /api/v2/public/stats/prompts/by/{statsType} | List Prompts by Statistics
[**listPromptsByStatistic1**](InteractiveStatisticsApi.md#listPromptsByStatistic1) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize} | List Prompts by Statistics
[**listPromptsByStatistic2**](InteractiveStatisticsApi.md#listPromptsByStatistic2) | **GET** /api/v2/public/stats/prompts/by/{statsType}/{pageSize}/{pageNum} | List Prompts by Statistics


# **addStatistic**
> number addStatistic()

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiAddStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiAddStatisticRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Unique resource identifier
  infoId: "infoId_example",
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Delta in statistical value
  delta: 1,
};

const data = await apiInstance.addStatistic(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiGetScoreRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiGetScoreRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Unique resource identifier
  infoId: "infoId_example",
};

const data = await apiInstance.getScore(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiGetStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiGetStatisticRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Unique resource identifier
  infoId: "infoId_example",
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
};

const data = await apiInstance.getStatistic(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiGetStatisticsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiGetStatisticsRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Unique resource identifier
  infoId: "infoId_example",
};

const data = await apiInstance.getStatistics(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiIncreaseStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiIncreaseStatisticRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Unique resource identifier
  infoId: "infoId_example",
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
};

const data = await apiInstance.increaseStatistic(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
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

# **listAgentsByStatistic**
> Array<AgentSummaryStatsDTO> listAgentsByStatistic()

List agents based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListAgentsByStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListAgentsByStatisticRequest = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listAgentsByStatistic(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<AgentSummaryStatsDTO>**

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

# **listAgentsByStatistic1**
> Array<AgentSummaryStatsDTO> listAgentsByStatistic1()

List agents based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListAgentsByStatistic1Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListAgentsByStatistic1Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listAgentsByStatistic1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<AgentSummaryStatsDTO>**

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

# **listAgentsByStatistic2**
> Array<AgentSummaryStatsDTO> listAgentsByStatistic2()

List agents based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListAgentsByStatistic2Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListAgentsByStatistic2Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listAgentsByStatistic2(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | [**string**] | Statistics type: view_count | refer_count | recommend_count | score | defaults to undefined
 **asc** | [**string**] | Default is descending order, set asc&#x3D;1 for ascending order | (optional) defaults to undefined


### Return type

**Array<AgentSummaryStatsDTO>**

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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListCharactersByStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListCharactersByStatisticRequest = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listCharactersByStatistic(request);
console.log('API called successfully. Returned data:', data);
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

# **listCharactersByStatistic1**
> Array<CharacterSummaryStatsDTO> listCharactersByStatistic1()

List characters based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListCharactersByStatistic1Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListCharactersByStatistic1Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listCharactersByStatistic1(request);
console.log('API called successfully. Returned data:', data);
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

# **listCharactersByStatistic2**
> Array<CharacterSummaryStatsDTO> listCharactersByStatistic2()

List characters based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListCharactersByStatistic2Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListCharactersByStatistic2Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listCharactersByStatistic2(request);
console.log('API called successfully. Returned data:', data);
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

# **listHotTags**
> Array<HotTagDTO> listHotTags()

Get popular tags for a specified info type.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListHotTagsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListHotTagsRequest = {
    // Info type: prompt | agent | plugin | character
  infoType: "infoType_example",
    // Maximum quantity
  pageSize: 1,
    // Key word (optional)
  text: "text_example",
};

const data = await apiInstance.listHotTags(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | [**string**] | Info type: prompt | agent | plugin | character | defaults to undefined
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **text** | [**string**] | Key word | (optional) defaults to undefined


### Return type

**Array<HotTagDTO>**

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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPluginsByStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPluginsByStatisticRequest = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPluginsByStatistic(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPluginsByStatistic1Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPluginsByStatistic1Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPluginsByStatistic1(request);
console.log('API called successfully. Returned data:', data);
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

# **listPluginsByStatistic2**
> Array<PluginSummaryStatsDTO> listPluginsByStatistic2()

List plugins based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPluginsByStatistic2Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPluginsByStatistic2Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPluginsByStatistic2(request);
console.log('API called successfully. Returned data:', data);
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

# **listPromptsByStatistic**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPromptsByStatisticRequest } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPromptsByStatisticRequest = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPromptsByStatistic(request);
console.log('API called successfully. Returned data:', data);
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

# **listPromptsByStatistic1**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic1()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPromptsByStatistic1Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPromptsByStatistic1Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPromptsByStatistic1(request);
console.log('API called successfully. Returned data:', data);
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

# **listPromptsByStatistic2**
> Array<PromptSummaryStatsDTO> listPromptsByStatistic2()

List prompts based on statistics, including interactive statistical data.

### Example


```typescript
import { createConfiguration, InteractiveStatisticsApi } from '';
import type { InteractiveStatisticsApiListPromptsByStatistic2Request } from '';

const configuration = createConfiguration();
const apiInstance = new InteractiveStatisticsApi(configuration);

const request: InteractiveStatisticsApiListPromptsByStatistic2Request = {
    // Statistics type: view_count | refer_count | recommend_count | score
  statsType: "statsType_example",
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
    // Default is descending order, set asc=1 for ascending order (optional)
  asc: "asc_example",
};

const data = await apiInstance.listPromptsByStatistic2(request);
console.log('API called successfully. Returned data:', data);
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


