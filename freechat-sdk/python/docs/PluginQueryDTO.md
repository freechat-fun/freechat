# PluginQueryDTO

Plugin information query request

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**PluginQueryWhere**](PluginQueryWhere.md) |  | [optional] 
**order_by** | **List[str]** | Ordering condition, supported sorting fields are: - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**page_num** | **int** | Page number, default is 0 | [optional] 
**page_size** | **int** | Page item count, 1～50, default is 10 | [optional] 

## Example

```python
from freechat_sdk.models.plugin_query_dto import PluginQueryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PluginQueryDTO from a JSON string
plugin_query_dto_instance = PluginQueryDTO.from_json(json)
# print the JSON string representation of the object
print(PluginQueryDTO.to_json())

# convert the object into a dict
plugin_query_dto_dict = plugin_query_dto_instance.to_dict()
# create an instance of PluginQueryDTO from a dict
plugin_query_dto_from_dict = PluginQueryDTO.from_dict(plugin_query_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


