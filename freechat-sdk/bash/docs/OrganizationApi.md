# OrganizationApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**getOwners**](OrganizationApi.md#getOwners) | **GET** /api/v2/org/owners | Get My Superior Relationship
[**getOwnersDot**](OrganizationApi.md#getOwnersDot) | **GET** /api/v2/org/owners/dot | Get DOT of Superior Relationship
[**getSubordinateOwners**](OrganizationApi.md#getSubordinateOwners) | **GET** /api/v2/org/manage/{username}/owners | Get Superior Relationship
[**getSubordinateSubordinates**](OrganizationApi.md#getSubordinateSubordinates) | **GET** /api/v2/org/manage/{username}/subordinates | Get Subordinate Relationship
[**getSubordinates**](OrganizationApi.md#getSubordinates) | **GET** /api/v2/org/subordinates | Get My Subordinate Relationship
[**getSubordinatesDot**](OrganizationApi.md#getSubordinatesDot) | **GET** /api/v2/org/subordinates/dot | Get DOT of Subordinate Relationship
[**listSubordinateAuthorities**](OrganizationApi.md#listSubordinateAuthorities) | **GET** /api/v2/org/authority/{username} | List Subordinate Permissions
[**removeSubordinateSubordinatesTree**](OrganizationApi.md#removeSubordinateSubordinatesTree) | **DELETE** /api/v2/org/manage/{username}/subordinates | Clear Subordinate Relationship
[**updateSubordinateAuthorities**](OrganizationApi.md#updateSubordinateAuthorities) | **PUT** /api/v2/org/authority/{username} | Update Subordinate Permissions
[**updateSubordinateOwners**](OrganizationApi.md#updateSubordinateOwners) | **PUT** /api/v2/org/manage/{username}/owners | Update Superior Relationship
[**updateSubordinateSubordinates**](OrganizationApi.md#updateSubordinateSubordinates) | **PUT** /api/v2/org/manage/{username}/subordinates | Update Subordinate Relationship



## getOwners

Get My Superior Relationship

Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.

### Example

```bash
freechat-cli getOwners  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **string** | Whether to return virtual reported owners | [optional] [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getOwnersDot

Get DOT of Superior Relationship

Same as /api/v2/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example

```bash
freechat-cli getOwnersDot  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **string** | Whether to return virtual reported owners | [optional] [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getSubordinateOwners

Get Superior Relationship

Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/>
By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.

### Example

```bash
freechat-cli getSubordinateOwners username=value  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The account being queried, must be a subordinate account of the current account | [default to null]
 **all** | **string** | Whether to return virtual reported owners | [optional] [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getSubordinateSubordinates

Get Subordinate Relationship

Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example

```bash
freechat-cli getSubordinateSubordinates username=value  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The account being queried, must be a subordinate account of the current account | [default to null]
 **all** | **string** | Whether to return virtual managed subordinates | [optional] [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getSubordinates

Get My Subordinate Relationship

Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example

```bash
freechat-cli getSubordinates  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **string** | Whether to return virtual managed subordinates | [optional] [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getSubordinatesDot

Get DOT of Subordinate Relationship

Same as /api/v2/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example

```bash
freechat-cli getSubordinatesDot  all=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **string** | Whether to return virtual managed subordinates | [optional] [default to null]

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listSubordinateAuthorities

List Subordinate Permissions

List the permission list of the subordinate account.

### Example

```bash
freechat-cli listSubordinateAuthorities username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]

### Return type

**array[string]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## removeSubordinateSubordinatesTree

Clear Subordinate Relationship

Fully delete the direct subordinate relationship of the subordinate account.

### Example

```bash
freechat-cli removeSubordinateSubordinatesTree username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The account being operated, must be a subordinate account of the current account | [default to null]

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateSubordinateAuthorities

Update Subordinate Permissions

Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.

### Example

```bash
freechat-cli updateSubordinateAuthorities username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | Username | [default to null]
 **requestBody** | [**Set[string]**](string.md) | Permission list |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateSubordinateOwners

Update Superior Relationship

Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example

```bash
freechat-cli updateSubordinateOwners username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The account being operated, must be a subordinate account of the current account | [default to null]
 **requestBody** | [**array[string]**](string.md) | The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateSubordinateSubordinates

Update Subordinate Relationship

Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example

```bash
freechat-cli updateSubordinateSubordinates username=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The account being operated, must be a subordinate account of the current account | [default to null]
 **requestBody** | [**array[string]**](string.md) | The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account |

### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

