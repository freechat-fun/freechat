# TTSServiceApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**listTtsBuiltinSpeakers**](TTSServiceApi.md#listTtsBuiltinSpeakers) | **GET** /api/v2/public/tts/builtin/speakers | List Builtin Speakers
[**playSample**](TTSServiceApi.md#playSample) | **GET** /api/v2/public/tts/play/sample/{speakerType}/{speaker} | Play Sample Audio
[**speakMessage**](TTSServiceApi.md#speakMessage) | **GET** /api/v2/tts/speak/{messageId} | Speak Message



## listTtsBuiltinSpeakers

List Builtin Speakers

Return builtin TTS speakers.

### Example

```bash
freechat listTtsBuiltinSpeakers
```

### Parameters

This endpoint does not need any parameter.

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## playSample

Play Sample Audio

Play TTS sample audio of the builtin/custom speaker.

### Example

```bash
freechat playSample speakerType=value speaker=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **speakerType** | **string** | The speaker type | [default to null]
 **speaker** | **string** | The speaker | [default to null]

### Return type

[**AnyType**](AnyType.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## speakMessage

Speak Message

Read out the message.

### Example

```bash
freechat speakMessage messageId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **messageId** | **integer** | The message id | [default to null]

### Return type

[**AnyType**](AnyType.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

