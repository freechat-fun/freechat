

# PromptUpdateDTO

Request data for updating prompt information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**parentUid** | **String** | Referenced prompt |  [optional] |
|**visibility** | **String** | Visibility: private (default), public, public_org, hidden |  [optional] |
|**name** | **String** | Prompt name |  |
|**description** | **String** | Prompt description |  [optional] |
|**template** | **String** | Prompt text template content, choose one from template and chatTemplate field, priority: template &gt; chatTemplate |  [optional] |
|**chatTemplate** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) | Prompt chat template content |  [optional] |
|**format** | **String** | Prompt format: mustache (default) | f_string |  [optional] |
|**lang** | **String** | Prompt language: en (default) | zh_CN | ... |  [optional] |
|**example** | **String** | Prompt example |  [optional] |
|**inputs** | **String** | Prompt parameters, JSON format |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Draft content |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | **List&lt;String&gt;** | Supported model set, empty means no model limit |  [optional] |



