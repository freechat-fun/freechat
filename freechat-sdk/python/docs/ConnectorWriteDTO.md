# ConnectorWriteDTO

Private remote MCP configuration. Saves validate syntax only; no network requests are made.

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** | Printable display name, 1–64 characters | [optional]
**transport** | **str** | Remote HTTP or SSE only | [optional]
**url** | **str** | HTTP(S) base URL, at most 2048 characters; no credentials, query, or fragment | [optional]
**timeout** | **int** | Timeout in milliseconds, 1000–120000 | [optional] [default to 60000]
**headers** | **Dict[str, str]** | New header values, HTTPS only. At most 16 headers and 16 KiB total. Values are never returned. | [optional]
**keep_header_names** | **List[str]** | Stored header names to retain on update; cannot overlap new headers or move to another origin | [optional]
**expected_revision** | **str** | Current revision required on update; omit on create | [optional]
**active** | **bool** |  | [optional] [default to True]

## Example

```python
from freechat_sdk.models.connector_write_dto import ConnectorWriteDTO

# TODO update the JSON string below
json = "{}"
# create an instance of ConnectorWriteDTO from a JSON string
connector_write_dto_instance = ConnectorWriteDTO.from_json(json)
# print the JSON string representation of the object
print(ConnectorWriteDTO.to_json())

# convert the object into a dict
connector_write_dto_dict = connector_write_dto_instance.to_dict()
# create an instance of ConnectorWriteDTO from a dict
connector_write_dto_from_dict = ConnectorWriteDTO.from_dict(connector_write_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


