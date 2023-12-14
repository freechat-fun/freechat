

# PluginQueryDTO

Plugin information query request

## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**where** | [**PluginQueryWhere**](PluginQueryWhere.md) |  |  [optional] |
|**orderBy** | **List&lt;String&gt;** | Ordering condition, supported sorting fields are: - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  |  [optional] |
|**pageNum** | **Long** | Page number, default is 0 |  [optional] |
|**pageSize** | **Long** | Page item count, 1～50, default is 10 |  [optional] |



