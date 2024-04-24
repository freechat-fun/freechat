# AgentQueryWhere

Query condition

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **str** | Visibility: public, public_org (search this organization), private (default) | [optional] 
**username** | **str** | Effective when searching public, public_org prompts, if not specified, search all users | [optional] 
**format** | **str** | Agent configuration format, currently supported: langflow. | [optional] 
**tags** | **List[str]** | Tags | [optional] 
**tags_op** | **str** | Relationship between tags: and | or (default) | [optional] 
**ai_models** | **List[str]** | Model set | [optional] 
**ai_models_op** | **str** | Relationship between model sets: and | or (default) | [optional] 
**name** | **str** | Name, left match | [optional] 
**text** | **str** | Name, description, example, fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time. | [optional] 

## Example

```python
from freechat_sdk.models.agent_query_where import AgentQueryWhere

# TODO update the JSON string below
json = "{}"
# create an instance of AgentQueryWhere from a JSON string
agent_query_where_instance = AgentQueryWhere.from_json(json)
# print the JSON string representation of the object
print(AgentQueryWhere.to_json())

# convert the object into a dict
agent_query_where_dict = agent_query_where_instance.to_dict()
# create an instance of AgentQueryWhere from a dict
agent_query_where_from_dict = AgentQueryWhere.from_dict(agent_query_where_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


