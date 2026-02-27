# PluginApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchPluginDetails**](PluginApi.md#batchSearchPluginDetails) | **POST** /api/v2/plugin/batch/details/search | Batch Search Plugin Details
[**batchSearchPluginSummary**](PluginApi.md#batchSearchPluginSummary) | **POST** /api/v2/plugin/batch/search | Batch Search Plugin Summaries
[**countPlugins**](PluginApi.md#countPlugins) | **POST** /api/v2/plugin/count | Calculate Number of Plugins
[**createPlugin**](PluginApi.md#createPlugin) | **POST** /api/v2/plugin | Create Plugin
[**createPlugins**](PluginApi.md#createPlugins) | **POST** /api/v2/plugin/batch | Batch Create Plugins
[**deletePlugin**](PluginApi.md#deletePlugin) | **DELETE** /api/v2/plugin/{pluginId} | Delete Plugin
[**deletePlugins**](PluginApi.md#deletePlugins) | **DELETE** /api/v2/plugin/batch | Batch Delete Plugins
[**getPluginDetails**](PluginApi.md#getPluginDetails) | **GET** /api/v2/plugin/details/{pluginId} | Get Plugin Details
[**getPluginSummary**](PluginApi.md#getPluginSummary) | **GET** /api/v2/plugin/summary/{pluginId} | Get Plugin Summary
[**refreshPluginInfo**](PluginApi.md#refreshPluginInfo) | **PUT** /api/v2/plugin/refresh/{pluginId} | Refresh Plugin Information
[**searchPluginDetails**](PluginApi.md#searchPluginDetails) | **POST** /api/v2/plugin/details/search | Search Plugin Details
[**searchPluginSummary**](PluginApi.md#searchPluginSummary) | **POST** /api/v2/plugin/search | Search Plugin Summary
[**updatePlugin**](PluginApi.md#updatePlugin) | **PUT** /api/v2/plugin/{pluginId} | Update Plugin



## batchSearchPluginDetails

Batch Search Plugin Details

Batch call shortcut for /api/v2/plugin/details/search.

### Example

```bash
freechat batchSearchPluginDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**array[PluginQueryDTO]**](PluginQueryDTO.md) | Query conditions |

### Return type

**array[array[PluginDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchPluginSummary

Batch Search Plugin Summaries

Batch call shortcut for /api/v2/plugin/search.

### Example

```bash
freechat batchSearchPluginSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**array[PluginQueryDTO]**](PluginQueryDTO.md) | Query conditions |

### Return type

**array[array[PluginSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countPlugins

Calculate Number of Plugins

Calculate the number of plugins according to the specified query conditions.

### Example

```bash
freechat countPlugins
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createPlugin

Create Plugin

Create a plugin, required fields:
- Plugin name
- Plugin manifestInfo (URL or JSON)
- Plugin apiInfo (URL or JSON)

Limitations:
- Name: 100 characters
- Example: 2000 characters
- Tags: 5

### Example

```bash
freechat createPlugin
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | [**PluginCreateDTO**](PluginCreateDTO.md) | Information of the plugin to be created |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createPlugins

Batch Create Plugins

Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.

### Example

```bash
freechat createPlugins
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginCreateDTO** | [**array[PluginCreateDTO]**](PluginCreateDTO.md) | List of plugin information to be created |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePlugin

Delete Plugin

Delete plugin. Returns success or failure.

### Example

```bash
freechat deletePlugin pluginId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **integer** | The pluginId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePlugins

Batch Delete Plugins

Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.

### Example

```bash
freechat deletePlugins
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**array[integer]**](integer.md) | List of pluginIds to be deleted |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getPluginDetails

Get Plugin Details

Get plugin detailed information.

### Example

```bash
freechat getPluginDetails pluginId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **integer** | PluginId to be obtained | [default to null]

### Return type

[**PluginDetailsDTO**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getPluginSummary

Get Plugin Summary

Get plugin summary information.

### Example

```bash
freechat getPluginSummary pluginId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **integer** | PluginId to be obtained | [default to null]

### Return type

[**PluginSummaryDTO**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## refreshPluginInfo

Refresh Plugin Information

For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.

### Example

```bash
freechat refreshPluginInfo pluginId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **integer** | The pluginId to be fetched | [default to null]

### Return type

(empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPluginDetails

Search Plugin Details

Same as /api/v2/plugin/search, but returns detailed information of the plugin.

### Example

```bash
freechat searchPluginDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md) | Query conditions |

### Return type

[**array[PluginDetailsDTO]**](PluginDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPluginSummary

Search Plugin Summary

Search plugins:
- Specifiable query fields, and relationship:
  - Scope: private, public_org or public. Private can only search this account.
  - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
  - Plugin information format: currently supported: dash_scope, open_ai.
  - Interface information format: currently supported: openapi_v3.
  - Tags: exact match (support and, or logic).
  - Model type: exact match (support and, or logic).
  - Name: left match.
  - Provider: left match.
  - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the plugin summary content.
- Support pagination.

### Example

```bash
freechat searchPluginSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginQueryDTO** | [**PluginQueryDTO**](PluginQueryDTO.md) | Query conditions |

### Return type

[**array[PluginSummaryDTO]**](PluginSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updatePlugin

Update Plugin

Update plugin, refer to /api/v2/plugin/create, required field: pluginId. Returns success or failure.

### Example

```bash
freechat updatePlugin pluginId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **pluginId** | **integer** | The pluginId to be updated | [default to null]
 **pluginUpdateDTO** | [**PluginUpdateDTO**](PluginUpdateDTO.md) | The plugin information to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

