# QwenParamDTO

Qwen series model parameters

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**api_key** | **str** | API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified. | [optional] 
**api_key_name** | **str** | API-KEY name | [optional] 
**model_id** | **str** | Model identifier | 
**top_p** | **float** | Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0.5. | [optional] 
**top_k** | **int** | The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective. The default value is 0. | [optional] 
**enable_search** | **bool** | Whether to use a search engine for data enhancement. Default is false. | [optional] 
**seed** | **int** | During generation, the seed of the random number, used to control the randomness of the model generation. If the same seed is used, the results of each run will be the same; when you need to reproduce the results of the model, you can use the same seed. The default value is 1234. | [optional] 

## Example

```python
from freechat-sdk.models.qwen_param_dto import QwenParamDTO

# TODO update the JSON string below
json = "{}"
# create an instance of QwenParamDTO from a JSON string
qwen_param_dto_instance = QwenParamDTO.from_json(json)
# print the JSON string representation of the object
print QwenParamDTO.to_json()

# convert the object into a dict
qwen_param_dto_dict = qwen_param_dto_instance.to_dict()
# create an instance of QwenParamDTO from a dict
qwen_param_dto_form_dict = qwen_param_dto.from_dict(qwen_param_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


