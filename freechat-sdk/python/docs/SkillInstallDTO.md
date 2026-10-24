# SkillInstallDTO

Install a prompt-only skill bundle from text documents or an LLM-analyzed approved source link

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**files** | **Dict[str, str]** | Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl | [optional]
**source_url** | **str** | GitHub repository/skill-directory URL, ClawHub skill-page URL, or raw document URL from an allowed host. May include surrounding text or omit the HTTPS scheme; the LLM selects only independently validated source candidates. Provider bundles are pinned and fully validated, not recursively crawled. | [optional]

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


