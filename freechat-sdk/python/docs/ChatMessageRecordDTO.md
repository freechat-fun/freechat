# ChatMessageRecordDTO

Chat message record

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**message** | [**ChatMessageDTO**](ChatMessageDTO.md) |  | [optional] 
**message_id** | **int** | Message identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**ext** | **str** | Additional information | [optional] 

## Example

```python
from freechat_sdk.models.chat_message_record_dto import ChatMessageRecordDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatMessageRecordDTO from a JSON string
chat_message_record_dto_instance = ChatMessageRecordDTO.from_json(json)
# print the JSON string representation of the object
print(ChatMessageRecordDTO.to_json())

# convert the object into a dict
chat_message_record_dto_dict = chat_message_record_dto_instance.to_dict()
# create an instance of ChatMessageRecordDTO from a dict
chat_message_record_dto_from_dict = ChatMessageRecordDTO.from_dict(chat_message_record_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


