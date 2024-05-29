# CharacterSummaryStatsDTO

Character summary content, including interactive statistical information

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
**default_scene** | **str** | Default scene, which will be set as the default conversation background information when creating a new chat | [optional] 
**priority** | **int** | Character priority | [optional] 
**username** | **str** | Character owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**view_count** | **int** | View count | [optional] 
**refer_count** | **int** | Reference count | [optional] 
**recommend_count** | **int** | Recommendation count | [optional] 
**score_count** | **int** | Score count | [optional] 
**score** | **int** | Average score | [optional] 

## Example

```python
from freechat_sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterSummaryStatsDTO from a JSON string
character_summary_stats_dto_instance = CharacterSummaryStatsDTO.from_json(json)
# print the JSON string representation of the object
print(CharacterSummaryStatsDTO.to_json())

# convert the object into a dict
character_summary_stats_dto_dict = character_summary_stats_dto_instance.to_dict()
# create an instance of CharacterSummaryStatsDTO from a dict
character_summary_stats_dto_from_dict = CharacterSummaryStatsDTO.from_dict(character_summary_stats_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


