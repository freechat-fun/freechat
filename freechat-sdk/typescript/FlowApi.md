# .FlowApi

All URIs are relative to *https://freechat.fun*

Method | HTTP request | Description
------------- | ------------- | -------------
[**batchSearchFlowDetails**](FlowApi.md#batchSearchFlowDetails) | **POST** /api/v1/flow/batch/details/search | Batch Search Flow Details
[**batchSearchFlowSummary**](FlowApi.md#batchSearchFlowSummary) | **POST** /api/v1/flow/batch/search | Batch Search Flow Summaries
[**cloneFlow**](FlowApi.md#cloneFlow) | **POST** /api/v1/flow/clone/{flowId} | Clone Flow
[**cloneFlows**](FlowApi.md#cloneFlows) | **POST** /api/v1/flow/batch/clone | Batch Clone Flows
[**countFlows**](FlowApi.md#countFlows) | **POST** /api/v1/flow/count | Calculate Number of Flows
[**createFlow**](FlowApi.md#createFlow) | **POST** /api/v1/flow | Create Flow
[**createFlows**](FlowApi.md#createFlows) | **POST** /api/v1/flow/batch | Batch Create Flows
[**deleteFlow**](FlowApi.md#deleteFlow) | **DELETE** /api/v1/flow/{flowId} | Delete Flow
[**deleteFlows**](FlowApi.md#deleteFlows) | **DELETE** /api/v1/flow/batch/delete | Batch Delete Flows
[**getFlowDetails**](FlowApi.md#getFlowDetails) | **GET** /api/v1/flow/details/{flowId} | Get Flow Details
[**getFlowSummary**](FlowApi.md#getFlowSummary) | **GET** /api/v1/flow/summary/{flowId} | Get Flow Summary
[**listFlowVersionsByName**](FlowApi.md#listFlowVersionsByName) | **POST** /api/v1/flow/versions/{name} | List Versions by Flow Name
[**publishFlow**](FlowApi.md#publishFlow) | **POST** /api/v1/flow/publish/{flowId}/{visibility} | Publish Flow
[**searchFlowDetails**](FlowApi.md#searchFlowDetails) | **POST** /api/v1/flow/details/search | Search Flow Details
[**searchFlowSummary**](FlowApi.md#searchFlowSummary) | **POST** /api/v1/flow/search | Search Flow Summary
[**updateFlow**](FlowApi.md#updateFlow) | **PUT** /api/v1/flow/{flowId} | Update Flow


# **batchSearchFlowDetails**
> Array<Array<FlowDetailsDTO>> batchSearchFlowDetails(flowQueryDTO)

Batch call shortcut for /api/v1/flow/details/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiBatchSearchFlowDetailsRequest = {
  // Array<FlowQueryDTO> | Query conditions
  flowQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "aiModelsOp_example",
        name: "name_example",
        type: "type_example",
        lang: "lang_example",
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

apiInstance.batchSearchFlowDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | **Array<FlowQueryDTO>**| Query conditions |


### Return type

**Array<Array<FlowDetailsDTO>>**

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

# **batchSearchFlowSummary**
> Array<Array<FlowSummaryDTO>> batchSearchFlowSummary(flowQueryDTO)

Batch call shortcut for /api/v1/flow/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiBatchSearchFlowSummaryRequest = {
  // Array<FlowQueryDTO> | Query conditions
  flowQueryDTO: [
    {
      where: {
        visibility: "visibility_example",
        username: "username_example",
        tags: [
          "tags_example",
        ],
        tagsOp: "tagsOp_example",
        aiModels: [
          "aiModels_example",
        ],
        aiModelsOp: "aiModelsOp_example",
        name: "name_example",
        type: "type_example",
        lang: "lang_example",
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

apiInstance.batchSearchFlowSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | **Array<FlowQueryDTO>**| Query conditions |


### Return type

**Array<Array<FlowSummaryDTO>>**

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

# **cloneFlow**
> string cloneFlow()

Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiCloneFlowRequest = {
  // string | The referenced flowId
  flowId: "flowId_example",
};

apiInstance.cloneFlow(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | [**string**] | The referenced flowId | defaults to undefined


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

# **cloneFlows**
> Array<string> cloneFlows(requestBody)

Batch clone multiple flows. Ensure transactionality, return the flowId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiCloneFlowsRequest = {
  // Array<string> | List of flow information to be created
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.cloneFlows(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of flow information to be created |


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

# **countFlows**
> number countFlows(flowQueryDTO)

Calculate the number of flows according to the specified query conditions.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiCountFlowsRequest = {
  // FlowQueryDTO | Query conditions
  flowQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      type: "type_example",
      lang: "lang_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.countFlows(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | **FlowQueryDTO**| Query conditions |


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

# **createFlow**
> string createFlow(flowCreateDTO)

Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiCreateFlowRequest = {
  // FlowCreateDTO | Information of the flow to be created
  flowCreateDTO: {
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

apiInstance.createFlow(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowCreateDTO** | **FlowCreateDTO**| Information of the flow to be created |


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

# **createFlows**
> Array<string> createFlows(flowCreateDTO)

Batch create multiple flows. Ensure transactionality, return the flowId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiCreateFlowsRequest = {
  // Array<FlowCreateDTO> | List of flow information to be created
  flowCreateDTO: [
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

apiInstance.createFlows(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowCreateDTO** | **Array<FlowCreateDTO>**| List of flow information to be created |


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

# **deleteFlow**
> boolean deleteFlow()

Delete flow. Return success or failure.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiDeleteFlowRequest = {
  // string | FlowId to be deleted
  flowId: "flowId_example",
};

apiInstance.deleteFlow(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | [**string**] | FlowId to be deleted | defaults to undefined


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

# **deleteFlows**
> Array<string> deleteFlows(requestBody)

Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiDeleteFlowsRequest = {
  // Array<string> | List of flowId to be deleted
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.deleteFlows(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of flowId to be deleted |


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

# **getFlowDetails**
> FlowDetailsDTO getFlowDetails()

Get flow detailed information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiGetFlowDetailsRequest = {
  // string | FlowId to be obtained
  flowId: "flowId_example",
};

apiInstance.getFlowDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | [**string**] | FlowId to be obtained | defaults to undefined


### Return type

**FlowDetailsDTO**

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

# **getFlowSummary**
> FlowSummaryDTO getFlowSummary()

Get flow summary information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiGetFlowSummaryRequest = {
  // string | flowId to be obtained
  flowId: "flowId_example",
};

apiInstance.getFlowSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | [**string**] | flowId to be obtained | defaults to undefined


### Return type

**FlowSummaryDTO**

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

# **listFlowVersionsByName**
> Array<FlowItemForNameDTO> listFlowVersionsByName()

List the versions and corresponding flowIds by flow name.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiListFlowVersionsByNameRequest = {
  // string | Flow name
  name: "name_example",
};

apiInstance.listFlowVersionsByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Flow name | defaults to undefined


### Return type

**Array<FlowItemForNameDTO>**

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

# **publishFlow**
> string publishFlow()

Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiPublishFlowRequest = {
  // string | The flowId to be published
  flowId: "flowId_example",
  // string | Visibility: public | private | ...
  visibility: "visibility_example",
};

apiInstance.publishFlow(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowId** | [**string**] | The flowId to be published | defaults to undefined
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

# **searchFlowDetails**
> Array<FlowDetailsDTO> searchFlowDetails(flowQueryDTO)

Same as /api/v1/flow/search, but returns detailed information of the flow.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiSearchFlowDetailsRequest = {
  // FlowQueryDTO | Query conditions
  flowQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      type: "type_example",
      lang: "lang_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.searchFlowDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | **FlowQueryDTO**| Query conditions |


### Return type

**Array<FlowDetailsDTO>**

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

# **searchFlowSummary**
> Array<FlowSummaryDTO> searchFlowSummary(flowQueryDTO)

Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiSearchFlowSummaryRequest = {
  // FlowQueryDTO | Query conditions
  flowQueryDTO: {
    where: {
      visibility: "visibility_example",
      username: "username_example",
      tags: [
        "tags_example",
      ],
      tagsOp: "tagsOp_example",
      aiModels: [
        "aiModels_example",
      ],
      aiModelsOp: "aiModelsOp_example",
      name: "name_example",
      type: "type_example",
      lang: "lang_example",
      text: "text_example",
    },
    orderBy: [
      "orderBy_example",
    ],
    pageNum: 1,
    pageSize: 1,
  },
};

apiInstance.searchFlowSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowQueryDTO** | **FlowQueryDTO**| Query conditions |


### Return type

**Array<FlowSummaryDTO>**

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

# **updateFlow**
> boolean updateFlow(flowUpdateDTO)

Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .FlowApi(configuration);

let body:.FlowApiUpdateFlowRequest = {
  // string | FlowId to be updated
  flowId: "flowId_example",
  // FlowUpdateDTO | Flow information to be updated
  flowUpdateDTO: {
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

apiInstance.updateFlow(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **flowUpdateDTO** | **FlowUpdateDTO**| Flow information to be updated |
 **flowId** | [**string**] | FlowId to be updated | defaults to undefined


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


