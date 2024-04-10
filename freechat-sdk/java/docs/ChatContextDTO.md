

# ChatContextDTO

Chat context

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**chatId** | **String** | Chat identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**gmtRead** | **OffsetDateTime** | Read time |  [optional] |
|**userNickname** | **String** | User nickname for this session |  [optional] |
|**userProfile** | **String** | User profile for this session |  [optional] |
|**characterNickname** | **String** | Character nickname for this session |  [optional] |
|**about** | **String** | Anything about this session |  [optional] |
|**backendId** | **String** | Character backend for this session |  |
|**apiKeyName** | **String** | API-KEY name, priority: apiKeyName &gt; apiKeyValue |  [optional] |
|**apiKeyValue** | **String** | API-KEY value |  [optional] |
|**quota** | **Long** | Quota of this chat |  [optional] |
|**quotaType** | **String** | Quota type: messages | tokens | none (not limited) |  [optional] |
|**ext** | **String** | Extra info for this session |  [optional] |



