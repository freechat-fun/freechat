# freechat-sdk.OrganizationApi

All URIs are relative to *http://127.0.0.1:8080*

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



## getOwners

> [String] getOwners(opts)

Get My Superior Relationship

Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let opts = {
  'all': "all_example" // String | Whether to return virtual reported owners
};
apiInstance.getOwners(opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **String**| Whether to return virtual reported owners | [optional] 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getOwnersDot

> String getOwnersDot(opts)

Get DOT of Superior Relationship

Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let opts = {
  'all': "all_example" // String | Whether to return virtual reported owners
};
apiInstance.getOwnersDot(opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **String**| Whether to return virtual reported owners | [optional] 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getSubordinateOwners

> [String] getSubordinateOwners(username, opts)

Get Superior Relationship

Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt; By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship. 

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | The account being queried, must be a subordinate account of the current account
let opts = {
  'all': "all_example" // String | Whether to return virtual reported owners
};
apiInstance.getSubordinateOwners(username, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The account being queried, must be a subordinate account of the current account | 
 **all** | **String**| Whether to return virtual reported owners | [optional] 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getSubordinateSubordinates

> [String] getSubordinateSubordinates(username, opts)

Get Subordinate Relationship

Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | The account being queried, must be a subordinate account of the current account
let opts = {
  'all': "all_example" // String | Whether to return virtual managed subordinates
};
apiInstance.getSubordinateSubordinates(username, opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The account being queried, must be a subordinate account of the current account | 
 **all** | **String**| Whether to return virtual managed subordinates | [optional] 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getSubordinates

> [String] getSubordinates(opts)

Get My Subordinate Relationship

Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let opts = {
  'all': "all_example" // String | Whether to return virtual managed subordinates
};
apiInstance.getSubordinates(opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **String**| Whether to return virtual managed subordinates | [optional] 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getSubordinatesDot

> String getSubordinatesDot(opts)

Get DOT of Subordinate Relationship

Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let opts = {
  'all': "all_example" // String | Whether to return virtual managed subordinates
};
apiInstance.getSubordinatesDot(opts).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **all** | **String**| Whether to return virtual managed subordinates | [optional] 

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listSubordinateAuthorities

> [String] listSubordinateAuthorities(username)

List Subordinate Permissions

List the permission list of the subordinate account.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | Username
apiInstance.listSubordinateAuthorities(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 

### Return type

**[String]**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## removeSubordinateSubordinatesTree

> Boolean removeSubordinateSubordinatesTree(username)

Clear Subordinate Relationship

Fully delete the direct subordinate relationship of the subordinate account.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
apiInstance.removeSubordinateSubordinatesTree(username).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The account being operated, must be a subordinate account of the current account | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updateSubordinateAuthorities

> Boolean updateSubordinateAuthorities(username, requestBody)

Update Subordinate Permissions

Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | Username
let requestBody = ["null"]; // [String] | Permission list
apiInstance.updateSubordinateAuthorities(username, requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| Username | 
 **requestBody** | [**[String]**](String.md)| Permission list | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateSubordinateOwners

> Boolean updateSubordinateOwners(username, requestBody)

Update Superior Relationship

Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
let requestBody = ["null"]; // [String] | The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
apiInstance.updateSubordinateOwners(username, requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The account being operated, must be a subordinate account of the current account | 
 **requestBody** | [**[String]**](String.md)| The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateSubordinateSubordinates

> Boolean updateSubordinateSubordinates(username, requestBody)

Update Subordinate Relationship

Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example

```javascript
import freechat-sdk from 'freechat-sdk';
let defaultClient = freechat-sdk.ApiClient.instance;
// Configure Bearer access token for authorization: bearerAuth
let bearerAuth = defaultClient.authentications['bearerAuth'];
bearerAuth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new freechat-sdk.OrganizationApi();
let username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
let requestBody = ["null"]; // [String] | The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
apiInstance.updateSubordinateSubordinates(username, requestBody).then((data) => {
  console.log('API called successfully. Returned data: ' + data);
}, (error) => {
  console.error(error);
});

```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The account being operated, must be a subordinate account of the current account | 
 **requestBody** | [**[String]**](String.md)| The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account | 

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

