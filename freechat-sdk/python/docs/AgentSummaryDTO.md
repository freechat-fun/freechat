# AgentSummaryDTO

Agent summary information

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

## Example

```python
from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AgentSummaryDTO from a JSON string
agent_summary_dto_instance = AgentSummaryDTO.from_json(json)
# print the JSON string representation of the object
print(AgentSummaryDTO.to_json())

# convert the object into a dict
agent_summary_dto_dict = agent_summary_dto_instance.to_dict()
# create an instance of AgentSummaryDTO from a dict
agent_summary_dto_from_dict = AgentSummaryDTO.from_dict(agent_summary_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


