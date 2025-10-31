# ChatMessageDTO

Chat message

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**role** | **str** | Chat role: system | assistant | user | tool_call | tool_result | [optional] 
**name** | **str** | user: name of the user role; tool_call: name of the called tool | [optional] 
**contents** | [**List[ChatContentDTO]**](ChatContentDTO.md) | default: dialogue content; tool_result: tool call result, serialized as json | [optional] 
**tool_calls** | [**List[ChatToolCallDTO]**](ChatToolCallDTO.md) | Tool calls information during the conversation | [optional] 
**thinking** | **str** | Thinking information | [optional] 
**context** | **str** | Contextual information in this round of conversation (the external RAG result can be passed in through this parameter) | [optional] 
**message_id** | **int** | Message identifier | [optional] 

## Example

```python
from freechat_sdk.models.chat_message_dto import ChatMessageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatMessageDTO from a JSON string
chat_message_dto_instance = ChatMessageDTO.from_json(json)
# print the JSON string representation of the object
print(ChatMessageDTO.to_json())

# convert the object into a dict
chat_message_dto_dict = chat_message_dto_instance.to_dict()
# create an instance of ChatMessageDTO from a dict
chat_message_dto_from_dict = ChatMessageDTO.from_dict(chat_message_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


