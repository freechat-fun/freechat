# .AccountApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createToken**](AccountApi.md#createToken) | **POST** /api/v1/account/token | Create API Token
[**createToken1**](AccountApi.md#createToken1) | **POST** /api/v1/account/token/{duration} | Create API Token
[**deleteToken**](AccountApi.md#deleteToken) | **DELETE** /api/v1/account/token/{token} | Delete API Token
[**deleteTokenById**](AccountApi.md#deleteTokenById) | **DELETE** /api/v1/account/token/id/{id} | Delete API Token by Id
[**disableToken**](AccountApi.md#disableToken) | **PUT** /api/v1/account/token/{token} | Disable API Token
[**disableTokenById**](AccountApi.md#disableTokenById) | **PUT** /api/v1/account/token/id/{id} | Disable API Token by Id
[**getTokenById**](AccountApi.md#getTokenById) | **GET** /api/v1/account/token/id/{id} | Get API Token by Id
[**getUserBasic**](AccountApi.md#getUserBasic) | **GET** /api/v1/account/basic | Get User Basic Information
[**getUserBasic1**](AccountApi.md#getUserBasic1) | **GET** /api/v1/account/basic/{username} | Get User Basic Information
[**getUserDetails**](AccountApi.md#getUserDetails) | **GET** /api/v1/account/details | Get User Details
[**listTokens**](AccountApi.md#listTokens) | **GET** /api/v1/account/tokens | List API Tokens
[**updateUserInfo**](AccountApi.md#updateUserInfo) | **PUT** /api/v1/account/details | Update User Details
[**uploadUserPicture**](AccountApi.md#uploadUserPicture) | **POST** /api/v1/account/picture | Upload User Picture


# **createToken**
> string createToken()

Create a timed API Token, valid for {duration} seconds.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:any = {};

apiInstance.createToken(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **createToken1**
> string createToken1()

Create a timed API Token, valid for {duration} seconds.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiCreateToken1Request = {
  // number | Token validity duration (seconds)
  duration: 1,
};

apiInstance.createToken1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **deleteToken**
> string deleteToken()

Delete an API Token.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiDeleteTokenRequest = {
  // string | Token content
  token: "token_example",
};

apiInstance.deleteToken(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiDeleteTokenByIdRequest = {
  // number | Token id
  id: 1,
};

apiInstance.deleteTokenById(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiDisableTokenRequest = {
  // string | Token content
  token: "token_example",
};

apiInstance.disableToken(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiDisableTokenByIdRequest = {
  // number | Token id
  id: 1,
};

apiInstance.disableTokenById(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiGetTokenByIdRequest = {
  // number | Token id
  id: 1,
};

apiInstance.getTokenById(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:any = {};

apiInstance.getUserBasic(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **getUserBasic1**
> UserBasicInfoDTO getUserBasic1()

Return user basic information, including: username, nickname, avatar link.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiGetUserBasic1Request = {
  // string | Username
  username: "username_example",
};

apiInstance.getUserBasic1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **getUserDetails**
> UserDetailsDTO getUserDetails()

Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:any = {};

apiInstance.getUserDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:any = {};

apiInstance.listTokens(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiUpdateUserInfoRequest = {
  // UserDetailsDTO | User information
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

apiInstance.updateUserInfo(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AccountApi(configuration);

let body:.AccountApiUploadUserPictureRequest = {
  // HttpFile | User picture
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

apiInstance.uploadUserPicture(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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


