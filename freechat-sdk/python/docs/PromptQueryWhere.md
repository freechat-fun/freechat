# PromptQueryWhere

Query condition

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **str** | Visibility: public, public_org (search this organization), private (default) | [optional] 
**username** | **str** | Effective when searching public, public_org prompts, if not specified, search all users | [optional] 
**tags** | **List[str]** | Tags | [optional] 
**tags_op** | **str** | Relationship between tags: and | or (default) | [optional] 
**ai_models** | **List[str]** | Model set | [optional] 
**ai_models_op** | **str** | Relationship between model sets: and | or (default) | [optional] 
**name** | **str** | Name, left match | [optional] 
**type** | **str** | Type, exact match: string (default) | chat | [optional] 
**lang** | **str** | Language, exact match | [optional] 
**text** | **str** | Name, description, template, example, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time. | [optional] 

## Example

```python
from freechat_sdk.models.prompt_query_where import PromptQueryWhere

# TODO update the JSON string below
json = "{}"
# create an instance of PromptQueryWhere from a JSON string
prompt_query_where_instance = PromptQueryWhere.from_json(json)
# print the JSON string representation of the object
print(PromptQueryWhere.to_json())

# convert the object into a dict
prompt_query_where_dict = prompt_query_where_instance.to_dict()
# create an instance of PromptQueryWhere from a dict
prompt_query_where_from_dict = PromptQueryWhere.from_dict(prompt_query_where_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


