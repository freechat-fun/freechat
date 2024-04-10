# .CharacterApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCharacterBackend**](CharacterApi.md#addCharacterBackend) | **POST** /api/v1/character/backend/{characterId} | Add Character Backend
[**batchSearchCharacterDetails**](CharacterApi.md#batchSearchCharacterDetails) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details
[**batchSearchCharacterSummary**](CharacterApi.md#batchSearchCharacterSummary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries
[**cloneCharacter**](CharacterApi.md#cloneCharacter) | **POST** /api/v1/character/clone/{characterId} | Clone Character
[**countCharacters**](CharacterApi.md#countCharacters) | **POST** /api/v1/character/count | Calculate Number of Characters
[**createCharacter**](CharacterApi.md#createCharacter) | **POST** /api/v1/character | Create Character
[**deleteCharacter**](CharacterApi.md#deleteCharacter) | **DELETE** /api/v1/character/{characterId} | Delete Character
[**deleteCharacterByName**](CharacterApi.md#deleteCharacterByName) | **DELETE** /api/v1/character/name/{name} | Delete Character by Name
[**deleteCharacterDocument**](CharacterApi.md#deleteCharacterDocument) | **DELETE** /api/v1/character/document/{key} | Delete Character Document
[**deleteCharacterPicture**](CharacterApi.md#deleteCharacterPicture) | **DELETE** /api/v1/character/picture/{key} | Delete Character Picture
[**existsCharacterName**](CharacterApi.md#existsCharacterName) | **GET** /api/v1/character/exists/name/{name} | Check If Character Name Exists
[**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details
[**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
[**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
[**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend
[**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backend/ids/{characterId} | List Character Backend ids
[**listCharacterBackends**](CharacterApi.md#listCharacterBackends) | **GET** /api/v1/character/backends/{characterId} | List Character Backends
[**listCharacterDocuments**](CharacterApi.md#listCharacterDocuments) | **GET** /api/v1/character/documents/{characterId} | List Character Documents
[**listCharacterPictures**](CharacterApi.md#listCharacterPictures) | **GET** /api/v1/character/pictures/{characterId} | List Character Pictures
[**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
[**newCharacterName**](CharacterApi.md#newCharacterName) | **GET** /api/v1/character/create/name/{desired} | Create New Character Name
[**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v1/character/publish/{characterId} | Publish Character
[**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character
[**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend
[**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v1/character/details/search | Search Character Details
[**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v1/character/search | Search Character Summary
[**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend
[**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v1/character/{characterId} | Update Character
[**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend
[**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v1/character/avatar/{characterId} | Upload Character Avatar
[**uploadCharacterDocument**](CharacterApi.md#uploadCharacterDocument) | **POST** /api/v1/character/document/{characterId} | Upload Character Document
[**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v1/character/picture/{characterId} | Upload Character Picture


# **addCharacterBackend**
> string addCharacterBackend(characterBackendDTO)

Add a backend configuration for a character.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiAddCharacterBackendRequest = {
  // number | The characterId to be added a backend
  characterId: 1,
  // CharacterBackendDTO | The character backend to be added
  characterBackendDTO: {
    isDefault: true,
    chatPromptTaskId: "chatPromptTaskId_example",
    greetingPromptTaskId: "greetingPromptTaskId_example",
    moderationModelId: "moderationModelId_example",
    moderationApiKeyName: "moderationApiKeyName_example",
    moderationParams: "moderationParams_example",
    messageWindowSize: 1,
    initQuota: 1,
    quotaType: "quotaType_example",
    forwardToUser: true,
  },
};

apiInstance.addCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendDTO** | **CharacterBackendDTO**| The character backend to be added |
 **characterId** | [**number**] | The characterId to be added a backend | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiBatchSearchCharacterDetailsRequest = {
  // Array<CharacterQueryDTO> | Query conditions
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
        priority: 1,
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

apiInstance.batchSearchCharacterDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiBatchSearchCharacterSummaryRequest = {
  // Array<CharacterQueryDTO> | Query conditions
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
        priority: 1,
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

apiInstance.batchSearchCharacterSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiCloneCharacterRequest = {
  // number | The referenced characterId
  characterId: 1,
};

apiInstance.cloneCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiCountCharactersRequest = {
  // CharacterQueryDTO | Query conditions
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
      priority: 1,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.countCharacters(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiCreateCharacterRequest = {
  // CharacterCreateDTO | Information of the character to be created
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
    lang: "lang_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
  },
};

apiInstance.createCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiDeleteCharacterRequest = {
  // number | The characterId to be deleted
  characterId: 1,
};

apiInstance.deleteCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiDeleteCharacterByNameRequest = {
  // string | The character name to be deleted
  name: "name_example",
};

apiInstance.deleteCharacterByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **deleteCharacterDocument**
> boolean deleteCharacterDocument()

Delete a document of the character by key.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiDeleteCharacterDocumentRequest = {
  // string | Document key
  key: "key_example",
};

apiInstance.deleteCharacterDocument(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiDeleteCharacterPictureRequest = {
  // string | Image key
  key: "key_example",
};

apiInstance.deleteCharacterPicture(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiExistsCharacterNameRequest = {
  // string | Name
  name: "name_example",
};

apiInstance.existsCharacterName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

# **getCharacterDetails**
> CharacterDetailsDTO getCharacterDetails()

Get character detailed information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiGetCharacterDetailsRequest = {
  // number | CharacterId to be obtained
  characterId: 1,
};

apiInstance.getCharacterDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiGetCharacterLatestIdByNameRequest = {
  // string | Character name
  name: "name_example",
};

apiInstance.getCharacterLatestIdByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiGetCharacterSummaryRequest = {
  // number | CharacterId to be obtained
  characterId: 1,
};

apiInstance.getCharacterSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiGetDefaultCharacterBackendRequest = {
  // number | The characterId to be queried
  characterId: 1,
};

apiInstance.getDefaultCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be queried | defaults to undefined


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

# **listCharacterBackendIds**
> Array<string> listCharacterBackendIds()

List character backend identifiers.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListCharacterBackendIdsRequest = {
  // number | The characterId to be queried
  characterId: 1,
};

apiInstance.listCharacterBackendIds(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be queried | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListCharacterBackendsRequest = {
  // number | The characterId to be queried
  characterId: 1,
};

apiInstance.listCharacterBackends(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | The characterId to be queried | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListCharacterDocumentsRequest = {
  // number | Character identifier
  characterId: 1,
};

apiInstance.listCharacterDocuments(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListCharacterPicturesRequest = {
  // number | Character identifier
  characterId: 1,
};

apiInstance.listCharacterPictures(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListCharacterVersionsByNameRequest = {
  // string | Character name
  name: "name_example",
};

apiInstance.listCharacterVersionsByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiNewCharacterNameRequest = {
  // string | Desired name
  desired: "desired_example",
};

apiInstance.newCharacterName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiPublishCharacterRequest = {
  // number | The characterId to be published
  characterId: 1,
};

apiInstance.publishCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiPublishCharacter1Request = {
  // number | The characterId to be published
  characterId: 1,
  // string | Visibility: public | private | ...
  visibility: "visibility_example",
};

apiInstance.publishCharacter1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiRemoveCharacterBackendRequest = {
  // string | The characterBackendId to be removed
  characterBackendId: "characterBackendId_example",
};

apiInstance.removeCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiSearchCharacterDetailsRequest = {
  // CharacterQueryDTO | Query conditions
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
      priority: 1,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.searchCharacterDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiSearchCharacterSummaryRequest = {
  // CharacterQueryDTO | Query conditions
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
      priority: 1,
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.searchCharacterSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiSetDefaultCharacterBackendRequest = {
  // string | The characterBackendId to be set to default
  characterBackendId: "characterBackendId_example",
};

apiInstance.setDefaultCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiUpdateCharacterRequest = {
  // number | The characterId to be updated
  characterId: 1,
  // CharacterUpdateDTO | The character information to be updated
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
    lang: "lang_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
  },
};

apiInstance.updateCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiUpdateCharacterBackendRequest = {
  // string | The characterBackendId to be updated
  characterBackendId: "characterBackendId_example",
  // CharacterBackendDTO | The character backend configuration to be updated
  characterBackendDTO: {
    isDefault: true,
    chatPromptTaskId: "chatPromptTaskId_example",
    greetingPromptTaskId: "greetingPromptTaskId_example",
    moderationModelId: "moderationModelId_example",
    moderationApiKeyName: "moderationApiKeyName_example",
    moderationParams: "moderationParams_example",
    messageWindowSize: 1,
    initQuota: 1,
    quotaType: "quotaType_example",
    forwardToUser: true,
  },
};

apiInstance.updateCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiUploadCharacterAvatarRequest = {
  // number | Character identifier
  characterId: 1,
  // HttpFile | Character avatar
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

apiInstance.uploadCharacterAvatar(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiUploadCharacterDocumentRequest = {
  // number | Character identifier
  characterId: 1,
  // HttpFile | Character document
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

apiInstance.uploadCharacterDocument(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiUploadCharacterPictureRequest = {
  // number | Character identifier
  characterId: 1,
  // HttpFile | Character picture
  file: { data: Buffer.from(fs.readFileSync('/path/to/file', 'utf-8')), name: '/path/to/file' },
};

apiInstance.uploadCharacterPicture(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**number**] | Character identifier | defaults to undefined
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


