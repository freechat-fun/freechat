# CharacterSummaryDTO

Character summary content

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
**lang** | **str** | Character language: English | Chinese (Simplified) | ... | [optional] 
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


