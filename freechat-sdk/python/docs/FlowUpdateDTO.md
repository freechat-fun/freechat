# FlowUpdateDTO

Request data for updating flow information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parent_id** | **str** | Referenced flow | [optional] 
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**format** | **str** | Flow format, currently supported: langflow | [optional] 
**name** | **str** | Flow name | 
**description** | **str** | Flow description | [optional] 
**config** | **str** | Flow configuration | [optional] 
**example** | **str** | Flow example | [optional] 
**parameters** | **str** | Flow parameters, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | **List[str]** | Supported model set, empty means no model limit | [optional] 

## Example

```python
from freechat_sdk.models.flow_update_dto import FlowUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FlowUpdateDTO from a JSON string
flow_update_dto_instance = FlowUpdateDTO.from_json(json)
# print the JSON string representation of the object
print FlowUpdateDTO.to_json()

# convert the object into a dict
flow_update_dto_dict = flow_update_dto_instance.to_dict()
# create an instance of FlowUpdateDTO from a dict
flow_update_dto_form_dict = flow_update_dto.from_dict(flow_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


