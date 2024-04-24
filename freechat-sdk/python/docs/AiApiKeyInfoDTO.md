# AiApiKeyInfoDTO

Model credential information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**id** | **int** | Credential identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**gmt_used** | **datetime** | Last use time | [optional] 
**name** | **str** | Credential name | [optional] 
**provider** | **str** | Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown | [optional] 
**token** | **str** | Credential content | [optional] 
**enabled** | **bool** | Whether to enable | [optional] 
**username** | **str** | Credential owner | [optional] 

## Example

```python
from freechat_sdk.models.ai_api_key_info_dto import AiApiKeyInfoDTO

# TODO update the JSON string below
json = "{}"
# create an instance of AiApiKeyInfoDTO from a JSON string
ai_api_key_info_dto_instance = AiApiKeyInfoDTO.from_json(json)
# print the JSON string representation of the object
print(AiApiKeyInfoDTO.to_json())

# convert the object into a dict
ai_api_key_info_dto_dict = ai_api_key_info_dto_instance.to_dict()
# create an instance of AiApiKeyInfoDTO from a dict
ai_api_key_info_dto_from_dict = AiApiKeyInfoDTO.from_dict(ai_api_key_info_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


