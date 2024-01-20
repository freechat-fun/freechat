# LlmTokenUsageDTO

Token usage information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**input_token_count** | **int** | Input token count | [optional] 
**output_token_count** | **int** | Output token count | [optional] 
**total_token_count** | **int** | Total token count | [optional] 

## Example

```python
from freechat_sdk.models.llm_token_usage_dto import LlmTokenUsageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of LlmTokenUsageDTO from a JSON string
llm_token_usage_dto_instance = LlmTokenUsageDTO.from_json(json)
# print the JSON string representation of the object
print LlmTokenUsageDTO.to_json()

# convert the object into a dict
llm_token_usage_dto_dict = llm_token_usage_dto_instance.to_dict()
# create an instance of LlmTokenUsageDTO from a dict
llm_token_usage_dto_form_dict = llm_token_usage_dto.from_dict(llm_token_usage_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


