# PromptTaskApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**createPromptTask**](PromptTaskApi.md#createPromptTask) | **POST** /api/v2/prompt/task | Create Prompt Task
[**deletePromptTask**](PromptTaskApi.md#deletePromptTask) | **DELETE** /api/v2/prompt/task/{promptTaskId} | Delete Prompt Task
[**getPromptTask**](PromptTaskApi.md#getPromptTask) | **GET** /api/v2/prompt/task/{promptTaskId} | Get Prompt Task
[**updatePromptTask**](PromptTaskApi.md#updatePromptTask) | **PUT** /api/v2/prompt/task/{promptTaskId} | Update Prompt Task



## createPromptTask

Create Prompt Task

Create a prompt task.

### Example

```bash
freechat-cli createPromptTask
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md) | The prompt task to be added |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePromptTask

Delete Prompt Task

Delete a prompt task.

### Example

```bash
freechat-cli deletePromptTask promptTaskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **string** | The promptTaskId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getPromptTask

Get Prompt Task

Get the prompt task details.

### Example

```bash
freechat-cli getPromptTask promptTaskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **string** | The promptTaskId to be queried | [default to null]

### Return type

[**PromptTaskDetailsDTO**](PromptTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updatePromptTask

Update Prompt Task

Update a prompt task.

### Example

```bash
freechat-cli updatePromptTask promptTaskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTaskId** | **string** | The promptTaskId to be updated | [default to null]
 **promptTaskDTO** | [**PromptTaskDTO**](PromptTaskDTO.md) | The prompt task info to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

