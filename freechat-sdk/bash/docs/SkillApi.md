# SkillApi

All URIs are relative to **

Method | HTTP request | Description
------------- | ------------- | -------------
[**confirmSkill**](SkillApi.md#confirmSkill) | **POST** /api/v2/skill/install/{draftId} | Confirm Skill Installation
[**deleteSkill**](SkillApi.md#deleteSkill) | **DELETE** /api/v2/skill/{skillId} | Delete My Skill
[**getSkill**](SkillApi.md#getSkill) | **GET** /api/v2/skill/{skillId} | Get My Skill
[**listSkills**](SkillApi.md#listSkills) | **GET** /api/v2/skill | List My Skills
[**prepareSkill**](SkillApi.md#prepareSkill) | **POST** /api/v2/skill/install | Prepare Skill Installation
[**updateSkill**](SkillApi.md#updateSkill) | **PUT** /api/v2/skill/{skillId} | Update My Skill



## confirmSkill

Confirm Skill Installation

Publish an unexpired reviewed draft belonging to the authenticated user. Confirmation is single-use and bound to its exact content digest.

### Example

```bash
freechat-cli confirmSkill draftId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **draftId** | **string** |  | [default to null]
 **skillConfirmDTO** | [**SkillConfirmDTO**](SkillConfirmDTO.md) |  |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## deleteSkill

Delete My Skill

Revoke an owned skill at its expected revision; characters cannot continue loading it.

### Example

```bash
freechat-cli deleteSkill skillId=value  expectedRevision=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillId** | **string** |  | [default to null]
 **expectedRevision** | **string** |  | [default to null]

### Return type

(empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## getSkill

Get My Skill

Read a complete skill document set owned by the authenticated user.

### Example

```bash
freechat-cli getSkill skillId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillId** | **string** |  | [default to null]

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## listSkills

List My Skills

List only the authenticated user's installed skills.

### Example

```bash
freechat-cli listSkills
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**array[SkillSummaryDTO]**](SkillSummaryDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## prepareSkill

Prepare Skill Installation

Validate and review pasted/uploaded text documents or a raw document from an operator-approved HTTPS host. Publication requires explicit confirmation of the returned digest.

### Example

```bash
freechat-cli prepareSkill
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillInstallDTO** | [**SkillInstallDTO**](SkillInstallDTO.md) |  |

### Return type

[**SkillDraftDTO**](SkillDraftDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)


## updateSkill

Update My Skill

Rebuild, validate and review the complete document set before atomically replacing the expected revision. Rejection leaves the installed revision unchanged.

### Example

```bash
freechat-cli updateSkill skillId=value
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **skillId** | **string** |  | [default to null]
 **skillUpdateDTO** | [**SkillUpdateDTO**](SkillUpdateDTO.md) |  |

### Return type

[**SkillDetailsDTO**](SkillDetailsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

