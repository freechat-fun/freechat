# .AppConfigForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getDefaultConfig**](AppConfigForAdminApi.md#getDefaultConfig) | **GET** /api/v2/admin/app/configs/default | Get Default Config


# **getDefaultConfig**
> string getDefaultConfig()

Get default configuration information of the application.

### Example


```typescript
import { createConfiguration, AppConfigForAdminApi } from '';

const configuration = createConfiguration();
const apiInstance = new AppConfigForAdminApi(configuration);

const request = {};

const data = await apiInstance.getDefaultConfig(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


