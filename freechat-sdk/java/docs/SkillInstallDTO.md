

# SkillInstallDTO

Install a prompt-only skill from either text documents or an operator-approved HTTPS source

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**files** | **Map&lt;String, String&gt;** | Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl |  [optional] |
|**sourceUrl** | **String** | Approved HTTPS URL of a raw SKILL.md document; no recursive fetching or redirects |  [optional] |



