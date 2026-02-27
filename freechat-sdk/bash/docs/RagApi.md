# RagApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelRagTask**](RagApi.md#cancelRagTask) | **POST** /api/v2/rag/task/cancel/{taskId} | Cancel RAG Task
[**createRagTask**](RagApi.md#createRagTask) | **POST** /api/v2/rag/task/{characterUid} | Create RAG Task
[**deleteRagTask**](RagApi.md#deleteRagTask) | **DELETE** /api/v2/rag/task/{taskId} | Delete RAG Task
[**getRagTask**](RagApi.md#getRagTask) | **GET** /api/v2/rag/task/{taskId} | Get RAG Task
[**getRagTaskStatus**](RagApi.md#getRagTaskStatus) | **GET** /api/v2/rag/task/status/{taskId} | Get RAG Task Status
[**listRagTasks**](RagApi.md#listRagTasks) | **GET** /api/v2/rag/tasks/{characterUid} | List RAG Tasks
[**startRagTask**](RagApi.md#startRagTask) | **POST** /api/v2/rag/task/start/{taskId} | Start RAG Task
[**updateRagTask**](RagApi.md#updateRagTask) | **PUT** /api/v2/rag/task/{taskId} | Update RAG Task



## cancelRagTask

Cancel RAG Task

Cancel a RAG task.

### Example

```bash
freechat-cli cancelRagTask taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be canceled | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createRagTask

Create RAG Task

Create a RAG task.

### Example

```bash
freechat-cli createRagTask characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be added a RAG task | [default to null]
 **ragTaskDTO** | [**RagTaskDTO**](RagTaskDTO.md) | The RAG task to be added |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteRagTask

Delete RAG Task

Delete a RAG task.

### Example

```bash
freechat-cli deleteRagTask taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getRagTask

Get RAG Task

Get the RAG task details.

### Example

```bash
freechat-cli getRagTask taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be queried | [default to null]

### Return type

[**RagTaskDetailsDTO**](RagTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getRagTaskStatus

Get RAG Task Status

Get the RAG task execution status: pending | running | succeeded | failed | canceled.

### Example

```bash
freechat-cli getRagTaskStatus taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be queried status | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listRagTasks

List RAG Tasks

List the RAG tasks by characterId.

### Example

```bash
freechat-cli listRagTasks characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be queried | [default to null]

### Return type

[**array[RagTaskDetailsDTO]**](RagTaskDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## startRagTask

Start RAG Task

Start a RAG task.

### Example

```bash
freechat-cli startRagTask taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be started | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateRagTask

Update RAG Task

Update a RAG task.

### Example

```bash
freechat-cli updateRagTask taskId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **taskId** | **integer** | The taskId to be updated | [default to null]
 **ragTaskDTO** | [**RagTaskDTO**](RagTaskDTO.md) | The prompt task info to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

