# PromptAiParamDTO

Prompt AI service information

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt** | **str** | Complete input content, priority: prompt &gt; promptTemplate &gt; promptRef | [optional] 
**prompt_template** | [**PromptTemplateDTO**](PromptTemplateDTO.md) |  | [optional] 
**prompt_ref** | [**PromptRefDTO**](PromptRefDTO.md) |  | [optional] 
**params** | **Dict[str, object]** | Model call parameters, the actual supported fields are related to modelId, depending on the model provider, specific fields can refer to: OpenAiParamDTO, QwenParamDTO | 

## Example

```python
from freechat-sdk.models.prompt_ai_param_dto import PromptAiParamDTO

# TODO update the JSON string below
json = "{}"
# create an instance of PromptAiParamDTO from a JSON string
prompt_ai_param_dto_instance = PromptAiParamDTO.from_json(json)
# print the JSON string representation of the object
print PromptAiParamDTO.to_json()

# convert the object into a dict
prompt_ai_param_dto_dict = prompt_ai_param_dto_instance.to_dict()
# create an instance of PromptAiParamDTO from a dict
prompt_ai_param_dto_form_dict = prompt_ai_param_dto.from_dict(prompt_ai_param_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


