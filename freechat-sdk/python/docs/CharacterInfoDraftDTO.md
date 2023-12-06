# CharacterInfoDraftDTO

Character draft information (for prompt)

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**profile** | **str** | Character profile | [optional] 
**greeting** | **str** | Character greeting | [optional] 
**chat_style** | **str** | Character chat-style | [optional] 
**chat_example** | **str** | Character chat-example | [optional] 
**experience** | **str** | Character experience | [optional] 

## Example

```python
from freechat-sdk.models.character_info_draft_dto import CharacterInfoDraftDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterInfoDraftDTO from a JSON string
character_info_draft_dto_instance = CharacterInfoDraftDTO.from_json(json)
# print the JSON string representation of the object
print CharacterInfoDraftDTO.to_json()

# convert the object into a dict
character_info_draft_dto_dict = character_info_draft_dto_instance.to_dict()
# create an instance of CharacterInfoDraftDTO from a dict
character_info_draft_dto_form_dict = character_info_draft_dto.from_dict(character_info_draft_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


