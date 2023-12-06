# freechat-sdk.PluginQueryDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**Where**](Where.md) |  | [optional] 
**orderBy** | **[String]** | Ordering condition, supported sorting fields are: - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**pageNum** | **Number** | Page number, default is 0 | [optional] 
**pageSize** | **Number** | Page item count, 1～50, default is 10 | [optional] 


