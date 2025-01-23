# .PluginApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchPluginDetails**](PluginApi.md#batchSearchPluginDetails) | **POST** /api/v2/plugin/batch/details/search | Batch Search Plugin Details
[**batchSearchPluginSummary**](PluginApi.md#batchSearchPluginSummary) | **POST** /api/v2/plugin/batch/search | Batch Search Plugin Summaries
[**countPlugins**](PluginApi.md#countPlugins) | **POST** /api/v2/plugin/count | Calculate Number of Plugins
[**createPlugin**](PluginApi.md#createPlugin) | **POST** /api/v2/plugin | Create Plugin
[**createPlugins**](PluginApi.md#createPlugins) | **POST** /api/v2/plugin/batch | Batch Create Plugins
[**deletePlugin**](PluginApi.md#deletePlugin) | **DELETE** /api/v2/plugin/{pluginId} | Delete Plugin
[**deletePlugins**](PluginApi.md#deletePlugins) | **DELETE** /api/v2/plugin/batch | Batch Delete Plugins
[**getPluginDetails**](PluginApi.md#getPluginDetails) | **GET** /api/v2/plugin/details/{pluginId} | Get Plugin Details
[**getPluginSummary**](PluginApi.md#getPluginSummary) | **GET** /api/v2/plugin/summary/{pluginId} | Get Plugin Summary
[**refreshPluginInfo**](PluginApi.md#refreshPluginInfo) | **PUT** /api/v2/plugin/refresh/{pluginId} | Refresh Plugin Information
[**searchPluginDetails**](PluginApi.md#searchPluginDetails) | **POST** /api/v2/plugin/details/search | Search Plugin Details
[**searchPluginSummary**](PluginApi.md#searchPluginSummary) | **POST** /api/v2/plugin/search | Search Plugin Summary
[**updatePlugin**](PluginApi.md#updatePlugin) | **PUT** /api/v2/plugin/{pluginId} | Update Plugin


# **batchSearchPluginDetails**
> Array<Array<PluginDetailsDTO>> batchSearchPluginDetails(pluginQueryDTO)

Batch call shortcut for /api/v2/plugin/details/search.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiBatchSearchPluginDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiBatchSearchPluginDetailsRequest = {
    // Query conditions
  pluginQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        manifestFormat: "manifestFormat_example",
        apiFormat: "apiFormat_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "or",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "or",
        name: "name_example",
        provider: "provider_example",
        text: "text_example",
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchPluginDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | **Array<PluginQueryDTO>**| Query conditions |


### Return type

**Array<Array<PluginDetailsDTO>>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **batchSearchPluginSummary**
> Array<Array<PluginSummaryDTO>> batchSearchPluginSummary(pluginQueryDTO)

Batch call shortcut for /api/v2/plugin/search.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiBatchSearchPluginSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiBatchSearchPluginSummaryRequest = {
    // Query conditions
  pluginQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        manifestFormat: "manifestFormat_example",
        apiFormat: "apiFormat_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "or",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "or",
        name: "name_example",
        provider: "provider_example",
        text: "text_example",
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchPluginSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | **Array<PluginQueryDTO>**| Query conditions |


### Return type

**Array<Array<PluginSummaryDTO>>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **countPlugins**
> number countPlugins(pluginQueryDTO)

Calculate the number of plugins according to the specified query conditions.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiCountPluginsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiCountPluginsRequest = {
    // Query conditions
  pluginQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      manifestFormat: "manifestFormat_example",
      apiFormat: "apiFormat_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "or",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "or",
      name: "name_example",
      provider: "provider_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.countPlugins(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | **PluginQueryDTO**| Query conditions |


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **createPlugin**
> number createPlugin(pluginCreateDTO)

Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiCreatePluginRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiCreatePluginRequest = {
    // Information of the plugin to be created
  pluginCreateDTO: {
    visibility: "visibility_example",
    name: "name_example",
    manifestFormat: "manifestFormat_example",
    manifestInfo: "manifestInfo_example",
    apiFormat: "apiFormat_example",
    apiInfo: "apiInfo_example",
    provider: "provider_example",
    ext: "ext_example",
    tags: [
      "tags_example",
    ],
    aiModels: [
      "aiModels_example",
    ],
  },
};

const data = await apiInstance.createPlugin(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | **PluginCreateDTO**| Information of the plugin to be created |


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **createPlugins**
> Array<number> createPlugins(pluginCreateDTO)

Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiCreatePluginsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiCreatePluginsRequest = {
    // List of plugin information to be created
  pluginCreateDTO: [
    {
      visibility: "visibility_example",
      name: "name_example",
      manifestFormat: "manifestFormat_example",
      manifestInfo: "manifestInfo_example",
      apiFormat: "apiFormat_example",
      apiInfo: "apiInfo_example",
      provider: "provider_example",
      ext: "ext_example",
      tags: [
        "tags_example",
      ],
      aiModels: [
        "aiModels_example",
      ],
    },
  ],
};

const data = await apiInstance.createPlugins(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | **Array<PluginCreateDTO>**| List of plugin information to be created |


### Return type

**Array<number>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **deletePlugin**
> boolean deletePlugin()

Delete plugin. Returns success or failure.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiDeletePluginRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiDeletePluginRequest = {
    // The pluginId to be deleted
  pluginId: 1,
};

const data = await apiInstance.deletePlugin(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | [**number**] | The pluginId to be deleted | defaults to undefined


### Return type

**boolean**

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

# **deletePlugins**
> Array<number> deletePlugins(requestBody)

Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiDeletePluginsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiDeletePluginsRequest = {
    // List of pluginIds to be deleted
  requestBody: [
    1,
  ],
};

const data = await apiInstance.deletePlugins(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<number>**| List of pluginIds to be deleted |


### Return type

**Array<number>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getPluginDetails**
> PluginDetailsDTO getPluginDetails()

Get plugin detailed information.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiGetPluginDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiGetPluginDetailsRequest = {
    // PluginId to be obtained
  pluginId: 1,
};

const data = await apiInstance.getPluginDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | [**number**] | PluginId to be obtained | defaults to undefined


### Return type

**PluginDetailsDTO**

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

# **getPluginSummary**
> PluginSummaryDTO getPluginSummary()

Get plugin summary information.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiGetPluginSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiGetPluginSummaryRequest = {
    // PluginId to be obtained
  pluginId: 1,
};

const data = await apiInstance.getPluginSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | [**number**] | PluginId to be obtained | defaults to undefined


### Return type

**PluginSummaryDTO**

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

# **refreshPluginInfo**
> void refreshPluginInfo()

For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiRefreshPluginInfoRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiRefreshPluginInfoRequest = {
    // The pluginId to be fetched
  pluginId: 1,
};

const data = await apiInstance.refreshPluginInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | [**number**] | The pluginId to be fetched | defaults to undefined


### Return type

**void**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchPluginDetails**
> Array<PluginDetailsDTO> searchPluginDetails(pluginQueryDTO)

Same as /api/v2/plugin/search, but returns detailed information of the plugin.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiSearchPluginDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiSearchPluginDetailsRequest = {
    // Query conditions
  pluginQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      manifestFormat: "manifestFormat_example",
      apiFormat: "apiFormat_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "or",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "or",
      name: "name_example",
      provider: "provider_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchPluginDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | **PluginQueryDTO**| Query conditions |


### Return type

**Array<PluginDetailsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchPluginSummary**
> Array<PluginSummaryDTO> searchPluginSummary(pluginQueryDTO)

Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiSearchPluginSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiSearchPluginSummaryRequest = {
    // Query conditions
  pluginQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      manifestFormat: "manifestFormat_example",
      apiFormat: "apiFormat_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "or",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "or",
      name: "name_example",
      provider: "provider_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchPluginSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | **PluginQueryDTO**| Query conditions |


### Return type

**Array<PluginSummaryDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **updatePlugin**
> boolean updatePlugin(pluginUpdateDTO)

Update plugin, refer to /api/v2/plugin/create, required field: pluginId. Returns success or failure.

### Example


```typescript
import { createConfiguration, PluginApi } from '';
import type { PluginApiUpdatePluginRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PluginApi(configuration);

const request: PluginApiUpdatePluginRequest = {
    // The pluginId to be updated
  pluginId: 1,
    // The plugin information to be updated
  pluginUpdateDTO: {
    visibility: "visibility_example",
    name: "name_example",
    manifestFormat: "manifestFormat_example",
    manifestInfo: "manifestInfo_example",
    apiFormat: "apiFormat_example",
    apiInfo: "apiInfo_example",
    provider: "provider_example",
    ext: "ext_example",
    tags: [
      "tags_example",
    ],
    aiModels: [
      "aiModels_example",
    ],
  },
};

const data = await apiInstance.updatePlugin(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginUpdateDTO** | **PluginUpdateDTO**| The plugin information to be updated |
 **pluginId** | [**number**] | The pluginId to be updated | defaults to undefined


### Return type

**boolean**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


