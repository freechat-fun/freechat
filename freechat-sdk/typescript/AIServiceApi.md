# .AIServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addAiApiKey**](AIServiceApi.md#addAiApiKey) | **POST** /api/v2/ai/apikey | Add Model Provider Credential
[**deleteAiApiKey**](AIServiceApi.md#deleteAiApiKey) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider
[**disableAiApiKey**](AIServiceApi.md#disableAiApiKey) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enableAiApiKey**](AIServiceApi.md#enableAiApiKey) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential
[**getAiApiKey**](AIServiceApi.md#getAiApiKey) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider
[**getAiModelInfo**](AIServiceApi.md#getAiModelInfo) | **GET** /api/v2/public/ai/model/{modelId} | Get Model Information
[**listAiApiKeys**](AIServiceApi.md#listAiApiKeys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider
[**listAiModelInfo**](AIServiceApi.md#listAiModelInfo) | **GET** /api/v2/public/ai/models | List Models
[**listAiModelInfo1**](AIServiceApi.md#listAiModelInfo1) | **GET** /api/v2/public/ai/models/{pageSize} | List Models
[**listAiModelInfo2**](AIServiceApi.md#listAiModelInfo2) | **GET** /api/v2/public/ai/models/{pageSize}/{pageNum} | List Models


# **addAiApiKey**
> number addAiApiKey(aiApiKeyCreateDTO)

Add a credential for the model service.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiAddAiApiKeyRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiAddAiApiKeyRequest = {
    // Model call credential information
  aiApiKeyCreateDTO: {
    name: "name_example",
    provider: "provider_example",
    token: "token_example",
    enabled: true,
  },
};

const data = await apiInstance.addAiApiKey(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aiApiKeyCreateDTO** | **AiApiKeyCreateDTO**| Model call credential information |


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

# **deleteAiApiKey**
> boolean deleteAiApiKey()

Delete the credential information of the model provider.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiDeleteAiApiKeyRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiDeleteAiApiKeyRequest = {
    // Credential identifier
  id: 1,
};

const data = await apiInstance.deleteAiApiKey(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Credential identifier | defaults to undefined


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

# **disableAiApiKey**
> boolean disableAiApiKey()

Disable the credential information of the model provider.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiDisableAiApiKeyRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiDisableAiApiKeyRequest = {
    // Credential identifier
  id: 1,
};

const data = await apiInstance.disableAiApiKey(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Credential identifier | defaults to undefined


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

# **enableAiApiKey**
> boolean enableAiApiKey()

Enable the credential information of the model provider.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiEnableAiApiKeyRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiEnableAiApiKeyRequest = {
    // Credential identifier
  id: 1,
};

const data = await apiInstance.enableAiApiKey(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Credential identifier | defaults to undefined


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

# **getAiApiKey**
> AiApiKeyInfoDTO getAiApiKey()

Get the credential information of the model provider.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiGetAiApiKeyRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiGetAiApiKeyRequest = {
    // Credential identifier
  id: 1,
};

const data = await apiInstance.getAiApiKey(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Credential identifier | defaults to undefined


### Return type

**AiApiKeyInfoDTO**

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

# **getAiModelInfo**
> AiModelInfoDTO getAiModelInfo()

Return specific model information.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiGetAiModelInfoRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiGetAiModelInfoRequest = {
    // Model identifier
  modelId: "modelId_example",
};

const data = await apiInstance.getAiModelInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **modelId** | [**string**] | Model identifier | defaults to undefined


### Return type

**AiModelInfoDTO**

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

# **listAiApiKeys**
> Array<AiApiKeyInfoDTO> listAiApiKeys()

List all credential information of the model provider.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiListAiApiKeysRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiListAiApiKeysRequest = {
    // Model provider
  provider: "provider_example",
};

const data = await apiInstance.listAiApiKeys(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **provider** | [**string**] | Model provider | defaults to undefined


### Return type

**Array<AiApiKeyInfoDTO>**

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

# **listAiModelInfo**
> Array<AiModelInfoDTO> listAiModelInfo()

Return model information by page, return the pageNum page, up to pageSize model information.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request = {};

const data = await apiInstance.listAiModelInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<AiModelInfoDTO>**

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

# **listAiModelInfo1**
> Array<AiModelInfoDTO> listAiModelInfo1()

Return model information by page, return the pageNum page, up to pageSize model information.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiListAiModelInfo1Request } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiListAiModelInfo1Request = {
    // Maximum quantity
  pageSize: 1,
};

const data = await apiInstance.listAiModelInfo1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined


### Return type

**Array<AiModelInfoDTO>**

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

# **listAiModelInfo2**
> Array<AiModelInfoDTO> listAiModelInfo2()

Return model information by page, return the pageNum page, up to pageSize model information.

### Example


```typescript
import { createConfiguration, AIServiceApi } from '';
import type { AIServiceApiListAiModelInfo2Request } from '';

const configuration = createConfiguration();
const apiInstance = new AIServiceApi(configuration);

const request: AIServiceApiListAiModelInfo2Request = {
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
};

const data = await apiInstance.listAiModelInfo2(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined


### Return type

**Array<AiModelInfoDTO>**

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


