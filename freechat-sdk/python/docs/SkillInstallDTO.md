# SkillInstallDTO

Install a prompt-only skill from either text documents or an operator-approved HTTPS source

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**files** | **Dict[str, str]** | Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl | [optional]
**source_url** | **str** | Approved HTTPS URL of a raw SKILL.md document; no recursive fetching or redirects | [optional]

## Example

```python
from freechat_sdk.models.skill_install_dto import SkillInstallDTO

# TODO update the JSON string below
json = "{}"
# create an instance of SkillInstallDTO from a JSON string
skill_install_dto_instance = SkillInstallDTO.from_json(json)
# print the JSON string representation of the object
print(SkillInstallDTO.to_json())

# convert the object into a dict
skill_install_dto_dict = skill_install_dto_instance.to_dict()
# create an instance of SkillInstallDTO from a dict
skill_install_dto_from_dict = SkillInstallDTO.from_dict(skill_install_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


