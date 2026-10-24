# .ConnectorApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createConnector**](ConnectorApi.md#createConnector) | **POST** /api/v2/connector | Create My Connector
[**deleteConnector**](ConnectorApi.md#deleteConnector) | **DELETE** /api/v2/connector/{connectorId} | Delete My Connector
[**getConnector**](ConnectorApi.md#getConnector) | **GET** /api/v2/connector/{connectorId} | Get My Connector
[**listConnectors**](ConnectorApi.md#listConnectors) | **GET** /api/v2/connector | List My Connectors
[**testConnector**](ConnectorApi.md#testConnector) | **POST** /api/v2/connector/{connectorId}/test | Test My Connector
[**updateConnector**](ConnectorApi.md#updateConnector) | **PUT** /api/v2/connector/{connectorId} | Update My Connector


# **createConnector**
> ConnectorDetailsDTO createConnector(connectorWriteDTO)

Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.

### Example


```typescript
import { createConfiguration, ConnectorApi } from '';
import type { ConnectorApiCreateConnectorRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request: ConnectorApiCreateConnectorRequest = {
  
  connectorWriteDTO: {
    name: "name_example",
    transport: "http",
    url: "url_example",
    timeout: 60000,
    headers: {
      "key": "key_example",
    },
    keepHeaderNames: [
      "keepHeaderNames_example",
    ],
    expectedRevision: "expectedRevision_example",
    active: true,
  },
};

const data = await apiInstance.createConnector(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorWriteDTO** | **ConnectorWriteDTO**|  |


### Return type

**ConnectorDetailsDTO**

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

# **deleteConnector**
> void deleteConnector()


### Example


```typescript
import { createConfiguration, ConnectorApi } from '';
import type { ConnectorApiDeleteConnectorRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request: ConnectorApiDeleteConnectorRequest = {
  
  connectorId: "connectorId_example",
  
  expectedRevision: "expectedRevision_example",
};

const data = await apiInstance.deleteConnector(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | [**string**] |  | defaults to undefined
 **expectedRevision** | [**string**] |  | defaults to undefined


### Return type

**void**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getConnector**
> ConnectorDetailsDTO getConnector()

Returns metadata and header names, never header values.

### Example


```typescript
import { createConfiguration, ConnectorApi } from '';
import type { ConnectorApiGetConnectorRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request: ConnectorApiGetConnectorRequest = {
  
  connectorId: "connectorId_example",
};

const data = await apiInstance.getConnector(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | [**string**] |  | defaults to undefined


### Return type

**ConnectorDetailsDTO**

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

# **listConnectors**
> Array<ConnectorDetailsDTO> listConnectors()


### Example


```typescript
import { createConfiguration, ConnectorApi } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request = {};

const data = await apiInstance.listConnectors(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<ConnectorDetailsDTO>**

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

# **testConnector**
> ConnectorTestDTO testConnector()

Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. Private addresses and redirects are rejected. The stored configuration revision must match.

### Example


```typescript
import { createConfiguration, ConnectorApi } from '';
import type { ConnectorApiTestConnectorRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request: ConnectorApiTestConnectorRequest = {
  
  connectorId: "connectorId_example",
  
  expectedRevision: "expectedRevision_example",
};

const data = await apiInstance.testConnector(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | [**string**] |  | defaults to undefined
 **expectedRevision** | [**string**] |  | defaults to undefined


### Return type

**ConnectorTestDTO**

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

# **updateConnector**
> ConnectorDetailsDTO updateConnector(connectorWriteDTO)

Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.

### Example


```typescript
import { createConfiguration, ConnectorApi } from '';
import type { ConnectorApiUpdateConnectorRequest } from '';

const configuration = createConfiguration();
const apiInstance = new ConnectorApi(configuration);

const request: ConnectorApiUpdateConnectorRequest = {
  
  connectorId: "connectorId_example",
  
  connectorWriteDTO: {
    name: "name_example",
    transport: "http",
    url: "url_example",
    timeout: 60000,
    headers: {
      "key": "key_example",
    },
    keepHeaderNames: [
      "keepHeaderNames_example",
    ],
    expectedRevision: "expectedRevision_example",
    active: true,
  },
};

const data = await apiInstance.updateConnector(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorWriteDTO** | **ConnectorWriteDTO**|  |
 **connectorId** | [**string**] |  | defaults to undefined


### Return type

**ConnectorDetailsDTO**

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


