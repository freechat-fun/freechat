# PromptTaskDetailsDTO

Prompt task detailed information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**task_id** | **str** | Prompt task identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**gmt_executed** | **datetime** | Latest executed time | [optional] 
**prompt_ref** | [**PromptRefDTO**](PromptRefDTO.md) |  | [optional] 
**model_id** | **str** | Model identifier | [optional] 
**api_key_name** | **str** | API-KEY name | [optional] 
**api_key_value** | **str** | API-KEY value | [optional] 
**params** | **Dict[str, object]** | Model call parameters | [optional] 
**cron** | **str** | Task scheduling configuration which compatible with Quartz cron format | [optional] 
**status** | **str** | Task execution status: pending | running | succeeded | failed | [optional] 

## Example

```python
from freechat_sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptTaskDetailsDTO from a JSON string
prompt_task_details_dto_instance = PromptTaskDetailsDTO.from_json(json)
# print the JSON string representation of the object
print PromptTaskDetailsDTO.to_json()

# convert the object into a dict
prompt_task_details_dto_dict = prompt_task_details_dto_instance.to_dict()
# create an instance of PromptTaskDetailsDTO from a dict
prompt_task_details_dto_form_dict = prompt_task_details_dto.from_dict(prompt_task_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


