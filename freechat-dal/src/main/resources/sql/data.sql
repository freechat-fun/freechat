USE `freechat`;

INSERT IGNORE INTO `ai_model_info` VALUES ('[dash_scope]qwen-turbo', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-turbo', 'qwen base model, 4k context', 'dash_scope', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[dash_scope]qwen-plus', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-plus', 'qwen plus model, 8k context', 'dash_scope', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[dash_scope]qwen-7b-chat', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-7b-chat', 'qwen open sourced 7-billion-parameters version, 8k context', 'dash_scope', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[dash_scope]qwen-14b-chat', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'qwen-14b-chat', 'qwen open sourced 14-billion-parameters version, 8k context', 'dash_scope', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[dash_scope]text-embedding-v1', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-embedding-v1', 'embedding model', 'dash_scope', 'embedding');

INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo', 'alias for the ChatGPT 4k latest model', 'open_ai', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-16k', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-16k', 'alias for the ChatGPT 16k latest model', 'open_ai', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]gpt-4', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4', 'alias for the GPT-4 8k latest model', 'open_ai', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]gpt-4-32k', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-4-32k', 'alias for the GPT-4 32k latest model', 'open_ai', 'text2chat');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]text-davinci-003', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-davinci-003', 'davinci fine-tuning model', 'open_ai', 'text2text');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]gpt-3.5-turbo-instruct', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'gpt-3.5-turbo-instruct', 'trained similarly as the text-davinci series while maintaining the same speed as the turbo models.', 'open_ai', 'text2text');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]text-embedding-ada-002', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-embedding-ada-002', 'embedding model', 'open_ai', 'embedding');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]text-moderation-stable', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-moderation-stable', 'moderation stable model', 'open_ai', 'moderation');
INSERT IGNORE INTO `ai_model_info` VALUES ('[open_ai]text-moderation-latest', '2023-08-20 19:11:32', '2023-08-20 19:11:32', 'text-moderation-latest', 'moderation latest model', 'open_ai', 'moderation');

