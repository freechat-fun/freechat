

# AgentQueryDTO

Agent information query request

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**where** | [**AgentQueryWhere**](AgentQueryWhere.md) | Query condition |  [optional] |
|**orderBy** | **List&lt;String&gt;** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending. If ascending is expected, specify after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  |  [optional] |
|**pageNum** | **Long** | Page number, default is 0 |  [optional] |
|**pageSize** | **Long** | Number of items per page, 1~50, default is 10 |  [optional] |



