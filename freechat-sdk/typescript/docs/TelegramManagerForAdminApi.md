# .TelegramManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**findTelegramChat**](TelegramManagerForAdminApi.md#findTelegramChat) | **GET** /api/v2/admin/chat/tg/{backendId}/{tgChatId} | Find Telegram Chat
[**listTelegramMessages**](TelegramManagerForAdminApi.md#listTelegramMessages) | **GET** /api/v2/admin/chat/tg/messages/{chatId} | List Telegram Messages


# **findTelegramChat**
> string findTelegramChat()

Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.

### Example


```typescript
import { createConfiguration, TelegramManagerForAdminApi } from '';
import type { TelegramManagerForAdminApiFindTelegramChatRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TelegramManagerForAdminApi(configuration);

const request: TelegramManagerForAdminApiFindTelegramChatRequest = {
    // Character backend identifier
  backendId: "backendId_example",
    // Telegram chat id
  tgChatId: 1,
};

const data = await apiInstance.findTelegramChat(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **backendId** | [**string**] | Character backend identifier | defaults to undefined
 **tgChatId** | [**number**] | Telegram chat id | defaults to undefined


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

# **listTelegramMessages**
> Array<TgMessageDTO> listTelegramMessages()

List Telegram messages recorded against the given tg_chat.chat_id, newest first.

### Example


```typescript
import { createConfiguration, TelegramManagerForAdminApi } from '';
import type { TelegramManagerForAdminApiListTelegramMessagesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TelegramManagerForAdminApi(configuration);

const request: TelegramManagerForAdminApiListTelegramMessagesRequest = {
    // tg_chat.chat_id
  chatId: "chatId_example",
    // Max rows to return (default 100) (optional)
  limit: 1,
    // Row offset (default 0) (optional)
  offset: 1,
};

const data = await apiInstance.listTelegramMessages(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | tg_chat.chat_id | defaults to undefined
 **limit** | [**number**] | Max rows to return (default 100) | (optional) defaults to undefined
 **offset** | [**number**] | Row offset (default 0) | (optional) defaults to undefined


### Return type

**Array<TgMessageDTO>**

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


