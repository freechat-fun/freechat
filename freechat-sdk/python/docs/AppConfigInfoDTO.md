# AppConfigInfoDTO

Configuration information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**name** | **str** | Configuration name | [optional] 
**format** | **str** | Configuration format: kv | json | yaml | [optional] 
**content** | **str** | Configuration content | [optional] 
**version** | **int** | Configuration version | [optional] 

## Example

```python
from freechat_sdk.models.app_config_info_dto import AppConfigInfoDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AppConfigInfoDTO from a JSON string
app_config_info_dto_instance = AppConfigInfoDTO.from_json(json)
# print the JSON string representation of the object
print(AppConfigInfoDTO.to_json())

# convert the object into a dict
app_config_info_dto_dict = app_config_info_dto_instance.to_dict()
# create an instance of AppConfigInfoDTO from a dict
app_config_info_dto_from_dict = AppConfigInfoDTO.from_dict(app_config_info_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


