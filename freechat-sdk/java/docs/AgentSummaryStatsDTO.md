

# AgentSummaryStatsDTO

Agent template summary content, including interactive statistical information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**agentId** | **String** | Agent identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**parentId** | **String** | Referenced agent |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**format** | **String** | Agent format, currently supported: langflow |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Agent name |  [optional] |
|**description** | **String** | Agent description |  [optional] |
|**username** | **String** | Agent owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |
|**viewCount** | **Long** | View count |  [optional] |
|**referCount** | **Long** | Reference count |  [optional] |
|**recommendCount** | **Long** | Recommendation count |  [optional] |
|**scoreCount** | **Long** | Score count |  [optional] |
|**score** | **Long** | Average score |  [optional] |



