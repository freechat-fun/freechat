# freechat-sdk.QwenParamDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**apiKey** | **String** | API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified. | [optional] 
**apiKeyName** | **String** | API-KEY name | [optional] 
**modelId** | **String** | Model identifier | 
**topP** | **Number** | Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0.5. | [optional] 
**topK** | **Number** | The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective. The default value is 0. | [optional] 
**enableSearch** | **Boolean** | Whether to use a search engine for data enhancement. Default is false. | [optional] 
**seed** | **Number** | During generation, the seed of the random number, used to control the randomness of the model generation. If the same seed is used, the results of each run will be the same; when you need to reproduce the results of the model, you can use the same seed. The default value is 1234. | [optional] 


