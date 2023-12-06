# freechat-sdk.CharacterApi

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
[**deleteChat**](CharacterApi.md#deleteChat) | **DELETE** /api/v1/character/chat/{chatId} | Delete Chat Session
[**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v1/character/details/{characterId} | Get Character Details
[**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
[**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
[**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend
[**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v1/character/backends/{characterId} | List Character Backend ids
[**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
[**listMessages**](CharacterApi.md#listMessages) | **GET** /api/v1/character/chat/messages/{chatId}/{limit} | List Chat Messages
[**listMessages1**](CharacterApi.md#listMessages1) | **GET** /api/v1/character/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**listMessages2**](CharacterApi.md#listMessages2) | **GET** /api/v1/character/chat/messages/{chatId} | List Chat Messages
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



## addCharacterBackend

> String addCharacterBackend(characterId, characterBackendDTO)

Add Character Backend

Add a backend configuration for a character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be added a backend
let characterBackendDTO = new freechat-sdk.CharacterBackendDTO(); // CharacterBackendDTO | The character backend to be added
apiInstance.addCharacterBackend(characterId, characterBackendDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be added a backend | 
 **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend to be added | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## batchSearchCharacterDetails

> [[CharacterDetailsDTO]] batchSearchCharacterDetails(characterQueryDTO)

Batch Search Character Details

Batch call shortcut for /api/v1/character/details/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [CharacterQueryDTO] | Query conditions
apiInstance.batchSearchCharacterDetails(characterQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**[CharacterQueryDTO]**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**[[CharacterDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## batchSearchCharacterSummary

> [[CharacterSummaryDTO]] batchSearchCharacterSummary(characterQueryDTO)

Batch Search Character Summaries

Batch call shortcut for /api/v1/character/search.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterQueryDTO = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}]; // [CharacterQueryDTO] | Query conditions
apiInstance.batchSearchCharacterSummary(characterQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**[CharacterQueryDTO]**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**[[CharacterSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## cloneCharacter

> String cloneCharacter(characterId)

Clone Character

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The referenced characterId
apiInstance.cloneCharacter(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The referenced characterId | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## countCharacters

> Number countCharacters(characterQueryDTO)

Calculate Number of Characters

Calculate the number of characters according to the specified query conditions.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterQueryDTO = new freechat-sdk.CharacterQueryDTO(); // CharacterQueryDTO | Query conditions
apiInstance.countCharacters(characterQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**Number**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## createCharacter

> String createCharacter(characterCreateDTO)

Create Character

Create a character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterCreateDTO = {"name":"A Test Character","description":"A character description","profile":"Hello world. I'm Jack","tags":["test","demo"]}; // CharacterCreateDTO | Information of the character to be created
apiInstance.createCharacter(characterCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterCreateDTO** | [**CharacterCreateDTO**](CharacterCreateDTO.md)| Information of the character to be created | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## deleteCharacter

> Boolean deleteCharacter(characterId)

Delete Character

Delete character. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be deleted
apiInstance.deleteCharacter(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be deleted | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## deleteChat

> Boolean deleteChat(chatId)

Delete Chat Session

Delete the chat session.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
apiInstance.deleteChat(chatId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getCharacterDetails

> CharacterDetailsDTO getCharacterDetails(characterId)

Get Character Details

Get character detailed information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | CharacterId to be obtained
apiInstance.getCharacterDetails(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| CharacterId to be obtained | 

### Return type

[**CharacterDetailsDTO**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getCharacterLatestIdByName

> String getCharacterLatestIdByName(name)

Get Latest Character Id by Name

Get latest characterId by character name.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let name = "name_example"; // String | Character name
apiInstance.getCharacterLatestIdByName(name).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Character name | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## getCharacterSummary

> CharacterSummaryDTO getCharacterSummary(characterId)

Get Character Summary

Get character summary information.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | CharacterId to be obtained
apiInstance.getCharacterSummary(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| CharacterId to be obtained | 

### Return type

[**CharacterSummaryDTO**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getDefaultCharacterBackend

> CharacterBackendDetailsDTO getDefaultCharacterBackend(characterId)

Get Default Character Backend

Get the default backend configuration.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be queried
apiInstance.getDefaultCharacterBackend(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be queried | 

### Return type

[**CharacterBackendDetailsDTO**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCharacterBackendIds

> [String] listCharacterBackendIds(characterId)

List Character Backend ids

List Character Backend identifiers.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be queried
apiInstance.listCharacterBackendIds(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be queried | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCharacterVersionsByName

> [CharacterItemForNameDTO] listCharacterVersionsByName(name)

List Versions by Character Name

List the versions and corresponding characterIds by character name.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let name = "name_example"; // String | Character name
apiInstance.listCharacterVersionsByName(name).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| Character name | 

### Return type

[**[CharacterItemForNameDTO]**](CharacterItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listMessages

> [ChatMessageDTO] listMessages(chatId, limit)

List Chat Messages

List messages of a chat.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
let limit = 56; // Number | Messages limit
apiInstance.listMessages(chatId, limit).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 
 **limit** | **Number**| Messages limit | 

### Return type

[**[ChatMessageDTO]**](ChatMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listMessages1

> [ChatMessageDTO] listMessages1(chatId, limit, offset)

List Chat Messages

List messages of a chat.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
let limit = 56; // Number | Messages limit
let offset = 56; // Number | Messages offset (from new to old)
apiInstance.listMessages1(chatId, limit, offset).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 
 **limit** | **Number**| Messages limit | 
 **offset** | **Number**| Messages offset (from new to old) | 

### Return type

[**[ChatMessageDTO]**](ChatMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listMessages2

> [ChatMessageDTO] listMessages2(chatId)

List Chat Messages

List messages of a chat.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
apiInstance.listMessages2(chatId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 

### Return type

[**[ChatMessageDTO]**](ChatMessageDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## publishCharacter

> String publishCharacter(characterId)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be published
apiInstance.publishCharacter(characterId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be published | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## publishCharacter1

> String publishCharacter1(characterId, visibility)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be published
let visibility = "visibility_example"; // String | Visibility: public | private | ...
apiInstance.publishCharacter1(characterId, visibility).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be published | 
 **visibility** | **String**| Visibility: public | private | ... | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/plain


## removeCharacterBackend

> Boolean removeCharacterBackend(characterBackendId)

Remove Character Backend

Remove a backend configuration.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be removed
apiInstance.removeCharacterBackend(characterBackendId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **String**| The characterBackendId to be removed | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## searchCharacterDetails

> [CharacterDetailsDTO] searchCharacterDetails(characterQueryDTO)

Search Character Details

Same as /api/v1/character/search, but returns detailed information of the character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // CharacterQueryDTO | Query conditions
apiInstance.searchCharacterDetails(characterQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

[**[CharacterDetailsDTO]**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## searchCharacterSummary

> [CharacterSummaryDTO] searchCharacterSummary(characterQueryDTO)

Search Character Summary

Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterQueryDTO = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}; // CharacterQueryDTO | Query conditions
apiInstance.searchCharacterSummary(characterQueryDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

[**[CharacterSummaryDTO]**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## sendMessage

> LlmResultDTO sendMessage(chatId, chatContentDTO)

Send Chat Message

Send a chat message to character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
let chatContentDTO = new freechat-sdk.ChatContentDTO(); // ChatContentDTO | Chat content
apiInstance.sendMessage(chatId, chatContentDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 
 **chatContentDTO** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | 

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## setDefaultCharacterBackend

> Boolean setDefaultCharacterBackend(characterBackendId)

Set Default Character Backend

Set the default backend configuration.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be set to default
apiInstance.setDefaultCharacterBackend(characterBackendId).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **String**| The characterBackendId to be set to default | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## startChat

> String startChat(chatCreateDTO)

Start Chat Session

Start a chat session.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatCreateDTO = new freechat-sdk.ChatCreateDTO(); // ChatCreateDTO | Parameters for starting a chat session
apiInstance.startChat(chatCreateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatCreateDTO** | [**ChatCreateDTO**](ChatCreateDTO.md)| Parameters for starting a chat session | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain


## streamSendMessage

> SseEmitter streamSendMessage(chatId, chatContentDTO)

Send Chat Message by Streaming Back

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let chatId = "chatId_example"; // String | Chat session identifier
let chatContentDTO = new freechat-sdk.ChatContentDTO(); // ChatContentDTO | Chat content
apiInstance.streamSendMessage(chatId, chatContentDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chatId** | **String**| Chat session identifier | 
 **chatContentDTO** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | 

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/event-stream


## updateCharacter

> Boolean updateCharacter(characterId, characterUpdateDTO)

Update Character

Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterId = "characterId_example"; // String | The characterId to be updated
let characterUpdateDTO = {"version":2,"name":"Second Test Character (New)","visibility":"public","description":"Second character description (new)","profile":"I am Kelvin","tags":["test2","demo2","robot"]}; // CharacterUpdateDTO | The character information to be updated
apiInstance.updateCharacter(characterId, characterUpdateDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **String**| The characterId to be updated | 
 **characterUpdateDTO** | [**CharacterUpdateDTO**](CharacterUpdateDTO.md)| The character information to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateCharacterBackend

> Boolean updateCharacterBackend(characterBackendId, characterBackendDTO)

Update Character Backend

Update a backend configuration.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let characterBackendId = "characterBackendId_example"; // String | The characterBackendId to be updated
let characterBackendDTO = new freechat-sdk.CharacterBackendDTO(); // CharacterBackendDTO | The character backend configuration to be updated
apiInstance.updateCharacterBackend(characterBackendId, characterBackendDTO).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **String**| The characterBackendId to be updated | 
 **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend configuration to be updated | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## uploadCharacterAvatar

> String uploadCharacterAvatar(file)

Upload Character Avatar

Upload an avatar of the character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let file = "/path/to/file"; // File | Character avatar
apiInstance.uploadCharacterAvatar(file).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **File**| Character avatar | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain


## uploadCharacterPicture

> String uploadCharacterPicture(file)

Upload Character Picture

Upload a picture of the character.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.CharacterApi();
let file = "/path/to/file"; // File | Character picture
apiInstance.uploadCharacterPicture(file).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **File**| Character picture | 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

