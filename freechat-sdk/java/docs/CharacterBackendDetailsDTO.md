

# CharacterBackendDetailsDTO

Character backend detailed information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**backendId** | **String** | Character backend identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**characterId** | **String** | Character identifier |  [optional] |
|**isDefault** | **Boolean** | Whether it is the default backend |  [optional] |
|**chatPromptTaskId** | **String** | Prompt task identifier for chat |  [optional] |
|**greetingPromptTaskId** | **String** | Prompt task identifier for greeting |  [optional] |
|**moderationModelId** | **String** | Moderation model for the character&#39;s response |  [optional] |
|**moderationApiKeyName** | **String** | Api key name for moderation model |  [optional] |
|**moderationParams** | **String** | Parameters for moderation model |  [optional] |
|**messageWindowSize** | **Integer** | Max messages in the character&#39;s memory |  [optional] |
|**forwardToUser** | **Boolean** | Whether to forward messages to the character owner |  [optional] |



