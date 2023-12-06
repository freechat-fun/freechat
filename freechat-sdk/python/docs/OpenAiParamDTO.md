# OpenAiParamDTO

OpenAI series model parameters

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**api_key** | **str** | API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified. | [optional] 
**api_key_name** | **str** | API-KEY name | [optional] 
**model_id** | **str** | Model identifier | 
**base_url** | **str** | OpenAI service provider address, default: https://api.openai.com/v1 | [optional] 
**temperature** | **float** | Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness. | [optional] 
**top_p** | **float** | Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. | [optional] 
**max_tokens** | **int** | The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model&#39;s context length. | [optional] 
**presence_penalty** | **float** | Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model&#39;s likelihood to talk about new topics. | [optional] 
**frequency_penalty** | **float** | Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model&#39;s likelihood to repeat the same line verbatim. | [optional] 
**seed** | **int** | If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result. | [optional] 
**stop** | **List[str]** | Up to 4 sequences where the API will stop generating further tokens. | [optional] 

## Example

```python
from freechat-sdk.models.open_ai_param_dto import OpenAiParamDTO

# TODO update the JSON string below
json = "{}"
# create an instance of OpenAiParamDTO from a JSON string
open_ai_param_dto_instance = OpenAiParamDTO.from_json(json)
# print the JSON string representation of the object
print OpenAiParamDTO.to_json()

# convert the object into a dict
open_ai_param_dto_dict = open_ai_param_dto_instance.to_dict()
# create an instance of OpenAiParamDTO from a dict
open_ai_param_dto_form_dict = open_ai_param_dto.from_dict(open_ai_param_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


