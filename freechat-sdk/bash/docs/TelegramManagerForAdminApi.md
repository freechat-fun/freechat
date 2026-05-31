# TelegramManagerForAdminApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**findTelegramChat**](TelegramManagerForAdminApi.md#findTelegramChat) | **GET** /api/v2/admin/chat/tg/{backendId}/{tgChatId} | Find Telegram Chat
[**listTelegramMessages**](TelegramManagerForAdminApi.md#listTelegramMessages) | **GET** /api/v2/admin/chat/tg/messages/{chatId} | List Telegram Messages



## findTelegramChat

Find Telegram Chat

Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.

### Example

```bash
freechat-cli findTelegramChat backendId=value tgChatId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **backendId** | **string** | Character backend identifier | [default to null]
 **tgChatId** | **integer** | Telegram chat id | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listTelegramMessages

List Telegram Messages

List Telegram messages recorded against the given tg_chat.chat_id, newest first.

### Example

```bash
freechat-cli listTelegramMessages chatId=value  limit=value  offset=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **string** | tg_chat.chat_id | [default to null]
 **limit** | **integer** | Max rows to return (default 100) | [optional] [default to null]
 **offset** | **integer** | Row offset (default 0) | [optional] [default to null]

### Return type

[**array[TgMessageDTO]**](TgMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

