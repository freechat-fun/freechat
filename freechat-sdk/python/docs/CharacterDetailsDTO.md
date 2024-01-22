# CharacterDetailsDTO

Character detailed content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**character_id** | **str** | Character identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Character name | 
**description** | **str** | Character description | [optional] 
**avatar** | **str** | Character avatar url | [optional] 
**picture** | **str** | Character picture url | [optional] 
**gender** | **str** | Character gender: male | female | other | [optional] 
**lang** | **str** | Character language: English | Chinese (Simplified) | ... | [optional] 
**username** | **str** | Character owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**profile** | **str** | Character profile | [optional] 
**greeting** | **str** | Character greeting | [optional] 
**chat_style** | **str** | Character chat-style | [optional] 
**chat_example** | **str** | Character chat-example | [optional] 
**experience** | **str** | Character experience | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 
**draft** | **str** | Character draft information | [optional] 
**backends** | [**List[CharacterBackendDetailsDTO]**](CharacterBackendDetailsDTO.md) | Character backends information | [optional] 

## Example

```python
from freechat_sdk.models.character_details_dto import CharacterDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterDetailsDTO from a JSON string
character_details_dto_instance = CharacterDetailsDTO.from_json(json)
# print the JSON string representation of the object
print CharacterDetailsDTO.to_json()

# convert the object into a dict
character_details_dto_dict = character_details_dto_instance.to_dict()
# create an instance of CharacterDetailsDTO from a dict
character_details_dto_form_dict = character_details_dto.from_dict(character_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


