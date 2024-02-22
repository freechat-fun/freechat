# freechat_sdk.OrganizationApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_owners**](OrganizationApi.md#get_owners) | **GET** /api/v1/org/owners | Get My Superior Relationship
[**get_owners_dot**](OrganizationApi.md#get_owners_dot) | **GET** /api/v1/org/owners/dot | Get DOT of Superior Relationship
[**get_subordinate_owners**](OrganizationApi.md#get_subordinate_owners) | **GET** /api/v1/org/manage/{username}/owners | Get Superior Relationship
[**get_subordinate_subordinates**](OrganizationApi.md#get_subordinate_subordinates) | **GET** /api/v1/org/manage/{username}/subordinates | Get Subordinate Relationship
[**get_subordinates**](OrganizationApi.md#get_subordinates) | **GET** /api/v1/org/subordinates | Get My Subordinate Relationship
[**get_subordinates_dot**](OrganizationApi.md#get_subordinates_dot) | **GET** /api/v1/org/subordinates/dot | Get DOT of Subordinate Relationship
[**list_subordinate_authorities**](OrganizationApi.md#list_subordinate_authorities) | **GET** /api/v1/org/authority/{username} | List Subordinate Permissions
[**remove_subordinate_subordinates_tree**](OrganizationApi.md#remove_subordinate_subordinates_tree) | **DELETE** /api/v1/org/manage/{username}/subordinates | Clear Subordinate Relationship
[**update_subordinate_authorities**](OrganizationApi.md#update_subordinate_authorities) | **PUT** /api/v1/org/authority/{username} | Update Subordinate Permissions
[**update_subordinate_owners**](OrganizationApi.md#update_subordinate_owners) | **PUT** /api/v1/org/manage/{username}/owners | Update Superior Relationship
[**update_subordinate_subordinates**](OrganizationApi.md#update_subordinate_subordinates) | **PUT** /api/v1/org/manage/{username}/subordinates | Update Subordinate Relationship


# **get_owners**
> List[str] get_owners(all=all)

Get My Superior Relationship

Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    all = 'all_example' # str | Whether to return virtual reported owners (optional)

    try:
        # Get My Superior Relationship
        api_response = api_instance.get_owners(all=all)
        print("The response of OrganizationApi->get_owners:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_owners: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **str**| Whether to return virtual reported owners | [optional] 

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

# **get_owners_dot**
> str get_owners_dot(all=all)

Get DOT of Superior Relationship

Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    all = 'all_example' # str | Whether to return virtual reported owners (optional)

    try:
        # Get DOT of Superior Relationship
        api_response = api_instance.get_owners_dot(all=all)
        print("The response of OrganizationApi->get_owners_dot:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_owners_dot: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **str**| Whether to return virtual reported owners | [optional] 

### Return type

**str**

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

# **get_subordinate_owners**
> List[str] get_subordinate_owners(username, all=all)

Get Superior Relationship

Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | The account being queried, must be a subordinate account of the current account
    all = 'all_example' # str | Whether to return virtual reported owners (optional)

    try:
        # Get Superior Relationship
        api_response = api_instance.get_subordinate_owners(username, all=all)
        print("The response of OrganizationApi->get_subordinate_owners:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_subordinate_owners: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| The account being queried, must be a subordinate account of the current account | 
 **all** | **str**| Whether to return virtual reported owners | [optional] 

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

# **get_subordinate_subordinates**
> List[str] get_subordinate_subordinates(username, all=all)

Get Subordinate Relationship

Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | The account being queried, must be a subordinate account of the current account
    all = 'all_example' # str | Whether to return virtual managed subordinates (optional)

    try:
        # Get Subordinate Relationship
        api_response = api_instance.get_subordinate_subordinates(username, all=all)
        print("The response of OrganizationApi->get_subordinate_subordinates:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_subordinate_subordinates: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| The account being queried, must be a subordinate account of the current account | 
 **all** | **str**| Whether to return virtual managed subordinates | [optional] 

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

# **get_subordinates**
> List[str] get_subordinates(all=all)

Get My Subordinate Relationship

Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    all = 'all_example' # str | Whether to return virtual managed subordinates (optional)

    try:
        # Get My Subordinate Relationship
        api_response = api_instance.get_subordinates(all=all)
        print("The response of OrganizationApi->get_subordinates:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_subordinates: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **str**| Whether to return virtual managed subordinates | [optional] 

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

# **get_subordinates_dot**
> str get_subordinates_dot(all=all)

Get DOT of Subordinate Relationship

Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    all = 'all_example' # str | Whether to return virtual managed subordinates (optional)

    try:
        # Get DOT of Subordinate Relationship
        api_response = api_instance.get_subordinates_dot(all=all)
        print("The response of OrganizationApi->get_subordinates_dot:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->get_subordinates_dot: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **str**| Whether to return virtual managed subordinates | [optional] 

### Return type

**str**

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

# **list_subordinate_authorities**
> List[str] list_subordinate_authorities(username)

List Subordinate Permissions

List the permission list of the subordinate account.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | Username

    try:
        # List Subordinate Permissions
        api_response = api_instance.list_subordinate_authorities(username)
        print("The response of OrganizationApi->list_subordinate_authorities:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->list_subordinate_authorities: %s\n" % e)
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

# **remove_subordinate_subordinates_tree**
> bool remove_subordinate_subordinates_tree(username)

Clear Subordinate Relationship

Fully delete the direct subordinate relationship of the subordinate account.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | The account being operated, must be a subordinate account of the current account

    try:
        # Clear Subordinate Relationship
        api_response = api_instance.remove_subordinate_subordinates_tree(username)
        print("The response of OrganizationApi->remove_subordinate_subordinates_tree:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->remove_subordinate_subordinates_tree: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| The account being operated, must be a subordinate account of the current account | 

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

# **update_subordinate_authorities**
> bool update_subordinate_authorities(username, request_body)

Update Subordinate Permissions

Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | Username
    request_body = ['request_body_example'] # List[str] | Permission list

    try:
        # Update Subordinate Permissions
        api_response = api_instance.update_subordinate_authorities(username, request_body)
        print("The response of OrganizationApi->update_subordinate_authorities:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->update_subordinate_authorities: %s\n" % e)
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

# **update_subordinate_owners**
> bool update_subordinate_owners(username, request_body)

Update Superior Relationship

Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | The account being operated, must be a subordinate account of the current account
    request_body = ['request_body_example'] # List[str] | The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account

    try:
        # Update Superior Relationship
        api_response = api_instance.update_subordinate_owners(username, request_body)
        print("The response of OrganizationApi->update_subordinate_owners:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->update_subordinate_owners: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| The account being operated, must be a subordinate account of the current account | 
 **request_body** | [**List[str]**](str.md)| The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account | 

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

# **update_subordinate_subordinates**
> bool update_subordinate_subordinates(username, request_body)

Update Subordinate Relationship

Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

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
    api_instance = freechat_sdk.OrganizationApi(api_client)
    username = 'username_example' # str | The account being operated, must be a subordinate account of the current account
    request_body = ['request_body_example'] # List[str] | The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account

    try:
        # Update Subordinate Relationship
        api_response = api_instance.update_subordinate_subordinates(username, request_body)
        print("The response of OrganizationApi->update_subordinate_subordinates:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OrganizationApi->update_subordinate_subordinates: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **str**| The account being operated, must be a subordinate account of the current account | 
 **request_body** | [**List[str]**](str.md)| The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account | 

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

