

# FlowUpdateDTO

Request data for updating flow information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**parentId** | **String** | Referenced flow |  [optional] |
|**visibility** | **String** | Visibility: private (default), public, public_org, hidden |  [optional] |
|**format** | **String** | Flow format, currently supported: langflow |  [optional] |
|**name** | **String** | Flow name |  |
|**description** | **String** | Flow description |  [optional] |
|**config** | **String** | Flow configuration |  [optional] |
|**example** | **String** | Flow example |  [optional] |
|**parameters** | **String** | Flow parameters, JSON format |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Draft content |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | **List&lt;String&gt;** | Supported model set, empty means no model limit |  [optional] |



