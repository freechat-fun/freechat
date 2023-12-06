# freechat-sdk.PromptAiParamDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**prompt** | **String** | Complete input content, priority: prompt &gt; promptTemplate &gt; promptRef | [optional] 
**promptTemplate** | [**PromptTemplateDTO**](PromptTemplateDTO.md) |  | [optional] 
**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) |  | [optional] 
**params** | **{String: Object}** | Model call parameters, the actual supported fields are related to modelId, depending on the model provider, specific fields can refer to: OpenAiParamDTO, QwenParamDTO | 


