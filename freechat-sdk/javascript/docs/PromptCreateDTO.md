# freechat-sdk.PromptCreateDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parentId** | **String** | Referenced prompt | [optional] 
**visibility** | **String** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **String** | Prompt name | 
**description** | **String** | Prompt description | [optional] 
**template** | **String** | Prompt text template content, choose one from template and chatTemplate field, priority: template &gt; chatTemplate | [optional] 
**chatTemplate** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) |  | [optional] 
**format** | **String** | Prompt format: mustache (default) | f_string | [optional] 
**lang** | **String** | Prompt language: en (default) | zh_CN | ... | [optional] 
**example** | **String** | Prompt example | [optional] 
**inputs** | **String** | Prompt parameters, JSON format | [optional] 
**ext** | **String** | Additional information, JSON format | [optional] 
**draft** | **String** | Draft content | [optional] 
**tags** | **[String]** | Tag set | [optional] 
**aiModels** | **[String]** | Supported model set, empty means no model limit | [optional] 


