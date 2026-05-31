

# TgMessageDTO

Telegram message information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Long** | Record identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**chatId** | **String** | Related tg_chat.chat_id |  [optional] |
|**tgMessageId** | **Long** | Telegram message id |  [optional] |
|**tgUserId** | **Long** | Sender telegram user id |  [optional] |
|**direction** | **String** | Direction: in | out |  [optional] |
|**messageType** | **String** | Message type |  [optional] |
|**content** | **String** | Plain text content |  [optional] |
|**payload** | **String** | Raw telegram update payload (JSON) |  [optional] |



