

# PromptTaskDetailsDTO

Prompt task detailed information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**taskId** | **String** | Prompt task identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**gmtStart** | **OffsetDateTime** | Task start execution time |  [optional] |
|**gmtEnd** | **OffsetDateTime** | Task end execution time |  [optional] |
|**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) | Prompt reference information |  [optional] |
|**modelId** | **String** | Model identifier |  [optional] |
|**apiKeyName** | **String** | API-KEY name |  [optional] |
|**apiKeyValue** | **String** | API-KEY value |  [optional] |
|**params** | **Map&lt;String, Object&gt;** | Model call parameters |  [optional] |
|**cron** | **String** | Task scheduling configuration which compatible with Quartz cron format |  [optional] |
|**status** | **String** | Task execution status: pending | running | succeeded | failed | canceled |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |



