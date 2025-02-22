

# ChatMessageDTO

Chat message

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**role** | **String** | Chat role: system | assistant | user | tool_call | tool_result |  [optional] |
|**name** | **String** | user: Name of the user role; tool_call: Name of the called tool |  [optional] |
|**contents** | [**List&lt;ChatContentDTO&gt;**](ChatContentDTO.md) | default: Dialogue content; tool_result: tool call result, serialized as json |  [optional] |
|**toolCalls** | [**List&lt;ChatToolCallDTO&gt;**](ChatToolCallDTO.md) | Tool calls information during the conversation |  [optional] |
|**context** | **String** | Contextual information in this round of conversation (the external RAG result can be passed in through this parameter) |  [optional] |
|**messageId** | **Long** | Message identifier |  [optional] |



