# .AccountManagerForAdminApi

All URIs are relative to *https://freechat.fun*

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


# **createTokenForUser**
> string createTokenForUser()

Create an API Token for a specified user, valid for duration seconds.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiCreateTokenForUserRequest = {
  // string | Username
  username: "username_example",
  // number | Validity period (seconds)
  duration: 0,
};

apiInstance.createTokenForUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined
 **duration** | [**number**] | Validity period (seconds) | defaults to undefined


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

# **createUser**
> boolean createUser(userFullDetailsDTO)

Create user.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiCreateUserRequest = {
  // UserFullDetailsDTO | User information
  userFullDetailsDTO: {
    requestId: "requestId_example",
    username: "username_example",
    nickname: "nickname_example",
    givenName: "givenName_example",
    middleName: "middleName_example",
    familyName: "familyName_example",
    preferredUsername: "preferredUsername_example",
    profile: "profile_example",
    picture: "picture_example",
    website: "website_example",
    email: "email_example",
    gender: "gender_example",
    birthdate: new Date('1970-01-01T00:00:00.00Z'),
    zoneinfo: "zoneinfo_example",
    locale: "locale_example",
    phoneNumber: "phoneNumber_example",
    updatedAt: new Date('1970-01-01T00:00:00.00Z'),
    platform: "platform_example",
    enabled: true,
    locked: true,
    expiresAt: new Date('1970-01-01T00:00:00.00Z'),
    passwordExpiresAt: new Date('1970-01-01T00:00:00.00Z'),
    address: "address_example",
    password: "password_example",
  },
};

apiInstance.createUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | **UserFullDetailsDTO**| User information |


### Return type

**boolean**

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

# **deleteTokenForUser**
> boolean deleteTokenForUser()

Delete the specified API Token.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiDeleteTokenForUserRequest = {
  // string | API Token
  token: "token_example",
};

apiInstance.deleteTokenForUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | [**string**] | API Token | defaults to undefined


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

# **deleteUser**
> boolean deleteUser()

Delete user by username.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiDeleteUserRequest = {
  // string | Username
  username: "username_example",
};

apiInstance.deleteUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


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

# **disableTokenForUser**
> boolean disableTokenForUser()

Disable the specified API Token.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiDisableTokenForUserRequest = {
  // string | API Token
  token: "token_example",
};

apiInstance.disableTokenForUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | [**string**] | API Token | defaults to undefined


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

# **getDetailsOfUser**
> UserDetailsDTO getDetailsOfUser()

Return detailed user information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiGetDetailsOfUserRequest = {
  // string | Username
  username: "username_example",
};

apiInstance.getDetailsOfUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


### Return type

**UserDetailsDTO**

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

# **getUserByToken**
> UserDetailsDTO getUserByToken()

Get the detailed user information corresponding to the API Token.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiGetUserByTokenRequest = {
  // string | API Token
  token: "token_example",
};

apiInstance.getUserByToken(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | [**string**] | API Token | defaults to undefined


### Return type

**UserDetailsDTO**

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

# **listAuthoritiesOfUser**
> Set<string> listAuthoritiesOfUser()

List the user\'s permissions.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiListAuthoritiesOfUserRequest = {
  // string | Username
  username: "username_example",
};

apiInstance.listAuthoritiesOfUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


### Return type

**Set<string>**

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

# **listTokensOfUser**
> Array<string> listTokensOfUser()

Get the list of API Tokens of the user.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiListTokensOfUserRequest = {
  // string | Username
  username: "username_example",
};

apiInstance.listTokensOfUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


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

# **listUsers**
> Array<UserBasicInfoDTO> listUsers()

Return user information by page, return the pageNum page, up to pageSize user information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiListUsersRequest = {
  // number | Maximum quantity
  pageSize: 1,
  // number | Current page number
  pageNum: 0,
};

apiInstance.listUsers(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined
 **pageNum** | [**number**] | Current page number | defaults to undefined


### Return type

**Array<UserBasicInfoDTO>**

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

# **listUsers1**
> Array<UserBasicInfoDTO> listUsers1()

Return user information by page, return the pageNum page, up to pageSize user information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiListUsers1Request = {
  // number | Maximum quantity
  pageSize: 1,
};

apiInstance.listUsers1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | [**number**] | Maximum quantity | defaults to undefined


### Return type

**Array<UserBasicInfoDTO>**

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

# **listUsers2**
> Array<UserBasicInfoDTO> listUsers2()

Return user information by page, return the pageNum page, up to pageSize user information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:any = {};

apiInstance.listUsers2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<UserBasicInfoDTO>**

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

# **updateAuthoritiesOfUser**
> boolean updateAuthoritiesOfUser(requestBody)

Update the user\'s permission list.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest = {
  // string | Username
  username: "username_example",
  // Set<string> | Permission list
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.updateAuthoritiesOfUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Set<string>**| Permission list |
 **username** | [**string**] | Username | defaults to undefined


### Return type

**boolean**

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

# **updateUser**
> boolean updateUser(userFullDetailsDTO)

Update user information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountManagerForAdminApi(configuration);

let body:.AccountManagerForAdminApiUpdateUserRequest = {
  // UserFullDetailsDTO | User information
  userFullDetailsDTO: {
    requestId: "requestId_example",
    username: "username_example",
    nickname: "nickname_example",
    givenName: "givenName_example",
    middleName: "middleName_example",
    familyName: "familyName_example",
    preferredUsername: "preferredUsername_example",
    profile: "profile_example",
    picture: "picture_example",
    website: "website_example",
    email: "email_example",
    gender: "gender_example",
    birthdate: new Date('1970-01-01T00:00:00.00Z'),
    zoneinfo: "zoneinfo_example",
    locale: "locale_example",
    phoneNumber: "phoneNumber_example",
    updatedAt: new Date('1970-01-01T00:00:00.00Z'),
    platform: "platform_example",
    enabled: true,
    locked: true,
    expiresAt: new Date('1970-01-01T00:00:00.00Z'),
    passwordExpiresAt: new Date('1970-01-01T00:00:00.00Z'),
    address: "address_example",
    password: "password_example",
  },
};

apiInstance.updateUser(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | **UserFullDetailsDTO**| User information |


### Return type

**boolean**

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


