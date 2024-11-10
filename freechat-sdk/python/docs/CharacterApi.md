# freechat_sdk.CharacterApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_character_backend**](CharacterApi.md#add_character_backend) | **POST** /api/v2/character/backend/{characterUid} | Add Character Backend
[**batch_search_character_details**](CharacterApi.md#batch_search_character_details) | **POST** /api/v2/character/batch/details/search | Batch Search Character Details
[**batch_search_character_summary**](CharacterApi.md#batch_search_character_summary) | **POST** /api/v2/character/batch/search | Batch Search Character Summaries
[**clone_character**](CharacterApi.md#clone_character) | **POST** /api/v2/character/clone/{characterId} | Clone Character
[**count_characters**](CharacterApi.md#count_characters) | **POST** /api/v2/character/count | Calculate Number of Characters
[**count_public_characters**](CharacterApi.md#count_public_characters) | **POST** /api/v2/public/character/count | Calculate Number of Public Characters
[**create_character**](CharacterApi.md#create_character) | **POST** /api/v2/character | Create Character
[**delete_character**](CharacterApi.md#delete_character) | **DELETE** /api/v2/character/{characterId} | Delete Character
[**delete_character_by_name**](CharacterApi.md#delete_character_by_name) | **DELETE** /api/v2/character/name/{name} | Delete Character by Name
[**delete_character_by_uid**](CharacterApi.md#delete_character_by_uid) | **DELETE** /api/v2/character/uid/{characterUid} | Delete Character by Uid
[**delete_character_document**](CharacterApi.md#delete_character_document) | **DELETE** /api/v2/character/document/{key} | Delete Character Document
[**delete_character_picture**](CharacterApi.md#delete_character_picture) | **DELETE** /api/v2/character/picture/{key} | Delete Character Picture
[**exists_character_name**](CharacterApi.md#exists_character_name) | **GET** /api/v2/character/exists/name/{name} | Check If Character Name Exists
[**export_character**](CharacterApi.md#export_character) | **GET** /api/v2/character/export/{characterId} | Export Character Configuration
[**get_character_details**](CharacterApi.md#get_character_details) | **GET** /api/v2/character/details/{characterId} | Get Character Details
[**get_character_latest_id_by_name**](CharacterApi.md#get_character_latest_id_by_name) | **POST** /api/v2/character/latest/{name} | Get Latest Character Id by Name
[**get_character_summary**](CharacterApi.md#get_character_summary) | **GET** /api/v2/character/summary/{characterId} | Get Character Summary
[**get_default_character_backend**](CharacterApi.md#get_default_character_backend) | **GET** /api/v2/character/backend/default/{characterUid} | Get Default Character Backend
[**import_character**](CharacterApi.md#import_character) | **POST** /api/v2/character/import | Import Character Configuration
[**list_character_backend_ids**](CharacterApi.md#list_character_backend_ids) | **GET** /api/v2/character/backend/ids/{characterUid} | List Character Backend ids
[**list_character_backends**](CharacterApi.md#list_character_backends) | **GET** /api/v2/character/backends/{characterUid} | List Character Backends
[**list_character_documents**](CharacterApi.md#list_character_documents) | **GET** /api/v2/character/documents/{characterUid} | List Character Documents
[**list_character_pictures**](CharacterApi.md#list_character_pictures) | **GET** /api/v2/character/pictures/{characterUid} | List Character Pictures
[**list_character_versions_by_name**](CharacterApi.md#list_character_versions_by_name) | **POST** /api/v2/character/versions/{name} | List Versions by Character Name
[**new_character_name**](CharacterApi.md#new_character_name) | **GET** /api/v2/character/create/name/{desired} | Create New Character Name
[**publish_character**](CharacterApi.md#publish_character) | **POST** /api/v2/character/publish/{characterId}/{visibility} | Publish Character
[**publish_character1**](CharacterApi.md#publish_character1) | **POST** /api/v2/character/publish/{characterId} | Publish Character
[**remove_character_backend**](CharacterApi.md#remove_character_backend) | **DELETE** /api/v2/character/backend/{characterBackendId} | Remove Character Backend
[**search_character_details**](CharacterApi.md#search_character_details) | **POST** /api/v2/character/details/search | Search Character Details
[**search_character_summary**](CharacterApi.md#search_character_summary) | **POST** /api/v2/character/search | Search Character Summary
[**search_public_character_summary**](CharacterApi.md#search_public_character_summary) | **POST** /api/v2/public/character/search | Search Public Character Summary
[**set_default_character_backend**](CharacterApi.md#set_default_character_backend) | **PUT** /api/v2/character/backend/default/{characterBackendId} | Set Default Character Backend
[**update_character**](CharacterApi.md#update_character) | **PUT** /api/v2/character/{characterId} | Update Character
[**update_character_backend**](CharacterApi.md#update_character_backend) | **PUT** /api/v2/character/backend/{characterBackendId} | Update Character Backend
[**upload_character_avatar**](CharacterApi.md#upload_character_avatar) | **POST** /api/v2/character/avatar/{characterUid} | Upload Character Avatar
[**upload_character_document**](CharacterApi.md#upload_character_document) | **POST** /api/v2/character/document/{characterUid} | Upload Character Document
[**upload_character_picture**](CharacterApi.md#upload_character_picture) | **POST** /api/v2/character/picture/{characterUid} | Upload Character Picture


# **add_character_backend**
> str add_character_backend(character_uid, character_backend_dto)

Add Character Backend

Add a backend configuration for a character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | The characterUid to be added a backend
    character_backend_dto = freechat_sdk.CharacterBackendDTO() # CharacterBackendDTO | The character backend to be added

    try:
        # Add Character Backend
        api_response = api_instance.add_character_backend(character_uid, character_backend_dto)
        print("The response of CharacterApi->add_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->add_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| The characterUid to be added a backend | 
 **character_backend_dto** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend to be added | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **batch_search_character_details**
> List[List[CharacterDetailsDTO]] batch_search_character_details(character_query_dto)

Batch Search Character Details

Batch call shortcut for /api/v2/character/details/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[CharacterQueryDTO] | Query conditions

    try:
        # Batch Search Character Details
        api_response = api_instance.batch_search_character_details(character_query_dto)
        print("The response of CharacterApi->batch_search_character_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->batch_search_character_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**List[CharacterQueryDTO]**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**List[List[CharacterDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **batch_search_character_summary**
> List[List[CharacterSummaryDTO]] batch_search_character_summary(character_query_dto)

Batch Search Character Summaries

Batch call shortcut for /api/v2/character/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = [{"where":{"visibility":"public","username":"amin","text":"robot"},"orderBy":["version","modifyTime asc"],"pageNum":1,"pageSize":1},{"where":{"visibility":"private","name":"A Test"}},{"where":{"visibility":"private","tags":["test1"]}},{"where":{"visibility":"public","username":"amin","name":"Second Test","text":"robot","tags":["robot"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1}] # List[CharacterQueryDTO] | Query conditions

    try:
        # Batch Search Character Summaries
        api_response = api_instance.batch_search_character_summary(character_query_dto)
        print("The response of CharacterApi->batch_search_character_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->batch_search_character_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**List[CharacterQueryDTO]**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**List[List[CharacterSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **clone_character**
> int clone_character(character_id)

Clone Character

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | The referenced characterId

    try:
        # Clone Character
        api_response = api_instance.clone_character(character_id)
        print("The response of CharacterApi->clone_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->clone_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The referenced characterId | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **count_characters**
> int count_characters(character_query_dto)

Calculate Number of Characters

Calculate the number of characters according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = freechat_sdk.CharacterQueryDTO() # CharacterQueryDTO | Query conditions

    try:
        # Calculate Number of Characters
        api_response = api_instance.count_characters(character_query_dto)
        print("The response of CharacterApi->count_characters:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->count_characters: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **count_public_characters**
> int count_public_characters(character_query_dto)

Calculate Number of Public Characters

Calculate the number of characters according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = freechat_sdk.CharacterQueryDTO() # CharacterQueryDTO | Query conditions

    try:
        # Calculate Number of Public Characters
        api_response = api_instance.count_public_characters(character_query_dto)
        print("The response of CharacterApi->count_public_characters:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->count_public_characters: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_character**
> int create_character(character_create_dto)

Create Character

Create a character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_create_dto import CharacterCreateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_create_dto = {"name":"A Test Character","description":"A character description","profile":"Hello world. I'm Jack","tags":["test","demo"]} # CharacterCreateDTO | Information of the character to be created

    try:
        # Create Character
        api_response = api_instance.create_character(character_create_dto)
        print("The response of CharacterApi->create_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->create_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_create_dto** | [**CharacterCreateDTO**](CharacterCreateDTO.md)| Information of the character to be created | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_character**
> bool delete_character(character_id)

Delete Character

Delete character. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | The characterId to be deleted

    try:
        # Delete Character
        api_response = api_instance.delete_character(character_id)
        print("The response of CharacterApi->delete_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be deleted | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_character_by_name**
> List[int] delete_character_by_name(name)

Delete Character by Name

Delete character by name. return the list of successfully deleted characterIds.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    name = 'name_example' # str | The character name to be deleted

    try:
        # Delete Character by Name
        api_response = api_instance.delete_character_by_name(name)
        print("The response of CharacterApi->delete_character_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_character_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| The character name to be deleted | 

### Return type

**List[int]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_character_by_uid**
> List[int] delete_character_by_uid(character_uid)

Delete Character by Uid

Delete character by uid. return the list of successfully deleted characterIds.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | The character uid to be deleted

    try:
        # Delete Character by Uid
        api_response = api_instance.delete_character_by_uid(character_uid)
        print("The response of CharacterApi->delete_character_by_uid:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_character_by_uid: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| The character uid to be deleted | 

### Return type

**List[int]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_character_document**
> bool delete_character_document(key)

Delete Character Document

Delete a document of the character by key.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    key = 'key_example' # str | Document key

    try:
        # Delete Character Document
        api_response = api_instance.delete_character_document(key)
        print("The response of CharacterApi->delete_character_document:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_character_document: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **str**| Document key | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_character_picture**
> bool delete_character_picture(key)

Delete Character Picture

Delete a picture of the character by key.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    key = 'key_example' # str | Image key

    try:
        # Delete Character Picture
        api_response = api_instance.delete_character_picture(key)
        print("The response of CharacterApi->delete_character_picture:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_character_picture: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **str**| Image key | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **exists_character_name**
> bool exists_character_name(name)

Check If Character Name Exists

Check if the character name already exists.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    name = 'name_example' # str | Name

    try:
        # Check If Character Name Exists
        api_response = api_instance.exists_character_name(name)
        print("The response of CharacterApi->exists_character_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->exists_character_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Name | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **export_character**
> export_character(character_id)

Export Character Configuration

Export character configuration in tar.gz format, including settings, documents and pictures.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | Character identifier

    try:
        # Export Character Configuration
        api_instance.export_character(character_id)
    except Exception as e:
        print("Exception when calling CharacterApi->export_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| Character identifier | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_character_details**
> CharacterDetailsDTO get_character_details(character_id)

Get Character Details

Get character detailed information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | CharacterId to be obtained

    try:
        # Get Character Details
        api_response = api_instance.get_character_details(character_id)
        print("The response of CharacterApi->get_character_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->get_character_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| CharacterId to be obtained | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_character_latest_id_by_name**
> int get_character_latest_id_by_name(name)

Get Latest Character Id by Name

Get latest characterId by character name.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    name = 'name_example' # str | Character name

    try:
        # Get Latest Character Id by Name
        api_response = api_instance.get_character_latest_id_by_name(name)
        print("The response of CharacterApi->get_character_latest_id_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->get_character_latest_id_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Character name | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_character_summary**
> CharacterSummaryDTO get_character_summary(character_id)

Get Character Summary

Get character summary information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | CharacterId to be obtained

    try:
        # Get Character Summary
        api_response = api_instance.get_character_summary(character_id)
        print("The response of CharacterApi->get_character_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->get_character_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| CharacterId to be obtained | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_default_character_backend**
> CharacterBackendDetailsDTO get_default_character_backend(character_uid)

Get Default Character Backend

Get the default backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | The characterUid to be queried

    try:
        # Get Default Character Backend
        api_response = api_instance.get_default_character_backend(character_uid)
        print("The response of CharacterApi->get_default_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->get_default_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| The characterUid to be queried | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **import_character**
> int import_character(file)

Import Character Configuration

Import character configuration from a tar.gz file.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    file = None # bytearray | Character avatar

    try:
        # Import Character Configuration
        api_response = api_instance.import_character(file)
        print("The response of CharacterApi->import_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->import_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**| Character avatar | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_character_backend_ids**
> List[str] list_character_backend_ids(character_uid)

List Character Backend ids

List character backend identifiers.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | The characterUid to be queried

    try:
        # List Character Backend ids
        api_response = api_instance.list_character_backend_ids(character_uid)
        print("The response of CharacterApi->list_character_backend_ids:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_backend_ids: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| The characterUid to be queried | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_character_backends**
> List[CharacterBackendDetailsDTO] list_character_backends(character_uid)

List Character Backends

List character backends.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | The characterUid to be queried

    try:
        # List Character Backends
        api_response = api_instance.list_character_backends(character_uid)
        print("The response of CharacterApi->list_character_backends:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_backends: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| The characterUid to be queried | 

### Return type

[**List[CharacterBackendDetailsDTO]**](CharacterBackendDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_character_documents**
> List[str] list_character_documents(character_uid)

List Character Documents

List documents of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | Character unique identifier

    try:
        # List Character Documents
        api_response = api_instance.list_character_documents(character_uid)
        print("The response of CharacterApi->list_character_documents:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_documents: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| Character unique identifier | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_character_pictures**
> List[str] list_character_pictures(character_uid)

List Character Pictures

List pictures of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | Character unique identifier

    try:
        # List Character Pictures
        api_response = api_instance.list_character_pictures(character_uid)
        print("The response of CharacterApi->list_character_pictures:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_pictures: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| Character unique identifier | 

### Return type

**List[str]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_character_versions_by_name**
> List[CharacterItemForNameDTO] list_character_versions_by_name(name)

List Versions by Character Name

List the versions and corresponding characterIds by character name.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    name = 'name_example' # str | Character name

    try:
        # List Versions by Character Name
        api_response = api_instance.list_character_versions_by_name(name)
        print("The response of CharacterApi->list_character_versions_by_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_versions_by_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **str**| Character name | 

### Return type

[**List[CharacterItemForNameDTO]**](CharacterItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **new_character_name**
> str new_character_name(desired)

Create New Character Name

Create a new character name starting with a desired name.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    desired = 'desired_example' # str | Desired name

    try:
        # Create New Character Name
        api_response = api_instance.new_character_name(desired)
        print("The response of CharacterApi->new_character_name:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->new_character_name: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **desired** | **str**| Desired name | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **publish_character**
> int publish_character(character_id, visibility)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | The characterId to be published
    visibility = 'visibility_example' # str | Visibility: public | private | ...

    try:
        # Publish Character
        api_response = api_instance.publish_character(character_id, visibility)
        print("The response of CharacterApi->publish_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->publish_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be published | 
 **visibility** | **str**| Visibility: public | private | ... | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **publish_character1**
> int publish_character1(character_id)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | The characterId to be published

    try:
        # Publish Character
        api_response = api_instance.publish_character1(character_id)
        print("The response of CharacterApi->publish_character1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->publish_character1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be published | 

### Return type

**int**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **remove_character_backend**
> bool remove_character_backend(character_backend_id)

Remove Character Backend

Remove a backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_backend_id = 'character_backend_id_example' # str | The characterBackendId to be removed

    try:
        # Remove Character Backend
        api_response = api_instance.remove_character_backend(character_backend_id)
        print("The response of CharacterApi->remove_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->remove_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_backend_id** | **str**| The characterBackendId to be removed | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_character_details**
> List[CharacterDetailsDTO] search_character_details(character_query_dto)

Search Character Details

Same as /api/v2/character/search, but returns detailed information of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # CharacterQueryDTO | Query conditions

    try:
        # Search Character Details
        api_response = api_instance.search_character_details(character_query_dto)
        print("The response of CharacterApi->search_character_details:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->search_character_details: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

[**List[CharacterDetailsDTO]**](CharacterDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_character_summary**
> List[CharacterSummaryDTO] search_character_summary(character_query_dto)

Search Character Summary

Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # CharacterQueryDTO | Query conditions

    try:
        # Search Character Summary
        api_response = api_instance.search_character_summary(character_query_dto)
        print("The response of CharacterApi->search_character_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->search_character_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

[**List[CharacterSummaryDTO]**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **search_public_character_summary**
> List[CharacterSummaryDTO] search_public_character_summary(character_query_dto)

Search Public Character Summary

Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_query_dto = {"where":{"visibility":"public","username":"amin","name":"Second Test","text":"(new)","tags":["demo2"]},"orderBy":["version","modifyTime asc"],"pageNum":0,"pageSize":1} # CharacterQueryDTO | Query conditions

    try:
        # Search Public Character Summary
        api_response = api_instance.search_public_character_summary(character_query_dto)
        print("The response of CharacterApi->search_public_character_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->search_public_character_summary: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_query_dto** | [**CharacterQueryDTO**](CharacterQueryDTO.md)| Query conditions | 

### Return type

[**List[CharacterSummaryDTO]**](CharacterSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_default_character_backend**
> bool set_default_character_backend(character_backend_id)

Set Default Character Backend

Set the default backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_backend_id = 'character_backend_id_example' # str | The characterBackendId to be set to default

    try:
        # Set Default Character Backend
        api_response = api_instance.set_default_character_backend(character_backend_id)
        print("The response of CharacterApi->set_default_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->set_default_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_backend_id** | **str**| The characterBackendId to be set to default | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_character**
> bool update_character(character_id, character_update_dto)

Update Character

Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_id = 56 # int | The characterId to be updated
    character_update_dto = {"version":2,"name":"Second Test Character (New)","visibility":"public","description":"Second character description (new)","profile":"I am Kelvin","tags":["test2","demo2","robot"]} # CharacterUpdateDTO | The character information to be updated

    try:
        # Update Character
        api_response = api_instance.update_character(character_id, character_update_dto)
        print("The response of CharacterApi->update_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->update_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **int**| The characterId to be updated | 
 **character_update_dto** | [**CharacterUpdateDTO**](CharacterUpdateDTO.md)| The character information to be updated | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_character_backend**
> bool update_character_backend(character_backend_id, character_backend_dto)

Update Character Backend

Update a backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_backend_id = 'character_backend_id_example' # str | The characterBackendId to be updated
    character_backend_dto = freechat_sdk.CharacterBackendDTO() # CharacterBackendDTO | The character backend configuration to be updated

    try:
        # Update Character Backend
        api_response = api_instance.update_character_backend(character_backend_id, character_backend_dto)
        print("The response of CharacterApi->update_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->update_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_backend_id** | **str**| The characterBackendId to be updated | 
 **character_backend_dto** | [**CharacterBackendDTO**](CharacterBackendDTO.md)| The character backend configuration to be updated | 

### Return type

**bool**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upload_character_avatar**
> str upload_character_avatar(character_uid, file)

Upload Character Avatar

Upload an avatar of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | Character unique identifier
    file = None # bytearray | Character avatar

    try:
        # Upload Character Avatar
        api_response = api_instance.upload_character_avatar(character_uid, file)
        print("The response of CharacterApi->upload_character_avatar:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->upload_character_avatar: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| Character unique identifier | 
 **file** | **bytearray**| Character avatar | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upload_character_document**
> str upload_character_document(character_uid, file)

Upload Character Document

Upload a document of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | Character unique identifier
    file = None # bytearray | Character document

    try:
        # Upload Character Document
        api_response = api_instance.upload_character_document(character_uid, file)
        print("The response of CharacterApi->upload_character_document:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->upload_character_document: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| Character unique identifier | 
 **file** | **bytearray**| Character document | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upload_character_picture**
> str upload_character_picture(character_uid, file)

Upload Character Picture

Upload a picture of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization: bearerAuth
configuration = freechat_sdk.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with freechat_sdk.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = freechat_sdk.CharacterApi(api_client)
    character_uid = 'character_uid_example' # str | Character unique identifier
    file = None # bytearray | Character picture

    try:
        # Upload Character Picture
        api_response = api_instance.upload_character_picture(character_uid, file)
        print("The response of CharacterApi->upload_character_picture:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->upload_character_picture: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_uid** | **str**| Character unique identifier | 
 **file** | **bytearray**| Character picture | 

### Return type

**str**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

