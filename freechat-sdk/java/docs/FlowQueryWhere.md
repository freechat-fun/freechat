

# FlowQueryWhere

Query condition

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**visibility** | **String** | Visibility: public, public_org (search this organization), private (default) |  [optional] |
|**username** | **String** | Effective when searching public, public_org prompts, if not specified, search all users |  [optional] |
|**format** | **String** | Flow configuration format, currently supported: langflow. |  [optional] |
|**tags** | **List&lt;String&gt;** | Tags |  [optional] |
|**tagsOp** | **String** | Relationship between tags: and | or (default) |  [optional] |
|**aiModels** | **List&lt;String&gt;** | Model set |  [optional] |
|**aiModelsOp** | **String** | Relationship between model sets: and | or (default) |  [optional] |
|**name** | **String** | Name, left match |  [optional] |
|**text** | **String** | Name, description, example, fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time. |  [optional] |



