# AccountApi

All URIs are relative to **

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



## createToken

Create API Token

Create a timed API Token, valid for {duration} seconds.

### Example

```bash
freechat createToken duration=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **duration** | **integer** | Token validity duration (seconds) | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createToken1

Create API Token

Create a timed API Token, valid for {duration} seconds.

### Example

```bash
freechat createToken1
```

### Parameters

This endpoint does not need any parameter.

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteToken

Delete API Token

Delete an API Token.

### Example

```bash
freechat deleteToken token=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **string** | Token content | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteTokenById

Delete API Token by Id

Delete the API token by id.

### Example

```bash
freechat deleteTokenById id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Token id | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## disableToken

Disable API Token

Disable an API Token, the token is not deleted.

### Example

```bash
freechat disableToken token=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **string** | Token content | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## disableTokenById

Disable API Token by Id

Disable the API token by id.

### Example

```bash
freechat disableTokenById id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Token id | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getTokenById

Get API Token by Id

Get the API token by id.

### Example

```bash
freechat getTokenById id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Token id | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getUserBasic

Get User Basic Information

Return user basic information, including: username, nickname, avatar link.

### Example

```bash
freechat getUserBasic username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

[**UserBasicInfoDTO**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getUserBasic1

Get User Basic Information

Return user basic information, including: username, nickname, avatar link.

### Example

```bash
freechat getUserBasic1
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**UserBasicInfoDTO**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getUserDetails

Get User Details

Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).

### Example

```bash
freechat getUserDetails
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listTokens

List API Tokens

List currently valid tokens.

### Example

```bash
freechat listTokens
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**array[ApiTokenInfoDTO]**](ApiTokenInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateUserInfo

Update User Details

Update the detailed user information of the current account.

### Example

```bash
freechat updateUserInfo
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userDetailsDTO** | [**UserDetailsDTO**](UserDetailsDTO.md) | User information |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadUserPicture

Upload User Picture

Upload a picture of the user.

### Example

```bash
freechat uploadUserPicture
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **binary** | User picture | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

