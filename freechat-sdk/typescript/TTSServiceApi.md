# .TTSServiceApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**listTtsBuiltinSpeakers**](TTSServiceApi.md#listTtsBuiltinSpeakers) | **GET** /api/v2/public/tts/builtin/speakers | List Builtin Speakers
[**playSample**](TTSServiceApi.md#playSample) | **GET** /api/v2/public/tts/play/sample/{speakerType}/{speaker} | Play Sample Audio
[**speakMessage**](TTSServiceApi.md#speakMessage) | **GET** /api/v2/tts/speak/{messageId} | Speak Message


# **listTtsBuiltinSpeakers**
> Array<string> listTtsBuiltinSpeakers()

Return builtin TTS speakers.

### Example


```typescript
import { createConfiguration, TTSServiceApi } from '';

const configuration = createConfiguration();
const apiInstance = new TTSServiceApi(configuration);

const request = {};

const data = await apiInstance.listTtsBuiltinSpeakers(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<string>**

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

# **playSample**
> any playSample()

Play TTS sample audio of the builtin/custom speaker.

### Example


```typescript
import { createConfiguration, TTSServiceApi } from '';
import type { TTSServiceApiPlaySampleRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TTSServiceApi(configuration);

const request: TTSServiceApiPlaySampleRequest = {
    // The speaker type
  speakerType: "wav",
    // The speaker
  speaker: "speaker_example",
};

const data = await apiInstance.playSample(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **speakerType** | [**string**] | The speaker type | defaults to undefined
 **speaker** | [**string**] | The speaker | defaults to undefined


### Return type

**any**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **speakMessage**
> any speakMessage()

Read out the message.

### Example


```typescript
import { createConfiguration, TTSServiceApi } from '';
import type { TTSServiceApiSpeakMessageRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TTSServiceApi(configuration);

const request: TTSServiceApiSpeakMessageRequest = {
    // The message id
  messageId: 1,
};

const data = await apiInstance.speakMessage(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **messageId** | [**number**] | The message id | defaults to undefined


### Return type

**any**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


