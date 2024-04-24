# PromptDetailsDTO

Prompt detailed content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**prompt_id** | **int** | Prompt identifier, will change after publish | [optional] 
**prompt_uid** | **str** | Prompt immutable identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**parent_uid** | **str** | Referenced prompt | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Prompt name | [optional] 
**type** | **str** | Prompt type: string | chat | [optional] 
**description** | **str** | Prompt description (50 characters, the excess part is represented by ellipsis) | [optional] 
**format** | **str** | Prompt format: mustache (default) | f_string | [optional] 
**lang** | **str** | Prompt language: en (default) | zh | ... | [optional] 
**username** | **str** | Prompt owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | [**List[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 
**template** | **str** | Prompt text template content | [optional] 
**chat_template** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) |  | [optional] 
**example** | **str** | Prompt example | [optional] 
**inputs** | **str** | Prompt inputs, JSON format | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Draft content | [optional] 

## Example

```python
from freechat_sdk.models.prompt_details_dto import PromptDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptDetailsDTO from a JSON string
prompt_details_dto_instance = PromptDetailsDTO.from_json(json)
# print the JSON string representation of the object
print(PromptDetailsDTO.to_json())

# convert the object into a dict
prompt_details_dto_dict = prompt_details_dto_instance.to_dict()
# create an instance of PromptDetailsDTO from a dict
prompt_details_dto_from_dict = PromptDetailsDTO.from_dict(prompt_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


