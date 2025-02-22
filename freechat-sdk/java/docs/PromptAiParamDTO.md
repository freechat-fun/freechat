

# PromptAiParamDTO

Prompt AI service information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**prompt** | **String** | Complete input content, priority: prompt &gt; promptTemplate &gt; promptRef |  [optional] |
|**promptTemplate** | [**PromptTemplateDTO**](PromptTemplateDTO.md) | Prompt template content |  [optional] |
|**promptRef** | [**PromptRefDTO**](PromptRefDTO.md) | Prompt reference information |  [optional] |
|**params** | **Map&lt;String, Object&gt;** | Model call parameters, the actual supported fields are related to modelId, depending on the model provider, specific fields can refer to: OpenAiParamDTO, QwenParamDTO |  |



