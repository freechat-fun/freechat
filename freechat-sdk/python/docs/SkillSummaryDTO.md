# SkillSummaryDTO

An installed, private, prompt-only skill

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**skill_id** | **str** | Opaque skill identifier | [optional]
**revision** | **str** | Current immutable revision identifier | [optional]
**name** | **str** | Skill name | [optional]
**description** | **str** | Skill description | [optional]
**tags** | **List[str]** | Descriptive labels, not permission grants | [optional]
**modified** | **datetime** | Last publication time | [optional]

## Example

```python
from freechat_sdk.models.skill_summary_dto import SkillSummaryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillSummaryDTO from a JSON string
skill_summary_dto_instance = SkillSummaryDTO.from_json(json)
# print the JSON string representation of the object
print(SkillSummaryDTO.to_json())

# convert the object into a dict
skill_summary_dto_dict = skill_summary_dto_instance.to_dict()
# create an instance of SkillSummaryDTO from a dict
skill_summary_dto_from_dict = SkillSummaryDTO.from_dict(skill_summary_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


