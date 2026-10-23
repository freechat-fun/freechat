

# SkillDraftDTO

Reviewed installation preview; confirmation is bound to its exact digest

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**draftId** | **String** | Owner-scoped single-use draft identifier |  [optional] |
|**skillId** | **String** | Server-assigned skill identifier |  [optional] |
|**expectedRevision** | **String** | Revision being replaced, absent for a new skill |  [optional] |
|**name** | **String** | Validated skill name |  [optional] |
|**description** | **String** | Validated skill description |  [optional] |
|**files** | **Map&lt;String, String&gt;** | Complete staged document set for confirmation |  [optional] |
|**digest** | **String** | Digest to supply when confirming this preview |  [optional] |
|**expiresAt** | **OffsetDateTime** | Confirmation expiry time |  [optional] |



