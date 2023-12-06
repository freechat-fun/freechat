# OrganizationApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getOwners**](OrganizationApi.md#getOwners) | **GET** /api/v1/org/owners | Get My Superior Relationship |
| [**getOwnersDot**](OrganizationApi.md#getOwnersDot) | **GET** /api/v1/org/owners/dot | Get DOT of Superior Relationship |
| [**getSubordinateOwners**](OrganizationApi.md#getSubordinateOwners) | **GET** /api/v1/org/manage/{username}/owners | Get Superior Relationship |
| [**getSubordinateSubordinates**](OrganizationApi.md#getSubordinateSubordinates) | **GET** /api/v1/org/manage/{username}/subordinates | Get Subordinate Relationship |
| [**getSubordinates**](OrganizationApi.md#getSubordinates) | **GET** /api/v1/org/subordinates | Get My Subordinate Relationship |
| [**getSubordinatesDot**](OrganizationApi.md#getSubordinatesDot) | **GET** /api/v1/org/subordinates/dot | Get DOT of Subordinate Relationship |
| [**listSubordinateAuthorities**](OrganizationApi.md#listSubordinateAuthorities) | **GET** /api/v1/org/authority/{username} | List Subordinate Permissions |
| [**removeSubordinateSubordinatesTree**](OrganizationApi.md#removeSubordinateSubordinatesTree) | **DELETE** /api/v1/org/manage/{username}/subordinates | Clear Subordinate Relationship |
| [**updateSubordinateAuthorities**](OrganizationApi.md#updateSubordinateAuthorities) | **PUT** /api/v1/org/authority/{username} | Update Subordinate Permissions |
| [**updateSubordinateOwners**](OrganizationApi.md#updateSubordinateOwners) | **PUT** /api/v1/org/manage/{username}/owners | Update Superior Relationship |
| [**updateSubordinateSubordinates**](OrganizationApi.md#updateSubordinateSubordinates) | **PUT** /api/v1/org/manage/{username}/subordinates | Update Subordinate Relationship |


<a id="getOwners"></a>
# **getOwners**
> List&lt;String&gt; getOwners(all)

Get My Superior Relationship

Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String all = "all_example"; // String | Whether to return virtual reported owners
    try {
      List<String> result = apiInstance.getOwners(all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getOwners");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **all** | **String**| Whether to return virtual reported owners | [optional] |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getOwnersDot"></a>
# **getOwnersDot**
> String getOwnersDot(all)

Get DOT of Superior Relationship

Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String all = "all_example"; // String | Whether to return virtual reported owners
    try {
      String result = apiInstance.getOwnersDot(all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getOwnersDot");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **all** | **String**| Whether to return virtual reported owners | [optional] |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getSubordinateOwners"></a>
# **getSubordinateOwners**
> List&lt;String&gt; getSubordinateOwners(username, all)

Get Superior Relationship

Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt; By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | The account being queried, must be a subordinate account of the current account
    String all = "all_example"; // String | Whether to return virtual reported owners
    try {
      List<String> result = apiInstance.getSubordinateOwners(username, all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getSubordinateOwners");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| The account being queried, must be a subordinate account of the current account | |
| **all** | **String**| Whether to return virtual reported owners | [optional] |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getSubordinateSubordinates"></a>
# **getSubordinateSubordinates**
> List&lt;String&gt; getSubordinateSubordinates(username, all)

Get Subordinate Relationship

Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | The account being queried, must be a subordinate account of the current account
    String all = "all_example"; // String | Whether to return virtual managed subordinates
    try {
      List<String> result = apiInstance.getSubordinateSubordinates(username, all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getSubordinateSubordinates");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| The account being queried, must be a subordinate account of the current account | |
| **all** | **String**| Whether to return virtual managed subordinates | [optional] |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getSubordinates"></a>
# **getSubordinates**
> List&lt;String&gt; getSubordinates(all)

Get My Subordinate Relationship

Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String all = "all_example"; // String | Whether to return virtual managed subordinates
    try {
      List<String> result = apiInstance.getSubordinates(all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getSubordinates");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **all** | **String**| Whether to return virtual managed subordinates | [optional] |

### Return type

**List&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getSubordinatesDot"></a>
# **getSubordinatesDot**
> String getSubordinatesDot(all)

Get DOT of Subordinate Relationship

Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String all = "all_example"; // String | Whether to return virtual managed subordinates
    try {
      String result = apiInstance.getSubordinatesDot(all);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#getSubordinatesDot");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **all** | **String**| Whether to return virtual managed subordinates | [optional] |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listSubordinateAuthorities"></a>
# **listSubordinateAuthorities**
> Set&lt;String&gt; listSubordinateAuthorities(username)

List Subordinate Permissions

List the permission list of the subordinate account.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | Username
    try {
      Set<String> result = apiInstance.listSubordinateAuthorities(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#listSubordinateAuthorities");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| Username | |

### Return type

**Set&lt;String&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="removeSubordinateSubordinatesTree"></a>
# **removeSubordinateSubordinatesTree**
> Boolean removeSubordinateSubordinatesTree(username)

Clear Subordinate Relationship

Fully delete the direct subordinate relationship of the subordinate account.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
    try {
      Boolean result = apiInstance.removeSubordinateSubordinatesTree(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#removeSubordinateSubordinatesTree");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| The account being operated, must be a subordinate account of the current account | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateSubordinateAuthorities"></a>
# **updateSubordinateAuthorities**
> Boolean updateSubordinateAuthorities(username, requestBody)

Update Subordinate Permissions

Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | Username
    Set<String> requestBody = Arrays.asList(); // Set<String> | Permission list
    try {
      Boolean result = apiInstance.updateSubordinateAuthorities(username, requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#updateSubordinateAuthorities");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| Username | |
| **requestBody** | [**Set&lt;String&gt;**](String.md)| Permission list | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateSubordinateOwners"></a>
# **updateSubordinateOwners**
> Boolean updateSubordinateOwners(username, requestBody)

Update Superior Relationship

Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
    List<String> requestBody = Arrays.asList(); // List<String> | The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
    try {
      Boolean result = apiInstance.updateSubordinateOwners(username, requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#updateSubordinateOwners");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| The account being operated, must be a subordinate account of the current account | |
| **requestBody** | [**List&lt;String&gt;**](String.md)| The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateSubordinateSubordinates"></a>
# **updateSubordinateSubordinates**
> Boolean updateSubordinateSubordinates(username, requestBody)

Update Subordinate Relationship

Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.OrganizationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    OrganizationApi apiInstance = new OrganizationApi(defaultClient);
    String username = "username_example"; // String | The account being operated, must be a subordinate account of the current account
    List<String> requestBody = Arrays.asList(); // List<String> | The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
    try {
      Boolean result = apiInstance.updateSubordinateSubordinates(username, requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrganizationApi#updateSubordinateSubordinates");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **username** | **String**| The account being operated, must be a subordinate account of the current account | |
| **requestBody** | [**List&lt;String&gt;**](String.md)| The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account | |

### Return type

**Boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

