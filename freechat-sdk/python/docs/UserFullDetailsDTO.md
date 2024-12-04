# UserFullDetailsDTO

Account detailed information (with password)

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**username** | **str** |  | [optional] 
**nickname** | **str** |  | [optional] 
**given_name** | **str** |  | [optional] 
**middle_name** | **str** |  | [optional] 
**family_name** | **str** |  | [optional] 
**preferred_username** | **str** |  | [optional] 
**profile** | **str** |  | [optional] 
**picture** | **str** |  | [optional] 
**website** | **str** |  | [optional] 
**email** | **str** |  | [optional] 
**gender** | **str** |  | [optional] 
**birthdate** | **datetime** |  | [optional] 
**zoneinfo** | **str** |  | [optional] 
**locale** | **str** |  | [optional] 
**phone_number** | **str** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 
**platform** | **str** |  | [optional] 
**enabled** | **bool** |  | [optional] 
**locked** | **bool** |  | [optional] 
**expires_at** | **datetime** |  | [optional] 
**password_expires_at** | **datetime** |  | [optional] 
**address** | **str** |  | [optional] 
**password** | **str** |  | [optional] 

## Example

```python
from freechat_sdk.models.user_full_details_dto import UserFullDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of UserFullDetailsDTO from a JSON string
user_full_details_dto_instance = UserFullDetailsDTO.from_json(json)
# print the JSON string representation of the object
print(UserFullDetailsDTO.to_json())

# convert the object into a dict
user_full_details_dto_dict = user_full_details_dto_instance.to_dict()
# create an instance of UserFullDetailsDTO from a dict
user_full_details_dto_from_dict = UserFullDetailsDTO.from_dict(user_full_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


