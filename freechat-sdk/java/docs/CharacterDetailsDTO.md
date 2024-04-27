

# CharacterDetailsDTO

Character detailed content

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**requestId** | **String** | Request identifier |  [optional] |
|**characterId** | **Long** | Character identifier, will change after publish |  [optional] |
|**characterUid** | **String** | Character immutable identifier |  [optional] |
|**gmtCreate** | **OffsetDateTime** | Creation time |  [optional] |
|**gmtModified** | **OffsetDateTime** | Modification time |  [optional] |
|**parentUid** | **String** | Referenced character |  [optional] |
|**visibility** | **String** | Visibility: private, public, public_org, hidden |  [optional] |
|**version** | **Integer** | Version |  [optional] |
|**name** | **String** | Character name |  [optional] |
|**description** | **String** | Character description |  [optional] |
|**nickname** | **String** | Character nickname |  [optional] |
|**avatar** | **String** | Character avatar url |  [optional] |
|**picture** | **String** | Character picture url |  [optional] |
|**gender** | **String** | Character gender: male | female | other |  [optional] |
|**lang** | **String** | Character language: en (default) | zh | ... |  [optional] |
|**greeting** | **String** | Character greeting |  [optional] |
|**defaultScene** | **String** | Default scene, which will be set as the default conversation background information when creating a new chat |  [optional] |
|**username** | **String** | Character owner |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |
|**profile** | **String** | Character profile |  [optional] |
|**chatStyle** | **String** | Character chat-style |  [optional] |
|**chatExample** | **String** | Character chat-example |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Character draft information |  [optional] |
|**backends** | [**List&lt;CharacterBackendDetailsDTO&gt;**](CharacterBackendDetailsDTO.md) | Character backends information |  [optional] |



