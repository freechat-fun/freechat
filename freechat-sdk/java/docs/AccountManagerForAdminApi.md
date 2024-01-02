# AccountManagerForAdminApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createTokenForUser**](AccountManagerForAdminApi.md#createTokenForUser) | **POST** /api/v1/admin/token/{username}/{duration} | Create API Token for User. |
| [**createUser**](AccountManagerForAdminApi.md#createUser) | **POST** /api/v1/admin/user | Create User |
| [**deleteTokenForUser**](AccountManagerForAdminApi.md#deleteTokenForUser) | **DELETE** /api/v1/admin/token/{token} | Delete API Token |
| [**deleteUser**](AccountManagerForAdminApi.md#deleteUser) | **DELETE** /api/v1/admin/user/{username} | Delete User |
| [**disableTokenForUser**](AccountManagerForAdminApi.md#disableTokenForUser) | **PUT** /api/v1/admin/token/{token} | Disable API Token |
| [**getDetailsOfUser**](AccountManagerForAdminApi.md#getDetailsOfUser) | **GET** /api/v1/admin/user/{username} | Get User Details |
| [**getUserByToken**](AccountManagerForAdminApi.md#getUserByToken) | **GET** /api/v1/admin/tokenBy/{token} | Get User by API Token |
| [**listAuthoritiesOfUser**](AccountManagerForAdminApi.md#listAuthoritiesOfUser) | **GET** /api/v1/admin/authority/{username} | List User Permissions |
| [**listTokensOfUser**](AccountManagerForAdminApi.md#listTokensOfUser) | **GET** /api/v1/admin/token/{username} | Get API Token of User |
| [**listUsers**](AccountManagerForAdminApi.md#listUsers) | **GET** /api/v1/admin/users/{pageSize}/{pageNum} | List User Information |
| [**listUsers1**](AccountManagerForAdminApi.md#listUsers1) | **GET** /api/v1/admin/users/{pageSize} | List User Information |
| [**listUsers2**](AccountManagerForAdminApi.md#listUsers2) | **GET** /api/v1/admin/users | List User Information |
| [**updateAuthoritiesOfUser**](AccountManagerForAdminApi.md#updateAuthoritiesOfUser) | **PUT** /api/v1/admin/authority/{username} | Update User Permissions |
| [**updateUser**](AccountManagerForAdminApi.md#updateUser) | **PUT** /api/v1/admin/user | Update User |


<a id="createTokenForUser"></a>
# **createTokenForUser**
> String createTokenForUser(username, duration)

Create API Token for User.

Create an API Token for a specified user, valid for duration seconds.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    Long duration = 56L; // Long | Validity period (seconds)
    try {
      String result = apiInstance.createTokenForUser(username, duration);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#createTokenForUser");
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
| **duration** | **Long**| Validity period (seconds) | |

### Return type

**String**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createUser"></a>
# **createUser**
> Boolean createUser(userFullDetailsDTO)

Create User

Create user.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    UserFullDetailsDTO userFullDetailsDTO = new UserFullDetailsDTO(); // UserFullDetailsDTO | User information
    try {
      Boolean result = apiInstance.createUser(userFullDetailsDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#createUser");
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
| **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | |

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

<a id="deleteTokenForUser"></a>
# **deleteTokenForUser**
> Boolean deleteTokenForUser(token)

Delete API Token

Delete the specified API Token.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String token = "token_example"; // String | API Token
    try {
      Boolean result = apiInstance.deleteTokenForUser(token);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#deleteTokenForUser");
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
| **token** | **String**| API Token | |

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

<a id="deleteUser"></a>
# **deleteUser**
> Boolean deleteUser(username)

Delete User

Delete user by username.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    try {
      Boolean result = apiInstance.deleteUser(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#deleteUser");
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

<a id="disableTokenForUser"></a>
# **disableTokenForUser**
> Boolean disableTokenForUser(token)

Disable API Token

Disable the specified API Token.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String token = "token_example"; // String | API Token
    try {
      Boolean result = apiInstance.disableTokenForUser(token);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#disableTokenForUser");
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
| **token** | **String**| API Token | |

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

<a id="getDetailsOfUser"></a>
# **getDetailsOfUser**
> UserDetailsDTO getDetailsOfUser(username)

Get User Details

Return detailed user information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    try {
      UserDetailsDTO result = apiInstance.getDetailsOfUser(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#getDetailsOfUser");
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

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getUserByToken"></a>
# **getUserByToken**
> UserDetailsDTO getUserByToken(token)

Get User by API Token

Get the detailed user information corresponding to the API Token.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String token = "token_example"; // String | API Token
    try {
      UserDetailsDTO result = apiInstance.getUserByToken(token);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#getUserByToken");
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
| **token** | **String**| API Token | |

### Return type

[**UserDetailsDTO**](UserDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAuthoritiesOfUser"></a>
# **listAuthoritiesOfUser**
> Set&lt;String&gt; listAuthoritiesOfUser(username)

List User Permissions

List the user&#39;s permissions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    try {
      Set<String> result = apiInstance.listAuthoritiesOfUser(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#listAuthoritiesOfUser");
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

<a id="listTokensOfUser"></a>
# **listTokensOfUser**
> List&lt;ApiTokenInfoDTO&gt; listTokensOfUser(username)

Get API Token of User

Get the list of API Tokens of the user.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    try {
      List<ApiTokenInfoDTO> result = apiInstance.listTokensOfUser(username);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#listTokensOfUser");
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

[**List&lt;ApiTokenInfoDTO&gt;**](ApiTokenInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listUsers"></a>
# **listUsers**
> List&lt;UserBasicInfoDTO&gt; listUsers(pageSize, pageNum)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    Long pageSize = 56L; // Long | Maximum quantity
    Long pageNum = 56L; // Long | Current page number
    try {
      List<UserBasicInfoDTO> result = apiInstance.listUsers(pageSize, pageNum);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#listUsers");
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
| **pageSize** | **Long**| Maximum quantity | |
| **pageNum** | **Long**| Current page number | |

### Return type

[**List&lt;UserBasicInfoDTO&gt;**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listUsers1"></a>
# **listUsers1**
> List&lt;UserBasicInfoDTO&gt; listUsers1(pageSize)

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    Long pageSize = 56L; // Long | Maximum quantity
    try {
      List<UserBasicInfoDTO> result = apiInstance.listUsers1(pageSize);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#listUsers1");
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
| **pageSize** | **Long**| Maximum quantity | |

### Return type

[**List&lt;UserBasicInfoDTO&gt;**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listUsers2"></a>
# **listUsers2**
> List&lt;UserBasicInfoDTO&gt; listUsers2()

List User Information

Return user information by page, return the pageNum page, up to pageSize user information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    try {
      List<UserBasicInfoDTO> result = apiInstance.listUsers2();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#listUsers2");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;UserBasicInfoDTO&gt;**](UserBasicInfoDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateAuthoritiesOfUser"></a>
# **updateAuthoritiesOfUser**
> Boolean updateAuthoritiesOfUser(username, requestBody)

Update User Permissions

Update the user&#39;s permission list.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    String username = "username_example"; // String | Username
    Set<String> requestBody = Arrays.asList(); // Set<String> | Permission list
    try {
      Boolean result = apiInstance.updateAuthoritiesOfUser(username, requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#updateAuthoritiesOfUser");
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

<a id="updateUser"></a>
# **updateUser**
> Boolean updateUser(userFullDetailsDTO)

Update User

Update user information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AccountManagerForAdminApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AccountManagerForAdminApi apiInstance = new AccountManagerForAdminApi(defaultClient);
    UserFullDetailsDTO userFullDetailsDTO = new UserFullDetailsDTO(); // UserFullDetailsDTO | User information
    try {
      Boolean result = apiInstance.updateUser(userFullDetailsDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AccountManagerForAdminApi#updateUser");
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
| **userFullDetailsDTO** | [**UserFullDetailsDTO**](UserFullDetailsDTO.md)| User information | |

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

