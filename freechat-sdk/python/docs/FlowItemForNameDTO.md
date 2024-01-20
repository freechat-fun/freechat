# FlowItemForNameDTO

Flow identifier and version information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**flow_id** | **str** | flowId | [optional] 
**version** | **int** | version | [optional] 
**stats** | [**InteractiveStatsDTO**](InteractiveStatsDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.flow_item_for_name_dto import FlowItemForNameDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FlowItemForNameDTO from a JSON string
flow_item_for_name_dto_instance = FlowItemForNameDTO.from_json(json)
# print the JSON string representation of the object
print FlowItemForNameDTO.to_json()

# convert the object into a dict
flow_item_for_name_dto_dict = flow_item_for_name_dto_instance.to_dict()
# create an instance of FlowItemForNameDTO from a dict
flow_item_for_name_dto_form_dict = flow_item_for_name_dto.from_dict(flow_item_for_name_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


