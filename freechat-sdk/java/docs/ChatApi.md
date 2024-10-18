# ChatApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**clearMemory**](ChatApi.md#clearMemory) | **DELETE** /api/v1/chat/memory/{chatId} | Clear Memory |
| [**deleteChat**](ChatApi.md#deleteChat) | **DELETE** /api/v1/chat/{chatId} | Delete Chat Session |
| [**getDefaultChatId**](ChatApi.md#getDefaultChatId) | **GET** /api/v1/chat/{characterId} | Get Default Chat |
| [**getMemoryUsage**](ChatApi.md#getMemoryUsage) | **GET** /api/v1/chat/memory/usage/{chatId} | Get Memory Usage |
| [**listChats**](ChatApi.md#listChats) | **GET** /api/v1/chat | List Chats |
| [**listDebugMessages**](ChatApi.md#listDebugMessages) | **GET** /api/v1/chat/messages/debug/{chatId}/{limit} | List Chat Debug Messages |
| [**listDebugMessages1**](ChatApi.md#listDebugMessages1) | **GET** /api/v1/chat/messages/debug/{chatId}/{limit}/{offset} | List Chat Debug Messages |
| [**listDebugMessages2**](ChatApi.md#listDebugMessages2) | **GET** /api/v1/chat/messages/debug/{chatId} | List Chat Debug Messages |
| [**listMessages**](ChatApi.md#listMessages) | **GET** /api/v1/chat/messages/{chatId} | List Chat Messages |
| [**listMessages1**](ChatApi.md#listMessages1) | **GET** /api/v1/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages |
| [**listMessages2**](ChatApi.md#listMessages2) | **GET** /api/v1/chat/messages/{chatId}/{limit} | List Chat Messages |
| [**rollbackMessages**](ChatApi.md#rollbackMessages) | **POST** /api/v1/chat/messages/rollback/{chatId}/{count} | Rollback Chat Messages |
| [**sendAssistant**](ChatApi.md#sendAssistant) | **GET** /api/v1/chat/send/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message |
| [**sendMessage**](ChatApi.md#sendMessage) | **POST** /api/v1/chat/send/{chatId} | Send Chat Message |
| [**startChat**](ChatApi.md#startChat) | **POST** /api/v1/chat | Start Chat Session |
| [**streamSendAssistant**](ChatApi.md#streamSendAssistant) | **GET** /api/v1/chat/send/stream/assistant/{chatId}/{assistantUid} | Send Assistant for Chat Message by Streaming Back |
| [**streamSendMessage**](ChatApi.md#streamSendMessage) | **POST** /api/v1/chat/send/stream/{chatId} | Send Chat Message by Streaming Back |
| [**updateChat**](ChatApi.md#updateChat) | **PUT** /api/v1/chat/{chatId} | Update Chat Session |


<a id="clearMemory"></a>
# **clearMemory**
> List&lt;ChatMessageRecordDTO&gt; clearMemory(chatId)

Clear Memory

Clear memory of the chat session.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      List<ChatMessageRecordDTO> result = apiInstance.clearMemory(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#clearMemory");
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
| **chatId** | **String**| Chat session identifier | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteChat"></a>
# **deleteChat**
> Boolean deleteChat(chatId)

Delete Chat Session

Delete the chat session.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      Boolean result = apiInstance.deleteChat(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#deleteChat");
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
| **chatId** | **String**| Chat session identifier | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getDefaultChatId"></a>
# **getDefaultChatId**
> String getDefaultChatId(characterId)

Get Default Chat

Get default chat id of current user and the character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    try {
      String result = apiInstance.getDefaultChatId(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#getDefaultChatId");
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
| **characterId** | **Long**| Character identifier | |

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

<a id="getMemoryUsage"></a>
# **getMemoryUsage**
> MemoryUsageDTO getMemoryUsage(chatId)

Get Memory Usage

Get memory usage of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      MemoryUsageDTO result = apiInstance.getMemoryUsage(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#getMemoryUsage");
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
| **chatId** | **String**| Chat session identifier | |

### Return type

[**MemoryUsageDTO**](MemoryUsageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listChats"></a>
# **listChats**
> List&lt;ChatSessionDTO&gt; listChats()

List Chats

List chats of current user.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    try {
      List<ChatSessionDTO> result = apiInstance.listChats();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listChats");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;ChatSessionDTO&gt;**](ChatSessionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listDebugMessages"></a>
# **listDebugMessages**
> List&lt;ChatMessageRecordDTO&gt; listDebugMessages(chatId, limit)

List Chat Debug Messages

List debug messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listDebugMessages(chatId, limit);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listDebugMessages");
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
| **chatId** | **String**| Chat session identifier | |
| **limit** | **Integer**| Messages limit | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listDebugMessages1"></a>
# **listDebugMessages1**
> List&lt;ChatMessageRecordDTO&gt; listDebugMessages1(chatId, limit, offset)

List Chat Debug Messages

List debug messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    Integer offset = 56; // Integer | Messages offset (from new to old)
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listDebugMessages1(chatId, limit, offset);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listDebugMessages1");
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
| **chatId** | **String**| Chat session identifier | |
| **limit** | **Integer**| Messages limit | |
| **offset** | **Integer**| Messages offset (from new to old) | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listDebugMessages2"></a>
# **listDebugMessages2**
> List&lt;ChatMessageRecordDTO&gt; listDebugMessages2(chatId)

List Chat Debug Messages

List debug messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listDebugMessages2(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listDebugMessages2");
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
| **chatId** | **String**| Chat session identifier | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listMessages"></a>
# **listMessages**
> List&lt;ChatMessageRecordDTO&gt; listMessages(chatId)

List Chat Messages

List messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listMessages(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listMessages");
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
| **chatId** | **String**| Chat session identifier | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listMessages1"></a>
# **listMessages1**
> List&lt;ChatMessageRecordDTO&gt; listMessages1(chatId, limit, offset)

List Chat Messages

List messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    Integer offset = 56; // Integer | Messages offset (from new to old)
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listMessages1(chatId, limit, offset);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listMessages1");
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
| **chatId** | **String**| Chat session identifier | |
| **limit** | **Integer**| Messages limit | |
| **offset** | **Integer**| Messages offset (from new to old) | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listMessages2"></a>
# **listMessages2**
> List&lt;ChatMessageRecordDTO&gt; listMessages2(chatId, limit)

List Chat Messages

List messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    try {
      List<ChatMessageRecordDTO> result = apiInstance.listMessages2(chatId, limit);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#listMessages2");
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
| **chatId** | **String**| Chat session identifier | |
| **limit** | **Integer**| Messages limit | |

### Return type

[**List&lt;ChatMessageRecordDTO&gt;**](ChatMessageRecordDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="rollbackMessages"></a>
# **rollbackMessages**
> List&lt;Long&gt; rollbackMessages(chatId, count)

Rollback Chat Messages

Rollback messages of a chat.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer count = 56; // Integer | Message count to be rolled back
    try {
      List<Long> result = apiInstance.rollbackMessages(chatId, count);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#rollbackMessages");
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
| **chatId** | **String**| Chat session identifier | |
| **count** | **Integer**| Message count to be rolled back | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="sendAssistant"></a>
# **sendAssistant**
> LlmResultDTO sendAssistant(chatId, assistantUid)

Send Assistant for Chat Message

Send a message to assistant for a new chat message.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    String assistantUid = "assistantUid_example"; // String | Assistant uid
    try {
      LlmResultDTO result = apiInstance.sendAssistant(chatId, assistantUid);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#sendAssistant");
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
| **chatId** | **String**| Chat session identifier | |
| **assistantUid** | **String**| Assistant uid | |

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="sendMessage"></a>
# **sendMessage**
> LlmResultDTO sendMessage(chatId, chatMessageDTO)

Send Chat Message

Send a chat message to character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    ChatMessageDTO chatMessageDTO = new ChatMessageDTO(); // ChatMessageDTO | Chat message
    try {
      LlmResultDTO result = apiInstance.sendMessage(chatId, chatMessageDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#sendMessage");
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
| **chatId** | **String**| Chat session identifier | |
| **chatMessageDTO** | [**ChatMessageDTO**](ChatMessageDTO.md)| Chat message | |

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="startChat"></a>
# **startChat**
> String startChat(chatCreateDTO)

Start Chat Session

Start a chat session.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    ChatCreateDTO chatCreateDTO = new ChatCreateDTO(); // ChatCreateDTO | Parameters for starting a chat session
    try {
      String result = apiInstance.startChat(chatCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#startChat");
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
| **chatCreateDTO** | [**ChatCreateDTO**](ChatCreateDTO.md)| Parameters for starting a chat session | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="streamSendAssistant"></a>
# **streamSendAssistant**
> SseEmitter streamSendAssistant(chatId, assistantUid)

Send Assistant for Chat Message by Streaming Back

Refer to /api/v1/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    String assistantUid = "assistantUid_example"; // String | Assistant uid
    try {
      SseEmitter result = apiInstance.streamSendAssistant(chatId, assistantUid);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#streamSendAssistant");
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
| **chatId** | **String**| Chat session identifier | |
| **assistantUid** | **String**| Assistant uid | |

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/event-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="streamSendMessage"></a>
# **streamSendMessage**
> SseEmitter streamSendMessage(chatId, chatMessageDTO)

Send Chat Message by Streaming Back

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    ChatMessageDTO chatMessageDTO = new ChatMessageDTO(); // ChatMessageDTO | Chat message
    try {
      SseEmitter result = apiInstance.streamSendMessage(chatId, chatMessageDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#streamSendMessage");
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
| **chatId** | **String**| Chat session identifier | |
| **chatMessageDTO** | [**ChatMessageDTO**](ChatMessageDTO.md)| Chat message | |

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateChat"></a>
# **updateChat**
> Boolean updateChat(chatId, chatUpdateDTO)

Update Chat Session

Update the chat session.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ChatApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ChatApi apiInstance = new ChatApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    ChatUpdateDTO chatUpdateDTO = new ChatUpdateDTO(); // ChatUpdateDTO | The chat session information to be updated
    try {
      Boolean result = apiInstance.updateChat(chatId, chatUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ChatApi#updateChat");
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
| **chatId** | **String**| Chat session identifier | |
| **chatUpdateDTO** | [**ChatUpdateDTO**](ChatUpdateDTO.md)| The chat session information to be updated | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

