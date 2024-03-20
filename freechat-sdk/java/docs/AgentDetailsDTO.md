

# AgentDetailsDTO

Agent detailed content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**agentId** | **Long** | Agent identifier, will change after publish |  [optional] |
|**agentUid** | **String** | Agent immutable identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**parentUid** | **String** | Referenced agent |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**format** | **String** | Agent format, currently supported: langflow |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Agent name |  [optional] |
|**description** | **String** | Agent description |  [optional] |
|**username** | **String** | Agent owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |
|**config** | **String** | Agent configuration |  [optional] |
|**example** | **String** | Agent example |  [optional] |
|**parameters** | **String** | Agent parameters, JSON format |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Draft content |  [optional] |



