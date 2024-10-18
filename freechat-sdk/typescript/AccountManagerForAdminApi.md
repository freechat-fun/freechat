# .AccountManagerForAdminApi

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


# **createTokenForUser**
> string createTokenForUser()

Create an API Token for a specified user, valid for duration seconds.

### Example


```typescript
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiCreateTokenForUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiCreateTokenForUserRequest = {
    // Username
  username: "username_example",
    // Validity period (seconds)
  duration: 0,
};

const data = await apiInstance.createTokenForUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiCreateUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiCreateUserRequest = {
    // User information
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

const data = await apiInstance.createUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiDeleteTokenForUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiDeleteTokenForUserRequest = {
    // API Token
  token: "token_example",
};

const data = await apiInstance.deleteTokenForUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiDeleteUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiDeleteUserRequest = {
    // Username
  username: "username_example",
};

const data = await apiInstance.deleteUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiDisableTokenForUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiDisableTokenForUserRequest = {
    // API Token
  token: "token_example",
};

const data = await apiInstance.disableTokenForUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiGetDetailsOfUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiGetDetailsOfUserRequest = {
    // Username
  username: "username_example",
};

const data = await apiInstance.getDetailsOfUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiGetUserByTokenRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiGetUserByTokenRequest = {
    // API Token
  token: "token_example",
};

const data = await apiInstance.getUserByToken(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiListAuthoritiesOfUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiListAuthoritiesOfUserRequest = {
    // Username
  username: "username_example",
};

const data = await apiInstance.listAuthoritiesOfUser(request);
console.log('API called successfully. Returned data:', data);
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
> Array<ApiTokenInfoDTO> listTokensOfUser()

Get the list of API Tokens of the user.

### Example


```typescript
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiListTokensOfUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiListTokensOfUserRequest = {
    // Username
  username: "username_example",
};

const data = await apiInstance.listTokensOfUser(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


### Return type

**Array<ApiTokenInfoDTO>**

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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiListUsersRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiListUsersRequest = {
    // Maximum quantity
  pageSize: 1,
    // Current page number
  pageNum: 1,
};

const data = await apiInstance.listUsers(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiListUsers1Request } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiListUsers1Request = {
    // Maximum quantity
  pageSize: 1,
};

const data = await apiInstance.listUsers1(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request = {};

const data = await apiInstance.listUsers2(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest = {
    // Username
  username: "username_example",
    // Permission list
  requestBody: [
    "requestBody_example",
  ],
};

const data = await apiInstance.updateAuthoritiesOfUser(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, AccountManagerForAdminApi } from '';
import type { AccountManagerForAdminApiUpdateUserRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountManagerForAdminApi(configuration);

const request: AccountManagerForAdminApiUpdateUserRequest = {
    // User information
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

const data = await apiInstance.updateUser(request);
console.log('API called successfully. Returned data:', data);
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


