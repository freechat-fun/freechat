# freechat-sdk.PromptTaskDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) |  | 
**modelId** | **String** | Model identifier | [optional] 
**apiKeyName** | **String** | API-KEY name | [optional] 
**params** | **{String: Object}** | Model call parameters | [optional] 
**cron** | **String** | Task scheduling configuration which compatible with Quartz cron format | [optional] 
**status** | **String** | Task execution status: pending | running | succeeded | failed | [optional] 


