# ChatPromptContentDTO

Prompt chat template content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**system** | **str** | Prompt system template | [optional] 
**message_to_send** | [**ChatMessageDTO**](ChatMessageDTO.md) |  | [optional] 
**messages** | [**List[ChatMessageDTO]**](ChatMessageDTO.md) | Pre-set chat messages in the Prompt | [optional] 

## Example

```python
from freechat_sdk.models.chat_prompt_content_dto import ChatPromptContentDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatPromptContentDTO from a JSON string
chat_prompt_content_dto_instance = ChatPromptContentDTO.from_json(json)
# print the JSON string representation of the object
print ChatPromptContentDTO.to_json()

# convert the object into a dict
chat_prompt_content_dto_dict = chat_prompt_content_dto_instance.to_dict()
# create an instance of ChatPromptContentDTO from a dict
chat_prompt_content_dto_form_dict = chat_prompt_content_dto.from_dict(chat_prompt_content_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


