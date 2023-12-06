# UserBasicInfoDTO

User summary information

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**username** | **str** | Username | [optional] 
**nickname** | **str** | Nickname | [optional] 
**picture** | **str** | Avatar | [optional] 

## Example

```python
from freechat-sdk.models.user_basic_info_dto import UserBasicInfoDTO

# TODO update the JSON string below
json = "{}"
# create an instance of UserBasicInfoDTO from a JSON string
user_basic_info_dto_instance = UserBasicInfoDTO.from_json(json)
# print the JSON string representation of the object
print UserBasicInfoDTO.to_json()

# convert the object into a dict
user_basic_info_dto_dict = user_basic_info_dto_instance.to_dict()
# create an instance of UserBasicInfoDTO from a dict
user_basic_info_dto_form_dict = user_basic_info_dto.from_dict(user_basic_info_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


