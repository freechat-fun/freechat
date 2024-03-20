

# PromptDetailsDTO

Prompt detailed content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**promptId** | **Long** | Prompt identifier, will change after publish |  [optional] |
|**promptUid** | **String** | Prompt immutable identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**parentUid** | **String** | Referenced prompt |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Prompt name |  [optional] |
|**type** | **String** | Prompt type: string | chat |  [optional] |
|**description** | **String** | Prompt description (50 characters, the excess part is represented by ellipsis) |  [optional] |
|**format** | **String** | Prompt format: mustache (default) | f_string |  [optional] |
|**lang** | **String** | Prompt language: en (default) | zh | ... |  [optional] |
|**username** | **String** | Prompt owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**aiModels** | [**List&lt;AiModelInfoDTO&gt;**](AiModelInfoDTO.md) | Supported model set |  [optional] |
|**template** | **String** | Prompt text template content |  [optional] |
|**chatTemplate** | [**ChatPromptContentDTO**](ChatPromptContentDTO.md) |  |  [optional] |
|**example** | **String** | Prompt example |  [optional] |
|**inputs** | **String** | Prompt inputs, JSON format |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Draft content |  [optional] |



