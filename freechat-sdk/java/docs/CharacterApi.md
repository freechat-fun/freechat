# CharacterApi

All URIs are relative to *https://freechat.fun*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addCharacterBackend**](CharacterApi.md#addCharacterBackend) | **POST** /api/v1/character/backend/{characterId} | Add Character Backend |
| [**batchSearchCharacterDetails**](CharacterApi.md#batchSearchCharacterDetails) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details |
| [**batchSearchCharacterSummary**](CharacterApi.md#batchSearchCharacterSummary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries |
| [**cloneCharacter**](CharacterApi.md#cloneCharacter) | **POST** /api/v1/character/clone/{characterId} | Clone Character |
| [**countCharacters**](CharacterApi.md#countCharacters) | **POST** /api/v1/character/count | Calculate Number of Characters |
| [**createCharacter**](CharacterApi.md#createCharacter) | **POST** /api/v1/character | Create Character |
| [**deleteCharacter**](CharacterApi.md#deleteCharacter) | **DELETE** /api/v1/character/{characterId} | Delete Character |
| [**deleteChat**](CharacterApi.md#deleteChat) | **DELETE** /api/v1/character/chat/{chatId} | Delete Chat Session |
| [**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details |
| [**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name |
| [**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary |
| [**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend |
| [**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backends/{characterId} | List Character Backend ids |
| [**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name |
| [**listMessages**](CharacterApi.md#listMessages) | **GET** /api/v1/character/chat/messages/{chatId}/{limit} | List Chat Messages |
| [**listMessages1**](CharacterApi.md#listMessages1) | **GET** /api/v1/character/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages |
| [**listMessages2**](CharacterApi.md#listMessages2) | **GET** /api/v1/character/chat/messages/{chatId} | List Chat Messages |
| [**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v1/character/publish/{characterId} | Publish Character |
| [**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character |
| [**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend |
| [**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v1/character/details/search | Search Character Details |
| [**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v1/character/search | Search Character Summary |
| [**sendMessage**](CharacterApi.md#sendMessage) | **POST** /api/v1/character/chat/send/{chatId} | Send Chat Message |
| [**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend |
| [**startChat**](CharacterApi.md#startChat) | **POST** /api/v1/character/chat | Start Chat Session |
| [**streamSendMessage**](CharacterApi.md#streamSendMessage) | **POST** /api/v1/character/chat/send/stream/{chatId} | Send Chat Message by Streaming Back |
| [**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v1/character/{characterId} | Update Character |
| [**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend |
| [**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v1/character/avatar | Upload Character Avatar |
| [**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v1/character/picture | Upload Character Picture |


<a id="addCharacterBackend"></a>
# **addCharacterBackend**
> String addCharacterBackend(characterId, characterBackendDTO)

Add Character Backend

Add a backend configuration for a character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be added a backend
    CharacterBackendDTO characterBackendDTO = new CharacterBackendDTO(); // CharacterBackendDTO | The character backend to be added
    try {
      String result = apiInstance.addCharacterBackend(characterId, characterBackendDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#addCharacterBackend");
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
| **characterId** | **String**| The characterId to be added a backend | |
| **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend to be added | |

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

<a id="batchSearchCharacterDetails"></a>
# **batchSearchCharacterDetails**
> List&lt;List&lt;CharacterDetailsDTO&gt;&gt; batchSearchCharacterDetails(characterQueryDTO)

Batch Search Character Details

Batch call shortcut for /api/v1/character/details/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    List<CharacterQueryDTO> characterQueryDTO = Arrays.asList(); // List<CharacterQueryDTO> | Query conditions
    try {
      List<List<CharacterDetailsDTO>> result = apiInstance.batchSearchCharacterDetails(characterQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#batchSearchCharacterDetails");
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
| **characterQueryDTO** | [**List&lt;CharacterQueryDTO&gt;**](CharacterQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;CharacterDetailsDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="batchSearchCharacterSummary"></a>
# **batchSearchCharacterSummary**
> List&lt;List&lt;CharacterSummaryDTO&gt;&gt; batchSearchCharacterSummary(characterQueryDTO)

Batch Search Character Summaries

Batch call shortcut for /api/v1/character/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    List<CharacterQueryDTO> characterQueryDTO = Arrays.asList(); // List<CharacterQueryDTO> | Query conditions
    try {
      List<List<CharacterSummaryDTO>> result = apiInstance.batchSearchCharacterSummary(characterQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#batchSearchCharacterSummary");
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
| **characterQueryDTO** | [**List&lt;CharacterQueryDTO&gt;**](CharacterQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;CharacterSummaryDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="cloneCharacter"></a>
# **cloneCharacter**
> String cloneCharacter(characterId)

Clone Character

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The referenced characterId
    try {
      String result = apiInstance.cloneCharacter(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#cloneCharacter");
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
| **characterId** | **String**| The referenced characterId | |

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

<a id="countCharacters"></a>
# **countCharacters**
> Long countCharacters(characterQueryDTO)

Calculate Number of Characters

Calculate the number of characters according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    CharacterQueryDTO characterQueryDTO = new CharacterQueryDTO(); // CharacterQueryDTO | Query conditions
    try {
      Long result = apiInstance.countCharacters(characterQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#countCharacters");
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
| **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createCharacter"></a>
# **createCharacter**
> String createCharacter(characterCreateDTO)

Create Character

Create a character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    CharacterCreateDTO characterCreateDTO = new CharacterCreateDTO(); // CharacterCreateDTO | Information of the character to be created
    try {
      String result = apiInstance.createCharacter(characterCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#createCharacter");
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
| **characterCreateDTO** | [**CharacterCreateDTO**](CharacterCreateDTO.md)| Information of the character to be created | |

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

<a id="deleteCharacter"></a>
# **deleteCharacter**
> Boolean deleteCharacter(characterId)

Delete Character

Delete character. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be deleted
    try {
      Boolean result = apiInstance.deleteCharacter(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#deleteCharacter");
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
| **characterId** | **String**| The characterId to be deleted | |

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      Boolean result = apiInstance.deleteChat(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#deleteChat");
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

<a id="getCharacterDetails"></a>
# **getCharacterDetails**
> CharacterDetailsDTO getCharacterDetails(characterId)

Get Character Details

Get character detailed information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | CharacterId to be obtained
    try {
      CharacterDetailsDTO result = apiInstance.getCharacterDetails(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#getCharacterDetails");
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
| **characterId** | **String**| CharacterId to be obtained | |

### Return type

[**CharacterDetailsDTO**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getCharacterLatestIdByName"></a>
# **getCharacterLatestIdByName**
> String getCharacterLatestIdByName(name)

Get Latest Character Id by Name

Get latest characterId by character name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String name = "name_example"; // String | Character name
    try {
      String result = apiInstance.getCharacterLatestIdByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#getCharacterLatestIdByName");
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
| **name** | **String**| Character name | |

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

<a id="getCharacterSummary"></a>
# **getCharacterSummary**
> CharacterSummaryDTO getCharacterSummary(characterId)

Get Character Summary

Get character summary information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | CharacterId to be obtained
    try {
      CharacterSummaryDTO result = apiInstance.getCharacterSummary(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#getCharacterSummary");
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
| **characterId** | **String**| CharacterId to be obtained | |

### Return type

[**CharacterSummaryDTO**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getDefaultCharacterBackend"></a>
# **getDefaultCharacterBackend**
> CharacterBackendDetailsDTO getDefaultCharacterBackend(characterId)

Get Default Character Backend

Get the default backend configuration.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be queried
    try {
      CharacterBackendDetailsDTO result = apiInstance.getDefaultCharacterBackend(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#getDefaultCharacterBackend");
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
| **characterId** | **String**| The characterId to be queried | |

### Return type

[**CharacterBackendDetailsDTO**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharacterBackendIds"></a>
# **listCharacterBackendIds**
> List&lt;String&gt; listCharacterBackendIds(characterId)

List Character Backend ids

List Character Backend identifiers.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be queried
    try {
      List<String> result = apiInstance.listCharacterBackendIds(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listCharacterBackendIds");
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
| **characterId** | **String**| The characterId to be queried | |

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

<a id="listCharacterVersionsByName"></a>
# **listCharacterVersionsByName**
> List&lt;CharacterItemForNameDTO&gt; listCharacterVersionsByName(name)

List Versions by Character Name

List the versions and corresponding characterIds by character name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String name = "name_example"; // String | Character name
    try {
      List<CharacterItemForNameDTO> result = apiInstance.listCharacterVersionsByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listCharacterVersionsByName");
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
| **name** | **String**| Character name | |

### Return type

[**List&lt;CharacterItemForNameDTO&gt;**](CharacterItemForNameDTO.md)

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
> List&lt;ChatMessageDTO&gt; listMessages(chatId, limit)

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    try {
      List<ChatMessageDTO> result = apiInstance.listMessages(chatId, limit);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listMessages");
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

[**List&lt;ChatMessageDTO&gt;**](ChatMessageDTO.md)

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
> List&lt;ChatMessageDTO&gt; listMessages1(chatId, limit, offset)

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    Integer limit = 56; // Integer | Messages limit
    Integer offset = 56; // Integer | Messages offset (from new to old)
    try {
      List<ChatMessageDTO> result = apiInstance.listMessages1(chatId, limit, offset);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listMessages1");
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

[**List&lt;ChatMessageDTO&gt;**](ChatMessageDTO.md)

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
> List&lt;ChatMessageDTO&gt; listMessages2(chatId)

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    try {
      List<ChatMessageDTO> result = apiInstance.listMessages2(chatId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listMessages2");
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

[**List&lt;ChatMessageDTO&gt;**](ChatMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="publishCharacter"></a>
# **publishCharacter**
> String publishCharacter(characterId)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be published
    try {
      String result = apiInstance.publishCharacter(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#publishCharacter");
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
| **characterId** | **String**| The characterId to be published | |

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

<a id="publishCharacter1"></a>
# **publishCharacter1**
> String publishCharacter1(characterId, visibility)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be published
    String visibility = "visibility_example"; // String | Visibility: public | private | ...
    try {
      String result = apiInstance.publishCharacter1(characterId, visibility);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#publishCharacter1");
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
| **characterId** | **String**| The characterId to be published | |
| **visibility** | **String**| Visibility: public | private | ... | |

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

<a id="removeCharacterBackend"></a>
# **removeCharacterBackend**
> Boolean removeCharacterBackend(characterBackendId)

Remove Character Backend

Remove a backend configuration.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be removed
    try {
      Boolean result = apiInstance.removeCharacterBackend(characterBackendId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#removeCharacterBackend");
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
| **characterBackendId** | **String**| The characterBackendId to be removed | |

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

<a id="searchCharacterDetails"></a>
# **searchCharacterDetails**
> List&lt;CharacterDetailsDTO&gt; searchCharacterDetails(characterQueryDTO)

Search Character Details

Same as /api/v1/character/search, but returns detailed information of the character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    CharacterQueryDTO characterQueryDTO = new CharacterQueryDTO(); // CharacterQueryDTO | Query conditions
    try {
      List<CharacterDetailsDTO> result = apiInstance.searchCharacterDetails(characterQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#searchCharacterDetails");
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
| **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;CharacterDetailsDTO&gt;**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchCharacterSummary"></a>
# **searchCharacterSummary**
> List&lt;CharacterSummaryDTO&gt; searchCharacterSummary(characterQueryDTO)

Search Character Summary

Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    CharacterQueryDTO characterQueryDTO = new CharacterQueryDTO(); // CharacterQueryDTO | Query conditions
    try {
      List<CharacterSummaryDTO> result = apiInstance.searchCharacterSummary(characterQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#searchCharacterSummary");
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
| **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;CharacterSummaryDTO&gt;**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="sendMessage"></a>
# **sendMessage**
> LlmResultDTO sendMessage(chatId, chatContentDTO)

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    ChatContentDTO chatContentDTO = new ChatContentDTO(); // ChatContentDTO | Chat content
    try {
      LlmResultDTO result = apiInstance.sendMessage(chatId, chatContentDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#sendMessage");
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
| **chatContentDTO** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | |

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

<a id="setDefaultCharacterBackend"></a>
# **setDefaultCharacterBackend**
> Boolean setDefaultCharacterBackend(characterBackendId)

Set Default Character Backend

Set the default backend configuration.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be set to default
    try {
      Boolean result = apiInstance.setDefaultCharacterBackend(characterBackendId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#setDefaultCharacterBackend");
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
| **characterBackendId** | **String**| The characterBackendId to be set to default | |

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    ChatCreateDTO chatCreateDTO = new ChatCreateDTO(); // ChatCreateDTO | Parameters for starting a chat session
    try {
      String result = apiInstance.startChat(chatCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#startChat");
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

<a id="streamSendMessage"></a>
# **streamSendMessage**
> SseEmitter streamSendMessage(chatId, chatContentDTO)

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
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String chatId = "chatId_example"; // String | Chat session identifier
    ChatContentDTO chatContentDTO = new ChatContentDTO(); // ChatContentDTO | Chat content
    try {
      SseEmitter result = apiInstance.streamSendMessage(chatId, chatContentDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#streamSendMessage");
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
| **chatContentDTO** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | |

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

<a id="updateCharacter"></a>
# **updateCharacter**
> Boolean updateCharacter(characterId, characterUpdateDTO)

Update Character

Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterId = "characterId_example"; // String | The characterId to be updated
    CharacterUpdateDTO characterUpdateDTO = new CharacterUpdateDTO(); // CharacterUpdateDTO | The character information to be updated
    try {
      Boolean result = apiInstance.updateCharacter(characterId, characterUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#updateCharacter");
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
| **characterId** | **String**| The characterId to be updated | |
| **characterUpdateDTO** | [**CharacterUpdateDTO**](CharacterUpdateDTO.md)| The character information to be updated | |

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

<a id="updateCharacterBackend"></a>
# **updateCharacterBackend**
> Boolean updateCharacterBackend(characterBackendId, characterBackendDTO)

Update Character Backend

Update a backend configuration.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be updated
    CharacterBackendDTO characterBackendDTO = new CharacterBackendDTO(); // CharacterBackendDTO | The character backend configuration to be updated
    try {
      Boolean result = apiInstance.updateCharacterBackend(characterBackendId, characterBackendDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#updateCharacterBackend");
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
| **characterBackendId** | **String**| The characterBackendId to be updated | |
| **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend configuration to be updated | |

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

<a id="uploadCharacterAvatar"></a>
# **uploadCharacterAvatar**
> String uploadCharacterAvatar(_file)

Upload Character Avatar

Upload an avatar of the character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    File _file = new File("/path/to/file"); // File | Character avatar
    try {
      String result = apiInstance.uploadCharacterAvatar(_file);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#uploadCharacterAvatar");
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
| **_file** | **File**| Character avatar | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="uploadCharacterPicture"></a>
# **uploadCharacterPicture**
> String uploadCharacterPicture(_file)

Upload Character Picture

Upload a picture of the character.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.CharacterApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://freechat.fun");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    File _file = new File("/path/to/file"); // File | Character picture
    try {
      String result = apiInstance.uploadCharacterPicture(_file);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#uploadCharacterPicture");
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
| **_file** | **File**| Character picture | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

