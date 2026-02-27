# ChatApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**clearMemory**](ChatApi.md#clearMemory) | **DELETE** /api/v2/chat/memory/{chatId} | Clear Memory
[**deleteChat**](ChatApi.md#deleteChat) | **DELETE** /api/v2/chat/{chatId} | Delete Chat Session
[**getDefaultChatId**](ChatApi.md#getDefaultChatId) | **GET** /api/v2/chat/{characterUid} | Get Default Chat
[**getMemoryUsage**](ChatApi.md#getMemoryUsage) | **GET** /api/v2/chat/memory/usage/{chatId} | Get Memory Usage
[**listChats**](ChatApi.md#listChats) | **GET** /api/v2/chat | List Chats
[**listDebugMessages**](ChatApi.md#listDebugMessages) | **GET** /api/v2/chat/messages/debug/{chatId}/{limit} | List Chat Debug Messages
[**listDebugMessages1**](ChatApi.md#listDebugMessages1) | **GET** /api/v2/chat/messages/debug/{chatId}/{limit}/{offset} | List Chat Debug Messages
[**listDebugMessages2**](ChatApi.md#listDebugMessages2) | **GET** /api/v2/chat/messages/debug/{chatId} | List Chat Debug Messages
[**listMessages**](ChatApi.md#listMessages) | **GET** /api/v2/chat/messages/{chatId}/{limit} | List Chat Messages
[**listMessages1**](ChatApi.md#listMessages1) | **GET** /api/v2/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**listMessages2**](ChatApi.md#listMessages2) | **GET** /api/v2/chat/messages/{chatId} | List Chat Messages
[**rollbackMessages**](ChatApi.md#rollbackMessages) | **POST** /api/v2/chat/messages/rollback/{chatId}/{count} | Rollback Chat Messages
[**sendAssistant**](ChatApi.md#sendAssistant) | **GET** /api/v2/chat/send/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message
[**sendMessage**](ChatApi.md#sendMessage) | **POST** /api/v2/chat/send/{chatId} | Send Chat Message
[**startChat**](ChatApi.md#startChat) | **POST** /api/v2/chat | Start Chat Session
[**streamSendAssistant**](ChatApi.md#streamSendAssistant) | **GET** /api/v2/chat/send/stream/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message by Streaming Back
[**streamSendMessage**](ChatApi.md#streamSendMessage) | **POST** /api/v2/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
[**updateChat**](ChatApi.md#updateChat) | **PUT** /api/v2/chat/{chatId} | Update Chat Session



## clearMemory

Clear Memory

Clear memory of the chat session.

### Example

```bash
freechat-cli clearMemory chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteChat

Delete Chat Session

Delete the chat session.

### Example

```bash
freechat-cli deleteChat chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getDefaultChatId

Get Default Chat

Get default chat id of current user and the character.

### Example

```bash
freechat-cli getDefaultChatId characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character uid | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getMemoryUsage

Get Memory Usage

Get memory usage of a chat.

### Example

```bash
freechat-cli getMemoryUsage chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]

### Return type

[**MemoryUsageDTO**](MemoryUsageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listChats

List Chats

List chats of current user.

### Example

```bash
freechat-cli listChats
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**array[ChatSessionDTO]**](ChatSessionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listDebugMessages

List Chat Debug Messages

List debug messages of a chat.

### Example

```bash
freechat-cli listDebugMessages chatId=value limit=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **limit** | **integer** | Messages limit | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listDebugMessages1

List Chat Debug Messages

List debug messages of a chat.

### Example

```bash
freechat-cli listDebugMessages1 chatId=value limit=value offset=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **limit** | **integer** | Messages limit | [default to null]
 **offset** | **integer** | Messages offset (from new to old) | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listDebugMessages2

List Chat Debug Messages

List debug messages of a chat.

### Example

```bash
freechat-cli listDebugMessages2 chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listMessages

List Chat Messages

List messages of a chat.

### Example

```bash
freechat-cli listMessages chatId=value limit=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **limit** | **integer** | Messages limit | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listMessages1

List Chat Messages

List messages of a chat.

### Example

```bash
freechat-cli listMessages1 chatId=value limit=value offset=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **limit** | **integer** | Messages limit | [default to null]
 **offset** | **integer** | Messages offset (from new to old) | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listMessages2

List Chat Messages

List messages of a chat.

### Example

```bash
freechat-cli listMessages2 chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]

### Return type

[**array[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## rollbackMessages

Rollback Chat Messages

Rollback messages of a chat.

### Example

```bash
freechat-cli rollbackMessages chatId=value count=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **count** | **integer** | Message count to be rolled back | [default to null]

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## sendAssistant

Send Assistant for Chat Message

Send a message to assistant for a new chat message.

### Example

```bash
freechat-cli sendAssistant chatId=value assistantUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **assistantUid** | **string** | Assistant uid | [default to null]

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## sendMessage

Send Chat Message

Send a chat message to character.

### Example

```bash
freechat-cli sendMessage chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **chatMessageDTO** | [**ChatMessageDTO**](ChatMessageDTO.md) | Chat message |

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## startChat

Start Chat Session

Start a chat session.

### Example

```bash
freechat-cli startChat
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatCreateDTO** | [**ChatCreateDTO**](ChatCreateDTO.md) | Parameters for starting a chat session |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## streamSendAssistant

Send Assistant for Chat Message by Streaming Back

Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.

### Example

```bash
freechat-cli streamSendAssistant chatId=value assistantUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **assistantUid** | **string** | Assistant uid | [default to null]

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/event-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## streamSendMessage

Send Chat Message by Streaming Back

Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.

### Example

```bash
freechat-cli streamSendMessage chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **chatMessageDTO** | [**ChatMessageDTO**](ChatMessageDTO.md) | Chat message |

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/event-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateChat

Update Chat Session

Update the chat session.

### Example

```bash
freechat-cli updateChat chatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | Chat session identifier | [default to null]
 **chatUpdateDTO** | [**ChatUpdateDTO**](ChatUpdateDTO.md) | The chat session information to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

