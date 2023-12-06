# SseEmitter


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**timeout** | **int** |  | [optional] 

## Example

```python
from freechat-sdk.models.sse_emitter import SseEmitter

# TODO update the JSON string below
json = "{}"
# create an instance of SseEmitter from a JSON string
sse_emitter_instance = SseEmitter.from_json(json)
# print the JSON string representation of the object
print SseEmitter.to_json()

# convert the object into a dict
sse_emitter_dict = sse_emitter_instance.to_dict()
# create an instance of SseEmitter from a dict
sse_emitter_form_dict = sse_emitter.from_dict(sse_emitter_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


