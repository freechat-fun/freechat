# HotTagDTO

Hot tag

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**content** | **str** | Tag content | [optional] 
**count** | **int** | Tag count | [optional] 

## Example

```python
from freechat_sdk.models.hot_tag_dto import HotTagDTO

# TODO update the JSON string below
json = "{}"
# create an instance of HotTagDTO from a JSON string
hot_tag_dto_instance = HotTagDTO.from_json(json)
# print the JSON string representation of the object
print(HotTagDTO.to_json())

# convert the object into a dict
hot_tag_dto_dict = hot_tag_dto_instance.to_dict()
# create an instance of HotTagDTO from a dict
hot_tag_dto_from_dict = HotTagDTO.from_dict(hot_tag_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


