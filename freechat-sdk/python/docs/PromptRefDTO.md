# PromptRefDTO

Prompt reference information

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt_id** | **str** | Prompt identifier | 
**variables** | **Dict[str, object]** | Variables applied to the template, can be empty | [optional] 
**draft** | **bool** | Whether to use draft content | [optional] 

## Example

```python
from freechat-sdk.models.prompt_ref_dto import PromptRefDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptRefDTO from a JSON string
prompt_ref_dto_instance = PromptRefDTO.from_json(json)
# print the JSON string representation of the object
print PromptRefDTO.to_json()

# convert the object into a dict
prompt_ref_dto_dict = prompt_ref_dto_instance.to_dict()
# create an instance of PromptRefDTO from a dict
prompt_ref_dto_form_dict = prompt_ref_dto.from_dict(prompt_ref_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


