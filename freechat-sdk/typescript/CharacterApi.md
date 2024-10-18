# .CharacterApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCharacterBackend**](CharacterApi.md#addCharacterBackend) | **POST** /api/v1/character/backend/{characterUid} | Add Character Backend
[**batchSearchCharacterDetails**](CharacterApi.md#batchSearchCharacterDetails) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details
[**batchSearchCharacterSummary**](CharacterApi.md#batchSearchCharacterSummary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries
[**cloneCharacter**](CharacterApi.md#cloneCharacter) | **POST** /api/v1/character/clone/{characterId} | Clone Character
[**countCharacters**](CharacterApi.md#countCharacters) | **POST** /api/v1/character/count | Calculate Number of Characters
[**countPublicCharacters**](CharacterApi.md#countPublicCharacters) | **POST** /api/v1/public/character/count | Calculate Number of Public Characters
[**createCharacter**](CharacterApi.md#createCharacter) | **POST** /api/v1/character | Create Character
[**deleteCharacter**](CharacterApi.md#deleteCharacter) | **DELETE** /api/v1/character/{characterId} | Delete Character
[**deleteCharacterByName**](CharacterApi.md#deleteCharacterByName) | **DELETE** /api/v1/character/name/{name} | Delete Character by Name
[**deleteCharacterByUid**](CharacterApi.md#deleteCharacterByUid) | **DELETE** /api/v1/character/uid/{characterUid} | Delete Character by Uid
[**deleteCharacterDocument**](CharacterApi.md#deleteCharacterDocument) | **DELETE** /api/v1/character/document/{key} | Delete Character Document
[**deleteCharacterPicture**](CharacterApi.md#deleteCharacterPicture) | **DELETE** /api/v1/character/picture/{key} | Delete Character Picture
[**existsCharacterName**](CharacterApi.md#existsCharacterName) | **GET** /api/v1/character/exists/name/{name} | Check If Character Name Exists
[**exportCharacter**](CharacterApi.md#exportCharacter) | **GET** /api/v1/character/export/{characterId} | Export Character Configuration
[**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details
[**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
[**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
[**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterUid} | Get Default Character Backend
[**importCharacter**](CharacterApi.md#importCharacter) | **POST** /api/v1/character/import | Import Character Configuration
[**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backend/ids/{characterUid} | List Character Backend ids
[**listCharacterBackends**](CharacterApi.md#listCharacterBackends) | **GET** /api/v1/character/backends/{characterUid} | List Character Backends
[**listCharacterDocuments**](CharacterApi.md#listCharacterDocuments) | **GET** /api/v1/character/documents/{characterUid} | List Character Documents
[**listCharacterPictures**](CharacterApi.md#listCharacterPictures) | **GET** /api/v1/character/pictures/{characterUid} | List Character Pictures
[**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
[**newCharacterName**](CharacterApi.md#newCharacterName) | **GET** /api/v1/character/create/name/{desired} | Create New Character Name
[**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v1/character/publish/{characterId} | Publish Character
[**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character
[**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend
[**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v1/character/details/search | Search Character Details
[**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v1/character/search | Search Character Summary
[**searchPublicCharacterSummary**](CharacterApi.md#searchPublicCharacterSummary) | **POST** /api/v1/public/character/search | Search Public Character Summary
[**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend
[**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v1/character/{characterId} | Update Character
[**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend
[**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v1/character/avatar/{characterUid} | Upload Character Avatar
[**uploadCharacterDocument**](CharacterApi.md#uploadCharacterDocument) | **POST** /api/v1/character/document/{characterUid} | Upload Character Document
[**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v1/character/picture/{characterUid} | Upload Character Picture


# **addCharacterBackend**
> string addCharacterBackend(characterBackendDTO)

Add a backend configuration for a character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiAddCharacterBackendRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiAddCharacterBackendRequest = {
    // The characterUid to be added a backend
  characterUid: "characterUid_example",
    // The character backend to be added
  characterBackendDTO: {
    isDefault: true,
    chatPromptTaskId: "chatPromptTaskId_example",
    greetingPromptTaskId: "greetingPromptTaskId_example",
    moderationModelId: "moderationModelId_example",
    moderationApiKeyName: "moderationApiKeyName_example",
    moderationParams: "moderationParams_example",
    messageWindowSize: 1,
    longTermMemoryWindowSize: 1,
    proactiveChatWaitingTime: 1,
    initQuota: 1,
    quotaType: "quotaType_example",
  },
};

const data = await apiInstance.addCharacterBackend(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendDTO** | **CharacterBackendDTO**| The character backend to be added |
 **characterUid** | [**string**] | The characterUid to be added a backend | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **batchSearchCharacterDetails**
> Array<Array<CharacterDetailsDTO>> batchSearchCharacterDetails(characterQueryDTO)

Batch call shortcut for /api/v1/character/details/search.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiBatchSearchCharacterDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiBatchSearchCharacterDetailsRequest = {
    // Query conditions
  characterQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        name: "name_example",
        lang: "lang_example",
        text: "text_example",
        highPriority: true,
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchCharacterDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **Array<CharacterQueryDTO>**| Query conditions |


### Return type

**Array<Array<CharacterDetailsDTO>>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **batchSearchCharacterSummary**
> Array<Array<CharacterSummaryDTO>> batchSearchCharacterSummary(characterQueryDTO)

Batch call shortcut for /api/v1/character/search.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiBatchSearchCharacterSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiBatchSearchCharacterSummaryRequest = {
    // Query conditions
  characterQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        name: "name_example",
        lang: "lang_example",
        text: "text_example",
        highPriority: true,
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchCharacterSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **Array<CharacterQueryDTO>**| Query conditions |


### Return type

**Array<Array<CharacterSummaryDTO>>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **cloneCharacter**
> number cloneCharacter()

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiCloneCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiCloneCharacterRequest = {
    // The referenced characterId
  characterId: 1,
};

const data = await apiInstance.cloneCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The referenced characterId | defaults to undefined


### Return type

**number**

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

# **countCharacters**
> number countCharacters(characterQueryDTO)

Calculate the number of characters according to the specified query conditions.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiCountCharactersRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiCountCharactersRequest = {
    // Query conditions
  characterQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      name: "name_example",
      lang: "lang_example",
      text: "text_example",
      highPriority: true,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.countCharacters(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **CharacterQueryDTO**| Query conditions |


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **countPublicCharacters**
> number countPublicCharacters(characterQueryDTO)

Calculate the number of characters according to the specified query conditions.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiCountPublicCharactersRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiCountPublicCharactersRequest = {
    // Query conditions
  characterQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      name: "name_example",
      lang: "lang_example",
      text: "text_example",
      highPriority: true,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.countPublicCharacters(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **CharacterQueryDTO**| Query conditions |


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **createCharacter**
> number createCharacter(characterCreateDTO)

Create a character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiCreateCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiCreateCharacterRequest = {
    // Information of the character to be created
  characterCreateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    nickname: "nickname_example",
    avatar: "avatar_example",
    picture: "picture_example",
    gender: "gender_example",
    profile: "profile_example",
    greeting: "greeting_example",
    chatStyle: "chatStyle_example",
    chatExample: "chatExample_example",
    defaultScene: "defaultScene_example",
    lang: "lang_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
  },
};

const data = await apiInstance.createCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterCreateDTO** | **CharacterCreateDTO**| Information of the character to be created |


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **deleteCharacter**
> boolean deleteCharacter()

Delete character. Returns success or failure.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiDeleteCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiDeleteCharacterRequest = {
    // The characterId to be deleted
  characterId: 1,
};

const data = await apiInstance.deleteCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be deleted | defaults to undefined


### Return type

**boolean**

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

# **deleteCharacterByName**
> Array<number> deleteCharacterByName()

Delete character by name. return the list of successfully deleted characterIds.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiDeleteCharacterByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiDeleteCharacterByNameRequest = {
    // The character name to be deleted
  name: "name_example",
};

const data = await apiInstance.deleteCharacterByName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | The character name to be deleted | defaults to undefined


### Return type

**Array<number>**

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

# **deleteCharacterByUid**
> Array<number> deleteCharacterByUid()

Delete character by uid. return the list of successfully deleted characterIds.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiDeleteCharacterByUidRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiDeleteCharacterByUidRequest = {
    // The character uid to be deleted
  characterUid: "characterUid_example",
};

const data = await apiInstance.deleteCharacterByUid(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | The character uid to be deleted | defaults to undefined


### Return type

**Array<number>**

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

# **deleteCharacterDocument**
> boolean deleteCharacterDocument()

Delete a document of the character by key.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiDeleteCharacterDocumentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiDeleteCharacterDocumentRequest = {
    // Document key
  key: "key_example",
};

const data = await apiInstance.deleteCharacterDocument(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | [**string**] | Document key | defaults to undefined


### Return type

**boolean**

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

# **deleteCharacterPicture**
> boolean deleteCharacterPicture()

Delete a picture of the character by key.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiDeleteCharacterPictureRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiDeleteCharacterPictureRequest = {
    // Image key
  key: "key_example",
};

const data = await apiInstance.deleteCharacterPicture(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | [**string**] | Image key | defaults to undefined


### Return type

**boolean**

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

# **existsCharacterName**
> boolean existsCharacterName()

Check if the character name already exists.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiExistsCharacterNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiExistsCharacterNameRequest = {
    // Name
  name: "name_example",
};

const data = await apiInstance.existsCharacterName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Name | defaults to undefined


### Return type

**boolean**

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

# **exportCharacter**
> void exportCharacter()

Export character configuration in tar.gz format, including settings, documents and pictures.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiExportCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiExportCharacterRequest = {
    // Character identifier
  characterId: 1,
};

const data = await apiInstance.exportCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined


### Return type

**void**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getCharacterDetails**
> CharacterDetailsDTO getCharacterDetails()

Get character detailed information.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiGetCharacterDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiGetCharacterDetailsRequest = {
    // CharacterId to be obtained
  characterId: 1,
};

const data = await apiInstance.getCharacterDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | CharacterId to be obtained | defaults to undefined


### Return type

**CharacterDetailsDTO**

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

# **getCharacterLatestIdByName**
> number getCharacterLatestIdByName()

Get latest characterId by character name.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiGetCharacterLatestIdByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiGetCharacterLatestIdByNameRequest = {
    // Character name
  name: "name_example",
};

const data = await apiInstance.getCharacterLatestIdByName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Character name | defaults to undefined


### Return type

**number**

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

# **getCharacterSummary**
> CharacterSummaryDTO getCharacterSummary()

Get character summary information.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiGetCharacterSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiGetCharacterSummaryRequest = {
    // CharacterId to be obtained
  characterId: 1,
};

const data = await apiInstance.getCharacterSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | CharacterId to be obtained | defaults to undefined


### Return type

**CharacterSummaryDTO**

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

# **getDefaultCharacterBackend**
> CharacterBackendDetailsDTO getDefaultCharacterBackend()

Get the default backend configuration.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiGetDefaultCharacterBackendRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiGetDefaultCharacterBackendRequest = {
    // The characterUid to be queried
  characterUid: "characterUid_example",
};

const data = await apiInstance.getDefaultCharacterBackend(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | The characterUid to be queried | defaults to undefined


### Return type

**CharacterBackendDetailsDTO**

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

# **importCharacter**
> number importCharacter()

Import character configuration from a tar.gz file.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiImportCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiImportCharacterRequest = {
    // Character avatar
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

const data = await apiInstance.importCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | [**HttpFile**] | Character avatar | defaults to undefined


### Return type

**number**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **listCharacterBackendIds**
> Array<string> listCharacterBackendIds()

List character backend identifiers.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiListCharacterBackendIdsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiListCharacterBackendIdsRequest = {
    // The characterUid to be queried
  characterUid: "characterUid_example",
};

const data = await apiInstance.listCharacterBackendIds(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | The characterUid to be queried | defaults to undefined


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

# **listCharacterBackends**
> Array<CharacterBackendDetailsDTO> listCharacterBackends()

List character backends.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiListCharacterBackendsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiListCharacterBackendsRequest = {
    // The characterUid to be queried
  characterUid: "characterUid_example",
};

const data = await apiInstance.listCharacterBackends(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | The characterUid to be queried | defaults to undefined


### Return type

**Array<CharacterBackendDetailsDTO>**

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

# **listCharacterDocuments**
> Array<string> listCharacterDocuments()

List documents of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiListCharacterDocumentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiListCharacterDocumentsRequest = {
    // Character unique identifier
  characterUid: "characterUid_example",
};

const data = await apiInstance.listCharacterDocuments(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | Character unique identifier | defaults to undefined


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

# **listCharacterPictures**
> Array<string> listCharacterPictures()

List pictures of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiListCharacterPicturesRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiListCharacterPicturesRequest = {
    // Character unique identifier
  characterUid: "characterUid_example",
};

const data = await apiInstance.listCharacterPictures(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | Character unique identifier | defaults to undefined


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

# **listCharacterVersionsByName**
> Array<CharacterItemForNameDTO> listCharacterVersionsByName()

List the versions and corresponding characterIds by character name.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiListCharacterVersionsByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiListCharacterVersionsByNameRequest = {
    // Character name
  name: "name_example",
};

const data = await apiInstance.listCharacterVersionsByName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Character name | defaults to undefined


### Return type

**Array<CharacterItemForNameDTO>**

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

# **newCharacterName**
> string newCharacterName()

Create a new character name starting with a desired name.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiNewCharacterNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiNewCharacterNameRequest = {
    // Desired name
  desired: "desired_example",
};

const data = await apiInstance.newCharacterName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **desired** | [**string**] | Desired name | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **publishCharacter**
> number publishCharacter()

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiPublishCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiPublishCharacterRequest = {
    // The characterId to be published
  characterId: 1,
};

const data = await apiInstance.publishCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be published | defaults to undefined


### Return type

**number**

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

# **publishCharacter1**
> number publishCharacter1()

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiPublishCharacter1Request } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiPublishCharacter1Request = {
    // The characterId to be published
  characterId: 1,
    // Visibility: public | private | ...
  visibility: "visibility_example",
};

const data = await apiInstance.publishCharacter1(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be published | defaults to undefined
 **visibility** | [**string**] | Visibility: public | private | ... | defaults to undefined


### Return type

**number**

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

# **removeCharacterBackend**
> boolean removeCharacterBackend()

Remove a backend configuration.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiRemoveCharacterBackendRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiRemoveCharacterBackendRequest = {
    // The characterBackendId to be removed
  characterBackendId: "characterBackendId_example",
};

const data = await apiInstance.removeCharacterBackend(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | [**string**] | The characterBackendId to be removed | defaults to undefined


### Return type

**boolean**

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

# **searchCharacterDetails**
> Array<CharacterDetailsDTO> searchCharacterDetails(characterQueryDTO)

Same as /api/v1/character/search, but returns detailed information of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiSearchCharacterDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiSearchCharacterDetailsRequest = {
    // Query conditions
  characterQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      name: "name_example",
      lang: "lang_example",
      text: "text_example",
      highPriority: true,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchCharacterDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **CharacterQueryDTO**| Query conditions |


### Return type

**Array<CharacterDetailsDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchCharacterSummary**
> Array<CharacterSummaryDTO> searchCharacterSummary(characterQueryDTO)

Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiSearchCharacterSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiSearchCharacterSummaryRequest = {
    // Query conditions
  characterQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      name: "name_example",
      lang: "lang_example",
      text: "text_example",
      highPriority: true,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchCharacterSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **CharacterQueryDTO**| Query conditions |


### Return type

**Array<CharacterSummaryDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchPublicCharacterSummary**
> Array<CharacterSummaryDTO> searchPublicCharacterSummary(characterQueryDTO)

Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiSearchPublicCharacterSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiSearchPublicCharacterSummaryRequest = {
    // Query conditions
  characterQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      name: "name_example",
      lang: "lang_example",
      text: "text_example",
      highPriority: true,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchPublicCharacterSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | **CharacterQueryDTO**| Query conditions |


### Return type

**Array<CharacterSummaryDTO>**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **setDefaultCharacterBackend**
> boolean setDefaultCharacterBackend()

Set the default backend configuration.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiSetDefaultCharacterBackendRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiSetDefaultCharacterBackendRequest = {
    // The characterBackendId to be set to default
  characterBackendId: "characterBackendId_example",
};

const data = await apiInstance.setDefaultCharacterBackend(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | [**string**] | The characterBackendId to be set to default | defaults to undefined


### Return type

**boolean**

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

# **updateCharacter**
> boolean updateCharacter(characterUpdateDTO)

Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiUpdateCharacterRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiUpdateCharacterRequest = {
    // The characterId to be updated
  characterId: 1,
    // The character information to be updated
  characterUpdateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    nickname: "nickname_example",
    avatar: "avatar_example",
    picture: "picture_example",
    gender: "gender_example",
    profile: "profile_example",
    greeting: "greeting_example",
    chatStyle: "chatStyle_example",
    chatExample: "chatExample_example",
    defaultScene: "defaultScene_example",
    lang: "lang_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
  },
};

const data = await apiInstance.updateCharacter(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUpdateDTO** | **CharacterUpdateDTO**| The character information to be updated |
 **characterId** | [**number**] | The characterId to be updated | defaults to undefined


### Return type

**boolean**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **updateCharacterBackend**
> boolean updateCharacterBackend(characterBackendDTO)

Update a backend configuration.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiUpdateCharacterBackendRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiUpdateCharacterBackendRequest = {
    // The characterBackendId to be updated
  characterBackendId: "characterBackendId_example",
    // The character backend configuration to be updated
  characterBackendDTO: {
    isDefault: true,
    chatPromptTaskId: "chatPromptTaskId_example",
    greetingPromptTaskId: "greetingPromptTaskId_example",
    moderationModelId: "moderationModelId_example",
    moderationApiKeyName: "moderationApiKeyName_example",
    moderationParams: "moderationParams_example",
    messageWindowSize: 1,
    longTermMemoryWindowSize: 1,
    proactiveChatWaitingTime: 1,
    initQuota: 1,
    quotaType: "quotaType_example",
  },
};

const data = await apiInstance.updateCharacterBackend(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendDTO** | **CharacterBackendDTO**| The character backend configuration to be updated |
 **characterBackendId** | [**string**] | The characterBackendId to be updated | defaults to undefined


### Return type

**boolean**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **uploadCharacterAvatar**
> string uploadCharacterAvatar()

Upload an avatar of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiUploadCharacterAvatarRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiUploadCharacterAvatarRequest = {
    // Character unique identifier
  characterUid: "characterUid_example",
    // Character avatar
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

const data = await apiInstance.uploadCharacterAvatar(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | Character unique identifier | defaults to undefined
 **file** | [**HttpFile**] | Character avatar | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **uploadCharacterDocument**
> string uploadCharacterDocument()

Upload a document of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiUploadCharacterDocumentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiUploadCharacterDocumentRequest = {
    // Character unique identifier
  characterUid: "characterUid_example",
    // Character document
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

const data = await apiInstance.uploadCharacterDocument(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | Character unique identifier | defaults to undefined
 **file** | [**HttpFile**] | Character document | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **uploadCharacterPicture**
> string uploadCharacterPicture()

Upload a picture of the character.

### Example


```typescript
import { createConfiguration, CharacterApi } from '';
import type { CharacterApiUploadCharacterPictureRequest } from '';

const configuration = createConfiguration();
const apiInstance = new CharacterApi(configuration);

const request: CharacterApiUploadCharacterPictureRequest = {
    // Character unique identifier
  characterUid: "characterUid_example",
    // Character picture
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

const data = await apiInstance.uploadCharacterPicture(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | [**string**] | Character unique identifier | defaults to undefined
 **file** | [**HttpFile**] | Character picture | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)


