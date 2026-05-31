# TgMessageDTO

Telegram message information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** | Record identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**chat_id** | **str** | Related tg_chat.chat_id | [optional] 
**tg_message_id** | **int** | Telegram message id | [optional] 
**tg_user_id** | **int** | Sender telegram user id | [optional] 
**direction** | **str** | Direction: in | out | [optional] 
**message_type** | **str** | Message type | [optional] 
**content** | **str** | Plain text content | [optional] 
**payload** | **str** | Raw telegram update payload (JSON) | [optional] 

## Example

```python
from freechat_sdk.models.tg_message_dto import TgMessageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of TgMessageDTO from a JSON string
tg_message_dto_instance = TgMessageDTO.from_json(json)
# print the JSON string representation of the object
print(TgMessageDTO.to_json())

# convert the object into a dict
tg_message_dto_dict = tg_message_dto_instance.to_dict()
# create an instance of TgMessageDTO from a dict
tg_message_dto_from_dict = TgMessageDTO.from_dict(tg_message_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


