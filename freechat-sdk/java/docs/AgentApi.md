# AgentApi

All URIs are relative to *http://127.0.0.1:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**batchSearchAgentDetails**](AgentApi.md#batchSearchAgentDetails) | **POST** /api/v2/agent/batch/details/search | Batch Search Agent Details |
| [**batchSearchAgentSummary**](AgentApi.md#batchSearchAgentSummary) | **POST** /api/v2/agent/batch/search | Batch Search Agent Summaries |
| [**cloneAgent**](AgentApi.md#cloneAgent) | **POST** /api/v2/agent/clone/{agentId} | Clone Agent |
| [**cloneAgents**](AgentApi.md#cloneAgents) | **POST** /api/v2/agent/batch/clone | Batch Clone Agents |
| [**countAgents**](AgentApi.md#countAgents) | **POST** /api/v2/agent/count | Calculate Number of Agents |
| [**createAgent**](AgentApi.md#createAgent) | **POST** /api/v2/agent | Create Agent |
| [**createAgents**](AgentApi.md#createAgents) | **POST** /api/v2/agent/batch | Batch Create Agents |
| [**deleteAgent**](AgentApi.md#deleteAgent) | **DELETE** /api/v2/agent/{agentId} | Delete Agent |
| [**deleteAgents**](AgentApi.md#deleteAgents) | **DELETE** /api/v2/agent/batch/delete | Batch Delete Agents |
| [**getAgentDetails**](AgentApi.md#getAgentDetails) | **GET** /api/v2/agent/details/{agentId} | Get Agent Details |
| [**getAgentSummary**](AgentApi.md#getAgentSummary) | **GET** /api/v2/agent/summary/{agentId} | Get Agent Summary |
| [**listAgentVersionsByName**](AgentApi.md#listAgentVersionsByName) | **POST** /api/v2/agent/versions/{name} | List Versions by Agent Name |
| [**publishAgent**](AgentApi.md#publishAgent) | **POST** /api/v2/agent/publish/{agentId}/{visibility} | Publish Agent |
| [**searchAgentDetails**](AgentApi.md#searchAgentDetails) | **POST** /api/v2/agent/details/search | Search Agent Details |
| [**searchAgentSummary**](AgentApi.md#searchAgentSummary) | **POST** /api/v2/agent/search | Search Agent Summary |
| [**updateAgent**](AgentApi.md#updateAgent) | **PUT** /api/v2/agent/{agentId} | Update Agent |


<a id="batchSearchAgentDetails"></a>
# **batchSearchAgentDetails**
> List&lt;List&lt;AgentDetailsDTO&gt;&gt; batchSearchAgentDetails(agentQueryDTO)

Batch Search Agent Details

Batch call shortcut for /api/v2/agent/details/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    List<AgentQueryDTO> agentQueryDTO = Arrays.asList(); // List<AgentQueryDTO> | Query conditions
    try {
      List<List<AgentDetailsDTO>> result = apiInstance.batchSearchAgentDetails(agentQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#batchSearchAgentDetails");
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
| **agentQueryDTO** | [**List&lt;AgentQueryDTO&gt;**](AgentQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;AgentDetailsDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="batchSearchAgentSummary"></a>
# **batchSearchAgentSummary**
> List&lt;List&lt;AgentSummaryDTO&gt;&gt; batchSearchAgentSummary(agentQueryDTO)

Batch Search Agent Summaries

Batch call shortcut for /api/v2/agent/search.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    List<AgentQueryDTO> agentQueryDTO = Arrays.asList(); // List<AgentQueryDTO> | Query conditions
    try {
      List<List<AgentSummaryDTO>> result = apiInstance.batchSearchAgentSummary(agentQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#batchSearchAgentSummary");
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
| **agentQueryDTO** | [**List&lt;AgentQueryDTO&gt;**](AgentQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;List&lt;AgentSummaryDTO&gt;&gt;**](List.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="cloneAgent"></a>
# **cloneAgent**
> Long cloneAgent(agentId)

Clone Agent

Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | The referenced agentId
    try {
      Long result = apiInstance.cloneAgent(agentId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#cloneAgent");
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
| **agentId** | **Long**| The referenced agentId | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="cloneAgents"></a>
# **cloneAgents**
> List&lt;Long&gt; cloneAgents(requestBody)

Batch Clone Agents

Batch clone multiple agents. Ensure transactionality, return the agentId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    List<Long> requestBody = Arrays.asList(); // List<Long> | List of agent information to be created
    try {
      List<Long> result = apiInstance.cloneAgents(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#cloneAgents");
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
| **requestBody** | [**List&lt;Long&gt;**](Long.md)| List of agent information to be created | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="countAgents"></a>
# **countAgents**
> Long countAgents(agentQueryDTO)

Calculate Number of Agents

Calculate the number of agents according to the specified query conditions.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    AgentQueryDTO agentQueryDTO = new AgentQueryDTO(); // AgentQueryDTO | Query conditions
    try {
      Long result = apiInstance.countAgents(agentQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#countAgents");
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
| **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createAgent"></a>
# **createAgent**
> Long createAgent(agentCreateDTO)

Create Agent

Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    AgentCreateDTO agentCreateDTO = new AgentCreateDTO(); // AgentCreateDTO | Information of the agent to be created
    try {
      Long result = apiInstance.createAgent(agentCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#createAgent");
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
| **agentCreateDTO** | [**AgentCreateDTO**](AgentCreateDTO.md)| Information of the agent to be created | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="createAgents"></a>
# **createAgents**
> List&lt;Long&gt; createAgents(agentCreateDTO)

Batch Create Agents

Batch create multiple agents. Ensure transactionality, return the agentId list after success.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    List<AgentCreateDTO> agentCreateDTO = Arrays.asList(); // List<AgentCreateDTO> | List of agent information to be created
    try {
      List<Long> result = apiInstance.createAgents(agentCreateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#createAgents");
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
| **agentCreateDTO** | [**List&lt;AgentCreateDTO&gt;**](AgentCreateDTO.md)| List of agent information to be created | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="deleteAgent"></a>
# **deleteAgent**
> Boolean deleteAgent(agentId)

Delete Agent

Delete agent. Return success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | AgentId to be deleted
    try {
      Boolean result = apiInstance.deleteAgent(agentId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#deleteAgent");
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
| **agentId** | **Long**| AgentId to be deleted | |

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

<a id="deleteAgents"></a>
# **deleteAgents**
> List&lt;Long&gt; deleteAgents(requestBody)

Batch Delete Agents

Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    List<Long> requestBody = Arrays.asList(); // List<Long> | List of agentId to be deleted
    try {
      List<Long> result = apiInstance.deleteAgents(requestBody);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#deleteAgents");
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
| **requestBody** | [**List&lt;Long&gt;**](Long.md)| List of agentId to be deleted | |

### Return type

**List&lt;Long&gt;**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getAgentDetails"></a>
# **getAgentDetails**
> AgentDetailsDTO getAgentDetails(agentId)

Get Agent Details

Get agent detailed information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | AgentId to be obtained
    try {
      AgentDetailsDTO result = apiInstance.getAgentDetails(agentId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#getAgentDetails");
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
| **agentId** | **Long**| AgentId to be obtained | |

### Return type

[**AgentDetailsDTO**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="getAgentSummary"></a>
# **getAgentSummary**
> AgentSummaryDTO getAgentSummary(agentId)

Get Agent Summary

Get agent summary information.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | agentId to be obtained
    try {
      AgentSummaryDTO result = apiInstance.getAgentSummary(agentId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#getAgentSummary");
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
| **agentId** | **Long**| agentId to be obtained | |

### Return type

[**AgentSummaryDTO**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="listAgentVersionsByName"></a>
# **listAgentVersionsByName**
> List&lt;AgentItemForNameDTO&gt; listAgentVersionsByName(name)

List Versions by Agent Name

List the versions and corresponding agentIds by agent name.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    String name = "name_example"; // String | Agent name
    try {
      List<AgentItemForNameDTO> result = apiInstance.listAgentVersionsByName(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#listAgentVersionsByName");
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
| **name** | **String**| Agent name | |

### Return type

[**List&lt;AgentItemForNameDTO&gt;**](AgentItemForNameDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="publishAgent"></a>
# **publishAgent**
> Long publishAgent(agentId, visibility)

Publish Agent

Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | The agentId to be published
    String visibility = "visibility_example"; // String | Visibility: public | private | ...
    try {
      Long result = apiInstance.publishAgent(agentId, visibility);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#publishAgent");
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
| **agentId** | **Long**| The agentId to be published | |
| **visibility** | **String**| Visibility: public | private | ... | |

### Return type

**Long**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchAgentDetails"></a>
# **searchAgentDetails**
> List&lt;AgentDetailsDTO&gt; searchAgentDetails(agentQueryDTO)

Search Agent Details

Same as /api/v2/agent/search, but returns detailed information of the agent.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    AgentQueryDTO agentQueryDTO = new AgentQueryDTO(); // AgentQueryDTO | Query conditions
    try {
      List<AgentDetailsDTO> result = apiInstance.searchAgentDetails(agentQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#searchAgentDetails");
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
| **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;AgentDetailsDTO&gt;**](AgentDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="searchAgentSummary"></a>
# **searchAgentSummary**
> List&lt;AgentSummaryDTO&gt; searchAgentSummary(agentQueryDTO)

Search Agent Summary

Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    AgentQueryDTO agentQueryDTO = new AgentQueryDTO(); // AgentQueryDTO | Query conditions
    try {
      List<AgentSummaryDTO> result = apiInstance.searchAgentSummary(agentQueryDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#searchAgentSummary");
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
| **agentQueryDTO** | [**AgentQueryDTO**](AgentQueryDTO.md)| Query conditions | |

### Return type

[**List&lt;AgentSummaryDTO&gt;**](AgentSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

<a id="updateAgent"></a>
# **updateAgent**
> Boolean updateAgent(agentId, agentUpdateDTO)

Update Agent

Update agent, refer to /api/v2/agent/create, required field: agentId. Return success or failure.

### Example
```java
// Import classes:
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.Configuration;
import fun.freechat.client.auth.*;
import fun.freechat.client.models.*;
import fun.freechat.client.api.AgentApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://127.0.0.1:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AgentApi apiInstance = new AgentApi(defaultClient);
    Long agentId = 56L; // Long | AgentId to be updated
    AgentUpdateDTO agentUpdateDTO = new AgentUpdateDTO(); // AgentUpdateDTO | Agent information to be updated
    try {
      Boolean result = apiInstance.updateAgent(agentId, agentUpdateDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AgentApi#updateAgent");
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
| **agentId** | **Long**| AgentId to be updated | |
| **agentUpdateDTO** | [**AgentUpdateDTO**](AgentUpdateDTO.md)| Agent information to be updated | |

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

