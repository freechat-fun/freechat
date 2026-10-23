# SkillDraftDTO

Reviewed installation preview; confirmation is bound to its exact digest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**draft_id** | **str** | Owner-scoped single-use draft identifier | [optional]
**skill_id** | **str** | Server-assigned skill identifier | [optional]
**expected_revision** | **str** | Revision being replaced, absent for a new skill | [optional]
**name** | **str** | Validated skill name | [optional]
**description** | **str** | Validated skill description | [optional]
**files** | **Dict[str, str]** | Complete staged document set for confirmation | [optional]
**digest** | **str** | Digest to supply when confirming this preview | [optional]
**expires_at** | **datetime** | Confirmation expiry time | [optional]

## Example

```python
from freechat_sdk.models.skill_draft_dto import SkillDraftDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillDraftDTO from a JSON string
skill_draft_dto_instance = SkillDraftDTO.from_json(json)
# print the JSON string representation of the object
print(SkillDraftDTO.to_json())

# convert the object into a dict
skill_draft_dto_dict = skill_draft_dto_instance.to_dict()
# create an instance of SkillDraftDTO from a dict
skill_draft_dto_from_dict = SkillDraftDTO.from_dict(skill_draft_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


