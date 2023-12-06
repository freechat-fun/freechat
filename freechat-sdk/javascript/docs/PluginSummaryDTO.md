# freechat-sdk.PluginSummaryDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestId** | **String** | Request identifier | [optional] 
**pluginId** | **String** | Plugin identifier | [optional] 
**gmtCreate** | **Date** | Creation time | [optional] 
**gmtModified** | **Date** | Modification time | [optional] 
**visibility** | **String** | Visibility: private, public, public_org, hidden | [optional] 
**name** | **String** | Plugin name | [optional] 
**provider** | **String** | Information of the provider | [optional] 
**manifestFormat** | **String** | Manifest format, currently supported: dash_scope, open_ai | [optional] 
**apiFormat** | **String** | API description format, currently supported: openapi_v3 | [optional] 
**username** | **String** | Plugin owner | [optional] 
**tags** | **[String]** | Tag set | [optional] 
**aiModels** | [**[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 


