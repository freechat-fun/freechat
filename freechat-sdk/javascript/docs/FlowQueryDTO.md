# freechat-sdk.FlowQueryDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**Where**](Where.md) |  | [optional] 
**orderBy** | **[String]** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending. If ascending is expected, specify after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**pageNum** | **Number** | Page number, default is 0 | [optional] 
**pageSize** | **Number** | Number of items per page, 1~50, default is 10 | [optional] 


