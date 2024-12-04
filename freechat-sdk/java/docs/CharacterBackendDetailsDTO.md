

# CharacterBackendDetailsDTO

Character backend detailed information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**backendId** | **String** | Character backend identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**characterUid** | **String** | Character immutable identifier |  [optional] |
|**isDefault** | **Boolean** | Whether it is the default backend |  [optional] |
|**chatPromptTaskId** | **String** | Prompt task identifier for chat |  [optional] |
|**greetingPromptTaskId** | **String** | Prompt task identifier for greeting |  [optional] |
|**moderationModelId** | **String** | Moderation model for the character&#39;s response |  [optional] |
|**moderationApiKeyName** | **String** | Api key name for moderation model |  [optional] |
|**moderationParams** | **String** | Parameters for moderation model |  [optional] |
|**messageWindowSize** | **Integer** | Max messages in the character&#39;s memory |  [optional] |
|**longTermMemoryWindowSize** | **Integer** | Max rounds (a round includes a user message and a character reply) in the character&#39;s long term memory |  [optional] |
|**proactiveChatWaitingTime** | **Integer** | Minutes to wait for a proactive chat |  [optional] |
|**initQuota** | **Long** | Initial quota when opening a chat |  [optional] |
|**quotaType** | **String** | Quota type: messages | tokens | none (not limited) |  [optional] |
|**enableAlbumTool** | **Boolean** | Enable character album image retrieval tool |  [optional] |



