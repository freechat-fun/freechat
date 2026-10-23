# SkillDetailsDTO

Owner-only contents of an installed prompt skill

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**skill_id** | **str** | Opaque skill identifier | [optional]
**revision** | **str** | Current immutable revision identifier | [optional]
**name** | **str** | Skill name | [optional]
**description** | **str** | Skill description | [optional]
**tags** | **List[str]** | Descriptive labels, not permission grants | [optional]
**modified** | **datetime** | Last publication time | [optional]
**files** | **Dict[str, str]** | Logical document names and exact UTF-8 text; never server filesystem paths | [optional]
**digest** | **str** | SHA-256 digest of the complete validated bundle | [optional]

## Example

```python
from freechat_sdk.models.skill_details_dto import SkillDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillDetailsDTO from a JSON string
skill_details_dto_instance = SkillDetailsDTO.from_json(json)
# print the JSON string representation of the object
print(SkillDetailsDTO.to_json())

# convert the object into a dict
skill_details_dto_dict = skill_details_dto_instance.to_dict()
# create an instance of SkillDetailsDTO from a dict
skill_details_dto_from_dict = SkillDetailsDTO.from_dict(skill_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


