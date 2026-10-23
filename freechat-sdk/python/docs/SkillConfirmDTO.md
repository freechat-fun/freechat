# SkillConfirmDTO

Confirm publication of the exact reviewed installation preview

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**digest** | **str** | SHA-256 digest returned with the preview |

## Example

```python
from freechat_sdk.models.skill_confirm_dto import SkillConfirmDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillConfirmDTO from a JSON string
skill_confirm_dto_instance = SkillConfirmDTO.from_json(json)
# print the JSON string representation of the object
print(SkillConfirmDTO.to_json())

# convert the object into a dict
skill_confirm_dto_dict = skill_confirm_dto_instance.to_dict()
# create an instance of SkillConfirmDTO from a dict
skill_confirm_dto_from_dict = SkillConfirmDTO.from_dict(skill_confirm_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


