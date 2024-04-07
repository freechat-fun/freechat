# .RagApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelRagTask**](RagApi.md#cancelRagTask) | **POST** /api/v1/rag/task/cancel/{taskId} | Cancel RAG Task
[**createRagTask**](RagApi.md#createRagTask) | **POST** /api/v1/rag/task/{characterId} | Create RAG Task
[**deleteRagTask**](RagApi.md#deleteRagTask) | **DELETE** /api/v1/rag/task/{taskId} | Delete RAG Task
[**getRagTask**](RagApi.md#getRagTask) | **GET** /api/v1/rag/task/{taskId} | Get RAG Task
[**getRagTaskStatus**](RagApi.md#getRagTaskStatus) | **GET** /api/v1/rag/task/status/{taskId} | Get RAG Task Status
[**listRagTasks**](RagApi.md#listRagTasks) | **GET** /api/v1/rag/tasks/{characterId} | List RAG Tasks
[**startRagTask**](RagApi.md#startRagTask) | **POST** /api/v1/rag/task/start/{taskId} | Start RAG Task
[**updateRagTask**](RagApi.md#updateRagTask) | **PUT** /api/v1/rag/task/{taskId} | Update RAG Task


# **cancelRagTask**
> boolean cancelRagTask()

Cancel a RAG task.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiCancelRagTaskRequest = {
  // number | The taskId to be canceled
  taskId: 1,
};

apiInstance.cancelRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiCreateRagTaskRequest = {
  // number | The characterId to be added a RAG task
  characterId: 1,
  // RagTaskDTO | The RAG task to be added
  ragTaskDTO: {
    sourceType: "sourceType_example",
    source: "source_example",
  },
};

apiInstance.createRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ragTaskDTO** | **RagTaskDTO**| The RAG task to be added |
 **characterId** | [**number**] | The characterId to be added a RAG task | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiDeleteRagTaskRequest = {
  // number | The taskId to be deleted
  taskId: 1,
};

apiInstance.deleteRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiGetRagTaskRequest = {
  // number | The taskId to be queried
  taskId: 1,
};

apiInstance.getRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiGetRagTaskStatusRequest = {
  // number | The taskId to be queried status
  taskId: 1,
};

apiInstance.getRagTaskStatus(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiListRagTasksRequest = {
  // number | The characterId to be queried
  characterId: 1,
};

apiInstance.listRagTasks(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be queried | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiStartRagTaskRequest = {
  // number | The taskId to be started
  taskId: 1,
};

apiInstance.startRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .RagApi(configuration);

let body:.RagApiUpdateRagTaskRequest = {
  // number | The taskId to be updated
  taskId: 1,
  // RagTaskDTO | The prompt task info to be updated
  ragTaskDTO: {
    sourceType: "sourceType_example",
    source: "source_example",
  },
};

apiInstance.updateRagTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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


