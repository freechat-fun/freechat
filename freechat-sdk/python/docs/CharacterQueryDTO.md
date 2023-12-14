# CharacterQueryDTO

Character query request

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**CharacterQueryWhere**](CharacterQueryWhere.md) |  | [optional] 
**order_by** | **List[str]** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**page_num** | **int** | Page number, default is 0 | [optional] 
**page_size** | **int** | Number of items per page, 1～50, default is 10 | [optional] 

## Example

```python
from freechat-sdk.models.character_query_dto import CharacterQueryDTO

# TODO update the JSON string below
json = "{}"
# create an instance of CharacterQueryDTO from a JSON string
character_query_dto_instance = CharacterQueryDTO.from_json(json)
# print the JSON string representation of the object
print CharacterQueryDTO.to_json()

# convert the object into a dict
character_query_dto_dict = character_query_dto_instance.to_dict()
# create an instance of CharacterQueryDTO from a dict
character_query_dto_form_dict = character_query_dto.from_dict(character_query_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


