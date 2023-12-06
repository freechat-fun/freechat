# freechat-sdk.LlmResultDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestId** | **String** | Request identifier | [optional] 
**text** | **String** | Model response content, the complete content is included in non-streaming responses; only the delta content is included in streaming responses (the complete content of streaming responses is in the content of the last frame message field) | [optional] 
**message** | [**ChatMessageDTO**](ChatMessageDTO.md) |  | [optional] 
**finishReason** | **String** | Model end reason: stop | length | tool_execution | content_filter | [optional] 
**tokenUsage** | [**LlmTokenUsageDTO**](LlmTokenUsageDTO.md) |  | [optional] 


