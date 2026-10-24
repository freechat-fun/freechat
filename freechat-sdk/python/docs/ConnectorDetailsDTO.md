# ConnectorDetailsDTO

Private connector metadata. Header names only; secret values are never returned.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**connector_id** | **str** |  | [optional]
**revision** | **str** |  | [optional]
**name** | **str** |  | [optional]
**transport** | **str** |  | [optional]
**url** | **str** |  | [optional]
**timeout** | **int** |  | [optional]
**active** | **bool** |  | [optional]
**header_names** | **List[str]** |  | [optional]
**modified** | **datetime** |  | [optional]

## Example

```python
from freechat_sdk.models.connector_details_dto import ConnectorDetailsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ConnectorDetailsDTO from a JSON string
connector_details_dto_instance = ConnectorDetailsDTO.from_json(json)
# print the JSON string representation of the object
print(ConnectorDetailsDTO.to_json())

# convert the object into a dict
connector_details_dto_dict = connector_details_dto_instance.to_dict()
# create an instance of ConnectorDetailsDTO from a dict
connector_details_dto_from_dict = ConnectorDetailsDTO.from_dict(connector_details_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


