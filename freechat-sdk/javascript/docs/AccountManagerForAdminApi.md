# freechat-sdk.AccountManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createTokenForUser**](AccountManagerForAdminApi.md#createTokenForUser) | **POST** /api/v1/admin/token/{username}/{duration} | Create API Token for User.
[**createUser**](AccountManagerForAdminApi.md#createUser) | **POST** /api/v1/admin/user | Create User
[**deleteTokenForUser**](AccountManagerForAdminApi.md#deleteTokenForUser) | **DELETE** /api/v1/admin/token/{token} | Delete API Token
[**deleteUser**](AccountManagerForAdminApi.md#deleteUser) | **DELETE** /api/v1/admin/user/{username} | Delete User
[**disableTokenForUser**](AccountManagerForAdminApi.md#disableTokenForUser) | **PUT** /api/v1/admin/token/{token} | Disable API Token
[**getDetailsOfUser**](AccountManagerForAdminApi.md#getDetailsOfUser) | **GET** /api/v1/admin/user/{username} | Get User Details
[**getUserByToken**](AccountManagerForAdminApi.md#getUserByToken) | **GET** /api/v1/admin/tokenBy/{token} | Get User by API Token
[**listAuthoritiesOfUser**](AccountManagerForAdminApi.md#listAuthoritiesOfUser) | **GET** /api/v1/admin/authority/{username} | List User Permissions
[**listTokensOfUser**](AccountManagerForAdminApi.md#listTokensOfUser) | **GET** /api/v1/admin/token/{username} | Get API Token of User
[**listUsers**](AccountManagerForAdminApi.md#listUsers) | **GET** /api/v1/admin/users/{pageSize}/{pageNum} | List User Information
[**listUsers1**](AccountManagerForAdminApi.md#listUsers1) | **GET** /api/v1/admin/users/{pageSize} | List User Information
[**listUsers2**](AccountManagerForAdminApi.md#listUsers2) | **GET** /api/v1/admin/users | List User Information
[**updateAuthoritiesOfUser**](AccountManagerForAdminApi.md#updateAuthoritiesOfUser) | **PUT** /api/v1/admin/authority/{username} | Update User Permissions
[**updateUser**](AccountManagerForAdminApi.md#updateUser) | **PUT** /api/v1/admin/user | Update User



## createTokenForUser

> String createTokenForUser(username, duration)

Create API Token for User.

Create an API Token for a specified user, valid for duration seconds.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
let duration = 789; // Number | Validity period (seconds)
apiInstance.createTokenForUser(username, duration).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 
 **duration** | **Number**| Validity period (seconds) | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## createUser

> Boolean createUser(userFullDetailsDTO)

Create User

Create user.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let userFullDetailsDTO = {"username":"Jack","password":"jack","nickname":"Jack（测试账号）"}; // UserFullDetailsDTO | User information
apiInstance.createUser(userFullDetailsDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deleteTokenForUser

> Boolean deleteTokenForUser(token)

Delete API Token

Delete the specified API Token.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let token = "token_example"; // String | API Token
apiInstance.deleteTokenForUser(token).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **String**| API Token | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## deleteUser

> Boolean deleteUser(username)

Delete User

Delete user by username.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
apiInstance.deleteUser(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## disableTokenForUser

> Boolean disableTokenForUser(token)

Disable API Token

Disable the specified API Token.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let token = "token_example"; // String | API Token
apiInstance.disableTokenForUser(token).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **String**| API Token | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getDetailsOfUser

> UserDetailsDTO getDetailsOfUser(username)

Get User Details

Return detailed user information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
apiInstance.getDetailsOfUser(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getUserByToken

> UserDetailsDTO getUserByToken(token)

Get User by API Token

Get the detailed user information corresponding to the API Token.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let token = "token_example"; // String | API Token
apiInstance.getUserByToken(token).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **String**| API Token | 

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listAuthoritiesOfUser

> [String] listAuthoritiesOfUser(username)

List User Permissions

List the user&#39;s permissions.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
apiInstance.listAuthoritiesOfUser(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listTokensOfUser

> [String] listTokensOfUser(username)

Get API Token of User

Get the list of API Tokens of the user.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
apiInstance.listTokensOfUser(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listUsers

> [UserBasicInfoDTO] listUsers(pageSize, pageNum)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let pageSize = 789; // Number | Maximum quantity
let pageNum = 789; // Number | Current page number
apiInstance.listUsers(pageSize, pageNum).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **Number**| Maximum quantity | 
 **pageNum** | **Number**| Current page number | 

### Return type

[**[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listUsers1

> [UserBasicInfoDTO] listUsers1(pageSize)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let pageSize = 789; // Number | Maximum quantity
apiInstance.listUsers1(pageSize).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **Number**| Maximum quantity | 

### Return type

[**[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listUsers2

> [UserBasicInfoDTO] listUsers2()

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
apiInstance.listUsers2().then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters

This endpoint does not need any parameter.

### Return type

[**[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updateAuthoritiesOfUser

> Boolean updateAuthoritiesOfUser(username, requestBody)

Update User Permissions

Update the user&#39;s permission list.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let username = "username_example"; // String | Username
let requestBody = ["null"]; // [String] | Permission list
apiInstance.updateAuthoritiesOfUser(username, requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 
 **requestBody** | [**[String]**](String.md)| Permission list | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateUser

> Boolean updateUser(userFullDetailsDTO)

Update User

Update user information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.AccountManagerForAdminApi();
let userFullDetailsDTO = new freechat-sdk.UserFullDetailsDTO(); // UserFullDetailsDTO | User information
apiInstance.updateUser(userFullDetailsDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

