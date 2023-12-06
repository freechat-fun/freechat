# freechat-sdk.PluginCreateDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **String** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **String** | Plugin name | 
**manifestFormat** | **String** | Manifest format, currently supported: dash_scope (default), open_ai | [optional] 
**manifestInfo** | **String** | Manifest content, can be full content or a URL | [optional] 
**apiFormat** | **String** | API description format, currently supported: openapi_v3 (default) | [optional] 
**apiInfo** | **String** | API description content, can be full content or a URL | [optional] 
**provider** | **String** | Provider information, default is the current user&#39;s username | [optional] 
**ext** | **String** | Additional information, JSON format | [optional] 
**tags** | **[String]** | Tag set | [optional] 
**aiModels** | **[String]** | Supported model set, empty means no model limit | [optional] 


