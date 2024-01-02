

# ChatMessageDTO

Chat message

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**role** | **String** | Chat role: system | assistant | user | function_call | function_result |  [optional] |
|**name** | **String** | user: Name of the user role; function_call: Name of the called tool |  [optional] |
|**content** | **String** | default: Dialogue content; function_result: function call result, serialized as json |  [optional] |
|**toolCalls** | [**List&lt;ChatToolCallDTO&gt;**](ChatToolCallDTO.md) | Tool calls information during the conversation |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |



