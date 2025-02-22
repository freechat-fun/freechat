# TtsServiceApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**listTtsBuiltinSpeakers**](TtsServiceApi.md#listTtsBuiltinSpeakers) | **GET** /api/v2/public/tts/builtin/speakers | List Builtin Speakers |
| [**playSample**](TtsServiceApi.md#playSample) | **GET** /api/v2/public/tts/play/sample/{speakerType}/{speaker} | Play Sample Audio |
| [**speakMessage**](TtsServiceApi.md#speakMessage) | **GET** /api/v2/tts/speak/{messageId} | Speak Message |


<a id="listTtsBuiltinSpeakers"></a>
# **listTtsBuiltinSpeakers**
> List&lt;String&gt; listTtsBuiltinSpeakers()

List Builtin Speakers

Return builtin TTS speakers.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TtsServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TtsServiceApi apiInstance = new TtsServiceApi(defaultClient);
    try {
      List<String> result = apiInstance.listTtsBuiltinSpeakers();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TtsServiceApi#listTtsBuiltinSpeakers");
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

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="playSample"></a>
# **playSample**
> Object playSample(speakerType, speaker)

Play Sample Audio

Play TTS sample audio of the builtin/custom speaker.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TtsServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TtsServiceApi apiInstance = new TtsServiceApi(defaultClient);
    String speakerType = "speakerType_example"; // String | The speaker type
    String speaker = "speaker_example"; // String | The speaker
    try {
      Object result = apiInstance.playSample(speakerType, speaker);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TtsServiceApi#playSample");
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
| **speakerType** | **String**| The speaker type | |
| **speaker** | **String**| The speaker | |

### Return type

**Object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="speakMessage"></a>
# **speakMessage**
> Object speakMessage(messageId)

Speak Message

Read out the message.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.TtsServiceApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    TtsServiceApi apiInstance = new TtsServiceApi(defaultClient);
    Long messageId = 56L; // Long | The message id
    try {
      Object result = apiInstance.speakMessage(messageId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TtsServiceApi#speakMessage");
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
| **messageId** | **Long**| The message id | |

### Return type

**Object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: audio/mpeg, audio/aac, audio/mp3, audio/wav, audio/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

