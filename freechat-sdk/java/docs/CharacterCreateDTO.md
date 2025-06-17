

# CharacterCreateDTO

Request data for creating new character information

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**parentUid** | **String** | Referenced character |  [optional] |
|**visibility** | **String** | Visibility: private (default), public, public_org, hidden |  [optional] |
|**name** | **String** | Character name |  |
|**description** | **String** | Character description |  [optional] |
|**nickname** | **String** | Character nickname |  [optional] |
|**avatar** | **String** | Character avatar url |  [optional] |
|**picture** | **String** | Character picture url |  [optional] |
|**video** | **String** | Character video url |  [optional] |
|**gender** | **String** | Character gender: male | female | other |  [optional] |
|**profile** | **String** | Character profile |  [optional] |
|**greeting** | **String** | Character greeting |  [optional] |
|**chatStyle** | **String** | Character chat-style |  [optional] |
|**chatExample** | **String** | Character chat-example |  [optional] |
|**defaultScene** | **String** | Default scene, which will be set as the default conversation background information when creating a new chat |  [optional] |
|**lang** | **String** | Character language: en (default) | zh | ... |  [optional] |
|**ext** | **String** | Additional information, JSON format |  [optional] |
|**draft** | **String** | Character draft information |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |



