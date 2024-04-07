# RagTaskDetailsDTO

Prompt task detailed information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**id** | **int** | RAG task identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**gmt_start** | **datetime** | Task start execution time | [optional] 
**gmt_end** | **datetime** | Task end execution time | [optional] 
**character_uid** | **str** | Character identifier | [optional] 
**source_type** | **str** | Source type: file (default) | url | [optional] 
**source** | **str** | Source information, url, or a key for file | [optional] 
**status** | **str** | Task execution status: pending | running | succeeded | failed | canceled | [optional] 
**ext** | **str** | Additional information, JSON format | [optional] 

## Example

```python
from freechat_sdk.models.rag_task_details_dto import RagTaskDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of RagTaskDetailsDTO from a JSON string
rag_task_details_dto_instance = RagTaskDetailsDTO.from_json(json)
# print the JSON string representation of the object
print RagTaskDetailsDTO.to_json()

# convert the object into a dict
rag_task_details_dto_dict = rag_task_details_dto_instance.to_dict()
# create an instance of RagTaskDetailsDTO from a dict
rag_task_details_dto_form_dict = rag_task_details_dto.from_dict(rag_task_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


