# CharacterApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCharacterBackend**](CharacterApi.md#addCharacterBackend) | **POST** /api/v2/character/backend/{characterUid} | Add Character Backend
[**batchSearchCharacterDetails**](CharacterApi.md#batchSearchCharacterDetails) | **POST** /api/v2/character/batch/details/search | Batch Search Character Details
[**batchSearchCharacterSummary**](CharacterApi.md#batchSearchCharacterSummary) | **POST** /api/v2/character/batch/search | Batch Search Character Summaries
[**cloneCharacter**](CharacterApi.md#cloneCharacter) | **POST** /api/v2/character/clone/{characterId} | Clone Character
[**countCharacters**](CharacterApi.md#countCharacters) | **POST** /api/v2/character/count | Calculate Number of Characters
[**countPublicCharacters**](CharacterApi.md#countPublicCharacters) | **POST** /api/v2/public/character/count | Calculate Number of Public Characters
[**createCharacter**](CharacterApi.md#createCharacter) | **POST** /api/v2/character | Create Character
[**deleteCharacter**](CharacterApi.md#deleteCharacter) | **DELETE** /api/v2/character/{characterId} | Delete Character
[**deleteCharacterByName**](CharacterApi.md#deleteCharacterByName) | **DELETE** /api/v2/character/name/{name} | Delete Character by Name
[**deleteCharacterByUid**](CharacterApi.md#deleteCharacterByUid) | **DELETE** /api/v2/character/uid/{characterUid} | Delete Character by Uid
[**deleteCharacterDocument**](CharacterApi.md#deleteCharacterDocument) | **DELETE** /api/v2/character/document/{key} | Delete Character Document
[**deleteCharacterPicture**](CharacterApi.md#deleteCharacterPicture) | **DELETE** /api/v2/character/picture/{key} | Delete Character Picture
[**deleteCharacterVideo**](CharacterApi.md#deleteCharacterVideo) | **DELETE** /api/v2/character/video/{key} | Delete Character Video
[**deleteCharacterVoice**](CharacterApi.md#deleteCharacterVoice) | **DELETE** /api/v2/character/voice/{characterBackendId}/{key} | Delete Character Voice
[**existsCharacterName**](CharacterApi.md#existsCharacterName) | **GET** /api/v2/character/exists/name/{name} | Check If Character Name Exists
[**exportCharacter**](CharacterApi.md#exportCharacter) | **GET** /api/v2/character/export/{characterId} | Export Character Configuration
[**getCharacterDetails**](CharacterApi.md#getCharacterDetails) | **GET** /api/v2/character/details/{characterId} | Get Character Details
[**getCharacterLatestIdByName**](CharacterApi.md#getCharacterLatestIdByName) | **POST** /api/v2/character/latest/{name} | Get Latest Character Id by Name
[**getCharacterSummary**](CharacterApi.md#getCharacterSummary) | **GET** /api/v2/character/summary/{characterId} | Get Character Summary
[**getDefaultCharacterBackend**](CharacterApi.md#getDefaultCharacterBackend) | **GET** /api/v2/character/backend/default/{characterUid} | Get Default Character Backend
[**importCharacter**](CharacterApi.md#importCharacter) | **POST** /api/v2/character/import | Import Character Configuration
[**listCharacterBackendIds**](CharacterApi.md#listCharacterBackendIds) | **GET** /api/v2/character/backend/ids/{characterUid} | List Character Backend ids
[**listCharacterBackends**](CharacterApi.md#listCharacterBackends) | **GET** /api/v2/character/backends/{characterUid} | List Character Backends
[**listCharacterDocuments**](CharacterApi.md#listCharacterDocuments) | **GET** /api/v2/character/documents/{characterUid} | List Character Documents
[**listCharacterPictures**](CharacterApi.md#listCharacterPictures) | **GET** /api/v2/character/pictures/{characterUid} | List Character Pictures
[**listCharacterVersionsByName**](CharacterApi.md#listCharacterVersionsByName) | **POST** /api/v2/character/versions/{name} | List Versions by Character Name
[**listCharacterVideos**](CharacterApi.md#listCharacterVideos) | **GET** /api/v2/character/videos/{characterUid} | List Character Videos
[**listCharacterVoices**](CharacterApi.md#listCharacterVoices) | **GET** /api/v2/character/voices/{characterBackendId} | List Character Voices
[**newCharacterName**](CharacterApi.md#newCharacterName) | **GET** /api/v2/character/create/name/{desired} | Create New Character Name
[**publishCharacter**](CharacterApi.md#publishCharacter) | **POST** /api/v2/character/publish/{characterId}/{visibility} | Publish Character
[**publishCharacter1**](CharacterApi.md#publishCharacter1) | **POST** /api/v2/character/publish/{characterId} | Publish Character
[**removeCharacterBackend**](CharacterApi.md#removeCharacterBackend) | **DELETE** /api/v2/character/backend/{characterBackendId} | Remove Character Backend
[**searchCharacterDetails**](CharacterApi.md#searchCharacterDetails) | **POST** /api/v2/character/details/search | Search Character Details
[**searchCharacterSummary**](CharacterApi.md#searchCharacterSummary) | **POST** /api/v2/character/search | Search Character Summary
[**searchPublicCharacterSummary**](CharacterApi.md#searchPublicCharacterSummary) | **POST** /api/v2/public/character/search | Search Public Character Summary
[**setDefaultCharacterBackend**](CharacterApi.md#setDefaultCharacterBackend) | **PUT** /api/v2/character/backend/default/{characterBackendId} | Set Default Character Backend
[**updateCharacter**](CharacterApi.md#updateCharacter) | **PUT** /api/v2/character/{characterId} | Update Character
[**updateCharacterBackend**](CharacterApi.md#updateCharacterBackend) | **PUT** /api/v2/character/backend/{characterBackendId} | Update Character Backend
[**uploadCharacterAvatar**](CharacterApi.md#uploadCharacterAvatar) | **POST** /api/v2/character/avatar/{characterUid} | Upload Character Avatar
[**uploadCharacterDocument**](CharacterApi.md#uploadCharacterDocument) | **POST** /api/v2/character/document/{characterUid} | Upload Character Document
[**uploadCharacterPicture**](CharacterApi.md#uploadCharacterPicture) | **POST** /api/v2/character/picture/{characterUid} | Upload Character Picture
[**uploadCharacterVideo**](CharacterApi.md#uploadCharacterVideo) | **POST** /api/v2/character/video/{characterUid} | Upload Character Video
[**uploadCharacterVoice**](CharacterApi.md#uploadCharacterVoice) | **POST** /api/v2/character/voice/{characterBackendId} | Upload Character Voice



## addCharacterBackend

Add Character Backend

Add a backend configuration for a character.

### Example

```bash
freechat addCharacterBackend characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be added a backend | [default to null]
 **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md) | The character backend to be added |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchCharacterDetails

Batch Search Character Details

Batch call shortcut for /api/v2/character/details/search.

### Example

```bash
freechat batchSearchCharacterDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**array[CharacterQueryDTO]**](CharacterQueryDTO.md) | Query conditions |

### Return type

**array[array[CharacterDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchCharacterSummary

Batch Search Character Summaries

Batch call shortcut for /api/v2/character/search.

### Example

```bash
freechat batchSearchCharacterSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**array[CharacterQueryDTO]**](CharacterQueryDTO.md) | Query conditions |

### Return type

**array[array[CharacterSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## cloneCharacter

Clone Character

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different:
- Version number is 1
- Visibility is private
- The parent character is the source characterId
- The creation time is the current moment.
- All statistical indicators are zeroed.

Return the new characterId.

### Example

```bash
freechat cloneCharacter characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | The referenced characterId | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countCharacters

Calculate Number of Characters

Calculate the number of characters according to the specified query conditions.

### Example

```bash
freechat countCharacters
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countPublicCharacters

Calculate Number of Public Characters

Calculate the number of characters according to the specified query conditions.

### Example

```bash
freechat countPublicCharacters
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createCharacter

Create Character

Create a character.

### Example

```bash
freechat createCharacter
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterCreateDTO** | [**CharacterCreateDTO**](CharacterCreateDTO.md) | Information of the character to be created |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacter

Delete Character

Delete character. Returns success or failure.

### Example

```bash
freechat deleteCharacter characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | The characterId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterByName

Delete Character by Name

Delete character by name. return the list of successfully deleted characterIds.

### Example

```bash
freechat deleteCharacterByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | The character name to be deleted | [default to null]

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterByUid

Delete Character by Uid

Delete character by uid. return the list of successfully deleted characterIds.

### Example

```bash
freechat deleteCharacterByUid characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The character uid to be deleted | [default to null]

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterDocument

Delete Character Document

Delete a document of the character by key.

### Example

```bash
freechat deleteCharacterDocument key=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **string** | Document key | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterPicture

Delete Character Picture

Delete a picture of the character by key.

### Example

```bash
freechat deleteCharacterPicture key=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **string** | Image key | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterVideo

Delete Character Video

Delete a video of the character by key.

### Example

```bash
freechat deleteCharacterVideo key=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **string** | Video key | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteCharacterVoice

Delete Character Voice

Delete a voice of the character by key.

### Example

```bash
freechat deleteCharacterVoice characterBackendId=value key=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId | [default to null]
 **key** | **string** | Voice key | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## existsCharacterName

Check If Character Name Exists

Check if the character name already exists.

### Example

```bash
freechat existsCharacterName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Name | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## exportCharacter

Export Character Configuration

Export character configuration in tar.gz format, including settings, documents and pictures.

### Example

```bash
freechat exportCharacter characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | Character identifier | [default to null]

### Return type

(empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getCharacterDetails

Get Character Details

Get character detailed information.

### Example

```bash
freechat getCharacterDetails characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | CharacterId to be obtained | [default to null]

### Return type

[**CharacterDetailsDTO**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getCharacterLatestIdByName

Get Latest Character Id by Name

Get latest characterId by character name.

### Example

```bash
freechat getCharacterLatestIdByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Character name | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getCharacterSummary

Get Character Summary

Get character summary information.

### Example

```bash
freechat getCharacterSummary characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | CharacterId to be obtained | [default to null]

### Return type

[**CharacterSummaryDTO**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getDefaultCharacterBackend

Get Default Character Backend

Get the default backend configuration.

### Example

```bash
freechat getDefaultCharacterBackend characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be queried | [default to null]

### Return type

[**CharacterBackendDetailsDTO**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## importCharacter

Import Character Configuration

Import character configuration from a tar.gz file.

### Example

```bash
freechat importCharacter
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **binary** | Character avatar | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterBackendIds

List Character Backend ids

List character backend identifiers.

### Example

```bash
freechat listCharacterBackendIds characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be queried | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterBackends

List Character Backends

List character backends.

### Example

```bash
freechat listCharacterBackends characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | The characterUid to be queried | [default to null]

### Return type

[**array[CharacterBackendDetailsDTO]**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterDocuments

List Character Documents

List documents of the character.

### Example

```bash
freechat listCharacterDocuments characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterPictures

List Character Pictures

List pictures of the character.

### Example

```bash
freechat listCharacterPictures characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterVersionsByName

List Versions by Character Name

List the versions and corresponding characterIds by character name.

### Example

```bash
freechat listCharacterVersionsByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Character name | [default to null]

### Return type

[**array[CharacterItemForNameDTO]**](CharacterItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterVideos

List Character Videos

List videos of the character.

### Example

```bash
freechat listCharacterVideos characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listCharacterVoices

List Character Voices

List voices of the character.

### Example

```bash
freechat listCharacterVoices characterBackendId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## newCharacterName

Create New Character Name

Create a new character name starting with a desired name.

### Example

```bash
freechat newCharacterName desired=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **desired** | **string** | Desired name | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## publishCharacter

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

```bash
freechat publishCharacter characterId=value visibility=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | The characterId to be published | [default to null]
 **visibility** | **string** | Visibility: public | private | ... | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## publishCharacter1

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

```bash
freechat publishCharacter1 characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | The characterId to be published | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## removeCharacterBackend

Remove Character Backend

Remove a backend configuration.

### Example

```bash
freechat removeCharacterBackend characterBackendId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId to be removed | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchCharacterDetails

Search Character Details

Same as /api/v2/character/search, but returns detailed information of the character.

### Example

```bash
freechat searchCharacterDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md) | Query conditions |

### Return type

[**array[CharacterDetailsDTO]**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchCharacterSummary

Search Character Summary

Search characters:
- Specifiable query fields, and relationship:
  - Scope: private, public_org or public. Private can only search this account.
  - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
  - Tags: exact match (support and, or logic).
  - Name: left match.
  - Language, exact match.
  - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the character summary content.
- Support pagination.

### Example

```bash
freechat searchCharacterSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md) | Query conditions |

### Return type

[**array[CharacterSummaryDTO]**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPublicCharacterSummary

Search Public Character Summary

Search characters:
- Specifiable query fields, and relationship:
  - Scope: public(fixed).
  - Username: exact match. If not specified, search all users.
  - Tags: exact match (support and, or logic).
  - Name: left match.
  - Language, exact match.
  - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the character summary content.
- Support pagination.

### Example

```bash
freechat searchPublicCharacterSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterQueryDTO** | [**CharacterQueryDTO**](CharacterQueryDTO.md) | Query conditions |

### Return type

[**array[CharacterSummaryDTO]**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## setDefaultCharacterBackend

Set Default Character Backend

Set the default backend configuration.

### Example

```bash
freechat setDefaultCharacterBackend characterBackendId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId to be set to default | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateCharacter

Update Character

Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.

### Example

```bash
freechat updateCharacter characterId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterId** | **integer** | The characterId to be updated | [default to null]
 **characterUpdateDTO** | [**CharacterUpdateDTO**](CharacterUpdateDTO.md) | The character information to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateCharacterBackend

Update Character Backend

Update a backend configuration.

### Example

```bash
freechat updateCharacterBackend characterBackendId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId to be updated | [default to null]
 **characterBackendDTO** | [**CharacterBackendDTO**](CharacterBackendDTO.md) | The character backend configuration to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadCharacterAvatar

Upload Character Avatar

Upload an avatar of the character.

### Example

```bash
freechat uploadCharacterAvatar characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]
 **file** | **binary** | Character avatar | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadCharacterDocument

Upload Character Document

Upload a document of the character.

### Example

```bash
freechat uploadCharacterDocument characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]
 **file** | **binary** | Character document | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadCharacterPicture

Upload Character Picture

Upload a picture of the character.

### Example

```bash
freechat uploadCharacterPicture characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]
 **file** | **binary** | Character picture | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadCharacterVideo

Upload Character Video

Upload a video of the character.

### Example

```bash
freechat uploadCharacterVideo characterUid=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterUid** | **string** | Character unique identifier | [default to null]
 **file** | **binary** | Character video | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## uploadCharacterVoice

Upload Character Voice

Upload a voice of the character.

### Example

```bash
freechat uploadCharacterVoice characterBackendId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **characterBackendId** | **string** | The characterBackendId | [default to null]
 **file** | **binary** | Character voice | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

