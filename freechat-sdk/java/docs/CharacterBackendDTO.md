

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
|**initQuota** | **Long** | Initial quota when opening a chat |  [optional] |
|**quotaType** | **String** | Quota type: messages | tokens | none (not limited) |  [optional] |
|**forwardToUser** | **Boolean** | Weather to forward messages to the character owner |  [optional] |



