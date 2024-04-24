# PromptTaskDTO

Prompt task information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt_ref** | [**PromptRefDTO**](PromptRefDTO.md) |  | 
**model_id** | **str** | Model identifier | [optional] 
**api_key_name** | **str** | API-KEY name, priority: apiKeyName &gt; apiKeyValue | [optional] 
**api_key_value** | **str** | API-KEY value | [optional] 
**params** | **Dict[str, object]** | Model call parameters | [optional] 
**cron** | **str** | Task scheduling configuration which compatible with Quartz cron format | [optional] 
**status** | **str** | Task execution status: pending | running | succeeded | failed | [optional] 

## Example

```python
from freechat_sdk.models.prompt_task_dto import PromptTaskDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptTaskDTO from a JSON string
prompt_task_dto_instance = PromptTaskDTO.from_json(json)
# print the JSON string representation of the object
print(PromptTaskDTO.to_json())

# convert the object into a dict
prompt_task_dto_dict = prompt_task_dto_instance.to_dict()
# create an instance of PromptTaskDTO from a dict
prompt_task_dto_from_dict = PromptTaskDTO.from_dict(prompt_task_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


