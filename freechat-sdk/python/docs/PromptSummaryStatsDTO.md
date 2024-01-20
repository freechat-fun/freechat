# PromptSummaryStatsDTO

Prompt template summary content, including interactive statistical information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**prompt_id** | **str** | Prompt identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**visibility** | **str** | Visibility: private, public, public_org, hidden | [optional] 
**version** | **int** | Version | [optional] 
**name** | **str** | Prompt name | [optional] 
**type** | **str** | Prompt type: string | chat | [optional] 
**description** | **str** | Prompt description (50 characters, the excess part is represented by ellipsis) | [optional] 
**format** | **str** | Prompt format: mustache (default) | f_string | [optional] 
**lang** | **str** | Prompt language: en (default) | zh_CN | ... | [optional] 
**username** | **str** | Prompt owner | [optional] 
**tags** | **List[str]** | Tag set | [optional] 
**ai_models** | [**List[AiModelInfoDTO]**](AiModelInfoDTO.md) | Supported model set | [optional] 
**view_count** | **int** | View count | [optional] 
**refer_count** | **int** | Reference count | [optional] 
**recommend_count** | **int** | Recommendation count | [optional] 
**score_count** | **int** | Score count | [optional] 
**score** | **int** | Average score | [optional] 

## Example

```python
from freechat_sdk.models.prompt_summary_stats_dto import PromptSummaryStatsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptSummaryStatsDTO from a JSON string
prompt_summary_stats_dto_instance = PromptSummaryStatsDTO.from_json(json)
# print the JSON string representation of the object
print PromptSummaryStatsDTO.to_json()

# convert the object into a dict
prompt_summary_stats_dto_dict = prompt_summary_stats_dto_instance.to_dict()
# create an instance of PromptSummaryStatsDTO from a dict
prompt_summary_stats_dto_form_dict = prompt_summary_stats_dto.from_dict(prompt_summary_stats_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


