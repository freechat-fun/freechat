# LlmResultDTO

Prompt service result

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**text** | **str** | Model response content, the complete content is included in non-streaming responses; only the delta content is included in streaming responses (the complete content of streaming responses is in the content of the last frame message field) | [optional] 
**message** | [**ChatMessageDTO**](ChatMessageDTO.md) |  | [optional] 
**finish_reason** | **str** | Model end reason: stop | length | tool_execution | content_filter | [optional] 
**token_usage** | [**LlmTokenUsageDTO**](LlmTokenUsageDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.llm_result_dto import LlmResultDTO

# TODO update the JSON string below
json = "{}"
# create an instance of LlmResultDTO from a JSON string
llm_result_dto_instance = LlmResultDTO.from_json(json)
# print the JSON string representation of the object
print LlmResultDTO.to_json()

# convert the object into a dict
llm_result_dto_dict = llm_result_dto_instance.to_dict()
# create an instance of LlmResultDTO from a dict
llm_result_dto_form_dict = llm_result_dto.from_dict(llm_result_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


