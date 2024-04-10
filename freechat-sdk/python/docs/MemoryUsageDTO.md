# MemoryUsageDTO

Memory usage information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**message_usage** | **int** | Messages usage information | [optional] 
**token_usage** | [**TokenUsageDTO**](TokenUsageDTO.md) |  | [optional] 

## Example

```python
from freechat_sdk.models.memory_usage_dto import MemoryUsageDTO

# TODO update the JSON string below
json = "{}"
# create an instance of MemoryUsageDTO from a JSON string
memory_usage_dto_instance = MemoryUsageDTO.from_json(json)
# print the JSON string representation of the object
print MemoryUsageDTO.to_json()

# convert the object into a dict
memory_usage_dto_dict = memory_usage_dto_instance.to_dict()
# create an instance of MemoryUsageDTO from a dict
memory_usage_dto_form_dict = memory_usage_dto.from_dict(memory_usage_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


