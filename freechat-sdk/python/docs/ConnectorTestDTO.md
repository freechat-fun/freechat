# ConnectorTestDTO

Connector discovery result; no remote tools have been executed

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**tools** | [**List[ConnectorToolDTO]**](ConnectorToolDTO.md) | Bounded catalog of remote tools | [optional]

## Example

```python
from freechat_sdk.models.connector_test_dto import ConnectorTestDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ConnectorTestDTO from a JSON string
connector_test_dto_instance = ConnectorTestDTO.from_json(json)
# print the JSON string representation of the object
print(ConnectorTestDTO.to_json())

# convert the object into a dict
connector_test_dto_dict = connector_test_dto_instance.to_dict()
# create an instance of ConnectorTestDTO from a dict
connector_test_dto_from_dict = ConnectorTestDTO.from_dict(connector_test_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


