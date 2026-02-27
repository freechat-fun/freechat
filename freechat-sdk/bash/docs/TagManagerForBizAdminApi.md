# TagManagerForBizAdminApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**createTag**](TagManagerForBizAdminApi.md#createTag) | **POST** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Create Tag
[**deleteTag**](TagManagerForBizAdminApi.md#deleteTag) | **DELETE** /api/v2/biz/admin/tag/{referType}/{referId}/{tag} | Delete Tag



## createTag

Create Tag

Create a tag, tags created by the administrator cannot be deleted by ordinary users.

### Example

```bash
freechat-cli createTag referType=value referId=value tag=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **referType** | **string** | Tag type (prompt, agent, plugin...) | [default to null]
 **referId** | **string** | Resource identifier of the tag | [default to null]
 **tag** | **string** | Tag content | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteTag

Delete Tag

Delete a tag, any tag created by anyone can be deleted.

### Example

```bash
freechat-cli deleteTag referType=value referId=value tag=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **referType** | **string** | Tag type (prompt, agent, plugin...) | [default to null]
 **referId** | **string** | Resource identifier of the tag | [default to null]
 **tag** | **string** | Tag content | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

