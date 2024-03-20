# PluginSummaryDTO

Plugin summary information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**plugin_id** | **int** | Plugin identifier | [optional] 
**plugin_uid** | **str** | Plugin immutable identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**name** | **str** | Plugin name | [optional] 
**provider** | **str** | Information of the provider | [optional] 
**manifest_format** | **str** | Manifest format, currently supported: dash_scope, open_ai | [optional] 
**api_format** | **str** | API description format, currently supported: openapi_v3 | [optional] 
**username** | **str** | Plugin owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | [**List[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 

## Example

```python
from freechat_sdk.models.plugin_summary_dto import PluginSummaryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PluginSummaryDTO from a JSON string
plugin_summary_dto_instance = PluginSummaryDTO.from_json(json)
# print the JSON string representation of the object
print PluginSummaryDTO.to_json()

# convert the object into a dict
plugin_summary_dto_dict = plugin_summary_dto_instance.to_dict()
# create an instance of PluginSummaryDTO from a dict
plugin_summary_dto_form_dict = plugin_summary_dto.from_dict(plugin_summary_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


