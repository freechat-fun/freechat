# TokenUsageDTO

Token usage information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**input_token_count** | **int** | Input token count | [optional] 
**output_token_count** | **int** | Output token count | [optional] 
**total_token_count** | **int** | Total token count | [optional] 

## Example

```python
from freechat_sdk.models.token_usage_dto import TokenUsageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of TokenUsageDTO from a JSON string
token_usage_dto_instance = TokenUsageDTO.from_json(json)
# print the JSON string representation of the object
print TokenUsageDTO.to_json()

# convert the object into a dict
token_usage_dto_dict = token_usage_dto_instance.to_dict()
# create an instance of TokenUsageDTO from a dict
token_usage_dto_form_dict = token_usage_dto.from_dict(token_usage_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


