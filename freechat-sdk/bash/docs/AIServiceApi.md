# AIServiceApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**addAiApiKey**](AIServiceApi.md#addAiApiKey) | **POST** /api/v2/ai/apikey | Add Model Provider Credential
[**deleteAiApiKey**](AIServiceApi.md#deleteAiApiKey) | **DELETE** /api/v2/ai/apikey/{id} | Delete Credential of Model Provider
[**disableAiApiKey**](AIServiceApi.md#disableAiApiKey) | **PUT** /api/v2/ai/apikey/disable/{id} | Disable Model Provider Credential
[**enableAiApiKey**](AIServiceApi.md#enableAiApiKey) | **PUT** /api/v2/ai/apikey/enable/{id} | Enable Model Provider Credential
[**getAiApiKey**](AIServiceApi.md#getAiApiKey) | **GET** /api/v2/ai/apikey/{id} | Get credential of Model Provider
[**listAiApiKeys**](AIServiceApi.md#listAiApiKeys) | **GET** /api/v2/ai/apikeys/{provider} | List Credentials of Model Provider



## addAiApiKey

Add Model Provider Credential

Add a credential for the model service.

### Example

```bash
freechat addAiApiKey
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aiApiKeyCreateDTO** | [**AiApiKeyCreateDTO**](AiApiKeyCreateDTO.md) | Model call credential information |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteAiApiKey

Delete Credential of Model Provider

Delete the credential information of the model provider.

### Example

```bash
freechat deleteAiApiKey id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Credential identifier | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## disableAiApiKey

Disable Model Provider Credential

Disable the credential information of the model provider.

### Example

```bash
freechat disableAiApiKey id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Credential identifier | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## enableAiApiKey

Enable Model Provider Credential

Enable the credential information of the model provider.

### Example

```bash
freechat enableAiApiKey id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Credential identifier | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getAiApiKey

Get credential of Model Provider

Get the credential information of the model provider.

### Example

```bash
freechat getAiApiKey id=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **integer** | Credential identifier | [default to null]

### Return type

[**AiApiKeyInfoDTO**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAiApiKeys

List Credentials of Model Provider

List all credential information of the model provider.

### Example

```bash
freechat listAiApiKeys provider=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **provider** | **string** | Model provider | [default to null]

### Return type

[**array[AiApiKeyInfoDTO]**](AiApiKeyInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

