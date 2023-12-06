# freechat-sdk.OpenAiParamDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**apiKey** | **String** | API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified. | [optional] 
**apiKeyName** | **String** | API-KEY name | [optional] 
**modelId** | **String** | Model identifier | 
**baseUrl** | **String** | OpenAI service provider address, default: https://api.openai.com/v1 | [optional] 
**temperature** | **Number** | Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness. | [optional] 
**topP** | **Number** | Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. | [optional] 
**maxTokens** | **Number** | The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model&#39;s context length. | [optional] 
**presencePenalty** | **Number** | Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model&#39;s likelihood to talk about new topics. | [optional] 
**frequencyPenalty** | **Number** | Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model&#39;s likelihood to repeat the same line verbatim. | [optional] 
**seed** | **Number** | If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result. | [optional] 
**stop** | **[String]** | Up to 4 sequences where the API will stop generating further tokens. | [optional] 


