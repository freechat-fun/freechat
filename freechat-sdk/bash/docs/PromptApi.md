# PromptApi

All URIs are relative to **

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



## applyPromptRef

Apply Parameters to Prompt Record

Apply parameters to prompt record.

### Example

```bash
freechat applyPromptRef
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptRefDTO** | [**PromptRefDTO**](PromptRefDTO.md) | Prompt record |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## applyPromptTemplate

Apply Parameters to Prompt Template

Apply parameters to prompt template.

### Example

```bash
freechat applyPromptTemplate
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptTemplateDTO** | [**PromptTemplateDTO**](PromptTemplateDTO.md) | String type prompt template |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchPromptDetails

Batch Search Prompt Details

Batch call shortcut for /api/v2/prompt/details/search.

### Example

```bash
freechat batchSearchPromptDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**array[PromptQueryDTO]**](PromptQueryDTO.md) | Query conditions |

### Return type

**array[array[PromptDetailsDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## batchSearchPromptSummary

Batch Search Prompt Summaries

Batch call shortcut for /api/v2/prompt/search.

### Example

```bash
freechat batchSearchPromptSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**array[PromptQueryDTO]**](PromptQueryDTO.md) | Query conditions |

### Return type

**array[array[PromptSummaryDTO]]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## clonePrompt

Clone Prompt

Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different:
- Version number is 1
- Visibility is private
- The parent prompt is the source promptId
- The creation time is the current moment.
- All statistical indicators are zeroed.

Return the new promptId.

### Example

```bash
freechat clonePrompt promptId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | The referenced promptId | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## clonePrompts

Batch Clone Prompts

Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

```bash
freechat clonePrompts
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**array[integer]**](integer.md) | List of prompt information to be created |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countPrompts

Calculate Number of Prompts

Calculate the number of prompts according to the specified query conditions.

### Example

```bash
freechat countPrompts
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## countPublicPrompts

Calculate Number of Public Prompts

Calculate the number of prompts according to the specified query conditions.

### Example

```bash
freechat countPublicPrompts
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md) | Query conditions |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createPrompt

Create Prompt

Create a prompt, required fields:
- Prompt name
- Prompt content
- Applicable model

Limitations:
- Description: 300 characters
- Template: 1000 characters
- Example: 2000 characters
- Tags: 5
- Parameters: 10

### Example

```bash
freechat createPrompt
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | [**PromptCreateDTO**](PromptCreateDTO.md) | Information of the prompt to be created |

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## createPrompts

Batch Create Prompts

Batch create multiple prompts. Ensure transactionality, return the promptId list after success.

### Example

```bash
freechat createPrompts
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptCreateDTO** | [**array[PromptCreateDTO]**](PromptCreateDTO.md) | List of prompt information to be created |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePrompt

Delete Prompt

Delete prompt. Returns success or failure.

### Example

```bash
freechat deletePrompt promptId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | The promptId to be deleted | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePromptByName

Delete Prompt by Name

Delete prompt by name. return the list of successfully deleted promptIds.

### Example

```bash
freechat deletePromptByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | The prompt name to be deleted | [default to null]

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deletePrompts

Batch Delete Prompts

Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.

### Example

```bash
freechat deletePrompts
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | [**array[integer]**](integer.md) | List of promptIds to be deleted |

### Return type

**array[integer]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## existsPromptName

Check If Prompt Name Exists

Check if the prompt name already exists.

### Example

```bash
freechat existsPromptName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Name | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getPromptDetails

Get Prompt Details

Get prompt detailed information.

### Example

```bash
freechat getPromptDetails promptId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | PromptId to be obtained | [default to null]

### Return type

[**PromptDetailsDTO**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getPromptSummary

Get Prompt Summary

Get prompt summary information.

### Example

```bash
freechat getPromptSummary promptId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | PromptId to be obtained | [default to null]

### Return type

[**PromptSummaryDTO**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listPromptVersionsByName

List Versions by Prompt Name

List the versions and corresponding promptIds by prompt name.

### Example

```bash
freechat listPromptVersionsByName name=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **string** | Prompt name | [default to null]

### Return type

[**array[PromptItemForNameDTO]**](PromptItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## newPromptName

Create New Prompt Name

Create a new prompt name starting with a desired name.

### Example

```bash
freechat newPromptName desired=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **desired** | **string** | Desired name | [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: text/plain

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## publishPrompt

Publish Prompt

Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.

### Example

```bash
freechat publishPrompt promptId=value visibility=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | The promptId to be published | [default to null]
 **visibility** | **string** | Visibility: public | private | ... | [default to null]

### Return type

**integer**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPromptDetails

Search Prompt Details

Same as /api/v2/prompt/search, but returns detailed information of the prompt.

### Example

```bash
freechat searchPromptDetails
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md) | Query conditions |

### Return type

[**array[PromptDetailsDTO]**](PromptDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPromptSummary

Search Prompt Summary

Search prompts:
- Specifiable query fields, and relationship:
  - Scope: private, public_org or public. Private can only search this account.
  - Username: exact match, only valid when searching public, public_org. If not specified, search all users.
  - Tags: exact match (support and, or logic).
  - Model type: exact match (support and, or logic).
  - Name: left match.
  - Type, exact match: string (default) | chat.
  - Language, exact match.
  - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the prompt summary content.
- Support pagination.

### Example

```bash
freechat searchPromptSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md) | Query conditions |

### Return type

[**array[PromptSummaryDTO]**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## searchPublicPromptSummary

Search Public Prompt Summary

Search prompts:
- Specifiable query fields, and relationship:
  - Scope: public(fixed).
  - Username: exact match. If not specified, search all users.
  - Tags: exact match (support and, or logic).
  - Model type: exact match (support and, or logic).
  - Name: left match.
  - Type, exact match: string (default) | chat.
  - Language, exact match.
  - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user's general search does not guarantee timeliness.
- A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending.
- The search result is the prompt summary content.
- Support pagination.

### Example

```bash
freechat searchPublicPromptSummary
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptQueryDTO** | [**PromptQueryDTO**](PromptQueryDTO.md) | Query conditions |

### Return type

[**array[PromptSummaryDTO]**](PromptSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## sendPrompt

Send Prompt

Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.

### Example

```bash
freechat sendPrompt
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md) | Call parameters |

### Return type

[**LlmResultDTO**](LlmResultDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## streamSendPrompt

Send Prompt by Streaming Back

Refer to /api/v2/prompt/send, stream back chunks of the response.

### Example

```bash
freechat streamSendPrompt
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptAiParamDTO** | [**PromptAiParamDTO**](PromptAiParamDTO.md) | Call parameters |

### Return type

[**SseEmitter**](SseEmitter.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/event-stream

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updatePrompt

Update Prompt

Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.

### Example

```bash
freechat updatePrompt promptId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **promptId** | **integer** | The promptId to be updated | [default to null]
 **promptUpdateDTO** | [**PromptUpdateDTO**](PromptUpdateDTO.md) | The prompt information to be updated |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

