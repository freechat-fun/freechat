# PluginCreateDTO

Request data for creating new plugin information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **str** | Plugin name | 
**manifest_format** | **str** | Manifest format, currently supported: dash_scope (default), open_ai | [optional] 
**manifest_info** | **str** | Manifest content, can be full content or a URL | [optional] 
**api_format** | **str** | API description format, currently supported: openapi_v3 (default) | [optional] 
**api_info** | **str** | API description content, can be full content or a URL | [optional] 
**provider** | **str** | Provider information, default is the current user&#39;s username | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | **List[str]** | Supported model set, empty means no model limit | [optional] 

## Example

```python
from freechat_sdk.models.plugin_create_dto import PluginCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PluginCreateDTO from a JSON string
plugin_create_dto_instance = PluginCreateDTO.from_json(json)
# print the JSON string representation of the object
print(PluginCreateDTO.to_json())

# convert the object into a dict
plugin_create_dto_dict = plugin_create_dto_instance.to_dict()
# create an instance of PluginCreateDTO from a dict
plugin_create_dto_from_dict = PluginCreateDTO.from_dict(plugin_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


