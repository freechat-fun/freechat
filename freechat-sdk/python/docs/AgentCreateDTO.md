# AgentCreateDTO

Request data for creating new agent information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parent_uid** | **str** | Referenced agent | [optional] 
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**format** | **str** | Agent format, currently supported: langflow | [optional] 
**name** | **str** | Agent name | 
**description** | **str** | Agent description | [optional] 
**config** | **str** | Agent configuration | [optional] 
**example** | **str** | Agent example | [optional] 
**parameters** | **str** | Agent parameters, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | **List[str]** | Supported model set, empty means no model limit | [optional] 

## Example

```python
from freechat_sdk.models.agent_create_dto import AgentCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AgentCreateDTO from a JSON string
agent_create_dto_instance = AgentCreateDTO.from_json(json)
# print the JSON string representation of the object
print AgentCreateDTO.to_json()

# convert the object into a dict
agent_create_dto_dict = agent_create_dto_instance.to_dict()
# create an instance of AgentCreateDTO from a dict
agent_create_dto_form_dict = agent_create_dto.from_dict(agent_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


