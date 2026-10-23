# SkillApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**confirmSkill**](SkillApi.md#confirmSkill) | **POST** /api/v2/skill/install/{draftId} | Confirm Skill Installation |
| [**deleteSkill**](SkillApi.md#deleteSkill) | **DELETE** /api/v2/skill/{skillId} | Delete My Skill |
| [**getSkill**](SkillApi.md#getSkill) | **GET** /api/v2/skill/{skillId} | Get My Skill |
| [**listSkills**](SkillApi.md#listSkills) | **GET** /api/v2/skill | List My Skills |
| [**prepareSkill**](SkillApi.md#prepareSkill) | **POST** /api/v2/skill/install | Prepare Skill Installation |
| [**updateSkill**](SkillApi.md#updateSkill) | **PUT** /api/v2/skill/{skillId} | Update My Skill |


<a id="confirmSkill"></a>
# **confirmSkill**
> SkillDetailsDTO confirmSkill(draftId, skillConfirmDTO)

Confirm Skill Installation

Publish an unexpired reviewed draft belonging to the authenticated user. Confirmation is single-use and bound to its exact content digest.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    String draftId = "draftId_example"; // String | 
    SkillConfirmDTO skillConfirmDTO = new SkillConfirmDTO(); // SkillConfirmDTO | 
    try {
      SkillDetailsDTO result = apiInstance.confirmSkill(draftId, skillConfirmDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#confirmSkill");
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
| **draftId** | **String**|  | |
| **skillConfirmDTO** | [**SkillConfirmDTO**](SkillConfirmDTO.md)|  | |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteSkill"></a>
# **deleteSkill**
> deleteSkill(skillId, expectedRevision)

Delete My Skill

Revoke an owned skill at its expected revision; characters cannot continue loading it.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    String skillId = "skillId_example"; // String | 
    String expectedRevision = "expectedRevision_example"; // String | 
    try {
      apiInstance.deleteSkill(skillId, expectedRevision);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#deleteSkill");
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
| **skillId** | **String**|  | |
| **expectedRevision** | **String**|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |

<a id="getSkill"></a>
# **getSkill**
> SkillDetailsDTO getSkill(skillId)

Get My Skill

Read a complete skill document set owned by the authenticated user.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    String skillId = "skillId_example"; // String | 
    try {
      SkillDetailsDTO result = apiInstance.getSkill(skillId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#getSkill");
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
| **skillId** | **String**|  | |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listSkills"></a>
# **listSkills**
> List&lt;SkillSummaryDTO&gt; listSkills()

List My Skills

List only the authenticated user&#39;s installed skills.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    try {
      List<SkillSummaryDTO> result = apiInstance.listSkills();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#listSkills");
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

[**List&lt;SkillSummaryDTO&gt;**](SkillSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="prepareSkill"></a>
# **prepareSkill**
> SkillDraftDTO prepareSkill(skillInstallDTO)

Prepare Skill Installation

Validate and review pasted/uploaded text documents or a raw document from an operator-approved HTTPS host. Publication requires explicit confirmation of the returned digest.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    SkillInstallDTO skillInstallDTO = new SkillInstallDTO(); // SkillInstallDTO | 
    try {
      SkillDraftDTO result = apiInstance.prepareSkill(skillInstallDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#prepareSkill");
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
| **skillInstallDTO** | [**SkillInstallDTO**](SkillInstallDTO.md)|  | |

### Return type

[**SkillDraftDTO**](SkillDraftDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateSkill"></a>
# **updateSkill**
> SkillDetailsDTO updateSkill(skillId, skillUpdateDTO)

Update My Skill

Rebuild, validate and review the complete document set before atomically replacing the expected revision. Rejection leaves the installed revision unchanged.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.SkillApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    SkillApi apiInstance = new SkillApi(defaultClient);
    String skillId = "skillId_example"; // String | 
    SkillUpdateDTO skillUpdateDTO = new SkillUpdateDTO(); // SkillUpdateDTO | 
    try {
      SkillDetailsDTO result = apiInstance.updateSkill(skillId, skillUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkillApi#updateSkill");
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
| **skillId** | **String**|  | |
| **skillUpdateDTO** | [**SkillUpdateDTO**](SkillUpdateDTO.md)|  | |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

