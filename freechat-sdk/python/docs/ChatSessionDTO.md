# ChatSessionDTO

Chat session

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**context** | [**ChatContextDTO**](ChatContextDTO.md) |  | [optional] 
**character** | [**CharacterSummaryDTO**](CharacterSummaryDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.chat_session_dto import ChatSessionDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatSessionDTO from a JSON string
chat_session_dto_instance = ChatSessionDTO.from_json(json)
# print the JSON string representation of the object
print ChatSessionDTO.to_json()

# convert the object into a dict
chat_session_dto_dict = chat_session_dto_instance.to_dict()
# create an instance of ChatSessionDTO from a dict
chat_session_dto_form_dict = chat_session_dto.from_dict(chat_session_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


