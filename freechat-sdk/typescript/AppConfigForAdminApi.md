# .AppConfigForAdminApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAppConfigs**](AppConfigForAdminApi.md#getAppConfigs) | **GET** /api/v2/admin/app/configs | Get Configurations


# **getAppConfigs**
> AppConfigInfoDTO getAppConfigs()

Get all configuration information of the application.

### Example


```typescript
import { createConfiguration, AppConfigForAdminApi } from '';

const configuration = createConfiguration();
const apiInstance = new AppConfigForAdminApi(configuration);

const request = {};

const data = await apiInstance.getAppConfigs(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


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


