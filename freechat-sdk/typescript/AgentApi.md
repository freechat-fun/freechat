# .AgentApi

All URIs are relative to *http://127.0.0.1:8080*

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


# **batchSearchAgentDetails**
> Array<Array<AgentDetailsDTO>> batchSearchAgentDetails(agentQueryDTO)

Batch call shortcut for /api/v2/agent/details/search.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiBatchSearchAgentDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiBatchSearchAgentDetailsRequest = {
    // Query conditions
  agentQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        format: "format_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "aiModelsOp_example",
        name: "name_example",
        text: "text_example",
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchAgentDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | **Array<AgentQueryDTO>**| Query conditions |


### Return type

**Array<Array<AgentDetailsDTO>>**

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

# **batchSearchAgentSummary**
> Array<Array<AgentSummaryDTO>> batchSearchAgentSummary(agentQueryDTO)

Batch call shortcut for /api/v2/agent/search.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiBatchSearchAgentSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiBatchSearchAgentSummaryRequest = {
    // Query conditions
  agentQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        format: "format_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "aiModelsOp_example",
        name: "name_example",
        text: "text_example",
      },
      orderBy: [
        "orderBy_example",
      ],
      pageNum: 1,
      pageSize: 1,
    },
  ],
};

const data = await apiInstance.batchSearchAgentSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | **Array<AgentQueryDTO>**| Query conditions |


### Return type

**Array<Array<AgentSummaryDTO>>**

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

# **cloneAgent**
> number cloneAgent()

Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiCloneAgentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiCloneAgentRequest = {
    // The referenced agentId
  agentId: 1,
};

const data = await apiInstance.cloneAgent(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**number**] | The referenced agentId | defaults to undefined


### Return type

**number**

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

# **cloneAgents**
> Array<number> cloneAgents(requestBody)

Batch clone multiple agents. Ensure transactionality, return the agentId list after success.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiCloneAgentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiCloneAgentsRequest = {
    // List of agent information to be created
  requestBody: [
    1,
  ],
};

