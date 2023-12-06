# freechat-sdk.CharacterQueryDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**where** | [**Where**](Where.md) |  | [optional] 
**orderBy** | **[String]** | Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\&quot;score\\\&quot;, \\\&quot;scoreCount asc\\\&quot;] (scoreCount in ascending order)  | [optional] 
**pageNum** | **Number** | Page number, default is 0 | [optional] 
**pageSize** | **Number** | Number of items per page, 1～50, default is 10 | [optional] 


