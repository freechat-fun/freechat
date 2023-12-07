# .AppConfigForAdminApi

All URIs are relative to *https://freechat.fun*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAppConfig**](AppConfigForAdminApi.md#getAppConfig) | **GET** /api/v1/admin/app/config/{name} | Get Configuration
[**getAppConfigByVersion**](AppConfigForAdminApi.md#getAppConfigByVersion) | **GET** /api/v1/admin/app/config/{name}/{version} | Get Specified Version of Configuration
[**listAppConfigNames**](AppConfigForAdminApi.md#listAppConfigNames) | **POST** /api/v1/admin/app/configs | List Configuration Names
[**publishAppConfig**](AppConfigForAdminApi.md#publishAppConfig) | **POST** /api/v1/admin/app/config | Publish Configuration


# **getAppConfig**
> AppConfigInfoDTO getAppConfig()

Get the latest configuration information of the application by name.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppConfigForAdminApi(configuration);

let body:.AppConfigForAdminApiGetAppConfigRequest = {
  // string | Configuration name
  name: "name_example",
};

apiInstance.getAppConfig(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Configuration name | defaults to undefined


### Return type

**AppConfigInfoDTO**

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

# **getAppConfigByVersion**
> AppConfigInfoDTO getAppConfigByVersion()

Get the configuration information of the application by name and version.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppConfigForAdminApi(configuration);

let body:.AppConfigForAdminApiGetAppConfigByVersionRequest = {
  // string | Configuration name
  name: "name_example",
  // number | Configuration version
  version: 1,
};

apiInstance.getAppConfigByVersion(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Configuration name | defaults to undefined
 **version** | [**number**] | Configuration version | defaults to undefined


### Return type

**AppConfigInfoDTO**

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

# **listAppConfigNames**
> Array<string> listAppConfigNames()

List all application configuration names.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppConfigForAdminApi(configuration);

let body:any = {};

apiInstance.listAppConfigNames(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<string>**

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

# **publishAppConfig**
> number publishAppConfig(appConfigCreateDTO)

Publish application configuration, return configuration version.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AppConfigForAdminApi(configuration);

let body:.AppConfigForAdminApiPublishAppConfigRequest = {
  // AppConfigCreateDTO | Configuration information
  appConfigCreateDTO: {
    name: "name_example",
    format: "format_example",
    content: "content_example",
  },
};

apiInstance.publishAppConfig(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **appConfigCreateDTO** | **AppConfigCreateDTO**| Configuration information |


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


