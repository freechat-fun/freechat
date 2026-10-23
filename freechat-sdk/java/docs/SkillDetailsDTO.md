

# SkillDetailsDTO

Owner-only contents of an installed prompt skill

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**skillId** | **String** | Opaque skill identifier |  [optional] |
|**revision** | **String** | Current immutable revision identifier |  [optional] |
|**name** | **String** | Skill name |  [optional] |
|**description** | **String** | Skill description |  [optional] |
|**tags** | **Set&lt;String&gt;** | Descriptive labels, not permission grants |  [optional] |
|**modified** | **OffsetDateTime** | Last publication time |  [optional] |
|**files** | **Map&lt;String, String&gt;** | Logical document names and exact UTF-8 text; never server filesystem paths |  [optional] |
|**digest** | **String** | SHA-256 digest of the complete validated bundle |  [optional] |



