# freechat-sdk.ChatMessageDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**role** | **String** | Chat role: system | assistant | user | function_call | function_result | [optional] 
**name** | **String** | user: Name of the user role; function_call: Name of the called tool | [optional] 
**content** | **String** | default: Dialogue content; function_result: function call result, serialized as json | [optional] 
**toolCall** | [**ChatToolCallDTO**](ChatToolCallDTO.md) |  | [optional] 
**gmtCreate** | **Date** | Creation time | [optional] 


