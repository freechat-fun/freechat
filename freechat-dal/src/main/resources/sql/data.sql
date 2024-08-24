USE `freechat`;

INSERT IGNORE INTO `ai_model_info` (`model_id`, `gmt_create`, `gmt_modified`, `name`, `description`, `provider`, `type`) VALUES
('[dash_scope]qwen-turbo', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-turbo', 'qwen base model, 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-plus', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-plus', 'qwen plus model, 32k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-max', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-max', 'qwen max model, 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-max-longcontext', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-max-longcontext', 'qwen max model, 30k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-7b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-7b-chat', 'qwen open sourced 7-billion-parameters model, 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-14b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-14b-chat', 'qwen open sourced 14-billion-parameters model, 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-72b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-72b-chat', 'qwen open sourced 72-billion-parameters model, 32k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen1.5-7b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen1.5-7b-chat', 'qwen open sourced 7-billion-parameters model (v1.5), 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen1.5-14b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen1.5-14b-chat', 'qwen open sourced 14-billion-parameters model (v1.5), 8k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen1.5-32b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen1.5-32b-chat', 'qwen open sourced 32-billion-parameters model (v1.5), 32k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen1.5-72b-chat', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen1.5-72b-chat', 'qwen open sourced 72-billion-parameters model (v1.5), 32k context', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-vl-plus', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-vl-plus', 'qwen multi-modal model, supports image and text information', 'dash_scope', 'text2chat'),
('[dash_scope]qwen-vl-max', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'qwen-vl-max', 'qwen multi-modal model, offers optimal performance on a wider range of complex tasks', 'dash_scope', 'text2chat'),
('[dash_scope]text-embedding-v1', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-embedding-v1', 'embedding model', 'dash_scope', 'embedding'),
('[open_ai]gpt-3.5-turbo', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-3.5-turbo', 'alias for the ChatGPT 4k latest model', 'open_ai', 'text2chat'),
('[open_ai]gpt-4', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4', 'alias for the GPT-4 8k latest model', 'open_ai', 'text2chat'),
('[open_ai]gpt-4-turbo', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4-turbo', 'the latest GPT-4 Turbo model with vision capabilities', 'open_ai', 'text2chat'),
('[open_ai]gpt-4o', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4o', 'most advanced, multimodal flagship model that’s cheaper and faster than GPT-4 Turbo', 'open_ai', 'text2chat'),
('[open_ai]gpt-4o-mini', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4o-mini', 'gpt-40 mini version', 'open_ai', 'text2chat'),
('[open_ai]gpt-4-vision-preview', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4-vision-preview', '128k context, vision', 'open_ai', 'text2chat'),
('[open_ai]text-davinci-003', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-davinci-003', 'davinci fine-tuning model', 'open_ai', 'text2text'),
('[open_ai]gpt-3.5-turbo-instruct', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-3.5-turbo-instruct', 'trained similarly as the text-davinci series while maintaining the same speed as the turbo models.', 'open_ai', 'text2text'),
('[open_ai]text-embedding-ada-002', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-embedding-ada-002', 'embedding model', 'open_ai', 'embedding'),
('[open_ai]text-moderation-stable', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-moderation-stable', 'moderation stable model', 'open_ai', 'moderation'),
('[open_ai]text-moderation-latest', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-moderation-latest', 'moderation latest model', 'open_ai', 'moderation'),
('[azure_open_ai]gpt-3.5-turbo', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-3.5-turbo', 'alias for the ChatGPT 4k latest model', 'azure_open_ai', 'text2chat'),
('[azure_open_ai]gpt-4', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4', 'alias for the GPT-4 8k latest model', 'azure_open_ai', 'text2chat'),
('[azure_open_ai]gpt-4-32k', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4-32k', 'the latest GPT-4 32k latest model', 'azure_open_ai', 'text2chat'),
('[azure_open_ai]gpt-4o', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'gpt-4o', 'most advanced, multimodal flagship model', 'azure_open_ai', 'text2chat'),
('[azure_open_ai]text-embedding-ada-002', '2024-05-01 19:11:32', '2024-05-01 19:11:32', 'text-embedding-ada-002', 'embedding model', 'azure_open_ai', 'embedding');
