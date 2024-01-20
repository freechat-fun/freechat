# FlowDetailsDTO

Flow detailed content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**flow_id** | **str** | Flow identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**parent_id** | **str** | Referenced flow | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**format** | **str** | Flow format, currently supported: langflow | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Flow name | [optional] 
**description** | **str** | Flow description | [optional] 
**username** | **str** | Flow owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | [**List[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 
**config** | **str** | Flow configuration | [optional] 
**example** | **str** | Flow example | [optional] 
**parameters** | **str** | Flow parameters, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 

## Example

```python
from freechat_sdk.models.flow_details_dto import FlowDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FlowDetailsDTO from a JSON string
flow_details_dto_instance = FlowDetailsDTO.from_json(json)
# print the JSON string representation of the object
print FlowDetailsDTO.to_json()

# convert the object into a dict
flow_details_dto_dict = flow_details_dto_instance.to_dict()
# create an instance of FlowDetailsDTO from a dict
flow_details_dto_form_dict = flow_details_dto.from_dict(flow_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


