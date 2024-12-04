# .PromptApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**applyPromptRef**](PromptApi.md#applyPromptRef) | **POST** /api/v2/prompt/apply/ref | Apply Parameters to Prompt Record
[**applyPromptTemplate**](PromptApi.md#applyPromptTemplate) | **POST** /api/v2/prompt/apply/template | Apply Parameters to Prompt Template
[**batchSearchPromptDetails**](PromptApi.md#batchSearchPromptDetails) | **POST** /api/v2/prompt/batch/details/search | Batch Search Prompt Details
[**batchSearchPromptSummary**](PromptApi.md#batchSearchPromptSummary) | **POST** /api/v2/prompt/batch/search | Batch Search Prompt Summaries
[**clonePrompt**](PromptApi.md#clonePrompt) | **POST** /api/v2/prompt/clone/{promptId} | Clone Prompt
[**clonePrompts**](PromptApi.md#clonePrompts) | **POST** /api/v2/prompt/batch/clone | Batch Clone Prompts
[**countPrompts**](PromptApi.md#countPrompts) | **POST** /api/v2/prompt/count | Calculate Number of Prompts
[**countPublicPrompts**](PromptApi.md#countPublicPrompts) | **POST** /api/v2/public/prompt/count | Calculate Number of Public Prompts
[**createPrompt**](PromptApi.md#createPrompt) | **POST** /api/v2/prompt | Create Prompt
[**createPrompts**](PromptApi.md#createPrompts) | **POST** /api/v2/prompt/batch | Batch Create Prompts
[**deletePrompt**](PromptApi.md#deletePrompt) | **DELETE** /api/v2/prompt/{promptId} | Delete Prompt
[**deletePromptByName**](PromptApi.md#deletePromptByName) | **DELETE** /api/v2/prompt/name/{name} | Delete Prompt by Name
[**deletePrompts**](PromptApi.md#deletePrompts) | **DELETE** /api/v2/prompt/batch | Batch Delete Prompts
[**existsPromptName**](PromptApi.md#existsPromptName) | **GET** /api/v2/prompt/exists/name/{name} | Check If Prompt Name Exists
[**getPromptDetails**](PromptApi.md#getPromptDetails) | **GET** /api/v2/prompt/details/{promptId} | Get Prompt Details
[**getPromptSummary**](PromptApi.md#getPromptSummary) | **GET** /api/v2/prompt/summary/{promptId} | Get Prompt Summary
[**listPromptVersionsByName**](PromptApi.md#listPromptVersionsByName) | **POST** /api/v2/prompt/versions/{name} | List Versions by Prompt Name
[**newPromptName**](PromptApi.md#newPromptName) | **GET** /api/v2/prompt/create/name/{desired} | Create New Prompt Name
[**publishPrompt**](PromptApi.md#publishPrompt) | **POST** /api/v2/prompt/publish/{promptId}/{visibility} | Publish Prompt
[**searchPromptDetails**](PromptApi.md#searchPromptDetails) | **POST** /api/v2/prompt/details/search | Search Prompt Details
[**searchPromptSummary**](PromptApi.md#searchPromptSummary) | **POST** /api/v2/prompt/search | Search Prompt Summary
[**searchPublicPromptSummary**](PromptApi.md#searchPublicPromptSummary) | **POST** /api/v2/public/prompt/search | Search Public Prompt Summary
[**sendPrompt**](PromptApi.md#sendPrompt) | **POST** /api/v2/prompt/send | Send Prompt
[**streamSendPrompt**](PromptApi.md#streamSendPrompt) | **POST** /api/v2/prompt/send/stream | Send Prompt by Streaming Back
[**updatePrompt**](PromptApi.md#updatePrompt) | **PUT** /api/v2/prompt/{promptId} | Update Prompt


# **applyPromptRef**
> string applyPromptRef(promptRefDTO)

Apply parameters to prompt record.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiApplyPromptRefRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiApplyPromptRefRequest = {
    // Prompt record
  promptRefDTO: {
    promptId: 1,
    variables: {
      "key": {},
    },
    draft: true,
  },
};

const data = await apiInstance.applyPromptRef(request);
console.log('API called successfully. Returned data:', data);
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

Apply parameters to prompt template.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiApplyPromptTemplateRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiApplyPromptTemplateRequest = {
    // String type prompt template
  promptTemplateDTO: {
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        contents: [
          {
            type: "type_example",
            content: "content_example",
          },
        ],
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        context: "context_example",
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
        },
      ],
    },
    variables: {
      "key": {},
    },
    format: "format_example",
  },
};

const data = await apiInstance.applyPromptTemplate(request);
console.log('API called successfully. Returned data:', data);
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

Batch call shortcut for /api/v2/prompt/details/search.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiBatchSearchPromptDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiBatchSearchPromptDetailsRequest = {
    // Query conditions
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

const data = await apiInstance.batchSearchPromptDetails(request);
console.log('API called successfully. Returned data:', data);
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

Batch call shortcut for /api/v2/prompt/search.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiBatchSearchPromptSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiBatchSearchPromptSummaryRequest = {
    // Query conditions
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

const data = await apiInstance.batchSearchPromptSummary(request);
console.log('API called successfully. Returned data:', data);
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
> number clonePrompt()

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiClonePromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiClonePromptRequest = {
    // The referenced promptId
  promptId: 1,
};

const data = await apiInstance.clonePrompt(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**number**] | The referenced promptId | defaults to undefined


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

# **clonePrompts**
> Array<number> clonePrompts(requestBody)

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiClonePromptsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiClonePromptsRequest = {
    // List of prompt information to be created
  requestBody: [
    1,
  ],
};

const data = await apiInstance.clonePrompts(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<number>**| List of prompt information to be created |


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

# **countPrompts**
> number countPrompts(promptQueryDTO)

Calculate the number of prompts according to the specified query conditions.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiCountPromptsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiCountPromptsRequest = {
    // Query conditions
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

const data = await apiInstance.countPrompts(request);
console.log('API called successfully. Returned data:', data);
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

# **countPublicPrompts**
> number countPublicPrompts(promptQueryDTO)

Calculate the number of prompts according to the specified query conditions.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiCountPublicPromptsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiCountPublicPromptsRequest = {
    // Query conditions
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

const data = await apiInstance.countPublicPrompts(request);
console.log('API called successfully. Returned data:', data);
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
> number createPrompt(promptCreateDTO)

Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiCreatePromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiCreatePromptRequest = {
    // Information of the prompt to be created
  promptCreateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        contents: [
          {
            type: "type_example",
            content: "content_example",
          },
        ],
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        context: "context_example",
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
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

const data = await apiInstance.createPrompt(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | **PromptCreateDTO**| Information of the prompt to be created |


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

# **createPrompts**
> Array<number> createPrompts(promptCreateDTO)

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiCreatePromptsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiCreatePromptsRequest = {
    // List of prompt information to be created
  promptCreateDTO: [
    {
      parentUid: "parentUid_example",
      visibility: "visibility_example",
      name: "name_example",
      description: "description_example",
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            contents: [
              {
                type: "type_example",
                content: "content_example",
              },
            ],
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            context: "context_example",
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

const data = await apiInstance.createPrompts(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | **Array<PromptCreateDTO>**| List of prompt information to be created |


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

# **deletePrompt**
> boolean deletePrompt()

Delete prompt. Returns success or failure.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiDeletePromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiDeletePromptRequest = {
    // The promptId to be deleted
  promptId: 1,
};

const data = await apiInstance.deletePrompt(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**number**] | The promptId to be deleted | defaults to undefined


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

# **deletePromptByName**
> Array<number> deletePromptByName()

Delete prompt by name. return the list of successfully deleted promptIds.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiDeletePromptByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiDeletePromptByNameRequest = {
    // The prompt name to be deleted
  name: "name_example",
};

const data = await apiInstance.deletePromptByName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | [**string**] | The prompt name to be deleted | defaults to undefined


### Return type

**Array<number>**

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
> Array<number> deletePrompts(requestBody)

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiDeletePromptsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiDeletePromptsRequest = {
    // List of promptIds to be deleted
  requestBody: [
    1,
  ],
};

const data = await apiInstance.deletePrompts(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<number>**| List of promptIds to be deleted |


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

# **existsPromptName**
> boolean existsPromptName()

Check if the prompt name already exists.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiExistsPromptNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiExistsPromptNameRequest = {
    // Name
  name: "name_example",
};

const data = await apiInstance.existsPromptName(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, PromptApi } from '';
import type { PromptApiGetPromptDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiGetPromptDetailsRequest = {
    // PromptId to be obtained
  promptId: 1,
};

const data = await apiInstance.getPromptDetails(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**number**] | PromptId to be obtained | defaults to undefined


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
import { createConfiguration, PromptApi } from '';
import type { PromptApiGetPromptSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiGetPromptSummaryRequest = {
    // PromptId to be obtained
  promptId: 1,
};

const data = await apiInstance.getPromptSummary(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**number**] | PromptId to be obtained | defaults to undefined


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
import { createConfiguration, PromptApi } from '';
import type { PromptApiListPromptVersionsByNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiListPromptVersionsByNameRequest = {
    // Prompt name
  name: "name_example",
};

const data = await apiInstance.listPromptVersionsByName(request);
console.log('API called successfully. Returned data:', data);
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

# **newPromptName**
> string newPromptName()

Create a new prompt name starting with a desired name.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiNewPromptNameRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiNewPromptNameRequest = {
    // Desired name
  desired: "desired_example",
};

const data = await apiInstance.newPromptName(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **desired** | [**string**] | Desired name | defaults to undefined


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

# **publishPrompt**
> number publishPrompt()

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiPublishPromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiPublishPromptRequest = {
    // The promptId to be published
  promptId: 1,
    // Visibility: public | private | ...
  visibility: "visibility_example",
};

const data = await apiInstance.publishPrompt(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | [**number**] | The promptId to be published | defaults to undefined
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

# **searchPromptDetails**
> Array<PromptDetailsDTO> searchPromptDetails(promptQueryDTO)

Same as /api/v2/prompt/search, but returns detailed information of the prompt.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiSearchPromptDetailsRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiSearchPromptDetailsRequest = {
    // Query conditions
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

const data = await apiInstance.searchPromptDetails(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, PromptApi } from '';
import type { PromptApiSearchPromptSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiSearchPromptSummaryRequest = {
    // Query conditions
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

const data = await apiInstance.searchPromptSummary(request);
console.log('API called successfully. Returned data:', data);
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

# **searchPublicPromptSummary**
> Array<PromptSummaryDTO> searchPublicPromptSummary(promptQueryDTO)

Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiSearchPublicPromptSummaryRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiSearchPublicPromptSummaryRequest = {
    // Query conditions
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

const data = await apiInstance.searchPublicPromptSummary(request);
console.log('API called successfully. Returned data:', data);
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
import { createConfiguration, PromptApi } from '';
import type { PromptApiSendPromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiSendPromptRequest = {
    // Call parameters
  promptAiParamDTO: {
    prompt: "prompt_example",
    promptTemplate: {
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            contents: [
              {
                type: "type_example",
                content: "content_example",
              },
            ],
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            context: "context_example",
          },
        ],
      },
      variables: {
        "key": {},
      },
      format: "format_example",
    },
    promptRef: {
      promptId: 1,
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

const data = await apiInstance.sendPrompt(request);
console.log('API called successfully. Returned data:', data);
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

Refer to /api/v2/prompt/send, stream back chunks of the response.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiStreamSendPromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiStreamSendPromptRequest = {
    // Call parameters
  promptAiParamDTO: {
    prompt: "prompt_example",
    promptTemplate: {
      template: "template_example",
      chatTemplate: {
        system: "system_example",
        messageToSend: {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
        },
        messages: [
          {
            role: "role_example",
            name: "name_example",
            contents: [
              {
                type: "type_example",
                content: "content_example",
              },
            ],
            toolCalls: [
              {
                id: "id_example",
                name: "name_example",
                arguments: "arguments_example",
              },
            ],
            context: "context_example",
          },
        ],
      },
      variables: {
        "key": {},
      },
      format: "format_example",
    },
    promptRef: {
      promptId: 1,
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

const data = await apiInstance.streamSendPrompt(request);
console.log('API called successfully. Returned data:', data);
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

Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.

### Example


```typescript
import { createConfiguration, PromptApi } from '';
import type { PromptApiUpdatePromptRequest } from '';

const configuration = createConfiguration();
const apiInstance = new PromptApi(configuration);

const request: PromptApiUpdatePromptRequest = {
    // The promptId to be updated
  promptId: 1,
    // The prompt information to be updated
  promptUpdateDTO: {
    parentUid: "parentUid_example",
    visibility: "visibility_example",
    name: "name_example",
    description: "description_example",
    template: "template_example",
    chatTemplate: {
      system: "system_example",
      messageToSend: {
        role: "role_example",
        name: "name_example",
        contents: [
          {
            type: "type_example",
            content: "content_example",
          },
        ],
        toolCalls: [
          {
            id: "id_example",
            name: "name_example",
            arguments: "arguments_example",
          },
        ],
        context: "context_example",
      },
      messages: [
        {
          role: "role_example",
          name: "name_example",
          contents: [
            {
              type: "type_example",
              content: "content_example",
            },
          ],
          toolCalls: [
            {
              id: "id_example",
              name: "name_example",
              arguments: "arguments_example",
            },
          ],
          context: "context_example",
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

const data = await apiInstance.updatePrompt(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptUpdateDTO** | **PromptUpdateDTO**| The prompt information to be updated |
 **promptId** | [**number**] | The promptId to be updated | defaults to undefined


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


