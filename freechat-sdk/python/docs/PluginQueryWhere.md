# PluginQueryWhere

Query condition

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **str** | Visibility: public, public_org (search this organization), private (default) | [optional] 
**username** | **str** | Effective when searching public, public_org prompts, if not specified, search all users | [optional] 
**manifest_format** | **str** | Manifest configuration format, currently supported: dash_scope, open_ai. | [optional] 
**api_format** | **str** | API description format, currently supported: openapi_v3. | [optional] 
**tags** | **List[str]** | Tags | [optional] 
**tags_op** | **str** | Relationship between tags: and | or (default) | [optional] 
**ai_models** | **List[str]** | Model set | [optional] 
**ai_models_op** | **str** | Relationship between model sets: and | or (default) | [optional] 
**name** | **str** | Name, left match | [optional] 
**provider** | **str** | Provider, left match | [optional] 
**text** | **str** | Name, provider Information, manifest (real-time pulling is not supported at the moment), fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time. | [optional] 

## Example

```python
from freechat_sdk.models.plugin_query_where import PluginQueryWhere

# TODO update the JSON string below
json = "{}"
# create an instance of PluginQueryWhere from a JSON string
plugin_query_where_instance = PluginQueryWhere.from_json(json)
# print the JSON string representation of the object
print(PluginQueryWhere.to_json())

# convert the object into a dict
plugin_query_where_dict = plugin_query_where_instance.to_dict()
# create an instance of PluginQueryWhere from a dict
plugin_query_where_from_dict = PluginQueryWhere.from_dict(plugin_query_where_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


