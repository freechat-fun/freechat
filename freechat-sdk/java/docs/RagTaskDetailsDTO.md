

# RagTaskDetailsDTO

Prompt task detailed information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**id** | **Long** | RAG task identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**gmtStart** | **OffsetDateTime** | Task start execution time |  [optional] |
|**gmtEnd** | **OffsetDateTime** | Task end execution time |  [optional] |
|**characterUid** | **String** | Character identifier |  [optional] |
|**sourceType** | **String** | Source type: file (default) | url |  [optional] |
|**source** | **String** | Source information, url, or a key for file |  [optional] |
|**status** | **String** | Task execution status: pending | running | succeeded | failed | canceled |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |



