# freechat_sdk.CharacterApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**add_character_backend**](CharacterApi.md#add_character_backend) | **POST** /api/v1/character/backend/{characterId} | Add Character Backend
[**batch_search_character_details**](CharacterApi.md#batch_search_character_details) | **POST** /api/v1/character/batch/details/search | Batch Search Character Details
[**batch_search_character_summary**](CharacterApi.md#batch_search_character_summary) | **POST** /api/v1/character/batch/search | Batch Search Character Summaries
[**clone_character**](CharacterApi.md#clone_character) | **POST** /api/v1/character/clone/{characterId} | Clone Character
[**count_characters**](CharacterApi.md#count_characters) | **POST** /api/v1/character/count | Calculate Number of Characters
[**create_character**](CharacterApi.md#create_character) | **POST** /api/v1/character | Create Character
[**delete_character**](CharacterApi.md#delete_character) | **DELETE** /api/v1/character/{characterId} | Delete Character
[**delete_character_by_name**](CharacterApi.md#delete_character_by_name) | **DELETE** /api/v1/character/name/{name} | Delete Character by Name
[**delete_chat**](CharacterApi.md#delete_chat) | **DELETE** /api/v1/character/chat/{chatId} | Delete Chat Session
[**get_character_details**](CharacterApi.md#get_character_details) | **GET** /api/v1/character/details/{characterId} | Get Character Details
[**get_character_latest_id_by_name**](CharacterApi.md#get_character_latest_id_by_name) | **POST** /api/v1/character/latest/{name} | Get Latest Character Id by Name
[**get_character_summary**](CharacterApi.md#get_character_summary) | **GET** /api/v1/character/summary/{characterId} | Get Character Summary
[**get_default_character_backend**](CharacterApi.md#get_default_character_backend) | **GET** /api/v1/character/backend/default/{characterId} | Get Default Character Backend
[**list_character_backend_ids**](CharacterApi.md#list_character_backend_ids) | **GET** /api/v1/character/backends/{characterId} | List Character Backend ids
[**list_character_versions_by_name**](CharacterApi.md#list_character_versions_by_name) | **POST** /api/v1/character/versions/{name} | List Versions by Character Name
[**list_messages**](CharacterApi.md#list_messages) | **GET** /api/v1/character/chat/messages/{chatId}/{limit} | List Chat Messages
[**list_messages1**](CharacterApi.md#list_messages1) | **GET** /api/v1/character/chat/messages/{chatId}/{limit}/{offset} | List Chat Messages
[**list_messages2**](CharacterApi.md#list_messages2) | **GET** /api/v1/character/chat/messages/{chatId} | List Chat Messages
[**publish_character**](CharacterApi.md#publish_character) | **POST** /api/v1/character/publish/{characterId} | Publish Character
[**publish_character1**](CharacterApi.md#publish_character1) | **POST** /api/v1/character/publish/{characterId}/{visibility} | Publish Character
[**remove_character_backend**](CharacterApi.md#remove_character_backend) | **DELETE** /api/v1/character/backend/{characterBackendId} | Remove Character Backend
[**search_character_details**](CharacterApi.md#search_character_details) | **POST** /api/v1/character/details/search | Search Character Details
[**search_character_summary**](CharacterApi.md#search_character_summary) | **POST** /api/v1/character/search | Search Character Summary
[**send_message**](CharacterApi.md#send_message) | **POST** /api/v1/character/chat/send/{chatId} | Send Chat Message
[**set_default_character_backend**](CharacterApi.md#set_default_character_backend) | **PUT** /api/v1/character/backend/default/{characterBackendId} | Set Default Character Backend
[**start_chat**](CharacterApi.md#start_chat) | **POST** /api/v1/character/chat | Start Chat Session
[**stream_send_message**](CharacterApi.md#stream_send_message) | **POST** /api/v1/character/chat/send/stream/{chatId} | Send Chat Message by Streaming Back
[**update_character**](CharacterApi.md#update_character) | **PUT** /api/v1/character/{characterId} | Update Character
[**update_character_backend**](CharacterApi.md#update_character_backend) | **PUT** /api/v1/character/backend/{characterBackendId} | Update Character Backend
[**upload_character_avatar**](CharacterApi.md#upload_character_avatar) | **POST** /api/v1/character/avatar | Upload Character Avatar
[**upload_character_picture**](CharacterApi.md#upload_character_picture) | **POST** /api/v1/character/picture | Upload Character Picture


# **add_character_backend**
> str add_character_backend(character_id, character_backend_dto)

Add Character Backend

Add a backend configuration for a character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be added a backend
    character_backend_dto = freechat_sdk.CharacterBackendDTO() # CharacterBackendDTO | The character backend to be added

    try:
        # Add Character Backend
        api_response = api_instance.add_character_backend(character_id, character_backend_dto)
        print("The response of CharacterApi->add_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->add_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **str**| The characterId to be added a backend | 
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

Batch call shortcut for /api/v1/character/details/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

Batch call shortcut for /api/v1/character/search.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
> str clone_character(character_id)

Clone Character

Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The referenced characterId

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
 **character_id** | **str**| The referenced characterId | 

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

# **count_characters**
> int count_characters(character_query_dto)

Calculate Number of Characters

Calculate the number of characters according to the specified query conditions.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **create_character**
> str create_character(character_create_dto)

Create Character

Create a character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_create_dto import CharacterCreateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **delete_character**
> bool delete_character(character_id)

Delete Character

Delete character. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be deleted

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
 **character_id** | **str**| The characterId to be deleted | 

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
> List[str] delete_character_by_name(name)

Delete Character by Name

Delete character by name. return the list of successfully deleted characterIds.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **delete_chat**
> bool delete_chat(chat_id)

Delete Chat Session

Delete the chat session.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier

    try:
        # Delete Chat Session
        api_response = api_instance.delete_chat(chat_id)
        print("The response of CharacterApi->delete_chat:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->delete_chat: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 

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

# **get_character_details**
> CharacterDetailsDTO get_character_details(character_id)

Get Character Details

Get character detailed information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | CharacterId to be obtained

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
 **character_id** | **str**| CharacterId to be obtained | 

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
> str get_character_latest_id_by_name(name)

Get Latest Character Id by Name

Get latest characterId by character name.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **get_character_summary**
> CharacterSummaryDTO get_character_summary(character_id)

Get Character Summary

Get character summary information.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | CharacterId to be obtained

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
 **character_id** | **str**| CharacterId to be obtained | 

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
> CharacterBackendDetailsDTO get_default_character_backend(character_id)

Get Default Character Backend

Get the default backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be queried

    try:
        # Get Default Character Backend
        api_response = api_instance.get_default_character_backend(character_id)
        print("The response of CharacterApi->get_default_character_backend:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->get_default_character_backend: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **str**| The characterId to be queried | 

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

# **list_character_backend_ids**
> List[str] list_character_backend_ids(character_id)

List Character Backend ids

List Character Backend identifiers.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be queried

    try:
        # List Character Backend ids
        api_response = api_instance.list_character_backend_ids(character_id)
        print("The response of CharacterApi->list_character_backend_ids:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_character_backend_ids: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **str**| The characterId to be queried | 

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
import time
import os
import freechat_sdk
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **list_messages**
> List[ChatMessageDTO] list_messages(chat_id, limit)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier
    limit = 56 # int | Messages limit

    try:
        # List Chat Messages
        api_response = api_instance.list_messages(chat_id, limit)
        print("The response of CharacterApi->list_messages:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_messages: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **limit** | **int**| Messages limit | 

### Return type

[**List[ChatMessageDTO]**](ChatMessageDTO.md)

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

# **list_messages1**
> List[ChatMessageDTO] list_messages1(chat_id, limit, offset)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier
    limit = 56 # int | Messages limit
    offset = 56 # int | Messages offset (from new to old)

    try:
        # List Chat Messages
        api_response = api_instance.list_messages1(chat_id, limit, offset)
        print("The response of CharacterApi->list_messages1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_messages1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **limit** | **int**| Messages limit | 
 **offset** | **int**| Messages offset (from new to old) | 

### Return type

[**List[ChatMessageDTO]**](ChatMessageDTO.md)

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

# **list_messages2**
> List[ChatMessageDTO] list_messages2(chat_id)

List Chat Messages

List messages of a chat.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_message_dto import ChatMessageDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier

    try:
        # List Chat Messages
        api_response = api_instance.list_messages2(chat_id)
        print("The response of CharacterApi->list_messages2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->list_messages2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 

### Return type

[**List[ChatMessageDTO]**](ChatMessageDTO.md)

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

# **publish_character**
> str publish_character(character_id)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be published

    try:
        # Publish Character
        api_response = api_instance.publish_character(character_id)
        print("The response of CharacterApi->publish_character:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->publish_character: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **str**| The characterId to be published | 

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

# **publish_character1**
> str publish_character1(character_id, visibility)

Publish Character

Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be published
    visibility = 'visibility_example' # str | Visibility: public | private | ...

    try:
        # Publish Character
        api_response = api_instance.publish_character1(character_id, visibility)
        print("The response of CharacterApi->publish_character1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->publish_character1: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **character_id** | **str**| The characterId to be published | 
 **visibility** | **str**| Visibility: public | private | ... | 

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

# **remove_character_backend**
> bool remove_character_backend(character_backend_id)

Remove Character Backend

Remove a backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

Same as /api/v1/character/search, but returns detailed information of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
import time
import os
import freechat_sdk
from freechat_sdk.models.character_query_dto import CharacterQueryDTO
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **send_message**
> LlmResultDTO send_message(chat_id, chat_content_dto)

Send Chat Message

Send a chat message to character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_content_dto import ChatContentDTO
from freechat_sdk.models.llm_result_dto import LlmResultDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier
    chat_content_dto = freechat_sdk.ChatContentDTO() # ChatContentDTO | Chat content

    try:
        # Send Chat Message
        api_response = api_instance.send_message(chat_id, chat_content_dto)
        print("The response of CharacterApi->send_message:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->send_message: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **chat_content_dto** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_default_character_backend**
> bool set_default_character_backend(character_backend_id)

Set Default Character Backend

Set the default backend configuration.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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

# **start_chat**
> str start_chat(chat_create_dto)

Start Chat Session

Start a chat session.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_create_dto import ChatCreateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_create_dto = freechat_sdk.ChatCreateDTO() # ChatCreateDTO | Parameters for starting a chat session

    try:
        # Start Chat Session
        api_response = api_instance.start_chat(chat_create_dto)
        print("The response of CharacterApi->start_chat:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->start_chat: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_create_dto** | [**ChatCreateDTO**](ChatCreateDTO.md)| Parameters for starting a chat session | 

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

# **stream_send_message**
> SseEmitter stream_send_message(chat_id, chat_content_dto)

Send Chat Message by Streaming Back

Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.chat_content_dto import ChatContentDTO
from freechat_sdk.models.sse_emitter import SseEmitter
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    chat_id = 'chat_id_example' # str | Chat session identifier
    chat_content_dto = freechat_sdk.ChatContentDTO() # ChatContentDTO | Chat content

    try:
        # Send Chat Message by Streaming Back
        api_response = api_instance.stream_send_message(chat_id, chat_content_dto)
        print("The response of CharacterApi->stream_send_message:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->stream_send_message: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chat_id** | **str**| Chat session identifier | 
 **chat_content_dto** | [**ChatContentDTO**](ChatContentDTO.md)| Chat content | 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_character**
> bool update_character(character_id, character_update_dto)

Update Character

Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    character_id = 'character_id_example' # str | The characterId to be updated
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
 **character_id** | **str**| The characterId to be updated | 
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
import time
import os
import freechat_sdk
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
> str upload_character_avatar(file)

Upload Character Avatar

Upload an avatar of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
        # Upload Character Avatar
        api_response = api_instance.upload_character_avatar(file)
        print("The response of CharacterApi->upload_character_avatar:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->upload_character_avatar: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
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

# **upload_character_picture**
> str upload_character_picture(file)

Upload Character Picture

Upload a picture of the character.

### Example

* Bearer Authentication (bearerAuth):

```python
import time
import os
import freechat_sdk
from freechat_sdk.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://127.0.0.1:8080
# See configuration.py for a list of all supported configuration parameters.
configuration = freechat_sdk.Configuration(
    host = "http://127.0.0.1:8080"
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
    file = None # bytearray | Character picture

    try:
        # Upload Character Picture
        api_response = api_instance.upload_character_picture(file)
        print("The response of CharacterApi->upload_character_picture:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling CharacterApi->upload_character_picture: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
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

