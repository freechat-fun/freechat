# AiApiKeyCreateDTO

Request data for adding new model credential information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Credential name | 
**provider** | **str** | Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown | 
**token** | **str** | Credential content | 
**enabled** | **bool** | Whether to enable (enabled by default) | [optional] 

## Example

```python
from freechat_sdk.models.ai_api_key_create_dto import AiApiKeyCreateDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AiApiKeyCreateDTO from a JSON string
ai_api_key_create_dto_instance = AiApiKeyCreateDTO.from_json(json)
# print the JSON string representation of the object
print(AiApiKeyCreateDTO.to_json())

# convert the object into a dict
ai_api_key_create_dto_dict = ai_api_key_create_dto_instance.to_dict()
# create an instance of AiApiKeyCreateDTO from a dict
ai_api_key_create_dto_from_dict = AiApiKeyCreateDTO.from_dict(ai_api_key_create_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


