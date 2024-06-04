# ChatSessionDTO

Chat session

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**context** | [**ChatContextDTO**](ChatContextDTO.md) |  | [optional] 
**character** | [**CharacterSummaryDTO**](CharacterSummaryDTO.md) |  | [optional] 
**provider** | **str** | Model provider: hugging_face | open_ai | azure_open_ai | local_ai | in_process | dash_scope | unknown | [optional] 
**latest_message_record** | [**ChatMessageRecordDTO**](ChatMessageRecordDTO.md) |  | [optional] 
**proactive_chat_waiting_time** | **int** | Minutes to wait for a proactive chat | [optional] 
**sender_status** | **str** | Sender status: online | offline | invisible | [optional] 
**is_debug_enabled** | **bool** | Is it possible to debug | [optional] 
**is_customized_api_key_enabled** | **bool** | Is it possible to customize api-key | [optional] 

## Example

```python
from freechat_sdk.models.chat_session_dto import ChatSessionDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatSessionDTO from a JSON string
chat_session_dto_instance = ChatSessionDTO.from_json(json)
# print the JSON string representation of the object
print(ChatSessionDTO.to_json())

# convert the object into a dict
chat_session_dto_dict = chat_session_dto_instance.to_dict()
# create an instance of ChatSessionDTO from a dict
chat_session_dto_from_dict = ChatSessionDTO.from_dict(chat_session_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


