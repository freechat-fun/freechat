# freechat_sdk.TTSServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**list_tts_builtin_speakers**](TTSServiceApi.md#list_tts_builtin_speakers) | **GET** /api/v2/public/tts/builtin/speakers | List Builtin Speakers
[**play_sample**](TTSServiceApi.md#play_sample) | **GET** /api/v2/public/tts/play/sample/{speakerType}/{speaker} | Play Sample Audio
[**speak_message**](TTSServiceApi.md#speak_message) | **GET** /api/v2/tts/speak/{messageId} | Speak Message


# **list_tts_builtin_speakers**
> List[str] list_tts_builtin_speakers()

List Builtin Speakers

Return builtin TTS speakers.

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
    api_instance = freechat_sdk.TTSServiceApi(api_client)

    try:
        # List Builtin Speakers
        api_response = api_instance.list_tts_builtin_speakers()
        print("The response of TTSServiceApi->list_tts_builtin_speakers:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TTSServiceApi->list_tts_builtin_speakers: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

**List[str]**

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

# **play_sample**
> object play_sample(speaker_type, speaker)

Play Sample Audio

Play TTS sample audio of the builtin/custom speaker.

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
    api_instance = freechat_sdk.TTSServiceApi(api_client)
    speaker_type = 'speaker_type_example' # str | The speaker type
    speaker = 'speaker_example' # str | The speaker

    try:
        # Play Sample Audio
        api_response = api_instance.play_sample(speaker_type, speaker)
        print("The response of TTSServiceApi->play_sample:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TTSServiceApi->play_sample: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **speaker_type** | **str**| The speaker type | 
 **speaker** | **str**| The speaker | 

### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **speak_message**
> object speak_message(message_id)

Speak Message

Read out the message.

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
    api_instance = freechat_sdk.TTSServiceApi(api_client)
    message_id = 56 # int | The message id

    try:
        # Speak Message
        api_response = api_instance.speak_message(message_id)
        print("The response of TTSServiceApi->speak_message:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling TTSServiceApi->speak_message: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **message_id** | **int**| The message id | 

### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

