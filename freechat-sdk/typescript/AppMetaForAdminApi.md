# .AppMetaForAdminApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**expose**](AppMetaForAdminApi.md#expose) | **GET** /api/v2/admin/app/expose | Expose DTO definitions
[**getAppMeta**](AppMetaForAdminApi.md#getAppMeta) | **GET** /api/v2/admin/app/meta | Get Application Information


# **expose**
> string expose()

This method does nothing.

### Example


```typescript
import { createConfiguration, AppMetaForAdminApi } from '';
import type { AppMetaForAdminApiExposeRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AppMetaForAdminApi(configuration);

const request: AppMetaForAdminApiExposeRequest = {
  
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
};

const data = await apiInstance.expose(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **openAiParam** | **OpenAiParamDTO** |  | defaults to undefined
 **qwenParam** | **QwenParamDTO** |  | defaults to undefined


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
import { createConfiguration, AppMetaForAdminApi } from '';

const configuration = createConfiguration();
const apiInstance = new AppMetaForAdminApi(configuration);

const request = {};

const data = await apiInstance.getAppMeta(request);
console.log('API called successfully. Returned data:', data);
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


