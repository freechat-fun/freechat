# ChatContextDTO

Chat context

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**chat_id** | **str** | Chat identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**gmt_read** | **datetime** | Read time | [optional] 
**user_nickname** | **str** | User nickname for this session | [optional] 
**user_profile** | **str** | User profile for this session | [optional] 
**character_nickname** | **str** | Character nickname for this session | [optional] 
**about** | **str** | Anything about this session | [optional] 
**backend_id** | **str** | Character backend for this session | 
**api_key_name** | **str** | API-KEY name, priority: apiKeyName &gt; apiKeyValue | [optional] 
**api_key_value** | **str** | API-KEY value | [optional] 
**quota** | **int** | Quota of this chat | [optional] 
**quota_type** | **str** | Quota type: messages | tokens | none (not limited) | [optional] 
**ext** | **str** | Extra info for this session | [optional] 

## Example

```python
from freechat_sdk.models.chat_context_dto import ChatContextDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatContextDTO from a JSON string
chat_context_dto_instance = ChatContextDTO.from_json(json)
# print the JSON string representation of the object
print ChatContextDTO.to_json()

# convert the object into a dict
chat_context_dto_dict = chat_context_dto_instance.to_dict()
# create an instance of ChatContextDTO from a dict
chat_context_dto_form_dict = chat_context_dto.from_dict(chat_context_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


