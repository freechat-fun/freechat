# PluginUpdateDTO

Request data for updating plugin information

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
from freechat-sdk.models.plugin_update_dto import PluginUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PluginUpdateDTO from a JSON string
plugin_update_dto_instance = PluginUpdateDTO.from_json(json)
# print the JSON string representation of the object
print PluginUpdateDTO.to_json()

# convert the object into a dict
plugin_update_dto_dict = plugin_update_dto_instance.to_dict()
# create an instance of PluginUpdateDTO from a dict
plugin_update_dto_form_dict = plugin_update_dto.from_dict(plugin_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


