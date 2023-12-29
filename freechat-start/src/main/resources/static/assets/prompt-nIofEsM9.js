const e="模版",t="示例",n={"Do you really want to delete this prompt?":"你真的想删除这个 prompt 吗？",Template:e,Example:t,"Set API Key":"设置 API Key","Select a key":"选择外部凭证","or you can use a temporary key":"或者你可以使用一个临时凭证","Model Parameters":"模型参数","Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.":"生成过程中核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。","The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective.":"生成时，采样候选集的大小。例如，取值为50时，仅将单次生成中得分最高的50个token组成随机采样的候选集。取值越大，生成的随机性越高；取值越小，生成的确定性越高。默认不传递该参数，取值为None或当top_k大于100时，表示不启用top_k策略，此时，仅有top_p策略生效。","Whether to use a search engine for data enhancement.":"模型内置了互联网搜索服务，该参数控制模型在生成文本时是否参考使用互联网搜索结果。","The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.":"生成时使用的随机数种子，用户控制模型生成内容的随机性。seed支持无符号64位整数，默认值为1234。在使用seed时，模型将尽可能生成相同或相似的结果，但目前不保证每次生成的结果完全相同。","Used to control the repeatability when generating models. Increasing repetition_penalty can reduce the duplication of model generation. 1.0 means no punishment.":"用于控制模型生成时的重复度。提高repetition_penalty时可以降低模型生成的重复度。1.0表示不做惩罚。","Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.":"用于调整生成模型中采样的随机程度，值范围为 [0, 2)，温度为 0 时将始终产生相同的输出。 温度越高，随机性越大。","Sequences where the API will stop generating further tokens.":"停止词集合，用于控制 API 停止生成更多令牌。","The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length.":"聊天完成时生成的最大令牌数。 输入标记和生成标记的总长度受到模型上下文长度的限制。"};export{t as Example,e as Template,n as default};
