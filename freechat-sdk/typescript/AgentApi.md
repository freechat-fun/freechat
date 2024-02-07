# .AgentApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchAgentDetails**](AgentApi.md#batchSearchAgentDetails) | **POST** /api/v1/agent/batch/details/search | Batch Search Agent Details
[**batchSearchAgentSummary**](AgentApi.md#batchSearchAgentSummary) | **POST** /api/v1/agent/batch/search | Batch Search Agent Summaries
[**cloneAgent**](AgentApi.md#cloneAgent) | **POST** /api/v1/agent/clone/{agentId} | Clone Agent
[**cloneAgents**](AgentApi.md#cloneAgents) | **POST** /api/v1/agent/batch/clone | Batch Clone Agents
[**countAgents**](AgentApi.md#countAgents) | **POST** /api/v1/agent/count | Calculate Number of Agents
[**createAgent**](AgentApi.md#createAgent) | **POST** /api/v1/agent | Create Agent
[**createAgents**](AgentApi.md#createAgents) | **POST** /api/v1/agent/batch | Batch Create Agents
[**deleteAgent**](AgentApi.md#deleteAgent) | **DELETE** /api/v1/agent/{agentId} | Delete Agent
[**deleteAgents**](AgentApi.md#deleteAgents) | **DELETE** /api/v1/agent/batch/delete | Batch Delete Agents
[**getAgentDetails**](AgentApi.md#getAgentDetails) | **GET** /api/v1/agent/details/{agentId} | Get Agent Details
[**getAgentSummary**](AgentApi.md#getAgentSummary) | **GET** /api/v1/agent/summary/{agentId} | Get Agent Summary
[**listAgentVersionsByName**](AgentApi.md#listAgentVersionsByName) | **POST** /api/v1/agent/versions/{name} | List Versions by Agent Name
[**publishAgent**](AgentApi.md#publishAgent) | **POST** /api/v1/agent/publish/{agentId}/{visibility} | Publish Agent
[**searchAgentDetails**](AgentApi.md#searchAgentDetails) | **POST** /api/v1/agent/details/search | Search Agent Details
[**searchAgentSummary**](AgentApi.md#searchAgentSummary) | **POST** /api/v1/agent/search | Search Agent Summary
[**updateAgent**](AgentApi.md#updateAgent) | **PUT** /api/v1/agent/{agentId} | Update Agent


# **batchSearchAgentDetails**
> Array<Array<AgentDetailsDTO>> batchSearchAgentDetails(agentQueryDTO)

Batch call shortcut for /api/v1/agent/details/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiBatchSearchAgentDetailsRequest = {
  // Array<AgentQueryDTO> | Query conditions
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

apiInstance.batchSearchAgentDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

Batch call shortcut for /api/v1/agent/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiBatchSearchAgentSummaryRequest = {
  // Array<AgentQueryDTO> | Query conditions
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

apiInstance.batchSearchAgentSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
> string cloneAgent()

Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiCloneAgentRequest = {
  // string | The referenced agentId
  agentId: "agentId_example",
};

apiInstance.cloneAgent(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**string**] | The referenced agentId | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **cloneAgents**
> Array<string> cloneAgents(requestBody)

Batch clone multiple agents. Ensure transactionality, return the agentId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiCloneAgentsRequest = {
  // Array<string> | List of agent information to be created
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.cloneAgents(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of agent information to be created |


### Return type

**Array<string>**

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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiCountAgentsRequest = {
  // AgentQueryDTO | Query conditions
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

apiInstance.countAgents(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
> string createAgent(agentCreateDTO)

Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiCreateAgentRequest = {
  // AgentCreateDTO | Information of the agent to be created
  agentCreateDTO: {
    parentId: "parentId_example",
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

apiInstance.createAgent(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | **AgentCreateDTO**| Information of the agent to be created |


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **createAgents**
> Array<string> createAgents(agentCreateDTO)

Batch create multiple agents. Ensure transactionality, return the agentId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiCreateAgentsRequest = {
  // Array<AgentCreateDTO> | List of agent information to be created
  agentCreateDTO: [
    {
      parentId: "parentId_example",
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

apiInstance.createAgents(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentCreateDTO** | **Array<AgentCreateDTO>**| List of agent information to be created |


### Return type

**Array<string>**

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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiDeleteAgentRequest = {
  // string | AgentId to be deleted
  agentId: "agentId_example",
};

apiInstance.deleteAgent(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**string**] | AgentId to be deleted | defaults to undefined


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
> Array<string> deleteAgents(requestBody)

Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiDeleteAgentsRequest = {
  // Array<string> | List of agentId to be deleted
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.deleteAgents(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of agentId to be deleted |


### Return type

**Array<string>**

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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiGetAgentDetailsRequest = {
  // string | AgentId to be obtained
  agentId: "agentId_example",
};

apiInstance.getAgentDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**string**] | AgentId to be obtained | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiGetAgentSummaryRequest = {
  // string | agentId to be obtained
  agentId: "agentId_example",
};

apiInstance.getAgentSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**string**] | agentId to be obtained | defaults to undefined


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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiListAgentVersionsByNameRequest = {
  // string | Agent name
  name: "name_example",
};

apiInstance.listAgentVersionsByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
> string publishAgent()

Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiPublishAgentRequest = {
  // string | The agentId to be published
  agentId: "agentId_example",
  // string | Visibility: public | private | ...
  visibility: "visibility_example",
};

apiInstance.publishAgent(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentId** | [**string**] | The agentId to be published | defaults to undefined
 **visibility** | [**string**] | Visibility: public | private | ... | defaults to undefined


### Return type

**string**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **searchAgentDetails**
> Array<AgentDetailsDTO> searchAgentDetails(agentQueryDTO)

Same as /api/v1/agent/search, but returns detailed information of the agent.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiSearchAgentDetailsRequest = {
  // AgentQueryDTO | Query conditions
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

apiInstance.searchAgentDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiSearchAgentSummaryRequest = {
  // AgentQueryDTO | Query conditions
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

apiInstance.searchAgentSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
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

Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .AgentApi(configuration);

let body:.AgentApiUpdateAgentRequest = {
  // string | AgentId to be updated
  agentId: "agentId_example",
  // AgentUpdateDTO | Agent information to be updated
  agentUpdateDTO: {
    parentId: "parentId_example",
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

apiInstance.updateAgent(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **agentUpdateDTO** | **AgentUpdateDTO**| Agent information to be updated |
 **agentId** | [**string**] | AgentId to be updated | defaults to undefined


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


