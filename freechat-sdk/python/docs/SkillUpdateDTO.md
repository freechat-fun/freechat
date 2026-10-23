# SkillUpdateDTO

Replace a skill only after the complete resulting bundle is validated and reviewed again

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**expected_revision** | **str** | Current revision returned by getSkill; stale saves are rejected |
**files** | **Dict[str, str]** | Complete resulting document set, including unchanged files |

## Example

```python
from freechat_sdk.models.skill_update_dto import SkillUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillUpdateDTO from a JSON string
skill_update_dto_instance = SkillUpdateDTO.from_json(json)
# print the JSON string representation of the object
print(SkillUpdateDTO.to_json())

# convert the object into a dict
skill_update_dto_dict = skill_update_dto_instance.to_dict()
# create an instance of SkillUpdateDTO from a dict
skill_update_dto_from_dict = SkillUpdateDTO.from_dict(skill_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


