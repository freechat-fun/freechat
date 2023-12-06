

# PromptSummaryStatsDTO

Prompt template summary content, including interactive statistical information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**promptId** | **String** | Prompt identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Prompt name |  [optional] |
|**type** | **String** | Prompt type: string | chat |  [optional] |
|**description** | **String** | Prompt description (50 characters, the excess part is represented by ellipsis) |  [optional] |
|**format** | **String** | Prompt format: mustache (default) | f_string |  [optional] |
|**lang** | **String** | Prompt language: en (default) | zh_CN | ... |  [optional] |
|**username** | **String** | Prompt owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |
|**viewCount** | **Long** | View count |  [optional] |
|**referCount** | **Long** | Reference count |  [optional] |
|**recommendCount** | **Long** | Recommendation count |  [optional] |
|**scoreCount** | **Long** | Score count |  [optional] |
|**score** | **Long** | Average score |  [optional] |



