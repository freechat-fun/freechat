# CharacterItemForNameDTO

Character identifier and version information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**character_id** | **str** | characterId | [optional] 
**version** | **int** | version | [optional] 
**stats** | [**InteractiveStatsDTO**](InteractiveStatsDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterItemForNameDTO from a JSON string
character_item_for_name_dto_instance = CharacterItemForNameDTO.from_json(json)
# print the JSON string representation of the object
print CharacterItemForNameDTO.to_json()

# convert the object into a dict
character_item_for_name_dto_dict = character_item_for_name_dto_instance.to_dict()
# create an instance of CharacterItemForNameDTO from a dict
character_item_for_name_dto_form_dict = character_item_for_name_dto.from_dict(character_item_for_name_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


