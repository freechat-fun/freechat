# CharacterCreateDTO

Request data for creating new character information

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parent_id** | **str** | Referenced character | [optional] 
**visibility** | **str** | Visibility: private (default), public, public_org, hidden | [optional] 
**name** | **str** | Character name | 
**description** | **str** | Character description | [optional] 
**avatar** | **str** | Character avatar url | [optional] 
**picture** | **str** | Character picture url | [optional] 
**gender** | **str** | Character gender: male | female | non_human | unknown | [optional] 
**profile** | **str** | Character profile | [optional] 
**greeting** | **str** | Character greeting | [optional] 
**chat_style** | **str** | Character chat-style | [optional] 
**chat_example** | **str** | Character chat-example | [optional] 
**experience** | **str** | Character experience | [optional] 
**lang** | **str** | Character language: English | Chinese (Simplified) | ... | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | [**CharacterInfoDraftDTO**](CharacterInfoDraftDTO.md) |  | [optional] 
**tags** | **List[str]** | Tag set | [optional] 

## Example

```python
from freechat-sdk.models.character_create_dto import CharacterCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterCreateDTO from a JSON string
character_create_dto_instance = CharacterCreateDTO.from_json(json)
# print the JSON string representation of the object
print CharacterCreateDTO.to_json()

# convert the object into a dict
character_create_dto_dict = character_create_dto_instance.to_dict()
# create an instance of CharacterCreateDTO from a dict
character_create_dto_form_dict = character_create_dto.from_dict(character_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


