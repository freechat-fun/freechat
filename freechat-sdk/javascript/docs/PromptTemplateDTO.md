# freechat-sdk.PromptTemplateDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**template** | **String** | Prompt text template content, choose one between this and chatTemplate field, priority: template &gt; chatTemplate | [optional] 
**chatTemplate** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) |  | [optional] 
**variables** | **{String: Object}** | Variables applied to the template, can be empty | [optional] 
**format** | **String** | Prompt format: mustache (default) | f_string | [optional] 


