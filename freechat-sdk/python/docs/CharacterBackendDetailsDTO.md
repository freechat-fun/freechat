# CharacterBackendDetailsDTO

Character backend detailed information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**backend_id** | **str** | Character backend identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**character_uid** | **str** | Character immutable identifier | [optional] 
**is_default** | **bool** | Whether it is the default backend | [optional] 
**chat_prompt_task_id** | **str** | Prompt task identifier for chat | [optional] 
**greeting_prompt_task_id** | **str** | Prompt task identifier for greeting | [optional] 
**moderation_model_id** | **str** | Moderation model for the character&#39;s response | [optional] 
**moderation_api_key_name** | **str** | Api key name for moderation model | [optional] 
**moderation_params** | **str** | Parameters for moderation model | [optional] 
**message_window_size** | **int** | Max messages in the character&#39;s memory | [optional] 
**init_quota** | **int** | Initial quota when opening a chat | [optional] 
**quota_type** | **str** | Quota type: messages | tokens | none (not limited) | [optional] 
**forward_to_user** | **bool** | Whether to forward messages to the character owner | [optional] 

## Example

```python
from freechat_sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterBackendDetailsDTO from a JSON string
character_backend_details_dto_instance = CharacterBackendDetailsDTO.from_json(json)
# print the JSON string representation of the object
print CharacterBackendDetailsDTO.to_json()

# convert the object into a dict
character_backend_details_dto_dict = character_backend_details_dto_instance.to_dict()
# create an instance of CharacterBackendDetailsDTO from a dict
character_backend_details_dto_form_dict = character_backend_details_dto.from_dict(character_backend_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


