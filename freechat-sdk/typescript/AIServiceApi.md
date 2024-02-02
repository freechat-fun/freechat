# .AIServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addAiApiKey**](AIServiceApi.md#addAiApiKey) | **POST** /api/v1/ai/apikey | Add Model Provider Credential
[**deleteAiApiKey**](AIServiceApi.md#deleteAiApiKey) | **DELETE** /api/v1/ai/apikey/{id} | Delete Credential of Model Provider
[**disableAiApiKey**](AIServiceApi.md#disableAiApiKey) | **PUT** /api/v1/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enableAiApiKey**](AIServiceApi.md#enableAiApiKey) | **PUT** /api/v1/ai/apikey/enable/{id} | Enable Model Provider Credential
[**getAiApiKey**](AIServiceApi.md#getAiApiKey) | **GET** /api/v1/ai/apikey/{id} | Get credential of Model Provider
[**getAiModelInfo**](AIServiceApi.md#getAiModelInfo) | **GET** /api/v1/ai/model/{modelId} | Get Model Information
[**listAiApiKeys**](AIServiceApi.md#listAiApiKeys) | **GET** /api/v1/ai/apikeys/{provider} | List Credentials of Model Provider
[**listAiModelInfo**](AIServiceApi.md#listAiModelInfo) | **GET** /api/v1/ai/models/{pageSize} | List Models
[**listAiModelInfo1**](AIServiceApi.md#listAiModelInfo1) | **GET** /api/v1/ai/models | List Models
[**listAiModelInfo2**](AIServiceApi.md#listAiModelInfo2) | **GET** /api/v1/ai/models/{pageSize}/{pageNum} | List Models


# **addAiApiKey**
> number addAiApiKey(aiApiKeyCreateDTO)

Add a credential for the model service.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiAddAiApiKeyRequest = {
  // AiApiKeyCreateDTO | Model call credential information
  aiApiKeyCreateDTO: {
    name: "name_example",
    provider: "provider_example",
    token: "token_example",
    enabled: true,
  },
};

apiInstance.addAiApiKey(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiDeleteAiApiKeyRequest = {
  // number | Credential identifier
  id: 1,
};

apiInstance.deleteAiApiKey(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiDisableAiApiKeyRequest = {
  // number | Credential identifier
  id: 1,
};

apiInstance.disableAiApiKey(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiEnableAiApiKeyRequest = {
  // number | Credential identifier
  id: 1,
};

apiInstance.enableAiApiKey(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiGetAiApiKeyRequest = {
  // number | Credential identifier
  id: 1,
};

apiInstance.getAiApiKey(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiGetAiModelInfoRequest = {
  // string | Model identifier
  modelId: "modelId_example",
};

apiInstance.getAiModelInfo(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiListAiApiKeysRequest = {
  // string | Model provider
  provider: "provider_example",
};

apiInstance.listAiApiKeys(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiListAiModelInfoRequest = {
  // number | Maximum quantity
  pageSize: 1,
};

apiInstance.listAiModelInfo(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **listAiModelInfo1**
> Array<AiModelInfoDTO> listAiModelInfo1()

Return model information by page, return the pageNum page, up to pageSize model information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:any = {};

apiInstance.listAiModelInfo1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **listAiModelInfo2**
> Array<AiModelInfoDTO> listAiModelInfo2()

Return model information by page, return the pageNum page, up to pageSize model information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AIServiceApi(configuration);

let body:.AIServiceApiListAiModelInfo2Request = {
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 1,
};

apiInstance.listAiModelInfo2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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


