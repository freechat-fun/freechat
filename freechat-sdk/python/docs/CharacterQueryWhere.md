# CharacterQueryWhere

Query condition

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**visibility** | **str** | Visibility: public, public_org (search this organization), private (default) | [optional] 
**username** | **str** | Effective when searching public, public_org characters, if not specified, search all users | [optional] 
**tags** | **List[str]** | Tags | [optional] 
**tags_op** | **str** | Relationship between tags: and | or (default) | [optional] 
**name** | **str** | Name, left match | [optional] 
**lang** | **str** | Language, exact match | [optional] 
**text** | **str** | Name, description, profile, chat style, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time. | [optional] 
**high_priority** | **bool** | Character priority, greater than 1 indicates a high priority | [optional] 

## Example

```python
from freechat_sdk.models.character_query_where import CharacterQueryWhere

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterQueryWhere from a JSON string
character_query_where_instance = CharacterQueryWhere.from_json(json)
# print the JSON string representation of the object
print(CharacterQueryWhere.to_json())

# convert the object into a dict
character_query_where_dict = character_query_where_instance.to_dict()
# create an instance of CharacterQueryWhere from a dict
character_query_where_from_dict = CharacterQueryWhere.from_dict(character_query_where_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


