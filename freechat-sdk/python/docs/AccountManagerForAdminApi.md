# freechat_sdk.AccountManagerForAdminApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_token_for_user**](AccountManagerForAdminApi.md#create_token_for_user) | **POST** /api/v2/admin/token/{username}/{duration} | Create API Token for User.
[**create_user**](AccountManagerForAdminApi.md#create_user) | **POST** /api/v2/admin/user | Create User
[**delete_token_for_user**](AccountManagerForAdminApi.md#delete_token_for_user) | **DELETE** /api/v2/admin/token/{token} | Delete API Token
[**delete_user**](AccountManagerForAdminApi.md#delete_user) | **DELETE** /api/v2/admin/user/{username} | Delete User
[**disable_token_for_user**](AccountManagerForAdminApi.md#disable_token_for_user) | **PUT** /api/v2/admin/token/{token} | Disable API Token
[**get_details_of_user**](AccountManagerForAdminApi.md#get_details_of_user) | **GET** /api/v2/admin/user/{username} | Get User Details
[**get_user_by_token**](AccountManagerForAdminApi.md#get_user_by_token) | **GET** /api/v2/admin/tokenBy/{token} | Get User by API Token
[**list_authorities_of_user**](AccountManagerForAdminApi.md#list_authorities_of_user) | **GET** /api/v2/admin/authority/{username} | List User Permissions
[**list_tokens_of_user**](AccountManagerForAdminApi.md#list_tokens_of_user) | **GET** /api/v2/admin/token/{username} | Get API Token of User
[**list_users**](AccountManagerForAdminApi.md#list_users) | **GET** /api/v2/admin/users/{pageSize}/{pageNum} | List User Information
[**list_users1**](AccountManagerForAdminApi.md#list_users1) | **GET** /api/v2/admin/users | List User Information
[**list_users2**](AccountManagerForAdminApi.md#list_users2) | **GET** /api/v2/admin/users/{pageSize} | List User Information
[**update_authorities_of_user**](AccountManagerForAdminApi.md#update_authorities_of_user) | **PUT** /api/v2/admin/authority/{username} | Update User Permissions
[**update_user**](AccountManagerForAdminApi.md#update_user) | **PUT** /api/v2/admin/user | Update User


# **create_token_for_user**
> str create_token_for_user(username, duration)

Create API Token for User.

Create an API Token for a specified user, valid for duration seconds.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username
    duration = 56 # int | Validity period (seconds)

    try:
        # Create API Token for User.
        api_response = api_instance.create_token_for_user(username, duration)
        print("The response of AccountManagerForAdminApi->create_token_for_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->create_token_for_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 
 **duration** | **int**| Validity period (seconds) | 

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

# **create_user**
> bool create_user(user_full_details_dto)

Create User

Create user.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    user_full_details_dto = {"username":"Jack","password":"jack","nickname":"Jack（测试账号）"} # UserFullDetailsDTO | User information

    try:
        # Create User
        api_response = api_instance.create_user(user_full_details_dto)
        print("The response of AccountManagerForAdminApi->create_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->create_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_full_details_dto** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | 

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

# **delete_token_for_user**
> bool delete_token_for_user(token)

Delete API Token

Delete the specified API Token.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    token = 'token_example' # str | API Token

    try:
        # Delete API Token
        api_response = api_instance.delete_token_for_user(token)
        print("The response of AccountManagerForAdminApi->delete_token_for_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->delete_token_for_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **str**| API Token | 

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

# **delete_user**
> bool delete_user(username)

Delete User

Delete user by username.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username

    try:
        # Delete User
        api_response = api_instance.delete_user(username)
        print("The response of AccountManagerForAdminApi->delete_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->delete_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 

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

# **disable_token_for_user**
> bool disable_token_for_user(token)

Disable API Token

Disable the specified API Token.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    token = 'token_example' # str | API Token

    try:
        # Disable API Token
        api_response = api_instance.disable_token_for_user(token)
        print("The response of AccountManagerForAdminApi->disable_token_for_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->disable_token_for_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **str**| API Token | 

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

# **get_details_of_user**
> UserDetailsDTO get_details_of_user(username)

Get User Details

Return detailed user information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_details_dto import UserDetailsDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username

    try:
        # Get User Details
        api_response = api_instance.get_details_of_user(username)
        print("The response of AccountManagerForAdminApi->get_details_of_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->get_details_of_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

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

# **get_user_by_token**
> UserDetailsDTO get_user_by_token(token)

Get User by API Token

Get the detailed user information corresponding to the API Token.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_details_dto import UserDetailsDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    token = 'token_example' # str | API Token

    try:
        # Get User by API Token
        api_response = api_instance.get_user_by_token(token)
        print("The response of AccountManagerForAdminApi->get_user_by_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->get_user_by_token: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **token** | **str**| API Token | 

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

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

# **list_authorities_of_user**
> List[str] list_authorities_of_user(username)

List User Permissions

List the user's permissions.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username

    try:
        # List User Permissions
        api_response = api_instance.list_authorities_of_user(username)
        print("The response of AccountManagerForAdminApi->list_authorities_of_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->list_authorities_of_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 

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

# **list_tokens_of_user**
> List[ApiTokenInfoDTO] list_tokens_of_user(username)

Get API Token of User

Get the list of API Tokens of the user.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.api_token_info_dto import ApiTokenInfoDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username

    try:
        # Get API Token of User
        api_response = api_instance.list_tokens_of_user(username)
        print("The response of AccountManagerForAdminApi->list_tokens_of_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->list_tokens_of_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 

### Return type

[**List[ApiTokenInfoDTO]**](ApiTokenInfoDTO.md)

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

# **list_users**
> List[UserBasicInfoDTO] list_users(page_size, page_num)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    page_size = 56 # int | Maximum quantity
    page_num = 56 # int | Current page number

    try:
        # List User Information
        api_response = api_instance.list_users(page_size, page_num)
        print("The response of AccountManagerForAdminApi->list_users:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->list_users: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page_size** | **int**| Maximum quantity | 
 **page_num** | **int**| Current page number | 

### Return type

[**List[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

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

# **list_users1**
> List[UserBasicInfoDTO] list_users1()

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)

    try:
        # List User Information
        api_response = api_instance.list_users1()
        print("The response of AccountManagerForAdminApi->list_users1:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->list_users1: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

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

# **list_users2**
> List[UserBasicInfoDTO] list_users2(page_size)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_basic_info_dto import UserBasicInfoDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    page_size = 56 # int | Maximum quantity

    try:
        # List User Information
        api_response = api_instance.list_users2(page_size)
        print("The response of AccountManagerForAdminApi->list_users2:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->list_users2: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **page_size** | **int**| Maximum quantity | 

### Return type

[**List[UserBasicInfoDTO]**](UserBasicInfoDTO.md)

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

# **update_authorities_of_user**
> bool update_authorities_of_user(username, request_body)

Update User Permissions

Update the user's permission list.

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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    username = 'username_example' # str | Username
    request_body = ['request_body_example'] # List[str] | Permission list

    try:
        # Update User Permissions
        api_response = api_instance.update_authorities_of_user(username, request_body)
        print("The response of AccountManagerForAdminApi->update_authorities_of_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->update_authorities_of_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| Username | 
 **request_body** | [**List[str]**](str.md)| Permission list | 

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

# **update_user**
> bool update_user(user_full_details_dto)

Update User

Update user information.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO
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
    api_instance = freechat_sdk.AccountManagerForAdminApi(api_client)
    user_full_details_dto = freechat_sdk.UserFullDetailsDTO() # UserFullDetailsDTO | User information

    try:
        # Update User
        api_response = api_instance.update_user(user_full_details_dto)
        print("The response of AccountManagerForAdminApi->update_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AccountManagerForAdminApi->update_user: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_full_details_dto** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | 

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

