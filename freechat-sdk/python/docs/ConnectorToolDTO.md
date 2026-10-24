# ConnectorToolDTO

An untrusted tool description advertised by a remote Connector

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Remote tool name | [optional]
**description** | **str** | Untrusted remote tool description; display as text | [optional]

## Example

```python
from freechat_sdk.models.connector_tool_dto import ConnectorToolDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ConnectorToolDTO from a JSON string
connector_tool_dto_instance = ConnectorToolDTO.from_json(json)
# print the JSON string representation of the object
print(ConnectorToolDTO.to_json())

# convert the object into a dict
connector_tool_dto_dict = connector_tool_dto_instance.to_dict()
# create an instance of ConnectorToolDTO from a dict
connector_tool_dto_from_dict = ConnectorToolDTO.from_dict(connector_tool_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