INSERT IGNORE INTO `prompt_info` (`prompt_id`, `gmt_create`, `gmt_modified`, `user_id`, `visibility`, `name`, `type`, `description`, `template`, `format`, `lang`, `example`, `inputs`, `version`, `ext`)
VALUES (
  '98036e846d0b4830a01aa96f52199a82',
  '2023-12-19 14:34:08',
  '2023-12-19 14:34:08',
  '1',
  'public',
  'prompt生成器',
  'chat',
  '为自定义场景优化prompt；prompt扩写',
  '{"system":"你是一个大模型的prompt提示机器人","messageToBeSent":{"content":"你的目标是为{task}任务改善以下给出的 prompt：\n\nPrompt: {origin_prompt}：\n\n--------------------------\n\n以下是编写优秀 prompt 的几个技巧：\n\n1. 开始提示，说明它是该主题的专家。\n\n2. 将指令放在提示符的开头并使用 # # # ，或者将指令和上下文分开\n\n3. 尽可能详细地描述所需的上下文、结果、长度、格式、风格等\n\n--------------------------\n\n下面是一个很好的提示例子:\n\n作为 YouTube 的内容创作大师，开发一个围绕“探索古代遗迹”这一主题的引人入胜的脚本\n\n你的剧本应该包括令人兴奋的发现，历史洞察力和冒险精神。\n\n包括屏幕上的叙述，引人入胜的视觉效果，可能还有与联合主持人或专家的互动。\n\n剧本理想的结果应该是一个约10-15分钟的视频，为观众提供了一个迷人的旅程，通过过去的秘密。\n\n示例：\n\n“欢迎各位历史爱好者回到我们的频道！今天，我们开始了一次激动人心的探险……”\n\n--------------------------\n\n现在，改进提示。\n\n改进的提示：","functionCall":null,"name":"","role":"user"},"messages":[]}',
  'f_string',
  'zh_CN',
  '{"enter":"","output":""}',
  '{"task": "工作汇报", "origin_prompt": "为今年工作写一个总结纲要"}',
  1,
  '{"top_p": 0.7, "maxTokens": 4096, "max_length": 500, "temperature": 0.7, "presencePenalty": 0.5, "frequencyPenalty": 0.5}'
),(
  'e6aeb598fe494573ab6d4ed674861e00',
  '2023-12-20 18:32:35',
  '2023-12-20 18:32:35',
  '1',
  'public',
  'blog-generator-v2',
  'chat',
  '根据给定文本为特定目标受众生成博客文章的提示。',
  '{"system":"你是一个博客专家，也是一个有用的聊天机器人。","messageToBeSent":{"role":"user","content":"根据提供的上下文创建一篇结构良好的博客文章。\n博客文章应该有效地捕捉上下文中的关键点、见解和信息。\n专注于保持连贯的流程，并使用正确的语法和语言。\n结合相关的标题、副标题和要点来组织内容。\n确保博客文章的基调引人入胜，内容丰富，迎合{target_audience}受众。\n如有必要，请随意添加额外的上下文、示例和解释，以增强文字记录。\n目标是在保持准确性和连贯性的同时，将上下文转换为精致而有价值的书面资源。\nCONTEXT: {text}\nBLOG POST: "},"messages":[]}',
  'f_string',
  'zh_CN',
  NULl,
  '{}',
  1,
  '{"top_p": 1, "max_length": 500, "temperature":1}'
), (
  '10f9de7a85ab4d7d978f896cdacaf4a0',
  '2023-12-20 14:03:07',
  '2023-12-20 14:03:07',
  '1',
  'public',
  '试题制作',
  'string',
  '此提示有助于根据上下文为测试提问。给定一个问题，此提示将生成一个多选问题，确定正确答案，并给出答案正确或不正确的理由',
  '您的目标是为特定问题的测试创建一组精心制作的答案。\n你的答案将用于获取学生知识的测试。你将得到这个问题，你的目标是遵循下面的输出格式和指导方针\n答案选择指南\n定位正确的选项，使其在测验的每个可能位置上出现的次数大致相同。\n答案的选择应该写得清楚，在内容、长度和语法上彼此相似；避免通过使用错误的语法结构来提供线索。\n使所有干扰因素都合理；它们应该是学习者可能存在的常见误解。\n在选择答案时，避免“以上所有”和“以上都没有”，这可能会导致人为提高绩效。\n在选择答案时，避免使用字母（即：“A和B”），因为我们的答案是随机的\n使用数字选项时，选项应按数字顺序以单一格式列出（即，作为术语或范围）。\n｛context｝\n问题：｛question｝{input}测试',
  'f_string',
  'zh_CN',
  NULl,
  '{"input": "测收到", "context": "大师的"}',
  1,
  '{"top_p": 1, "maxTokens": 4096, "max_length": 500, "temperature": 0.7, "presencePenalty": 0, "frequencyPenalty": 0}'
), (
  'af42b5f76465471b9a01399d2017ac72',
  '2023-12-19 16:46:10',
  '2023-12-19 16:46:10',
  '1',
  'public',
  'onboard-email',
  'string',
  'The SaaS User On-Boarding Email Prompt is designed for generating bespoke onboarding emails tailored to Software as a Service platforms. Guided by the Pain-Agitate-Solution strategy, this prompt takes in a [context] about a specific [topic] to produce an email of [word_count] length. The output encompasses a captivating subject line followed by an email body, segmented into the pain point, its amplification, the solution provided by the topic, and a closing that nudges users toward action, all constrained within the specified [word_count]. This tool is a blend of marketing flair and algorithmic precision,ensuring the produced content resonates with the targeted users and produces fewer drops.',
  'Always include a Call To Action.\n\nNever use words like: "Feature", "Religion"\n\n[topic]: {topic}\n[word_count]: {word_count}\n[context]: {context}',
  'f_string',
  'en',
  NULl,
  '{}',
  1,
  '{"top_p": 0.7, "max_length": 500, "temperature": 0.7}'
), (
  '2da94f5c5cc44ad99c8cf93536d8abb5',
  '2023-12-16 17:39:34',
  '2023-12-16 17:39:34',
  '1',
  'public',
  'an_expert',
  'chat',
  '某方面的专家，且限制了拒识回复。',
  '{"system":"你是一个对`{something}`非常了解的助手，请判断问题是否跟`{something}`相关，如果不相关，则回复`{default_reply}`。1111","messageToBeSent":{"role":"user","name":"","content":"{human_input}"},"messages":[{"role":"user","name":"","content":"请问明天天气怎么样？"},{"role":"assistant","name":"","content":"{default_reply}"}]}',
  'f_string',
  'en_US',
  '{"enter":"","output":""}',
  '{"something": "篮球", "human_input": "请问什么是足球越位？", "default_reply": "我母鸡啊！"}',
  2,
  '{"top_p": 0.7, "maxTokens": 4096, "max_length": 500, "temperature": 0.7, "presencePenalty": 0, "frequencyPenalty": 0}'
), (
  'e14c50da1b3145b282ab72d60ba8bcb6',
  '2023-12-20 13:55:21',
  '2023-12-20 13:55:21',
  '1',
  'public',
  'blog-generator',
  'chat',
  '根据给定文本为特定目标受众生成博客文章的提示。',
  '{"system":"你是一个博客专家，也是一个有用的聊天机器人。","messageToBeSent":{"role":"user","content":"根据提供的上下文创建一篇结构良好的博客文章。\n博客文章应该有效地捕捉上下文中的关键点、见解和信息。\n专注于保持连贯的流程，并使用正确的语法和语言。\n结合相关的标题、副标题和要点来组织内容。\n确保博客文章的基调引人入胜，内容丰富，迎合{target_audience}受众。\n如有必要，请随意添加额外的上下文、示例和解释，以增强文字记录。\n目标是在保持准确性和连贯性的同时，将上下文转换为精致而有价值的书面资源。\nCONTEXT: {text}\nBLOG POST: "},"messages":[]}',
  'f_string',
  'zh_CN',
  NULl,
  '{}',
  1,
  '{"top_p": 1, "max_length": 500, "temperature": 1}'
), (
  'f21952ffff9349fbb59fdbe00ad05913',
  '2023-11-28 17:31:36',
  '2023-11-28 17:31:36',
  '1',
  'private',
  '翻译助手',
  'chat',
  '',
  '{"system":"你是一个把输入翻译成{lang}的翻译助手v2","messageToBeSent":{"role":"user","content":"{input}"},"messages":[]}',
  'f_string',
  'zh_CN',
  NULl,
  '{"lang": null}',
  1,
  NULL
);

