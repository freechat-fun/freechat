

# AgentUpdateDTO

Request data for updating agent information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**parentId** | **String** | Referenced agent |  [optional] |
|**visibility** | **String** | Visibility: private (default), public, public_org, hidden |  [optional] |
|**format** | **String** | Agent format, currently supported: langflow |  [optional] |
|**name** | **String** | Agent name |  |
|**description** | **String** | Agent description |  [optional] |
|**config** | **String** | Agent configuration |  [optional] |
|**example** | **String** | Agent example |  [optional] |
|**parameters** | **String** | Agent parameters, JSON format |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Draft content |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | **List&lt;String&gt;** | Supported model set, empty means no model limit |  [optional] |



