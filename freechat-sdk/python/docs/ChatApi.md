# freechat_sdk.ChatApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**clear_memory**](ChatApi.md#clear_memory) | **DELETE** /api/v1/chat/memory/{chatId} | Clear Memory
[**delete_chat**](ChatApi.md#delete_chat) | **DELETE** /api/v1/chat/{chatId} | Delete Chat Session
[**list_chats**](ChatApi.md#list_chats) | **GET** /api/v1/chat | List Chats
[**list_messages**](ChatApi.md#list_messages) | **GET** /api/v1/chat/messages/{chatId} | List Chat Messages
[**list_messages1**](ChatApi.md#list_messages1) | **GET** /api/v1/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**list_messages2**](ChatApi.md#list_messages2) | **GET** /api/v1/chat/messages/{chatId}/{limit} | List Chat Messages
[**send_message**](ChatApi.md#send_message) | **POST** /api/v1/chat/send/{chatId} | Send Chat Message
[**start_chat**](ChatApi.md#start_chat) | **POST** /api/v1/chat | Start Chat Session
[**stream_send_message**](ChatApi.md#stream_send_message) | **POST** /api/v1/chat/send/stream/{chatId} | Send Chat Message by Streaming Back


# **clear_memory**
> List[ChatMessageRecordDTO] clear_memory(chat_id)

Clear Memory

Clear memory of the chat session.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier

    try:
        # Clear Memory
        api_response = api_instance.clear_memory(chat_id)
        print("The response of ChatApi->clear_memory:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->clear_memory: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 

### Return type

[**List[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

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

# **delete_chat**
> bool delete_chat(chat_id)

Delete Chat Session

Delete the chat session.

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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier

    try:
        # Delete Chat Session
        api_response = api_instance.delete_chat(chat_id)
        print("The response of ChatApi->delete_chat:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->delete_chat: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 

### Return type

**bool**

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

# **list_chats**
> List[ChatSessionDTO] list_chats()

List Chats

List chats of current user.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_session_dto import ChatSessionDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)

    try:
        # List Chats
        api_response = api_instance.list_chats()
        print("The response of ChatApi->list_chats:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->list_chats: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ChatSessionDTO]**](ChatSessionDTO.md)

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

# **list_messages**
> List[ChatMessageRecordDTO] list_messages(chat_id)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier

    try:
        # List Chat Messages
        api_response = api_instance.list_messages(chat_id)
        print("The response of ChatApi->list_messages:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->list_messages: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 

### Return type

[**List[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

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

# **list_messages1**
> List[ChatMessageRecordDTO] list_messages1(chat_id, limit, offset)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier
    limit = 56 # int | Messages limit
    offset = 56 # int | Messages offset (from new to old)

    try:
        # List Chat Messages
        api_response = api_instance.list_messages1(chat_id, limit, offset)
        print("The response of ChatApi->list_messages1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->list_messages1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **limit** | **int**| Messages limit | 
 **offset** | **int**| Messages offset (from new to old) | 

### Return type

[**List[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

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

# **list_messages2**
> List[ChatMessageRecordDTO] list_messages2(chat_id, limit)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier
    limit = 56 # int | Messages limit

    try:
        # List Chat Messages
        api_response = api_instance.list_messages2(chat_id, limit)
        print("The response of ChatApi->list_messages2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->list_messages2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **limit** | **int**| Messages limit | 

### Return type

[**List[ChatMessageRecordDTO]**](ChatMessageRecordDTO.md)

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

# **send_message**
> LlmResultDTO send_message(chat_id, chat_message_dto)

Send Chat Message

Send a chat message to character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.models.llm_result_dto import LlmResultDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier
    chat_message_dto = freechat_sdk.ChatMessageDTO() # ChatMessageDTO | Chat message

    try:
        # Send Chat Message
        api_response = api_instance.send_message(chat_id, chat_message_dto)
        print("The response of ChatApi->send_message:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->send_message: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **chat_message_dto** | [**ChatMessageDTO**](ChatMessageDTO.md)| Chat message | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **start_chat**
> str start_chat(chat_create_dto)

Start Chat Session

Start a chat session.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_create_dto import ChatCreateDTO
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_create_dto = freechat_sdk.ChatCreateDTO() # ChatCreateDTO | Parameters for starting a chat session

    try:
        # Start Chat Session
        api_response = api_instance.start_chat(chat_create_dto)
        print("The response of ChatApi->start_chat:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->start_chat: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_create_dto** | [**ChatCreateDTO**](ChatCreateDTO.md)| Parameters for starting a chat session | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **stream_send_message**
> SseEmitter stream_send_message(chat_id, chat_message_dto)

Send Chat Message by Streaming Back

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.models.sse_emitter import SseEmitter
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
    api_instance = freechat_sdk.ChatApi(api_client)
    chat_id = 'chat_id_example' # str | Chat session identifier
    chat_message_dto = freechat_sdk.ChatMessageDTO() # ChatMessageDTO | Chat message

    try:
        # Send Chat Message by Streaming Back
        api_response = api_instance.stream_send_message(chat_id, chat_message_dto)
        print("The response of ChatApi->stream_send_message:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ChatApi->stream_send_message: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **chat_message_dto** | [**ChatMessageDTO**](ChatMessageDTO.md)| Chat message | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

