# AgentItemForNameDTO

Agent identifier and version information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**agent_id** | **int** | agentId | [optional] 
**version** | **int** | version | [optional] 
**stats** | [**InteractiveStatsDTO**](InteractiveStatsDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AgentItemForNameDTO from a JSON string
agent_item_for_name_dto_instance = AgentItemForNameDTO.from_json(json)
# print the JSON string representation of the object
print(AgentItemForNameDTO.to_json())

# convert the object into a dict
agent_item_for_name_dto_dict = agent_item_for_name_dto_instance.to_dict()
# create an instance of AgentItemForNameDTO from a dict
agent_item_for_name_dto_from_dict = AgentItemForNameDTO.from_dict(agent_item_for_name_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


