# .AppMetaForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**expose**](AppMetaForAdminApi.md#expose) | **GET** /api/v1/admin/app/expose | Expose DTO definitions
[**getAppMeta**](AppMetaForAdminApi.md#getAppMeta) | **GET** /api/v1/admin/app/meta | Get Application Information


# **expose**
> string expose()

This method does nothing.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppMetaForAdminApi(configuration);

let body:.AppMetaForAdminApiExposeRequest = {
  // OpenAiParamDTO
  openAiParam: {
    apiKey: "apiKey_example",
    apiKeyName: "apiKeyName_example",
    modelId: "modelId_example",
    baseUrl: "baseUrl_example",
    temperature: 3.14,
    topP: 3.14,
    maxTokens: 1,
    presencePenalty: 3.14,
    frequencyPenalty: 3.14,
    seed: 1,
    stop: [
      "stop_example",
    ],
  },
  // QwenParamDTO
  qwenParam: {
    apiKey: "apiKey_example",
    apiKeyName: "apiKeyName_example",
    modelId: "modelId_example",
    topP: 3.14,
    topK: 1,
    maxTokens: 1,
    enableSearch: true,
    seed: 1,
    repetitionPenalty: 3.14,
    temperature: 3.14,
    stop: [
      "stop_example",
    ],
  },
  // LlmResultDTO
  aiForPromptResult: {
    requestId: "requestId_example",
    text: "text_example",
    message: {
      role: "role_example",
      name: "name_example",
      content: "content_example",
      toolCalls: [
        {
          id: "id_example",
          name: "name_example",
          arguments: "arguments_example",
        },
      ],
      gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
    },
    finishReason: "finishReason_example",
    tokenUsage: {
      inputTokenCount: 1,
      outputTokenCount: 1,
      totalTokenCount: 1,
    },
  },
};

apiInstance.expose(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **openAiParam** | **OpenAiParamDTO** |  | defaults to undefined
 **qwenParam** | **QwenParamDTO** |  | defaults to undefined
 **aiForPromptResult** | **LlmResultDTO** |  | defaults to undefined


### Return type

**string**

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

# **getAppMeta**
> AppMetaDTO getAppMeta()

Get application information to accurately locate the corresponding project version.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppMetaForAdminApi(configuration);

let body:any = {};

apiInstance.getAppMeta(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters
This endpoint does not need any parameter.


### Return type

**AppMetaDTO**

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


