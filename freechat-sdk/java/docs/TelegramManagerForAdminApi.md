# TelegramManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**findTelegramChat**](TelegramManagerForAdminApi.md#findTelegramChat) | **GET** /api/v2/admin/chat/tg/{backendId}/{tgChatId} | Find Telegram Chat |
| [**listTelegramMessages**](TelegramManagerForAdminApi.md#listTelegramMessages) | **GET** /api/v2/admin/chat/tg/messages/{chatId} | List Telegram Messages |


<a id="findTelegramChat"></a>
# **findTelegramChat**
> String findTelegramChat(backendId, tgChatId)

Find Telegram Chat

Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TelegramManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TelegramManagerForAdminApi apiInstance = new TelegramManagerForAdminApi(defaultClient);
    String backendId = "backendId_example"; // String | Character backend identifier
    Long tgChatId = 56L; // Long | Telegram chat id
    try {
      String result = apiInstance.findTelegramChat(backendId, tgChatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TelegramManagerForAdminApi#findTelegramChat");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **backendId** | **String**| Character backend identifier | |
| **tgChatId** | **Long**| Telegram chat id | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listTelegramMessages"></a>
# **listTelegramMessages**
> List&lt;TgMessageDTO&gt; listTelegramMessages(chatId, limit, offset)

List Telegram Messages

List Telegram messages recorded against the given tg_chat.chat_id, newest first.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TelegramManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TelegramManagerForAdminApi apiInstance = new TelegramManagerForAdminApi(defaultClient);
    String chatId = "chatId_example"; // String | tg_chat.chat_id
    Integer limit = 56; // Integer | Max rows to return (default 100)
    Integer offset = 56; // Integer | Row offset (default 0)
    try {
      List<TgMessageDTO> result = apiInstance.listTelegramMessages(chatId, limit, offset);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TelegramManagerForAdminApi#listTelegramMessages");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **chatId** | **String**| tg_chat.chat_id | |
| **limit** | **Integer**| Max rows to return (default 100) | [optional] |
| **offset** | **Integer**| Row offset (default 0) | [optional] |

### Return type

[**List&lt;TgMessageDTO&gt;**](TgMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

