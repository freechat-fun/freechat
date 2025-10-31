# ChatContentDTO

Chat content

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | **str** | Chat type: text (default) | image | video | audio | pdf | ... | [optional] 
**content** | **str** | Chat content(for image, it might be a normal url or data url) | 

## Example

```python
from freechat_sdk.models.chat_content_dto import ChatContentDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ChatContentDTO from a JSON string
chat_content_dto_instance = ChatContentDTO.from_json(json)
# print the JSON string representation of the object
print(ChatContentDTO.to_json())

# convert the object into a dict
chat_content_dto_dict = chat_content_dto_instance.to_dict()
# create an instance of ChatContentDTO from a dict
chat_content_dto_from_dict = ChatContentDTO.from_dict(chat_content_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


