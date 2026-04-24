# .TagManagerForBizAdminApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createTag**](TagManagerForBizAdminApi.md#createTag) | **POST** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Create Tag
[**deleteTag**](TagManagerForBizAdminApi.md#deleteTag) | **DELETE** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Delete Tag


# **createTag**
> boolean createTag()

Create a tag, tags created by the administrator cannot be deleted by ordinary users.

### Example


```typescript
import { createConfiguration, TagManagerForBizAdminApi } from '';
import type { TagManagerForBizAdminApiCreateTagRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TagManagerForBizAdminApi(configuration);

const request: TagManagerForBizAdminApiCreateTagRequest = {
    // Tag type (prompt, agent, plugin...)
  referType: "referType_example",
    // Resource identifier of the tag
  referId: "referId_example",
    // Tag content
  tag: "tag_example",
};

const data = await apiInstance.createTag(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **referType** | [**string**] | Tag type (prompt, agent, plugin...) | defaults to undefined
 **referId** | [**string**] | Resource identifier of the tag | defaults to undefined
 **tag** | [**string**] | Tag content | defaults to undefined


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

# **deleteTag**
> boolean deleteTag()

Delete a tag, any tag created by anyone can be deleted.

### Example


```typescript
import { createConfiguration, TagManagerForBizAdminApi } from '';
import type { TagManagerForBizAdminApiDeleteTagRequest } from '';

const configuration = createConfiguration();
const apiInstance = new TagManagerForBizAdminApi(configuration);

const request: TagManagerForBizAdminApiDeleteTagRequest = {
    // Tag type (prompt, agent, plugin...)
  referType: "referType_example",
    // Resource identifier of the tag
  referId: "referId_example",
    // Tag content
  tag: "tag_example",
};

const data = await apiInstance.deleteTag(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **referType** | [**string**] | Tag type (prompt, agent, plugin...) | defaults to undefined
 **referId** | [**string**] | Resource identifier of the tag | defaults to undefined
 **tag** | [**string**] | Tag content | defaults to undefined


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


