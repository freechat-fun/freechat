# ChatToolCallDTO

Tool call information during the conversation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Tool id | [optional] 
**name** | **str** | Tool name | [optional] 
**arguments** | **str** | Tool parameters | [optional] 

## Example

```python
from freechat-sdk.models.chat_tool_call_dto import ChatToolCallDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatToolCallDTO from a JSON string
chat_tool_call_dto_instance = ChatToolCallDTO.from_json(json)
# print the JSON string representation of the object
print ChatToolCallDTO.to_json()

# convert the object into a dict
chat_tool_call_dto_dict = chat_tool_call_dto_instance.to_dict()
# create an instance of ChatToolCallDTO from a dict
chat_tool_call_dto_form_dict = chat_tool_call_dto.from_dict(chat_tool_call_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


