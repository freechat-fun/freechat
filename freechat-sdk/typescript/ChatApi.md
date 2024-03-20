# .ChatApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**clearMemory**](ChatApi.md#clearMemory) | **DELETE** /api/v1/chat/memory/{chatId} | Clear Memory
[**deleteChat**](ChatApi.md#deleteChat) | **DELETE** /api/v1/chat/{chatId} | Delete Chat Session
[**getDefaultChatId**](ChatApi.md#getDefaultChatId) | **GET** /api/v1/chat/{characterId} | Get Default Chat
[**listChats**](ChatApi.md#listChats) | **GET** /api/v1/chat | List Chats
[**listMessages**](ChatApi.md#listMessages) | **GET** /api/v1/chat/messages/{chatId} | List Chat Messages
[**listMessages1**](ChatApi.md#listMessages1) | **GET** /api/v1/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**listMessages2**](ChatApi.md#listMessages2) | **GET** /api/v1/chat/messages/{chatId}/{limit} | List Chat Messages
[**rollbackMessages**](ChatApi.md#rollbackMessages) | **POST** /api/v1/chat/messages/rollback/{chatId}/{count} | Rollback Chat Messages
[**sendMessage**](ChatApi.md#sendMessage) | **POST** /api/v1/chat/send/{chatId} | Send Chat Message
[**startChat**](ChatApi.md#startChat) | **POST** /api/v1/chat | Start Chat Session
[**streamSendMessage**](ChatApi.md#streamSendMessage) | **POST** /api/v1/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
[**updateChat**](ChatApi.md#updateChat) | **PUT** /api/v1/chat/{chatId} | Update Chat Session


# **clearMemory**
> Array<ChatMessageRecordDTO> clearMemory()

Clear memory of the chat session.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiClearMemoryRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
};

apiInstance.clearMemory(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiDeleteChatRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
};

apiInstance.deleteChat(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiGetDefaultChatIdRequest = {
  // number | Character identifier
  characterId: 1,
};

apiInstance.getDefaultChatId(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **listChats**
> Array<ChatSessionDTO> listChats()

List chats of current user.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:any = {};

apiInstance.listChats(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **listMessages**
> Array<ChatMessageRecordDTO> listMessages()

List messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiListMessagesRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
};

apiInstance.listMessages(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **listMessages1**
> Array<ChatMessageRecordDTO> listMessages1()

List messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiListMessages1Request = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // number | Messages limit
  limit: 1,
  // number | Messages offset (from new to old)
  offset: 1,
};

apiInstance.listMessages1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiListMessages2Request = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // number | Messages limit
  limit: 1,
};

apiInstance.listMessages2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **rollbackMessages**
> Array<number> rollbackMessages()

Rollback messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiRollbackMessagesRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // number | Message count to be rolled back
  count: 1,
};

apiInstance.rollbackMessages(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **sendMessage**
> LlmResultDTO sendMessage(chatMessageDTO)

Send a chat message to character.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiSendMessageRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // ChatMessageDTO | Chat message
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

apiInstance.sendMessage(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiStartChatRequest = {
  // ChatCreateDTO | Parameters for starting a chat session
  chatCreateDTO: {
    userNickname: "userNickname_example",
    userProfile: "userProfile_example",
    characterNickname: "characterNickname_example",
    about: "about_example",
    characterId: 1,
    backendId: "backendId_example",
    ext: "ext_example",
  },
};

apiInstance.startChat(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **streamSendMessage**
> SseEmitter streamSendMessage(chatMessageDTO)

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiStreamSendMessageRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // ChatMessageDTO | Chat message
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

apiInstance.streamSendMessage(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .ChatApi(configuration);

let body:.ChatApiUpdateChatRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // ChatUpdateDTO | The chat session information to be updated
  chatUpdateDTO: {
    userNickname: "userNickname_example",
    userProfile: "userProfile_example",
    characterNickname: "characterNickname_example",
    about: "about_example",
    characterId: 1,
    backendId: "backendId_example",
    ext: "ext_example",
    gmtRead: new Date('1970-01-01T00:00:00.00Z'),
  },
};

apiInstance.updateChat(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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


