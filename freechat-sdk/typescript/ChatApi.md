# .ChatApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**clearMemory**](ChatApi.md#clearMemory) | **DELETE** /api/v2/chat/memory/{chatId} | Clear Memory
[**deleteChat**](ChatApi.md#deleteChat) | **DELETE** /api/v2/chat/{chatId} | Delete Chat Session
[**getDefaultChatId**](ChatApi.md#getDefaultChatId) | **GET** /api/v2/chat/{characterId} | Get Default Chat
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


# **clearMemory**
> Array<ChatMessageRecordDTO> clearMemory()

Clear memory of the chat session.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiClearMemoryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiClearMemoryRequest = {
    // Chat session identifier
  chatId: "chatId_example",
};

const data = await apiInstance.clearMemory(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **deleteChat**
> boolean deleteChat()

Delete the chat session.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiDeleteChatRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiDeleteChatRequest = {
    // Chat session identifier
  chatId: "chatId_example",
};

const data = await apiInstance.deleteChat(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


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

# **getDefaultChatId**
> string getDefaultChatId()

Get default chat id of current user and the character.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiGetDefaultChatIdRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiGetDefaultChatIdRequest = {
    // Character identifier
  characterId: 1,
};

const data = await apiInstance.getDefaultChatId(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined


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

# **getMemoryUsage**
> MemoryUsageDTO getMemoryUsage()

Get memory usage of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiGetMemoryUsageRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiGetMemoryUsageRequest = {
    // Chat session identifier
  chatId: "chatId_example",
};

const data = await apiInstance.getMemoryUsage(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**MemoryUsageDTO**

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

# **listChats**
> Array<ChatSessionDTO> listChats()

List chats of current user.

### Example


```typescript
import { createConfiguration, ChatApi } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request = {};

const data = await apiInstance.listChats(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<ChatSessionDTO>**

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

# **listDebugMessages**
> Array<ChatMessageRecordDTO> listDebugMessages()

List debug messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListDebugMessagesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListDebugMessagesRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Messages limit
  limit: 1,
};

const data = await apiInstance.listDebugMessages(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **listDebugMessages1**
> Array<ChatMessageRecordDTO> listDebugMessages1()

List debug messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListDebugMessages1Request } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListDebugMessages1Request = {
    // Chat session identifier
  chatId: "chatId_example",
    // Messages limit
  limit: 1,
    // Messages offset (from new to old)
  offset: 1,
};

const data = await apiInstance.listDebugMessages1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined
 **offset** | [**number**] | Messages offset (from new to old) | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **listDebugMessages2**
> Array<ChatMessageRecordDTO> listDebugMessages2()

List debug messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListDebugMessages2Request } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListDebugMessages2Request = {
    // Chat session identifier
  chatId: "chatId_example",
};

const data = await apiInstance.listDebugMessages2(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **listMessages**
> Array<ChatMessageRecordDTO> listMessages()

List messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListMessagesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListMessagesRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Messages limit
  limit: 1,
};

const data = await apiInstance.listMessages(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **listMessages1**
> Array<ChatMessageRecordDTO> listMessages1()

List messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListMessages1Request } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListMessages1Request = {
    // Chat session identifier
  chatId: "chatId_example",
    // Messages limit
  limit: 1,
    // Messages offset (from new to old)
  offset: 1,
};

const data = await apiInstance.listMessages1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined
 **offset** | [**number**] | Messages offset (from new to old) | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **listMessages2**
> Array<ChatMessageRecordDTO> listMessages2()

List messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiListMessages2Request } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiListMessages2Request = {
    // Chat session identifier
  chatId: "chatId_example",
};

const data = await apiInstance.listMessages2(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**Array<ChatMessageRecordDTO>**

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

# **rollbackMessages**
> Array<number> rollbackMessages()

Rollback messages of a chat.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiRollbackMessagesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiRollbackMessagesRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Message count to be rolled back
  count: 1,
};

const data = await apiInstance.rollbackMessages(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **count** | [**number**] | Message count to be rolled back | defaults to undefined


### Return type

**Array<number>**

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

# **sendAssistant**
> LlmResultDTO sendAssistant()

Send a message to assistant for a new chat message.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiSendAssistantRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiSendAssistantRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Assistant uid
  assistantUid: "assistantUid_example",
};

const data = await apiInstance.sendAssistant(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **assistantUid** | [**string**] | Assistant uid | defaults to undefined


### Return type

**LlmResultDTO**

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

# **sendMessage**
> LlmResultDTO sendMessage(chatMessageDTO)

Send a chat message to character.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiSendMessageRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiSendMessageRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Chat message
  chatMessageDTO: {
    role: "role_example",
    name: "name_example",
    contents: [
      {
        type: "type_example",
        content: "content_example",
      },
    ],
    toolCalls: [
      {
        id: "id_example",
        name: "name_example",
        arguments: "arguments_example",
      },
    ],
    context: "context_example",
    contentText: "contentText_example",
  },
};

const data = await apiInstance.sendMessage(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatMessageDTO** | **ChatMessageDTO**| Chat message |
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**LlmResultDTO**

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

# **startChat**
> string startChat(chatCreateDTO)

Start a chat session.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiStartChatRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiStartChatRequest = {
    // Parameters for starting a chat session
  chatCreateDTO: {
    userNickname: "userNickname_example",
    userProfile: "userProfile_example",
    characterNickname: "characterNickname_example",
    about: "about_example",
    characterUid: "characterUid_example",
    backendId: "backendId_example",
    apiKeyName: "apiKeyName_example",
    apiKeyValue: "apiKeyValue_example",
    ext: "ext_example",
  },
};

const data = await apiInstance.startChat(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatCreateDTO** | **ChatCreateDTO**| Parameters for starting a chat session |


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **streamSendAssistant**
> SseEmitter streamSendAssistant()

Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiStreamSendAssistantRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiStreamSendAssistantRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Assistant uid
  assistantUid: "assistantUid_example",
};

const data = await apiInstance.streamSendAssistant(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **assistantUid** | [**string**] | Assistant uid | defaults to undefined


### Return type

**SseEmitter**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/event-stream


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **streamSendMessage**
> SseEmitter streamSendMessage(chatMessageDTO)

Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiStreamSendMessageRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiStreamSendMessageRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // Chat message
  chatMessageDTO: {
    role: "role_example",
    name: "name_example",
    contents: [
      {
        type: "type_example",
        content: "content_example",
      },
    ],
    toolCalls: [
      {
        id: "id_example",
        name: "name_example",
        arguments: "arguments_example",
      },
    ],
    context: "context_example",
    contentText: "contentText_example",
  },
};

const data = await apiInstance.streamSendMessage(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatMessageDTO** | **ChatMessageDTO**| Chat message |
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**SseEmitter**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **updateChat**
> boolean updateChat(chatUpdateDTO)

Update the chat session.

### Example


```typescript
import { createConfiguration, ChatApi } from '';
import type { ChatApiUpdateChatRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ChatApi(configuration);

const request: ChatApiUpdateChatRequest = {
    // Chat session identifier
  chatId: "chatId_example",
    // The chat session information to be updated
  chatUpdateDTO: {
    userNickname: "userNickname_example",
    userProfile: "userProfile_example",
    characterNickname: "characterNickname_example",
    about: "about_example",
    characterUid: "characterUid_example",
    backendId: "backendId_example",
    apiKeyName: "apiKeyName_example",
    apiKeyValue: "apiKeyValue_example",
    ext: "ext_example",
    gmtRead: new Date('1970-01-01T00:00:00.00Z'),
  },
};

const data = await apiInstance.updateChat(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatUpdateDTO** | **ChatUpdateDTO**| The chat session information to be updated |
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


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


