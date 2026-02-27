# InteractiveStatisticsApi

All URIs are relative to **

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



## addStatistic

Add Statistics

Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.

### Example

```bash
freechat-cli addStatistic infoType=value infoId=value statsType=value delta=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **infoId** | **string** | Unique resource identifier | [default to null]
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **delta** | **integer** | Delta in statistical value | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getScore

Get Score for Resource

Get the current user's score for the corresponding resource.

### Example

```bash
freechat-cli getScore infoType=value infoId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **infoId** | **string** | Unique resource identifier | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getStatistic

Get Statistics

Get the statistics of the corresponding metrics of the corresponding resources.

### Example

```bash
freechat-cli getStatistic infoType=value infoId=value statsType=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **infoId** | **string** | Unique resource identifier | [default to null]
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getStatistics

Get All Statistics

Get all statistics of the corresponding resources.

### Example

```bash
freechat-cli getStatistics infoType=value infoId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **infoId** | **string** | Unique resource identifier | [default to null]

### Return type

[**InteractiveStatsDTO**](InteractiveStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## increaseStatistic

Increase Statistics

Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.

### Example

```bash
freechat-cli increaseStatistic infoType=value infoId=value statsType=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **infoId** | **string** | Unique resource identifier | [default to null]
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAgentsByStatistic

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listAgentsByStatistic statsType=value pageSize=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[AgentSummaryStatsDTO]**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAgentsByStatistic1

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listAgentsByStatistic1 statsType=value pageSize=value pageNum=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **pageNum** | **integer** | Current page number | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[AgentSummaryStatsDTO]**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAgentsByStatistic2

List Agents by Statistics

List agents based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listAgentsByStatistic2 statsType=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[AgentSummaryStatsDTO]**](AgentSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharactersByStatistic

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listCharactersByStatistic statsType=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharactersByStatistic1

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listCharactersByStatistic1 statsType=value pageSize=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharactersByStatistic2

List Characters by Statistics

List characters based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listCharactersByStatistic2 statsType=value pageSize=value pageNum=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **pageNum** | **integer** | Current page number | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[CharacterSummaryStatsDTO]**](CharacterSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listHotTags

Hot Tags

Get popular tags for a specified info type.

### Example

```bash
freechat-cli listHotTags infoType=value pageSize=value  text=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **infoType** | **string** | Info type: prompt | agent | plugin | character | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **text** | **string** | Key word | [optional] [default to null]

### Return type

[**array[HotTagDTO]**](HotTagDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPluginsByStatistic

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPluginsByStatistic statsType=value pageSize=value pageNum=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **pageNum** | **integer** | Current page number | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPluginsByStatistic1

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPluginsByStatistic1 statsType=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPluginsByStatistic2

List Plugins by Statistics

List plugins based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPluginsByStatistic2 statsType=value pageSize=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PluginSummaryStatsDTO]**](PluginSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPromptsByStatistic

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPromptsByStatistic statsType=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPromptsByStatistic1

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPromptsByStatistic1 statsType=value pageSize=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPromptsByStatistic2

List Prompts by Statistics

List prompts based on statistics, including interactive statistical data.

### Example

```bash
freechat-cli listPromptsByStatistic2 statsType=value pageSize=value pageNum=value  asc=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statsType** | **string** | Statistics type: view_count | refer_count | recommend_count | score | [default to null]
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **pageNum** | **integer** | Current page number | [default to null]
 **asc** | **string** | Default is descending order, set asc=1 for ascending order | [optional] [default to null]

### Return type

[**array[PromptSummaryStatsDTO]**](PromptSummaryStatsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

