

# CharacterUpdateDTO

Request data for updating character information

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
|**ext** | **String** | Additional JSON information. tools.enabled is an exact tool-name allowlist; missing or null inherits roleplay, memory and image defaults, subject to prerequisites. An empty tools list disables all tools. skills.enabled is a list of the creator&#39;s installed skill IDs; missing, null or empty enables no skills. With skills enabled, default tools include skill and read_skill_resource, and only approved in-process roleplay, memory and skill-reading tools are available. Skill references are omitted from character exports and cleared on import or cloning. |  [optional] |
|**draft** | **String** | Character draft information |  [optional] |
|**tags** | **List&lt;String&gt;** | Tag set |  [optional] |



