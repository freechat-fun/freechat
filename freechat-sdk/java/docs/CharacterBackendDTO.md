

# CharacterBackendDTO

Character backend information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**isDefault** | **Boolean** | Whether it is the default backend |  [optional] |
|**chatPromptTaskId** | **String** | Prompt task identifier for chat |  [optional] |
|**greetingPromptTaskId** | **String** | Prompt task identifier for greeting |  [optional] |
|**moderationModelId** | **String** | Moderation model for the character&#39;s response |  [optional] |
|**moderationApiKeyName** | **String** | Api key name for moderation model |  [optional] |
|**moderationParams** | **String** | Parameters for moderation model |  [optional] |
|**messageWindowSize** | **Integer** | Max messages in the character&#39;s memory |  [optional] |
|**longTermMemoryWindowSize** | **Integer** | Max rounds (a round includes a user message and a character reply) in the character&#39;s long term memory, 0 to disable |  [optional] |
|**proactiveChatWaitingTime** | **Integer** | Minutes to wait for a proactive chat |  [optional] |
|**initQuota** | **Long** | Initial quota when opening a chat |  [optional] |
|**quotaType** | **String** | Quota type: messages | tokens | none (not limited) |  [optional] |
|**enableAlbumTool** | **Boolean** | Enable character album image retrieval tool |  [optional] |
|**enableTts** | **Boolean** | Enable character tts feature |  [optional] |
|**ttsSpeakerIdx** | **String** | Character&#39;s speaker idx for tts |  [optional] |
|**ttsSpeakerWav** | **String** | Character&#39;s speaker sample for tts |  [optional] |
|**ttsSpeakerType** | **String** | Character&#39;s speaker type for tts: idx | wav |  [optional] |



