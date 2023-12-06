# freechat-sdk.PromptTaskDetailsDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestId** | **String** | Request identifier | [optional] 
**taskId** | **String** | Prompt task identifier | [optional] 
**gmtCreate** | **Date** | Creation time | [optional] 
**gmtModified** | **Date** | Modification time | [optional] 
**gmtExecuted** | **Date** | Latest executed time | [optional] 
**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) |  | [optional] 
**modelId** | **String** | Model identifier | [optional] 
**apiKeyName** | **String** | API-KEY name | [optional] 
**params** | **{String: Object}** | Model call parameters | [optional] 
**cron** | **String** | Task scheduling configuration which compatible with Quartz cron format | [optional] 
**status** | **String** | Task execution status: pending | running | succeeded | failed | [optional] 


