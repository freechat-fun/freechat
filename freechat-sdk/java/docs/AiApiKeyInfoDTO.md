

# AiApiKeyInfoDTO

Model credential information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**id** | **Long** | Credential identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**gmtUsed** | **OffsetDateTime** | Last use time |  [optional] |
|**name** | **String** | Credential name |  [optional] |
|**provider** | **String** | Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown |  [optional] |
|**token** | **String** | Credential content |  [optional] |
|**enabled** | **Boolean** | Whether to enable |  [optional] |
|**username** | **String** | Credential owner |  [optional] |