const data = await apiInstance.cloneAgents(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<number>**| List of agent information to be created |


### Return type

**Array<number>**

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

# **countAgents**
> number countAgents(agentQueryDTO)

Calculate the number of agents according to the specified query conditions.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiCountAgentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiCountAgentsRequest = {
    // Query conditions
  agentQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      format: "format_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.countAgents(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | **AgentQueryDTO**| Query conditions |


### Return type

**number**

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

# **createAgent**
> number createAgent(agentCreateDTO)

Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiCreateAgentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiCreateAgentRequest = {
    // Information of the agent to be created
  agentCreateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    format: "format_example",
    name: "name_example",
    description: "description_example",
    config: "config_example",
    example: "example_example",
    parameters: "parameters_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
    aiModels: [
      "aiModels_example",
    ],
  },
};

const data = await apiInstance.createAgent(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | **AgentCreateDTO**| Information of the agent to be created |


### Return type

**number**

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

# **createAgents**
> Array<number> createAgents(agentCreateDTO)

Batch create multiple agents. Ensure transactionality, return the agentId list after success.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiCreateAgentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiCreateAgentsRequest = {
    // List of agent information to be created
  agentCreateDTO: [
    {
      parentUid: "parentUid_example",
      visibility: "visibility_example",
      format: "format_example",
      name: "name_example",
      description: "description_example",
      config: "config_example",
      example: "example_example",
      parameters: "parameters_example",
      ext: "ext_example",
      draft: "draft_example",
      tags: [
        "tags_example",
      ],
      aiModels: [
        "aiModels_example",
      ],
    },
  ],
};

const data = await apiInstance.createAgents(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | **Array<AgentCreateDTO>**| List of agent information to be created |


### Return type

**Array<number>**

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

# **deleteAgent**
> boolean deleteAgent()

Delete agent. Return success or failure.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiDeleteAgentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiDeleteAgentRequest = {
    // AgentId to be deleted
  agentId: 1,
};

const data = await apiInstance.deleteAgent(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**number**] | AgentId to be deleted | defaults to undefined


### Return type

**boolean**

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

# **deleteAgents**
> Array<number> deleteAgents(requestBody)

Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiDeleteAgentsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiDeleteAgentsRequest = {
    // List of agentId to be deleted
  requestBody: [
    1,
  ],
};

const data = await apiInstance.deleteAgents(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<number>**| List of agentId to be deleted |


### Return type

**Array<number>**

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

# **getAgentDetails**
> AgentDetailsDTO getAgentDetails()

Get agent detailed information.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiGetAgentDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiGetAgentDetailsRequest = {
    // AgentId to be obtained
  agentId: 1,
};

const data = await apiInstance.getAgentDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**number**] | AgentId to be obtained | defaults to undefined


### Return type

**AgentDetailsDTO**

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

# **getAgentSummary**
> AgentSummaryDTO getAgentSummary()

Get agent summary information.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiGetAgentSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiGetAgentSummaryRequest = {
    // agentId to be obtained
  agentId: 1,
};

const data = await apiInstance.getAgentSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**number**] | agentId to be obtained | defaults to undefined


### Return type

**AgentSummaryDTO**

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

# **listAgentVersionsByName**
> Array<AgentItemForNameDTO> listAgentVersionsByName()

List the versions and corresponding agentIds by agent name.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiListAgentVersionsByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiListAgentVersionsByNameRequest = {
    // Agent name
  name: "name_example",
};

const data = await apiInstance.listAgentVersionsByName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Agent name | defaults to undefined


### Return type

**Array<AgentItemForNameDTO>**

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

# **publishAgent**
> number publishAgent()

Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiPublishAgentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiPublishAgentRequest = {
    // The agentId to be published
  agentId: 1,
    // Visibility: public | private | ...
  visibility: "visibility_example",
};

const data = await apiInstance.publishAgent(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**number**] | The agentId to be published | defaults to undefined
 **visibility** | [**string**] | Visibility: public | private | ... | defaults to undefined


### Return type

**number**

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

# **searchAgentDetails**
> Array<AgentDetailsDTO> searchAgentDetails(agentQueryDTO)

Same as /api/v2/agent/search, but returns detailed information of the agent.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiSearchAgentDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiSearchAgentDetailsRequest = {
    // Query conditions
  agentQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      format: "format_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchAgentDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | **AgentQueryDTO**| Query conditions |


### Return type

**Array<AgentDetailsDTO>**

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

# **searchAgentSummary**
> Array<AgentSummaryDTO> searchAgentSummary(agentQueryDTO)

Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiSearchAgentSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiSearchAgentSummaryRequest = {
    // Query conditions
  agentQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      format: "format_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

const data = await apiInstance.searchAgentSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentQueryDTO** | **AgentQueryDTO**| Query conditions |


### Return type

**Array<AgentSummaryDTO>**

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

# **updateAgent**
> boolean updateAgent(agentUpdateDTO)

Update agent, refer to /api/v2/agent/create, required field: agentId. Return success or failure.

### Example


```typescript
import { createConfiguration, AgentApi } from '';
import type { AgentApiUpdateAgentRequest } from '';

const configuration = createConfiguration();
const apiInstance = new AgentApi(configuration);

const request: AgentApiUpdateAgentRequest = {
    // AgentId to be updated
  agentId: 1,
    // Agent information to be updated
  agentUpdateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    format: "format_example",
    name: "name_example",
    description: "description_example",
    config: "config_example",
    example: "example_example",
    parameters: "parameters_example",
    ext: "ext_example",
    draft: "draft_example",
    tags: [
      "tags_example",
    ],
    aiModels: [
      "aiModels_example",
    ],
  },
};

const data = await apiInstance.updateAgent(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentUpdateDTO** | **AgentUpdateDTO**| Agent information to be updated |
 **agentId** | [**number**] | AgentId to be updated | defaults to undefined


### Return type

**boolean**

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


