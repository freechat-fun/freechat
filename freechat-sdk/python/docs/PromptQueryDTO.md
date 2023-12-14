# PromptQueryDTO

Prompt template query request

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**PromptQueryWhere**](PromptQueryWhere.md) |  | [optional] 
**order_by** | **List[str]** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**page_num** | **int** | Page number, default is 0 | [optional] 
**page_size** | **int** | Number of items per page, 1～50, default is 10 | [optional] 

## Example

```python
from freechat-sdk.models.prompt_query_dto import PromptQueryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptQueryDTO from a JSON string
prompt_query_dto_instance = PromptQueryDTO.from_json(json)
# print the JSON string representation of the object
print PromptQueryDTO.to_json()

# convert the object into a dict
prompt_query_dto_dict = prompt_query_dto_instance.to_dict()
# create an instance of PromptQueryDTO from a dict
prompt_query_dto_form_dict = prompt_query_dto.from_dict(prompt_query_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


