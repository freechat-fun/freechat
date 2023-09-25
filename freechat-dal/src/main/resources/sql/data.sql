use `freechat`;

INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-turbo', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-turbo', 'qwen base model, 4k context', 'dash_scope', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-plus', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-plus', 'qwen plus model, 8k context', 'dash_scope', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-7b-chat', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-7b-chat', 'qwen open sourced 7-billion-parameters version, 8k context', 'dash_scope', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-14b-chat', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-14b-chat', 'qwen open sourced 14-billion-parameters version, 8k context', 'dash_scope', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-spark-v1', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-spark-v1', 'qwen fine-tuning model for conversation scene, 4k context', 'dash_scope', 'text2text');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]qwen-spark-v2', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-spark-v2', 'qwen fine-tuning model for conversation scene, 8k context', 'dash_scope', 'text2text');
INSERT INTO `ai_model_info` VALUES ('[dash_scope]text-embedding-v1', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-embedding-v1', 'embedding model', 'dash_scope', 'embedding');

INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo', 'alias for the ChatGPT 4k latest model', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-0301', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-0301', '4k context', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-0613', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-0613', '4k context, functions', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-16k', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-16k', 'alias for the ChatGPT 16k latest model', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-16k-0613', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-16k-0613', '16k context, functions', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4', 'alias for the GPT-4 8k latest model', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4-0314', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-0314', '8k context', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4-0613', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-0613', '8k context, functions', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4-32k', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-32k', 'alias for the GPT-4 32k latest model', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4-32k-0314', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-32k-0314', '32k context', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-4-32k-0613', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-32k-0613', '32k context, functions', 'open_ai', 'text2chat');
INSERT INTO `ai_model_info` VALUES ('[open_ai]text-davinci-003', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-davinci-003', 'davinci fine-tuning model', 'open_ai', 'text2text');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-instruct', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-instruct', 'trained similarly as the text-davinci series while maintaining the same speed as the turbo models.', 'open_ai', 'text2text');
INSERT INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-instruct-0914', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-instruct-0914', 'gpt-3.5-turbo-instruct 2023 Sep version', 'open_ai', 'text2text');
INSERT INTO `ai_model_info` VALUES ('[open_ai]text-embedding-ada-002', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-embedding-ada-002', 'embedding model', 'open_ai', 'embedding');
INSERT INTO `ai_model_info` VALUES ('[open_ai]text-moderation-stable', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-moderation-stable', 'moderation stable model', 'open_ai', 'moderation');
INSERT INTO `ai_model_info` VALUES ('[open_ai]text-moderation-latest', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-moderation-latest', 'moderation latest model', 'open_ai', 'moderation');
