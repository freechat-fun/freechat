# ChatContentDTO

Chat content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**content** | **str** | Chat content. | 
**attachment** | **str** | Chat attachment. | [optional] 
**context** | **str** | Contextual information the character should know in this round of conversation | [optional] 

## Example

```python
from freechat_sdk.models.chat_content_dto import ChatContentDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatContentDTO from a JSON string
chat_content_dto_instance = ChatContentDTO.from_json(json)
# print the JSON string representation of the object
print ChatContentDTO.to_json()

# convert the object into a dict
chat_content_dto_dict = chat_content_dto_instance.to_dict()
# create an instance of ChatContentDTO from a dict
chat_content_dto_form_dict = chat_content_dto.from_dict(chat_content_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


