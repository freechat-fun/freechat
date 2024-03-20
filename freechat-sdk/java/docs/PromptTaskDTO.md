

# PromptTaskDTO

Prompt task information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) |  |  |
|**modelId** | **String** | Model identifier |  [optional] |
|**apiKeyName** | **String** | API-KEY name, priority: apiKeyName &gt; apiKeyValue |  [optional] |
|**apiKeyValue** | **String** | API-KEY value |  [optional] |
|**params** | **Map&lt;String, Object&gt;** | Model call parameters |  [optional] |
|**cron** | **String** | Task scheduling configuration which compatible with Quartz cron format |  [optional] |
|**status** | **String** | Task execution status: pending | running | succeeded | failed |  [optional] |



