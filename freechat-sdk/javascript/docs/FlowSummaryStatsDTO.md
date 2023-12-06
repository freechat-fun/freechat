# freechat-sdk.FlowSummaryStatsDTO

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
**viewCount** | **Number** | View count | [optional] 
**referCount** | **Number** | Reference count | [optional] 
**recommendCount** | **Number** | Recommendation count | [optional] 
**scoreCount** | **Number** | Score count | [optional] 
**score** | **Number** | Average score | [optional] 


