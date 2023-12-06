

# CharacterDetailsDTO

Character detailed content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**characterId** | **String** | Character identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Character name |  |
|**description** | **String** | Character description |  [optional] |
|**avatar** | **String** | Character avatar url |  [optional] |
|**picture** | **String** | Character picture url |  [optional] |
|**lang** | **String** | Character language: English | Chinese (Simplified) | ... |  [optional] |
|**username** | **String** | Character owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**profile** | **String** | Character profile |  [optional] |
|**greeting** | **String** | Character greeting |  [optional] |
|**chatStyle** | **String** | Character chat-style |  [optional] |
|**chatExample** | **String** | Character chat-example |  [optional] |
|**experience** | **String** | Character experience |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | [**CharacterInfoDraftDTO**](CharacterInfoDraftDTO.md) |  |  [optional] |
|**backends** | [**List&lt;CharacterBackendDetailsDTO&gt;**](CharacterBackendDetailsDTO.md) | Character backends information |  [optional] |



