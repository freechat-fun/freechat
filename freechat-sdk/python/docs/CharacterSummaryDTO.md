# CharacterSummaryDTO

Character summary content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**character_id** | **int** | Character identifier, will change after publish | [optional] 
**character_uid** | **str** | Character immutable identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**parent_uid** | **str** | Referenced character | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Character name | [optional] 
**description** | **str** | Character description | [optional] 
**nickname** | **str** | Character nickname | [optional] 
**avatar** | **str** | Character avatar url | [optional] 
**picture** | **str** | Character picture url | [optional] 
**gender** | **str** | Character gender: male | female | other | [optional] 
**lang** | **str** | Character language: en (default) | zh | ... | [optional] 
**greeting** | **str** | Character greeting | [optional] 
**username** | **str** | Character owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 

## Example

```python
from freechat_sdk.models.character_summary_dto import CharacterSummaryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterSummaryDTO from a JSON string
character_summary_dto_instance = CharacterSummaryDTO.from_json(json)
# print the JSON string representation of the object
print CharacterSummaryDTO.to_json()

# convert the object into a dict
character_summary_dto_dict = character_summary_dto_instance.to_dict()
# create an instance of CharacterSummaryDTO from a dict
character_summary_dto_form_dict = character_summary_dto.from_dict(character_summary_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


