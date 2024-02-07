# AgentQueryDTO

Agent information query request

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**AgentQueryWhere**](AgentQueryWhere.md) |  | [optional] 
**order_by** | **List[str]** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending. If ascending is expected, specify after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**page_num** | **int** | Page number, default is 0 | [optional] 
**page_size** | **int** | Number of items per page, 1~50, default is 10 | [optional] 

## Example

```python
from freechat_sdk.models.agent_query_dto import AgentQueryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AgentQueryDTO from a JSON string
agent_query_dto_instance = AgentQueryDTO.from_json(json)
# print the JSON string representation of the object
print AgentQueryDTO.to_json()

# convert the object into a dict
agent_query_dto_dict = agent_query_dto_instance.to_dict()
# create an instance of AgentQueryDTO from a dict
agent_query_dto_form_dict = agent_query_dto.from_dict(agent_query_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


