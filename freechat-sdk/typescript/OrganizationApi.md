# .OrganizationApi

All URIs are relative to *https://freechat.fun*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getOwners**](OrganizationApi.md#getOwners) | **GET** /api/v1/org/owners | Get My Superior Relationship
[**getOwnersDot**](OrganizationApi.md#getOwnersDot) | **GET** /api/v1/org/owners/dot | Get DOT of Superior Relationship
[**getSubordinateOwners**](OrganizationApi.md#getSubordinateOwners) | **GET** /api/v1/org/manage/{username}/owners | Get Superior Relationship
[**getSubordinateSubordinates**](OrganizationApi.md#getSubordinateSubordinates) | **GET** /api/v1/org/manage/{username}/subordinates | Get Subordinate Relationship
[**getSubordinates**](OrganizationApi.md#getSubordinates) | **GET** /api/v1/org/subordinates | Get My Subordinate Relationship
[**getSubordinatesDot**](OrganizationApi.md#getSubordinatesDot) | **GET** /api/v1/org/subordinates/dot | Get DOT of Subordinate Relationship
[**listSubordinateAuthorities**](OrganizationApi.md#listSubordinateAuthorities) | **GET** /api/v1/org/authority/{username} | List Subordinate Permissions
[**removeSubordinateSubordinatesTree**](OrganizationApi.md#removeSubordinateSubordinatesTree) | **DELETE** /api/v1/org/manage/{username}/subordinates | Clear Subordinate Relationship
[**updateSubordinateAuthorities**](OrganizationApi.md#updateSubordinateAuthorities) | **PUT** /api/v1/org/authority/{username} | Update Subordinate Permissions
[**updateSubordinateOwners**](OrganizationApi.md#updateSubordinateOwners) | **PUT** /api/v1/org/manage/{username}/owners | Update Superior Relationship
[**updateSubordinateSubordinates**](OrganizationApi.md#updateSubordinateSubordinates) | **PUT** /api/v1/org/manage/{username}/subordinates | Update Subordinate Relationship


# **getOwners**
> Array<string> getOwners()

Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetOwnersRequest = {
  // string | Whether to return virtual reported owners (optional)
  all: "all_example",
};

apiInstance.getOwners(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | [**string**] | Whether to return virtual reported owners | (optional) defaults to undefined


### Return type

**Array<string>**

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

# **getOwnersDot**
> string getOwnersDot()

Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetOwnersDotRequest = {
  // string | Whether to return virtual reported owners (optional)
  all: "all_example",
};

apiInstance.getOwnersDot(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | [**string**] | Whether to return virtual reported owners | (optional) defaults to undefined


### Return type

**string**

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

# **getSubordinateOwners**
> Array<string> getSubordinateOwners()

Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetSubordinateOwnersRequest = {
  // string | The account being queried, must be a subordinate account of the current account
  username: "username_example",
  // string | Whether to return virtual reported owners (optional)
  all: "all_example",
};

apiInstance.getSubordinateOwners(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | The account being queried, must be a subordinate account of the current account | defaults to undefined
 **all** | [**string**] | Whether to return virtual reported owners | (optional) defaults to undefined


### Return type

**Array<string>**

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

# **getSubordinateSubordinates**
> Array<string> getSubordinateSubordinates()

Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetSubordinateSubordinatesRequest = {
  // string | The account being queried, must be a subordinate account of the current account
  username: "username_example",
  // string | Whether to return virtual managed subordinates (optional)
  all: "all_example",
};

apiInstance.getSubordinateSubordinates(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | The account being queried, must be a subordinate account of the current account | defaults to undefined
 **all** | [**string**] | Whether to return virtual managed subordinates | (optional) defaults to undefined


### Return type

**Array<string>**

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

# **getSubordinates**
> Array<string> getSubordinates()

Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetSubordinatesRequest = {
  // string | Whether to return virtual managed subordinates (optional)
  all: "all_example",
};

apiInstance.getSubordinates(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | [**string**] | Whether to return virtual managed subordinates | (optional) defaults to undefined


### Return type

**Array<string>**

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

# **getSubordinatesDot**
> string getSubordinatesDot()

Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiGetSubordinatesDotRequest = {
  // string | Whether to return virtual managed subordinates (optional)
  all: "all_example",
};

apiInstance.getSubordinatesDot(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | [**string**] | Whether to return virtual managed subordinates | (optional) defaults to undefined


### Return type

**string**

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

# **listSubordinateAuthorities**
> Set<string> listSubordinateAuthorities()

List the permission list of the subordinate account.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiListSubordinateAuthoritiesRequest = {
  // string | Username
  username: "username_example",
};

apiInstance.listSubordinateAuthorities(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | Username | defaults to undefined


### Return type

**Set<string>**

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

# **removeSubordinateSubordinatesTree**
> boolean removeSubordinateSubordinatesTree()

Fully delete the direct subordinate relationship of the subordinate account.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiRemoveSubordinateSubordinatesTreeRequest = {
  // string | The account being operated, must be a subordinate account of the current account
  username: "username_example",
};

apiInstance.removeSubordinateSubordinatesTree(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | [**string**] | The account being operated, must be a subordinate account of the current account | defaults to undefined


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

# **updateSubordinateAuthorities**
> boolean updateSubordinateAuthorities(requestBody)

Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiUpdateSubordinateAuthoritiesRequest = {
  // string | Username
  username: "username_example",
  // Set<string> | Permission list
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.updateSubordinateAuthorities(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Set<string>**| Permission list |
 **username** | [**string**] | Username | defaults to undefined


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

# **updateSubordinateOwners**
> boolean updateSubordinateOwners(requestBody)

Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiUpdateSubordinateOwnersRequest = {
  // string | The account being operated, must be a subordinate account of the current account
  username: "username_example",
  // Array<string> | The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.updateSubordinateOwners(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account |
 **username** | [**string**] | The account being operated, must be a subordinate account of the current account | defaults to undefined


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

# **updateSubordinateSubordinates**
> boolean updateSubordinateSubordinates(requestBody)

Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example


```typescript
import {  } from '';
import * as fs from 'fs';

const configuration = .createConfiguration();
const apiInstance = new .OrganizationApi(configuration);

let body:.OrganizationApiUpdateSubordinateSubordinatesRequest = {
  // string | The account being operated, must be a subordinate account of the current account
  username: "username_example",
  // Array<string> | The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
  requestBody: [
    "requestBody_example",
  ],
};

apiInstance.updateSubordinateSubordinates(body).then((data:any) => {
  console.log('API called successfully. Returned data: ' + data);
}).catch((error:any) => console.error(error));
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestBody** | **Array<string>**| The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account |
 **username** | [**string**] | The account being operated, must be a subordinate account of the current account | defaults to undefined


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


