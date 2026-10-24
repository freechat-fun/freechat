

# SkillInstallDTO

Install a prompt-only skill bundle from text documents or an LLM-analyzed approved source link

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**files** | **Map&lt;String, String&gt;** | Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl |  [optional] |
|**sourceUrl** | **String** | GitHub repository/skill-directory URL, ClawHub skill-page URL, or raw document URL from an allowed host. May include surrounding text or omit the HTTPS scheme; the LLM selects only independently validated source candidates. Provider bundles are pinned and fully validated, not recursively crawled. |  [optional] |



