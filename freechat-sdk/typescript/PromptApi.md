# .PromptApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**applyPromptRef**](PromptApi.md#applyPromptRef) | **POST** /api/v1/prompt/apply/ref | Apply Parameters to Prompt Record
[**applyPromptTemplate**](PromptApi.md#applyPromptTemplate) | **POST** /api/v1/prompt/apply/template | Apply Parameters to String Prompt Template
[**batchSearchPromptDetails**](PromptApi.md#batchSearchPromptDetails) | **POST** /api/v1/prompt/batch/details/search | Batch Search Prompt Details
[**batchSearchPromptSummary**](PromptApi.md#batchSearchPromptSummary) | **POST** /api/v1/prompt/batch/search | Batch Search Prompt Summaries
[**clonePrompt**](PromptApi.md#clonePrompt) | **POST** /api/v1/prompt/clone/{promptId} | Clone Prompt
[**clonePrompts**](PromptApi.md#clonePrompts) | **POST** /api/v1/prompt/batch/clone | Batch Clone Prompts
[**countPrompts**](PromptApi.md#countPrompts) | **POST** /api/v1/prompt/count | Calculate Number of Prompts
[**createPrompt**](PromptApi.md#createPrompt) | **POST** /api/v1/prompt | Create Prompt
[**createPrompts**](PromptApi.md#createPrompts) | **POST** /api/v1/prompt/batch | Batch Create Prompts
[**deletePrompt**](PromptApi.md#deletePrompt) | **DELETE** /api/v1/prompt/{promptId} | Delete Prompt
[**deletePrompts**](PromptApi.md#deletePrompts) | **DELETE** /api/v1/prompt/batch | Batch Delete Prompts
[**existsName**](PromptApi.md#existsName) | **GET** /api/v1/prompt/exists/name/{name} | Check If Name Exists
[**getPromptDetails**](PromptApi.md#getPromptDetails) | **GET** /api/v1/prompt/details/{promptId} | Get Prompt Details
[**getPromptSummary**](PromptApi.md#getPromptSummary) | **GET** /api/v1/prompt/summary/{promptId} | Get Prompt Summary
[**listPromptVersionsByName**](PromptApi.md#listPromptVersionsByName) | **POST** /api/v1/prompt/versions/{name} | List Versions by Prompt Name
[**publishPrompt**](PromptApi.md#publishPrompt) | **POST** /api/v1/prompt/publish/{promptId}/{visibility} | Publish Prompt
[**searchPromptDetails**](PromptApi.md#searchPromptDetails) | **POST** /api/v1/prompt/details/search | Search Prompt Details
[**searchPromptSummary**](PromptApi.md#searchPromptSummary) | **POST** /api/v1/prompt/search | Search Prompt Summary
[**sendPrompt**](PromptApi.md#sendPrompt) | **POST** /api/v1/prompt/send | Send Prompt
[**streamSendPrompt**](PromptApi.md#streamSendPrompt) | **POST** /api/v1/prompt/send/stream | Send Prompt by Streaming Back
[**updatePrompt**](PromptApi.md#updatePrompt) | **PUT** /api/v1/prompt/{promptId} | Update Prompt


# **applyPromptRef**
> string applyPromptRef(promptRefDTO)

Apply parameters to prompt record.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiApplyPromptRefRequest = {
  // PromptRefDTO | Prompt record
  promptRefDTO: {
    promptId: "promptId_example",
    variables: {
      "key": {},
    },
    draft: true,
  },
};

apiInstance.applyPromptRef(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptRefDTO** | **PromptRefDTO**| Prompt record |


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

# **applyPromptTemplate**
> string applyPromptTemplate(promptTemplateDTO)

Apply parameters to string type prompt template.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiApplyPromptTemplateRequest = {
  // PromptTemplateDTO | String type prompt template
  promptTemplateDTO: {
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        content: "content_example",
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
      ],
    },
    variables: {
      "key": {},
    },
    format: "format_example",
  },
};

apiInstance.applyPromptTemplate(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTemplateDTO** | **PromptTemplateDTO**| String type prompt template |


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

# **batchSearchPromptDetails**
> Array<Array<PromptDetailsDTO>> batchSearchPromptDetails(promptQueryDTO)

Batch call shortcut for /api/v1/prompt/details/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiBatchSearchPromptDetailsRequest = {
  // Array<PromptQueryDTO> | Query conditions
  promptQueryDTO: [
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

apiInstance.batchSearchPromptDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | **Array<PromptQueryDTO>**| Query conditions |


### Return type

**Array<Array<PromptDetailsDTO>>**

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

# **batchSearchPromptSummary**
> Array<Array<PromptSummaryDTO>> batchSearchPromptSummary(promptQueryDTO)

Batch call shortcut for /api/v1/prompt/search.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiBatchSearchPromptSummaryRequest = {
  // Array<PromptQueryDTO> | Query conditions
  promptQueryDTO: [
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

apiInstance.batchSearchPromptSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | **Array<PromptQueryDTO>**| Query conditions |


### Return type

**Array<Array<PromptSummaryDTO>>**

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

# **clonePrompt**
> string clonePrompt()

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiClonePromptRequest = {
  // string | The referenced promptId
  promptId: "promptId_example",
};

apiInstance.clonePrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**string**] | The referenced promptId | defaults to undefined


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

# **clonePrompts**
> Array<string> clonePrompts(requestBody)

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiClonePromptsRequest = {
  // Array<string> | List of prompt information to be created
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.clonePrompts(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of prompt information to be created |


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

# **countPrompts**
> number countPrompts(promptQueryDTO)

Calculate the number of prompts according to the specified query conditions.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiCountPromptsRequest = {
  // PromptQueryDTO | Query conditions
  promptQueryDTO: {
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

apiInstance.countPrompts(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | **PromptQueryDTO**| Query conditions |


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

# **createPrompt**
> string createPrompt(promptCreateDTO)

Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiCreatePromptRequest = {
  // PromptCreateDTO | Information of the prompt to be created
  promptCreateDTO: {
    parentId: "parentId_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        content: "content_example",
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
      ],
    },
    format: "format_example",
    lang: "lang_example",
    example: "example_example",
    inputs: "inputs_example",
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

apiInstance.createPrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | **PromptCreateDTO**| Information of the prompt to be created |


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

# **createPrompts**
> Array<string> createPrompts(promptCreateDTO)

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiCreatePromptsRequest = {
  // Array<PromptCreateDTO> | List of prompt information to be created
  promptCreateDTO: [
    {
      parentId: "parentId_example",
      visibility: "visibility_example",
      name: "name_example",
      description: "description_example",
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            content: "content_example",
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
          },
        ],
      },
      format: "format_example",
      lang: "lang_example",
      example: "example_example",
      inputs: "inputs_example",
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

apiInstance.createPrompts(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | **Array<PromptCreateDTO>**| List of prompt information to be created |


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

# **deletePrompt**
> boolean deletePrompt()

Delete prompt. Returns success or failure.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiDeletePromptRequest = {
  // string | The promptId to be deleted
  promptId: "promptId_example",
};

apiInstance.deletePrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**string**] | The promptId to be deleted | defaults to undefined


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

# **deletePrompts**
> Array<string> deletePrompts(requestBody)

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiDeletePromptsRequest = {
  // Array<string> | List of promptIds to be deleted
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.deletePrompts(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| List of promptIds to be deleted |


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

# **existsName**
> boolean existsName()

Check if the name already exists.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiExistsNameRequest = {
  // string | Name
  name: "name_example",
};

apiInstance.existsName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Name | defaults to undefined


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

# **getPromptDetails**
> PromptDetailsDTO getPromptDetails()

Get prompt detailed information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiGetPromptDetailsRequest = {
  // string | PromptId to be obtained
  promptId: "promptId_example",
};

apiInstance.getPromptDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**string**] | PromptId to be obtained | defaults to undefined


### Return type

**PromptDetailsDTO**

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

# **getPromptSummary**
> PromptSummaryDTO getPromptSummary()

Get prompt summary information.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiGetPromptSummaryRequest = {
  // string | PromptId to be obtained
  promptId: "promptId_example",
};

apiInstance.getPromptSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**string**] | PromptId to be obtained | defaults to undefined


### Return type

**PromptSummaryDTO**

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

# **listPromptVersionsByName**
> Array<PromptItemForNameDTO> listPromptVersionsByName()

List the versions and corresponding promptIds by prompt name.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiListPromptVersionsByNameRequest = {
  // string | Prompt name
  name: "name_example",
};

apiInstance.listPromptVersionsByName(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | Prompt name | defaults to undefined


### Return type

**Array<PromptItemForNameDTO>**

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

# **publishPrompt**
> string publishPrompt()

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiPublishPromptRequest = {
  // string | The promptId to be published
  promptId: "promptId_example",
  // string | Visibility: public | private | ...
  visibility: "visibility_example",
};

apiInstance.publishPrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**string**] | The promptId to be published | defaults to undefined
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

# **searchPromptDetails**
> Array<PromptDetailsDTO> searchPromptDetails(promptQueryDTO)

Same as /api/v1/prompt/search, but returns detailed information of the prompt.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiSearchPromptDetailsRequest = {
  // PromptQueryDTO | Query conditions
  promptQueryDTO: {
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

apiInstance.searchPromptDetails(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | **PromptQueryDTO**| Query conditions |


### Return type

**Array<PromptDetailsDTO>**

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

# **searchPromptSummary**
> Array<PromptSummaryDTO> searchPromptSummary(promptQueryDTO)

Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiSearchPromptSummaryRequest = {
  // PromptQueryDTO | Query conditions
  promptQueryDTO: {
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

apiInstance.searchPromptSummary(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | **PromptQueryDTO**| Query conditions |


### Return type

**Array<PromptSummaryDTO>**

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

# **sendPrompt**
> LlmResultDTO sendPrompt(promptAiParamDTO)

Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiSendPromptRequest = {
  // PromptAiParamDTO | Call parameters
  promptAiParamDTO: {
    prompt: "prompt_example",
    promptTemplate: {
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            content: "content_example",
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
          },
        ],
      },
      variables: {
        "key": {},
      },
      format: "format_example",
    },
    promptRef: {
      promptId: "promptId_example",
      variables: {
        "key": {},
      },
      draft: true,
    },
    params: {
      "key": {},
    },
  },
};

apiInstance.sendPrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | **PromptAiParamDTO**| Call parameters |


### Return type

**LlmResultDTO**

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

# **streamSendPrompt**
> SseEmitter streamSendPrompt(promptAiParamDTO)

Refer to /api/v1/prompt/send, stream back chunks of the response.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiStreamSendPromptRequest = {
  // PromptAiParamDTO | Call parameters
  promptAiParamDTO: {
    prompt: "prompt_example",
    promptTemplate: {
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            content: "content_example",
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
          },
        ],
      },
      variables: {
        "key": {},
      },
      format: "format_example",
    },
    promptRef: {
      promptId: "promptId_example",
      variables: {
        "key": {},
      },
      draft: true,
    },
    params: {
      "key": {},
    },
  },
};

apiInstance.streamSendPrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | **PromptAiParamDTO**| Call parameters |


### Return type

**SseEmitter**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/event-stream


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **updatePrompt**
> boolean updatePrompt(promptUpdateDTO)

Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .PromptApi(configuration);

let body:.PromptApiUpdatePromptRequest = {
  // string | The promptId to be updated
  promptId: "promptId_example",
  // PromptUpdateDTO | The prompt information to be updated
  promptUpdateDTO: {
    parentId: "parentId_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        content: "content_example",
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          content: "content_example",
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          gmtCreate: new Date('1970-01-01T00:00:00.00Z'),
        },
      ],
    },
    format: "format_example",
    lang: "lang_example",
    example: "example_example",
    inputs: "inputs_example",
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

apiInstance.updatePrompt(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptUpdateDTO** | **PromptUpdateDTO**| The prompt information to be updated |
 **promptId** | [**string**] | The promptId to be updated | defaults to undefined


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


