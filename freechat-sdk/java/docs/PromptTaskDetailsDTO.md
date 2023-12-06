

# PromptTaskDetailsDTO

Prompt task detailed information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**taskId** | **String** | Prompt task identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**gmtExecuted** | **OffsetDateTime** | Latest executed time |  [optional] |
|**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) |  |  [optional] |
|**modelId** | **String** | Model identifier |  [optional] |
|**apiKeyName** | **String** | API-KEY name |  [optional] |
|**params** | **Map&lt;String, Object&gt;** | Model call parameters |  [optional] |
|**cron** | **String** | Task scheduling configuration which compatible with Quartz cron format |  [optional] |
|**status** | **String** | Task execution status: pending | running | succeeded | failed |  [optional] |



