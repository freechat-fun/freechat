# PromptUpdateDTO

Request data for updating prompt information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parent_uid** | **str** | Referenced prompt | [optional] 
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **str** | Prompt name | 
**description** | **str** | Prompt description | [optional] 
**template** | **str** | Prompt text template content, choose one from template and chatTemplate field, priority: template &gt; chatTemplate | [optional] 
**chat_template** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) | Prompt chat template content | [optional] 
**format** | **str** | Prompt format: mustache (default) | f_string | [optional] 
**lang** | **str** | Prompt language: en (default) | zh_CN | ... | [optional] 
**example** | **str** | Prompt example | [optional] 
**inputs** | **str** | Prompt parameters, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | **List[str]** | Supported model set, empty means no model limit | [optional] 

## Example

```python
from freechat_sdk.models.prompt_update_dto import PromptUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptUpdateDTO from a JSON string
prompt_update_dto_instance = PromptUpdateDTO.from_json(json)
# print the JSON string representation of the object
print(PromptUpdateDTO.to_json())

# convert the object into a dict
prompt_update_dto_dict = prompt_update_dto_instance.to_dict()
# create an instance of PromptUpdateDTO from a dict
prompt_update_dto_from_dict = PromptUpdateDTO.from_dict(prompt_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


