# freechat_sdk.SkillApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**confirm_skill**](SkillApi.md#confirm_skill) | **POST** /api/v2/skill/install/{draftId} | Confirm Skill Installation
[**delete_skill**](SkillApi.md#delete_skill) | **DELETE** /api/v2/skill/{skillId} | Delete My Skill
[**get_skill**](SkillApi.md#get_skill) | **GET** /api/v2/skill/{skillId} | Get My Skill
[**get_skill_allowed_hosts**](SkillApi.md#get_skill_allowed_hosts) | **GET** /api/v2/skill/sources/allowed-hosts | Get Allowed Skill Source Hosts
[**list_skills**](SkillApi.md#list_skills) | **GET** /api/v2/skill | List My Skills
[**prepare_skill**](SkillApi.md#prepare_skill) | **POST** /api/v2/skill/install | Prepare Skill Installation
[**update_skill**](SkillApi.md#update_skill) | **PUT** /api/v2/skill/{skillId} | Update My Skill


# **confirm_skill**
> SkillDetailsDTO confirm_skill(draft_id, skill_confirm_dto)

Confirm Skill Installation

Publish an unexpired reviewed draft belonging to the authenticated user. Confirmation is single-use and bound to its exact content digest.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.skill_confirm_dto import SkillConfirmDTO
from freechat_sdk.models.skill_details_dto import SkillDetailsDTO
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
    api_instance = freechat_sdk.SkillApi(api_client)
    draft_id = 'draft_id_example' # str | 
    skill_confirm_dto = freechat_sdk.SkillConfirmDTO() # SkillConfirmDTO | 

    try:
        # Confirm Skill Installation
        api_response = api_instance.confirm_skill(draft_id, skill_confirm_dto)
        print("The response of SkillApi->confirm_skill:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->confirm_skill: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **draft_id** | **str**|  |
 **skill_confirm_dto** | [**SkillConfirmDTO**](SkillConfirmDTO.md)|  |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

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

# **delete_skill**
> delete_skill(skill_id, expected_revision)

Delete My Skill

Revoke an owned skill at its expected revision; characters cannot continue loading it.

### Example

* Bearer Authentication (bearerAuth):

```python
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
    api_instance = freechat_sdk.SkillApi(api_client)
    skill_id = 'skill_id_example' # str | 
    expected_revision = 'expected_revision_example' # str | 

    try:
        # Delete My Skill
        api_instance.delete_skill(skill_id, expected_revision)
    except Exception as e:
        print("Exception when calling SkillApi->delete_skill: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skill_id** | **str**|  |
 **expected_revision** | **str**|  |

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
**204** | No Content |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_skill**
> SkillDetailsDTO get_skill(skill_id)

Get My Skill

Read a complete skill document set owned by the authenticated user.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.skill_details_dto import SkillDetailsDTO
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
    api_instance = freechat_sdk.SkillApi(api_client)
    skill_id = 'skill_id_example' # str | 

    try:
        # Get My Skill
        api_response = api_instance.get_skill(skill_id)
        print("The response of SkillApi->get_skill:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->get_skill: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skill_id** | **str**|  |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

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

# **get_skill_allowed_hosts**
> List[str] get_skill_allowed_hosts()

Get Allowed Skill Source Hosts

Return the effective source hostname allowlist for skill imports. An empty list disables URL imports. GitHub repository and ClawHub skill links use fixed provider APIs to import a pinned document bundle; other allowed sources must return raw text or Markdown.

### Example

* Bearer Authentication (bearerAuth):

```python
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
    api_instance = freechat_sdk.SkillApi(api_client)

    try:
        # Get Allowed Skill Source Hosts
        api_response = api_instance.get_skill_allowed_hosts()
        print("The response of SkillApi->get_skill_allowed_hosts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->get_skill_allowed_hosts: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

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

# **list_skills**
> List[SkillSummaryDTO] list_skills()

List My Skills

List only the authenticated user's installed skills.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.skill_summary_dto import SkillSummaryDTO
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
    api_instance = freechat_sdk.SkillApi(api_client)

    try:
        # List My Skills
        api_response = api_instance.list_skills()
        print("The response of SkillApi->list_skills:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->list_skills: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[SkillSummaryDTO]**](SkillSummaryDTO.md)

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

# **prepare_skill**
> SkillDraftDTO prepare_skill(skill_install_dto)

Prepare Skill Installation

Analyze an approved source URL with the server-configured LLM, import a pinned GitHub or ClawHub skill bundle or a raw document, and validate and review every file. Also accepts pasted/uploaded text bundles. Publication requires explicit confirmation of the returned digest.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.skill_draft_dto import SkillDraftDTO
from freechat_sdk.models.skill_install_dto import SkillInstallDTO
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
    api_instance = freechat_sdk.SkillApi(api_client)
    skill_install_dto = freechat_sdk.SkillInstallDTO() # SkillInstallDTO | 

    try:
        # Prepare Skill Installation
        api_response = api_instance.prepare_skill(skill_install_dto)
        print("The response of SkillApi->prepare_skill:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->prepare_skill: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skill_install_dto** | [**SkillInstallDTO**](SkillInstallDTO.md)|  |

### Return type

[**SkillDraftDTO**](SkillDraftDTO.md)

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

# **update_skill**
> SkillDetailsDTO update_skill(skill_id, skill_update_dto)

Update My Skill

Rebuild, validate and review the complete document set before atomically replacing the expected revision. Rejection leaves the installed revision unchanged.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.skill_details_dto import SkillDetailsDTO
from freechat_sdk.models.skill_update_dto import SkillUpdateDTO
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
    api_instance = freechat_sdk.SkillApi(api_client)
    skill_id = 'skill_id_example' # str | 
    skill_update_dto = freechat_sdk.SkillUpdateDTO() # SkillUpdateDTO | 

    try:
        # Update My Skill
        api_response = api_instance.update_skill(skill_id, skill_update_dto)
        print("The response of SkillApi->update_skill:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SkillApi->update_skill: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skill_id** | **str**|  |
 **skill_update_dto** | [**SkillUpdateDTO**](SkillUpdateDTO.md)|  |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

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

