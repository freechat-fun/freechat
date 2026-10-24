# ConnectorApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createConnector**](ConnectorApi.md#createConnector) | **POST** /api/v2/connector | Create My Connector |
| [**deleteConnector**](ConnectorApi.md#deleteConnector) | **DELETE** /api/v2/connector/{connectorId} | Delete My Connector |
| [**getConnector**](ConnectorApi.md#getConnector) | **GET** /api/v2/connector/{connectorId} | Get My Connector |
| [**listConnectors**](ConnectorApi.md#listConnectors) | **GET** /api/v2/connector | List My Connectors |
| [**testConnector**](ConnectorApi.md#testConnector) | **POST** /api/v2/connector/{connectorId}/test | Test My Connector |
| [**updateConnector**](ConnectorApi.md#updateConnector) | **PUT** /api/v2/connector/{connectorId} | Update My Connector |


<a id="createConnector"></a>
# **createConnector**
> ConnectorDetailsDTO createConnector(connectorWriteDTO)

Create My Connector

Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    ConnectorWriteDTO connectorWriteDTO = new ConnectorWriteDTO(); // ConnectorWriteDTO | 
    try {
      ConnectorDetailsDTO result = apiInstance.createConnector(connectorWriteDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#createConnector");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **connectorWriteDTO** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md)|  | |

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
| **200** | OK |  -  |

<a id="deleteConnector"></a>
# **deleteConnector**
> deleteConnector(connectorId, expectedRevision)

Delete My Connector

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    String connectorId = "connectorId_example"; // String | 
    String expectedRevision = "expectedRevision_example"; // String | 
    try {
      apiInstance.deleteConnector(connectorId, expectedRevision);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#deleteConnector");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **connectorId** | **String**|  | |
| **expectedRevision** | **String**|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |

<a id="getConnector"></a>
# **getConnector**
> ConnectorDetailsDTO getConnector(connectorId)

Get My Connector

Returns metadata and header names, never header values.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    String connectorId = "connectorId_example"; // String | 
    try {
      ConnectorDetailsDTO result = apiInstance.getConnector(connectorId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#getConnector");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **connectorId** | **String**|  | |

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
| **200** | OK |  -  |

<a id="listConnectors"></a>
# **listConnectors**
> List&lt;ConnectorDetailsDTO&gt; listConnectors()

List My Connectors

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    try {
      List<ConnectorDetailsDTO> result = apiInstance.listConnectors();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#listConnectors");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;ConnectorDetailsDTO&gt;**](ConnectorDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="testConnector"></a>
# **testConnector**
> ConnectorTestDTO testConnector(connectorId, expectedRevision)

Test My Connector

Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. Private addresses and redirects are rejected. The stored configuration revision must match.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    String connectorId = "connectorId_example"; // String | 
    String expectedRevision = "expectedRevision_example"; // String | 
    try {
      ConnectorTestDTO result = apiInstance.testConnector(connectorId, expectedRevision);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#testConnector");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **connectorId** | **String**|  | |
| **expectedRevision** | **String**|  | |

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
| **200** | OK |  -  |

<a id="updateConnector"></a>
# **updateConnector**
> ConnectorDetailsDTO updateConnector(connectorId, connectorWriteDTO)

Update My Connector

Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.ConnectorApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    ConnectorApi apiInstance = new ConnectorApi(defaultClient);
    String connectorId = "connectorId_example"; // String | 
    ConnectorWriteDTO connectorWriteDTO = new ConnectorWriteDTO(); // ConnectorWriteDTO | 
    try {
      ConnectorDetailsDTO result = apiInstance.updateConnector(connectorId, connectorWriteDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ConnectorApi#updateConnector");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **connectorId** | **String**|  | |
| **connectorWriteDTO** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md)|  | |

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
| **200** | OK |  -  |

