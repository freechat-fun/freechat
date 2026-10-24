# ConnectorApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**createConnector**](ConnectorApi.md#createConnector) | **POST** /api/v2/connector | Create My Connector
[**deleteConnector**](ConnectorApi.md#deleteConnector) | **DELETE** /api/v2/connector/{connectorId} | Delete My Connector
[**getConnector**](ConnectorApi.md#getConnector) | **GET** /api/v2/connector/{connectorId} | Get My Connector
[**listConnectors**](ConnectorApi.md#listConnectors) | **GET** /api/v2/connector | List My Connectors
[**testConnector**](ConnectorApi.md#testConnector) | **POST** /api/v2/connector/{connectorId}/test | Test My Connector
[**updateConnector**](ConnectorApi.md#updateConnector) | **PUT** /api/v2/connector/{connectorId} | Update My Connector



## createConnector

Create My Connector

Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.

### Example

```bash
freechat-cli createConnector
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorWriteDTO** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md) |  |

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteConnector

Delete My Connector

### Example

```bash
freechat-cli deleteConnector connectorId=value  expectedRevision=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | **string** |  | [default to null]
 **expectedRevision** | **string** |  | [default to null]

### Return type

(empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getConnector

Get My Connector

Returns metadata and header names, never header values.

### Example

```bash
freechat-cli getConnector connectorId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | **string** |  | [default to null]

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listConnectors

List My Connectors

### Example

```bash
freechat-cli listConnectors
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**array[ConnectorDetailsDTO]**](ConnectorDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## testConnector

Test My Connector

Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. Private addresses and redirects are rejected. The stored configuration revision must match.

### Example

```bash
freechat-cli testConnector connectorId=value  expectedRevision=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | **string** |  | [default to null]
 **expectedRevision** | **string** |  | [default to null]

### Return type

[**ConnectorTestDTO**](ConnectorTestDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateConnector

Update My Connector

Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.

### Example

```bash
freechat-cli updateConnector connectorId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectorId** | **string** |  | [default to null]
 **connectorWriteDTO** | [**ConnectorWriteDTO**](ConnectorWriteDTO.md) |  |

### Return type

[**ConnectorDetailsDTO**](ConnectorDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

