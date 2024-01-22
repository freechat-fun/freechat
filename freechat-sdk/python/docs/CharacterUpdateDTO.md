# CharacterUpdateDTO

Request data for updating character information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parent_id** | **str** | Referenced character | [optional] 
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **str** | Character name | 
**description** | **str** | Character description | [optional] 
**avatar** | **str** | Character avatar url | [optional] 
**picture** | **str** | Character picture url | [optional] 
**gender** | **str** | Character gender: male | female | other | [optional] 
**profile** | **str** | Character profile | [optional] 
**greeting** | **str** | Character greeting | [optional] 
**chat_style** | **str** | Character chat-style | [optional] 
**chat_example** | **str** | Character chat-example | [optional] 
**experience** | **str** | Character experience | [optional] 
**lang** | **str** | Character language: English | Chinese (Simplified) | ... | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Character draft information | [optional] 
**tags** | **List[str]** | Tag set | [optional] 

## Example

```python
from freechat_sdk.models.character_update_dto import CharacterUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterUpdateDTO from a JSON string
character_update_dto_instance = CharacterUpdateDTO.from_json(json)
# print the JSON string representation of the object
print CharacterUpdateDTO.to_json()

# convert the object into a dict
character_update_dto_dict = character_update_dto_instance.to_dict()
# create an instance of CharacterUpdateDTO from a dict
character_update_dto_form_dict = character_update_dto.from_dict(character_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


