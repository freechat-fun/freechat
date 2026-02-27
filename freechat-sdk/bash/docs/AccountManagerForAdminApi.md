# AccountManagerForAdminApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**createTokenForUser**](AccountManagerForAdminApi.md#createTokenForUser) | **POST** /api/v2/admin/token/{username}/{duration} | Create API Token for User.
[**createUser**](AccountManagerForAdminApi.md#createUser) | **POST** /api/v2/admin/user | Create User
[**deleteTokenForUser**](AccountManagerForAdminApi.md#deleteTokenForUser) | **DELETE** /api/v2/admin/token/{token} | Delete API Token
[**deleteUser**](AccountManagerForAdminApi.md#deleteUser) | **DELETE** /api/v2/admin/user/{username} | Delete User
[**disableTokenForUser**](AccountManagerForAdminApi.md#disableTokenForUser) | **PUT** /api/v2/admin/token/{token} | Disable API Token
[**getDetailsOfUser**](AccountManagerForAdminApi.md#getDetailsOfUser) | **GET** /api/v2/admin/user/{username} | Get User Details
[**getUserByToken**](AccountManagerForAdminApi.md#getUserByToken) | **GET** /api/v2/admin/tokenBy/{token} | Get User by API Token
[**listAuthoritiesOfUser**](AccountManagerForAdminApi.md#listAuthoritiesOfUser) | **GET** /api/v2/admin/authority/{username} | List User Permissions
[**listTokensOfUser**](AccountManagerForAdminApi.md#listTokensOfUser) | **GET** /api/v2/admin/token/{username} | Get API Token of User
[**listUsers**](AccountManagerForAdminApi.md#listUsers) | **GET** /api/v2/admin/users/{pageSize}/{pageNum} | List User Information
[**listUsers1**](AccountManagerForAdminApi.md#listUsers1) | **GET** /api/v2/admin/users | List User Information
[**listUsers2**](AccountManagerForAdminApi.md#listUsers2) | **GET** /api/v2/admin/users/{pageSize} | List User Information
[**updateAuthoritiesOfUser**](AccountManagerForAdminApi.md#updateAuthoritiesOfUser) | **PUT** /api/v2/admin/authority/{username} | Update User Permissions
[**updateUser**](AccountManagerForAdminApi.md#updateUser) | **PUT** /api/v2/admin/user | Update User



## createTokenForUser

Create API Token for User.

Create an API Token for a specified user, valid for duration seconds.

### Example

```bash
freechat-cli createTokenForUser username=value duration=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]
 **duration** | **integer** | Validity period (seconds) | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createUser

Create User

Create user.

### Example

```bash
freechat-cli createUser
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md) | User information |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteTokenForUser

Delete API Token

Delete the specified API Token.

### Example

```bash
freechat-cli deleteTokenForUser token=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **string** | API Token | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteUser

Delete User

Delete user by username.

### Example

```bash
freechat-cli deleteUser username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## disableTokenForUser

Disable API Token

Disable the specified API Token.

### Example

```bash
freechat-cli disableTokenForUser token=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **string** | API Token | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getDetailsOfUser

Get User Details

Return detailed user information.

### Example

```bash
freechat-cli getDetailsOfUser username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getUserByToken

Get User by API Token

Get the detailed user information corresponding to the API Token.

### Example

```bash
freechat-cli getUserByToken token=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **string** | API Token | [default to null]

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAuthoritiesOfUser

List User Permissions

List the user's permissions.

### Example

```bash
freechat-cli listAuthoritiesOfUser username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

**Set[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listTokensOfUser

Get API Token of User

Get the list of API Tokens of the user.

### Example

```bash
freechat-cli listTokensOfUser username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

[**array[ApiTokenInfoDTO]**](ApiTokenInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listUsers

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```bash
freechat-cli listUsers pageSize=value pageNum=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **integer** | Maximum quantity | [default to null]
 **pageNum** | **integer** | Current page number | [default to null]

### Return type

[**array[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listUsers1

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```bash
freechat-cli listUsers1
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**array[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listUsers2

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

```bash
freechat-cli listUsers2 pageSize=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pageSize** | **integer** | Maximum quantity | [default to null]

### Return type

[**array[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateAuthoritiesOfUser

Update User Permissions

Update the user's permission list.

### Example

```bash
freechat-cli updateAuthoritiesOfUser username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]
 **requestBody** | [**Set[string]**](string.md) | Permission list |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateUser

Update User

Update user information.

### Example

```bash
freechat-cli updateUser
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md) | User information |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

