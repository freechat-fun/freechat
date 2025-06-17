# .AIServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addAiApiKey**](AIServiceApi.md#addAiApiKey) | **POST** /api/v2/ai/apikey | Add Model Provider Credential
[**deleteAiApiKey**](AIServiceApi.md#deleteAiApiKey) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider
[**disableAiApiKey**](AIServiceApi.md#disableAiApiKey) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enableAiApiKey**](AIServiceApi.md#enableAiApiKey) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential
[**getAiApiKey**](AIServiceApi.md#getAiApiKey) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider
[**listAiApiKeys**](AIServiceApi.md#listAiApiKeys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider


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
    provider: "azure_open_ai",
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


