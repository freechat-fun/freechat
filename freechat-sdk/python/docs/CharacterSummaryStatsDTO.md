# CharacterSummaryStatsDTO

Character summary content, including interactive statistical information

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
**view_count** | **int** | View count | [optional] 
**refer_count** | **int** | Reference count | [optional] 
**recommend_count** | **int** | Recommendation count | [optional] 
**score_count** | **int** | Score count | [optional] 
**score** | **int** | Average score | [optional] 

## Example

```python
from freechat-sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterSummaryStatsDTO from a JSON string
character_summary_stats_dto_instance = CharacterSummaryStatsDTO.from_json(json)
# print the JSON string representation of the object
print CharacterSummaryStatsDTO.to_json()

# convert the object into a dict
character_summary_stats_dto_dict = character_summary_stats_dto_instance.to_dict()
# create an instance of CharacterSummaryStatsDTO from a dict
character_summary_stats_dto_form_dict = character_summary_stats_dto.from_dict(character_summary_stats_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


