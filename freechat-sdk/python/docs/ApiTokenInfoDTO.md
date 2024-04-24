# ApiTokenInfoDTO

API token information

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**request_id** | **str** | Request identifier | [optional] 
**id** | **int** | Token identifier | [optional] 
**gmt_create** | **datetime** | Creation time | [optional] 
**gmt_modified** | **datetime** | Modification time | [optional] 
**type** | **str** | Token type | [optional] 
**issued_at** | **datetime** | Token issuance time | [optional] 
**expires_at** | **datetime** | Token expiration time | [optional] 
**token** | **str** | Token content | [optional] 
**policy** | **str** | Token policy | [optional] 
**username** | **str** | Token owner | [optional] 

## Example

```python
from freechat_sdk.models.api_token_info_dto import ApiTokenInfoDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ApiTokenInfoDTO from a JSON string
api_token_info_dto_instance = ApiTokenInfoDTO.from_json(json)
# print the JSON string representation of the object
print(ApiTokenInfoDTO.to_json())

# convert the object into a dict
api_token_info_dto_dict = api_token_info_dto_instance.to_dict()
# create an instance of ApiTokenInfoDTO from a dict
api_token_info_dto_from_dict = ApiTokenInfoDTO.from_dict(api_token_info_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


