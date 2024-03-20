# PromptItemForNameDTO

Prompt identifier and version information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt_id** | **int** | promptId | [optional] 
**version** | **int** | version | [optional] 
**stats** | [**InteractiveStatsDTO**](InteractiveStatsDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.prompt_item_for_name_dto import PromptItemForNameDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptItemForNameDTO from a JSON string
prompt_item_for_name_dto_instance = PromptItemForNameDTO.from_json(json)
# print the JSON string representation of the object
print PromptItemForNameDTO.to_json()

# convert the object into a dict
prompt_item_for_name_dto_dict = prompt_item_for_name_dto_instance.to_dict()
# create an instance of PromptItemForNameDTO from a dict
prompt_item_for_name_dto_form_dict = prompt_item_for_name_dto.from_dict(prompt_item_for_name_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


