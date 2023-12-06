# AppMetaDTO

Application metadata

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Application name | [optional] 
**version** | **str** | Application version | [optional] 
**build_timestamp** | **str** | Build time | [optional] 
**build_number** | **str** | Build number | [optional] 
**commit_url** | **str** | Commit URL | [optional] 
**release_note_url** | **str** | Release notes | [optional] 
**running_env** | **str** | Running environment | [optional] 

## Example

```python
from freechat-sdk.models.app_meta_dto import AppMetaDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AppMetaDTO from a JSON string
app_meta_dto_instance = AppMetaDTO.from_json(json)
# print the JSON string representation of the object
print AppMetaDTO.to_json()

# convert the object into a dict
app_meta_dto_dict = app_meta_dto_instance.to_dict()
# create an instance of AppMetaDTO from a dict
app_meta_dto_form_dict = app_meta_dto.from_dict(app_meta_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


