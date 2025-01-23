# .AIManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createOrUpdateAiModelInfo**](AIManagerForBizAdminApi.md#createOrUpdateAiModelInfo) | **PUT** /api/v2/biz/admin/ai/model | Create or Update Model Information
[**deleteAiModelInfo**](AIManagerForBizAdminApi.md#deleteAiModelInfo) | **DELETE** /api/v2/biz/admin/ai/model/{modelId} | Delete Model Information


# **createOrUpdateAiModelInfo**
> string createOrUpdateAiModelInfo(aiModelInfoUpdateDTO)

Create or update model information. If no modelId is passed or the modelId does not exist in the database, create a new one (keep the same modelId); otherwise update. Return modelId if successful.

### Example


```typescript
import { createConfiguration, AIManagerForBizAdminApi } from '';
import type { AIManagerForBizAdminApiCreateOrUpdateAiModelInfoRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIManagerForBizAdminApi(configuration);

const request: AIManagerForBizAdminApiCreateOrUpdateAiModelInfoRequest = {
    // Model information
  aiModelInfoUpdateDTO: {
    name: "name_example",
    description: "description_example",
    provider: "azure_open_ai",
    type: "text2text",
  },
};

const data = await apiInstance.createOrUpdateAiModelInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aiModelInfoUpdateDTO** | **AiModelInfoUpdateDTO**| Model information |


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

# **deleteAiModelInfo**
> boolean deleteAiModelInfo()

Delete model information based on modelId.

### Example


```typescript
import { createConfiguration, AIManagerForBizAdminApi } from '';
import type { AIManagerForBizAdminApiDeleteAiModelInfoRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIManagerForBizAdminApi(configuration);

const request: AIManagerForBizAdminApiDeleteAiModelInfoRequest = {
    // Model identifier
  modelId: "modelId_example",
};

const data = await apiInstance.deleteAiModelInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **modelId** | [**string**] | Model identifier | defaults to undefined


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


