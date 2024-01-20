# InteractiveStatsDTO

Interactive statistics information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**refer_type** | **str** | Resource type | [optional] 
**refer_id** | **str** | Resource identifier | [optional] 
**view_count** | **int** | View count | [optional] 
**refer_count** | **int** | Reference count | [optional] 
**recommend_count** | **int** | Recommendation count | [optional] 
**score_count** | **int** | Score count | [optional] 
**score** | **int** | Average score | [optional] 

## Example

```python
from freechat_sdk.models.interactive_stats_dto import InteractiveStatsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of InteractiveStatsDTO from a JSON string
interactive_stats_dto_instance = InteractiveStatsDTO.from_json(json)
# print the JSON string representation of the object
print InteractiveStatsDTO.to_json()

# convert the object into a dict
interactive_stats_dto_dict = interactive_stats_dto_instance.to_dict()
# create an instance of InteractiveStatsDTO from a dict
interactive_stats_dto_form_dict = interactive_stats_dto.from_dict(interactive_stats_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


