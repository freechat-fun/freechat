# AiModelInfoDTO

Model information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**model_id** | **str** | Model identifier: [provider]name | [optional] 
**name** | **str** | Model name | [optional] 
**description** | **str** | Model description | [optional] 
**provider** | **str** | Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown | [optional] 
**type** | **str** | Model type: text2text | text2chat | text2image | embedding | moderation | [optional] 

## Example

```python
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AiModelInfoDTO from a JSON string
ai_model_info_dto_instance = AiModelInfoDTO.from_json(json)
# print the JSON string representation of the object
print(AiModelInfoDTO.to_json())

# convert the object into a dict
ai_model_info_dto_dict = ai_model_info_dto_instance.to_dict()
# create an instance of AiModelInfoDTO from a dict
ai_model_info_dto_from_dict = AiModelInfoDTO.from_dict(ai_model_info_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


