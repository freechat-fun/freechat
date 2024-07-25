# CharacterApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addCharacterBackend**](CharacterApi.md#addCharacterBackend) | **POST** /api/v1/character/backend/{characterId} | Add Character Backend |
| [**batchSearchCharacterDetails**](CharacterApi.md#batchSearchCharacterDetails) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details |
| [**batchSearchCharacterSummary**](CharacterApi.md#batchSearchCharacterSummary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries |
| [**cloneCharacter**](CharacterApi.md#cloneCharacter) | **POST** /api/v1/character/clone/{characterId} | Clone Character |
| [**countCharacters**](CharacterApi.md#countCharacters) | **POST** /api/v1/character/count | Calculate Number of Characters |
| [**createCharacter**](CharacterApi.md#createCharacter) | **POST** /api/v1/character | Create Character |
| [**deleteCharacter**](CharacterApi.md#deleteCharacter) | **DELETE** /api/v1/character/{characterId} | Delete Character |
| [**deleteCharacterByName**](CharacterApi.md#deleteCharacterByName) | **DELETE** /api/v1/character/name/{name} | Delete Character by Name |
| [**deleteCharacterDocument**](CharacterApi.md#deleteCharacterDocument) | **DELETE** /api/v1/character/document/{key} | Delete Character Document |
| [**deleteCharacterPicture**](CharacterApi.md#deleteCharacterPicture) | **DELETE** /api/v1/character/picture/{key} | Delete Character Picture |
| [**existsCharacterName**](CharacterApi.md#existsCharacterName) | **GET** /api/v1/character/exists/name/{name} | Check If Character Name Exists |
| [**exportCharacter**](CharacterApi.md#exportCharacter) | **GET** /api/v1/character/export/{characterId} | Export Character Configuration |
| [**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details |
| [**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name |
| [**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary |
| [**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend |
| [**importCharacter**](CharacterApi.md#importCharacter) | **POST** /api/v1/character/import | Import Character Configuration |
| [**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backend/ids/{characterId} | List Character Backend ids |
| [**listCharacterBackends**](CharacterApi.md#listCharacterBackends) | **GET** /api/v1/character/backends/{characterId} | List Character Backends |
| [**listCharacterDocuments**](CharacterApi.md#listCharacterDocuments) | **GET** /api/v1/character/documents/{characterId} | List Character Documents |
| [**listCharacterPictures**](CharacterApi.md#listCharacterPictures) | **GET** /api/v1/character/pictures/{characterId} | List Character Pictures |
| [**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name |
| [**newCharacterName**](CharacterApi.md#newCharacterName) | **GET** /api/v1/character/create/name/{desired} | Create New Character Name |
| [**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v1/character/publish/{characterId} | Publish Character |
| [**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character |
| [**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend |
| [**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v1/character/details/search | Search Character Details |
| [**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v1/character/search | Search Character Summary |
| [**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend |
| [**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v1/character/{characterId} | Update Character |
| [**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend |
| [**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v1/character/avatar/{characterId} | Upload Character Avatar |
| [**uploadCharacterDocument**](CharacterApi.md#uploadCharacterDocument) | **POST** /api/v1/character/document/{characterId} | Upload Character Document |
| [**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v1/character/picture/{characterId} | Upload Character Picture |


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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be added a backend
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
| **characterId** | **Long**| The characterId to be added a backend | |
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
> Long cloneCharacter(characterId)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The referenced characterId
    try {
      Long result = apiInstance.cloneCharacter(characterId);
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
| **characterId** | **Long**| The referenced characterId | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
> Long createCharacter(characterCreateDTO)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    CharacterCreateDTO characterCreateDTO = new CharacterCreateDTO(); // CharacterCreateDTO | Information of the character to be created
    try {
      Long result = apiInstance.createCharacter(characterCreateDTO);
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be deleted
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
| **characterId** | **Long**| The characterId to be deleted | |

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

<a id="deleteCharacterByName"></a>
# **deleteCharacterByName**
> List&lt;Long&gt; deleteCharacterByName(name)

Delete Character by Name

Delete character by name. return the list of successfully deleted characterIds.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String name = "name_example"; // String | The character name to be deleted
    try {
      List<Long> result = apiInstance.deleteCharacterByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#deleteCharacterByName");
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
| **name** | **String**| The character name to be deleted | |

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

<a id="deleteCharacterDocument"></a>
# **deleteCharacterDocument**
> Boolean deleteCharacterDocument(key)

Delete Character Document

Delete a document of the character by key.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String key = "key_example"; // String | Document key
    try {
      Boolean result = apiInstance.deleteCharacterDocument(key);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#deleteCharacterDocument");
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
| **key** | **String**| Document key | |

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

<a id="deleteCharacterPicture"></a>
# **deleteCharacterPicture**
> Boolean deleteCharacterPicture(key)

Delete Character Picture

Delete a picture of the character by key.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String key = "key_example"; // String | Image key
    try {
      Boolean result = apiInstance.deleteCharacterPicture(key);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#deleteCharacterPicture");
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
| **key** | **String**| Image key | |

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

<a id="existsCharacterName"></a>
# **existsCharacterName**
> Boolean existsCharacterName(name)

Check If Character Name Exists

Check if the character name already exists.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String name = "name_example"; // String | Name
    try {
      Boolean result = apiInstance.existsCharacterName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#existsCharacterName");
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
| **name** | **String**| Name | |

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

<a id="exportCharacter"></a>
# **exportCharacter**
> exportCharacter(characterId)

Export Character Configuration

Export character configuration in tar.gz format, including settings, documents and pictures.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    try {
      apiInstance.exportCharacter(characterId);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#exportCharacter");
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

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | CharacterId to be obtained
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
| **characterId** | **Long**| CharacterId to be obtained | |

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
> Long getCharacterLatestIdByName(name)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String name = "name_example"; // String | Character name
    try {
      Long result = apiInstance.getCharacterLatestIdByName(name);
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

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | CharacterId to be obtained
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
| **characterId** | **Long**| CharacterId to be obtained | |

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be queried
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
| **characterId** | **Long**| The characterId to be queried | |

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

<a id="importCharacter"></a>
# **importCharacter**
> Long importCharacter(_file)

Import Character Configuration

Import character configuration from a tar.gz file.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    File _file = new File("/path/to/file"); // File | Character avatar
    try {
      Long result = apiInstance.importCharacter(_file);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#importCharacter");
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

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharacterBackendIds"></a>
# **listCharacterBackendIds**
> List&lt;String&gt; listCharacterBackendIds(characterId)

List Character Backend ids

List character backend identifiers.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be queried
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
| **characterId** | **Long**| The characterId to be queried | |

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

<a id="listCharacterBackends"></a>
# **listCharacterBackends**
> List&lt;CharacterBackendDetailsDTO&gt; listCharacterBackends(characterId)

List Character Backends

List character backends.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be queried
    try {
      List<CharacterBackendDetailsDTO> result = apiInstance.listCharacterBackends(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listCharacterBackends");
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
| **characterId** | **Long**| The characterId to be queried | |

### Return type

[**List&lt;CharacterBackendDetailsDTO&gt;**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listCharacterDocuments"></a>
# **listCharacterDocuments**
> List&lt;String&gt; listCharacterDocuments(characterId)

List Character Documents

List documents of the character.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    try {
      List<String> result = apiInstance.listCharacterDocuments(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listCharacterDocuments");
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

<a id="listCharacterPictures"></a>
# **listCharacterPictures**
> List&lt;String&gt; listCharacterPictures(characterId)

List Character Pictures

List pictures of the character.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    try {
      List<String> result = apiInstance.listCharacterPictures(characterId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#listCharacterPictures");
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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

<a id="newCharacterName"></a>
# **newCharacterName**
> String newCharacterName(desired)

Create New Character Name

Create a new character name starting with a desired name.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    String desired = "desired_example"; // String | Desired name
    try {
      String result = apiInstance.newCharacterName(desired);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#newCharacterName");
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
| **desired** | **String**| Desired name | |

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

<a id="publishCharacter"></a>
# **publishCharacter**
> Long publishCharacter(characterId)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be published
    try {
      Long result = apiInstance.publishCharacter(characterId);
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
| **characterId** | **Long**| The characterId to be published | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="publishCharacter1"></a>
# **publishCharacter1**
> Long publishCharacter1(characterId, visibility)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be published
    String visibility = "visibility_example"; // String | Visibility: public | private | ...
    try {
      Long result = apiInstance.publishCharacter1(characterId, visibility);
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
| **characterId** | **Long**| The characterId to be published | |
| **visibility** | **String**| Visibility: public | private | ... | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | The characterId to be updated
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
| **characterId** | **Long**| The characterId to be updated | |
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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
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
> String uploadCharacterAvatar(characterId, _file)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    File _file = new File("/path/to/file"); // File | Character avatar
    try {
      String result = apiInstance.uploadCharacterAvatar(characterId, _file);
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
| **characterId** | **Long**| Character identifier | |
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

<a id="uploadCharacterDocument"></a>
# **uploadCharacterDocument**
> String uploadCharacterDocument(characterId, _file)

Upload Character Document

Upload a document of the character.

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    File _file = new File("/path/to/file"); // File | Character document
    try {
      String result = apiInstance.uploadCharacterDocument(characterId, _file);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling CharacterApi#uploadCharacterDocument");
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
| **_file** | **File**| Character document | |

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
> String uploadCharacterPicture(characterId, _file)

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
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    CharacterApi apiInstance = new CharacterApi(defaultClient);
    Long characterId = 56L; // Long | Character identifier
    File _file = new File("/path/to/file"); // File | Character picture
    try {
      String result = apiInstance.uploadCharacterPicture(characterId, _file);
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
| **characterId** | **Long**| Character identifier | |
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

