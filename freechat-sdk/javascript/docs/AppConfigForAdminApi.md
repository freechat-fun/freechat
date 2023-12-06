# freechat-sdk.AppConfigForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAppConfig**](AppConfigForAdminApi.md#getAppConfig) | **GET** /api/v1/admin/app/config/{name} | Get Configuration
[**getAppConfigByVersion**](AppConfigForAdminApi.md#getAppConfigByVersion) | **GET** /api/v1/admin/app/config/{name}/{version} | Get Specified Version of Configuration
[**listAppConfigNames**](AppConfigForAdminApi.md#listAppConfigNames) | **POST** /api/v1/admin/app/configs | List Configuration Names
[**publishAppConfig**](AppConfigForAdminApi.md#publishAppConfig) | **POST** /api/v1/admin/app/config | Publish Configuration



## getAppConfig

> AppConfigInfoDTO getAppConfig(name)

Get Configuration

Get the latest configuration information of the application by name.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppConfigForAdminApi();
let name = "name_example"; // String | Configuration name
apiInstance.getAppConfig(name).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Configuration name | 

### Return type

[**AppConfigInfoDTO**](AppConfigInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAppConfigByVersion

> AppConfigInfoDTO getAppConfigByVersion(name, version)

Get Specified Version of Configuration

Get the configuration information of the application by name and version.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppConfigForAdminApi();
let name = "name_example"; // String | Configuration name
let version = 56; // Number | Configuration version
apiInstance.getAppConfigByVersion(name, version).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Configuration name | 
 **version** | **Number**| Configuration version | 

### Return type

[**AppConfigInfoDTO**](AppConfigInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAppConfigNames

> [String] listAppConfigNames()

List Configuration Names

List all application configuration names.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppConfigForAdminApi();
apiInstance.listAppConfigNames().then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters

This endpoint does not need any parameter.

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## publishAppConfig

> Number publishAppConfig(appConfigCreateDTO)

Publish Configuration

Publish application configuration, return configuration version.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AppConfigForAdminApi();
let appConfigCreateDTO = new freechat-sdk.AppConfigCreateDTO(); // AppConfigCreateDTO | Configuration information
apiInstance.publishAppConfig(appConfigCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **appConfigCreateDTO** | [**AppConfigCreateDTO**](AppConfigCreateDTO.md)| Configuration information | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

