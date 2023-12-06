# freechat-sdk.PromptSummaryDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestId** | **String** | Request identifier | [optional] 
**promptId** | **String** | Prompt identifier | [optional] 
**gmtCreate** | **Date** | Creation time | [optional] 
**gmtModified** | **Date** | Modification time | [optional] 
**visibility** | **String** | Visibility: private, public, public_org, hidden | [optional] 
**version** | **Number** | Version | [optional] 
**name** | **String** | Prompt name | [optional] 
**type** | **String** | Prompt type: string | chat | [optional] 
**description** | **String** | Prompt description (50 characters, the excess part is represented by ellipsis) | [optional] 
**format** | **String** | Prompt format: mustache (default) | f_string | [optional] 
**lang** | **String** | Prompt language: en (default) | zh_CN | ... | [optional] 
**username** | **String** | Prompt owner | [optional] 
**tags** | **[String]** | Tag set | [optional] 
**aiModels** | [**[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 


