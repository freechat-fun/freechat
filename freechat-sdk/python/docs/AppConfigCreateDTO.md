# AppConfigCreateDTO

Configuration creation information

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Configuration name | 
**format** | **str** | Configuration format: kv | json | yaml | [optional] 
**content** | **str** | Configuration content | [optional] 

## Example

```python
from freechat-sdk.models.app_config_create_dto import AppConfigCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AppConfigCreateDTO from a JSON string
app_config_create_dto_instance = AppConfigCreateDTO.from_json(json)
# print the JSON string representation of the object
print AppConfigCreateDTO.to_json()

# convert the object into a dict
app_config_create_dto_dict = app_config_create_dto_instance.to_dict()
# create an instance of AppConfigCreateDTO from a dict
app_config_create_dto_form_dict = app_config_create_dto.from_dict(app_config_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


