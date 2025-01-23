# .RagApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelRagTask**](RagApi.md#cancelRagTask) | **POST** /api/v2/rag/task/cancel/{taskId} | Cancel RAG Task
[**createRagTask**](RagApi.md#createRagTask) | **POST** /api/v2/rag/task/{characterUid} | Create RAG Task
[**deleteRagTask**](RagApi.md#deleteRagTask) | **DELETE** /api/v2/rag/task/{taskId} | Delete RAG Task
[**getRagTask**](RagApi.md#getRagTask) | **GET** /api/v2/rag/task/{taskId} | Get RAG Task
[**getRagTaskStatus**](RagApi.md#getRagTaskStatus) | **GET** /api/v2/rag/task/status/{taskId} | Get RAG Task Status
[**listRagTasks**](RagApi.md#listRagTasks) | **GET** /api/v2/rag/tasks/{characterUid} | List RAG Tasks
[**startRagTask**](RagApi.md#startRagTask) | **POST** /api/v2/rag/task/start/{taskId} | Start RAG Task
[**updateRagTask**](RagApi.md#updateRagTask) | **PUT** /api/v2/rag/task/{taskId} | Update RAG Task


# **cancelRagTask**
> boolean cancelRagTask()

Cancel a RAG task.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiCancelRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiCancelRagTaskRequest = {
    // The taskId to be canceled
  taskId: 1,
};

const data = await apiInstance.cancelRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | [**number**] | The taskId to be canceled | defaults to undefined


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

# **createRagTask**
> number createRagTask(ragTaskDTO)

Create a RAG task.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiCreateRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiCreateRagTaskRequest = {
    // The characterUid to be added a RAG task
  characterUid: "characterUid_example",
    // The RAG task to be added
  ragTaskDTO: {
    sourceType: "url",
    source: "source_example",
    maxSegmentSize: 1,
    maxOverlapSize: 1,
  },
};

const data = await apiInstance.createRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ragTaskDTO** | **RagTaskDTO**| The RAG task to be added |
 **characterUid** | [**string**] | The characterUid to be added a RAG task | defaults to undefined


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

# **deleteRagTask**
> boolean deleteRagTask()

Delete a RAG task.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiDeleteRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiDeleteRagTaskRequest = {
    // The taskId to be deleted
  taskId: 1,
};

const data = await apiInstance.deleteRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | [**number**] | The taskId to be deleted | defaults to undefined


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

# **getRagTask**
> RagTaskDetailsDTO getRagTask()

Get the RAG task details.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiGetRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiGetRagTaskRequest = {
    // The taskId to be queried
  taskId: 1,
};

const data = await apiInstance.getRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | [**number**] | The taskId to be queried | defaults to undefined


### Return type

**RagTaskDetailsDTO**

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

# **getRagTaskStatus**
> string getRagTaskStatus()

Get the RAG task execution status: pending | running | succeeded | failed | canceled.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiGetRagTaskStatusRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiGetRagTaskStatusRequest = {
    // The taskId to be queried status
  taskId: 1,
};

const data = await apiInstance.getRagTaskStatus(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | [**number**] | The taskId to be queried status | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listRagTasks**
> Array<RagTaskDetailsDTO> listRagTasks()

List the RAG tasks by characterId.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiListRagTasksRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiListRagTasksRequest = {
    // The characterUid to be queried
  characterUid: "characterUid_example",
};

const data = await apiInstance.listRagTasks(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | The characterUid to be queried | defaults to undefined


### Return type

**Array<RagTaskDetailsDTO>**

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

# **startRagTask**
> boolean startRagTask()

Start a RAG task.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiStartRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiStartRagTaskRequest = {
    // The taskId to be started
  taskId: 1,
};

const data = await apiInstance.startRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | [**number**] | The taskId to be started | defaults to undefined


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

# **updateRagTask**
> boolean updateRagTask(ragTaskDTO)

Update a RAG task.

### Example


```typescript
import { createConfiguration, RagApi } from '';
import type { RagApiUpdateRagTaskRequest } from '';

const configuration = createConfiguration();
const apiInstance = new RagApi(configuration);

const request: RagApiUpdateRagTaskRequest = {
    // The taskId to be updated
  taskId: 1,
    // The prompt task info to be updated
  ragTaskDTO: {
    sourceType: "url",
    source: "source_example",
    maxSegmentSize: 1,
    maxOverlapSize: 1,
  },
};

const data = await apiInstance.updateRagTask(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ragTaskDTO** | **RagTaskDTO**| The prompt task info to be updated |
 **taskId** | [**number**] | The taskId to be updated | defaults to undefined


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


