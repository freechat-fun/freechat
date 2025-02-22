

# PromptTemplateDTO

Prompt template content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**template** | **String** | Prompt text template content, choose one between this and chatTemplate field, priority: template &gt; chatTemplate |  [optional] |
|**chatTemplate** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) | Prompt chat template content |  [optional] |
|**variables** | **Map&lt;String, Object&gt;** | Variables applied to the template, can be empty |  [optional] |
|**format** | **String** | Prompt format: mustache (default) | f_string |  [optional] |



