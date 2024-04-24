# RagTaskDTO

RAG task information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**source_type** | **str** | Source type: file (default) | url | [optional] 
**source** | **str** | Source information, url, or a key for file | [optional] 
**max_segment_size** | **int** | The maximum size of a segment in tokens. | [optional] 
**max_overlap_size** | **int** | The maximum size of the overlap between segments in tokens. | [optional] 

## Example

```python
from freechat_sdk.models.rag_task_dto import RagTaskDTO

# TODO update the JSON string below
json = "{}"
# create an instance of RagTaskDTO from a JSON string
rag_task_dto_instance = RagTaskDTO.from_json(json)
# print the JSON string representation of the object
print(RagTaskDTO.to_json())

# convert the object into a dict
rag_task_dto_dict = rag_task_dto_instance.to_dict()
# create an instance of RagTaskDTO from a dict
rag_task_dto_from_dict = RagTaskDTO.from_dict(rag_task_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


