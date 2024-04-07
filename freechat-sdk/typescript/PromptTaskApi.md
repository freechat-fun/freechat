# .PromptTaskApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createPromptTask**](PromptTaskApi.md#createPromptTask) | **POST** /api/v1/prompt/task | Create Prompt Task
[**deletePromptTask**](PromptTaskApi.md#deletePromptTask) | **DELETE** /api/v1/prompt/task/{promptTaskId} | Delete Prompt Task
[**getPromptTask**](PromptTaskApi.md#getPromptTask) | **GET** /api/v1/prompt/task/{promptTaskId} | Get Prompt Task
[**updatePromptTask**](PromptTaskApi.md#updatePromptTask) | **PUT** /api/v1/prompt/task/{promptTaskId} | Update Prompt Task


# **createPromptTask**
> string createPromptTask(promptTaskDTO)

Create a prompt task.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptTaskApi(configuration);

let body:.PromptTaskApiCreatePromptTaskRequest = {
  // PromptTaskDTO | The prompt task to be added
  promptTaskDTO: {
    promptRef: {
      promptId: 1,
      variables: {
        "key": {},
      },
      draft: true,
    },
    modelId: "modelId_example",
    apiKeyName: "apiKeyName_example",
    apiKeyValue: "apiKeyValue_example",
    params: {
      "key": {},
    },
    cron: "cron_example",
    status: "status_example",
  },
};

apiInstance.createPromptTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskDTO** | **PromptTaskDTO**| The prompt task to be added |


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **deletePromptTask**
> boolean deletePromptTask()

Delete a prompt task.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptTaskApi(configuration);

let body:.PromptTaskApiDeletePromptTaskRequest = {
  // string | The promptTaskId to be deleted
  promptTaskId: "promptTaskId_example",
};

apiInstance.deletePromptTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | [**string**] | The promptTaskId to be deleted | defaults to undefined


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

# **getPromptTask**
> PromptTaskDetailsDTO getPromptTask()

Get the prompt task details.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptTaskApi(configuration);

let body:.PromptTaskApiGetPromptTaskRequest = {
  // string | The promptTaskId to be queried
  promptTaskId: "promptTaskId_example",
};

apiInstance.getPromptTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | [**string**] | The promptTaskId to be queried | defaults to undefined


### Return type

**PromptTaskDetailsDTO**

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

# **updatePromptTask**
> boolean updatePromptTask(promptTaskDTO)

Update a prompt task.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptTaskApi(configuration);

let body:.PromptTaskApiUpdatePromptTaskRequest = {
  // string | The promptTaskId to be updated
  promptTaskId: "promptTaskId_example",
  // PromptTaskDTO | The prompt task info to be updated
  promptTaskDTO: {
    promptRef: {
      promptId: 1,
      variables: {
        "key": {},
      },
      draft: true,
    },
    modelId: "modelId_example",
    apiKeyName: "apiKeyName_example",
    apiKeyValue: "apiKeyValue_example",
    params: {
      "key": {},
    },
    cron: "cron_example",
    status: "status_example",
  },
};

apiInstance.updatePromptTask(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskDTO** | **PromptTaskDTO**| The prompt task info to be updated |
 **promptTaskId** | [**string**] | The promptTaskId to be updated | defaults to undefined


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


