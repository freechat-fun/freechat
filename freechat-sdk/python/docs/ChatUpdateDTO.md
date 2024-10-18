# ChatUpdateDTO

Request data for updating a chat session

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**user_nickname** | **str** | User nickname for this session | [optional] 
**user_profile** | **str** | User profile for this session | [optional] 
**character_nickname** | **str** | Character nickname for this session | [optional] 
**about** | **str** | Anything about this session | [optional] 
**character_uid** | **str** | Character uid for this session | 
**backend_id** | **str** | Character backend for this session | [optional] 
**api_key_name** | **str** | API-KEY name, priority: apiKeyName &gt; apiKeyValue | [optional] 
**api_key_value** | **str** | API-KEY value | [optional] 
**ext** | **str** | Extra info for this session | [optional] 
**gmt_read** | **datetime** | Read time | [optional] 

## Example

```python
from freechat_sdk.models.chat_update_dto import ChatUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatUpdateDTO from a JSON string
chat_update_dto_instance = ChatUpdateDTO.from_json(json)
# print the JSON string representation of the object
print(ChatUpdateDTO.to_json())

# convert the object into a dict
chat_update_dto_dict = chat_update_dto_instance.to_dict()
# create an instance of ChatUpdateDTO from a dict
chat_update_dto_from_dict = ChatUpdateDTO.from_dict(chat_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


