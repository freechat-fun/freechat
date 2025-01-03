# ChatCreateDTO

Request data for starting a chat session

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

## Example

```python
from freechat_sdk.models.chat_create_dto import ChatCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatCreateDTO from a JSON string
chat_create_dto_instance = ChatCreateDTO.from_json(json)
# print the JSON string representation of the object
print(ChatCreateDTO.to_json())

# convert the object into a dict
chat_create_dto_dict = chat_create_dto_instance.to_dict()
# create an instance of ChatCreateDTO from a dict
chat_create_dto_from_dict = ChatCreateDTO.from_dict(chat_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