INSERT IGNORE INTO `ai_model` (`gmt_create`, `gmt_modified`, `model_id`, `refer_type`, `refer_id`) VALUES
('2023-12-19 16:46:10', '2023-12-19 16:46:10',  '[open_ai]gpt-3.5-turbo', 'prompt', 'af42b5f76465471b9a01399d2017ac72'),
('2023-12-20 11:53:07', '2023-12-20 11:53:07', '[dash_scope]qwen-plus', 'prompt', '98036e846d0b4830a01aa96f52199a82'),
('2023-12-20 14:03:07', '2023-12-20 14:03:07', '[dash_scope]qwen-plus', 'prompt', '10f9de7a85ab4d7d978f896cdacaf4a0'),
('2023-12-20 18:32:35', '2023-12-20 18:32:35', '[open_ai]gpt-3.5-turbo', 'prompt', 'e6aeb598fe494573ab6d4ed674861e00'),
('2023-12-21 19:34:08', '2023-12-21 19:34:08', '[dash_scope]qwen-plus', 'prompt', '2da94f5c5cc44ad99c8cf93536d8abb5');

INSERT IGNORE INTO `tag` (`gmt_create`, `gmt_modified`, `content`, `refer_type`, `refer_id`, `user_id`) VALUES
('2023-12-19 16:46:10', '2023-12-19 16:46:10',  'Agents', 'prompt', '', '1'),
('2023-12-19 16:46:10', '2023-12-19 16:46:10',  'ChatBots', 'prompt', '', '1'),
('2023-12-19 16:46:10', '2023-12-19 16:46:10',  'Autonomous agents', 'prompt', '', '1'),
('2023-12-20 11:53:07', '2023-12-20 11:53:07',  'Agents', 'prompt', '', '1'),
('2023-12-20 13:55:21', '2023-12-20 13:55:21',  '问答', 'prompt', '', '1'),
('2023-12-20 13:55:21', '2023-12-20 13:55:21',  '写作', 'prompt', '', '1'),
('2023-12-20 14:03:07', '2023-12-20 14:03:07',  '问答', 'prompt', '', '1'),
('2023-12-20 18:32:35', '2023-12-20 18:32:35',  '问答', 'prompt', '', '1'),
('2023-12-20 18:32:35', '2023-12-20 18:32:35',  '写作', 'prompt', '', '1'),
('2023-12-21 17:20:25', '2023-12-21 17:20:25',  '问答', 'prompt', '', '1');
