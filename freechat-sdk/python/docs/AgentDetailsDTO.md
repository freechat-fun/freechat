# AgentDetailsDTO

Agent detailed content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**agent_id** | **int** | Agent identifier, will change after publish | [optional] 
**agent_uid** | **str** | Agent immutable identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**parent_uid** | **str** | Referenced agent | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**format** | **str** | Agent format, currently supported: langflow | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Agent name | [optional] 
**description** | **str** | Agent description | [optional] 
**username** | **str** | Agent owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | [**List[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 
**config** | **str** | Agent configuration | [optional] 
**example** | **str** | Agent example | [optional] 
**parameters** | **str** | Agent parameters, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 

## Example

```python
from freechat_sdk.models.agent_details_dto import AgentDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AgentDetailsDTO from a JSON string
agent_details_dto_instance = AgentDetailsDTO.from_json(json)
# print the JSON string representation of the object
print(AgentDetailsDTO.to_json())

# convert the object into a dict
agent_details_dto_dict = agent_details_dto_instance.to_dict()
# create an instance of AgentDetailsDTO from a dict
agent_details_dto_from_dict = AgentDetailsDTO.from_dict(agent_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


