# AiModelInfoUpdateDTO

Request data for creating/updating model information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Model name | 
**description** | **str** | Model description | [optional] 
**provider** | **str** | Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown | [optional] 
**type** | **str** | Model type: text2text | text2chat | text2image | embedding | moderation | [optional] 

## Example

```python
from freechat_sdk.models.ai_model_info_update_dto import AiModelInfoUpdateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AiModelInfoUpdateDTO from a JSON string
ai_model_info_update_dto_instance = AiModelInfoUpdateDTO.from_json(json)
# print the JSON string representation of the object
print(AiModelInfoUpdateDTO.to_json())

# convert the object into a dict
ai_model_info_update_dto_dict = ai_model_info_update_dto_instance.to_dict()
# create an instance of AiModelInfoUpdateDTO from a dict
ai_model_info_update_dto_from_dict = AiModelInfoUpdateDTO.from_dict(ai_model_info_update_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


