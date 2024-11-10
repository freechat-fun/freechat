# .AccountApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createToken**](AccountApi.md#createToken) | **POST** /api/v2/account/token/{duration} | Create API Token
[**createToken1**](AccountApi.md#createToken1) | **POST** /api/v2/account/token | Create API Token
[**deleteToken**](AccountApi.md#deleteToken) | **DELETE** /api/v2/account/token/{token} | Delete API Token
[**deleteTokenById**](AccountApi.md#deleteTokenById) | **DELETE** /api/v2/account/token/id/{id} | Delete API Token by Id
[**disableToken**](AccountApi.md#disableToken) | **PUT** /api/v2/account/token/{token} | Disable API Token
[**disableTokenById**](AccountApi.md#disableTokenById) | **PUT** /api/v2/account/token/id/{id} | Disable API Token by Id
[**getTokenById**](AccountApi.md#getTokenById) | **GET** /api/v2/account/token/id/{id} | Get API Token by Id
[**getUserBasic**](AccountApi.md#getUserBasic) | **GET** /api/v2/account/basic/{username} | Get User Basic Information
[**getUserBasic1**](AccountApi.md#getUserBasic1) | **GET** /api/v2/account/basic | Get User Basic Information
[**getUserDetails**](AccountApi.md#getUserDetails) | **GET** /api/v2/account/details | Get User Details
[**listTokens**](AccountApi.md#listTokens) | **GET** /api/v2/account/tokens | List API Tokens
[**updateUserInfo**](AccountApi.md#updateUserInfo) | **PUT** /api/v2/account/details | Update User Details
[**uploadUserPicture**](AccountApi.md#uploadUserPicture) | **POST** /api/v2/account/picture | Upload User Picture


# **createToken**
> string createToken()

Create a timed API Token, valid for {duration} seconds.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiCreateTokenRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiCreateTokenRequest = {
    // Token validity duration (seconds)
  duration: 1,
};

const data = await apiInstance.createToken(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **duration** | [**number**] | Token validity duration (seconds) | defaults to undefined


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

# **createToken1**
> string createToken1()

Create a timed API Token, valid for {duration} seconds.

### Example


```typescript
import { createConfiguration, AccountApi } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request = {};

const data = await apiInstance.createToken1(request);
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

# **deleteToken**
> string deleteToken()

Delete an API Token.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiDeleteTokenRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiDeleteTokenRequest = {
    // Token content
  token: "token_example",
};

const data = await apiInstance.deleteToken(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | [**string**] | Token content | defaults to undefined


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

# **deleteTokenById**
> boolean deleteTokenById()

Delete the API token by id.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiDeleteTokenByIdRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiDeleteTokenByIdRequest = {
    // Token id
  id: 1,
};

const data = await apiInstance.deleteTokenById(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Token id | defaults to undefined


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

# **disableToken**
> string disableToken()

Disable an API Token, the token is not deleted.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiDisableTokenRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiDisableTokenRequest = {
    // Token content
  token: "token_example",
};

const data = await apiInstance.disableToken(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | [**string**] | Token content | defaults to undefined


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

# **disableTokenById**
> boolean disableTokenById()

Disable the API token by id.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiDisableTokenByIdRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiDisableTokenByIdRequest = {
    // Token id
  id: 1,
};

const data = await apiInstance.disableTokenById(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Token id | defaults to undefined


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

# **getTokenById**
> string getTokenById()

Get the API token by id.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiGetTokenByIdRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiGetTokenByIdRequest = {
    // Token id
  id: 1,
};

const data = await apiInstance.getTokenById(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | [**number**] | Token id | defaults to undefined


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

# **getUserBasic**
> UserBasicInfoDTO getUserBasic()

Return user basic information, including: username, nickname, avatar link.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiGetUserBasicRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiGetUserBasicRequest = {
    // Username
  username: "username_example",
};

const data = await apiInstance.getUserBasic(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


### Return type

**UserBasicInfoDTO**

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

# **getUserBasic1**
> UserBasicInfoDTO getUserBasic1()

Return user basic information, including: username, nickname, avatar link.

### Example


```typescript
import { createConfiguration, AccountApi } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request = {};

const data = await apiInstance.getUserBasic1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**UserBasicInfoDTO**

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

# **getUserDetails**
> UserDetailsDTO getUserDetails()

Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).

### Example


```typescript
import { createConfiguration, AccountApi } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request = {};

const data = await apiInstance.getUserDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


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

# **listTokens**
> Array<ApiTokenInfoDTO> listTokens()

List currently valid tokens.

### Example


```typescript
import { createConfiguration, AccountApi } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request = {};

const data = await apiInstance.listTokens(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


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

# **updateUserInfo**
> boolean updateUserInfo(userDetailsDTO)

Update the detailed user information of the current account.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiUpdateUserInfoRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiUpdateUserInfoRequest = {
    // User information
  userDetailsDTO: {
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
  },
};

const data = await apiInstance.updateUserInfo(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userDetailsDTO** | **UserDetailsDTO**| User information |


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

# **uploadUserPicture**
> string uploadUserPicture()

Upload a picture of the user.

### Example


```typescript
import { createConfiguration, AccountApi } from '';
import type { AccountApiUploadUserPictureRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AccountApi(configuration);

const request: AccountApiUploadUserPictureRequest = {
    // User picture
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

const data = await apiInstance.uploadUserPicture(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | [**HttpFile**] | User picture | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


