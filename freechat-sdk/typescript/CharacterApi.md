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
[**deleteChat**](CharacterApi.md#deleteChat) | **DELETE** /api/v1/character/chat/{chatId} | Delete Chat Session
[**existsCharacterName**](CharacterApi.md#existsCharacterName) | **GET** /api/v1/character/exists/name/{name} | Check If Character Name Exists
[**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details
[**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
[**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
[**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend
[**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backend/ids/{characterId} | List Character Backend ids
[**listCharacterBackends**](CharacterApi.md#listCharacterBackends) | **GET** /api/v1/character/backends/{characterId} | List Character Backends
[**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
[**listMessages**](CharacterApi.md#listMessages) | **GET** /api/v1/character/chat/messages/{chatId}/{limit} | List Chat Messages
[**listMessages1**](CharacterApi.md#listMessages1) | **GET** /api/v1/character/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**listMessages2**](CharacterApi.md#listMessages2) | **GET** /api/v1/character/chat/messages/{chatId} | List Chat Messages
[**newCharacterName**](CharacterApi.md#newCharacterName) | **GET** /api/v1/character/create/name/{desired} | Create New Character Name
[**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v1/character/publish/{characterId} | Publish Character
[**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character
[**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend
[**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v1/character/details/search | Search Character Details
[**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v1/character/search | Search Character Summary
[**sendMessage**](CharacterApi.md#sendMessage) | **POST** /api/v1/character/chat/send/{chatId} | Send Chat Message
[**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend
[**startChat**](CharacterApi.md#startChat) | **POST** /api/v1/character/chat | Start Chat Session
[**streamSendMessage**](CharacterApi.md#streamSendMessage) | **POST** /api/v1/character/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
[**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v1/character/{characterId} | Update Character
[**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend
[**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v1/character/avatar | Upload Character Avatar
[**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v1/character/picture | Upload Character Picture


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
  // string | The characterId to be added a backend
  characterId: "characterId_example",
  // CharacterBackendDTO | The character backend to be added
  characterBackendDTO: {
    isDefault: true,
    chatPromptTaskId: "chatPromptTaskId_example",
    greetingPromptTaskId: "greetingPromptTaskId_example",
    moderationModelId: "moderationModelId_example",
    moderationApiKeyName: "moderationApiKeyName_example",
    moderationParams: "moderationParams_example",
    messageWindowSize: 1,
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
 **characterId** | [**string**] | The characterId to be added a backend | defaults to undefined


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
> string cloneCharacter()

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiCloneCharacterRequest = {
  // string | The referenced characterId
  characterId: "characterId_example",
};

apiInstance.cloneCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The referenced characterId | defaults to undefined


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
> string createCharacter(characterCreateDTO)

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
    parentId: "parentId_example",
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
  // string | The characterId to be deleted
  characterId: "characterId_example",
};

apiInstance.deleteCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The characterId to be deleted | defaults to undefined


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
> Array<string> deleteCharacterByName()

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

# **deleteChat**
> boolean deleteChat()

Delete the chat session.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiDeleteChatRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
};

apiInstance.deleteChat(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


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
  // string | CharacterId to be obtained
  characterId: "characterId_example",
};

apiInstance.getCharacterDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | CharacterId to be obtained | defaults to undefined


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
> string getCharacterLatestIdByName()

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
  // string | CharacterId to be obtained
  characterId: "characterId_example",
};

apiInstance.getCharacterSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | CharacterId to be obtained | defaults to undefined


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
  // string | The characterId to be queried
  characterId: "characterId_example",
};

apiInstance.getDefaultCharacterBackend(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The characterId to be queried | defaults to undefined


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
  // string | The characterId to be queried
  characterId: "characterId_example",
};

apiInstance.listCharacterBackendIds(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The characterId to be queried | defaults to undefined


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
  // string | The characterId to be queried
  characterId: "characterId_example",
};

apiInstance.listCharacterBackends(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The characterId to be queried | defaults to undefined


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

# **listMessages**
> Array<ChatMessageDTO> listMessages()

List messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListMessagesRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // number | Messages limit
  limit: 1,
};

apiInstance.listMessages(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined


### Return type

**Array<ChatMessageDTO>**

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

# **listMessages1**
> Array<ChatMessageDTO> listMessages1()

List messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListMessages1Request = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // number | Messages limit
  limit: 1,
  // number | Messages offset (from new to old)
  offset: 1,
};

apiInstance.listMessages1(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined
 **limit** | [**number**] | Messages limit | defaults to undefined
 **offset** | [**number**] | Messages offset (from new to old) | defaults to undefined


### Return type

**Array<ChatMessageDTO>**

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

# **listMessages2**
> Array<ChatMessageDTO> listMessages2()

List messages of a chat.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiListMessages2Request = {
  // string | Chat session identifier
  chatId: "chatId_example",
};

apiInstance.listMessages2(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**Array<ChatMessageDTO>**

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
> string publishCharacter()

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiPublishCharacterRequest = {
  // string | The characterId to be published
  characterId: "characterId_example",
};

apiInstance.publishCharacter(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | [**string**] | The characterId to be published | defaults to undefined


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

# **publishCharacter1**
> string publishCharacter1()

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiPublishCharacter1Request = {
  // string | The characterId to be published
  characterId: "characterId_example",
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
 **characterId** | [**string**] | The characterId to be published | defaults to undefined
 **visibility** | [**string**] | Visibility: public | private | ... | defaults to undefined


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

# **sendMessage**
> LlmResultDTO sendMessage(chatMessageDTO)

Send a chat message to character.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiSendMessageRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // ChatMessageDTO | Chat message
  chatMessageDTO: {
    role: "role_example",
    name: "name_example",
    contents: [
      {
        type: "type_example",
        content: "content_example",
        mimeType: "mimeType_example",
      },
    ],
    toolCalls: [
      {
        id: "id_example",
        name: "name_example",
        arguments: "arguments_example",
      },
    ],
    gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
    context: "context_example",
  },
};

apiInstance.sendMessage(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatMessageDTO** | **ChatMessageDTO**| Chat message |
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**LlmResultDTO**

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

# **startChat**
> string startChat(chatCreateDTO)

Start a chat session.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiStartChatRequest = {
  // ChatCreateDTO | Parameters for starting a chat session
  chatCreateDTO: {
    userNickname: "userNickname_example",
    userProfile: "userProfile_example",
    characterNickname: "characterNickname_example",
    about: "about_example",
    backendId: "backendId_example",
    ext: "ext_example",
  },
};

apiInstance.startChat(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatCreateDTO** | **ChatCreateDTO**| Parameters for starting a chat session |


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

# **streamSendMessage**
> SseEmitter streamSendMessage(chatMessageDTO)

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .CharacterApi(configuration);

let body:.CharacterApiStreamSendMessageRequest = {
  // string | Chat session identifier
  chatId: "chatId_example",
  // ChatMessageDTO | Chat message
  chatMessageDTO: {
    role: "role_example",
    name: "name_example",
    contents: [
      {
        type: "type_example",
        content: "content_example",
        mimeType: "mimeType_example",
      },
    ],
    toolCalls: [
      {
        id: "id_example",
        name: "name_example",
        arguments: "arguments_example",
      },
    ],
    gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
    context: "context_example",
  },
};

apiInstance.streamSendMessage(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatMessageDTO** | **ChatMessageDTO**| Chat message |
 **chatId** | [**string**] | Chat session identifier | defaults to undefined


### Return type

**SseEmitter**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream


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
  // string | The characterId to be updated
  characterId: "characterId_example",
  // CharacterUpdateDTO | The character information to be updated
  characterUpdateDTO: {
    parentId: "parentId_example",
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
 **characterId** | [**string**] | The characterId to be updated | defaults to undefined


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


