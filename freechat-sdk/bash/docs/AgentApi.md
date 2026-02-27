# AgentApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchAgentDetails**](AgentApi.md#batchSearchAgentDetails) | **POST** /api/v2/agent/batch/details/search | Batch Search Agent Details
[**batchSearchAgentSummary**](AgentApi.md#batchSearchAgentSummary) | **POST** /api/v2/agent/batch/search | Batch Search Agent Summaries
[**cloneAgent**](AgentApi.md#cloneAgent) | **POST** /api/v2/agent/clone/{agentId} | Clone Agent
[**cloneAgents**](AgentApi.md#cloneAgents) | **POST** /api/v2/agent/batch/clone | Batch Clone Agents
[**countAgents**](AgentApi.md#countAgents) | **POST** /api/v2/agent/count | Calculate Number of Agents
[**createAgent**](AgentApi.md#createAgent) | **POST** /api/v2/agent | Create Agent
[**createAgents**](AgentApi.md#createAgents) | **POST** /api/v2/agent/batch | Batch Create Agents
[**deleteAgent**](AgentApi.md#deleteAgent) | **DELETE** /api/v2/agent/{agentId} | Delete Agent
[**deleteAgents**](AgentApi.md#deleteAgents) | **DELETE** /api/v2/agent/batch/delete | Batch Delete Agents
[**getAgentDetails**](AgentApi.md#getAgentDetails) | **GET** /api/v2/agent/details/{agentId} | Get Agent Details
[**getAgentSummary**](AgentApi.md#getAgentSummary) | **GET** /api/v2/agent/summary/{agentId} | Get Agent Summary
[**listAgentVersionsByName**](AgentApi.md#listAgentVersionsByName) | **POST** /api/v2/agent/versions/{name} | List Versions by Agent Name
[**publishAgent**](AgentApi.md#publishAgent) | **POST** /api/v2/agent/publish/{agentId}/{visibility} | Publish Agent
[**searchAgentDetails**](AgentApi.md#searchAgentDetails) | **POST** /api/v2/agent/details/search | Search Agent Details
[**searchAgentSummary**](AgentApi.md#searchAgentSummary) | **POST** /api/v2/agent/search | Search Agent Summary
[**updateAgent**](AgentApi.md#updateAgent) | **PUT** /api/v2/agent/{agentId} | Update Agent



## batchSearchAgentDetails

Batch Search Agent Details

Batch call shortcut for /api/v2/agent/details/search.

### Example

```bash
freechat-cli batchSearchAgentDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | [**array[AgentQueryDTO]**](AgentQueryDTO.md) | Query conditions |

### Return type

**array[array[AgentDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchAgentSummary

Batch Search Agent Summaries

Batch call shortcut for /api/v2/agent/search.

### Example

```bash
freechat-cli batchSearchAgentSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | [**array[AgentQueryDTO]**](AgentQueryDTO.md) | Query conditions |

### Return type

**array[array[AgentSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## cloneAgent

Clone Agent

Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different:
- Version number is 1
- Visibility is private
- The parent agent is the source agentId
- The creation time is the current moment.
 - All statistical indicators are zeroed.

Return the new agentId.

### Example

```bash
freechat-cli cloneAgent agentId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | The referenced agentId | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## cloneAgents

Batch Clone Agents

Batch clone multiple agents. Ensure transactionality, return the agentId list after success.

### Example

```bash
freechat-cli cloneAgents
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**array[integer]**](integer.md) | List of agent information to be created |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countAgents

Calculate Number of Agents

Calculate the number of agents according to the specified query conditions.

### Example

```bash
freechat-cli countAgents
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createAgent

Create Agent

Create a agent, ignore required fields:
- Agent name
- Agent configuration

Limitations:
- Description: 300 characters
- Configuration: 2000 characters
- Example: 2000 characters
- Tags: 5
- Parameters: 10

### Example

```bash
freechat-cli createAgent
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | [**AgentCreateDTO**](AgentCreateDTO.md) | Information of the agent to be created |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createAgents

Batch Create Agents

Batch create multiple agents. Ensure transactionality, return the agentId list after success.

### Example

```bash
freechat-cli createAgents
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | [**array[AgentCreateDTO]**](AgentCreateDTO.md) | List of agent information to be created |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteAgent

Delete Agent

Delete agent. Return success or failure.

### Example

```bash
freechat-cli deleteAgent agentId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | AgentId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteAgents

Batch Delete Agents

Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.

### Example

```bash
freechat-cli deleteAgents
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**array[integer]**](integer.md) | List of agentId to be deleted |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getAgentDetails

Get Agent Details

Get agent detailed information.

### Example

```bash
freechat-cli getAgentDetails agentId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | AgentId to be obtained | [default to null]

### Return type

[**AgentDetailsDTO**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getAgentSummary

Get Agent Summary

Get agent summary information.

### Example

```bash
freechat-cli getAgentSummary agentId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | agentId to be obtained | [default to null]

### Return type

[**AgentSummaryDTO**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listAgentVersionsByName

List Versions by Agent Name

List the versions and corresponding agentIds by agent name.

### Example

```bash
freechat-cli listAgentVersionsByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Agent name | [default to null]

### Return type

[**array[AgentItemForNameDTO]**](AgentItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## publishAgent

Publish Agent

Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.

### Example

```bash
freechat-cli publishAgent agentId=value visibility=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | The agentId to be published | [default to null]
 **visibility** | **string** | Visibility: public | private | ... | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchAgentDetails

Search Agent Details

Same as /api/v2/agent/search, but returns detailed information of the agent.

### Example

```bash
freechat-cli searchAgentDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md) | Query conditions |

### Return type

[**array[AgentDetailsDTO]**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchAgentSummary

Search Agent Summary

Search agents:
- Specifiable query fields, and relationship:
  - Scope: private, public_org or public. Private can only search this account.
  - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
  - Format: exact match, currently supported: langflow
  - Tags: exact match (support and, or logic).
  - Model type: exact match (support and, or logic).
  - Name: left match.
  - General: name, description, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the agent summary content.
- Support pagination.

### Example

```bash
freechat-cli searchAgentSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md) | Query conditions |

### Return type

[**array[AgentSummaryDTO]**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateAgent

Update Agent

Update agent, refer to /api/v2/agent/create, required field: agentId. Return success or failure.

### Example

```bash
freechat-cli updateAgent agentId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | **integer** | AgentId to be updated | [default to null]
 **agentUpdateDTO** | [**AgentUpdateDTO**](AgentUpdateDTO.md) | Agent information to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

