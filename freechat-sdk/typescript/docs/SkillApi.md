# .SkillApi

All URIs are relative to *http://127.0.0.1:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**confirmSkill**](SkillApi.md#confirmSkill) | **POST** /api/v2/skill/install/{draftId} | Confirm Skill Installation
[**deleteSkill**](SkillApi.md#deleteSkill) | **DELETE** /api/v2/skill/{skillId} | Delete My Skill
[**getSkill**](SkillApi.md#getSkill) | **GET** /api/v2/skill/{skillId} | Get My Skill
[**listSkills**](SkillApi.md#listSkills) | **GET** /api/v2/skill | List My Skills
[**prepareSkill**](SkillApi.md#prepareSkill) | **POST** /api/v2/skill/install | Prepare Skill Installation
[**updateSkill**](SkillApi.md#updateSkill) | **PUT** /api/v2/skill/{skillId} | Update My Skill


# **confirmSkill**
> SkillDetailsDTO confirmSkill(skillConfirmDTO)

Publish an unexpired reviewed draft belonging to the authenticated user. Confirmation is single-use and bound to its exact content digest.

### Example


```typescript
import { createConfiguration, SkillApi } from '';
import type { SkillApiConfirmSkillRequest } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request: SkillApiConfirmSkillRequest = {
  
  draftId: "draftId_example",
  
  skillConfirmDTO: {
    digest: "bf325375e030fccba00917317c574773100bf03b5fc61486286e564b23e9566b",
  },
};

const data = await apiInstance.confirmSkill(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillConfirmDTO** | **SkillConfirmDTO**|  |
 **draftId** | [**string**] |  | defaults to undefined


### Return type

**SkillDetailsDTO**

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

# **deleteSkill**
> void deleteSkill()

Revoke an owned skill at its expected revision; characters cannot continue loading it.

### Example


```typescript
import { createConfiguration, SkillApi } from '';
import type { SkillApiDeleteSkillRequest } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request: SkillApiDeleteSkillRequest = {
  
  skillId: "skillId_example",
  
  expectedRevision: "expectedRevision_example",
};

const data = await apiInstance.deleteSkill(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillId** | [**string**] |  | defaults to undefined
 **expectedRevision** | [**string**] |  | defaults to undefined


### Return type

**void**

### Authorization

[bearerAuth](README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |

[[Back to top]](#) [[Back to API list]](README.md#documentation-for-api-endpoints) [[Back to Model list]](README.md#documentation-for-models) [[Back to README]](README.md)

# **getSkill**
> SkillDetailsDTO getSkill()

Read a complete skill document set owned by the authenticated user.

### Example


```typescript
import { createConfiguration, SkillApi } from '';
import type { SkillApiGetSkillRequest } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request: SkillApiGetSkillRequest = {
  
  skillId: "skillId_example",
};

const data = await apiInstance.getSkill(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillId** | [**string**] |  | defaults to undefined


### Return type

**SkillDetailsDTO**

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

# **listSkills**
> Array<SkillSummaryDTO> listSkills()

List only the authenticated user\'s installed skills.

### Example


```typescript
import { createConfiguration, SkillApi } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request = {};

const data = await apiInstance.listSkills(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters
This endpoint does not need any parameter.


### Return type

**Array<SkillSummaryDTO>**

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

# **prepareSkill**
> SkillDraftDTO prepareSkill(skillInstallDTO)

Validate and review pasted/uploaded text documents or a raw document from an operator-approved HTTPS host. Publication requires explicit confirmation of the returned digest.

### Example


```typescript
import { createConfiguration, SkillApi } from '';
import type { SkillApiPrepareSkillRequest } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request: SkillApiPrepareSkillRequest = {
  
  skillInstallDTO: {
    files: {
      "key": "key_example",
    },
    sourceUrl: "sourceUrl_example",
  },
};

const data = await apiInstance.prepareSkill(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillInstallDTO** | **SkillInstallDTO**|  |


### Return type

**SkillDraftDTO**

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

# **updateSkill**
> SkillDetailsDTO updateSkill(skillUpdateDTO)

Rebuild, validate and review the complete document set before atomically replacing the expected revision. Rejection leaves the installed revision unchanged.

### Example


```typescript
import { createConfiguration, SkillApi } from '';
import type { SkillApiUpdateSkillRequest } from '';

const configuration = createConfiguration();
const apiInstance = new SkillApi(configuration);

const request: SkillApiUpdateSkillRequest = {
  
  skillId: "skillId_example",
  
  skillUpdateDTO: {
    expectedRevision: "expectedRevision_example",
    files: {
      "key": "key_example",
    },
  },
};

const data = await apiInstance.updateSkill(request);
console.log('API called successfully. Returned data:', data);
```


### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillUpdateDTO** | **SkillUpdateDTO**|  |
 **skillId** | [**string**] |  | defaults to undefined


### Return type

**SkillDetailsDTO**

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


