# freechat_sdk.ConnectorApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_connector**](ConnectorApi.md#create_connector) | **POST** /api/v2/connector | Create My Connector
[**delete_connector**](ConnectorApi.md#delete_connector) | **DELETE** /api/v2/connector/{connectorId} | Delete My Connector
[**get_connector**](ConnectorApi.md#get_connector) | **GET** /api/v2/connector/{connectorId} | Get My Connector
[**list_connectors**](ConnectorApi.md#list_connectors) | **GET** /api/v2/connector | List My Connectors
[**test_connector**](ConnectorApi.md#test_connector) | **POST** /api/v2/connector/{connectorId}/test | Test My Connector
[**update_connector**](ConnectorApi.md#update_connector) | **PUT** /api/v2/connector/{connectorId} | Update My Connector


# **create_connector**
> ConnectorDetailsDTO create_connector(connector_write_dto)

Create My Connector

Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.connector_details_dto import ConnectorDetailsDTO
from freechat_sdk.models.connector_write_dto import ConnectorWriteDTO
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
    api_instance = freechat_sdk.ConnectorApi(api_client)
    connector_write_dto = freechat_sdk.ConnectorWriteDTO() # ConnectorWriteDTO | 

    try:
        # Create My Connector
        api_response = api_instance.create_connector(connector_write_dto)
        print("The response of ConnectorApi->create_connector:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConnectorApi->create_connector: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connector_write_dto** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md)|  |

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

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

# **delete_connector**
> delete_connector(connector_id, expected_revision)

Delete My Connector

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
    api_instance = freechat_sdk.ConnectorApi(api_client)
    connector_id = 'connector_id_example' # str | 
    expected_revision = 'expected_revision_example' # str | 

    try:
        # Delete My Connector
        api_instance.delete_connector(connector_id, expected_revision)
    except Exception as e:
        print("Exception when calling ConnectorApi->delete_connector: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connector_id** | **str**|  |
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

# **get_connector**
> ConnectorDetailsDTO get_connector(connector_id)

Get My Connector

Returns metadata and header names, never header values.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.connector_details_dto import ConnectorDetailsDTO
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
    api_instance = freechat_sdk.ConnectorApi(api_client)
    connector_id = 'connector_id_example' # str | 

    try:
        # Get My Connector
        api_response = api_instance.get_connector(connector_id)
        print("The response of ConnectorApi->get_connector:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConnectorApi->get_connector: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connector_id** | **str**|  |

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

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

# **list_connectors**
> List[ConnectorDetailsDTO] list_connectors()

List My Connectors

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.connector_details_dto import ConnectorDetailsDTO
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
    api_instance = freechat_sdk.ConnectorApi(api_client)

    try:
        # List My Connectors
        api_response = api_instance.list_connectors()
        print("The response of ConnectorApi->list_connectors:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConnectorApi->list_connectors: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ConnectorDetailsDTO]**](ConnectorDetailsDTO.md)

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

# **test_connector**
> ConnectorTestDTO test_connector(connector_id, expected_revision)

Test My Connector

Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. Private addresses and redirects are rejected. The stored configuration revision must match.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.connector_test_dto import ConnectorTestDTO
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
    api_instance = freechat_sdk.ConnectorApi(api_client)
    connector_id = 'connector_id_example' # str | 
    expected_revision = 'expected_revision_example' # str | 

    try:
        # Test My Connector
        api_response = api_instance.test_connector(connector_id, expected_revision)
        print("The response of ConnectorApi->test_connector:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConnectorApi->test_connector: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connector_id** | **str**|  |
 **expected_revision** | **str**|  |

### Return type

[**ConnectorTestDTO**](ConnectorTestDTO.md)

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

# **update_connector**
> ConnectorDetailsDTO update_connector(connector_id, connector_write_dto)

Update My Connector

Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.

### Example

* Bearer Authentication (bearerAuth):

```python
import freechat_sdk
from freechat_sdk.models.connector_details_dto import ConnectorDetailsDTO
from freechat_sdk.models.connector_write_dto import ConnectorWriteDTO
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
    api_instance = freechat_sdk.ConnectorApi(api_client)
    connector_id = 'connector_id_example' # str | 
    connector_write_dto = freechat_sdk.ConnectorWriteDTO() # ConnectorWriteDTO | 

    try:
        # Update My Connector
        api_response = api_instance.update_connector(connector_id, connector_write_dto)
        print("The response of ConnectorApi->update_connector:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ConnectorApi->update_connector: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connector_id** | **str**|  |
 **connector_write_dto** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md)|  |

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

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

