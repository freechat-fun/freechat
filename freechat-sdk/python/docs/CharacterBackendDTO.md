# CharacterBackendDTO

Character backend information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**is_default** | **bool** | Whether it is the default backend | [optional] 
**chat_prompt_task_id** | **str** | Prompt task identifier for chat | [optional] 
**greeting_prompt_task_id** | **str** | Prompt task identifier for greeting | [optional] 
**moderation_model_id** | **str** | Moderation model for the character&#39;s response | [optional] 
**moderation_api_key_name** | **str** | Api key name for moderation model | [optional] 
**moderation_params** | **str** | Parameters for moderation model | [optional] 
**message_window_size** | **int** | Max messages in the character&#39;s memory | [optional] 
**long_term_memory_window_size** | **int** | Max rounds (a round includes a user message and a character reply) in the character&#39;s long term memory, 0 to disable | [optional] 
**proactive_chat_waiting_time** | **int** | Minutes to wait for a proactive chat | [optional] 
**init_quota** | **int** | Initial quota when opening a chat | [optional] 
**quota_type** | **str** | Quota type: messages | tokens | none (not limited) | [optional] 
**enable_album_tool** | **bool** | Enable character album image retrieval tool | [optional] 
**enable_tts** | **bool** | Enable character tts feature | [optional] 
**tts_speaker_idx** | **str** | Character&#39;s speaker idx for tts | [optional] 
**tts_speaker_wav** | **str** | Character&#39;s speaker sample for tts | [optional] 
**tts_speaker_type** | **str** | Character&#39;s speaker type for tts: idx | wav | [optional] 

## Example

```python
from freechat_sdk.models.character_backend_dto import CharacterBackendDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterBackendDTO from a JSON string
character_backend_dto_instance = CharacterBackendDTO.from_json(json)
# print the JSON string representation of the object
print(CharacterBackendDTO.to_json())

# convert the object into a dict
character_backend_dto_dict = character_backend_dto_instance.to_dict()
# create an instance of CharacterBackendDTO from a dict
character_backend_dto_from_dict = CharacterBackendDTO.from_dict(character_backend_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


