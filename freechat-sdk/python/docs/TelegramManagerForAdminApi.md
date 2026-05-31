# freechat_sdk.TelegramManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**find_telegram_chat**](TelegramManagerForAdminApi.md#find_telegram_chat) | **GET** /api/v2/admin/chat/tg/{backendId}/{tgChatId} | Find Telegram Chat
[**list_telegram_messages**](TelegramManagerForAdminApi.md#list_telegram_messages) | **GET** /api/v2/admin/chat/tg/messages/{chatId} | List Telegram Messages


# **find_telegram_chat**
> str find_telegram_chat(backend_id, tg_chat_id)

Find Telegram Chat

Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.TelegramManagerForAdminApi(api_client)
    backend_id = 'backend_id_example' # str | Character backend identifier
    tg_chat_id = 56 # int | Telegram chat id

    try:
        # Find Telegram Chat
        api_response = api_instance.find_telegram_chat(backend_id, tg_chat_id)
        print("The response of TelegramManagerForAdminApi->find_telegram_chat:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TelegramManagerForAdminApi->find_telegram_chat: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **backend_id** | **str**| Character backend identifier | 
 **tg_chat_id** | **int**| Telegram chat id | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_telegram_messages**
> List[TgMessageDTO] list_telegram_messages(chat_id, limit=limit, offset=offset)

List Telegram Messages

List Telegram messages recorded against the given tg_chat.chat_id, newest first.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.tg_message_dto import TgMessageDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.TelegramManagerForAdminApi(api_client)
    chat_id = 'chat_id_example' # str | tg_chat.chat_id
    limit = 56 # int | Max rows to return (default 100) (optional)
    offset = 56 # int | Row offset (default 0) (optional)

    try:
        # List Telegram Messages
        api_response = api_instance.list_telegram_messages(chat_id, limit=limit, offset=offset)
        print("The response of TelegramManagerForAdminApi->list_telegram_messages:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TelegramManagerForAdminApi->list_telegram_messages: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| tg_chat.chat_id | 
 **limit** | **int**| Max rows to return (default 100) | [optional] 
 **offset** | **int**| Row offset (default 0) | [optional] 

### Return type

[**List[TgMessageDTO]**](TgMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

