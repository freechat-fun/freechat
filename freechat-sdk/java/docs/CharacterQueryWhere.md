

# CharacterQueryWhere

Query condition

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**visibility** | **String** | Visibility: public, public_org (search this organization), private (default) |  [optional] |
|**username** | **String** | Effective when searching public, public_org characters, if not specified, search all users |  [optional] |
|**tags** | **List&lt;String&gt;** | Tags |  [optional] |
|**tagsOp** | **String** | Relationship between tags: and | or (default) |  [optional] |
|**name** | **String** | Name, left match |  [optional] |
|**lang** | **String** | Language, exact match |  [optional] |
|**text** | **String** | Name, description, profile, chat style, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time. |  [optional] |
|**highPriority** | **Boolean** | Character priority, greater than 1 indicates a high priority |  [optional] |



