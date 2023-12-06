# freechat-sdk.FlowDetailsDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestId** | **String** | Request identifier | [optional] 
**flowId** | **String** | Flow identifier | [optional] 
**gmtCreate** | **Date** | Creation time | [optional] 
**gmtModified** | **Date** | Modification time | [optional] 
**parentId** | **String** | Referenced flow | [optional] 
**visibility** | **String** | Visibility: private, public, public_org, hidden | [optional] 
**format** | **String** | Flow format, currently supported: langflow | [optional] 
**version** | **Number** | Version | [optional] 
**name** | **String** | Flow name | [optional] 
**description** | **String** | Flow description | [optional] 
**username** | **String** | Flow owner | [optional] 
**tags** | **[String]** | Tag set | [optional] 
**aiModels** | [**[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 
**config** | **String** | Flow configuration | [optional] 
**example** | **String** | Flow example | [optional] 
**parameters** | **String** | Flow parameters, JSON format | [optional] 
**ext** | **String** | Additional information, JSON format | [optional] 
**draft** | **String** | Draft content | [optional] 


