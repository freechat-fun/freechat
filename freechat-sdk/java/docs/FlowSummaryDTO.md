

# FlowSummaryDTO

Flow summary information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**flowId** | **String** | Flow identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**parentId** | **String** | Referenced flow |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**format** | **String** | Flow format, currently supported: langflow |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Flow name |  [optional] |
|**description** | **String** | Flow description |  [optional] |
|**username** | **String** | Flow owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |



