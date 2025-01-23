# .AppMetaForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAppMeta**](AppMetaForAdminApi.md#getAppMeta) | **GET** /api/v2/admin/app/meta | Get Application Information


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


