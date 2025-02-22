

# ChatSessionDTO

Chat session

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**context** | [**ChatContextDTO**](ChatContextDTO.md) | Chat context |  [optional] |
|**character** | [**CharacterSummaryDTO**](CharacterSummaryDTO.md) | Character summary info |  [optional] |
|**provider** | **String** | Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown |  [optional] |
|**latestMessageRecord** | [**ChatMessageRecordDTO**](ChatMessageRecordDTO.md) | Latest message record |  [optional] |
|**proactiveChatWaitingTime** | **Integer** | Minutes to wait for a proactive chat |  [optional] |
|**senderStatus** | **String** | Sender status: online | offline | invisible |  [optional] |
|**isDebugEnabled** | **Boolean** | Is it possible to debug |  [optional] |
|**isCustomizedApiKeyEnabled** | **Boolean** | Is it possible to customize api-key |  [optional] |
|**isTtsEnabled** | **Boolean** | Enable character tts feature or not |  [optional] |



