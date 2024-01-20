# ChatMessageDTO

Chat message

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**role** | **str** | Chat role: system | assistant | user | function_call | function_result | [optional] 
**name** | **str** | user: Name of the user role; function_call: Name of the called tool | [optional] 
**content** | **str** | default: Dialogue content; function_result: function call result, serialized as json | [optional] 
**tool_calls** | [**List[ChatToolCallDTO]**](ChatToolCallDTO.md) | Tool calls information during the conversation | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 

## Example

```python
from freechat_sdk.models.chat_message_dto import ChatMessageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatMessageDTO from a JSON string
chat_message_dto_instance = ChatMessageDTO.from_json(json)
# print the JSON string representation of the object
print ChatMessageDTO.to_json()

# convert the object into a dict
chat_message_dto_dict = chat_message_dto_instance.to_dict()
# create an instance of ChatMessageDTO from a dict
chat_message_dto_form_dict = chat_message_dto.from_dict(chat_message_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


