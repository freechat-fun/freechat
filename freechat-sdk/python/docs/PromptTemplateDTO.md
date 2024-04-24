# PromptTemplateDTO

Prompt template content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**template** | **str** | Prompt text template content, choose one between this and chatTemplate field, priority: template &gt; chatTemplate | [optional] 
**chat_template** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) |  | [optional] 
**variables** | **Dict[str, object]** | Variables applied to the template, can be empty | [optional] 
**format** | **str** | Prompt format: mustache (default) | f_string | [optional] 

## Example

```python
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptTemplateDTO from a JSON string
prompt_template_dto_instance = PromptTemplateDTO.from_json(json)
# print the JSON string representation of the object
print(PromptTemplateDTO.to_json())

# convert the object into a dict
prompt_template_dto_dict = prompt_template_dto_instance.to_dict()
# create an instance of PromptTemplateDTO from a dict
prompt_template_dto_from_dict = PromptTemplateDTO.from_dict(prompt_template_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


