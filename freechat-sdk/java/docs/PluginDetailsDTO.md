

# PluginDetailsDTO

Plugin detailed content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**pluginId** | **String** | Plugin identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**name** | **String** | Plugin name |  [optional] |
|**provider** | **String** | Information of the provider |  [optional] |
|**manifestFormat** | **String** | Manifest format, currently supported: dash_scope, open_ai |  [optional] |
|**apiFormat** | **String** | API description format, currently supported: openapi_v3 |  [optional] |
|**username** | **String** | Plugin owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |
|**manifestInfo** | **String** | Manifest content, different formats may have differences |  [optional] |
|**apiInfo** | **String** | API description content, different formats may have content differences |  [optional] |
|**toolSpecFormat** | **String** | Tool specification format, currently supported: open_ai |  [optional] |
|**toolSpecInfo** | **String** | Tool specification content |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |



